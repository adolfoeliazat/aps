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

import static 'into-u/utils-client into-u/ui ./stuff'
    
COLOR_1_MEDIUM = BLUE_GRAY_400
COLOR_1_DARK = BLUE_GRAY_600
COLOR_ZEBRA_LIGHT = WHITE
COLOR_ZEBRA_DARK = BLUE_GRAY_50

global.igniteShit = makeUIShitIgniter({
    Impl({ui}) {
        return {
            isDynamicPage,
            
            privatePageLoader(name) {
                return {
                    orders: ordersPageLoader,
                    
                    async 'admin-heap'() {
                        const pageTitle = t(`TOTE`, `Куча работы`)
                        const activeTab = ui.urlQuery.tab || 'support'
                        
                        const itemsFun
                        if (activeTab === 'support') {
                            itemsFun = 'private_getUnassignedSupportThreads'
                        }
                        
                        const res = await ui.rpcSoft({fun: itemsFun, fromID: 0})
                        if (res.error) {
                            return ui.setPage({
                                pageTitle,
                                pageBody: div(errorBanner(res.error))
                            })
                        }
                       
                        const tabs = Tabs({
                            activeTab,
                            tabs: {
                                support: {
                                    title: span(t(`TOTE`, `Поддержка`), ui.liveBadge({dataFieldName: 'heapTabs.supportBadge', liveStatusFieldName: 'unassignedSupportThreadCount'})),
                                    content: diva({},
                                        ui.renderMoreable({res, renderItem: renderSupportThreadItem, itemsFun, dataArrayName: 'supportThreads'})),
                                },
                                newOrders: {
                                    title: t(`TOTE`, `Новые заказы`),
                                    content: diva({}, t(`TOTE`, `todo new orders tab`)),
                                },
                                existingOrders: {
                                    title: t(`TOTE`, `Существующие заказы`),
                                    content: diva({}, t(`TOTE`, `todo existing orders tab`)),
                                },
                            }
                        })
                        
                        ui.setPage({
                            pageTitle,
                            pageBody: div(tabs)
                        })
                        
                        
                        function renderSupportThreadItem(item, i) {
                            const {rowBackground, lineColor} = zebraRowColors(i)
                            
                            return dataItemObject('supportThread', _=> {
                                let topicElement
                                const topicIsLink = false
                                if (topicIsLink) {
                                    // TODO:vgrechka dataField
                                    topicElement = ui.pageLink({title: item.topic, url: `support-thread.html?id=${item.id}`, name: `thread-${item.id}`, delayActionForFanciness: true, style: {color: BLACK_BOOT, fontWeight: 'bold'}})
                                } else {
                                    topicElement = spana({style: {color: BLACK_BOOT, fontWeight: 'bold'}}, dataField('topic', item.topic))
                                }
                                
                                const renderSupportThreadMessage = makeSupportThreadMessageRenderer({lineColor, dottedLines: true, dryFroms: true})
                                
                                return diva({style: {backgroundColor: rowBackground, position: 'relative'}},
                                    diva({style: {position: 'absolute', right: 0, top: 0, zIndex: 1000}},
                                        ui.busyButton({name: `takeAndReply-${item.id}`, icon: 'comment', iconColor: COLOR_1_DARK, hint: t(`TOTE`, `Взять себе и ответить`), async onClick() {
                                            await ui.rpc({fun: 'private_takeSupportThread', id: item.id})
                                            makeNextRPCNotLaggingInTests(2)
                                            await ui.pushNavigate(`support.html?thread=${item.id}`)
                                        }})),
                                    
                                    diva({className: '', style: {marginTop: 10,  marginBottom: 5, paddingRight: 45}},
                                        topicElement),
                                        
                                    dataArray('messages', _=> div(...item.messages.map(renderSupportThreadMessage))))
                            })
                        }
                    },
                    
                    async 'admin-my-tasks'() {
                        ui.setPage({
                            pageTitle: 'qweqwe',
                            pageBody: div(
                                t(`TOTE`, `foooooooooo`), aa({href: '#', onClick() { dlog('cliiiiick') }}, t('qqqqqqqqqqq')))
                        })
                    },
                    
                    async support() {
                        if (ui.urlQuery.thread) {
                            await lala({
                                entityFun: 'private_getSupportThread',
                                itemsFun: 'private_getSupportThreadMessages',
                                entityID: ui.urlQuery.thread,
                                pageTitle: entityRes => {
                                    if (entityRes.error) return t(`TOTE`, `Облом`)
                                    return t(`TOTE`, `Запрос в поддержку № ${entityRes.entity.id}`)
                                },
                                emptyMessage: t(`TOTE`, `Странно, здесь ничего нет. А должно что-то быть...`),
                                aboveItems(entityRes) {
                                    return pageTopBlockQuote(entityRes.entity.topic)
                                },
                                dataArrayName: 'supportThreadMessages',
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
                
                
                async function lala({pageTitle, entityID, entityFun, itemsFun, emptyMessage, plusGlyph='plus', plusForm, aboveItems, dataArrayName, renderItem, defaultOrdering='desc'}) {
                    const entityRes = await ui.rpcSoft({fun: entityFun, entityID})
                    if (entityRes.error) return showBadResponse(entityRes)
                    
                    let ordering = ui.urlQuery.ordering
                    if (!['asc', 'desc'].includes(ordering)) ordering = defaultOrdering

                    const itemsRes = await ui.rpcSoft({fun: itemsFun, entityID, fromID: 0, ordering})
                    if (itemsRes.error) return showBadResponse(itemsRes)
                    
                    let items, showEmptyLabel = true, form, plusButtonVisible = true, plusButtonClass, formClass
                    
                    const orderingSelect = Select({name: 'order', style: {width: 160, marginRight: 10},
                        values: [['desc', t(`TOTE`, `Сначала новые`)], ['asc', t(`TOTE`, `Сначала старые`)]],
                        initialValue: ordering,
                        async onChange() {
                            await ui.pushNavigate(`support.html?thread=${entityID}&ordering=${orderingSelect.getValue()}`)
                        }})
                    
                    ui.setPage({
                        pageTitle: fov(pageTitle, entityRes),
                        pageBody: _=> div(
                            form && diva({className: formClass, style: {marginBottom: 15}}, form),
                            fov(aboveItems, entityRes),
                            run(function renderItems() {
                                if (!itemsRes.items.length) {
                                    if (showEmptyLabel) {
                                        return div(emptyMessage)
                                    }
                                    return ''
                                }
                                return ui.renderMoreable({res: itemsRes, itemsFun, dataArrayName: 'supportThreadMessages', renderItem(message, i) {
                                    const {rowBackground, lineColor} = zebraRowColors(i)
                                    return diva({style: {background: rowBackground}},
                                        makeSupportThreadMessageRenderer({lineColor})(message, i))
                                }})
                            }),
                        ),
                        headerControls: _=> diva({style: {display: 'flex'}},
                            diva({style: {}}, orderingSelect),
                            plusButtonVisible && button.primary[plusGlyph]({name: 'plus', className: plusButtonClass, style: {background: COLOR_1_MEDIUM}}, _=> {
                                showEmptyLabel = false
                                plusButtonClass = undefined
                                formClass = 'aniFadeIn'
                                
                                form = ui.Form({
                                    primaryButtonTitle: plusForm.primaryButtonTitle,
                                    cancelButtonTitle: plusForm.cancelButtonTitle,
                                    fields: plusForm.fields,
                                    rpcFun: plusForm.rpcFun,
                                    onCancel: cancelForm,
                                    onSuccess: plusForm.onSuccess,
                                })
                                
                                plusButtonVisible = false
                                ui.updatePage()
                        })),
                        
                        onKeyDown(e) {
                            if (e.keyCode === 27) {
                                cancelForm()
                            }
                        }
                    })
                    
                    function cancelForm() {
                        plusButtonVisible = true
                        plusButtonClass = 'aniFadeIn'
                        form = undefined
                        ui.updatePage()
                            
                        timeoutSet(500, _=> {
                            plusButtonClass = undefined
                            ui.updatePage()
                        })
                    }
                    
                    function showBadResponse(res) {
                        return ui.setPage({
                            pageTitle: fov(pageTitle, res),
                            pageBody: div(errorBanner(res.error))
                        })
                    }
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
        
        
        // @ctx client functions
        
        function zebraRowColors(i) {
            let rowBackground, lineColor
            if (i % 2 === 0) {
                rowBackground = COLOR_ZEBRA_LIGHT
                lineColor = COLOR_ZEBRA_DARK
            } else {
                rowBackground = COLOR_ZEBRA_DARK
                lineColor = COLOR_ZEBRA_LIGHT
            }
            
            return {rowBackground, lineColor}
        }
        
        function makeSupportThreadMessageRenderer({lineColor, dottedLines, dryFroms}) {
            return function renderSupportThreadMessage(message, messageIndex) {

                return dataItemObject('supportThreadMessage', _=> diva({className: 'row',
                    style: asn({display: 'flex', flexWrap: 'wrap', paddingTop: messageIndex > 0 ? 5 : 0, paddingBottom: 5, paddingRight: 45, marginLeft: 0, marginRight: 0, position: 'relative'},
                           messageIndex > 0 && dottedLines && {borderTop: `3px dotted ${lineColor}`})},
                           
                    diva({className: 'col-sm-3', style: {display: 'flex', flexDirection: 'column', borderRight: `3px solid ${lineColor}`, paddingLeft: 0}},
                        messageIndex === 0 || !dryFroms
                            ? div(diva({style: {whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis'}}, spana({style: {fontWeight: 'bold'}},
                                      t(`TOTE`, `От: `)),
                                      userLabel({user: message.sender, dataFieldName: 'from'})),
                                  diva({style: {whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis'}}, spana({style: {fontWeight: 'bold'}},
                                      t(`TOTE`, `Кому: `)),
                                      dataField('to', message.recipient ? (message.recipient.first_name + ' ' + message.recipient.last_name)
                                                                        : t(`TOTE`, `В рельсу`))))
                            : diva({style: {fontWeight: 'bold'}}, dataField('continuation', t(`TOTE`, `В догонку`))),
                                                           
                        diva({style: {marginTop: 10}}, dataField('timestamp', timestampString(message.inserted_at)))
                    ),
                    diva({className: 'col-sm-9', style: {display: 'flex', flexDirection: 'column', paddingRight: 5, whiteSpace: 'pre-wrap', position: 'relative'}},
                        // message.unreadMessageCount && brightBadgea({style: {position: 'absolute', left: -12, top: 20}}, dataField('unreadMessageCount', t('' + message.unreadMessageCount))),
                        dataField('message', message.message))))
            }
        }

        function userLabel({user, dataFieldName, $sourceLocation}) {
            const glyphName = lookup(user.kind, {customer: 'user', writer: 'pencil', admin: 'cog'})
            const glyph = ia({className: `fa fa-${glyphName}`, style: {marginLeft: 5, marginRight: 5}})
            return spana_({$sourceLocation, $revealableType: 'userLabel'},
                glyph,
                maybeDataField(dataFieldName, user.first_name + ' ' + user.last_name))
        }

        function brightBadgea(def, content) {
            #extract {style} from def
            return spana({className: 'badge', style: asn({paddingRight: 8, backgroundColor: BLUE_GRAY_400}, style)}, content) 
        }
    },
    
    isTestScenarioNameOK(name) {
        if (LANG == 'ua' && CLIENT_KIND === 'customer' && name.startsWith('UA Customer :: ')) return true
        if (LANG == 'ua' && CLIENT_KIND === 'writer' && (name.startsWith('UA Writer :: ') || name.startsWith('UA Admin :: '))) return true
    },
    
    testScenarios({sim}) {
        return asn({},
            require('./client-writer-tests')({sim}),
            require('./client-admin-tests')({sim}),)
    },
})


function isDynamicPage(name) {
    if (CLIENT_KIND === 'customer') return ~customerDynamicPageNames().indexOf(name)
    return ~writerDynamicPageNames().indexOf(name)
}


export function renderTopNavbar({clientKind, highlightedItem, t, ui}) {
    let user
    if (ui) {
        user = ui.getUser()
    }
    
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
        if (!user || user.kind !== 'admin') {
            proseItems = [
                ['why', t(`Why Us?`, `Почему мы?`)],
                ['prices', t(`Prices`, `Цены`)],
                ['faq', t(`FAQ`, `ЧаВо`)],
            ]
        }
    }
    
    let privateItems
    if (user) {
        if (clientKind === 'customer') {
            privateItems = [
                ['orders', span(dataField('topNavItem.myOrders.title', t(`My Orders`, `Мои заказы`)))],
                ['support', span(dataField('topNavItem.support.title', t(`Support`, `Поддержка`)))],
            ]
        } else {
            if (user.kind !== 'admin') {
                privateItems = compact([
                    user.state === 'cool' && ['orders', span(dataField('topNavItem.myOrders.title', t(`My Orders`, `Мои заказы`)))],
                    user.state === 'cool' && ['store', span(dataField('topNavItem.store.title', t(`Store`, `Аукцион`)))],
                    ['profile', span(dataField('topNavItem.profile.title', t(`Profile`, `Профиль`)))],
                    ['support', span(dataField('topNavItem.support.title', t(`Support`, `Поддержка`)))]
                ])
            } else {
                privateItems = []
                if (user.roles.support) {
                    privateItems.push(['admin-my-tasks', span(dataField('topNavItem.admin-my-tasks.title', t(`TOTE`, `Мои задачи`)))])
                    
                    privateItems.push(['admin-heap',
                        span(dataField('topNavItem.admin-heap.title', t(`TOTE`, `Куча`)),
                        ui.liveBadge({dataFieldName: 'topNavItem.admin-heap.badge', liveStatusFieldName: 'heapSize'})
                        )])
                }
            }
        }
    }
    
    let leftNavbarItems, rightNavbarItem
    if (user) {
        const liaid = puid()
        leftNavbarItems = []
        if (user.kind !== 'admin') {
            let dropdownAStyle
            if (proseItems.some(x => x[0] === highlightedItem)) {
                dropdownAStyle = {backgroundColor: '#e7e7e7'}
            }
            leftNavbarItems.push(
                lia({className: 'dropdown'},
                    aa({href: '#', className: 'dropdown-toggle skipClearMenus', style: dropdownAStyle, 'data-toggle': 'dropdown', role: 'button'}, t(`Prose`, `Проза`), spana({className: 'caret', style: {marginLeft: 5}})),
                    ula({className: 'dropdown-menu'},
                        ...proseItems.map(itemToLia))))
        }
        leftNavbarItems.push(...privateItems.map(itemToLia))
        rightNavbarItem = itemToLia(['dashboard', t(user.first_name)])
    } else {
        leftNavbarItems = proseItems.map(itemToLia)
        rightNavbarItem = itemToLia(['sign-in', t(`Sign In`, `Вход`)])
    }
    
    let brand
    if (clientKind === 'customer') {
        brand = 'APS'
    } else {
        brand = t('Writer', 'Писец')
        if (user && user.kind === 'admin') {
            brand = t('Admin', 'Админ')
        }
    }
    
    return nava({className: 'navbar navbar-default navbar-fixed-top'},
               diva({className: 'container-fluid'},
                   diva({className: 'navbar-header'},
                       makeLink('home', brand, 'navbar-brand')),
                       
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
                
                let me, testActionHand
                testGlobal.topNavbarLinks[name] = me = {
                    async click() {
                        if (getTestSpeed() === 'slow') {
                            me.showHand()
                            await delay(DEBUG_ACTION_HAND_DELAY)
                            me.hideHand()
                            await onClick()
                        } else {
                            await onClick()
                        }
                    },
                    showHand({testActionHandOpts}={}) {
                        testActionHand = showTestActionHand(byid(id), testActionHandOpts)
                    },
                    hideHand() {
                        testActionHand.delete()
                    },
                }
                
                byid(id).on('click', onClick)
                
                
                async function onClick(e) {
                    if (e) { // Not simulated in test
                        if (e.ctrlKey && !e.shiftKey) return // Allow debug revelations
                        e.preventDefault()
                        e.stopPropagation()
                    }
                    
                    if (MODE === 'debug' && e && e.ctrlKey && e.shiftKey) {
                        const cp = getCapturePane()
                        cp.show()
                        cp.addCode(`                // Action\n`
                                 + `                ${'#'}hawait testGlobal.topNavbarLinks['${name}'].click()\n`
                                 + `                ${'#'}hawait art.uiStateAfterLiveStatusPolling({$tag: '${uuid()}', expected: {\n`
                                 + `\n`
                                 + `                }})\n`
                                 )
                        cp.focusAndSelect()
                        return
                    }
                    
                    effects.blinkOn(byid(id).parent(), {fixed: true, dleft, dwidth})
                    testGlobal['topNavbarLink_' + name + '_blinks'] = true
                    
                    if ((!isDynamicPage(name) || ~['sign-in', 'sign-up'].indexOf(name)) && !(isInTestScenario() && getTestSpeed() === 'fast')) {
                        await delay(ACTION_DELAY_FOR_FANCINESS)
                    }
                    await ui.pushNavigate(href)
                    
// TODO:vgrechka @kill
//                    history.pushState(null, '', href)
//                    if (DEBUG_SIMULATE_SLOW_NETWORK) {
//                        await delay(1000)
//                    }
//                    await ui.loadPageForURL()
                    
                    setTimeout(_=> {
                        effects.blinkOff()
                        testGlobal['topNavbarLink_' + name + '_blinks'] = false
                        bsClearMenus()
                    }, 250)
                }
            },
            
            componentWillUnmount() {
                delete testGlobal.topNavbarLinks[name]
                byid(id).off()
            },
        })
    }
}



export function customerDynamicPageNames() {
    return tokens('test sign-in sign-up dashboard orders support')
}

export function writerDynamicPageNames() {
    return tokens('test sign-in sign-up dashboard orders support store users profile admin-my-tasks admin-heap')
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

