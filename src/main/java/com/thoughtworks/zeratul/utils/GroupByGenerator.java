package com.thoughtworks.zeratul.utils;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;

public class GroupByGenerator {
    private String[] fieldNames;

    public GroupByGenerator(String... fieldNames) {
        this.fieldNames = fieldNames;
    }

    public List<Expression<?>> generate(Root root) {
        return Arrays.asList(fieldNames).stream().map(fieldName -> (Expression<?>) root.get(fieldName)).collect(
            toList());
    }
}
