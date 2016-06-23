/*
 * APS
 * 
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

BOOTSTRAP_VERSION = 3
DEBUG_SIMULATE_SLOW_NETWORK = true
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
                                impl.error && divsa({color: RED_300, marginTop: 5, marginRight: 9, textAlign: 'right'}, impl.error),
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
                                impl.error && divsa({color: RED_300, marginTop: 5, marginRight: 9, textAlign: 'right'}, impl.error))
                                   
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
                            error && quoteDanger(error),
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
            
            window.testScenario && doNoisa(window.testScenario)
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
            await delay(1000)
        }
        
        // dlog('response body', response.body)
        return response.body
    } catch (e) {
        console.error(e)
        return {error: t('Sorry, service is temporarily unavailable', 'Извините, сервис временно недоступен')}
    }
}

// ======================================== TEST SCENARIOS ========================================

if (typeof window === 'object') { // Module can be (actually is) required in server context
    window.testScenario = testScenario_signUp_missingEmail
}

async function testScenario_signUp_1() {
    simulateNavigatePage('sign-up')
    populateFields({
        email: 'fred-apstest@mailinator.com',
        firstName: 'Fred',
        lastName: 'Black',
        agreeTerms: true,
    })
}

async function testScenario_signUp_missingEmail() {
    simulateNavigatePage('sign-up')
    populateFields({
        firstName: 'Fred',
        lastName: 'Black',
        // agreeTerms: true,
    })
    simulateClick('primary')
}

function simulateNavigatePage(pageName) {
    history.replaceState(null, '', pageName + '.html')
    showWhatsInPath()
}

function populateFields(data) {
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
