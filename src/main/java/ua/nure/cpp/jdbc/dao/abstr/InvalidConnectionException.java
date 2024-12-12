package main.java.ua.nure.cpp.jdbc.dao.abstr;

public class InvalidConnectionException extends RuntimeException{
    public InvalidConnectionException(String message, Exception ex){
        super(message, ex);
    }

    public InvalidConnectionException(Exception ex){
        super(ex);
    }

    public InvalidConnectionException(String message){
        super(message);
    }
}
