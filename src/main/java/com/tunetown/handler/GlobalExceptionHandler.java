//package com.tunetown.handler;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.server.ResponseStatusException;
//
//@ControllerAdvice
//public class GlobalExceptionHandler {
//    @ExceptionHandler(ResponseStatusException.class)
//    @ResponseBody
//    public ResponseStatusException handleResponseStatusException(ResponseStatusException ex) {
//        return new ResponseStatusException(ex.getStatusCode(), ex.getReason());
//    }
//
//    @ExceptionHandler(Exception.class)
//    @ResponseBody
//    public ResponseStatusException handleException(Exception ex) {
//        return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
//    }
//}
