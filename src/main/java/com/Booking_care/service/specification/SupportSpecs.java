package com.Booking_care.service.specification;

import org.springframework.data.jpa.domain.Specification;

import com.Booking_care.domain.Account_;
import com.Booking_care.domain.Clinic_;
import com.Booking_care.domain.Support;
import com.Booking_care.domain.Support_;
import com.Booking_care.util.SpecUtil;

public class SupportSpecs {
    public static Specification<Support> addressJoinLikeIgnoreCase(String address) {
        return SpecUtil.joinLikeIgnoreCase(Support_.account, Account_.address, address);
    }

    public static Specification<Support> clinicJointEqual(Long clinicId) {
        return SpecUtil.joinEqual(Support_.clinic, Clinic_.id, clinicId);
    }

    public static Specification<Support> nameJoinLikeIgnoreCase(String name) {
        return SpecUtil.joinLikeIgnoreCase(Support_.account, Account_.name, name);
    }

    public static Specification<Support> phoneNumberJoinLikeIgnoreCase(String phoneNumber) {
        return SpecUtil.joinLikeIgnoreCase(Support_.account, Account_.phoneNumber, phoneNumber);
    }
}
