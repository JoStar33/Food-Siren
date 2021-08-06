package FoodSiren.Activity;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.eml_listview_test3.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import FoodSiren.Adapter.FoodAdapter;
import FoodSiren.DB.AppDatabase;
import FoodSiren.DB.FoodDao;
import FoodSiren.DB.FoodViewModel;
import FoodSiren.Data.Category;
import FoodSiren.Data.Food;
import FoodSiren.Widget.WidgetListview;


public class ManageActivity extends AppCompatActivity {


    public ArrayList<Food> foodList = new ArrayList<>();
    private ArrayAdapter<Category> categoryAdapter;

    private FoodAdapter foodAdapter;

    private Spinner spinner_category;

    //데이터 복제후 검색에 사용되는 객체
    ArrayList<Food> Copied_foodList_for_Search = new ArrayList<Food>();
    private EditText editText_for_Search;

    private ViewModelProvider.AndroidViewModelFactory viewModelFactory;
    private ViewModelStore viewModelStore = new ViewModelStore();
    private FoodViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);

        if (viewModelFactory == null) {
            viewModelFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication());
        }
        viewModel = new ViewModelProvider(this, viewModelFactory).get(FoodViewModel.class);

        try {
            Copied_foodList_for_Search = (ArrayList<Food>) viewModel.getAll();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Copied_foodList_for_Search 길이 : " + Copied_foodList_for_Search.size());

        viewModel.getAllUpdatedFoods().observe(this, foods -> {  // foods 는 DB에 들어있는 food 객체의 배열
            Copied_foodList_for_Search.clear();  // foodList를 초기화
            Copied_foodList_for_Search.addAll(foods);  // foodList에 변경된 데이터를 저장

            System.out.println("Copied_foodList_for_Search 길이 : " + Copied_foodList_for_Search.size());
        });

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(
                new ComponentName(this, WidgetListview.class));


        // 툴바 관련
        Toolbar toolbar = findViewById(R.id.ManageActivity_tb);
        setSupportActionBar(toolbar);


        // 카테고리 관련 스피너를 진행
        categoryAdapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.category_spinner_item,
                SplashActivity.categoryList);
        spinner_category = (Spinner) findViewById(R.id.ManageActivity_sp_for_category);
        spinner_category.setAdapter(categoryAdapter);
        spinner_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //스피너 리스너사용이 가능하지만, 푸드내에 카테고리가 지정되어있지 않기에
                //아직은 사용이 불가능함. 푸드내에 카테고리를 지정해주는걸 요함.
                String text = spinner_category.getSelectedItem().toString();
                spinner_search(text);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        // Food 객체 생성. 그리고 검색에 사용될 CopyFoodList 생성
        foodList.addAll(Copied_foodList_for_Search);
        System.out.println("foodList 길이 : " + foodList.size());

        // FoodAdapter를 이용하여 Food를 ListView에 Display
        // 추가로 SwipeMenuListView사용을 위해 반드시 build.gradle에
        // dependencies쪽에 >>> compile 'com.baoyz.swipemenulistview:library:1.3.0' 작성해야함.
        // 또한 activity_manage.xml파일내에 listview를 swipemenulistview로 변경함.
        SwipeMenuListView listView = (SwipeMenuListView) findViewById(R.id.ManageActivity_lv);
        foodAdapter = new FoodAdapter(this, foodList, viewModel);
        listView.setAdapter(foodAdapter);


        // 리스트뷰를 옆으로 밀어서 삭제 및 수정하는 기능.
        listView.setMenuCreator(creator);
        listView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {
            @Override
            public void onSwipeStart(int position) {
                // swipe start
                listView.smoothOpenMenu(position);
            }

            @Override
            public void onSwipeEnd(int position) {
                // swipe end
                listView.smoothOpenMenu(position);
            }
        });

        //수정 삭제기능 리스너
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        //수정 버튼 클릭시 다이얼로그 생성
                        Food selectedFood = foodAdapter.getItem(position);
                        Intent intent = new Intent(getApplicationContext(), ModifyDialog.class);
                        intent.putExtra("다이얼로그_데이터", Copied_foodList_for_Search.indexOf(selectedFood));
                        startActivityForResult(intent, 0);
                        foodAdapter.notifyDataSetChanged();
                        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_listview); // 위젯 내용 업데이트
                        break;
                    case 1:
                        //삭제버튼 클릭시 데이터 삭제
                        Copied_foodList_for_Search.remove(foodAdapter.getItem(position));    // foodList에서 삭제

                        // DB에서 객체 삭제
//                        db.foodDao().delete(foodAdapter.getItem(position)); // DB에서 바로 객체를 삭제하는 방식 (Activity에서 db를 직접 조작하는 행위는 위험함.)
//                        new DeleteAsyncTask(db.foodDao()).execute(foodAdapter.getItem(position));  // DeleteAsyncTask 로 백그라운드 상으로 DB의 객체 삭제
                        viewModel.delete(foodAdapter.getItem(position));
                        System.out.println("DB의 객체 삭제 성공");

                        foodAdapter.remove(foodAdapter.getItem(position));  // foodList에서 삭제한 객체의 foodAdapter를 제거
//                        System.out.println(foodAdapter.getItemId(position));
                        foodAdapter.notifyDataSetChanged();
                        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_listview); // 위젯 내용 업데이트
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }

        });


        // 리스트에서 선택 아이템 삭제 관련
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.menu_main, menu);
                return true;
            }

            @Override
            public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.selectAll:
                        final int checkedCount = foodList.size();

                        //항목을 이미 선택하거나 선택한 경우 제거하거나 선택 취소 후 다시 모두 선택
                        foodAdapter.removeSelection();

                        for (int i = 0; i < checkedCount; i++) {
                            listView.setItemChecked(i, true);
                            //  listviewadapter.toggleSelection(i);
                        }
                        mode.setTitle(checkedCount + "  Selected");
                        return true;

                    case R.id.delete:
                        AlertDialog.Builder builder = new AlertDialog.Builder(ManageActivity.this);
                        builder.setMessage("선택한 항목을 삭제하겠습니까?");
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                SparseBooleanArray selected = foodAdapter.getSelectedIds();

                                for (int i = (selected.size() - 1); i >= 0; i--) {
                                    if (selected.valueAt(i)) {
                                        Food selecteditem = foodAdapter.getItem(selected.keyAt(i));

                                        //다음에 선택한 항목 제거
                                        foodAdapter.remove(selecteditem);
                                    }
                                }
                                mode.finish();
                                selected.clear();
                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.setIcon(R.mipmap.ic_launcher_round);// dialog  Icon
                        alert.setTitle("Confirmation"); // dialog  Title
                        alert.show();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                final int checkedCount = listView.getCheckedItemCount();
                mode.setTitle(checkedCount + "  Selected");
                foodAdapter.toggleSelection(position);
            }
        });

        /* Fab 버튼 리스너
        FloatingActionButton checkBtn = findViewById(R.id.check_btn);
        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/


        //검색기능(Test)
        editText_for_Search = (EditText) findViewById(R.id.ManageActivity_et_for_search);
        editText_for_Search.addTextChangedListener(new TextWatcher() {

            //오버라이드해야만 하는 빈 함수들. 굳이 구현 필요는 X.
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // input창에 문자를 입력할때마다 호출된다.
                // search 메소드를 호출한다.
                String categoryText = spinner_category.getSelectedItem().toString();
                String searchText = editText_for_Search.getText().toString();
                Category_And_Food_search(categoryText, searchText);
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModelStore.clear();
    }

    @Override
    public ViewModelStore getViewModelStore() {
        return viewModelStore;
    }

    // 스피너 작업을 수행하는 메소드(Test)
    public void spinner_search(String charText) {

        // 문자 입력시마다 리스트를 지우고 새로 뿌려준다.
        foodList.clear();

        // 문자 입력이 없을때는 모든 데이터를 보여준다.
        if (charText.equals("전체보기")) {
            foodList.addAll(Copied_foodList_for_Search);
        }
        // 문자 입력을 할때..
        else {
            // 리스트의 모든 데이터를 검색한다.
            for (int i = 0; i < Copied_foodList_for_Search.size(); i++) {
                // arraylist의 모든 데이터에 입력받은 단어(charText)가 포함되어 있으면 true를 반환한다.
                // 여기서 문제가 있는데, 카테고리를 지정을 안해줘서(null) 그런지 카테고리의 equals 부분을 푸드에 넣으니
                // 오류가 발생한다.
                if (Copied_foodList_for_Search.get(i).contains(charText)) {
                    // 검색된 데이터를 리스트에 추가한다.
                    foodList.add(Copied_foodList_for_Search.get(i));
                }
            }
        }
        // 리스트 데이터가 변경되었으므로 어뎁터를 갱신하여 검색된 데이터를 화면에 보여준다.
        foodAdapter.notifyDataSetChanged();
    }

    // 카테고리별 검색 작업을 수행하는 메소드(Test)
    public void Category_And_Food_search(String categoryText, String searchText) {

        // 문자 입력시마다 리스트를 지우고 새로 뿌려준다.
        foodList.clear();

        // 문자 입력이 없을때는 모든 데이터를 보여준다.
        if (categoryText.equals("전체보기")) {
            foodList.addAll(Copied_foodList_for_Search);
            search(searchText);
        }
        // 문자 입력을 할때..
        else {
            // 리스트의 모든 데이터를 검색한다.
            for (int i = 0; i < Copied_foodList_for_Search.size(); i++) {
                if (Copied_foodList_for_Search.get(i).contains(categoryText) && Copied_foodList_for_Search.get(i).contains(searchText)) {
                    // 검색된 데이터를 리스트에 추가한다.
                    foodList.add(Copied_foodList_for_Search.get(i));
                } else if (Copied_foodList_for_Search.get(i).contains(categoryText) && searchText.length() == 0) {
                    foodList.add(Copied_foodList_for_Search.get(i));
                }
            }
        }
        // 리스트 데이터가 변경되었으므로 어뎁터를 갱신하여 검색된 데이터를 화면에 보여준다.
        foodAdapter.notifyDataSetChanged();
    }


    // 검색을 수행하는 메소드(Test)
    public void search(String searchText) {

        // 문자 입력시마다 리스트를 지우고 새로 뿌려준다.
        foodList.clear();

        // 문자 입력이 없을때는 모든 데이터를 보여준다.
        if (searchText.length() == 0) {
            foodList.addAll(Copied_foodList_for_Search);
        }
        // 문자 입력을 할때..
        else {
            // 리스트의 모든 데이터를 검색한다.
            for (int i = 0; i < Copied_foodList_for_Search.size(); i++) {
                // arraylist의 모든 데이터에 입력받은 단어(charText)가 포함되어 있으면 true를 반환한다.
                if (Copied_foodList_for_Search.get(i).contains(searchText)) {
                    // 검색된 데이터를 리스트에 추가한다.
                    foodList.add(Copied_foodList_for_Search.get(i));
                }
            }
        }
        // 리스트 데이터가 변경되었으므로 어뎁터를 갱신하여 검색된 데이터를 화면에 보여준다.
        foodAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        //갑작스럽게 뒤로가기 버튼을 누를경우에 데이터가 증발해버리는 버그를 위한 메소드.
        foodList.clear();
        foodList.addAll(Copied_foodList_for_Search);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }


    //슬라이드 삭제를 돕는 객체. 크게 중요한 부분은 아니고, 리스트를 슬라이드하면 나오는
    //버튼의 크기 색상을 변경할때 사용된다.
    SwipeMenuCreator creator = new SwipeMenuCreator() {
        @Override
        public void create(SwipeMenu menu) {
            // create "Close" item
            SwipeMenuItem openItem = new SwipeMenuItem(
                    getApplicationContext());
            // set item background
            openItem.setBackground(new ColorDrawable(0xff4093ff));
            // set item width
            openItem.setWidth(200);
            // set item title
            openItem.setTitle("수정");
            // set item title fontsize
            openItem.setTitleSize(18);
            // set item title font color
            openItem.setTitleColor(Color.WHITE);
            // add to menu
            menu.addMenuItem(openItem);
            // create "delete" item
            SwipeMenuItem deleteItem = new SwipeMenuItem(
                    getApplicationContext());
            // set item background
            deleteItem.setBackground(new ColorDrawable(0xffff4040));
            // set item width
            deleteItem.setWidth(200);
            // set item title
            deleteItem.setTitle("삭제");
            // set item title fontsize
            deleteItem.setTitleSize(18);
            // set item title font color
            deleteItem.setTitleColor(Color.WHITE);
            // add to menu
            menu.addMenuItem(deleteItem);
        }
    };

    //전달받은 엑티비티의 결과물. 다이얼로그에서 값수정이 다 이루어지므로
    //foodAdapter.notifyDataSetChanged();만 선언한다.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                //버그중에서, 만약 카테고리검색을 통해 카테고리를 지정해서 카테고리별로 식품이 나오고나서
                //식품내에 카테고리를 변경한다면, 변경이후에 식품의 위치가 다른 카테고리로 옮겨지기 때문에 해당 화면에서
                //사라져야하지만 그렇지 못했던 문제를 위해 한번 더 다시 spinner_search를 진행함.
                String text = spinner_category.getSelectedItem().toString();
                spinner_search(text);
                foodAdapter.notifyDataSetChanged();
            } else {
                foodAdapter.notifyDataSetChanged();
            }
        }
    }
}

