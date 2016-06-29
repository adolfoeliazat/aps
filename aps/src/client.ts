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

global.initUI = async function(opts) {
    const _t = makeT(LANG)
    function t(meta, ...args) {
        return {meta, meat: _t(...args)}
    }
    
    if (MODE === 'debug') {
        await initClientStackSourceMapConsumer()
    }
    
    const urlObject = url.parse(location.href)
    const urlQuery = querystring.parse(urlObject.query)
    
    window.onpopstate = function(e) {
        showWhatsInPath()
    }
    window.showWhatsInPath = showWhatsInPath
    showWhatsInPath()
    
    
    function showWhatsInPath() {
        const path = document.location.pathname
        if (path.endsWith('/sign-in.html')) return showSignIn()
        if (path.endsWith('/sign-up.html')) return showSignUp()
        
        if (localStorage.getItem('userTitle')) {
            if (path.endsWith('/orders.html')) return showOrders()
            return showDashboard()
        }
        
        history.replaceState(null, '', 'sign-in.html')
        return showSignIn()
    }
    
    function showSignIn() {
        renameme({
            pageTitle: t('Sign In', 'Вход'),
            primaryButtonTitle: t('Sign In', 'Войти'),
            fields: {
                email: {
                    title: t('E-mail', 'Почта'),
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
                byid('signInNavLink').attr('href', '#').text(t('Dashboard', 'Панель'))
                setRoot(DashboardPage())
            },
            bottomLinkTitle: t('Still don’t have an account? Create one!', 'Как? Еще нет аккаунта? Срочно создать!'),
            bottomLinkPath: 'sign-up.html',
        })
    }
    
    function showSignUp() {
        renameme({
            pageTitle: t('Sign Up', 'Регистрация'),
            primaryButtonTitle: t('Proceed', 'Вперед'),
            fields: {
                email: {
                    title: t('E-mail', 'Почта'),
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
                // byid('signInNavLink').attr('href', '#').text(t('Dashboard', 'Панель'))
                // setRoot(DashboardPage())
                showSignUpConfirmationInstructions()
            },
            bottomLinkTitle: t('Already have an account? Sign in here.', 'Уже есть аккаунт? Тогда входим сюда.'),
            bottomLinkPath: 'sign-in.html',
        })
    }
    
    function showSignUpConfirmationInstructions() {
        setRoot(div(
            responsivePageHeader(t('Sign Up', 'Регистрация')),
            p(t('Check your mailbox. We sent you instructions for confirming sign up.',
                'Проверьте почту. Мы отправили вам инструкции для подтверждения регистрации.'))
        ))
    }
    
    function renameme(def) {
        setRoot(updatableElement(update => {
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
                responsivePageHeader(def.pageTitle),
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
                    hr(),
                    divsa({textAlign: 'left'}, link(def.bottomLinkTitle, _=> {
                        history.pushState(null, '', def.bottomLinkPath)
                        showWhatsInPath()
                    })),
                ))
        }))
    }
    
    runTestScenario()
    
    
    function setRoot(comp) {
        ReactDOM.render(updatableElement(update => {
            return _=> div(
                comp,
                MODE === 'debug' && div(capturePane, assertionErrorPane))
        }), byid0('root'))
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
            return {error: t('Sorry, service is temporarily unavailable', 'Извините, сервис временно недоступен')}
        }
    }

    let rpcclient
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
    
    setUIDebugRPC(rpc)
    
    function testScenarios() {return{
        async 'Something'() {
        },
    
        // ======================================== CUSTOMER UA TEST SCENARIOS ========================================
        
        async 'Customer UA :: Sign In :: After Wilma signs up'() {
            await rpc({fun: 'danger_clearSentMails'})
            await rpc({fun: 'danger_killUser', email: 'wilma.blue@test.shit.ua'})
            simulateNavigatePath('sign-up.html')
            
            assertUIState({aid: '6aa1c1bf-804b-4f5c-98e5-c081cd6238a0', expected: {
                inputs: 
                { email: { value: '' },
                  firstName: { value: '' },
                  lastName: { value: '' },
                  agreeTerms: { value: false } },
               errorLabels: {},
               errorBanner: undefined 
            }})
            
            failForJumping('Implement me', '182853f7-c8ee-41b9-b45f-d52636f9a154')
            
//            // Inputs
//            testGlobal.inputs['email'].value = 'wilma.blue@test.shit.ua'
//            testGlobal.inputs['firstName'].value = 'Вильма'
//            testGlobal.inputs['lastName'].value = 'Блу'
//            testGlobal.inputs['agreeTerms'].value = true
//            // Action
//            testGlobal.buttons['primary'].click()
//            await assertShitSpinsForMax(2000)
//            // Expected state
//            assertNoErrorLabels()
//            assertNoErrorBanner()
//            assertTextSomewhere('Проверьте почту')
            
            // await assertSentMails({descr: 'Sign up confirmation mail', aid: '169a6331-c004-47fd-9b53-05242915d9f7'})
        },
        
        async 'Customer UA :: Sign Up :: 1'() {
            await rpc({fun: 'danger_killUser', email: 'wilma.blue@test.ua.shit'})
            simulateNavigatePath('sign-up.html')
            
            simulateClick('primary')
            await assertShitSpinsForMax(2000)
            assertErrorLabelTitlesExactly('Почта обязательна', 'Имя обязательно', 'Фамилия обязательна', 'Необходимо принять соглашение')
            assertErrorBanner('Пожалуйста, исправьте ошибки ниже')
            
            simulatePopulateFields({email: 'lalala'})
            simulateClick('primary')
            await assertShitSpinsForMax(2000)
            assertErrorLabelTitlesExactly('Интересная почта какая-то', 'Имя обязательно', 'Фамилия обязательна', 'Необходимо принять соглашение')
            assertErrorBanner('Пожалуйста, исправьте ошибки ниже')
            
            simulatePopulateFields({email: 'fred.red-apstest@mailinator.com'})
            simulateClick('primary')
            await assertShitSpinsForMax(2000)
            assertErrorLabelTitlesExactly('Имя обязательно', 'Фамилия обязательна', 'Необходимо принять соглашение')
            assertErrorBanner('Пожалуйста, исправьте ошибки ниже')
            
            simulatePopulateFields({firstName: 'WilmaWilmaWilmaWilmaWilmaWilmaWilmaWilmaWilmaWilma1'})
            simulateClick('primary')
            await assertShitSpinsForMax(2000)
            assertErrorLabelTitlesExactly('Не более 50 символов', 'Фамилия обязательна', 'Необходимо принять соглашение')
            assertErrorBanner('Пожалуйста, исправьте ошибки ниже')
            
            simulatePopulateFields({firstName: 'Wilma'})
            simulateClick('primary')
            await assertShitSpinsForMax(2000)
            assertErrorLabelTitlesExactly('Фамилия обязательна', 'Необходимо принять соглашение')
            assertErrorBanner('Пожалуйста, исправьте ошибки ниже')
            
            simulatePopulateFields({lastName: 'BlueBlueBlueBlueBlueBlueBlueBlueBlueBlueBlueBlue111'})
            simulateClick('primary')
            await assertShitSpinsForMax(2000)
            assertErrorLabelTitlesExactly('Не более 50 символов', 'Необходимо принять соглашение')
            assertErrorBanner('Пожалуйста, исправьте ошибки ниже')
            
            simulatePopulateFields({lastName: 'Blue'})
            simulateClick('primary')
            await assertShitSpinsForMax(2000)
            assertErrorLabelTitlesExactly('Необходимо принять соглашение')
            assertErrorBanner('Пожалуйста, исправьте ошибки ниже')
            
            simulatePopulateFields({agreeTerms: true})
            simulateClick('primary')
            await assertShitSpinsForMax(2000)
            assertErrorLabelTitlesExactly('Такая почта уже зарегистрирована')
            assertErrorBanner('Пожалуйста, исправьте ошибки ниже')
            
            // Extra spaces in email
            simulatePopulateFields({email: '      fred.red-apstest@mailinator.com     '})
            simulateClick('primary')
            await assertShitSpinsForMax(2000)
            assertErrorLabelTitlesExactly('Такая почта уже зарегистрирована')
            assertErrorBanner('Пожалуйста, исправьте ошибки ниже')

            simulatePopulateFields({email: '   wilma.blue-apstest@mailinator.com      '})
            simulateClick('primary')
            await assertShitSpinsForMax(2000)
            assertNoErrorBanner()
            assertNoErrorLabels()
        },
    }}
            
    function assertUIState(def) {
        const actual = {
            inputs: omapo(testGlobal.inputs, x => x.capture()),
            errorLabels: testGlobal.errorLabels,
            errorBanner: testGlobal.errorBanner,
        }
        assertRenameme(asn(def, {actual}))
    }
    
    async function assertSentMails(def) {
        const actual = await rpc({fun: 'danger_getSentMails'})
        assertRenameme(asn(def, {actual}))
    }
    
    function failForJumping(message, $tag) {
        uiAssert(false, message, {detailsUI: updatableElement(update => {
            const link = OpenSourceCodeLink({$tag})
            return _=> divsa({marginTop: 5, padding: 5, backgroundColor: WHITE, position: 'relative'},
                           div(horiz(t('Jump and fix that shit: '), link)))
        })})
    }
    
    function assertRenameme({descr='Describe me', aid, actual, expected}) {
        if (expected === undefined) {
            expected = EXPECTATIONS[aid] || '--- not yet hardened ---'
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
            if (actualStringForPasting[0] === '{'/*}*/) actualStringForPasting = actualStringForPasting.slice(1)
            if (actualStringForPasting[actualStringForPasting.length - 1] === /*{*/'}') actualStringForPasting = actualStringForPasting.slice(0, actualStringForPasting.length - 1)
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
                aid && divsa({position: 'absolute', right: 5, top: 5},
                    my.ueb = my.ueb || WorkButton({title: t('Update Expectation'), level: 'primary', glyph: 'pencil', async work() {
                        try {
                            await rpc({fun: 'danger_updateExpectation', aid, actual})
                            my.ueb = divsa({fontWeight: 'bold'}, 'Expectation updated')
                        } catch (e) {
                            console.error(e)
                            my.ueb = divsa({color: RED_700, fontWeight: 'bold'}, 'Expectation update fucked up')
                        }
                        update()
                    }})
                ),
                horiza({style: {marginBottom: 5}},
                    spana({$testme: true, style: {fontWeight: 'bold'}}, t('Assertion ID: ')),
                    my.codeLink = my.codeLink || OpenSourceCodeLink({$tag: aid})),
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
            s = s.replace(/\\n/g, '\n') // @expect-break
            return s
        }
    }

    function assertTextSomewhere(expected) {
        uiAssert(~$(document.body).text().indexOf(expected), `I want following text on screen: [${expected}]`)
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

    if (MODE === 'debug' && typeof window === 'object') {
        window.addEventListener('keydown', e => {
            if (MODE !== 'debug') return
            if (e.ctrlKey && e.altKey && e.key === 'k') return captureState()
            if (e.ctrlKey && e.altKey && e.key === 'i') return captureInputs()
            if (e.ctrlKey && e.altKey && e.key === 'a') return assertUIState({aid: 'just-showing-actual'})
        })
    }

    let currentTestScenarioName

    async function runTestScenario() {
        const testScenarioToRun = urlQuery.testScenario
        if (MODE !== 'debug' || !testScenarioToRun) return
        
        const initialPath = location.pathname + location.search
        try {
            currentTestScenarioName = testScenarioToRun
            await testScenarios()[testScenarioToRun]()
        } finally {
            currentTestScenarioName = undefined
            history.replaceState(null, '', initialPath)
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
    }

    function assertErrorLabelTitle(expectedTitle) {
        uiAssert(ofind(testGlobal.errorLabels, x => x.title === expectedTitle), `I want error label [${expectedTitle}] on screen`)
    }

    function assertFail() {
        uiAssert(false, 'Just failing here, OK?')
    }

    async function assertShitSpinsForMax(maxTime) {
        assertShitSpins()
        
        const t0 = Date.now()
        while (Date.now() - t0 < maxTime) {
            if (!testGlobal.shitSpins) return
            await delay(50)
        }
        
        uiAssert(false, `I expected the shit to stop spinning in ${maxTime}ms`)
    }

    function assertShitSpins() {
        uiAssert(testGlobal.shitSpins, 'I want shit to be spinning')
    }
    
    function uiFail(errorMessage) {
        uiAssert(false, errorMessage)
    }
    
    const assertionErrorPane = statefulElement(update => {
        let visible, content, top
        
        return {
            render() {
                if (!visible) return null
                return diva({$tag: 'a47a707b-9c52-4a60-837d-a00787bd4746', style: {position: 'absolute', left: 0, top, width: '100%', backgroundColor: RED_700, padding: '10px 10px', textAlign: 'left'}},
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

    function uiAssert(condition, errorMessage, {detailsUI}={}) {
        if (condition) return
        
        assertionErrorPane.set({message: errorMessage + mdash + currentTestScenarioName, stack: clientStack().join('\n'), detailsUI})
        raise('UI assertion failed')
    }

    function simulateNavigatePath(path) {
        history.replaceState(null, '', path)
        showWhatsInPath()
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
        return pageHeader(title, {className: 'padding-left-to-center-720'})
    }
}

export function dynamicPageNames() {
    return tokens('sign-in sign-up dashboard')
}

export function pageHeader(title, attrs={}) {
    #extract {className=''} from attrs
    return diva(asn({className: `page-header ${className}`, style: {marginTop: 30}}, attrs),
               h3(title))
//    return diva(asn({className: `page-header ${className}`, style: {marginTop: 30}}, attrs),
//               el('h3', {}, title))
}

                
clog('Client code is kind of loaded')
