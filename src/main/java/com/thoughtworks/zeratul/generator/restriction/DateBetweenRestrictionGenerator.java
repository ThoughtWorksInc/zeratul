package com.thoughtworks.zeratul.generator.restriction;

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class DateBetweenRestrictionGenerator extends ComplexRestrictionGeneratorBase<Date> {
    public DateBetweenRestrictionGenerator(String field, Date date1, Date date2) {
        super(field, date1, date2);
    }

    @Override
    protected List<Predicate> doGenerate(CriteriaBuilder criteriaBuilder, Root<?> entity, String field, Iterable<Expression<Date>> expressions) {
        Iterator<Expression<Date>> iterator = expressions.iterator();
        Expression<Date> expression1 = iterator.next();
        Expression<Date> expression2 = iterator.next();
        return Arrays.asList(criteriaBuilder.between(entity.<Date>get(field), expression1, expression2));
    }
}
