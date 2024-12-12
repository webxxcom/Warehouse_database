package main;

import main.java.ua.nure.cpp.jdbc.entity.Product;
import main.java.ua.nure.cpp.jdbc.entity.ProductDescription;
import main.java.ua.nure.cpp.jdbc.services.WarehouseService;
import main.java.ua.nure.cpp.jdbc.dao.abstr.DAOFactory;

import java.io.*;

public class Main {
    static final String URL;
    static final String USERNAME;
    static final String PASSWORD;

    static WarehouseService wh;


    static{
        try (BufferedReader br = new BufferedReader(new FileReader("src\\main\\resources\\MySqlRoot.d"))) {
            URL = br.readLine();
            USERNAME = br.readLine();
            PASSWORD = br.readLine();
            wh  = new WarehouseService(
                    DAOFactory.getInstance(
                            DAOFactory.MY_SQL, URL, USERNAME, PASSWORD));
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    static void showUpdating(){
        System.out.println("Before updating...");
        System.out.println(wh.getProduct(3));

        wh.updateProductQuantity(3, 4);
        System.out.println("After updating");
        System.out.println(wh.getProduct(3));
    }

    static void showRemoving(){
        System.out.println("Before removing...");
        wh.getAllProducts().forEach(System.out::println);

        wh.removeAvailableProduct(prd.getId());
        System.out.println("After removing...");
        wh.getAllProducts().forEach(System.out::println);
    }

    static Product prd = new Product("Toaster", 10.99,100, "Kitchen");
    static void showInsertion(){
        wh.addProducts(prd);
        System.out.println("After insertion...");
        wh.getAllProducts().forEach(System.out::println);
    }

    static void showAddingToCustomersCart(){
        int customerId = 3;
        int productId = 2;

        System.out.println("Before adding...");
        System.out.println(wh.getCustomer(customerId).get().getCart());
        System.out.println(wh.getProduct(productId).get());

        wh.addProductToCustomerCart(customerId, new ProductDescription(productId, 3));
        System.out.println("\nAfter Adding...");
        System.out.println(wh.getCustomer(customerId).get().getCart());
        System.out.println(wh.getProduct(productId).get());

        System.out.println("\nAnd delete...");
        wh.removeFromCustomerCart(customerId, productId);
        System.out.println(wh.getCustomer(customerId).get().getCart());
        System.out.println(wh.getProduct(productId).get());
    }

    public static void main(String[] args) {
        System.out.println("\nWe can see all the available products in the warehouse");
        wh.getAllProducts().forEach(System.out::println);

        System.out.println("\nWe can update the available product information");
        showUpdating();

        System.out.println("\nAnd insert product into the table");
        showInsertion();

        System.out.println("\nWe can remove the product");
        showRemoving();

        System.out.println("\nWe can add this product to the customer's cart which takes it from available products");
        showAddingToCustomersCart();
    }
}