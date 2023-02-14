package exception;

public class UnsupportedStockTypeException extends RuntimeException {
    public UnsupportedStockTypeException(String message) {
        super(message);
    }
}
