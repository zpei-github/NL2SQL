package com.web.my_exception;


public class InitializeException extends RuntimeException{
    public InitializeException(){
        super();
    }
    public InitializeException(String message){
        super(message);
    }
}
