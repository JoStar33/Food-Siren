package FoodSiren.DB;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import FoodSiren.Data.Food;

// 앱이 갖고 있는 내부 DB 정보

@Database(entities = {Food.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract FoodDao foodDao();

    private static AppDatabase INSTANCE;

    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) { // 싱글톤 패턴, 앱 내에서 DB가 딱 한번 생성됨
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "food_db")
//                    .allowMainThreadQueries()  // 비동기적 방식(Async)을 사용하지 않고도 DB에 접근할 수 있는 옵션
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
