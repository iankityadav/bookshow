package com.api.bookshow.repository;


import com.api.bookshow.model.Coupon;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    Optional<Coupon> findByCodeAndExpiryDateAfter(String couponCode, LocalDate now);
//    @Modifying
//    @Transactional
//    @Query("DELETE FROM Coupon c WHERE c.expiryDate < current_timestamp OR c.maxUsage <= 0")
//    void deleteExpiredOrConsumedCoupons(Long id);
}
