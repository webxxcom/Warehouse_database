package main.java.ua.nure.cpp.jdbc.dao.abstr;

import main.java.ua.nure.cpp.jdbc.dao.mysql.MySqlDAOFactory;
import main.java.ua.nure.cpp.jdbc.dao.abstr.daos.*;
import org.jetbrains.annotations.Nullable;

public abstract class DAOFactory {
    public static final int MY_SQL = 1;
    public final ConnectionManager cm;

    protected DAOFactory(ConnectionManager cm) {
        this.cm = cm;
    }

    public static @Nullable DAOFactory getInstance(int whichFactory, String url, String user, String password){
        return switch(whichFactory){
            case MY_SQL -> new MySqlDAOFactory(url, user, password);
            default -> null;
        };
    }

    public abstract CustomerDAO getCustomerDAO();
    public abstract SupplierDAO getSupplierDAO();
    public abstract AllProductsDAO getAllProductsDAO();
    public abstract JournalDAO getSalesJournalDAO();
    public abstract JournalDAO getSupplyJournalDAO();
}
