package com.thoughtworks.zeratul.generator.orderby;

import com.google.common.collect.Lists;
import com.thoughtworks.zeratul.utils.OrderByGenerator;
import com.thoughtworks.zeratul.geography.Location;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import java.util.List;

public class DistanceOrderByGenerator implements OrderByGenerator {
    private static final String LONGITUDE_FIELD_NAME = "longitude";
    private static final String LATITUDE_FIELD_NAME = "latitude";

    private final Location location;

    public DistanceOrderByGenerator(Location location) {
        this.location = location;
    }

    @Override
    public List<Order> generate(CriteriaBuilder builder, AbstractQuery<?> query) {
        final Root<?> entity = query.getRoots().iterator().next();
        Order order = builder.asc(builder.function("distance_between", Double.class,
                builder.literal(location.getLatitude()), builder.literal(location.getLongitude()),
                entity.get(LATITUDE_FIELD_NAME), entity.get(LONGITUDE_FIELD_NAME)));
        return Lists.newArrayList(order);
    }
}
