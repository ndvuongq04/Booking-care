package com.Booking_care.service.specification;

import java.time.Instant;
import org.springframework.data.jpa.domain.Specification;
import com.Booking_care.domain.Account_;
import com.Booking_care.domain.Account;
import com.Booking_care.domain.Role_;
import com.Booking_care.util.SpecUtil;

public class AccountSpecs {

    public static Specification<Account> roleNameEqual(String roleName) {
        return SpecUtil.joinEqual(Account_.role, Role_.name, roleName);
    }

    public static Specification<Account> genderEqual(String gender) {
        return SpecUtil.equal(Account_.gender, gender);
    }

    public static Specification<Account> createdAtBetween(Instant from, Instant to) {
        return SpecUtil.between(Account_.createAt, from, to);
    }

    public static Specification<Account> cccdLike(String cccd) {
        return SpecUtil.likeIgnoreCase(Account_.cccd, cccd);
    }

    public static Specification<Account> emailLike(String email) {
        return SpecUtil.likeIgnoreCase(Account_.email, email);
    }

    public static Specification<Account> phoneNumberLike(String phoneNumber) {
        return SpecUtil.likeIgnoreCase(Account_.phoneNumber, phoneNumber);
    }
}
