package com.thoughtworks.zeratul.generator.orderby;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

import com.thoughtworks.zeratul.utils.OrderByGenerator;

public class AscendOrderByGenerator implements OrderByGenerator {
    private final String[] fieldNames;

    public AscendOrderByGenerator(String... fieldNames) {
        this.fieldNames = fieldNames;
    }

    @Override
    public List<Order> generate(final CriteriaBuilder criteriaBuilder, AbstractQuery<?> query) {
        final Root<?> entity = query.getRoots().iterator().next();

        return Arrays.asList(this.fieldNames)
                .stream()
                .map((name) -> criteriaBuilder.asc(entity.get(name)))
                .collect(Collectors.toList());
    }
}
