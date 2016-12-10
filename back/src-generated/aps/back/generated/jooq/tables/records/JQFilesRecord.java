/**
 * This class is generated by jOOQ
 */
package aps.back.generated.jooq.tables.records;


import aps.back.generated.jooq.tables.JQFiles;

import java.sql.Timestamp;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record12;
import org.jooq.Row12;
import org.jooq.impl.UpdatableRecordImpl;


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
public class JQFilesRecord extends UpdatableRecordImpl<JQFilesRecord> implements Record12<Long, Boolean, Timestamp, Timestamp, Long, byte[], String, String, String, Integer, String, String> {

    private static final long serialVersionUID = 1466266536;

    /**
     * Setter for <code>public.files.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.files.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.files.deleted</code>.
     */
    public void setDeleted(Boolean value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.files.deleted</code>.
     */
    public Boolean getDeleted() {
        return (Boolean) get(1);
    }

    /**
     * Setter for <code>public.files.inserted_at</code>.
     */
    public void setInsertedAt(Timestamp value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.files.inserted_at</code>.
     */
    public Timestamp getInsertedAt() {
        return (Timestamp) get(2);
    }

    /**
     * Setter for <code>public.files.updated_at</code>.
     */
    public void setUpdatedAt(Timestamp value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.files.updated_at</code>.
     */
    public Timestamp getUpdatedAt() {
        return (Timestamp) get(3);
    }

    /**
     * Setter for <code>public.files.creator_id</code>.
     */
    public void setCreatorId(Long value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.files.creator_id</code>.
     */
    public Long getCreatorId() {
        return (Long) get(4);
    }

    /**
     * Setter for <code>public.files.content</code>.
     */
    public void setContent(byte[] value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.files.content</code>.
     */
    public byte[] getContent() {
        return (byte[]) get(5);
    }

    /**
     * Setter for <code>public.files.name</code>.
     */
    public void setName(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>public.files.name</code>.
     */
    public String getName() {
        return (String) get(6);
    }

    /**
     * Setter for <code>public.files.title</code>.
     */
    public void setTitle(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>public.files.title</code>.
     */
    public String getTitle() {
        return (String) get(7);
    }

    /**
     * Setter for <code>public.files.mime</code>.
     */
    public void setMime(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>public.files.mime</code>.
     */
    public String getMime() {
        return (String) get(8);
    }

    /**
     * Setter for <code>public.files.size_bytes</code>.
     */
    public void setSizeBytes(Integer value) {
        set(9, value);
    }

    /**
     * Getter for <code>public.files.size_bytes</code>.
     */
    public Integer getSizeBytes() {
        return (Integer) get(9);
    }

    /**
     * Setter for <code>public.files.details</code>.
     */
    public void setDetails(String value) {
        set(10, value);
    }

    /**
     * Getter for <code>public.files.details</code>.
     */
    public String getDetails() {
        return (String) get(10);
    }

    /**
     * Setter for <code>public.files.admin_notes</code>.
     */
    public void setAdminNotes(String value) {
        set(11, value);
    }

    /**
     * Getter for <code>public.files.admin_notes</code>.
     */
    public String getAdminNotes() {
        return (String) get(11);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record12 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row12<Long, Boolean, Timestamp, Timestamp, Long, byte[], String, String, String, Integer, String, String> fieldsRow() {
        return (Row12) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row12<Long, Boolean, Timestamp, Timestamp, Long, byte[], String, String, String, Integer, String, String> valuesRow() {
        return (Row12) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field1() {
        return JQFiles.FILES.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Boolean> field2() {
        return JQFiles.FILES.DELETED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field3() {
        return JQFiles.FILES.INSERTED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field4() {
        return JQFiles.FILES.UPDATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field5() {
        return JQFiles.FILES.CREATOR_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<byte[]> field6() {
        return JQFiles.FILES.CONTENT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field7() {
        return JQFiles.FILES.NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field8() {
        return JQFiles.FILES.TITLE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field9() {
        return JQFiles.FILES.MIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field10() {
        return JQFiles.FILES.SIZE_BYTES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field11() {
        return JQFiles.FILES.DETAILS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field12() {
        return JQFiles.FILES.ADMIN_NOTES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value1() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean value2() {
        return getDeleted();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value3() {
        return getInsertedAt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value4() {
        return getUpdatedAt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value5() {
        return getCreatorId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] value6() {
        return getContent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value7() {
        return getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value8() {
        return getTitle();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value9() {
        return getMime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value10() {
        return getSizeBytes();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value11() {
        return getDetails();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value12() {
        return getAdminNotes();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JQFilesRecord value1(Long value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JQFilesRecord value2(Boolean value) {
        setDeleted(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JQFilesRecord value3(Timestamp value) {
        setInsertedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JQFilesRecord value4(Timestamp value) {
        setUpdatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JQFilesRecord value5(Long value) {
        setCreatorId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JQFilesRecord value6(byte[] value) {
        setContent(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JQFilesRecord value7(String value) {
        setName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JQFilesRecord value8(String value) {
        setTitle(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JQFilesRecord value9(String value) {
        setMime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JQFilesRecord value10(Integer value) {
        setSizeBytes(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JQFilesRecord value11(String value) {
        setDetails(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JQFilesRecord value12(String value) {
        setAdminNotes(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JQFilesRecord values(Long value1, Boolean value2, Timestamp value3, Timestamp value4, Long value5, byte[] value6, String value7, String value8, String value9, Integer value10, String value11, String value12) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        value11(value11);
        value12(value12);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached JQFilesRecord
     */
    public JQFilesRecord() {
        super(JQFiles.FILES);
    }

    /**
     * Create a detached, initialised JQFilesRecord
     */
    public JQFilesRecord(Long id, Boolean deleted, Timestamp insertedAt, Timestamp updatedAt, Long creatorId, byte[] content, String name, String title, String mime, Integer sizeBytes, String details, String adminNotes) {
        super(JQFiles.FILES);

        set(0, id);
        set(1, deleted);
        set(2, insertedAt);
        set(3, updatedAt);
        set(4, creatorId);
        set(5, content);
        set(6, name);
        set(7, title);
        set(8, mime);
        set(9, sizeBytes);
        set(10, details);
        set(11, adminNotes);
    }
}