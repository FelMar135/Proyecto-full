package com.example.pago_service.exception;

public class RemoteServiceException extends RuntimeException {

    public RemoteServiceException(String message) {
        super(message);
    }
}