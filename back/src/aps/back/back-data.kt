package aps.back

import aps.*
import aps.back.generated.jooq.enums.*

fun UAAcademicLevel.toJOOQ(): JQUaAcademicLevel = when (this) {
    UAAcademicLevel.SCHOOL -> JQUaAcademicLevel.SCHOOL
    UAAcademicLevel.INSTITUTE -> JQUaAcademicLevel.INSTITUTE
}

fun UADocumentType.toJOOQ(): JQUaDocumentType = when (this) {
    UADocumentType.ESSAY -> JQUaDocumentType.ESSAY
    UADocumentType.COURSE -> JQUaDocumentType.COURSE
    UADocumentType.GRADUATION -> JQUaDocumentType.GRADUATION
}

fun DocumentUrgency.toJOOQ(): JQDocumentUrgency = when (this) {
    DocumentUrgency.H12 -> JQDocumentUrgency.H12
    DocumentUrgency.H24 -> JQDocumentUrgency.H24
    DocumentUrgency.D3 -> JQDocumentUrgency.D3
    DocumentUrgency.D5 -> JQDocumentUrgency.D5
    DocumentUrgency.D7 -> JQDocumentUrgency.D7
    DocumentUrgency.D8 -> JQDocumentUrgency.D8
}


