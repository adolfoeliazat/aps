/**
 * This class is generated by jOOQ
 */
package aps.back.generated.jooq.tables.records;


import aps.back.generated.jooq.tables.Users;

import java.sql.Timestamp;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record20;
import org.jooq.Row20;
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
public class UsersRecord extends UpdatableRecordImpl<UsersRecord> implements Record20<Long, Boolean, Timestamp, Timestamp, Timestamp, Object, String, String, String, String, String, String, String, Long, String, String, String, String, String, String> {

    private static final long serialVersionUID = -1099170077;

    /**
     * Setter for <code>public.users.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.users.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.users.deleted</code>.
     */
    public void setDeleted(Boolean value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.users.deleted</code>.
     */
    public Boolean getDeleted() {
        return (Boolean) get(1);
    }

    /**
     * Setter for <code>public.users.inserted_at</code>.
     */
    public void setInsertedAt(Timestamp value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.users.inserted_at</code>.
     */
    public Timestamp getInsertedAt() {
        return (Timestamp) get(2);
    }

    /**
     * Setter for <code>public.users.updated_at</code>.
     */
    public void setUpdatedAt(Timestamp value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.users.updated_at</code>.
     */
    public Timestamp getUpdatedAt() {
        return (Timestamp) get(3);
    }

    /**
     * Setter for <code>public.users.profile_updated_at</code>.
     */
    public void setProfileUpdatedAt(Timestamp value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.users.profile_updated_at</code>.
     */
    public Timestamp getProfileUpdatedAt() {
        return (Timestamp) get(4);
    }

    /**
     * Setter for <code>public.users.tsv</code>.
     */
    public void setTsv(Object value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.users.tsv</code>.
     */
    public Object getTsv() {
        return (Object) get(5);
    }

    /**
     * Setter for <code>public.users.kind</code>.
     */
    public void setKind(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>public.users.kind</code>.
     */
    public String getKind() {
        return (String) get(6);
    }

    /**
     * Setter for <code>public.users.lang</code>.
     */
    public void setLang(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>public.users.lang</code>.
     */
    public String getLang() {
        return (String) get(7);
    }

    /**
     * Setter for <code>public.users.email</code>.
     */
    public void setEmail(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>public.users.email</code>.
     */
    public String getEmail() {
        return (String) get(8);
    }

    /**
     * Setter for <code>public.users.password_hash</code>.
     */
    public void setPasswordHash(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>public.users.password_hash</code>.
     */
    public String getPasswordHash() {
        return (String) get(9);
    }

    /**
     * Setter for <code>public.users.state</code>.
     */
    public void setState(String value) {
        set(10, value);
    }

    /**
     * Getter for <code>public.users.state</code>.
     */
    public String getState() {
        return (String) get(10);
    }

    /**
     * Setter for <code>public.users.profile_rejection_reason</code>.
     */
    public void setProfileRejectionReason(String value) {
        set(11, value);
    }

    /**
     * Getter for <code>public.users.profile_rejection_reason</code>.
     */
    public String getProfileRejectionReason() {
        return (String) get(11);
    }

    /**
     * Setter for <code>public.users.ban_reason</code>.
     */
    public void setBanReason(String value) {
        set(12, value);
    }

    /**
     * Getter for <code>public.users.ban_reason</code>.
     */
    public String getBanReason() {
        return (String) get(12);
    }

    /**
     * Setter for <code>public.users.assigned_to</code>.
     */
    public void setAssignedTo(Long value) {
        set(13, value);
    }

    /**
     * Getter for <code>public.users.assigned_to</code>.
     */
    public Long getAssignedTo() {
        return (Long) get(13);
    }

    /**
     * Setter for <code>public.users.admin_notes</code>.
     */
    public void setAdminNotes(String value) {
        set(14, value);
    }

    /**
     * Getter for <code>public.users.admin_notes</code>.
     */
    public String getAdminNotes() {
        return (String) get(14);
    }

    /**
     * Setter for <code>public.users.first_name</code>.
     */
    public void setFirstName(String value) {
        set(15, value);
    }

    /**
     * Getter for <code>public.users.first_name</code>.
     */
    public String getFirstName() {
        return (String) get(15);
    }

    /**
     * Setter for <code>public.users.last_name</code>.
     */
    public void setLastName(String value) {
        set(16, value);
    }

    /**
     * Getter for <code>public.users.last_name</code>.
     */
    public String getLastName() {
        return (String) get(16);
    }

    /**
     * Setter for <code>public.users.phone</code>.
     */
    public void setPhone(String value) {
        set(17, value);
    }

    /**
     * Getter for <code>public.users.phone</code>.
     */
    public String getPhone() {
        return (String) get(17);
    }

    /**
     * Setter for <code>public.users.compact_phone</code>.
     */
    public void setCompactPhone(String value) {
        set(18, value);
    }

    /**
     * Getter for <code>public.users.compact_phone</code>.
     */
    public String getCompactPhone() {
        return (String) get(18);
    }

    /**
     * Setter for <code>public.users.about_me</code>.
     */
    public void setAboutMe(String value) {
        set(19, value);
    }

    /**
     * Getter for <code>public.users.about_me</code>.
     */
    public String getAboutMe() {
        return (String) get(19);
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
    // Record20 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row20<Long, Boolean, Timestamp, Timestamp, Timestamp, Object, String, String, String, String, String, String, String, Long, String, String, String, String, String, String> fieldsRow() {
        return (Row20) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row20<Long, Boolean, Timestamp, Timestamp, Timestamp, Object, String, String, String, String, String, String, String, Long, String, String, String, String, String, String> valuesRow() {
        return (Row20) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field1() {
        return Users.USERS.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Boolean> field2() {
        return Users.USERS.DELETED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field3() {
        return Users.USERS.INSERTED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field4() {
        return Users.USERS.UPDATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field5() {
        return Users.USERS.PROFILE_UPDATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Object> field6() {
        return Users.USERS.TSV;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field7() {
        return Users.USERS.KIND;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field8() {
        return Users.USERS.LANG;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field9() {
        return Users.USERS.EMAIL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field10() {
        return Users.USERS.PASSWORD_HASH;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field11() {
        return Users.USERS.STATE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field12() {
        return Users.USERS.PROFILE_REJECTION_REASON;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field13() {
        return Users.USERS.BAN_REASON;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field14() {
        return Users.USERS.ASSIGNED_TO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field15() {
        return Users.USERS.ADMIN_NOTES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field16() {
        return Users.USERS.FIRST_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field17() {
        return Users.USERS.LAST_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field18() {
        return Users.USERS.PHONE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field19() {
        return Users.USERS.COMPACT_PHONE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field20() {
        return Users.USERS.ABOUT_ME;
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
    public Timestamp value5() {
        return getProfileUpdatedAt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object value6() {
        return getTsv();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value7() {
        return getKind();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value8() {
        return getLang();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value9() {
        return getEmail();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value10() {
        return getPasswordHash();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value11() {
        return getState();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value12() {
        return getProfileRejectionReason();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value13() {
        return getBanReason();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value14() {
        return getAssignedTo();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value15() {
        return getAdminNotes();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value16() {
        return getFirstName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value17() {
        return getLastName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value18() {
        return getPhone();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value19() {
        return getCompactPhone();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value20() {
        return getAboutMe();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsersRecord value1(Long value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsersRecord value2(Boolean value) {
        setDeleted(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsersRecord value3(Timestamp value) {
        setInsertedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsersRecord value4(Timestamp value) {
        setUpdatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsersRecord value5(Timestamp value) {
        setProfileUpdatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsersRecord value6(Object value) {
        setTsv(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsersRecord value7(String value) {
        setKind(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsersRecord value8(String value) {
        setLang(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsersRecord value9(String value) {
        setEmail(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsersRecord value10(String value) {
        setPasswordHash(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsersRecord value11(String value) {
        setState(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsersRecord value12(String value) {
        setProfileRejectionReason(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsersRecord value13(String value) {
        setBanReason(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsersRecord value14(Long value) {
        setAssignedTo(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsersRecord value15(String value) {
        setAdminNotes(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsersRecord value16(String value) {
        setFirstName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsersRecord value17(String value) {
        setLastName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsersRecord value18(String value) {
        setPhone(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsersRecord value19(String value) {
        setCompactPhone(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsersRecord value20(String value) {
        setAboutMe(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsersRecord values(Long value1, Boolean value2, Timestamp value3, Timestamp value4, Timestamp value5, Object value6, String value7, String value8, String value9, String value10, String value11, String value12, String value13, Long value14, String value15, String value16, String value17, String value18, String value19, String value20) {
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
        value20(value20);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached UsersRecord
     */
    public UsersRecord() {
        super(Users.USERS);
    }

    /**
     * Create a detached, initialised UsersRecord
     */
    public UsersRecord(Long id, Boolean deleted, Timestamp insertedAt, Timestamp updatedAt, Timestamp profileUpdatedAt, Object tsv, String kind, String lang, String email, String passwordHash, String state, String profileRejectionReason, String banReason, Long assignedTo, String adminNotes, String firstName, String lastName, String phone, String compactPhone, String aboutMe) {
        super(Users.USERS);

        set(0, id);
        set(1, deleted);
        set(2, insertedAt);
        set(3, updatedAt);
        set(4, profileUpdatedAt);
        set(5, tsv);
        set(6, kind);
        set(7, lang);
        set(8, email);
        set(9, passwordHash);
        set(10, state);
        set(11, profileRejectionReason);
        set(12, banReason);
        set(13, assignedTo);
        set(14, adminNotes);
        set(15, firstName);
        set(16, lastName);
        set(17, phone);
        set(18, compactPhone);
        set(19, aboutMe);
    }
}