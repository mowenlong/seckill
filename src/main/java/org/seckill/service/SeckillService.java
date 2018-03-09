package org.seckill.service;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;

import java.util.List;

/**
 * Created by 莫文龙 on 2018/3/5.
 */
public interface SeckillService {

    /**
     * 查询所有的秒杀记录
     * @return
     */
    List<Seckill> getSeckillList();

    /**
     * 查询单个秒杀记录
     * @param id
     * @return
     */
    Seckill getById(Long id);


    /**
     * 秒杀开启时 输出秒杀的接口地址，
     * 否则就输出系统的时间和秒杀的时间
     * @param seckillId
     */
    Exposer exportSeckillUrl(Long seckillId);

    /**
     * 执行秒杀的操作
     * @param seckillId
     * @param userPhone
     * @param md5
     */
    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
    throws SeckillException,RuntimeException,SeckillCloseException;
}
