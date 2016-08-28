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
        'UA Writer :: Sign Up :: 1': {
            async run() {
                const drpc = getDebugRPC()
                
                const slowly = false
                
                if (slowly) { setTestSpeed('slow'); art.respectArtPauses = true }
                await art.resetTestDatabase({templateDB: 'test-template-ua-1', alsoRecreateTemplate: true})
                
                try {
                    await art.run(s{
                        instructions: [
                            ...vovchok1(),
                            s{worldPoint: {name: '1'}},
                            ...dasja1(),
                            s{worldPoint: {name: '2'}},
                            ...vovchok2(),
                            ...dasja2(),
                            s{worldPoint: {name: '3'}},
                            ...vovchok3(),
                        ]
                    })
                } finally {
                    // testGlobal.adhoc_showLastRequest()
                }
                
                function vovchok1() {
                    return [
                        s{beginSection: {long: t('Marko Vovchok signs up')}},
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
                            
                            s{beginSection: {long: t('Submitting sign-up form')}},
                                s{step: {kind: 'action', indent: 1, long: t('Trying to submit empty form')}},
                                s{click: {shame: 'button-primary', timestamp: '2016/08/14 12:55:19'}},
                                s{assert: {$tag: '9b0cad6b-c396-4b8a-b731-57905900a80a', expected: '---generated-shit---'}},
                                s{step: {kind: 'state', indent: 2, long: t('Got a bunch of errors')}},

                                s{step: {kind: 'action', indent: 2, long: t('Agreeing terms')}},
                                s{setValue: {shame: 'AgreeTermsField.Checkbox', value: true}},
                                s{click: {shame: 'button-primary', timestamp: '2016/08/14 14:22:04'}},
                                s{assert: {$tag: '7dbefeef-84f6-47e2-8635-d67ddcbb153d', expected: '---generated-shit---'}},
                                s{step: {kind: 'state', indent: 1, long: t('-1 error message')}},

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
                                s{do: {async action() { await drpc({fun: 'danger_imposeNextGeneratedPassword', password: 'fucking-big-generated-secret'}) }}},
                                s{click: {shame: 'button-primary', timestamp: '2016/08/14 17:48:51'}},
                                s{assert: {$tag: '0b7e96bf-4f42-41dc-b658-7b60e863356e', expected: '---generated-shit---'}},
                                s{step: {kind: 'state', long: t('Sign-in form with message that account is created. Got email with password')}},
                                s{do: {async action() { await drpc({fun: 'danger_clearSentEmails'}) }}},
                            s{endSection: {}},
                            
                            s{beginSection: {long: t('Submitting sign-in form')}},
                                s{step: {kind: 'action', long: t('First try with wrong password')}},
                                s{setValue: {shame: 'TextField-email.Input', value: 'vovchok@test.shit.ua'}},
                                s{setValue: {shame: 'TextField-password.Input', value: 'dead-wrong-shit'}},
                                s{click: {shame: 'button-primary', timestamp: '2016/08/15 19:26:34'}},
                                s{step: {kind: 'state', long: t('No way')}},
                                s{assert: {$tag: '51815ec8-62e9-4c25-aad5-fa97f30d6cff', expected: '---generated-shit---'}},

                                s{step: {kind: 'action', long: t('Now with password from received email')}},
                                s{setValue: {shame: 'TextField-email.Input', value: 'vovchok@test.shit.ua'}},
                                s{setValue: {shame: 'TextField-password.Input', value: 'fucking-big-generated-secret'}},
                                s{click: {shame: 'button-primary', timestamp: '2016/08/15 19:48:57'}},
                                s{step: {kind: 'state', long: t('Got page asking to fill profile')}},
                                s{assert: {$tag: '00997d56-f00a-4b6a-bffd-595ba4c23806', expected: '---generated-shit---'}},
                            s{endSection: {}},

                            s{beginSection: {long: t('Submitting profile')}},
                                s{step: {kind: 'action', long: t('Trying to send empty profile')}},
                                s{setValue: {shame: 'TextField-phone.Input', value: ''}},
                                s{setValue: {shame: 'TextField-aboutMe.Input', value: ''}},
                                s{click: {shame: 'button-primary', timestamp: '2016/08/15 20:12:37'}},
                                s{step: {kind: 'state', long: t('Bunch of errors')}},
                                s{assert: {$tag: '4fc3f3d3-2806-4c99-9769-6d046cd7a985', expected: '---generated-shit---'}},

                                s{step: {kind: 'action', long: t('Entering phone: some junk')}},
                                s{setValue: {shame: 'TextField-phone.Input', value: 'qwerty'}},
                                s{click: {shame: 'button-primary', timestamp: '2016/08/15 20:16:20'}},
                                s{step: {kind: 'state', long: t('No way')}},
                                s{assert: {$tag: '243f5834-48a4-496b-8770-371ac0c94b19', expected: '---generated-shit---'}},

                                s{step: {kind: 'action', long: t('Entering phone: too long')}},
                                s{setValue: {shame: 'TextField-phone.Input', value: '012345678901234567890'}},
                                s{click: {shame: 'button-primary', timestamp: '2016/08/16 11:56:01'}},
                                s{step: {kind: 'state', long: t('No way')}},
                                s{assert: {$tag: 'a07d90e4-60a6-45c1-a5c6-cd217a01a933', expected: '---generated-shit---'}},
                                
                                s{step: {kind: 'action', long: t('Entering phone: too few digits')}},
                                s{setValue: {shame: 'TextField-phone.Input', value: '--3234--++--'}},
                                s{click: {shame: 'button-primary', timestamp: '2016/08/16 11:56:05'}},
                                s{step: {kind: 'state', long: t('No way')}},
                                s{assert: {$tag: '3f290096-9162-419b-835d-ec2b02f8f5ca', expected: '---generated-shit---'}},

                                s{step: {kind: 'action', long: t('Entering phone: something sane')}},
                                s{setValue: {shame: 'TextField-phone.Input', value: '+38 (068) 9110032'}},
                                s{click: {shame: 'button-primary', timestamp: '2016/08/16 12:02:30'}},
                                s{step: {kind: 'state', long: t('-1 error')}},
                                s{assert: {$tag: 'd072d3c4-38c9-481a-9fec-0ecd9c5601a2', expected: '---generated-shit---'}},

                                s{step: {kind: 'action', long: t('Entering about: too long')}},
                                s{setValue: {shame: 'TextField-aboutMe.Input', value: common.LONG_SHIT_301}},
                                s{click: {shame: 'button-primary', timestamp: '2016/08/16 14:01:33'}},
                                s{step: {kind: 'state', long: t('TODO State description')}},
                                s{assert: {$tag: 'e3c1c929-27af-4742-850c-1c1b7cb2ff51', expected: '---generated-shit---'}},

                                s{step: {kind: 'action', long: t('Entering aboutMe: something sane')}},
                                s{setValue: {shame: 'TextField-aboutMe.Input', value: 'I am a fucking bitch. No, really. Wanna have one for the team?'}},
                                s{click: {shame: 'button-primary', timestamp: '2016/08/16 14:55:45'}},
                                s{step: {kind: 'state', long: t('TODO State description')}},
                                s{assert: {$tag: '9219ded3-54e4-4d6b-bf1b-63615cf8a56e', expected: '---generated-shit---'}},

                                s{actionPlaceholder: {$tag: '6bcd180a-9701-4e84-babe-99253f49e44b'}},
                            s{endSection: {}},
                        s{endSection: {}},
                    ]
                }
                
                function dasja1() {
                    return [
                        s{beginSection: {long: t('Dasja accepts that shitty profile')}},
                            ...selectBrowser(s{
                                clientKind: 'writer', browserName: 'dasja1',
                                stateDescription: t('Dasja, a support admin, comes into play')}),
                                
                            s{step: {kind: 'action', long: t('Normal sign in')}},
                            ...signIn(s{email: 'dasja@test.shit.ua', password: 'secret'}),
                            s{assert: {$tag: '659a49bb-8018-4ac1-8c64-0fa31e998a50', expected: '---generated-shit---'}},
                            s{step: {kind: 'state', long: t('TODO')}},
                                
                            s{step: {kind: 'action', long: t('Click on "Approve Profiles" link')}},
                            s{click: {shame: 'section-workPending.profilesToApprove.link', timestamp: '2016/08/18 14:58:16'}},
                            s{step: {kind: 'state', long: t('Got page with profiles to be approved')}},
                            s{assert: {$tag: 'dd32f775-d45f-4a86-b815-c8897fa01fe3', expected: '---generated-shit---', expectedExtender:
                                expectHeaderControls({filter: '2approve', ordering: 'desc'})}},

                            s{step: {kind: 'action', long: t('Click on edit icon')}},
                            s{click: {shame: 'chunk-i000.item-i000.heading.icon-edit', timestamp: '2016/08/26 13:56:04'}},
                            s{step: {kind: 'state', long: t('Got form')}},
                            s{assert: {$tag: 'ba17b066-b272-48eb-901c-d22b5b1f803b', expected: '---generated-shit---', expectedExtender:
                                expectHeaderControls({filter: '2approve', ordering: 'desc'})}},
                            
                            s{step: {kind: 'action', long: t('Make some changes and approve')}},
                            s{setValue: {shame: 'chunk-i000.item-i000.SelectField-state.Select', value: 'cool'}},
                            s{setValue: {shame: 'chunk-i000.item-i000.TextField-email.Input', value: 'vovchok@test.shit.ua'}},
                            s{setValue: {shame: 'chunk-i000.item-i000.TextField-firstName.Input', value: 'Маркожопик'}},
                            s{setValue: {shame: 'chunk-i000.item-i000.TextField-lastName.Input', value: 'Вовкулака'}},
                            s{setValue: {shame: 'chunk-i000.item-i000.TextField-phone.Input', value: '+38 (068) 9110032'}},
                            s{setValue: {shame: 'chunk-i000.item-i000.TextField-aboutMe.Input', value: 'I am a fucking bitch. No, really. Wanna have one for the team?'}},
                            s{setValue: {shame: 'chunk-i000.item-i000.TextField-adminNotes.Input', value: 'And she really is...'}},
                            s{click: {shame: 'chunk-i000.item-i000.button-primary', timestamp: '2016/08/26 14:14:26'}},
                            s{step: {kind: 'state', long: t('That bitch is now cool')}},
                            s{assert: {$tag: '59308ccf-67b9-4fab-a13d-154c96e8bd63', expected: '---generated-shit---', expectedExtender:
                                expectHeaderControls({filter: '2approve', ordering: 'desc'})}},

                            s{step: {kind: 'action', long: t('Press enter in search box to refresh shit')}},
                            s{keyDown: {shame: 'Input-search', keyCode: 13}},
                            s{step: {kind: 'state', long: t('Now we get nothing, cause there’s no shit to approve')}},
                            s{assert: {$tag: 'ffd89ee7-31b5-4742-b15a-196876ebec6f', expected: '---generated-shit---', expectedExtender:
                                expectHeaderControls({filter: '2approve', ordering: 'desc'})}},


                            s{step: {kind: 'action', long: t('Choose "All" to see all shit')}},
                            s{setValue: {shame: 'Select-filter', value: 'all'}},
                            s{step: {kind: 'state', long: t('Got all shit. That bitch is cool')}},
                            s{assert: {$tag: '88b14624-383f-4edf-8b3e-a9c413d87f27', expected: '---generated-shit---', expectedExtender:
                                expectHeaderControls({filter: 'all', ordering: 'desc'})}},
                            
                            s{actionPlaceholder: {$tag: '7b29b964-c9b3-406d-bfeb-3a1091e57e5d'}},
                        s{endSection: {}},
                    ]
                }
                
                function vovchok2() {
                    return [
                        s{beginSection: {long: t('Marko Vovchok can use site')}},
                            ...selectBrowser(s{
                                clientKind: 'writer', browserName: 'vovchok2',
                                stateDescription: t('Marko Vovchok, aka The Bitch, comes again some time later')}),
                                
                                s{step: {kind: 'action', long: t('Sign in')}},
                                s{setValue: {shame: 'TextField-email.Input', value: 'vovchok@test.shit.ua'}},
                                s{setValue: {shame: 'TextField-password.Input', value: 'fucking-big-generated-secret'}},
                                s{click: {shame: 'button-primary', timestamp: '2016/08/27 12:11:34'}},
                                s{step: {kind: 'state', long: t('Got something')}},
                                s{assert: {$tag: '86e56915-a334-4209-85c9-8d2c53cd9f0a', expected: '---generated-shit---'}},
                                
                                s{actionPlaceholder: {$tag: '8791b24d-fce2-4079-81ec-789332ac1863'}},
                        s{endSection: {}},
                    ]
                }
                
                function dasja2() {
                    return [
                        s{beginSection: {long: t('Dasja rejects the bitch')}},
                            ...selectBrowser(s{
                                clientKind: 'writer', browserName: 'dasja2',
                                stateDescription: t('Dasja decides to reject the bitch for whatever reasons')}),
                                
                            s{step: {kind: 'action', long: t('Normal sign in')}},
                            ...signIn(s{email: 'dasja@test.shit.ua', password: 'secret'}),
                            s{assert: {$tag: '908a5f71-6432-4a8d-8efd-922b25ed54b5', expected: '---generated-shit---'}},
                            s{step: {kind: 'state', long: t('Got dashboard')}},
                                
                            s{step: {kind: 'action', long: t('Select "Users" in top navbar')}},
                            s{click: {shame: 'TopNavItem-admin-users', timestamp: '2016/08/27 13:17:16'}},
                            s{step: {kind: 'state', long: t('Got users')}},
                            s{assert: {$tag: '6fbd591f-f62c-4a37-a6ae-0958ef6c81f7', expected: '---generated-shit---', expectedExtender:
                                expectHeaderControls({filter: 'all', ordering: 'desc'})}},

                            s{step: {kind: 'action', long: t('Click on edit icon for the bitch')}},
                            s{click: {shame: 'chunk-i000.item-i000.heading.icon-edit', timestamp: '2016/08/27 13:20:58'}},
                            s{step: {kind: 'state', long: t('TODO State description')}},
                            s{assert: {$tag: 'b138801b-9fec-43b3-94d6-f135318b2a22', expected: '---generated-shit---', expectedExtender:
                                expectHeaderControls({filter: 'all', ordering: 'desc'})}},
                            
                            // @wip rejection
                            s{step: {kind: 'action', long: t('Choose "Rejected" status')}},
                            s{setValue: {shame: 'chunk-i000.item-i000.SelectField-state.Select', value: 'profile-rejected'}},
                            s{step: {kind: 'state', long: t('Rejection reason field appears')}},
                            s{assert: {$tag: 'fcdac58b-e864-4a21-b3a2-47f29a1fbba9', expected: '---generated-shit---', expectedExtender:
                                expectHeaderControls({filter: 'all', ordering: 'desc'})}},
                            
                            s{step: {kind: 'action', long: t('Enter something into rejection reason, then change "Rejected" to something else')}},
                            s{setValue: {shame: 'chunk-i000.item-i000.TextField-profileRejectionReason.Input', value: 'Well... er...'}},
                            s{setValue: {shame: 'chunk-i000.item-i000.SelectField-state.Select', value: 'cool'}},
                            s{step: {kind: 'state', long: t('Rejection reason field disappears')}},
                            s{assert: {$tag: 'a81d2f49-aa12-45ea-92a2-fc6bf4ad0cf6', expected: '---generated-shit---', expectedExtender:
                                expectHeaderControls({filter: 'all', ordering: 'desc'})}},
                            
                            s{step: {kind: 'action', long: t('Select "Rejected" again')}},
                            s{setValue: {shame: 'chunk-i000.item-i000.SelectField-state.Select', value: 'profile-rejected'}},
                            s{step: {kind: 'state', long: t('Rejection reason is kept')}},
                            s{assert: {$tag: '2e650433-d5f6-4c94-ba5a-b3ff07e15865', expected: '---generated-shit---', expectedExtender:
                                expectHeaderControls({filter: 'all', ordering: 'desc'})}},
                            
                            s{step: {kind: 'action', long: t('Try to reject without reason')}},
                            s{setValue: {shame: 'chunk-i000.item-i000.TextField-profileRejectionReason.Input', value: ''}},
                            s{click: {shame: 'chunk-i000.item-i000.button-primary', timestamp: '2016/08/27 13:23:05'}},
                            s{step: {kind: 'state', long: t('No way')}},
                            s{assert: {$tag: 'c2f5de3e-ff25-4c38-80dc-e09d89e0944f', expected: '---generated-shit---', expectedExtender:
                                expectHeaderControls({filter: 'all', ordering: 'desc'})}},

                            s{step: {kind: 'action', long: t('Reject motherfucker along with changing name and email')}},
                            s{setValue: {shame: 'chunk-i000.item-i000.TextField-email.Input', value: 'vovkulaka@test.shit.ua'}},
                            s{setValue: {shame: 'chunk-i000.item-i000.TextField-firstName.Input', value: 'Мокрожопик'}},
                            s{setValue: {shame: 'chunk-i000.item-i000.TextField-profileRejectionReason.Input', value: 'Why I do this? Cause I can! I\'m the admin! U-ha-ha-ha...'}},
                            s{click: {shame: 'chunk-i000.item-i000.button-primary', timestamp: '2016/08/27 13:23:07'}},
                            s{step: {kind: 'state', long: t('The motherfucker is now reddish')}},
                            s{assert: {$tag: '0d4d388f-a0ad-4824-9a37-36e7e74365ae', expected: '---generated-shit---', expectedExtender:
                                expectHeaderControls({filter: 'all', ordering: 'desc'})}},

                            s{step: {kind: 'action', long: t('Choose to see only cool users')}},
                            s{setValue: {shame: 'Select-filter', value: 'cool'}},
                            s{step: {kind: 'state', long: t('No bitch in the list')}},
                            s{assert: {$tag: 'c9219504-6a67-4863-b09c-c6eafa80bc26', expected: '---generated-shit---', expectedExtender:
                                expectHeaderControls({filter: 'cool', ordering: 'desc'})}},

                            s{step: {kind: 'action', long: t('Choose to see only rejected users')}},
                            s{setValue: {shame: 'Select-filter', value: 'rejected'}},
                            s{step: {kind: 'state', long: t('Only bitch is in the list')}},
                            s{assert: {$tag: 'd21b167c-3064-41b7-a3d3-d8a7c5f91b92', expected: '---generated-shit---', expectedExtender:
                                expectHeaderControls({filter: 'rejected', ordering: 'desc'})}},

                            s{step: {kind: 'action', long: t('Choose to see all')}},
                            s{setValue: {shame: 'Select-filter', value: 'all'}},
                            s{step: {kind: 'state', long: t('Bitch and others are in the list')}},
                            s{assert: {$tag: 'd72124f6-0ad2-4f50-ba0e-7bedff1e6a79', expected: '---generated-shit---', expectedExtender:
                                expectHeaderControls({filter: 'all', ordering: 'desc'})}},

                            s{actionPlaceholder: {$tag: 'cd903ec5-a126-407a-92d6-a724b818054f'}},
                        s{endSection: {}},
                    ]
                }
                
                function vovchok3() {
                    return [
                        s{beginSection: {long: t('Marko Vovchok faces she’s rejected')}},
                            ...selectBrowser(s{
                                clientKind: 'writer', browserName: 'vovchok3',
                                stateDescription: t('The Bitch opens browser')}),
                                
                                s{step: {kind: 'action', long: t('Try to sign in with old email')}},
                                s{setValue: {shame: 'TextField-email.Input', value: 'vovchok@test.shit.ua'}},
                                s{setValue: {shame: 'TextField-password.Input', value: 'fucking-big-generated-secret'}},
                                s{click: {shame: 'button-primary', timestamp: '2016/08/27 14:53:22'}},
                                s{step: {kind: 'state', long: t('No way, cause admin have changed her email to "vovkulaka..."')}},
                                s{assert: {$tag: 'eeb4fc2a-0e24-4fc0-9e66-6ecccd932d8e', expected: '---generated-shit---'}},
                                
                                s{step: {kind: 'action', long: t('Sign in with new email')}},
                                s{setValue: {shame: 'TextField-email.Input', value: 'vovkulaka@test.shit.ua'}},
                                s{setValue: {shame: 'TextField-password.Input', value: 'fucking-big-generated-secret'}},
                                s{click: {shame: 'button-primary', timestamp: '2016/08/27 14:54:22'}},
                                s{step: {kind: 'state', long: t('Oh, they rejected me')}},
                                s{assert: {$tag: 'f80aea79-7308-444b-acd3-fe81cee46eff', expected: '---generated-shit---'}},
                                
                                s{actionPlaceholder: {$tag: '80c8c508-ef39-4e07-8be3-3e8a964e984c'}},
                        s{endSection: {}},
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
                
                function expectHeaderControls({search='', filter, ordering}) {
                    return function({expected}) {
                        asn(expected, {
                            'Input-search': search,
                            
                            'Select-filter.item-i000.title': `Все`,
                            'Select-filter.item-i000.value': `all`,
                            'Select-filter.item-i001.title': `Прохладные`,
                            'Select-filter.item-i001.value': `cool`,
                            'Select-filter.item-i002.title': `Ждут аппрува`,
                            'Select-filter.item-i002.value': `2approve`,
                            'Select-filter.item-i003.title': `Завернутые`,
                            'Select-filter.item-i003.value': `rejected`,
                            'Select-filter.item-i004.title': `Забаненые`,
                            'Select-filter.item-i004.value': `banned`,
                            
                            'Select-ordering.item-i000.title': `Сначала новые`,
                            'Select-ordering.item-i000.value': `desc`,
                            'Select-ordering.item-i001.title': `Сначала старые`,
                            'Select-ordering.item-i001.value': `asc`,
                            'Select-ordering.selected.title': `Сначала новые`,
                            'Select-ordering.selected.value': `desc`,
                        })
                        
                        if (filter === 'all') {
                            asn(expected, {
                                'Select-filter.selected.title': `Все`,
                                'Select-filter.selected.value': `all`,
                            })
                        }
                        else if (filter === 'cool') {
                            asn(expected, {
                                'Select-filter.selected.title': `Прохладные`,
                                'Select-filter.selected.value': `cool`,
                            })
                        }
                        else if (filter === '2approve') {
                            asn(expected, {
                                'Select-filter.selected.title': `Ждут аппрува`,
                                'Select-filter.selected.value': `2approve`,
                            })
                        }
                        else if (filter === 'rejected') {
                            asn(expected, {
                                'Select-filter.selected.title': `Завернутые`,
                                'Select-filter.selected.value': `rejected`,
                            })
                        }
                        else if (filter === 'banned') {
                            asn(expected, {
                                'Select-filter.selected.title': `Забаненые`,
                                'Select-filter.selected.value': `banned`,
                            })
                        }
                        else {
                            raise(`Weird filter: ${filter}`)
                        }
                        
                        if (ordering === 'asc') {
                            asn(expected, {
                                'Select-ordering.selected.title': `Сначала старые`,
                                'Select-ordering.selected.value': `asc`,
                            })
                        }
                        else if (ordering === 'desc') {
                            asn(expected, {
                                'Select-ordering.selected.title': `Сначала новые`,
                                'Select-ordering.selected.value': `desc`,
                            })
                        }
                        else {
                            raise(`Weird ordering: ${ordering}`)
                        }
                    }
                }
                
            }
            
        },
    }
}


