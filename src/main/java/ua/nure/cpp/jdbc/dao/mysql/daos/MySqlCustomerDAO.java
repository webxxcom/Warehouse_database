package main.java.ua.nure.cpp.jdbc.dao.mysql.daos;

import main.java.ua.nure.cpp.jdbc.dao.abstr.TransactionManager;
import main.java.ua.nure.cpp.jdbc.entity.Customer;
import main.java.ua.nure.cpp.jdbc.entity.ProductContainer;
import main.java.ua.nure.cpp.jdbc.entity.ProductDescription;
import main.java.ua.nure.cpp.jdbc.dao.abstr.ConnectionManager;
import main.java.ua.nure.cpp.jdbc.dao.abstr.DBException;
import main.java.ua.nure.cpp.jdbc.dao.abstr.EntityWasNotFoundException;
import main.java.ua.nure.cpp.jdbc.dao.abstr.daos.CustomerDAO;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.LongFunction;

public final class MySqlCustomerDAO implements CustomerDAO {
    private final MySqlProductRelationshipTableDAO cartItemsDAO;
    private final MySqlProductRelationshipTableDAO wishlistItemsDAO;
    private final ConnectionManager cm;
    private final TransactionManager tm;

    public MySqlProductRelationshipTableDAO getSupplierProductsDAO() {
        return cartItemsDAO != null
                ? cartItemsDAO
                : new MySqlProductRelationshipTableDAO(cm, "cart") { };
    }

    public MySqlProductRelationshipTableDAO getWishlistProductsDAO(){
        return wishlistItemsDAO != null
                ? wishlistItemsDAO
                : new MySqlProductRelationshipTableDAO(cm, "wishlist") { };
    }

    public MySqlCustomerDAO(ConnectionManager cm) {
        this.cm = cm;
        this.tm = new TransactionManager(cm);
        this.cartItemsDAO = getSupplierProductsDAO();
        this.wishlistItemsDAO = getWishlistProductsDAO();
    }

    @Override
    public @NotNull Optional<Long> getCartId(long customerId) {
        try(Connection con = cm.getConnection()){
            return getCartId(con, customerId);
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    private Optional<Long> getCartId(Connection con, long customerId){
        String query = "select cart_id from customers where id = ?";

        return getColumnValue(con, query, "cart_id", customerId)
                .map(o -> ((Integer) o).longValue());
    }

    @Override
    public @NotNull Optional<Long> getWishlistId(long customerId) {
        try(Connection con = cm.getConnection()){
            return getWishlistId(con, customerId);
        } catch (SQLException e) {
            throw new DBException("Error getting wishlist id for customer id = " + customerId);
        }
    }

    public @NotNull Optional<Long> getWishlistId(Connection con, long customerId) {
        String query = "select wishlist_id from customers where id = ?";

        return getColumnValue(con, query, "wishlist_id", customerId)
                .map(o -> ((Integer)o).longValue());
    }

    @Override
    public void addToWishList(long customerId, ProductDescription prd) {
        tm.executeTransaction(con -> {
            long wishlistId;
            Optional<Long> wishlistIdOpt = getWishlistId(con, customerId);
            if(wishlistIdOpt.isEmpty()){
                wishlistId = cartItemsDAO.generateId(con);
                setWishlistId(con, customerId, wishlistId);
            }else{
                wishlistId = wishlistIdOpt.get();
            }

            wishlistItemsDAO.insert(con, wishlistId, prd);
        });
    }

    @Override
    public void addToCart(long customerId, ProductDescription prd) {
        tm.executeTransaction(con -> {
            long cartId;
            Optional<Long> cartIdOpt = getCartId(con, customerId);
            if(cartIdOpt.isEmpty()){
                cartId = cartItemsDAO.generateId(con);
                setCartId(con, customerId, cartId);
            }else{
                cartId = cartIdOpt.get();
            }
            cartItemsDAO.insert(con, cartId, prd);
        });
    }

    @Override
    public void removeProductFromWishlist(long customerId, long productId) {
        tm.executeTransaction(con -> {
            Optional<Long> wishlistId = getWishlistId(con, customerId);
            if (wishlistId.isEmpty())
                throw new EntityWasNotFoundException("Customer with id = " + customerId + " does not have a wishlist to delete an item from");

            wishlistItemsDAO.removeProduct(con, wishlistId.get(), productId);
        });
    }

    @Override
    public void removeProductFromCart(long customerId, long productId) {
        tm.executeTransaction(con -> {
            Optional<Long> cartId = getCartId(con, customerId);
            if (cartId.isEmpty())
                throw new EntityWasNotFoundException("Customer with id = " + customerId + " does not have a cart to delete an item from");

            cartItemsDAO.removeProduct(con, cartId.get(), productId);
        });
    }

    @Override
    public void setCartId(long customerId, long cartId) {
        tm.executeTransaction(con ->
            setCartId(con, customerId, cartId)
        );
    }

    private void setCartId(Connection con, long customerId, long cartId){
        String query = "update customers set cart_id = ? where id = ?";

        processUpdate(con, query, cartId, customerId);
    }

    @Override
    public void setWishlistId(long customerId, long wishlistId) {
        tm.executeTransaction(con ->
                setWishlistId(con, customerId, wishlistId)
        );
    }

    public void setWishlistId(Connection con, long customerId, long wishlistId) {
        String query = "update customers set wishlist_id = ? where id = ?";

        processUpdate(con, query, wishlistId, customerId);
    }

    @Override
    public Optional<Customer> getById(long id) {
        final String query = "select * from customers where id = ?";

        return getSingle(processQuery(cm, query, id));
    }

    @Override
    public @NotNull List<Customer> getByName(String name) {
        String query = "select * from customers where name = ?";

        return processQuery(cm, query, name);
    }

    @Override
    public @NotNull List<Customer> getByNameLike(String pattern) {
        String query = "select * from customers where name like ?";

        return processQuery(cm, query, pattern);
    }

    @Override
    public @NotNull List<Customer> getAll() {
        final String query = "select * from customers";

        return processQuery(cm, query);
    }

    @Override
    public void insertAll(Customer... customers) {
        Objects.requireNonNull(customers);

        tm.executeTransaction(con -> {
            for(Customer cs : customers)
                insert(con, Objects.requireNonNull(cs));
        });
    }

    @Override
    public void insert(Customer cs) {
        tm.executeTransaction(con ->
                insert(con, cs)
        );
    }

    private void insert(Connection con, Customer cs) throws SQLException {
        ConnectionManager.requireValidConnection(con);
        Objects.requireNonNull(cs);
        Date registrationDate = Date.valueOf(LocalDate.now().toString());

        /* Try to insert the customer into table 'customers' */
        String query = "INSERT INTO " +
                "customers(name, address, email, date_of_birth, registration_date)" +
                " value (?, ?, ?, ?, ?)";
        List<Object> keys = processUpdateAndGetGeneratedKeys(con, query,
                cs.getName(), cs.getAddress(), cs.getEmail(), cs.getDateOfBirth(), registrationDate);
        cs.setId(((BigInteger) keys.getFirst()).intValue());
        cs.setRegistrationDate(registrationDate);

        /* Handle case when customer has a cart or a wishlist */
        if (cs.getCart() != null) {
            generateProductContainerWithItems(con, cartItemsDAO, cs.getCart());
            setCartId(con, cs.getId(), cs.getCart().getId());
        }
        if (cs.getWishlist() != null) {
            generateProductContainerWithItems(con, wishlistItemsDAO, cs.getWishlist());
            setWishlistId(con, cs.getId(), cs.getWishlist().getId());
        }
    }

    private void generateProductContainerWithItems(Connection con,
                                                   @NotNull MySqlProductRelationshipTableDAO dao,
                                                   @NotNull ProductContainer pc){
        /* Generate cart_id */
        long pcId = dao.generateId(con);

        /* Set customer's 'cart_id' column */
        pc.setId(pcId);

        /* Add records into the cart_items table */
        dao.insertAll(con, pcId, pc.getContent());
    }

    @Override
    public void delete(long id) {
        tm.executeTransaction(con -> {
                    /* First delete associated cart and wishlist */
                    deleteProductContainerIfExists(con, this::getCartId, cartItemsDAO, id);
                    deleteProductContainerIfExists(con, this::getWishlistId, wishlistItemsDAO, id);

                    String query = "delete from customers where id = ?";
                    processUpdate(con, query, id);
                }
        );
    }

    private void deleteProductContainerIfExists(Connection con,
                                                @NotNull LongFunction<Optional<Long>> idGetter,
                                                MySqlProductRelationshipTableDAO dao,
                                                long customerId) {
        Optional<Long> id = idGetter.apply(customerId);
        id.ifPresent(el -> dao.delete(con, el));
    }

    @Contract("_ -> new")
    @Override
    public @NotNull Customer map(@NotNull ResultSet rs) throws SQLException {
        int customerID = rs.getInt("id");
        int cartId = rs.getInt("cart_id");
        int wishlistId = rs.getInt("wishlist_id");

        return new Customer(
                customerID,
                rs.getString("name"),
                rs.getString("address"),
                rs.getString("email"),
                rs.getDate("date_of_birth"),
                rs.getDate("registration_date"),
                cartId != 0 ? new ProductContainer(cartId, cartItemsDAO.getAllRelations(cartId)) : null,
                wishlistId != 0 ? new ProductContainer(wishlistId, wishlistItemsDAO.getAllRelations(wishlistId)) : null
        );
    }
}
