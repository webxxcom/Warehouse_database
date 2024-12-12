package main.java.ua.nure.cpp.jdbc.dao.abstr;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface TransactionCallback {
    void doInTransaction(Connection connection) throws SQLException;
}

