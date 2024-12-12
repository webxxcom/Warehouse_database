package main.java.ua.nure.cpp.jdbc.entity;

import org.jetbrains.annotations.NotNull;

public record ProductDescription(long id, int quantity) implements Comparable<ProductDescription>{
    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;

        return obj instanceof ProductDescription(long id1, int quantity1)
                && this.id == id1
                && this.quantity == quantity1;
    }

    @Override
    public int compareTo(@NotNull ProductDescription o) {
        return Long.compare(o.id, this.id);
    }
}

