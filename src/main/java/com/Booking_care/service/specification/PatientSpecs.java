package com.Booking_care.service.specification;

import org.springframework.data.jpa.domain.Specification;

import com.Booking_care.domain.Account_;
import com.Booking_care.domain.Patient_;
import com.Booking_care.domain.Patient;

import com.Booking_care.util.SpecUtil;

public class PatientSpecs {

    public static Specification<Patient> addressJoinLikeIgnoreCase(String address) {
        return SpecUtil.joinLikeIgnoreCase(Patient_.account, Account_.address, address);
    }

    public static Specification<Patient> nameJoinLikeIgnoreCase(String name) {
        return SpecUtil.joinLikeIgnoreCase(Patient_.account, Account_.name, name);
    }

    public static Specification<Patient> phoneNumberJoinLikeIgnoreCase(String phoneNumber) {
        return SpecUtil.joinLikeIgnoreCase(Patient_.account, Account_.phoneNumber, phoneNumber);
    }

    public static Specification<Patient> bhytLikeIgnoreCase(String bhyt) {
        return SpecUtil.likeIgnoreCase(Patient_.bhyt, bhyt);
    }

    public static Specification<Patient> cccdJoinLikeIgnoreCase(String cccd) {
        return SpecUtil.joinLikeIgnoreCase(Patient_.account, Account_.cccd, cccd);
    }
}
