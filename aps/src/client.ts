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
            if (opts.pageName === 'sign-in') {
                setRoot(updatableElement(update => {
                        const emailInput = Input({autoFocus: true})
                        const passwordInput = Input({type: 'password'})
                        let working, error
                        
                        return _=> div(
                            pageHeader(t(`Sign In`, `Вход`)),
                            formsa({width: '50%', margin: '0 auto'},
                                error && quoteDanger(error),
                                diva({className: 'form-group'},
                                    label(t('E-mail', 'Почта')),
                                    emailInput),
                                diva({className: 'form-group'},
                                    label(t('Password', 'Пароль')),
                                    passwordInput),
                                divsa({textAlign: 'left'},
                                    button.primary({title: t('Sign In', 'Войти'), disabled: working}, async function() {
                                        emailInput.disabled = true
                                        passwordInput.disabled = true
                                        error = undefined
                                        working = true
                                        update()
                                        
                                        const res = await rpc({fun: 'signIn', email: emailInput.value, password: passwordInput.value})
                                        
                                        if (res.error) {
                                            error = res.error
                                        } else {
                                            dlog('successssssssssss')
                                            error = undefined
                                            byid('signInNavLink').attr('href', '#').text(t('Dashboard', 'Панель'))
                                            setRoot(DashboardPage())
                                        }
                                        
                                        working = false
                                        emailInput.disabled = false
                                        passwordInput.disabled = false
                                        update()
                                    }),
                                    working && divsa({float: 'right'}, spinnerMedium())),
                                hr(),
                                divsa({textAlign: 'left'}, link(t('Still don’t have an account? Create it!', 'Как? Еще нет аккаунта? Срочно создать!'), _=> {
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
