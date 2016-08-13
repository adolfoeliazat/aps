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

    
require('regenerator-runtime/runtime') // TODO:vgrechka Get rid of this shit, as I don't want to support old browsers anyway    090d9d02-b286-4b1e-a062-b2312855e2f1 

#import static 'into-u/utils-client ./stuff'

import {link, faIcon, Select, spanc, implementControlShit, renderStacks, OpenSourceCodeLink, CollapsibleShit,
        button, pageTopBlockQuote, nostring, openDebugPane, debugSectionTitle, horizontala, hor1, hor2,
        Input, input, preventAndStop, renderLangLabel, spancTitle} from 'into-u/ui'
#import static 'into-u/ui'
    

COLOR_1_MEDIUM = BLUE_GRAY_400
COLOR_1_DARK = BLUE_GRAY_600

Error.stackTraceLimit = Infinity


global.igniteShit = makeUIShitIgniter({
    Impl: function hot$ImplForShitIgniter({ui}) {
        return {
            isDynamicPage,
            
            css() {
                const zebraLight = WHITE
                const zebraDark = BLUE_GRAY_50
                
                let res = `
                    body {overflow-x: hidden;}
                
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
                
                    .showOnParentHovered {display: none;}
                    .showOnParentHovered-parent:hover .showOnParentHovered {display: initial;}
                    
                    .zebra-0 {background: ${zebraLight};}
                    .zebra-0 .borderTopColoredOnZebra {border-top-color: ${zebraDark};}
                    .zebra-0 .borderRightColoredOnZebra {border-right-color: ${zebraDark};}
                    .label1 {background-color: ${TEAL_50};}
                
                    .zebra-1 {background: ${zebraDark};}
                    .zebra-1 .borderTopColoredOnZebra {border-top-color: ${zebraLight};}
                    .zebra-1 .borderRightColoredOnZebra {border-right-color: ${zebraLight};}
                    .zebra-1 .label1 {background-color: ${TEAL_100};}
                `
                return res
            },
            
            
            privatePageLoader(name) {
                return {
                    orders: ordersPageLoader,
                    
                    async 'admin-heap'() {
                        // @wip admin-heap.html
                        await melinda(s{
                            urlPath: 'admin-heap.html',
                            trainName: 'Load admin-heap page',
                            itemsFun({activeTab}) {
                                if (activeTab === 'support') return 'private_getUnassignedSupportThreads'
                                raise('Weird active tab')
                            },
                            entityID: ui.urlQuery.thread,
                            header: pageHeader({title: t(`TOTE`, `Куча работы`)}),
                            defaultOrdering: 'asc',
                            defaultActiveTab: 'support',
                            tabDefs: [
                                {
                                    name: 'support',
                                    content: ui.taby({
                                        title: t(`TOTE`, `Поддержка`),
                                        liveStatusFieldName: 'unassignedSupportThreadCount',
                                        url: 'admin-heap.html?tab=support',
                                    })
                                },
                            ],
                            renderItem: makeRenderSupportThread({topicIsLink: false, hasTakeAndReplyButton: true, dryFroms: true}),
                        })
                        
                        
                        
                        return
                        
                        // @wip old admin-heap.html
                        
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
                                    header: pageHeader({title: pageTitle}),
                                    body: div(errorBanner(itemsRes.error))
                                })
                            }
                            
                            function tabydef(def) {
                                #extract {name} from def
                                
                                return {
                                    name,
                                    content: ui.taby({}.asn1(def))
                                }
                            }
                            
                            ui.setPage({
                                header: pageHeader({title: pageTitle}),
                                body: div(
                                    ui.tabs({name: 'main', active: activeTab, tabDefs: [
                                        tabydef(s{
                                            name: 'support',
                                            title: t(`TOTE`, `Поддержка`),
                                            liveStatusFieldName: 'unassignedSupportThreadCount',
                                            url: 'admin-heap.html?tab=support',
                                        }),
                                    ]}),
                                    ui.renderMoreable(s{itemsRes, itemsReq, renderItem: makeRenderSupportThread({topicIsLink: false, hasTakeAndReplyButton: true, dryFroms: true})})
                                )
                            })
                            
                        } finally { endTrain() }
                    },
                    
                    async 'admin-heap-bak'() {
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
                                    header: pageHeader({title: pageTitle}),
                                    body: div(errorBanner(itemsRes.error))
                                })
                            }
                            
                            function tabydef(def) {
                                #extract {name} from def
                                
                                return {
                                    name,
                                    content: ui.taby({}.asn1(def))
                                }
                            }
                            
                            ui.setPage({
                                header: pageHeader({title: pageTitle}),
                                body: div(
                                    ui.tabs({name: 'main', active: activeTab, tabDefs: [
                                        tabydef(s{
                                            name: 'support',
                                            title: t(`TOTE`, `Поддержка`),
                                            liveStatusFieldName: 'unassignedSupportThreadCount',
                                            url: 'admin-heap.html?tab=support',
                                        }),
                                    ]}),
                                    ui.renderMoreable(s{itemsRes, itemsReq, renderItem: makeRenderSupportThread({topicIsLink: false, hasTakeAndReplyButton: true, dryFroms: true})})
                                )
                            })
                            
                        } finally { endTrain() }
                    },
                    
                    async 'admin-my-tasks'() {
                        ui.setPage({
                            header: pageHeader({title: 'rtrtyyt'}),
                            body: div(
                                t(`TOTE`, `foooooooooo`), aa({href: '#', onClick() { dlog('cliiiiick') }}, t('qqqqqqqqqqq')))
                        })
                    },
                    
                    
                    async support() {
                        if (ui.urlQuery.thread) {
                            // @wip support.html
                            await melinda(s{
                                urlPath: 'support.html', urlEntityParamName: 'thread',
                                trainName: 'Load support page with thread param',
                                entityFun: 'private_getSupportThread',
                                itemsFun: 'private_getSupportThreadMessages',
                                entityID: ui.urlQuery.thread,
                                header: entityRes => {
                                    let labels
                                    if (entityRes.entity.status === 'resolved') {
                                        labels = [{level: 'success', title: t(`TOTE`, `Решён`)}]
                                    }
                                    return pageHeader({title: t(`TOTE`, `Запрос в поддержку № ${entityRes.entity.id}`), labels})
                                },
                                hasHeaderControls: entityRes => {
                                    if (ui.getUser().kind === 'admin') return true
                                    return entityRes.entity.status === 'open'
                                },
                                aboveItems(entityRes) {
                                    return pageTopBlockQuote({content: entityRes.entity.topic})
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
                                    statusValues.push({value: 'resolved', title: t(`TOTE`, `Решён`)})
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
                                
                                renderItem(def) {
                                    #extract {item: message, index} from def
                                    
                                    return diva({controlTypeName: 'renderItem-leila', className: `zebra-${index % 2}`, style: {}},
                                        makeRenderSupportThreadMessage({showMessageNewLabel: true})(s{message, index}))
                                },
                            })
                        } else if (!ui.urlQuery.thread) {
                            beginTrain({name: 'Load support page without thread param'}); try {
                                const itemsReq = s{fun: 'private_getSupportThreads', filter: ui.urlQuery.filter || 'updatedOrAll'}
                                const itemsRes = await ui.rpcSoft(itemsReq)
                                if (itemsRes.error) return ui.setToughLuckPage({res: itemsRes})
                                
                                const filter = itemsRes.filter
                                
                                const tabDefs = itemsRes.availableFilters.map(name => ({
                                    name,
                                    content: ui.taby({
                                        title: lookup(name, {
                                            updated: t(`TOTE`, `Обновленные`),
                                            all: t(`TOTE`, `Все`)
                                        }),
                                        url: `support.html?filter=${name}`
                                    })}))
                                
                                ui.setPage({
                                    header: pageHeader({title: t(`TOTE`, `Поддержка`)}),
                                    body: div(
                                        ui.tabs({name: 'main', tabDefs, active: filter}),
                                        ui.renderMoreable(s{itemsRes, itemsReq: {fun: 'private_getSupportThreadsChunk', filter}, renderItem: makeRenderSupportThread({topicIsLink: true, hasTakeAndReplyButton: false, showMessageNewLabel: true})}),
                                    )
                                })
                            } finally { endTrain() }
                        }
                    },
                
                    dashboard: dashboardPageLoader,
                    profile: profilePageLoader,
                }[name]
                
                // @wip melinda
                async function melinda(def) {
                    #extract {
                        trainName, urlPath, urlEntityParamName, tabDefs, defaultActiveTab,
                        header, entityID, entityFun, itemsFun, emptyMessage, plusIcon='plus', plusFormDef, editFormDef,
                        aboveItems, renderItem, defaultOrdering='desc', hasSearchBox=true, hasOrderingSelect=true, hasHeaderControls=true
                    } from def
                    
                    beginTrain({name: trainName}); try {
                        let entityRes
                        if (entityFun) {
                            entityRes = await ui.rpcSoft({fun: entityFun, entityID})
                            if (entityRes.error) return showBadResponse(entityRes)
                        }
                        
                        const searchString = ui.urlQuery.search
                        
                        let ordering = ui.urlQuery.ordering
                        if (!['asc', 'desc'].includes(ordering)) ordering = defaultOrdering
                        
                        let tabs, activeTab
                        if (tabDefs) {
                            activeTab = ui.urlQuery.tab || defaultActiveTab
                            tabs = ui.tabs({name: 'main', active: activeTab, tabDefs})
                        }

                        const itemsReq = {fun: fov(itemsFun, {activeTab}), entityID, ordering, searchString}
                        const itemsRes = await ui.rpcSoft(itemsReq)
                        if (itemsRes.error) return showBadResponse(itemsRes)
                        
                        let items, showEmptyLabel = true,
                            headerControlsVisible = true, headerControlsClass, headerControlsDisabled,
                            cancelForm,
                            plusShit, editShit
                            
                        let searchBox, searchBoxInput
                        if (hasSearchBox) {
                            searchBoxInput = Input({
                                style: {paddingLeft: 30, width: 160},
                                placeholder: t(`TOTE`, `Поиск...`),
                                disabled: _=> headerControlsDisabled,
                                async onKeyDown(e) {
                                    if (e.keyCode === 13) {
                                        preventAndStop(e)
                                        await applyHeaderControls({controlToBlink: searchBoxInput})
                                    }
                                }
                            })
                            searchBoxInput.setValue(itemsRes.searchString)
                                
                            searchBox = diva({style: {position: 'relative'}},
                                searchBoxInput,
                                faIcon({icon: 'search', style: {position: 'absolute', left: 10, top: 10, color: GRAY_500}}),
                            )
                        }
                        
                        let orderingSelect
                        if (hasOrderingSelect) {
                            orderingSelect = Select({
                                tamyShamy: 'ordering', isAction: true, style: {width: 160},
                                values: [{value: 'desc', title: t(`TOTE`, `Сначала новые`)}, {value: 'asc', title: t(`TOTE`, `Сначала старые`)}],
                                initialValue: ordering,
                                disabled: _=> headerControlsDisabled,
                                async onChange() {
                                    await applyHeaderControls({controlToBlink: orderingSelect})
                                },
                            })
                        }
                        
                        async function applyHeaderControls({controlToBlink}) {
                            setHeaderControlsDisabled(true)
                            controlToBlink.setBlinking(true)
                            const urlParamParts = []
                            if (urlEntityParamName) {
                                urlParamParts.push(`${urlEntityParamName}=${entityID}`)
                            }
                            urlParamParts.push(`ordering=${orderingSelect.getValue()}`)
                            const searchString = searchBoxInput.getValue().trim()
                            if (searchString) {
                                urlParamParts.push(`search=${searchString}`)
                            }
                            const url = `${urlPath}?${urlParamParts.join('&')}`
                            await ui.pushNavigate(url)
                            setHeaderControlsDisabled(false)
                            controlToBlink.setBlinking(false)
                        }
                        
                        if (plusFormDef) {
                            plusShit = makeButtonFormShit(s{name: 'plus', level: 'primary', icon: plusIcon, formDef: plusFormDef})
                        }
                        if (editFormDef) {
                            editShit = makeButtonFormShit(s{name: 'edit', level: 'default', icon: 'edit', formDef: editFormDef})
                        }
                            
                        
                        function makeButtonFormShit(def) {
                            #extract {name, level, icon, formDef} from def
                            
                            let form, formClass
                            
                            return {
                                button() {
                                    return button({tamyShamy: name, style: {marginLeft: 8}, level, icon, disabled: headerControlsDisabled, onClick() {
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
                        
                        let updateHeaderControls
                        
                        ui.setPage({
                            header: fov(header, entityRes),
                            body: _=> div(
                                tabs,
                                editShit && editShit.form,
                                plusShit && plusShit.form,
                                fov(aboveItems, entityRes),
                                run(function renderItems() {
                                    if (!itemsRes.items.length) {
                                        if (showEmptyLabel) {
                                            return diva({style: {marginTop: 10}}, emptyMessage || t(`TOTE`, `Странно, здесь ничего нет...`))
                                        }
                                        return ''
                                    }
                                    return ui.renderMoreable(s{itemsRes, itemsReq, renderItem,})
                                }),
                            ),
                            headerControls: _=> updatableElement(update => {
                                updateHeaderControls = update
                                if (!fov(hasHeaderControls, entityRes) || !headerControlsVisible) return
                                
                                return _=> hor2({
                                    style: {display: 'flex', marginTop: tabDefs ? 55 : 0},
                                    className: headerControlsClass},
                                    
                                    searchBox,
                                    orderingSelect,
                                    editShit && editShit.button,
                                    plusShit && plusShit.button,
                                )
                            }),
                            
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
                            updateHeaderControls()
                        } 
                        
                        function showBadResponse(res) {
                            return ui.setPage({
                                header: pageHeader({title: t(`TOTE`, `Облом`)}),
                                body: div(errorBanner(res.error))
                            })
                        }
                    } finally { endTrain() }
                }
                
                
                function ordersPageLoader() {
                    ui.setPage({
                        header: pageHeader({title: t('My Orders', 'Мои заказы')}),
                        body: div(
                            )
                    })
                }
                
                
                function dashboardPageLoader() {
                    ui.setPage({
                        header: pageHeader({title: t('Dashboard', 'Панель')}),
                        body: div(
// @killme
//                            elcl({
//                                render() {
//                                    dlog('rendering aaa')
//                                    return div(
//                                        elcl({
//                                            render() {
//                                                dlog('rendering bbb')
//                                                return div('bbb')
//                                            },
//                                            componentDidMount() {
//                                                dlog('mounted bbb')
//                                            },
//                                            componentWillMount() {
//                                                dlog('will mount bbb')
//                                            }
//                                        }),
//                                        'aaa',
//                                        elcl({
//                                            render() {
//                                                return div('ccc')
//                                            },
//                                            componentDidMount() {
//                                                dlog('mounted ccc')
//                                            }
//                                        }),
//                                        )
//                                },
//                                componentDidMount() {
//                                    dlog('mounted aaa')
//                                },
//                                componentWillMount() {
//                                    dlog('will mount aaa')
//                                }
//                            }),
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
                                           link({tamy: item.name, title: item.title, style: {color: '#333'}, onClick: item.onClick}))))
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
                        header: pageHeader({title: t('Profile', 'Профиль')}),
                        body: pageBody
                    })
                }
            },
            
            renderTopNavbar({highlightedItem}) {
                return renderTopNavbar({clientKind: CLIENT_KIND, highlightedItem, t, ui})
            },
        }
        
        // @ctx client helpers
        
        function makeRenderSupportThread({topicIsLink, hasTakeAndReplyButton, showMessageNewLabel, dryFroms}) {
            return function renderSupportThread(def) {
                #extract {item: thread, index} from def
                
                const url = `support.html?thread=${thread.id}`
                    
                let topicElement
                if (topicIsLink) {
                    topicElement = ui.urlLink({url, tamy: `topic`, shame: `link-threadTopic-${thread.id}`, title: {mopy: {model: thread, prop: 'topicWithID'}}, style: {color: BLACK_BOOT, fontWeight: 'bold'}})
                } else {
                    topicElement = spana({style: {color: BLACK_BOOT, fontWeight: 'bold'}}, spanc({tame: 'topic', content: {mopy: {model: thread, prop: 'topicWithID'}}}))
                }
                
                const paddingRight = hasTakeAndReplyButton ? 45 : 0
                const renderSupportThreadNewMessage = makeRenderSupportThreadMessage({dottedLines: true, dryFroms, showMessageNewLabel, paddingRight})
                const renderSupportThreadOldMessage = makeRenderSupportThreadMessage({dottedLines: true, dryFroms, showMessageNewLabel, paddingRight})
                
                const moreNewMessages = thread.newMessages.total - thread.newMessages.top.length
                const moreOldMessages = thread.oldMessages.total - thread.oldMessages.top.length
                
                return diva({controlTypeName: 'renderSupportThread', tame: `thread${sufindex(index)}`, className: `zebra-${index % 2}`, style: {position: 'relative'}},
                    diva({style: {position: 'absolute', right: 0, top: 0, zIndex: 1000}},
                        hasTakeAndReplyButton && ui.busyButton({tamy: `takeAndReply`, shame: `button-takeAndReply-${thread.id}`, icon: 'comment', iconColor: COLOR_1_DARK, hint: t(`TOTE`, `Взять себе и ответить`), async onClick() {
                            beginTrain({name: 'Take support thread and reply'}); try {
                                await ui.rpc({fun: 'private_takeSupportThread', id: thread.id})
                                // TODO:vgrechka Handle private_takeSupportThread RPC failure. Need error popup or something instead of trying to pushNavigate    12fbe33a-c4a5-4967-9cec-5c2aa217e947 
                                await ui.pushNavigate(`support.html?thread=${thread.id}`)
                            } finally { endTrain() }
                        }}),
                        ),
                    
                    diva({className: '', style: {marginTop: 10,  marginBottom: 5, paddingRight: 45}},
                        topicElement,
                        // renderLangLabel(s{style: {marginLeft: 8, fontWeight: 'bold'}, content: {mopy: {model: thread, prop: 'tslang'}}})
                        ),
                        
                    diva({tame: 'newMessages'},
                        diva({}, ...thread.newMessages.top.map((message, index) => renderSupportThreadNewMessage(s{message, index}))),
                        moreMessagesDiv({count: moreNewMessages, kind: 'new'}),
                    ),
                    
                    thread.newMessages.top.length > 0 && thread.oldMessages.top.length > 0 &&
                        diva({className: 'borderTopColoredOnZebra', style: {borderTopWidth: '3px', borderTopStyle: 'dotted', paddingTop: 5}}),
                                                  
                    diva({tame: 'oldMessages'},
                        div(...thread.oldMessages.top.map((message, index) => renderSupportThreadOldMessage(s{message, index}))),
                        moreMessagesDiv({count: moreOldMessages, kind: 'old'})
                    ),
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
                        return diva({style: {textAlign: 'right'}}, ui.urlLink({tamy: 'andMore', shame: `link-andMore-${thread.id}`, url, title, style: {color: BLACK_BOOT, fontWight: 'normal', fontStyle: 'italic'}}))
                    }
                }
            }
        }
        
        function makeRenderSupportThreadMessage({dottedLines, dryFroms, showMessageNewLabel, paddingRight=0}) {
            return function renderSupportThreadMessage(def) {
                #extract {message, index} from def
                
                return diva({controlTypeName: 'renderSupportThreadMessage', tame: `message${sufindex(index)}`, className: 'row borderTopColoredOnZebra', style: asn({display: 'flex', flexWrap: 'wrap', paddingTop: index > 0 ? 5 : 0, paddingBottom: 5, paddingRight, marginLeft: 0, marginRight: 0, position: 'relative'},
                           index > 0 && dottedLines && {borderTopStyle: 'dotted', borderTopWidth: '3px'})},
                           
                    diva({className: 'col-sm-3 borderRightColoredOnZebra', style: {display: 'flex', flexDirection: 'column', borderRightWidth: '3px', borderRightStyle: 'solid', paddingLeft: 0}},
                        index === 0 || !dryFroms
                            ? div(diva({style: {whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis'}}, spana({style: {fontWeight: 'bold'}},
                                      t(`TOTE`, `От: `)),
                                      userLabel({tamy: 'from', user: message.sender})),
                                  diva({style: {whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis'}}, spana({style: {fontWeight: 'bold'}},
                                      t(`TOTE`, `Кому: `)),
                                      message.recipient ? userLabel({tamy: 'to', user: message.recipient})
                                                        : spanc({tame: 'to', content: t(`TOTE`, `В рельсу`)})))
                            : diva({style: {fontWeight: 'bold'}}, spanc({tame: 'continuation', content: t(`TOTE`, `Вдогонку`)})),
                                                           
                        diva({style: {marginTop: 10}},
                            spanc({tame: 'timestamp', content: timestampString(message.inserted_at)}),
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
                                        return spanc({
                                            tame: 'newLabel',
                                            className: `label label-primary ${justBecameSeen ? 'aniFadeOutDelayed' : ''}`,
                                            style: {float: 'right'},
                                            content: t(`New`, `Новое`)})
                                    }
                                }
                            }),
                            spanc({tame: 'message', content: {mopy: {model: message, prop: 'message'}}}),
                            ),
                        
                    )
                )
            }
        }

        function userLabel(def) {
            #extract {user} from def
            
            const title = user.first_name + ' ' + user.last_name
            
            const me = {
                render() {
                    return spana({id: me.elementID},
                        faIcon({tame: 'icon', style: {marginLeft: 5, marginRight: 5}, icon: lookup(user.kind, {
                            customer: 'user', writer: 'pencil', admin: 'cog'})}),
                        spancTitle({title}))
                },
//                contributeTestState(state) {
//                    state.put(s{control: me, key: me.getTamePath(), value: title})
//                },
            }
            
            me.controlTypeName = 'userLabel'
            implementControlShit({me, def})
            return elcl(me)
        }

        function brightBadgea(def, content) {
            #extract {style} from def
            return spana({className: 'badge', style: asn({paddingRight: 8, backgroundColor: BLUE_GRAY_400}, style)}, content) 
        }
    },
    
    isTestScenarioNameOK(name) {
        if (LANG == 'ua' && CLIENT_KIND === 'customer' && name.startsWith('UA Customer :: ')) return true
        if (LANG == 'ua' && CLIENT_KIND === 'writer'
            && (name.startsWith('UA Writer :: ')
                || name.startsWith('UA Admin :: ')
                || name.startsWith('UA Bits :: '))) return true
    },
    
    testScenarios({sim}) {
        return asn({},
            require('./test-writer-ua')({sim}),
            require('./test-admin-ua')({sim}),)
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
    const proseCounter = [0]
    if (clientKind === 'customer') {
        proseItems = [
            TopNavItem({name: 'why', title: t(`Why Us?`, `Почему мы?`), counter: proseCounter}),
            TopNavItem({name: 'prices', title: t(`Prices`, `Цены`), counter: proseCounter}),
            TopNavItem({name: 'samples', title: t(`Samples`, `Примеры`), counter: proseCounter}),
            TopNavItem({name: 'faq', title: t(`FAQ`, `ЧаВо`), counter: proseCounter}),
            TopNavItem({name: 'contact', title: t(`Contact Us`, `Связь`), counter: proseCounter}),
            TopNavItem({name: 'blog', title: t(`Blog`, `Блог`), counter: proseCounter}),
        ]
    } else {
        if (!user || user.kind !== 'admin') {
            proseItems = [
                TopNavItem({name: 'why', title: t(`Why Us?`, `Почему мы?`), counter: proseCounter}),
                TopNavItem({name: 'prices', title: t(`Prices`, `Цены`), counter: proseCounter}),
                TopNavItem({name: 'faq', title: t(`FAQ`, `ЧаВо`), counter: proseCounter}),
            ]
        }
    }
    
    let privateItems
    const privateCounter = [0]
    if (user) {
        if (clientKind === 'customer') {
            privateItems = [
                TopNavItem({name: 'orders', title: t(`My Orders`, `Мои заказы`), counter: privateCounter}),
                TopNavItem({name: 'support', title: t(`Support`, `Поддержка`), liveStatusFieldName: 'supportMenuBadge', counter: privateCounter}),
            ]
        } else {
            if (user.kind === 'writer') {
                privateItems = compact([
                    user.state === 'cool' && TopNavItem({name: 'orders', title: t(`My Orders`, `Мои заказы`), counter: privateCounter}),
                    user.state === 'cool' && TopNavItem({name: 'store', title: t(`Store`, `Аукцион`), counter: privateCounter}),
                    TopNavItem({name: 'profile', title: t(`Profile`, `Профиль`), counter: privateCounter}),
                    TopNavItem({name: 'support', title: t(`Support`, `Поддержка`), liveStatusFieldName: 'supportMenuBadge', counter: privateCounter})
                ])
            } else if (user.kind === 'admin') {
                privateItems = []
                privateItems.push(TopNavItem({name: 'admin-heap', title: t(`TOTE`, `Куча`), liveStatusFieldName: 'heapSize', counter: privateCounter}))
                if (user.roles.support) {
                    privateItems.push(TopNavItem({name: 'support', title: t(`Support`, `Поддержка`), liveStatusFieldName: 'supportMenuBadge', counter: privateCounter}))
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
                lia({tame: 'prose', className: 'dropdown'},
                    aa({href: '#', className: 'dropdown-toggle skipClearMenus', style: dropdownAStyle, 'data-toggle': 'dropdown', role: 'button'}, t(`Prose`, `Проза`), spana({className: 'caret', style: {marginLeft: 5}})),
                    ula({className: 'dropdown-menu'},
                        ...proseItems)))
        }
        leftNavbarItems.push(...privateItems)
        rightNavbarItem = TopNavItem({name: 'dashboard', title: t(user.first_name), counter: [0]})
    } else {
        leftNavbarItems = proseItems
        rightNavbarItem = TopNavItem({name: 'sign-in', title: t(`Sign In`, `Вход`), counter: [0]})
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
                       ula({tame: 'topNavLeft', id: 'leftNavbar', className: 'nav navbar-nav', style: {float: 'none', display: 'inline-block', verticalAlign: 'top'}},
                           ...leftNavbarItems),
                       ula({tame: 'topNavRight', id: 'rightNavbar', className: 'nav navbar-nav navbar-right'},
                           rightNavbarItem))))
                           
                           
    function TopNavItem(def) {
        #extract {counter} from def
        
        const active = highlightedItem === def.name
        const res = TopNavItem_(asn(def, s{
            shame: `TopNavItem-${def.name}`,
            tame: `TopNavItem${sufindex(counter[0]++)}`,
            tattrs: {active: active || undefined},
            ui,
            active}))
        res.name = def.name
        return res
    }
    
    function killme_itemToLia({name, title, liveStatusFieldName}) {
        return TopNavItem({ui, name, title, liveStatusFieldName, active: highlightedItem === name})
        
        // TODO:vgrechka @refactor Kill renderTopNavbar::makeLink    47924ff3-db76-463f-9a3e-1099586d6219 
        const testName = `topNavItem-${name}`
        const id = puid()
        return lia({className: highlightedItem === name ? 'active' : ''},
                    makeLink(name, span(spanc({tame: testName, content: title}),
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













function compiler$getRegExpForAddingSourceLocationTo() {
    return /((\s|\(|\[|exports\.|ui_1\.)(diva|spana|ula|ola|lia|spanc|Input|userLabel|button|ui\.busyButton|Checkbox|Select|ui\.rpcSoft|ui\.liveBadge|ui\.liveBadge2|TopNavItem|link|ui\.pageLink|ui\.urlLink|ui\.taby|spancTitle|pageHeader|horizontala|hor1|hor2|renderStacks|faIcon|OpenSourceCodeLink|CollapsibleShit|blockquotea|pageTopBlockQuote)\(\{)/g /*})*/
}
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    