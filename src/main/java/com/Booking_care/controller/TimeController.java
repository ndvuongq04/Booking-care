package com.Booking_care.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Booking_care.domain.Time;
import com.Booking_care.service.TimeService;
import com.Booking_care.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1")
public class TimeController {
    private final TimeService timeService;

    public TimeController(TimeService timeService) {
        this.timeService = timeService;
    }

    @GetMapping("/times")
    @ApiMessage("Fetch all time")
    public ResponseEntity<List<Time>> getAllTimes() {
        return ResponseEntity.ok(this.timeService.getAllTimes());
    }

    @GetMapping("/times/{id}")
    @ApiMessage("Fetch time by id")
    public ResponseEntity<Time> getTimeById(@PathVariable("id") long id) {
        return ResponseEntity.ok(this.timeService.fetchTimeById(id));
    }
}
