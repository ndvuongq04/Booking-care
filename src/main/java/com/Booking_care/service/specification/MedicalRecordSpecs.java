package com.Booking_care.service.specification;

import org.springframework.data.jpa.domain.Specification;
import com.Booking_care.domain.Account_;
import com.Booking_care.domain.Doctor_;
import com.Booking_care.domain.MedicalRecord;
import com.Booking_care.domain.MedicalRecord_;
import com.Booking_care.domain.Patient_;
import com.Booking_care.util.SpecUtil;

public class MedicalRecordSpecs {
    public static Specification<MedicalRecord> nameJoinLikeIgnoreCase(String name) {
        return SpecUtil.joinLikeIgnoreCase(MedicalRecord_.patient, Patient_.account, Account_.name, name);
    }

    public static Specification<MedicalRecord> phoneNumberJoinLikeIgnoreCase(String phoneNumber) {
        return SpecUtil.joinLikeIgnoreCase(MedicalRecord_.patient, Patient_.account, Account_.phoneNumber, phoneNumber);
    }

    public static Specification<MedicalRecord> doctorIdJoinEqual(Long id) {
        return SpecUtil.joinEqual(MedicalRecord_.doctor, Doctor_.id, id);
    }
}
