package com.thoughtworks.zeratul.generator.restriction;

import java.util.Arrays;
import java.util.List;
import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

import com.thoughtworks.zeratul.utils.QueryUtils;
import com.thoughtworks.zeratul.utils.RestrictionGenerator;

public class AndRestrictionGenerator implements RestrictionGenerator {
    private final Iterable<RestrictionGenerator> generators;

    public AndRestrictionGenerator(Iterable<RestrictionGenerator> generators) {
        this.generators = generators;
    }

    @Override
    public List<Predicate> generate(CriteriaBuilder criteriaBuilder, AbstractQuery<?> query) {
        Predicate[] restrictions = QueryUtils.generateRestrictions(criteriaBuilder, query, generators);
        return Arrays.asList(criteriaBuilder.and(restrictions));
    }
}
