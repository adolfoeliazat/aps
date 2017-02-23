package aps.front

import aps.*

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

    suspend fun reloadPage() {
        Globus.world.replaceNavigate(makeURL(page, listOf(
            URLParamValue(TabithaURLQuery.id, entityID)
        )))
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

class UsualParamsTab<Req : RequestMatumba, Res : CommonResponseFields>(
    tabitha: Tabitha<*>,
    tabKey: TabKey,
    content: ToReactElementable,
    hasEditButton: Boolean,
    editModalTitle: String,
    formSpec: FormSpec<Req, Res>
)
    : TabithaTab
{
    override suspend fun load(): FormResponse2.Shitty<*>? = null

    override val tabSpec = SimpleTabSpec(
        key = tabKey,
        title = t("TOTE", "Параметры"),
        content = content,
        stripContent = kdiv{o->
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
    )
}

























