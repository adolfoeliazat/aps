package aps.front

import aps.TestSetUserFieldsRequest
import aps.UserState

class TestWriter_Boot_Banned : WriterBootTestScenario_FuckerToken() {
    override val path = "/"

    override fun setFuckerFields(o: TestSetUserFieldsRequest) {
        _setFuckerFields(o)
    }

    override fun buildStepsAfterWorldBoot() {
        assert_navbar_Gaylord_uncool_profileActive()
        o.assertRootHTML("""<div data-reactroot="" id="111"><div class="container" id="113" style="position: relative;"><div id="115"><div class="page-header " id="121" style="margin-top: 0px; margin-bottom: 15px;"><h3 id="119" style="margin-bottom: 0px;"><span id="117" class="">Профиль</span></h3></div></div><div id="93"><div id="91" style="margin-bottom: 20px; padding: 5px; background: rgb(255, 235, 238);"><div id="83" style="display: flex; align-items: center;"><i class="fa fa-minus-circle  " style="color: rgb(183, 28, 28); font-size: 200%; z-index: 20;"></i><i class="fa fa-circle  " style="color: rgb(255, 255, 255); font-size: 150%; z-index: 10; margin-left: -1em; margin-top: 0.2em;"></i><span id="81" style="margin-left: 8px;"><span id="79" class="">Тебя тупо забанили, всему конец</span></span></div><div id="89"><blockquote id="87" style="font-size: 1em; margin-top: -5px; margin-bottom: 0px; border-left-color: rgb(239, 154, 154); margin-left: 10px; padding-left: 13px;"><span id="85" class="">Нам такие мудаки здесь не нужны, иди в жопу</span></blockquote></div></div></div></div></div>""")
    }
}

class TestWriter_Boot_Banned_CanUseDashboardButOnlyToSignOut : WriterBootTestScenario_FuckerToken() {
    override val path = "/dashboard.html"

    override fun setFuckerFields(o: TestSetUserFieldsRequest) {
        _setFuckerFields(o)
    }

    override fun buildStepsAfterWorldBoot() {
        assert_navbar_Gaylord_uncool_dashboardActive()
        o.assertRootHTML("""<div data-reactroot="" id="115"><div class="container" id="117" style="position: relative;"><div id="119"><div class="page-header " id="125" style="margin-top: 0px; margin-bottom: 15px;"><h3 id="123" style="margin-bottom: 0px;"><span id="121" class="">Панель</span></h3></div></div><div id="98"><div class="row" id="96"><div class="col-sm-6" id="94"><div id="92"><div id="84" style="background-color: rgb(236, 239, 241); font-weight: bold; padding: 2px 5px; margin-bottom: 10px;">Аккаунт</div><ul class="fa-ul" id="90" style="margin-left: 20px;"><li id="88" style="margin-bottom: 5px;"><i class="fa fa-li fa-chevron-right" id="86" style="color: rgb(84, 110, 122);"></i><a id="82" class="" href="#" style="color: rgb(51, 51, 51);"><span id="80" class="">Выйти прочь</span></a></li></ul></div></div></div></div></div></div>""")
    }
}


private fun _setFuckerFields(o: TestSetUserFieldsRequest) {
    o.state.value = UserState.BANNED
    o.banReason.value = "Нам такие мудаки здесь не нужны, иди в жопу"
}

