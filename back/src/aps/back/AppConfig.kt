package aps.back

import aps.*
import org.h2.jdbcx.JdbcDataSource
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource

val springctx = AnnotationConfigApplicationContext(AppConfig::class.java)

@Configuration
@EnableJpaRepositories
@EnableTransactionManagement
@ComponentScan(basePackages = arrayOf("aps.back"))
open class AppConfig {
    @Bean open fun entityManagerFactory(dataSource: DataSource) = LocalContainerEntityManagerFactoryBean()-{o->
        o.jpaVendorAdapter = HibernateJpaVendorAdapter()-{o->
            o.setShowSql(true)
            o.setGenerateDdl(true)
        }
        o.setPackagesToScan("aps.back")
        o.dataSource = dataSource
    }

    @Bean open fun dataSource() = JdbcDataSource()-{o->
        o.setURL("jdbc:h2:mem:pizdabase;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false") // XXX This DB_* shit is actually needed
//        o.setURL("jdbc:h2:mem:pizdabase")
//        o.setURL("jdbc:h2:${SharedGlobus.APS_TEMP}/pizdabase;AUTO_SERVER=TRUE")
        o.user = "sa"
    }

//    @Bean open fun dataSource(): EmbeddedDatabase? {
//        val builder = EmbeddedDatabaseBuilder()
//        return builder.setType(EmbeddedDatabaseType.H2).build()
//    }

    @Bean open fun transactionManager(emf: EntityManagerFactory) = JpaTransactionManager()-{o->
        o.entityManagerFactory = emf
    }

    @Bean open fun warmWelcomer(sayer: ShitSayer) = WarmWelcomer(sayer)
}

interface Welcomer {
    fun sayHello()
}

class WarmWelcomer(val sayer: ShitSayer) : Welcomer {
    var count = 0

    override fun sayHello() {
        dwarnStriking("${++count} Hello? No-no-no... Fuck you!")
    }
}

@Component
class BrutalWelcomer(val sayer: ShitSayer) : Welcomer {
    var count = 0

    override fun sayHello() {
        sayer.sayShit("${++count} Hello, sweetheart. Wanna see some kittens?")
    }
}

@Component
class ShitSayer {
    fun sayShit(shit: String) {
        dwarnStriking(shit)
    }
}


