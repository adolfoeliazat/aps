/*
 * APS
 * 
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

DOMAIN_UA_CUSTOMER = 'aps-ua-customer.local'
DOMAIN_UA_WRITER = 'aps-ua-writer.local'
DOMAIN_EN_CUSTOMER = 'aps-en-customer.local'
DOMAIN_EN_WRITER = 'aps-en-writer.local'

import static 'into-u/utils-client'

export function deliveryOptions() {
    return ['8d', '7d', '5d', '3d', '24h', '12h']
}

export function deliveryOptionTitle(dopt, lang) {
    return {
        '8d': {en: '8+ days', ua: '8+ дней'},
        '7d': {en: '6-7 days', ua: '6-7 дней'},
        '5d': {en: '4-5 days', ua: '4-5 дней'},
        '3d': {en: '2-3 days', ua: '2-3 дня'},
        '24h': {en: '24 hours', ua: '24 часа'},
        '12h': {en: '12 hours', ua: '12 часов'},
    }[dopt][lang]
}

const TYPES_OF_PAPER = {
    en_essay: 'Essay, Research',
    en_biblio: 'Annotated Bibliography',
    en_editing: 'Proofreading, Editing',
        
    ua_essay: 'Реферат',
    ua_course: 'Курсовая работа',
    ua_graduate: 'Дипломная работа',
    ua_editing: 'Корректура'
}

export function typesOfPaper(lang) {
    return keys(TYPES_OF_PAPER).filter(x => x.startsWith(lang + '_'))
}

export function typeOfPaperTitle(top) {
    return TYPES_OF_PAPER[top]
}

export function moneyTitleWithoutCurrency(x, lang) {
    const integral = Math.floor(x)
    let decimal = '' + (Math.floor(x * 100) % 100)
    if (decimal.length === 1) decimal = '0' + decimal
    return integral + {en: '.', ua: ','}[lang] + decimal
}

export function currencyPrefixSuffix(curr, lang) {
    return {
        en: ['$', ''],
        ua: ['', {en: ' UAH', ua: ' грн.'}[lang]]
    }[curr]
}

export function moneyTitleWithCurrency(x, curr, lang) {
    const raw = moneyTitleWithoutCurrency(x, lang)
    const [prefix, suffix] = currencyPrefixSuffix(curr, lang)
    return prefix + raw + suffix
}

export function priceForDeliveryOptionAndTypeOfPaper(dopt, top) {
    return {
        '8d': {
            en_essay: 14.99,
            en_biblio: 10.99,
            en_editing: 3.99,
                
            ua_essay: 10.99,
            ua_course: 15.99,
            ua_graduate: 20.99,
            ua_editing: 5.99,
        },
        '7d': {
            en_essay: 16.99,
            en_biblio: 12.99,
            en_editing: 5.99,
                
            ua_essay: 12.99,
            ua_course: 17.99,
            ua_graduate: 22.99,
            ua_editing: 7.99,
        },
        '5d': {
            en_essay: 18.99,
            en_biblio: 14.99,
            en_editing: 7.99,
                
            ua_essay: 15.99,
            ua_course: 20.99,
            ua_graduate: 25.99,
            ua_editing: 10.99,
        },
        '3d': {
            en_essay: 20.99,
            en_biblio: 16.99,
            en_editing: 9.99,
                
            ua_essay: 20.99,
            ua_course: 25.99,
            ua_graduate: 30.99,
            ua_editing: 15.99,
        },
        '24h': {
            en_essay: 25.99,
            en_biblio: 18.99,
            en_editing: 11.99,
                
            ua_essay: 25.99,
            ua_course: 30.99,
            ua_graduate: 35.99,
            ua_editing: 20.99,
        },
        '12h': {
            en_essay: 30.99,
            en_biblio: 20.99,
            en_editing: 12.99,
                
            ua_essay: 35.99,
            ua_course: 40.99,
            ua_graduate: 45.99,
            ua_editing: 30.99,
        },
    }[dopt][top]
}

export function makeT(lang) {
    return function t(...args) {
        if (!args[0]) raise('I don’t want falsy first argument in t()')
        let ss
        if (typeof args[0] === 'object') {
            ss = args[0]
        } else {
            ss = {en: args[0], ua: args[1] || args[0]}
        }
        
        const res = ss[lang]
        if (!res) raise('Localize me: ' + deepInspect({lang, arguments}))
        return res
    }
}









