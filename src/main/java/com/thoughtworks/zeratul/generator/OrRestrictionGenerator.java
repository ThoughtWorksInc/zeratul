package com.thoughtworks.zeratul.generator;

import com.google.common.collect.Lists;
import com.thoughtworks.zeratul.utils.QueryUtils;
import com.thoughtworks.zeratul.utils.RestrictionGenerator;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.util.List;

public class OrRestrictionGenerator implements RestrictionGenerator {
    private final RestrictionGenerator[] generators;

    public OrRestrictionGenerator(RestrictionGenerator... generators) {
        this.generators = generators;
    }

    @Override
    public List<Predicate> generate(CriteriaBuilder criteriaBuilder, AbstractQuery<?> query) {
        Predicate[] restrictions = QueryUtils.generateRestrictions(criteriaBuilder, query, generators);
        return Lists.newArrayList(criteriaBuilder.or(restrictions));
    }
}
