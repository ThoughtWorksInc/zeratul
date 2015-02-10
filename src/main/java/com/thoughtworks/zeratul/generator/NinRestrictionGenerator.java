package com.thoughtworks.zeratul.generator;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public class NinRestrictionGenerator extends ComplexRestrictionGeneratorBase<Object> {
    public NinRestrictionGenerator(String field, Object... values) {
        super(field, values);
    }

    @Override
    protected List<Predicate> doGenerate(CriteriaBuilder criteriaBuilder, Root<?> entity, String field, Iterable<Expression<Object>> expressions) {
        return Lists.newArrayList(criteriaBuilder.not(entity.get(field).in(Iterables.toArray(expressions, Expression.class))));
    }


}
