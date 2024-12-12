package main.java.ua.nure.cpp.jdbc.dao.abstr;

public class DBException extends RuntimeException{
    public DBException(String message){
        super(message);
    }

    public DBException(String message, Exception ex){
        super(message, ex);
    }

    public DBException(Exception ex){
        super(ex);
    }
}
