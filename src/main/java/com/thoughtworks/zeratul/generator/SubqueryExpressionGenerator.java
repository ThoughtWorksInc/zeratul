package com.thoughtworks.zeratul.generator;

import static com.thoughtworks.zeratul.utils.QueryUtils.generateRestrictions;

import java.lang.reflect.Field;
import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import com.thoughtworks.zeratul.utils.ExpressionGenerator;
import com.thoughtworks.zeratul.utils.RestrictionGenerator;

public class SubqueryExpressionGenerator<F, T> implements ExpressionGenerator<T> {
    private final Class<F> fromType;
    private final String targetField;
    private final Class<T> targetFieldType;
    private final Iterable<RestrictionGenerator> byRestrictions;

    private SubqueryExpressionGenerator(Class<F> fromType, String targetField, Class<T> targetFieldType, Iterable<RestrictionGenerator> byRestrictions) {
        this.fromType = fromType;
        this.targetField = targetField;
        this.targetFieldType = targetFieldType;
        this.byRestrictions = byRestrictions;
    }

    @Override
    public Expression<T> generate(CriteriaBuilder criteriaBuilder, AbstractQuery<?> query) {
        Subquery<T> subquery = query.subquery(targetFieldType);
        Root<?> entity = subquery.from(fromType);
        Predicate[] restrictions = generateRestrictions(criteriaBuilder, subquery, byRestrictions);
        return subquery.select(entity.<T>get(targetField)).where(restrictions);
    }

    public static ExpressionGenerator subquery(Class<?> fromType, String targetField, Iterable<RestrictionGenerator> byRestrictions) {
        return new SubqueryExpressionGenerator(fromType, targetField, getField(fromType, targetField), byRestrictions);
    }

    private static Class<?> getField(Class<?> fromClass, String targetField) {
        for (Class<?> cls = fromClass; cls != null; cls = cls.getSuperclass()) {
            Field[] fields = cls.getDeclaredFields();
            for (Field field : fields) {
                if (field.getName().equals(targetField)) {
                    return field.getDeclaringClass();
                }
            }
        }

        throw new RuntimeException("No field " + targetField + " on class " + fromClass);
    }
}
