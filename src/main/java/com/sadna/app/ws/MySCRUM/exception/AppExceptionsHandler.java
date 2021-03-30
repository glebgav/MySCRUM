package com.sadna.app.ws.MySCRUM.exception;

import com.sadna.app.ws.MySCRUM.ui.model.response.ErrorMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@ControllerAdvice
public class AppExceptionsHandler {

    @ExceptionHandler(value = {ServiceException.class})
    public ResponseEntity<Object> handleServiceException(ServiceException ex , WebRequest request){

        ErrorMessage errorMessage = new ErrorMessage(new Date(),ex.getMessage());

        return new ResponseEntity<>(errorMessage,new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Object> handleOtherException(Exception ex , WebRequest request){

        ErrorMessage errorMessage = new ErrorMessage(new Date(),ex.getMessage());

        return new ResponseEntity<>(errorMessage,new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}

