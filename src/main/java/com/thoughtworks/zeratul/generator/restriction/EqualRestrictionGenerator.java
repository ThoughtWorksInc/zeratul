package com.thoughtworks.zeratul.generator.restriction;

import com.google.common.collect.Lists;
import com.thoughtworks.zeratul.generator.restriction.ComplexRestrictionGeneratorBase;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public class EqualRestrictionGenerator extends ComplexRestrictionGeneratorBase<Object> {
    public EqualRestrictionGenerator(String field, Object value) {
        super(field, value);
    }

    @Override
    protected List<Predicate> doGenerate(CriteriaBuilder criteriaBuilder, Root<?> entity, String field, Iterable<Expression<Object>> expressions) {
        Expression<?> expression = expressions.iterator().next();
        Predicate restriction;
        if (expression == null) {
            restriction = criteriaBuilder.isNull(entity.get(field));
        } else {
            restriction = criteriaBuilder.equal(entity.get(field), expression);
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