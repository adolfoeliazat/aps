package aps.front

import aps.*
import aps.front.testutils.*

class TestUACustomer_DownloadForbiddenFile : StepBasedTestScenario() {
    val shit = TestShit()

    override fun buildSteps() {
        o.orderFiles1(shit)
        o.acta {fuckingRemoteCall.executeSQL("Delete permission for crazy boobs file", """
            update file_user_permissions
            set deleted = true
            where
                file_id = (select id from files where name = 'crazy monster boobs.rtf') and
                user_id = (select id from users where first_name = 'Иво')
        """)}
        o.willWaitForModal()
        o.expectPieceOfShitDownload(PieceOfShitDownload(100001, "crazy monster boobs.rtf", forbidden = true)) {
            o.kicClick("download-1")
        }
        o.waitForModal()
        o.assertScreenHTML("Forbidden modal", "eb1ec474-315e-4184-829e-91882afcdf87")
    }
}


