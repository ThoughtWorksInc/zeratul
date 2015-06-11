package com.thoughtworks.zeratul.utils;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

public class QueryUtils {
    public static Predicate[] generateRestrictions(CriteriaBuilder criteriaBuilder, AbstractQuery<?> query, Iterable<RestrictionGenerator> generators) {
        List<Predicate> generatedRestrictions = new ArrayList();
        for (RestrictionGenerator generator : generators) {
            List<Predicate> restrictions = generator.generate(criteriaBuilder, query);
            if (restrictions != null) {
                generatedRestrictions.addAll(restrictions);
            }
        }

        return generatedRestrictions.toArray(new Predicate[0]);
    }
}
