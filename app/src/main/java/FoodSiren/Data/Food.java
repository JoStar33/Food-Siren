package FoodSiren.Data;


import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import FoodSiren.Activity.MainActivity;
import FoodSiren.Activity.SplashActivity;
import FoodSiren.DB.Converters;

import static FoodSiren.Activity.SplashActivity.categoryList;

@Entity // Food 테이블
public class Food implements Serializable, Comparable<Food> {

    @PrimaryKey(autoGenerate = true)  // Food 테이블의 기본 키, autoGenerate한 성질을 갖게되어 객체 생성 시 id 값을 주지 않아도 DB에서 알아서 생성함
    private int id;

    private String name;

    private String imagePath;

    private int cnt;
    private int progress;

    private String regDate;
    private String expDate;
    private Boolean SwitchBoolean;

    Category category;

    public Food() {

    }

    public Food(String imagePath, String name, String regDate, String expDate) {
        this.imagePath = imagePath;
        this.name = name;
        this.regDate = regDate;
        this.expDate = expDate;
        this.SwitchBoolean = false;

        //인롤에서 객체 생성시 생성된 객체가 수정기능을 사용할 수 없었기에(카테고리를 지정을 안해줘서) 임의로 전체보기로 정해줌.
        for (Category c : SplashActivity.categoryList)
            if (c.getName().equals("전체보기")) {
                setCategory(c);
            }
    }

    public Food(String imagePath, String name, String regDate, String expDate, String category_name) {
        this.imagePath = imagePath;
        this.name = name;
        this.regDate = regDate;
        this.expDate = expDate;
        this.SwitchBoolean = false;
        for (Category c : SplashActivity.categoryList)
            if (category_name.equals(c.getName())) {
                setCategory(c);
            }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    @Override
    public String toString() {
        return "Food{" +
                "name=" + name + "," +
                "imagePath=" + imagePath +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    public void setSwitchBoolean(Boolean SwitchBoolean) {
        this.SwitchBoolean = SwitchBoolean;
    }

    public Boolean getSwitchBoolean() {
        return SwitchBoolean;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
        category.addFoodList(this);
    }

    public void editCategory(Category category) {
        this.category.removeFoodList(this);
        this.category = category;
        category.addFoodList(this);
    }

    public int getDiffDaysRegToCurrent() throws ParseException {
        SimpleDateFormat fm = new SimpleDateFormat("yyyyMMdd");
        Date currentDate = new Date();
        Date regDate = fm.parse(this.regDate);

        long diffInMillies = Math.abs(currentDate.getTime() - regDate.getTime());
        int diffDaysRegToCurrent = (int) TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

//        System.out.println("지난 일 : " + diffDaysRegToCurrent);

        return diffDaysRegToCurrent;
    }

    public int getDiffDaysCurrentToExp() throws ParseException {
        SimpleDateFormat fm = new SimpleDateFormat("yyyyMMdd");
        Date currentDate = new Date();
        Date expDate = fm.parse(this.expDate);

        long diffInMillies = Math.abs(expDate.getTime() - currentDate.getTime());
        int diffDaysCurrentToExp = (int) TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

//        System.out.println("남은 일 : " + diffDaysCurrentToExp);

        return diffDaysCurrentToExp;
    }

    public boolean isExpOver() throws ParseException {
        SimpleDateFormat fm = new SimpleDateFormat("yyyyMMdd");
        Date currentDate = new Date();
        Date expDate = fm.parse(this.expDate);

        if (currentDate.getTime() < expDate.getTime())
            return false;
        return true;
    }

    public int calcProgress() throws ParseException {
        SimpleDateFormat fm = new SimpleDateFormat("yyyyMMdd");
        Date regDate = fm.parse(this.regDate);
        Date currentDate = new Date();
        Date expDate = fm.parse(this.expDate);

        long diffInMilliesRegToExp = Math.abs(expDate.getTime() - regDate.getTime());
        int diffDaysRegToExp = (int) TimeUnit.DAYS.convert(diffInMilliesRegToExp, TimeUnit.MILLISECONDS);

        long diffInMilliesRegToCurrent = Math.abs(currentDate.getTime() - regDate.getTime());
        int diffDaysRegToCurrent = (int) TimeUnit.DAYS.convert(diffInMilliesRegToCurrent, TimeUnit.MILLISECONDS);

        int progress = (diffDaysRegToCurrent * 100) / diffDaysRegToExp;

//        System.out.println("progress : " + progress);
//        System.out.println("===========================");

        return progress;
    }


    //검색기능을 위해 사용되는 matches 개념의 함수
    public boolean contains(String kwd) {
        //String categoryName = category.getName();
        if (name.equals(kwd))
            return true;
        else if (name.length() == 0)
            return true;
        else if (category.getName().equals(kwd))
            return true;
        return false;
    }

    @Override
    public int compareTo(Food food) {
        return this.expDate.compareTo(food.expDate);
    }
}
