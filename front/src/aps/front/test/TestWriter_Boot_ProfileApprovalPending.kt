/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import into.kommon.imf
import kotlin.browser.*

class TestWriter_Boot_ProfileApprovalPending : WriterBootTestScenario_FuckerToken() {
    override val path = "/"

    override fun setFuckerFields(o: TestSetUserFieldsRequest) {
        _setFuckerFields(o)
    }

    override fun buildStepsAfterWorldBoot() {
        assert_navbar_Gaylord_uncool_profileActive()
        o.assertRootHTML("""<div data-reactroot="" id="225"><div class="container" id="227" style="position: relative;"><div id="229"><div class="page-header " id="235" style="margin-top: 0px; margin-bottom: 15px;"><h3 id="233" style="margin-bottom: 0px;"><span id="231" class="">Профиль</span></h3></div></div><div id="191"><div id="81" style="margin-bottom: 20px;"><i class="fa fa-hourglass-half  " style="color: rgb(255, 111, 0);"></i><!-- react-text: 76 -->&nbsp;<!-- /react-text --><!-- react-text: 77 -->&nbsp;<!-- /react-text --><span id="79" class="">Админ проверяет профиль, жди извещения почтой</span></div><div id="189"><div class="row" id="131"><div class="col-sm-3" id="93"><div class="form-group" id="91"><label id="87"><span id="85" class="">Имя</span></label><div id="89"><span id="83" class="">Gaylord</span></div></div></div><div class="col-sm-3" id="105"><div class="form-group" id="103"><label id="99"><span id="97" class="">Фамилия</span></label><div id="101"><span id="95" class="">Fucker</span></div></div></div><div class="col-sm-3" id="117"><div class="form-group" id="115"><label id="111"><span id="109" class="">Почта</span></label><div id="113"><span id="107" class="">fucker@test.shit.ua</span></div></div></div><div class="col-sm-3" id="129"><div class="form-group" id="127"><label id="123"><span id="121" class="">Телефон</span></label><div id="125"><span id="119" class="">9110201</span></div></div></div></div><div class="row" id="173"><div class="col-sm-3" id="147"><div class="form-group" id="145"><label id="141"><span id="139" class="">Тип</span></label><div id="143"><div id="137"><i id="133" class="fa fa-pencil null" style="margin-right: 5px; margin-left: 5px;"></i><span id="135" class="">Писатель</span></div></div></div></div><div class="col-sm-3" id="159"><div class="form-group" id="157"><label id="153"><span id="151" class="">Аккаунт создан</span></label><div id="155"><span id="149" class="">23/11/2016 15:15:25 (Киев)</span></div></div></div><div class="col-sm-3" id="171"><div class="form-group" id="169"><label id="165"><span id="163" class="">Профиль залит</span></label><div id="167"><span id="161" class="">23/11/2016 16:23:57 (Киев)</span></div></div></div></div><div class="row" id="187"><div class="col-sm-12" id="185"><div class="form-group" id="183"><label id="179"><span id="177" class="">Набрехано о себе</span></label><div id="181"><span id="175" class="" style="white-space: pre-wrap;">I'm a little fucker, just a tiny little motherfucker</span></div></div></div></div></div></div></div></div>""")
    }

}


class TestWriter_Boot_ProfileApprovalPending_CanUseDashboardButOnlyForAccountManagement : WriterBootTestScenario_FuckerToken() {
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
    o.state.value = UserState.PROFILE_APPROVAL_PENDING
}




















