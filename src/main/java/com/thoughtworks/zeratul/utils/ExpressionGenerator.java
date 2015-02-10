package com.thoughtworks.zeratul.utils;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;

public interface ExpressionGenerator<T> {
    Expression<T> generate(CriteriaBuilder criteriaBuilder, AbstractQuery<?> query);
}
