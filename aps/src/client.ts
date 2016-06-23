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

asn(global, {
    initCustomerUI(opts) {
        lang = opts.lang
        t = makeT(lang)
        
        timeoutSet(DEBUG_SIMULATE_SLOW_NETWORK ? 1000 : 0, _=> {
            $('#wholePageSpinner').hide()
            
            window.onpopstate = function(e) {
                showWhatsInPath()
            }
            
            showWhatsInPath()
            
            
            function showWhatsInPath() {
                const path = document.location.pathname
                if (path.endsWith('/sign-in.html')) return showSignIn()
                if (path.endsWith('/sign-up.html')) return showSignUp()
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
                        password: {
                            title: t('Password', 'Пароль'),
                            type: 'password',
                        },
                        passwordAgain: {
                            title: t('Password Again', 'Пароль еще раз'),
                            type: 'password',
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
                    
                    for (const field of values(def.fields)) {
                        if (field.type === 'text') {
                            field.control = Input(asn({}, field.attrs))
                        } else if (field.type === 'password') {
                            field.control = Input(asn({type: 'password'}, field.attrs))
                        } else {
                            raiseInspect('WTF is the field', field)
                        }
                    }
                    
                    return _=> div(
                        pageHeader(def.pageTitle),
                        formsa({width: '50%', margin: '0 auto'},
                            error && quoteDanger(error),
                            ...values(def.fields).map(field => {
                                return diva({className: 'form-group'},
                                           label(field.title),
                                           field.control)
                            }),
                            divsa({textAlign: 'left'},
                                button.primary({title: def.primaryButtonTitle, disabled: working}, async function() {
                                    values(def.fields).forEach(x => x.disabled = true)

                                    error = undefined
                                    working = true
                                    update()
                                    
                                    const res = await rpc(asn({fun: def.rpcFun}, omapo(def.fields, x => x.control.value)))
                                    
                                    if (res.error) {
                                        error = res.error
                                    } else {
                                        error = undefined
                                        def.onSuccess(res)
                                    }
                                    
                                    working = false
                                    values(def.fields).forEach(x => x.disabled = false)
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

export function pageHeader(title) {
    return diva({className: 'page-header', style: {marginTop: 30}},
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


clog('Client code is kind of loaded')
