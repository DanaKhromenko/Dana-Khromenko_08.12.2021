package model;

public final class FurnitureShop extends Shop {
    private static Shop instance;

    private FurnitureShop() {
    }

    public static Shop getInstance() {
        if (instance == null) {
            instance = new FurnitureShop();
        }
        return instance;
    }

    @Override
    public String toString() {
        return "Furniture shop{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                '}';
    }
}
