package aps.front

import aps.*

object tabs                              : Fuckers<TabKey>(null) {
    object order                         : Fuckers<TabKey>(this) {
        val params                       by namedFucker(::TabKey); val params_testRef = TestRef(params)
        val files                        by namedFucker(::TabKey); val files_testRef = TestRef(files)
        val messages                     by namedFucker(::TabKey); val messages_testRef = TestRef(messages)
    }

    object shebang                       : Fuckers<TabKey>(this) {
        val diff                         by namedFucker(::TabKey); val diff_testRef = TestRef(diff)
        val actualPaste                  by namedFucker(::TabKey); val actualPaste_testRef = TestRef(actualPaste)
    }
}

object buttons                           : Fuckers<ButtonKey>(null) {
    val sendForApproval                  by namedFucker(::ButtonKey); val sendForApproval_testRef = TestRef(sendForApproval)
    val edit                             by namedFucker(::ButtonKey); val edit_testRef = TestRef(edit)
    val primary                          by namedFucker(::ButtonKey); val primary_testRef = TestRef(primary)
    val plus                             by namedFucker(::ButtonKey); val plus_testRef = TestRef(plus)
    val upload                           by namedFucker(::ButtonKey); val upload_testRef = TestRef(upload)
    val cancel                           by namedFucker(::ButtonKey); val cancel_testRef = TestRef(cancel)
    val refreshPage                      by namedFucker(::ButtonKey); val refreshPage_testRef = TestRef(refreshPage)
    val showMore                         by namedFucker(::ButtonKey); val showMore_testRef = TestRef(showMore)

    object modal                         : Fuckers<ButtonKey>(this) {
        val ok                           by namedFucker(::ButtonKey); val ok_testRef = TestRef(ok)
        val cancel                       by namedFucker(::ButtonKey); val cancel_testRef = TestRef(cancel)
    }

    object assertionBanner               : Fuckers<ButtonKey>(this) {
        val vdiff                        by namedFucker(::ButtonKey); val vdiff_testRef = TestRef(vdiff)
        val accept                       by namedFucker(::ButtonKey); val accept_testRef = TestRef(accept)
        val play                         by namedFucker(::ButtonKey); val play_testRef = TestRef(play)
    }
}

object kics                              : Fuckers<KicKey>(null) {
    object order                         : Fuckers<KicKey>(this) {
        object file                      : Fuckers<KicKey>(this) {
            val download                 by namedFucker(::KicKey); val download_testRef = TestRef(download)
            val delete                   by namedFucker(::KicKey); val delete_testRef = TestRef(delete)
            val edit                     by namedFucker(::KicKey); val edit_testRef = TestRef(edit)
        }
    }
}

object inputs                            : Fuckers<InputKey>(null) {
    val search                           by namedFucker(::InputKey); val search_testRef = TestRef(search)
}

object selects                           : Fuckers<SelectKey<*>>(null) {
    val ordering                         by namedFucker({SelectKey<Ordering>(it)}); val ordering_testRef = TestRef(ordering)
    val filter                           by namedFucker({SelectKey<CustomerFileFilter>(it)}); val filter_testRef = TestRef(filter)
}

object links                             : Fuckers<LinkKey>(null) {
    val createAccount                    by namedFucker(::LinkKey); val createAccount_testRef = TestRef(createAccount)
    val signUp                           by namedFucker(::LinkKey); val signUp_testRef = TestRef(signUp)

    object adminDashboard                : Fuckers<LinkKey>(null) {
        val ordersToApprove              by namedFucker(::LinkKey); val ordersToApprove_testRef = TestRef(ordersToApprove)
    }
}











