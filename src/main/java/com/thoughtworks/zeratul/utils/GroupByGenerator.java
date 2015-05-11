package com.thoughtworks.zeratul.utils;

import com.google.common.collect.Lists;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import java.util.List;

public class GroupByGenerator {
    private String[] fieldNames;

    public GroupByGenerator(String... fieldNames) {
        this.fieldNames = fieldNames;
    }

    public List<Expression<?>> generate(Root root) {
        List<Expression<?>> groupByExpresses = Lists.newArrayList();
        for (String fieldName : fieldNames) {
            groupByExpresses.add(root.get(fieldName));
        }

        return groupByExpresses;
    }
}
