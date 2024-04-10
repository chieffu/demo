package com.chieffu.pocker.blackjack;

public class NotFoundException extends Exception{
    public NotFoundException(String message){
        super(message);
    }

    public NotFoundException(String message,Throwable t){
        super(message, t);
    }
}
