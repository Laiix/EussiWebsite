package com.eussi.exception;

public class UserExistException extends Exception
{
    public UserExistException(String errorMsg)
    {
        super(errorMsg);
    }
}
