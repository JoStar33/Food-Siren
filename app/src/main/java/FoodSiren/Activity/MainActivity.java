package FoodSiren.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.eml_listview_test3.R;

public class MainActivity extends AppCompatActivity {
    private ImageView iv_nav;
    private DrawerLayout view_drawer;
    private ImageButton btn_enroll;
    private ImageButton btn_manage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_enroll = findViewById(R.id.MainActivity_btn_enroll);
        btn_manage = findViewById(R.id.MainActivity_btn_manage);

        iv_nav = findViewById(R.id.MainActivity_iv_nav);
        view_drawer = findViewById(R.id.MainActivity_drawer_layout);

        //커스텀한 툴바에서 imgeview 눌렀을 때 drawer가 오른쪽에서 열리도록 구성
        iv_nav.setOnClickListener((v) -> {
            view_drawer.openDrawer(Gravity.RIGHT);
            Intent intent = new Intent(getApplicationContext(), EditTimeActivity.class);
            startActivity(intent);
        });

        btn_enroll.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), EnrollActivity.class);
            startActivity(intent);
        });

        btn_manage.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ManageActivity.class);
            startActivity(intent);
        });
    }

}