package FoodSiren.Activity;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.eml_listview_test3.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import FoodSiren.DB.AppDatabase;
import FoodSiren.DB.FoodDao;
import FoodSiren.DB.FoodViewModel;
import FoodSiren.Data.Category;
import FoodSiren.Data.Food;


//수정에 사용되는 커스텀다이얼로그
public class ModifyDialog extends AppCompatActivity implements View.OnClickListener {
    public ArrayList<Food> foodList = new ArrayList<>();
    private Button btn_Modify;
    private EditText Modify_Name, Modify_regDate, Modify_expDate;
    private Spinner Modify_categorySpinner;
    private int FoodList_Position;
    private String Gotten_Name;
    private String Gotten_regDate, Gotten_expDate;
    private String Gotten_categoryName;
    private String Changed_categoryName;
    private ArrayAdapter<Category> categoryAdapter;
    private DatePickerDialog.OnDateSetListener btn_callbackMethodRegCal;
    private DatePickerDialog.OnDateSetListener btn_callbackMethodExpCal;
    private Food Gotten_Food;
    private String Gotten_ImagePath;

    private ViewModelProvider.AndroidViewModelFactory viewModelFactory;
    private ViewModelStore viewModelStore = new ViewModelStore();
    private FoodViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_modify_dialog);

        if (viewModelFactory == null) {
            viewModelFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication());
        }
        viewModel = new ViewModelProvider((ViewModelStoreOwner) this, viewModelFactory).get(FoodViewModel.class);

        try {
            foodList = (ArrayList<Food>) viewModel.getAll();
            System.out.println("foodList 길이 : " + foodList.size());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        InitializeView();

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes((WindowManager.LayoutParams) params);

        btn_Modify = findViewById(R.id.ModifyDialog_btn_for_modify);
        btn_Modify.setOnClickListener(this);
        InitializeListener();

        Modify_regDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String regDate = Modify_regDate.getText().toString();
                if (regDate.length() == 8) {
                    StringToDate(Modify_regDate.getText().toString(), Modify_regDate);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Modify_expDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String expDate = Modify_expDate.getText().toString();
                if (expDate.length() == 8) {
                    StringToDate(Modify_expDate.getText().toString(), Modify_expDate);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

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

    public void InitializeListener() {
        btn_callbackMethodRegCal = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month += 1;
                String change_year = Integer.toString(year);
                String change_month = Integer.toString(month);
                String change_dayofMonth = Integer.toString(dayOfMonth);
                String sum = change_year + change_month + change_dayofMonth;
                if (sum.length() != 8) {
                    if (change_month.length() < 2)
                        change_month = "0" + change_month;
                    if (change_dayofMonth.length() < 2)
                        change_dayofMonth = "0" + change_dayofMonth;
                }
                Modify_regDate.setText(change_year + change_month + change_dayofMonth);
            }

        };

        btn_callbackMethodExpCal = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month += 1;
                String change_year = Integer.toString(year);
                String change_month = Integer.toString(month);
                String change_dayofMonth = Integer.toString(dayOfMonth);
                String sum = change_year + change_month + change_dayofMonth;
                if (sum.length() != 8) {
                    if (change_month.length() < 2)
                        change_month = "0" + change_month;
                    if (change_dayofMonth.length() < 2)
                        change_dayofMonth = "0" + change_dayofMonth;
                }
                Modify_expDate.setText(change_year + change_month + change_dayofMonth);
            }
        };
    }


    @Override
    public void onClick(View v) {

        //EditText의 수정된 값 가져오기
        String name = Modify_Name.getText().toString();
        String regDate = Modify_regDate.getText().toString();
        String expDate = Modify_expDate.getText().toString();
        String categoryName = Changed_categoryName;


        for (Category c : SplashActivity.categoryList)
            if (c.getName().equals(categoryName))
                foodList.get(FoodList_Position).setCategory(c);
        foodList.get(FoodList_Position).setName(name);
//        foodList.get(FoodList_Position).setRegDate(regDate);
//        foodList.get(FoodList_Position).setExpDate(expDate);

        if (regDate.equals("") || expDate.equals("")) {
            Toast.makeText(getApplicationContext(), "날짜를 입력하세요!", Toast.LENGTH_SHORT).show();
        } else {

            // 캘린더로 입력한 경우 년월일 삭제

            if (regDate.length() > 8) {

                String splitRegByYear[] = regDate.split("년");
                String regYear = splitRegByYear[0];
                String regMonthAndDayOfMonth = splitRegByYear[1];

                String splitRegByMonth[] = regMonthAndDayOfMonth.split("월");
                String regMonth = splitRegByMonth[0];
                String regDayOfMonth = splitRegByMonth[1].replace("일", "");

                System.out.println("년 : " + regYear);
                System.out.println("월 : " + regMonth);
                System.out.println("일 : " + regDayOfMonth);

                if (regDate.length() != 11) {
                    if (Integer.parseInt(regMonth) < 10)
                        regMonth = "0" + regMonth;

                    if (Integer.parseInt(regDayOfMonth) < 10)
                        regDayOfMonth = "0" + regDayOfMonth;
                }

                regDate = regYear + regMonth + regDayOfMonth;
            }


            // 캘린더로 입력한 경우 년월일 삭제
            if (expDate.length() > 8) {
                String splitExpByYear[] = expDate.split("년");
                String expYear = splitExpByYear[0];
                String expMonthAndDayOfMonth = splitExpByYear[1];

                String splitExpByMonth[] = expMonthAndDayOfMonth.split("월");
                String expMonth = splitExpByMonth[0];
                String expDayOfMonth = splitExpByMonth[1].replace("일", "");

                System.out.println("년 : " + expYear);
                System.out.println("월 : " + expMonth);
                System.out.println("일 : " + expDayOfMonth);

                if (expDate.length() != 11) {
                    if (Integer.parseInt(expMonth) < 10) {
                        expMonth = "0" + expMonth;
                    }

                    if (Integer.parseInt(expDayOfMonth) < 10) {
                        expDayOfMonth = "0" + expDayOfMonth;
                    }
                }
                expDate = expYear + expMonth + expDayOfMonth;
            }

            try {
                SimpleDateFormat fromFormat = new SimpleDateFormat("yyyyMMdd");
                Date objDate1 = fromFormat.parse(regDate);
                Date objDate2 = fromFormat.parse(expDate);

                String limitReg = regDate.charAt(0) + "" + regDate.charAt(1) + "" + regDate.charAt(2) + "" + regDate.charAt(3);
                String limitExp = expDate.charAt(0) + "" + expDate.charAt(1) + "" + expDate.charAt(2) + "" + expDate.charAt(3);

                if (regDate.length() == 8 && expDate.length() == 8) {
                    if (Integer.parseInt(limitReg) < 2100 && Integer.parseInt(limitReg) > 2020
                            && Integer.parseInt(limitExp) < 2100 && Integer.parseInt(limitExp) > 2020) {

                        foodList.get(FoodList_Position).setRegDate(regDate);
                        foodList.get(FoodList_Position).setExpDate(expDate);
                        Intent intent = new Intent();
                        intent.putExtra("result", "UpdateData");
                        // DB에서 객체 수정
//                db.foodDao().update(foodList.get(FoodList_Position));  // DB에서 바로 객체를 수정하는 방식 (Activity에서 db를 직접 조작하는 행위는 위험함.)
                        viewModel.update(foodList.get(FoodList_Position));
                        System.out.println("DB의 객체 수정 성공");
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "연도를 확인해 주세요.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "연도를 확인해 주세요.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "잘못된 입력 값입니다.", Toast.LENGTH_SHORT).show();
            }
        }

        //값 전달을 하지 않는다. 위에 작업에서 데이터를 수정하기 때문.
    }


    public void OnClickHandlerRegCalBtn(View view) {
        Date currentDate = new Date();
        DatePickerDialog dialog = new DatePickerDialog(this, btn_callbackMethodRegCal, currentDate.getYear() + 1900, currentDate.getMonth(), currentDate.getDate());

        dialog.show();
    }

    public void OnClickHandlerExpCalBtn(View view) {
        Date currentDate = new Date();
        DatePickerDialog dialog = new DatePickerDialog(this, btn_callbackMethodExpCal, currentDate.getYear() + 1900, currentDate.getMonth(), currentDate.getDate());

        dialog.show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥 레이어 클릭 시 안 닫히게
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            return false;
        }
        return true;
    }

    private void StringToDate(String date, EditText et) {
        try {
            SimpleDateFormat fromFormat = new SimpleDateFormat("yyyyMMdd");
            Date objDate = fromFormat.parse(date);

            SimpleDateFormat toFormat = new SimpleDateFormat("yyyy년MM월dd일");
            String resultDate = toFormat.format(objDate);

            et.setText(resultDate);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void InitializeView() {
        Intent intent = getIntent();
        FoodList_Position = intent.getIntExtra("다이얼로그_데이터", 0);

        Gotten_Food = foodList.get(FoodList_Position);

        Gotten_Name = Gotten_Food.getName();
        Gotten_regDate = Gotten_Food.getRegDate();
        Gotten_expDate = Gotten_Food.getExpDate();
        Gotten_ImagePath = Gotten_Food.getImagePath();
        Gotten_categoryName = Gotten_Food.getCategory().getName();

        Modify_Name = findViewById(R.id.ModifyDialog_name);
        Modify_Name.setText(Gotten_Name);

        Modify_regDate = findViewById(R.id.ModifyDialog_regDate);
        StringToDate(Gotten_regDate, Modify_regDate);

        Modify_expDate = findViewById(R.id.ModifyDialog_expDate);
        StringToDate(Gotten_expDate, Modify_expDate);

        Modify_categorySpinner = (Spinner) findViewById(R.id.ModifyDialog_sp_for_category);
        categoryAdapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.category_spinner_item,
                SplashActivity.categoryList);
        Modify_categorySpinner.setAdapter(categoryAdapter);


        int categoryCounter = 0;
        for (Category c : SplashActivity.categoryList) {
            if (c.getName().equals(Gotten_categoryName))
                Modify_categorySpinner.setSelection(categoryCounter);
            categoryCounter++;
        }

        Modify_categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Changed_categoryName = Modify_categorySpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }
}
