/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import kotlin.browser.*

class TestWriter_Boot_ProfileRejected : WriterBootTestScenario_FuckerToken() {
    override val path = "/"

    override fun setFuckerFields(o: TestSetUserFieldsRequest) {
        _setFuckerFields(o)
    }

    override fun buildStepsAfterWorldBoot() {
        assert_navbar_Gaylord_uncool_profileActive()
        o.assertRootHTML("""<div data-reactroot="" id="167"><div class="container" id="169" style="position: relative;"><div id="171"><div class="page-header " id="177" style="margin-top: 0px; margin-bottom: 15px;"><h3 id="175" style="margin-bottom: 0px;"><span id="173" class="">Профиль</span></h3></div></div><div id="103"><div id="91" style="margin-bottom: 20px; padding: 5px; background: rgb(255, 235, 238);"><div id="83" style="display: flex; align-items: center;"><i class="fa fa-minus-circle  " style="color: rgb(183, 28, 28); font-size: 200%; z-index: 20;"></i><i class="fa fa-circle  " style="color: rgb(255, 255, 255); font-size: 150%; z-index: 10; margin-left: -1em; margin-top: 0.2em;"></i><span id="81" style="margin-left: 8px;"><span id="79" class="">Админ завернул твой профиль</span></span></div><div id="89"><blockquote id="87" style="font-size: 1em; margin-top: -5px; margin-bottom: 0px; border-left-color: rgb(239, 154, 154); margin-left: 10px; padding-left: 13px;"><span id="85" class="">Отстойный какой-то профиль, придумай что-то получше</span></blockquote></div></div><div id="220"><form class="" id="218"><div class="form-group" id="188"><label id="184"><span id="182" class="">Имя</span></label><div id="186" style="position: relative;"><input type="text" id="93" rows="5" class="form-control" value="Gaylord" style="resize: none;"></div></div><div class="form-group" id="196"><label id="192"><span id="190" class="">Фамилия</span></label><div id="194" style="position: relative;"><input type="text" id="95" rows="5" class="form-control" value="Fucker" style="resize: none;"></div></div><div class="form-group" id="204"><label id="200"><span id="198" class="">Телефон</span></label><div id="202" style="position: relative;"><input type="text" id="97" rows="5" class="form-control" value="9110201" style="resize: none;"></div></div><div class="form-group" id="212"><label id="208"><span id="206" class="">Пара ласковых о себе</span></label><div id="210" style="position: relative;"><textarea id="99" rows="5" type="text" class="form-control" style="resize: none;">I'm a little fucker, just a tiny little motherfucker</textarea></div></div><div id="216" style="text-align: left;"><button id="214" class="btn btn-primary "><span id="226">Отправить на проверку</span></button></div></form></div></div></div></div>""")
    }

}

class TestWriter_Boot_ProfileRejected_CanUseDashboardButOnlyForAccountManagement : WriterBootTestScenario_FuckerToken() {
    override val path = "/dashboard.html"

    override fun setFuckerFields(o: TestSetUserFieldsRequest) {
        _setFuckerFields(o)
    }

    override fun buildStepsAfterWorldBoot() {
        assert_navbar_Gaylord_uncool_dashboardActive()
        o.assertRootHTML("""<div data-reactroot="" id="125"><div class="container" id="127" style="position: relative;"><div id="129"><div class="page-header " id="135" style="margin-top: 0px; margin-bottom: 15px;"><h3 id="133" style="margin-bottom: 0px;"><span id="131" class="">Панель</span></h3></div></div><div id="106"><div class="row" id="104"><div class="col-sm-6" id="102"><div id="100"><div id="88" style="background-color: rgb(236, 239, 241); font-weight: bold; padding: 2px 5px; margin-bottom: 10px;">Аккаунт</div><ul class="fa-ul" id="98" style="margin-left: 20px;"><li id="92" style="margin-bottom: 5px;"><i class="fa fa-li fa-chevron-right" id="90" style="color: rgb(84, 110, 122);"></i><a id="82" class="" href="#" style="color: rgb(51, 51, 51);"><span id="80" class="">Выйти прочь</span></a></li><li id="96" style="margin-bottom: 5px;"><i class="fa fa-li fa-chevron-right" id="94" style="color: rgb(84, 110, 122);"></i><a id="86" class="" href="#" style="color: rgb(51, 51, 51);"><span id="84" class="">Сменить пароль</span></a></li></ul></div></div></div></div></div></div>""")
    }

}

private fun _setFuckerFields(o: TestSetUserFieldsRequest) {
    fillTestProfile_Gaylord(o)
    o.state.value = UserState.PROFILE_REJECTED
    o.profileRejectionReason.value = "Отстойный какой-то профиль, придумай что-то получше"
}




















