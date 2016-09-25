/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.build
import org.jooq.util.GenerationTool
import org.jooq.util.jaxb.*
import org.jooq.util.jaxb.Target

fun main(args: Array<String>) {
    recreateTestDB()

    with (GenerationTool()) {
        setDataSource(DS.apsTestOnTestServer)
        run(build(Configuration()) {
            generator = build(Generator()) {
                generate = build(Generate()) {
                    isPojos = true
                }
                database = build(Database()) {
                    inputSchema = "public"
                    includes = ".*"
                }
                target = build(Target()) {
                    packageName = "aps.back.generated.jooq"
                    directory = "src-generated"
                }
            }
        })
    }
}

