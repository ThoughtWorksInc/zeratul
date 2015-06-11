package com.thoughtworks.zeratul.generator.orderby;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

import com.thoughtworks.zeratul.utils.OrderByGenerator;

public class DescendOrderByGenerator implements OrderByGenerator {
    private final String[] fieldNames;

    public DescendOrderByGenerator(String... fieldNames) {
        this.fieldNames = fieldNames;
    }

    @Override
    public List<Order> generate(final CriteriaBuilder criteriaBuilder, AbstractQuery<?> query) {
        final Root<?> entity = query.getRoots().iterator().next();

        return Arrays.asList(fieldNames)
                .stream()
                .map((name) -> criteriaBuilder.desc(entity.get(name)))
                .collect(Collectors.toList());
    }
}
