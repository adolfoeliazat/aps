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


