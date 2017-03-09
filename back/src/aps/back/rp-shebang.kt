package aps.back

import aps.*
import org.springframework.data.repository.findOrDie

@Remote fun reginaAdminSendOrderToStore(orderID: Long) {
    check(requestUserEntity.user.kind == UserKind.ADMIN){"0af9f1b0-b5fb-4fb2-b3a9-198a0185ee15"}
    // TODO:vgrechka Security

    val order = uaOrderRepo.findOrDie(orderID)
    val ord = order.order
    check(ord.state in setOf(UAOrderState.WAITING_ADMIN_APPROVAL)){"7af262c7-2a28-43f8-910a-ccf3569142e9"}
    if (-1 in setOf(ord.minAllowedDurationOffer, ord.maxAllowedDurationOffer,
                    ord.minAllowedPriceOffer, ord.maxAllowedPriceOffer)) {
        bitchExpectedly(t("TOTE", "Сперва заполни параметры для стора"))
    }

    ord.whatShouldBeFixedByCustomer = null
    ord.movedToStoreAt = RequestGlobus.stamp
    ord.state = UAOrderState.IN_STORE
}

