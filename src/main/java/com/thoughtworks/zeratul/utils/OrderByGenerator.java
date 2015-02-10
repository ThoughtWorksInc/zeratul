package com.thoughtworks.zeratul.utils;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import java.util.List;

public interface OrderByGenerator {
    List<Order> generate(CriteriaBuilder criteriaBuilder, AbstractQuery<?> query);
}
