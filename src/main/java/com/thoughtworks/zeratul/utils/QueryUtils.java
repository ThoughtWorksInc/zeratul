package com.thoughtworks.zeratul.utils;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.util.List;

public class QueryUtils {
    public static Predicate[] generateRestrictions(CriteriaBuilder criteriaBuilder, AbstractQuery<?> query, RestrictionGenerator... generators) {
        List<Predicate> generatedRestrictions = Lists.newArrayList();
        for (RestrictionGenerator generator : generators) {
            List<Predicate> restrictions = generator.generate(criteriaBuilder, query);
            if (restrictions != null) {
                generatedRestrictions.addAll(restrictions);
            }
        }

        return Iterables.toArray(generatedRestrictions, Predicate.class);
    }
}
