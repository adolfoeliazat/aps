/*
 * APS
 * 
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

const DEBUG_SIMULATE_SLOW_NETWORK = true

require('regenerator-runtime/runtime')
import static 'into-u/utils-client into-u/ui'

let lang

asn(global, {
    initCustomerUI(opts) {
        lang = opts.lang
        timeoutSet(DEBUG_SIMULATE_SLOW_NETWORK ? 1000 : 0, _=> {
            $('#wholePageSpinner').hide()
            if (opts.pageName === 'sign-in') {
                const emailInput = Input()
                const passwordInput = Input({type: 'password'})
                ReactDOM.render(
                    form(
                        diva({className: 'form-group'},
                            label(t({en: 'E-mail', ua: 'Почта'})),
                            emailInput),
                        diva({className: 'form-group'},
                            label(t({en: 'Password', ua: 'Пароль'})),
                            passwordInput),
                    ),
                    byid0('root'))
            }
        })
    }
})

function t(ss) {
    return ss[lang]
}

clog('Client code is kind of loaded')
