package com.thoughtworks.zeratul.generator.restriction;

import java.util.Arrays;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class DoubleLteRestrictionGenerator extends ComplexRestrictionGeneratorBase<Double> {
    public DoubleLteRestrictionGenerator(String fieldName, Double number) {
        super(fieldName, number);
    }


    @Override
    protected List<Predicate> doGenerate(CriteriaBuilder criteriaBuilder, Root<?> entity, String field, Iterable<Expression<Double>> expressions) {
        Expression<Double> expression = expressions.iterator().next();
        return Arrays.asList(criteriaBuilder.lessThanOrEqualTo(entity.<Double>get(field), expression));
    }
}
