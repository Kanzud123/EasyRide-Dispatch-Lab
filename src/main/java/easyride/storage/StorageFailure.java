package easyride.storage;

public class StorageFailure extends RuntimeException {
    public StorageFailure(String message, Throwable cause) {
        super(message, cause);
    }
}
