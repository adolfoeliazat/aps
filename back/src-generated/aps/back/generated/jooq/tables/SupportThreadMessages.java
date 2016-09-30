/**
 * This class is generated by jOOQ
 */
package aps.back.generated.jooq.tables;


import aps.back.generated.jooq.Keys;
import aps.back.generated.jooq.Public;
import aps.back.generated.jooq.tables.records.SupportThreadMessagesRecord;

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
public class SupportThreadMessages extends TableImpl<SupportThreadMessagesRecord> {

    private static final long serialVersionUID = 1270544628;

    /**
     * The reference instance of <code>public.support_thread_messages</code>
     */
    public static final SupportThreadMessages SUPPORT_THREAD_MESSAGES = new SupportThreadMessages();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<SupportThreadMessagesRecord> getRecordType() {
        return SupportThreadMessagesRecord.class;
    }

    /**
     * The column <code>public.support_thread_messages.id</code>.
     */
    public final TableField<SupportThreadMessagesRecord, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('support_thread_messages_id_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>public.support_thread_messages.deleted</code>.
     */
    public final TableField<SupportThreadMessagesRecord, Boolean> DELETED = createField("deleted", org.jooq.impl.SQLDataType.BOOLEAN.nullable(false), this, "");

    /**
     * The column <code>public.support_thread_messages.inserted_at</code>.
     */
    public final TableField<SupportThreadMessagesRecord, Timestamp> INSERTED_AT = createField("inserted_at", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "");

    /**
     * The column <code>public.support_thread_messages.updated_at</code>.
     */
    public final TableField<SupportThreadMessagesRecord, Timestamp> UPDATED_AT = createField("updated_at", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "");

    /**
     * The column <code>public.support_thread_messages.tslang</code>.
     */
    public final TableField<SupportThreadMessagesRecord, String> TSLANG = createField("tslang", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>public.support_thread_messages.thread_id</code>.
     */
    public final TableField<SupportThreadMessagesRecord, Long> THREAD_ID = createField("thread_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.support_thread_messages.sender_id</code>.
     */
    public final TableField<SupportThreadMessagesRecord, Long> SENDER_ID = createField("sender_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.support_thread_messages.recipient_id</code>.
     */
    public final TableField<SupportThreadMessagesRecord, Long> RECIPIENT_ID = createField("recipient_id", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>public.support_thread_messages.message</code>.
     */
    public final TableField<SupportThreadMessagesRecord, String> MESSAGE = createField("message", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>public.support_thread_messages.data</code>.
     */
    public final TableField<SupportThreadMessagesRecord, Object> DATA = createField("data", org.jooq.impl.DefaultDataType.getDefaultDataType("jsonb"), this, "");

    /**
     * Create a <code>public.support_thread_messages</code> table reference
     */
    public SupportThreadMessages() {
        this("support_thread_messages", null);
    }

    /**
     * Create an aliased <code>public.support_thread_messages</code> table reference
     */
    public SupportThreadMessages(String alias) {
        this(alias, SUPPORT_THREAD_MESSAGES);
    }

    private SupportThreadMessages(String alias, Table<SupportThreadMessagesRecord> aliased) {
        this(alias, aliased, null);
    }

    private SupportThreadMessages(String alias, Table<SupportThreadMessagesRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<SupportThreadMessagesRecord, Long> getIdentity() {
        return Keys.IDENTITY_SUPPORT_THREAD_MESSAGES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<SupportThreadMessagesRecord> getPrimaryKey() {
        return Keys.SUPPORT_THREAD_MESSAGES_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<SupportThreadMessagesRecord>> getKeys() {
        return Arrays.<UniqueKey<SupportThreadMessagesRecord>>asList(Keys.SUPPORT_THREAD_MESSAGES_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<SupportThreadMessagesRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<SupportThreadMessagesRecord, ?>>asList(Keys.SUPPORT_THREAD_MESSAGES__SUPPORT_THREAD_MESSAGES_THREAD_ID_FKEY, Keys.SUPPORT_THREAD_MESSAGES__SUPPORT_THREAD_MESSAGES_SENDER_ID_FKEY, Keys.SUPPORT_THREAD_MESSAGES__SUPPORT_THREAD_MESSAGES_RECIPIENT_ID_FKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SupportThreadMessages as(String alias) {
        return new SupportThreadMessages(alias, this);
    }

    /**
     * Rename this table
     */
    public SupportThreadMessages rename(String name) {
        return new SupportThreadMessages(name, null);
    }
}