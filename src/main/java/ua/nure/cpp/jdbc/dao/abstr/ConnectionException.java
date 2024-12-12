package main.java.ua.nure.cpp.jdbc.dao.abstr;

public class ConnectionException extends RuntimeException{
    public ConnectionException(String message, Exception ex){
        super(message, ex);
    }

    public ConnectionException(Exception ex){
        super(ex);
    }

    public ConnectionException(String message){
        super(message);
    }
}
