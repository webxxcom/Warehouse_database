package main.java.ua.nure.cpp.jdbc.entity;

import java.util.*;

public class Supplier extends Partner{
    private final ProductContainer products;

    public Supplier(String name, String address, String email) {
        this(NO_ID, name, address, email);
    }

    public Supplier(String name, String address, String email, List<ProductDescription> products) {
        this(NO_ID, name, address, email, products);
    }

    public Supplier(long id, String name, String address, String email) {
        this(id, name, address, email, new ArrayList<>());
    }

    public Supplier(long id, String name, String address, String email, List<ProductDescription> products) {
        super(id, name, address, email);

        this.products = new ProductContainer(id, products);
    }

    public List<ProductDescription> getProducts() {
        if (products == null)
            return List.of();

        return products.content;
    }

    @Override
    public void setId(long id) {
        super.setId(id);
        products.setId(id);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || o.getClass() != getClass()) return false;

        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "Supplier{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", email='" + email + '\'' +
                ", products=" + products +
                '}';
    }
}
