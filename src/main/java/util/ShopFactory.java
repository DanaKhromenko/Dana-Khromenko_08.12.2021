package util;

import model.Shop;
import model.ShopType;
import model.FoodShop;
import model.FurnitureShop;

public class ShopFactory {
    public static Shop getShop(ShopType category) {
        Shop shop = null;
        switch (category) {
            case FOOD:
                shop = FoodShop.getInstance();
                if (shop.getName() == null) {
                    shop.setName("Food shop");
                }
                break;
            case FURNITURE:
                shop = FurnitureShop.getInstance();
                if (shop.getName() == null) {
                    shop.setName("Furniture shop");
                }
                break;
        }
        return shop;
    }
}
