package service.impl;

import java.util.List;

import dao.ShopDao;
import exception.NoSuchObjectInDBException;
import model.Shop;
import service.ShopService;

public class ShopServiceImpl implements ShopService {
    private final ShopDao shopDao;

    public ShopServiceImpl(ShopDao shopDao) {
        this.shopDao = shopDao;
    }

    @Override
    public Shop add(Shop shop) {
        return shopDao.add(shop);
    }

    @Override
    public Shop get(Long id) {
        return shopDao.get(id).orElseThrow(() -> new NoSuchObjectInDBException("Cannot find shop by id " + id));
    }

    @Override
    public Shop get(String name) {
        return shopDao.get(name).orElseThrow(() -> new NoSuchObjectInDBException("Cannot find shop by name " + name));
    }

    @Override
    public List<Shop> getAll() {
        return shopDao.getAll();
    }

    @Override
    public Shop update(Shop shop) {
        return shopDao.update(shop);
    }

    @Override
    public void delete(Long id) {
        shopDao.delete(id);
    }
}
