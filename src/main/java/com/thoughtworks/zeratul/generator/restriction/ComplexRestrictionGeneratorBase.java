package com.thoughtworks.zeratul.generator.restriction;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.thoughtworks.zeratul.utils.ExpressionGenerator;
import com.thoughtworks.zeratul.utils.RestrictionGenerator;

import javax.persistence.criteria.*;
import java.util.List;

public abstract class ComplexRestrictionGeneratorBase<T> implements RestrictionGenerator {
    private final String field;
    private final Iterable<T> values;

    protected ComplexRestrictionGeneratorBase(String field, T... values) {
        this.field = field;
        this.values = Lists.newArrayList(values);
    }

    public final List<Predicate> generate(final CriteriaBuilder criteriaBuilder, final AbstractQuery<?> query) {
        if (Iterables.isEmpty(values)) {
            return null;
        }

        Iterable<Expression<T>> expressions = Iterables.transform(values, new Function<T, Expression<T>>() {
            @Override
            public Expression<T> apply(T input) {
                if (input != null && ExpressionGenerator.class.isInstance(input)) {
                    ExpressionGenerator expressionGenerator = ExpressionGenerator.class.cast(input);
                    return expressionGenerator.generate(criteriaBuilder, query);
                } else {
                    return valueToExpress(criteriaBuilder, input);
                }
            }
        });

        Root<?> entity = query.getRoots().iterator().next();
        return doGenerate(criteriaBuilder, entity, field, expressions);
    }

    protected Expression<T> valueToExpress(CriteriaBuilder criteriaBuilder, T input) {
        return criteriaBuilder.literal(input);
    }

    protected abstract List<Predicate> doGenerate(CriteriaBuilder criteriaBuilder, Root<?> entity, String field, Iterable<Expression<T>> expressions);

    @Override
    public String toString() {
        return "ComplexRestrictionGeneratorBase{" +
                "field='" + field + '\'' +
                ", values=" + values +
                '}';
    }
}
