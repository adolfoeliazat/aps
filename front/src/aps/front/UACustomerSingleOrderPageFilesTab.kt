package aps.front

import aps.*
import into.kommon.*
import kotlin.properties.Delegates.notNull

class TestDownloadContext {
    val downloadStartedLock by notNullNamed(TestLock(virgin = true))
    val bitsReceivedLock by notNullNamed(TestLock(virgin = true))
    var shit by notNull<DownloadFileResponse>()
}

class UACustomerSingleOrderPageFilesTab(val order: UAOrderRTO) : TabithaTab {
    var boobsInterface by notNullOnce<MelindaBoobsInterface>()

    override val tabSpec = object:TabSpec {
        override val key = tabs.order.files
        override val title = t("Files", "Файлы")
        override val renderBody = {boobsInterface.mainContent}
        override val renderStrip = {boobsInterface.controlsContent}
    }

    override suspend fun load(): FormResponse2.Shitty<*>? {
        val boobs = MelindaBoobs<
            UAOrderFileRTO,
            UAOrderFileParamsRequest, UACreateOrderFileResponse,
            UAOrderFileParamsRequest, UAUpdateOrderFileResponse
        >(
            createParams = MelindaCreateParams(
                hasCreateButton = when (Globus.world.user.kind) {
                    UserKind.CUSTOMER -> order.state in setOf(UAOrderState.CUSTOMER_DRAFT, UAOrderState.RETURNED_TO_CUSTOMER_FOR_FIXING)
                    UserKind.ADMIN -> true
                    UserKind.WRITER -> imf("3fec622f-bd27-4704-b114-da676a25f00c")
                },
                createModalTitle = t("TOTE", "Новый файл"),
                makeCreateRequest = {UAOrderFileParamsRequest(isAdmin = isAdmin(), isUpdate = false)-{o->
                    o.orderID.value = order.id
                }},
                createProcedureNameIfNotDefault = "UACreateOrderFile",
                makeURLAfterCreation = {
                    makeURL(pages.uaCustomer.order, myURLParamValues())
                }
            ),
            makeURLForReload = {boobsParams->
                makeURL(pages.uaCustomer.order, myURLParamValues() + boobsParams)
            },
            filterValues = enumValuesToStringIDTimesTitleList(CustomerFileFilter.values()),
            defaultFilterValue = CustomerFileFilter.ALL.name,
            filterSelectKey = selects.customerFileFilter,
            vaginalInterface = vaginalInterface
        )

        boobsInterface = boobs.boobsInterface

        return boobs.load()
    }

    private fun myURLParamValues(): List<URLParamValue<*>> {
        val q = TabithaURLQuery
        val paramValues = listOf(
            URLParamValue(q.id, order.id),
            URLParamValue(q.tab, simpleName(tabs.order.files.fqn))
        )
        return paramValues
    }

    private suspend fun onDownload(cloudIconID: String, item: UAOrderFileRTO, updateTitleControls: (FileLipsState) -> Unit) {
        updateTitleControls(FileLipsState(downloadActive = true))
        val blinker = await(effects).blinkOn(byid(cloudIconID), BlinkOpts())

        val testCtx = TestGlobal.orderFileIDToDownloadContext[item.id]
        testCtx?.downloadStartedLock?.resumeTestAndPauseSutFromSut()

        val res = send(UADownloadOrderFileRequest()-{o->
            o.fileID.value = item.id
        })
        exhaustive=when (res) {
            is FormResponse2.Shitty -> {
                imf("onDownload fuckup")
            }
            is FormResponse2.Hunky -> {
                val dataURL = "data:application/octet-stream;base64," + res.meat.base64
                downloadjs(dataURL, res.meat.fileName, "application/octet-stream")
                blinker.unblink()
                updateTitleControls(FileLipsState(downloadActive = false))
                testCtx?.let {
                    it.shit = res.meat
                    it.bitsReceivedLock.resumeTestFromSut()
                }
            }
        }
    }

    private class FileLipsState(val downloadActive: Boolean)

    val vaginalInterface = MelindaVagina<UAOrderFileRTO, UAOrderFileParamsRequest, UAUpdateOrderFileResponse> (
        updateParams = MelindaVaginalUpdateParams(
            updateItemProcedureNameIfNotDefault = "UAUpdateOrderFile",
            getItemFromUpdateItemResponse = {res-> res.updatedFile},
            makeUpdateItemRequest = {item-> UAOrderFileParamsRequest(isAdmin = isAdmin(), isUpdate = true).populateCheckingCompleteness{o->
                o.fileID.value = item.id
                o.file.content = FileField.Content.Unchanged(item.name, item.sizeBytes)
                o.title.value = item.title
                o.details.value = item.details
                populateWithAdminNotes(o, item)
            }}
        ),

        humanItemTypeName = t("TOTE", "файл"),
        makeDeleteItemRequest = {UADeleteOrderFileRequest()},
        getParentEntityID = {order.id},
        sendItemsRequest = {req -> sendUACustomerGetOrderFiles(req)},

        shouldShowFilter = {when (order.state) {
            UAOrderState.CUSTOMER_DRAFT,
            UAOrderState.WAITING_ADMIN_APPROVAL,
            UAOrderState.RETURNED_TO_CUSTOMER_FOR_FIXING -> false
            else -> true
        }},

        makeLipsInterface = {viewRootID, tongue ->
            val cloudIconID = puid()
            makeUsualMelindaLips(
                viewRootID,
                searchString = boobsInterface.getSearchString(),
                icon = {fa.file},
                initialLipsState = FileLipsState(downloadActive = false),
                controlsDisabled = {state-> state.downloadActive},
                smallOverlayIcon = {userKindIcon(tongue.item.seenAsFrom)},
                tinySubtitle = {when (tongue.item.seenAsFrom) {
                    Globus.world.user.kind -> t("Mine", "Мой")
                    UserKind.CUSTOMER -> t("From customer", "От заказчика")
                    UserKind.WRITER -> t("From writer", "От писателя")
                    UserKind.ADMIN -> t("From support", "От саппорта")
                }},
                renderAdditionalControls = {o, state, updateTitleControls ->
                    val cloudClass = when {
                        state.downloadActive -> css.cunt.header.rightIconActive
                        else -> css.cunt.header.rightIcon
                    }
                    o- kic("${fa.cloudDownload} $cloudClass",
                           id = cloudIconID,
                           style = Style(marginTop = "0.45rem"),
                           key = SubscriptKicKey(kics.download, tongue.item.id),
                           onClicka = disableableHandler(state.downloadActive) {onDownload(cloudIconID, tongue.item, updateTitleControls)})
                },
                renderContent = {o->
                    val orderFile = tongue.item
                    renderOrderFileParams(o, orderFile)
                },
                titleLinkURL = null,
                callbacks = tongue.toMakeUsualMelindaLipsCallbacks(),
                getItem = tongue.toItemSupplier()
            )
        }
    )

}

private fun renderOrderFileParams(o: ElementBuilder, orderFile: UAOrderFileRTO) {
    val m = MelindaTools
    o- m.row{o->
        o- m.createdAtCol(3, orderFile.createdAt)
        o- m.updatedAtCol(3, orderFile.updatedAt)
        o- m.col(3, t("File name", "Имя файла")){o->
            o- highlightedShit(orderFile.name, orderFile.nameHighlightRanges, tag = "span")
        }
        o- m.col(3, t("Size", "Размер")){o->
            o- formatFileSizeApprox(Globus.lang, orderFile.sizeBytes)
        }
    }
    o- m.detailsRow(orderFile.details, orderFile.detailsHighlightRanges)
    o- renderAdminNotesIfNeeded(orderFile)
}



















