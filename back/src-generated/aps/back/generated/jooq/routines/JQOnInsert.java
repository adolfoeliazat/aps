/*
 * This file is generated by jOOQ.
*/
package aps.back.generated.jooq.routines;


import aps.back.generated.jooq.JQPublic;

import javax.annotation.Generated;

import org.jooq.Parameter;
import org.jooq.impl.AbstractRoutine;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.9.0"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class JQOnInsert extends AbstractRoutine<Object> {

    private static final long serialVersionUID = -259428727;

    /**
     * The parameter <code>public.on_insert.RETURN_VALUE</code>.
     */
    public static final Parameter<Object> RETURN_VALUE = createParameter("RETURN_VALUE", org.jooq.impl.DefaultDataType.getDefaultDataType("trigger"), false, false);

    /**
     * Create a new routine call instance
     */
    public JQOnInsert() {
        super("on_insert", JQPublic.PUBLIC, org.jooq.impl.DefaultDataType.getDefaultDataType("trigger"));

        setReturnParameter(RETURN_VALUE);
    }
}
