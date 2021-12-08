package service.impl;

import java.util.List;

import dao.ProductDao;
import exception.NoSuchObjectInDBException;
import model.Category;
import model.Product;
import model.Shop;
import service.ProductService;

public class ProductServiceImpl implements ProductService {
    private final ProductDao productDao;

    public ProductServiceImpl(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Override
    public Product add(Product product) {
        return productDao.add(product);
    }

    @Override
    public Product get(Long id) {
        return productDao.get(id).orElseThrow(() -> new NoSuchObjectInDBException("Cannot get product by id " + id));
    }

    @Override
    public List<Product> getAll() {
        return productDao.getAll();
    }

    @Override
    public List<Product> getAllByCategory(Category category) {
        return productDao.getAllByCategory(category);
    }

    @Override
    public List<Product> getAllByShop(Shop shop) {
        return productDao.getAllByShop(shop);
    }

    @Override
    public Product update(Product product) {
        return productDao.update(product);
    }

    @Override
    public void delete(Long id) {
        productDao.delete(id);
    }
}
