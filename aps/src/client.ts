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

global.igniteShit = makeUIShitIgniter(({t, setPage, replaceNavigate, pushNavigate, signOut, art}) => {
    let ui
        
    return {
        injectUI(x) {
            ui = x
        },
        
        isDynamicPage(name) {
            if (CLIENT_KIND === 'customer') return ~customerDynamicPageNames().indexOf(name)
            return ~writerDynamicPageNames().indexOf(name)
        },
        
        privatePageLoader(name) {
            return {
                orders: ordersPageLoader,
                support: supportPageLoader,
                dashboard: dashboardPageLoader,
                profile: profilePageLoader,
            }[name]
            
            
            function ordersPageLoader() {
                setPage({
                    pageTitle: t('My Orders', 'Мои заказы'),
                    pageBody: div(
                        )
                })
            }
            
            function supportPageLoader() {
                setPage({
                    pageTitle: t('Support', 'Служба поддержки'),
                    pageBody: div(
                        )
                })
            }
            
            function dashboardPageLoader() {
                setPage({
                    pageTitle: t('Dashboard', 'Панель'),
                    pageBody: div(
                        diva({className: 'row'},
                            diva({className: 'col-sm-6'},
                                sectionTitle(t('Account', 'Аккаунт')),
                                sectionLinks(
                                    [t('Sign out', 'Выйти прочь'), _=> {
                                        signOut()
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
                if (getUser().state === 'profile-pending') primaryButtonTitle = t('TOTE', 'Отправить на проверку')
                else primaryButtonTitle = t('WTF')
                
                const form = Form({
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
                        dlog('implement update profile success')
                    },
                })
                
                setPage({
                    pageTitle: t('Profile', 'Профиль'),
                    pageBody: div(
                        getUser().state === 'profile-pending' && preludeWithOrangeTriangle(
                            t('TOTE', 'Сначала заполняешь профиль. Админ связывается с тобой и активирует аккаунт. Потом все остальное.'),
                            {center: 720}),

                        form
                        )
                })
            }
        },
        
        renderTopNavbar,
        
        isTestScenarioNameOK(name) {
            if (LANG == 'ua' && CLIENT_KIND === 'customer' && name.startsWith('UA Customer :: ')) return true
            if (LANG == 'ua' && CLIENT_KIND === 'writer' && name.startsWith('UA Writer :: ')) return true
        },
        
        testScenarios() {return{
            async 'Something'() {
            },
        
            // ======================================== UA CUSTOMER TEST SCENARIOS ========================================
            
            async 'UA Customer :: Sign Up :: 1'() {
                await rpc({fun: 'danger_clearSentEmails'})
                await rpc({fun: 'danger_killUser', email: 'wilma.blue@test.shit.ua'})
                await rpc({fun: 'danger_fixNextGeneratedPassword', password: '63b2439c-bf18-42c5-9f7a-42d7357f966a'})
                
                await art.simulateURLNavigation('dashboard.html')
                art.assertUIState({$tag: '62112552-36ac-47fd-9bac-a4d6a7b3c4d4', expected: {
                    url: `http://aps-ua-customer.local:3012/sign-in.html`,
                    pageHeader: `Вход`,
                    inputs: { email: { value: `` }, password: { value: `` } },
                    errorLabels: {},
                    errorBanner: undefined 
                }})           
                            
                await art.simulateURLNavigation('sign-up.html')
                art.assertUIState({$tag: '6aa1c1bf-804b-4f5c-98e5-c081cd6238a0', expected: {
                    url: `http://aps-ua-customer.local:3012/sign-up.html`,
                    pageHeader: `Регистрация`,
                    inputs: 
                     { email: { value: `` },
                       firstName: { value: `` },
                       lastName: { value: `` },
                       agreeTerms: { value: false } },
                    errorLabels: {},
                    errorBanner: undefined 
                }})

                // Inputs
                testGlobal.inputs.email.value = 'wilma.blue@test.shit.ua'
                testGlobal.inputs.firstName.value = 'Вильма'
                testGlobal.inputs.lastName.value = 'Блу'
                testGlobal.inputs.agreeTerms.value = true
                // Action
                testGlobal.buttons.primary.click()
                await assertShitSpinsForMax({$tag: '29832372-ff89-46dd-ba9d-cf54154503f5', max: 2000})
                // Check
                assertTextSomewhere({$tag: '853610e2-c607-4ce5-9d60-74744ca63580', expected: 'Все круто. Теперь у тебя есть аккаунт. Пароль мы отправили письмом.'})
                art.assertUIState({$tag: '361d46a0-6ec1-40c4-a683-bc5263c41bba', expected: {
                    url: `http://aps-ua-customer.local:3012/sign-in.html`,
                    pageHeader: `Вход`,
                    inputs: { email: { value: `` }, password: { value: `` } },
                    errorLabels: {},
                    errorBanner: undefined 
                }})
                await assertSentEmails({$tag: '169a6331-c004-47fd-9b53-05242915d9f7', descr: 'Email with password', expected: [
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
                art.assertUIState({$tag: 'd9c42d17-322e-4427-b4ec-d946af422ba0', expected: {
                    url: `http://aps-ua-customer.local:3012/dashboard.html`,
                    pageHeader: `Панель`,
                    inputs: {},
                    errorLabels: {},
                    errorBanner: undefined 
                }})
                
                await art.simulateURLNavigation('dashboard.html')
            },
            
            // ======================================== UA WRITER TEST SCENARIOS ========================================
            
            async 'UA Writer :: Sign Up :: 1    b583c010-f383-4635-a826-3d2bb79f0806'() {
                await rpc({fun: 'danger_clearSentEmails'})
                await rpc({fun: 'danger_killUser', email: 'fred.red@test.shit.ua'})
                await rpc({fun: 'danger_fixNextGeneratedPassword', password: 'b34b80fb-ae50-4456-8557-399366fe45e4'})
                
                await art.simulateURLNavigation('dashboard.html')
                art.assertUIState({$tag: '20059334-7dff-4922-8bf5-ac07999d892d', expected: {
                    url: `http://aps-ua-writer.local:3022/sign-in.html`,
                    pageHeader: `Вход`,
                    inputs: { email: { value: `` }, password: { value: `` } },
                    errorLabels: {},
                    errorBanner: undefined 
                }})
                
                testGlobal.links.createAccount.click()
                art.assertUIState({$tag: 'b1a53c66-21db-42e5-8b0b-4d430b7b4ea6', expected: {
                    url: `http://aps-ua-writer.local:3022/sign-up.html`,
                    pageHeader: `Регистрация`,
                    inputs: 
                     { email: { value: `` },
                       firstName: { value: `` },
                       lastName: { value: `` },
                       agreeTerms: { value: false } },
                    errorLabels: {},
                    errorBanner: undefined 
                }})            
                
                // Inputs
                testGlobal.inputs.email.value = 'fred.red@test.shit.ua'
                testGlobal.inputs.firstName.value = 'Фред'
                testGlobal.inputs.lastName.value = 'Ред'
                testGlobal.inputs.agreeTerms.value = true
                // Action
                testGlobal.buttons.primary.click()
                await assertShitSpinsForMax({$tag: '39df3f4b-5ca0-4929-bae7-ec1d3bd008ed', max: 2000})
                
                await assertSentEmails({$tag: '024f202c-ee75-44ed-ac26-44154d4caf13', descr: 'Email with password', expected: [
                    { to: `Фред Ред <fred.red@test.shit.ua>`,
                        subject: `Пароль для Writer UA`,
                        html: dedent(`
                            Привет, Фред!<br><br>
                            Вот твой пароль: b34b80fb-ae50-4456-8557-399366fe45e4
                            <br><br>
                            <a href="http://aps-ua-writer.local:3022/sign-in.html">http://aps-ua-writer.local:3022/sign-in.html</a>`) } 
                ]})
                art.assertUIState({$tag: '24ca0059-e2e9-4fc4-9056-ede17e586029', expected: {
                    url: `http://aps-ua-writer.local:3022/sign-in.html`,
                    pageHeader: `Вход`,
                    inputs: { email: { value: `` }, password: { value: `` } },
                    errorLabels: {},
                    errorBanner: undefined 
                }})            
                assertTextSomewhere({$tag: 'bad7019b-a1d3-432c-a376-a872f5b27506', expected: 'Все круто. Теперь у тебя есть аккаунт. Пароль мы отправили письмом.'})

                // Inputs
                testGlobal.inputs.email.value = 'fred.red@test.shit.ua'
                testGlobal.inputs.password.value = 'b34b80fb-ae50-4456-8557-399366fe45e4'
                // Action
                testGlobal.buttons.primary.click()
                await assertShitSpinsForMax({$tag: 'd880053c-0f24-46ec-8c47-c635e91d6a39', max: 2000})

                art.assertUIState({$tag: '4d88eed7-d800-4a00-bfea-6b011329eaf0', expected: {
                    url: `http://aps-ua-writer.local:3022/profile.html`,
                    pageHeader: `Профиль`,
                    inputs: {},
                    errorLabels: {},
                    errorBanner: undefined 
                }})                        
                assertTextSomewhere({$tag: 'bad7019b-a1d3-432c-a376-a872f5b27506', expected: 'Фред'})
                
                assertNoTextSomewhere({$tag: '4d0713f8-ccfb-4d05-b064-3987492852a5', unexpected: 'Мои заказы'})
                assertNoTextSomewhere({$tag: 'a3e73a3e-8ed7-4a69-b748-e955ae4fd606', unexpected: 'Аукцион'})
            },
            
            // preventRestoringURLAfterTest = true
            // failForJumping('Implement me', '182853f7-c8ee-41b9-b45f-d52636f9a154')
            
        }},
    }
})

export function renderTopNavbar({clientKind, user, highlightedItem, spa=true, loadPageForURL, t}) {
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
    if (user) {
        if (clientKind === 'customer') {
            privateItems = [
                ['orders', t(`My Orders`, `Мои заказы`)],
                ['support', t(`Support`, `Служба поддержки`)],
            ]
        } else {
            privateItems = compact([
                user.state === 'cool' && ['orders', t(`My Orders`, `Мои заказы`)],
                user.state === 'cool' && ['store', t(`Store`, `Аукцион`)],
                ['profile', t(`Profile`, `Профиль`)],
                ['support', t(`Support`, `Служба поддержки`)]
            ])
        }
    }
    
    let leftNavbarItems, rightNavbarItem
    if (user) {
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
        rightNavbarItem = itemToLia(['dashboard', t(user.first_name)])
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
                    getEffects().blinkOn(byid(id).parent(), {fixed: true, dleft, dwidth})
                    
                    history.pushState(null, '', href)
                    
                    if (DEBUG_SIMULATE_SLOW_NETWORK) {
                        await delay(1000)
                    }
                    
                    await loadPageForURL()
                    
                    setTimeout(_=> {
                        getEffects().blinkOff()
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













//let t, effects, updateNavbar, token
//let debugShitInitialized, currentTestScenarioName, preventRestoringURLAfterTest, assertionErrorPane,
//    debugStatusBar, testPassedPane, updateDebugShit
    
async function zzzzznormalInit() {
    
    // @ctx state
    let urlObject, urlQuery, updateReactShit, rootContent, pageState, rpcclient, signedUpOK, user, activePage
    let updateCurrentPage
    let testScenarioToRun
    
    if (MODE === 'debug' && !debugShitInitialized) {
        window.addEventListener('keydown', e => {
            if (e.ctrlKey && e.altKey && e.key === 'k') return captureState()
            if (e.ctrlKey && e.altKey && e.key === 'i') return captureInputs()
            if (e.ctrlKey && e.altKey && e.key === 'a') return art.assertUIState({$tag: 'just-showing-actual', expected: undefined})
        })
        
        
        
        debugShitInitialized = true
    }
    
    
    
    if (false) { // TODO:vgrechka @kill
        spaifyNavbar()
        
        window.onpopstate = function(e) {
            loadPageForURL()
        }
        
        const userJSON = localStorage.getItem('user') // TODO:vgrechka This can throw (according to MDN), should handle
        if (userJSON) {
            user = JSON.parse(userJSON)
        }
    }
    
    if (testScenarioToRun) {
        
        window.locationProxy = {
            set href(x) {
                history.replaceState(null, '', x)
            }
        }
        
        
    } else {
    }
    
    
    function showProfile() {
    }
    
    function showSupport() {
    }
    
    function showOrders() {
    }
    
    function showDashboard() {
    }
    
    
    
    function loadSignUpPage() {
        const form = Form({
            primaryButtonTitle: t('Proceed', 'Вперед'),
            fields: {
                email: {
                    title: t('Email', 'Почта'),
                    type: 'text',
                    attrs: {autoFocus: true},
                },
                firstName: {
                    title: t('First Name', 'Имя'),
                    type: 'text',
                },
                lastName: {
                    title: t('Last Name', 'Фамилия'),
                    type: 'text',
                },
                agreeTerms: {
                    type: 'agreeTerms'
                },
            },
            rpcFun: 'signUp',
            onSuccess(res) {
                signedUpOK = true
                pushNavigate('sign-in.html')
            },
        })
        
        setPage({
            pageTitle: t('Sign Up', 'Регистрация'),
            pageBody: div(
                form,
                               
                div(
                    hr(),
                    divsa({textAlign: 'left'}, link(t('Already have an account? Sign in here.', 'Уже есть аккаунт? Тогда входим сюда.'), _=> {
                        pushNavigate('sign-in.html')
                    })))
                )
        })
        
        //------------------------------ Sign up debug functions ------------------------------
        
        const debugFuns = []
        if (CLIENT_KIND === 'writer') {
            debugFuns.push({
                title: t('joe'),
                async action() {
                    await rpc({fun: 'danger_killUser', email: 'joe.average@test.shit.ua'})
                    await rpc({fun: 'danger_fixNextGeneratedPassword', password: '5fca502f-73e2-4c3d-89c8-bc3dabf434d6'})
                    testGlobal.inputs.email.value = 'joe.average@test.shit.ua'
                    testGlobal.inputs.firstName.value = 'Джо'
                    testGlobal.inputs.lastName.value = 'Авераж'
                    testGlobal.inputs.agreeTerms.value = true
                    testGlobal.buttons.primary.click()
                }
            })
        }
        debugStatusBar.setFunctions(debugFuns)
    }
    
    
    
    function assertLinkWithTextSomewhere({$tag, expected}) {
        for (const a of $('a')) {
            if (a.text === expected) return
        }
        uiAssert(false, `I want a link with following text somewhere: [${expected}]`, jumpToShitDetailsUI($tag))
    }
    
    function assertHref({$tag, expected}) {
        uiAssert(document.location.href === expected, `I want to be at following URL: [${expected}]`, jumpToShitDetailsUI($tag))
    }
            
    
    async function assertSentEmails(def) {
        const actual = await rpc({fun: 'danger_getSentEmails'})
        assertRenameme(asn(def, {actual}))
    }
    
    function failForJumping(message, $tag) {
        uiAssert(false, message, jumpToShitDetailsUI($tag))
    }
    

    function assertTextSomewhere({$tag, expected}) {
        uiAssert(textSomewhere(expected), `I want following text on screen: [${expected}]`, jumpToShitDetailsUI($tag))
    }
    
    function assertNoTextSomewhere({$tag, unexpected}) {
        if (!unexpected) raise('Gimme `unexpected` argument. Maybe you misnamed it `expected`?')
        uiAssert(!textSomewhere(unexpected), `I DON’T want following text on screen: [${unexpected}]`, jumpToShitDetailsUI($tag))
    }
    
    function textSomewhere(text) {
        let found
        run(function descend(element) {
            if (element.nodeType === 3 /*TEXT_NODE*/) {
                if (~element.textContent.indexOf(text)) {
                    found = true
                    return
                }
            }
            // TODO:vgrechka Cut off invisible elements
            if (element.tagName !== 'SCRIPT') {
                for (let i = 0; i < element.childNodes.length; ++i) {
                    descend(element.childNodes[i])
                }
            }
        }, document.body)
        return found
    }
    
    function jumpToShitDetailsUI($tag) {
        return {
            detailsUI: updatableElement(update => {
                const link = OpenSourceCodeLink({$tag})
                return _=> divsa({marginTop: 5, padding: 5, backgroundColor: WHITE, position: 'relative'},
                               div(horiz(t('Jump and fix that shit: '), link)))
            })
        } 
    }

    function assertErrorBanner(expected) {
        uiAssert(testGlobal.errorBanner === expected, `I want error banner [${expected}]`)
    }

    function assertNoErrorBanner() {
        uiAssert(testGlobal.errorBanner === undefined, `I don't want an error banner hanging here`)
    }


    function assertNoErrorLabels() {
        uiAssert(isEmpty(testGlobal.errorLabels), `I don't want any error labels here`)
    }

    function assertErrorLabelTitlesExactly(...expected) {
        const actual = values(testGlobal.errorLabels).map(x => x.title)
        const expectedDescr = expected.map(x => `[${x}]`).join(', ')
        uiAssert(deepEquals(sortBy(expected), sortBy(actual)), `I want exactly following error labels: ${expectedDescr}`)
    }


    function assertErrorLabelTitle(expectedTitle) {
        uiAssert(ofind(testGlobal.errorLabels, x => x.title === expectedTitle), `I want error label [${expectedTitle}] on screen`)
    }

    function assertFail() {
        uiAssert(false, 'Just failing here, OK?')
    }
    
    async function assertSpinsForMax({$tag, name, max}) {
        assertSpins({$tag, name})
        
        const t0 = Date.now()
        while (Date.now() - t0 < max) {
            if (!testGlobal[name + 'Spins']) return
            await delay(50)
        }
        
        uiAssert(false, `I expected ${name} to stop spinning in ${max}ms`)
    }

    function assertSpins({$tag, name}) {
        uiAssert(testGlobal[name + 'Spins'], `I want ${name} to be spinning`)
    }

    async function assertShitSpinsForMax({$tag, name, max}) {
        await assertSpinsForMax({$tag, name: 'shit', max})
    }

    function assertShitSpins({$tag}) {
        assertSpins({name: 'shit'})
    }
    
//    async function assertHeaderShitSpinsForMax({$tag, max}) {
//        await assertSpinsForMax({$tag, name: 'headerShit', max})
//    }
//
//    function assertHeaderShitSpins() {
//        assertSpins({name: 'headerShit'})
//    }
    
    function uiFail(errorMessage) {
        uiAssert(false, errorMessage)
    }
    

    function simulatePopulateFields(data) {
        for (const [name, value] of toPairs(data)) {
            const functionName = 'simulate_setControlValue_' + name
            const setValue = window[functionName]
            if (!setValue) raise('I want function ' + functionName)
            setValue(value)
        }
    }

    // TODO:vgrechka Use testGlobal.buttons[...].click()
    function simulateClick(name) {
        const functionName = 'simulate_click_' + name
        const click = window[functionName]
        if (!click) raise('I want function ' + functionName)
        click()
    }


}

export function imposeClientT(newT) {
    t = newT
}

export function customerDynamicPageNames() {
    return tokens('test sign-in sign-up dashboard orders support')
}

export function writerDynamicPageNames() {
    return tokens('test sign-in sign-up dashboard orders support store users profile')
}


//function isDynamicPage(name, clientKind) {
//    return clientKind === 'customer' ? ~customerDynamicPageNames().indexOf(name)
//                                     : ~writerDynamicPageNames().indexOf(name)
//}


                
clog('Client code is kind of loaded')




//------------------------------ SHIT BEGINS HERE ------------------------------


        
//                    spana({className: 'fa-stack fa-lg'},
//                        el('i', {className: 'fa fa-circle fa-stack-2x', style: {color: LIGHT_GREEN_700}}),
//                        el('i', {className: 'fa fa-check fa-stack-1x fa-inverse'})),

