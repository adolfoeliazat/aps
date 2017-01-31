package aps.back

import aps.*
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.persistence.EntityManagerFactory
import org.h2.jdbcx.JdbcDataSource
import javax.sql.DataSource

val springctx = AnnotationConfigApplicationContext(AppConfig::class.java)

@Configuration
@EnableJpaRepositories
@EnableTransactionManagement
open class AppConfig {
    @Bean open fun entityManagerFactory(dataSource: DataSource) = LocalContainerEntityManagerFactoryBean()-{o->
        o.jpaVendorAdapter = HibernateJpaVendorAdapter()-{o->
            o.setGenerateDdl(true)
        }
        o.setPackagesToScan("aps.back")
        o.dataSource = dataSource
    }

    @Bean open fun dataSource() = JdbcDataSource()-{o->
        o.setURL("jdbc:h2:${SharedGlobus.APS_TEMP}/pizdabase;AUTO_SERVER=TRUE")
        o.user = "sa"
    }

    @Bean open fun transactionManager(emf: EntityManagerFactory) = JpaTransactionManager()-{o->
        o.entityManagerFactory = emf
    }

    @Bean open fun warmWelcomer() = WarmWelcomer()
}

class WarmWelcomer {
    var count = 0

    fun sayHello() {
        dwarnStriking("${++count} Hello? No-no-no... Fuck you!")
    }
}


