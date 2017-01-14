/**
 * This class is generated by jOOQ
 */
package aps.back.generated.jooq;


import aps.back.generated.jooq.tables.JQFileUserPermissions;
import aps.back.generated.jooq.tables.JQFiles;
import aps.back.generated.jooq.tables.JQKeyValueStore;
import aps.back.generated.jooq.tables.JQSupportThreadMessages;
import aps.back.generated.jooq.tables.JQSupportThreads;
import aps.back.generated.jooq.tables.JQUaOrderAreas;
import aps.back.generated.jooq.tables.JQUaOrderFiles;
import aps.back.generated.jooq.tables.JQUaOrders;
import aps.back.generated.jooq.tables.JQUserRoles;
import aps.back.generated.jooq.tables.JQUserTokens;
import aps.back.generated.jooq.tables.JQUsers;
import aps.back.generated.jooq.tables.records.JQFileUserPermissionsRecord;
import aps.back.generated.jooq.tables.records.JQFilesRecord;
import aps.back.generated.jooq.tables.records.JQKeyValueStoreRecord;
import aps.back.generated.jooq.tables.records.JQSupportThreadMessagesRecord;
import aps.back.generated.jooq.tables.records.JQSupportThreadsRecord;
import aps.back.generated.jooq.tables.records.JQUaOrderAreasRecord;
import aps.back.generated.jooq.tables.records.JQUaOrderFilesRecord;
import aps.back.generated.jooq.tables.records.JQUaOrdersRecord;
import aps.back.generated.jooq.tables.records.JQUserRolesRecord;
import aps.back.generated.jooq.tables.records.JQUserTokensRecord;
import aps.back.generated.jooq.tables.records.JQUsersRecord;

import javax.annotation.Generated;

import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.UniqueKey;
import org.jooq.impl.AbstractKeys;


/**
 * A class modelling foreign key relationships between tables of the <code>public</code> 
 * schema
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.8.4"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // IDENTITY definitions
    // -------------------------------------------------------------------------

    public static final Identity<JQFileUserPermissionsRecord, Long> IDENTITY_FILE_USER_PERMISSIONS = Identities0.IDENTITY_FILE_USER_PERMISSIONS;
    public static final Identity<JQFilesRecord, Long> IDENTITY_FILES = Identities0.IDENTITY_FILES;
    public static final Identity<JQKeyValueStoreRecord, Long> IDENTITY_KEY_VALUE_STORE = Identities0.IDENTITY_KEY_VALUE_STORE;
    public static final Identity<JQSupportThreadMessagesRecord, Long> IDENTITY_SUPPORT_THREAD_MESSAGES = Identities0.IDENTITY_SUPPORT_THREAD_MESSAGES;
    public static final Identity<JQSupportThreadsRecord, Long> IDENTITY_SUPPORT_THREADS = Identities0.IDENTITY_SUPPORT_THREADS;
    public static final Identity<JQUaOrderAreasRecord, Long> IDENTITY_UA_ORDER_AREAS = Identities0.IDENTITY_UA_ORDER_AREAS;
    public static final Identity<JQUaOrderFilesRecord, Long> IDENTITY_UA_ORDER_FILES = Identities0.IDENTITY_UA_ORDER_FILES;
    public static final Identity<JQUaOrdersRecord, Long> IDENTITY_UA_ORDERS = Identities0.IDENTITY_UA_ORDERS;
    public static final Identity<JQUserRolesRecord, Long> IDENTITY_USER_ROLES = Identities0.IDENTITY_USER_ROLES;
    public static final Identity<JQUserTokensRecord, Long> IDENTITY_USER_TOKENS = Identities0.IDENTITY_USER_TOKENS;
    public static final Identity<JQUsersRecord, Long> IDENTITY_USERS = Identities0.IDENTITY_USERS;

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<JQFileUserPermissionsRecord> FILE_USER_PERMISSIONS_PKEY = UniqueKeys0.FILE_USER_PERMISSIONS_PKEY;
    public static final UniqueKey<JQFilesRecord> FILES_PKEY = UniqueKeys0.FILES_PKEY;
    public static final UniqueKey<JQKeyValueStoreRecord> KEY_VALUE_STORE_PKEY = UniqueKeys0.KEY_VALUE_STORE_PKEY;
    public static final UniqueKey<JQKeyValueStoreRecord> KEY_VALUE_STORE_KEY_KEY = UniqueKeys0.KEY_VALUE_STORE_KEY_KEY;
    public static final UniqueKey<JQSupportThreadMessagesRecord> SUPPORT_THREAD_MESSAGES_PKEY = UniqueKeys0.SUPPORT_THREAD_MESSAGES_PKEY;
    public static final UniqueKey<JQSupportThreadsRecord> SUPPORT_THREADS_PKEY = UniqueKeys0.SUPPORT_THREADS_PKEY;
    public static final UniqueKey<JQUaOrderAreasRecord> UA_ORDER_AREAS_PKEY = UniqueKeys0.UA_ORDER_AREAS_PKEY;
    public static final UniqueKey<JQUaOrderAreasRecord> UA_ORDER_AREAS_NAME_KEY = UniqueKeys0.UA_ORDER_AREAS_NAME_KEY;
    public static final UniqueKey<JQUaOrderFilesRecord> UA_ORDER_FILES_PKEY = UniqueKeys0.UA_ORDER_FILES_PKEY;
    public static final UniqueKey<JQUaOrdersRecord> UA_ORDERS_PKEY = UniqueKeys0.UA_ORDERS_PKEY;
    public static final UniqueKey<JQUserRolesRecord> USER_ROLES_PKEY = UniqueKeys0.USER_ROLES_PKEY;
    public static final UniqueKey<JQUserRolesRecord> UNIQUE_USER_ID_ROLE = UniqueKeys0.UNIQUE_USER_ID_ROLE;
    public static final UniqueKey<JQUserTokensRecord> USER_TOKENS_PKEY = UniqueKeys0.USER_TOKENS_PKEY;
    public static final UniqueKey<JQUsersRecord> USERS_PKEY = UniqueKeys0.USERS_PKEY;
    public static final UniqueKey<JQUsersRecord> USERS_EMAIL_KEY = UniqueKeys0.USERS_EMAIL_KEY;

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------

    public static final ForeignKey<JQFileUserPermissionsRecord, JQFilesRecord> FILE_USER_PERMISSIONS__FILE_USER_PERMISSIONS_FILE_ID_FKEY = ForeignKeys0.FILE_USER_PERMISSIONS__FILE_USER_PERMISSIONS_FILE_ID_FKEY;
    public static final ForeignKey<JQFileUserPermissionsRecord, JQUsersRecord> FILE_USER_PERMISSIONS__FILE_USER_PERMISSIONS_USER_ID_FKEY = ForeignKeys0.FILE_USER_PERMISSIONS__FILE_USER_PERMISSIONS_USER_ID_FKEY;
    public static final ForeignKey<JQFilesRecord, JQUsersRecord> FILES__FILES_CREATOR_ID_FKEY = ForeignKeys0.FILES__FILES_CREATOR_ID_FKEY;
    public static final ForeignKey<JQSupportThreadMessagesRecord, JQSupportThreadsRecord> SUPPORT_THREAD_MESSAGES__SUPPORT_THREAD_MESSAGES_THREAD_ID_FKEY = ForeignKeys0.SUPPORT_THREAD_MESSAGES__SUPPORT_THREAD_MESSAGES_THREAD_ID_FKEY;
    public static final ForeignKey<JQSupportThreadMessagesRecord, JQUsersRecord> SUPPORT_THREAD_MESSAGES__SUPPORT_THREAD_MESSAGES_SENDER_ID_FKEY = ForeignKeys0.SUPPORT_THREAD_MESSAGES__SUPPORT_THREAD_MESSAGES_SENDER_ID_FKEY;
    public static final ForeignKey<JQSupportThreadMessagesRecord, JQUsersRecord> SUPPORT_THREAD_MESSAGES__SUPPORT_THREAD_MESSAGES_RECIPIENT_ID_FKEY = ForeignKeys0.SUPPORT_THREAD_MESSAGES__SUPPORT_THREAD_MESSAGES_RECIPIENT_ID_FKEY;
    public static final ForeignKey<JQSupportThreadsRecord, JQUsersRecord> SUPPORT_THREADS__SUPPORT_THREADS_SUPPORTEE_ID_FKEY = ForeignKeys0.SUPPORT_THREADS__SUPPORT_THREADS_SUPPORTEE_ID_FKEY;
    public static final ForeignKey<JQSupportThreadsRecord, JQUsersRecord> SUPPORT_THREADS__SUPPORT_THREADS_SUPPORTER_ID_FKEY = ForeignKeys0.SUPPORT_THREADS__SUPPORT_THREADS_SUPPORTER_ID_FKEY;
    public static final ForeignKey<JQUaOrderAreasRecord, JQUaOrdersRecord> UA_ORDER_AREAS__UA_ORDER_AREAS_UA_ORDER_ID_FKEY = ForeignKeys0.UA_ORDER_AREAS__UA_ORDER_AREAS_UA_ORDER_ID_FKEY;
    public static final ForeignKey<JQUaOrderFilesRecord, JQUsersRecord> UA_ORDER_FILES__UA_ORDER_FILES_CREATOR_ID_FKEY = ForeignKeys0.UA_ORDER_FILES__UA_ORDER_FILES_CREATOR_ID_FKEY;
    public static final ForeignKey<JQUaOrderFilesRecord, JQUaOrdersRecord> UA_ORDER_FILES__UA_ORDER_FILES_UA_ORDER_ID_FKEY = ForeignKeys0.UA_ORDER_FILES__UA_ORDER_FILES_UA_ORDER_ID_FKEY;
    public static final ForeignKey<JQUaOrderFilesRecord, JQUaOrderAreasRecord> UA_ORDER_FILES__UA_ORDER_FILES_UA_ORDER_AREA_ID_FKEY = ForeignKeys0.UA_ORDER_FILES__UA_ORDER_FILES_UA_ORDER_AREA_ID_FKEY;
    public static final ForeignKey<JQUaOrderFilesRecord, JQFilesRecord> UA_ORDER_FILES__UA_ORDER_FILES_FILE_ID_FKEY = ForeignKeys0.UA_ORDER_FILES__UA_ORDER_FILES_FILE_ID_FKEY;
    public static final ForeignKey<JQUaOrdersRecord, JQUsersRecord> UA_ORDERS__UA_ORDERS_CREATOR_ID_FKEY = ForeignKeys0.UA_ORDERS__UA_ORDERS_CREATOR_ID_FKEY;
    public static final ForeignKey<JQUaOrdersRecord, JQUsersRecord> UA_ORDERS__UA_ORDERS_CUSTOMER_ID_FKEY = ForeignKeys0.UA_ORDERS__UA_ORDERS_CUSTOMER_ID_FKEY;
    public static final ForeignKey<JQUaOrdersRecord, JQUsersRecord> UA_ORDERS__UA_ORDERS_WRITER_ID_FKEY = ForeignKeys0.UA_ORDERS__UA_ORDERS_WRITER_ID_FKEY;
    public static final ForeignKey<JQUserRolesRecord, JQUsersRecord> USER_ROLES__USER_ROLES_USER_ID_FKEY = ForeignKeys0.USER_ROLES__USER_ROLES_USER_ID_FKEY;
    public static final ForeignKey<JQUserTokensRecord, JQUsersRecord> USER_TOKENS__USER_TOKENS_USER_ID_FKEY = ForeignKeys0.USER_TOKENS__USER_TOKENS_USER_ID_FKEY;
    public static final ForeignKey<JQUsersRecord, JQUsersRecord> USERS__USERS_ASSIGNED_TO_FKEY = ForeignKeys0.USERS__USERS_ASSIGNED_TO_FKEY;

    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class Identities0 extends AbstractKeys {
        public static Identity<JQFileUserPermissionsRecord, Long> IDENTITY_FILE_USER_PERMISSIONS = createIdentity(JQFileUserPermissions.FILE_USER_PERMISSIONS, JQFileUserPermissions.FILE_USER_PERMISSIONS.FILE_ID);
        public static Identity<JQFilesRecord, Long> IDENTITY_FILES = createIdentity(JQFiles.FILES, JQFiles.FILES.ID);
        public static Identity<JQKeyValueStoreRecord, Long> IDENTITY_KEY_VALUE_STORE = createIdentity(JQKeyValueStore.KEY_VALUE_STORE, JQKeyValueStore.KEY_VALUE_STORE.ID);
        public static Identity<JQSupportThreadMessagesRecord, Long> IDENTITY_SUPPORT_THREAD_MESSAGES = createIdentity(JQSupportThreadMessages.SUPPORT_THREAD_MESSAGES, JQSupportThreadMessages.SUPPORT_THREAD_MESSAGES.ID);
        public static Identity<JQSupportThreadsRecord, Long> IDENTITY_SUPPORT_THREADS = createIdentity(JQSupportThreads.SUPPORT_THREADS, JQSupportThreads.SUPPORT_THREADS.ID);
        public static Identity<JQUaOrderAreasRecord, Long> IDENTITY_UA_ORDER_AREAS = createIdentity(JQUaOrderAreas.UA_ORDER_AREAS, JQUaOrderAreas.UA_ORDER_AREAS.ID);
        public static Identity<JQUaOrderFilesRecord, Long> IDENTITY_UA_ORDER_FILES = createIdentity(JQUaOrderFiles.UA_ORDER_FILES, JQUaOrderFiles.UA_ORDER_FILES.ID);
        public static Identity<JQUaOrdersRecord, Long> IDENTITY_UA_ORDERS = createIdentity(JQUaOrders.UA_ORDERS, JQUaOrders.UA_ORDERS.ID);
        public static Identity<JQUserRolesRecord, Long> IDENTITY_USER_ROLES = createIdentity(JQUserRoles.USER_ROLES, JQUserRoles.USER_ROLES.ID);
        public static Identity<JQUserTokensRecord, Long> IDENTITY_USER_TOKENS = createIdentity(JQUserTokens.USER_TOKENS, JQUserTokens.USER_TOKENS.ID);
        public static Identity<JQUsersRecord, Long> IDENTITY_USERS = createIdentity(JQUsers.USERS, JQUsers.USERS.ID);
    }

    private static class UniqueKeys0 extends AbstractKeys {
        public static final UniqueKey<JQFileUserPermissionsRecord> FILE_USER_PERMISSIONS_PKEY = createUniqueKey(JQFileUserPermissions.FILE_USER_PERMISSIONS, "file_user_permissions_pkey", JQFileUserPermissions.FILE_USER_PERMISSIONS.FILE_ID, JQFileUserPermissions.FILE_USER_PERMISSIONS.USER_ID);
        public static final UniqueKey<JQFilesRecord> FILES_PKEY = createUniqueKey(JQFiles.FILES, "files_pkey", JQFiles.FILES.ID);
        public static final UniqueKey<JQKeyValueStoreRecord> KEY_VALUE_STORE_PKEY = createUniqueKey(JQKeyValueStore.KEY_VALUE_STORE, "key_value_store_pkey", JQKeyValueStore.KEY_VALUE_STORE.ID);
        public static final UniqueKey<JQKeyValueStoreRecord> KEY_VALUE_STORE_KEY_KEY = createUniqueKey(JQKeyValueStore.KEY_VALUE_STORE, "key_value_store_key_key", JQKeyValueStore.KEY_VALUE_STORE.KEY);
        public static final UniqueKey<JQSupportThreadMessagesRecord> SUPPORT_THREAD_MESSAGES_PKEY = createUniqueKey(JQSupportThreadMessages.SUPPORT_THREAD_MESSAGES, "support_thread_messages_pkey", JQSupportThreadMessages.SUPPORT_THREAD_MESSAGES.ID);
        public static final UniqueKey<JQSupportThreadsRecord> SUPPORT_THREADS_PKEY = createUniqueKey(JQSupportThreads.SUPPORT_THREADS, "support_threads_pkey", JQSupportThreads.SUPPORT_THREADS.ID);
        public static final UniqueKey<JQUaOrderAreasRecord> UA_ORDER_AREAS_PKEY = createUniqueKey(JQUaOrderAreas.UA_ORDER_AREAS, "ua_order_areas_pkey", JQUaOrderAreas.UA_ORDER_AREAS.ID);
        public static final UniqueKey<JQUaOrderAreasRecord> UA_ORDER_AREAS_NAME_KEY = createUniqueKey(JQUaOrderAreas.UA_ORDER_AREAS, "ua_order_areas_name_key", JQUaOrderAreas.UA_ORDER_AREAS.NAME);
        public static final UniqueKey<JQUaOrderFilesRecord> UA_ORDER_FILES_PKEY = createUniqueKey(JQUaOrderFiles.UA_ORDER_FILES, "ua_order_files_pkey", JQUaOrderFiles.UA_ORDER_FILES.ID);
        public static final UniqueKey<JQUaOrdersRecord> UA_ORDERS_PKEY = createUniqueKey(JQUaOrders.UA_ORDERS, "ua_orders_pkey", JQUaOrders.UA_ORDERS.ID);
        public static final UniqueKey<JQUserRolesRecord> USER_ROLES_PKEY = createUniqueKey(JQUserRoles.USER_ROLES, "user_roles_pkey", JQUserRoles.USER_ROLES.ID);
        public static final UniqueKey<JQUserRolesRecord> UNIQUE_USER_ID_ROLE = createUniqueKey(JQUserRoles.USER_ROLES, "unique_user_id_role", JQUserRoles.USER_ROLES.USER_ID, JQUserRoles.USER_ROLES.ROLE);
        public static final UniqueKey<JQUserTokensRecord> USER_TOKENS_PKEY = createUniqueKey(JQUserTokens.USER_TOKENS, "user_tokens_pkey", JQUserTokens.USER_TOKENS.ID);
        public static final UniqueKey<JQUsersRecord> USERS_PKEY = createUniqueKey(JQUsers.USERS, "users_pkey", JQUsers.USERS.ID);
        public static final UniqueKey<JQUsersRecord> USERS_EMAIL_KEY = createUniqueKey(JQUsers.USERS, "users_email_key", JQUsers.USERS.EMAIL);
    }

    private static class ForeignKeys0 extends AbstractKeys {
        public static final ForeignKey<JQFileUserPermissionsRecord, JQFilesRecord> FILE_USER_PERMISSIONS__FILE_USER_PERMISSIONS_FILE_ID_FKEY = createForeignKey(aps.back.generated.jooq.Keys.FILES_PKEY, JQFileUserPermissions.FILE_USER_PERMISSIONS, "file_user_permissions__file_user_permissions_file_id_fkey", JQFileUserPermissions.FILE_USER_PERMISSIONS.FILE_ID);
        public static final ForeignKey<JQFileUserPermissionsRecord, JQUsersRecord> FILE_USER_PERMISSIONS__FILE_USER_PERMISSIONS_USER_ID_FKEY = createForeignKey(aps.back.generated.jooq.Keys.USERS_PKEY, JQFileUserPermissions.FILE_USER_PERMISSIONS, "file_user_permissions__file_user_permissions_user_id_fkey", JQFileUserPermissions.FILE_USER_PERMISSIONS.USER_ID);
        public static final ForeignKey<JQFilesRecord, JQUsersRecord> FILES__FILES_CREATOR_ID_FKEY = createForeignKey(aps.back.generated.jooq.Keys.USERS_PKEY, JQFiles.FILES, "files__files_creator_id_fkey", JQFiles.FILES.CREATOR_ID);
        public static final ForeignKey<JQSupportThreadMessagesRecord, JQSupportThreadsRecord> SUPPORT_THREAD_MESSAGES__SUPPORT_THREAD_MESSAGES_THREAD_ID_FKEY = createForeignKey(aps.back.generated.jooq.Keys.SUPPORT_THREADS_PKEY, JQSupportThreadMessages.SUPPORT_THREAD_MESSAGES, "support_thread_messages__support_thread_messages_thread_id_fkey", JQSupportThreadMessages.SUPPORT_THREAD_MESSAGES.THREAD_ID);
        public static final ForeignKey<JQSupportThreadMessagesRecord, JQUsersRecord> SUPPORT_THREAD_MESSAGES__SUPPORT_THREAD_MESSAGES_SENDER_ID_FKEY = createForeignKey(aps.back.generated.jooq.Keys.USERS_PKEY, JQSupportThreadMessages.SUPPORT_THREAD_MESSAGES, "support_thread_messages__support_thread_messages_sender_id_fkey", JQSupportThreadMessages.SUPPORT_THREAD_MESSAGES.SENDER_ID);
        public static final ForeignKey<JQSupportThreadMessagesRecord, JQUsersRecord> SUPPORT_THREAD_MESSAGES__SUPPORT_THREAD_MESSAGES_RECIPIENT_ID_FKEY = createForeignKey(aps.back.generated.jooq.Keys.USERS_PKEY, JQSupportThreadMessages.SUPPORT_THREAD_MESSAGES, "support_thread_messages__support_thread_messages_recipient_id_fkey", JQSupportThreadMessages.SUPPORT_THREAD_MESSAGES.RECIPIENT_ID);
        public static final ForeignKey<JQSupportThreadsRecord, JQUsersRecord> SUPPORT_THREADS__SUPPORT_THREADS_SUPPORTEE_ID_FKEY = createForeignKey(aps.back.generated.jooq.Keys.USERS_PKEY, JQSupportThreads.SUPPORT_THREADS, "support_threads__support_threads_supportee_id_fkey", JQSupportThreads.SUPPORT_THREADS.SUPPORTEE_ID);
        public static final ForeignKey<JQSupportThreadsRecord, JQUsersRecord> SUPPORT_THREADS__SUPPORT_THREADS_SUPPORTER_ID_FKEY = createForeignKey(aps.back.generated.jooq.Keys.USERS_PKEY, JQSupportThreads.SUPPORT_THREADS, "support_threads__support_threads_supporter_id_fkey", JQSupportThreads.SUPPORT_THREADS.SUPPORTER_ID);
        public static final ForeignKey<JQUaOrderAreasRecord, JQUaOrdersRecord> UA_ORDER_AREAS__UA_ORDER_AREAS_UA_ORDER_ID_FKEY = createForeignKey(aps.back.generated.jooq.Keys.UA_ORDERS_PKEY, JQUaOrderAreas.UA_ORDER_AREAS, "ua_order_areas__ua_order_areas_ua_order_id_fkey", JQUaOrderAreas.UA_ORDER_AREAS.UA_ORDER_ID);
        public static final ForeignKey<JQUaOrderFilesRecord, JQUsersRecord> UA_ORDER_FILES__UA_ORDER_FILES_CREATOR_ID_FKEY = createForeignKey(aps.back.generated.jooq.Keys.USERS_PKEY, JQUaOrderFiles.UA_ORDER_FILES, "ua_order_files__ua_order_files_creator_id_fkey", JQUaOrderFiles.UA_ORDER_FILES.CREATOR_ID);
        public static final ForeignKey<JQUaOrderFilesRecord, JQUaOrdersRecord> UA_ORDER_FILES__UA_ORDER_FILES_UA_ORDER_ID_FKEY = createForeignKey(aps.back.generated.jooq.Keys.UA_ORDERS_PKEY, JQUaOrderFiles.UA_ORDER_FILES, "ua_order_files__ua_order_files_ua_order_id_fkey", JQUaOrderFiles.UA_ORDER_FILES.UA_ORDER_ID);
        public static final ForeignKey<JQUaOrderFilesRecord, JQUaOrderAreasRecord> UA_ORDER_FILES__UA_ORDER_FILES_UA_ORDER_AREA_ID_FKEY = createForeignKey(aps.back.generated.jooq.Keys.UA_ORDER_AREAS_PKEY, JQUaOrderFiles.UA_ORDER_FILES, "ua_order_files__ua_order_files_ua_order_area_id_fkey", JQUaOrderFiles.UA_ORDER_FILES.UA_ORDER_AREA_ID);
        public static final ForeignKey<JQUaOrderFilesRecord, JQFilesRecord> UA_ORDER_FILES__UA_ORDER_FILES_FILE_ID_FKEY = createForeignKey(aps.back.generated.jooq.Keys.FILES_PKEY, JQUaOrderFiles.UA_ORDER_FILES, "ua_order_files__ua_order_files_file_id_fkey", JQUaOrderFiles.UA_ORDER_FILES.FILE_ID);
        public static final ForeignKey<JQUaOrdersRecord, JQUsersRecord> UA_ORDERS__UA_ORDERS_CREATOR_ID_FKEY = createForeignKey(aps.back.generated.jooq.Keys.USERS_PKEY, JQUaOrders.UA_ORDERS, "ua_orders__ua_orders_creator_id_fkey", JQUaOrders.UA_ORDERS.CREATOR_ID);
        public static final ForeignKey<JQUaOrdersRecord, JQUsersRecord> UA_ORDERS__UA_ORDERS_CUSTOMER_ID_FKEY = createForeignKey(aps.back.generated.jooq.Keys.USERS_PKEY, JQUaOrders.UA_ORDERS, "ua_orders__ua_orders_customer_id_fkey", JQUaOrders.UA_ORDERS.CUSTOMER_ID);
        public static final ForeignKey<JQUaOrdersRecord, JQUsersRecord> UA_ORDERS__UA_ORDERS_WRITER_ID_FKEY = createForeignKey(aps.back.generated.jooq.Keys.USERS_PKEY, JQUaOrders.UA_ORDERS, "ua_orders__ua_orders_writer_id_fkey", JQUaOrders.UA_ORDERS.WRITER_ID);
        public static final ForeignKey<JQUserRolesRecord, JQUsersRecord> USER_ROLES__USER_ROLES_USER_ID_FKEY = createForeignKey(aps.back.generated.jooq.Keys.USERS_PKEY, JQUserRoles.USER_ROLES, "user_roles__user_roles_user_id_fkey", JQUserRoles.USER_ROLES.USER_ID);
        public static final ForeignKey<JQUserTokensRecord, JQUsersRecord> USER_TOKENS__USER_TOKENS_USER_ID_FKEY = createForeignKey(aps.back.generated.jooq.Keys.USERS_PKEY, JQUserTokens.USER_TOKENS, "user_tokens__user_tokens_user_id_fkey", JQUserTokens.USER_TOKENS.USER_ID);
        public static final ForeignKey<JQUsersRecord, JQUsersRecord> USERS__USERS_ASSIGNED_TO_FKEY = createForeignKey(aps.back.generated.jooq.Keys.USERS_PKEY, JQUsers.USERS, "users__users_assigned_to_fkey", JQUsers.USERS.ASSIGNED_TO);
    }
}
