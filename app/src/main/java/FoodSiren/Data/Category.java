package FoodSiren.Data;

import java.util.ArrayList;

public class Category {

    private int id;

    private String name;

    public Category(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    private ArrayList<Food> foodList = new ArrayList<Food>();

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Food> getProductList() {
        return foodList;
    }

    public void addFoodList(Food food) {
        foodList.add(food);
    }

    public void removeFoodList(Food food) {
        foodList.remove(food);
    }
}
