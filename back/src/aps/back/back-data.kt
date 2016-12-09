package aps.back

import aps.*
import aps.back.generated.jooq.enums.*
import aps.back.generated.jooq.tables.JQUsers.*
import aps.back.generated.jooq.tables.pojos.*

fun UAAcademicLevel.toJOOQ(): JQUaAcademicLevel = when (this) {
    UAAcademicLevel.SCHOOL -> JQUaAcademicLevel.SCHOOL
    UAAcademicLevel.INSTITUTE -> JQUaAcademicLevel.INSTITUTE
}

fun UADocumentType.toJOOQ(): JQUaDocumentType = when (this) {
    UADocumentType.ESSAY -> JQUaDocumentType.ESSAY
    UADocumentType.COURSE -> JQUaDocumentType.COURSE
    UADocumentType.GRADUATION -> JQUaDocumentType.GRADUATION
}

fun JQUaDocumentType.toApp(): UADocumentType = when (this) {
    JQUaDocumentType.ESSAY -> UADocumentType.ESSAY
    JQUaDocumentType.COURSE -> UADocumentType.COURSE
    JQUaDocumentType.GRADUATION -> UADocumentType.GRADUATION
}

fun DocumentUrgency.toJOOQ(): JQDocumentUrgency = when (this) {
    DocumentUrgency.H12 -> JQDocumentUrgency.H12
    DocumentUrgency.H24 -> JQDocumentUrgency.H24
    DocumentUrgency.D3 -> JQDocumentUrgency.D3
    DocumentUrgency.D5 -> JQDocumentUrgency.D5
    DocumentUrgency.D7 -> JQDocumentUrgency.D7
    DocumentUrgency.D8 -> JQDocumentUrgency.D8
}

fun JQUaOrderState.toApp(): UAOrderState = when (this) {
    JQUaOrderState.CREATED -> UAOrderState.CREATED
    JQUaOrderState.LOOKING_FOR_WRITERS -> UAOrderState.LOOKING_FOR_WRITERS
    JQUaOrderState.WAITING_FOR_PAYMENT -> UAOrderState.WAITING_FOR_PAYMENT
    JQUaOrderState.WRITER_ASSIGNED -> UAOrderState.WRITER_ASSIGNED
}

fun UAOrderState.toJOOQ(): JQUaOrderState = when (this) {
    UAOrderState.CREATED -> JQUaOrderState.CREATED
    UAOrderState.LOOKING_FOR_WRITERS -> JQUaOrderState.LOOKING_FOR_WRITERS
    UAOrderState.WAITING_FOR_PAYMENT -> JQUaOrderState.WAITING_FOR_PAYMENT
    UAOrderState.WRITER_ASSIGNED -> JQUaOrderState.WRITER_ASSIGNED
}

fun ProcedureContext.loadUser(id: Long): UserRTO {
    return q("Select user")
        .select().from(USERS)
        .where(USERS.ID.eq(id))
        .fetchOne().into(JQUsers::class.java).toRTO(q)
}









