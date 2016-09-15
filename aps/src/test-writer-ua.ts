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
    
    // @hack for Kotlin
    global.sim = sim
    global.drpc = drpc
    global.apsCommon = common
    
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
                            ...dasja3(),
                            ...vovchok4(),
                            s{worldPoint: {name: '4'}},
                            ...dasja4(),
                        ]
                    })
                } finally {
                    // testGlobal.adhoc_showLastRequest()
                }
                
                function vovchok1() {
                    return kot.aps.KotlinShit.shittyShit.test_UA_Writer_SignUp_1_vovchok1()
                }
                
//                function vovchok1_bak() {
//                    return [
//                        s{beginSection: {long: t('Marko Vovchok signs up')}},
//                        
//                            ...selectBrowser(s{
//                                clientKind: 'writer', browserName: 'vovchok1',
//                                stateDescription: t('Marko Vovchok, an eager writer wannabe, comes to our site')}),
//                                
//                            s{step: {kind: 'action', long: t('Trying to sign in (to non-existing account)')}},
//                            ...signIn(s{email: 'vovchok@test.shit.ua', password: 'something'}),
//                            s{assert: {$tag: '1a543472-b429-4add-88e8-799598607ad3', expected: '---generated-shit---'}},
//                            s{step: {kind: 'state', long: t('Of course it failed')}},
//                            
//                            s{step: {kind: 'action', long: t('Clicking "Sign Up" link')}},
//                            s{setValue: {shame: 'TextField-email.Input', value: 'vovchok@test.shit.ua'}},
//                            s{setValue: {shame: 'TextField-password.Input', value: 'something'}},
//                            s{click: {shame: 'link-createAccount', timestamp: '2016/08/12 20:40:58'}},
//                            s{assert: {$tag: '877a3f2f-ad7d-41c7-af4b-9665526fc27f', expected: '---generated-shit---'}},
//                            s{step: {kind: 'state', long: t('Got registration form')}},
//                            
//                            s{beginSection: {long: t('Submitting sign-up form')}},
//                                s{step: {kind: 'action', long: t('Trying to submit empty form')}},
//                                s{click: {shame: 'button-primary', timestamp: '2016/08/14 12:55:19'}},
//                                s{assert: {$tag: '9b0cad6b-c396-4b8a-b731-57905900a80a', expected: '---generated-shit---'}},
//                                s{step: {kind: 'state', long: t('Got a bunch of errors')}},
//
//                                s{step: {kind: 'action', long: t('Agreeing terms')}},
//                                s{setValue: {shame: 'AgreeTermsField.Checkbox', value: true}},
//                                s{click: {shame: 'button-primary', timestamp: '2016/08/14 14:22:04'}},
//                                s{assert: {$tag: '7dbefeef-84f6-47e2-8635-d67ddcbb153d', expected: '---generated-shit---'}},
//                                s{step: {kind: 'state', long: t('-1 error message')}},
//
//                                s{step: {kind: 'action', long: t('Entering email')}},
//                                s{setValue: {shame: 'TextField-email.Input', value: 'vovchok@test.shit.ua'}},
//                                s{click: {shame: 'button-primary', timestamp: '2016/08/14 14:23:51'}},
//                                s{assert: {$tag: 'ac403942-31f7-45a3-b42c-e37f6c1b83a2', expected: '---generated-shit---'}},
//                                s{step: {kind: 'state', long: t('-1 error message')}},
//
//                                s{step: {kind: 'action', long: t('Entering first name')}},
//                                s{setValue: {shame: 'TextField-firstName.Input', value: 'Марко'}},
//                                s{click: {shame: 'button-primary', timestamp: '2016/08/14 14:25:32'}},
//                                s{assert: {$tag: 'f866c286-58f4-4785-a2da-bad213bd4567', expected: '---generated-shit---'}},
//                                s{step: {kind: 'state', long: t('-1 error message')}},
//
//                                s{step: {kind: 'action', long: t('Entering last name')}},
//                                s{setValue: {shame: 'TextField-lastName.Input', value: 'Вовчок'}},
//                                s{do: {async action() { await drpc({fun: 'danger_imposeNextGeneratedPassword', password: 'fucking-big-generated-secret'}) }}},
//                                s{click: {shame: 'button-primary', timestamp: '2016/08/14 17:48:51'}},
//                                s{assert: {$tag: '0b7e96bf-4f42-41dc-b658-7b60e863356e', expected: '---generated-shit---'}},
//                                s{step: {kind: 'state', long: t('Sign-in form with message that account is created. Got email with password')}},
//                                s{do: {async action() { await drpc({fun: 'danger_clearSentEmails'}) }}},
//                            s{endSection: {}},
//                            
//                            ...kot.aps.KotlinShit.shittyShit.pieceOfTest100(),
//                            
////                            s{beginSection: {long: t('Submitting sign-in form')}},
////                                s{step: {kind: 'action', long: t('First try with wrong password')}},
////                                s{setValue: {shame: 'TextField-email.Input', value: 'vovchok@test.shit.ua'}},
////                                s{setValue: {shame: 'TextField-password.Input', value: 'dead-wrong-shit'}},
////                                s{click: {shame: 'button-primary', timestamp: '2016/08/15 19:26:34'}},
////                                s{step: {kind: 'state', long: t('No way')}},
////                                s{assert: {$tag: '51815ec8-62e9-4c25-aad5-fa97f30d6cff', expected: '---generated-shit---'}},
////
////                                s{step: {kind: 'action', long: t('Now with password from received email')}},
////                                s{setValue: {shame: 'TextField-email.Input', value: 'vovchok@test.shit.ua'}},
////                                s{setValue: {shame: 'TextField-password.Input', value: 'fucking-big-generated-secret'}},
////                                s{click: {shame: 'button-primary', timestamp: '2016/08/15 19:48:57'}},
////                                s{step: {kind: 'state', long: t('Got page asking to fill profile')}},
////                                s{assert: {$tag: '00997d56-f00a-4b6a-bffd-595ba4c23806', expected: '---generated-shit---'}},
////                            s{endSection: {}},
//
//                            s{beginSection: {long: t('Submitting profile')}},
//                                s{step: {kind: 'action', long: t('Trying to send empty profile')}},
//                                s{setValue: {shame: 'TextField-phone.Input', value: ''}},
//                                s{setValue: {shame: 'TextField-aboutMe.Input', value: ''}},
//                                s{click: {shame: 'button-primary', timestamp: '2016/08/15 20:12:37'}},
//                                s{step: {kind: 'state', long: t('Bunch of errors')}},
//                                s{assert: {$tag: '4fc3f3d3-2806-4c99-9769-6d046cd7a985', expected: '---generated-shit---'}},
//
//                                s{step: {kind: 'action', long: t('Entering phone: some junk')}},
//                                s{setValue: {shame: 'TextField-phone.Input', value: 'qwerty'}},
//                                s{click: {shame: 'button-primary', timestamp: '2016/08/15 20:16:20'}},
//                                s{step: {kind: 'state', long: t('No way')}},
//                                s{assert: {$tag: '243f5834-48a4-496b-8770-371ac0c94b19', expected: '---generated-shit---'}},
//
//                                s{step: {kind: 'action', long: t('Entering phone: too long')}},
//                                s{setValue: {shame: 'TextField-phone.Input', value: '012345678901234567890'}},
//                                s{click: {shame: 'button-primary', timestamp: '2016/08/16 11:56:01'}},
//                                s{step: {kind: 'state', long: t('No way')}},
//                                s{assert: {$tag: 'a07d90e4-60a6-45c1-a5c6-cd217a01a933', expected: '---generated-shit---'}},
//                                
//                                s{step: {kind: 'action', long: t('Entering phone: too few digits')}},
//                                s{setValue: {shame: 'TextField-phone.Input', value: '--3234--++--'}},
//                                s{click: {shame: 'button-primary', timestamp: '2016/08/16 11:56:05'}},
//                                s{step: {kind: 'state', long: t('No way')}},
//                                s{assert: {$tag: '3f290096-9162-419b-835d-ec2b02f8f5ca', expected: '---generated-shit---'}},
//
//                                s{step: {kind: 'action', long: t('Entering phone: something sane')}},
//                                s{setValue: {shame: 'TextField-phone.Input', value: '+38 (068) 9110032'}},
//                                s{click: {shame: 'button-primary', timestamp: '2016/08/16 12:02:30'}},
//                                s{step: {kind: 'state', long: t('-1 error')}},
//                                s{assert: {$tag: 'd072d3c4-38c9-481a-9fec-0ecd9c5601a2', expected: '---generated-shit---'}},
//
//                                s{step: {kind: 'action', long: t('Entering about: too long')}},
//                                s{setValue: {shame: 'TextField-aboutMe.Input', value: common.LONG_SHIT_301}},
//                                s{click: {shame: 'button-primary', timestamp: '2016/08/16 14:01:33'}},
//                                s{step: {kind: 'state', long: t('TODO State description')}},
//                                s{assert: {$tag: 'e3c1c929-27af-4742-850c-1c1b7cb2ff51', expected: '---generated-shit---'}},
//
//                                s{step: {kind: 'action', long: t('Entering aboutMe: something sane')}},
//                                s{setValue: {shame: 'TextField-aboutMe.Input', value: 'I am a fucking bitch. No, really. Wanna have one for the team?'}},
//                                s{click: {shame: 'button-primary', timestamp: '2016/08/16 14:55:45'}},
//                                s{step: {kind: 'state', long: t('TODO State description')}},
//                                s{assert: {$tag: '9219ded3-54e4-4d6b-bf1b-63615cf8a56e', expected: '---generated-shit---'}},
//
//                                s{actionPlaceholder: {$tag: '6bcd180a-9701-4e84-babe-99253f49e44b'}},
//                            s{endSection: {}},
//                        s{endSection: {}},
//                    ]
//                }
                
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
                                expectAll(
                                    expectHeaderControls({filter: '2approve', ordering: 'desc'}),
                                    expectItemEditorFormControls({state: 'profile-approval-pending'}))}},
                            
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
                                expectAll(
                                    expectHeaderControls({filter: 'all', ordering: 'desc'}),
                                    expectItemEditorFormControls({state: 'cool'}))}},
                            
                            // @wip rejection
                            s{step: {kind: 'action', long: t('Choose "Rejected" status')}},
                            s{setValue: {shame: 'chunk-i000.item-i000.SelectField-state.Select', value: 'profile-rejected'}},
                            s{step: {kind: 'state', long: t('Rejection reason field appears')}},
                            s{assert: {$tag: 'fcdac58b-e864-4a21-b3a2-47f29a1fbba9', expected: '---generated-shit---', expectedExtender:
                                expectAll(
                                    expectHeaderControls({filter: 'all', ordering: 'desc'}),
                                    expectItemEditorFormControls({state: 'profile-rejected'}))}},
                            
                            s{step: {kind: 'action', long: t('Enter something into rejection reason, then change "Rejected" to something else')}},
                            s{setValue: {shame: 'chunk-i000.item-i000.TextField-profileRejectionReason.Input', value: 'Well... er...'}},
                            s{setValue: {shame: 'chunk-i000.item-i000.SelectField-state.Select', value: 'cool'}},
                            s{step: {kind: 'state', long: t('Rejection reason field disappears')}},
                            s{assert: {$tag: 'a81d2f49-aa12-45ea-92a2-fc6bf4ad0cf6', expected: '---generated-shit---', expectedExtender:
                                expectAll(
                                    expectHeaderControls({filter: 'all', ordering: 'desc'}),
                                    expectItemEditorFormControls({state: 'cool'}))}},
                            
                            s{step: {kind: 'action', long: t('Select "Rejected" again')}},
                            s{setValue: {shame: 'chunk-i000.item-i000.SelectField-state.Select', value: 'profile-rejected'}},
                            s{step: {kind: 'state', long: t('Rejection reason is kept')}},
                            s{assert: {$tag: '2e650433-d5f6-4c94-ba5a-b3ff07e15865', expected: '---generated-shit---', expectedExtender:
                                expectAll(
                                    expectHeaderControls({filter: 'all', ordering: 'desc'}),
                                    expectItemEditorFormControls({state: 'profile-rejected'}))}},
                            
                            s{step: {kind: 'action', long: t('Try to reject without reason')}},
                            s{setValue: {shame: 'chunk-i000.item-i000.TextField-profileRejectionReason.Input', value: ''}},
                            s{click: {shame: 'chunk-i000.item-i000.button-primary', timestamp: '2016/08/27 13:23:05'}},
                            s{step: {kind: 'state', long: t('No way')}},
                            s{assert: {$tag: 'c2f5de3e-ff25-4c38-80dc-e09d89e0944f', expected: '---generated-shit---', expectedExtender:
                                expectAll(
                                    expectHeaderControls({filter: 'all', ordering: 'desc'}),
                                    expectItemEditorFormControls({state: 'profile-rejected'}))}},

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
                                
                                s{step: {kind: 'action', long: t('Improve "About Me" section')}},
                                s{setValue: {shame: 'TextField-aboutMe.Input', value: 'Пишу тексты за еду. Любые стили и направления.'}},
                                s{click: {shame: 'button-primary', timestamp: '2016/08/28 01:19:00'}},
                                s{step: {kind: 'state', long: t('Got waiting screen')}},
                                s{assert: {$tag: '35a2c077-01b2-407c-beba-4debb789d07d', expected: '---generated-shit---'}},

                                s{actionPlaceholder: {$tag: '80c8c508-ef39-4e07-8be3-3e8a964e984c'}},
                        s{endSection: {}},
                    ]
                }
                
                function dasja3() {
                    return [
                        s{beginSection: {long: t('Dasja accepts improved profile')}},
                            ...selectBrowser(s{
                                clientKind: 'writer', browserName: 'dasja3',
                                stateDescription: t('Dasja comes to do some work')}),
                                
                            s{step: {kind: 'action', long: t('Normal sign in')}},
                            ...signIn(s{email: 'dasja@test.shit.ua', password: 'secret'}),
                            s{assert: {$tag: 'e72560ca-3f35-423a-8d8b-e65006eb26f0', expected: '---generated-shit---'}},
                            s{step: {kind: 'state', long: t('Got dashboard')}},

                            s{step: {kind: 'action', long: t('Click on "Profiles to approve"')}},
                            s{click: {shame: 'section-workPending.profilesToApprove.link', timestamp: '2016/08/28 01:33:48'}},
                            s{step: {kind: 'state', long: t('Bitch somewhat improved her profile')}},
                            s{assert: {$tag: '48b2f0d2-a1f0-446d-8c10-550b9abfd3e3', expected: '---generated-shit---', expectedExtender:
                                expectHeaderControls({filter: '2approve', ordering: 'desc'})}},

                            s{step: {kind: 'action', long: t('Click on pencil')}},
                            s{click: {shame: 'chunk-i000.item-i000.heading.icon-edit', timestamp: '2016/08/28 01:56:33'}},
                            s{step: {kind: 'state', long: t('Edit form opens')}},
                            s{assert: {$tag: '18f8286c-ea61-4820-95c0-9ce3a129edfb', expected: '---generated-shit---', expectedExtender:
                                expectAll(
                                    expectHeaderControls({filter: '2approve', ordering: 'desc'}),
                                    expectItemEditorFormControls({state: 'profile-approval-pending'}))}},

                            s{step: {kind: 'action', long: t('Accept now')}},
                            
                            s{setValue: {shame: 'chunk-i000.item-i000.SelectField-state.Select', value: 'cool'}},
                            s{setValue: {shame: 'chunk-i000.item-i000.TextField-adminNotes.Input', value: 'Free workforce. Good. We\'ll exploit her to death. U-ha-ha-ha...'}},
                            s{click: {shame: 'chunk-i000.item-i000.button-primary', timestamp: '2016/08/28 01:59:30'}},
                            s{step: {kind: 'state', long: t('She is cool now')}},
                            s{assert: {$tag: '5d82460a-268f-43d8-bd00-e04dd671029c', expected: '---generated-shit---', expectedExtender:
                                expectHeaderControls({filter: '2approve', ordering: 'desc'})}},

                            s{actionPlaceholder: {$tag: '70d0bad3-5670-41db-87cb-c6fbb04b5954'}},
                        s{endSection: {}},
                    ]
                }
                
                function vovchok4() {
                    return [
                        s{beginSection: {long: t('Marko Vovchok can finally enter')}},
                            ...selectBrowser(s{
                                clientKind: 'writer', browserName: 'vovchok4',
                                stateDescription: t('The Bitch opens browser')}),
                                
                                s{step: {kind: 'action', long: t('Sign in')}},
                                s{setValue: {shame: 'TextField-email.Input', value: 'vovkulaka@test.shit.ua'}},
                                s{setValue: {shame: 'TextField-password.Input', value: 'fucking-big-generated-secret'}},
                                s{click: {shame: 'button-primary', timestamp: '2016/08/28 02:04:24'}},
                                s{step: {kind: 'state', long: t('Great, they accepted my profile')}},
                                s{assert: {$tag: '5c4d0c0c-8e1f-4dfc-898d-dec7c3e6bbf7', expected: '---generated-shit---'}},
                                
                                s{actionPlaceholder: {$tag: '0d1b694c-a300-4505-ae34-3b829e2655dd'}},
                        s{endSection: {}},
                    ]
                }
                
                function dasja4() {
                    return [
                        s{beginSection: {long: t('Dasja bans bitch')}},
                            ...selectBrowser(s{
                                clientKind: 'writer', browserName: 'dasja4',
                                stateDescription: t('Dasja comes to ban that bitch')}),
                                
                            s{step: {kind: 'action', long: t('Normal sign in')}},
                            ...signIn(s{email: 'dasja@test.shit.ua', password: 'secret'}),
                            s{assert: {$tag: '6bd7cea2-b181-4978-b278-31a86086606f', expected: '---generated-shit---'}},
                            s{step: {kind: 'state', long: t('Got dashboard')}},
                            
                            s{step: {kind: 'action', long: t('Click on "Users"')}},
                            s{click: {shame: 'TopNavItem-admin-users', timestamp: '2016/08/28 08:36:33'}},
                            s{step: {kind: 'state', long: t('Got users, bitch is here')}},
                            s{assert: {$tag: '4c7fa9d2-c993-4fdf-91e6-fc7e45d49e3b', expected: '---generated-shit---', expectedExtender:
                                expectHeaderControls({filter: 'all', ordering: 'desc'})}},
                            
                            s{step: {kind: 'action', long: t('Click on pencil to edit bitch')}},
                            s{click: {shame: 'chunk-i000.item-i000.heading.icon-edit', timestamp: '2016/08/28 08:38:01'}},
                            s{step: {kind: 'state', long: t('Got editing form')}},
                            s{assert: {$tag: '4290992b-9d2b-4ab2-97e6-e32c87e9ea0c', expected: '---generated-shit---', expectedExtender:
                                expectAll(
                                    expectHeaderControls({filter: 'all', ordering: 'desc'}),
                                    expectItemEditorFormControls({state: 'cool'}))}},
                            
                            s{step: {kind: 'action', long: t('Choose "Banned" from the state list')}},
                            s{setValue: {shame: 'chunk-i000.item-i000.SelectField-state.Select', value: 'banned'}},
                            s{step: {kind: 'state', long: t('Ban reason field appears')}},
                            s{assert: {$tag: 'fa31639d-adae-4ba5-8b47-a58b75f6e17a', expected: '---generated-shit---', expectedExtender:
                                expectAll(
                                    expectHeaderControls({filter: 'all', ordering: 'desc'}),
                                    expectItemEditorFormControls({state: 'banned'}))}},

                            s{step: {kind: 'action', long: t('Try to submit without giving a reason')}},
                            s{click: {shame: 'chunk-i000.item-i000.button-primary', timestamp: '2016/08/28 10:00:48'}},
                            s{step: {kind: 'state', long: t('No way')}},
                            s{assert: {$tag: '4ec74b0c-df11-4228-a327-92b4a3c1ac64', expected: '---generated-shit---', expectedExtender:
                                expectAll(
                                    expectHeaderControls({filter: 'all', ordering: 'desc'}),
                                    expectItemEditorFormControls({state: 'banned'}))}},

                            s{step: {kind: 'action', long: t('Give some reason')}},
                            s{setValue: {shame: 'chunk-i000.item-i000.TextField-banReason.Input', value: 'Мы тебя больше не хотим'}},
                            s{click: {shame: 'chunk-i000.item-i000.button-primary', timestamp: '2016/08/28 10:01:48'}},
                            s{step: {kind: 'state', long: t('She is very red now')}},
                            s{assert: {$tag: '06325345-e660-4f6c-989c-aafdbac35d55', expected: '---generated-shit---', expectedExtender:
                                expectAll(
                                    expectHeaderControls({filter: 'all', ordering: 'desc'}))}},

                            s{step: {kind: 'action', long: t('Show only "Cool"')}},
                            s{setValue: {shame: 'Select-filter', value: 'cool'}},
                            s{step: {kind: 'state', long: t('Got no bitch')}},
                            s{assert: {$tag: '0213b214-02e0-41ce-9596-a4aa4d50389e', expected: '---generated-shit---', expectedExtender:
                                expectAll(
                                    expectHeaderControls({filter: 'cool', ordering: 'desc'}))}},

                            s{step: {kind: 'action', long: t('Show only "Banned"')}},
                            s{setValue: {shame: 'Select-filter', value: 'banned'}},
                            s{step: {kind: 'state', long: t('Got bitch')}},
                            s{assert: {$tag: 'ae6f3952-0a58-4542-8c48-68c6654e6626', expected: '---generated-shit---', expectedExtender:
                                expectAll(
                                    expectHeaderControls({filter: 'banned', ordering: 'desc'}))}},


                            s{actionPlaceholder: {$tag: 'de41a9d0-c319-455c-8c6d-a2f0cf894721'}},
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
                
                function expectAll(...funs) {
                    return function({expected}) {
                        for (const fun of funs) {
                            fun({expected})
                        }
                    }
                }
                
                function expectItemEditorFormControls({chunkIndex='000', itemIndex='000', state}) {
                    return function({expected}) {
                        asn(expected, {
                            [`chunk-i${chunkIndex}.item-i${itemIndex}.SelectField-state.Select.item-i000.title`]: `Прохладный`,
                            [`chunk-i${chunkIndex}.item-i${itemIndex}.SelectField-state.Select.item-i000.value`]: `cool`,
                            [`chunk-i${chunkIndex}.item-i${itemIndex}.SelectField-state.Select.item-i001.title`]: `Ждет аппрува профиля`,
                            [`chunk-i${chunkIndex}.item-i${itemIndex}.SelectField-state.Select.item-i001.value`]: `profile-approval-pending`,
                            [`chunk-i${chunkIndex}.item-i${itemIndex}.SelectField-state.Select.item-i002.title`]: `Профиль завернут`,
                            [`chunk-i${chunkIndex}.item-i${itemIndex}.SelectField-state.Select.item-i002.value`]: `profile-rejected`,
                            [`chunk-i${chunkIndex}.item-i${itemIndex}.SelectField-state.Select.item-i003.title`]: `Забанен`,
                            [`chunk-i${chunkIndex}.item-i${itemIndex}.SelectField-state.Select.item-i003.value`]: `banned`,
                            [`chunk-i${chunkIndex}.item-i${itemIndex}.SelectField-state.label`]: `Статус`,
                        })
                        
                        if (state === 'cool') {
                            asn(expected, {
                                [`chunk-i${chunkIndex}.item-i${itemIndex}.SelectField-state.Select.selected.title`]: `Прохладный`,
                                [`chunk-i${chunkIndex}.item-i${itemIndex}.SelectField-state.Select.selected.value`]: `cool`,
                            })
                        }
                        else if (state === 'profile-approval-pending') {
                            asn(expected, {
                                [`chunk-i${chunkIndex}.item-i${itemIndex}.SelectField-state.Select.selected.title`]: `Ждет аппрува профиля`,
                                [`chunk-i${chunkIndex}.item-i${itemIndex}.SelectField-state.Select.selected.value`]: `profile-approval-pending`,
                            })
                        }
                        else if (state === 'profile-rejected') {
                            asn(expected, {
                                [`chunk-i${chunkIndex}.item-i${itemIndex}.SelectField-state.Select.selected.title`]: `Профиль завернут`,
                                [`chunk-i${chunkIndex}.item-i${itemIndex}.SelectField-state.Select.selected.value`]: `profile-rejected`,
                            })
                        }
                        else if (state === 'banned') {
                            asn(expected, {
                                [`chunk-i${chunkIndex}.item-i${itemIndex}.SelectField-state.Select.selected.title`]: `Забанен`,
                                [`chunk-i${chunkIndex}.item-i${itemIndex}.SelectField-state.Select.selected.value`]: `banned`,
                            })
                        }
                        else {
                            raise(`Weird state: ${state}`)
                        }
                    }
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


