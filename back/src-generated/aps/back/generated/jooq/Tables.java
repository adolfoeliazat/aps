/**
 * This class is generated by jOOQ
 */
package aps.back.generated.jooq;


import aps.back.generated.jooq.tables.SupportThreadMessages;
import aps.back.generated.jooq.tables.SupportThreads;
import aps.back.generated.jooq.tables.UserRoles;
import aps.back.generated.jooq.tables.UserTokens;
import aps.back.generated.jooq.tables.Users;

import javax.annotation.Generated;


/**
 * Convenience access to all tables in public
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.8.4"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Tables {

    /**
     * The table <code>public.support_thread_messages</code>.
     */
    public static final SupportThreadMessages SUPPORT_THREAD_MESSAGES = aps.back.generated.jooq.tables.SupportThreadMessages.SUPPORT_THREAD_MESSAGES;

    /**
     * The table <code>public.support_threads</code>.
     */
    public static final SupportThreads SUPPORT_THREADS = aps.back.generated.jooq.tables.SupportThreads.SUPPORT_THREADS;

    /**
     * The table <code>public.user_roles</code>.
     */
    public static final UserRoles USER_ROLES = aps.back.generated.jooq.tables.UserRoles.USER_ROLES;

    /**
     * The table <code>public.user_tokens</code>.
     */
    public static final UserTokens USER_TOKENS = aps.back.generated.jooq.tables.UserTokens.USER_TOKENS;

    /**
     * The table <code>public.users</code>.
     */
    public static final Users USERS = aps.back.generated.jooq.tables.Users.USERS;
}
