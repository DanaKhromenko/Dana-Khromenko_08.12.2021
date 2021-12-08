package service;

import java.util.List;

import model.Category;
import model.Shop;

public interface CategoryService {
    Category add(Category category);

    Category get(Long id);

    List<Category> getAll();

    List<Category> getAllByShop(Shop shop);

    Category update(Category category);

    void delete(Long id);
}
