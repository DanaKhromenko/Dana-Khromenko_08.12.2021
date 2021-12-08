package model;

public final class FoodShop extends Shop {
    private static Shop instance;

    private FoodShop() {
    }

    public static Shop getInstance() {
        if (instance == null) {
            instance = new FoodShop();
        }
        return instance;
    }

    @Override
    public String toString() {
        return "Food shop{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                '}';
    }
}
