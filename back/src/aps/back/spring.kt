package aps.back

import aps.*
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.h2.jdbcx.JdbcDataSource
import org.hibernate.annotations.common.reflection.XProperty
import org.hibernate.cfg.Environment
import org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE
import org.springframework.context.annotation.*
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource
import kotlin.system.exitProcess

@Volatile var springctx = AnnotationConfigApplicationContext(AppConfig::class.java)

@Component @Scope(SCOPE_PROTOTYPE)
annotation class Servant

@Configuration
@EnableJpaRepositories
@EnableTransactionManagement
@ComponentScan(basePackages = arrayOf("aps.back"))
open class AppConfig {
    @Bean open fun entityManagerFactory(dataSource: DataSource) = LocalContainerEntityManagerFactoryBean()-{o->
        o.jpaVendorAdapter = HibernateJpaVendorAdapter()-{o->
            o.setShowSql(true)
        }
        o.jpaPropertyMap.put(Environment.HBM2DDL_AUTO, "create-drop")
        o.setPackagesToScan("aps.back")
        o.dataSource = dataSource
    }

    @Bean open fun dataSource() = JdbcDataSource()-{o->
        o.setURL("jdbc:h2:mem:pizdabase;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false") // XXX This DB_* shit is actually needed
        o.user = "sa"
    }

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
        sayer.sayShit("${++count} Hello? No-no-no... Fuck you!")
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


