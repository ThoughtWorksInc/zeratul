package com.thoughtworks.zeratul.generator.restriction;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class LongBetweenRestrictionGenerator extends ComplexRestrictionGeneratorBase<Long> {
    public LongBetweenRestrictionGenerator(String field, Long number1, Long number2) {
        super(field, number1, number2);
    }

    @Override
    protected List<Predicate> doGenerate(CriteriaBuilder criteriaBuilder, Root<?> entity, String field, Iterable<Expression<Long>> expressions) {
        Iterator<Expression<Long>> iterator = expressions.iterator();
        Expression<Long> expression1 = iterator.next();
        Expression<Long> expression2 = iterator.next();
        return Arrays.asList(criteriaBuilder.between(entity.<Long>get(field), expression1, expression2));
    }
}
