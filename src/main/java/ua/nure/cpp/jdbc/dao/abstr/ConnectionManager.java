package main.java.ua.nure.cpp.jdbc.dao.abstr;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
    private final String url;
    private final String user;
    private final String password;

    public ConnectionManager(String url, String user, String password){
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public Connection getConnection() throws ConnectionException{
        return getConnection(true, Connection.TRANSACTION_READ_COMMITTED);
    }

    public Connection getConnection(boolean autoCommit) throws ConnectionException{
        return getConnection(autoCommit, Connection.TRANSACTION_READ_COMMITTED);
    }

    public Connection getConnection(boolean autoCommit, int transactionLevel)
            throws ConnectionException{
        try{
            Connection con = DriverManager.getConnection(url, user, password);
            con.setAutoCommit(autoCommit);
            con.setTransactionIsolation(transactionLevel);

            return con;
        } catch (SQLException e) {
            throw new ConnectionException("Error while initializing Connection", e);
        }
    }

    public static void requireValidConnection(Connection con) throws SQLException {
        if(con == null || con.isClosed())
            throw new InvalidConnectionException("Connections passed to ConnectionManager must not be null and not be closed");
    }

    public static void beginTransaction(Connection con){
        try {
            requireValidConnection(con);

            con.setAutoCommit(false);
        }catch(SQLException ex){
            throw new ConnectionException("Error when trying to begin transaction", ex);
        }
    }

    public static void endTransaction(Connection con){
        try{
            requireValidConnection(con);

            con.commit();
            con.setAutoCommit(true);
        } catch(SQLException ex){
            throw new ConnectionException("Error when trying to end the transaction", ex);
        }
    }

    public static void rollback(Connection con) {
        try {
            requireValidConnection(con);

            con.rollback();
        }catch (SQLException ex){
            throw new ConnectionException("Error when trying to rollback in connection", ex);
        }
    }

    public static void closeConnection(Connection con) throws ConnectionException {
        try {
            if (con != null && !con.isClosed())
                con.close();
        }catch (SQLException ex) {
            throw new ConnectionException("Error when closing connection", ex);
        }
    }
}
