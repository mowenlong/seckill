package org.seckill.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by 莫文龙 on 2018/3/5.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml",
                        "classpath:spring/spring-service.xml"})
public class SeckillServiceTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    @Test
    public void getSeckillList() throws Exception {
        List<Seckill> list = seckillService.getSeckillList();
        logger.info("list={}",list);
    }

    @Test
    public void getById() throws Exception {
        long id = 1000;
        Seckill seckill = seckillService.getById(id);
        logger.info("seckill={}",seckill);
    }

    @Test
    public void exportSeckillUrl() throws Exception {
        long seckillId = 1000L;
        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
        if (exposer.isExposed()) {
            logger.info("exposer={}",exposer);
            long phone = 1999999999L;
            String md5 = exposer.getMd5();
            try {
                SeckillExecution execution = seckillService.executeSeckill(seckillId, phone, md5);
                logger.info("result={}",execution);
            }catch (RepeatKillException e) {
                logger.info(e.getMessage());
            }catch (SeckillCloseException e) {
                logger.info(e.getMessage());
            }
        }else {
            logger.warn("exposer",exposer);
        }
    }

    @Test
    public void executeSeckill() throws Exception {
        SeckillExecution seckillExecution = null;
        try {
            seckillExecution = seckillService.executeSeckill(1000L, 137274565007916L, "73a32cafc90ec242a61a0ba5cbcc36d7");
        } catch (RepeatKillException e) {
            logger.info(e.getMessage());
        } catch (SeckillCloseException e) {
            logger.info(e.getMessage());
        }
        logger.info("seckillExecution={}",seckillExecution);
    }

}