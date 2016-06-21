/*
 * APS
 * 
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

BOOTSTRAP_VERSION = 3
DEBUG_SIMULATE_SLOW_NETWORK = true

require('regenerator-runtime/runtime')
import static 'into-u/utils-client into-u/ui'

let lang

asn(global, {
    initCustomerUI(opts) {
        lang = opts.lang
        timeoutSet(DEBUG_SIMULATE_SLOW_NETWORK ? 1000 : 0, _=> {
            $('#wholePageSpinner').hide()
            if (opts.pageName === 'sign-in') {
                ReactDOM.render(updatableElement(update => {
                        const emailInput = Input({autoFocus: true})
                        const passwordInput = Input({type: 'password'})
                        let working
                        
                        return _=> formsa({width: '50%', margin: '0 auto'},
                            diva({className: 'form-group'},
                                label(t('E-mail', 'Почта')),
                                emailInput),
                            diva({className: 'form-group'},
                                label(t('Password', 'Пароль')),
                                passwordInput),
                            divsa({textAlign: 'left'},
                                button.primary({title: t('Sign In', 'Войти'), disabled: working}, async function() {
                                    working = true
                                    emailInput.disabled = true
                                    passwordInput.disabled = true
                                    update()
                                    
                                    const res = await rpc()
                                    dlog('got result', res)
                                    
                                    working = false
                                    emailInput.disabled = false
                                    passwordInput.disabled = false
                                    update()
                                }),
                                working && divsa({float: 'right'}, spinnerMedium())),
                            hr(),
                            divsa({textAlign: 'left'}, link(t('Still don’t have an account? Create it!', 'Как? Еще нет аккаунта? Срочно создать!'), _=> {
                                dlog('create acccccccc')
                            })),
                        )
                    }),
                    byid0('root'))
            }
        })
    }
})

function rpc() {
    return new Promise(resolve => {
        timeoutSet(3000, _=> resolve('hiiiiiii'))
    })
}

function t(first, second) {
    let ss
    if (second) {
        ss = {en: first, ua: second}
    } else {
        ss = first
    }
    return ss[lang]
}

clog('Client code is kind of loaded')
