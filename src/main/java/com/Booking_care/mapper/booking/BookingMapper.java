package com.Booking_care.mapper.booking;

import com.Booking_care.domain.Booking;
import com.Booking_care.domain.dto.BookingDTO.ResBookingDTO;
import com.Booking_care.mapper.clinic.ClinicMapper;
import com.Booking_care.mapper.doctor.DoctorMapper;
import com.Booking_care.mapper.patient.PatientMapper;

/**
 * Mapper for Booking entity
 */
public final class BookingMapper {
    
    private BookingMapper() {
        // Private constructor to prevent instantiation
    }

    /**
     * Convert Booking entity to ResBookingDTO
     * @param booking Booking entity
     * @return ResBookingDTO
     */
    public static ResBookingDTO toResBookingDTO(Booking booking) {
        if (booking == null) {
            return null;
        }

        ResBookingDTO dto = new ResBookingDTO();
        dto.setId(booking.getId());
        dto.setAppointmentDate(booking.getAppointmentDate());
        dto.setDescription(booking.getDescription());
        dto.setCreateAt(booking.getCreateAt());
        dto.setUpdateAt(booking.getUpdateAt());
        dto.setStatus(booking.getStatus());
        dto.setCheckFeedback(booking.getCheckFeedback());

        if (booking.getDoctor() != null) {
            dto.setDoctor(DoctorMapper.toResDoctorDTO(booking.getDoctor()));
        }

        if (booking.getPatient() != null) {
            dto.setPatient(PatientMapper.toResPatientDTO(booking.getPatient()));
        }

        if (booking.getClinic() != null) {
            dto.setClinic(ClinicMapper.toResClinicDTO(booking.getClinic()));
        }

        if (booking.getTime() != null) {
            dto.setTime(new ResBookingDTO.ResTimeDTO(
                    booking.getTime().getId(),
                    booking.getTime().getStart(),
                    booking.getTime().getEnd()));
        }

        return dto;
    }
}

