package com.Booking_care.service.specification;

import java.time.LocalDate;
import org.springframework.data.jpa.domain.Specification;
import com.Booking_care.domain.Booking_;
import com.Booking_care.domain.Clinic_;
import com.Booking_care.domain.Doctor_;
import com.Booking_care.domain.Patient_;
import com.Booking_care.domain.Account_;
import com.Booking_care.domain.Booking;
import com.Booking_care.util.SpecUtil;

public class BookingSpecs {
    public static Specification<Booking> patientAccountNameLikeIgnoreCase(String accountName) {
        return SpecUtil.joinLikeIgnoreCase(Booking_.patient, Patient_.account, Account_.name, accountName);
    }

    public static Specification<Booking> patientAccountPhoneNumberLikeIgnoreCase(String phoneNumber) {
        return SpecUtil.joinLikeIgnoreCase(Booking_.patient, Patient_.account, Account_.phoneNumber, phoneNumber);
    }

    public static Specification<Booking> dateBetween(LocalDate from, LocalDate to) {
        return SpecUtil.between(Booking_.appointmentDate, from, to);
    }

    public static Specification<Booking> doctorIdLikeIgnoreCase(String doctorName) {
        return SpecUtil.joinLikeIgnoreCase(Booking_.doctor, Doctor_.account, Account_.name, doctorName);
    }

    public static Specification<Booking> doctorIdEqual(Long doctorId) {
        return doctorId == null ? null
                : SpecUtil.joinEqual(Booking_.doctor, Doctor_.id, doctorId);
    }

    public static Specification<Booking> clinicIdEqual(Long clinicId) {
        return clinicId == null ? null
                : SpecUtil.joinEqual(Booking_.clinic, Clinic_.id, clinicId);
    }

    public static Specification<Booking> appointmentDateEqual(LocalDate d) {
        return SpecUtil.equal(Booking_.appointmentDate, d);
    }

}
