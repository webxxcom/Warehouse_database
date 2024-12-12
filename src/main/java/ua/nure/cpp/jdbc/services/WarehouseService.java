package main.java.ua.nure.cpp.jdbc.services;

import main.java.ua.nure.cpp.jdbc.dao.abstr.DAOFactory;
import main.java.ua.nure.cpp.jdbc.dao.abstr.daos.AllProductsDAO;
import main.java.ua.nure.cpp.jdbc.dao.abstr.daos.CustomerDAO;
import main.java.ua.nure.cpp.jdbc.dao.abstr.daos.EntityDAO;
import main.java.ua.nure.cpp.jdbc.dao.abstr.daos.SupplierDAO;
import main.java.ua.nure.cpp.jdbc.entity.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class WarehouseService {
    public static final int NUMBER_OF_SUPPLIERS = 5;
    public static final int NUMBER_OF_CUSTOMERS = 4;
    public static final int NUMBER_OF_PRODUCTS = 8;

    /* DAOs */
    private final AllProductsDAO allProductsDAO;
    private final CustomerDAO customerDAO;
    private final SupplierDAO supplierDAO;

    public WarehouseService(DAOFactory mySqlDaoFactory) {
        Objects.requireNonNull(mySqlDaoFactory, "DAO factory cannot be null");

        this.allProductsDAO = mySqlDaoFactory.getAllProductsDAO();
        this.customerDAO = mySqlDaoFactory.getCustomerDAO();
        this.supplierDAO = mySqlDaoFactory.getSupplierDAO();
    }

    public Optional<Product> getProduct(long id){
        return allProductsDAO.getById(id);
    }

    public Optional<Customer> getCustomer(long id){
        return customerDAO.getById(id);
    }

    public Optional<Supplier> getSupplier(long id){
        return supplierDAO.getById(id);
    }

    public void addSuppliers(Supplier... suppliers){
        addEntitiesTo(supplierDAO, suppliers);
    }

    public void addCustomers(Customer... customers){
        addEntitiesTo(customerDAO, customers);
    }

    public void addProducts(Product... products){
        allProductsDAO.insertAll(products);
    }

    private static <T extends Entity> void addEntitiesTo(@NotNull EntityDAO<T> entityDAO, T[] entities){
        Objects.requireNonNull(entities);
        if(Arrays.stream(entities).anyMatch(Objects::isNull))
            throw new IllegalArgumentException("Null main.entity cannot be added to the database");

        entityDAO.insertAll(entities);
    }

    public void updateProductQuantity(long productId, int quantity){
        allProductsDAO.updateQuantity(productId, quantity);
    }

    public void addProductToCustomerCart(long customerId, ProductDescription prd) {
        Objects.requireNonNull(prd);

        customerDAO.addToCart(customerId, prd);
    }

    public void removeFromCustomerCart(long customerId, long productId){
        customerDAO.removeProductFromCart(customerId, productId);
    }

    public void addProductToCustomerWishlist(long customerId, ProductDescription prd) {
        customerDAO.addToWishList(customerId, prd);
    }

    public void removeFromCustomerWishlist(long customerId, long productId){
        customerDAO.removeProductFromWishlist(customerId, productId);
    }

    public void removeSupplier(long id){
        supplierDAO.delete(id);
    }

    public void removeCustomer(long id) {
        customerDAO.delete(id);
    }

    public void removeAvailableProduct(long id){
        allProductsDAO.delete(id);
    }

    public Collection<Product> getAllProducts(){
        return allProductsDAO.getAll();
    }

    public Collection<Customer> getCustomers(){
        return customerDAO.getAll();
    }

    public Collection<Supplier> getSuppliers(){
        return supplierDAO.getAll();
    }
}
