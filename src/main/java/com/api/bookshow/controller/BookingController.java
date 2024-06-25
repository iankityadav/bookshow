package com.api.bookshow.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/")
@RestController
public class BookingController {

    @GetMapping("/get")
    public String get(){
        return "test";
    }
}
