package com.thoughtworks.zeratul.generator.orderby;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.thoughtworks.zeratul.utils.OrderByGenerator;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import java.util.List;

public class AscendOrderByGenerator implements OrderByGenerator {
    private final String[] fieldNames;

    public AscendOrderByGenerator(String... fieldNames) {
        this.fieldNames = fieldNames;
    }

    @Override
    public List<Order> generate(final CriteriaBuilder criteriaBuilder, AbstractQuery<?> query) {
        final Root<?> entity = query.getRoots().iterator().next();
        List<String> fieldNameList = Lists.newArrayList(fieldNames);
        return Lists.transform(fieldNameList, new Function<String, Order>() {
            @Override
            public Order apply(String fieldName) {
                return criteriaBuilder.asc(entity.get(fieldName));
            }
        });
    }
}
