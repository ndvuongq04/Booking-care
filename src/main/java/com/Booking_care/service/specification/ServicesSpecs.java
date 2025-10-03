package com.Booking_care.service.specification;

import java.math.BigDecimal;

import org.springframework.data.jpa.domain.Specification;
import com.Booking_care.domain.Services_;
import com.Booking_care.domain.Services;
import com.Booking_care.util.SpecUtil;

public class ServicesSpecs {

    public static Specification<Services> nameLikeIgnoreCase(String name) {
        return SpecUtil.likeIgnoreCase(Services_.name, name);
    }

    public static Specification<Services> costBetween(Integer minCost, Integer maxCost) {
        return SpecUtil.between(Services_.cost, BigDecimal.valueOf(minCost), BigDecimal.valueOf(maxCost));
    }
}
