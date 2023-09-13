package main.exceptions;

public class ResponseStatusCodeError extends RuntimeException {
    public ResponseStatusCodeError(String msg) {
        super(msg);
    }
}
