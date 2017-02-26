package aps.front

import aps.*
import into.kommon.*

object TabithaURLQuery : URLQueryParamsMarker {
    val id by LongURLParam()
    val tab by MaybeStringURLParam()
}

interface TabithaTab {
    val tabSpec: TabSpec
    suspend fun load(): FormResponse2.Shitty<*>?
}

class Tabitha<EntityRTO>(
    val tabKeys: Fuckers<TabKey>,
    val defaultTab: TabKey,
    val loadEntity: suspend (Long) -> FormResponse2<EntityResponse<EntityRTO>>,
    val renderBelowHeader: () -> ToReactElementable,
    val makeTabs: () -> List<TabithaTab>,
    val pageHeaderTitle: () -> String,
    val subtitle: () -> String?,
    val renderBelowSubtitle: () -> ToReactElementable,
    val page: PageSpec
) where
    EntityRTO : TabithaEntityRTO
{
    var entityID by notNullOnce<Long>()
    var entity by notNullOnce<EntityRTO>()

    suspend fun load(): PageLoadingError? {
        entityID = TabithaURLQuery.id.get()
        val tabKey = tabKeys.items.findSimplyNamed(TabithaURLQuery.tab.get(Globus.world)) ?: defaultTab

        val res = loadEntity(entityID)
        entity = when (res) {
            is FormResponse2.Shitty -> {
                return PageLoadingError(res.error)
            }
            is FormResponse2.Hunky -> res.meat.entity
        }

        val tabs = makeTabs()
        val tab = tabs.find {it.tabSpec.key == tabKey} ?: tabs.first()

        tab.load()?.let {
            return PageLoadingError(it.error)
        }

        Globus.world.setPage(Page(
            header = pageHeader3(kdiv{o->
                o- pageHeaderTitle()
            }),

            body = kdiv{o->
                o- renderBelowHeader()
                subtitle()?.let {
                    o- h4(marginBottom = "0.7em"){o->
                        o- it
                    }
                }
                o- renderBelowSubtitle()

                o- Tabs2(
                    initialActiveKey = tab.tabSpec.key,
                    switchOnTabClick = false,
                    onTabClicka = {clickOnTab(it)},
                    tabs = tabs.map {it.tabSpec}
                )
            }
        ))
        return pageLoadedFineResult
    }

    suspend fun reloadPage(paramValues: List<URLParamValue<*>> = listOf()) {
        Globus.world.replaceNavigate(makeURL(page, listOf(
            URLParamValue(TabithaURLQuery.id, entityID)
        ) + paramValues))
    }

    suspend fun clickOnTab(key: TabKey) {
        await(effects).blinkOn(byid(key.fqn), BlinkOpts(dwidth = "-0.15rem"))
        TestGlobal.switchTabHalfwayLock.resumeTestAndPauseSutFromSut()
        try {
            val q = TabithaURLQuery
            Globus.world.pushNavigate(makeURL(page, listOf(
                URLParamValue(q.id, entityID),
                URLParamValue(q.tab, simpleName(key.fqn))
            )))
        } finally {
            TestGlobal.switchTabDoneLock.resumeTestAndPauseSutFromSut()
        }
    }
}

class UsualParamsTab<ItemRTO, HistoryItemRTO, HistoryFilter, Req : RequestMatumba, Res : CommonResponseFields>(
    tabitha: Tabitha<*>,
    tabKey: TabKey,
    renderBody: () -> ToReactElementable,
    hasEditButton: Boolean,
    editModalTitle: String,
    formSpec: FormSpec<Req, Res>,
    val historyParams: HistoryParams<HistoryItemRTO, HistoryFilter>? = null
)
    : TabithaTab
where
    HistoryFilter : Enum<HistoryFilter>, HistoryFilter : Titled,
    HistoryItemRTO : MelindaItemRTO, HistoryItemRTO : HistoryItemRTOFields
{
    enum class Mode {
        CURRENT, HISTORY
    }
    private val mode get()= urlQuery.paramsMode.get()

    private var historyBoobs by notNullOnce<MelindaBoobsInterface>()

    override suspend fun load(): FormResponse2.Shitty<*>? {
        if (mode == Mode.HISTORY) {
            historyParams!!
            val boobs = MelindaBoobs<HistoryItemRTO, HistoryFilter, /*CreateRequest=*/ Nothing, /*CreateResponse=*/ Nothing, /*UpdateItemRequest=*/ Nothing, /*UpdateItemResponse=*/ Nothing>(
                createParams = null,
                makeURLForReload = {boobsParams->
                    makeURL(pages.uaCustomer.order, boobsParams + listOf(
                        URLParamValue(urlQuery.paramsMode, Mode.HISTORY)
                    ))
                },
                filterValues = historyParams.historyFilterValues,
                defaultFilterValue = historyParams.defaultHistoryFilterValue,
                filterSelectKey = historyParams.historyFilterSelectKey,
                vaginalInterface = MelindaVagina<HistoryItemRTO, HistoryFilter, /*UpdateItemRequest=*/ Nothing, /*UpdateItemResponse=*/ Nothing>(
                    sendItemsRequest = historyParams.sendItemsRequest,
                    shouldShowFilter = {true},
                    getParentEntityID = {TabithaURLQuery.id.get()},
                    humanItemTypeName = t("TOTE", "imf db47abf8-ae10-4450-8328-7eeace10c476"),
                    makeDeleteItemRequest = {unsupported("bae3c22e-5397-4b7d-82fa-7558f7836f39")},
                    updateParams = null,
                    makeLipsInterface = {viewRootID, tongue -> makeUsualMelindaLips(
                        tongue, viewRootID, historyBoobs,
                        icon = {fa.calendarCheckO},
                        initialLipsState = Unit,
                        renderContent = {o->
                            o- historyParams.renderItem(tongue)
                        },
                        titleLinkURL = null,
                        hasEditControl = {false},
                        hasDeleteControl = {false},
                        drawID = {false},
                        secondTitle = {formatUnixTime(tongue.item.createdAt, includeTZ = false)},
                        burgerMenu = {
                            Menu(mutableListOf<MenuItem>()-{o->
                                if (tongue.itemIndex < tongue.items.lastIndex) {
                                    val itemBelow = tongue.items[tongue.itemIndex + 1]
                                    o += MenuItem(t("TOTE", "Сравнить вниз")) {
                                        clog("Comparing", "this", formatUnixTime(tongue.item.createdAt), "other", formatUnixTime(itemBelow.createdAt))
                                    }
                                }
                            })
                        }
                    )}
                )
            )
            historyBoobs = boobs.boobsInterface
            boobs.load()
        }

        return null
    }

    object urlQuery : URLQueryParamsMarker {
        val paramsMode by EnumURLParam(Mode.values(), default = Mode.CURRENT)
    }

    override val tabSpec = SimpleTabSpec(
        key = tabKey,
        title = when (mode) {
            UsualParamsTab.Mode.CURRENT -> t("TOTE", "Параметры")
            UsualParamsTab.Mode.HISTORY -> t("TOTE", "Параметры: история")
        },
        renderBody = {when (mode) {
            UsualParamsTab.Mode.CURRENT -> renderBody()
            UsualParamsTab.Mode.HISTORY -> historyBoobs.mainContent
        }},
        renderStrip = {kdiv{o->
            o- when (mode) {
                Mode.CURRENT -> hor2{o->
                    if (historyParams != null) {
                        o- Button(icon = fa.calendar, level = Button.Level.DEFAULT, key = buttons.history) {
                            effects2.blinkOn(it.jqel)
                            tabitha.reloadPage(listOf(URLParamValue(urlQuery.paramsMode, Mode.HISTORY)))
                        }
                    }

                    if (hasEditButton) {
                        o- Button(icon = fa.pencil, level = Button.Level.DEFAULT, key = buttons.edit) {
                            openEditModal(
                                title = editModalTitle,
                                formSpec = formSpec,
                                onSuccessa = {
                                    tabitha.reloadPage()
                                }
                            )
                        }
                    }
                }

                Mode.HISTORY -> historyBoobs.controlsContent
            }
        }}
    )

}

class HistoryParams<HistoryItemRTO, Filter>(
    val renderItem: (tongue: MelindaTongueInterface<HistoryItemRTO>) -> ToReactElementable,
    val sendItemsRequest: suspend (req: ItemsRequest<Filter>) -> FormResponse2<ItemsResponse<HistoryItemRTO>>,
    val historyFilterValues: Array<Filter>,
    val defaultHistoryFilterValue: Filter,
    val historyFilterSelectKey: SelectKey<Filter>
) where
    Filter : Enum<Filter>,
    Filter : Titled


























