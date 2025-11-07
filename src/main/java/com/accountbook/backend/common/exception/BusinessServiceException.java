package com.accountbook.backend.common.exception;

/*业务异常类 */
public class BusinessServiceException extends RuntimeException{
    public BusinessServiceException(String message)
    {
        super(message);
    }
}
