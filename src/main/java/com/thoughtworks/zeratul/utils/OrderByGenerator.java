package com.thoughtworks.zeratul.utils;

import java.util.List;
import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;

public interface OrderByGenerator {
    List<Order> generate(CriteriaBuilder criteriaBuilder, AbstractQuery<?> query);
}
