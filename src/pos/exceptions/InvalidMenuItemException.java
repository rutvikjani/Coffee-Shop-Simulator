package pos.exceptions;

//Custom exceptions for menu items
public class InvalidMenuItemException extends Exception {
    public InvalidMenuItemException(String message) {
        super(message);
    }
}
