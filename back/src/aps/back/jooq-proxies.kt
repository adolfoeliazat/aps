package aps.back

import aps.*
import aps.RedisLogMessage.SQL.Stage.*
import into.kommon.*
import org.jooq.*
import org.jooq.SelectField
import org.jooq.conf.ParamType
import org.jooq.exception.DataAccessException
import org.jooq.exception.DataTypeException
import java.util.*
import java.util.concurrent.CompletionStage
import java.util.concurrent.Executor

class DSLContextProxy(val activityParams: ActivityParams, val q: DSLContext) {
    fun <R : Record> insertInto(into: Table<R>, vararg fields: Field<*>): InsertValuesStepN<R> {
        return q.insertInto(into, *fields)
    }

    fun fetch(sql: String, vararg bindings: Any?): Result<Record> =
        if (BackGlobus.tracingEnabled) executeTracing(activityParams) {q.fetch(sql, *bindings)}
        else q.fetch(sql, *bindings)

    fun selectCount(): SelectSelectStep<Record1<Int>> {
        return q.selectCount()
    }

    fun <R : Record> update(table: Table<R>): UpdateSetFirstStep<R> {
        return q.update(table)
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
        if (BackGlobus.tracingEnabled) executeTracing(activityParams) {q.execute(sql)}
        else q.execute(sql)
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
        return wrappee.executeAsync()
    }

    override fun executeAsync(executor: Executor): CompletionStage<Int> {
        return wrappee.executeAsync(executor)
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
        if (BackGlobus.tracingEnabled) executeTracing(activityParams) {wrappee.fetchOne()}
        else wrappee.fetchOne()

    @Support
    @Throws(DataAccessException::class)
    override fun fetchOptional(): Optional<R> {
        return wrappee.fetchOptional()
    }

    @Throws(DataAccessException::class)
    override fun execute(): Int {
        return wrappee.execute()
    }

    override fun executeAsync(): CompletionStage<Int> {
        return wrappee.executeAsync()
    }

    override fun executeAsync(executor: Executor): CompletionStage<Int> {
        return wrappee.executeAsync(executor)
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


private fun <T> executeTracing(activityParams: ActivityParams, block: () -> T): T {
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
