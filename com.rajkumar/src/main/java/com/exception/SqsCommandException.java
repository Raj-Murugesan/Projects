package com.exception;

public class SqsCommandException extends RuntimeException  {
    private String message;
    public SqsCommandException(String message){
        super(message);
        this.message=message;
    }
    public SqsCommandException(String message, Exception cause){
        super(message,cause);
        this.message=message;
    }
}
