package aps.front

import aps.*

object tabs                              : Fuckers<TabKey>(null) {
    object order                         : Fuckers<TabKey>(this) {
        val params                       by namedFucker(::TabKey); val params_testRef = TestRef(params)
        val files                        by namedFucker(::TabKey); val files_testRef = TestRef(files)
        val messages                     by namedFucker(::TabKey); val messages_testRef = TestRef(messages)
    }

    object user                          : Fuckers<TabKey>(this) {
        val params                       by namedFucker(::TabKey); val params_testRef = TestRef(params)
        val paramsHistory                by namedFucker(::TabKey); val paramsHistory_testRef = TestRef(paramsHistory)
    }


    object shebang                       : Fuckers<TabKey>(this) {
        val diff                         by namedFucker(::TabKey); val diff_testRef = TestRef(diff)
        val actualPaste                  by namedFucker(::TabKey); val actualPaste_testRef = TestRef(actualPaste)
    }
}

object buttons                           : Fuckers<ButtonKey>(null) {
    val sendForApproval                  by namedFucker(::ButtonKey); val sendForApproval_testRef = TestRef(sendForApproval)
    val sendForApprovalAfterFixing       by namedFucker(::ButtonKey); val sendForApprovalAfterFixing_testRef = TestRef(sendForApprovalAfterFixing)
    val accept                           by namedFucker(::ButtonKey); val accept_testRef = TestRef(accept)
    val reject                           by namedFucker(::ButtonKey); val reject_testRef = TestRef(reject)
    val edit                             by namedFucker(::ButtonKey); val edit_testRef = TestRef(edit)
    val primary                          by namedFucker(::ButtonKey); val primary_testRef = TestRef(primary)
    val plus                             by namedFucker(::ButtonKey); val plus_testRef = TestRef(plus)
    val upload                           by namedFucker(::ButtonKey); val upload_testRef = TestRef(upload)
    val cancel                           by namedFucker(::ButtonKey); val cancel_testRef = TestRef(cancel)
    val refreshPage                      by namedFucker(::ButtonKey); val refreshPage_testRef = TestRef(refreshPage)
    val showMore                         by namedFucker(::ButtonKey); val showMore_testRef = TestRef(showMore)
    val history                          by namedFucker(::ButtonKey); val history_testRef = TestRef(history)
    val back                             by namedFucker(::ButtonKey); val back_testRef = TestRef(back)
    val compare                          by namedFucker(::ButtonKey); val compare_testRef = TestRef(compare)
    val editStoreParams                  by namedFucker(::ButtonKey); val editStoreParams_testRef = TestRef(editStoreParams)
    val joke                             by namedFucker(::ButtonKey); val joke_testRef = TestRef(joke)
    val chooseDocumentCategory           by namedFucker(::ButtonKey); val chooseDocumentCategory_testRef = TestRef(chooseDocumentCategory)


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
    val download                         by namedFucker(::KicKey); val download_testRef = TestRef(download)
    val delete                           by namedFucker(::KicKey); val delete_testRef = TestRef(delete)
    val edit                             by namedFucker(::KicKey); val edit_testRef = TestRef(edit)
    val burger                           by namedFucker(::KicKey); val burger_testRef = TestRef(burger)

    object order                         : Fuckers<KicKey>(this) {
        object file                      : Fuckers<KicKey>(this) {
        }
    }
}

object inputs                            : Fuckers<InputKey>(null) {
    val search                           by namedFucker(::InputKey); val search_testRef = TestRef(search)
}

object enumSelects                       : Fuckers<EnumSelectKey<*>>(null) {
    val ordering                         by namedFucker({EnumSelectKey<Ordering>(it)}); val ordering_testRef = TestRef(ordering)

}

object selects                           : Fuckers<SelectKey>(null) {
    val adminUserFilter                  by namedFucker(::SelectKey); val adminUserFilter_testRef = TestRef(adminUserFilter)
    suspend fun adminUserFilter_testSetValue(value: AdminUserFilter) {
        tselect.setValue(adminUserFilter_testRef, value.name)
    }

    val writerStoreFilter                by namedFucker(::SelectKey); val writerStoreFilter_testRef = TestRef(writerStoreFilter)
    suspend fun writerStoreFilter_testSetValue(value: WriterStoreFilter) {
        tselect.setValue(writerStoreFilter_testRef, value.name)
    }

    val customerFileFilter               by namedFucker(::SelectKey); val customerFileFilter_testRef = TestRef(customerFileFilter)
    suspend fun customerFileFilter_testSetValue(value: CustomerFileFilter) {
        tselect.setValue(customerFileFilter_testRef, value.name)
    }

    val adminOrderFilter                 by namedFucker(::SelectKey); val adminOrderFilter_testRef = TestRef(adminOrderFilter)
    suspend fun adminOrderFilter_testSetValue(value: CustomerFileFilter) {
        tselect.setValue(adminOrderFilter_testRef, value.name)
    }

    val userParamsHistoryFilter          by namedFucker(::SelectKey); val userParamsHistoryFilter_testRef = TestRef(userParamsHistoryFilter)
    suspend fun userParamsHistoryFilter_testSetValue(value: CustomerFileFilter) {
        tselect.setValue(userParamsHistoryFilter_testRef, value.name)
    }
}

object links                             : Fuckers<LinkKey>(null) {
    val createAccount                    by namedFucker(::LinkKey); val createAccount_testRef = TestRef(createAccount)
    val signUp                           by namedFucker(::LinkKey); val signUp_testRef = TestRef(signUp)
    val lipsTitle                        by namedFucker(::LinkKey); val lipsTitle_testRef = TestRef(lipsTitle)
    val compareBelow                     by namedFucker(::LinkKey); val compareBelow_testRef = TestRef(compareBelow)

    object adminDashboard                : Fuckers<LinkKey>(null) {
        val ordersToApprove              by namedFucker(::LinkKey); val ordersToApprove_testRef = TestRef(ordersToApprove)
        val writerProfilesToApprove      by namedFucker(::LinkKey); val writerProfilesToApprove_testRef = TestRef(writerProfilesToApprove)
    }
}

object checkboxes                        : Fuckers<CheckboxKey>(null) {
    val allCategories                    by namedFucker(::CheckboxKey); val allCategories_testRef = TestRef(allCategories)
}












