package com.thoughtworks.zeratul.generator.restriction;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class IntegerBetweenRestrictionGenerator extends ComplexRestrictionGeneratorBase<Integer> {
    public IntegerBetweenRestrictionGenerator(String field, Integer number1, Integer number2) {
        super(field, number1, number2);
    }

    @Override
    protected List<Predicate> doGenerate(CriteriaBuilder criteriaBuilder, Root<?> entity, String field, Iterable<Expression<Integer>> expressions) {
        Iterator<Expression<Integer>> iterator = expressions.iterator();
        Expression<Integer> expression1 = iterator.next();
        Expression<Integer> expression2 = iterator.next();
        return Arrays.asList(criteriaBuilder.between(entity.<Integer>get(field), expression1, expression2));
    }
}