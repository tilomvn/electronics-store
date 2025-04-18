package com.electronics.store.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.AllArgsConstructor;
import lombok.Getter;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { 
        CartIsEmptyException.class,
        InsufficientQuantityException.class, 
        InvalidDiscountTypeException.class,
        NoDiscountFoundForProduct.class,
        NoDiscountWithCriteriaException.class,
        NoSuchCartExist.class,
        NoSuchProductInStore.class,
        ProductNotFoundException.class, 
        ProductNotInCartException.class
     })
    protected ResponseEntity<Object> handleBadRequest(Exception ex, WebRequest request) {
        ErrorResponse bodyOfResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), ex.getMessage());
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @AllArgsConstructor
    @Getter
    class ErrorResponse {
        private int status;
        private String error;
        private String message;
    }
}
