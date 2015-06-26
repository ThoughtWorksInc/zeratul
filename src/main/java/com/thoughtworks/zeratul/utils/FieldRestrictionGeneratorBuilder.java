package com.thoughtworks.zeratul.utils;

import com.thoughtworks.zeratul.generator.restriction.*;

import org.apache.log4j.Logger;

import java.util.Date;
import java.util.List;

public class FieldRestrictionGeneratorBuilder {
    private static final Logger log = Logger.getLogger(FieldRestrictionGeneratorBuilder.class);

    private Class<?> fromClass;
    private String fieldName;

    public FieldRestrictionGeneratorBuilder(Class<?> fromClass, String fieldName) {
        this.fromClass = fromClass;
        this.fieldName = fieldName;
    }

    public RestrictionGenerator eq(Object value) {
        return new EqualRestrictionGenerator(fieldName, value);
    }

    public RestrictionGenerator ne(Object value) { return new NotEqualRestrictionGenerator(fieldName, value); }

    public RestrictionGenerator like(String value) {
        return new LikeRestrictionGenerator(fieldName, value);
    }

    public RestrictionGenerator in(Object... values) {
        return new InRestrictionGenerator(fieldName, values);
    }

    public RestrictionGenerator in(List values) {
        if (values.size() == 0) {
            // 当情况是 where 'field' in 空的list时，就等价于查询总是返回空
            log.info("'in' restriction generated with empty list");
            return new EqualRestrictionGenerator("id", -1);
        }
        return new InRestrictionGenerator(fieldName, values.toArray());
    }

    public RestrictionGenerator nin(Object... values) {
        return new NinRestrictionGenerator(fieldName, values);
    }

    public RestrictionGenerator nin(List values) {
        if (values.size() == 0) {
            return new NinRestrictionGenerator(fieldName);
        }
        return new NinRestrictionGenerator(fieldName, values.toArray());
    }

    public RestrictionGenerator between(Date date1, Date date2) {
        return new DateBetweenRestrictionGenerator(fieldName, date1, date2);
    }

    public RestrictionGenerator between(Double number1, Double number2) {
        return new DoubleBetweenRestrictionGenerator(fieldName, number1, number2);
    }

    public RestrictionGenerator between(Long number1, Long number2) {
        return new LongBetweenRestrictionGenerator(fieldName, number1, number2);
    }

    public RestrictionGenerator between(Integer number1, Integer number2) {
        return new IntegerBetweenRestrictionGenerator(fieldName, number1, number2);
    }

    public RestrictionGenerator gt(Date date) {
        return new DateGtRestrictionGenerator(fieldName, date);
    }

    public RestrictionGenerator lt(Date date) {
        return new DateLtRestrictionGenerator(fieldName, date);
    }

    public RestrictionGenerator lte(Date date) {
        return new DateLteRestrictionGenerator(fieldName, date);
    }

    public RestrictionGenerator gte(Date date) {
        return new DateGteRestrictionGenerator(fieldName, date);
    }

    public <T extends Number> RestrictionGenerator gt(T number) {
        return new NumberGtRestrictionGenerator(fieldName, number);
    }

    public <T extends Number> RestrictionGenerator lt(T number) {
        return new NumberLtRestrictionGenerator(fieldName, number);
    }

    public <T extends Number> RestrictionGenerator lte(T number) {
        return new NumberLteRestrictionGenerator(fieldName, number);
    }

    public <T extends Number> RestrictionGenerator gte(T number) {
        return new NumberGteRestrictionGenerator(fieldName, number);
    }
}
