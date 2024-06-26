package com.api.bookshow.repository;


import com.api.bookshow.model.Coupon;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
//    @Modifying
//    @Transactional
//    @Query("DELETE FROM Coupon c WHERE c.expiryDate < current_timestamp OR c.maxUsage <= 0")
//    void deleteExpiredOrConsumedCoupons(Long id);
}
