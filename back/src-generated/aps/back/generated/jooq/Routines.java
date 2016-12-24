/**
 * This class is generated by jOOQ
 */
package aps.back.generated.jooq;


import aps.back.generated.jooq.routines.JQFilesTsvTrigger;
import aps.back.generated.jooq.routines.JQOnInsert;
import aps.back.generated.jooq.routines.JQOnUpdate;
import aps.back.generated.jooq.routines.JQUaOrdersTsvTrigger;
import aps.back.generated.jooq.routines.JQUsersTsvTrigger;

import javax.annotation.Generated;

import org.jooq.Configuration;
import org.jooq.Field;


/**
 * Convenience access to all stored procedures and functions in public
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.8.4"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Routines {

    /**
     * Call <code>public.files_tsv_trigger</code>
     */
    public static Object filesTsvTrigger(Configuration configuration) {
        JQFilesTsvTrigger f = new JQFilesTsvTrigger();

        f.execute(configuration);
        return f.getReturnValue();
    }

    /**
     * Get <code>public.files_tsv_trigger</code> as a field.
     */
    public static Field<Object> filesTsvTrigger() {
        JQFilesTsvTrigger f = new JQFilesTsvTrigger();

        return f.asField();
    }

    /**
     * Call <code>public.on_insert</code>
     */
    public static Object onInsert(Configuration configuration) {
        JQOnInsert f = new JQOnInsert();

        f.execute(configuration);
        return f.getReturnValue();
    }

    /**
     * Get <code>public.on_insert</code> as a field.
     */
    public static Field<Object> onInsert() {
        JQOnInsert f = new JQOnInsert();

        return f.asField();
    }

    /**
     * Call <code>public.on_update</code>
     */
    public static Object onUpdate(Configuration configuration) {
        JQOnUpdate f = new JQOnUpdate();

        f.execute(configuration);
        return f.getReturnValue();
    }

    /**
     * Get <code>public.on_update</code> as a field.
     */
    public static Field<Object> onUpdate() {
        JQOnUpdate f = new JQOnUpdate();

        return f.asField();
    }

    /**
     * Call <code>public.ua_orders_tsv_trigger</code>
     */
    public static Object uaOrdersTsvTrigger(Configuration configuration) {
        JQUaOrdersTsvTrigger f = new JQUaOrdersTsvTrigger();

        f.execute(configuration);
        return f.getReturnValue();
    }

    /**
     * Get <code>public.ua_orders_tsv_trigger</code> as a field.
     */
    public static Field<Object> uaOrdersTsvTrigger() {
        JQUaOrdersTsvTrigger f = new JQUaOrdersTsvTrigger();

        return f.asField();
    }

    /**
     * Call <code>public.users_tsv_trigger</code>
     */
    public static Object usersTsvTrigger(Configuration configuration) {
        JQUsersTsvTrigger f = new JQUsersTsvTrigger();

        f.execute(configuration);
        return f.getReturnValue();
    }

    /**
     * Get <code>public.users_tsv_trigger</code> as a field.
     */
    public static Field<Object> usersTsvTrigger() {
        JQUsersTsvTrigger f = new JQUsersTsvTrigger();

        return f.asField();
    }
}
