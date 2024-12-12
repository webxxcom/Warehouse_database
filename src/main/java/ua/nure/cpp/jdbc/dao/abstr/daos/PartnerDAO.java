package main.java.ua.nure.cpp.jdbc.dao.abstr.daos;

import main.java.ua.nure.cpp.jdbc.entity.Partner;
import main.java.ua.nure.cpp.jdbc.entity.Product;
import main.java.ua.nure.cpp.jdbc.entity.ProductDescription;
import main.java.ua.nure.cpp.jdbc.dao.mysql.daos.RelationshipTableDAO;

public interface PartnerDAO<T extends Partner> extends EntityDAO<T> {
    RelationshipTableDAO<ProductDescription,Product> getSupplierProductsDAO();
}
