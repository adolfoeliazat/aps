/*
 * APS
 *
 * (C) Copyright 2015-2017 Vladimir Grechka
 *
 * YOU DON'T MESS AROUND WITH THIS SHIT, IT WAS GENERATED BY A TOOL SMARTER THAN YOU
 */

package aps

import aps.*
import kotlin.reflect.KClass


// ==================================================================
// REMOTE PROCEDURE TOS
// ==================================================================

@Generated @Ser class __MirandaImposeNextGeneratedPassword(val password: String)
@Generated class __MirandaImposeNextGeneratedPassword_Response(override val value: Unit) : CommonResponseFieldsImpl(), SingleValueResponse<Unit>
@Generated @Ser class __MirandaImposeNextGeneratedUserToken(val token: String)
@Generated class __MirandaImposeNextGeneratedUserToken_Response(override val value: Unit) : CommonResponseFieldsImpl(), SingleValueResponse<Unit>
@Generated @Ser class __MirandaImposeNextOrderID(val id: Long)
@Generated class __MirandaImposeNextOrderID_Response(override val value: Unit) : CommonResponseFieldsImpl(), SingleValueResponse<Unit>
@Generated @Ser class __MirandaGetGeneratedTestTimestamps()
@Generated class __MirandaGetGeneratedTestTimestamps_Response(override val value: List<String>) : CommonResponseFieldsImpl(), SingleValueResponse<List<String>>
@Generated @Ser class __MirandaSeedSomeStuff1()
@Generated class __MirandaSeedSomeStuff1_Response(override val value: Unit) : CommonResponseFieldsImpl(), SingleValueResponse<Unit>
@Generated @Ser class __MirandaSaveRequestResponseLog(val name: String)
@Generated class __MirandaSaveRequestResponseLog_Response(override val value: Unit) : CommonResponseFieldsImpl(), SingleValueResponse<Unit>
@Generated @Ser class __MirandaMapStack(val rawStack: String)
@Generated class __MirandaMapStack_Response(override val value: String) : CommonResponseFieldsImpl(), SingleValueResponse<String>
@Generated @Ser class __MirandaGetSentEmails()
@Generated class __MirandaGetSentEmails_Response(override val value: MutableList<Email>) : CommonResponseFieldsImpl(), SingleValueResponse<MutableList<Email>>
@Generated @Ser class __MirandaClearSentEmails()
@Generated class __MirandaClearSentEmails_Response(override val value: Unit) : CommonResponseFieldsImpl(), SingleValueResponse<Unit>
@Generated @Ser class __MirandaImposeNextRequestTimestamp(val stamp: String)
@Generated class __MirandaImposeNextRequestTimestamp_Response(override val value: Unit) : CommonResponseFieldsImpl(), SingleValueResponse<Unit>
@Generated @Ser class __MirandaImposeNextRequestError(val error: String? = null)
@Generated class __MirandaImposeNextRequestError_Response(override val value: Unit) : CommonResponseFieldsImpl(), SingleValueResponse<Unit>
@Generated @Ser class __MirandaImposeNextGeneratedConfirmationSecret(val secret: String)
@Generated class __MirandaImposeNextGeneratedConfirmationSecret_Response(override val value: Unit) : CommonResponseFieldsImpl(), SingleValueResponse<Unit>
@Generated @Ser class __MirandaPing()
@Generated class __MirandaPing_Response(override val value: Unit) : CommonResponseFieldsImpl(), SingleValueResponse<Unit>
@Generated @Ser class __MirandaGetSoftwareVersion()
@Generated class __MirandaGetSoftwareVersion_Response(override val value: MirandaGetSoftwareVersionResult) : CommonResponseFieldsImpl(), SingleValueResponse<MirandaGetSoftwareVersionResult>
@Generated @Ser class __ReginaLoadUser(val userID: Long)
@Generated class __ReginaLoadUser_Response(override val value: UserRTO) : CommonResponseFieldsImpl(), SingleValueResponse<UserRTO>
@Generated @Ser class __ReginaAcceptProfile(val userID: Long)
@Generated class __ReginaAcceptProfile_Response(override val value: Unit) : CommonResponseFieldsImpl(), SingleValueResponse<Unit>
@Generated @Ser class __ReginaCustomerSendOrderForApprovalAfterFixing(val orderID: Long)
@Generated class __ReginaCustomerSendOrderForApprovalAfterFixing_Response(override val value: Unit) : CommonResponseFieldsImpl(), SingleValueResponse<Unit>
@Generated @Ser class __ReginaAdminSendOrderToStore(val orderID: Long)
@Generated class __ReginaAdminSendOrderToStore_Response(override val value: Unit) : CommonResponseFieldsImpl(), SingleValueResponse<Unit>
@Generated @Ser class __ReginaLoadUAOrder(val id: Long)
@Generated class __ReginaLoadUAOrder_Response(override val value: UAOrderRTO) : CommonResponseFieldsImpl(), SingleValueResponse<UAOrderRTO>
@Generated @Ser class __ReginaGetDocumentCategories()
@Generated class __ReginaGetDocumentCategories_Response(override val value: UADocumentCategoryRTO) : CommonResponseFieldsImpl(), SingleValueResponse<UADocumentCategoryRTO>
@Generated @Ser class __ReginaGetMyself()
@Generated class __ReginaGetMyself_Response(override val value: UserRTO) : CommonResponseFieldsImpl(), SingleValueResponse<UserRTO>
@Generated @Ser class __ReginaGetPairOfLastHistoryItems<T : HistoryItemRTOFields>(val type: KClass<T>, val  entityID: Long)
@Generated class __ReginaGetPairOfLastHistoryItems_Response<T : HistoryItemRTOFields>(override val value: PairOfLastHistoryItems<T>) : CommonResponseFieldsImpl(), SingleValueResponse<PairOfLastHistoryItems<T>>
