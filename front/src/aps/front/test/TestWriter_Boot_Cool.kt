/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import kotlin.browser.*

class TestWriter_Boot_Cool : WriterBootTestScenario_FuckerToken() {
    override val path = "/"

    override fun setFuckerFields(o: TestSetUserFieldsRequest) {
        o.state.value = UserState.COOL
    }

    override fun buildStepsAfterWorldBoot() {
        o.state("Fucker is signed in")
        assert_navbar_Gaylord_activeDashboard()
        o.assertRootHTML("""<div data-reactroot="" id="170"><div class="container" id="172" style="position: relative;"><div id="174"><div class="page-header " id="180" style="margin-top: 0px; margin-bottom: 15px;"><h3 id="178" style="margin-bottom: 0px;"><span id="176" class="">Панель</span></h3></div></div><div id="148"><div class="row" id="146"><div class="col-sm-6" id="120"><div id="118"><div id="110" style="background-color: rgb(236, 239, 241); font-weight: bold; padding: 2px 5px; margin-bottom: 10px;">Работенка</div><ul class="fa-ul" id="116" style="margin-left: 20px;"><li id="114" style="margin-bottom: 5px;"><i class="fa fa-li fa-chevron-right" id="112" style="color: rgb(84, 110, 122);"></i></li></ul></div></div><div class="col-sm-6" id="144"><div id="142"><div id="130" style="background-color: rgb(236, 239, 241); font-weight: bold; padding: 2px 5px; margin-bottom: 10px;">Аккаунт</div><ul class="fa-ul" id="140" style="margin-left: 20px;"><li id="134" style="margin-bottom: 5px;"><i class="fa fa-li fa-chevron-right" id="132" style="color: rgb(84, 110, 122);"></i><a id="124" class="" href="#" style="color: rgb(51, 51, 51);"><span id="122" class="">Выйти прочь</span></a></li><li id="138" style="margin-bottom: 5px;"><i class="fa fa-li fa-chevron-right" id="136" style="color: rgb(84, 110, 122);"></i><a id="128" class="" href="#" style="color: rgb(51, 51, 51);"><span id="126" class="">Сменить пароль</span></a></li></ul></div></div></div></div></div></div>""")
    }
}


fun <Meat, T> FormResponse2<Meat>.switch(hunky: (meat: Meat) -> T,
                                         shitty: (error: FormResponse2.Shitty<Meat>) -> T)
    : T = when (this) {
    is FormResponse2.Shitty -> shitty(this)
    is FormResponse2.Hunky -> hunky(this.meat)
}

fun <Meat> FormResponse2<Meat>.whenShitty(shitty: (error: FormResponse2.Shitty<Meat>) -> Unit)
    : Unit = this.switch(hunky = {it},
                         shitty = {shitty(it)})

val <Meat> FormResponse2<Meat>.orDie: Meat get() = this.switch(
    hunky = {it},
    shitty = {
        throw FatException("Got shitty response from ${Globus.lastAttemptedRPCName}: ${it.error}",
                           markdownPayload =
                               if (it.fieldErrors.isEmpty())
                                   "No field errors"
                               else
                                   "Field errors:\n" +
                                   it.fieldErrors.map{"* ${it.field}: ${it.error}"}.joinToString("\n"))
    }
)




















