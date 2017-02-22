/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import into.kommon.*
import org.mindrot.jbcrypt.BCrypt
import java.util.*

@Servant class ServeConfirmOrder(val orderRepo: UAOrderRepository, val userRepo: UserRepository, val userTokenRepo: UserTokenRepository) : BitchyProcedure() {
    override fun serve() {
        fuckCustomer(FuckCustomerParams(
            bpc = bpc,
            makeRequest = {ConfirmOrderRequest()},
            needsUser = NeedsUser.MAYBE,
            runShit = fun (ctx, req: ConfirmOrderRequest): ConfirmOrderRequest.Response {
                if (ctx.hasUser) {
                    imf("2e07612f-cf94-4f39-b153-592b2e8b5a30")
                } else {
                    val order = orderRepo.findByConfirmationSecret(req.secret.value)
                        ?: bitchExpectedly(t("TOTE", "Че за хрень ты мне тут передаешь в параметрах? Скопируй ссылку из письма нормально, или я не знаю..."))

                    if (order.state != UAOrderState.WAITING_EMAIL_CONFIRMATION)
                        bitchExpectedly(t("TOTE", "Хватит уже этот заказ подтверждать, ага?"))

                    val user = userRepo.findByEmail(order.customerEmail)
                    if (user == null) {
                        val newUser = userRepo.save(User(
                            email = order.customerEmail,
                            firstName = order.customerFirstName,
                            lastName = order.customerLastName,
                            passwordHash = hashPassword(generatePassword()),
                            profilePhone = order.customerPhone,
                            kind = UserKind.CUSTOMER,
                            state = UserState.COOL
                        ))
                        orderRepo.save(order-{o->
                            o.customer = newUser
                            o.state = UAOrderState.CUSTOMER_DRAFT
                        })

                        val userToken = userTokenRepo.save(UserToken(
                            user = newUser,
                            token = generateUserToken()
                        ))

                        return ConfirmOrderRequest.Response(
                            orderId = order.id.toString(),
                            userSignedInAsPartOfMakingOrder = UserSignedInAsPartOfMakingOrder(
                                user = newUser.toRTO(searchWords = listOf()),
                                token = userToken.token
                            ))
                    } else {
                        imf("ServeConfirmOrder -- user exists. TODO: What if banned, etc...    094cd36f-36c8-468c-bbd0-5314f5379ecd")
                    }
                }
            }
        ))
    }
}


