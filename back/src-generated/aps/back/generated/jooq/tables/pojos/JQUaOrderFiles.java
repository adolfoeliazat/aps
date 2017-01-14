/*
 * This file is generated by jOOQ.
*/
package aps.back.generated.jooq.tables.pojos;


import aps.back.generated.jooq.enums.JQUserKind;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.annotation.Generated;


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
public class JQUaOrderFiles implements Serializable {

    private static final long serialVersionUID = 1637826192;

    private Long       id;
    private Boolean    deleted;
    private Timestamp  insertedAt;
    private Timestamp  updatedAt;
    private Long       creatorId;
    private Long       uaOrderId;
    private Long       uaOrderAreaId;
    private Long       fileId;
    private JQUserKind seenAsFrom;

    public JQUaOrderFiles() {}

    public JQUaOrderFiles(JQUaOrderFiles value) {
        this.id = value.id;
        this.deleted = value.deleted;
        this.insertedAt = value.insertedAt;
        this.updatedAt = value.updatedAt;
        this.creatorId = value.creatorId;
        this.uaOrderId = value.uaOrderId;
        this.uaOrderAreaId = value.uaOrderAreaId;
        this.fileId = value.fileId;
        this.seenAsFrom = value.seenAsFrom;
    }

    public JQUaOrderFiles(
        Long       id,
        Boolean    deleted,
        Timestamp  insertedAt,
        Timestamp  updatedAt,
        Long       creatorId,
        Long       uaOrderId,
        Long       uaOrderAreaId,
        Long       fileId,
        JQUserKind seenAsFrom
    ) {
        this.id = id;
        this.deleted = deleted;
        this.insertedAt = insertedAt;
        this.updatedAt = updatedAt;
        this.creatorId = creatorId;
        this.uaOrderId = uaOrderId;
        this.uaOrderAreaId = uaOrderAreaId;
        this.fileId = fileId;
        this.seenAsFrom = seenAsFrom;
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

    public Long getCreatorId() {
        return this.creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public Long getUaOrderId() {
        return this.uaOrderId;
    }

    public void setUaOrderId(Long uaOrderId) {
        this.uaOrderId = uaOrderId;
    }

    public Long getUaOrderAreaId() {
        return this.uaOrderAreaId;
    }

    public void setUaOrderAreaId(Long uaOrderAreaId) {
        this.uaOrderAreaId = uaOrderAreaId;
    }

    public Long getFileId() {
        return this.fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public JQUserKind getSeenAsFrom() {
        return this.seenAsFrom;
    }

    public void setSeenAsFrom(JQUserKind seenAsFrom) {
        this.seenAsFrom = seenAsFrom;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("JQUaOrderFiles (");

        sb.append(id);
        sb.append(", ").append(deleted);
        sb.append(", ").append(insertedAt);
        sb.append(", ").append(updatedAt);
        sb.append(", ").append(creatorId);
        sb.append(", ").append(uaOrderId);
        sb.append(", ").append(uaOrderAreaId);
        sb.append(", ").append(fileId);
        sb.append(", ").append(seenAsFrom);

        sb.append(")");
        return sb.toString();
    }
}
