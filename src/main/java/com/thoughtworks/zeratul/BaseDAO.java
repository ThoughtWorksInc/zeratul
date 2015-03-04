package com.thoughtworks.zeratul;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.thoughtworks.zeratul.generator.orderby.AscendOrderByGenerator;
import com.thoughtworks.zeratul.generator.orderby.DescendOrderByGenerator;
import com.thoughtworks.zeratul.generator.orderby.DistanceOrderByGenerator;
import com.thoughtworks.zeratul.generator.orderby.MixedOrderByGenerator;
import com.thoughtworks.zeratul.generator.restriction.AndRestrictionGenerator;
import com.thoughtworks.zeratul.generator.restriction.OrRestrictionGenerator;
import com.thoughtworks.zeratul.generator.selection.*;
import com.thoughtworks.zeratul.utils.*;
import com.thoughtworks.zeratul.generator.*;
import com.thoughtworks.zeratul.geography.Location;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.thoughtworks.zeratul.utils.QueryUtils.generateRestrictions;

public abstract class BaseDAO<T> {
    private static final Logger log = Logger.getLogger(BaseDAO.class);

    @PersistenceContext
    protected EntityManager entityManager;

    private final Class<T> prototype;

    public BaseDAO(Class<T> prototype) {
        this.prototype = prototype;
    }

    public void save(T entity) {
        if (entity instanceof BaseModel) {
            BaseModel model = (BaseModel) entity;
            if (model.getTimeCreated() == null) {
                model.setTimeCreated(DateTime.now().toDate());
            }
        }
        entityManager.persist(entity);
    }

    public T update(T entity) {
        return entityManager.merge(entity);
    }

    public void remove(T entity) {
        entityManager.remove(entity);
    }

    public T getByUuid(String uuid) {
        return querySingleResult(field("uuid").eq(uuid));
    }

    protected long count(Iterable<RestrictionGenerator> generators) {
        return count(Iterables.toArray(generators, RestrictionGenerator.class));
    }

    protected Long count(RestrictionGenerator... generators) {
        CriteriaQuery<Long> criteriaQuery = createCountCriteria();

        Predicate[] generatedRestrictions = generateRestrictions(entityManager.getCriteriaBuilder(), criteriaQuery, generators);

        criteriaQuery.where(generatedRestrictions);
        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    protected Long distinctCount(RestrictionGenerator... generators) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = createQuery(Long.class);

        criteriaQuery.select(criteriaBuilder.countDistinct(getRoot(criteriaQuery)));

        Predicate[] generatedRestrictions = generateRestrictions(entityManager.getCriteriaBuilder(), criteriaQuery, generators);

        criteriaQuery.where(generatedRestrictions);
        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    private CriteriaQuery<Long> createCountCriteria() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = createQuery(Long.class);

        criteriaQuery.select(criteriaBuilder.count(getRoot(criteriaQuery)));
        return criteriaQuery;
    }

    protected T queryFirstResult(List<RestrictionGenerator> generators) {
        return queryFirstResult(null, Iterables.toArray(generators, RestrictionGenerator.class));
    }

    protected T queryFirstResult(RestrictionGenerator... generators) {
        return queryFirstResult(null, generators);
    }

    protected T queryFirstResult(OrderByGenerator orderByGenerator, List<RestrictionGenerator> generators) {
        return queryFirstResult(orderByGenerator, Iterables.toArray(generators, RestrictionGenerator.class));
    }

    protected T queryFirstResult(OrderByGenerator orderByGenerator, RestrictionGenerator... generators) {
        List<T> resultList = queryPageResult(1, 0, orderByGenerator, generators);

        if (resultList.isEmpty()) {
            return null;
        }

        return resultList.get(0);
    }

    protected Object queryFirstFieldsResult(List<SelectionGenerator> selection, OrderByGenerator orderByGenerator, List<RestrictionGenerator> generators) {
        return queryFirstFieldsResult(Object[].class, selection, orderByGenerator, Iterables.toArray(generators, RestrictionGenerator.class));
    }

    protected Object queryFirstFieldsResult(List<SelectionGenerator> selection, OrderByGenerator orderByGenerator, RestrictionGenerator... generators) {
        return queryFirstFieldsResult(Object[].class, selection, orderByGenerator, generators);
    }

    protected <F> F queryFirstFieldsResult(Class<F> wrapper, List<SelectionGenerator> selection, OrderByGenerator orderByGenerator, List<RestrictionGenerator> generators) {
        return queryFirstFieldsResult(wrapper, selection, orderByGenerator, Iterables.toArray(generators, RestrictionGenerator.class));
    }

    protected <F> F queryFirstFieldsResult(Class<F> wrapper, List<SelectionGenerator> selection, OrderByGenerator orderByGenerator, RestrictionGenerator... generators) {
        List<F> resultList = queryPageFieldsResult(1, 0, wrapper, selection, orderByGenerator, generators);

        if (resultList.isEmpty()) {
            return null;
        }

        return resultList.get(0);
    }

    protected T querySingleResult(Iterable<RestrictionGenerator> generators) {
        return querySingleResult(Iterables.toArray(generators, RestrictionGenerator.class));
    }

    protected T querySingleResult(RestrictionGenerator... generators) {
        List<T> resultList = queryPageResult(2, 0, generators);

        if (resultList.isEmpty()) {
            return null;
        }

        if (resultList.size() > 1) {
            String info = String.format("Find more than one entity of %s with generators: %s",
                    prototype.getName(), Arrays.toString(generators));
            log.warn(info);
            throw new IllegalStateException(info);
        }

        return resultList.get(0);
    }

    protected Object[] querySingleFieldsResult(List<SelectionGenerator> selections, Iterable<RestrictionGenerator> generators) {
        return querySingleFieldsResult(selections, Iterables.toArray(generators, RestrictionGenerator.class));
    }

    protected <F> F querySingleFieldsResult(Class<F> wrapper, List<SelectionGenerator> selections, Iterable<RestrictionGenerator> generators) {
        return querySingleFieldsResult(wrapper, selections, Iterables.toArray(generators, RestrictionGenerator.class));
    }

    protected Object[] querySingleFieldsResult(List<SelectionGenerator> selections, RestrictionGenerator... generators) {
        return querySingleFieldsResult(Object[].class, selections, generators);
    }

    protected <F> F querySingleFieldsResult(Class<F> wrapper, List<SelectionGenerator> selections, RestrictionGenerator... generators) {
        List<F> resultList = queryPageFieldsResult(2, 0, wrapper, selections, generators);

        if (resultList.isEmpty()) {
            return null;
        }

        if (resultList.size() > 1) {
            String info = String.format("Find more than one entity of %s with generators: %s",
                    prototype.getName(), Arrays.toString(generators));
            log.warn(info);
            throw new IllegalStateException(info);
        }

        return resultList.get(0);
    }

    protected T querySingleResultLocked(LockModeType lockModeType, RestrictionGenerator... generators) {
        CriteriaQuery<T> criteria = createQueryCriteriaByAttributes();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        Predicate[] generatedRestrictions = generateRestrictions(criteriaBuilder, criteria, generators);
        criteria.where(generatedRestrictions);

        TypedQuery<T> query = entityManager.createQuery(criteria);
        query.setLockMode(lockModeType);
        query.setMaxResults(1);

        List<T> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            String info = String.format("Failed to find entity of %s with generators: %s",
                    prototype.getName(), Arrays.toString(generators));
            log.info(info);
            return null;
        } else if (resultList.size() > 1) {
            String info = String.format("Find more than one entity of %s with generators: %s",
                    prototype.getName(), Arrays.toString(generators));
            log.warn(info);
            throw new IllegalStateException(info);
        }

        return resultList.get(0);
    }

    protected List<T> queryListResult(Iterable<RestrictionGenerator> generators) {
        return queryListResult(null, Iterables.toArray(generators, RestrictionGenerator.class));
    }

    protected List<T> queryListResult(RestrictionGenerator... generators) {
        return queryListResult(null, generators);
    }

    protected List<T> queryListResult(OrderByGenerator orderByGenerator, Iterable<RestrictionGenerator> generators) {
        return queryListResult(orderByGenerator, Iterables.toArray(generators, RestrictionGenerator.class));
    }

    protected List<T> queryListResult(OrderByGenerator orderByGenerator, RestrictionGenerator... generators) {
        CriteriaQuery<T> criteria = createQueryCriteriaByAttributes();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        appendOrderBy(criteriaBuilder, criteria, orderByGenerator);

        Predicate[] generatedRestrictions = generateRestrictions(criteriaBuilder, criteria, generators);
        criteria.where(generatedRestrictions);

        List<T> resultList = entityManager.createQuery(criteria).getResultList();
        if (resultList.isEmpty()) {
            String info = String.format("Failed to find entity of %s with generators: %s",
                    prototype.getName(), Arrays.toString(generators));
            log.info(info);
        }

        return resultList;
    }

    protected List queryListFieldsResult(List<SelectionGenerator> selection, Iterable<RestrictionGenerator> generators) {
        return queryListFieldsResult(selection, null, Iterables.toArray(generators, RestrictionGenerator.class));
    }

    protected List queryListFieldsResult(List<SelectionGenerator> selection, RestrictionGenerator... generators) {
        return queryListFieldsResult(selection, null, generators);
    }

    protected List queryListFieldsResult(List<SelectionGenerator> selection, OrderByGenerator orderByGenerator, Iterable<RestrictionGenerator> generators) {
        return queryListFieldsResult(selection, orderByGenerator, Iterables.toArray(generators, RestrictionGenerator.class));
    }

    protected List queryListFieldsResult(List<SelectionGenerator> selection, OrderByGenerator orderByGenerator, RestrictionGenerator... generators) {
        return queryListFieldsResult(Object[].class, selection, orderByGenerator, generators);
    }

    protected <F> List<F> queryListFieldsResult(Class<F> wrapper, List<SelectionGenerator> selection, Iterable<RestrictionGenerator> generators) {
        return queryListFieldsResult(wrapper, selection, null, Iterables.toArray(generators, RestrictionGenerator.class));
    }

    protected <F> List<F> queryListFieldsResult(Class<F> wrapper, List<SelectionGenerator> selection, RestrictionGenerator... generators) {
        return queryListFieldsResult(wrapper, selection, null, generators);
    }

    protected <F> List<F> queryListFieldsResult(Class<F> wrapper, List<SelectionGenerator> selection, OrderByGenerator orderByGenerator, Iterable<RestrictionGenerator> generators) {
        return queryListFieldsResult(wrapper, selection, orderByGenerator, Iterables.toArray(generators, RestrictionGenerator.class));
    }

    protected <F> List<F> queryListFieldsResult(Class<F> wrapper, List<SelectionGenerator> selection, OrderByGenerator orderByGenerator, RestrictionGenerator... generators) {
        CriteriaQuery criteria = createQuery(wrapper);
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        appendOrderBy(criteriaBuilder, criteria, orderByGenerator);

        generateSelect(criteria, criteriaBuilder, wrapper, selection);

        Predicate[] generatedRestrictions = generateRestrictions(criteriaBuilder, criteria, generators);
        criteria.where(generatedRestrictions);

        List<F> resultList = entityManager.createQuery(criteria).getResultList();
        if (resultList.isEmpty()) {
            String info = String.format("Failed to find entity of %s with generators: %s",
                    prototype.getName(), Arrays.toString(generators));
            log.info(info);
        }

        return resultList;
    }

    protected List<T> queryPageResult(int pageSize, int pageIndex, Iterable<RestrictionGenerator> generators) {
        return queryPageResult(pageSize, pageIndex, null, Iterables.toArray(generators, RestrictionGenerator.class));
    }

    protected List<T> queryPageResult(int pageSize, int pageIndex, RestrictionGenerator... generators) {
        return queryPageResult(pageSize, pageIndex, null, generators);
    }

    protected List<T> queryPageResult(int pageSize, int pageIndex, OrderByGenerator orderByGenerator, Iterable<RestrictionGenerator> generators) {
        return queryPageResult(pageSize, pageIndex, orderByGenerator, Iterables.toArray(generators, RestrictionGenerator.class));
    }

    protected List<T> queryPageResult(int pageSize, int pageIndex, OrderByGenerator orderByGenerator, RestrictionGenerator... generators) {
        CriteriaQuery<T> criteria = createQueryCriteriaByAttributes();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        appendOrderBy(criteriaBuilder, criteria, orderByGenerator);

        Predicate[] generatedRestrictions = generateRestrictions(criteriaBuilder, criteria, generators);

        criteria.where(generatedRestrictions);
        List<T> resultList = doQueryWithPage(criteria, pageSize, pageIndex);

        if (resultList.isEmpty()) {
            String info = String.format("Failed to find entity of %s, of page (%d, %d), with generators (%s)",
                    prototype.getName(), pageSize, pageIndex, Arrays.toString(generators));
            log.info(info);
        }

        return resultList;
    }

    protected List queryPageFieldsResult(int pageSize, int pageIndex, List<SelectionGenerator> selection, Iterable<RestrictionGenerator> generators) {
        return queryPageFieldsResult(pageSize, pageIndex, selection, null, Iterables.toArray(generators, RestrictionGenerator.class));
    }

    protected List queryPageFieldsResult(int pageSize, int pageIndex, List<SelectionGenerator> selection, RestrictionGenerator... generators) {
        return queryPageFieldsResult(pageSize, pageIndex, selection, null, generators);
    }

    protected List queryPageFieldsResult(int pageSize, int pageIndex, List<SelectionGenerator> selection, OrderByGenerator orderByGenerator, Iterable<RestrictionGenerator> generators) {
        return queryPageFieldsResult(pageSize, pageIndex, selection, orderByGenerator, Iterables.toArray(generators, RestrictionGenerator.class));
    }

    protected List queryPageFieldsResult(int pageSize, int pageIndex, List<SelectionGenerator> selection, OrderByGenerator orderByGenerator, RestrictionGenerator... generators) {
        return queryPageFieldsResult(pageSize, pageIndex, Object[].class, selection, orderByGenerator, generators);
    }

    protected <F> List<F> queryPageFieldsResult(int pageSize, int pageIndex, Class<F> wrapper, List<SelectionGenerator> selection, Iterable<RestrictionGenerator> generators) {
        return queryPageFieldsResult(pageSize, pageIndex, wrapper, selection, null, Iterables.toArray(generators, RestrictionGenerator.class));
    }

    protected <F> List<F> queryPageFieldsResult(int pageSize, int pageIndex, Class<F> wrapper, List<SelectionGenerator> selection, RestrictionGenerator... generators) {
        return queryPageFieldsResult(pageSize, pageIndex, wrapper, selection, null, generators);
    }

    protected <F> List<F> queryPageFieldsResult(int pageSize, int pageIndex, Class<F> wrapper, List<SelectionGenerator> selection, OrderByGenerator orderByGenerator, Iterable<RestrictionGenerator> generators) {
        return queryPageFieldsResult(pageSize, pageIndex, wrapper, selection, orderByGenerator, Iterables.toArray(generators, RestrictionGenerator.class));
    }

    protected <F> List<F> queryPageFieldsResult(int pageSize, int pageIndex, Class<F> wrapper, List<SelectionGenerator> selection, OrderByGenerator orderByGenerator, RestrictionGenerator... generators) {
        CriteriaQuery<F> criteria = createQuery(wrapper);
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        appendOrderBy(criteriaBuilder, criteria, orderByGenerator);

        generateSelect(criteria, criteriaBuilder, wrapper, selection);

        Predicate[] generatedRestrictions = generateRestrictions(criteriaBuilder, criteria, generators);

        criteria.where(generatedRestrictions);
        List resultList = doQueryWithPage(criteria, pageSize, pageIndex);

        if (resultList.isEmpty()) {
            String info = String.format("Failed to find entity of %s, of page (%d, %d), with generators (%s)",
                    prototype.getName(), pageSize, pageIndex, Arrays.toString(generators));
            log.info(info);
        }

        return resultList;
    }

    private <F> void generateSelect(CriteriaQuery criteria, CriteriaBuilder criteriaBuilder, Class<F> wrapper, List<SelectionGenerator> selections) {
        Root root = getRoot(criteria);
        List<Selection> paths = Lists.newArrayList();
        for (SelectionGenerator selection : selections) {
            paths.add(selection.generate(root, criteriaBuilder));
        }
        if (wrapper.isArray()) {
            criteria.multiselect(paths);
        } else {
            criteria.select(criteriaBuilder.construct(wrapper, Iterables.toArray(paths, Selection.class)));
        }
    }

    private void appendOrderBy(CriteriaBuilder criteriaBuilder, CriteriaQuery criteria, OrderByGenerator orderByGenerator) {
        if (orderByGenerator != null) {
            List<Order> orders = orderByGenerator.generate(criteriaBuilder, criteria);
            if (orders != null && !orders.isEmpty()) {
                criteria.orderBy(orders);
            }
        }
    }

    private <F> List<F> doQueryWithPage(CriteriaQuery<F> criteria, int pageSize, int pageIndex) {
        return entityManager.createQuery(criteria)
                .setMaxResults(pageSize)
                .setFirstResult(pageIndex * pageSize)
                .getResultList();
    }

    private CriteriaQuery<T> createQuery() {
        return createQuery(prototype);
    }

    private <R> CriteriaQuery<R> createQuery(Class<R> resultType) {
        CriteriaQuery<R> criteriaQuery = entityManager.getCriteriaBuilder().createQuery(resultType);
        criteriaQuery.from(prototype);

        return criteriaQuery;
    }

    private Root<T> getRoot(CriteriaQuery<?> criteriaQuery) {
        Set<Root<?>> roots = criteriaQuery.getRoots();
        if (roots.size() != 1) {
            throw new IllegalStateException("Query contains more than one root entity!");
        }

        return (Root<T>) roots.iterator().next();
    }

    private CriteriaQuery<T> createQueryCriteriaByAttributes() {
        CriteriaQuery<T> criteriaQuery = createQuery();
        Root<T> entity = getRoot(criteriaQuery);
        criteriaQuery.select(entity);

        return criteriaQuery;
    }

    protected FieldRestrictionGeneratorBuilder field(String fieldName) {
        return new FieldRestrictionGeneratorBuilder(prototype, fieldName);
    }

    protected SelectionGenerator value(String fieldName) {
        return new FieldSelectionGenerator(fieldName);
    }

    protected List<SelectionGenerator> filter(SelectionGenerator... selections) {
        return Lists.newArrayList(selections);
    }

    protected SelectionGenerator min(String fieldName) {
        return new MinSelectionGenerator(fieldName);
    }

    protected SelectionGenerator max(String fieldName) {
        return new MaxSelectionGenerator(fieldName);
    }

    protected SelectionGenerator abs(String fieldName) {
        return new AbsSelectionGenerator(fieldName);
    }

    protected SelectionGenerator avg(String fieldName) {
        return new AvgSelectionGenerator(fieldName);
    }

    protected OrderByGenerator asc(String... fieldNames) {
        return new AscendOrderByGenerator(fieldNames);
    }

    protected OrderByGenerator desc(String... fieldNames) {
        return new DescendOrderByGenerator(fieldNames);
    }

    protected OrderByGenerator mixed(OrderByGenerator... generators) {
        return new MixedOrderByGenerator(generators);
    }

    protected OrderByGenerator distance(Location location) {
        return new DistanceOrderByGenerator(location);
    }

    protected ExpressionGenerator subquery(Class<?> fromType, String targetField, RestrictionGenerator... byRestrictions) {
        return SubqueryExpressionGenerator.subquery(fromType, targetField, byRestrictions);
    }

    protected ExpressionGenerator subquery(Class<?> fromType, String targetField, Iterable<RestrictionGenerator> byRestrictions) {
        return SubqueryExpressionGenerator.subquery(fromType, targetField, Iterables.toArray(byRestrictions, RestrictionGenerator.class));
    }

    protected RestrictionGenerator or(RestrictionGenerator... generators) {
        return new OrRestrictionGenerator(generators);
    }

    protected RestrictionGenerator or(Iterable<RestrictionGenerator> generators) {
        return new OrRestrictionGenerator(Iterables.toArray(generators, RestrictionGenerator.class));
    }

    protected RestrictionGenerator and(RestrictionGenerator... generators) {
        return new AndRestrictionGenerator(generators);
    }

    protected RestrictionGenerator and(Iterable<RestrictionGenerator> generators) {
        return new AndRestrictionGenerator(Iterables.toArray(generators, RestrictionGenerator.class));
    }
}