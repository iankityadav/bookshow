package com.api.bookshow.scheduler;

import com.api.bookshow.repository.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class CouponScheduler {

    @Autowired
    private CouponRepository couponRepository;

    @Scheduled(fixedRate = 600000) // Runs every 10 minutes
    public void deleteExpiredOrConsumedCoupons() {
//        couponRepository.deleteExpiredOrConsumedCoupons(today);
    }
}
