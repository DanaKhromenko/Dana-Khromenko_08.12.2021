package dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import dao.ShopDao;
import exception.DataProcessingException;
import model.Shop;
import model.FoodShop;
import model.FurnitureShop;
import service.CategoryService;
import util.ConnectionUtil;

public class ShopDaoImpl implements ShopDao {
    private CategoryService categoryService;

    public ShopDaoImpl(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    public Shop add(Shop shop) {
        String insertShopRequest = "INSERT INTO shops(name, type) values(?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement createShopStatement =
                     connection.prepareStatement(insertShopRequest, Statement.RETURN_GENERATED_KEYS)) {
            createShopStatement.setString(1, shop.getName());
            createShopStatement.setString(2, getShopType(shop));
            createShopStatement.executeUpdate();
            ResultSet generatedKeys = createShopStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                shop.setId(generatedKeys.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot save " + shop + " to DB", e);
        }
        return shop;
    }

    @Override
    public Optional<Shop> get(Long id) {
        return GlobalDao.getShopById(id);
    }

    @Override
    public Optional<Shop> get(String name) {
        String getShopRequest = "SELECT s.id, s.name, s.type FROM shop s WHERE s.name = ?;";
        Shop shop = null;
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getShopStatement = connection.prepareStatement(getShopRequest)) {
            getShopStatement.setString(1, name);
            ResultSet resultSet = getShopStatement.executeQuery();
            if (resultSet.next()) {
                shop = GlobalDao.parseShop(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot get shop from DB by name " + name, e);
        }
        if (shop != null) {
            shop.setCategories(GlobalDao.getCategoriesForShop(shop));
        }
        return Optional.ofNullable(shop);
    }

    @Override
    public List<Shop> getAll() {
        List<Shop> shops = new ArrayList<>();
        String query = "SELECT s.id, s.name, s.type FROM shops s;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getAllShopsStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = getAllShopsStatement.executeQuery();
            while (resultSet.next()) {
                shops.add(GlobalDao.parseShop(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot get all shops from DB ", e);
        }
        for (Shop shop : shops) {
            shop.setCategories(GlobalDao.getCategoriesForShop(shop));
        }
        return shops;
    }

    @Override
    public Shop update(Shop shop) {
        String updateCategoryQuery = "UPDATE shops SET name = ?, type = ? WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement updateShopStatement = connection.prepareStatement(updateCategoryQuery)) {
            updateShopStatement.setString(1, shop.getName());
            updateShopStatement.setString(2, getShopType(shop));
            updateShopStatement.setLong(3, shop.getId());
            if (updateShopStatement.executeUpdate() == 0) {
                return shop;
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot update shop " + shop, e);
        }

        removeCategoriesByShopId(shop);
        shop.getCategories().forEach(p -> categoryService.add(p));

        return shop;
    }

    @Override
    public void delete(Long id) {
        String query = "DELETE FROM shops WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement deleteShopStatement = connection.prepareStatement(query)) {
            deleteShopStatement.setLong(1, id);
            deleteShopStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot delete shop by id " + id + " ", e);
        }
    }

    private void removeCategoriesByShopId(Shop shop) {
        String deleteCategoriesByShopIdQuery = "DELETE FROM categories p WHERE c.shop_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement deleteCategoriesByShopIdStatement = connection.prepareStatement(deleteCategoriesByShopIdQuery)) {
            deleteCategoriesByShopIdStatement.setLong(1, shop.getId());
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot delete categories that had shop " + shop + " before", e);
        }
    }

    private String getShopType(Shop shop) {
        Class shopClass = shop.getClass();
        String type = "";
        if (shopClass.equals(FoodShop.class)) {
            type = "FOOD";
        } else if (shopClass.equals(FurnitureShop.class)) {
            type = "FURNITURE";
        }
        return type;
    }
}
