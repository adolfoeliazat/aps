/*
 * APS
 *
 * (C) Copyright 2015-2017 Vladimir Grechka
 *
 * YOU DON'T MESS AROUND WITH THIS SHIT, IT WAS GENERATED BY A TOOL SMARTER THAN YOU
 */

package aps.front

import aps.*
import kotlin.reflect.KClass


// ==================================================================
// REMOTE PROCEDURE STUBS
// ==================================================================

@Generated suspend fun mirandaImposeNextGeneratedPassword(password: String): Unit = _askMiranda<__MirandaImposeNextGeneratedPassword_Response>(__MirandaImposeNextGeneratedPassword(password = password)).value
@Generated suspend fun mirandaImposeNextGeneratedUserToken(token: String): Unit = _askMiranda<__MirandaImposeNextGeneratedUserToken_Response>(__MirandaImposeNextGeneratedUserToken(token = token)).value
@Generated suspend fun mirandaImposeNextOrderID(id: Long): Unit = _askMiranda<__MirandaImposeNextOrderID_Response>(__MirandaImposeNextOrderID(id = id)).value
@Generated suspend fun mirandaGetGeneratedTestTimestamps(): List<String> = _askMiranda<__MirandaGetGeneratedTestTimestamps_Response>(__MirandaGetGeneratedTestTimestamps()).value
@Generated suspend fun mirandaSeedSomeStuff1(): Unit = _askMiranda<__MirandaSeedSomeStuff1_Response>(__MirandaSeedSomeStuff1()).value
@Generated suspend fun mirandaSaveRequestResponseLog(name: String): Unit = _askMiranda<__MirandaSaveRequestResponseLog_Response>(__MirandaSaveRequestResponseLog(name = name)).value
@Generated suspend fun mirandaMapStack(rawStack: String): String = _askMiranda<__MirandaMapStack_Response>(__MirandaMapStack(rawStack = rawStack)).value
@Generated suspend fun mirandaGetSentEmails(): MutableList<Email> = _askMiranda<__MirandaGetSentEmails_Response>(__MirandaGetSentEmails()).value
@Generated suspend fun mirandaClearSentEmails(): Unit = _askMiranda<__MirandaClearSentEmails_Response>(__MirandaClearSentEmails()).value
@Generated suspend fun mirandaImposeNextRequestTimestamp(stamp: String): Unit = _askMiranda<__MirandaImposeNextRequestTimestamp_Response>(__MirandaImposeNextRequestTimestamp(stamp = stamp)).value
@Generated suspend fun mirandaImposeNextRequestError(error: String? = null): Unit = _askMiranda<__MirandaImposeNextRequestError_Response>(__MirandaImposeNextRequestError(error = error)).value
@Generated suspend fun mirandaImposeNextGeneratedConfirmationSecret(secret: String): Unit = _askMiranda<__MirandaImposeNextGeneratedConfirmationSecret_Response>(__MirandaImposeNextGeneratedConfirmationSecret(secret = secret)).value
@Generated suspend fun mirandaPing(): Unit = _askMiranda<__MirandaPing_Response>(__MirandaPing()).value
@Generated suspend fun mirandaGetSoftwareVersion(): MirandaGetSoftwareVersionResult = _askMiranda<__MirandaGetSoftwareVersion_Response>(__MirandaGetSoftwareVersion()).value
@Generated suspend fun reginaLoadUser(userID: Long): FormResponse2<UserRTO> = _simplifyFormResponseMeat(_askRegina<__ReginaLoadUser_Response>(__ReginaLoadUser(userID = userID)))
@Generated suspend fun reginaAcceptProfile(userID: Long): FormResponse2<Unit> = _simplifyFormResponseMeat(_askRegina<__ReginaAcceptProfile_Response>(__ReginaAcceptProfile(userID = userID)))
@Generated suspend fun reginaCustomerSendOrderForApprovalAfterFixing(orderID: Long): FormResponse2<Unit> = _simplifyFormResponseMeat(_askRegina<__ReginaCustomerSendOrderForApprovalAfterFixing_Response>(__ReginaCustomerSendOrderForApprovalAfterFixing(orderID = orderID)))
@Generated suspend fun reginaAdminSendOrderToStore(orderID: Long): FormResponse2<Unit> = _simplifyFormResponseMeat(_askRegina<__ReginaAdminSendOrderToStore_Response>(__ReginaAdminSendOrderToStore(orderID = orderID)))
@Generated suspend fun reginaLoadUAOrder(id: Long): FormResponse2<UAOrderRTO> = _simplifyFormResponseMeat(_askRegina<__ReginaLoadUAOrder_Response>(__ReginaLoadUAOrder(id = id)))
@Generated suspend fun reginaGetDocumentCategories(): FormResponse2<UADocumentCategoryRTO> = _simplifyFormResponseMeat(_askRegina<__ReginaGetDocumentCategories_Response>(__ReginaGetDocumentCategories()))
@Generated suspend fun reginaGetMyself(): FormResponse2<UserRTO> = _simplifyFormResponseMeat(_askRegina<__ReginaGetMyself_Response>(__ReginaGetMyself()))
@Generated suspend fun <T : HistoryItemRTOFields> reginaGetPairOfLastHistoryItems(type: KClass<T>, entityID: Long): FormResponse2<PairOfLastHistoryItems<T>> = _simplifyFormResponseMeat(_askRegina<__ReginaGetPairOfLastHistoryItems_Response<T>>(__ReginaGetPairOfLastHistoryItems(type = type, entityID = entityID)))

// ==================================================================
// CONTROL TESTING HELPERS
// ==================================================================

// ------------------------------------------------------------------
// Select
// ------------------------------------------------------------------

// writerStoreFilter

    val tselect.writerStoreFilter get() = __WriterStoreFilterTester
    object __WriterStoreFilterTester {
        suspend fun setRawValue(value: String) = tselect.setValue(selects.writerStoreFilter, value)
        suspend fun setValue(value: WriterStoreFilter) = setRawValue(value.name)
    }

// adminUserFilter

    val tselect.adminUserFilter get() = __AdminUserFilterTester
    object __AdminUserFilterTester {
        suspend fun setRawValue(value: String) = tselect.setValue(selects.adminUserFilter, value)
        suspend fun setValue(value: AdminUserFilter) = setRawValue(value.name)
    }

// customerFileFilter

    val tselect.customerFileFilter get() = __CustomerFileFilterTester
    object __CustomerFileFilterTester {
        suspend fun setRawValue(value: String) = tselect.setValue(selects.customerFileFilter, value)
        suspend fun setValue(value: CustomerFileFilter) = setRawValue(value.name)
    }

// adminOrderFilter

    val tselect.adminOrderFilter get() = __AdminOrderFilterTester
    object __AdminOrderFilterTester {
        suspend fun setRawValue(value: String) = tselect.setValue(selects.adminOrderFilter, value)
        suspend fun setValue(value: CustomerFileFilter) = setRawValue(value.name)
    }

// userParamsHistoryFilter

    val tselect.userParamsHistoryFilter get() = __UserParamsHistoryFilterTester
    object __UserParamsHistoryFilterTester {
        suspend fun setRawValue(value: String) = tselect.setValue(selects.userParamsHistoryFilter, value)
        suspend fun setValue(value: CustomerFileFilter) = setRawValue(value.name)
    }

// adminBidFilter

    val tselect.adminBidFilter get() = __AdminBidFilterTester
    object __AdminBidFilterTester {
        suspend fun setRawValue(value: String) = tselect.setValue(selects.adminBidFilter, value)
        suspend fun setValue(value: AdminBidFilter) = setRawValue(value.name)
    }

