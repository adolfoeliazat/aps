package aps.front

import aps.*
import aps.front.testutils.*

class TestUACustomer_LongFileList : StepBasedTestScenario() {
    val testShit = TestShit()

    override fun buildSteps() {
        o.setUpOrderFilesTestTemplate(
            testShit,
            setUpOrders = {o.setUpBobulOrder(testShit) {oid-> async {
                await(setUpFiles1(testShit, oid))

                val mnt = moment("2016-12-02 13:17:08")
                for (i in 1..20) {
                    await(ImposeNextRequestTimestampRequest.send(mnt.formatPostgres()))
                    await(send(testShit.bobulToken, CustomerAddUAOrderFileRequest()-{o->
                        o.orderID.value = oid
                        o.file.testFileOnServer = FileField.TestFileOnServer("shitty document $i.rtf", "${testconst.filesRoot}shitty document $i.rtf")
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
            }}},
            assertScreen = {o.todo("setUpOrderFiles1 assertScreen")})
    }
}


