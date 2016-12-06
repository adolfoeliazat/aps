/**
 * This class is generated by jOOQ
 */
package aps.back.generated.jooq.tables.records;


import aps.back.generated.jooq.enums.JQDocumentUrgency;
import aps.back.generated.jooq.enums.JQOrderState;
import aps.back.generated.jooq.enums.JQUaDocumentType;
import aps.back.generated.jooq.tables.JQUaOrders;

import java.sql.Timestamp;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record19;
import org.jooq.Row19;
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
public class JQUaOrdersRecord extends UpdatableRecordImpl<JQUaOrdersRecord> implements Record19<Long, Boolean, Timestamp, Timestamp, Object, Long, Long, String, JQUaDocumentType, JQDocumentUrgency, Timestamp, Integer, Integer, Integer, Integer, String, String, JQOrderState, Long> {

    private static final long serialVersionUID = -1821256107;

    /**
     * Setter for <code>public.ua_orders.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.ua_orders.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.ua_orders.deleted</code>.
     */
    public void setDeleted(Boolean value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.ua_orders.deleted</code>.
     */
    public Boolean getDeleted() {
        return (Boolean) get(1);
    }

    /**
     * Setter for <code>public.ua_orders.inserted_at</code>.
     */
    public void setInsertedAt(Timestamp value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.ua_orders.inserted_at</code>.
     */
    public Timestamp getInsertedAt() {
        return (Timestamp) get(2);
    }

    /**
     * Setter for <code>public.ua_orders.updated_at</code>.
     */
    public void setUpdatedAt(Timestamp value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.ua_orders.updated_at</code>.
     */
    public Timestamp getUpdatedAt() {
        return (Timestamp) get(3);
    }

    /**
     * Setter for <code>public.ua_orders.tsv</code>.
     */
    public void setTsv(Object value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.ua_orders.tsv</code>.
     */
    public Object getTsv() {
        return (Object) get(4);
    }

    /**
     * Setter for <code>public.ua_orders.creator_id</code>.
     */
    public void setCreatorId(Long value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.ua_orders.creator_id</code>.
     */
    public Long getCreatorId() {
        return (Long) get(5);
    }

    /**
     * Setter for <code>public.ua_orders.customer_id</code>.
     */
    public void setCustomerId(Long value) {
        set(6, value);
    }

    /**
     * Getter for <code>public.ua_orders.customer_id</code>.
     */
    public Long getCustomerId() {
        return (Long) get(6);
    }

    /**
     * Setter for <code>public.ua_orders.title</code>.
     */
    public void setTitle(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>public.ua_orders.title</code>.
     */
    public String getTitle() {
        return (String) get(7);
    }

    /**
     * Setter for <code>public.ua_orders.document_type</code>.
     */
    public void setDocumentType(JQUaDocumentType value) {
        set(8, value);
    }

    /**
     * Getter for <code>public.ua_orders.document_type</code>.
     */
    public JQUaDocumentType getDocumentType() {
        return (JQUaDocumentType) get(8);
    }

    /**
     * Setter for <code>public.ua_orders.document_urgency</code>.
     */
    public void setDocumentUrgency(JQDocumentUrgency value) {
        set(9, value);
    }

    /**
     * Getter for <code>public.ua_orders.document_urgency</code>.
     */
    public JQDocumentUrgency getDocumentUrgency() {
        return (JQDocumentUrgency) get(9);
    }

    /**
     * Setter for <code>public.ua_orders.deadline</code>.
     */
    public void setDeadline(Timestamp value) {
        set(10, value);
    }

    /**
     * Getter for <code>public.ua_orders.deadline</code>.
     */
    public Timestamp getDeadline() {
        return (Timestamp) get(10);
    }

    /**
     * Setter for <code>public.ua_orders.page_cost</code>.
     */
    public void setPageCost(Integer value) {
        set(11, value);
    }

    /**
     * Getter for <code>public.ua_orders.page_cost</code>.
     */
    public Integer getPageCost() {
        return (Integer) get(11);
    }

    /**
     * Setter for <code>public.ua_orders.price</code>.
     */
    public void setPrice(Integer value) {
        set(12, value);
    }

    /**
     * Getter for <code>public.ua_orders.price</code>.
     */
    public Integer getPrice() {
        return (Integer) get(12);
    }

    /**
     * Setter for <code>public.ua_orders.num_pages</code>.
     */
    public void setNumPages(Integer value) {
        set(13, value);
    }

    /**
     * Getter for <code>public.ua_orders.num_pages</code>.
     */
    public Integer getNumPages() {
        return (Integer) get(13);
    }

    /**
     * Setter for <code>public.ua_orders.num_sources</code>.
     */
    public void setNumSources(Integer value) {
        set(14, value);
    }

    /**
     * Getter for <code>public.ua_orders.num_sources</code>.
     */
    public Integer getNumSources() {
        return (Integer) get(14);
    }

    /**
     * Setter for <code>public.ua_orders.details</code>.
     */
    public void setDetails(String value) {
        set(15, value);
    }

    /**
     * Getter for <code>public.ua_orders.details</code>.
     */
    public String getDetails() {
        return (String) get(15);
    }

    /**
     * Setter for <code>public.ua_orders.admin_notes</code>.
     */
    public void setAdminNotes(String value) {
        set(16, value);
    }

    /**
     * Getter for <code>public.ua_orders.admin_notes</code>.
     */
    public String getAdminNotes() {
        return (String) get(16);
    }

    /**
     * Setter for <code>public.ua_orders.state</code>.
     */
    public void setState(JQOrderState value) {
        set(17, value);
    }

    /**
     * Getter for <code>public.ua_orders.state</code>.
     */
    public JQOrderState getState() {
        return (JQOrderState) get(17);
    }

    /**
     * Setter for <code>public.ua_orders.writer_id</code>.
     */
    public void setWriterId(Long value) {
        set(18, value);
    }

    /**
     * Getter for <code>public.ua_orders.writer_id</code>.
     */
    public Long getWriterId() {
        return (Long) get(18);
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
    // Record19 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row19<Long, Boolean, Timestamp, Timestamp, Object, Long, Long, String, JQUaDocumentType, JQDocumentUrgency, Timestamp, Integer, Integer, Integer, Integer, String, String, JQOrderState, Long> fieldsRow() {
        return (Row19) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row19<Long, Boolean, Timestamp, Timestamp, Object, Long, Long, String, JQUaDocumentType, JQDocumentUrgency, Timestamp, Integer, Integer, Integer, Integer, String, String, JQOrderState, Long> valuesRow() {
        return (Row19) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field1() {
        return JQUaOrders.UA_ORDERS.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Boolean> field2() {
        return JQUaOrders.UA_ORDERS.DELETED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field3() {
        return JQUaOrders.UA_ORDERS.INSERTED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field4() {
        return JQUaOrders.UA_ORDERS.UPDATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Object> field5() {
        return JQUaOrders.UA_ORDERS.TSV;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field6() {
        return JQUaOrders.UA_ORDERS.CREATOR_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field7() {
        return JQUaOrders.UA_ORDERS.CUSTOMER_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field8() {
        return JQUaOrders.UA_ORDERS.TITLE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<JQUaDocumentType> field9() {
        return JQUaOrders.UA_ORDERS.DOCUMENT_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<JQDocumentUrgency> field10() {
        return JQUaOrders.UA_ORDERS.DOCUMENT_URGENCY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field11() {
        return JQUaOrders.UA_ORDERS.DEADLINE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field12() {
        return JQUaOrders.UA_ORDERS.PAGE_COST;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field13() {
        return JQUaOrders.UA_ORDERS.PRICE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field14() {
        return JQUaOrders.UA_ORDERS.NUM_PAGES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field15() {
        return JQUaOrders.UA_ORDERS.NUM_SOURCES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field16() {
        return JQUaOrders.UA_ORDERS.DETAILS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field17() {
        return JQUaOrders.UA_ORDERS.ADMIN_NOTES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<JQOrderState> field18() {
        return JQUaOrders.UA_ORDERS.STATE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field19() {
        return JQUaOrders.UA_ORDERS.WRITER_ID;
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
    public Object value5() {
        return getTsv();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value6() {
        return getCreatorId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value7() {
        return getCustomerId();
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
    public JQUaDocumentType value9() {
        return getDocumentType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JQDocumentUrgency value10() {
        return getDocumentUrgency();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value11() {
        return getDeadline();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value12() {
        return getPageCost();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value13() {
        return getPrice();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value14() {
        return getNumPages();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value15() {
        return getNumSources();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value16() {
        return getDetails();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value17() {
        return getAdminNotes();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JQOrderState value18() {
        return getState();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value19() {
        return getWriterId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JQUaOrdersRecord value1(Long value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JQUaOrdersRecord value2(Boolean value) {
        setDeleted(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JQUaOrdersRecord value3(Timestamp value) {
        setInsertedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JQUaOrdersRecord value4(Timestamp value) {
        setUpdatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JQUaOrdersRecord value5(Object value) {
        setTsv(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JQUaOrdersRecord value6(Long value) {
        setCreatorId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JQUaOrdersRecord value7(Long value) {
        setCustomerId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JQUaOrdersRecord value8(String value) {
        setTitle(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JQUaOrdersRecord value9(JQUaDocumentType value) {
        setDocumentType(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JQUaOrdersRecord value10(JQDocumentUrgency value) {
        setDocumentUrgency(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JQUaOrdersRecord value11(Timestamp value) {
        setDeadline(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JQUaOrdersRecord value12(Integer value) {
        setPageCost(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JQUaOrdersRecord value13(Integer value) {
        setPrice(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JQUaOrdersRecord value14(Integer value) {
        setNumPages(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JQUaOrdersRecord value15(Integer value) {
        setNumSources(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JQUaOrdersRecord value16(String value) {
        setDetails(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JQUaOrdersRecord value17(String value) {
        setAdminNotes(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JQUaOrdersRecord value18(JQOrderState value) {
        setState(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JQUaOrdersRecord value19(Long value) {
        setWriterId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JQUaOrdersRecord values(Long value1, Boolean value2, Timestamp value3, Timestamp value4, Object value5, Long value6, Long value7, String value8, JQUaDocumentType value9, JQDocumentUrgency value10, Timestamp value11, Integer value12, Integer value13, Integer value14, Integer value15, String value16, String value17, JQOrderState value18, Long value19) {
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
        value13(value13);
        value14(value14);
        value15(value15);
        value16(value16);
        value17(value17);
        value18(value18);
        value19(value19);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached JQUaOrdersRecord
     */
    public JQUaOrdersRecord() {
        super(JQUaOrders.UA_ORDERS);
    }

    /**
     * Create a detached, initialised JQUaOrdersRecord
     */
    public JQUaOrdersRecord(Long id, Boolean deleted, Timestamp insertedAt, Timestamp updatedAt, Object tsv, Long creatorId, Long customerId, String title, JQUaDocumentType documentType, JQDocumentUrgency documentUrgency, Timestamp deadline, Integer pageCost, Integer price, Integer numPages, Integer numSources, String details, String adminNotes, JQOrderState state, Long writerId) {
        super(JQUaOrders.UA_ORDERS);

        set(0, id);
        set(1, deleted);
        set(2, insertedAt);
        set(3, updatedAt);
        set(4, tsv);
        set(5, creatorId);
        set(6, customerId);
        set(7, title);
        set(8, documentType);
        set(9, documentUrgency);
        set(10, deadline);
        set(11, pageCost);
        set(12, price);
        set(13, numPages);
        set(14, numSources);
        set(15, details);
        set(16, adminNotes);
        set(17, state);
        set(18, writerId);
    }
}
