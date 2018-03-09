package org.seckill.service.impl;

import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStatEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by 莫文龙 on 2018/3/5.
 */
@Service
public class SeckillServiceImpl implements SeckillService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillDao seckillDao;

    @Autowired
    private SuccessKilledDao successKilledDao;

    //md5的盐值
    private final String slat = "asfasnlnalfknaafabfagsyfgaasifoasyfasa";

    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0,4);
    }

    public Seckill getById(Long id) {
        return seckillDao.queryById(id);
    }

    private String getMD5(long seckillId) {
        String base = seckillId + "/" + slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    //暴露秒杀接口的地址
    public Exposer exportSeckillUrl(Long seckillId) {
        Seckill seckill = seckillDao.queryById(seckillId);
        if (seckill == null) return new Exposer(false,seckillId);
        long nowTime = System.currentTimeMillis();
        if (nowTime < seckill.getStartTime().getTime() || nowTime > seckill.getEndTime().getTime()) {
            return new Exposer(false,seckillId,nowTime,seckill.getStartTime().getTime(),seckill.getEndTime().getTime());
        }
        String md5 = getMD5(seckillId);
        return new Exposer(true,md5,seckillId);
    }

    //执行秒杀
    @Transactional
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException, RuntimeException, SeckillCloseException {
        if (md5 == null || "".equals(md5) || !getMD5(seckillId).equals(md5)) {
            throw new SeckillException("秒杀数据重写");
        }
        //执行秒杀逻辑，减库存 + 记录购买记录
        //只要已出现异常，Spring框架 就会为我们做事物的回滚
        try {
            int updateCount = seckillDao.reduceNumber(seckillId, new Date());
            if (updateCount <= 0) {
                //没有更新记录，秒杀失败
                throw new SeckillCloseException("秒杀关闭了");
            } else {
                int count = successKilledDao.insertSuccessKilled(seckillId, userPhone);
                if (count <= 0) {
                    throw new RepeatKillException("重复秒杀了");
                } else {
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS,successKilled);
                }
            }
        }catch (SeckillCloseException e) {
            throw new SeckillCloseException("秒杀关闭了" + e.getMessage());
        }catch (RepeatKillException e) {
            throw new RepeatKillException("重复秒杀了" + e.getMessage());
        }catch (Exception e) {
            logger.error(e.getMessage());
            throw new SeckillException("秒杀异常" + e.getMessage());
        }
    }
}
