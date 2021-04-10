package com.sadna.app.ws.MySCRUM.exception;

/**
 * Custom service exception
 */
public class ServiceException extends RuntimeException {
    public ServiceException(String message){
        super(message);
    }
}
