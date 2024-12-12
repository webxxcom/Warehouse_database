package main.java.ua.nure.cpp.jdbc.dao.abstr.daos;

import main.java.ua.nure.cpp.jdbc.entity.Product;
import main.java.ua.nure.cpp.jdbc.entity.ProductDescription;

import java.util.List;
import java.util.Optional;

public interface AllProductsDAO extends EntityDAO<Product> {
    /* Getters */
    Optional<Integer> getQuantity(long id);
    Optional<Product> getById(long id);
    List<Product> getByCategoryLike(String pattern);

    /* Updaters */
    void updateQuantity(long productId, int newQuantity);
    void take(long productId, int quantity);
    void insert(Product product);
    void insertAll(Product... products);
}
