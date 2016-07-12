/*
 * APS
 * 
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

export const testdata = {users: {ua: {}}}

testdata.users.ua.admin = [
    {
        user: {id: 100, kind: 'admin', first_name: 'Тодд', last_name: 'Суппортод', email: 'todd@test.shit.ua', state: 'cool', lang: 'ua', password_hash: '$2a$10$x5bq4zVvcyTb2oUb5.fhreJfl/2NqsaH3TcAwm/C1apAazlBJX2t6',},
        roles: ['support'],
    },
]

testdata.users__ = [
    {id: 201, kind: 'writer', first_name: 'Франц', last_name: 'Кафка', email: 'kafka@test.shit.ua', state: 'cool', lang: 'ua', password_hash: '$2a$10$x5bq4zVvcyTb2oUb5.fhreJfl/2NqsaH3TcAwm/C1apAazlBJX2t6',},
    {id: 202, kind: 'writer', first_name: 'Лев', last_name: 'Толстой', email: 'leo@test.shit.ua', state: 'cool', lang: 'ua', password_hash: '$2a$10$x5bq4zVvcyTb2oUb5.fhreJfl/2NqsaH3TcAwm/C1apAazlBJX2t6',},
    {id: 203, kind: 'writer', first_name: 'Николай', last_name: 'Гоголь', email: 'gogol@test.shit.ua', state: 'cool', lang: 'ua', password_hash: '$2a$10$x5bq4zVvcyTb2oUb5.fhreJfl/2NqsaH3TcAwm/C1apAazlBJX2t6',},
    {id: 204, kind: 'writer', first_name: 'Федор', last_name: 'Достоевский', email: 'fedor@test.shit.ua', state: 'cool', lang: 'ua', password_hash: '$2a$10$x5bq4zVvcyTb2oUb5.fhreJfl/2NqsaH3TcAwm/C1apAazlBJX2t6',},
    {id: 205, kind: 'writer', first_name: 'Александр', last_name: 'Пушкин', email: 'pushkin@test.shit.ua', state: 'cool', lang: 'ua', password_hash: '$2a$10$x5bq4zVvcyTb2oUb5.fhreJfl/2NqsaH3TcAwm/C1apAazlBJX2t6',},
    {id: 300, kind: 'customer', first_name: 'Пися', last_name: 'Камушкин', email: 'pisya@test.shit.ua', state: 'cool', lang: 'ua', password_hash: '$2a$10$x5bq4zVvcyTb2oUb5.fhreJfl/2NqsaH3TcAwm/C1apAazlBJX2t6'},
    {id: 301, kind: 'customer', first_name: 'Люк', last_name: 'Хуюк', email: 'luke@test.shit.ua', state: 'cool', lang: 'ua', password_hash: '$2a$10$x5bq4zVvcyTb2oUb5.fhreJfl/2NqsaH3TcAwm/C1apAazlBJX2t6',},
    {id: 302, kind: 'customer', first_name: 'Павло', last_name: 'Зибров', email: 'zibrov@test.shit.ua', state: 'cool', lang: 'ua', password_hash: '$2a$10$x5bq4zVvcyTb2oUb5.fhreJfl/2NqsaH3TcAwm/C1apAazlBJX2t6',},
    {id: 303, kind: 'customer', first_name: 'Василий', last_name: 'Теркин', email: 'terkin@test.shit.ua', state: 'cool', lang: 'ua', password_hash: '$2a$10$x5bq4zVvcyTb2oUb5.fhreJfl/2NqsaH3TcAwm/C1apAazlBJX2t6',},
    {id: 304, kind: 'customer', first_name: 'Иво', last_name: 'Бобул', email: 'ivo@test.shit.ua', state: 'cool', lang: 'ua', password_hash: '$2a$10$x5bq4zVvcyTb2oUb5.fhreJfl/2NqsaH3TcAwm/C1apAazlBJX2t6',},
    {id: 305, kind: 'customer', first_name: 'Регина', last_name: 'Дубовицкая', email: 'regina@test.shit.ua', state: 'cool', lang: 'ua', password_hash: '$2a$10$x5bq4zVvcyTb2oUb5.fhreJfl/2NqsaH3TcAwm/C1apAazlBJX2t6',},
    {id: 306, kind: 'customer', first_name: 'Евгений', last_name: 'Ваганович', email: 'vaganovich@test.shit.ua', state: 'cool', lang: 'ua', password_hash: '$2a$10$x5bq4zVvcyTb2oUb5.fhreJfl/2NqsaH3TcAwm/C1apAazlBJX2t6',},
]

testdata.customerAndWriterUserIDs = []
for (const user of testdata.users) {
    if (user.kind === 'customer' || user.kind === 'writer') {
        testdata.customerAndWriterUserIDs.push(user.id)
    }
}

