package com.graph.self_exceptions;


public class InitializeException extends RuntimeException{
    public InitializeException(){
        super();
    }
    public InitializeException(String message){
        super(message);
    }
}
