/*
 * This file is generated by jOOQ.
*/
package aps.back.generated.jooq.tables.pojos;


import com.fasterxml.jackson.databind.JsonNode;

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
public class JQSupportThreadMessages implements Serializable {

    private static final long serialVersionUID = -599942009;

    private Long      id;
    private Boolean   deleted;
    private Timestamp insertedAt;
    private Timestamp updatedAt;
    private String    tslang;
    private Long      threadId;
    private Long      senderId;
    private Long      recipientId;
    private String    message;
    private JsonNode  data;

    public JQSupportThreadMessages() {}

    public JQSupportThreadMessages(JQSupportThreadMessages value) {
        this.id = value.id;
        this.deleted = value.deleted;
        this.insertedAt = value.insertedAt;
        this.updatedAt = value.updatedAt;
        this.tslang = value.tslang;
        this.threadId = value.threadId;
        this.senderId = value.senderId;
        this.recipientId = value.recipientId;
        this.message = value.message;
        this.data = value.data;
    }

    public JQSupportThreadMessages(
        Long      id,
        Boolean   deleted,
        Timestamp insertedAt,
        Timestamp updatedAt,
        String    tslang,
        Long      threadId,
        Long      senderId,
        Long      recipientId,
        String    message,
        JsonNode  data
    ) {
        this.id = id;
        this.deleted = deleted;
        this.insertedAt = insertedAt;
        this.updatedAt = updatedAt;
        this.tslang = tslang;
        this.threadId = threadId;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.message = message;
        this.data = data;
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

    public String getTslang() {
        return this.tslang;
    }

    public void setTslang(String tslang) {
        this.tslang = tslang;
    }

    public Long getThreadId() {
        return this.threadId;
    }

    public void setThreadId(Long threadId) {
        this.threadId = threadId;
    }

    public Long getSenderId() {
        return this.senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getRecipientId() {
        return this.recipientId;
    }

    public void setRecipientId(Long recipientId) {
        this.recipientId = recipientId;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public JsonNode getData() {
        return this.data;
    }

    public void setData(JsonNode data) {
        this.data = data;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("JQSupportThreadMessages (");

        sb.append(id);
        sb.append(", ").append(deleted);
        sb.append(", ").append(insertedAt);
        sb.append(", ").append(updatedAt);
        sb.append(", ").append(tslang);
        sb.append(", ").append(threadId);
        sb.append(", ").append(senderId);
        sb.append(", ").append(recipientId);
        sb.append(", ").append(message);
        sb.append(", ").append(data);

        sb.append(")");
        return sb.toString();
    }
}
