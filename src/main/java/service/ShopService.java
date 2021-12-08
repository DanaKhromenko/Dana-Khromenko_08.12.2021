package service;

import java.util.List;

import model.Shop;

public interface ShopService {
    Shop add(Shop shop);

    Shop get(Long id);

    Shop get(String name);

    List<Shop> getAll();

    Shop update(Shop shop);

    void delete(Long id);
}
