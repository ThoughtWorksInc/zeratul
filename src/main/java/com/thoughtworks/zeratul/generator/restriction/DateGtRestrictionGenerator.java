package com.thoughtworks.zeratul.generator.restriction;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class DateGtRestrictionGenerator extends ComplexRestrictionGeneratorBase<Date> {
    public DateGtRestrictionGenerator(String fieldName, Date date) {
        super(fieldName, date);
    }

    @Override
    protected List<Predicate> doGenerate(CriteriaBuilder criteriaBuilder, Root<?> entity, String field, Iterable<Expression<Date>> expressions) {
        Expression<Date> expression = expressions.iterator().next();
        return Arrays.asList(criteriaBuilder.greaterThan(entity.<Date>get(field), expression));
    }
}
