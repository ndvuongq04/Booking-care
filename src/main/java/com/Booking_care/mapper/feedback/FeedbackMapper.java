package com.Booking_care.mapper.feedback;

import com.Booking_care.domain.Feedback;
import com.Booking_care.domain.dto.ResFeedbackDTO;
import com.Booking_care.mapper.doctor.DoctorMapper;
import com.Booking_care.mapper.patient.PatientMapper;

/**
 * Mapper for Feedback entity
 */
public final class FeedbackMapper {
    
    private FeedbackMapper() {
        // Private constructor to prevent instantiation
    }

    /**
     * Convert Feedback entity to ResFeedbackDTO
     * @param feedback Feedback entity
     * @return ResFeedbackDTO
     */
    public static ResFeedbackDTO toResFeedbackDTO(Feedback feedback) {
        if (feedback == null) {
            return null;
        }

        ResFeedbackDTO res = new ResFeedbackDTO();
        res.setId(feedback.getId());
        res.setDescription(feedback.getDescription());
        res.setRate(feedback.getRate());

        // doctor
        if (feedback.getDoctor() != null) {
            res.setDoctor(DoctorMapper.toResDoctorDTO(feedback.getDoctor()));
        }

        // patient
        if (feedback.getPatient() != null) {
            res.setPatient(PatientMapper.toResPatientDTO(feedback.getPatient()));
        }

        return res;
    }
}
