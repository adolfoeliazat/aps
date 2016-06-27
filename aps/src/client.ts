/*
 * APS
 * 
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

MODE = 'debug'
DEBUG_SIMULATE_SLOW_NETWORK = true
BOOTSTRAP_VERSION = 3
BACKEND_URL = 'http://localhost:3100'

require('regenerator-runtime/runtime') // TODO:vgrechka Get rid of this shit, as I don't want to support old browsers anyway

import * as url from 'url'
import * as querystring from 'querystring'
import static 'into-u/utils-client into-u/ui ./stuff'

global.initUI = async function(opts) {
    const t = makeT(LANG)
    
    let sourceMapConsumer
    if (MODE === 'debug') {
        await async function initSourceMapConsumer() { // TODO:vgrechka @refactor Extract to foundation/utils-client
            const logTime = beginLogTime('initSourceMapConsumer')
            try {
                global.Buffer = require('buffer').Buffer
                const sourceMap = require('source-map')
                const convertSourceMap = require('convert-source-map')
                
                sourceMapConsumer = false // Will indicate failure of creation and prevent further attempts
                try {
                    const response = await superagent.get(`bundle.js`)
                    const text = response.text
                    const smcIndex = text.lastIndexOf('//# sourceMappingURL=data:application/json;')
                    if (~smcIndex) {
                        sourceMapConsumer = new sourceMap.SourceMapConsumer(
                            convertSourceMap.fromComment(
                                text.slice(smcIndex)).toJSON())
                    } else {
                        dlog(`No inline source map found in bundle.js`)
                        return lines
                    }
                } catch (e) {
                    dlog(`Failed to make source map consumer: ${e}`)
                    return lines
                }
            } finally {
                logTime.end()
            }
        }()
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
                        impl.error && errorLabel(impl.error, {style: {marginTop: 5, marginRight: 9, textAlign: 'right'}}),
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
                            t({en: div('I’ve read and agreed with ', link('terms and conditions', popupTerms)),
                               ua: div('Я прочитал и принял ', link('соглашение', popupTerms))}),
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
            {slowNetworkSimulationDelay: MODE === 'debug' && DEBUG_SIMULATE_SLOW_NETWORK && (currentTestScenarioName ? 250 : 1000)}
        )
    }
    
    function testScenarios() {return{
        async 'Something'() {
        },
    
        // ======================================== CUSTOMER UA TEST SCENARIOS ========================================
        
        async 'Customer UA :: Sign In :: After Wilma signs up'() {
            await rpc({fun: 'danger_clearSentMails'})
            await rpc({fun: 'danger_killUser', email: 'wilma.blue@test.shit.ua'})
            simulateNavigatePath('sign-up.html')
            
            // Inputs
            testGlobal.inputs['email'].value = 'wilma.blue@test.shit.ua'
            testGlobal.inputs['firstName'].value = 'Вильма'
            testGlobal.inputs['lastName'].value = 'Блу'
            testGlobal.inputs['agreeTerms'].value = true
            // Action
            testGlobal.buttons['primary'].click()
            await assertShitSpinsForMax(2000)
            // Expected state
            assertNoErrorLabels()
            assertNoErrorBanner()
            assertTextSomewhere('Проверьте почту')
            await assertSentMailsAreExactly([{
                from: `APS <noreply@aps.local>`,
                to: `Wilma Blue <wilma.blue@test.shit>`,
                subject: `Подтверждение регистрации в APS`,
                html: `
                    Привет, Wilma!<br><br>
                    Для подтверждения регистрации перейди по этой ссылке: zzzzz
                `
            }], uiFail)
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
    
    async function assertSentMailsAreExactly(expected) {
        const actual = await rpc({fun: 'danger_getSentMails'})
        if (deepEquals(actual, expected)) return
        
        sortKeys(actual)
        sortKeys(expected)
        const actualString = deepInspect(actual)
        const expectedString = deepInspect(expected)
        const diffItems = require('diff').diffLines(expectedString, actualString)
        const diffDivs = diffItems.map(item => {
            let backgroundColor, label
            if (item.added) {
                backgroundColor = GREEN_100
                label = 'Actual'
            } else {
                backgroundColor = RED_100
                label = 'Expected'
            }
            return divsa({backgroundColor},
                       divsa({fontWeight: 'bold'}, label),
                       divsa({}, item.value))
        })
        
        uiAssert(false, 'Shit', {detailsUI: updatableElement(update => {
            return _=> divsa({},
                divsa({height: 300, marginTop: 5, padding: 5, backgroundColor: WHITE, whiteSpace: 'pre-wrap', fontSize: '100%'},
                    ...diffDivs))
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

    function stack() { // TODO:vgrechka @refactor Extract to foundation/utils-client
        const e = Error()
        const lines = e.stack.split('\n').slice(3)
        const usefulLines = []
        lines.forEach((lineText, i) => {
            const bjsi = lineText.indexOf('bundle.js:')
            if (!~bjsi) return
            const [line, column] = lineText.slice(bjsi + 'bundle.js:'.length, lineText.length - 1).split(':')
            const pos = sourceMapConsumer.originalPositionFor({line: parseInt(line), column: parseInt(column)}) 
            
            // Members of returned thing can be null 
            // https://github.com/mozilla/source-map/blob/182f4459415de309667845af2b05716fcf9c59ad/lib/source-map-consumer.js#L637
            if (pos.source) {
                lineText = lineText.slice(0, lineText.indexOf('('/*)*/)) + `(${pos.source}:${pos.line}:${pos.column})`
            }
            
            usefulLines.push(lineText)
        })
        
        return usefulLines
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
        let visible, content
        
        return {
            render() {
                if (!visible) return null
                return divsa({position: 'absolute', left: 0, bottom: 0, width: '100%', backgroundColor: RED_700, padding: '10px 10px', textAlign: 'left'},
                           divsa({fontWeight: 'bold', borderBottom: '2px solid white', paddingBottom: 5, marginBottom: 5, color: WHITE}, content.message),
                           divsa({whiteSpace: 'pre-wrap', color: WHITE}, content.stack),
                           content.detailsUI,
                       )
            },
            
            set(_content) {
                update(content = _content, visible = true)
            },
        }
    })

    function uiAssert(condition, errorMessage, {detailsUI}={}) {
        if (condition) return
        
        assertionErrorPane.set({message: errorMessage + mdash + currentTestScenarioName, stack: stack().join('\n'), detailsUI})
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
               el('h3', {}, title))
}

                
clog('Client code is kind of loaded')
