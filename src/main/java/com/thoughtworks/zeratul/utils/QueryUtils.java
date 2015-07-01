package com.thoughtworks.zeratul.utils;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

public class QueryUtils {
    public static Predicate[] generateRestrictions(CriteriaBuilder criteriaBuilder, AbstractQuery<?> query, Iterable<RestrictionGenerator> generators) {
        return StreamSupport.stream(generators.spliterator(), false)
            .map(generator -> Optional.of(generator.generate(criteriaBuilder, query)).orElseGet(ArrayList::new))
            .flatMap(restrictions -> restrictions.stream())
            .toArray(Predicate[]::new);
    }
}
