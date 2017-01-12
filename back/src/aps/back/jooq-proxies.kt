package aps.back

import aps.*
import aps.RedisLogMessage.SQL.Stage.*
import into.kommon.*
import org.jooq.*
import org.jooq.SelectField
import org.jooq.conf.ParamType
import org.jooq.exception.*
import java.sql.ResultSet
import java.util.*
import java.util.concurrent.CompletionStage
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.function.Consumer
import java.util.stream.Stream

class DSLContextProxy(val activityParams: ActivityParams, val q: DSLContext) {
    fun <R : Record> insertInto(into: Table<R>, vararg fields: Field<*>): InsertValuesStepN<R> {
        return q.insertInto(into, *fields)
    }

    fun fetch(sql: String, vararg bindings: Any?): Result<Record> =
        executeTracing(activityParams) {q.fetch(sql, *bindings)}

    fun selectCount(): SelectSelectStep<Record1<Int>> {
        return q.selectCount()
    }

    fun <R : Record> update(table: Table<R>): UpdateSetFirstStep<R> {
        val res: UpdateSetFirstStep<R> = q.update(table)
        return UpdateSetFirstStepProxy(activityParams, res)
    }

    fun select(vararg fields: SelectField<*>): SelectSelectStep<Record> {
        return q.select(*fields)
    }

    fun fetch(sql: String): Result<Record> {
        return q.fetch(sql)
    }

    fun <R : Record> insertInto(into: Table<R>): InsertSetStep<R> {
        val res = q.insertInto(into)
        return InsertSetStepProxy(activityParams, res)
    }

    fun execute(sql: String): Int =
        executeTracing(activityParams) {q.execute(sql)}

    fun <R : Record> selectFrom(table: Table<R>): SelectWhereStep<R> {
        return q.selectFrom(table)
    }
}


class InsertSetStepProxy<R : Record>(val activityParams: ActivityParams, val wrappee: InsertSetStep<R>) : InsertSetStep<R> {

    override fun columns(fields: Array<Field<*>>): InsertValuesStepN<R> {
        return wrappee.columns(*fields)
    }

    override fun columns(fields: Collection<Field<*>>): InsertValuesStepN<R> {
        return wrappee.columns(fields)
    }

    override fun <T1> columns(field1: Field<T1>): InsertValuesStep1<R, T1> {
        return wrappee.columns(field1)
    }

    override fun <T1, T2> columns(field1: Field<T1>, field2: Field<T2>): InsertValuesStep2<R, T1, T2> {
        return wrappee.columns(field1, field2)
    }

    override fun <T1, T2, T3> columns(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>): InsertValuesStep3<R, T1, T2, T3> {
        return wrappee.columns(field1, field2, field3)
    }

    override fun <T1, T2, T3, T4> columns(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>): InsertValuesStep4<R, T1, T2, T3, T4> {
        return wrappee.columns(field1, field2, field3, field4)
    }

    override fun <T1, T2, T3, T4, T5> columns(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>, field5: Field<T5>): InsertValuesStep5<R, T1, T2, T3, T4, T5> {
        return wrappee.columns(field1, field2, field3, field4, field5)
    }

    override fun <T1, T2, T3, T4, T5, T6> columns(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>, field5: Field<T5>, field6: Field<T6>): InsertValuesStep6<R, T1, T2, T3, T4, T5, T6> {
        return wrappee.columns(field1, field2, field3, field4, field5, field6)
    }

    override fun <T1, T2, T3, T4, T5, T6, T7> columns(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>, field5: Field<T5>, field6: Field<T6>, field7: Field<T7>): InsertValuesStep7<R, T1, T2, T3, T4, T5, T6, T7> {
        return wrappee.columns(field1, field2, field3, field4, field5, field6, field7)
    }

    override fun <T1, T2, T3, T4, T5, T6, T7, T8> columns(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>, field5: Field<T5>, field6: Field<T6>, field7: Field<T7>, field8: Field<T8>): InsertValuesStep8<R, T1, T2, T3, T4, T5, T6, T7, T8> {
        return wrappee.columns(field1, field2, field3, field4, field5, field6, field7, field8)
    }

    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9> columns(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>, field5: Field<T5>, field6: Field<T6>, field7: Field<T7>, field8: Field<T8>, field9: Field<T9>): InsertValuesStep9<R, T1, T2, T3, T4, T5, T6, T7, T8, T9> {
        return wrappee.columns(field1, field2, field3, field4, field5, field6, field7, field8, field9)
    }

    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> columns(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>, field5: Field<T5>, field6: Field<T6>, field7: Field<T7>, field8: Field<T8>, field9: Field<T9>, field10: Field<T10>): InsertValuesStep10<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> {
        return wrappee.columns(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10)
    }

    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> columns(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>, field5: Field<T5>, field6: Field<T6>, field7: Field<T7>, field8: Field<T8>, field9: Field<T9>, field10: Field<T10>, field11: Field<T11>): InsertValuesStep11<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> {
        return wrappee.columns(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11)
    }

    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> columns(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>, field5: Field<T5>, field6: Field<T6>, field7: Field<T7>, field8: Field<T8>, field9: Field<T9>, field10: Field<T10>, field11: Field<T11>, field12: Field<T12>): InsertValuesStep12<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> {
        return wrappee.columns(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12)
    }

    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> columns(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>, field5: Field<T5>, field6: Field<T6>, field7: Field<T7>, field8: Field<T8>, field9: Field<T9>, field10: Field<T10>, field11: Field<T11>, field12: Field<T12>, field13: Field<T13>): InsertValuesStep13<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> {
        return wrappee.columns(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13)
    }

    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> columns(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>, field5: Field<T5>, field6: Field<T6>, field7: Field<T7>, field8: Field<T8>, field9: Field<T9>, field10: Field<T10>, field11: Field<T11>, field12: Field<T12>, field13: Field<T13>, field14: Field<T14>): InsertValuesStep14<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> {
        return wrappee.columns(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14)
    }

    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> columns(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>, field5: Field<T5>, field6: Field<T6>, field7: Field<T7>, field8: Field<T8>, field9: Field<T9>, field10: Field<T10>, field11: Field<T11>, field12: Field<T12>, field13: Field<T13>, field14: Field<T14>, field15: Field<T15>): InsertValuesStep15<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> {
        return wrappee.columns(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15)
    }

    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> columns(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>, field5: Field<T5>, field6: Field<T6>, field7: Field<T7>, field8: Field<T8>, field9: Field<T9>, field10: Field<T10>, field11: Field<T11>, field12: Field<T12>, field13: Field<T13>, field14: Field<T14>, field15: Field<T15>, field16: Field<T16>): InsertValuesStep16<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> {
        return wrappee.columns(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16)
    }

    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> columns(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>, field5: Field<T5>, field6: Field<T6>, field7: Field<T7>, field8: Field<T8>, field9: Field<T9>, field10: Field<T10>, field11: Field<T11>, field12: Field<T12>, field13: Field<T13>, field14: Field<T14>, field15: Field<T15>, field16: Field<T16>, field17: Field<T17>): InsertValuesStep17<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> {
        return wrappee.columns(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17)
    }

    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> columns(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>, field5: Field<T5>, field6: Field<T6>, field7: Field<T7>, field8: Field<T8>, field9: Field<T9>, field10: Field<T10>, field11: Field<T11>, field12: Field<T12>, field13: Field<T13>, field14: Field<T14>, field15: Field<T15>, field16: Field<T16>, field17: Field<T17>, field18: Field<T18>): InsertValuesStep18<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> {
        return wrappee.columns(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18)
    }

    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> columns(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>, field5: Field<T5>, field6: Field<T6>, field7: Field<T7>, field8: Field<T8>, field9: Field<T9>, field10: Field<T10>, field11: Field<T11>, field12: Field<T12>, field13: Field<T13>, field14: Field<T14>, field15: Field<T15>, field16: Field<T16>, field17: Field<T17>, field18: Field<T18>, field19: Field<T19>): InsertValuesStep19<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> {
        return wrappee.columns(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19)
    }

    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> columns(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>, field5: Field<T5>, field6: Field<T6>, field7: Field<T7>, field8: Field<T8>, field9: Field<T9>, field10: Field<T10>, field11: Field<T11>, field12: Field<T12>, field13: Field<T13>, field14: Field<T14>, field15: Field<T15>, field16: Field<T16>, field17: Field<T17>, field18: Field<T18>, field19: Field<T19>, field20: Field<T20>): InsertValuesStep20<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> {
        return wrappee.columns(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20)
    }

    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> columns(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>, field5: Field<T5>, field6: Field<T6>, field7: Field<T7>, field8: Field<T8>, field9: Field<T9>, field10: Field<T10>, field11: Field<T11>, field12: Field<T12>, field13: Field<T13>, field14: Field<T14>, field15: Field<T15>, field16: Field<T16>, field17: Field<T17>, field18: Field<T18>, field19: Field<T19>, field20: Field<T20>, field21: Field<T21>): InsertValuesStep21<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> {
        return wrappee.columns(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21)
    }

    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> columns(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>, field5: Field<T5>, field6: Field<T6>, field7: Field<T7>, field8: Field<T8>, field9: Field<T9>, field10: Field<T10>, field11: Field<T11>, field12: Field<T12>, field13: Field<T13>, field14: Field<T14>, field15: Field<T15>, field16: Field<T16>, field17: Field<T17>, field18: Field<T18>, field19: Field<T19>, field20: Field<T20>, field21: Field<T21>, field22: Field<T22>): InsertValuesStep22<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> {
        return wrappee.columns(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21, field22)
    }

    operator override fun <T> set(field: Field<T>, value: T): InsertSetMoreStep<R> {
        val res = wrappee.set(field, value)
        return InsertSetMoreStepProxy(activityParams, res)
    }

    operator override fun <T> set(field: Field<T>, value: Field<T>): InsertSetMoreStep<R> {
        val res = wrappee.set(field, value)
        return InsertSetMoreStepProxy(activityParams, res)
    }

    operator override fun <T> set(field: Field<T>, value: Select<out Record1<T>>): InsertSetMoreStep<R> {
        val res = wrappee.set(field, value)
        return InsertSetMoreStepProxy(activityParams, res)
    }

    override fun set(map: Map<out Field<*>, *>): InsertSetMoreStep<R> {
        val res = wrappee.set(map)
        return InsertSetMoreStepProxy(activityParams, res)
    }

    override fun set(record: Record): InsertSetMoreStep<R> {
        val res = wrappee.set(record)
        return InsertSetMoreStepProxy(activityParams, res)
    }

    override fun values(vararg values: Any): InsertValuesStepN<R> {
        return wrappee.values(*values)
    }

    override fun values(values: Array<Field<*>>): InsertValuesStepN<R> {
        return wrappee.values(*values)
    }

    override fun values(values: Collection<*>): InsertValuesStepN<R> {
        return wrappee.values(values)
    }

    override fun defaultValues(): InsertReturningStep<R> {
        return wrappee.defaultValues()
    }

    override fun select(select: Select<*>): InsertReturningStep<R> {
        return wrappee.select(select)
    }
}

class InsertSetMoreStepProxy<R : Record>(val activityParams: ActivityParams, val wrappee: InsertSetMoreStep<R>) : InsertSetMoreStep<R> {
    operator override fun <T> set(field: Field<T>, value: T): InsertSetMoreStep<R> {
        val res = wrappee.set(field, value)
        return InsertSetMoreStepProxy(activityParams, res)
    }

    operator override fun <T> set(field: Field<T>, value: Field<T>): InsertSetMoreStep<R> {
        val res = wrappee.set(field, value)
        return InsertSetMoreStepProxy(activityParams, res)
    }

    operator override fun <T> set(field: Field<T>, value: Select<out Record1<T>>): InsertSetMoreStep<R> {
        val res = wrappee.set(field, value)
        return InsertSetMoreStepProxy(activityParams, res)
    }

    override fun set(map: Map<out Field<*>, *>): InsertSetMoreStep<R> {
        val res = wrappee.set(map)
        return InsertSetMoreStepProxy(activityParams, res)
    }

    override fun set(record: Record): InsertSetMoreStep<R> {
        val res = wrappee.set(record)
        return InsertSetMoreStepProxy(activityParams, res)
    }

    override fun newRecord(): InsertSetStep<R> {
        return wrappee.newRecord()
    }

    override fun onDuplicateKeyUpdate(): InsertOnDuplicateSetStep<R> {
        return wrappee.onDuplicateKeyUpdate()
    }

    override fun onDuplicateKeyIgnore(): InsertFinalStep<R> {
        return wrappee.onDuplicateKeyIgnore()
    }

    override fun returning(): InsertResultStep<R> {
        return wrappee.returning()
    }

    override fun returning(fields: Array<Field<*>>): InsertResultStep<R> {
        val res = wrappee.returning(*fields)
        return InsertResultStepProxy(activityParams, res)
    }

    override fun returning(fields: Collection<Field<*>>): InsertResultStep<R> {
        return wrappee.returning(fields)
    }

    @Throws(DataAccessException::class)
    override fun execute(): Int {
        return executeTracing(activityParams) {wrappee.execute()}
    }

    override fun executeAsync(): CompletionStage<Int> {
        return executeTracing(activityParams) {wrappee.executeAsync()}
    }

    override fun executeAsync(executor: Executor): CompletionStage<Int> {
        return executeTracing(activityParams) {wrappee.executeAsync(executor)}
    }

    override fun isExecutable(): Boolean {
        return wrappee.isExecutable()
    }

    override fun getSQL(): String {
        return wrappee.getSQL()
    }

    @Deprecated("")
    override fun getSQL(inline: Boolean): String {
        return wrappee.getSQL(inline)
    }

    override fun getSQL(paramType: ParamType): String {
        return wrappee.getSQL(paramType)
    }

    override fun getBindValues(): List<Any> {
        return wrappee.bindValues
    }

    override fun getParams(): Map<String, Param<*>> = wrappee.params

    override fun getParam(name: String): Param<*> {
        return wrappee.getParam(name)
    }

    @Throws(IllegalArgumentException::class, DataTypeException::class)
    override fun bind(param: String, value: Any): Query {
        return wrappee.bind(param, value)
    }

    @Throws(IllegalArgumentException::class, DataTypeException::class)
    override fun bind(index: Int, value: Any): Query {
        return wrappee.bind(index, value)
    }

    override fun queryTimeout(timeout: Int): Query {
        return wrappee.queryTimeout(timeout)
    }

    override fun keepStatement(keepStatement: Boolean): Query {
        return wrappee.keepStatement(keepStatement)
    }

    @Throws(DataAccessException::class)
    override fun close() {
        wrappee.close()
    }

    @Throws(DataAccessException::class)
    override fun cancel() {
        wrappee.cancel()
    }

    override fun attach(configuration: Configuration) {
        wrappee.attach(configuration)
    }

    override fun detach() {
        wrappee.detach()
    }
}


class InsertResultStepProxy<R : Record>(val activityParams: ActivityParams, val wrappee: InsertResultStep<R>) : InsertResultStep<R> {

    @Support
    @Throws(DataAccessException::class)
    override fun fetch(): Result<R> {
        return wrappee.fetch()
    }

    @Support
    @Throws(DataAccessException::class)
    override fun fetchOne(): R =
        executeTracing(activityParams) {wrappee.fetchOne()}

    @Support
    @Throws(DataAccessException::class)
    override fun fetchOptional(): Optional<R> {
        return wrappee.fetchOptional()
    }

    @Throws(DataAccessException::class)
    override fun execute(): Int {
        return executeTracing(activityParams) {wrappee.execute()}
    }

    override fun executeAsync(): CompletionStage<Int> {
        return executeTracing(activityParams) {wrappee.executeAsync()}
    }

    override fun executeAsync(executor: Executor): CompletionStage<Int> {
        return executeTracing(activityParams) {wrappee.executeAsync(executor)}
    }

    override fun isExecutable(): Boolean {
        return wrappee.isExecutable
    }

    override fun getSQL(): String {
        return wrappee.sql
    }

    @Deprecated("")
    override fun getSQL(inline: Boolean): String {
        return wrappee.getSQL(inline)
    }

    override fun getSQL(paramType: ParamType): String {
        return wrappee.getSQL(paramType)
    }

    override fun getBindValues(): List<Any> {
        return wrappee.bindValues
    }

    override fun getParams(): Map<String, Param<*>> {
        return wrappee.params
    }

    override fun getParam(name: String): Param<*> {
        return wrappee.getParam(name)
    }

    @Throws(IllegalArgumentException::class, DataTypeException::class)
    override fun bind(param: String, value: Any): Query {
        return wrappee.bind(param, value)
    }

    @Throws(IllegalArgumentException::class, DataTypeException::class)
    override fun bind(index: Int, value: Any): Query {
        return wrappee.bind(index, value)
    }

    override fun queryTimeout(timeout: Int): Query {
        return wrappee.queryTimeout(timeout)
    }

    override fun keepStatement(keepStatement: Boolean): Query {
        return wrappee.keepStatement(keepStatement)
    }

    @Throws(DataAccessException::class)
    override fun close() {
        wrappee.close()
    }

    @Throws(DataAccessException::class)
    override fun cancel() {
        wrappee.cancel()
    }

    override fun attach(configuration: Configuration) {
        wrappee.attach(configuration)
    }

    override fun detach() {
        wrappee.detach()
    }
}

class UpdateSetFirstStepProxy<R : Record>(private val activityParams: ActivityParams, private val wrappee: UpdateSetFirstStep<R>) : UpdateSetFirstStep<R> {

    @Support(SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES)
    override fun <T1> set(row: Row1<T1>, value: Row1<T1>): UpdateFromStep<R> {
        return wrappee.set(row, value)
    }

    @Support(SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES)
    override fun <T1, T2> set(row: Row2<T1, T2>, value: Row2<T1, T2>): UpdateFromStep<R> {
        return wrappee.set(row, value)
    }

    @Support(SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES)
    override fun <T1, T2, T3> set(row: Row3<T1, T2, T3>, value: Row3<T1, T2, T3>): UpdateFromStep<R> {
        return wrappee.set(row, value)
    }

    @Support(SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES)
    override fun <T1, T2, T3, T4> set(row: Row4<T1, T2, T3, T4>, value: Row4<T1, T2, T3, T4>): UpdateFromStep<R> {
        return wrappee.set(row, value)
    }

    @Support(SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES)
    override fun <T1, T2, T3, T4, T5> set(row: Row5<T1, T2, T3, T4, T5>, value: Row5<T1, T2, T3, T4, T5>): UpdateFromStep<R> {
        return wrappee.set(row, value)
    }

    @Support(SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES)
    override fun <T1, T2, T3, T4, T5, T6> set(row: Row6<T1, T2, T3, T4, T5, T6>, value: Row6<T1, T2, T3, T4, T5, T6>): UpdateFromStep<R> {
        return wrappee.set(row, value)
    }

    @Support(SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES)
    override fun <T1, T2, T3, T4, T5, T6, T7> set(row: Row7<T1, T2, T3, T4, T5, T6, T7>, value: Row7<T1, T2, T3, T4, T5, T6, T7>): UpdateFromStep<R> {
        return wrappee.set(row, value)
    }

    @Support(SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES)
    override fun <T1, T2, T3, T4, T5, T6, T7, T8> set(row: Row8<T1, T2, T3, T4, T5, T6, T7, T8>, value: Row8<T1, T2, T3, T4, T5, T6, T7, T8>): UpdateFromStep<R> {
        return wrappee.set(row, value)
    }

    @Support(SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES)
    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9> set(row: Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9>, value: Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9>): UpdateFromStep<R> {
        return wrappee.set(row, value)
    }

    @Support(SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES)
    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> set(row: Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>, value: Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>): UpdateFromStep<R> {
        return wrappee.set(row, value)
    }

    @Support(SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES)
    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> set(row: Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>, value: Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>): UpdateFromStep<R> {
        return wrappee.set(row, value)
    }

    @Support(SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES)
    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> set(row: Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>, value: Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>): UpdateFromStep<R> {
        return wrappee.set(row, value)
    }

    @Support(SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES)
    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> set(row: Row13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>, value: Row13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>): UpdateFromStep<R> {
        return wrappee.set(row, value)
    }

    @Support(SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES)
    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> set(row: Row14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>, value: Row14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>): UpdateFromStep<R> {
        return wrappee.set(row, value)
    }

    @Support(SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES)
    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> set(row: Row15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>, value: Row15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>): UpdateFromStep<R> {
        return wrappee.set(row, value)
    }

    @Support(SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES)
    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> set(row: Row16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>, value: Row16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>): UpdateFromStep<R> {
        return wrappee.set(row, value)
    }

    @Support(SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES)
    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> set(row: Row17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>, value: Row17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>): UpdateFromStep<R> {
        return wrappee.set(row, value)
    }

    @Support(SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES)
    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> set(row: Row18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>, value: Row18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>): UpdateFromStep<R> {
        return wrappee.set(row, value)
    }

    @Support(SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES)
    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> set(row: Row19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>, value: Row19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>): UpdateFromStep<R> {
        return wrappee.set(row, value)
    }

    @Support(SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES)
    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> set(row: Row20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>, value: Row20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>): UpdateFromStep<R> {
        return wrappee.set(row, value)
    }

    @Support(SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES)
    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> set(row: Row21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>, value: Row21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>): UpdateFromStep<R> {
        return wrappee.set(row, value)
    }

    @Support(SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES)
    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> set(row: Row22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>, value: Row22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>): UpdateFromStep<R> {
        return wrappee.set(row, value)
    }

    @Support(SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES)
    override fun set(row: RowN, value: RowN): UpdateFromStep<R> {
        return wrappee.set(row, value)
    }

    @Support(SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES_9_5)
    override fun <T1> set(row: Row1<T1>, select: Select<out Record1<T1>>): UpdateFromStep<R> {
        return wrappee.set(row, select)
    }

    @Support(SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES_9_5)
    override fun <T1, T2> set(row: Row2<T1, T2>, select: Select<out Record2<T1, T2>>): UpdateFromStep<R> {
        return wrappee.set(row, select)
    }

    @Support(SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES_9_5)
    override fun <T1, T2, T3> set(row: Row3<T1, T2, T3>, select: Select<out Record3<T1, T2, T3>>): UpdateFromStep<R> {
        return wrappee.set(row, select)
    }

    @Support(SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES_9_5)
    override fun <T1, T2, T3, T4> set(row: Row4<T1, T2, T3, T4>, select: Select<out Record4<T1, T2, T3, T4>>): UpdateFromStep<R> {
        return wrappee.set(row, select)
    }

    @Support(SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES_9_5)
    override fun <T1, T2, T3, T4, T5> set(row: Row5<T1, T2, T3, T4, T5>, select: Select<out Record5<T1, T2, T3, T4, T5>>): UpdateFromStep<R> {
        return wrappee.set(row, select)
    }

    @Support(SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES_9_5)
    override fun <T1, T2, T3, T4, T5, T6> set(row: Row6<T1, T2, T3, T4, T5, T6>, select: Select<out Record6<T1, T2, T3, T4, T5, T6>>): UpdateFromStep<R> {
        return wrappee.set(row, select)
    }

    @Support(SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES_9_5)
    override fun <T1, T2, T3, T4, T5, T6, T7> set(row: Row7<T1, T2, T3, T4, T5, T6, T7>, select: Select<out Record7<T1, T2, T3, T4, T5, T6, T7>>): UpdateFromStep<R> {
        return wrappee.set(row, select)
    }

    @Support(SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES_9_5)
    override fun <T1, T2, T3, T4, T5, T6, T7, T8> set(row: Row8<T1, T2, T3, T4, T5, T6, T7, T8>, select: Select<out Record8<T1, T2, T3, T4, T5, T6, T7, T8>>): UpdateFromStep<R> {
        return wrappee.set(row, select)
    }

    @Support(SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES_9_5)
    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9> set(row: Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9>, select: Select<out Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>>): UpdateFromStep<R> {
        return wrappee.set(row, select)
    }

    @Support(SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES_9_5)
    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> set(row: Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>, select: Select<out Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>>): UpdateFromStep<R> {
        return wrappee.set(row, select)
    }

    @Support(SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES_9_5)
    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> set(row: Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>, select: Select<out Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>>): UpdateFromStep<R> {
        return wrappee.set(row, select)
    }

    @Support(SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES_9_5)
    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> set(row: Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>, select: Select<out Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>>): UpdateFromStep<R> {
        return wrappee.set(row, select)
    }

    @Support(SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES_9_5)
    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> set(row: Row13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>, select: Select<out Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>>): UpdateFromStep<R> {
        return wrappee.set(row, select)
    }

    @Support(SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES_9_5)
    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> set(row: Row14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>, select: Select<out Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>>): UpdateFromStep<R> {
        return wrappee.set(row, select)
    }

    @Support(SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES_9_5)
    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> set(row: Row15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>, select: Select<out Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>>): UpdateFromStep<R> {
        return wrappee.set(row, select)
    }

    @Support(SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES_9_5)
    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> set(row: Row16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>, select: Select<out Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>>): UpdateFromStep<R> {
        return wrappee.set(row, select)
    }

    @Support(SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES_9_5)
    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> set(row: Row17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>, select: Select<out Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>>): UpdateFromStep<R> {
        return wrappee.set(row, select)
    }

    @Support(SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES_9_5)
    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> set(row: Row18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>, select: Select<out Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>>): UpdateFromStep<R> {
        return wrappee.set(row, select)
    }

    @Support(SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES_9_5)
    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> set(row: Row19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>, select: Select<out Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>>): UpdateFromStep<R> {
        return wrappee.set(row, select)
    }

    @Support(SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES_9_5)
    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> set(row: Row20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>, select: Select<out Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>>): UpdateFromStep<R> {
        return wrappee.set(row, select)
    }

    @Support(SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES_9_5)
    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> set(row: Row21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>, select: Select<out Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>>): UpdateFromStep<R> {
        return wrappee.set(row, select)
    }

    @Support(SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES_9_5)
    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> set(row: Row22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>, select: Select<out Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>>): UpdateFromStep<R> {
        return wrappee.set(row, select)
    }

    @Support(SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES_9_5)
    override fun set(row: RowN, select: Select<*>): UpdateFromStep<R> {
        return wrappee.set(row, select)
    }

    @Support
    override fun <T> set(field: Field<T>, value: T): UpdateSetMoreStep<R> {
        return UpdateSetMoreStepProxy(activityParams, wrappee.set(field, value))
    }

    @Support
    override fun <T> set(field: Field<T>, value: Field<T>): UpdateSetMoreStep<R> {
        return UpdateSetMoreStepProxy(activityParams, wrappee.set(field, value))
    }

    @Support
    override fun <T> set(field: Field<T>, value: Select<out Record1<T>>): UpdateSetMoreStep<R> {
        return UpdateSetMoreStepProxy(activityParams, wrappee.set(field, value))
    }

    @Support
    override fun set(map: Map<out Field<*>, *>): UpdateSetMoreStep<R> {
        return UpdateSetMoreStepProxy(activityParams, wrappee.set(map))
    }

    @Support
    override fun set(record: Record): UpdateSetMoreStep<R> {
        return UpdateSetMoreStepProxy(activityParams, wrappee.set(record))
    }
}

internal class UpdateSetMoreStepProxy<R : Record>(private val activityParams: ActivityParams, private val wrappee: UpdateSetMoreStep<R>) : UpdateSetMoreStep<R> {

    @Support
    override fun <T> set(field: Field<T>, value: T): UpdateSetMoreStep<R> {
        return UpdateSetMoreStepProxy(activityParams, wrappee.set(field, value))
    }

    @Support
    override fun <T> set(field: Field<T>, value: Field<T>): UpdateSetMoreStep<R> {
        return UpdateSetMoreStepProxy(activityParams, wrappee.set(field, value))
    }

    @Support
    override fun <T> set(field: Field<T>, value: Select<out Record1<T>>): UpdateSetMoreStep<R> {
        return UpdateSetMoreStepProxy(activityParams, wrappee.set(field, value))
    }

    @Support
    override fun set(map: Map<out Field<*>, *>): UpdateSetMoreStep<R> {
        return UpdateSetMoreStepProxy(activityParams, wrappee.set(map))
    }

    @Support
    override fun set(record: Record): UpdateSetMoreStep<R> {
        return UpdateSetMoreStepProxy(activityParams, wrappee.set(record))
    }

    @Support(SQLDialect.POSTGRES)
    override fun from(table: TableLike<*>): UpdateWhereStep<R> {
        return wrappee.from(table)
    }

    @Support(SQLDialect.POSTGRES)
    override fun from(table: Array<TableLike<*>>): UpdateWhereStep<R> {
        return wrappee.from(*table)
    }

    @Support(SQLDialect.POSTGRES)
    override fun from(tables: Collection<TableLike<*>>): UpdateWhereStep<R> {
        return wrappee.from(tables)
    }

    @Support(SQLDialect.POSTGRES)
    @PlainSQL
    override fun from(sql: SQL): UpdateWhereStep<R> {
        return wrappee.from(sql)
    }

    @Support(SQLDialect.POSTGRES)
    @PlainSQL
    override fun from(sql: String): UpdateWhereStep<R> {
        return wrappee.from(sql)
    }

    @Support(SQLDialect.POSTGRES)
    @PlainSQL
    override fun from(sql: String, vararg bindings: Any): UpdateWhereStep<R> {
        return wrappee.from(sql, *bindings)
    }

    @Support(SQLDialect.POSTGRES)
    @PlainSQL
    override fun from(sql: String, vararg parts: QueryPart): UpdateWhereStep<R> {
        return wrappee.from(sql, *parts)
    }

    @Support(SQLDialect.POSTGRES)
    override fun from(name: Name): UpdateWhereStep<R> {
        return wrappee.from(name)
    }

    @Support
    override fun where(vararg conditions: Condition): UpdateConditionStep<R> {
        return UpdateConditionStepProxy(activityParams, wrappee.where(*conditions))
    }

    @Support
    override fun where(conditions: Collection<Condition>): UpdateConditionStep<R> {
        return UpdateConditionStepProxy(activityParams, wrappee.where(conditions))
    }

    @Support
    override fun where(condition: Field<Boolean>): UpdateConditionStep<R> {
        return UpdateConditionStepProxy(activityParams, wrappee.where(condition))
    }

    @Deprecated("")
    @Support
    override fun where(condition: Boolean?): UpdateConditionStep<R> {
        return UpdateConditionStepProxy(activityParams, wrappee.where(condition))
    }

    @Support
    @PlainSQL
    override fun where(sql: SQL): UpdateConditionStep<R> {
        return UpdateConditionStepProxy(activityParams, wrappee.where(sql))
    }

    @Support
    @PlainSQL
    override fun where(sql: String): UpdateConditionStep<R> {
        return UpdateConditionStepProxy(activityParams, wrappee.where(sql))
    }

    @Support
    @PlainSQL
    override fun where(sql: String, vararg bindings: Any): UpdateConditionStep<R> {
        return UpdateConditionStepProxy(activityParams, wrappee.where(sql, *bindings))
    }

    @Support
    @PlainSQL
    override fun where(sql: String, vararg parts: QueryPart): UpdateConditionStep<R> {
        return UpdateConditionStepProxy(activityParams, wrappee.where(sql, *parts))
    }

    @Support
    override fun whereExists(select: Select<*>): UpdateConditionStep<R> {
        return UpdateConditionStepProxy(activityParams, wrappee.whereExists(select))
    }

    @Support
    override fun whereNotExists(select: Select<*>): UpdateConditionStep<R> {
        return UpdateConditionStepProxy(activityParams, wrappee.whereNotExists(select))
    }

    @Throws(DataAccessException::class)
    override fun execute(): Int {
        return executeTracing(activityParams) {wrappee.execute()}
    }

    override fun executeAsync(): CompletionStage<Int> {
        return executeTracing(activityParams) {wrappee.executeAsync()}
    }

    override fun executeAsync(executor: Executor): CompletionStage<Int> {
        return executeTracing(activityParams) {wrappee.executeAsync(executor)}
    }

    override fun isExecutable(): Boolean {
        return wrappee.isExecutable
    }

    override fun getSQL(): String {
        return wrappee.sql
    }

    @Deprecated("")
    override fun getSQL(inline: Boolean): String {
        return wrappee.getSQL(inline)
    }

    override fun getSQL(paramType: ParamType): String {
        return wrappee.getSQL(paramType)
    }

    override fun getBindValues(): List<Any> {
        return wrappee.bindValues
    }

    override fun getParams(): Map<String, Param<*>> {
        return wrappee.params
    }

    override fun getParam(name: String): Param<*> {
        return wrappee.getParam(name)
    }

    @Throws(IllegalArgumentException::class, DataTypeException::class)
    override fun bind(param: String, value: Any): Query {
        return wrappee.bind(param, value)
    }

    @Throws(IllegalArgumentException::class, DataTypeException::class)
    override fun bind(index: Int, value: Any): Query {
        return wrappee.bind(index, value)
    }

    override fun queryTimeout(timeout: Int): Query {
        return wrappee.queryTimeout(timeout)
    }

    override fun keepStatement(keepStatement: Boolean): Query {
        return wrappee.keepStatement(keepStatement)
    }

    @Throws(DataAccessException::class)
    override fun close() {
        wrappee.close()
    }

    @Throws(DataAccessException::class)
    override fun cancel() {
        wrappee.cancel()
    }

    override fun toString(): String {
        return wrappee.toString()
    }

    override fun equals(`object`: Any?): Boolean {
        return wrappee == `object`
    }

    override fun hashCode(): Int {
        return wrappee.hashCode()
    }

    override fun attach(configuration: Configuration) {
        wrappee.attach(configuration)
    }

    override fun detach() {
        wrappee.detach()
    }

    @Support(SQLDialect.FIREBIRD, SQLDialect.POSTGRES)
    override fun returning(): UpdateResultStep<R> {
        return wrappee.returning()
    }

    @Support(SQLDialect.FIREBIRD, SQLDialect.POSTGRES)
    override fun returning(fields: Array<Field<*>>): UpdateResultStep<R> {
        return wrappee.returning(*fields)
    }

    @Support(SQLDialect.FIREBIRD, SQLDialect.POSTGRES)
    override fun returning(fields: Collection<Field<*>>): UpdateResultStep<R> {
        return wrappee.returning(fields)
    }
}

internal class UpdateConditionStepProxy<R : Record>(private val activityParams: ActivityParams, private val wrappee: UpdateConditionStep<R>) : UpdateConditionStep<R> {

    @Support
    override fun and(condition: Condition): UpdateConditionStep<R> {
        return UpdateConditionStepProxy(activityParams, wrappee.and(condition))
    }

    @Support
    override fun and(condition: Field<Boolean>): UpdateConditionStep<R> {
        return UpdateConditionStepProxy(activityParams, wrappee.and(condition))
    }

    @Deprecated("")
    @Support
    override fun and(condition: Boolean?): UpdateConditionStep<R> {
        return UpdateConditionStepProxy(activityParams, wrappee.and(condition))
    }

    @Support
    @PlainSQL
    override fun and(sql: SQL): UpdateConditionStep<R> {
        return UpdateConditionStepProxy(activityParams, wrappee.and(sql))
    }

    @Support
    @PlainSQL
    override fun and(sql: String): UpdateConditionStep<R> {
        return UpdateConditionStepProxy(activityParams, wrappee.and(sql))
    }

    @Support
    @PlainSQL
    override fun and(sql: String, vararg bindings: Any): UpdateConditionStep<R> {
        return UpdateConditionStepProxy(activityParams, wrappee.and(sql, *bindings))
    }

    @Support
    @PlainSQL
    override fun and(sql: String, vararg parts: QueryPart): UpdateConditionStep<R> {
        return UpdateConditionStepProxy(activityParams, wrappee.and(sql, *parts))
    }

    @Support
    override fun andNot(condition: Condition): UpdateConditionStep<R> {
        return UpdateConditionStepProxy(activityParams, wrappee.andNot(condition))
    }

    @Support
    override fun andNot(condition: Field<Boolean>): UpdateConditionStep<R> {
        return UpdateConditionStepProxy(activityParams, wrappee.andNot(condition))
    }

    @Deprecated("")
    @Support
    override fun andNot(condition: Boolean?): UpdateConditionStep<R> {
        return UpdateConditionStepProxy(activityParams, wrappee.andNot(condition))
    }

    @Support
    override fun andExists(select: Select<*>): UpdateConditionStep<R> {
        return UpdateConditionStepProxy(activityParams, wrappee.andExists(select))
    }

    @Support
    override fun andNotExists(select: Select<*>): UpdateConditionStep<R> {
        return UpdateConditionStepProxy(activityParams, wrappee.andNotExists(select))
    }

    @Support
    override fun or(condition: Condition): UpdateConditionStep<R> {
        return UpdateConditionStepProxy(activityParams, wrappee.or(condition))
    }

    @Support
    override fun or(condition: Field<Boolean>): UpdateConditionStep<R> {
        return UpdateConditionStepProxy(activityParams, wrappee.or(condition))
    }

    @Deprecated("")
    @Support
    override fun or(condition: Boolean?): UpdateConditionStep<R> {
        return UpdateConditionStepProxy(activityParams, wrappee.or(condition))
    }

    @Support
    @PlainSQL
    override fun or(sql: SQL): UpdateConditionStep<R> {
        return UpdateConditionStepProxy(activityParams, wrappee.or(sql))
    }

    @Support
    @PlainSQL
    override fun or(sql: String): UpdateConditionStep<R> {
        return UpdateConditionStepProxy(activityParams, wrappee.or(sql))
    }

    @Support
    @PlainSQL
    override fun or(sql: String, vararg bindings: Any): UpdateConditionStep<R> {
        return UpdateConditionStepProxy(activityParams, wrappee.or(sql, *bindings))
    }

    @Support
    @PlainSQL
    override fun or(sql: String, vararg parts: QueryPart): UpdateConditionStep<R> {
        return UpdateConditionStepProxy(activityParams, wrappee.or(sql, *parts))
    }

    @Support
    override fun orNot(condition: Condition): UpdateConditionStep<R> {
        return UpdateConditionStepProxy(activityParams, wrappee.orNot(condition))
    }

    @Support
    override fun orNot(condition: Field<Boolean>): UpdateConditionStep<R> {
        return UpdateConditionStepProxy(activityParams, wrappee.orNot(condition))
    }

    @Deprecated("")
    @Support
    override fun orNot(condition: Boolean?): UpdateConditionStep<R> {
        return UpdateConditionStepProxy(activityParams, wrappee.orNot(condition))
    }

    @Support
    override fun orExists(select: Select<*>): UpdateConditionStep<R> {
        return UpdateConditionStepProxy(activityParams, wrappee.orExists(select))
    }

    @Support
    override fun orNotExists(select: Select<*>): UpdateConditionStep<R> {
        return UpdateConditionStepProxy(activityParams, wrappee.orNotExists(select))
    }

    @Throws(DataAccessException::class)
    override fun execute(): Int {
        return executeTracing(activityParams) {wrappee.execute()}
    }

    override fun executeAsync(): CompletionStage<Int> {
        return executeTracing(activityParams) {wrappee.executeAsync()}
    }

    override fun executeAsync(executor: Executor): CompletionStage<Int> {
        return executeTracing(activityParams) {wrappee.executeAsync(executor)}
    }

    override fun isExecutable(): Boolean {
        return wrappee.isExecutable
    }

    override fun getSQL(): String {
        return wrappee.sql
    }

    @Deprecated("")
    override fun getSQL(inline: Boolean): String {
        return wrappee.getSQL(inline)
    }

    override fun getSQL(paramType: ParamType): String {
        return wrappee.getSQL(paramType)
    }

    override fun getBindValues(): List<Any> {
        return wrappee.bindValues
    }

    override fun getParams(): Map<String, Param<*>> {
        return wrappee.params
    }

    override fun getParam(name: String): Param<*> {
        return wrappee.getParam(name)
    }

    @Throws(IllegalArgumentException::class, DataTypeException::class)
    override fun bind(param: String, value: Any): Query {
        return wrappee.bind(param, value)
    }

    @Throws(IllegalArgumentException::class, DataTypeException::class)
    override fun bind(index: Int, value: Any): Query {
        return wrappee.bind(index, value)
    }

    override fun queryTimeout(timeout: Int): Query {
        return wrappee.queryTimeout(timeout)
    }

    override fun keepStatement(keepStatement: Boolean): Query {
        return wrappee.keepStatement(keepStatement)
    }

    @Throws(DataAccessException::class)
    override fun close() {
        wrappee.close()
    }

    @Throws(DataAccessException::class)
    override fun cancel() {
        wrappee.cancel()
    }

    override fun toString(): String {
        return wrappee.toString()
    }

    override fun equals(`object`: Any?): Boolean {
        return wrappee == `object`
    }

    override fun hashCode(): Int {
        return wrappee.hashCode()
    }

    override fun attach(configuration: Configuration) {
        wrappee.attach(configuration)
    }

    override fun detach() {
        wrappee.detach()
    }

    @Support(SQLDialect.FIREBIRD, SQLDialect.POSTGRES)
    override fun returning(): UpdateResultStep<R> {
        return wrappee.returning()
    }

    @Support(SQLDialect.FIREBIRD, SQLDialect.POSTGRES)
    override fun returning(fields: Array<Field<*>>): UpdateResultStep<R> {
        return wrappee.returning(*fields)
    }

    @Support(SQLDialect.FIREBIRD, SQLDialect.POSTGRES)
    override fun returning(fields: Collection<Field<*>>): UpdateResultStep<R> {
        return wrappee.returning(fields)
    }
}



private fun <T> executeTracing(activityParams: ActivityParams, block: () -> T): T {
    if (!BackGlobus.tracingEnabled) return block()

    val rlm = RedisLogMessage.SQL() - {o ->
        o.shortDescription = activityParams.shortDescription
        o.stage = PENDING
        o.text = "Not known yet"
    }
    redisLog.send(rlm)

    requestShit.actualSQLFromJOOQ = null

    try {
        val res = block()
        rlm.stage = SUCCESS
        return res
    } catch (e: Throwable) {
        rlm.stage = FAILURE
        rlm.exceptionStack = e.stackString()
        throw e
    } finally {
        requestShit.actualSQLFromJOOQ?.let {rlm.text = it}
        rlm.endMillis = currentTimeMillis()
        redisLog.amend(rlm)
    }
}


//class SelectWhereStepProxy<R : Record>(private val activityParams: ActivityParams, private val wrappee: SelectWhereStep<R>) : SelectWhereStep<R> {
//
//    @Support
//    override fun where(vararg conditions: Condition): SelectConditionStep<R> {
//        return wrappee.where(*conditions)
//    }
//
//    @Support
//    override fun where(conditions: Collection<Condition>): SelectConditionStep<R> {
//        return wrappee.where(conditions)
//    }
//
//    @Support
//    override fun where(field: Field<Boolean>): SelectConditionStep<R> {
//        return wrappee.where(field)
//    }
//
//    @Deprecated("")
//    @Support
//    override fun where(field: Boolean?): SelectConditionStep<R> {
//        return wrappee.where(field)
//    }
//
//    @Support
//    @PlainSQL
//    override fun where(sql: SQL): SelectConditionStep<R> {
//        return wrappee.where(sql)
//    }
//
//    @Support
//    @PlainSQL
//    override fun where(sql: String): SelectConditionStep<R> {
//        return wrappee.where(sql)
//    }
//
//    @Support
//    @PlainSQL
//    override fun where(sql: String, vararg bindings: Any): SelectConditionStep<R> {
//        return wrappee.where(sql, *bindings)
//    }
//
//    @Support
//    @PlainSQL
//    override fun where(sql: String, vararg parts: QueryPart): SelectConditionStep<R> {
//        return wrappee.where(sql, *parts)
//    }
//
//    @Support
//    override fun whereExists(select: Select<*>): SelectConditionStep<R> {
//        return wrappee.whereExists(select)
//    }
//
//    @Support
//    override fun whereNotExists(select: Select<*>): SelectConditionStep<R> {
//        return wrappee.whereNotExists(select)
//    }
//
//    @Support(SQLDialect.CUBRID)
//    override fun connectBy(condition: Condition): SelectConnectByConditionStep<R> {
//        return wrappee.connectBy(condition)
//    }
//
//    @Support(SQLDialect.CUBRID)
//    override fun connectBy(condition: Field<Boolean>): SelectConnectByConditionStep<R> {
//        return wrappee.connectBy(condition)
//    }
//
//    @Deprecated("")
//    @Support(SQLDialect.CUBRID)
//    override fun connectBy(condition: Boolean?): SelectConnectByConditionStep<R> {
//        return wrappee.connectBy(condition)
//    }
//
//    @Support(SQLDialect.CUBRID)
//    @PlainSQL
//    override fun connectBy(sql: SQL): SelectConnectByConditionStep<R> {
//        return wrappee.connectBy(sql)
//    }
//
//    @Support(SQLDialect.CUBRID)
//    @PlainSQL
//    override fun connectBy(sql: String): SelectConnectByConditionStep<R> {
//        return wrappee.connectBy(sql)
//    }
//
//    @Support(SQLDialect.CUBRID)
//    @PlainSQL
//    override fun connectBy(sql: String, vararg bindings: Any): SelectConnectByConditionStep<R> {
//        return wrappee.connectBy(sql, *bindings)
//    }
//
//    @Support(SQLDialect.CUBRID)
//    @PlainSQL
//    override fun connectBy(sql: String, vararg parts: QueryPart): SelectConnectByConditionStep<R> {
//        return wrappee.connectBy(sql, *parts)
//    }
//
//    @Support(SQLDialect.CUBRID)
//    override fun connectByNoCycle(condition: Condition): SelectConnectByConditionStep<R> {
//        return wrappee.connectByNoCycle(condition)
//    }
//
//    @Support(SQLDialect.CUBRID)
//    override fun connectByNoCycle(condition: Field<Boolean>): SelectConnectByConditionStep<R> {
//        return wrappee.connectByNoCycle(condition)
//    }
//
//    @Deprecated("")
//    @Support(SQLDialect.CUBRID)
//    override fun connectByNoCycle(condition: Boolean?): SelectConnectByConditionStep<R> {
//        return wrappee.connectByNoCycle(condition)
//    }
//
//    @Support(SQLDialect.CUBRID)
//    @PlainSQL
//    override fun connectByNoCycle(sql: SQL): SelectConnectByConditionStep<R> {
//        return wrappee.connectByNoCycle(sql)
//    }
//
//    @Support(SQLDialect.CUBRID)
//    @PlainSQL
//    override fun connectByNoCycle(sql: String): SelectConnectByConditionStep<R> {
//        return wrappee.connectByNoCycle(sql)
//    }
//
//    @Support(SQLDialect.CUBRID)
//    @PlainSQL
//    override fun connectByNoCycle(sql: String, vararg bindings: Any): SelectConnectByConditionStep<R> {
//        return wrappee.connectByNoCycle(sql, *bindings)
//    }
//
//    @Support(SQLDialect.CUBRID)
//    @PlainSQL
//    override fun connectByNoCycle(sql: String, vararg parts: QueryPart): SelectConnectByConditionStep<R> {
//        return wrappee.connectByNoCycle(sql, *parts)
//    }
//
//    @Support
//    override fun groupBy(vararg fields: GroupField): SelectHavingStep<R> {
//        return wrappee.groupBy(*fields)
//    }
//
//    @Support
//    override fun groupBy(fields: Collection<GroupField>): SelectHavingStep<R> {
//        return wrappee.groupBy(fields)
//    }
//
//    @Support
//    override fun having(vararg conditions: Condition): SelectHavingConditionStep<R> {
//        return wrappee.having(*conditions)
//    }
//
//    @Support
//    override fun having(conditions: Collection<Condition>): SelectHavingConditionStep<R> {
//        return wrappee.having(conditions)
//    }
//
//    @Support
//    override fun having(condition: Field<Boolean>): SelectHavingConditionStep<R> {
//        return wrappee.having(condition)
//    }
//
//    @Deprecated("")
//    @Support
//    override fun having(condition: Boolean?): SelectHavingConditionStep<R> {
//        return wrappee.having(condition)
//    }
//
//    @Support
//    @PlainSQL
//    override fun having(sql: SQL): SelectHavingConditionStep<R> {
//        return wrappee.having(sql)
//    }
//
//    @Support
//    @PlainSQL
//    override fun having(sql: String): SelectHavingConditionStep<R> {
//        return wrappee.having(sql)
//    }
//
//    @Support
//    @PlainSQL
//    override fun having(sql: String, vararg bindings: Any): SelectHavingConditionStep<R> {
//        return wrappee.having(sql, *bindings)
//    }
//
//    @Support
//    @PlainSQL
//    override fun having(sql: String, vararg parts: QueryPart): SelectHavingConditionStep<R> {
//        return wrappee.having(sql, *parts)
//    }
//
//    @Support(SQLDialect.CUBRID, SQLDialect.FIREBIRD_3_0, SQLDialect.POSTGRES)
//    override fun window(vararg definitions: WindowDefinition): SelectOrderByStep<R> {
//        return wrappee.window(*definitions)
//    }
//
//    @Support(SQLDialect.CUBRID, SQLDialect.FIREBIRD_3_0, SQLDialect.POSTGRES)
//    override fun window(definitions: Collection<WindowDefinition>): SelectOrderByStep<R> {
//        return wrappee.window(definitions)
//    }
//
//    @Support
//    override fun <T1> orderBy(field1: Field<T1>): SelectSeekStep1<R, T1> {
//        return wrappee.orderBy(field1)
//    }
//
//    @Support
//    override fun <T1, T2> orderBy(field1: Field<T1>, field2: Field<T2>): SelectSeekStep2<R, T1, T2> {
//        return wrappee.orderBy(field1, field2)
//    }
//
//    @Support
//    override fun <T1, T2, T3> orderBy(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>): SelectSeekStep3<R, T1, T2, T3> {
//        return wrappee.orderBy(field1, field2, field3)
//    }
//
//    @Support
//    override fun <T1, T2, T3, T4> orderBy(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>): SelectSeekStep4<R, T1, T2, T3, T4> {
//        return wrappee.orderBy(field1, field2, field3, field4)
//    }
//
//    @Support
//    override fun <T1, T2, T3, T4, T5> orderBy(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>, field5: Field<T5>): SelectSeekStep5<R, T1, T2, T3, T4, T5> {
//        return wrappee.orderBy(field1, field2, field3, field4, field5)
//    }
//
//    @Support
//    override fun <T1, T2, T3, T4, T5, T6> orderBy(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>, field5: Field<T5>, field6: Field<T6>): SelectSeekStep6<R, T1, T2, T3, T4, T5, T6> {
//        return wrappee.orderBy(field1, field2, field3, field4, field5, field6)
//    }
//
//    @Support
//    override fun <T1, T2, T3, T4, T5, T6, T7> orderBy(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>, field5: Field<T5>, field6: Field<T6>, field7: Field<T7>): SelectSeekStep7<R, T1, T2, T3, T4, T5, T6, T7> {
//        return wrappee.orderBy(field1, field2, field3, field4, field5, field6, field7)
//    }
//
//    @Support
//    override fun <T1, T2, T3, T4, T5, T6, T7, T8> orderBy(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>, field5: Field<T5>, field6: Field<T6>, field7: Field<T7>, field8: Field<T8>): SelectSeekStep8<R, T1, T2, T3, T4, T5, T6, T7, T8> {
//        return wrappee.orderBy(field1, field2, field3, field4, field5, field6, field7, field8)
//    }
//
//    @Support
//    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9> orderBy(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>, field5: Field<T5>, field6: Field<T6>, field7: Field<T7>, field8: Field<T8>, field9: Field<T9>): SelectSeekStep9<R, T1, T2, T3, T4, T5, T6, T7, T8, T9> {
//        return wrappee.orderBy(field1, field2, field3, field4, field5, field6, field7, field8, field9)
//    }
//
//    @Support
//    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> orderBy(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>, field5: Field<T5>, field6: Field<T6>, field7: Field<T7>, field8: Field<T8>, field9: Field<T9>, field10: Field<T10>): SelectSeekStep10<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> {
//        return wrappee.orderBy(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10)
//    }
//
//    @Support
//    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> orderBy(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>, field5: Field<T5>, field6: Field<T6>, field7: Field<T7>, field8: Field<T8>, field9: Field<T9>, field10: Field<T10>, field11: Field<T11>): SelectSeekStep11<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> {
//        return wrappee.orderBy(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11)
//    }
//
//    @Support
//    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> orderBy(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>, field5: Field<T5>, field6: Field<T6>, field7: Field<T7>, field8: Field<T8>, field9: Field<T9>, field10: Field<T10>, field11: Field<T11>, field12: Field<T12>): SelectSeekStep12<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> {
//        return wrappee.orderBy(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12)
//    }
//
//    @Support
//    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> orderBy(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>, field5: Field<T5>, field6: Field<T6>, field7: Field<T7>, field8: Field<T8>, field9: Field<T9>, field10: Field<T10>, field11: Field<T11>, field12: Field<T12>, field13: Field<T13>): SelectSeekStep13<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> {
//        return wrappee.orderBy(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13)
//    }
//
//    @Support
//    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> orderBy(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>, field5: Field<T5>, field6: Field<T6>, field7: Field<T7>, field8: Field<T8>, field9: Field<T9>, field10: Field<T10>, field11: Field<T11>, field12: Field<T12>, field13: Field<T13>, field14: Field<T14>): SelectSeekStep14<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> {
//        return wrappee.orderBy(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14)
//    }
//
//    @Support
//    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> orderBy(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>, field5: Field<T5>, field6: Field<T6>, field7: Field<T7>, field8: Field<T8>, field9: Field<T9>, field10: Field<T10>, field11: Field<T11>, field12: Field<T12>, field13: Field<T13>, field14: Field<T14>, field15: Field<T15>): SelectSeekStep15<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> {
//        return wrappee.orderBy(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15)
//    }
//
//    @Support
//    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> orderBy(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>, field5: Field<T5>, field6: Field<T6>, field7: Field<T7>, field8: Field<T8>, field9: Field<T9>, field10: Field<T10>, field11: Field<T11>, field12: Field<T12>, field13: Field<T13>, field14: Field<T14>, field15: Field<T15>, field16: Field<T16>): SelectSeekStep16<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> {
//        return wrappee.orderBy(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16)
//    }
//
//    @Support
//    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> orderBy(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>, field5: Field<T5>, field6: Field<T6>, field7: Field<T7>, field8: Field<T8>, field9: Field<T9>, field10: Field<T10>, field11: Field<T11>, field12: Field<T12>, field13: Field<T13>, field14: Field<T14>, field15: Field<T15>, field16: Field<T16>, field17: Field<T17>): SelectSeekStep17<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> {
//        return wrappee.orderBy(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17)
//    }
//
//    @Support
//    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> orderBy(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>, field5: Field<T5>, field6: Field<T6>, field7: Field<T7>, field8: Field<T8>, field9: Field<T9>, field10: Field<T10>, field11: Field<T11>, field12: Field<T12>, field13: Field<T13>, field14: Field<T14>, field15: Field<T15>, field16: Field<T16>, field17: Field<T17>, field18: Field<T18>): SelectSeekStep18<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> {
//        return wrappee.orderBy(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18)
//    }
//
//    @Support
//    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> orderBy(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>, field5: Field<T5>, field6: Field<T6>, field7: Field<T7>, field8: Field<T8>, field9: Field<T9>, field10: Field<T10>, field11: Field<T11>, field12: Field<T12>, field13: Field<T13>, field14: Field<T14>, field15: Field<T15>, field16: Field<T16>, field17: Field<T17>, field18: Field<T18>, field19: Field<T19>): SelectSeekStep19<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> {
//        return wrappee.orderBy(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19)
//    }
//
//    @Support
//    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> orderBy(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>, field5: Field<T5>, field6: Field<T6>, field7: Field<T7>, field8: Field<T8>, field9: Field<T9>, field10: Field<T10>, field11: Field<T11>, field12: Field<T12>, field13: Field<T13>, field14: Field<T14>, field15: Field<T15>, field16: Field<T16>, field17: Field<T17>, field18: Field<T18>, field19: Field<T19>, field20: Field<T20>): SelectSeekStep20<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> {
//        return wrappee.orderBy(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20)
//    }
//
//    @Support
//    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> orderBy(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>, field5: Field<T5>, field6: Field<T6>, field7: Field<T7>, field8: Field<T8>, field9: Field<T9>, field10: Field<T10>, field11: Field<T11>, field12: Field<T12>, field13: Field<T13>, field14: Field<T14>, field15: Field<T15>, field16: Field<T16>, field17: Field<T17>, field18: Field<T18>, field19: Field<T19>, field20: Field<T20>, field21: Field<T21>): SelectSeekStep21<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> {
//        return wrappee.orderBy(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21)
//    }
//
//    @Support
//    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> orderBy(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>, field5: Field<T5>, field6: Field<T6>, field7: Field<T7>, field8: Field<T8>, field9: Field<T9>, field10: Field<T10>, field11: Field<T11>, field12: Field<T12>, field13: Field<T13>, field14: Field<T14>, field15: Field<T15>, field16: Field<T16>, field17: Field<T17>, field18: Field<T18>, field19: Field<T19>, field20: Field<T20>, field21: Field<T21>, field22: Field<T22>): SelectSeekStep22<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> {
//        return wrappee.orderBy(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21, field22)
//    }
//
//    @Support
//    override fun orderBy(fields: Array<Field<*>>): SelectSeekStepN<R> {
//        return wrappee.orderBy(*fields)
//    }
//
//    @Support
//    override fun <T1> orderBy(field1: SortField<T1>): SelectSeekStep1<R, T1> {
//        return wrappee.orderBy(field1)
//    }
//
//    @Support
//    override fun <T1, T2> orderBy(field1: SortField<T1>, field2: SortField<T2>): SelectSeekStep2<R, T1, T2> {
//        return wrappee.orderBy(field1, field2)
//    }
//
//    @Support
//    override fun <T1, T2, T3> orderBy(field1: SortField<T1>, field2: SortField<T2>, field3: SortField<T3>): SelectSeekStep3<R, T1, T2, T3> {
//        return wrappee.orderBy(field1, field2, field3)
//    }
//
//    @Support
//    override fun <T1, T2, T3, T4> orderBy(field1: SortField<T1>, field2: SortField<T2>, field3: SortField<T3>, field4: SortField<T4>): SelectSeekStep4<R, T1, T2, T3, T4> {
//        return wrappee.orderBy(field1, field2, field3, field4)
//    }
//
//    @Support
//    override fun <T1, T2, T3, T4, T5> orderBy(field1: SortField<T1>, field2: SortField<T2>, field3: SortField<T3>, field4: SortField<T4>, field5: SortField<T5>): SelectSeekStep5<R, T1, T2, T3, T4, T5> {
//        return wrappee.orderBy(field1, field2, field3, field4, field5)
//    }
//
//    @Support
//    override fun <T1, T2, T3, T4, T5, T6> orderBy(field1: SortField<T1>, field2: SortField<T2>, field3: SortField<T3>, field4: SortField<T4>, field5: SortField<T5>, field6: SortField<T6>): SelectSeekStep6<R, T1, T2, T3, T4, T5, T6> {
//        return wrappee.orderBy(field1, field2, field3, field4, field5, field6)
//    }
//
//    @Support
//    override fun <T1, T2, T3, T4, T5, T6, T7> orderBy(field1: SortField<T1>, field2: SortField<T2>, field3: SortField<T3>, field4: SortField<T4>, field5: SortField<T5>, field6: SortField<T6>, field7: SortField<T7>): SelectSeekStep7<R, T1, T2, T3, T4, T5, T6, T7> {
//        return wrappee.orderBy(field1, field2, field3, field4, field5, field6, field7)
//    }
//
//    @Support
//    override fun <T1, T2, T3, T4, T5, T6, T7, T8> orderBy(field1: SortField<T1>, field2: SortField<T2>, field3: SortField<T3>, field4: SortField<T4>, field5: SortField<T5>, field6: SortField<T6>, field7: SortField<T7>, field8: SortField<T8>): SelectSeekStep8<R, T1, T2, T3, T4, T5, T6, T7, T8> {
//        return wrappee.orderBy(field1, field2, field3, field4, field5, field6, field7, field8)
//    }
//
//    @Support
//    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9> orderBy(field1: SortField<T1>, field2: SortField<T2>, field3: SortField<T3>, field4: SortField<T4>, field5: SortField<T5>, field6: SortField<T6>, field7: SortField<T7>, field8: SortField<T8>, field9: SortField<T9>): SelectSeekStep9<R, T1, T2, T3, T4, T5, T6, T7, T8, T9> {
//        return wrappee.orderBy(field1, field2, field3, field4, field5, field6, field7, field8, field9)
//    }
//
//    @Support
//    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> orderBy(field1: SortField<T1>, field2: SortField<T2>, field3: SortField<T3>, field4: SortField<T4>, field5: SortField<T5>, field6: SortField<T6>, field7: SortField<T7>, field8: SortField<T8>, field9: SortField<T9>, field10: SortField<T10>): SelectSeekStep10<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> {
//        return wrappee.orderBy(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10)
//    }
//
//    @Support
//    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> orderBy(field1: SortField<T1>, field2: SortField<T2>, field3: SortField<T3>, field4: SortField<T4>, field5: SortField<T5>, field6: SortField<T6>, field7: SortField<T7>, field8: SortField<T8>, field9: SortField<T9>, field10: SortField<T10>, field11: SortField<T11>): SelectSeekStep11<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> {
//        return wrappee.orderBy(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11)
//    }
//
//    @Support
//    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> orderBy(field1: SortField<T1>, field2: SortField<T2>, field3: SortField<T3>, field4: SortField<T4>, field5: SortField<T5>, field6: SortField<T6>, field7: SortField<T7>, field8: SortField<T8>, field9: SortField<T9>, field10: SortField<T10>, field11: SortField<T11>, field12: SortField<T12>): SelectSeekStep12<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> {
//        return wrappee.orderBy(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12)
//    }
//
//    @Support
//    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> orderBy(field1: SortField<T1>, field2: SortField<T2>, field3: SortField<T3>, field4: SortField<T4>, field5: SortField<T5>, field6: SortField<T6>, field7: SortField<T7>, field8: SortField<T8>, field9: SortField<T9>, field10: SortField<T10>, field11: SortField<T11>, field12: SortField<T12>, field13: SortField<T13>): SelectSeekStep13<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> {
//        return wrappee.orderBy(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13)
//    }
//
//    @Support
//    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> orderBy(field1: SortField<T1>, field2: SortField<T2>, field3: SortField<T3>, field4: SortField<T4>, field5: SortField<T5>, field6: SortField<T6>, field7: SortField<T7>, field8: SortField<T8>, field9: SortField<T9>, field10: SortField<T10>, field11: SortField<T11>, field12: SortField<T12>, field13: SortField<T13>, field14: SortField<T14>): SelectSeekStep14<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> {
//        return wrappee.orderBy(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14)
//    }
//
//    @Support
//    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> orderBy(field1: SortField<T1>, field2: SortField<T2>, field3: SortField<T3>, field4: SortField<T4>, field5: SortField<T5>, field6: SortField<T6>, field7: SortField<T7>, field8: SortField<T8>, field9: SortField<T9>, field10: SortField<T10>, field11: SortField<T11>, field12: SortField<T12>, field13: SortField<T13>, field14: SortField<T14>, field15: SortField<T15>): SelectSeekStep15<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> {
//        return wrappee.orderBy(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15)
//    }
//
//    @Support
//    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> orderBy(field1: SortField<T1>, field2: SortField<T2>, field3: SortField<T3>, field4: SortField<T4>, field5: SortField<T5>, field6: SortField<T6>, field7: SortField<T7>, field8: SortField<T8>, field9: SortField<T9>, field10: SortField<T10>, field11: SortField<T11>, field12: SortField<T12>, field13: SortField<T13>, field14: SortField<T14>, field15: SortField<T15>, field16: SortField<T16>): SelectSeekStep16<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> {
//        return wrappee.orderBy(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16)
//    }
//
//    @Support
//    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> orderBy(field1: SortField<T1>, field2: SortField<T2>, field3: SortField<T3>, field4: SortField<T4>, field5: SortField<T5>, field6: SortField<T6>, field7: SortField<T7>, field8: SortField<T8>, field9: SortField<T9>, field10: SortField<T10>, field11: SortField<T11>, field12: SortField<T12>, field13: SortField<T13>, field14: SortField<T14>, field15: SortField<T15>, field16: SortField<T16>, field17: SortField<T17>): SelectSeekStep17<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> {
//        return wrappee.orderBy(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17)
//    }
//
//    @Support
//    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> orderBy(field1: SortField<T1>, field2: SortField<T2>, field3: SortField<T3>, field4: SortField<T4>, field5: SortField<T5>, field6: SortField<T6>, field7: SortField<T7>, field8: SortField<T8>, field9: SortField<T9>, field10: SortField<T10>, field11: SortField<T11>, field12: SortField<T12>, field13: SortField<T13>, field14: SortField<T14>, field15: SortField<T15>, field16: SortField<T16>, field17: SortField<T17>, field18: SortField<T18>): SelectSeekStep18<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> {
//        return wrappee.orderBy(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18)
//    }
//
//    @Support
//    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> orderBy(field1: SortField<T1>, field2: SortField<T2>, field3: SortField<T3>, field4: SortField<T4>, field5: SortField<T5>, field6: SortField<T6>, field7: SortField<T7>, field8: SortField<T8>, field9: SortField<T9>, field10: SortField<T10>, field11: SortField<T11>, field12: SortField<T12>, field13: SortField<T13>, field14: SortField<T14>, field15: SortField<T15>, field16: SortField<T16>, field17: SortField<T17>, field18: SortField<T18>, field19: SortField<T19>): SelectSeekStep19<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> {
//        return wrappee.orderBy(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19)
//    }
//
//    @Support
//    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> orderBy(field1: SortField<T1>, field2: SortField<T2>, field3: SortField<T3>, field4: SortField<T4>, field5: SortField<T5>, field6: SortField<T6>, field7: SortField<T7>, field8: SortField<T8>, field9: SortField<T9>, field10: SortField<T10>, field11: SortField<T11>, field12: SortField<T12>, field13: SortField<T13>, field14: SortField<T14>, field15: SortField<T15>, field16: SortField<T16>, field17: SortField<T17>, field18: SortField<T18>, field19: SortField<T19>, field20: SortField<T20>): SelectSeekStep20<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> {
//        return wrappee.orderBy(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20)
//    }
//
//    @Support
//    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> orderBy(field1: SortField<T1>, field2: SortField<T2>, field3: SortField<T3>, field4: SortField<T4>, field5: SortField<T5>, field6: SortField<T6>, field7: SortField<T7>, field8: SortField<T8>, field9: SortField<T9>, field10: SortField<T10>, field11: SortField<T11>, field12: SortField<T12>, field13: SortField<T13>, field14: SortField<T14>, field15: SortField<T15>, field16: SortField<T16>, field17: SortField<T17>, field18: SortField<T18>, field19: SortField<T19>, field20: SortField<T20>, field21: SortField<T21>): SelectSeekStep21<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> {
//        return wrappee.orderBy(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21)
//    }
//
//    @Support
//    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> orderBy(field1: SortField<T1>, field2: SortField<T2>, field3: SortField<T3>, field4: SortField<T4>, field5: SortField<T5>, field6: SortField<T6>, field7: SortField<T7>, field8: SortField<T8>, field9: SortField<T9>, field10: SortField<T10>, field11: SortField<T11>, field12: SortField<T12>, field13: SortField<T13>, field14: SortField<T14>, field15: SortField<T15>, field16: SortField<T16>, field17: SortField<T17>, field18: SortField<T18>, field19: SortField<T19>, field20: SortField<T20>, field21: SortField<T21>, field22: SortField<T22>): SelectSeekStep22<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> {
//        return wrappee.orderBy(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21, field22)
//    }
//
//    @Support
//    override fun orderBy(fields: Array<SortField<*>>): SelectSeekStepN<R> {
//        return wrappee.orderBy(*fields)
//    }
//
//    @Support
//    override fun orderBy(fields: Collection<SortField<*>>): SelectSeekStepN<R> {
//        return wrappee.orderBy(fields)
//    }
//
//    @Support
//    override fun orderBy(vararg fieldIndexes: Int): SelectLimitStep<R> {
//        return wrappee.orderBy(*fieldIndexes)
//    }
//
//    @Support(SQLDialect.CUBRID)
//    override fun orderSiblingsBy(fields: Array<Field<*>>): SelectLimitStep<R> {
//        return wrappee.orderSiblingsBy(*fields)
//    }
//
//    @Support(SQLDialect.CUBRID)
//    override fun orderSiblingsBy(fields: Array<SortField<*>>): SelectLimitStep<R> {
//        return wrappee.orderSiblingsBy(*fields)
//    }
//
//    @Support(SQLDialect.CUBRID)
//    override fun orderSiblingsBy(fields: Collection<SortField<*>>): SelectLimitStep<R> {
//        return wrappee.orderSiblingsBy(fields)
//    }
//
//    @Support(SQLDialect.CUBRID)
//    override fun orderSiblingsBy(vararg fieldIndexes: Int): SelectLimitStep<R> {
//        return wrappee.orderSiblingsBy(*fieldIndexes)
//    }
//
//    @Support
//    override fun limit(numberOfRows: Int): SelectOffsetStep<R> {
//        return wrappee.limit(numberOfRows)
//    }
//
//    @Support(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE)
//    override fun limit(numberOfRows: Param<Int>): SelectOffsetStep<R> {
//        return wrappee.limit(numberOfRows)
//    }
//
//    @Support(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE)
//    override fun limit(offset: Int, numberOfRows: Int): SelectForUpdateStep<R> {
//        return wrappee.limit(offset, numberOfRows)
//    }
//
//    @Support(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE)
//    override fun limit(offset: Int, numberOfRows: Param<Int>): SelectForUpdateStep<R> {
//        return wrappee.limit(offset, numberOfRows)
//    }
//
//    @Support(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE)
//    override fun limit(offset: Param<Int>, numberOfRows: Int): SelectForUpdateStep<R> {
//        return wrappee.limit(offset, numberOfRows)
//    }
//
//    @Support(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE)
//    override fun limit(offset: Param<Int>, numberOfRows: Param<Int>): SelectForUpdateStep<R> {
//        return wrappee.limit(offset, numberOfRows)
//    }
//
//    @Support(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE)
//    override fun offset(offset: Int): SelectLimitAfterOffsetStep<R> {
//        return wrappee.offset(offset)
//    }
//
//    @Support(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE)
//    override fun offset(offset: Param<Int>): SelectLimitAfterOffsetStep<R> {
//        return wrappee.offset(offset)
//    }
//
//    @Support(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES)
//    override fun forUpdate(): SelectForUpdateOfStep<R> {
//        return wrappee.forUpdate()
//    }
//
//    @Support(SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES)
//    override fun forShare(): SelectOptionStep<R> {
//        return wrappee.forShare()
//    }
//
//    @Support
//    override fun option(string: String): SelectUnionStep<R> {
//        return wrappee.option(string)
//    }
//
//    @Support
//    override fun union(select: Select<out R>): SelectOrderByStep<R> {
//        return wrappee.union(select)
//    }
//
//    @Support
//    override fun unionAll(select: Select<out R>): SelectOrderByStep<R> {
//        return wrappee.unionAll(select)
//    }
//
//    @Support(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE)
//    override fun except(select: Select<out R>): SelectOrderByStep<R> {
//        return wrappee.except(select)
//    }
//
//    @Support(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.POSTGRES)
//    override fun exceptAll(select: Select<out R>): SelectOrderByStep<R> {
//        return wrappee.exceptAll(select)
//    }
//
//    @Support(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE)
//    override fun intersect(select: Select<out R>): SelectOrderByStep<R> {
//        return wrappee.intersect(select)
//    }
//
//    @Support(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.POSTGRES)
//    override fun intersectAll(select: Select<out R>): SelectOrderByStep<R> {
//        return wrappee.intersectAll(select)
//    }
//
//    override fun getQuery(): SelectQuery<R> {
//        return wrappee.query
//    }
//
//    override fun getSelect(): List<Field<*>> {
//        return wrappee.select
//    }
//
//    @Deprecated("")
//    @Throws(DataAccessException::class)
//    override fun fetchCount(): Int {
//        return wrappee.fetchCount()
//    }
//
//    override fun getResult(): Result<R> {
//        return wrappee.result
//    }
//
//    @Throws(DataAccessException::class)
//    override fun fetch(): Result<R> {
//        return wrappee.fetch()
//    }
//
//    @Throws(DataAccessException::class)
//    override fun fetchResultSet(): ResultSet {
//        return wrappee.fetchResultSet()
//    }
//
//    @Throws(DataAccessException::class)
//    override fun iterator(): Iterator<R> {
//        return wrappee.iterator()
//    }
//
//    @Throws(DataAccessException::class)
//    override fun fetchStream(): Stream<R> {
//        return wrappee.fetchStream()
//    }
//
//    @Throws(DataAccessException::class)
//    override fun stream(): Stream<R> {
//        return wrappee.stream()
//    }
//
//    @Throws(DataAccessException::class)
//    override fun fetchLazy(): Cursor<R> {
//        return wrappee.fetchLazy()
//    }
//
//    @Deprecated("")
//    @Throws(DataAccessException::class)
//    override fun fetchLazy(fetchSize: Int): Cursor<R> {
//        return wrappee.fetchLazy(fetchSize)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun fetchMany(): Results {
//        return wrappee.fetchMany()
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <T> fetch(field: Field<T>): List<T> {
//        return wrappee.fetch(field)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <T> fetch(field: Field<*>, type: Class<out T>): List<T> {
//        return wrappee.fetch(field, type)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <T, U> fetch(field: Field<T>, converter: Converter<in T, U>): List<U> {
//        return wrappee.fetch(field, converter)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun fetch(fieldIndex: Int): List<*> {
//        return wrappee.fetch(fieldIndex)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <T> fetch(fieldIndex: Int, type: Class<out T>): List<T> {
//        return wrappee.fetch(fieldIndex, type)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <U> fetch(fieldIndex: Int, converter: Converter<*, U>): List<U> {
//        return wrappee.fetch(fieldIndex, converter)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun fetch(fieldName: String): List<*> {
//        return wrappee.fetch(fieldName)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <T> fetch(fieldName: String, type: Class<out T>): List<T> {
//        return wrappee.fetch(fieldName, type)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <U> fetch(fieldName: String, converter: Converter<*, U>): List<U> {
//        return wrappee.fetch(fieldName, converter)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun fetch(fieldName: Name): List<*> {
//        return wrappee.fetch(fieldName)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <T> fetch(fieldName: Name, type: Class<out T>): List<T> {
//        return wrappee.fetch(fieldName, type)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <U> fetch(fieldName: Name, converter: Converter<*, U>): List<U> {
//        return wrappee.fetch(fieldName, converter)
//    }
//
//    @Throws(DataAccessException::class, TooManyRowsException::class)
//    override fun <T> fetchOne(field: Field<T>): T {
//        return wrappee.fetchOne(field)
//    }
//
//    @Throws(DataAccessException::class, TooManyRowsException::class)
//    override fun <T> fetchOne(field: Field<*>, type: Class<out T>): T {
//        return wrappee.fetchOne(field, type)
//    }
//
//    @Throws(DataAccessException::class, TooManyRowsException::class)
//    override fun <T, U> fetchOne(field: Field<T>, converter: Converter<in T, U>): U {
//        return wrappee.fetchOne(field, converter)
//    }
//
//    @Throws(DataAccessException::class, TooManyRowsException::class)
//    override fun fetchOne(fieldIndex: Int): Any {
//        return wrappee.fetchOne(fieldIndex)
//    }
//
//    @Throws(DataAccessException::class, TooManyRowsException::class)
//    override fun <T> fetchOne(fieldIndex: Int, type: Class<out T>): T {
//        return wrappee.fetchOne(fieldIndex, type)
//    }
//
//    @Throws(DataAccessException::class, TooManyRowsException::class)
//    override fun <U> fetchOne(fieldIndex: Int, converter: Converter<*, U>): U {
//        return wrappee.fetchOne(fieldIndex, converter)
//    }
//
//    @Throws(DataAccessException::class, TooManyRowsException::class)
//    override fun fetchOne(fieldName: String): Any {
//        return wrappee.fetchOne(fieldName)
//    }
//
//    @Throws(DataAccessException::class, TooManyRowsException::class)
//    override fun <T> fetchOne(fieldName: String, type: Class<out T>): T {
//        return wrappee.fetchOne(fieldName, type)
//    }
//
//    @Throws(DataAccessException::class, TooManyRowsException::class)
//    override fun <U> fetchOne(fieldName: String, converter: Converter<*, U>): U {
//        return wrappee.fetchOne(fieldName, converter)
//    }
//
//    @Throws(DataAccessException::class, TooManyRowsException::class)
//    override fun fetchOne(fieldName: Name): Any {
//        return wrappee.fetchOne(fieldName)
//    }
//
//    @Throws(DataAccessException::class, TooManyRowsException::class)
//    override fun <T> fetchOne(fieldName: Name, type: Class<out T>): T {
//        return wrappee.fetchOne(fieldName, type)
//    }
//
//    @Throws(DataAccessException::class, TooManyRowsException::class)
//    override fun <U> fetchOne(fieldName: Name, converter: Converter<*, U>): U {
//        return wrappee.fetchOne(fieldName, converter)
//    }
//
//    @Throws(DataAccessException::class, TooManyRowsException::class)
//    override fun fetchOne(): R {
//        return wrappee.fetchOne()
//    }
//
//    @Throws(DataAccessException::class, TooManyRowsException::class)
//    override fun <E> fetchOne(mapper: RecordMapper<in R, E>): E {
//        return wrappee.fetchOne(mapper)
//    }
//
//    @Throws(DataAccessException::class, TooManyRowsException::class)
//    override fun fetchOneMap(): Map<String, Any> {
//        return wrappee.fetchOneMap()
//    }
//
//    @Throws(DataAccessException::class, TooManyRowsException::class)
//    override fun fetchOneArray(): Array<Any> {
//        return wrappee.fetchOneArray()
//    }
//
//    @Throws(DataAccessException::class, MappingException::class, TooManyRowsException::class)
//    override fun <E> fetchOneInto(type: Class<out E>): E {
//        return wrappee.fetchOneInto(type)
//    }
//
//    @Throws(DataAccessException::class, TooManyRowsException::class)
//    override fun <Z : Record> fetchOneInto(table: Table<Z>): Z {
//        return wrappee.fetchOneInto(table)
//    }
//
//    @Throws(DataAccessException::class, TooManyRowsException::class)
//    override fun <T> fetchOptional(field: Field<T>): Optional<T> {
//        return wrappee.fetchOptional(field)
//    }
//
//    @Throws(DataAccessException::class, TooManyRowsException::class)
//    override fun <T> fetchOptional(field: Field<*>, type: Class<out T>): Optional<T> {
//        return wrappee.fetchOptional(field, type)
//    }
//
//    @Throws(DataAccessException::class, TooManyRowsException::class)
//    override fun <T, U> fetchOptional(field: Field<T>, converter: Converter<in T, U>): Optional<U> {
//        return wrappee.fetchOptional(field, converter)
//    }
//
//    @Throws(DataAccessException::class, TooManyRowsException::class)
//    override fun fetchOptional(fieldIndex: Int): Optional<*> {
//        return wrappee.fetchOptional(fieldIndex)
//    }
//
//    @Throws(DataAccessException::class, TooManyRowsException::class)
//    override fun <T> fetchOptional(fieldIndex: Int, type: Class<out T>): Optional<T> {
//        return wrappee.fetchOptional(fieldIndex, type)
//    }
//
//    @Throws(DataAccessException::class, TooManyRowsException::class)
//    override fun <U> fetchOptional(fieldIndex: Int, converter: Converter<*, U>): Optional<U> {
//        return wrappee.fetchOptional(fieldIndex, converter)
//    }
//
//    @Throws(DataAccessException::class, TooManyRowsException::class)
//    override fun fetchOptional(fieldName: String): Optional<*> {
//        return wrappee.fetchOptional(fieldName)
//    }
//
//    @Throws(DataAccessException::class, TooManyRowsException::class)
//    override fun <T> fetchOptional(fieldName: String, type: Class<out T>): Optional<T> {
//        return wrappee.fetchOptional(fieldName, type)
//    }
//
//    @Throws(DataAccessException::class, TooManyRowsException::class)
//    override fun <U> fetchOptional(fieldName: String, converter: Converter<*, U>): Optional<U> {
//        return wrappee.fetchOptional(fieldName, converter)
//    }
//
//    @Throws(DataAccessException::class, TooManyRowsException::class)
//    override fun fetchOptional(fieldName: Name): Optional<*> {
//        return wrappee.fetchOptional(fieldName)
//    }
//
//    @Throws(DataAccessException::class, TooManyRowsException::class)
//    override fun <T> fetchOptional(fieldName: Name, type: Class<out T>): Optional<T> {
//        return wrappee.fetchOptional(fieldName, type)
//    }
//
//    @Throws(DataAccessException::class, TooManyRowsException::class)
//    override fun <U> fetchOptional(fieldName: Name, converter: Converter<*, U>): Optional<U> {
//        return wrappee.fetchOptional(fieldName, converter)
//    }
//
//    @Throws(DataAccessException::class, TooManyRowsException::class)
//    override fun fetchOptional(): Optional<R> {
//        return wrappee.fetchOptional()
//    }
//
//    @Throws(DataAccessException::class, TooManyRowsException::class)
//    override fun <E> fetchOptional(mapper: RecordMapper<in R, E>): Optional<E> {
//        return wrappee.fetchOptional(mapper)
//    }
//
//    @Throws(DataAccessException::class, TooManyRowsException::class)
//    override fun fetchOptionalMap(): Optional<Map<String, Any>> {
//        return wrappee.fetchOptionalMap()
//    }
//
//    @Throws(DataAccessException::class, TooManyRowsException::class)
//    override fun fetchOptionalArray(): Optional<Array<Any>> {
//        return wrappee.fetchOptionalArray()
//    }
//
//    @Throws(DataAccessException::class, MappingException::class, TooManyRowsException::class)
//    override fun <E> fetchOptionalInto(type: Class<out E>): Optional<E> {
//        return wrappee.fetchOptionalInto(type)
//    }
//
//    @Throws(DataAccessException::class, TooManyRowsException::class)
//    override fun <Z : Record> fetchOptionalInto(table: Table<Z>): Optional<Z> {
//        return wrappee.fetchOptionalInto(table)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <T> fetchAny(field: Field<T>): T {
//        return wrappee.fetchAny(field)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <T> fetchAny(field: Field<*>, type: Class<out T>): T {
//        return wrappee.fetchAny(field, type)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <T, U> fetchAny(field: Field<T>, converter: Converter<in T, U>): U {
//        return wrappee.fetchAny(field, converter)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun fetchAny(fieldIndex: Int): Any {
//        return wrappee.fetchAny(fieldIndex)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <T> fetchAny(fieldIndex: Int, type: Class<out T>): T {
//        return wrappee.fetchAny(fieldIndex, type)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <U> fetchAny(fieldIndex: Int, converter: Converter<*, U>): U {
//        return wrappee.fetchAny(fieldIndex, converter)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun fetchAny(fieldName: String): Any {
//        return wrappee.fetchAny(fieldName)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <T> fetchAny(fieldName: String, type: Class<out T>): T {
//        return wrappee.fetchAny(fieldName, type)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <U> fetchAny(fieldName: String, converter: Converter<*, U>): U {
//        return wrappee.fetchAny(fieldName, converter)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun fetchAny(fieldName: Name): Any {
//        return wrappee.fetchAny(fieldName)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <T> fetchAny(fieldName: Name, type: Class<out T>): T {
//        return wrappee.fetchAny(fieldName, type)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <U> fetchAny(fieldName: Name, converter: Converter<*, U>): U {
//        return wrappee.fetchAny(fieldName, converter)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun fetchAny(): R {
//        return wrappee.fetchAny()
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <E> fetchAny(mapper: RecordMapper<in R, E>): E {
//        return wrappee.fetchAny(mapper)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun fetchAnyMap(): Map<String, Any> {
//        return wrappee.fetchAnyMap()
//    }
//
//    @Throws(DataAccessException::class)
//    override fun fetchAnyArray(): Array<Any> {
//        return wrappee.fetchAnyArray()
//    }
//
//    @Throws(DataAccessException::class, MappingException::class)
//    override fun <E> fetchAnyInto(type: Class<out E>): E {
//        return wrappee.fetchAnyInto(type)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <Z : Record> fetchAnyInto(table: Table<Z>): Z {
//        return wrappee.fetchAnyInto(table)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun fetchMaps(): List<Map<String, Any>> {
//        return wrappee.fetchMaps()
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <K> fetchMap(key: Field<K>): Map<K, R> {
//        return wrappee.fetchMap(key)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun fetchMap(keyFieldIndex: Int): Map<*, R> {
//        return wrappee.fetchMap(keyFieldIndex)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun fetchMap(keyFieldName: String): Map<*, R> {
//        return wrappee.fetchMap(keyFieldName)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun fetchMap(keyFieldName: Name): Map<*, R> {
//        return wrappee.fetchMap(keyFieldName)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <K, V> fetchMap(key: Field<K>, value: Field<V>): Map<K, V> {
//        return wrappee.fetchMap(key, value)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun fetchMap(keyFieldIndex: Int, valueFieldIndex: Int): Map<*, *> {
//        return wrappee.fetchMap(keyFieldIndex, valueFieldIndex)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun fetchMap(keyFieldName: String, valueFieldName: String): Map<*, *> {
//        return wrappee.fetchMap(keyFieldName, valueFieldName)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun fetchMap(keyFieldName: Name, valueFieldName: Name): Map<*, *> {
//        return wrappee.fetchMap(keyFieldName, valueFieldName)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun fetchMap(keys: Array<Field<*>>): Map<Record, R> {
//        return wrappee.fetchMap(keys)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun fetchMap(keyFieldIndexes: IntArray): Map<Record, R> {
//        return wrappee.fetchMap(keyFieldIndexes)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun fetchMap(keyFieldNames: Array<String>): Map<Record, R> {
//        return wrappee.fetchMap(keyFieldNames)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun fetchMap(keyFieldNames: Array<Name>): Map<Record, R> {
//        return wrappee.fetchMap(keyFieldNames)
//    }
//
//    @Throws(DataAccessException::class, MappingException::class)
//    override fun <E> fetchMap(keys: Array<Field<*>>, type: Class<out E>): Map<List<*>, E> {
//        return wrappee.fetchMap(keys, type)
//    }
//
//    @Throws(DataAccessException::class, MappingException::class)
//    override fun <E> fetchMap(keyFieldIndexes: IntArray, type: Class<out E>): Map<List<*>, E> {
//        return wrappee.fetchMap(keyFieldIndexes, type)
//    }
//
//    @Throws(DataAccessException::class, MappingException::class)
//    override fun <E> fetchMap(keyFieldNames: Array<String>, type: Class<out E>): Map<List<*>, E> {
//        return wrappee.fetchMap(keyFieldNames, type)
//    }
//
//    @Throws(DataAccessException::class, MappingException::class)
//    override fun <E> fetchMap(keyFieldNames: Array<Name>, type: Class<out E>): Map<List<*>, E> {
//        return wrappee.fetchMap(keyFieldNames, type)
//    }
//
//    @Throws(DataAccessException::class, MappingException::class)
//    override fun <E> fetchMap(keys: Array<Field<*>>, mapper: RecordMapper<in R, E>): Map<List<*>, E> {
//        return wrappee.fetchMap(keys, mapper)
//    }
//
//    @Throws(DataAccessException::class, MappingException::class)
//    override fun <E> fetchMap(keyFieldIndexes: IntArray, mapper: RecordMapper<in R, E>): Map<List<*>, E> {
//        return wrappee.fetchMap(keyFieldIndexes, mapper)
//    }
//
//    @Throws(DataAccessException::class, MappingException::class)
//    override fun <E> fetchMap(keyFieldNames: Array<String>, mapper: RecordMapper<in R, E>): Map<List<*>, E> {
//        return wrappee.fetchMap(keyFieldNames, mapper)
//    }
//
//    @Throws(DataAccessException::class, MappingException::class)
//    override fun <E> fetchMap(keyFieldNames: Array<Name>, mapper: RecordMapper<in R, E>): Map<List<*>, E> {
//        return wrappee.fetchMap(keyFieldNames, mapper)
//    }
//
//    @Throws(DataAccessException::class, MappingException::class, InvalidResultException::class)
//    override fun <K> fetchMap(keyType: Class<out K>): Map<K, R> {
//        return wrappee.fetchMap(keyType)
//    }
//
//    @Throws(DataAccessException::class, MappingException::class, InvalidResultException::class)
//    override fun <K, V> fetchMap(keyType: Class<out K>, valueType: Class<out V>): Map<K, V> {
//        return wrappee.fetchMap(keyType, valueType)
//    }
//
//    @Throws(DataAccessException::class, InvalidResultException::class, MappingException::class)
//    override fun <K, V> fetchMap(keyType: Class<out K>, valueMapper: RecordMapper<in R, V>): Map<K, V> {
//        return wrappee.fetchMap(keyType, valueMapper)
//    }
//
//    @Throws(DataAccessException::class, InvalidResultException::class, MappingException::class)
//    override fun <K> fetchMap(keyMapper: RecordMapper<in R, K>): Map<K, R> {
//        return wrappee.fetchMap(keyMapper)
//    }
//
//    @Throws(DataAccessException::class, InvalidResultException::class, MappingException::class)
//    override fun <K, V> fetchMap(keyMapper: RecordMapper<in R, K>, valueType: Class<V>): Map<K, V> {
//        return wrappee.fetchMap(keyMapper, valueType)
//    }
//
//    @Throws(DataAccessException::class, InvalidResultException::class, MappingException::class)
//    override fun <K, V> fetchMap(keyMapper: RecordMapper<in R, K>, valueMapper: RecordMapper<in R, V>): Map<K, V> {
//        return wrappee.fetchMap(keyMapper, valueMapper)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <S : Record> fetchMap(table: Table<S>): Map<S, R> {
//        return wrappee.fetchMap(table)
//    }
//
//    @Throws(DataAccessException::class, MappingException::class)
//    override fun <E, S : Record> fetchMap(table: Table<S>, type: Class<out E>): Map<S, E> {
//        return wrappee.fetchMap(table, type)
//    }
//
//    @Throws(DataAccessException::class, MappingException::class)
//    override fun <E, S : Record> fetchMap(table: Table<S>, mapper: RecordMapper<in R, E>): Map<S, E> {
//        return wrappee.fetchMap(table, mapper)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <K, E> fetchMap(key: Field<K>, type: Class<out E>): Map<K, E> {
//        return wrappee.fetchMap(key, type)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <E> fetchMap(keyFieldIndex: Int, type: Class<out E>): Map<*, E> {
//        return wrappee.fetchMap(keyFieldIndex, type)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <E> fetchMap(keyFieldName: String, type: Class<out E>): Map<*, E> {
//        return wrappee.fetchMap(keyFieldName, type)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <E> fetchMap(keyFieldName: Name, type: Class<out E>): Map<*, E> {
//        return wrappee.fetchMap(keyFieldName, type)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <K, E> fetchMap(key: Field<K>, mapper: RecordMapper<in R, E>): Map<K, E> {
//        return wrappee.fetchMap(key, mapper)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <E> fetchMap(keyFieldIndex: Int, mapper: RecordMapper<in R, E>): Map<*, E> {
//        return wrappee.fetchMap(keyFieldIndex, mapper)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <E> fetchMap(keyFieldName: String, mapper: RecordMapper<in R, E>): Map<*, E> {
//        return wrappee.fetchMap(keyFieldName, mapper)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <E> fetchMap(keyFieldName: Name, mapper: RecordMapper<in R, E>): Map<*, E> {
//        return wrappee.fetchMap(keyFieldName, mapper)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <K> fetchGroups(key: Field<K>): Map<K, Result<R>> {
//        return wrappee.fetchGroups(key)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun fetchGroups(keyFieldIndex: Int): Map<*, Result<R>> {
//        return wrappee.fetchGroups(keyFieldIndex)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun fetchGroups(keyFieldName: String): Map<*, Result<R>> {
//        return wrappee.fetchGroups(keyFieldName)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun fetchGroups(keyFieldName: Name): Map<*, Result<R>> {
//        return wrappee.fetchGroups(keyFieldName)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <K, V> fetchGroups(key: Field<K>, value: Field<V>): Map<K, List<V>> {
//        return wrappee.fetchGroups(key, value)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun fetchGroups(keyFieldIndex: Int, valueFieldIndex: Int): Map<*, List<*>> {
//        return wrappee.fetchGroups(keyFieldIndex, valueFieldIndex)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun fetchGroups(keyFieldName: String, valueFieldName: String): Map<*, List<*>> {
//        return wrappee.fetchGroups(keyFieldName, valueFieldName)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun fetchGroups(keyFieldName: Name, valueFieldName: Name): Map<*, List<*>> {
//        return wrappee.fetchGroups(keyFieldName, valueFieldName)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun fetchGroups(keys: Array<Field<*>>): Map<Record, Result<R>> {
//        return wrappee.fetchGroups(keys)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun fetchGroups(keyFieldIndexes: IntArray): Map<Record, Result<R>> {
//        return wrappee.fetchGroups(keyFieldIndexes)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun fetchGroups(keyFieldNames: Array<String>): Map<Record, Result<R>> {
//        return wrappee.fetchGroups(keyFieldNames)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun fetchGroups(keyFieldNames: Array<Name>): Map<Record, Result<R>> {
//        return wrappee.fetchGroups(keyFieldNames)
//    }
//
//    @Throws(MappingException::class)
//    override fun <E> fetchGroups(keys: Array<Field<*>>, type: Class<out E>): Map<Record, List<E>> {
//        return wrappee.fetchGroups(keys, type)
//    }
//
//    @Throws(MappingException::class)
//    override fun <E> fetchGroups(keyFieldIndexes: IntArray, type: Class<out E>): Map<Record, List<E>> {
//        return wrappee.fetchGroups(keyFieldIndexes, type)
//    }
//
//    @Throws(MappingException::class)
//    override fun <E> fetchGroups(keyFieldNames: Array<String>, type: Class<out E>): Map<Record, List<E>> {
//        return wrappee.fetchGroups(keyFieldNames, type)
//    }
//
//    @Throws(MappingException::class)
//    override fun <E> fetchGroups(keyFieldNames: Array<Name>, type: Class<out E>): Map<Record, List<E>> {
//        return wrappee.fetchGroups(keyFieldNames, type)
//    }
//
//    @Throws(MappingException::class)
//    override fun <E> fetchGroups(keys: Array<Field<*>>, mapper: RecordMapper<in R, E>): Map<Record, List<E>> {
//        return wrappee.fetchGroups(keys, mapper)
//    }
//
//    @Throws(MappingException::class)
//    override fun <E> fetchGroups(keyFieldIndexes: IntArray, mapper: RecordMapper<in R, E>): Map<Record, List<E>> {
//        return wrappee.fetchGroups(keyFieldIndexes, mapper)
//    }
//
//    @Throws(MappingException::class)
//    override fun <E> fetchGroups(keyFieldNames: Array<String>, mapper: RecordMapper<in R, E>): Map<Record, List<E>> {
//        return wrappee.fetchGroups(keyFieldNames, mapper)
//    }
//
//    @Throws(MappingException::class)
//    override fun <E> fetchGroups(keyFieldNames: Array<Name>, mapper: RecordMapper<in R, E>): Map<Record, List<E>> {
//        return wrappee.fetchGroups(keyFieldNames, mapper)
//    }
//
//    @Throws(MappingException::class)
//    override fun <K> fetchGroups(keyType: Class<out K>): Map<K, Result<R>> {
//        return wrappee.fetchGroups(keyType)
//    }
//
//    @Throws(MappingException::class)
//    override fun <K, V> fetchGroups(keyType: Class<out K>, valueType: Class<out V>): Map<K, List<V>> {
//        return wrappee.fetchGroups(keyType, valueType)
//    }
//
//    @Throws(MappingException::class)
//    override fun <K, V> fetchGroups(keyType: Class<out K>, valueMapper: RecordMapper<in R, V>): Map<K, List<V>> {
//        return wrappee.fetchGroups(keyType, valueMapper)
//    }
//
//    @Throws(MappingException::class)
//    override fun <K> fetchGroups(keyMapper: RecordMapper<in R, K>): Map<K, Result<R>> {
//        return wrappee.fetchGroups(keyMapper)
//    }
//
//    @Throws(MappingException::class)
//    override fun <K, V> fetchGroups(keyMapper: RecordMapper<in R, K>, valueType: Class<V>): Map<K, List<V>> {
//        return wrappee.fetchGroups(keyMapper, valueType)
//    }
//
//    @Throws(MappingException::class)
//    override fun <K, V> fetchGroups(keyMapper: RecordMapper<in R, K>, valueMapper: RecordMapper<in R, V>): Map<K, List<V>> {
//        return wrappee.fetchGroups(keyMapper, valueMapper)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <S : Record> fetchGroups(table: Table<S>): Map<S, Result<R>> {
//        return wrappee.fetchGroups(table)
//    }
//
//    @Throws(DataAccessException::class, MappingException::class)
//    override fun <E, S : Record> fetchGroups(table: Table<S>, type: Class<out E>): Map<S, List<E>> {
//        return wrappee.fetchGroups(table, type)
//    }
//
//    @Throws(DataAccessException::class, MappingException::class)
//    override fun <E, S : Record> fetchGroups(table: Table<S>, mapper: RecordMapper<in R, E>): Map<S, List<E>> {
//        return wrappee.fetchGroups(table, mapper)
//    }
//
//    @Throws(DataAccessException::class, MappingException::class)
//    override fun <K, E> fetchGroups(key: Field<K>, type: Class<out E>): Map<K, List<E>> {
//        return wrappee.fetchGroups(key, type)
//    }
//
//    @Throws(DataAccessException::class, MappingException::class)
//    override fun <E> fetchGroups(keyFieldIndex: Int, type: Class<out E>): Map<*, List<E>> {
//        return wrappee.fetchGroups(keyFieldIndex, type)
//    }
//
//    @Throws(DataAccessException::class, MappingException::class)
//    override fun <E> fetchGroups(keyFieldName: String, type: Class<out E>): Map<*, List<E>> {
//        return wrappee.fetchGroups(keyFieldName, type)
//    }
//
//    @Throws(DataAccessException::class, MappingException::class)
//    override fun <E> fetchGroups(keyFieldName: Name, type: Class<out E>): Map<*, List<E>> {
//        return wrappee.fetchGroups(keyFieldName, type)
//    }
//
//    @Throws(DataAccessException::class, MappingException::class)
//    override fun <K, E> fetchGroups(key: Field<K>, mapper: RecordMapper<in R, E>): Map<K, List<E>> {
//        return wrappee.fetchGroups(key, mapper)
//    }
//
//    @Throws(DataAccessException::class, MappingException::class)
//    override fun <E> fetchGroups(keyFieldIndex: Int, mapper: RecordMapper<in R, E>): Map<*, List<E>> {
//        return wrappee.fetchGroups(keyFieldIndex, mapper)
//    }
//
//    @Throws(DataAccessException::class, MappingException::class)
//    override fun <E> fetchGroups(keyFieldName: String, mapper: RecordMapper<in R, E>): Map<*, List<E>> {
//        return wrappee.fetchGroups(keyFieldName, mapper)
//    }
//
//    @Throws(DataAccessException::class, MappingException::class)
//    override fun <E> fetchGroups(keyFieldName: Name, mapper: RecordMapper<in R, E>): Map<*, List<E>> {
//        return wrappee.fetchGroups(keyFieldName, mapper)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun fetchArrays(): Array<Array<Any>> {
//        return wrappee.fetchArrays()
//    }
//
//    @Throws(DataAccessException::class)
//    override fun fetchArray(): Array<R> {
//        return wrappee.fetchArray()
//    }
//
//    @Throws(DataAccessException::class)
//    override fun fetchArray(fieldIndex: Int): Array<Any> {
//        return wrappee.fetchArray(fieldIndex)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <T> fetchArray(fieldIndex: Int, type: Class<out T>): Array<T> {
//        return wrappee.fetchArray(fieldIndex, type)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <U> fetchArray(fieldIndex: Int, converter: Converter<*, U>): Array<U> {
//        return wrappee.fetchArray(fieldIndex, converter)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun fetchArray(fieldName: String): Array<Any> {
//        return wrappee.fetchArray(fieldName)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <T> fetchArray(fieldName: String, type: Class<out T>): Array<T> {
//        return wrappee.fetchArray(fieldName, type)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <U> fetchArray(fieldName: String, converter: Converter<*, U>): Array<U> {
//        return wrappee.fetchArray(fieldName, converter)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun fetchArray(fieldName: Name): Array<Any> {
//        return wrappee.fetchArray(fieldName)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <T> fetchArray(fieldName: Name, type: Class<out T>): Array<T> {
//        return wrappee.fetchArray(fieldName, type)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <U> fetchArray(fieldName: Name, converter: Converter<*, U>): Array<U> {
//        return wrappee.fetchArray(fieldName, converter)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <T> fetchArray(field: Field<T>): Array<T> {
//        return wrappee.fetchArray(field)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <T> fetchArray(field: Field<*>, type: Class<out T>): Array<T> {
//        return wrappee.fetchArray(field, type)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <T, U> fetchArray(field: Field<T>, converter: Converter<in T, U>): Array<U> {
//        return wrappee.fetchArray(field, converter)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun fetchSet(fieldIndex: Int): Set<*> {
//        return wrappee.fetchSet(fieldIndex)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <T> fetchSet(fieldIndex: Int, type: Class<out T>): Set<T> {
//        return wrappee.fetchSet(fieldIndex, type)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <U> fetchSet(fieldIndex: Int, converter: Converter<*, U>): Set<U> {
//        return wrappee.fetchSet(fieldIndex, converter)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun fetchSet(fieldName: String): Set<*> {
//        return wrappee.fetchSet(fieldName)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <T> fetchSet(fieldName: String, type: Class<out T>): Set<T> {
//        return wrappee.fetchSet(fieldName, type)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <U> fetchSet(fieldName: String, converter: Converter<*, U>): Set<U> {
//        return wrappee.fetchSet(fieldName, converter)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun fetchSet(fieldName: Name): Set<*> {
//        return wrappee.fetchSet(fieldName)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <T> fetchSet(fieldName: Name, type: Class<out T>): Set<T> {
//        return wrappee.fetchSet(fieldName, type)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <U> fetchSet(fieldName: Name, converter: Converter<*, U>): Set<U> {
//        return wrappee.fetchSet(fieldName, converter)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <T> fetchSet(field: Field<T>): Set<T> {
//        return wrappee.fetchSet(field)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <T> fetchSet(field: Field<*>, type: Class<out T>): Set<T> {
//        return wrappee.fetchSet(field, type)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <T, U> fetchSet(field: Field<T>, converter: Converter<in T, U>): Set<U> {
//        return wrappee.fetchSet(field, converter)
//    }
//
//    @Throws(DataAccessException::class, MappingException::class)
//    override fun <E> fetchInto(type: Class<out E>): List<E> {
//        return wrappee.fetchInto(type)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <Z : Record> fetchInto(table: Table<Z>): Result<Z> {
//        return wrappee.fetchInto(table)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <H : RecordHandler<in R>> fetchInto(handler: H): H {
//        return wrappee.fetchInto(handler)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun <E> fetch(mapper: RecordMapper<in R, E>): List<E> {
//        return wrappee.fetch(mapper)
//    }
//
//    override fun fetchAsync(): CompletionStage<Result<R>> {
//        return wrappee.fetchAsync()
//    }
//
//    override fun fetchAsync(executor: Executor): CompletionStage<Result<R>> {
//        return wrappee.fetchAsync(executor)
//    }
//
//    @Deprecated("")
//    @Throws(DataAccessException::class)
//    override fun fetchLater(): FutureResult<R> {
//        return wrappee.fetchLater()
//    }
//
//    @Deprecated("")
//    @Throws(DataAccessException::class)
//    override fun fetchLater(executor: ExecutorService): FutureResult<R> {
//        return wrappee.fetchLater(executor)
//    }
//
//    override fun getRecordType(): Class<out R> {
//        return wrappee.recordType
//    }
//
//    @Throws(IllegalArgumentException::class, DataTypeException::class)
//    override fun bind(param: String, value: Any): ResultQuery<R> {
//        return wrappee.bind(param, value)
//    }
//
//    @Throws(IllegalArgumentException::class, DataTypeException::class)
//    override fun bind(index: Int, value: Any): ResultQuery<R> {
//        return wrappee.bind(index, value)
//    }
//
//    override fun queryTimeout(timeout: Int): ResultQuery<R> {
//        return wrappee.queryTimeout(timeout)
//    }
//
//    override fun keepStatement(keepStatement: Boolean): ResultQuery<R> {
//        return wrappee.keepStatement(keepStatement)
//    }
//
//    override fun maxRows(rows: Int): ResultQuery<R> {
//        return wrappee.maxRows(rows)
//    }
//
//    override fun fetchSize(rows: Int): ResultQuery<R> {
//        return wrappee.fetchSize(rows)
//    }
//
//    override fun resultSetConcurrency(resultSetConcurrency: Int): ResultQuery<R> {
//        return wrappee.resultSetConcurrency(resultSetConcurrency)
//    }
//
//    override fun resultSetType(resultSetType: Int): ResultQuery<R> {
//        return wrappee.resultSetType(resultSetType)
//    }
//
//    override fun resultSetHoldability(resultSetHoldability: Int): ResultQuery<R> {
//        return wrappee.resultSetHoldability(resultSetHoldability)
//    }
//
//    override fun intern(fields: Array<Field<*>>): ResultQuery<R> {
//        return wrappee.intern(*fields)
//    }
//
//    override fun intern(vararg fieldIndexes: Int): ResultQuery<R> {
//        return wrappee.intern(*fieldIndexes)
//    }
//
//    override fun intern(vararg fieldNames: String): ResultQuery<R> {
//        return wrappee.intern(*fieldNames)
//    }
//
//    override fun intern(vararg fieldNames: Name): ResultQuery<R> {
//        return wrappee.intern(*fieldNames)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun execute(): Int {
//        return wrappee.execute()
//    }
//
//    override fun executeAsync(): CompletionStage<Int> {
//        return wrappee.executeAsync()
//    }
//
//    override fun executeAsync(executor: Executor): CompletionStage<Int> {
//        return wrappee.executeAsync(executor)
//    }
//
//    override fun isExecutable(): Boolean {
//        return wrappee.isExecutable
//    }
//
//    override fun getSQL(): String {
//        return wrappee.sql
//    }
//
//    @Deprecated("")
//    override fun getSQL(inline: Boolean): String {
//        return wrappee.getSQL(inline)
//    }
//
//    override fun getSQL(paramType: ParamType): String {
//        return wrappee.getSQL(paramType)
//    }
//
//    override fun getBindValues(): List<Any> {
//        return wrappee.bindValues
//    }
//
//    override fun getParams(): Map<String, Param<*>> {
//        return wrappee.params
//    }
//
//    override fun getParam(name: String): Param<*> {
//        return wrappee.getParam(name)
//    }
//
//    @Throws(DataAccessException::class)
//    override fun close() {
//        wrappee.close()
//    }
//
//    @Throws(DataAccessException::class)
//    override fun cancel() {
//        wrappee.cancel()
//    }
//
//    override fun toString(): String {
//        return wrappee.toString()
//    }
//
//    override fun equals(`object`: Any?): Boolean {
//        return wrappee == `object`
//    }
//
//    override fun hashCode(): Int {
//        return wrappee.hashCode()
//    }
//
//    override fun attach(configuration: Configuration) {
//        wrappee.attach(configuration)
//    }
//
//    override fun detach() {
//        wrappee.detach()
//    }
//
//    override fun forEach(action: Consumer<in R>) {
//        wrappee.forEach(action)
//    }
//
//    override fun spliterator(): Spliterator<R> {
//        return wrappee.spliterator()
//    }
//
//    override fun fieldsRow(): Row {
//        return wrappee.fieldsRow()
//    }
//
//    override fun <T> field(field: Field<T>): Field<T> {
//        return wrappee.field(field)
//    }
//
//    override fun field(name: String): Field<*> {
//        return wrappee.field(name)
//    }
//
//    override fun <T> field(name: String, type: Class<T>): Field<T> {
//        return wrappee.field(name, type)
//    }
//
//    override fun <T> field(name: String, dataType: DataType<T>): Field<T> {
//        return wrappee.field(name, dataType)
//    }
//
//    override fun field(name: Name): Field<*> {
//        return wrappee.field(name)
//    }
//
//    override fun <T> field(name: Name, type: Class<T>): Field<T> {
//        return wrappee.field(name, type)
//    }
//
//    override fun <T> field(name: Name, dataType: DataType<T>): Field<T> {
//        return wrappee.field(name, dataType)
//    }
//
//    override fun field(index: Int): Field<*> {
//        return wrappee.field(index)
//    }
//
//    override fun <T> field(index: Int, type: Class<T>): Field<T> {
//        return wrappee.field(index, type)
//    }
//
//    override fun <T> field(index: Int, dataType: DataType<T>): Field<T> {
//        return wrappee.field(index, dataType)
//    }
//
//    override fun fields(): Array<Field<*>> {
//        return wrappee.fields()
//    }
//
//    override fun fields(fields: Array<Field<*>>): Array<Field<*>> {
//        return wrappee.fields(*fields)
//    }
//
//    override fun fields(vararg fieldNames: String): Array<Field<*>> {
//        return wrappee.fields(*fieldNames)
//    }
//
//    override fun fields(vararg fieldNames: Name): Array<Field<*>> {
//        return wrappee.fields(*fieldNames)
//    }
//
//    override fun fields(vararg fieldIndexes: Int): Array<Field<*>> {
//        return wrappee.fields(*fieldIndexes)
//    }
//
//    @Support
//    override fun asTable(): Table<R> {
//        return wrappee.asTable()
//    }
//
//    @Support
//    override fun asTable(alias: String): Table<R> {
//        return wrappee.asTable(alias)
//    }
//
//    @Support
//    override fun asTable(alias: String, vararg fieldAliases: String): Table<R> {
//        return wrappee.asTable(alias, *fieldAliases)
//    }
//
//    override fun <T> asField(): Field<T> {
//        return wrappee.asField<T>()
//    }
//
//    override fun <T> asField(alias: String): Field<T> {
//        return wrappee.asField<T>(alias)
//    }
//}
