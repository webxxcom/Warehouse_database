package main.java.ua.nure.cpp.jdbc.dao.abstr;

public class OperationWasNotPerformedException extends DBException{

    public OperationWasNotPerformedException(String message) {
        super(message);
    }

    public OperationWasNotPerformedException(String message, Exception ex) {
        super(message, ex);
    }

    public OperationWasNotPerformedException(Exception ex) {
        super(ex);
    }
}
