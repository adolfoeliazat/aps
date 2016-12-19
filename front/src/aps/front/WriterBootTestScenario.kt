package aps.front

import aps.*

abstract class WriterBootTestScenario : BootTestScenario() {
    override val clientKind = ClientKind.WRITER
    lateinit var fuckerToken: String

    fun prepareFucker(fieldsReq: TestSetUserFieldsRequest): Promise<Unit> {"__async"
        __await(ImposeNextGeneratedPasswordRequest.send("fucker-secret"))

        __await(send(null, SignUpRequest()-{o->
            o.agreeTerms.value = true
            o.immutableSignUpFields-{o->
                o.email.value = "fucker@test.shit.ua"
            }
            o.mutableSignUpFields-{o->
                o.firstName.value = "Gaylord"
                o.lastName.value = "Fucker"
            }
        })).orDie()

        fuckerToken = __await(sendSafe(null, SignInWithPasswordRequest()-{o->
            o.email.value = "fucker@test.shit.ua"
            o.password.value = "fucker-secret"
        })).orDie().token

        fieldsReq.email.value = "fucker@test.shit.ua"
        __await(send(fieldsReq))

        return __asyncResult(Unit)
    }

    fun assert_staticHomePage_rightNavbarGaylord() {
        o.assertVisibleText("Приветствуем")
        assert_navbar_Gaylord_activeDashboard()
    }

    fun assert_breatheBanner_rightNavbarEmpty() {
        o.assertNavbarHTML("""<nav class="navbar navbar-default navbar-fixed-top" id="MakeStaticSites-194"><div class="container" id="MakeStaticSites-192"><div class="navbar-header" id="MakeStaticSites-186"><a id="MakeStaticSites-184" class="navbar-brand" href="/">Писец</a></div><div style="text-align: left;" id="MakeStaticSites-190"><ul id="leftNavbar" class="nav navbar-nav" style="float: none; display: inline-block; vertical-align: top;"><li id="MakeStaticSites-174" class=""><a id="MakeStaticSites-172" href="why.html"><span id="MakeStaticSites-197" class="">Почему мы?</span></a></li><li id="MakeStaticSites-177" class=""><a id="MakeStaticSites-175" href="prices.html"><span id="MakeStaticSites-202" class="">Цены</span></a></li><li id="MakeStaticSites-180" class=""><a id="MakeStaticSites-178" href="faq.html"><span id="MakeStaticSites-207" class="">ЧаВо</span></a></li></ul><ul id="rightNavbar" class="nav navbar-nav navbar-right"><li id="MakeStaticSites-183" class=""></li></ul></div></div></nav>""")
        o.assertRootHTML("""<div id="ticker"><div>
            <div class="container">
            <div style="display:  flex;  align-items:  center;  justify-content:  center;  position:  absolute;  left:  0px;  top:  200px;  width:  100%;">
            <span style="margin-left:  10">Дышите глубоко...</span>
            <div id="wholePageTicker" class="progressTicker" style="background-color:  #546e7a;  width:  14px;  height:  28px;  margin-left:  10px;  margin-top:  -5px"></div>
            </div>
            </div></div></div>""")
    }

    fun assert_navbar_Gaylord_activeDashboard() {
        o.assertNavbarHTML("""<nav data-reactroot="" class="navbar navbar-default navbar-fixed-top" id="229"><div class="container" id="227"><div class="navbar-header" id="221"><a id="219" class="navbar-brand" href="/">Писец</a></div><div id="225" style="text-align: left;"><ul id="leftNavbar" class="nav navbar-nav" style="float: none; display: inline-block; vertical-align: top;"><li class="dropdown" id="215"><a href="#" class="dropdown-toggle skipClearMenus" data-toggle="dropdown" role="button" id="211"><!-- react-text: 42 -->Проза<!-- /react-text --><span class="caret" id="209" style="margin-left: 5px;"></span></a><ul class="dropdown-menu" id="213"><li id="191" class=""><a id="189" href="why.html"><span id="232" class="">Почему мы?</span></a></li><li id="194" class=""><a id="192" href="prices.html"><span id="237" class="">Цены</span></a></li><li id="197" class=""><a id="195" href="faq.html"><span id="242" class="">ЧаВо</span></a></li></ul></li><li id="200" class=""><a id="198" href="orders.html"><span id="247" class="">Мои заказы</span></a></li><li id="203" class=""><a id="201" href="store.html"><span id="252" class="">Аукцион</span></a></li><li id="206" class=""><a id="204" href="profile.html"><span id="257" class="">Профиль</span></a></li></ul><ul id="rightNavbar" class="nav navbar-nav navbar-right"><li id="218" class="active"><a id="216" href="dashboard.html"><span id="262" class="">Gaylord</span></a></li></ul></div></div></nav>""")
    }

    protected fun assert_navbar_Gaylord_uncool_profileActive() {
        o.assertNavbarHTML("""<nav data-reactroot="" class="navbar navbar-default navbar-fixed-top" id="1096"><div class="container" id="1094"><div class="navbar-header" id="1088"><a id="1086" class="navbar-brand" href="/">Писец</a></div><div id="1092" style="text-align: left;"><ul id="leftNavbar" class="nav navbar-nav" style="float: none; display: inline-block; vertical-align: top;"><li class="dropdown" id="1082"><a href="#" class="dropdown-toggle skipClearMenus" data-toggle="dropdown" role="button" id="1078"><!-- react-text: 36 -->Проза<!-- /react-text --><span class="caret" id="1076" style="margin-left: 5px;"></span></a><ul class="dropdown-menu" id="1080"><li id="1064" class=""><a id="1062" href="why.html"><span id="1099" class="">Почему мы?</span></a></li><li id="1067" class=""><a id="1065" href="prices.html"><span id="1104" class="">Цены</span></a></li><li id="1070" class=""><a id="1068" href="faq.html"><span id="1109" class="">ЧаВо</span></a></li></ul></li><li id="1073" class="active"><a id="1071" href="profile.html"><span id="1114" class="">Профиль</span></a></li></ul><ul id="rightNavbar" class="nav navbar-nav navbar-right"><li id="1085" class=""><a id="1083" href="dashboard.html"><span id="1119" class="">Gaylord</span></a></li></ul></div></div></nav>""")
    }

    protected fun assert_navbar_Gaylord_uncool_dashboardActive() {
        o.assertNavbarHTML("""<nav data-reactroot="" class="navbar navbar-default navbar-fixed-top" id="175"><div class="container" id="173"><div class="navbar-header" id="167"><a id="165" class="navbar-brand" href="/">Писец</a></div><div id="171" style="text-align: left;"><ul id="leftNavbar" class="nav navbar-nav" style="float: none; display: inline-block; vertical-align: top;"><li class="dropdown" id="161"><a href="#" class="dropdown-toggle skipClearMenus" data-toggle="dropdown" role="button" id="157"><!-- react-text: 36 -->Проза<!-- /react-text --><span class="caret" id="155" style="margin-left: 5px;"></span></a><ul class="dropdown-menu" id="159"><li id="143" class=""><a id="141" href="why.html"><span id="178" class="">Почему мы?</span></a></li><li id="146" class=""><a id="144" href="prices.html"><span id="183" class="">Цены</span></a></li><li id="149" class=""><a id="147" href="faq.html"><span id="188" class="">ЧаВо</span></a></li></ul></li><li id="152" class=""><a id="150" href="profile.html"><span id="193" class="">Профиль</span></a></li></ul><ul id="rightNavbar" class="nav navbar-nav navbar-right"><li id="164" class="active"><a id="162" href="dashboard.html"><span id="198" class="">Gaylord</span></a></li></ul></div></div></nav>""")
    }

}

