package FoodSiren.DB;

import androidx.room.TypeConverter;

import FoodSiren.Activity.SplashActivity;
import FoodSiren.Data.Category;

public class Converters {
    
    // DB에 들어갈 수 없는 타입인 Category 타입을 스트링 타입으로 변환하여 저장하기 위한 컨버터
    
    // DB에서 꺼내올 때  String -> Category
    @TypeConverter
    public static Category categoryFromString(String categoryName) {
        for (Category c: SplashActivity.categoryList) {
            if (c.getName().equals(categoryName))
                return c;
        }
        return null;
    }

    // DB에 저장될 때  Category -> String
    @TypeConverter
    public static String categoryToString(Category category) {
        return category.getName();
    }
}
