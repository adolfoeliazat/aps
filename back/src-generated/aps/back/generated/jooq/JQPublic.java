/*
 * This file is generated by jOOQ.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Catalog;
import org.jooq.Sequence;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;


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
public class JQPublic extends SchemaImpl {

    private static final long serialVersionUID = 438877114;

    /**
     * The reference instance of <code>public</code>
     */
    public static final JQPublic PUBLIC = new JQPublic();

    /**
     * The table <code>public.file_user_permissions</code>.
     */
    public final JQFileUserPermissions FILE_USER_PERMISSIONS = aps.back.generated.jooq.tables.JQFileUserPermissions.FILE_USER_PERMISSIONS;

    /**
     * The table <code>public.files</code>.
     */
    public final JQFiles FILES = aps.back.generated.jooq.tables.JQFiles.FILES;

    /**
     * The table <code>public.key_value_store</code>.
     */
    public final JQKeyValueStore KEY_VALUE_STORE = aps.back.generated.jooq.tables.JQKeyValueStore.KEY_VALUE_STORE;

    /**
     * The table <code>public.support_thread_messages</code>.
     */
    public final JQSupportThreadMessages SUPPORT_THREAD_MESSAGES = aps.back.generated.jooq.tables.JQSupportThreadMessages.SUPPORT_THREAD_MESSAGES;

    /**
     * The table <code>public.support_threads</code>.
     */
    public final JQSupportThreads SUPPORT_THREADS = aps.back.generated.jooq.tables.JQSupportThreads.SUPPORT_THREADS;

    /**
     * The table <code>public.ua_order_areas</code>.
     */
    public final JQUaOrderAreas UA_ORDER_AREAS = aps.back.generated.jooq.tables.JQUaOrderAreas.UA_ORDER_AREAS;

    /**
     * The table <code>public.ua_order_files</code>.
     */
    public final JQUaOrderFiles UA_ORDER_FILES = aps.back.generated.jooq.tables.JQUaOrderFiles.UA_ORDER_FILES;

    /**
     * The table <code>public.ua_orders</code>.
     */
    public final JQUaOrders UA_ORDERS = aps.back.generated.jooq.tables.JQUaOrders.UA_ORDERS;

    /**
     * The table <code>public.user_roles</code>.
     */
    public final JQUserRoles USER_ROLES = aps.back.generated.jooq.tables.JQUserRoles.USER_ROLES;

    /**
     * The table <code>public.user_tokens</code>.
     */
    public final JQUserTokens USER_TOKENS = aps.back.generated.jooq.tables.JQUserTokens.USER_TOKENS;

    /**
     * The table <code>public.users</code>.
     */
    public final JQUsers USERS = aps.back.generated.jooq.tables.JQUsers.USERS;

    /**
     * No further instances allowed
     */
    private JQPublic() {
        super("public", null);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Catalog getCatalog() {
        return JQDefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Sequence<?>> getSequences() {
        List result = new ArrayList();
        result.addAll(getSequences0());
        return result;
    }

    private final List<Sequence<?>> getSequences0() {
        return Arrays.<Sequence<?>>asList(
            Sequences.FILE_USER_PERMISSIONS_FILE_ID_SEQ,
            Sequences.FILE_USER_PERMISSIONS_USER_ID_SEQ,
            Sequences.FILES_ID_SEQ,
            Sequences.KEY_VALUE_STORE_ID_SEQ,
            Sequences.SUPPORT_THREAD_MESSAGES_ID_SEQ,
            Sequences.SUPPORT_THREADS_ID_SEQ,
            Sequences.UA_ORDER_AREAS_ID_SEQ,
            Sequences.UA_ORDER_FILES_ID_SEQ,
            Sequences.UA_ORDERS_ID_SEQ,
            Sequences.USER_ROLES_ID_SEQ,
            Sequences.USER_TOKENS_ID_SEQ,
            Sequences.USERS_ID_SEQ);
    }

    @Override
    public final List<Table<?>> getTables() {
        List result = new ArrayList();
        result.addAll(getTables0());
        return result;
    }

    private final List<Table<?>> getTables0() {
        return Arrays.<Table<?>>asList(
            JQFileUserPermissions.FILE_USER_PERMISSIONS,
            JQFiles.FILES,
            JQKeyValueStore.KEY_VALUE_STORE,
            JQSupportThreadMessages.SUPPORT_THREAD_MESSAGES,
            JQSupportThreads.SUPPORT_THREADS,
            JQUaOrderAreas.UA_ORDER_AREAS,
            JQUaOrderFiles.UA_ORDER_FILES,
            JQUaOrders.UA_ORDERS,
            JQUserRoles.USER_ROLES,
            JQUserTokens.USER_TOKENS,
            JQUsers.USERS);
    }
}
