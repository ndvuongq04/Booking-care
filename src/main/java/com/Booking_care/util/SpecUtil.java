package com.Booking_care.util;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.metamodel.ListAttribute;
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

    public static <T, J, K> Specification<T> joinLikeIgnoreCase(
            SingularAttribute<? super T, J> joinAttr1,
            SingularAttribute<? super J, K> joinAttr2,
            SingularAttribute<? super K, String> field,
            String value) {
        return (root, query, cb) -> {
            if (value == null || value.trim().isEmpty()) {
                return null;
            }
            return cb.like(
                    cb.lower(root.join(joinAttr1).join(joinAttr2).get(field)),
                    "%" + value.toLowerCase() + "%");
        };
    }

    public static <T, J, K, Y> Specification<T> joinEqual(
            ListAttribute<? super T, J> joinAttr1, // onToMany
            SingularAttribute<? super J, K> joinAttr2,
            SingularAttribute<? super K, Y> field,
            Y value) {
        return (root, query, cb) -> value == null ? null
                : cb.equal(root.join(joinAttr1).join(joinAttr2).get(field), value);
    }

    public static <T, J, K, Y> Specification<T> joinEqual(
            SingularAttribute<? super T, J> joinAttr1, // many-to-one / one-to-one
            SingularAttribute<? super J, K> joinAttr2, // many-to-one / one-to-one
            SingularAttribute<? super K, Y> field,
            Y value) {
        return (root, query, cb) -> {
            if (value == null)
                return null;
            return cb.equal(
                    root.join(joinAttr1).join(joinAttr2).get(field),
                    value);
        };
    }

}