package FoodSiren.Receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.eml_listview_test3.R;

import FoodSiren.Activity.ManageActivity;


public class AlarmReceiver extends BroadcastReceiver {

    public AlarmReceiver() {
    }

    NotificationManager manager;
    NotificationCompat.Builder builder;


    //오레오 이상은 반드시 채널을 설정해줘야 Notification이 작동함
    private static String CHANNEL_ID = "channel1";
    private static String CHANNEL_NAME = "Channel1";


    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("제목");
        int endToday = intent.getIntExtra("오늘 마감", -1);
        int leftDays = intent.getIntExtra("남은 일 수", -1);
        int passDays = intent.getIntExtra("지난 일 수", -1);
        builder = null;
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                    new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            );
            builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(context);
        }

        if (endToday != -1) {
            builder.setContentTitle(title + "의 유통기한이 오늘 끝나요!");
        } else if (leftDays != -1) {
            builder.setContentTitle(title + "의 유통기한이 " + leftDays + "일 남았어요!");
        } else
            builder.setContentTitle(title + "의 유통기한이 " + passDays + "일 지났어요!");

        //알림창 클릭 시 activity 화면 부름
        Intent manageIntent = new Intent(context, ManageActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) (System.currentTimeMillis() / 1000), manageIntent, PendingIntent.FLAG_ONE_SHOT);

        //알림창 제목
        //알림창 아이콘
        builder.setSmallIcon(R.drawable.ic_launcher_background);
        //알림창 터치시 자동 삭제
        builder.setAutoCancel(true);

        builder.setContentIntent(pendingIntent);

        Notification notification = builder.build();
        manager.notify((int) (System.currentTimeMillis() / 1000), notification);
    }
}