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

@Servant class ServeConfirmOrder(
    val orderRepo: UAOrderRepository,
    val userRepo: UserRepository,
    val userTokenRepo: UserTokenRepository
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
                    val order = orderRepo.findByConfirmationSecret(req.secret.value)
                    if (order == null)
                        bitchExpectedly(t("TOTE", "Че за хрень ты мне тут передаешь в параметрах? Скопируй ссылку из письма нормально, или я не знаю..."))
                    if (order.state != UAOrderState.WAITING_EMAIL_CONFIRMATION)
                        bitchExpectedly(t("TOTE", "Хватит уже этот заказ подтверждать, ага?"))

                    val anonymousCustomerEmail = order.anonymousCustomerEmail
                    if (anonymousCustomerEmail == null) {
                        imf("ServeConfirmOrder -- no anonymousCustomerEmail (meaning order was created by signed-in user)")
                    } else {
                        val user = userRepo.findByEmail(anonymousCustomerEmail)
                        if (user == null) {
                            val password = TestServerFiddling.nextGeneratedPassword.getAndReset()
                                ?: UUID.randomUUID().toString()

                            val newCustomer = userRepo.save(User(
                                email = anonymousCustomerEmail,
                                firstName = order.anonymousCustomerName!!,
                                lastName = "ХЗ",
                                passwordHash = BCrypt.hashpw(password, BCrypt.gensalt()),
                                phone = order.phone,
                                kind = UserKind.CUSTOMER,
                                state = UserState.COOL
                            ))
                            orderRepo.save(order-{o->
                                o.customer = newCustomer
                                o.state = UAOrderState.WAITING_ADMIN_APPROVAL
                            })

                            val userToken = UserToken(
                                user = newCustomer,
                                token = UUID.randomUUID().toString()
                            )
                            userTokenRepo.save(userToken)

                            return ConfirmOrderRequest.Response(
                                orderId = order.id.toString(),
                                userSignedInAsPartOfMakingOrder = UserSignedInAsPartOfMakingOrder(
                                    user = newCustomer.toRTO(),
                                    token = userToken.token
                                ))
                        } else {
                            imf("ServeConfirmOrder -- user exists. TODO: What if banned, etc...")
                        }
                    }
                }
            }
        ))
    }
}


