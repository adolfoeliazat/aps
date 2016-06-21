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
                const emailInput = Input({autoFocus: true})
                const passwordInput = Input({type: 'password'})
                ReactDOM.render(
                    formsa({width: '50%', margin: '0 auto'},
                        diva({className: 'form-group'},
                            label(t('E-mail', 'Почта')),
                            emailInput),
                        diva({className: 'form-group'},
                            label(t('Password', 'Пароль')),
                            passwordInput),
                        div(link(t('Still don’t have an account? Create it!', 'Как? Еще нет аккаунта? Срочно создать!'), _=> {
                            dlog('create acccccccc')
                        })),
                        div(
                            button({title: 'Go', onClick() { dlog('goooooing') }})),
                    ),
                    byid0('root'))
            }
        })
    }
})

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
