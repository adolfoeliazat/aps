package aps.front

import aps.*
import into.kommon.*
import kotlin.properties.Delegates.notNull

class TestDownloadContext {
    val downloadStartedLock by notNullNamed(TestLock(virgin = true))
    val bitsReceivedLock by notNullNamed(TestLock(virgin = true))
    var shit by notNull<DownloadFileResponse>()
}

class UACustomerSingleOrderPageFilesTab(val page: UASingleOrderPage, val world: World, val order: UAOrderRTO) : CustomerSingleUAOrderPageTab {
    var boobsInterface by notNullOnce<MelindaBoobsInterface>()

    override val tabSpec = object:TabSpec {
        override val key = tabs.order.files
        override val title = t("Files", "Файлы")
        override val content get()= boobsInterface.mainContent
        override val stripContent get()= boobsInterface.controlsContent
    }

    override suspend fun load(): FormResponse2.Shitty<*>? {
        val boobs = MelindaBoobs<
            UAOrderFileRTO, CustomerFileFilter,
            UACreateOrderFileRequest, UACreateOrderFileRequest.Response,
            UAUpdateOrderFileRequest, UAUpdateOrderFileRequest.Response
        >(
            hasCreateButton = order.state in setOf(UAOrderState.CUSTOMER_DRAFT, UAOrderState.RETURNED_TO_CUSTOMER_FOR_FIXING),
            createModalTitle = t("TOTE", "Новый файл"),
            makeCreateRequest = {UACreateOrderFileRequest()-{o->
                o.orderID.value = order.id
            }},
            makeURLAfterCreation = {
                makeURL(pages.uaCustomer.order, myURLParamValues())
            },
            makeURLForReload = {boobsParams->
                makeURL(pages.uaCustomer.order, myURLParamValues() + boobsParams)
            },
            filterValues = CustomerFileFilter.values(),
            defaultFilterValue = CustomerFileFilter.ALL,
            filterSelectKey = selects.customerFileFilter,
            vaginalInterface = vaginalInterface
        )

        boobsInterface = boobs.boobsInterface

        return boobs.load()
    }

    private fun myURLParamValues(): List<URLParamValue<*>> {
        val q = UASingleOrderPage.urlQuery
        val paramValues = listOf(
            URLParamValue(q.id, order.id),
            URLParamValue(q.tab, simpleName(tabs.order.files.fqn))
        )
        return paramValues
    }

    val vaginalInterface = object:MelindaVaginalInterface<UAOrderFileRTO, CustomerFileFilter, UAUpdateOrderFileRequest, UAUpdateOrderFileRequest.Response> {
        private inner class FileLipsState(val downloadActive: Boolean)

        override fun makeLipsInterface(viewRootID: String, tongue: MelindaTongueInterface<UAOrderFileRTO>): MelindaLipsInterface {
            // if (world.user.kind != UserKind.CUSTOMER) imf("order vaginalInterface for ${world.user.kind}")

            val cloudIconID = puid()
            return makeUsualMelindaLips(
                tongue, viewRootID, boobsInterface,
                icon = fa.file,
                initialState = FileLipsState(downloadActive = false),
                controlsDisabled = {state-> state.downloadActive},
                smallOverlayIcon = {when (tongue.getItem().seenAsFrom) {
                    UserKind.CUSTOMER -> fa.user
                    UserKind.WRITER -> fa.pencil
                    UserKind.ADMIN -> fa.cog
                }},
                tinySubtitle = {when (tongue.getItem().seenAsFrom) {
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
                            key = SubscriptKicKey(kics.order.file.download, tongue.getItem().id),
                            onClicka = disableableHandler(state.downloadActive) {onDownload(cloudIconID, tongue.getItem(), updateTitleControls)})
                },
                renderContent = {o->
                    val m = MelindaTools
                    val item = tongue.getItem()
                    o- m.row{o->
                        o- m.createdAtCol(3, item.createdAt)
                        o- m.updatedAtCol(3, item.updatedAt)
                        o- m.col(3, t("File name", "Имя файла")){o->
                            o- highlightedShit(item.name, item.nameHighlightRanges, tag = "span")
                        }
                        o- m.col(3, t("Size", "Размер")){o->
                            o- formatFileSizeApprox(Globus.lang, item.sizeBytes)
                        }
                    }
                    o- m.detailsRow(item.details, item.detailsHighlightRanges)
                },
                titleLinkURL = null,
                hasEditControl = {true},
                hasDeleteControl = {true}
            )
        }

        private suspend fun onDownload(cloudIconID: String, item: UAOrderFileRTO, updateTitleControls: (FileLipsState) -> Unit) {
            updateTitleControls(FileLipsState(downloadActive = true))
            val blinker = await(effects).blinkOn(byid(cloudIconID), BlinkOpts())

            val testCtx = TestGlobal.orderFileIDToDownloadContext[item.id]
            testCtx?.downloadStartedLock?.resumeTestAndPauseSutFromSut()

            val res = send(UADownloadOrderFileRequest()-{o->
                o.fileID.value = item.id
            })
            exhaustive / when (res) {
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

        override val humanItemTypeName = t("TOTE", "файл")

        override fun getItemFromUpdateItemResponse(res: UAUpdateOrderFileRequest.Response) = res.updatedFile

        override fun makeUpdateItemRequest(item: UAOrderFileRTO): UAUpdateOrderFileRequest {
            return UAUpdateOrderFileRequest()-{o->
                o.fileID.value = item.id
                o.file.content = FileField.Content.Unchanged(item.name, item.sizeBytes)
                o.fields1-{o->
                    o.title.value = item.title
                    o.details.value = item.details
                }
            }
        }

        override fun makeDeleteItemRequest(): DeleteRequest {
            return UADeleteOrderFileRequest()
        }

        override fun getParentEntityID(): Long {
            return order.id
        }

        override fun shouldShowFilter(): Boolean {
            return when (order.state) {
                UAOrderState.CUSTOMER_DRAFT,
                UAOrderState.WAITING_ADMIN_APPROVAL,
                UAOrderState.RETURNED_TO_CUSTOMER_FOR_FIXING -> false

                else -> true
            }
        }

        override suspend fun sendItemsRequest(req: ItemsRequest<CustomerFileFilter>): FormResponse2<ItemsResponse<UAOrderFileRTO>> {
            return sendUACustomerGetOrderFiles(req)
        }
    }
}




















