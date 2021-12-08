import java.util.ArrayList;
import java.util.List;

import dao.impl.CategoryDaoImpl;
import dao.impl.ProductDaoImpl;
import dao.impl.ShopDaoImpl;
import model.Shop;
import model.ShopType;
import model.Category;
import service.CategoryService;
import service.ProductService;
import service.ShopService;
import service.impl.CategoryServiceImpl;
import service.impl.ProductServiceImpl;
import service.impl.ShopServiceImpl;
import thread.ShopRunnable;
import util.ShopFactory;

public class Main {
    public static void main(String[] args) {
        ProductService productService = new ProductServiceImpl(new ProductDaoImpl());
        CategoryService categoryService = new CategoryServiceImpl(new CategoryDaoImpl(productService));
        ShopService shopService = new ShopServiceImpl(new ShopDaoImpl(categoryService));

        List<Shop> shops = createShops(shopService);
        List<Category> categories = createCategories(shops, categoryService);
        Category categoryToChangeStatusToAbsent = categories.get(4);
        createProducts(productService, shops, categoryToChangeStatusToAbsent, 20);
    }

    private static List<Shop> createShops(ShopService shopService) {
        List<Shop> shops = new ArrayList<>();
        shops.add(ShopFactory.getShop(ShopType.FOOD));
        shops.add(ShopFactory.getShop(ShopType.FURNITURE));
        shops.forEach(shopService::add);
        return shops;
    }

    private static List<Category> createCategories(List<Shop> shops, CategoryService categoryService) {
        List<Category> categories = new ArrayList<>();

        categories.add(new Category("Vegetable"));
        categories.add(new Category("Meat"));
        categories.add(new Category("Grocery"));
        categories.forEach(c -> shops.get(0).addCategory(c));

        int productsToSkip = categories.size();

        categories.add(new Category("Bed"));
        categories.add(new Category("Sofa"));
        categories.add(new Category("Table"));
        categories.stream()
                .skip(productsToSkip)
                .forEach(c -> shops.get(1).addCategory(c));

        categories.forEach(categoryService::add);

        return categories;
    }

    private static void createProducts(ProductService productService, List<Shop> shops,
                                       Category categoryToChangeStatusToAbsent, int percentForChangePrice) {
        long streamDelay = 0;
        List<Thread> threads = new ArrayList<>();

        for (Shop shop : shops) {
            ShopRunnable runnable = new ShopRunnable(productService, shop, 1_000, 3,
                    categoryToChangeStatusToAbsent, percentForChangePrice);
            Thread thread = new Thread(runnable, (shop.getName() + " thread").toUpperCase());
            threads.add(thread);
            try {
                thread.sleep(streamDelay);
            } catch (InterruptedException e) {
                throw new RuntimeException(thread.getName() + "'s sleep was interrupted");
            }
            thread.start();
            streamDelay += 10_000L;
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        showTheFinishMessage();
    }

    private static void showTheFinishMessage() {
        System.out.println(("\n--------------------------" +
                "\nAll the threads were over!" +
                "\n--------------------------").toUpperCase());
    }
}
