package com.thoughtworks.zeratul.generator.selection;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;

import com.thoughtworks.zeratul.utils.SelectionGenerator;

public class MaxSelectionGenerator extends SelectionGenerator {
    public MaxSelectionGenerator(String fieldName) {
        super(fieldName);
    }

    @Override
    public Expression generate(Root root, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.max(root.get(getFieldName()));
    }
}
