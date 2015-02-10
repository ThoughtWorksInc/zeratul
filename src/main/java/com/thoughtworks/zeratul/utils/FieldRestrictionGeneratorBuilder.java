package com.thoughtworks.zeratul.utils;

import com.thoughtworks.zeratul.generator.*;
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

    public RestrictionGenerator gt(Double number) {
        return new DoubleGtRestrictionGenerator(fieldName, number);
    }

    public RestrictionGenerator lt(Double number) {
        return new DoubleLtRestrictionGenerator(fieldName, number);
    }

    public RestrictionGenerator lte(Double number) {
        return new DoubleLteRestrictionGenerator(fieldName, number);
    }

    public RestrictionGenerator gte(Double number) {
        return new DoubleGteRestrictionGenerator(fieldName, number);
    }

    public RestrictionGenerator lt(Long number) {
        return new LongLtRestrictionGenerator(fieldName, number);
    }

    public RestrictionGenerator gte(Long number) {
        return new LongGteRestrictionGenerator(fieldName, number);
    }


}
