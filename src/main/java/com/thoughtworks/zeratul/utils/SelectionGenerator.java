package com.thoughtworks.zeratul.utils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;

public abstract class SelectionGenerator<T> {
    private String fieldName;

    public SelectionGenerator(String fieldName) {
        this.fieldName = fieldName;
    }

    abstract public Expression<T> generate(Root root, CriteriaBuilder criteriaBuilder);

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
}
