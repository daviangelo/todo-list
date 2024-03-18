package com.lessa.todolist.service.exception;

public class ConflictException extends Exception{
    public ConflictException(String message) {
        super(message);
    }
}
