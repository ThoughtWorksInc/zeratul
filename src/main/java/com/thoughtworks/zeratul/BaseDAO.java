package com.thoughtworks.zeratul;

import static com.thoughtworks.zeratul.utils.QueryUtils.generateRestrictions;

import com.thoughtworks.zeratul.generator.SubqueryExpressionGenerator;
import com.thoughtworks.zeratul.generator.orderby.AscendOrderByGenerator;
import com.thoughtworks.zeratul.generator.orderby.DescendOrderByGenerator;
import com.thoughtworks.zeratul.generator.orderby.MixedOrderByGenerator;
import com.thoughtworks.zeratul.generator.restriction.AndRestrictionGenerator;
import com.thoughtworks.zeratul.generator.restriction.OrRestrictionGenerator;
import com.thoughtworks.zeratul.generator.selection.AbsSelectionGenerator;
import com.thoughtworks.zeratul.generator.selection.AvgSelectionGenerator;
import com.thoughtworks.zeratul.generator.selection.CountSelectionGenerator;
import com.thoughtworks.zeratul.generator.selection.FieldSelectionGenerator;
import com.thoughtworks.zeratul.generator.selection.MaxSelectionGenerator;
import com.thoughtworks.zeratul.generator.selection.MinSelectionGenerator;
import com.thoughtworks.zeratul.generator.selection.SumSelectionGenerator;
import com.thoughtworks.zeratul.utils.ExpressionGenerator;
import com.thoughtworks.zeratul.utils.FieldRestrictionGeneratorBuilder;
import com.thoughtworks.zeratul.utils.GroupByGenerator;
import com.thoughtworks.zeratul.utils.OrderByGenerator;
import com.thoughtworks.zeratul.utils.RestrictionGenerator;
import com.thoughtworks.zeratul.utils.SelectionGenerator;
import com.thoughtworks.zeratul.utils.Selections;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.StreamSupport;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

public abstract class BaseDAO<T> {
    private static final Logger log = Logger.getLogger(BaseDAO.class);

    @PersistenceContext
    protected EntityManager entityManager;

    private final Class<T> prototype;

    public BaseDAO(Class<T> prototype) {
        this.prototype = prototype;
    }

    public void save(T entity) {
        entityManager.persist(entity);
    }

    public T update(T entity) {
        return entityManager.merge(entity);
    }

    public void remove(T entity) {
        entityManager.remove(entity);
    }

    public T getById(Object id) {
        return entityManager.find(prototype, id);
    }

    public T getByIdLocked(Long id, LockModeType lockModeType) {
        return entityManager.find(prototype, id, lockModeType);
    }

    protected long count(Iterable<RestrictionGenerator> generators) {
        CriteriaQuery<Long> criteriaQuery = createCountCriteria();

        Predicate[] generatedRestrictions = generateRestrictions(entityManager.getCriteriaBuilder(), criteriaQuery,
                                                                 generators);

        criteriaQuery.where(generatedRestrictions);
        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    protected Long count(RestrictionGenerator... generators) {
        return count(Arrays.asList(generators));
    }

    protected Long distinctCount(Iterable<RestrictionGenerator> generators) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = createQuery(Long.class);

        criteriaQuery.select(criteriaBuilder.countDistinct(getRoot(criteriaQuery)));

        Predicate[] generatedRestrictions = generateRestrictions(entityManager.getCriteriaBuilder(), criteriaQuery,
                                                                 generators);
        List<Predicate> t = Arrays.asList(generatedRestrictions);
        criteriaQuery.where(t.toArray(new Predicate[0]));
        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    protected Long distinctCount(RestrictionGenerator... generators) {
        return distinctCount(Arrays.asList(generators));
    }

    private CriteriaQuery<Long> createCountCriteria() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = createQuery(Long.class);

        criteriaQuery.select(criteriaBuilder.count(getRoot(criteriaQuery)));
        return criteriaQuery;
    }

    protected T queryFirstResult(List<RestrictionGenerator> generators) {
        return queryFirstResult(null, null, generators);
    }

    protected T queryFirstResult(GroupByGenerator groupByGenerator, List<RestrictionGenerator> generators) {
        return queryFirstResult(null, groupByGenerator, generators);
    }

    protected T queryFirstResult(RestrictionGenerator... generators) {
        return queryFirstResult(null, null, generators);
    }

    protected T queryFirstResult(GroupByGenerator groupByGenerator, RestrictionGenerator... generators) {
        return queryFirstResult(null, groupByGenerator, generators);
    }

    protected T queryFirstResult(OrderByGenerator orderByGenerator, List<RestrictionGenerator> generators) {
        return queryFirstResult(orderByGenerator, null, generators);
    }

    protected T queryFirstResult(OrderByGenerator orderByGenerator, RestrictionGenerator... generators) {
        return queryFirstResult(orderByGenerator, null, Arrays.asList(generators));
    }

    protected T queryFirstResult(OrderByGenerator orderByGenerator, GroupByGenerator groupByGenerator,
                                 RestrictionGenerator... generators) {
        return queryFirstResult(orderByGenerator, groupByGenerator, Arrays.asList(generators));
    }

    protected T queryFirstResult(OrderByGenerator orderByGenerator, GroupByGenerator groupByGenerator,
                                 Iterable<RestrictionGenerator> generators) {
        List<T> resultList = queryPageResult(1, 0, orderByGenerator, groupByGenerator, generators);

        if (resultList.isEmpty()) {
            return null;
        }

        return resultList.get(0);
    }

    protected Object queryFirstFieldsResult(Selections selection, OrderByGenerator orderByGenerator,
                                            Iterable<RestrictionGenerator> generators) {
        return queryFirstFieldsResult(Object[].class, selection, orderByGenerator, null, generators);
    }

    protected Object queryFirstFieldsResult(Selections selection, OrderByGenerator orderByGenerator,
                                            GroupByGenerator groupByGenerator, List<RestrictionGenerator> generators) {
        return queryFirstFieldsResult(Object[].class, selection, orderByGenerator, groupByGenerator, generators);
    }

    protected Object queryFirstFieldsResult(Selections selection, OrderByGenerator orderByGenerator,
                                            RestrictionGenerator... generators) {
        return queryFirstFieldsResult(Object[].class, selection, orderByGenerator, null, generators);
    }

    protected Object queryFirstFieldsResult(Selections selection, OrderByGenerator orderByGenerator,
                                            GroupByGenerator groupByGenerator, RestrictionGenerator... generators) {
        return queryFirstFieldsResult(Object[].class, selection, orderByGenerator, groupByGenerator, generators);
    }

    protected <F> F queryFirstFieldsResult(Class<F> wrapper, Selections selection, OrderByGenerator orderByGenerator,
                                           List<RestrictionGenerator> generators) {
        return queryFirstFieldsResult(wrapper, selection, orderByGenerator, null, generators);
    }

    protected <F> F queryFirstFieldsResult(Class<F> wrapper, Selections selection, OrderByGenerator orderByGenerator,
                                           GroupByGenerator groupByGenerator, RestrictionGenerator... generators) {
        return queryFirstFieldsResult(wrapper, selection, orderByGenerator, groupByGenerator,
                                      Arrays.asList(generators));
    }

    protected <F> F queryFirstFieldsResult(Class<F> wrapper, Selections selection, OrderByGenerator orderByGenerator,
                                           GroupByGenerator groupByGenerator,
                                           Iterable<RestrictionGenerator> generators) {
        List<F> resultList = queryPageFieldsResult(1, 0, wrapper, selection, orderByGenerator, groupByGenerator,
                                                   generators);

        if (resultList.isEmpty()) {
            return null;
        }

        return resultList.get(0);
    }

    protected T querySingleResult(RestrictionGenerator... generators) {
        return querySingleResult(Arrays.asList(generators));
    }

    protected T querySingleResult(Iterable<RestrictionGenerator> generators) {
        List<T> resultList = queryPageResult(2, 0, generators);

        if (resultList.isEmpty()) {
            return null;
        }

        if (resultList.size() > 1) {
            String info = String.format("Find more than one entity of %s with generators: %s",
                                        prototype.getName(), generators.toString());
            log.warn(info);
            throw new IllegalStateException(info);
        }

        return resultList.get(0);
    }

    protected Object querySingleFieldsResult(Selections selections, Iterable<RestrictionGenerator> generators) {
        return querySingleFieldsResult(Object[].class, selections, generators);
    }

    protected Object querySingleFieldsResult(Selections selections, RestrictionGenerator... generators) {
        return querySingleFieldsResult(Object[].class, selections, Arrays.asList(generators));
    }

    protected <F> F querySingleFieldsResult(Class<F> wrapper, Selections selections,
                                            RestrictionGenerator... generators) {
        return querySingleFieldsResult(wrapper, selections, Arrays.asList(generators));
    }

    protected <F> F querySingleFieldsResult(Class<F> wrapper, Selections selections,
                                            Iterable<RestrictionGenerator> generators) {
        List<F> resultList = queryPageFieldsResult(2, 0, wrapper, selections, generators);

        if (resultList.isEmpty()) {
            return null;
        }

        if (resultList.size() > 1) {
            String info = String.format("Find more than one entity of %s with generators: %s",
                                        prototype.getName(), generators.toString());
            log.warn(info);
            throw new IllegalStateException(info);
        }

        return resultList.get(0);
    }

    protected T querySingleResultLocked(LockModeType lockModeType, RestrictionGenerator... generators) {
        return querySingleResultLocked(lockModeType, Arrays.asList(generators));
    }

    protected T querySingleResultLocked(LockModeType lockModeType, Iterable<RestrictionGenerator> generators) {
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
                                        prototype.getName(), generators.toString());
            log.info(info);
            return null;
        } else if (resultList.size() > 1) {
            String info = String.format("Find more than one entity of %s with generators: %s",
                                        prototype.getName(), generators.toString());
            log.warn(info);
            throw new IllegalStateException(info);
        }

        return resultList.get(0);
    }

    protected List<T> queryListResult(GroupByGenerator groupByGenerator, Iterable<RestrictionGenerator> generators) {
        return queryListResult(null, groupByGenerator, generators);
    }

    protected List<T> queryListResult(Iterable<RestrictionGenerator> generators) {
        return queryListResult(null, null, generators);
    }

    protected List<T> queryListResult(RestrictionGenerator... generators) {
        return queryListResult(null, null, generators);
    }

    protected List<T> queryListResult(GroupByGenerator groupByGenerator, RestrictionGenerator... generators) {
        return queryListResult(null, groupByGenerator, generators);
    }

    protected List<T> queryListResult(OrderByGenerator orderByGenerator, Iterable<RestrictionGenerator> generators) {
        return queryListResult(orderByGenerator, null, generators);
    }

    protected List<T> queryListResult(OrderByGenerator orderByGenerator, GroupByGenerator groupByGenerator,
                                      RestrictionGenerator... generators) {
        return queryListResult(orderByGenerator, groupByGenerator, Arrays.asList(generators));
    }

    protected List<T> queryListResult(OrderByGenerator orderByGenerator, RestrictionGenerator... generators) {
        return queryListResult(orderByGenerator, null, generators);
    }

    protected List<T> queryListResult(OrderByGenerator orderByGenerator, GroupByGenerator groupByGenerator,
                                      Iterable<RestrictionGenerator> generators) {
        CriteriaQuery<T> criteria = createQueryCriteriaByAttributes();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        appendOrderBy(criteriaBuilder, criteria, orderByGenerator);

        Predicate[] generatedRestrictions = generateRestrictions(criteriaBuilder, criteria, generators);
        criteria.where(generatedRestrictions);

        if (groupByGenerator != null) {
            criteria.groupBy(groupByGenerator.generate(getRoot(criteria)));
        }

        List<T> resultList = entityManager.createQuery(criteria).getResultList();
        if (resultList.isEmpty()) {
            String info = String.format("Failed to find entity of %s with generators: %s",
                                        prototype.getName(), generators.toString());
            log.info(info);
        }

        return resultList;
    }

    protected List queryListFieldsResult(Selections selection, Iterable<RestrictionGenerator> generators) {
        return queryListFieldsResult(selection, null, null, generators);
    }

    protected List queryListFieldsResult(Selections selection, GroupByGenerator groupByGenerator,
                                         Iterable<RestrictionGenerator> generators) {
        return queryListFieldsResult(selection, null, groupByGenerator, generators);
    }

    protected List queryListFieldsResult(Selections selection, RestrictionGenerator... generators) {
        return queryListFieldsResult(selection, null, null, generators);
    }

    protected List queryListFieldsResult(Selections selection, GroupByGenerator groupByGenerator,
                                         RestrictionGenerator... generators) {
        return queryListFieldsResult(selection, null, groupByGenerator, generators);
    }

    protected List queryListFieldsResult(Selections selection, OrderByGenerator orderByGenerator,
                                         Iterable<RestrictionGenerator> generators) {
        return queryListFieldsResult(selection, orderByGenerator, null, generators);
    }

    protected List queryListFieldsResult(Selections selection, OrderByGenerator orderByGenerator,
                                         GroupByGenerator groupByGenerator, Iterable<RestrictionGenerator> generators) {
        return queryListFieldsResult(Object[].class, selection, orderByGenerator, groupByGenerator, generators);
    }

    protected List queryListFieldsResult(Selections selection, OrderByGenerator orderByGenerator,
                                         RestrictionGenerator... generators) {
        return queryListFieldsResult(Object[].class, selection, orderByGenerator, null, generators);
    }

    protected List queryListFieldsResult(Selections selection, OrderByGenerator orderByGenerator,
                                         GroupByGenerator groupByGenerator, RestrictionGenerator... generators) {
        return queryListFieldsResult(Object[].class, selection, orderByGenerator, groupByGenerator, generators);
    }

    protected <F> List<F> queryListFieldsResult(Class<F> wrapper, Selections selection,
                                                Iterable<RestrictionGenerator> generators) {
        return queryListFieldsResult(wrapper, selection, null, null, generators);
    }

    protected <F> List<F> queryListFieldsResult(Class<F> wrapper, Selections selection,
                                                GroupByGenerator groupByGenerator,
                                                Iterable<RestrictionGenerator> generators) {
        return queryListFieldsResult(wrapper, selection, null, groupByGenerator, generators);
    }

    protected <F> List<F> queryListFieldsResult(Class<F> wrapper, Selections selection,
                                                RestrictionGenerator... generators) {
        return queryListFieldsResult(wrapper, selection, null, null, generators);
    }

    protected <F> List<F> queryListFieldsResult(Class<F> wrapper, Selections selection,
                                                GroupByGenerator groupByGenerator, RestrictionGenerator... generators) {
        return queryListFieldsResult(wrapper, selection, null, groupByGenerator, generators);
    }

    protected <F> List<F> queryListFieldsResult(Class<F> wrapper, Selections selection,
                                                OrderByGenerator orderByGenerator,
                                                Iterable<RestrictionGenerator> generators) {
        return queryListFieldsResult(wrapper, selection, orderByGenerator, null, generators);
    }

    protected <F> List<F> queryListFieldsResult(Class<F> wrapper, Selections selection,
                                                OrderByGenerator orderByGenerator, GroupByGenerator groupByGenerator,
                                                RestrictionGenerator... generators) {
        return queryListFieldsResult(wrapper, selection, orderByGenerator, groupByGenerator, Arrays.asList(generators));
    }

    protected <F> List<F> queryListFieldsResult(Class<F> wrapper, Selections selection,
                                                OrderByGenerator orderByGenerator, RestrictionGenerator... generators) {
        return queryListFieldsResult(wrapper, selection, orderByGenerator, null, Arrays.asList(generators));
    }

    protected <F> List<F> queryListFieldsResult(Class<F> wrapper, Selections selection,
                                                OrderByGenerator orderByGenerator, GroupByGenerator groupByGenerator,
                                                Iterable<RestrictionGenerator> generators) {
        CriteriaQuery criteria = createQuery(wrapper);
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        appendOrderBy(criteriaBuilder, criteria, orderByGenerator);

        generateSelect(criteria, criteriaBuilder, wrapper, selection);

        Predicate[] generatedRestrictions = generateRestrictions(criteriaBuilder, criteria, generators);
        criteria.where(generatedRestrictions);

        if (groupByGenerator != null) {
            criteria.groupBy(groupByGenerator.generate(getRoot(criteria)));
        }

        List<F> resultList = entityManager.createQuery(criteria).getResultList();
        if (resultList.isEmpty()) {
            String info = String.format("Failed to find entity of %s with generators: %s",
                                        prototype.getName(), generators.toString());
            log.info(info);
        }

        return resultList;
    }

    protected List<T> queryPageResult(int pageSize, int pageIndex, Iterable<RestrictionGenerator> generators) {
        return queryPageResult(pageSize, pageIndex, null, null, generators);
    }

    protected List<T> queryPageResult(int pageSize, int pageIndex, GroupByGenerator groupByGenerator,
                                      Iterable<RestrictionGenerator> generators) {
        return queryPageResult(pageSize, pageIndex, null, groupByGenerator, generators);
    }

    protected List<T> queryPageResult(int pageSize, int pageIndex, RestrictionGenerator... generators) {
        return queryPageResult(pageSize, pageIndex, null, null, generators);
    }

    protected List<T> queryPageResult(int pageSize, int pageIndex, GroupByGenerator groupByGenerator,
                                      RestrictionGenerator... generators) {
        return queryPageResult(pageSize, pageIndex, null, groupByGenerator, generators);
    }

    protected List<T> queryPageResult(int pageSize, int pageIndex, OrderByGenerator orderByGenerator,
                                      Iterable<RestrictionGenerator> generators) {
        return queryPageResult(pageSize, pageIndex, orderByGenerator, null, generators);
    }

    protected List<T> queryPageResult(int pageSize, int pageIndex, OrderByGenerator orderByGenerator,
                                      GroupByGenerator groupByGenerator, RestrictionGenerator... generators) {
        return queryPageResult(pageSize, pageIndex, orderByGenerator, groupByGenerator, Arrays.asList(generators));
    }

    protected List<T> queryPageResult(int pageSize, int pageIndex, OrderByGenerator orderByGenerator,
                                      RestrictionGenerator... generators) {
        return queryPageResult(pageSize, pageIndex, orderByGenerator, null, generators);
    }

    protected List<T> queryPageResult(int pageSize, int pageIndex, OrderByGenerator orderByGenerator,
                                      GroupByGenerator groupByGenerator, Iterable<RestrictionGenerator> generators) {
        CriteriaQuery<T> criteria = createQueryCriteriaByAttributes();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        appendOrderBy(criteriaBuilder, criteria, orderByGenerator);

        Predicate[] generatedRestrictions = generateRestrictions(criteriaBuilder, criteria, generators);

        criteria.where(generatedRestrictions);

        if (groupByGenerator != null) {
            criteria.groupBy(groupByGenerator.generate(getRoot(criteria)));
        }

        List<T> resultList = doQueryWithPage(criteria, pageSize, pageIndex);

        if (resultList.isEmpty()) {
            String info = String.format("Failed to find entity of %s, of page (%d, %d), with generators (%s)",
                                        prototype.getName(), pageSize, pageIndex, generators.toString());
            log.info(info);
        }

        return resultList;
    }

    protected List queryPageFieldsResult(int pageSize, int pageIndex, Selections selection,
                                         Iterable<RestrictionGenerator> generators) {
        return queryPageFieldsResult(pageSize, pageIndex, selection, null, null, generators);
    }

    protected List queryPageFieldsResult(int pageSize, int pageIndex, Selections selection,
                                         GroupByGenerator groupByGenerator, Iterable<RestrictionGenerator> generators) {
        return queryPageFieldsResult(pageSize, pageIndex, selection, null, groupByGenerator, generators);
    }

    protected List queryPageFieldsResult(int pageSize, int pageIndex, Selections selection,
                                         RestrictionGenerator... generators) {
        return queryPageFieldsResult(pageSize, pageIndex, selection, null, null, generators);
    }

    protected List queryPageFieldsResult(int pageSize, int pageIndex, Selections selection,
                                         GroupByGenerator groupByGenerator, RestrictionGenerator... generators) {
        return queryPageFieldsResult(pageSize, pageIndex, selection, null, groupByGenerator, generators);
    }

    protected List queryPageFieldsResult(int pageSize, int pageIndex, Selections selection,
                                         OrderByGenerator orderByGenerator, Iterable<RestrictionGenerator> generators) {
        return queryPageFieldsResult(pageSize, pageIndex, selection, orderByGenerator, null, generators);
    }

    protected List queryPageFieldsResult(int pageSize, int pageIndex, Selections selection,
                                         OrderByGenerator orderByGenerator, GroupByGenerator groupByGenerator,
                                         Iterable<RestrictionGenerator> generators) {
        return queryPageFieldsResult(pageSize, pageIndex, selection, orderByGenerator, groupByGenerator,
                                     StreamSupport.stream(generators.spliterator(), false).toArray(
                                         size -> new RestrictionGenerator[size]));
    }

    protected List queryPageFieldsResult(int pageSize, int pageIndex, Selections selection,
                                         OrderByGenerator orderByGenerator, RestrictionGenerator... generators) {
        return queryPageFieldsResult(pageSize, pageIndex, Object[].class, selection, orderByGenerator, null,
                                     generators);
    }

    protected List queryPageFieldsResult(int pageSize, int pageIndex, Selections selection,
                                         OrderByGenerator orderByGenerator, GroupByGenerator groupByGenerator,
                                         RestrictionGenerator... generators) {
        return queryPageFieldsResult(pageSize, pageIndex, Object[].class, selection, orderByGenerator, groupByGenerator,
                                     generators);
    }

    protected <F> List<F> queryPageFieldsResult(int pageSize, int pageIndex, Class<F> wrapper, Selections selection,
                                                Iterable<RestrictionGenerator> generators) {
        return queryPageFieldsResult(pageSize, pageIndex, wrapper, selection, null, null, generators);
    }

    protected <F> List<F> queryPageFieldsResult(int pageSize, int pageIndex, Class<F> wrapper, Selections selection,
                                                GroupByGenerator groupByGenerator,
                                                Iterable<RestrictionGenerator> generators) {
        return queryPageFieldsResult(pageSize, pageIndex, wrapper, selection, null, groupByGenerator, generators);
    }

    protected <F> List<F> queryPageFieldsResult(int pageSize, int pageIndex, Class<F> wrapper, Selections selection,
                                                RestrictionGenerator... generators) {
        return queryPageFieldsResult(pageSize, pageIndex, wrapper, selection, null, null, generators);
    }

    protected <F> List<F> queryPageFieldsResult(int pageSize, int pageIndex, Class<F> wrapper, Selections selection,
                                                GroupByGenerator groupByGenerator, RestrictionGenerator... generators) {
        return queryPageFieldsResult(pageSize, pageIndex, wrapper, selection, null, groupByGenerator, generators);
    }

    protected <F> List<F> queryPageFieldsResult(int pageSize, int pageIndex, Class<F> wrapper, Selections selection,
                                                OrderByGenerator orderByGenerator,
                                                Iterable<RestrictionGenerator> generators) {
        return queryPageFieldsResult(pageSize, pageIndex, wrapper, selection, orderByGenerator, null, generators);
    }

    protected <F> List<F> queryPageFieldsResult(int pageSize, int pageIndex, Class<F> wrapper, Selections selection,
                                                OrderByGenerator orderByGenerator, GroupByGenerator groupByGenerator,
                                                RestrictionGenerator... generators) {
        return queryPageFieldsResult(pageSize, pageIndex, wrapper, selection, orderByGenerator, groupByGenerator,
                                     generators);
    }

    protected <F> List<F> queryPageFieldsResult(int pageSize, int pageIndex, Class<F> wrapper, Selections selection,
                                                OrderByGenerator orderByGenerator, GroupByGenerator groupByGenerator,
                                                Iterable<RestrictionGenerator> generators) {
        CriteriaQuery<F> criteria = createQuery(wrapper);
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        appendOrderBy(criteriaBuilder, criteria, orderByGenerator);

        generateSelect(criteria, criteriaBuilder, wrapper, selection);

        Predicate[] generatedRestrictions = generateRestrictions(criteriaBuilder, criteria, generators);

        criteria.where(generatedRestrictions);

        if (groupByGenerator != null) {
            criteria.groupBy(groupByGenerator.generate(getRoot(criteria)));
        }
        List resultList = doQueryWithPage(criteria, pageSize, pageIndex);

        if (resultList.isEmpty()) {
            String info = String.format("Failed to find entity of %s, of page (%d, %d), with generators (%s)",
                                        prototype.getName(), pageSize, pageIndex, generators.toString());
            log.info(info);
        }

        return resultList;
    }

    private <F> void generateSelect(CriteriaQuery criteria, CriteriaBuilder criteriaBuilder, Class<F> wrapper,
                                    Selections selections) {
        Root root = getRoot(criteria);
        List<Selection> paths = new ArrayList();
        selections.getSelectionGenerators().forEach(
            (selection) -> paths.add(selection.generate(root, criteriaBuilder)));

        if (wrapper.isArray()) {
            criteria.multiselect(paths);
        } else {
            criteria.select(criteriaBuilder.construct(wrapper, paths.toArray(new Selection[0])));
        }
        criteria.distinct(selections.isDistinct());
    }

    private void appendOrderBy(CriteriaBuilder criteriaBuilder, CriteriaQuery criteria,
                               OrderByGenerator orderByGenerator) {
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

    protected Selections filter(SelectionGenerator... selections) {
        return new Selections(false, Arrays.asList(selections));
    }

    protected Selections filter(Iterable<SelectionGenerator> selections) {
        return new Selections(false, selections);
    }

    protected Selections filter(boolean distinct, SelectionGenerator... selections) {
        return new Selections(distinct, Arrays.asList(selections));
    }

    protected Selections filter(boolean distinct, Iterable<SelectionGenerator> selections) {
        return new Selections(distinct, selections);
    }

    protected Selections distinct(SelectionGenerator... selections) {
        return new Selections(true, Arrays.asList(selections));
    }

    protected Selections distinct(Iterable<SelectionGenerator> selections) {
        return new Selections(true, selections);
    }

    protected SelectionGenerator min(String fieldName) {
        return new MinSelectionGenerator(fieldName);
    }

    protected SelectionGenerator max(String fieldName) {
        return new MaxSelectionGenerator(fieldName);
    }

    protected SelectionGenerator sum(String fieldName) {
        return new SumSelectionGenerator(fieldName);
    }

    protected SelectionGenerator abs(String fieldName) {
        return new AbsSelectionGenerator(fieldName);
    }

    protected SelectionGenerator avg(String fieldName) {
        return new AvgSelectionGenerator(fieldName);
    }

    protected SelectionGenerator count(String fieldName) {
        return new CountSelectionGenerator(fieldName);
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

    protected ExpressionGenerator subquery(Class<?> fromType, String targetField,
                                           RestrictionGenerator... byRestrictions) {
        return SubqueryExpressionGenerator.subquery(fromType, targetField, Arrays.asList(byRestrictions));
    }

    protected ExpressionGenerator subquery(Class<?> fromType, String targetField,
                                           Iterable<RestrictionGenerator> byRestrictions) {
        return SubqueryExpressionGenerator.subquery(fromType, targetField, byRestrictions);
    }

    protected RestrictionGenerator or(RestrictionGenerator... generators) {
        return new OrRestrictionGenerator(Arrays.asList(generators));
    }

    protected RestrictionGenerator or(Iterable<RestrictionGenerator> generators) {
        return new OrRestrictionGenerator(generators);
    }

    protected RestrictionGenerator and(RestrictionGenerator... generators) {
        return new AndRestrictionGenerator(Arrays.asList(generators));
    }

    protected RestrictionGenerator and(Iterable<RestrictionGenerator> generators) {
        return new AndRestrictionGenerator(generators);
    }

    protected GroupByGenerator groupby(String... fieldNames) {
        return new GroupByGenerator(fieldNames);
    }

    private RestrictionGenerator nop() {
        return new RestrictionGenerator() {
            @Override
            public List<Predicate> generate(CriteriaBuilder criteriaBuilder, AbstractQuery<?> query) {
                return new ArrayList<>();
            }
        };
    }

    protected RestrictionGenerator queryIf(boolean condition, RestrictionGenerator generator) {
        if (condition) {
            return generator;
        } else {
            return nop();
        }
    }
}