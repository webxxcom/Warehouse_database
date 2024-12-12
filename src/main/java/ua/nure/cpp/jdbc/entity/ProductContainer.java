package main.java.ua.nure.cpp.jdbc.entity;

import java.util.*;

public class ProductContainer {
    long id;
    final List<ProductDescription> content;

    public ProductContainer(){
        this(Entity.NO_ID, List.of());
    }

    public ProductContainer(long id) {
        this(id, List.of());
    }

    public ProductContainer(ProductDescription... products) {
        this(Entity.NO_ID, Arrays.stream(products).toList());
    }

    public ProductContainer(long id, ProductDescription... products) {
        this(id, Arrays.stream(products).toList());
    }

    public ProductContainer(List<ProductDescription> products) {
        this(Entity.NO_ID, products);
    }

    public ProductContainer(long id, List<ProductDescription> products) {
        this.id = id;
        this.content = products;
    }

    public ProductContainer(ProductContainer other){
        Objects.requireNonNull(other);

        this.id = other.id;
        this.content = other.content;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean has(ProductDescription prd){
        return content.stream()
                .anyMatch(el -> el.id() == prd.id() && el.quantity() >= prd.quantity());
    }

    public List<ProductDescription> getContent(){
        return content;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;

        return obj instanceof ProductContainer that
                && that.id == id
                && Objects.equals(
                        that.content.stream().sorted().toList(),
                content.stream().sorted().toList()
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, content);
    }

    @Override
    public String toString() {
        return "ProductContainer{" +
                "id=" + id +
                ", content=" + content +
                '}';
    }
}
