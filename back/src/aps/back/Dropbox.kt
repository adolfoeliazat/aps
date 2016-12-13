package aps.back

import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.v2.DbxClientV2

val dropboxClient by lazy {
    eprintln("Connecting to Dropbox")
    val config = DbxRequestConfig("apsback")
    val client = DbxClientV2(config, System.getenv("APS_DROPBOX_TOKEN"))
    val account = client.users().currentAccount
    eprintln("Dropbox account: " + account.name.displayName)
    client
}

