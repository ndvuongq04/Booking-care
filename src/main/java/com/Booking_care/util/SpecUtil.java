package com.Booking_care.util;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.metamodel.SingularAttribute;

@Service
public class SpecUtil {

    public static <T, Y> Specification<T> likeIgnoreCase(
            SingularAttribute<? super T, Y> field,
            String value) {
        return (root, query, criteriaBuilder) -> value == null ? null
                : criteriaBuilder.like(criteriaBuilder.lower(root.get(field.getName())),
                        "%" + value.toLowerCase() + "%");
    }

    public static <T, Y> Specification<T> equal(
            SingularAttribute<? super T, Y> field,
            Object value) {
        return (root, query, criteriaBuilder) -> value == null ? null
                : criteriaBuilder.equal(root.get(field.getName()), value);
    }

    public static <T, Y extends Comparable<? super Y>> Specification<T> between(
            SingularAttribute<? super T, Y> field,
            Y from,
            Y to) {
        return (root, query, criteriaBuilder) -> {
            if (from != null && to != null) {
                return criteriaBuilder.between(root.get(field), from, to);
            } else if (from != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get(field), from);
            } else if (to != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get(field), to);
            }
            return null;
        };
    }

    public static <T, J, Y> Specification<T> joinEqual(
            SingularAttribute<? super T, J> joinAttr,
            SingularAttribute<? super J, Y> field,
            Object value) {
        return (root, query, criteriaBuilder) -> value == null ? null
                : criteriaBuilder.equal(root.join(joinAttr).get(field), value);
    }

    public static <T, J> Specification<T> joinLikeIgnoreCase(
            SingularAttribute<? super T, J> joinAttr,
            SingularAttribute<? super J, String> field,
            String value) {
        return (root, query, criteriaBuilder) -> {
            if (value == null || value.trim().isEmpty())
                return null;

            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.join(joinAttr).get(field)),
                    "%" + value.toLowerCase().trim() + "%");
        };
    }

}