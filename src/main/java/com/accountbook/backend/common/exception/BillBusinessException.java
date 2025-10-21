package com.accountbook.backend.common.exception;

/*账单业务异常类 */
public class BillBusinessException extends RuntimeException{
    public BillBusinessException(String message)
    {
        super(message);
    }
}
