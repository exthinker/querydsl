/*
 * Copyright (c) 2010 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.query.hql;

import com.mysema.query.types.Constant;
import com.mysema.query.types.Operator;
import com.mysema.query.types.OperatorImpl;
import com.mysema.query.types.Ops;
import com.mysema.query.types.PathType;
import com.mysema.query.types.Templates;

/**
 * JPQLTemplates extends Templates to provide operator patterns for HQL
 * serialization
 *
 * @author tiwe
 * @see HQLTemplates
 * @see EclipseLinkTemplates
 */
public class JPQLTemplates extends Templates {

    public static final Operator<Object> CAST = new OperatorImpl<Object>("CAST",Object.class, Object.class);

    public static final Operator<Boolean> MEMBER_OF = new OperatorImpl<Boolean>("MEMBER_OF",Object.class, Object.class);

    public static final JPQLTemplates DEFAULT = new JPQLTemplates();

    protected JPQLTemplates() {
        //CHECKSTYLE:OFF
        // boolean
        add(Ops.AND, "{0} and {1}", 36);
        add(Ops.NOT, "not {0}", 3);
        add(Ops.OR, "{0} or {1}", 38);
        add(Ops.XNOR, "{0} xnor {1}", 39);
        add(Ops.XOR, "{0} xor {1}", 39);

        // comparison
        add(Ops.BETWEEN, "{0} between {1} and {2}", 30);

        // numeric
        add(Ops.MathOps.SQRT, "sqrt({0})");
        add(Ops.MOD, "mod({0},{1})", 0);

        // various
        add(Ops.NE_PRIMITIVE, "{0} <> {1}", 25);
        add(Ops.NE_OBJECT, "{0} <> {1}", 25);
        add(Ops.IS_NULL, "{0} is null", 26);
        add(Ops.IS_NOT_NULL, "{0} is not null", 26);

        // collection
        add(MEMBER_OF, "{0} member of {1}");

        add(Ops.IN, "{0} in {1}");
        add(Ops.COL_IS_EMPTY, "{0} is empty");
        add(Ops.COL_SIZE, "size({0})");
        add(Ops.ARRAY_SIZE, "size({0})");

        // string
        add(Ops.LIKE, "{0} like {1}",1);
        add(Ops.CONCAT, "concat({0},{1})",0);
        add(Ops.MATCHES, "{0} like {1}", 27); // TODO : support real regexes
        add(Ops.LOWER, "lower({0})");
        add(Ops.SUBSTR_1ARG, "substring({0},{1}+1)");
        add(Ops.SUBSTR_2ARGS, "substring({0},{1}+1,{2})");
        add(Ops.TRIM, "trim({0})");
        add(Ops.UPPER, "upper({0})");
        add(Ops.EQ_IGNORE_CASE, "{0l} = {1l}");
        add(Ops.CHAR_AT, "cast(substring({0},{1}+1,1) as char)");
        add(Ops.STRING_IS_EMPTY, "length({0}) = 0");

        add(Ops.STRING_CONTAINS, "{0} like {%1%}");
        add(Ops.STRING_CONTAINS_IC, "{0l} like {%%1%%}");
        add(Ops.ENDS_WITH, "{0} like {%1}");
        add(Ops.ENDS_WITH_IC, "{0l} like {%%1}");
        add(Ops.STARTS_WITH, "{0} like {1%}");
        add(Ops.STARTS_WITH_IC, "{0l} like {1%%}");
        add(Ops.INDEX_OF, "locate({1},{0}) - 1");
        add(Ops.INDEX_OF_2ARGS, "locate({1},{0},{2}+1) - 1");

        // date time
        add(Ops.DateTimeOps.SYSDATE, "sysdate");
        add(Ops.DateTimeOps.CURRENT_DATE, "current_date()");
        add(Ops.DateTimeOps.CURRENT_TIME, "current_time()");
        add(Ops.DateTimeOps.CURRENT_TIMESTAMP, "current_timestamp()");

        // path types
        add(PathType.PROPERTY, "{0}.{1s}");
        add(PathType.VARIABLE, "{0s}");

        // case for eq
        add(Ops.CASE_EQ, "case {1} end");
        add(Ops.CASE_EQ_WHEN,  "when {0} = {1} then {2} {3}");
        add(Ops.CASE_EQ_ELSE,  "else {0}");

        add(Ops.INSTANCE_OF, "type({0}) = {1}");

        //CHECKSTYLE:ON
    }

    public boolean wrapElements(Operator<?> operator){
        return false;
    }

    public boolean wrapConstant(Constant<?> expr) {
        return false;
    }

    public boolean isTypeAsString() {
        // TODO : get rid of this when Hibernate supports type(alias)
        return false;
    }
}