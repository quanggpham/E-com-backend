package com.example.project.core.exception;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;
import java.util.Map;

@Getter
@Setter
public class ErrorResponse {
    private Date timestamp;
    private int status;
    private String path;
    private String error;
    private String message;

    private Map<String, String> validationErrors;

    public ErrorResponse(int status, String path, String error, String message) {
        this.timestamp = new Date();
        this.status = status;
        this.path = path;
        this.error = error;
        this.message = message;
    }
}