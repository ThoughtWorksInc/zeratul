package com.thoughtworks.zeratul.generator.restriction;

import com.google.common.collect.Lists;
import com.thoughtworks.zeratul.generator.restriction.ComplexRestrictionGeneratorBase;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public class LongGteRestrictionGenerator extends ComplexRestrictionGeneratorBase<Long> {
    public LongGteRestrictionGenerator(String fieldName, Long number) {
        super(fieldName, number);
    }

    @Override
    protected List<Predicate> doGenerate(CriteriaBuilder criteriaBuilder, Root<?> entity, String field, Iterable<Expression<Long>> expressions) {
        Expression<Long> expression = expressions.iterator().next();
        return Lists.newArrayList(criteriaBuilder.greaterThanOrEqualTo(entity.<Long>get(field), expression));
    }
}
