package com.thoughtworks.zeratul.generator.restriction;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class DoubleBetweenRestrictionGenerator extends ComplexRestrictionGeneratorBase<Double> {
    public DoubleBetweenRestrictionGenerator(String field, Double number1, Double number2) {
        super(field, number1, number2);
    }

    @Override
    protected List<Predicate> doGenerate(CriteriaBuilder criteriaBuilder, Root<?> entity, String field, Iterable<Expression<Double>> expressions) {
        Iterator<Expression<Double>> iterator = expressions.iterator();
        Expression<Double> expression1 = iterator.next();
        Expression<Double> expression2 = iterator.next();
        return Arrays.asList(criteriaBuilder.between(entity.<Double>get(field), expression1, expression2));
    }
}
