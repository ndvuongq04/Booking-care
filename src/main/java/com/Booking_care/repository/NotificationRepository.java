package com.Booking_care.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Booking_care.domain.Notification;

public interface NotificationRepository extends JpaRepository<Notification,Long>{

    boolean existsByAccountId(long id);
    
}
