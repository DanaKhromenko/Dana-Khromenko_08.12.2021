package dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import dao.CategoryDao;
import exception.DataProcessingException;
import model.Category;
import model.Shop;
import service.ProductService;
import util.ConnectionUtil;

public class CategoryDaoImpl implements CategoryDao {
    private ProductService productService;

    public CategoryDaoImpl(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public Category add(Category category) {
        String insertCategoryRequest =
                "INSERT INTO categories(name, shop_id) values(?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement createCategoryStatement =
                     connection.prepareStatement(insertCategoryRequest, Statement.RETURN_GENERATED_KEYS)) {
            createCategoryStatement.setString(1, category.getName());
            createCategoryStatement.setLong(2, category.getShop().getId());
            createCategoryStatement.executeUpdate();
            ResultSet generatedKeys = createCategoryStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                category.setId(generatedKeys.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot save " + category + " to DB", e);
        }
        return category;
    }

    @Override
    public Optional<Category> get(Long id) {
        return GlobalDao.getCategoryById(id);
    }

    @Override
    public List<Category> getAll() {
        List<Category> categories = new ArrayList<>();
        String query = "SELECT c.id, c.name, c.shop_id,\n" +
                "s.name as shop_name\n" +
                "FROM categories c\n" +
                "LEFT JOIN shops s ON c.shop_id = s.id;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getAllCategoriesStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = getAllCategoriesStatement.executeQuery();
            while (resultSet.next()) {
                categories.add(GlobalDao.parseCategory(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot get all categories from DB ", e);
        }
        for (Category category : categories) {
            category.setProducts(GlobalDao.getProductsForCategory(category));
        }
        return categories;
    }

    @Override
    public List<Category> getAllByShop(Shop shop) {
        List<Category> categories = new ArrayList<>();
        String query = "SELECT c.id, c.name, c.shop_id\n" +
                "FROM categories c\n" +
                "LEFT JOIN shops s ON c.shop_id = s.id\n" +
                "WHERE s.id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getAllCategoriesStatementByShop = connection.prepareStatement(query)) {
            getAllCategoriesStatementByShop.setLong(1, shop.getId());
            ResultSet resultSet = getAllCategoriesStatementByShop.executeQuery();
            while (resultSet.next()) {
                categories.add(GlobalDao.parseCategory(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot get all categories by shop " + shop + " from DB ", e);
        }
        for (Category category : categories) {
            category.setProducts(GlobalDao.getProductsForCategory(category));
        }
        return categories;
    }

    @Override
    public Category update(Category category) {
        String updateCategoryQuery = "UPDATE categories SET name = ?, shop_id = ? WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement updateCategoryStatement = connection.prepareStatement(updateCategoryQuery)) {
            updateCategoryStatement.setString(1, category.getName());
            updateCategoryStatement.setLong(2, category.getShop().getId());
            updateCategoryStatement.setLong(3, category.getId());
            if (updateCategoryStatement.executeUpdate() == 0) {
                return category;
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot update category " + category, e);
        }

        removeProductsByCategoryId(category);
        category.getProducts().forEach(p -> productService.add(p));

        return category;
    }

    @Override
    public void delete(Long id) {
        String query = "DELETE FROM categories WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement deleteCategoryStatement = connection.prepareStatement(query)) {
            deleteCategoryStatement.setLong(1, id);
            deleteCategoryStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot delete category by id " + id + " ", e);
        }
    }

    private void removeProductsByCategoryId(Category category) {
        String deleteProductsByCategoryIdQuery = "DELETE FROM products p WHERE p.category_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement deleteProductsByCategoryIdStatement = connection.prepareStatement(deleteProductsByCategoryIdQuery)) {
            deleteProductsByCategoryIdStatement.setLong(1, category.getId());
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot delete products that had category " + category + " before", e);
        }
    }
}
