package aps.front

import aps.*
import aps.front.testutils.*

class TestUACustomer_DownloadForbiddenFile : StepBasedTestScenario() {
    val shit = TestShit()

    override fun buildSteps() {
        o.setUpOrderAndFiles1(shit)
        o.acta {fuckingRemoteCall.executeSQL("Delete permission for crazy boobs file", """
            delete from file_user_permissions
            where
                file_id = (select id from files where name = 'crazy monster boobs.rtf') and
                user_id = (select id from users where first_name = 'Иво')
        """)}
        o.willWaitForModal()
        o.expectPieceOfShitDownload(PieceOfShitDownload(100001, "crazy monster boobs.rtf", forbidden = true, sha1 = "pizda2")) {
            o.kicClick("download-1")
        }
        o.waitForModalShown()
        o.assertScreenHTML("Forbidden modal", "eb1ec474-315e-4184-829e-91882afcdf87")
        o.clickModalOKAndWaitTillHidden()
        o.assertScreenHTML("Modal closed", "fb05d2d8-18f6-48a1-9dfc-bd18e212c498")
    }
}


