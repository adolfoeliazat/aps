package aps.front

import aps.*
import aps.front.testutils.*
import into.kommon.*
import into.mochka.assertEquals

class TestUACustomer_Order_Files_Search : StepBasedTestScenario() {
    val shit = TestShit()

    override fun buildSteps() {
        o.setUpOrderFilesTestTemplate_1(
            shit,
            setUpUsers = {
                o.setUpBobul(shit)
                o.setUpFedor(shit)
            },
            setUpOrders = {
                o.setUpBobulOrder(
                    shit,
                    setUpFiles = {oid-> async {
                        await(setUpFilesByBobul_1(shit, oid))
                        await(setUpFilesByFedor_1(shit, oid))
                        await(setUpFilesByBobul_2(shit, oid))
                    }})
            },
            assertScreen = {o.assertScreenHTML_todo("1", "0f77828b-fe22-4606-8f97-0efecc6fefde")}
        )

        o.inputSetValue("search", "мышкин")
        o.inputPressEnter("search")
        o.assertScreenHTML("2", "561577ab-ad4b-4bb5-bf1d-f50e9eac2c68")

        o.inputSetValue("search", "мышкин настасья")
        o.inputPressEnter("search")
        o.assertScreenHTML("3", "df1e1441-9b97-40b9-8e87-ddcad49f5c06")

        o.inputSetValue("search", "искренний")
        o.inputPressEnter("search")
        o.assertScreenHTML("4", "bf5e705e-c181-477c-9c8e-d16866756f65")

        o.selectSetValue("filter", CustomerFileFilter.values(), CustomerFileFilter.FROM_WRITER)
        o.assertScreenHTML("5", "c91c05d1-aedb-44f3-a00a-7875de20642e")

        o.selectSetValue("filter", CustomerFileFilter.values(), CustomerFileFilter.FROM_ME)
        o.assertScreenHTML("6", "e6164186-36f8-465b-b7ff-81bc7739946a")

        o.selectSetValue("filter", CustomerFileFilter.values(), CustomerFileFilter.ALL)
        o.assertScreenHTML("7", "0ee8b2c5-9252-477a-a8f0-8e0ab6bd7e0d")

        o.inputSetValue("search", "жопа")
        o.inputPressEnter("search")
        o.assertScreenHTML("8", "e9e102ed-a6fd-4bd4-a4a9-98a9391e5920")

        o.inputSetValue("search", "одно сообщить")
        o.inputPressEnter("search")
        o.assertScreenHTML("9", "4c756d27-b32e-456c-85bd-72c18ed5c419")

        o.inputSetValue("search", "trial")
        o.inputPressEnter("search")
        o.assertScreenHTML("10", "fde730d2-10f9-4ba0-8abf-d2a5b275a7a8")

        o.inputSetValue("search", "boobs")
        o.inputPressEnter("search")
        o.assertScreenHTML("11", "7871923d-d3b4-4ed1-9c61-5dc2d29a269d")

        o.inputSetValue("search", "100004")
        o.inputPressEnter("search")
        o.assertScreenHTML("12", "5db37d2c-367f-4913-b9c1-7075ebe87bbc")

        o.inputSetValue("search", "100004 100000")
        o.inputPressEnter("search")
        o.assertScreenHTML("13", "b47d716a-9b85-4cc9-9ade-274c6e13fc32")
    }
}






