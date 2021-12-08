package service.impl;

import java.util.List;

import dao.CategoryDao;
import exception.NoSuchObjectInDBException;
import model.Category;
import model.Shop;
import service.CategoryService;

public class CategoryServiceImpl implements CategoryService {
    private final CategoryDao categoryDao;

    public CategoryServiceImpl(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    @Override
    public Category add(Category category) {
        return categoryDao.add(category);
    }

    @Override
    public Category get(Long id) {
        return categoryDao.get(id).orElseThrow(() -> new NoSuchObjectInDBException("Cannot get category by id " + id));
    }

    @Override
    public List<Category> getAll() {
        return categoryDao.getAll();
    }

    @Override
    public List<Category> getAllByShop(Shop shop) {
        return categoryDao.getAllByShop(shop);
    }

    @Override
    public Category update(Category category) {
        return categoryDao.update(category);
    }

    @Override
    public void delete(Long id) {
        categoryDao.delete(id);
    }
}
