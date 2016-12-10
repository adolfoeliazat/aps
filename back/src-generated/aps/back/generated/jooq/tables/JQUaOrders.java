/**
 * This class is generated by jOOQ
 */
package aps.back.generated.jooq.tables;


import aps.back.generated.jooq.JQPublic;
import aps.back.generated.jooq.Keys;
import aps.back.generated.jooq.enums.JQUaDocumentType;
import aps.back.generated.jooq.enums.JQUaOrderState;
import aps.back.generated.jooq.tables.records.JQUaOrdersRecord;

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
public class JQUaOrders extends TableImpl<JQUaOrdersRecord> {

    private static final long serialVersionUID = -2013547661;

    /**
     * The reference instance of <code>public.ua_orders</code>
     */
    public static final JQUaOrders UA_ORDERS = new JQUaOrders();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<JQUaOrdersRecord> getRecordType() {
        return JQUaOrdersRecord.class;
    }

    /**
     * The column <code>public.ua_orders.id</code>.
     */
    public final TableField<JQUaOrdersRecord, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('ua_orders_id_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>public.ua_orders.deleted</code>.
     */
    public final TableField<JQUaOrdersRecord, Boolean> DELETED = createField("deleted", org.jooq.impl.SQLDataType.BOOLEAN.nullable(false), this, "");

    /**
     * The column <code>public.ua_orders.inserted_at</code>.
     */
    public final TableField<JQUaOrdersRecord, Timestamp> INSERTED_AT = createField("inserted_at", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "");

    /**
     * The column <code>public.ua_orders.updated_at</code>.
     */
    public final TableField<JQUaOrdersRecord, Timestamp> UPDATED_AT = createField("updated_at", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "");

    /**
     * The column <code>public.ua_orders.tsv</code>.
     */
    public final TableField<JQUaOrdersRecord, Object> TSV = createField("tsv", org.jooq.impl.DefaultDataType.getDefaultDataType("tsvector"), this, "");

    /**
     * The column <code>public.ua_orders.creator_id</code>.
     */
    public final TableField<JQUaOrdersRecord, Long> CREATOR_ID = createField("creator_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.ua_orders.customer_id</code>.
     */
    public final TableField<JQUaOrdersRecord, Long> CUSTOMER_ID = createField("customer_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.ua_orders.title</code>.
     */
    public final TableField<JQUaOrdersRecord, String> TITLE = createField("title", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>public.ua_orders.document_type</code>.
     */
    public final TableField<JQUaOrdersRecord, JQUaDocumentType> DOCUMENT_TYPE = createField("document_type", org.jooq.util.postgres.PostgresDataType.VARCHAR.asEnumDataType(aps.back.generated.jooq.enums.JQUaDocumentType.class), this, "");

    /**
     * The column <code>public.ua_orders.deadline</code>.
     */
    public final TableField<JQUaOrdersRecord, Timestamp> DEADLINE = createField("deadline", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "");

    /**
     * The column <code>public.ua_orders.price</code>.
     */
    public final TableField<JQUaOrdersRecord, Integer> PRICE = createField("price", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>public.ua_orders.num_pages</code>.
     */
    public final TableField<JQUaOrdersRecord, Integer> NUM_PAGES = createField("num_pages", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>public.ua_orders.num_sources</code>.
     */
    public final TableField<JQUaOrdersRecord, Integer> NUM_SOURCES = createField("num_sources", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>public.ua_orders.details</code>.
     */
    public final TableField<JQUaOrdersRecord, String> DETAILS = createField("details", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>public.ua_orders.admin_notes</code>.
     */
    public final TableField<JQUaOrdersRecord, String> ADMIN_NOTES = createField("admin_notes", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>public.ua_orders.state</code>.
     */
    public final TableField<JQUaOrdersRecord, JQUaOrderState> STATE = createField("state", org.jooq.util.postgres.PostgresDataType.VARCHAR.asEnumDataType(aps.back.generated.jooq.enums.JQUaOrderState.class), this, "");

    /**
     * The column <code>public.ua_orders.writer_id</code>.
     */
    public final TableField<JQUaOrdersRecord, Long> WRITER_ID = createField("writer_id", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * Create a <code>public.ua_orders</code> table reference
     */
    public JQUaOrders() {
        this("ua_orders", null);
    }

    /**
     * Create an aliased <code>public.ua_orders</code> table reference
     */
    public JQUaOrders(String alias) {
        this(alias, UA_ORDERS);
    }

    private JQUaOrders(String alias, Table<JQUaOrdersRecord> aliased) {
        this(alias, aliased, null);
    }

    private JQUaOrders(String alias, Table<JQUaOrdersRecord> aliased, Field<?>[] parameters) {
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
    public Identity<JQUaOrdersRecord, Long> getIdentity() {
        return Keys.IDENTITY_UA_ORDERS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<JQUaOrdersRecord> getPrimaryKey() {
        return Keys.UA_ORDERS_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<JQUaOrdersRecord>> getKeys() {
        return Arrays.<UniqueKey<JQUaOrdersRecord>>asList(Keys.UA_ORDERS_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<JQUaOrdersRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<JQUaOrdersRecord, ?>>asList(Keys.UA_ORDERS__UA_ORDERS_CREATOR_ID_FKEY, Keys.UA_ORDERS__UA_ORDERS_CUSTOMER_ID_FKEY, Keys.UA_ORDERS__UA_ORDERS_WRITER_ID_FKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JQUaOrders as(String alias) {
        return new JQUaOrders(alias, this);
    }

    /**
     * Rename this table
     */
    public JQUaOrders rename(String name) {
        return new JQUaOrders(name, null);
    }
}