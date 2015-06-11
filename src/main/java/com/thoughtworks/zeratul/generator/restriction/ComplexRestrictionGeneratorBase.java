package com.thoughtworks.zeratul.generator.restriction;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.thoughtworks.zeratul.utils.ExpressionGenerator;
import com.thoughtworks.zeratul.utils.RestrictionGenerator;

public abstract class ComplexRestrictionGeneratorBase<T> implements RestrictionGenerator {
    private final String field;
    private final List<T> values;

    protected ComplexRestrictionGeneratorBase(String field, T... values) {
        this.field = field;
        this.values = Arrays.asList(values);
    }

    public final List<Predicate> generate(final CriteriaBuilder criteriaBuilder, final AbstractQuery<?> query) {
        if (values.isEmpty()) {
            return null;
        }

        Iterable<Expression<T>> expressions = values.stream().map(new Function<T, Expression<T>>() {
            @Override
            public Expression<T> apply(T input) {
                if (input != null && ExpressionGenerator.class.isInstance(input)) {
                    ExpressionGenerator expressionGenerator = ExpressionGenerator.class.cast(input);
                    return expressionGenerator.generate(criteriaBuilder, query);
                } else {
                    return valueToExpress(criteriaBuilder, input);
                }
            }
        }).collect(Collectors.toList());

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
