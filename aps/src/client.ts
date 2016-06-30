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
    // @ctx state
    const _t = makeT(LANG)
    let urlObject, urlQuery, updateReactShit, rootContent, pageState, rpcclient, signedUpOK
    let updateCurrentPage
    let testScenarioToRun, preventRestoringURLAfterTest, currentTestScenarioName, assertionErrorPane
    
    if (MODE === 'debug') {
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
        
        urlObject = url.parse(location.href)
        urlQuery = querystring.parse(urlObject.query)
        testScenarioToRun = urlQuery.testScenario
    }
    
    ReactDOM.render(updatableElement(update => {
        updateReactShit = update
        return _=> div(rootContent, assertionErrorPane, capturePane)
    }), byid0('root'))
    
    window.onpopstate = function(e) {
        showWhatsInURL()
    }
    
    if (!testScenarioToRun) {
        showWhatsInURL()
    } else {
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
    }
    
    
    function t(meta, ...args) {
        return {meta, meat: _t(...args)}
    }
    
    function showWhatsInURL() {
        urlObject = url.parse(location.href)
        urlQuery = querystring.parse(urlObject.query)
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
                byid('signInNavLink').attr('href', '#').text(t('Dashboard', 'Панель'))
                raise('implement successful login')
            },
        })
        
        setPage({
            pageTitle: t('Sign In', 'Вход'),
            pageBody: div(
                signedUpOK && preludeWithCheck(
                    t('Cool. You have an account now. We sent you email with password.',
                      'Все круто. Теперь у тебя есть аккаунт. Пароль мы отправили письмом.')),
                    
                form,
                               
                !signedUpOK && div(
                    hr(),
                    divsa({textAlign: 'left'}, link(t('Still don’t have an account? Create one!', 'Как? Еще нет аккаунта? Срочно создать!'), _=> {
                        pushNavigate('sign-up.html')
                    })))
                )
        })
    }
    
    function preludeWithCheck(content) {
        return diva({style: {marginBottom: 15}},
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
    }
    
    function pushNavigate(where) {
        history.pushState(null, '', where)
//        requestAnimationFrame(_=> showWhatsInURL())
        showWhatsInURL()
    }
    
    function setPage(def) {
        updateReactShit(rootContent = updatableElement(update => {
            updateCurrentPage = update
            
            pageState = {
                pageBody: def.pageBody
            }
            
            return _=> diva({style: {position: 'relative'}},
                responsivePageHeader(fov(def.pageTitle)),
                pageState.headerShitSpins && diva({style: {position: 'absolute', right: 0, top: 0}}, spinnerMedium({name: 'headerShit'})),
                pageState.error && errorBanner(pageState.error),
                pageState.pageBody,
            )
        }))
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
    
        // ======================================== CUSTOMER UA TEST SCENARIOS ========================================
        
        async 'Customer UA :: Sign In :: After Wilma signs up'() {
            await rpc({fun: 'danger_clearSentEmails'})
            await rpc({fun: 'danger_killUser', email: 'wilma.blue@test.shit.ua'})
            await rpc({fun: 'danger_fixNextGeneratedPassword', password: '63b2439c-bf18-42c5-9f7a-42d7357f966a'})
            
            simulateURLNavigation('sign-up.html')
            assertUIState({$tag: '6aa1c1bf-804b-4f5c-98e5-c081cd6238a0', expected: {
                inputs: 
                { email: { value: `` },
                  firstName: { value: `` },
                  lastName: { value: `` },
                  agreeTerms: { value: false } },
               errorLabels: {},
               errorBanner: undefined,
               pageHeader: `Регистрация` 
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
                inputs: { email: { value: `` }, password: { value: `` } },
                errorLabels: {},
                errorBanner: undefined,
                pageHeader: `Вход` 
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
        },
        
// preventRestoringURLAfterTest = true
// failForJumping('Implement me', '182853f7-c8ee-41b9-b45f-d52636f9a154')
        
        async 'Customer UA :: Sign Up :: 1'() {
        },
    }}
    
    function assertHref({$tag, expected}) {
        uiAssert(document.location.href === expected, `I want to be at following URL: [${expected}]`, jumpToShitDetailsUI($tag))
    }
            
    function assertUIState(def) {
        const actual = {
            inputs: omapo(testGlobal.inputs, x => x.capture()),
            errorLabels: testGlobal.errorLabels,
            errorBanner: testGlobal.errorBanner,
            pageHeader: testGlobal.pageHeader,
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
        showWhatsInURL()
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

