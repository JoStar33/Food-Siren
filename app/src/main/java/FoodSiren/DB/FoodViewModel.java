package FoodSiren.DB;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;

import FoodSiren.Activity.ManageActivity;
import FoodSiren.Data.Food;

// View(Activity)에게 Model(Data)를 전달하는 클래스
// Activity가 직접 DB에 접근해서 Data를 뽑아오는 것은 Activity와 같은 View가 맡는 일이 아니기 때문에(View는 오로지 화면 구성에만 집중) 이를 ViewModel이 맡아서 처리한다.

public class FoodViewModel extends AndroidViewModel {
    private AppDatabase db;

    public FoodViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getDatabase(application);  // DB 생성, 싱글톤 패턴
    }

    public LiveData<List<Food>> getAllUpdatedFoods() {  // DB에 있는 모든 걸 가져옴, LiveData이므로 B가 갱신되면 새로 가져옴
        return db.foodDao().getAllUpdatedFoods();
    }  

    public List<Food> getAll() throws ExecutionException, InterruptedException {  // DB에 있는 모든 걸 가져옴(1회성)
        return new SelectAsyncTask(db.foodDao()).execute().get();
    }
    
    // 매개변수로 넘기는 Food 객체를 DB 추가
    public void insert(Food food) {
        new InsertAsyncTask(db.foodDao()).execute(food);
    }

    // 매개변수로 넘기는 Food 객체와 같은 객체를 DB에서 찾아서 업데이트
    public void update(Food food) {
        new UpdateAsyncTask(db.foodDao()).execute(food);
    }  // 

    // 매개변수로 넘기는 Food 객체와 같은 객체를 DB에서 찾아서 삭제
    public void delete(Food food) {
        new DeleteAsyncTask(db.foodDao()).execute(food);
    }

    // getAll()을 수행하는 비동기문, DB에 접근하는 건 시간이 오래걸릴 수 있으므로 비동기적으로 처리해야 함.
    public static class SelectAsyncTask extends AsyncTask<Food, Void, List<Food>> {
        private FoodDao mFoodDao;

        public SelectAsyncTask(FoodDao foodDao) {
            this.mFoodDao = foodDao;
        }

        @Override
        protected List<Food> doInBackground(Food... foods) {
            return mFoodDao.getAll();
        }
    }

    // insert()을 수행하는 비동기문
    public static class InsertAsyncTask extends AsyncTask<Food, Void, Void> {
        private FoodDao mFoodDao;

        public InsertAsyncTask(FoodDao foodDao) {
            this.mFoodDao = foodDao;
        }

        @Override
        protected Void doInBackground(Food... foods) {
            mFoodDao.insert(foods[0]);
            return null;
        }
    }

    // update()을 수행하는 비동기문
    public static class UpdateAsyncTask extends AsyncTask<Food, Void, Void> {
        private FoodDao mFoodDao;

        public UpdateAsyncTask(FoodDao foodDao) {
            this.mFoodDao = foodDao;
        }

        @Override
        protected Void doInBackground(Food... foods) {
            mFoodDao.update(foods[0]);
            return null;
        }
    }

    // delete()을 수행하는 비동기문
    public static class DeleteAsyncTask extends AsyncTask<Food, Void, Void> {
        private FoodDao mFoodDao;

        public DeleteAsyncTask(FoodDao foodDao) {
            this.mFoodDao = foodDao;
        }

        @Override
        protected Void doInBackground(Food... foods) {
            mFoodDao.delete(foods[0]);
            return null;
        }
    }
}
