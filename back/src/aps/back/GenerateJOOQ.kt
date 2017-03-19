/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import com.fasterxml.jackson.databind.JsonNode
import org.apache.tools.ant.Project
import org.apache.tools.ant.taskdefs.Copy
import org.apache.tools.ant.taskdefs.Delete
import org.apache.tools.ant.taskdefs.Mkdir
import org.apache.tools.ant.types.FileSet
import org.jooq.util.DefaultGeneratorStrategy
import org.jooq.util.Definition
import org.jooq.util.GenerationTool
import org.jooq.util.GeneratorStrategy
import org.jooq.util.jaxb.*
import org.jooq.util.jaxb.Generate
import org.jooq.util.jaxb.Target
import java.io.File

fun main(args: Array<String>) {
//    BackGlobus.tracingEnabled = false
//    DB.apsTestOnTestServer.recreateSchema()
//
//    Mkdir()-{o->
//        o.dir = File("${const.file.APS_HOME}/back/bak")
//        o.execute()
//    }
//
//    Delete()-{o->
//        o.setDir(File("${const.file.APS_HOME}/back/bak/src-generated"))
//        o.execute()
//    }
//
//    Copy()-{o->
//        o.project = Project() // Necessary dummy
//        o.setTodir(File("${const.file.APS_HOME}/back/bak"))
//        o.addFileset(FileSet()-{o->
//            o.dir = File("${const.file.APS_HOME}/back")
//            o.setIncludes("src-generated/**")
//        })
//        o.execute()
//    }
//
//    with(GenerationTool()) {
//        setDataSource(DB.apsTestOnTestServer.ds)
//        run(Configuration()-{o->
//            o.generator = Generator()-{o->
//                o.strategy = Strategy()-{o->
//                    class Strategy : DefaultGeneratorStrategy() {
//                        override fun getJavaClassName(definition: Definition?, mode: GeneratorStrategy.Mode?): String {
//                            return "JQ" + super.getJavaClassName(definition, mode)
//                        }
//                    }
//                    o.name = Strategy ::class.java.name
//                }
//                o.generate = Generate()-{o->
//                    o.isPojos = true
//                }
//                o.database = Database()-{o->
//                    o.inputSchema = "public"
//                    o.includes = ".*"
//                    o.customTypes = listOf(
//                        CustomType()-{o->
//                            o.name = PostgresJSONBJacksonJsonNodeBinding::class.qualifiedName
//                            o.type = JsonNode::class.qualifiedName
//                            o.binding = PostgresJSONBJacksonJsonNodeBinding::class.qualifiedName
//                        }
//                    )
//                    o.forcedTypes = listOf(
//                        ForcedType()-{o->
//                            o.name = PostgresJSONBJacksonJsonNodeBinding::class.qualifiedName
//                            o.types = "jsonb"
//                        }
//                    )
//                }
//                o.target = Target()-{o->
//                    o.packageName = "aps.back.generated.jooq"
//                    o.directory = "${const.file.APS_HOME}/back/src-generated"
//                }
//            }
//        })
//    }
}


