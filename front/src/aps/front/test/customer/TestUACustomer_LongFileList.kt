package aps.front

import aps.*
import aps.front.testutils.*

class TestUACustomer_LongFileList : StepBasedTestScenario() {
    val testShit = TestShit()

    override fun buildSteps() {
        o.setUpOrderFilesTestTemplate_1(
            testShit,
            setUpUsers = {
                o.setUpBobul(testShit)
            },
            setUpOrders = {o.setUpBobulOrder(
                testShit,
                setUpFiles = {oid-> async {
                    await(setUpFilesByBobul_1(testShit, oid))

                    val mnt = moment("2016-12-02 13:17:08")
                    for (i in 1..20) {
                        await(ImposeNextRequestTimestampRequest.send(mnt.formatPostgres()))
                        await(send(testShit.bobulToken, CustomerAddUAOrderFileRequest()-{o->
                            o.orderID.value = oid
                            o.file.content = FileField.Content.TestFileOnServer("shitty document $i.rtf", "${testconst.filesRoot}shitty document $i.rtf")
                            o.title.value = "Some shitty document $i"
                            o.details.value = dedent("""
                                        This is shitty text $i.
                                        Фрау Грубах, у которой вид был вовсе не виноватый, отперла
                                        двери в  прихожей  перед  всей  компанией,  и  К.  по  привычке
                                        взглянул  на завязки фартука, которые слишком глубоко врезались
                                        в ее мощный стан. На улице К. поглядел на часы  и  решил  взять
                                        такси,  чтобы  не  затягивать еще больше получасовое опоздание.
                                    """)
                        }))
                        await(fuckingRemoteCall.executeSQL("Add file permissions", """
                                    insert into file_user_permissions(file_id, user_id) values(${100002 + i}, 100000)
                                """))
                        mnt.add(23, "minutes").add(14, "seconds")
                    }
                }})
            },
            assertScreen = {o.todo("TestUACustomer_LongFileList assertScreen 1")}
        )

        o.act {jqbody.scrollTop(1900)}
        o.buttonClick("loadMore")
        o.todo("TestUACustomer_LongFileList assertScreen 2")

        o.act {jqbody.scrollTop(4224)}
        o.buttonClick("loadMore")
        o.todo("TestUACustomer_LongFileList assertScreen 3")

        o.act {jqbody.scrollTop(4624)}
        o.todo("TestUACustomer_LongFileList assertScreen 4")
    }
}


