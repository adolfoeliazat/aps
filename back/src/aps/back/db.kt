/*
 * APS
 *
 * (C) Copyright 2015-2017 Vladimir Grechka
 */

package aps.back

import aps.*
//import aps.RedisLogMessage.SQL.Stage.*
import aps.back.generated.jooq.Tables
import aps.back.generated.jooq.tables.records.JQUserRolesRecord
import aps.back.generated.jooq.tables.records.JQUserTokensRecord
import aps.back.generated.jooq.tables.records.JQUsersRecord
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.NullNode
import com.zaxxer.hikari.HikariDataSource
import org.hibernate.boot.model.naming.Identifier
import org.hibernate.boot.model.naming.ImplicitJoinColumnNameSource
import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl
import org.hibernate.boot.model.source.spi.AttributePath
import org.jooq.*
import org.jooq.SelectField
import org.jooq.conf.ParamType
import org.jooq.conf.Settings
import org.jooq.exception.DataAccessException
import org.jooq.exception.DataTypeException
import org.jooq.impl.*
import org.jooq.tools.Convert.convert
import org.springframework.data.repository.CrudRepository
import java.sql.SQLFeatureNotSupportedException
import java.sql.Timestamp
import java.sql.Types
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.*
import java.util.concurrent.CompletionStage
import java.util.concurrent.Executor
import javax.annotation.Generated
import javax.persistence.EntityManagerFactory
import kotlin.system.exitProcess

val PG_LOCAL_DATE_TIME = DateTimeFormatterBuilder()
    .parseCaseInsensitive()
    .append(DateTimeFormatter.ISO_LOCAL_DATE)
    .appendLiteral(' ')
    .append(DateTimeFormatter.ISO_LOCAL_TIME)
    .toFormatter()

fun stringToStamp(s: String): Timestamp = Timestamp.valueOf(LocalDateTime.parse(s, PG_LOCAL_DATE_TIME))


/**
 * Based on http://stackoverflow.com/questions/27044702/how-to-insert-a-updatable-record-with-json-column-in-postgresql-using-jooq
 */
class PostgresJSONBJacksonJsonNodeBinding : Binding<Any?, JsonNode> {

    override fun converter(): Converter<Any?, JsonNode> {
        return PostgresJSONBJacksonJsonNodeConverter()
    }

    override fun sql(ctx: BindingSQLContext<JsonNode>) {
        // This ::jsonb cast is explicitly needed by PostgreSQL:
        ctx.render().visit(DSL.`val`(ctx.convert(converter()).value())).sql("::jsonb")
    }

    override fun register(ctx: BindingRegisterContext<JsonNode>) {
        ctx.statement().registerOutParameter(ctx.index(), Types.VARCHAR)
    }

    override fun set(ctx: BindingSetStatementContext<JsonNode>) {
        ctx.statement().setString(
            ctx.index(),
            convert(ctx.convert(converter()).value(), String::class.java))
    }

    override fun get(ctx: BindingGetResultSetContext<JsonNode>) {
        ctx.convert(converter()).value(ctx.resultSet().getString(ctx.index()))
    }

    override fun get(ctx: BindingGetStatementContext<JsonNode>) {
        ctx.convert(converter()).value(ctx.statement().getString(ctx.index()))
    }

    // The below methods aren't needed in PostgreSQL:

    override fun set(ctx: BindingSetSQLOutputContext<JsonNode>) {
        throw SQLFeatureNotSupportedException()
    }

    override fun get(ctx: BindingGetSQLInputContext<JsonNode>) {
        throw SQLFeatureNotSupportedException()
    }
}

class PostgresJSONBJacksonJsonNodeConverter : Converter<Any?, JsonNode> {
    override fun from(t: Any?): JsonNode {
        return if (t == null) NullNode.instance
        else ObjectMapper().readTree(t.toString())
    }

    override fun to(u: JsonNode?): Any? {
        return if (u == null || u == NullNode.instance) null
        else ObjectMapper().writeValueAsString(u)
    }

    override fun fromType(): Class<Any?> {
        @Suppress("UNCHECKED_CAST")
        return Object::class.java as Class<Any?>
    }

    override fun toType(): Class<JsonNode> {
        return JsonNode::class.java
    }
}



class APSHibernateNamingStrategy : ImplicitNamingStrategyJpaCompliantImpl() {
    override fun transformAttributePath(attributePath: AttributePath): String {
        return attributePath.fullPath.replace(".", "_")
    }

    override fun determineJoinColumnName(source: ImplicitJoinColumnNameSource): Identifier {
        val name: String
        if (source.nature == ImplicitJoinColumnNameSource.Nature.ELEMENT_COLLECTION || source.attributePath == null ) {
            name = transformEntityName(source.entityNaming) + "__" + source.referencedColumnName.text
        } else {
            name = transformAttributePath(source.attributePath) + "__" + source.referencedColumnName.text
        }
        return toIdentifier(name, source.buildingContext)
    }
}



















