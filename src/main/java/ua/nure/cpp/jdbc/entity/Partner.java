package main.java.ua.nure.cpp.jdbc.entity;


import java.util.*;

public abstract class Partner extends Entity {
    protected String address;
    protected String email;

    protected Partner(String name, String address, String email){
        super(name);

        this.address = address;
        this.email = email;
    }

    protected Partner(long id, String name, String address, String email){
        super(id, name);

        this.address = address;
        this.email = email;
    }

    public String getAddress(){
        return address;
    }

    public String getEmail(){
        return email;
    }

    @Override
    public String toString() {
        return super.toString() + ", " +
                "address='" + address + "', " +
                "email='" + email + "'";
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;

        return o instanceof Partner that
                && super.equals(that)
                && Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), address);
    }
}
