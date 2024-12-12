package main.java.ua.nure.cpp.jdbc.dao.mysql;

import main.java.ua.nure.cpp.jdbc.dao.abstr.*;
import main.java.ua.nure.cpp.jdbc.dao.abstr.daos.*;
import main.java.ua.nure.cpp.jdbc.dao.mysql.daos.*;

public class MySqlDAOFactory extends DAOFactory {

    public MySqlDAOFactory(String url, String user, String password){
        super(new ConnectionManager(url, user, password));
    }

    @Override
    public CustomerDAO getCustomerDAO() {
        return new MySqlCustomerDAO(cm);
    }

    @Override
    public SupplierDAO getSupplierDAO() {
        return new MySqlSupplierDAO(cm);
    }

    @Override
    public AllProductsDAO getAllProductsDAO() {
        return new MySqlAllProductsDAO(cm);
    }

    @Override
    public JournalDAO getSalesJournalDAO() {
        return new MySqlSalesJournalDAO(cm);
    }

    @Override
    public JournalDAO getSupplyJournalDAO() {
        return new MySqlSupplyJournalDAO(cm);
    }
}
