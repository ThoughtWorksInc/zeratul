package com.thoughtworks.zeratul.generator.orderby;

import static java.util.stream.Collectors.toList;

import com.thoughtworks.zeratul.utils.OrderByGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;

public class MixedOrderByGenerator implements OrderByGenerator {
    private OrderByGenerator[] generators;

    public MixedOrderByGenerator(OrderByGenerator... generators) {
        this.generators = generators;
    }

    @Override
    public List<Order> generate(final CriteriaBuilder criteriaBuilder, AbstractQuery<?> query) {
        return Arrays.asList(generators).stream()
            .map(generator -> Optional.of(generator.generate(criteriaBuilder, query)).orElseGet(ArrayList::new))
            .flatMap(orders -> orders.stream())
            .collect(toList());
    }
}
