package main.java.ua.nure.cpp.jdbc.dao.mysql.daos;

import main.java.ua.nure.cpp.jdbc.dao.abstr.TransactionManager;
import main.java.ua.nure.cpp.jdbc.entity.Product;
import main.java.ua.nure.cpp.jdbc.entity.Supplier;
import main.java.ua.nure.cpp.jdbc.dao.abstr.ConnectionManager;
import main.java.ua.nure.cpp.jdbc.dao.abstr.DBException;
import main.java.ua.nure.cpp.jdbc.dao.abstr.daos.SupplierDAO;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class MySqlSupplierDAO implements SupplierDAO {
    private final MySqlProductRelationshipTableDAO supplierProductsDAO;
    private final ConnectionManager cm;
    private final TransactionManager tm;

    @Override
    public @NotNull MySqlProductRelationshipTableDAO getSupplierProductsDAO() {
        return supplierProductsDAO != null
                ? supplierProductsDAO
                : new MySqlProductRelationshipTableDAO(cm, "supplier"){

            @Override
            public Optional<Product> getById(long customerId, long productId) {
                String query = """
                            SELECT
                                pr.id,
                                pr.name,
                                dm.quantity,
                                pr.price,
                                pr.category
                            FROM
                                all_products pr
                            JOIN
                                %s dm ON pr.id = dm.product_id
                            WHERE
                                pr.id = ? AND
                            EXISTS(
                                SELECT 1
                                FROM
                                    suppliers s
                                WHERE
                                s.id = ? AND s.id = dm.%s)
                        """.formatted(tableName, columnName);

                return getSingle(processQuery(cm, query, productId, customerId));
            }

            @Override
            public List<Product> getAll(long supplierId) {
                String query = """
                            SELECT
                                pr.id,
                                pr.name,
                                dm.quantity,
                                pr.price,
                                pr.category
                            FROM
                                all_products pr
                            JOIN
                                %s dm ON pr.id = dm.product_id
                            WHERE
                                EXISTS(
                                    SELECT 1
                                    FROM
                                        suppliers s
                                    WHERE
                                        s.id = ? AND s.id = dm.%s)
                        """.formatted(tableName, columnName);

                return processQuery(cm, query, supplierId);
            }
        };
    }

    public MySqlSupplierDAO(ConnectionManager cm) {
        this.cm = cm;
        this.tm = new TransactionManager(cm);
        this.supplierProductsDAO = getSupplierProductsDAO();
    }

    @Override
    public Optional<Supplier> getById(long id) {
        String query = "select * from suppliers where id = ?";

        return getSingle(processQuery(cm, query, id));
    }

    @Override
    public @NotNull List<Supplier> getByName(String name) {
        String query = "select * from suppliers where name = ?";

        return processQuery(cm, query, name);
    }

    @Contract(pure = true)
    @Override
    public @NotNull List<Supplier> getByNameLike(String pattern) {
        String query = "select * from suppliers where name like ?";

        return processQuery(cm, query, pattern);
    }

    @Override
    public @NotNull List<Supplier> getAll() {
        String query = "select * from suppliers";

        return processQuery(cm, query);
    }

    @Override
    public void insertAll(Supplier... suppliers) {
        Objects.requireNonNull(suppliers);
        tm.executeTransaction(con ->{
            for(Supplier sp : suppliers)
                insert(con, sp);
        });
    }

    @Override
    public void insert(Supplier supplier) {
        tm.executeTransaction(con ->{
            insert(con, supplier);
        });
    }

    public void insert(Connection con, Supplier s) {
        Objects.requireNonNull(s);

        String query = "insert into suppliers(name, address, email) value(?, ?, ?)";
        List<Object> rs = processUpdateAndGetGeneratedKeys(con, query,
                s.getName(), s.getAddress(), s.getEmail());
        s.setId(((Number) rs.getFirst()).longValue());

        if(!s.getProducts().isEmpty()){
            supplierProductsDAO.insertAll(con, s.getId(), s.getProducts());
        }
    }

    @Override
    public void delete(long supplierId) {
        tm.executeTransaction(con ->
            delete(con, supplierId)
        );
    }

    public void delete(Connection con, long supplierId){
        String query = "delete from suppliers where id = ?";
        processUpdate(con, query, supplierId);
    }

    @Contract("_ -> new")
    @Override
    public @NotNull Supplier map(@NotNull ResultSet rs) throws SQLException {
        long id = rs.getInt("id");

        return new Supplier(
                id,
                rs.getString("name"),
                rs.getString("address"),
                rs.getString("email"),
                supplierProductsDAO.getAllRelations(id)
        );
    }
}
