package aps.back.spike

import aps.*
import aps.back.*

fun main(args: Array<String>) {
    val con = DB.testLocalMariaDB.ds.connection
    clog(con)
    val rs = con.prepareStatement("select 2 + 3").executeQuery()
    rs.next()
    clog(rs.getInt(1))
    clog("Very fucking cool")
}

