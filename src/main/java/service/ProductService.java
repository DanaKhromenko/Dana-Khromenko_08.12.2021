package service;

import java.util.List;

import model.Category;
import model.Product;
import model.Shop;

public interface ProductService {
    Product add(Product product);

    Product get(Long id);

    List<Product> getAll();

    List<Product> getAllByCategory(Category category);

    List<Product> getAllByShop(Shop shop);

    Product update(Product product);

    void delete(Long id);
}
