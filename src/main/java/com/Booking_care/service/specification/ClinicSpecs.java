package com.Booking_care.service.specification;

import java.time.Instant;
import org.springframework.data.jpa.domain.Specification;
import com.Booking_care.domain.Clinic_;
import com.Booking_care.domain.Doctor_;
import com.Booking_care.domain.Address_;
import com.Booking_care.domain.Clinic;

import com.Booking_care.util.SpecUtil;

public class ClinicSpecs {
    public static Specification<Clinic> addressLikeIgnoreCase(String address) {
        return SpecUtil.likeIgnoreCase(Clinic_.address, address);
    }

    public static Specification<Clinic> nameLikeIgnoreCase(String name) {
        return SpecUtil.likeIgnoreCase(Clinic_.name, name);
    }

    public static Specification<Clinic> phoneNumberLikeIgnoreCase(String phoneNumber) {
        return SpecUtil.likeIgnoreCase(Clinic_.phoneNumber, phoneNumber);
    }

    public static Specification<Clinic> dateBetween(Instant from, Instant to) {
        return SpecUtil.between(Clinic_.createAt, from, to);
    }

    public static Specification<Clinic> addressJointEqual(Long addressId) {
        return SpecUtil.joinEqual(Clinic_.address, Address_.id, addressId);
    }
}
