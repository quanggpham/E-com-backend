package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public com.example.project.core.exception.ErrorResponse handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        return new com.example.project.core.exception.ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                request.getDescription(false).replace("uri=", ""),
                "Resource Not Found",
                ex.getMessage()
        );
    }

    // loi nghiep vu
    @ExceptionHandler(BaseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public com.example.project.core.exception.ErrorResponse handleBusinessException(BaseException ex, WebRequest request) {

        return new com.example.project.core.exception.ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                request.getDescription(false).replace("uri=", ""),
                "Business Error",
                ex.getMessage()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", 400);
        response.put("message", "Dữ liệu đầu vào không hợp lệ");

        // chi tiet tung field
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        response.put("errors", errors);
        return response;
    }

    // loi he thong
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public com.example.project.core.exception.ErrorResponse handleSystemException(Exception ex, WebRequest request) {

        return new com.example.project.core.exception.ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request.getDescription(false).replace("uri=", ""),
                "Internal Server Error",
                "Lỗi hệ thống"
        );
    }
}