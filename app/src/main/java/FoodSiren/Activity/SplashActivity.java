package FoodSiren.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eml_listview_test3.R;

import java.util.ArrayList;
import java.util.Collections;

import FoodSiren.DB.AppDatabase;
import FoodSiren.Data.Category;
import FoodSiren.Data.Food;


public class SplashActivity extends AppCompatActivity {

    public static ArrayList<Category> categoryList = new ArrayList<>();
//    public ArrayList<Food> foodList;
    public static String date_text;
//    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        db = AppDatabase.getDatabase(this);
//
//        // 라이브 데이터인 getAll에 observe메소드를 걸어두어, 만일 getAll의 결과가 바뀌면(=데이터가 변경되면) 안의 로직을 다시 수행
//        db.foodDao().getAll().observe(this, foods -> {  // foods 는 DB에 들어있는 food 객체의 배열
//            SplashActivity.foodList.clear();  // foodList를 초기화
//            SplashActivity.foodList.addAll(foods);  // foodList에 변경된 데이터를 저장
//        });

        // SplashActivity 가 여러번 수행되도 categoryList가 비어있을 때만 카테고리 생성 => 추후에 DB로 변경
        if(categoryList.size() == 0) {
            createCategoryData();
        }
//        createFoodData();
//        sortingFoodData();

        setContentView(R.layout.activity_splash);

        Handler hd = new Handler();
        hd.postDelayed(new splashhandler(), 2000);

    }

    private class splashhandler implements Runnable{
        public void run(){
            startActivity(new Intent(getApplication(), MainActivity.class)); //로딩이 끝난 후, ChoiceFunction 이동
            SplashActivity.this.finish(); // 로딩페이지 Activity stack에서 제거
        }
    }

    @Override
    public void onBackPressed() {
        //초반 플래시 화면에서 넘어갈때 뒤로가기 버튼 못누르게 함
    }

    // 데이터에 값을 넣는 메소드, 추후에 SQL에서 SELECT 해오는 것으로 변경
    private void createCategoryData() {
        categoryList.add(new Category(1, "전체보기"));
        categoryList.add(new Category(2, "냉장실"));
        categoryList.add(new Category(3, "냉동실"));
        categoryList.add(new Category(4, "창고"));
        date_text="24:00:00"; // 자정
    }

//    public void createFoodData() {
//        foodList.add(new Food(null, "돼지 목살", "20210407", "20210424", "냉장실"));
//        foodList.add(new Food(null, "서울우유", "20210408", "20210425", "냉장실"));
//        foodList.add(new Food(null, "앙념 갈비", "20210409", "20210426", "냉동실"));
//        foodList.add(new Food(null, "순두부", "20210410", "20210427", "창고"));
//        foodList.add(new Food(null, "고등어", "20210410", "20210428", "창고"));
//        foodList.add(new Food(null, "대파", "20210410", "20210429", "창고"));
//        foodList.add(new Food(null, "감자", "20210410", "20210503", "창고"));
//        date_text="24:00:00"; // 자정
//        //초기 시간 설정
//    }

//    public static void sortingFoodData() {
//        Collections.sort(foodList);
//    }

}

