package FoodSiren.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eml_listview_test3.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;


public class LoadingActivity extends AppCompatActivity {
    private String htmlPageUrl;
    private String htmlContentInStringFormat;
    private String mCurrentPhotoPath = null;
    String BarcodeFoodName;
    String BarcodeNumber;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        Intent LoadingIntent = getIntent();
        BarcodeNumber = LoadingIntent.getStringExtra("바코드_번호");
        htmlPageUrl = "https://www.productbankkorea.or.kr/products/info?q=%7B%22mainKeyword%22:%22" + BarcodeNumber + "%22,%22subKeyword%22:%22%22%7D&page=1&size=3";
        JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
        jsoupAsyncTask.execute();
    }

    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                SSLConnect ssl = new SSLConnect();
                ssl.postHttps(htmlPageUrl, 1000, 1000);
                Document doc = Jsoup.connect(htmlPageUrl).get();
                Elements links = doc.select("p.spl_pt");
                for (Element link : links) {
                    BarcodeFoodName = link.select("strong").text();
                }
                Elements linksIMG = doc.select("span.spl_img");
                for (Element link : linksIMG) {
                    mCurrentPhotoPath = link.select("img").attr("src");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Intent IntentName = new Intent();
            IntentName.putExtra("바코드_식품_이름", BarcodeFoodName);
            IntentName.putExtra("바코드_식품_사진", mCurrentPhotoPath);

            //Toast.makeText(LoadingActivity.this, BarcodeFoodName, Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK, IntentName);
            finish();
        }
    }
}
