package org.example.exceptions;

public class StorageIsFullException extends RuntimeException {

    public StorageIsFullException(String message) {
        super(message);
    }
}
