package aps.back

import aps.*
import aps.back.generated.jooq.enums.*
import aps.back.generated.jooq.tables.JQFiles.*
import aps.back.generated.jooq.tables.JQUserRoles.*
import aps.back.generated.jooq.tables.JQUsers.*
import aps.back.generated.jooq.tables.pojos.*
import into.kommon.*
import org.jooq.*
import java.sql.Timestamp

@Suppress("UNCHECKED_CAST")
fun <R : Record> ProcedureContext.insertShit(
    descr: String,
    table: Table<R>
): InsertSetMoreStep<R> {
    var step = this.q(descr)
        .insertInto(table)
        .set(table.field("inserted_at") as Field<Timestamp>, this.requestTimestamp)
        .set(table.field("updated_at") as Field<Timestamp>, this.requestTimestamp)

    table.field("creator_id")?.let {
        step = step.set(it as Field<Long>, this.user.id.toLong())
    }

    return step
}

@Suppress("UNCHECKED_CAST")
fun <R : Record> ProcedureContext.updateShit(
    descr: String,
    table: Table<R>
): UpdateSetMoreStep<R> {
    var step = this.q(descr)
        .update(table)
        .set(table.field("updated_at") as Field<Timestamp>, this.requestTimestamp)
    return step
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

fun loadFile(q: DSLContextProxyFactory, id: Long, searchWords: List<String>, lang: Language): FileRTO {
    val x = q("Select file")
        .select().from(FILES)
        .where(FILES.ID.eq(id))
        .fetchOne().into(JQFiles::class.java)

    val analyzer = when (lang) {
        Language.UA -> russianAnalyzer
        else -> imf("Support analyzing for $lang")
    }

    return FileRTO(
        id = "" + x.id,
        name = x.name,
        nameHighlightRanges =
            if (searchWords.isEmpty()) listOf()
            else luceneHighlightRanges(x.name.chopOffFileExtension(), searchWords, analyzer),
        title = x.title,
        titleHighlightRanges =
            if (searchWords.isEmpty()) listOf()
            else luceneHighlightRanges(x.title, searchWords, analyzer),
        details = x.details,
        detailsHighlightRanges =
            if (searchWords.isEmpty()) listOf()
            else luceneHighlightRanges(x.details, searchWords, analyzer),
        sizeBytes = x.sizeBytes,
        insertedAt = x.insertedAt.time
    )
}

fun JQUsers.toRTO(q: DSLContextProxyFactory): UserRTO {
    val roles = q("Select roles")
        .select().from(USER_ROLES)
        .where(USER_ROLES.USER_ID.eq(id))
        .fetchInto(JQUserRoles::class.java)

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

fun JQUaOrderFiles.toRTO(q: DSLContextProxyFactory, searchWords: List<String> = listOf()): UAOrderFileRTO {
    return UAOrderFileRTO(
        id = this.id.toString(),
        file = loadFile(q, this.fileId, searchWords, Language.UA),
        seenAsFrom = this.seenAsFrom.toApp()
    )
}










