package one.aitec.ddb;

public class StorageRuntimeException extends RuntimeException {

    public StorageRuntimeException() {
    }

    public StorageRuntimeException(String message) {
        super(message);
    }

    public StorageRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public StorageRuntimeException(Throwable cause) {
        super(cause);
    }

    public StorageRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
