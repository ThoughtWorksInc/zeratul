package com.thoughtworks.zeratul.generator.restriction;

import com.google.common.collect.Lists;
import com.thoughtworks.zeratul.generator.restriction.ComplexRestrictionGeneratorBase;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public class DoubleLtRestrictionGenerator extends ComplexRestrictionGeneratorBase<Double> {
    public DoubleLtRestrictionGenerator(String fieldName, Double number) {
        super(fieldName, number);
    }


    @Override
    protected List<Predicate> doGenerate(CriteriaBuilder criteriaBuilder, Root<?> entity, String field, Iterable<Expression<Double>> expressions) {
        Expression<Double> expression = expressions.iterator().next();
        return Lists.newArrayList(criteriaBuilder.lessThan(entity.<Double>get(field), expression));
    }
}
