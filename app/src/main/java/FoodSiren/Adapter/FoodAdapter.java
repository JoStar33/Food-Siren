package FoodSiren.Adapter;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;

import com.bumptech.glide.Glide;
import com.example.eml_listview_test3.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import FoodSiren.Activity.ModifyDialog;
import FoodSiren.Activity.TipActivity;
import FoodSiren.DB.AppDatabase;
import FoodSiren.DB.FoodDao;
import FoodSiren.DB.FoodViewModel;
import FoodSiren.Data.Food;
import FoodSiren.Receiver.AlarmReceiver;

import static FoodSiren.Activity.SplashActivity.date_text;
import static android.content.Context.ALARM_SERVICE;

public class FoodAdapter extends BaseAdapter {

    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<Food> foodList;
    private SparseBooleanArray mSelectedItemsIds;

    private FoodViewModel mViewModel;

    //알림기능을 위한 변수들
    private AlarmManager alarmManager;

    public FoodAdapter(Context context, ArrayList<Food> data, FoodViewModel viewModel) {
        mContext = context;
        foodList = data;
        mViewModel = viewModel;
        mLayoutInflater = LayoutInflater.from(mContext);
        mSelectedItemsIds = new SparseBooleanArray();
    }

    public static class ViewHolder {
        ImageView iv_foodImage;
        TextView tv_foodName;
        ProgressBar pb_progressBar;
        ImageButton btn_tip;
        TextView tv_regToCurrent;
        TextView tv_currentToExp;
        Switch sw_alarm;
    }

    @Override
    public int getCount() {
        return foodList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Food getItem(int position) {
        return foodList.get(position);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //알림기능.
        alarmManager = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);
        ViewHolder holder;

        if (convertView == null) { // convertView가 없을 때만 새로 View를 inflate하여 메모리 손실을 줄임

            holder = new ViewHolder(); // 생성한 객체들의 정보를 갖고 있을 홀더

//            System.out.println("새로 연결@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            // 홀더의 필드에 값을 저장
            convertView = mLayoutInflater.inflate(R.layout.adapter_food, null);
            holder.iv_foodImage = convertView.findViewById(R.id.FoodAdapter_iv_food_image);
            holder.tv_foodName = convertView.findViewById(R.id.FoodAdapter_tv_food_name);
            holder.pb_progressBar = convertView.findViewById(R.id.FoodAdapter_progress_bar);
            holder.btn_tip = convertView.findViewById(R.id.FoodAdapter_btn_tip);// tip button test
            holder.tv_regToCurrent = convertView.findViewById(R.id.FoodAdapter_tv_reg_to_current);
            holder.tv_currentToExp = convertView.findViewById(R.id.FoodAdapter_tv_current_to_exp);
            holder.sw_alarm = convertView.findViewById(R.id.FoodAdapter_swt_alarm);

            convertView.setTag(holder); // 리스트의 각 아이템 별로 홀더의 내용을 저장함
        } else { // 처음에 converView가 있을 경우

            holder = (ViewHolder) convertView.getTag(); // 리스트의 각 아이템에서 저장한 홀더에 저장해둔 값을 가져옴
        }

        if (foodList.get(position).getImagePath() == null) {
            holder.iv_foodImage.setImageResource(R.drawable.baguette);
        } else {
            String photoPath = foodList.get(position).getImagePath();
            Glide.with(mContext).load(photoPath).into(holder.iv_foodImage);
        }

        holder.tv_foodName.setText(foodList.get(position).getName());
        try {
            holder.tv_regToCurrent.setText((foodList.get(position).getDiffDaysRegToCurrent()) + "일 째");
            if ((foodList.get(position).isExpOver()) && (foodList.get(position).getDiffDaysCurrentToExp() == 0))
                holder.tv_currentToExp.setText((foodList.get(position).getDiffDaysCurrentToExp()) + "일 남음");
            else if (foodList.get(position).isExpOver())
                holder.tv_currentToExp.setText((foodList.get(position).getDiffDaysCurrentToExp()) + "일 지남");
            else
                holder.tv_currentToExp.setText((foodList.get(position).getDiffDaysCurrentToExp() + 1) + "일 남음");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            if (foodList.get(position).isExpOver()) {
                holder.pb_progressBar.setProgressTintList(ColorStateList.valueOf(0xFFFF4D37));
            } else if (foodList.get(position).getDiffDaysCurrentToExp() >= 0 && foodList.get(position).getDiffDaysCurrentToExp() <= 4) {
                holder.pb_progressBar.setProgressTintList(ColorStateList.valueOf(Color.YELLOW));
                holder.pb_progressBar.setProgress(foodList.get(position).calcProgress());
            }
            if (foodList.get(position).getDiffDaysCurrentToExp() > 4) {
                holder.pb_progressBar.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
                holder.pb_progressBar.setProgress(foodList.get(position).calcProgress());
            }
            holder.pb_progressBar.setProgress(foodList.get(position).calcProgress());
        } catch (
                ParseException e) {
            e.printStackTrace();
        }

        holder.btn_tip.setOnClickListener(new View.OnClickListener() { // tip button click시 toast 받는지 test
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, TipActivity.class);
                intent.putExtra("데이터", foodList.get(position).getName());
                mContext.startActivity(intent);
            }
        });
        holder.sw_alarm.setChecked(foodList.get(position).getSwitchBoolean());
        holder.sw_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // UpdateAsyncTask 로 백그라운드 상으로 DB의 객체 수정
                if (holder.sw_alarm.isChecked()) {
                    setAlarm(true, position);
                    foodList.get(position).setSwitchBoolean(true);
                } else {
                    setAlarm(false, position);
                    foodList.get(position).setSwitchBoolean(false);
                }
                mViewModel.update(foodList.get(position));
            }
        });

        return convertView;
    }

    public void remove(Food object) {
        foodList.remove(object);
        notifyDataSetChanged();
    }


    //업데이트 또는 삭제 후 목록 가져 오기
    public ArrayList<Food> getMyList() {
        return foodList;
    }


    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }


    //선택 해제 후 선택 제거
    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }


    //선택시 체크된 된 항목
    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);

        notifyDataSetChanged();
    }


    //선택한 항목 수 가져 오기
    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }


    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }

    public void setAlarm(boolean isAlarmOn, int position) {

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        Date expDate = null;
        try {
            expDate = df.parse(foodList.get(position).getExpDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar expMinus7 = Calendar.getInstance();
        expMinus7.setTime(expDate);
        expMinus7.add(Calendar.DATE, -7);
        String strExpMinus7 = df.format(expMinus7.getTime());

        Calendar expMinus3 = Calendar.getInstance();
        expMinus3.setTime(expDate);
        expMinus3.add(Calendar.DATE, -3);
        String strExpMinus3 = df.format(expMinus3.getTime());

        Calendar expPlus3 = Calendar.getInstance();
        expPlus3.setTime(expDate);
        expPlus3.add(Calendar.DATE, +3);
        String strExpPlus3 = df.format(expPlus3.getTime());

        Calendar expPlus7 = Calendar.getInstance();
        expPlus7.setTime(expDate);
        expPlus7.add(Calendar.DATE, +7);
        String strExpPlus7 = df.format(expPlus7.getTime());

        int DoubleRequestCode = position * 5;

        try {
            if ((foodList.get(position).getDiffDaysCurrentToExp() + 1 >= 7) && (!foodList.get(position).isExpOver())) { // 유통기한까지 남은 일자가 7일 이상인 경우, D-7일 알람 설정
                makeNotification(isAlarmOn, position, DoubleRequestCode + 0, strExpMinus7);
                makeNotification(isAlarmOn, position, DoubleRequestCode + 1, strExpMinus3);
                makeNotification(isAlarmOn, position, DoubleRequestCode + 2, foodList.get(position).getExpDate());
                makeNotification(isAlarmOn, position, DoubleRequestCode + 3, strExpPlus3);
                makeNotification(isAlarmOn, position, DoubleRequestCode + 4, strExpPlus7);
            } else if ((foodList.get(position).getDiffDaysCurrentToExp() + 1 >= 3) && (!foodList.get(position).isExpOver())) { // 유통기한까지 남은 일자가 3일 이상인 경우, D-3일 알람 설정
                makeNotification(isAlarmOn, position, DoubleRequestCode + 1, strExpMinus3);
                makeNotification(isAlarmOn, position, DoubleRequestCode + 2, foodList.get(position).getExpDate());
                makeNotification(isAlarmOn, position, DoubleRequestCode + 3, strExpPlus3);
                makeNotification(isAlarmOn, position, DoubleRequestCode + 4, strExpPlus7);
            } else if ((foodList.get(position).isExpOver()) && (foodList.get(position).getDiffDaysCurrentToExp() == 0)) { // 유통기한이 오늘 마감되는 경우, 0일 알람, D+3일 알람, D+7일 알람 설정
                makeNotification(isAlarmOn, position, DoubleRequestCode + 2, foodList.get(position).getExpDate());
                makeNotification(isAlarmOn, position, DoubleRequestCode + 3, strExpPlus3);
                makeNotification(isAlarmOn, position, DoubleRequestCode + 4, strExpPlus7);
            } else if ((foodList.get(position).isExpOver()) && (foodList.get(position).getDiffDaysCurrentToExp() >= 1) && (foodList.get(position).getDiffDaysCurrentToExp() <= 3)) { // 유통기한이 지난 경우, Toast 메시지 출력
                makeNotification(isAlarmOn, position, DoubleRequestCode + 3, strExpPlus3);
                makeNotification(isAlarmOn, position, DoubleRequestCode + 4, strExpPlus7);
                Toast.makeText(mContext, "이미 유통기한이 지난 상품입니다.", Toast.LENGTH_SHORT).show();
            } else if ((foodList.get(position).isExpOver()) && (foodList.get(position).getDiffDaysCurrentToExp() >= 4) && (foodList.get(position).getDiffDaysCurrentToExp() <= 7)) { // 유통기한이 지난 경우, Toast 메시지 출력
                makeNotification(isAlarmOn, position, DoubleRequestCode + 4, strExpPlus7);
                Toast.makeText(mContext, "이미 유통기한이 지난 상품입니다.", Toast.LENGTH_SHORT).show();
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    private void makeNotification(boolean isAlarmOn, int position, int DoubleRequestCode, String strAlarmDate) {
        Intent receiverIntent = new Intent(mContext, AlarmReceiver.class);
        Calendar getToday = Calendar.getInstance();
        getToday.setTime(new Date()); // 현재 날짜
        try {
            if ((foodList.get(position).isExpOver()) && (foodList.get(position).getDiffDaysCurrentToExp() == 0)) {
                receiverIntent.putExtra("오늘 마감", foodList.get(position).getDiffDaysCurrentToExp());
            } else if (foodList.get(position).isExpOver()) {
                receiverIntent.putExtra("지난 일 수", foodList.get(position).getDiffDaysCurrentToExp());
            } else {
                receiverIntent.putExtra("남은 일 수", foodList.get(position).getDiffDaysCurrentToExp() + 1);
            }
        } catch (
                ParseException e) {
            e.printStackTrace();
        }
        receiverIntent.putExtra("제목", foodList.get(position).getName());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, DoubleRequestCode, receiverIntent, PendingIntent.FLAG_CANCEL_CURRENT);


        String alarmTime = strAlarmDate + " " + date_text; //임의로 날짜와 시간을 지정
        if (isAlarmOn == true) {
            //날짜 포맷을 바꿔주는 소스코드
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd kk:mm:ss");
            Date alarmDate = null;
            try {
                alarmDate = df.parse(alarmTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar alarmThisTime = Calendar.getInstance();
            alarmThisTime.setTime(alarmDate);


            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    //API 19 이상 API 23미만
                    alarmManager.setExact(AlarmManager.RTC, alarmThisTime.getTimeInMillis(), pendingIntent);
                } else {
                    //API 19미만
                    alarmManager.set(AlarmManager.RTC, alarmThisTime.getTimeInMillis(), pendingIntent);
                }
            } else {
                //API 23 이상
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC, alarmThisTime.getTimeInMillis(), pendingIntent);
            }
        } else
            alarmManager.cancel(pendingIntent);
    }
}
