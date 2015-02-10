package com.thoughtworks.zeratul.generator;

import com.google.common.collect.Lists;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public class NotEqualRestrictionGenerator extends ComplexRestrictionGeneratorBase<Object> {
    public NotEqualRestrictionGenerator(String fieldName, Object value) {
        super(fieldName, value);
    }
    @Override
    protected List<Predicate> doGenerate(CriteriaBuilder criteriaBuilder, Root<?> entity, String field, Iterable<Expression<Object>> expressions) {
        Expression<?> expression = expressions.iterator().next();
        Predicate restriction;
        if (expression == null) {
            restriction = criteriaBuilder.isNotNull(entity.get(field));
        } else {
            restriction = criteriaBuilder.notEqual(entity.get(field), expression);
        }

        return Lists.newArrayList(restriction);
    }

    @Override
    protected Expression<Object> valueToExpress(CriteriaBuilder criteriaBuilder, Object input) {
        if (input == null) {
            return null;
        } else {
            return super.valueToExpress(criteriaBuilder, input);
        }
    }
}
