package com.example.demo.exception;

import com.fasterxml.jackson.databind.ser.Serializers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import com.example.project.core.exception.ErrorResponse;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public com.example.project.core.exception.ErrorResponse handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        logger.warn("ResourceNotFound: Message= {}", ex.getMessage());
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
    public ErrorResponse handleBusinessException(BaseException ex, WebRequest request) {

        logger.warn("BussinessException: Class= {} | Message= {}", ex.getClass().getName(), ex.getMessage());
        return new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                request.getDescription(false).replace("uri=", ""),
                "Business Error",
                ex.getMessage()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {

        logger.warn("MethodArgumentNotValidException: Message= {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                request.getDescription(false).replace("uri=", ""),
                "Validation Error",
                "Dữ liệu đầu vào không hợp lệ"
        );

        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        errorResponse.setValidationErrors(errors);

        return errorResponse;
    }

    // loi he thong
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public com.example.project.core.exception.ErrorResponse handleSystemException(Exception ex, WebRequest request) {

        logger.error("SystemException: ", ex);
        return new com.example.project.core.exception.ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request.getDescription(false).replace("uri=", ""),
                "Internal Server Error",
                "Lỗi hệ thống"
        );
    }
}