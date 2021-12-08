package dao;

import java.util.List;
import java.util.Optional;

import model.Category;
import model.Product;
import model.Shop;

public interface ProductDao {
    Product add(Product product);

    Optional<Product> get(Long id);

    List<Product> getAll();

    List<Product> getAllByCategory(Category category);

    List<Product> getAllByShop(Shop shop);

    Product update(Product product);

    void delete(Long id);
}
