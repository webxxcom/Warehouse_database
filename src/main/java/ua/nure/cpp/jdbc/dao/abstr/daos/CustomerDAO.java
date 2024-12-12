package main.java.ua.nure.cpp.jdbc.dao.abstr.daos;

import main.java.ua.nure.cpp.jdbc.entity.Customer;
import main.java.ua.nure.cpp.jdbc.entity.ProductDescription;

import java.util.Optional;

public interface CustomerDAO extends PartnerDAO<Customer> {

    /* Getters */
    Optional<Long> getCartId(long customerId);
    Optional<Long> getWishlistId(long customerId);

    /* Updaters */
    void addToWishList(long productId, ProductDescription prd);
    void addToCart(long customerId, ProductDescription prd);
    void removeProductFromCart(long customerId, long productId);
    void removeProductFromWishlist(long customerId, long productId);
    void setCartId(long customerId, long cartId);
    void setWishlistId(long customerId, long wishlistId);
}
