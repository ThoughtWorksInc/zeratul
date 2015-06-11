package com.thoughtworks.zeratul.utils;

import java.util.List;
import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

public interface RestrictionGenerator {
    List<Predicate> generate(CriteriaBuilder criteriaBuilder, AbstractQuery<?> query);
}
