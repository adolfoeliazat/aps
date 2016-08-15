/*
 * APS
 * 
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

#pragma instrument-ui-rendering

import {TestCommon} from './test-common'
import static 'into-u/utils-client into-u/ui ./stuff'

GENERATED_SHIT = require('./generated-shit')

module.exports = function({sim}) {
    const drpc = getDebugRPC()
    
    const common = TestCommon({sim})
    
    return {
        'UA Writer :: Sign Up :: 1 cca87b63-eedd-407d-a681-814f128b1822': {
            async run() {
                const slowly = false
                
                if (slowly) { setTestSpeed('slow'); art.respectArtPauses = true }
                await art.resetTestDatabase({templateDB: 'test-template-ua-1', alsoRecreateTemplate: true})
                
                await art.run(s{
                    instructions: [
                        ...vovchok1(),
                    ]
                })
                
                function vovchok1() {
                    return [
                        ...selectBrowser(s{
                            clientKind: 'writer', browserName: 'vovchok1',
                            stateDescription: t('Marko Vovchok, an eager writer wannabe, comes to our site')}),
                            
                        s{step: {kind: 'action', long: t('Trying to sign in (to non-existing account)')}},
                        ...signIn(s{email: 'vovchok@test.shit.ua', password: 'something'}),
                        s{assert: {$tag: '1a543472-b429-4add-88e8-799598607ad3', expected: '---generated-shit---'}},
                        s{step: {kind: 'state', long: t('Of course it failed')}},
                        
                        s{step: {kind: 'action', long: t('Clicking "Sign Up" link')}},
                        s{setValue: {shame: 'TextField-email.Input', value: 'vovchok@test.shit.ua'}},
                        s{setValue: {shame: 'TextField-password.Input', value: 'something'}},
                        s{click: {shame: 'link-createAccount', timestamp: '2016/08/12 20:40:58'}},
                        s{assert: {$tag: '877a3f2f-ad7d-41c7-af4b-9665526fc27f', expected: '---generated-shit---'}},
                        s{step: {kind: 'state', long: t('Got registration form')}},
                        
                        s{step: {kind: 'action', long: t('Trying to submit empty form')}},
                        s{click: {shame: 'button-primary', timestamp: '2016/08/14 12:55:19'}},
                        s{assert: {$tag: '9b0cad6b-c396-4b8a-b731-57905900a80a', expected: '---generated-shit---'}},
                        s{step: {kind: 'state', long: t('Got a bunch of errors')}},

                        s{step: {kind: 'action', long: t('Agreeing terms')}},
                        s{setValue: {shame: 'AgreeTermsField.Checkbox', value: true}},
                        s{click: {shame: 'button-primary', timestamp: '2016/08/14 14:22:04'}},
                        s{assert: {$tag: '7dbefeef-84f6-47e2-8635-d67ddcbb153d', expected: '---generated-shit---'}},
                        s{step: {kind: 'state', long: t('-1 error message')}},

                        s{step: {kind: 'action', long: t('Entering email')}},
                        s{setValue: {shame: 'TextField-email.Input', value: 'vovchok@test.shit.ua'}},
                        s{click: {shame: 'button-primary', timestamp: '2016/08/14 14:23:51'}},
                        s{assert: {$tag: 'ac403942-31f7-45a3-b42c-e37f6c1b83a2', expected: '---generated-shit---'}},
                        s{step: {kind: 'state', long: t('-1 error message')}},

                        s{step: {kind: 'action', long: t('Entering first name')}},
                        s{setValue: {shame: 'TextField-firstName.Input', value: 'Марко'}},
                        s{click: {shame: 'button-primary', timestamp: '2016/08/14 14:25:32'}},
                        s{assert: {$tag: 'f866c286-58f4-4785-a2da-bad213bd4567', expected: '---generated-shit---'}},
                        s{step: {kind: 'state', long: t('-1 error message')}},

                        s{step: {kind: 'action', long: t('Entering last name')}},
                        s{setValue: {shame: 'TextField-lastName.Input', value: 'Вовчок'}},
                        s{click: {shame: 'button-primary', timestamp: '2016/08/14 17:48:51'}},
                        s{assert: {$tag: '0b7e96bf-4f42-41dc-b658-7b60e863356e', expected: '---generated-shit---'}},
                        s{step: {kind: 'state', long: t('Sign-in form with message that account is created')}},
                        
                        s{actionPlaceholder: {$tag: '6bcd180a-9701-4e84-babe-99253f49e44b'}},
                    ]
                }
                
                
                function selectBrowser(def) {
                    #extract {stateDescription, browserName, clientKind} from def
                    
                    const res = [
                        s{step: {kind: 'navigation', long: t('Trying to open dashboard page as ' + browserName)}},
                        s{do: {async action() {
                            CLIENT_KIND = clientKind
                            sim.selectBrowser(browserName)
                            await sim.navigate('dashboard.html')
                        }}},
                        
                        s{step: {kind: 'state', long: stateDescription}},
                        // s{pausePoint: {title: stateDescription, theme: 'blue'}},
                    ]
                    
                    if (clientKind === 'writer') {
                        res.push(...[
                            s{assert: {$tag: '42816503-1dc0-4e43-aaa4-a8b11a680501', expected: {
                                'TextField-email.Input': ``,
                                  'TextField-email.label': `Почта`,
                                  'TextField-password.Input': ``,
                                  'TextField-password.Input.type': `password`,
                                  'TextField-password.label': `Пароль`,
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
                            }}},
                        ])
                    } else if (clientKind === 'customer') {
                        res.push(...[
                            s{assert: {$tag: '4d7bfd1d-d5cd-4b64-a069-a99f85f934da', expected: {
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
                            }}}                
                        ])
                    } else {
                        raise('WTF is the clientKind')
                    }
                    
                    return res
                }
                
                function signIn(def) {
                    #extract {email, password='secret'} from def
                    
                    return [
                        s{setValue: {shame: 'TextField-email.Input', value: email}},
                        s{setValue: {shame: 'TextField-password.Input', value: password}},
                        s{click: {shame: 'button-primary'}},
                    ]
                }
            }
            
        },
    }
}


