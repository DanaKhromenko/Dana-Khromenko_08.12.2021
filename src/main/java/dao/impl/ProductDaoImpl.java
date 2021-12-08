package dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import dao.ProductDao;
import exception.DataProcessingException;
import model.Category;
import model.Product;
import model.Shop;
import model.Status;
import util.ConnectionUtil;

public class ProductDaoImpl implements ProductDao {

    @Override
    public Product add(Product product) {
        String insertProductRequest =
                "INSERT INTO products(title, price, status, category_id) values(?, ?, ?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement createProductStatement =
                     connection.prepareStatement(insertProductRequest, Statement.RETURN_GENERATED_KEYS)) {
            createProductStatement.setString(1, product.getTitle());
            createProductStatement.setDouble(2, product.getPrice());
            createProductStatement.setString(3, product.getStatus().name());
            createProductStatement.setLong(4, product.getCategory().getId());
            createProductStatement.executeUpdate();
            ResultSet generatedKeys = createProductStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                product.setId(generatedKeys.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot save " + product + " to DB", e);
        }
        return product;
    }

    @Override
    public Optional<Product> get(Long id) {
        String getProductRequest = "SELECT * FROM products WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getProductStatement = connection.prepareStatement(getProductRequest)) {
            getProductStatement.setLong(1, id);
            ResultSet resultSet = getProductStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(getProduct(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot get product from DB by id " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Product> getAll() {
        List<Product> products = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
             Statement getAllProductsStatement = connection.createStatement()) {
            ResultSet resultSet = getAllProductsStatement
                    .executeQuery("SELECT * FROM products;");
            while (resultSet.next()) {
                products.add(getProduct(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot read all products from DB", e);
        }
        return products;
    }

    @Override
    public List<Product> getAllByCategory(Category category) {
        String getAllProductsByCategoryRequest = "SELECT * FROM products WHERE category_id = ?;";
        List<Product> products = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getAllProductsByCategoryStatement = connection.prepareStatement(getAllProductsByCategoryRequest)) {
            getAllProductsByCategoryStatement.setLong(1, category.getId());
            ResultSet resultSet = getAllProductsByCategoryStatement.executeQuery();
            while (resultSet.next()) {
                products.add(getProduct(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot read all products by category " + category + " from DB", e);
        }
        return products;
    }

    @Override
    public List<Product> getAllByShop(Shop shop) {
        String getAllProductsByCategoryRequest = "SELECT\n" +
                "p.id, p.title, p.price, p.status, p.category_id\n" +
                "FROM products p INNER JOIN categories c ON p.category_id = c.id\n" +
                "WHERE c.shop_id = ?;";
        List<Product> products = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getAllProductsByCategoryStatement = connection.prepareStatement(getAllProductsByCategoryRequest)) {
            getAllProductsByCategoryStatement.setLong(1, shop.getId());
            ResultSet resultSet = getAllProductsByCategoryStatement.executeQuery();
            while (resultSet.next()) {
                products.add(getProduct(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot read all products by shop " + shop + " from DB", e);
        }
        return products;
    }

    @Override
    public Product update(Product product) {
        String updateProductRequest = "UPDATE products SET title = ?, price = ?, status = ?, category_id = ? WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement updateProductStatement =
                     connection.prepareStatement(updateProductRequest, Statement.NO_GENERATED_KEYS)) {
            updateProductStatement.setString(1, product.getTitle());
            updateProductStatement.setDouble(2, product.getPrice());
            updateProductStatement.setString(3, product.getStatus().name());
            updateProductStatement.setLong(4, product.getCategory().getId());
            updateProductStatement.setLong(5, product.getId());
            updateProductStatement.executeUpdate();
            return product;
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot update " + product + " in DB", e);
        }
    }

    @Override
    public void delete(Long id) {
        String deleteRequest = "DELETE FROM products WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement deleteProductStatement = connection.prepareStatement(
                     deleteRequest, Statement.RETURN_GENERATED_KEYS)) {
            deleteProductStatement.setLong(1, id);
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete product with id " + id + " from DB", e);
        }
    }

    private Product getProduct(ResultSet resultSet) throws SQLException {
        Product product = new Product();
        product.setId(resultSet.getObject(1, Long.class));
        product.setTitle(resultSet.getString(2));
        product.setPrice(resultSet.getDouble(3));
        product.setStatus(Status.valueOf(resultSet.getString(4)));
        product.setCategory(GlobalDao.getCategoryById(resultSet.getObject(5, Long.class))
                .orElseThrow(() -> new NoSuchElementException("Cannot get category")));
        return product;
    }
}
