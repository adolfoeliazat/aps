package aps.front

import aps.*

abstract class WriterBootTestScenario : BootTestScenario() {
    override val clientKind = ClientKind.WRITER
    lateinit var fuckerToken: String

    fun prepareFucker(userState: UserState): Promise<Unit> {"__async"
        __await(ImposeNextGeneratedPasswordRequest.send("fucker-secret"))

        __await(send(null, SignUpRequest() - {o ->
            o.agreeTerms.value = true
            o.signUpFields.firstName.value = "Gaylord"
            o.signUpFields.lastName.value = "Fucker"
            o.signUpFields.email.value = "fucker@test.shit.ua"
        })).orDie

        fuckerToken = __await(sendSafe(null, SignInWithPasswordRequest() - {o ->
            o.email.value = "fucker@test.shit.ua"
            o.password.value = "fucker-secret"
        })).orDie.token

        __await(TestSetUserStateRequest.send("fucker@test.shit.ua", userState))
        return __asyncResult(Unit)
    }

    fun assert_staticHomePage_rightNavbarSignIn() {
        o.assertVisibleText("Приветствуем")
        o.assertNavbarHTML("""<nav data-reactroot="" class="navbar navbar-default navbar-fixed-top" id="75"><div class="container" id="73"><div class="navbar-header" id="67"><a id="65" class="navbar-brand" href="/">Писец</a></div><div id="71" style="text-align: left;"><ul id="leftNavbar" class="nav navbar-nav" style="float: none; display: inline-block; vertical-align: top;"><li id="55" class=""><a id="53" href="why.html"><span id="78" class="">Почему мы?</span></a></li><li id="58" class=""><a id="56" href="prices.html"><span id="83" class="">Цены</span></a></li><li id="61" class=""><a id="59" href="faq.html"><span id="88" class="">ЧаВо</span></a></li></ul><ul id="rightNavbar" class="nav navbar-nav navbar-right"><li id="64" class=""><a id="62" href="sign-in.html"><span id="93" class="">Вход</span></a></li></ul></div></div></nav>""")
    }

    fun assert_staticHomePage_rightNavbarGaylord() {
        o.assertVisibleText("Приветствуем")
        assert_rightNavbarGaylord()
    }

    fun assert_rightNavbarGaylord() {
        o.assertNavbarHTML("""<nav data-reactroot="" class="navbar navbar-default navbar-fixed-top" id="138"><div class="container" id="136"><div class="navbar-header" id="130"><a id="128" class="navbar-brand" href="/">Писец</a></div><div id="134" style="text-align: left;"><ul id="leftNavbar" class="nav navbar-nav" style="float: none; display: inline-block; vertical-align: top;"><li class="dropdown" id="124"><a href="#" class="dropdown-toggle skipClearMenus" data-toggle="dropdown" role="button" id="120"><!-- react-text: 42 -->Проза<!-- /react-text --><span class="caret" id="118" style="margin-left: 5px;"></span></a><ul class="dropdown-menu" id="122"><li id="100" class=""><a id="98" href="why.html"><span id="141" class="">Почему мы?</span></a></li><li id="103" class=""><a id="101" href="prices.html"><span id="146" class="">Цены</span></a></li><li id="106" class=""><a id="104" href="faq.html"><span id="151" class="">ЧаВо</span></a></li></ul></li><li id="109" class=""><a id="107" href="orders.html"><span id="156" class="">Мои заказы</span></a></li><li id="112" class=""><a id="110" href="store.html"><span id="161" class="">Аукцион</span></a></li><li id="115" class=""><a id="113" href="profile.html"><span id="166" class="">Профиль</span></a></li></ul><ul id="rightNavbar" class="nav navbar-nav navbar-right"><li id="127" class=""><a id="125" href="dashboard.html"><span id="171" class="">Gaylord</span></a></li></ul></div></div></nav>""")
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

}

