package com.thoughtworks.zeratul.generator.restriction;

import java.util.Arrays;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class NumberGteRestrictionGenerator<T extends Number> extends ComplexRestrictionGeneratorBase<T> {
    public NumberGteRestrictionGenerator(String fieldName, T number) {
        super(fieldName, number);
    }

    @Override
    protected List<Predicate> doGenerate(CriteriaBuilder criteriaBuilder, Root<?> entity, String field, Iterable<Expression<T>> expressions) {
        Expression<T> expression = expressions.iterator().next();
        return Arrays.asList(criteriaBuilder.ge(entity.<T>get(field), expression));
    }
}
