/*
 * APS
 * 
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

#pragma instrument-ui-rendering

import static 'into-u/utils-client into-u/ui ./stuff'

export function TestCommon({sim}) {
    const me = {
        async selectBrowser({$tag, stateDescription, browserName, clientKind}) {
            CLIENT_KIND = clientKind
            sim.selectBrowser(browserName)
            
            art.pushStepDescription(s{kind: 'navigation', long: t('Trying to open dashboard')})
            #hawait sim.navigate('dashboard.html')
            
            if (stateDescription) {
                art.pushStepDescription(s{kind: 'state', long: stateDescription})
                #hawait art.pausePoint({title: stateDescription, theme: 'blue', $tag})
            }
            
            if (clientKind === 'writer') {
                art.uiState({$tag, expected: {
                    'Input-email': ``,
                      'Input-password': ``,
                      'button-primary.title': `Войти`,
                      'link-createAccount.title': `Срочно создать!`,
                      'pageHeader.title': `Вход`,
                      'topNavLeft.TopNavItem-i000.shame': `TopNavItem-why`,
                      'topNavLeft.TopNavItem-i000.title': `Почему мы?`,
                      'topNavLeft.TopNavItem-i001.shame': `TopNavItem-prices`,
                      'topNavLeft.TopNavItem-i001.title': `Цены`,
                      'topNavLeft.TopNavItem-i002.shame': `TopNavItem-faq`,
                      'topNavLeft.TopNavItem-i002.title': `ЧаВо`,
                      'topNavRight.TopNavItem-i000.active': true,
                      'topNavRight.TopNavItem-i000.shame': `TopNavItem-sign-in`,
                      'topNavRight.TopNavItem-i000.title': `Вход`,
                      url: `http://aps-ua-writer.local:3022/sign-in.html`
                }})                
            } else if (clientKind === 'customer') {
                art.uiState({$tag, expected: {
                     'Input-email': ``,
                      'Input-password': ``,
                      'TopNavItem-blog': { title: `Блог` },
                      'TopNavItem-contact': { title: `Связь` },
                      'TopNavItem-faq': { title: `ЧаВо` },
                      'TopNavItem-prices': { title: `Цены` },
                      'TopNavItem-samples': { title: `Примеры` },
                      'TopNavItem-sign-in': { active: true, title: `Вход` },
                      'TopNavItem-why': { title: `Почему мы?` },
                      'button-primary': { title: `Войти` },
                      'link-createAccount': { title: `Срочно создать!` },
                      pageHeader: `Вход`,
                      url: `http://aps-ua-writer.local:3022/sign-in.html`
                }})                
            } else {
                raise('WTF is the clientKind')
            }
        },
        
        async signIn({email, password='secret'}) {
            // Inputs
            #hawait testGlobal.controls['Input-email'].testSetValue({value: email})
            #hawait testGlobal.controls['Input-password'].testSetValue({value: password})
            // Action
            #hawait testGlobal.controls['button-primary'].testClick()
        },
    }
    
    return me
}


