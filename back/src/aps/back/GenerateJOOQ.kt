/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import org.jooq.util.GenerationTool
import org.jooq.util.jaxb.*
import org.jooq.util.jaxb.Generate
import org.jooq.util.jaxb.Target

fun main(args: Array<String>) {
    DB.apsTestOnTestServer.recreate()

    with(GenerationTool()) {
        setDataSource(DB.apsTestOnTestServer.ds)
        run(Configuration().apply {
            generator = Generator().apply {
                generate = Generate().apply {
                    isPojos = true
                }
                database = Database().apply {
                    inputSchema = "public"
                    includes = ".*"
                }
                target = Target().apply {
                    packageName = "aps.back.generated.jooq"
                    directory = "src-generated"
                }
            }
        })
    }
}

