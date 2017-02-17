package aps.front

import aps.*
import aps.front.frontSymbols.numberSign
import into.kommon.*
import kotlin.properties.Delegates.notNull

class TestDownloadContext {
    val downloadStartedLock by notNullNamed(TestLock(virgin = true))
    val bitsReceivedLock by notNullNamed(TestLock(virgin = true))
    var shit by notNull<DownloadFileResponse>()
}

class UACustomerSingleOrderPageFilesTab(val page: UACustomerSingleOrderPage, val world: World, val order: UAOrderRTO) : CustomerSingleUAOrderPageTab {
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
            hasCreateButton = order.state == UAOrderState.CUSTOMER_DRAFT,
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

    private fun myURLParamValues(): List<URLParamValue<String>> {
        val q = UACustomerSingleOrderPage.urlQuery
        val paramValues = listOf(
            URLParamValue(q.id, order.id.toString()),
            URLParamValue(q.tab, simpleName(tabs.order.files.fqn))
        )
        return paramValues
    }

    val vaginalInterface = object:MelindaVaginalInterface
    <
        UAOrderFileRTO,
        CustomerFileFilter,
        UAUpdateOrderFileRequest,
        UAUpdateOrderFileRequest.Response
    > {
        override fun makeLipsInterface(viewRootID: String, tongueInterface: MelindaTongueInterface<UAOrderFileRTO>): MelindaLipsInterface {
            return object:MelindaLipsInterface {
                private val titleRightPlace = Placeholder(renderTitleControls())
                private val cloudIconID = puid()
                private val item get()= tongueInterface.getItem()

                override fun renderItem(): ToReactElementable {
                    return when (world.user.kind) {
                        UserKind.CUSTOMER -> {
                            kdiv(id = viewRootID, className = css.item, opacity = 1.0){o->
                                o- row{o->
                                    o- renderFileTitle()
                                }
                                o- row{o->
                                    o- kdiv(className = "col-md-3"){o->
                                        o- label(t("Created", "Создан"))
                                        o- kdiv{o->
                                            o- formatUnixTime(item.createdAt)
                                        }
                                    }
                                    o- kdiv(className = "col-md-3"){o->
                                        o- label(t("Updated", "Изменен"))
                                        o- kdiv{o->
                                            o- formatUnixTime(item.updatedAt)
                                        }
                                    }
                                    o- kdiv(className = "col-md-3"){o->
                                        o- label(t("File name", "Имя файла"))
                                        o- kdiv{o->
                                            o- highlightedShit(item.name, item.nameHighlightRanges, tag = "span")
                                        }
                                    }
                                    o- kdiv(className = "col-md-3"){o->
                                        o- label(t("Size", "Размер"))
                                        o- kdiv{o->
                                            o- formatFileSizeApprox(Globus.lang, item.sizeBytes)
                                        }
                                    }
                                }
                                o- row{o->
                                    o- kdiv(className = "col-md-12"){o->
                                        o- label(t("Details", "Детали"))
                                        o- kdiv(whiteSpace = "pre-wrap"){o->
                                            o- highlightedShit(item.details, item.detailsHighlightRanges)
                                        }
                                    }
                                }
                            }
                        }

                        UserKind.WRITER -> imf()
                        UserKind.ADMIN -> imf()
                    }
                }

                private fun label(title: String) =
                    klabel(marginBottom = 0) {it - title}

                private fun row(build: (ElementBuilder) -> Unit) =
                    kdiv(className = "row", marginBottom = "0.5em"){o->
                        build(o)
                    }

                private fun renderTitleControls(downloadActive: Boolean = false): ToReactElementable {
                    val cloudClass: String; val trashClass: String; val pencilClass: String
                    val c = css.cunt.header
                    if (downloadActive) {
                        cloudClass = c.rightIconActive
                        trashClass = c.rightIconDisabled
                        pencilClass = c.rightIconDisabled
                    } else {
                        cloudClass = c.rightIcon
                        trashClass = c.rightIcon
                        pencilClass = c.rightIcon
                    }

                    fun ifNotDownloading(f: suspend () -> Unit) = when {
                        downloadActive -> {{TestGlobal.disabledActionHitLock.resumeTestFromSut()}}
                        else -> f
                    }

                    return hor3(style = Style(position = "absolute", right = 0, top = 0, marginRight = "0.5rem", marginTop = "0.1rem")) {o->
                        o- kic("${fa.cloudDownload} $cloudClass", id = cloudIconID, style = Style(marginTop = "0.45rem"), key = SubscriptKicKey(kics.order.file.download, item.id), onClicka = ifNotDownloading {onDownload()})
                        if (item.editable) {
                            o- kic("${fa.trash} $trashClass", style = Style(), key = SubscriptKicKey(kics.order.file.delete, item.id), onClicka = ifNotDownloading {tongueInterface.onDelete()})
                            o- kic("${fa.pencil} $pencilClass", style = Style(), key = SubscriptKicKey(kics.order.file.edit, item.id), onClicka = ifNotDownloading {tongueInterface.onEdit()})
                        }
                    }
                }

                private suspend fun onDownload() {
                    titleRightPlace.setContent(renderTitleControls(downloadActive = true))
                    val blinker = await(effects).blinkOn(byid(cloudIconID), BlinkOpts())

                    val testCtx = TestGlobal.orderFileIDToDownloadContext[item.id]
                    testCtx?.downloadStartedLock?.resumeTestAndPauseSutFromSut()

                    val res = send(UADownloadOrderFileRequest()-{o->
                        o.fileID.value = item.id
                    })
                    titleRightPlace.setContent(renderTitleControls())
                    exhaustive/when (res) {
                        is FormResponse2.Shitty -> {
                            imf("onDownload fuckup")
                        }
                        is FormResponse2.Hunky -> {
                            val dataURL = "data:application/octet-stream;base64," + res.meat.base64
                            downloadjs(dataURL, res.meat.fileName, "application/octet-stream")
                            blinker.unblink()
                            titleRightPlace.setContent(renderTitleControls(downloadActive = false))
                            testCtx?.let {
                                it.shit = res.meat
                                it.bitsReceivedLock.resumeTestFromSut()
                            }
                        }
                    }
                }

                private fun renderFileTitle(): ToReactElementable {
                    val editing = false // TODO:vgrechka @kill
                    return kdiv(className = "col-md-12"){o->
                        o- kdiv(className = if (editing) css.cunt.header.editing else css.cunt.header.viewing){o->
                            o- ki(className = "${if (editing) css.cunt.header.leftIcon.editing else css.cunt.header.leftIcon.viewing} ${fa.file}")
                            o- ki(className = "${if (editing) css.cunt.header.leftOverlayBottomLeftIcon.editing else css.cunt.header.leftOverlayBottomLeftIcon.viewing} " +
                                when (item.seenAsFrom) {
                                    UserKind.CUSTOMER -> fa.user
                                    UserKind.WRITER -> fa.pencil
                                    UserKind.ADMIN -> fa.cog
                                })
                            o- " "
                            o- highlightedShit(item.title, item.titleHighlightRanges, tag = "span")

                            val idColor: Color?; val idBackground: Color?
                            if (boobsInterface.getSearchString().split(Regex("\\s+")).contains(item.id.toString())) {
                                idColor = Color.GRAY_800
                                idBackground = Color.AMBER_200
                            } else {
                                idColor = Color.GRAY_500
                                idBackground = null
                            }
                            o- kspan(marginLeft = "0.5em", fontSize = "75%", color = idColor, backgroundColor = idBackground){o->
                                o- "$numberSign${item.id}"
                            }

                            o- kspan(marginLeft = "0.5em", fontSize = "75%", color = Color.GRAY_500){o->
                                o- when (item.seenAsFrom) {
                                    world.user.kind -> t("Mine", "Мой")
                                    UserKind.CUSTOMER -> t("From customer", "От заказчика")
                                    UserKind.WRITER -> t("From writer", "От писателя")
                                    UserKind.ADMIN -> t("From support", "От саппорта")
                                }
                            }

                            o- titleRightPlace
                        }
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
                UAOrderState.CUSTOMER_DRAFT, UAOrderState.WAITING_ADMIN_APPROVAL -> false
                else -> true
            }
        }

        override suspend fun sendItemsRequest(req: ItemsRequest<CustomerFileFilter>): FormResponse2<ItemsResponse<UAOrderFileRTO>> {
            return sendUACustomerGetOrderFiles(req)
        }
    }
}




















