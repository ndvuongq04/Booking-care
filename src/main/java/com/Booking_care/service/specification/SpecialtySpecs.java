package com.Booking_care.service.specification;

import java.time.Instant;
import org.springframework.data.jpa.domain.Specification;
import com.Booking_care.domain.Specialty;
import com.Booking_care.domain.Specialty_;
import com.Booking_care.util.SpecUtil;

public class SpecialtySpecs {

    public static Specification<Specialty> nameLikeIgnoreCase(String name) {
        return SpecUtil.likeIgnoreCase(Specialty_.name, name);
    }

    public static Specification<Specialty> dateBetween(Instant from, Instant to) {
        return SpecUtil.between(Specialty_.createAt, from, to);
    }
}
