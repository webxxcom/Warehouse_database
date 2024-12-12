package main.java.ua.nure.cpp.jdbc.entity;

import java.util.*;

public class Customer extends Partner{
    private final Date dateOfBirth;
    private Date registrationDate;
    private ProductContainer cart;
    private ProductContainer wishlist;

    /* Constructor to initialize Customer without a cart and a wishlist */
    public Customer(int id, String name, String address, String email,
                    Date dateOfBirth, Date registrationDate){
        this(id, name, address, email, dateOfBirth, registrationDate, null, null);
    }

    /* Constructor to add customers into the database */
    public Customer(String name, String address, String email,
                    Date dateOfBirth, ProductContainer cart, ProductContainer wishlist) {
        super(name, address, email);

        this.registrationDate = null;

        this.dateOfBirth = dateOfBirth;
        this.cart = cart == null ? null : new ProductContainer(cart);
        this.wishlist = wishlist == null ? null : new ProductContainer(wishlist);
    }

    public Customer(int id, String name, String address, String email,
                    Date dateOfBirth, Date registrationDate, ProductContainer cart,
                    ProductContainer wishlist) {
        super(id, name, address, email);

        this.dateOfBirth = dateOfBirth;
        this.registrationDate = registrationDate;
        this.cart = cart == null ? null : new ProductContainer(cart);
        this.wishlist = wishlist == null ? null : new ProductContainer(wishlist);
    }

    public ProductContainer getCart(){
        return cart;
    }

    public void setCart(ProductContainer cart) {
        this.cart = cart;
    }

    public ProductContainer getWishlist(){
        return wishlist;
    }

    public void setWishlist(ProductContainer wishlist) {
        this.wishlist = wishlist;
    }

    public Date getDateOfBirth(){
        return dateOfBirth;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;

        return o instanceof Customer that
                && super.equals(o)
                && Objects.equals(email, that.email)
                && Objects.equals(dateOfBirth, that.dateOfBirth)
                && Objects.equals(registrationDate, that.registrationDate)
                && Objects.equals(cart, that.cart)
                && Objects.equals(wishlist, that.wishlist);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", wishlist=" + wishlist +
                ", cart=" + cart +
                ", registrationDate=" + registrationDate +
                ", dateOfBirth=" + dateOfBirth +
                '}';
    }
}
