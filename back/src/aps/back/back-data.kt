package aps.back

import aps.*
import aps.back.generated.jooq.enums.*
import aps.back.generated.jooq.tables.JQFileUserPermissions.*
import aps.back.generated.jooq.tables.JQFiles.*
import aps.back.generated.jooq.tables.JQUaOrderAreas.*
import aps.back.generated.jooq.tables.JQUaOrderFiles.*
import aps.back.generated.jooq.tables.JQUserRoles.*
import aps.back.generated.jooq.tables.JQUsers.*
import aps.back.generated.jooq.tables.pojos.*
import aps.back.generated.jooq.tables.records.*
import into.kommon.*
import org.jooq.*
import java.sql.Timestamp

@Suppress("UNCHECKED_CAST")
fun <Res, R : Record> ProcedureContext.insertShit(
    descr: String,
    table: Table<R>,
    block: (InsertSetMoreStep<R>) -> Res
): Res {
    return tracingSQL(descr) {
        var step = q
            .insertInto(table)
            .set(table.field("inserted_at") as Field<Timestamp>, RequestGlobus.stamp)
            .set(table.field("updated_at") as Field<Timestamp>, RequestGlobus.stamp)

        table.field("creator_id")?.let {
            step = step.set(it as Field<Long>, this.user_killme.id.toLong())
        }

        block(step)
    }
}

@Suppress("UNCHECKED_CAST")
fun <Res, R : Record> ProcedureContext.updateShit(
    descr: String,
    table: Table<R>,
    block: (UpdateSetMoreStep<R>) -> Res
): Res {
    return tracingSQL(descr) {
        val step = q
            .update(table)
            .set(table.field("updated_at") as Field<Timestamp>, RequestGlobus.stamp)
        block(step)
    }
}

@Suppress("UNCHECKED_CAST")
fun <R : Record> InsertSetMoreStep<R>.returnID(table: Table<R>): Long {
    val idField = table.field("id") as Field<Long>
    return this
         .returning(idField)
         .fetchOne()
         .getValue(idField)
}

fun UserKind.toJOOQ(): JQUserKind = when (this) {
    UserKind.CUSTOMER -> JQUserKind.CUSTOMER
    UserKind.WRITER -> JQUserKind.WRITER
    UserKind.ADMIN -> JQUserKind.ADMIN
}

fun UAAcademicLevel.toJOOQ(): JQUaAcademicLevel = when (this) {
    UAAcademicLevel.SCHOOL -> JQUaAcademicLevel.SCHOOL
    UAAcademicLevel.INSTITUTE -> JQUaAcademicLevel.INSTITUTE
}

fun UADocumentType.toJOOQ(): JQUaDocumentType = when (this) {
    UADocumentType.ESSAY -> JQUaDocumentType.ESSAY
    UADocumentType.COURSE -> JQUaDocumentType.COURSE
    UADocumentType.GRADUATION -> JQUaDocumentType.GRADUATION
    UADocumentType.ABSTRACT -> JQUaDocumentType.ABSTRACT
    UADocumentType.LAB -> JQUaDocumentType.LAB
    UADocumentType.TEST -> JQUaDocumentType.TEST
    UADocumentType.RGR -> JQUaDocumentType.RGR
    UADocumentType.DRAWING -> JQUaDocumentType.DRAWING
    UADocumentType.DISSERTATION -> JQUaDocumentType.DISSERTATION
    UADocumentType.PRACTICE -> JQUaDocumentType.PRACTICE
    UADocumentType.OTHER -> JQUaDocumentType.OTHER
}

fun JQUserKind.toApp(): UserKind = when (this) {
    JQUserKind.CUSTOMER -> UserKind.CUSTOMER
    JQUserKind.WRITER -> UserKind.WRITER
    JQUserKind.ADMIN -> UserKind.ADMIN
}

fun JQUaDocumentType.toApp(): UADocumentType = when (this) {
    JQUaDocumentType.ESSAY -> UADocumentType.ESSAY
    JQUaDocumentType.COURSE -> UADocumentType.COURSE
    JQUaDocumentType.GRADUATION -> UADocumentType.GRADUATION
    JQUaDocumentType.ABSTRACT -> UADocumentType.ABSTRACT
    JQUaDocumentType.LAB -> UADocumentType.LAB
    JQUaDocumentType.TEST -> UADocumentType.TEST
    JQUaDocumentType.RGR -> UADocumentType.RGR
    JQUaDocumentType.DRAWING -> UADocumentType.DRAWING
    JQUaDocumentType.DISSERTATION -> UADocumentType.DISSERTATION
    JQUaDocumentType.PRACTICE -> UADocumentType.PRACTICE
    JQUaDocumentType.OTHER-> UADocumentType.OTHER
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
    JQUaOrderState.WAITING_EMAIL_CONFIRMATION -> UAOrderState.WAITING_EMAIL_CONFIRMATION
}

fun UAOrderState.toJOOQ(): JQUaOrderState = when (this) {
    UAOrderState.CREATED -> JQUaOrderState.CREATED
    UAOrderState.LOOKING_FOR_WRITERS -> JQUaOrderState.LOOKING_FOR_WRITERS
    UAOrderState.WAITING_FOR_PAYMENT -> JQUaOrderState.WAITING_FOR_PAYMENT
    UAOrderState.WRITER_ASSIGNED -> JQUaOrderState.WRITER_ASSIGNED
    UAOrderState.WAITING_EMAIL_CONFIRMATION -> JQUaOrderState.WAITING_EMAIL_CONFIRMATION
    else -> die("Fuck UAOrderState.toJOOQ")
}

fun ProcedureContext.loadUser(id: Long): UserRTO {
    return tracingSQL("Select user") {q
        .select().from(USERS)
        .where(USERS.ID.eq(id))
        .fetchOne().into(JQUsers::class.java).toRTO(q)
    }
}

//fun loadFile(q: DSLContext, id: Long, searchWords: List<String>, lang: Language): FileRTO {
//    val x = tracingSQL("Select file") {q
//        .select().from(FILES)
//        .where(FILES.ID.eq(id))
//        .fetchOne().into(JQFiles::class.java)
//    }
//
//    val analyzer = when (lang) {
//        Language.UA -> russianAnalyzer
//        else -> imf("Support analyzing for $lang")
//    }
//
//    return FileRTO(
//        id = "" + x.id,
//        name = x.name,
//        nameHighlightRanges =
//            if (searchWords.isEmpty()) listOf()
//            else luceneHighlightRanges(x.name.chopOffFileExtension(), searchWords, analyzer),
//        title = x.title,
//        titleHighlightRanges =
//            if (searchWords.isEmpty()) listOf()
//            else luceneHighlightRanges(x.title, searchWords, analyzer),
//        details = x.details,
//        detailsHighlightRanges =
//            if (searchWords.isEmpty()) listOf()
//            else luceneHighlightRanges(x.details, searchWords, analyzer),
//        sizeBytes = x.sizeBytes,
//        insertedAt = x.insertedAt.time,
//        updatedAt = x.updatedAt.time
//    )
//}

fun JQUsers.toRTO(q: DSLContext): UserRTO {
    val roles = tracingSQL("Select roles") {q
        .select().from(USER_ROLES)
        .where(USER_ROLES.USER_ID.eq(id))
        .fetchInto(JQUserRoles::class.java)
    }

    // TODO:vgrechka Double-check all secrets are excluded from UserRTO    7c2d1191-d43b-485c-af67-b95b46bbf62b
    return UserRTO(
        id = "" + id,
        deleted = deleted,
        insertedAt = insertedAt.toPortable(),
        updatedAt = updatedAt.toPortable(),
        profileUpdatedAt = profileUpdatedAt.toMaybePortable(),
        kind = kind.toApp(),
        lang = lang.toLanguage(),
        email = email,
        state = state.toUserState(),
        profileRejectionReason = profileRejectionReason,
        banReason = banReason,
        adminNotes = adminNotes,
        firstName = firstName,
        lastName = lastName,
        phone = phone,
        compactPhone = compactPhone,
        aboutMe = aboutMe,
        roles = roles.map{UserRole.valueOf(it.role)}.toSet()
    )
}

fun JQUaOrderFilesRecord.toRTO(ctx: ProcedureContext, searchWords: List<String> = listOf()): UAOrderFileRTO {
    die("Kill JQUaOrderFilesRecord.toRTO")
//    return UAOrderFileRTO(
//        id = id.toString(),
//        file = loadFile(ctx.q, fileId, searchWords, Language.UA),
//        seenAsFrom = seenAsFrom.toApp(),
//        editable = creatorId == ctx.user_killme.id.toLong(),
//        createdAt = insertedAt.time,
//        updatedAt = updatedAt.time
//    )
}

fun insertFileUserPermission(ctx: ProcedureContext, fileID: Long, userID: Long) {
    FILE_USER_PERMISSIONS.let {t->
        ctx.insertShit("Insert file permission", t) {it
            .set(t.FILE_ID, fileID)
            .set(t.USER_ID, userID)
            .execute()
        }
    }
}

fun userKindToAreaName(userKind: UserKind): String {
    return when (userKind) {
        UserKind.CUSTOMER -> const.orderArea.customer
        UserKind.WRITER -> const.orderArea.writer
        UserKind.ADMIN -> const.orderArea.admin
    }
}

fun selectUAOrderFile(ctx: ProcedureContext, orderFileID: Long): JQUaOrderFilesRecord {
    return tracingSQL("Select updated order file") {ctx.q
        .selectFrom(UA_ORDER_FILES)
        .where(UA_ORDER_FILES.ID.eq(orderFileID))
        .fetchOne()!!
    }
}

fun selectUAOrderAreaByName(ctx: ProcedureContext, orderID: Long, name: String): JQUaOrderAreasRecord {
    return UA_ORDER_AREAS.let {
        tracingSQL("Select area") {ctx.q
            .selectFrom(it)
            .where(it.NAME.eq(name))
            .and(it.UA_ORDER_ID.eq(orderID))
            .fetchOne()!!
        }
    }
}







