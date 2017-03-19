package org.springframework.data.repository

import aps.*
import java.io.Serializable

@NoRepositoryBean
interface CrudRepository<T, ID : Serializable> : Repository<T, ID> {
    fun <S : T> save(entity: S): S
    fun <S : T> save(entities: Iterable<S>): Iterable<S>
    fun findOne(id: ID): T?
    fun exists(id: ID): Boolean
    fun findAll(): Iterable<T>
    fun findAll(ids: Iterable<ID>): Iterable<T>
    fun count(): Long
    fun delete(id: ID)
    fun delete(entity: T)
    fun delete(entities: Iterable<T>)
    fun deleteAll()
}

inline fun <reified T, ID : java.io.Serializable> CrudRepository<T, ID>.findOrDie(id: ID): T {
    return findOne(id) ?: die("No fucking ${T::class.simpleName} with ID $id")
}


