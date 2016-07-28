/*
 * APS
 * 
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

#pragma instrument-ui-rendering

MODE = 'debug'
DEBUG_SIMULATE_SLOW_NETWORK = true
DEBUG_RPC_LAG_FOR_MANUAL_TESTS = 500
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
            
            css() {
                return `
                    .form-control:focus {border-color: #b0bec5; box-shadow: inset 0 1px 1px rgba(0,0,0,.075),0 0 8px rgba(176,190,197,.6);}

                    .btn-primary {background-color: #78909c; border-color: #546e7a;}
                    .btn-primary:hover {background-color: #546e7a; border-color: #37474f;}
                    .btn-primary:focus {background-color: #455a64; border-color: #263238; outline-color: #b0bec5;}
                    .btn-primary:focus:hover {background-color: #455a64; border-color: #263238;}
                    .btn-primary:active {background-color: #455a64; border-color: #263238;}
                    .btn-primary:active:focus {background-color: #455a64; border-color: #263238; outline-color: #b0bec5;}
                    .btn-primary:active:hover {background-color: #455a64; border-color: #263238;}
                
                    .btn-primary.disabled.focus,
                    .btn-primary.disabled:focus,
                    .btn-primary.disabled:hover,
                    .btn-primary[disabled].focus,
                    .btn-primary[disabled]:focus,
                    .btn-primary[disabled]:hover,
                    fieldset[disabled] .btn-primary.focus,
                    fieldset[disabled] .btn-primary:focus,
                    fieldset[disabled] .btn-primary:hover {
                        background-color: #78909c;
                        border-color: #546e7a;
                    }      
                
                    .aniFadeOutDelayed {
                        animation-name: aniFadeOutDelayed;
                        animation-delay: 0.5s;
                        animation-duration: 500ms;
                        animation-iteration-count: 1;
                        animation-fill-mode: forwards;
                    }
                    @keyframes aniFadeOutDelayed {
                        0% {
                            opacity: 1;
                        }
                        
                        100% {
                            opacity: 0;
                        }
                    }
                
                    .aniFadeOutAfterSomeBlinking {
                        animation-name: aniFadeOutAfterSomeBlinking;
                        animation-delay: 0;
                        animation-duration: 500ms;
                        animation-iteration-count: 3;
                        animation-fill-mode: forwards;
                    }
                    @keyframes aniFadeOutAfterSomeBlinking {
                        0% {
                            opacity: 1;
                        }
                        100% {
                            opacity: 0;
                        }
                    }
                `
            },
            
            privatePageLoader(name) {
                return {
                    orders: ordersPageLoader,
                    
                    async 'admin-heap'() {
                        beginTrain({name: 'Load page admin-heap'}); try {
                            const pageTitle = t(`TOTE`, `Куча работы`)
                            const activeTab = ui.urlQuery.tab || 'support'
                            
                            const itemsFun
                            if (activeTab === 'support') {
                                itemsFun = 'private_getUnassignedSupportThreads'
                            }
                            
                            const itemsReq = {fun: itemsFun}
                            const itemsRes = await ui.rpcSoft(itemsReq)
                            if (itemsRes.error) {
                                return ui.setPage({
                                    pageTitle,
                                    pageBody: div(errorBanner(itemsRes.error))
                                })
                            }
                           
                            ui.setPage({
                                pageTitle,
                                pageBody: div(tabs({
                                    name: 'main',
                                    activeTab,
                                    tabs: {
                                        support: {
                                            title: _=> span(spanc({name: 'title', content: t(`TOTE`, `Поддержка`)}), ui.liveBadge({name: 'supportTab', liveStatusFieldName: 'unassignedSupportThreadCount'})),
                                            content: _=> diva({},
                                                ui.renderMoreable({itemsRes, itemsReq, renderItem: makeRenderSupportThread({topicIsLink: false, hasTakeAndReplyButton: true, dryFroms: true})})),
                                        },
//                                        orders: {
//                                            title: _=> spanc({name: 'title', content: t(`TOTE`, `Заказы`)}),
//                                            content: _=> diva({}, t(`TOTE`, `todo new orders tab`)),
//                                        },
                                    }
                                }))
                            })
                            
                        } finally { endTrain() }
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
                            beginTrain({name: 'Load support page with thread param'}); try {
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
                                    plusIcon: 'comment',
                                    
                                    plusFormDef: {
                                        primaryButtonTitle: t(`TOTE`, `Запостить`),
                                        cancelButtonTitle: t(`TOTE`, `Передумал`),
                                        autoFocus: 'message',
                                        fields: [
                                            ui.HiddenField({
                                                name: 'threadID',
                                                value: ui.urlQuery.thread,
                                            }),
                                            ui.TextField({
                                                name: 'message',
                                                kind: 'textarea',
                                                title: t(`TOTE`, `Сообщение`),
                                            }),
                                        ],
                                        rpcFun: 'private_createSupportThreadMessage',
                                        async onSuccess(res) {
                                            await ui.pushNavigate(`support.html?thread=${ui.urlQuery.thread}`)
                                        },
                                    },
                                    
                                    editFormDef: run(_=> {
                                        const fields = [
                                            ui.HiddenField({
                                                name: 'threadID',
                                                value: ui.urlQuery.thread,
                                            }),
                                        ]
                                        
                                        const statusValues = []
                                        if (ui.getUser().kind === 'admin') {
                                            statusValues.push({value: 'open', title: t(`TOTE`, `Открыт`)})
                                        }
                                        statusValues.push({value: 'resolved', title: t(`TOTE`, `Решен`)})
                                        fields.push(ui.SelectField({
                                            name: 'status',
                                            title: t(`TOTE`, `Статус`),
                                            values: statusValues
                                        }))
                                        
                                        return {
                                            primaryButtonTitle: t(`TOTE`, `Сохранить`),
                                            cancelButtonTitle: t(`TOTE`, `Не стоит`),
                                            // autoFocus: 'resolution',
                                            fields,
                                            rpcFun: 'private_updateSupportThreadMessage',
                                            async onSuccess(res) {
                                                await ui.pushNavigate(`support.html?thread=${ui.urlQuery.thread}`)
                                            },
                                        }
                                    }),
                                    
                                    renderItem(message, i) {
                                        const {rowBackground, lineColor} = zebraRowColors(i)
                                        return diva({style: {background: rowBackground}},
                                            makeRenderSupportThreadMessage({lineColor, showMessageNewLabel: true})(message, i))
                                    },
                                })
                            } finally { endTrain() }
                        } else if (!ui.urlQuery.thread) {
                            beginTrain({name: 'Load support page without thread param'}); try {
                                const activeTab = ui.urlQuery.tab || 'updated'
                                
//                                const itemsFun
//                                if (activeTab === 'support') {
//                                    itemsFun = 'private_getUnassignedSupportThreads'
//                                }
//                                
//                                const res = await ui.rpcSoft({fun: itemsFun, fromID: 0})
                                
                                const itemsReq = {fun: 'private_getUpdatedSupportThreads'}
                                const itemsRes = await ui.rpcSoft(itemsReq)
                                if (itemsRes.error) return ui.setToughLuckPage({res: itemsRes})
                               
                                ui.setPage({
                                    pageTitle: t(`Support`, `Поддержка`),
                                    pageBody: div(tabs({
                                        name: 'main',
                                        activeTab,
                                        tabs: {
                                            updated: {
                                                title: _=> spanc({name: 'title', content: t(`New`, `Новые`)}),
                                                content() {
                                                    return diva({},
                                                        ui.renderMoreable({itemsRes, itemsReq, renderItem: makeRenderSupportThread({topicIsLink: true, hasTakeAndReplyButton: false, showMessageNewLabel: true})}))
                                                },
                                            },
                                        }
                                    }))
                                })
                                
                                return
                                await lala({
                                    pageTitle: t('Support', 'Поддержка'),
                                    itemsFun: 'private_getSupportThreads',
                                    emptyMessage: t(`TOTE`, `Запросов в поддержку не было. Чтобы добавить, нажми плюсик вверху.`),
                                    renderItem(item, i) {
                                        return t('todo render item')
                                    },
                                    plusIcon: 'plus',
                                    plusFormDef: {
                                        primaryButtonTitle: t(`TOTE`, `Запостить`),
                                        cancelButtonTitle: t(`TOTE`, `Не стоит`),
                                        autoFocus: 'topic',
                                        fields: [
                                            ui.TextField({
                                                name: 'topic',
                                                title: t(`TOTE`, `Тема`),
                                            }),
                                            ui.TextField({
                                                name: 'message',
                                                kind: 'textarea',
                                                title: t(`TOTE`, `Сообщение`),
                                            })
                                        ],
                                        rpcFun: 'private_createSupportThread',
                                        async onSuccess(res) {
                                            await ui.pushNavigate(`support.html?thread=${res.entity.id}`)
                                        },
                                    },
                                })
                            } finally { endTrain() }
                        }
                    },
                
                    dashboard: dashboardPageLoader,
                    profile: profilePageLoader,
                }[name]
                
                
                async function lala({pageTitle, entityID, entityFun, itemsFun, emptyMessage, plusIcon='plus', plusFormDef, editFormDef, aboveItems, renderItem, defaultOrdering='desc', hasOrderingSelect=true}) {
                    let entityRes
                    if (entityFun) {
                        entityRes = await ui.rpcSoft({fun: entityFun, entityID})
                        if (entityRes.error) return showBadResponse(entityRes)
                    }
                    
                    let ordering = ui.urlQuery.ordering
                    if (!['asc', 'desc'].includes(ordering)) ordering = defaultOrdering

                    const itemsReq = {fun: itemsFun, entityID, ordering}
                    const itemsRes = await ui.rpcSoft(itemsReq)
                    if (itemsRes.error) return showBadResponse(itemsRes)
                    
                    let items, showEmptyLabel = true,
                        headerControlsVisible = true, headerControlsClass, headerControlsDisabled,
                        orderingSelect,
                        cancelForm,
                        plusShit, editShit
                    
                    if (hasOrderingSelect) {
                        orderingSelect = Select({name: 'ordering', isAction: true, style: {width: 160},
                            values: [{value: 'desc', title: t(`TOTE`, `Сначала новые`)}, {value: 'asc', title: t(`TOTE`, `Сначала старые`)}],
                            initialValue: ordering,
                            disabled: _=> headerControlsDisabled,
                            async onChange() {
                                setHeaderControlsDisabled(true)
                                orderingSelect.setBlinking(true)
                                await ui.pushNavigate(`support.html?thread=${entityID}&ordering=${orderingSelect.getValue()}`)
                                setHeaderControlsDisabled(false)
                                orderingSelect.setBlinking(false)
                            }
                        })
                    }
                    
                    if (plusFormDef) {
                        plusShit = makeButtonFormShit({name: 'plus', level: 'primary', icon: plusIcon, formDef: plusFormDef})
                    }
                    if (editFormDef) {
                        editShit = makeButtonFormShit({name: 'edit', level: 'default', icon: 'edit', formDef: editFormDef})
                    }
                        
                    function makeButtonFormShit({name, level, icon, formDef}) {
                        let form, formClass
                        
                        return {
                            button() {
                                return button({style: {marginLeft: 8}, name, level, icon, disabled: headerControlsDisabled, onClick() {
                                    showEmptyLabel = false
                                    setHeaderControlsDisappearing()
                                    formClass = 'aniFadeIn'
                                        
                                    cancelForm = function() {
                                        setHeaderControlsAppearing()
                                        form = undefined
                                        ui.updatePage()
                                    }
                                    
                                    form = ui.Form(asn(formDef, {
                                        onCancel: cancelForm,
                                    }))
                                    
                                    ui.updatePage()
                                }})
                            },
                            
                            form() {
                                return form && diva({className: formClass, style: {marginBottom: 15}}, form)
                            },
                        }
                    }
                    
                    ui.setPage({
                        pageTitle: fov(pageTitle, entityRes),
                        pageBody: _=> div(
                            editShit && editShit.form,
                            plusShit && plusShit.form,
                            fov(aboveItems, entityRes),
                            run(function renderItems() {
                                if (!itemsRes.items.length) {
                                    if (showEmptyLabel) {
                                        return div(emptyMessage)
                                    }
                                    return ''
                                }
                                return ui.renderMoreable({itemsRes, itemsReq, renderItem,})
                            }),
                        ),
                        headerControls: _=> headerControlsVisible && diva({style: {display: 'flex'}, className: headerControlsClass},
                            orderingSelect,
                            editShit && editShit.button,
                            plusShit && plusShit.button,
                        ),
                        
                        onKeyDown(e) {
                            if (e.keyCode === 27) {
                                fov(cancelForm)
                            }
                        }
                    })
                    
                    
                    function setHeaderControlsDisappearing() {
                        headerControlsVisible = false
                        headerControlsClass = undefined
                    }
                    
                    function setHeaderControlsAppearing() {
                        headerControlsVisible = true
                        headerControlsClass = 'aniFadeIn'
                        timeoutSet(500, _=> {
                            headerControlsClass = undefined
                            ui.updatePage()
                        })
                    }
                    
                    function setHeaderControlsDisabled(b) {
                        headerControlsDisabled = b
                        ui.updatePage()
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
                                        {
                                            name: 'signOut',
                                            title: t('Sign out', 'Выйти прочь'),
                                            onClick() {
                                                ui.signOut()
                                            }
                                        },
                                        {
                                            name: 'changePassword',
                                            title: t('Change password', 'Сменить пароль'),
                                            onClick() {
                                                dlog('implement change password')
                                            }
                                        },
                                    )))
                            )
                    })
                    
                    
                    function sectionTitle(title) {
                        return diva({style: {backgroundColor: BLUE_GRAY_50, fontWeight: 'bold', padding: '2px 5px', marginBottom: 10}}, title)
                    }
                    
                    function sectionLinks(...items) {
                        return ula({className: 'fa-ul', style: {marginLeft: 20}},
                                   ...items.map(item =>
                                       lia({style: {marginBottom: 5}},
                                           ia({className: 'fa fa-li fa-chevron-right', style: {color: BLUE_GRAY_600}}),
                                           link({name: item.name, title: item.title, style: {color: '#333'}, onClick: item.onClick}))))
                    }
                }
                
                function profilePageLoader() {
                    let primaryButtonTitle
                    if (ui.getUser().state === 'profile-pending') primaryButtonTitle = t('TOTE', 'Отправить на проверку')
                    else primaryButtonTitle = t('WTF')
                    
                    const form = ui.Form({
                        primaryButtonTitle,
                        autoFocus: 'phone',
                        fields: [
                            ui.TextField({
                                name: 'phone',
                                title: t('Phone', 'Телефон'),
                            }),
                        ],
                        rpcFun: 'private_updateProfile',
                        async onSuccess(res) {
                            // ui.getUser().state = 'profile-approval-pending'
                            ui.setUser(res.newUser)
                            await ui.replaceNavigate('profile.html')
                        },
                    })
                    
                    let pageBody
                    const userState = ui.getUser().state
                    if (userState === 'profile-pending') {
                        pageBody = div(preludeWithOrangeTriangle({content: t('TOTE', 'Сначала заполняешь профиль. Админ связывается с тобой и активирует аккаунт. Потом все остальное.'), center: 720}),
                                       form)
                    } else if (userState === 'profile-approval-pending') {
                        pageBody = div(preludeWithHourglass({content: span(t('TOTE', 'Админ проверяет профиль, жди извещения почтой. Если есть вопросы, можно написать в '),
                                                                 ui.pageLink({title: t('TOTE', 'поддержку'), url: 'support.html', name: 'support'}),
                                                                 t('.'))}),
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
        
        
        // @ctx client helpers
        
        function makeRenderSupportThread({topicIsLink, hasTakeAndReplyButton, showMessageNewLabel, dryFroms}) {
            return function renderSupportThread(item, i) {
                const {rowBackground, lineColor} = zebraRowColors(i)
                
                const url = `support.html?thread=${item.id}`
                
                return uiStateScope({name: `thread-${i}`, render() {
                    let topicElement
                    if (topicIsLink) {
                        topicElement = ui.pageLink({url, name: `topic`, title: item.topic, style: {color: BLACK_BOOT, fontWeight: 'bold'}})
                    } else {
                        topicElement = spana({style: {color: BLACK_BOOT, fontWeight: 'bold'}}, spanc({name: 'topic', content: item.topic}))
                    }
                    
                    const paddingRight = hasTakeAndReplyButton ? 45 : 0
                    const renderSupportThreadNewMessage = makeRenderSupportThreadMessage({lineColor, dottedLines: true, dryFroms, showMessageNewLabel, paddingRight})
                    const renderSupportThreadOldMessage = makeRenderSupportThreadMessage({lineColor, dottedLines: true, dryFroms, showMessageNewLabel, paddingRight})
                    
                    const moreNewMessages = item.newMessages.total - item.newMessages.top.length
                    const moreOldMessages = item.oldMessages.total - item.oldMessages.top.length
                    
                    return diva({style: {backgroundColor: rowBackground, position: 'relative'}},
                        diva({style: {position: 'absolute', right: 0, top: 0, zIndex: 1000}},
                            hasTakeAndReplyButton && ui.busyButton({name: `takeAndReply`, icon: 'comment', iconColor: COLOR_1_DARK, hint: t(`TOTE`, `Взять себе и ответить`), async onClick() {
                                beginTrain({name: 'Take support thread and reply'}); try {
                                    await ui.rpc({fun: 'private_takeSupportThread', id: item.id})
                                    // TODO:vgrechka Handle private_takeSupportThread RPC failure. Need error popup or something instead of trying to pushNavigate    12fbe33a-c4a5-4967-9cec-5c2aa217e947 
                                    await ui.pushNavigate(`support.html?thread=${item.id}`)
                                } finally { endTrain() }
                            }}),
                            ),
                        
                        diva({className: '', style: {marginTop: 10,  marginBottom: 5, paddingRight: 45}},
                            topicElement),
                            
                        uiStateScope({name: 'newMessages', render: _=> div(
                            div(...item.newMessages.top.map(renderSupportThreadNewMessage)),
                            moreMessagesDiv({count: moreNewMessages, kind: 'new'}),
                        )}),
                        
                        item.newMessages.top.length > 0 && item.oldMessages.top.length > 0 &&
                            diva({style: {borderTop: `3px dotted ${lineColor}`, paddingTop: 5}}),
                                                      
                        uiStateScope({name: 'oldMessages', render: _=> div(
                            div(...item.oldMessages.top.map(renderSupportThreadOldMessage)),
                            moreMessagesDiv({count: moreOldMessages, kind: 'old'})
                        )}),
                        
                    )
                    
                    function moreMessagesDiv({count, kind}) {
                        if (count > 0) {
                            let title
                            if (LANG === 'en') {
                                raise('Implement en moreMessagesDiv')
                            } else if (LANG === 'ua') {
                                if (count === 1) {
                                    title = `...и еще одно`
                                    if (kind === 'new') title += ` новое сообщение`
                                    else if (kind === 'old') title += ` старое сообщение`
                                } else {
                                    title = `...и еще ${count}`
                                    if (kind === 'new') title += ` новых`
                                    else if (kind === 'old') title += ` старых`
                                        
                                    if (count >=2 && count <= 4) title += ` сообщения`
                                    else title += ` сообщений`
                                }
                            }
                            return diva({style: {textAlign: 'right'}}, ui.pageLink({name: 'andMore', url, title, style: {color: BLACK_BOOT, fontWight: 'normal', fontStyle: 'italic'}}))
                        }
                    }
                }})
            }
        }
        
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
        
        function makeRenderSupportThreadMessage({lineColor, dottedLines, dryFroms, showMessageNewLabel, paddingRight=0}) {
            return function renderSupportThreadMessage(message, messageIndex) {

                return uiStateScope({name: `message-${messageIndex}`, render: _=>
                diva({$model: message, className: 'row', style: asn({display: 'flex', flexWrap: 'wrap', paddingTop: messageIndex > 0 ? 5 : 0, paddingBottom: 5, paddingRight, marginLeft: 0, marginRight: 0, position: 'relative'},
                           messageIndex > 0 && dottedLines && {borderTop: `3px dotted ${lineColor}`})},
                           
                    diva({className: 'col-sm-3', style: {display: 'flex', flexDirection: 'column', borderRight: `3px solid ${lineColor}`, paddingLeft: 0}},
                        messageIndex === 0 || !dryFroms
                            ? div(diva({style: {whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis'}}, spana({style: {fontWeight: 'bold'}},
                                      t(`TOTE`, `От: `)),
                                      userLabel({name: 'from', user: message.sender})),
                                  diva({style: {whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis'}}, spana({style: {fontWeight: 'bold'}},
                                      t(`TOTE`, `Кому: `)),
                                      message.recipient ? userLabel({name: 'to', user: message.recipient})
                                                        : spanc({name: 'to', content: t(`TOTE`, `В рельсу`)})))
                            : diva({style: {fontWeight: 'bold'}}, spanc({name: 'continuation', content: t(`TOTE`, `В догонку`)})),
                                                           
                        diva({style: {marginTop: 10}},
                            spanc({name: 'timestamp', content: timestampString(message.inserted_at)}),
                        )
                    ),
                    diva({className: 'col-sm-9', style: {display: 'flex', flexDirection: 'column', paddingRight: 5, whiteSpace: 'pre-wrap', position: 'relative'}},
                        div(
                            run(function renderLabels() {
                                if (showMessageNewLabel) {
                                    const seen = message.data.seenBy[ui.getUser().id]
                                    const justBecameSeen = seen && ui.prevPageStuff[`supportThreadMessageNewLabelWasRendered-${message.id}`]
                                    if (!seen) {
                                        ui.currentPageStuff[`supportThreadMessageNewLabelWasRendered-${message.id}`] = true
                                    }
                                    if (!seen || justBecameSeen) {
                                        return spanc({name: 'newLabel', className: `label label-primary ${justBecameSeen ? 'aniFadeOutDelayed' : ''}`, style: {float: 'right'}, content: t(`New`, `Новое`)})
                                    }
                                }
                            }),
                            spanc({name: 'message', content: message.message}),
                            ),
                        
                    ))})
            }
        }

        function userLabel(def) {
            #extract {user} from def
            
            const glyphName = lookup(user.kind, {customer: 'user', writer: 'pencil', admin: 'cog'})
            const glyph = ia({className: `fa fa-${glyphName}`, style: {marginLeft: 5, marginRight: 5}})
            
            const me = {
                render() {
                    return spana(def, glyph, me.getValue())
                },
                getValue() { return user.first_name + ' ' + user.last_name },
            }
            
            implementControlTestFacilities({me, def, controlTypeName: 'userLabel'})
            return elcl(me)
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
            TopNavItem({name: 'why', title: t(`Why Us?`, `Почему мы?`)}),
            TopNavItem({name: 'prices', title: t(`Prices`, `Цены`)}),
            TopNavItem({name: 'samples', title: t(`Samples`, `Примеры`)}),
            TopNavItem({name: 'faq', title: t(`FAQ`, `ЧаВо`)}),
            TopNavItem({name: 'contact', title: t(`Contact Us`, `Связь`)}),
            TopNavItem({name: 'blog', title: t(`Blog`, `Блог`)}),
        ]
    } else {
        if (!user || user.kind !== 'admin') {
            proseItems = [
                TopNavItem({name: 'why', title: t(`Why Us?`, `Почему мы?`)}),
                TopNavItem({name: 'prices', title: t(`Prices`, `Цены`)}),
                TopNavItem({name: 'faq', title: t(`FAQ`, `ЧаВо`)}),
            ]
        }
    }
    
    let privateItems
    if (user) {
        if (clientKind === 'customer') {
            privateItems = [
                TopNavItem({name: 'orders', title: t(`My Orders`, `Мои заказы`)}),
                TopNavItem({name: 'support', title: t(`Support`, `Поддержка`), liveStatusFieldName: 'supportMenuBadge'}),
            ]
        } else {
            if (user.kind === 'writer') {
                privateItems = compact([
                    user.state === 'cool' && TopNavItem({name: 'orders', title: t(`My Orders`, `Мои заказы`)}),
                    user.state === 'cool' && TopNavItem({name: 'store', title: t(`Store`, `Аукцион`)}),
                    TopNavItem({name: 'profile', title: t(`Profile`, `Профиль`)}),
                    TopNavItem({name: 'support', title: t(`Support`, `Поддержка`), liveStatusFieldName: 'supportMenuBadge'})
                ])
            } else if (user.kind === 'admin') {
                privateItems = []
                privateItems.push(TopNavItem({name: 'admin-heap', title: t(`TOTE`, `Куча`), liveStatusFieldName: 'heapSize'}))
                if (user.roles.support) {
                    privateItems.push(TopNavItem({name: 'support', title: t(`Support`, `Поддержка`), liveStatusFieldName: 'supportMenuBadge'}))
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
            if (proseItems.some(x => x.name === highlightedItem)) {
                dropdownAStyle = {backgroundColor: '#e7e7e7'}
            }
            leftNavbarItems.push(
                lia({className: 'dropdown'},
                    aa({href: '#', className: 'dropdown-toggle skipClearMenus', style: dropdownAStyle, 'data-toggle': 'dropdown', role: 'button'}, t(`Prose`, `Проза`), spana({className: 'caret', style: {marginLeft: 5}})),
                    ula({className: 'dropdown-menu'},
                        ...proseItems)))
        }
        leftNavbarItems.push(...privateItems)
        rightNavbarItem = TopNavItem({name: 'dashboard', title: t(user.first_name)})
    } else {
        leftNavbarItems = proseItems
        rightNavbarItem = TopNavItem({name: 'sign-in', title: t(`Sign In`, `Вход`)})
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
                           
                           
    function TopNavItem(def) {
        const res = TopNavItem_(asn(def, {ui, active: highlightedItem === def.name}))
        res.name = def.name
        return res
    }
    
    function killme_itemToLia({name, title, liveStatusFieldName}) {
        return TopNavItem({ui, name, title, liveStatusFieldName, active: highlightedItem === name})
        
        // TODO:vgrechka @refactor Kill renderTopNavbar::makeLink    47924ff3-db76-463f-9a3e-1099586d6219 
        const testName = `topNavItem-${name}`
        const id = puid()
        return lia({className: highlightedItem === name ? 'active' : ''},
                    makeLink(name, span(spanc({name: testName, content: title}),
                                        liveStatusFieldName && ui.liveBadge({name: testName, liveStatusFieldName}))))
    }
                           
    // TODO:vgrechka @refactor Kill renderTopNavbar::makeLink    47924ff3-db76-463f-9a3e-1099586d6219 
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
                        testActionHand = showTestActionHand(asn({target: byid(id)}, testActionHandOpts))
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
                    
                    effects.blinkOn({target: byid(id).parent(), fixed: true, dleft, dwidth})
                    testGlobal['topNavbarLink_' + name + '_blinks'] = true
                    
                    if ((!isDynamicPage(name) || ~['sign-in', 'sign-up'].indexOf(name)) && !(isInTestScenario() && getTestSpeed() === 'fast')) {
                        await delay(ACTION_DELAY_FOR_FANCINESS)
                    }
                    await ui.pushNavigate(href)
                    
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



