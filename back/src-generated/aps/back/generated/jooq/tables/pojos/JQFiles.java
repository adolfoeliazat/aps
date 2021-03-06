/*
 * This file is generated by jOOQ.
*/
package aps.back.generated.jooq.tables.pojos;


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
public class JQFiles implements Serializable {

    private static final long serialVersionUID = -1654145606;

    private Long      id;
    private Boolean   deleted;
    private Timestamp insertedAt;
    private Timestamp updatedAt;
    private Long      creatorId;
    private Object    tsv;
    private byte[]    content;
    private String    name;
    private String    title;
    private String    mime;
    private Integer   sizeBytes;
    private String    details;
    private String    adminNotes;
    private String    sha1;

    public JQFiles() {}

    public JQFiles(JQFiles value) {
        this.id = value.id;
        this.deleted = value.deleted;
        this.insertedAt = value.insertedAt;
        this.updatedAt = value.updatedAt;
        this.creatorId = value.creatorId;
        this.tsv = value.tsv;
        this.content = value.content;
        this.name = value.name;
        this.title = value.title;
        this.mime = value.mime;
        this.sizeBytes = value.sizeBytes;
        this.details = value.details;
        this.adminNotes = value.adminNotes;
        this.sha1 = value.sha1;
    }

    public JQFiles(
        Long      id,
        Boolean   deleted,
        Timestamp insertedAt,
        Timestamp updatedAt,
        Long      creatorId,
        Object    tsv,
        byte[]    content,
        String    name,
        String    title,
        String    mime,
        Integer   sizeBytes,
        String    details,
        String    adminNotes,
        String    sha1
    ) {
        this.id = id;
        this.deleted = deleted;
        this.insertedAt = insertedAt;
        this.updatedAt = updatedAt;
        this.creatorId = creatorId;
        this.tsv = tsv;
        this.content = content;
        this.name = name;
        this.title = title;
        this.mime = mime;
        this.sizeBytes = sizeBytes;
        this.details = details;
        this.adminNotes = adminNotes;
        this.sha1 = sha1;
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

    public Object getTsv() {
        return this.tsv;
    }

    public void setTsv(Object tsv) {
        this.tsv = tsv;
    }

    public byte[] getContent() {
        return this.content;
    }

    public void setContent(byte... content) {
        this.content = content;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMime() {
        return this.mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public Integer getSizeBytes() {
        return this.sizeBytes;
    }

    public void setSizeBytes(Integer sizeBytes) {
        this.sizeBytes = sizeBytes;
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

    public String getSha1() {
        return this.sha1;
    }

    public void setSha1(String sha1) {
        this.sha1 = sha1;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("JQFiles (");

        sb.append(id);
        sb.append(", ").append(deleted);
        sb.append(", ").append(insertedAt);
        sb.append(", ").append(updatedAt);
        sb.append(", ").append(creatorId);
        sb.append(", ").append(tsv);
        sb.append(", ").append("[binary...]");
        sb.append(", ").append(name);
        sb.append(", ").append(title);
        sb.append(", ").append(mime);
        sb.append(", ").append(sizeBytes);
        sb.append(", ").append(details);
        sb.append(", ").append(adminNotes);
        sb.append(", ").append(sha1);

        sb.append(")");
        return sb.toString();
    }
}
