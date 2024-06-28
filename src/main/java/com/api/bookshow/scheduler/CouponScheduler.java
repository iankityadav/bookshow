package com.api.bookshow.scheduler;

import com.api.bookshow.repository.CouponRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CouponScheduler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private CouponRepository couponRepository;

    @Scheduled(fixedRate = 600000) // Runs every 10 minutes
    public void deleteExpiredOrConsumedCoupons() {
        logger.info("Scheduler started");
        couponRepository.deleteExpiredOrConsumedCoupons();
        logger.info("Scheduler completed");
    }
}
