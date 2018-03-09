package org.seckill.exception;

/**
 * Created by 莫文龙 on 2018/3/5.
 */
public class SeckillException extends RuntimeException {
    public SeckillException(String message) {
        super(message);
    }

    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }
}
