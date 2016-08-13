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
                        s{step: {kind: 'state', long: t('Of course it failed')}},
                        s{assert: {$tag: '1a543472-b429-4add-88e8-799598607ad3', expected: '---generated-shit---'}},
                        
                        s{setValue: {shame: 'Input-email', value: 'vovchok@test.shit.ua'}},
                        s{setValue: {shame: 'Input-password', value: 'something'}},
                        s{step: {kind: 'action', long: t('Clicking "Sign Up" link')}},
                        s{click: {shame: 'link-createAccount', timestamp: '2016/08/12 20:40:58'}},
                        s{step: {kind: 'state', long: t('Got registration form')}},
                        s{assert: {$tag: '877a3f2f-ad7d-41c7-af4b-9665526fc27f', expected: '---generated-shit---'}},
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
                            }}}                
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
                        s{setValue: {shame: 'Input-email', value: email}},
                        s{setValue: {shame: 'Input-password', value: password}},
                        s{click: {shame: 'button-primary'}},
                    ]
                }
            }
            
//            async run_bak() {
//                const slowly = false
//                
//                if (slowly) { setTestSpeed('slow'); art.respectArtPauses = true }
//                await art.resetTestDatabase({templateDB: 'test-template-ua-1', alsoRecreateTemplate: true})
//                
//                #hawait vovchok1()
//                
//                async function vovchok1() {
//                    #hawait common.selectBrowser({$tag: '6152786a-7a29-41d0-bf96-8e35ac4e2773',
//                        clientKind: 'writer', browserName: 'vovchok1',
//                        stateDescription: t('Marko Vovchok, an eager writer wannabe, comes to our site')})
//                        
//                    art.pushStepDescription(s{kind: 'action', long: t('Trying to sign in (to non-existing account)')})
//                    #hawait common.signIn({$tag: '80b40f65-9e49-48df-b1e4-cf34e5a0fc99', email: 'vovchok@test.shit.ua', password: 'something'})
//                    art.pushStepDescription(s{kind: 'state', long: t('Of course it failed')})
//                    art.uiState({$tag: 'e44a44a7-fe84-42a6-ac80-a6aee4ac87ac', expected: '---generated-shit---'})
//                    
//                    // Inputs
//                    #hawait testGlobal.controls['Input-email'].testSetValue({value: 'vovchok@test.shit.ua'})
//                    #hawait testGlobal.controls['Input-password'].testSetValue({value: 'something'})
//                    // Action
//                    art.pushStepDescription(s{kind: 'action', long: t('Clicking "Sign Up" link')})
//                    // #hawait art.pausePoint(s{title: '$fromLastStepDescription'})
//                    #hawait drpc({fun: 'danger_imposeNextRequestTimestamp', timestamp: '2016/08/12 20:40:58'})
//                    #hawait testGlobal.controls['link-createAccount'].testClick()
//                    art.pushStepDescription(s{kind: 'state', long: t('Got registration form')})
//                    art.uiState({$tag: 'da9f7f21-34fe-415f-952a-d8f34e3fe1cb', expected: '---generated-shit---'})
//                    // #hawait art.pausePoint({title: '$fromLastStepDescription'})
//
//                    art.actionPlaceholder({$tag: 'e84d7a46-7320-4629-a0ac-25232d64dc0a'})
//                }
//            }
        },
    }
}


