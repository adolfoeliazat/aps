/*
 * APS
 *
 * (C) Copyright 2015-2017 Vladimir Grechka
 *
 * YOU DON'T MESS AROUND WITH THIS SHIT, IT WAS GENERATED BY A TOOL SMARTER THAN YOU
 */

package aps.back

import aps.*


// ==================================================================
// REMOTE PROCEDURE SKELETONS
// ==================================================================

@Generated fun __MirandaImposeNextGeneratedPassword.serve(): __MirandaImposeNextGeneratedPassword_Response {
    return __MirandaImposeNextGeneratedPassword_Response(mirandaImposeNextGeneratedPassword(password = this.password))
}
@Generated fun __MirandaImposeNextGeneratedUserToken.serve(): __MirandaImposeNextGeneratedUserToken_Response {
    return __MirandaImposeNextGeneratedUserToken_Response(mirandaImposeNextGeneratedUserToken(token = this.token))
}
@Generated fun __MirandaImposeNextOrderID.serve(): __MirandaImposeNextOrderID_Response {
    return __MirandaImposeNextOrderID_Response(mirandaImposeNextOrderID(id = this.id))
}
@Generated fun __MirandaGetGeneratedTestTimestamps.serve(): __MirandaGetGeneratedTestTimestamps_Response {
    return __MirandaGetGeneratedTestTimestamps_Response(mirandaGetGeneratedTestTimestamps())
}
@Generated fun __MirandaSeedSomeStuff1.serve(): __MirandaSeedSomeStuff1_Response {
    return __MirandaSeedSomeStuff1_Response(mirandaSeedSomeStuff1())
}
@Generated fun __ReginaAdminSendOrderToStore.serve(): __ReginaAdminSendOrderToStore_Response {
    return __ReginaAdminSendOrderToStore_Response(reginaAdminSendOrderToStore(orderID = this.orderID))
}
