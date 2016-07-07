/*
 * APS
 * 
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

#pragma instrument-ui-rendering

MODE = 'debug'
DEBUG_SIMULATE_SLOW_NETWORK = true
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
                        render(await ui.rpcSoft({fun: 'private_getSupportThreads'}))
                        
                        function render(res) {
                            if (res.error) {
                                return ui.setPage({
                                    pageTitle: t('Support', 'Поддержка'),
                                    pageBody: div(errorBanner(res.error))
                                })
                            }
                            
                            // dlogs('messages', res.messages)
                            const messageDivs = []
//                            for (const msg of res.messages) {
//                                let fromTitle
//                                if (msg.sender_id === ui.getUser().id) {
//                                    fromTitle = t(`TOTE`, `Я`)
//                                } else {
//                                    fromTitle = msg.sender_name
//                                }
//                                messageDivs.push(diva({style},
//                                    diva({}, timestampString(msg.inserted_at)),
//                                    diva({}, fromTitle),
//                                    diva({}, toTitle),
//                                    diva({}, msg.message),
//                                    ))
//                            }
                            
                            const form = ui.Form({
                                primaryButtonTitle: t(`TOTE`, `Создать тему`),
                                fields: {
                                    topic: {
                                        title: t(`TOTE`, `Тема`),
                                        type: 'text',
                                        attrs: {autoFocus: true},
                                    },
                                    message: {
                                        title: t(`TOTE`, `Сообщение`),
                                        type: 'textarea',
                                    },
                                },
                                rpcFun: 'private_createSupportThread',
                                onSuccess(res) {
                                    render(res)
                                },
                            })
                            
                            ui.setPage({
                                pageTitle: t('Support', 'Поддержка'),
                                pageBody: div(...messageDivs, form)
                            })
                        }
                    },
                
                    dashboard: dashboardPageLoader,
                    profile: profilePageLoader,
                }[name]
                
                
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
                                                                 t('.')), {center: 720}),
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
                testGlobal.inputs.email.value = 'wilma.blue@test.shit.ua'
                testGlobal.inputs.firstName.value = 'Вильма'
                testGlobal.inputs.lastName.value = 'Блу'
                testGlobal.inputs.agreeTerms.value = true
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
                testGlobal.inputs.email.value = 'wilma.blue@test.shit.ua'
                testGlobal.inputs.password.value = '63b2439c-bf18-42c5-9f7a-42d7357f966a'
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
                await drpc({fun: 'danger_clearSentEmails'})
                await drpc({fun: 'danger_killUser', email: 'fred.red@test.shit.ua'})
                await drpc({fun: 'danger_fixNextGeneratedPassword', password: 'b34b80fb-ae50-4456-8557-399366fe45e4'})
                
                await sim.navigate('dashboard.html')
                art.uiState({$tag: '20059334-7dff-4922-8bf5-ac07999d892d', expected: {
                    url: `http://aps-ua-writer.local:3022/sign-in.html`,
                    pageHeader: `Вход`,
                    inputs: { email: { value: `` }, password: { value: `` } },
                    errorLabels: {},
                    errorBanner: undefined,
                    displayLabels: {} 
                }})
                
                testGlobal.links.createAccount.click()
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
                
                await drpc({fun: 'danger_imposeNextRequestTimestamp', timestamp: '2016-07-03 13:21:36'})
                
                // Inputs
                testGlobal.inputs.email.value = 'fred.red@test.shit.ua'
                testGlobal.inputs.firstName.value = 'Фред'
                testGlobal.inputs.lastName.value = 'Ред'
                testGlobal.inputs.agreeTerms.value = true
                // Action
                testGlobal.buttons.primary.click()
                await art.shitSpinsForMax({$tag: '39df3f4b-5ca0-4929-bae7-ec1d3bd008ed', max: 2000})
                
                await art.sentEmails({$tag: '024f202c-ee75-44ed-ac26-44154d4caf13', descr: 'Email with password', expected: [
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

                // Inputs
                testGlobal.inputs.email.value = 'fred.red@test.shit.ua'
                testGlobal.inputs.password.value = 'b34b80fb-ae50-4456-8557-399366fe45e4'
                // Action
                testGlobal.buttons.primary.click()
                await art.shitSpinsForMax({$tag: 'd880053c-0f24-46ec-8c47-c635e91d6a39', max: 2000})

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
                testGlobal.inputs.phone.value = ''
                // Action
                testGlobal.buttons.primary.click()
                await art.shitSpinsForMax({$tag: 'fbe5bc76-cf5a-4ed4-90af-a815784cfd1e', max: 2000})
                art.uiState({$tag: '80db2840-cf3b-428e-8f7a-3a447f94d93a', expected: {
                    url: `http://aps-ua-writer.local:3022/profile.html`,
                    pageHeader: `Профиль`,
                    inputs: { phone: { value: `` } },
                    errorLabels: { phone: { title: `Телефон обязателен` } },
                    errorBanner: `Пожалуйста, исправьте ошибки ниже`,
                    displayLabels: {}
                }})

                // Inputs
                testGlobal.inputs.phone.value = 'adsfasdf'
                // Action
                testGlobal.buttons.primary.click()
                await art.shitSpinsForMax({$tag: '2d6f5c02-1eae-49cb-9c5a-0509a4f29e05', max: 2000})
                art.uiState({$tag: '24d5e9b2-0dac-40d6-94e8-57d0cfe00c9b', expected: {
                    url: `http://aps-ua-writer.local:3022/profile.html`,
                    pageHeader: `Профиль`,
                    inputs: { phone: { value: `adsfasdf` } },
                    errorLabels: { phone: { title: `Странный телефон какой-то` } },
                    errorBanner: `Пожалуйста, исправьте ошибки ниже`,
                    displayLabels: {}
                }})
                
                await drpc({fun: 'danger_imposeNextRequestTimestamp', timestamp: '2016-07-03 13:24:51'})
                // Inputs
                testGlobal.inputs.phone.value = '123-45-67'
                // Action
                testGlobal.buttons.primary.click()
                await art.shitSpinsForMax({$tag: 'e804da7e-6d1e-4fe4-a40e-e7697cb23622', max: 2000})
                
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
                
                // ---------- Posting a message to support ----------
                            
                // Action
                testGlobal.links.support.click()
                await art.linkBlinksForMax({$tag: 'eceeb886-f96e-4baa-a0c1-e75cc79d4e84', name: 'support', max: 2000})
                art.uiState({$tag: '0f630ccd-9936-4d27-ac1c-4d391a184e79', expected: {
                    url: `http://aps-ua-writer.local:3022/support.html`,
                    pageHeader: `Поддержка`,
                    inputs: { topic: { value: `` }, message: { value: `` } },
                    errorLabels: {},
                    errorBanner: undefined,
                    displayLabels: {} 
                }})                

                // Inputs
                testGlobal.inputs.topic.value = ''
                testGlobal.inputs.message.value = ''
                // Action
                testGlobal.buttons.primary.click()
                await art.shitSpinsForMax({$tag: '88d16caa-e430-4f95-8279-f3a25a0c857d', max: 2000})
                art.uiState({$tag: 'b4572e9b-f721-494f-980a-9c2a13041e3b', expected: {
                    url: `http://aps-ua-writer.local:3022/support.html`,
                    pageHeader: `Поддержка`,
                    inputs: { topic: { value: `` }, message: { value: `` } },
                    errorLabels: 
                     { topic: { title: `Поле обязательно` },
                       message: { title: `Поле обязательно` } },
                    errorBanner: `Пожалуйста, исправьте ошибки ниже`,
                    displayLabels: {} 
                }})
                
                // Inputs
                testGlobal.inputs.topic.value = 'Заапрувьте меня'
                testGlobal.inputs.message.value = 'И побыстрее, мать вашу!'
                // Action
                testGlobal.buttons.primary.click()
                await art.shitSpinsForMax({$tag: '0f39d2be-7ebe-454d-9c3b-413bb7a4b9f3', max: 2000})
                art.uiState({$tag: 'c02d34e4-4a28-4d59-bd09-91d84bd06a1a', expected: {

                }})




                // @ctx test
            },
            
            // preventRestoringURLAfterTest = true
            // failForJumping('Implement me', '182853f7-c8ee-41b9-b45f-d52636f9a154')
            
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
    return centered720(diva({},
        diva({className: 'row'},
            diva({className: 'col-sm-4'},
                diva({className: 'form-group'},
                    labela({}, t(`TOTE`, `Имя`)),
                    diva({}, displayLabel({name: 'first_name', content: user.first_name})))),
            diva({className: 'col-sm-4'},
                diva({className: 'form-group'},
                    labela({}, t(`TOTE`, `Фамилия`)),
                    diva({}, displayLabel({name: 'last_name', content: user.last_name})))),
            diva({className: 'col-sm-4'},
                diva({className: 'form-group'},
                    labela({}, t(`TOTE`, `Почта`)),
                    diva({}, displayLabel({name: 'email', content: user.email})))),
        ),
        diva({className: 'row'},
            diva({className: 'col-sm-4'},
                diva({className: 'form-group'},
                    labela({}, t(`TOTE`, `Телефон`)),
                    diva({}, displayLabel({name: 'phone', content: user.phone})))),
        ),
        diva({className: 'row'},
            diva({className: 'col-sm-4'},
                diva({className: 'form-group'},
                    labela({}, t(`TOTE`, `Аккаунт создан`)),
                    diva({}, displayLabel({name: 'inserted_at', content: timestampString(user.inserted_at)})))),
            diva({className: 'col-sm-4'},
                diva({className: 'form-group'},
                    labela({}, t(`TOTE`, `Профиль изменен`)),
                    diva({}, displayLabel({name: 'profile_updated_at', content: timestampString(user.profile_updated_at)})))),
        ),
    ))
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

