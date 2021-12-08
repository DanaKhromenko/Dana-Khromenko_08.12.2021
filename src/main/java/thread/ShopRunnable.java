package thread;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;

import model.Category;
import model.Product;
import model.Shop;
import model.Status;
import service.ProductService;

public class ShopRunnable implements Runnable {
    private final ProductService productService;
    private final Shop shop;
    private final int priceBound;
    private final int productQuantity;
    private final Category categoryToChangeStatusToAbsent;
    private final int percentForChangePrice;

    public ShopRunnable(ProductService productService, Shop shop, int priceBound, int productQuantity,
                        Category categoryToChangeStatusToAbsent, int percentForChangePrice) {
        this.productService = productService;
        this.shop = shop;
        this.priceBound = priceBound;
        this.productQuantity = productQuantity;
        this.categoryToChangeStatusToAbsent = categoryToChangeStatusToAbsent;
        this.percentForChangePrice = percentForChangePrice;
    }

    public void run() {
        int needToChangeProductIterator = 0;
        for (Category category : shop.getCategories()) {
            for (int i = 0; i < productQuantity; i = i + 1) {
                Product product = createProduct(category);
                product = changePriceAndStatusIfNeeded(product, category, ++needToChangeProductIterator % 2 == 0);
                if (product != null) {
                    productService.update(product);
                }
            }

            if (category == categoryToChangeStatusToAbsent) {
                List<Product> products = shop.changeStatus(category, Status.ABSENT);
                products.forEach(productService::update);
                products.forEach(p -> System.out.println("\n" + Thread.currentThread().getName() + ": STATUS changed to ABSENT for " + p));
            }
        }
    }

    private Product createProduct(Category category) {
        Random random = new Random();

        BigDecimal coinsBigDecimal = new BigDecimal(Double.toString(random.nextDouble()));
        coinsBigDecimal = coinsBigDecimal.setScale(2, RoundingMode.HALF_UP);
        double coins = coinsBigDecimal.doubleValue();

        Product product = new Product("Product #" + random.nextInt(10_000), random.nextInt(priceBound) + coins, Status.AVAILABLE);
        Product productFromShop = shop.addProduct(product, category);
        productService.add(productFromShop);

        System.out.println("\n" + Thread.currentThread().getName() + ": CREATED " + productFromShop);

        return productFromShop;
    }

    private Product changePriceAndStatusIfNeeded(Product product, Category category, boolean changeStatusToExpected) {
        if (category == categoryToChangeStatusToAbsent) {
            return null;
        }

        if (changeStatusToExpected && product.getStatus() != Status.EXPECTED) {
            product = shop.changeStatus(product, Status.EXPECTED);
            System.out.println(Thread.currentThread().getName() + ": STATUS changed to EXPECTED for " + product);
        }

        if (product.getStatus().equals(Status.AVAILABLE)) {
            product = shop.changePrice(product, percentForChangePrice);
            System.out.println(Thread.currentThread().getName() + ": PRICE increased by " + percentForChangePrice + "% for " + product);
        }
        return product;
    }
}
