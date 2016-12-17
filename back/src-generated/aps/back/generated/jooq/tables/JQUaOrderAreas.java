/**
 * This class is generated by jOOQ
 */
package aps.back.generated.jooq.tables;


import aps.back.generated.jooq.JQPublic;
import aps.back.generated.jooq.Keys;
import aps.back.generated.jooq.tables.records.JQUaOrderAreasRecord;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.8.4"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class JQUaOrderAreas extends TableImpl<JQUaOrderAreasRecord> {

    private static final long serialVersionUID = -2064910210;

    /**
     * The reference instance of <code>public.ua_order_areas</code>
     */
    public static final JQUaOrderAreas UA_ORDER_AREAS = new JQUaOrderAreas();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<JQUaOrderAreasRecord> getRecordType() {
        return JQUaOrderAreasRecord.class;
    }

    /**
     * The column <code>public.ua_order_areas.id</code>.
     */
    public final TableField<JQUaOrderAreasRecord, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('ua_order_areas_id_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>public.ua_order_areas.deleted</code>.
     */
    public final TableField<JQUaOrderAreasRecord, Boolean> DELETED = createField("deleted", org.jooq.impl.SQLDataType.BOOLEAN.nullable(false), this, "");

    /**
     * The column <code>public.ua_order_areas.inserted_at</code>.
     */
    public final TableField<JQUaOrderAreasRecord, Timestamp> INSERTED_AT = createField("inserted_at", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "");

    /**
     * The column <code>public.ua_order_areas.updated_at</code>.
     */
    public final TableField<JQUaOrderAreasRecord, Timestamp> UPDATED_AT = createField("updated_at", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "");

    /**
     * The column <code>public.ua_order_areas.ua_order_id</code>.
     */
    public final TableField<JQUaOrderAreasRecord, Long> UA_ORDER_ID = createField("ua_order_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.ua_order_areas.name</code>.
     */
    public final TableField<JQUaOrderAreasRecord, String> NAME = createField("name", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * Create a <code>public.ua_order_areas</code> table reference
     */
    public JQUaOrderAreas() {
        this("ua_order_areas", null);
    }

    /**
     * Create an aliased <code>public.ua_order_areas</code> table reference
     */
    public JQUaOrderAreas(String alias) {
        this(alias, UA_ORDER_AREAS);
    }

    private JQUaOrderAreas(String alias, Table<JQUaOrderAreasRecord> aliased) {
        this(alias, aliased, null);
    }

    private JQUaOrderAreas(String alias, Table<JQUaOrderAreasRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return JQPublic.PUBLIC;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<JQUaOrderAreasRecord, Long> getIdentity() {
        return Keys.IDENTITY_UA_ORDER_AREAS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<JQUaOrderAreasRecord> getPrimaryKey() {
        return Keys.UA_ORDER_AREAS_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<JQUaOrderAreasRecord>> getKeys() {
        return Arrays.<UniqueKey<JQUaOrderAreasRecord>>asList(Keys.UA_ORDER_AREAS_PKEY, Keys.UA_ORDER_AREAS_NAME_KEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<JQUaOrderAreasRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<JQUaOrderAreasRecord, ?>>asList(Keys.UA_ORDER_AREAS__UA_ORDER_AREAS_UA_ORDER_ID_FKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JQUaOrderAreas as(String alias) {
        return new JQUaOrderAreas(alias, this);
    }

    /**
     * Rename this table
     */
    public JQUaOrderAreas rename(String name) {
        return new JQUaOrderAreas(name, null);
    }
}
