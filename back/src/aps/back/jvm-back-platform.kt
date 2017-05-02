package aps.back

import aps.*
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.zaxxer.hikari.HikariDataSource
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.id.IdentityGenerator
import org.mindrot.jbcrypt.BCrypt
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import java.lang.reflect.Method
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.persistence.*
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement
import kotlin.reflect.KClass

typealias XSerializable = java.io.Serializable
typealias XTransient = Transient
typealias XCrudRepository<T, ID> = org.springframework.data.repository.CrudRepository<T, ID>
typealias XGenericGenerator = org.hibernate.annotations.GenericGenerator
typealias XTimestamp = java.sql.Timestamp
typealias XFetchType = FetchType
typealias XCascadeType = CascadeType
typealias XIndex = Index
typealias XEmbeddable = Embeddable
typealias XPreUpdate = PreUpdate
typealias XGenerationType = GenerationType
typealias XGeneratedValue = GeneratedValue
typealias XId = Id
typealias XMappedSuperclass = MappedSuperclass
typealias XEntity = Entity
typealias XTable = Table
typealias XEmbedded = Embedded
typealias XOneToMany = OneToMany
typealias XColumn = Column
typealias XEnumerated = Enumerated
typealias XEnumType = EnumType
typealias XManyToOne = ManyToOne
typealias XJsonIgnoreProperties = JsonIgnoreProperties
typealias XLogger = org.slf4j.Logger
typealias XDate = java.util.Date
typealias XXmlRootElement = XmlRootElement
typealias XXmlAccessorType = XmlAccessorType
typealias XXmlAccessType = XmlAccessType
typealias XXmlElement = XmlElement
typealias XCollections = Collections
typealias XBeanDefinition = org.springframework.beans.factory.config.BeanDefinition
typealias XScope = org.springframework.context.annotation.Scope
typealias XComponent =  org.springframework.stereotype.Component
typealias XDataSource = com.zaxxer.hikari.HikariDataSource
typealias XServletException = javax.servlet.ServletException
typealias XHttpServletRequest = javax.servlet.http.HttpServletRequest
typealias XHttpServletResponse = javax.servlet.http.HttpServletResponse
typealias XThreadLocal<T> = ThreadLocal<T>


private val paramClassToServeMethod = ConcurrentHashMap<Class<*>, Method>()

val backPlatform = object : XBackPlatform {

    override fun makeDataSource(db: DB.Database): XDataSource {
        return HikariDataSource().applet {o->
            o.dataSourceClassName = when (db.engine) {
                DB.DatabaseEngine.POSTGRESQL -> "org.postgresql.ds.PGSimpleDataSource"
                DB.DatabaseEngine.MARIADB -> "org.mariadb.jdbc.MariaDbDataSource"
            }
            o.dataSourceProperties.let {o->
                o.put("serverName", db.host)
                o.put("databaseName", db.name)
                o.put("portNumber", db.port)
                o.put("user", db.user)
                db.password?.let {o.put("password", it)}
            }
            o.connectionInitSql = when (db.engine) {
                DB.DatabaseEngine.POSTGRESQL -> "set time zone 'UTC';"
                DB.DatabaseEngine.MARIADB -> "set time_zone = '+0:00';"
            }
        }
    }

    override val userRepo get() = springctx.getBean(UserRepository::class.java)!!
    override val userTokenRepo get() = springctx.getBean(UserTokenRepository::class.java)!!
    override val userParamsHistoryItemRepo get() = springctx.getBean(UserParamsHistoryItemRepository::class.java)!!
    override val uaOrderRepo get() = springctx.getBean(UAOrderRepository::class.java)!!
    override val uaOrderFileRepo get() = springctx.getBean(UAOrderFileRepository::class.java)!!
    override val uaDocumentCategoryRepo get() = springctx.getBean(UADocumentCategoryRepository::class.java)!!
    override val userTimesDocumentCategoryRepo get() = springctx.getBean(UserTimesDocumentCategoryRepository::class.java)!!
    override val userParamsHistoryItemTimesDocumentCategoryRepo get() = springctx.getBean(UserParamsHistoryItemTimesDocumentCategoryRepository::class.java)!!
    override val bidRepo get() = springctx.getBean(BidRepository::class.java)!!

    override val requestGlobus: RequestGlobusType
        get() = requestGlobusThreadLocal.get() ?: bitch("RequestGlobus? What else?")

    override fun isRequestThread() =
        requestGlobusThreadLocal.get() != null


    override fun getServeObjectRequestFunction(params: Any): (Any) -> Any {
        val method = paramClassToServeMethod.computeIfAbsent(params::class.java) {
            for (cname in listOf("Generated_backKt", "Rp_orderKt", "Rp_userKt", "Rp_historyKt", "Rp_testKt", "Rp_test_2Kt")) {
                val clazz = Class.forName("aps.back.$cname")
                try {
                    return@computeIfAbsent clazz.getDeclaredMethod("serve", params::class.java)
                } catch (e: Throwable) {
                }
            }
            wtf("p::class = ${params::class}    a322c2b4-25af-45e1-a7ae-a5484a941ec3")
        }

        val serveFunction = {p: Any -> method.invoke(null, p)}
        return serveFunction
    }

    override fun captureStackTrace() = Exception().stackTrace.map(::JVM_XStackTraceElement).toTypedArray()

    override val log = LoggerFactory.getLogger("")
    override val debugLog = LoggerFactory.getLogger("::::: DEBUG :::::")

    override val hackyObjectMapper = object:XHackyObjectMapper {
        override fun <T : Any> readValue(content: String, valueType: KClass<T>): T {
            return _hackyObjectMapper.readValue(content, valueType.java)
        }
    }

    override val shittyObjectMapper = object:XShittyObjectMapper {
        override fun writeValueAsString(shit: Any): String {
            return _shittyObjectMapper.writeValueAsString(shit)
        }
    }

    override fun getResourceAsText(path: String) = this::class.java.classLoader.getResource(path).readText()

    override fun highlightRanges(text: String, searchWords: List<String>): List<IntRangeRTO> {
        val lang = Language.UA
        val analyzer = when (lang) {
            Language.UA -> russianAnalyzer
            else -> imf("Support analyzing for $lang")
        }
        return when {
            searchWords.isEmpty() -> listOf()
            else -> luceneHighlightRanges(text, searchWords, analyzer)
        }
    }

    override fun recreateDBSchema() {
        springctx = AnnotationConfigApplicationContext(AppConfig::class.java)
    }

    override fun hashPassword(clearText: String): String = BCrypt.hashpw(clearText, BCrypt.gensalt())

    override fun dbTransaction(block: (ShitToDoInTransaction) -> Unit) {
        // TODO:vgrechka Check if necessary columns/indexes are already there
        val emf = springctx.getBean(EntityManagerFactory::class.java)
        val em = emf.createEntityManager()
        em.transaction.begin()
        try {
            block(object:ShitToDoInTransaction {
                override fun executeUpdate(sql: String): Int {
                    return em.createNativeQuery(sql).executeUpdate()
                }
            })
        } finally {
            em.transaction.commit()
            em.close()
        }
    }

    override fun makeServant(procedureName: String): BitchyProcedure {
        val procNameCaps = procedureName.capitalize()
        val server = springctx.getBean("serve" + procNameCaps, BitchyProcedure::class.java)
        return server
    }

    override fun requestTransaction(pathInfo: String, block: () -> Unit) {
        val useTx = !pathInfo.contains("RecreateTestDatabaseSchema") // XXX
        if (useTx) {
            TransactionTemplate(springctx.getBean(PlatformTransactionManager::class.java)).execute {
                block()
            }
        } else {
            block()
        }
    }


}


class JVM_XStackTraceElement(val stackTraceElement: StackTraceElement) : XStackTraceElement {
    override fun toString(): String {
        return stackTraceElement.toString()
    }
}

@Suppress("Unused")
class IdentityIfNotSetGenerator : IdentityGenerator() {
    private val logic = IdentityIfNotSetGeneratorLogic()

    override fun generate(s: SharedSessionContractImplementor?, obj: Any?): XSerializable {
        val id = logic.generate(obj)
        return when {
            id != null -> id
            else -> super.generate(s, obj)
        }
    }
}

















