package org.seckill.exception;

/**
 * 重复秒杀异常
 * Created by 莫文龙 on 2018/3/5.
 */
public class RepeatKillException extends RuntimeException {
    public RepeatKillException(String message) {
        super(message);
    }

    public RepeatKillException(String message, Throwable cause) {
        super(message, cause);
    }
}
