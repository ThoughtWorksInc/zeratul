package com.thoughtworks.zeratul.utils;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.util.List;

public interface RestrictionGenerator {
    List<Predicate> generate(CriteriaBuilder criteriaBuilder, AbstractQuery<?> query);
}
