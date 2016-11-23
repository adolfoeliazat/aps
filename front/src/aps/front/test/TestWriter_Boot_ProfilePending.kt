/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import into.kommon.imf
import kotlin.browser.*

class TestWriter_Boot_ProfilePending : WriterBootTestScenario_FuckerToken() {
    override val path = "/"

    override fun setFuckerFields(o: TestSetUserFieldsRequest) {
        o.state.value = UserState.PROFILE_PENDING
    }

    override fun buildStepsAfterWorldBoot() {
        assert_navbar_Gaylord_uncool_profileActive()
        o.assertRootHTML("""<div data-reactroot="" id="603"><div class="container" id="605" style="position: relative;"><div id="607"><div class="page-header " id="613" style="margin-top: 0px; margin-bottom: 15px;"><h3 id="611" style="margin-bottom: 0px;"><span id="609" class="">Профиль</span></h3></div></div><div id="540"><div id="528" style="width: 720px; margin-bottom: 15px;"><i class="fa fa-exclamation-triangle" id="524" style="color: rgb(255, 111, 0);"></i><!-- react-text: 47 -->&nbsp;<!-- /react-text --><!-- react-text: 48 -->&nbsp;<!-- /react-text --><span id="526" class="">Сначала заполняешь профиль. Админ связывается с тобой и активирует аккаунт. Потом все остальное.</span></div><div id="655"><form class="" id="653"><div class="form-group" id="623"><label id="619"><span id="617" class="">Имя</span></label><div id="621" style="position: relative;"><input type="text" id="530" rows="5" class="form-control" value="Gaylord" style="resize: none;"></div></div><div class="form-group" id="631"><label id="627"><span id="625" class="">Фамилия</span></label><div id="629" style="position: relative;"><input type="text" id="532" rows="5" class="form-control" value="Fucker" style="resize: none;"></div></div><div class="form-group" id="639"><label id="635"><span id="633" class="">Телефон</span></label><div id="637" style="position: relative;"><input type="text" id="534" rows="5" class="form-control" value="" style="resize: none;"></div></div><div class="form-group" id="647"><label id="643"><span id="641" class="">Пара ласковых о себе</span></label><div id="645" style="position: relative;"><textarea id="536" rows="5" type="text" class="form-control" style="resize: none;"></textarea></div></div><div id="651" style="text-align: left;"><button id="649" class="btn btn-primary "><span id="661">Отправить на проверку</span></button></div></form></div></div></div></div>""")
    }

}

class TestWriter_Boot_ProfilePending_CanUseDashboardButOnlyForAccountManagement : WriterBootTestScenario_FuckerToken() {
    override val path = "/dashboard.html"

    override fun setFuckerFields(o: TestSetUserFieldsRequest) {
        o.state.value = UserState.PROFILE_PENDING
    }

    override fun buildStepsAfterWorldBoot() {
        assert_navbar_Gaylord_uncool_dashboardActive()
        o.assertRootHTML("""<div data-reactroot="" id="125"><div class="container" id="127" style="position: relative;"><div id="129"><div class="page-header " id="135" style="margin-top: 0px; margin-bottom: 15px;"><h3 id="133" style="margin-bottom: 0px;"><span id="131" class="">Панель</span></h3></div></div><div id="106"><div class="row" id="104"><div class="col-sm-6" id="102"><div id="100"><div id="88" style="background-color: rgb(236, 239, 241); font-weight: bold; padding: 2px 5px; margin-bottom: 10px;">Аккаунт</div><ul class="fa-ul" id="98" style="margin-left: 20px;"><li id="92" style="margin-bottom: 5px;"><i class="fa fa-li fa-chevron-right" id="90" style="color: rgb(84, 110, 122);"></i><a id="82" class="" href="#" style="color: rgb(51, 51, 51);"><span id="80" class="">Выйти прочь</span></a></li><li id="96" style="margin-bottom: 5px;"><i class="fa fa-li fa-chevron-right" id="94" style="color: rgb(84, 110, 122);"></i><a id="86" class="" href="#" style="color: rgb(51, 51, 51);"><span id="84" class="">Сменить пароль</span></a></li></ul></div></div></div></div></div></div>""")
    }

}




















