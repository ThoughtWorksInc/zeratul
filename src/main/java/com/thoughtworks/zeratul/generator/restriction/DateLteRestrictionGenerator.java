package com.thoughtworks.zeratul.generator.restriction;

import com.google.common.collect.Lists;
import com.thoughtworks.zeratul.generator.restriction.ComplexRestrictionGeneratorBase;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.List;

public class DateLteRestrictionGenerator extends ComplexRestrictionGeneratorBase<Date> {
    public DateLteRestrictionGenerator(String fieldName, Date date) {
        super(fieldName, date);
    }

    @Override
    protected List<Predicate> doGenerate(CriteriaBuilder criteriaBuilder, Root<?> entity, String field, Iterable<Expression<Date>> expressions) {
        Expression<Date> expression = expressions.iterator().next();
        return Lists.newArrayList(criteriaBuilder.lessThanOrEqualTo(entity.<Date>get(field), expression));
    }
}