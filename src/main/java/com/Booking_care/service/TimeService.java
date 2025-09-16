package com.Booking_care.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.Booking_care.domain.Time;
import com.Booking_care.repository.TimeRepository;

@Service
public class TimeService {
    private final TimeRepository timeRepository;

    public TimeService(TimeRepository timeRepository) {
        this.timeRepository = timeRepository;
    }

    public List<Time> getAllTimes() {
        return this.timeRepository.findAll();
    }

    public Time fetchTimeById(long id) {
        return this.timeRepository.findById(id).orElse(null);
    }
}
