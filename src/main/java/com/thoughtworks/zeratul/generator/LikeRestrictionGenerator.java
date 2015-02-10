package com.thoughtworks.zeratul.generator;

import com.google.common.collect.Lists;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public class LikeRestrictionGenerator extends ComplexRestrictionGeneratorBase<String> {
    public LikeRestrictionGenerator(String field, String value) {
        super(field, value);
    }

    @Override
    protected List<Predicate> doGenerate(CriteriaBuilder criteriaBuilder, Root<?> entity, String field, Iterable<Expression<String>> values) {
        Expression<String> expression = values.iterator().next();
        return Lists.newArrayList(criteriaBuilder.like(entity.<String>get(field), expression));
    }

    @Override
    protected Expression<String> valueToExpress(CriteriaBuilder criteriaBuilder, String input) {
        return super.valueToExpress(criteriaBuilder, "%" + input + "%");
    }
}
