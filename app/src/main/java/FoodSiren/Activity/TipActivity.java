package FoodSiren.Activity;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.eml_listview_test3.R;

public class TipActivity extends AppCompatActivity {

    private Button btn_store;
    private Button btn_dispose;
    private ImageButton btn_youtube;
    private ImageButton btn_google;
    private ImageButton btn_recipe10000;
    private ImageButton btn_navershopping;
    private EditText et_foodName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip);

        // 툴바
        Toolbar toolbar = findViewById(R.id.TipActivity_tb);
        setSupportActionBar(toolbar);

        InitializeView();
        getFoodName();
        InitializeListener();
    }

    private void InitializeView() {
        et_foodName = (EditText) findViewById(R.id.TipActivity_et_food_name);
        btn_store = findViewById(R.id.TipActivity_btn_store);
        btn_dispose = findViewById(R.id.TipActivity_btn_dispose);
        btn_youtube = findViewById(R.id.TipActivity_btn_youtube);
        btn_google = findViewById(R.id.TipActivity_btn_google);
        btn_recipe10000 = findViewById(R.id.TipActivity_btn_recipe10000);
        btn_navershopping = findViewById(R.id.TipActivity_btn_navershopping);
    }

    private void getFoodName() {
        Intent SecondIntent = getIntent();
        String TipFood = SecondIntent.getStringExtra("데이터");

        et_foodName.setText(TipFood);
        et_foodName.setGravity(Gravity.CENTER); // 받아온 TipFood 값 중앙정렬 -> xml 적용x
    }

    private void InitializeListener() {
        // 구글에 "[식자재명] 보관 방법" 이라고 검색
        btn_store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY, et_foodName.getText().toString() + " 보관 방법");
                startActivity(intent);
            }
        });

        // 구글에 "[식자재명] 버리는법" 이라고 검색
        btn_dispose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY, et_foodName.getText().toString() + " 버리는법");
                startActivity(intent);
            }
        });

        // 유튜브 앱에서 "[식자재명] 레시피" 라고 검색
        btn_youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEARCH);
                intent.setPackage("com.google.android.youtube");
                intent.putExtra("query", et_foodName.getText().toString() + " 레시피");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        // 구글에 "[식자재명] 레시피" 라고 검색
        btn_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY, et_foodName.getText().toString() + " 레시피");
                startActivity(intent);
            }
        });

        // 만개의레시피 웹페이지에서 "[식자재명] 레시피" 라고 검색
        btn_recipe10000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://m.10000recipe.com/recipe/list.html?q="
                                + et_foodName.getText().toString() + "+레시피&order=accuracy&cat1=&cat2=&cat3=&cat4=&ref="));
                startActivity(intent);
            }
        });

        // 네이버 쇼핑에서 [식자재명] 검색
        btn_navershopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://msearch.shopping.naver.com/search/all?query="
                                + et_foodName.getText().toString() + "&frm=NVSHSRC&cat_id=&pb=true&mall="));
                startActivity(intent);
            }
        });
    }
}