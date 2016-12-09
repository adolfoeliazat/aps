/**
 * This class is generated by jOOQ
 */
package aps.back.generated.jooq.tables;


import aps.back.generated.jooq.JQPublic;
import aps.back.generated.jooq.Keys;
import aps.back.generated.jooq.tables.records.JQFilesRecord;

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
public class JQFiles extends TableImpl<JQFilesRecord> {

    private static final long serialVersionUID = -572261197;

    /**
     * The reference instance of <code>public.files</code>
     */
    public static final JQFiles FILES = new JQFiles();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<JQFilesRecord> getRecordType() {
        return JQFilesRecord.class;
    }

    /**
     * The column <code>public.files.id</code>.
     */
    public final TableField<JQFilesRecord, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('files_id_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>public.files.deleted</code>.
     */
    public final TableField<JQFilesRecord, Boolean> DELETED = createField("deleted", org.jooq.impl.SQLDataType.BOOLEAN.nullable(false), this, "");

    /**
     * The column <code>public.files.inserted_at</code>.
     */
    public final TableField<JQFilesRecord, Timestamp> INSERTED_AT = createField("inserted_at", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "");

    /**
     * The column <code>public.files.updated_at</code>.
     */
    public final TableField<JQFilesRecord, Timestamp> UPDATED_AT = createField("updated_at", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "");

    /**
     * The column <code>public.files.creator_id</code>.
     */
    public final TableField<JQFilesRecord, Long> CREATOR_ID = createField("creator_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.files.content</code>.
     */
    public final TableField<JQFilesRecord, byte[]> CONTENT = createField("content", org.jooq.impl.SQLDataType.BLOB.nullable(false), this, "");

    /**
     * The column <code>public.files.name</code>.
     */
    public final TableField<JQFilesRecord, String> NAME = createField("name", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>public.files.title</code>.
     */
    public final TableField<JQFilesRecord, String> TITLE = createField("title", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>public.files.mime</code>.
     */
    public final TableField<JQFilesRecord, String> MIME = createField("mime", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>public.files.size_bytes</code>.
     */
    public final TableField<JQFilesRecord, Integer> SIZE_BYTES = createField("size_bytes", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>public.files.details</code>.
     */
    public final TableField<JQFilesRecord, String> DETAILS = createField("details", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>public.files.admin_notes</code>.
     */
    public final TableField<JQFilesRecord, String> ADMIN_NOTES = createField("admin_notes", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * Create a <code>public.files</code> table reference
     */
    public JQFiles() {
        this("files", null);
    }

    /**
     * Create an aliased <code>public.files</code> table reference
     */
    public JQFiles(String alias) {
        this(alias, FILES);
    }

    private JQFiles(String alias, Table<JQFilesRecord> aliased) {
        this(alias, aliased, null);
    }

    private JQFiles(String alias, Table<JQFilesRecord> aliased, Field<?>[] parameters) {
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
    public Identity<JQFilesRecord, Long> getIdentity() {
        return Keys.IDENTITY_FILES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<JQFilesRecord> getPrimaryKey() {
        return Keys.FILES_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<JQFilesRecord>> getKeys() {
        return Arrays.<UniqueKey<JQFilesRecord>>asList(Keys.FILES_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<JQFilesRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<JQFilesRecord, ?>>asList(Keys.FILES__FILES_CREATOR_ID_FKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JQFiles as(String alias) {
        return new JQFiles(alias, this);
    }

    /**
     * Rename this table
     */
    public JQFiles rename(String name) {
        return new JQFiles(name, null);
    }
}
