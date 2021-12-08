package model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Shop {
    private Long id;
    private String name;
    private List<Category> categories;

    public Shop() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public Category addCategory(Category category) {
        if (categories == null) {
            categories = new ArrayList<>();
        }
        if (!categories.contains(category)) {
            categories.add(category);
            category.setShop(this);
        }
        return category;
    }

    public Product addProduct(Product product, Category category) {
        addCategory(category);
        return category.addProduct(product);
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        if (categories == null) {
            return products;
        }
        return categories.stream()
                .map(Category::getProducts)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public Product changeStatus(Product product, Status status) {
        product.setStatus(status);
        return product;
    }

    public List<Product> changeStatus(List<Product> products, Status status) {
        products.forEach(p -> p.setStatus(status));
        return products;
    }

    public List<Product> changeStatus(Category category, Status status) {
        if (category == null) {
            return new ArrayList<>();
        }
        List<Product> products = category.getProducts();
        products.forEach(p -> p.setStatus(status));
        return products;
    }

    public List<Product> changeStatus(Status status) {
        List<Product> products = getAllProducts();
        products.forEach(p -> p.setStatus(status));
        return products;
    }

    public Product changePrice(Product product, double percent) {
        product.setPrice(countPrice(product, percent));
        return product;
    }

    public List<Product> changePrice(List<Product> products, double percent) {
        products.forEach(p -> p.setPrice(countPrice(p, percent)));
        return products;
    }

    public List<Product> changePrice(Category category, double percent) {
        if (category == null) {
            return new ArrayList<>();
        }
        List<Product> products = category.getProducts();
        products.forEach(p -> p.setPrice(countPrice(p, percent)));
        return products;
    }

    public List<Product> changePrice(double percent) {
        List<Product> products = getAllProducts();
        products.forEach(p -> p.setPrice(countPrice(p, percent)));
        return products;
    }

    private double countPrice(Product product, double percent) {
        double resultWithoutRounding = product.getPrice() + product.getPrice() / 100 * percent;
        return Math.round(resultWithoutRounding * 100.0) / 100.0;
    }

    @Override
    public String toString() {
        return "Shop{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
