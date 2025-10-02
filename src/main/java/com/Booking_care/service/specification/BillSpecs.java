package com.Booking_care.service.specification;

import java.time.Instant;

import org.springframework.data.jpa.domain.Specification;
import com.Booking_care.domain.Bill_;
import com.Booking_care.domain.Patient_;
import com.Booking_care.domain.Services_;
import com.Booking_care.domain.Account_;
import com.Booking_care.domain.Bill;
import com.Booking_care.domain.BillDetail_;
import com.Booking_care.util.SpecUtil;

public class BillSpecs {

    public static Specification<Bill> billIdEqual(Long id) {
        return SpecUtil.equal(Bill_.id, id);
    }

    public static Specification<Bill> dateBetween(Instant from, Instant to) {
        return SpecUtil.between(Bill_.createAt, from, to);
    }

    public static Specification<Bill> patientAccountNameLikeIgnoreCase(String accountName) {
        return SpecUtil.joinLikeIgnoreCase(
                Bill_.patient,
                Patient_.account,
                Account_.name,
                accountName);
    }

    public static Specification<Bill> serviceIdJoinEqual(Long serviceId) {
        return SpecUtil.joinEqual(
                Bill_.billDetails,
                BillDetail_.service,
                Services_.id,
                serviceId);
    }
}
