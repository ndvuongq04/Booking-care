package com.Booking_care.service.specification;

import java.math.BigDecimal;
import java.time.Instant;

import org.springframework.data.jpa.domain.Specification;
import com.Booking_care.domain.Account_;
import com.Booking_care.domain.Clinic_;
import com.Booking_care.domain.Doctor;
import com.Booking_care.domain.Doctor_;
import com.Booking_care.domain.Specialty_;
import com.Booking_care.util.SpecUtil;

public class DoctorSpecs {

    public static Specification<Doctor> nameJoinLikeIgnoreCase(String name) {
        return SpecUtil.joinLikeIgnoreCase(Doctor_.account, Account_.name, name);
    }

    public static Specification<Doctor> specialtyJointEqual(Long specialtyId) {
        return SpecUtil.joinEqual(Doctor_.specialty, Specialty_.id, specialtyId);
    }

    public static Specification<Doctor> clinicJointEqual(Long clinicId) {
        return SpecUtil.joinEqual(Doctor_.clinic, Clinic_.id, clinicId);
    }

    public static Specification<Doctor> degreeEqual(String degree) {
        return SpecUtil.equal(Doctor_.degree, degree);
    }

    public static Specification<Doctor> phoneNumberJointLike(String phoneNumber) {
        return SpecUtil.joinLikeIgnoreCase(Doctor_.account, Account_.phoneNumber, phoneNumber);
    }

    public static Specification<Doctor> costBetween(Integer minCost, Integer maxCost) {
        return SpecUtil.between(Doctor_.cost, BigDecimal.valueOf(minCost), BigDecimal.valueOf(maxCost));
    }

    public static Specification<Doctor> dateBetween(Instant from, Instant to) {
        return SpecUtil.between(Doctor_.createAt, from, to);
    }

}
