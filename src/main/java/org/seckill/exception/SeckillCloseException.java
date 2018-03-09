package org.seckill.exception;

/**
 * 秒杀关闭异常
 * Created by 莫文龙 on 2018/3/5.
 */
public class SeckillCloseException  extends  SeckillException{
    public SeckillCloseException(String message) {
        super(message);
    }

    public SeckillCloseException(String message, Throwable cause) {
        super(message, cause);
    }
}
