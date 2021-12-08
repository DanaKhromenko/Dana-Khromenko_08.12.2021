package dao;

import java.util.List;
import java.util.Optional;

import model.Shop;

public interface ShopDao {
    Shop add(Shop shop);

    Optional<Shop> get(Long id);

    Optional<Shop> get(String name);

    List<Shop> getAll();

    Shop update(Shop shop);

    void delete(Long id);
}
