package com.api.bookshow.controller;

import com.api.bookshow.model.Coupon;
import com.api.bookshow.service.CouponService;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/coupon")
@RestController
public class CouponController {

    @Autowired
    private CouponService couponService;

    @RolesAllowed("ADMIN")
    @PostMapping
    public Coupon createCoupon(@RequestBody Coupon coupon) {
        return couponService.createCoupon(coupon);
    }
}
