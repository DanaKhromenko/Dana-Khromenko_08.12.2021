package dao.impl;

import exception.DataProcessingException;
import model.Category;
import model.Product;
import model.Shop;
import model.ShopType;
import model.Status;
import util.ConnectionUtil;
import util.ShopFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class GlobalDao {

    protected static Optional<Category> getCategoryById(Long id) {
        String getCategoryRequest = "SELECT c.id, c.name, c.shop_id,\n" + "s.name as shop_name\n" + "FROM categories c\n" + "LEFT JOIN shops s ON c.shop_id = s.id\n" + "WHERE c.id = ?;";
        Category category = null;
        try (Connection connection = ConnectionUtil.getConnection(); PreparedStatement getCategoryStatement = connection.prepareStatement(getCategoryRequest)) {
            getCategoryStatement.setLong(1, id);
            ResultSet resultSet = getCategoryStatement.executeQuery();
            if (resultSet.next()) {
                category = parseCategory(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot get category from DB by id " + id, e);
        }
        if (category != null) {
            category.setProducts(getProductsForCategory(category));
        }
        return Optional.ofNullable(category);
    }

    protected static Category parseCategory(ResultSet resultSet) throws SQLException {
        Category category = new Category();
        category.setId(resultSet.getObject(1, Long.class));
        category.setName(resultSet.getString(2));
        long shopId = resultSet.getObject(3, Long.class);
        category.setShop(getShopById(shopId).orElseThrow(() -> new NoSuchElementException("Cannot find shop by id " + shopId)));
        return category;
    }

    protected static List<Product> getProductsForCategory(Category category) {
        List<Product> products = new ArrayList<>();
        String query = "SELECT p.id, p.title, p.status FROM products p WHERE p.category_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection(); PreparedStatement getProductsByCategoryIdStatement = connection.prepareStatement(query)) {
            getProductsByCategoryIdStatement.setLong(1, category.getId());
            ResultSet resultSet = getProductsByCategoryIdStatement.executeQuery();
            while (resultSet.next()) {
                products.add(parseProduct(resultSet, category));
            }
            return products;
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot get products for category with id " + category.getId(), e);
        }
    }

    protected static Product parseProduct(ResultSet resultSet, Category category) throws SQLException {
        Product product = new Product();
        product.setId(resultSet.getObject(1, Long.class));
        product.setTitle(resultSet.getString(2));
        product.setStatus(Status.valueOf(resultSet.getString(3)));
        product.setCategory(category);
        return product;
    }

    protected static Optional<Shop> getShopById(Long id) {
        String getShopRequest = "SELECT s.id, s.name, s.type FROM shop s WHERE s.id = ?;";
        Shop shop = null;
        try (Connection connection = ConnectionUtil.getConnection(); PreparedStatement getShopStatement = connection.prepareStatement(getShopRequest)) {
            getShopStatement.setLong(1, id);
            ResultSet resultSet = getShopStatement.executeQuery();
            if (resultSet.next()) {
                shop = parseShop(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot get shop from DB by id " + id, e);
        }
        if (shop != null) {
            shop.setCategories(getCategoriesForShop(shop));
        }
        return Optional.ofNullable(shop);
    }

    protected static Shop parseShop(ResultSet resultSet) throws SQLException {
        ShopType type = ShopType.valueOf(resultSet.getString(3));
        Shop shop = ShopFactory.getShop(type);
        shop.setId(resultSet.getObject(1, Long.class));
        shop.setName(resultSet.getString(2));
        return shop;
    }

    protected static List<Category> getCategoriesForShop(Shop shop) {
        List<Category> categories = new ArrayList<>();
        String query = "SELECT c.id, c.name FROM categories c WHERE c.shop_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection(); PreparedStatement getCategoriesByShopIdStatement = connection.prepareStatement(query)) {
            getCategoriesByShopIdStatement.setLong(1, shop.getId());
            ResultSet resultSet = getCategoriesByShopIdStatement.executeQuery();
            while (resultSet.next()) {
                categories.add(parseCategory(resultSet));
            }
            return categories;
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot get categories for shop with id " + shop.getId(), e);
        }
    }
}
