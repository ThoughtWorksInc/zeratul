package com.thoughtworks.zeratul.generator.restriction;

import com.google.common.collect.Lists;
import com.thoughtworks.zeratul.utils.QueryUtils;
import com.thoughtworks.zeratul.utils.RestrictionGenerator;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.util.List;

public class AndRestrictionGenerator implements RestrictionGenerator {
    private final RestrictionGenerator[] generators;

    public AndRestrictionGenerator(RestrictionGenerator... generators) {
        this.generators = generators;
    }

    @Override
    public List<Predicate> generate(CriteriaBuilder criteriaBuilder, AbstractQuery<?> query) {
        Predicate[] restrictions = QueryUtils.generateRestrictions(criteriaBuilder, query, generators);
        return Lists.newArrayList(criteriaBuilder.and(restrictions));
    }
}
