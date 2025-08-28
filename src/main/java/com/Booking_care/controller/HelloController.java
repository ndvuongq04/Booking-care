package com.Booking_care.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class HelloController {

    @GetMapping("/sayHello")
    public String getMethodName() {
        return "Hello word hehe";
    }

}
