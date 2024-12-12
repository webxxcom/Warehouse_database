package main.java.ua.nure.cpp.jdbc.dao.mysql.daos;

import main.java.ua.nure.cpp.jdbc.dao.abstr.TransactionManager;
import main.java.ua.nure.cpp.jdbc.entity.Product;
import main.java.ua.nure.cpp.jdbc.entity.ProductDescription;
import main.java.ua.nure.cpp.jdbc.dao.abstr.ConnectionManager;
import main.java.ua.nure.cpp.jdbc.dao.abstr.DBException;
import main.java.ua.nure.cpp.jdbc.dao.abstr.daos.AllProductsDAO;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MySqlAllProductsDAO implements AllProductsDAO {

    private final ConnectionManager cm;
    private final TransactionManager tm;

    public MySqlAllProductsDAO(ConnectionManager cm) {
        this.cm = cm;
        this.tm = new TransactionManager(cm);
    }

    @Override
    public Optional<Integer> getQuantity(long productId) {
        try(Connection con = cm.getConnection()){
            String query = "select quantity from all_products where id = ?";
            return getColumnValue(con, query, "quantity", productId);
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    @Override
    public Optional<Product> getById(long id) {
        String query = "select * from all_products where id = ?";

        return getSingle(processQuery(cm, query, id));
    }

    @Override
    public List<Product> getByName(String name) {
        String query = "select * from all_products where name = ?";

        return processQuery(cm, query, name);
    }

    @Override
    public List<Product> getByNameLike(String pattern) {
        String query = "select * from all_products where name like ?";

        return processQuery(cm, query, pattern);
    }

    @Override
    public List<Product> getByCategoryLike(String pattern) {
        String query = "select * from all_products where category like ?";

        return processQuery(cm, query, pattern);
    }

    @Override
    public void updateQuantity(long productId, int newQuantity) {
        String query = "update all_products " +
                "set quantity = ? " +
                "where id = ?";
        processUpdate(cm, query, newQuantity, productId);
    }

    @Override
    public void take(long productId, int quantity) {
        String query = "update all_products " +
                "set quantity = ? " +
                "where id = ?";

        Optional<?> quantityOpt = getQuantity(productId);
        if (quantityOpt.isEmpty()) {
            throw new DBException("product with such id " + productId + " does not exist in table 'al_products'");
        }
        processUpdate(cm, query, ((Number) quantityOpt.get()).intValue() - quantity, productId);
    }

    @Override
    public void insert(Product product) {
        tm.executeTransaction(con -> insert(con, product));
    }

    public void insert(Connection con, Product product){
        Objects.requireNonNull(product);

        String query = "insert into all_products (id, name, price, category, quantity)" +
                " value (?, ?, ?, ?, ?)";
        processUpdate(con, query,
                product.getId(), product.getName(), product.getPrice(), product.getCategory(), product.getQuantity());
    }

    @Override
    public List<Product> getAll() {
        String query = "select * from all_products";

        return processQuery(cm, query);
    }

    @Override
    public void insertAll(Product[] entities) {
        if (entities == null || entities.length == 0) {
            return;
        }

        Arrays.stream(entities).forEach(ent ->
                tm.executeTransaction(con -> insert(con, ent))
        );
    }

    @Override
    public void delete(long productId) {
        String query = "delete from all_products where id = ?";
        processUpdate(cm, query, productId);
    }

    @Override
    public Product map(@NotNull ResultSet rs) throws SQLException {
        return new Product(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getBigDecimal("price"),
                rs.getInt("quantity"),
                rs.getString("category")
        );
    }
}
