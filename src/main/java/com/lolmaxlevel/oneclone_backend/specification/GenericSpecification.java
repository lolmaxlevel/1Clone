package com.lolmaxlevel.oneclone_backend.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;


public class GenericSpecification<T> implements Specification<T> {
    private String key;
    private List<String> values;
    private static final long serialVersionUID = 1L;

    public GenericSpecification(String key, List<String> values) {
        this.key = key;
        // Create a defensive copy to prevent modification of the internal state
        this.values = new ArrayList<>(values);
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        // Check if the field is an Enum
        if (root.get(key).getJavaType().isEnum()) {
            // Convert each String to the corresponding Enum
            Class<Enum> enumType = (Class<Enum>) root.get(key).getJavaType();
            List<Predicate> predicates = new ArrayList<>();
            for (String value : values) {
                String[] enumValues = value.split(",");
                for (String enumValue : enumValues) {
                    Enum enumConst = Enum.valueOf(enumType, enumValue);
                    predicates.add(builder.equal(root.get(key), enumConst));
                }
            }
            // Combine the predicates with 'or'
            return builder.or(predicates.toArray(new Predicate[0]));
        } else {
            // If it's not an Enum, proceed as usual
            List<Predicate> predicates = new ArrayList<>();
            for (String value : values) {
                predicates.add(builder.like(root.get(key), "%" + value + "%"));
            }
            return builder.or(predicates.toArray(new Predicate[0]));
        }
    }
}