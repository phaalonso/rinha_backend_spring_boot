package com.example.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY)
public class InvalidPeopleDataException extends RuntimeException{
    public InvalidPeopleDataException(String message) {
        super(message);
    }
}
