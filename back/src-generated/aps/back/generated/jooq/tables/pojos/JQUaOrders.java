/**
 * This class is generated by jOOQ
 */
package aps.back.generated.jooq.tables.pojos;


import aps.back.generated.jooq.enums.JQUaDocumentType;
import aps.back.generated.jooq.enums.JQUaOrderState;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.annotation.Generated;


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
public class JQUaOrders implements Serializable {

    private static final long serialVersionUID = 1914808912;

    private Long             id;
    private Boolean          deleted;
    private Timestamp        insertedAt;
    private Timestamp        updatedAt;
    private Object           tsv;
    private Long             creatorId;
    private Long             customerId;
    private String           title;
    private JQUaDocumentType documentType;
    private Timestamp        deadline;
    private Integer          price;
    private Integer          numPages;
    private Integer          numSources;
    private String           details;
    private String           adminNotes;
    private JQUaOrderState   state;
    private Long             writerId;

    public JQUaOrders() {}

    public JQUaOrders(JQUaOrders value) {
        this.id = value.id;
        this.deleted = value.deleted;
        this.insertedAt = value.insertedAt;
        this.updatedAt = value.updatedAt;
        this.tsv = value.tsv;
        this.creatorId = value.creatorId;
        this.customerId = value.customerId;
        this.title = value.title;
        this.documentType = value.documentType;
        this.deadline = value.deadline;
        this.price = value.price;
        this.numPages = value.numPages;
        this.numSources = value.numSources;
        this.details = value.details;
        this.adminNotes = value.adminNotes;
        this.state = value.state;
        this.writerId = value.writerId;
    }

    public JQUaOrders(
        Long             id,
        Boolean          deleted,
        Timestamp        insertedAt,
        Timestamp        updatedAt,
        Object           tsv,
        Long             creatorId,
        Long             customerId,
        String           title,
        JQUaDocumentType documentType,
        Timestamp        deadline,
        Integer          price,
        Integer          numPages,
        Integer          numSources,
        String           details,
        String           adminNotes,
        JQUaOrderState   state,
        Long             writerId
    ) {
        this.id = id;
        this.deleted = deleted;
        this.insertedAt = insertedAt;
        this.updatedAt = updatedAt;
        this.tsv = tsv;
        this.creatorId = creatorId;
        this.customerId = customerId;
        this.title = title;
        this.documentType = documentType;
        this.deadline = deadline;
        this.price = price;
        this.numPages = numPages;
        this.numSources = numSources;
        this.details = details;
        this.adminNotes = adminNotes;
        this.state = state;
        this.writerId = writerId;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getDeleted() {
        return this.deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Timestamp getInsertedAt() {
        return this.insertedAt;
    }

    public void setInsertedAt(Timestamp insertedAt) {
        this.insertedAt = insertedAt;
    }

    public Timestamp getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Object getTsv() {
        return this.tsv;
    }

    public void setTsv(Object tsv) {
        this.tsv = tsv;
    }

    public Long getCreatorId() {
        return this.creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public Long getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public JQUaDocumentType getDocumentType() {
        return this.documentType;
    }

    public void setDocumentType(JQUaDocumentType documentType) {
        this.documentType = documentType;
    }

    public Timestamp getDeadline() {
        return this.deadline;
    }

    public void setDeadline(Timestamp deadline) {
        this.deadline = deadline;
    }

    public Integer getPrice() {
        return this.price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getNumPages() {
        return this.numPages;
    }

    public void setNumPages(Integer numPages) {
        this.numPages = numPages;
    }

    public Integer getNumSources() {
        return this.numSources;
    }

    public void setNumSources(Integer numSources) {
        this.numSources = numSources;
    }

    public String getDetails() {
        return this.details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getAdminNotes() {
        return this.adminNotes;
    }

    public void setAdminNotes(String adminNotes) {
        this.adminNotes = adminNotes;
    }

    public JQUaOrderState getState() {
        return this.state;
    }

    public void setState(JQUaOrderState state) {
        this.state = state;
    }

    public Long getWriterId() {
        return this.writerId;
    }

    public void setWriterId(Long writerId) {
        this.writerId = writerId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("JQUaOrders (");

        sb.append(id);
        sb.append(", ").append(deleted);
        sb.append(", ").append(insertedAt);
        sb.append(", ").append(updatedAt);
        sb.append(", ").append(tsv);
        sb.append(", ").append(creatorId);
        sb.append(", ").append(customerId);
        sb.append(", ").append(title);
        sb.append(", ").append(documentType);
        sb.append(", ").append(deadline);
        sb.append(", ").append(price);
        sb.append(", ").append(numPages);
        sb.append(", ").append(numSources);
        sb.append(", ").append(details);
        sb.append(", ").append(adminNotes);
        sb.append(", ").append(state);
        sb.append(", ").append(writerId);

        sb.append(")");
        return sb.toString();
    }
}
