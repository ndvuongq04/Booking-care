package com.Booking_care.service.specification;

import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneOffset;
import org.springframework.data.jpa.domain.Specification;
import com.Booking_care.domain.Account;
import com.Booking_care.domain.Account_;
import com.Booking_care.domain.Role;
import com.Booking_care.domain.Role_;
import com.Booking_care.domain.enums.GenderEnum;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;

public class AccountSpecs {

    public static Specification<Account> cccdEqual(String cccd) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Account_.CCCD), cccd);
    }

    public static Specification<Account> phoneNumberLike(String phoneNumber) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(Account_.PHONE_NUMBER),
                "%" + phoneNumber + "%");
    }

    public static Specification<Account> emailLike(String email) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(Account_.EMAIL),
                "%" + email + "%");
    }

    public static Specification<Account> roleEqual(long role) { // service: roleName -> roleId
        return (root, query, criteriaBuilder) -> {
            Join<Account, Role> roleJoin = root.join("role", JoinType.INNER);
            return criteriaBuilder.equal(roleJoin.get(Role_.ID), role);
        };
    }

    public static Specification<Account> genderEqual(String genderStr) {
        return (root, query, criteriaBuilder) -> {
            GenderEnum gender = GenderEnum.valueOf(genderStr.trim().toUpperCase());
            return criteriaBuilder.equal(root.get(Account_.GENDER), gender);
        };
    }

    public static Specification<Account> createAtBetween(YearMonth monthYear) {
        return (root, query, criteriaBuilder) -> {
            // trong tháng/ năm này sẽ có những tài khoản nào được tạo ra
            Instant start = monthYear.atDay(1).atStartOfDay(ZoneOffset.UTC).toInstant();
            Instant end = monthYear.plusMonths(1).atDay(1).atStartOfDay(ZoneOffset.UTC).toInstant();

            return criteriaBuilder.between(root.get(Account_.CREATE_AT), start, end);
        };
    }

}
