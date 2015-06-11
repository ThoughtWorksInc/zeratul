package com.thoughtworks.zeratul.utils;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;

public class GroupByGenerator {
    private String[] fieldNames;

    public GroupByGenerator(String... fieldNames) {
        this.fieldNames = fieldNames;
    }

    public List<Expression<?>> generate(Root root) {
        List<Expression<?>> groupByExpresses = new ArrayList();
        for (String fieldName : fieldNames) {
            groupByExpresses.add(root.get(fieldName));
        }

        return groupByExpresses;
    }
}
