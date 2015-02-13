package com.thoughtworks.zeratul.utils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;

public class CriteriaFactory<T> {

    private final Class<T> prototype;

    public CriteriaFactory(Class<T> prototype) {
        this.prototype = prototype;
    }

    @PersistenceContext
    protected EntityManager entityManager;

    public CriteriaQuery<Object[]> createCriteria() {
        return (CriteriaQuery<Object[]>) createQuery(prototype);
    }

    private <R> CriteriaQuery<R> createQuery(Class<R> resultType) {
        CriteriaQuery<R> criteriaQuery = entityManager.getCriteriaBuilder().createQuery(resultType);
        criteriaQuery.from(prototype);

        return criteriaQuery;
    }
}
