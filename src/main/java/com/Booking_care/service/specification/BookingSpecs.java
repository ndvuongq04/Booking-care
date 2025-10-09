package com.Booking_care.service.specification;

import java.time.Instant;
import org.springframework.data.jpa.domain.Specification;
import com.Booking_care.domain.Booking_;
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

    public static Specification<Booking> dateBetween(Instant from, Instant to) {
        return SpecUtil.between(Booking_.appointmentDate, from, to);
    }

    public static Specification<Booking> doctorIdEqual(Long doctorId) {
        return (root, query, cb) -> {
            if (doctorId == null)
                return null;
            return cb.equal(root.get(Booking_.doctor).get(Doctor_.id), doctorId);
        };
    }

}
