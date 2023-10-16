package com.tunetown.handler;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResponseStatusException.class)
    @ResponseBody
    public ResponseStatusException handleResponseStatusException(ResponseStatusException ex) {
        return new ResponseStatusException(ex.getStatusCode(), ex.getReason());
    }
}
