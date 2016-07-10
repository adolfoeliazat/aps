/*
 * APS
 * 
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

#pragma instrument-ui-rendering

MODE = 'debug'
DEBUG_SIMULATE_SLOW_NETWORK = true
DEBUG_RPC_LAG_FOR_MANUAL_TESTS = 1000
BOOTSTRAP_VERSION = 3
BACKEND_URL = 'http://localhost:3100'

require('regenerator-runtime/runtime') // TODO:vgrechka Get rid of this shit, as I don't want to support old browsers anyway

import '../gen/client-expectations'
import static 'into-u/utils-client into-u/ui ./stuff'

global.igniteShit = makeUIShitIgniter({
    Impl({ui}) {
        return {
            isDynamicPage(name) {
                if (CLIENT_KIND === 'customer') return ~customerDynamicPageNames().indexOf(name)
                return ~writerDynamicPageNames().indexOf(name)
            },
            
            privatePageLoader(name) {
                return {
                    orders: ordersPageLoader,
                    
                    async support() {
                        if (ui.urlQuery.thread) {
                            await lala({
                                itemsFun: 'private_getSupportThreadMessages',
                                entityID: ui.urlQuery.thread,
                                pageTitle: res => t(`TOTE`, `Запрос в поддержку № ${res.entity.id}`),
                                emptyMessage: t(`TOTE`, `Странно, здесь ничего нет. А должно что-то быть...`),
                                aboveItems(res) {
                                    return div(
                                        blockquotea({}, res.entity.topic))
                                },
                                dataArrayName: 'supportThreadMessages',
                                renderItem(item, i) {
                                    let rowBackground, lineColor
                                    if (item.sender.kind === 'customer' || item.sender.kind === 'writer') {
                                        rowBackground = WHITE
                                        lineColor = BLUE_GRAY_50
                                    } else {
                                        rowBackground = BLUE_GRAY_50
                                        lineColor = WHITE
                                    }
                                    
                                    return dataItemObject('supportThreadMessage', _=> diva({className: 'row', style: {display: 'flex', flexWrap: 'wrap', backgroundColor: rowBackground, paddingTop: 5, paddingBottom: 5, marginLeft: 0, marginRight: 0}},
                                        diva({className: 'col-sm-3', style: {display: 'flex', flexDirection: 'column', borderRight: `3px solid ${lineColor}`, paddingLeft: 0}},
                                            diva({}, spana({style: {fontWeight: 'bold'}},
                                                t(`TOTE`, `От: `)),
                                                dataField('from', item.sender.first_name + ' ' + item.sender.last_name)),
                                            diva({}, spana({style: {fontWeight: 'bold'}},
                                                t(`TOTE`, `Кому: `)),
                                                dataField('to', item.recipient ? (item.recipient.first_name + ' ' + item.recipient.last_name)
                                                                               : t(`TOTE`, `В рельсу`))),
                                            diva({style: {marginTop: 10}}, dataField('timestamp', timestampString(item.inserted_at)))
                                        ),
                                        diva({className: 'col-sm-9', style: {display: 'flex', flexDirection: 'column', paddingRight: 5}},
                                            dataField('message', item.message))
                                        ))
                                },
                                plusGlyph: 'comment',
                                plusForm: {
                                    primaryButtonTitle: t(`TOTE`, `Запостить`),
                                    cancelButtonTitle: t(`TOTE`, `Передумал`),
                                    fields: {
                                        entityID: {
                                            type: 'hidden',
                                            value: ui.urlQuery.thread,
                                        },
                                        message: {
                                            title: t(`TOTE`, `Сообщение`),
                                            type: 'textarea',
                                            autoFocus: true,
                                        },
                                    },
                                    rpcFun: 'private_createSupportThreadMessage',
                                    async onSuccess(res) {
                                        makeNextRPCNotLaggingInTests()
                                        await ui.pushNavigate(`support.html?thread=${ui.urlQuery.thread}`)
                                    },
                                },
                            })
                        } else if (!ui.urlQuery.thread) {
                            await lala({
                                pageTitle: t('Support', 'Поддержка'),
                                itemsFun: 'private_getSupportThreads',
                                emptyMessage: t(`TOTE`, `Запросов в поддержку не было. Чтобы добавить, нажми плюсик вверху.`),
                                renderItem(item, i) {
                                    return t('todo render item')
                                },
                                plusGlyph: 'plus',
                                plusForm: {
                                    primaryButtonTitle: t(`TOTE`, `Запостить`),
                                    cancelButtonTitle: t(`TOTE`, `Не стоит`),
                                    fields: {
                                        topic: {
                                            title: t(`TOTE`, `Тема`),
                                            type: 'text',
                                            autoFocus: true,
                                        },
                                        message: {
                                            title: t(`TOTE`, `Сообщение`),
                                            type: 'textarea',
                                        },
                                    },
                                    rpcFun: 'private_createSupportThread',
                                    async onSuccess(res) {
                                        makeNextRPCNotLaggingInTests()
                                        await ui.pushNavigate(`support.html?thread=${res.entity.id}`)
                                    },
                                },
                            })
                        }
                    },
                
                    dashboard: dashboardPageLoader,
                    profile: profilePageLoader,
                }[name]
                
                
                async function lala({pageTitle, entityID, itemsFun, emptyMessage, plusGlyph='plus', plusForm, aboveItems, dataArrayName, renderItem}) {
                    const res = await ui.rpcSoft({fun: itemsFun, entityID, fromID: 0})
                    pageTitle = fov(pageTitle, res)
                    if (res.error) {
                        return ui.setPage({
                            pageTitle,
                            pageBody: div(errorBanner(res.error))
                        })
                    }
                    
                    let items, showEmptyLabel = true, form, plusButtonVisible = true, plusButtonClass, formClass
                    
                    ui.setPage({
                        pageTitle,
                        pageBody: _=> div(
                            form && diva({className: formClass, style: {marginBottom: 15}}, form),
                            fov(aboveItems, res),
                            run(function renderItems() {
                                if (!res.items.length) {
                                    if (showEmptyLabel) {
                                        return div(emptyMessage)
                                    }
                                    return ''
                                }
                                
                                return dataArray(dataArrayName, _=> div(...res.items.map((item, i) => {
                                    return renderItem(item, i)
                                })))
                            }),
                        ),
                        headerControls: _=> diva({style: {display: 'flex'}},
                            diva({style: {marginRight: 10, marginTop: 8}}, t('Filter here')),
                            plusButtonVisible && button.primary[plusGlyph]({name: 'plus', className: plusButtonClass}, _=> {
                                showEmptyLabel = false
                                plusButtonClass = 'aniMinimize'
                                formClass = 'aniFadeIn'
                                
                                form = ui.Form({
                                    primaryButtonTitle: plusForm.primaryButtonTitle,
                                    cancelButtonTitle: plusForm.cancelButtonTitle,
                                    fields: plusForm.fields,
                                    rpcFun: plusForm.rpcFun,
                                    onCancel() {
                                        plusButtonVisible = true
                                        plusButtonClass = 'aniFadeIn'
                                        form = undefined
                                        ui.updatePage()
                                            
                                        timeoutSet(500, _=> {
                                            plusButtonClass = undefined
                                            ui.updatePage()
                                        })
                                    },
                                    onSuccess: plusForm.onSuccess,
                                })
                                
                                ui.updatePage()
                                
                                timeoutSet(250, _=> {
                                    plusButtonVisible = false
                                    formClass = undefined
                                    ui.updatePageHeader()
                                })
                        }))
                    })
                }
                
                
                function ordersPageLoader() {
                    ui.setPage({
                        pageTitle: t('My Orders', 'Мои заказы'),
                        pageBody: div(
                            )
                    })
                }
                
                
                function dashboardPageLoader() {
                    ui.setPage({
                        pageTitle: t('Dashboard', 'Панель'),
                        pageBody: div(
                            diva({className: 'row'},
                                diva({className: 'col-sm-6'},
                                    sectionTitle(t('Account', 'Аккаунт')),
                                    sectionLinks(
                                        [t('Sign out', 'Выйти прочь'), _=> {
                                            ui.signOut()
                                        }],
                                        [t('Change password', 'Сменить пароль'), _=> {
                                            dlog('implement change password')
                                        }]
                                    )))
                            )
                    })
                    
                    
                    function sectionTitle(title) {
                        return diva({style: {backgroundColor: BLUE_GRAY_50, fontWeight: 'bold', padding: '2px 5px', marginBottom: 10}}, title)
                    }
                    
                    function sectionLinks(...items) {
                        return ula({className: 'fa-ul', style: {marginLeft: 20}},
                                   ...items.map(([itemTitle, action]) =>
                                       lia({style: {marginBottom: 5}},
                                           ia({className: 'fa fa-li fa-chevron-right', style: {color: BLUE_GRAY_600}}),
                                           link(itemTitle, {style: {color: '#333'}}, action))))
                    }
                }
                
                function profilePageLoader() {
                    let primaryButtonTitle
                    if (ui.getUser().state === 'profile-pending') primaryButtonTitle = t('TOTE', 'Отправить на проверку')
                    else primaryButtonTitle = t('WTF')
                    
                    const form = ui.Form({
                        primaryButtonTitle,
                        fields: {
                            phone: {
                                title: t('Phone', 'Телефон'),
                                type: 'text',
                                attrs: {autoFocus: true},
                            },
                        },
                        rpcFun: 'private_updateProfile',
                        onSuccess(res) {
                            // ui.getUser().state = 'profile-approval-pending'
                            ui.setUser(res.newUser)
                            ui.replaceNavigate('profile.html')
                        },
                    })
                    
                    let pageBody
                    const userState = ui.getUser().state
                    if (userState === 'profile-pending') {
                        pageBody = div(preludeWithOrangeTriangle(t('TOTE', 'Сначала заполняешь профиль. Админ связывается с тобой и активирует аккаунт. Потом все остальное.'), {center: 720}),
                                       form)
                    } else if (userState === 'profile-approval-pending') {
                        pageBody = div(preludeWithHourglass(span(t('TOTE', 'Админ проверяет профиль, жди извещения почтой. Если есть вопросы, можно написать в '),
                                                                 ui.pageLink({title: t('TOTE', 'поддержку'), url: 'support.html', name: 'support'}),
                                                                 t('.'))),
                                       userDisplayForm(ui.getUser()))
                    }
                    
                    ui.setPage({
                        pageTitle: t('Profile', 'Профиль'),
                        pageBody
                    })
                }
            },
            
            renderTopNavbar({highlightedItem}) {
                return renderTopNavbar({clientKind: CLIENT_KIND, highlightedItem, t, ui})
            },
        }
    },
    
    isTestScenarioNameOK(name) {
        if (LANG == 'ua' && CLIENT_KIND === 'customer' && name.startsWith('UA Customer :: ')) return true
        if (LANG == 'ua' && CLIENT_KIND === 'writer' && name.startsWith('UA Writer :: ')) return true
    },
    
    testScenarios({sim}) {
        const drpc = getDebugRPC()
        
        return {
            async 'Something'() {
            },
        
            // ======================================== UA CUSTOMER TEST SCENARIOS ========================================
            
            async 'UA Customer :: Sign Up :: 1'() {
                await drpc({fun: 'danger_clearSentEmails'})
                await drpc({fun: 'danger_killUser', email: 'wilma.blue@test.shit.ua'})
                await drpc({fun: 'danger_fixNextGeneratedPassword', password: '63b2439c-bf18-42c5-9f7a-42d7357f966a'})
                
                await sim.navigate('dashboard.html')
                art.uiState({$tag: '62112552-36ac-47fd-9bac-a4d6a7b3c4d4', expected: {
                    url: `http://aps-ua-customer.local:3012/sign-in.html`,
                    pageHeader: `Вход`,
                    inputs: { email: { value: `` }, password: { value: `` } },
                    errorLabels: {},
                    errorBanner: undefined,
                    displayLabels: {} 
                }})           
                            
                await sim.navigate('sign-up.html')
                art.uiState({$tag: '6aa1c1bf-804b-4f5c-98e5-c081cd6238a0', expected: {
                    url: `http://aps-ua-customer.local:3012/sign-up.html`,
                    pageHeader: `Регистрация`,
                    inputs: 
                     { email: { value: `` },
                       firstName: { value: `` },
                       lastName: { value: `` },
                       agreeTerms: { value: false } },
                    errorLabels: {},
                    errorBanner: undefined,
                    displayLabels: {}
                }})

                // Inputs
                await testGlobal.inputs.email.setValue('wilma.blue@test.shit.ua')
                await testGlobal.inputs.firstName.setValue('Вильма')
                await testGlobal.inputs.lastName.setValue('Блу')
                await testGlobal.inputs.agreeTerms.setValue(true)
                // Action
                testGlobal.buttons.primary.click()
                await art.shitSpinsForMax({$tag: '29832372-ff89-46dd-ba9d-cf54154503f5', max: 2000})
                // Check
                art.textSomewhere({$tag: '853610e2-c607-4ce5-9d60-74744ca63580', expected: 'Все круто. Теперь у тебя есть аккаунт. Пароль мы отправили письмом.'})
                art.uiState({$tag: '361d46a0-6ec1-40c4-a683-bc5263c41bba', expected: {
                    url: `http://aps-ua-customer.local:3012/sign-in.html`,
                    pageHeader: `Вход`,
                    inputs: { email: { value: `` }, password: { value: `` } },
                    errorLabels: {},
                    errorBanner: undefined,
                    displayLabels: {} 
                }})
                await art.sentEmails({$tag: '169a6331-c004-47fd-9b53-05242915d9f7', descr: 'Email with password', expected: [
                    { to: `Вильма Блу <wilma.blue@test.shit.ua>`,
                        subject: `Пароль для APS`,
                        html: dedent(`
                            Привет, Вильма!<br><br>
                            Вот твой пароль: 63b2439c-bf18-42c5-9f7a-42d7357f966a
                            <br><br>
                            <a href="http://aps-ua-customer.local:3012/sign-in.html">http://aps-ua-customer.local:3012/sign-in.html</a>`) } 
                ]})
                
                // Inputs
                await testGlobal.inputs.email.setValue('wilma.blue@test.shit.ua')
                await testGlobal.inputs.password.setValue('63b2439c-bf18-42c5-9f7a-42d7357f966a')
                // Action
                testGlobal.buttons.primary.click()
                await assertShitSpinsForMax({$tag: '96f4aa5d-4f5d-4de4-869b-07f2f6f53b8b', max: 2000})
                assertLinkWithTextSomewhere({$tag: 'aa6eda4b-fc78-43ac-959d-a2eb44f3061f', expected: 'Вильма'})
                art.uiState({$tag: 'd9c42d17-322e-4427-b4ec-d946af422ba0', expected: {
                    url: `http://aps-ua-customer.local:3012/dashboard.html`,
                    pageHeader: `Панель`,
                    inputs: {},
                    errorLabels: {},
                    errorBanner: undefined,
                    displayLabels: {} 
                }})
                
                await sim.navigate('dashboard.html')
                
            },
            
            // ======================================== UA WRITER TEST SCENARIOS ========================================
            
            async 'UA Writer :: Sign Up :: 1    b583c010-f383-4635-a826-3d2bb79f0806'() {
                #hawait drpc({fun: 'danger_clearSentEmails'})
                #hawait drpc({fun: 'danger_killUser', email: 'fred.red@test.shit.ua'})
                
                // ---------- Browser: fred ----------
                
                sim.selectBrowser('fred')
                
                #hawait drpc({fun: 'danger_fixNextGeneratedPassword', password: 'b34b80fb-ae50-4456-8557-399366fe45e4'})
                
                #hawait sim.navigate('dashboard.html')
                art.uiState({$tag: '20059334-7dff-4922-8bf5-ac07999d892d', expected: {
                    url: `http://aps-ua-writer.local:3022/sign-in.html`,
                    pageHeader: `Вход`,
                    inputs: { email: { value: `` }, password: { value: `` } },
                    errorLabels: {},
                    errorBanner: undefined,
                    displayLabels: {} 
                }})
                
                #hawait art.pausePoint({title: 'Before clicking "Create account" link', $tag: 'ee90e201-9429-4577-aea9-0277e66979ad'})
                #hawait testGlobal.links.createAccount.click()
                #hawait art.linkBlinksForMax({$tag: '67abf17a-ab93-4891-a844-54b6baa0c387', name: 'createAccount', max: 2000})
                art.uiState({$tag: 'b1a53c66-21db-42e5-8b0b-4d430b7b4ea6', expected: {
                    url: `http://aps-ua-writer.local:3022/sign-up.html`,
                    pageHeader: `Регистрация`,
                    inputs: 
                     { email: { value: `` },
                       firstName: { value: `` },
                       lastName: { value: `` },
                       agreeTerms: { value: false } },
                    errorLabels: {},
                    errorBanner: undefined,
                    displayLabels: {} 
                }})            
                #hawait art.pausePoint({title: 'After clicking "Create account" link', $tag: '8cf1b341-6f24-4c5b-89e8-aff163a505f5'})
                
                #hawait drpc({fun: 'danger_imposeNextRequestTimestamp', timestamp: '2016-07-03 13:21:36'})
                
                // Inputs
                #hawait testGlobal.inputs.email.setValue('fred.red@test.shit.ua')
                #hawait testGlobal.inputs.firstName.setValue('Фред')
                #hawait testGlobal.inputs.lastName.setValue('Ред')
                #hawait testGlobal.inputs.agreeTerms.setValue(true)
                
                // Action
                #hawait testGlobal.buttons.primary.click()
                #hawait art.shitSpinsForMax({$tag: '39df3f4b-5ca0-4929-bae7-ec1d3bd008ed', max: 2000})
                
                #hawait art.sentEmails({$tag: '024f202c-ee75-44ed-ac26-44154d4caf13', descr: 'Email with password', expected: [
                    { to: `Фред Ред <fred.red@test.shit.ua>`,
                        subject: `Пароль для Writer UA`,
                        html: dedent(`
                            Привет, Фред!<br><br>
                            Вот твой пароль: b34b80fb-ae50-4456-8557-399366fe45e4
                            <br><br>
                            <a href="http://aps-ua-writer.local:3022/sign-in.html">http://aps-ua-writer.local:3022/sign-in.html</a>`) } 
                ]})
                art.uiState({$tag: '24ca0059-e2e9-4fc4-9056-ede17e586029', expected: {
                    url: `http://aps-ua-writer.local:3022/sign-in.html`,
                    pageHeader: `Вход`,
                    inputs: { email: { value: `` }, password: { value: `` } },
                    errorLabels: {},
                    errorBanner: undefined,
                    displayLabels: {} 
                }})            
                art.textSomewhere({$tag: 'bad7019b-a1d3-432c-a376-a872f5b27506', expected: 'Все круто. Теперь у тебя есть аккаунт. Пароль мы отправили письмом.'})

                #hawait art.pausePoint({title: 'Before entering password received by email', $tag: '51076622-8afd-45f5-9fda-a55b98380410'})
                // Inputs
                #hawait testGlobal.inputs.email.setValue('fred.red@test.shit.ua')
                #hawait testGlobal.inputs.password.setValue('b34b80fb-ae50-4456-8557-399366fe45e4')
                // Action
                #hawait testGlobal.buttons.primary.click()
                #hawait art.shitSpinsForMax({$tag: 'd880053c-0f24-46ec-8c47-c635e91d6a39', max: 2000})

                art.uiState({$tag: '4d88eed7-d800-4a00-bfea-6b011329eaf0', expected: {
                    url: `http://aps-ua-writer.local:3022/profile.html`,
                    pageHeader: `Профиль`,
                    inputs: { phone: { value: `` } },
                    errorLabels: {},
                    errorBanner: undefined,
                    displayLabels: {} 
                }})                        
                art.textSomewhere({$tag: 'bad7019b-a1d3-432c-a376-a872f5b27506', expected: 'Фред'})
                
                art.noTextSomewhere({$tag: '4d0713f8-ccfb-4d05-b064-3987492852a5', unexpected: 'Мои заказы'})
                art.noTextSomewhere({$tag: 'a3e73a3e-8ed7-4a69-b748-e955ae4fd606', unexpected: 'Аукцион'})
                
                // Inputs
                #hawait testGlobal.inputs.phone.setValue('')
                // Action
                #hawait testGlobal.buttons.primary.click()
                #hawait art.shitSpinsForMax({$tag: 'fbe5bc76-cf5a-4ed4-90af-a815784cfd1e', max: 2000})
                art.uiState({$tag: '80db2840-cf3b-428e-8f7a-3a447f94d93a', expected: {
                    url: `http://aps-ua-writer.local:3022/profile.html`,
                    pageHeader: `Профиль`,
                    inputs: { phone: { value: `` } },
                    errorLabels: { phone: { title: `Телефон обязателен` } },
                    errorBanner: `Пожалуйста, исправьте ошибки ниже`,
                    displayLabels: {}
                }})

                // Inputs
                #hawait testGlobal.inputs.phone.setValue('adsfasdf')
                // Action
                #hawait testGlobal.buttons.primary.click()
                #hawait art.shitSpinsForMax({$tag: '2d6f5c02-1eae-49cb-9c5a-0509a4f29e05', max: 2000})
                art.uiState({$tag: '24d5e9b2-0dac-40d6-94e8-57d0cfe00c9b', expected: {
                    url: `http://aps-ua-writer.local:3022/profile.html`,
                    pageHeader: `Профиль`,
                    inputs: { phone: { value: `adsfasdf` } },
                    errorLabels: { phone: { title: `Странный телефон какой-то` } },
                    errorBanner: `Пожалуйста, исправьте ошибки ниже`,
                    displayLabels: {}
                }})
                
                #hawait drpc({fun: 'danger_imposeNextRequestTimestamp', timestamp: '2016-07-03 13:24:51'})
                // Inputs
                #hawait testGlobal.inputs.phone.setValue('123-45-67')
                // Action
                #hawait testGlobal.buttons.primary.click()
                #hawait art.shitSpinsForMax({$tag: 'e804da7e-6d1e-4fe4-a40e-e7697cb23622', max: 2000})
                
                art.uiState({$tag: '47a1c72b-4813-4dcd-a2fc-25ca3b739a92', expected: {
                    url: `http://aps-ua-writer.local:3022/profile.html`,
                    pageHeader: `Профиль`,
                    inputs: {},
                    errorLabels: {},
                    errorBanner: undefined,
                    displayLabels: 
                     { first_name: { content: `Фред` },
                       last_name: { content: `Ред` },
                       email: { content: `fred.red@test.shit.ua` },
                       phone: { content: `123-45-67` },
                       inserted_at: { content: `03/07/2016 16:21:36 (Киев)` },
                       profile_updated_at: { content: `03/07/2016 16:24:51 (Киев)` } } 
                }})
                
                #hawait art.pausePoint({title: 'Before clicking "Support" link', $tag: 'c7d7eed5-df24-4db3-a361-9da4470c4bd1'})
                // Action
                #hawait testGlobal.links.support.click()
                #hawait art.linkBlinksForMax({$tag: 'eceeb886-f96e-4baa-a0c1-e75cc79d4e84', name: 'support', max: 2000})
                art.textSomewhere({$tag: '35738e20-16f0-4657-bdc1-60ba524d011b', expected: 'Запросов в поддержку не было. Чтобы добавить, нажми плюсик вверху.'})
                art.uiState({$tag: '0f630ccd-9936-4d27-ac1c-4d391a184e79', expected: {
                    url: `http://aps-ua-writer.local:3022/support.html`,
                    pageHeader: `Поддержка`,
                    inputs: {},
                    errorLabels: {},
                    errorBanner: undefined,
                    displayLabels: {} 
                }})                
                
                #hawait art.pausePoint({title: 'Before clicking plus', $tag: '328b20bf-9fa3-4633-8ee3-fdd80d712bfb'})
                // Action
                #hawait testGlobal.buttons.plus.click()
                art.uiState({$tag: 'b990c804-6621-4b49-879e-57caffc7bcce', expected: {
                    url: `http://aps-ua-writer.local:3022/support.html`,
                    pageHeader: `Поддержка`,
                    inputs: { topic: { value: `` }, message: { value: `` } },
                    errorLabels: {},
                    errorBanner: undefined,
                    displayLabels: {} 
                }})

                // Inputs
                #hawait testGlobal.inputs.topic.setValue('')
                #hawait testGlobal.inputs.message.setValue('')
                // Action
                #hawait testGlobal.buttons.primary.click()
                #hawait art.shitSpinsForMax({$tag: '40894d9d-cc5f-486c-a1c5-213283b754fe', max: 2000})
                art.uiState({$tag: '1c53f5c2-ed8b-4157-a4ef-400c1617f16d', expected: {
                    url: `http://aps-ua-writer.local:3022/support.html`,
                    pageHeader: `Поддержка`,
                    inputs: { topic: { value: `` }, message: { value: `` } },
                    errorLabels: 
                     { topic: { title: `Поле обязательно` },
                       message: { title: `Поле обязательно` } },
                    errorBanner: `Пожалуйста, исправьте ошибки ниже`,
                    displayLabels: {} 
                }})
                
                #hawait drpc({fun: 'danger_imposeNextID', id: 312})
                #hawait drpc({fun: 'danger_imposeNextRequestTimestamp', timestamp: '2016-07-03 13:30:45'})

                // Inputs
                #hawait testGlobal.inputs.topic.setValue('Заапрувьте меня')
                #hawait testGlobal.inputs.message.setValue('И побыстрее давайте!')
                // Action
                #hawait testGlobal.buttons.primary.click()
                #hawait art.shitSpinsForMax({$tag: '08a79fdd-1e1e-48b4-8f80-f2b1695ee096', max: 2000})
                art.uiState({$tag: '848839fb-f059-4cc3-87bf-3875e6deff0f', expected: {
                    url: `http://aps-ua-writer.local:3022/support.html?thread=312`,
                        pageHeader: `Запрос в поддержку № 312`,
                        inputs: {},
                        errorLabels: {},
                        errorBanner: undefined,
                        displayLabels: {},
                        pageData: 
                         { supportThreadMessages: 
                            [ { '$$type': `supportThreadMessage`,
                                from: `Фред Ред`,
                                to: `В рельсу`,
                                timestamp: `03/07/2016 16:30:45`,
                                message: `И побыстрее давайте!` } ] } 
                }})

                // Action
                #hawait testGlobal.buttons.plus.click()
                art.uiState({$tag: '01cb87af-fe6c-42fe-a490-5943a5bb5d1d', expected: {
                    url: `http://aps-ua-writer.local:3022/support.html?thread=312`,
                        pageHeader: `Запрос в поддержку № 312`,
                        inputs: { message: { value: `` } },
                        errorLabels: {},
                        errorBanner: undefined,
                        displayLabels: {},
                        pageData: 
                         { supportThreadMessages: 
                            [ { '$$type': `supportThreadMessage`,
                                from: `Фред Ред`,
                                to: `В рельсу`,
                                timestamp: `03/07/2016 16:30:45`,
                                message: `И побыстрее давайте!` } ] } 
                }})
                
                // Inputs
                await testGlobal.inputs.message.setValue('')
                // Action
                #hawait testGlobal.buttons.primary.click()
                #hawait art.shitSpinsForMax({$tag: '644b634f-e8a3-4c4d-be61-69970a4082b0', max: 2000})
                art.uiState({$tag: '24f268a1-d56c-4f1f-a10f-4d637ff4e2b4', expected: {
                    url: `http://aps-ua-writer.local:3022/support.html?thread=312`,
                        pageHeader: `Запрос в поддержку № 312`,
                        inputs: { message: { value: `` } },
                        errorLabels: { message: { title: `Поле обязательно` } },
                        errorBanner: `Пожалуйста, исправьте ошибки ниже`,
                        displayLabels: {},
                        pageData: 
                         { supportThreadMessages: 
                            [ { '$$type': `supportThreadMessage`,
                                from: `Фред Ред`,
                                to: `В рельсу`,
                                timestamp: `03/07/2016 16:30:45`,
                                message: `И побыстрее давайте!` } ] } 
                }})
                
                #hawait drpc({fun: 'danger_imposeNextRequestTimestamp', timestamp: '2016-07-03 13:33:17'})
                
                // Inputs
                await testGlobal.inputs.message.setValue('Че за фигня? Где админы? Почему так долго все?')
                // Action
                #hawait testGlobal.buttons.primary.click()
                #hawait art.shitSpinsForMax({$tag: '0133f044-47fc-419a-8aed-93350d909fb5', max: 2000})
                art.uiState({$tag: 'de45a710-a428-4e2c-ac2f-e918850e4dd8', expected: {
                    url: `http://aps-ua-writer.local:3022/support.html?thread=312`,
                        pageHeader: `Запрос в поддержку № 312`,
                        inputs: {},
                        errorLabels: {},
                        errorBanner: undefined,
                        displayLabels: {},
                        pageData: 
                         { supportThreadMessages: 
                            [ { '$$type': `supportThreadMessage`,
                                from: `Фред Ред`,
                                to: `В рельсу`,
                                timestamp: `03/07/2016 16:33:17`,
                                message: `Че за фигня? Где админы? Почему так долго все?` },
                              { '$$type': `supportThreadMessage`,
                                from: `Фред Ред`,
                                to: `В рельсу`,
                                timestamp: `03/07/2016 16:30:45`,
                                message: `И побыстрее давайте!` } ] } 
                }})


                // ---------- Browser: todd ----------
                
                sim.selectBrowser('todd')
                #hawait sim.navigate('dashboard.html')
                #hawait art.pausePoint({title: 'Now Todd from support comes into play', $tag: '4dbe962a-9a7d-4d43-bcc2-7e1e2f8785a8'})
                

                // ---------- Browser: fred ----------
                
                sim.selectBrowser('fred')
                
                
                //art.boom('1fbfe070-6d05-4fdf-9bf1-f250cfb7089a')

                // @ctx test
                
                // @ctx templates
                // #hawait art.pausePoint({title: '', $tag: ''})
            },
        }
    },
})

export function renderTopNavbar({clientKind, highlightedItem, t, ui}) {
    let proseItems
    if (clientKind === 'customer') {
        proseItems = [
            ['why', t(`Why Us?`, `Почему мы?`)],
            ['prices', t(`Prices`, `Цены`)],
            ['samples', t(`Samples`, `Примеры`)],
            ['faq', t(`FAQ`, `ЧаВо`)],
            ['contact', t(`Contact Us`, `Связь`)],
            ['blog', t(`Blog`, `Блог`)],
        ]
    } else {
        proseItems = [
            ['why', t(`Why Us?`, `Почему мы?`)],
            ['prices', t(`Prices`, `Цены`)],
            ['faq', t(`FAQ`, `ЧаВо`)],
        ]
    }
    
    let privateItems
    if (ui && ui.getUser()) {
        if (clientKind === 'customer') {
            privateItems = [
                ['orders', t(`My Orders`, `Мои заказы`)],
                ['support', t(`Support`, `Поддержка`)],
            ]
        } else {
            privateItems = compact([
                ui.getUser().state === 'cool' && ['orders', t(`My Orders`, `Мои заказы`)],
                ui.getUser().state === 'cool' && ['store', t(`Store`, `Аукцион`)],
                ['profile', t(`Profile`, `Профиль`)],
                ['support', t(`Support`, `Поддержка`)]
            ])
        }
    }
    
    let leftNavbarItems, rightNavbarItem
    if (ui && ui.getUser()) {
        let dropdownAStyle
        if (proseItems.some(x => x[0] === highlightedItem)) {
            dropdownAStyle = {backgroundColor: '#e7e7e7'}
        }
        const liaid = puid()
        leftNavbarItems = [
            lia({className: 'dropdown'},
                aa({href: '#', className: 'dropdown-toggle skipClearMenus', style: dropdownAStyle, 'data-toggle': 'dropdown', role: 'button'}, t(`Prose`, `Проза`), spana({className: 'caret', style: {marginLeft: 5}})),
                ula({className: 'dropdown-menu'},
                    ...proseItems.map(itemToLia))),
            ...privateItems.map(itemToLia)
        ]
        rightNavbarItem = itemToLia(['dashboard', t(ui.getUser().first_name)])
    } else {
        leftNavbarItems = proseItems.map(itemToLia)
        rightNavbarItem = itemToLia(['sign-in', t(`Sign In`, `Вход`)])
    }
    
    return nava({className: 'navbar navbar-default navbar-fixed-top'},
               diva({className: 'container-fluid'},
                   diva({className: 'navbar-header'},
                       makeLink('home', clientKind === 'customer' ? 'APS' : t('Writer', 'Писец'), 'navbar-brand')),
                       
                   diva({style: {textAlign: 'left'}},
                       ula({id: 'leftNavbar', className: 'nav navbar-nav', style: {float: 'none', display: 'inline-block', verticalAlign: 'top'}},
                           ...leftNavbarItems),
                       ula({id: 'rightNavbar', className: 'nav navbar-nav navbar-right'},
                           rightNavbarItem))))
                           
    
    function itemToLia([name, title]) {
        return lia({className: highlightedItem === name ? 'active' : ''},
            makeLink(name, title))
    }
                           
    function makeLink(name, title, className) {
        const id = puid()
        const href = name === 'home' ? '/' : `${name}.html`
        
        let dleft = 0, dwidth = 0
        if (name === 'home') { // XXX For some reason jQuery cannot find width/offset of navbar-header element precisely
            dleft = -15
            dwidth = 15
        }
        
        return elcl({
            render() {
                return aa({id, className, href}, title)
            },
            
            componentDidMount() {
                // XXX Have to add event handler in weird way in order to prevent Bootstrap from hiding dropdown.
                //     It turned out, React doesn't actually add event handlers on elements, that's why e.stopPropagation()
                //     in onClick(e) doesn't cancel non-React handlers on upper-level elements.
                //
                //     https://facebook.github.io/react/docs/interactivity-and-dynamic-uis.html#under-the-hood-autobinding-and-event-delegation
                
                byid(id).on('click', async function(e) {
                    e.preventDefault()
                    e.stopPropagation()
                    effects.blinkOn(byid(id).parent(), {fixed: true, dleft, dwidth})
                    
                    history.pushState(null, '', href)
                    
                    if (DEBUG_SIMULATE_SLOW_NETWORK) {
                        await delay(1000)
                    }
                    
                    await ui.loadPageForURL()
                    
                    setTimeout(_=> {
                        effects.blinkOff()
                        bsClearMenus()
                    }, 250)
                })
            },
            
            componentWillUnmount() {
                byid(id).off()
            },
        })
    }
}



export function customerDynamicPageNames() {
    return tokens('test sign-in sign-up dashboard orders support')
}

export function writerDynamicPageNames() {
    return tokens('test sign-in sign-up dashboard orders support store users profile')
}

function userDisplayForm(user) {
    return diva({},
        diva({className: 'row'},
            diva({className: 'col-sm-3'},
                diva({className: 'form-group'},
                    labela({}, t(`TOTE`, `Имя`)),
                    diva({}, displayLabel({name: 'first_name', content: user.first_name})))),
            diva({className: 'col-sm-3'},
                diva({className: 'form-group'},
                    labela({}, t(`TOTE`, `Фамилия`)),
                    diva({}, displayLabel({name: 'last_name', content: user.last_name})))),
            diva({className: 'col-sm-3'},
                diva({className: 'form-group'},
                    labela({}, t(`TOTE`, `Почта`)),
                    diva({}, displayLabel({name: 'email', content: user.email})))),
            diva({className: 'col-sm-3'},
                diva({className: 'form-group'},
                    labela({}, t(`TOTE`, `Телефон`)),
                    diva({}, displayLabel({name: 'phone', content: user.phone})))),
        ),
//        diva({className: 'row'},
//            diva({className: 'col-sm-4'},
//                diva({className: 'form-group'},
//                    labela({}, t(`TOTE`, `Телефон`)),
//                    diva({}, displayLabel({name: 'phone', content: user.phone})))),
//        ),
        diva({className: 'row'},
            diva({className: 'col-sm-3'},
                diva({className: 'form-group'},
                    labela({}, t(`TOTE`, `Аккаунт создан`)),
                    diva({}, displayLabel({name: 'inserted_at', content: timestampString(user.inserted_at, {includeTZ: true})})))),
            diva({className: 'col-sm-3'},
                diva({className: 'form-group'},
                    labela({}, t(`TOTE`, `Профиль изменен`)),
                    diva({}, displayLabel({name: 'profile_updated_at', content: timestampString(user.profile_updated_at, {includeTZ: true})})))),
        ),
    )
}

clog('Client code is kind of loaded')



//------------------------------ SHIT BEGINS HERE ------------------------------


//async function zzzzznormalInit() {
//    
//    if (MODE === 'debug' && !debugShitInitialized) {
//        window.addEventListener('keydown', e => {
//            if (e.ctrlKey && e.altKey && e.key === 'k') return captureState()
//            if (e.ctrlKey && e.altKey && e.key === 'i') return captureInputs()
//            if (e.ctrlKey && e.altKey && e.key === 'a') return art.uiState({$tag: 'just-showing-actual', expected: undefined})
//        })
//        
//        
//        
//        debugShitInitialized = true
//    }
//    
//    
//    
//    if (false) { // TODO:vgrechka @kill
//        spaifyNavbar()
//        
//        window.onpopstate = function(e) {
//            loadPageForURL()
//        }
//        
//        const userJSON = localStorage.getItem('user') // TODO:vgrechka This can throw (according to MDN), should handle
//        if (userJSON) {
//            user = JSON.parse(userJSON)
//        }
//    }
//    
//    if (testScenarioToRun) {
//        
//        window.locationProxy = {
//            set href(x) {
//                history.replaceState(null, '', x)
//            }
//        }
//        
//        
//    } else {
//    }
//    
//    
//    function showProfile() {
//    }
//    
//    function showSupport() {
//    }
//    
//    function showOrders() {
//    }
//    
//    function showDashboard() {
//    }
//    
//    
//    
//    
//    
//    
//    function assertLinkWithTextSomewhere({$tag, expected}) {
//        for (const a of $('a')) {
//            if (a.text === expected) return
//        }
//        uiAssert(false, `I want a link with following text somewhere: [${expected}]`, jumpToShitDetailsUI($tag))
//    }
//    
//    function assertHref({$tag, expected}) {
//        uiAssert(document.location.href === expected, `I want to be at following URL: [${expected}]`, jumpToShitDetailsUI($tag))
//    }
//            
//    
//    function failForJumping(message, $tag) {
//        uiAssert(false, message, jumpToShitDetailsUI($tag))
//    }
//    
//
//    
//    
//
//    function assertErrorBanner(expected) {
//        uiAssert(testGlobal.errorBanner === expected, `I want error banner [${expected}]`)
//    }
//
//    function assertNoErrorBanner() {
//        uiAssert(testGlobal.errorBanner === undefined, `I don't want an error banner hanging here`)
//    }
//
//
//    function assertNoErrorLabels() {
//        uiAssert(isEmpty(testGlobal.errorLabels), `I don't want any error labels here`)
//    }
//
//    function assertErrorLabelTitlesExactly(...expected) {
//        const actual = values(testGlobal.errorLabels).map(x => x.title)
//        const expectedDescr = expected.map(x => `[${x}]`).join(', ')
//        uiAssert(deepEquals(sortBy(expected), sortBy(actual)), `I want exactly following error labels: ${expectedDescr}`)
//    }
//
//
//    function assertErrorLabelTitle(expectedTitle) {
//        uiAssert(ofind(testGlobal.errorLabels, x => x.title === expectedTitle), `I want error label [${expectedTitle}] on screen`)
//    }
//
//    function assertFail() {
//        uiAssert(false, 'Just failing here, OK?')
//    }
//    
//
//
//    function assertShitSpins({$tag}) {
//        assertSpins({name: 'shit'})
//    }
//    
////    async function assertHeaderShitSpinsForMax({$tag, max}) {
////        await assertSpinsForMax({$tag, name: 'headerShit', max})
////    }
////
////    function assertHeaderShitSpins() {
////        assertSpins({name: 'headerShit'})
////    }
//    
//    function uiFail(errorMessage) {
//        uiAssert(false, errorMessage)
//    }
//    
//
//    function simulatePopulateFields(data) {
//        for (const [name, value] of toPairs(data)) {
//            const functionName = 'simulate_setControlValue_' + name
//            const setValue = window[functionName]
//            if (!setValue) raise('I want function ' + functionName)
//            setValue(value)
//        }
//    }
//
//    // TODO:vgrechka Use testGlobal.buttons[...].click()
//    function simulateClick(name) {
//        const functionName = 'simulate_click_' + name
//        const click = window[functionName]
//        if (!click) raise('I want function ' + functionName)
//        click()
//    }
//
//
//}

        
//                    spana({className: 'fa-stack fa-lg'},
//                        el('i', {className: 'fa fa-circle fa-stack-2x', style: {color: LIGHT_GREEN_700}}),
//                        el('i', {className: 'fa fa-check fa-stack-1x fa-inverse'})),

