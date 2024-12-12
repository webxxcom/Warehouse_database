package main.java.ua.nure.cpp.jdbc.dao.abstr;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;

public class TransactionManager {
    private final ConnectionManager cm;

    public TransactionManager(ConnectionManager connectionSource) {
        this.cm = connectionSource;
    }

    public void executeTransaction(@NotNull TransactionCallback callback) {
        try (Connection con = cm.getConnection()) {
            try {
                ConnectionManager.beginTransaction(con);

                callback.doInTransaction(con);

                ConnectionManager.endTransaction(con);
            } catch(RuntimeException ex){
                ConnectionManager.rollback(con);
                throw ex;
            }

        } catch (SQLException e) {
            throw new TransactionFailedException("Error managing transaction", e);
        }
    }
}
