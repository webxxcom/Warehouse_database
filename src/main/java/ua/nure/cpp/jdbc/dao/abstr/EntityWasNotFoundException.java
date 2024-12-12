package main.java.ua.nure.cpp.jdbc.dao.abstr;

public class EntityWasNotFoundException extends DBException{
    public EntityWasNotFoundException(String message) {
        super(message);
    }

    public EntityWasNotFoundException(String message, Exception ex) {
        super(message, ex);
    }

    public EntityWasNotFoundException(Exception ex) {
        super(ex);
    }
}
