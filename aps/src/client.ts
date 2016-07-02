/*
 * APS
 * 
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

#pragma instrument-ui-rendering

MODE = 'debug'
DEBUG_SIMULATE_SLOW_NETWORK = true
BOOTSTRAP_VERSION = 3
BACKEND_URL = 'http://localhost:3100'

require('regenerator-runtime/runtime') // TODO:vgrechka Get rid of this shit, as I don't want to support old browsers anyway

import * as url from 'url'
import * as querystring from 'querystring'
import '../gen/client-expectations'
import static 'into-u/utils-client into-u/ui ./stuff'

let debugShitInitialized, currentTestScenarioName, preventRestoringURLAfterTest, assertionErrorPane, debugStatusBar

global.initUI = async function(opts) {
    const navLinkNames = tokens('right orders support')
        
    // @ctx state
    const _t = makeT(LANG)
    let urlObject, urlQuery, updateReactShit, rootContent, pageState, rpcclient, signedUpOK, user, activePage
    let updateCurrentPage
    let testScenarioToRun
    
    if (MODE === 'debug' && !debugShitInitialized) {
        await initClientStackSourceMapConsumer()
        setUIDebugRPC(rpc)
        window.addEventListener('keydown', e => {
            if (e.ctrlKey && e.altKey && e.key === 'k') return captureState()
            if (e.ctrlKey && e.altKey && e.key === 'i') return captureInputs()
            if (e.ctrlKey && e.altKey && e.key === 'a') return assertUIState({$tag: 'just-showing-actual', expected: undefined})
        })
        
        assertionErrorPane = statefulElement(update => {
            let visible, content, top
            
            return {
                render() {
                    if (!visible) return null
                    return diva({style: {position: 'absolute', left: 0, top, width: '100%', backgroundColor: RED_700, padding: '10px 10px', textAlign: 'left'}},
                               divsa({fontWeight: 'bold', borderBottom: '2px solid white', paddingBottom: 5, marginBottom: 5, color: WHITE}, content.message),
                               divsa({whiteSpace: 'pre-wrap', color: WHITE}, content.stack),
                               content.detailsUI,
                           )
                },
                
                set(_content) {
                    top = $('#footer').offset().top + 40
                    update(content = _content, visible = true)
                    document.body.scrollTop = 99999
                },
            }
        })
        
        debugStatusBar = statefulElement(update => {
            let functions = []
            let screenSize
            
            var mqLarge = window.matchMedia('(min-width: 1200px)')
            var mqMedium = window.matchMedia('(min-width: 992px)')
            var mqSmall = window.matchMedia('(min-width: 768px)')
            mqLarge.addListener(onScreenSizeChange)
            mqMedium.addListener(onScreenSizeChange)
            mqSmall.addListener(onScreenSizeChange)
            onScreenSizeChange()
            
            function onScreenSizeChange() {
                if (window.matchMedia('(min-width: 1200px)').matches) {
                    update(screenSize = 'Large')
                } else if (window.matchMedia('(min-width: 992px)').matches) {
                    update(screenSize = 'Medium')
                } else if (window.matchMedia('(min-width: 768px)').matches) {
                    update(screenSize = 'Small')
                } else {
                    update(screenSize = 'Weird screen')
                }
            }
            
            return {
                render() {
                    return diva({style: {position: 'absolute', display: 'flex', right: 0, bottom: 0, height: 28, zIndex: 1000}},
                        ...functions.map(({title, theme='default', action}) => {
                            const style = {marginLeft: 3, height: '100%', padding: '0 5px', paddingTop: 7, fontSize: '85%', cursor: 'pointer'}
                            if (theme === 'default') {
                                asn(style, {backgroundColor: BLUE_GRAY_500, color: WHITE})
                            } else if (theme === 'danger') {
                                asn(style, {backgroundColor: RED_500, color: WHITE})
                            } else if (theme === 'black') {
                                asn(style, {backgroundColor: BLACK, color: WHITE})
                            }
                            return diva({style, onClick: action}, title)
                        }),
                            
                        diva({style: {marginLeft: 3, height: '100%', padding: '0 5px', paddingTop: 7, backgroundColor: BLUE_GRAY_500, color: WHITE, fontSize: '85%'}},
                            screenSize),
                    )
                },
                
                setFunctions(x) {
                    functions = x
                    functions.unshift({
                        title: t('Q'),
                        theme: 'black',
                        action() {
                            revealer.reveal(updatableElement(update => {
                                let ui
                                let loading = true
                                
                                run(async function() {
                                    update(ui = spinnerMedium())
                                    try {
                                        const queries = await rpc({fun: 'danger_getQueries', last: 5})
                                        // dlogs({queries})
                                        if (!queries.length) return update(ui = div('No queries'))
                                        const divs = []
                                        queries.forEach((query, i) => {
                                            if (i > 0) {
                                                divs.push(diva({style: {marginTop: 5, marginBottom: 5, height: 2, backgroundColor: GRAY_500}}))
                                            }
                                            if (query.$tag) {
                                                divs.push(diva({}, OpenSourceCodeLink(query)))
                                            }
                                            let descr = deepInspect({sql: query.sql, args: query.args})
                                            divs.push(diva({style: {space: 'pre-wrap', fontFamily: 'monospace'}}, descr))
                                        })
                                        update(ui = div(...divs))
                                    } catch (e) {
                                       update(ui = spansa({color: RED_700}, glyph('exclamation-triangle'), spansa({marginLeft: 10}, 'Shit: ' + e.message)))
                                    }
                                })
                                
                                return _=> ui
                            }))
                        }
                    })
                    
                    update()
                },
            }
        })
        
        $(document.body).append('<div id="debugShit"></div>')
        ReactDOM.render(updatableElement(update => {
            updateReactShit = update
            return _=> div(assertionErrorPane, capturePane, debugStatusBar)
        }), byid0('debugShit'))
        
        urlObject = url.parse(location.href)
        urlQuery = querystring.parse(urlObject.query)
        testScenarioToRun = urlQuery.testScenario
        
        debugShitInitialized = true
    }
    
    spaifyNavbar()
    
    ReactDOM.render(updatableElement(update => {
        updateReactShit = update
        return _=> div(rootContent)
    }), byid0('root'))
    
    window.onpopstate = function(e) {
        showWhatsInURL()
    }
    
    const userJSON = localStorage.getItem('user') // TODO:vgrechka This can throw (according to MDN), should handle
    if (userJSON) {
        user = JSON.parse(userJSON)
    }
    
    if (testScenarioToRun) {
        let scenarioNameOK
        if (LANG == 'ua' && CLIENT_KIND === 'customer' && testScenarioToRun.startsWith('UA Customer :: ')) scenarioNameOK = true
        if (LANG == 'ua' && CLIENT_KIND === 'writer' && testScenarioToRun.startsWith('UA Writer :: ')) scenarioNameOK = true
        if (!scenarioNameOK) raise(`Bad scenario name for ${LANG} ${CLIENT_KIND} client: ${testScenarioToRun}`)
        
        window.locationProxy = {
            set href(x) {
                history.replaceState(null, '', x)
            }
        }
        
        const initialPath = location.pathname + location.search
        try {
            currentTestScenarioName = testScenarioToRun
            await testScenarios()[testScenarioToRun]()
        } finally {
            currentTestScenarioName = undefined
            if (!preventRestoringURLAfterTest) {
                history.replaceState(null, '', initialPath)
            }
        }
        
        $(document.body).append(`
            <div id="uiTestPassedBanner" style="
                position: absolute;
                bottom: 0px;
                width: 100%;
                background-color: ${GREEN_700};
                color: ${WHITE};
                padding: 10px 10px;
                text-align: center;
                font-weight: bold;
            "></div>
        `)
        $('#uiTestPassedBanner').text(testScenarioToRun)
    } else {
        showWhatsInURL()
    }
    
    
    function spaifyNavbar() {
        for (const name of navLinkNames) {
            const link = byid0(name + 'NavLink')
            if (link) {
                link.onclick = e => {
                    e.preventDefault()
                    pushNavigate(link.getAttribute('href'))
                }
            }
        }
    }
    
    function t(meta, ...args) {
        return {meta, meat: _t(...args)}
    }
    
    function showWhatsInURL() {
        urlObject = url.parse(location.href)
        urlQuery = querystring.parse(urlObject.query)
        const path = document.location.pathname
        
        let shower, activeNavLink
        
        if (path.endsWith('/sign-in.html')) {
            shower = showSignIn
            activeNavLink = 'right'
        } else if (path.endsWith('/sign-up.html')) {
            shower = showSignUp
            activeNavLink = 'right'
        } else if (user) {
            if (path.endsWith('/orders.html')) {
                activeNavLink = 'orders'
                shower = showOrders
            } else if (path.endsWith('/support.html')) {
                activeNavLink = 'support'
                shower = showSupport
            } else if (path.endsWith('/dashboard.html')) {
                activeNavLink = 'right'
                shower = showDashboard
            }
        }
        
        if (!shower) raise(`Can’t determine fucking shower for path ${path}`)
        if (!activeNavLink) raise(`Can’t determine fucking activeNavLink for path ${path}`)
        
        for (const name of navLinkNames) {
            const link = byid(name + 'NavLink')
            if (link.length) {
                const li = link.parent()
                li.removeClass('active')
                if (name === activeNavLink) {
                    li.addClass('active')
                }
            }
        }
        
        shower()
    }
    
    function showSupport() {
        setPage({
            pageTitle: t('Support', 'Служба поддержки'),
            pageBody: div(
                )
        })
    }
    
    function showOrders() {
        setPage({
            pageTitle: t('My Orders', 'Мои заказы'),
            pageBody: div(
                )
        })
    }
    
    function showDashboard() {
        setPage({
            pageTitle: t('Dashboard', 'Панель'),
            pageBody: div(
                diva({className: 'row'},
                    diva({className: 'col-sm-6'},
                        sectionTitle(t('Account', 'Аккаунт')),
                        sectionLinks(
                            [t('Sign out', 'Выйти прочь'), _=> {
                                localStorage.removeItem('user')
                                user = undefined
                                location.href = '/'
                            }],
                            [t('Change password', 'Сменить пароль'), _=> {
                                dlog('implement change password')
                            }]
                        )))
                )
        })
        
        
        function sectionTitle(title) {
            return diva({style: {backgroundColor: BLUE_GRAY_50, fontWeight: 'bold', padding: '2px 5px', marginBottom: 10}}, title)
        }
        
        function sectionLinks(...items) {
            return ula({className: 'fa-ul', style: {marginLeft: 20}},
                       ...items.map(([itemTitle, action]) =>
                           lia({style: {marginBottom: 5}},
                               ia({className: 'fa fa-li fa-chevron-right', style: {color: BLUE_GRAY_500}}),
                               link(itemTitle, {style: {color: '#333'}}, action))))
        }
    }
    
    function showSignIn() {
        const form = Form({
            primaryButtonTitle: t('Sign In', 'Войти'),
            fields: {
                email: {
                    title: t('Email', 'Почта'),
                    type: 'text',
                    attrs: {autoFocus: true},
                },
                password: {
                    title: t('Password', 'Пароль'),
                    type: 'password',
                },
            },
            rpcFun: 'signIn',
            onSuccess(res) {
                user = res.user
                localStorage.setItem('user', JSON.stringify(user))
                initUI0()
                spaifyNavbar()
                pushNavigate('dashboard.html')
            },
        })
        
        setPage({
            pageTitle: t('Sign In', 'Вход'),
            pageBody: div(
                signedUpOK && preludeWithCheck(
                    t('Cool. You have an account now. We sent you email with password.',
                      'Все круто. Теперь у тебя есть аккаунт. Пароль мы отправили письмом.'),
                    {center: 720}),
                    
                form,
                               
                !signedUpOK && div(
                    hr(),
                    divsa({textAlign: 'left'}, link(t('Still don’t have an account? Create one!', 'Как? Еще нет аккаунта? Срочно создать!'), {name: 'createAccount'}, _=> {
                        pushNavigate('sign-up.html')
                    })))
                )
        })
        
        //------------------------------ Sign in debug functions ------------------------------
        
        const debugFuns = [
            {
                title: t('F1'),
                action() {
                    if (CLIENT_KIND === 'customer') {
                        testGlobal.inputs.email.value = 'wilma.blue@test.shit.ua'
                        testGlobal.inputs.password.value = '63b2439c-bf18-42c5-9f7a-42d7357f966a'
                    } else if (CLIENT_KIND === 'writer') {
                        testGlobal.inputs.email.value = 'fred.red@test.shit.ua'
                        testGlobal.inputs.password.value = 'b34b80fb-ae50-4456-8557-399366fe45e4'
                    }
                    
                    testGlobal.buttons.primary.click()
                }
            }
        ]
        if (CLIENT_KIND === 'writer') {
            debugFuns.push({
                title: t('dasja'),
                action() {
                    testGlobal.inputs.email.value = 'dasja@test.shit.ua'
                    testGlobal.inputs.password.value = 'adminsecret'
                    testGlobal.buttons.primary.click()
                }
            })
            debugFuns.push({
                title: t('joe'),
                action() {
                    testGlobal.inputs.email.value = 'joe.average@test.shit.ua'
                    testGlobal.inputs.password.value = '5fca502f-73e2-4c3d-89c8-bc3dabf434d6'
                    testGlobal.buttons.primary.click()
                }
            })
        }
        debugStatusBar.setFunctions(debugFuns)
    }
    
    function preludeWithCheck(content, {center}={}) {
        const style = {}
        if (center) {
            asn(style, {width: center, margin: '0 auto'})
        }
        asn(style, {marginBottom: 15})
        
        return diva({style},
                    glyph('check', {style: {color: LIGHT_GREEN_700}}),
                    nbsp, nbsp,
                    content)
    }
    
    function showSignUp() {
        const form = Form({
            primaryButtonTitle: t('Proceed', 'Вперед'),
            fields: {
                email: {
                    title: t('Email', 'Почта'),
                    type: 'text',
                    attrs: {autoFocus: true},
                },
                firstName: {
                    title: t('First Name', 'Имя'),
                    type: 'text',
                },
                lastName: {
                    title: t('Last Name', 'Фамилия'),
                    type: 'text',
                },
                agreeTerms: {
                    type: 'agreeTerms'
                },
            },
            rpcFun: 'signUp',
            onSuccess(res) {
                signedUpOK = true
                pushNavigate('sign-in.html')
            },
        })
        
        setPage({
            pageTitle: t('Sign Up', 'Регистрация'),
            pageBody: div(
                form,
                               
                div(
                    hr(),
                    divsa({textAlign: 'left'}, link(t('Already have an account? Sign in here.', 'Уже есть аккаунт? Тогда входим сюда.'), _=> {
                        pushNavigate('sign-in.html')
                    })))
                )
        })
        
        //------------------------------ Sign up debug functions ------------------------------
        
        const debugFuns = []
        if (CLIENT_KIND === 'writer') {
            debugFuns.push({
                title: t('joe'),
                async action() {
                    await rpc({fun: 'danger_killUser', email: 'joe.average@test.shit.ua'})
                    await rpc({fun: 'danger_fixNextGeneratedPassword', password: '5fca502f-73e2-4c3d-89c8-bc3dabf434d6'})
                    testGlobal.inputs.email.value = 'joe.average@test.shit.ua'
                    testGlobal.inputs.firstName.value = 'Джо'
                    testGlobal.inputs.lastName.value = 'Авераж'
                    testGlobal.inputs.agreeTerms.value = true
                    testGlobal.buttons.primary.click()
                }
            })
        }
        debugStatusBar.setFunctions(debugFuns)
    }
    
    function pushNavigate(where) {
        history.pushState(null, '', where)
        showWhatsInURL()
    }
    
    function setPage(def) {
        updateReactShit(rootContent = updatableElement(update => {
            updateCurrentPage = update
            
            pageState = {
                pageBody: def.pageBody
            }
            
            return _=> diva({className: 'container', style: {position: 'relative'}},
                responsivePageHeader(fov(def.pageTitle)),
                pageState.headerShitSpins && diva({style: {position: 'absolute', right: 0, top: 0}}, spinnerMedium({name: 'headerShit'})),
                pageState.error && errorBanner(pageState.error),
                pageState.pageBody,
            )
        }))
        debugStatusBar.setFunctions([])
    }
    
    function Form(def) {
        return updatableElement(update => {
            let working, error
            
            for (const [name, field] of toPairs(def.fields)) {
                field.attrs = field.attrs || {}
                field.attrs.id = field.attrs.id || 'field-' + name // TODO:vgrechka @kill
                
                if (field.type === 'text') {
                    const input = Input({
                        testName: name,
                        volatileStyle() {
                            if (impl.error) return {paddingRight: 30}
                        }
                    })
                    const impl = genericFieldImpl(field, input)
                    field.control = _=> divsa({position: 'relative'},
                        input,
                        impl.error && errorLabel(impl.error, {testName: name, style: {marginTop: 5, marginRight: 9, textAlign: 'right'}}),
                        impl.error && divsa({width: 15, height: 15, backgroundColor: RED_300, borderRadius: 10, position: 'absolute', right: 8, top: 10}))
                } else if (field.type === 'password') {
                    field.control = Input(asn({type: 'password', testName: name}, field.attrs))
                } else if (field.type === 'agreeTerms') {
                    const checkbox = Checkbox({testName: name})
                    const impl = genericFieldImpl(field, checkbox)
                    field.control = _=> div(
                        divsa({display: 'flex'},
                            checkbox,
                            divsa({width: 5}),
                            div(t('I’ve read and agreed with ', 'Я прочитал и принял '), link(t('terms and conditions', 'соглашение'), popupTerms)),
                            impl.error && divsa({width: 15, height: 15, borderRadius: 10, marginTop: 3, marginRight: 9, marginLeft: 'auto', backgroundColor: RED_300})),
                        impl.error && errorLabel(impl.error, {style: {marginTop: 5, marginRight: 9, textAlign: 'right'}}))
                           
                    function popupTerms() {
                        alert('terms here')
                    }
                } else {
                    raiseInspect('WTF is the field', field)
                }
                
                field.getValue = field.getValue || (_=> field.control.value)
                field.setValue = field.setValue || (x => field.control.value = x)
                field.setDisabled = field.setDisabled || (x => field.control.disabled = x)
                field.setError = field.setError || (x => field.control.error = x)
                
                if (field.titleControl === undefined && field.title) {
                    field.titleControl = label(field.title)
                }
                
                window['simulate_setControlValue_' + name] = function(x) { // TODO:vgrechka Use testGlobal.inputs
                    field.setValue(x)
                }
                
                
                function genericFieldImpl(field, to) {
                    field.getValue = _=> to.value
                    field.setValue = x => to.value = x
                    field.setDisabled = x => to.disabled = x
                    field.setError = x => impl.error = x
                    
                    const impl = {}
                    return impl
                }
            }
            
            return _=> div(
                formsa({width: 720, margin: '0 auto'},
                    error && errorBanner(error),
                    ...values(def.fields).map(field => {
                        return diva({className: 'form-group'},
                                    field.titleControl,
                                    field.control)
                    }),
                    divsa({textAlign: 'left'},
                        button.primary({title: def.primaryButtonTitle, disabled: working, testName: 'primary'}, async function() {
                            for (const [name, field] of toPairs(def.fields)) {
                                field.setError(undefined)
                                field.setDisabled(true)
                            }
                            error = undefined
                            working = true
                            update()
                            
                            const res = await rpcSoft(asn({fun: def.rpcFun}, omapo(def.fields, x => x.getValue())))
                            
                            if (res.error) {
                                error = res.error
                            } else {
                                error = undefined
                                def.onSuccess(res)
                            }
                            
                            working = false
                            for (const [name, field] of toPairs(def.fields)) {
                                field.setError(res.fieldErrors && res.fieldErrors[name])
                                field.setDisabled(false)
                            }
                            update()
                        }),
                        working && divsa({float: 'right'}, spinnerMedium())),
                ))
        })
    }
    
    
    function DashboardPage() {
        return updatableElement(update => {
            return _=> div(
                pageHeader(t('Dashboard', 'Панель')),
                div('i am dashboard'))
        })
    }
    
    async function rpcSoft(message) {
        try {
            return await rpc(message)
        } catch (e) {
            if (MODE === 'debug') {
                let resp = rpcclient.lastResponse
                if (resp.body.stack) {
                    debugStatusBar.setFunctions([{
                        title: t('BS'),
                        theme: 'danger',
                        action() {
                            revealer.revealStack(resp.body.stack, resp.body.stackBeforeAwait)
                        }
                    }])
                }
            }
            return {error: t('Sorry, service is temporarily unavailable', 'Извините, сервис временно недоступен')}
        }
    }

    async function rpc(message) {
        if (!rpcclient) {
            rpcclient = RPCClient({url: `${BACKEND_URL}/rpc`})
        }
        return await rpcclient.call(
            asn({LANG, CLIENT_KIND, APS_DANGEROUS_TOKEN, isTesting: !!currentTestScenarioName}, message),
            {slowNetworkSimulationDelay: MODE === 'debug'
                                         && DEBUG_SIMULATE_SLOW_NETWORK
                                         && !message.fun.startsWith('danger_')
                                         && (currentTestScenarioName ? 250 : 1000)}
        )
    }
    
    function testScenarios() {return{
        async 'Something'() {
        },
    
        // ======================================== UA CUSTOMER TEST SCENARIOS ========================================
        
        async 'UA Customer :: Sign Up :: 1'() {
            await rpc({fun: 'danger_clearSentEmails'})
            await rpc({fun: 'danger_killUser', email: 'wilma.blue@test.shit.ua'})
            await rpc({fun: 'danger_fixNextGeneratedPassword', password: '63b2439c-bf18-42c5-9f7a-42d7357f966a'})
            
            simulateURLNavigation('dashboard.html')
            assertUIState({$tag: '62112552-36ac-47fd-9bac-a4d6a7b3c4d4', expected: {
                url: `http://aps-ua-customer.local:3012/sign-in.html`,
                pageHeader: `Вход`,
                inputs: { email: { value: `` }, password: { value: `` } },
                errorLabels: {},
                errorBanner: undefined 
            }})           
                        
            simulateURLNavigation('sign-up.html')
            assertUIState({$tag: '6aa1c1bf-804b-4f5c-98e5-c081cd6238a0', expected: {
                url: `http://aps-ua-customer.local:3012/sign-up.html`,
                pageHeader: `Регистрация`,
                inputs: 
                 { email: { value: `` },
                   firstName: { value: `` },
                   lastName: { value: `` },
                   agreeTerms: { value: false } },
                errorLabels: {},
                errorBanner: undefined 
            }})

            // Inputs
            testGlobal.inputs.email.value = 'wilma.blue@test.shit.ua'
            testGlobal.inputs.firstName.value = 'Вильма'
            testGlobal.inputs.lastName.value = 'Блу'
            testGlobal.inputs.agreeTerms.value = true
            // Action
            testGlobal.buttons.primary.click()
            await assertShitSpinsForMax({$tag: '29832372-ff89-46dd-ba9d-cf54154503f5', max: 2000})
            // Check
            assertTextSomewhere({$tag: '853610e2-c607-4ce5-9d60-74744ca63580', expected: 'Все круто. Теперь у тебя есть аккаунт. Пароль мы отправили письмом.'})
            assertUIState({$tag: '361d46a0-6ec1-40c4-a683-bc5263c41bba', expected: {
                url: `http://aps-ua-customer.local:3012/sign-in.html`,
                pageHeader: `Вход`,
                inputs: { email: { value: `` }, password: { value: `` } },
                errorLabels: {},
                errorBanner: undefined 
            }})
            await assertSentEmails({$tag: '169a6331-c004-47fd-9b53-05242915d9f7', descr: 'Email with password', expected: [
                { to: `Вильма Блу <wilma.blue@test.shit.ua>`,
                    subject: `Пароль для APS`,
                    html: dedent(`
                        Привет, Вильма!<br><br>
                        Вот твой пароль: 63b2439c-bf18-42c5-9f7a-42d7357f966a
                        <br><br>
                        <a href="http://aps-ua-customer.local:3012/sign-in.html">http://aps-ua-customer.local:3012/sign-in.html</a>`) } 
            ]})
            
            // Inputs
            testGlobal.inputs.email.value = 'wilma.blue@test.shit.ua'
            testGlobal.inputs.password.value = '63b2439c-bf18-42c5-9f7a-42d7357f966a'
            // Action
            testGlobal.buttons.primary.click()
            await assertShitSpinsForMax({$tag: '96f4aa5d-4f5d-4de4-869b-07f2f6f53b8b', max: 2000})
            assertLinkWithTextSomewhere({$tag: 'aa6eda4b-fc78-43ac-959d-a2eb44f3061f', expected: 'Вильма'})
            assertUIState({$tag: 'd9c42d17-322e-4427-b4ec-d946af422ba0', expected: {
                url: `http://aps-ua-customer.local:3012/dashboard.html`,
                pageHeader: `Панель`,
                inputs: {},
                errorLabels: {},
                errorBanner: undefined 
            }})
            
            simulateURLNavigation('dashboard.html')
        },
        
        // ======================================== UA WRITER TEST SCENARIOS ========================================
        
        async 'UA Writer :: Sign Up :: 1'() {
            await rpc({fun: 'danger_clearSentEmails'})
            await rpc({fun: 'danger_killUser', email: 'fred.red@test.shit.ua'})
            await rpc({fun: 'danger_fixNextGeneratedPassword', password: 'b34b80fb-ae50-4456-8557-399366fe45e4'})
            
            simulateURLNavigation('dashboard.html')
            assertUIState({$tag: '20059334-7dff-4922-8bf5-ac07999d892d', expected: {
                url: `http://aps-ua-writer.local:3022/sign-in.html`,
                pageHeader: `Вход`,
                inputs: { email: { value: `` }, password: { value: `` } },
                errorLabels: {},
                errorBanner: undefined 
            }})
            
            testGlobal.links.createAccount.click()
            assertUIState({$tag: 'b1a53c66-21db-42e5-8b0b-4d430b7b4ea6', expected: {
                url: `http://aps-ua-writer.local:3022/sign-up.html`,
                pageHeader: `Регистрация`,
                inputs: 
                 { email: { value: `` },
                   firstName: { value: `` },
                   lastName: { value: `` },
                   agreeTerms: { value: false } },
                errorLabels: {},
                errorBanner: undefined 
            }})            
            
            // Inputs
            testGlobal.inputs.email.value = 'fred.red@test.shit.ua'
            testGlobal.inputs.firstName.value = 'Фред'
            testGlobal.inputs.lastName.value = 'Ред'
            testGlobal.inputs.agreeTerms.value = true
            // Action
            testGlobal.buttons.primary.click()
            await assertShitSpinsForMax({$tag: '39df3f4b-5ca0-4929-bae7-ec1d3bd008ed', max: 2000})
            
            await assertSentEmails({$tag: '024f202c-ee75-44ed-ac26-44154d4caf13', descr: 'Email with password', expected: [
                { to: `Фред Ред <fred.red@test.shit.ua>`,
                    subject: `Пароль для Writer UA`,
                    html: dedent(`
                        Привет, Фред!<br><br>
                        Вот твой пароль: b34b80fb-ae50-4456-8557-399366fe45e4
                        <br><br>
                        <a href="http://aps-ua-writer.local:3022/sign-in.html">http://aps-ua-writer.local:3022/sign-in.html</a>`) } 
            ]})
            assertUIState({$tag: '24ca0059-e2e9-4fc4-9056-ede17e586029', expected: {
                url: `http://aps-ua-writer.local:3022/sign-in.html`,
                pageHeader: `Вход`,
                inputs: { email: { value: `` }, password: { value: `` } },
                errorLabels: {},
                errorBanner: undefined 
            }})            
            assertTextSomewhere({$tag: 'bad7019b-a1d3-432c-a376-a872f5b27506', expected: 'Все круто. Теперь у тебя есть аккаунт. Пароль мы отправили письмом.'})

            // Inputs
            testGlobal.inputs.email.value = 'fred.red@test.shit.ua'
            testGlobal.inputs.password.value = 'b34b80fb-ae50-4456-8557-399366fe45e4'
            // Action
            testGlobal.buttons.primary.click()
            await assertShitSpinsForMax({$tag: 'd880053c-0f24-46ec-8c47-c635e91d6a39', max: 2000})

            assertUIState({$tag: '4d88eed7-d800-4a00-bfea-6b011329eaf0', expected: {
                url: `http://aps-ua-writer.local:3022/dashboard.html`,
                pageHeader: `Панель`,
                inputs: {},
                errorLabels: {},
                errorBanner: undefined 
            }})                        
            assertTextSomewhere({$tag: 'bad7019b-a1d3-432c-a376-a872f5b27506', expected: 'Фред'})
        },
        
// preventRestoringURLAfterTest = true
// failForJumping('Implement me', '182853f7-c8ee-41b9-b45f-d52636f9a154')
        
    }}
    
    function assertLinkWithTextSomewhere({$tag, expected}) {
        for (const a of $('a')) {
            if (a.text === expected) return
        }
        uiAssert(false, `I want a link with following text somewhere: [${expected}]`, jumpToShitDetailsUI($tag))
    }
    
    function assertHref({$tag, expected}) {
        uiAssert(document.location.href === expected, `I want to be at following URL: [${expected}]`, jumpToShitDetailsUI($tag))
    }
            
    function assertUIState(def) {
        const actual = {
            url: location.href,
            pageHeader: testGlobal.pageHeader,
            inputs: omapo(testGlobal.inputs, x => x.capture()),
            errorLabels: testGlobal.errorLabels,
            errorBanner: testGlobal.errorBanner,
        }
        assertRenameme(asn(def, {actual}))
    }
    
    async function assertSentEmails(def) {
        const actual = await rpc({fun: 'danger_getSentEmails'})
        assertRenameme(asn(def, {actual}))
    }
    
    function failForJumping(message, $tag) {
        uiAssert(false, message, jumpToShitDetailsUI($tag))
    }
    
    function assertRenameme({descr='Describe me', $tag, actual, expected}) {
        if (expected === undefined) {
            expected = EXPECTATIONS[$tag] || '--- not yet hardened ---'
        }
        if (deepEquals(actual, expected)) return
        
        sortKeys(actual) // Order of keys sent over the wire is mangled
        sortKeys(expected)
        const actualString = repr(actual)
        const expectedString = repr(expected)
        const diffLineItems = require('diff').diffLines(expectedString, actualString)
        const diffDivs = []
        let prevLabel
        for (const item of diffLineItems) {
            let backgroundColor, label
            if (item.added) {
                backgroundColor = RED_100
                label = t('Actual')
            } else if (item.removed) {
                backgroundColor = GREEN_100
                label = t('Expected')
            } else {
                backgroundColor = WHITE
                label = undefined
            }
            if (label && label !== prevLabel) {
                diffDivs.push(divsa({backgroundColor, fontWeight: 'bold'}, label))
            }
            prevLabel = label
            diffDivs.push(divsa({backgroundColor, position: 'relative'},
                item.value))
        }
        
        uiAssert(false, descr, {detailsUI: updatableElement(update => {
            const my = {}
            
            let actualStringForPasting = actualString.trim()
            if (actualStringForPasting[0] === '{'/*}*/ || actualStringForPasting[0] === '['/*]*/) {
                actualStringForPasting = actualStringForPasting.slice(1, actualStringForPasting.length - 1)
            }
            const chars = actualStringForPasting.split('')
            for (let i = 0; i < chars.length; ++i) {
                if (chars[i] === "'" && (i === 0 || chars[i - 1] !== '\\')) {
                    chars[i] = '`'
                }
            }
            actualStringForPasting = chars.join('')
            const replacements = []
            let backtickIndex, from = 0, btis = []
            while (~(backtickIndex = actualStringForPasting.indexOf('`', from))) {
                btis.push(backtickIndex)
                if (btis.length === 2) {
                    let literal = actualStringForPasting.slice(btis[0], btis[1] + 1)
                    if (/\r|\n/.test(literal)) {
                        literal = literal[0] + '\n' + literal.slice(1)
                        literal = literal.replace(/(\r?\n)/g, '$1        ')
                        literal = `dedent(${literal})`
                        replacements.push({from: btis[0], oldStringLength: btis[1] - btis[0] + 1, newString: literal})
                    }
                    btis = []
                }
                from = backtickIndex + 1
            }
            replacements = sortBy(replacements, 'from')
            let newActualStringForPasting = ''; from = 0
            for (const replacement of replacements) {
                newActualStringForPasting += actualStringForPasting.slice(from, replacement.from) + replacement.newString
                from = replacement.from + replacement.oldStringLength
            }
            newActualStringForPasting += actualStringForPasting.slice(from)
            actualStringForPasting = newActualStringForPasting
            actualStringForPasting += '\n'
            
            const tabs = Tabs({
                tabs: {
                    diff: {
                        title: t('Difference'),
                        content: divsa({whiteSpace: 'pre-wrap'}, ...diffDivs),
                    },
                    actual: {
                        title: t('Actual'),
                        content: divsa({whiteSpace: 'pre-wrap'}, Input({initialValue: actualStringForPasting, kind: 'textarea', rows: 10, style: {width: '100%', height: '100%'}})),
                    },
                    expected: {
                        title: t('Expected'),
                        content: divsa({whiteSpace: 'pre-wrap'}, expectedString),
                    },
                }
            })
            return _=> divsa({marginTop: 5, padding: 5, backgroundColor: WHITE, position: 'relative'},
                $tag && divsa({position: 'absolute', right: 5, top: 5},
//                    my.ueb = my.ueb || WorkButton({title: t('Update Expectation'), level: 'primary', glyph: 'pencil', async work() {
//                        try {
//                            await rpc({fun: 'danger_updateExpectation', aid: $tag, actual})
//                            my.ueb = divsa({fontWeight: 'bold'}, 'Expectation updated')
//                        } catch (e) {
//                            console.error(e)
//                            my.ueb = divsa({color: RED_700, fontWeight: 'bold'}, 'Expectation update fucked up')
//                        }
//                        update()
//                    }})
                ),
                horiza({style: {marginBottom: 5}},
                    spana({$testme: true, style: {fontWeight: 'bold'}}, t('Assertion ID: ')),
                    my.codeLink = my.codeLink || OpenSourceCodeLink({$tag})),
                divsa({fontSize: '100%'},
                    tabs))
        })})
        
        
        function sortKeys(o) {
            if (isArray(o)) {
                o.filter(isObject).forEach(sortKeys)
            } else if (isObject(o)) {
                const pairs = toPairs(o)
                sortBy(pairs, x => x[0])
                pairs.forEach(([k, v]) => delete o[k])
                pairs.forEach(([k, v]) => {
                    o[k] = v
                    if (isObject(v)) {
                        sortKeys(v)
                    }
                })
            }
        }
        
        function repr(it) {
            let s = deepInspect(it)
            s = s.replace(/\\n/g, '\n')
            return s
        }
    }

    function assertTextSomewhere({$tag, expected}) {
        uiAssert(~$(document.body).text().indexOf(expected), `I want following text on screen: [${expected}]`, jumpToShitDetailsUI($tag))
    }
    
    function jumpToShitDetailsUI($tag) {
        return {
            detailsUI: updatableElement(update => {
                const link = OpenSourceCodeLink({$tag})
                return _=> divsa({marginTop: 5, padding: 5, backgroundColor: WHITE, position: 'relative'},
                               div(horiz(t('Jump and fix that shit: '), link)))
            })
        } 
    }

    function assertErrorBanner(expected) {
        uiAssert(testGlobal.errorBanner === expected, `I want error banner [${expected}]`)
    }

    function assertNoErrorBanner() {
        uiAssert(testGlobal.errorBanner === undefined, `I don't want an error banner hanging here`)
    }


    function assertNoErrorLabels() {
        uiAssert(isEmpty(testGlobal.errorLabels), `I don't want any error labels here`)
    }

    function assertErrorLabelTitlesExactly(...expected) {
        const actual = values(testGlobal.errorLabels).map(x => x.title)
        const expectedDescr = expected.map(x => `[${x}]`).join(', ')
        uiAssert(deepEquals(sortBy(expected), sortBy(actual)), `I want exactly following error labels: ${expectedDescr}`)
    }


    function assertErrorLabelTitle(expectedTitle) {
        uiAssert(ofind(testGlobal.errorLabels, x => x.title === expectedTitle), `I want error label [${expectedTitle}] on screen`)
    }

    function assertFail() {
        uiAssert(false, 'Just failing here, OK?')
    }
    
    async function assertSpinsForMax({$tag, name, max}) {
        assertSpins({$tag, name})
        
        const t0 = Date.now()
        while (Date.now() - t0 < max) {
            if (!testGlobal[name + 'Spins']) return
            await delay(50)
        }
        
        uiAssert(false, `I expected ${name} to stop spinning in ${max}ms`)
    }

    function assertSpins({$tag, name}) {
        uiAssert(testGlobal[name + 'Spins'], `I want ${name} to be spinning`)
    }

    async function assertShitSpinsForMax({$tag, name, max}) {
        await assertSpinsForMax({$tag, name: 'shit', max})
    }

    function assertShitSpins({$tag}) {
        assertSpins({name: 'shit'})
    }
    
    async function assertHeaderShitSpinsForMax({$tag, max}) {
        await assertSpinsForMax({$tag, name: 'headerShit', max})
    }

    function assertHeaderShitSpins() {
        assertSpins({name: 'headerShit'})
    }
    
    function uiFail(errorMessage) {
        uiAssert(false, errorMessage)
    }
    
    function uiAssert(condition, errorMessage, {detailsUI}={}) {
        if (condition) return
        
        assertionErrorPane.set({message: errorMessage + mdash + currentTestScenarioName, stack: clientStack().join('\n'), detailsUI})
        raise('UI assertion failed')
    }

    function simulateURLNavigation(url) {
        history.replaceState(null, '', url)
        initUI0()
        initUI()
    }

    function simulatePopulateFields(data) {
        for (const [name, value] of toPairs(data)) {
            const functionName = 'simulate_setControlValue_' + name
            const setValue = window[functionName]
            if (!setValue) raise('I want function ' + functionName)
            setValue(value)
        }
    }

    // TODO:vgrechka Use testGlobal.buttons[...].click()
    function simulateClick(name) {
        const functionName = 'simulate_click_' + name
        const click = window[functionName]
        if (!click) raise('I want function ' + functionName)
        click()
    }


    function responsivePageHeader(title) {
        return pageHeader(title, {})
//        return pageHeader(title, {className: 'padding-left-to-center-720'})
    }
}

export function customerDynamicPageNames() {
    return tokens('sign-in sign-up dashboard orders support')
}

export function writerDynamicPageNames() {
    return tokens('sign-in sign-up dashboard orders support store users')
}

export function pageHeader(title, attrs={}) {
    #extract {className=''} from attrs
    return elcl({
        render() {
            return diva(asn({className: `page-header ${className}`, style: {marginTop: 30}}, attrs),
                       h3(title))
        },
        componentDidMount() {
            testGlobal.pageHeader = textMeat(title)
        },
        componentWillUnmount() {
            testGlobal.pageHeader = undefined
        },
    })
}

                
clog('Client code is kind of loaded')



        
//                    spana({className: 'fa-stack fa-lg'},
//                        el('i', {className: 'fa fa-circle fa-stack-2x', style: {color: LIGHT_GREEN_700}}),
//                        el('i', {className: 'fa fa-check fa-stack-1x fa-inverse'})),

