package com.thoughtworks.zeratul.generator.selection;

import com.thoughtworks.zeratul.utils.SelectionGenerator;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;

public class FieldSelectionGenerator extends SelectionGenerator {
    public FieldSelectionGenerator(String fieldName) {
        super(fieldName);
    }

    @Override
    public Expression generate(Root root, CriteriaBuilder criteriaBuilder) {
        return root.get(getFieldName());
    }
}
