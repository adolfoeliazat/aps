/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import into.kommon.*
import org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import java.util.*

@Component @Scope(SCOPE_PROTOTYPE) class ServeConfirmOrder(
    val repo: UAOrderRepository
) : BitchyProcedure() {
    override fun serve() {
        fuckCustomer(FuckCustomerParams(
            bpc = bpc,
            makeRequest = {ConfirmOrderRequest()},
            needsUser = NeedsUser.MAYBE,
            runShit = fun (ctx, req: ConfirmOrderRequest): ConfirmOrderRequest.Response {
                if (ctx.hasUser) {
                    imf("ServeConfirmOrderRequest hasUser")
                } else {
                    val order = repo.findByConfirmationSecret(req.secret.value)
                    if (order == null)
                        bitchExpectedly(t("TOTE", "Че за хрень ты мне тут передаешь в параметрах? Скопируй ссылку из письма нормально, или я не знаю..."))
                    if (order.state != UAOrderState.WAITING_EMAIL_CONFIRMATION)
                        bitchExpectedly(t("TOTE", "Хватит уже этот заказ подтверждать, ага?"))

                    order.state = UAOrderState.WAITING_ADMIN_APPROVAL
                    repo.save(order)
                    return ConfirmOrderRequest.Response()
                }
            }
        ))
    }
}


