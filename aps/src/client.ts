/*
 * APS
 * 
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

MODE = 'debug'
DEBUG_SIMULATE_SLOW_NETWORK = true
BOOTSTRAP_VERSION = 3
BACKEND_URL = 'http://localhost:3100'

require('regenerator-runtime/runtime')
import static 'into-u/utils-client into-u/ui ./stuff'

let lang, t

export function dynamicPageNames() {
    return tokens('sign-in sign-up dashboard')
}

asn(global, {
    initDynamicCustomerUI(opts) {
        lang = opts.lang
        t = makeT(lang)
        
        timeoutSet(DEBUG_SIMULATE_SLOW_NETWORK ? 1000 : 0, _=> {
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
                        dlog('todo show confirmation instructions')
                    },
                    bottomLinkTitle: t('Already have an account? Sign in here.', 'Уже есть аккаунт? Тогда входим сюда.'),
                    bottomLinkPath: 'sign-in.html',
                })
            }
            
            function renameme(def) {
                setRoot(updatableElement(update => {
                    let working, error
                    
                    for (const [name, field] of toPairs(def.fields)) {
                        field.attrs = field.attrs || {}
                        field.attrs.id = field.attrs.id || 'field-' + name // TODO:vgrechka @kill
                        
                        if (field.type === 'text') {
                            const input = Input({
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
                            field.control = Input(asn({type: 'password'}, field.attrs))
                        } else if (field.type === 'agreeTerms') {
                            const checkbox = Checkbox()
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
                        
                        window['simulate_setControlValue_' + name] = function(x) {
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
                        pageHeader(def.pageTitle, {className: 'padding-left-to-center-720'}),
                        formsa({width: 720, margin: '0 auto'},
                            error && errorBanner(error),
                            ...values(def.fields).map(field => {
                                return diva({className: 'form-group'},
                                           field.titleControl,
                                           field.control)
                            }),
                            divsa({textAlign: 'left'},
                                button.primary({title: def.primaryButtonTitle, disabled: working}, window['simulate_click_primary'] = async function() {
                                    for (const [name, field] of toPairs(def.fields)) {
                                        field.setError(undefined)
                                        field.setDisabled(true)
                                    }
                                    error = undefined
                                    working = true
                                    update()
                                    
                                    const res = await rpc(asn({fun: def.rpcFun}, omapo(def.fields, x => x.getValue())))
                                    
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
        })
    }
})

function setRoot(comp) {
    ReactDOM.render(comp, byid0('root'))
}

function DashboardPage() {
    return updatableElement(update => {
        return _=> div(
            pageHeader(t('Dashboard', 'Панель')),
            div('i am dashboard'))
    })
}

export function pageHeader(title, attrs={}) {
    #extract {className=''} from attrs
    return diva(asn({className: `page-header ${className}`, style: {marginTop: 30}}, attrs),
               el('h3', {}, title))
}

async function rpc(message) {
    try {
        var request = require('superagent');
        const response = await request
            .post(`${BACKEND_URL}/rpc`)
            .set('X-Requested-With', 'XMLHttpRequest')
            .set('Expires', '-1')
            .set('Cache-Control', 'no-cache,no-store,must-revalidate,max-age=-1,private')
            .set('APS-Token', 'something')
            .type('application/json')
            .send(asn({lang}, message))
            
        if (DEBUG_SIMULATE_SLOW_NETWORK) {
            await delay(250)
        }
        
        // dlog('response body', response.body)
        return response.body
    } catch (e) {
        console.error(e)
        return {error: t('Sorry, service is temporarily unavailable', 'Извините, сервис временно недоступен')}
    }
}

// ======================================== TEST SCENARIOS ========================================

const testScenarioToRun = 'Customer UA :: Sign Up :: 1'
    
global.testGlobal = {errorLabels: {}}

const testScenarios = {
    async 'Customer UA :: Sign Up :: 1'() {
        simulateNavigatePage('sign-up')
        
        simulatePopulateFields({
        })
        simulateClick('primary')
        await assertShitSpinsForMax(2000)
        assertErrorLabelTitlesExactly('Почта обязательна', 'Имя обязательно', 'Фамилия обязательна', 'Необходимо принять соглашение')
        assertErrorBanner('Пожалуйста, исправьте ошибки ниже')
        
        simulatePopulateFields({
            email: 'lalala',
        })
        simulateClick('primary')
        await assertShitSpinsForMax(2000)
        assertErrorLabelTitlesExactly('Интересная почта какая-то', 'Имя обязательно', 'Фамилия обязательна', 'Необходимо принять соглашение')
        assertErrorBanner('Пожалуйста, исправьте ошибки ниже')
        
        simulatePopulateFields({
            email: 'fred-apstest@mailinator.com',
        })
        simulateClick('primary')
        await assertShitSpinsForMax(2000)

        
            // firstName: 'Fred',
            // lastName: 'Black',
            // agreeTerms: true,
    },
}

function assertErrorBanner(expected) {
    uiAssert(testGlobal.errorBanner === expected, `I want error banner [${expected}]`)
}

function assertNoErrorBanner() {
    uiAssert(testGlobal.errorBanner === undefined, `I don't want error banner`)
}

function assertErrorLabelTitlesExactly(...expected) {
    const actual = values(testGlobal.errorLabels).map(x => x.title)
    const expectedDescr = expected.map(x => `[${x}]`).join(', ')
    uiAssert(deepEquals(sortBy(expected), sortBy(actual)), `I want exactly following error labels: ${expectedDescr}`)
}

if (MODE === 'debug' && typeof window === 'object') {
    window.captureShitToAssert = function() {
        clog('------------ BEGIN Generated code -------------')
        clog(capture())
        clog('------------ END Generated code -------------')
    }
    
    window.addEventListener('keydown', e => {
        if (e.ctrlKey && e.altKey && e.key === 'k') {
            const code = capture()
            $(document.body).append(`
                <textarea id="capturedCode" rows="10" style="position: absolute; bottom: 0px; width: 100%; font-family: monospace;"></textarea>
            `)
            const area = $('#capturedCode')
            area.val(code)
            area.focus()
            area.select()
        }
    })

    function capture() {
        let gen = ''
        gen += `assertErrorLabelTitlesExactly(${values(testGlobal.errorLabels).map(x => "'" + escapeStringLiteral(x.title) + "'").join(', ')})\n`
        if (testGlobal.errorBanner === undefined) {
            gen += `assertNoErrorBanner()\n`
        } else {
            gen += `assertErrorBanner(${toStringLiteralCode(testGlobal.errorBanner)})\n`
        }
        return gen
    }
}

async function runTestScenario() {
    if (MODE !== 'debug' || !testScenarioToRun) return
    await testScenarios[testScenarioToRun]()
    
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

async function assertShitSpinsForMax(maxTime) {
    assertShitSpins()
    
    const t0 = Date.now()
    while (Date.now() - t0 < maxTime) {
        if (!testGlobal.shitSpins) return
        await delay(100)
    }
    
    uiAssert(false, `I expected the shit to stop spinning in ${maxTime}ms`)
}

function assertShitSpins() {
    uiAssert(testGlobal.shitSpins, 'I want shit to be spinning')
}

function uiAssert(condition, errorMessage) {
    if (condition) return
    
    $(document.body).append(`
        <div id="uiAssertionErrorBanner" style="
            position: absolute;
            bottom: 0px;
            width: 100%;
            background-color: ${RED_700};
            color: ${WHITE};
            padding: 10px 10px;
            text-align: center;
            font-weight: bold;
        "></div>
    `)
    $('#uiAssertionErrorBanner').text(errorMessage)
    
    raise('UI assertion failed')
}

function simulateNavigatePage(pageName) {
    history.replaceState(null, '', pageName + '.html')
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

function simulateClick(name) {
    const functionName = 'simulate_click_' + name
    const click = window[functionName]
    if (!click) raise('I want function ' + functionName)
    click()
}


clog('Client code is kind of loaded')
