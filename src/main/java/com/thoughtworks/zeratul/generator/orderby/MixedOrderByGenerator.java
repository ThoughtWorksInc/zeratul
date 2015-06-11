package com.thoughtworks.zeratul.generator.orderby;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;

import com.thoughtworks.zeratul.utils.OrderByGenerator;

public class MixedOrderByGenerator implements OrderByGenerator {
    private OrderByGenerator[] generators;

    public MixedOrderByGenerator(OrderByGenerator... generators) {
        this.generators = generators;
    }

    @Override
    public List<Order> generate(final CriteriaBuilder criteriaBuilder, AbstractQuery<?> query) {
        List<Order> combinedOrders = new ArrayList();
        for (OrderByGenerator generator : generators) {
            List<Order> orders = generator.generate(criteriaBuilder, query);
            if (orders != null && !orders.isEmpty()) {
                combinedOrders.addAll(orders);
            }
        }

        return combinedOrders;
    }
}
