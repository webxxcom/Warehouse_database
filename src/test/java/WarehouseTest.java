package test.java;

import main.java.ua.nure.cpp.jdbc.entity.*;
import main.java.ua.nure.cpp.jdbc.dao.abstr.DAOFactory;
import main.java.ua.nure.cpp.jdbc.services.WarehouseService;
import org.junit.Test;

import java.sql.Date;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class WarehouseTest {
    static final String URL = "jdbc:mysql://localhost:3306/wh";
    static final String USERNAME = "root";
    static final String PASSWORD = "password";
    WarehouseService wh = new WarehouseService(
            DAOFactory.getInstance(DAOFactory.MY_SQL, URL, USERNAME, PASSWORD));

    /******************** Customer ******************/
    @Test
    public void testGetCustomer() {
        Customer expectedCustomer = new Customer(
                2, "Bob Smith", "456 Oak Ave", "bob@example.com",
                Date.valueOf("1985-05-22"), Date.valueOf("2024-11-26"),
                new ProductContainer(2),
                null
        );

        Optional<Customer> customerOpt = wh.getCustomer(2);
        assertTrue(customerOpt.isPresent());

        Customer customer = customerOpt.get();
        assertNotNull(customer.getCart());
        assertEquals(expectedCustomer.getName(), customer.getName());
        assertEquals(expectedCustomer.getEmail(), customer.getEmail());
        assertEquals(expectedCustomer.getDateOfBirth(), customer.getDateOfBirth());
    }

    @Test
    public void testGetCustomerCart() {
        int customerId = 1;
        Optional<Customer> customerOptional = wh.getCustomer(customerId);
        assertTrue("Customer with id " + customerId + " should be found", customerOptional.isPresent());

        List<ProductDescription> items = customerOptional.get().getCart().getContent();

        assertNotNull(items);
        assertEquals(3, items.size());

        ProductDescription[] prds = {
                new ProductDescription(1,  1), new ProductDescription(2, 2),
                new ProductDescription(4,  50)};
        for (int i = 0; i < prds.length; ++i)
            assertEquals(prds[i], items.get(i));
    }

    @Test
    public void testGetCustomerWishlist(){
        int customerId = 3;
        Optional<Customer> customerOptional = wh.getCustomer(customerId);
        assertTrue("Customer with id " + customerId + " should be found", customerOptional.isPresent());

        ProductContainer wl = customerOptional.get().getWishlist();
        assertNotNull(wl);
        assertEquals(2, wl.getId());

        List<ProductDescription> items = wl.getContent();
        assertNotNull(items);
        assertEquals(2, items.size());

        ProductDescription[] prds = {
                new ProductDescription(2,  5), new ProductDescription(4,  1)};
        for (int i = 0; i < prds.length; ++i)
            assertEquals(prds[i], items.get(i));
    }

    @Test
    public void testGetCustomerNullWishlist() {
        int customerId = 2;
        Optional<Customer> customerOptional = wh.getCustomer(customerId);
        assertTrue("Customer with id " + customerId + " should be found", customerOptional.isPresent());

        assertNull(customerOptional.get().getWishlist());
    }

    @Test
    public void testGetCustomerWithAll() {
        Customer expectedCustomer = new Customer(
                1, "Alice Johnson", "123 Main St", "alice@example.com",
                Date.valueOf("1990-01-15"), Date.valueOf("2024-11-26"),
                new ProductContainer(1, List.of(
                        new ProductDescription(1, 1),
                        new ProductDescription(2, 2),
                        new ProductDescription(4, 50)
                )),
                new ProductContainer(1, List.of(
                        new ProductDescription(6, 1)
                ))
        );

        Optional<Customer> actualCustomer = wh.getCustomer(1);
        assertTrue(actualCustomer.isPresent());
        assertEquals(expectedCustomer, actualCustomer.get());
    }

    @Test
    public void testInsertAndRemoveCustomer() {
        assertNotEquals("To test insert the number of customers should be less than " + WarehouseService.NUMBER_OF_CUSTOMERS,
                wh.getCustomers().size(), WarehouseService.NUMBER_OF_CUSTOMERS);

        Customer toAdd = new Customer(
                "Eve White", "999 Maple Dr", "eve@example.com",
                Date.valueOf("1995-07-14"),
                new ProductContainer(15, List.of()),
                new ProductContainer());

        wh.addCustomers(toAdd);

        Optional<Customer> csOpt = wh.getCustomer(toAdd.getId());
        assertTrue("Customer should have been inserted", csOpt.isPresent());
        wh.removeCustomer(toAdd.getId());

        Customer cs = csOpt.get();
        assertNotNull(cs.getCart());
        assertEquals(cs.getCart(), toAdd.getCart());
        assertEquals(cs.getEmail(), toAdd.getEmail());
    }

    @Test
    public void testInsertAndRemoveCustomerWithCart() {
        assertNotEquals("To test insert the number of customers should be less than " + WarehouseService.NUMBER_OF_CUSTOMERS,
                wh.getCustomers().size(), WarehouseService.NUMBER_OF_CUSTOMERS);

        Customer toAdd = new Customer(
                "Eve White", "999 Maple Dr", "eve@example.com",
                Date.valueOf("1995-07-14"),
                new ProductContainer(
                        new ProductDescription(5, 100),
                        new ProductDescription(2, 20)
                ),
                null);

        wh.addCustomers(toAdd);

        Optional<Customer> customerOptional = wh.getCustomer(toAdd.getId());
        assertNotNull("Customer should have been inserted", customerOptional);
        wh.removeCustomer(toAdd.getId());

        Customer cs = customerOptional.get();
        assertNotNull(cs.getCart());
        assertNull(cs.getWishlist());
        assertFalse(cs.getCart().getContent().isEmpty());
        assertEquals(cs.getCart(), toAdd.getCart());
        assertEquals(cs.getEmail(), toAdd.getEmail());
    }

    @Test
    public void testInsertAndRemoveCustomerWithWishlist() {
        assertNotEquals("To test insert the number of customers should be less than " + WarehouseService.NUMBER_OF_CUSTOMERS,
                wh.getCustomers().size(), WarehouseService.NUMBER_OF_CUSTOMERS);

        Customer toAdd = new Customer(
                "Eve White", "999 Maple Dr", "eve@example.com",
                Date.valueOf("1995-07-14"),
                null,
                new ProductContainer(
                        new ProductDescription(3, 100),
                        new ProductDescription(2, 12)
                )
        );

        wh.addCustomers(toAdd);

        Optional<Customer> csOpt = wh.getCustomer(toAdd.getId());
        assertTrue("Customer should have been inserted", csOpt.isPresent());
        wh.removeCustomer(toAdd.getId());

        Customer cs = csOpt.get();
        assertNull(cs.getCart());
        assertFalse(cs.getWishlist().getContent().isEmpty());
        assertEquals(cs.getWishlist(), toAdd.getWishlist());
        assertEquals(cs.getEmail(), toAdd.getEmail());
    }

    @Test
    public void testInsertAndRemoveCustomerWithAll() {
        assertNotEquals("To test insert the number of customers should be less than " + WarehouseService.NUMBER_OF_CUSTOMERS,
                wh.getCustomers().size(), WarehouseService.NUMBER_OF_CUSTOMERS);

        Customer toAdd = new Customer(
                "Eve White", "999 Maple Dr", "eve@example.com",
                Date.valueOf("1995-07-14"),
                new ProductContainer(
                        new ProductDescription(7, 2),
                        new ProductDescription(1, 14)),
                new ProductContainer(
                        new ProductDescription(3, 100),
                        new ProductDescription(2, 12)
                )
        );

        wh.addCustomers(toAdd);

        Optional<Customer> customerOptional = wh.getCustomer(toAdd.getId());
        assertNotNull("Customer should have been inserted", customerOptional);
        wh.removeCustomer(toAdd.getId());

        Customer cs = customerOptional.get();
        assertNotNull(cs.getCart());
        assertNotNull(cs.getWishlist());
        assertFalse(cs.getWishlist().getContent().isEmpty());
        assertFalse(cs.getCart().getContent().isEmpty());
        assertEquals(cs.getWishlist(), toAdd.getWishlist());
        assertEquals(cs.getCart(), toAdd.getCart());
        assertEquals(cs.getEmail(), toAdd.getEmail());
    }

    @Test
    public void testAddItemsToCustomerCart() {
        final long CUSTOMER_ID = 2;
        final ProductDescription prd = new ProductDescription(7, 2);
        wh.addProductToCustomerCart(CUSTOMER_ID, prd);

        ProductContainer cart = wh.getCustomer(CUSTOMER_ID).get().getCart();
        assertNotNull(cart);
        assertFalse(cart.getContent().isEmpty());
        assertTrue("Should contain the added product", cart.getContent().stream().anyMatch(el -> el.equals(prd)));
        assertEquals("Should not modify any other product",
                cart.getContent(), List.of(
                new ProductDescription(5, 3),
                prd));
        wh.removeFromCustomerCart(CUSTOMER_ID, prd.id());
    }

    @Test
    public void testAddItemsToCustomerWishlist() {
        final long CUSTOMER_ID = 3;
        final ProductDescription prd = new ProductDescription(5, 5);
        wh.addProductToCustomerWishlist(CUSTOMER_ID, prd);

        Optional<Customer> csOpt = wh.getCustomer(CUSTOMER_ID);
        assertTrue(csOpt.isPresent());

        ProductContainer wishlist = csOpt.get().getWishlist();
        assertNotNull(wishlist);
        assertFalse(wishlist.getContent().isEmpty());
        assertTrue("Should contain the added product", wishlist.getContent().stream().anyMatch(el -> el.equals(prd)));
        assertEquals("Should not modify any other product",
                List.of(
                        new ProductDescription(2, 5),
                        new ProductDescription(4, 1),
                        prd),
                wishlist.getContent());
        wh.removeFromCustomerWishlist(CUSTOMER_ID, prd.id());
    }

    @Test
    public void testGetAllCustomers(){
        List<Customer> ls = wh.getCustomers().stream().toList();

        assertNotNull(ls);
        assertEquals(3, ls.size());

        int[] ids = {1,2,3};
        for(int i = 0; i < ls.size(); ++i)
            assertEquals(ids[i], ls.get(i).getId());
    }

    /******************** Supplier ******************/
    @Test
    public void testGetSupplier() {
        Supplier expectedSupplier = new Supplier(
                3,
                "Acme Supplies",
                "123 Industrial Road, Cityville",
                "contact@acmesupplies.com"
        );

        Optional<Supplier> supplier = wh.getSupplier(3);
        assertTrue(supplier.isPresent());
        assertTrue(supplier.get().getProducts().isEmpty());
        assertEquals(expectedSupplier, supplier.get());
    }

    @Test
    public void testGetSupplierWithProducts() {
        Optional<Supplier> supplier = wh.getSupplier(2);
        assertTrue(supplier.isPresent());
        assertEquals("sales@techequip.com", supplier.get().getEmail());

        List<ProductDescription> productDescriptions = supplier.get().getProducts();
        assertNotNull(productDescriptions);
        assertFalse(productDescriptions.isEmpty());

        int[] ids = {3,4,7};
        for(int i = 0; i < ids.length; ++i)
            assertEquals(ids[i], productDescriptions.get(i).id());
    }

    @Test
    public void testInsertAndRemoveSupplier() {
        assertNotEquals("To test insert the number of suppliers should be less than " + WarehouseService.NUMBER_OF_SUPPLIERS,
                wh.getSuppliers().size(), WarehouseService.NUMBER_OF_SUPPLIERS);

        Supplier toAdd = new Supplier(
                "Eve White",
                "999 Maple Dr",
                "eve@example.com"
                );

        wh.addSuppliers(toAdd);

        Optional<Supplier> spOpt = wh.getSupplier(toAdd.getId());
        assertTrue("Supplier should have been inserted", spOpt.isPresent());
        wh.removeSupplier(toAdd.getId());

        Supplier sp = spOpt.get();
        assertNotNull(sp.getProducts());
        assertEquals(sp.getProducts(), sp.getProducts());
        assertEquals(sp.getEmail(), toAdd.getEmail());
    }

    @Test
    public void testInsertAndRemoveCustomerWithProducts() {
        assertNotEquals("To test insert the number of suppliers should be less than " + WarehouseService.NUMBER_OF_SUPPLIERS,
                wh.getSuppliers().size(), WarehouseService.NUMBER_OF_SUPPLIERS);

        Supplier toAdd = new Supplier(
                "Eve White",
                "999 Maple Dr",
                "eve@example.com",
                List.of(
                        new ProductDescription(4, 10),
                        new ProductDescription(7, 142),
                        new ProductDescription(1, 132)
                )
        );

        wh.addSuppliers(toAdd);
        assertNotEquals(Entity.NO_ID, toAdd.getId());

        Optional<Supplier> sp = wh.getSupplier(toAdd.getId());
        assertTrue("Supplier should have been inserted", sp.isPresent());
        wh.removeSupplier(toAdd.getId());

        List<ProductDescription> products = sp.get().getProducts();
        assertNotNull(products);
        assertFalse(products.isEmpty());
        assertEquals(toAdd.getProducts().stream().sorted().toList(), products.stream().sorted().toList());
        assertEquals(sp.get().getEmail(), toAdd.getEmail());
    }

    @Test
    public void testGetAllSuppliers() {
        List<Supplier> suppliers = wh.getSuppliers().stream().toList();

        assertNotNull(suppliers);
        assertEquals(4, suppliers.size());

        Supplier thirdSupplier = suppliers.get(2);
        assertEquals(thirdSupplier, new Supplier(3, "Acme Supplies", "123 Industrial Road, Cityville", "contact@acmesupplies.com", null));
    }

    /******************** All products ******************/
    @Test
    public void testGetAllProducts() {
        List<Product> products = wh.getAllProducts().stream().toList();

        assertNotNull(products);
        assertFalse(products.isEmpty());
        assertEquals(7, products.size()); // Based on schema
        assertEquals("Pack of 1000 Disposable Cups", products.getLast().getName());

        int[] ids = {1, 2, 3, 4, 5, 6, 7};
        for (int i = 0; i < ids.length; ++i) {
            assertEquals(ids[i], products.get(i).getId());
        }
    }

    /******************** Available products ******************/
    @Test
    public void testUpdateAvailableProductQuantity() {
        Optional<Product> productOpt = wh.getProduct(1);
        assertTrue(productOpt.isPresent());

        Product product = productOpt.get();
        int newQuantity = product.getQuantity() * 2;
        wh.updateProductQuantity(product.getId(), newQuantity);

        Product updatedProduct = wh.getProduct(1).get();
        assertEquals(newQuantity, updatedProduct.getQuantity());
        wh.updateProductQuantity(product.getId(), product.getQuantity());
    }

    @Test
    public void testInsertProductWithNull(){
        Collection<Product> productsBefore = wh.getAllProducts();

        assertThrows(NullPointerException.class,
                () -> wh.addProducts(new Product[]{null})
        );
        assertEquals(productsBefore, wh.getAllProducts());
    }

    @Test
    public void testInsertAndRemoveProduct(){
        assertNotEquals("To test insert the number of available products should be less than " + WarehouseService.NUMBER_OF_PRODUCTS,
                wh.getSuppliers().size(), WarehouseService.NUMBER_OF_PRODUCTS);

        Product toAdd = new Product("Toaster", 10.99,100, "Kitchen");
        wh.addProducts(toAdd);

        assertEquals(wh.getProduct(toAdd.getId()).get().getId(), toAdd.getId());
        wh.removeAvailableProduct(toAdd.getId());
        assertTrue(wh.getProduct(toAdd.getId()).isEmpty());
    }
}
