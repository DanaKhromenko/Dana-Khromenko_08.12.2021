package dao;

import java.util.List;
import java.util.Optional;

import model.Category;
import model.Shop;

public interface CategoryDao {
    Category add(Category category);

    Optional<Category> get(Long id);

    List<Category> getAll();

    List<Category> getAllByShop(Shop shop);

    Category update(Category category);

    void delete(Long id);
}
