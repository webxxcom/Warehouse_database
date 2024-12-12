package main.java.ua.nure.cpp.jdbc.dao.abstr;

public class TransactionFailedException extends RuntimeException {
    public TransactionFailedException(String message){
        super(message);
    }

    public TransactionFailedException(String message, Exception ex){
        super(message,ex);
    }
}
