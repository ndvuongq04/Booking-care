package com.Booking_care.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Booking_care.domain.Feedback;

public interface FeedbackRepository extends JpaRepository<Feedback,Long>{

    boolean existsByDoctorId(long id);

    
}
