/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import aps.back.generated.jooq.Tables.USERS
import aps.back.generated.jooq.enums.*
import aps.back.generated.jooq.tables.JQUaOrderFiles.*
import aps.back.generated.jooq.tables.records.*
import com.google.debugging.sourcemap.SourceMapConsumerFactory
import com.google.debugging.sourcemap.SourceMapping
import com.sun.jna.platform.win32.User32
import org.apache.lucene.analysis.ru.RussianAnalyzer
import org.jooq.Record
import org.jooq.UpdateSetMoreStep
import org.jooq.UpdateSetStep
import org.mindrot.jbcrypt.BCrypt
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.data.jpa.repository.JpaContext
import java.awt.MouseInfo
import java.awt.Robot
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes
import java.sql.Timestamp
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


fun <Req : RequestMatumba, Res : CommonResponseFields>
testProcedure(
    req: (ProcedureContext) -> Req,
    runShit: (ProcedureContext, Req) -> Res,
    needsDB: Boolean? = null,
    logRequestJSON: Boolean? = null
): (HttpServletRequest, HttpServletResponse) -> Unit =
    remoteProcedure(ProcedureSpec(
        req,
        runShit = runShit,
        wrapInFormResponse = false,
        needsDB = needsDB ?: false,
        needsDangerousToken = true,
        needsUser = NeedsUser.NO,
        userKinds = setOf(),
        considerNextRequestTimestampFiddling = false,
        logRequestJSON = logRequestJSON ?: true
    ))

@RemoteProcedureFactory fun imposeNextRequestTimestamp() = testProcedure(
    {ImposeNextRequestTimestampRequest()},
    runShit = fun(ctx, req): GenericResponse {
        TestServerFiddling.nextRequestTimestamp.set(stringToStamp(req.stamp.value))
        return GenericResponse()
    }
)

@RemoteProcedureFactory fun imposeNextRequestError() = testProcedure(
    {ImposeNextRequestErrorRequest()},
    runShit = fun(ctx, req): GenericResponse {
        TestServerFiddling.nextRequestError.set(req.error.value ?: const.msg.serviceFuckedUp)
        return GenericResponse()
    }
)

@RemoteProcedureFactory fun imposeNextGeneratedPassword() = testProcedure(
    {ImposeNextGeneratedPasswordRequest()},
    runShit = fun(ctx, req): ImposeNextGeneratedPasswordRequest.Response {
        TestServerFiddling.nextGeneratedPassword.set(req.password.value)
        return ImposeNextGeneratedPasswordRequest.Response()
    }
)

@RemoteProcedureFactory fun imposeNextGeneratedConfirmationSecret() = testProcedure(
    {ImposeNextGeneratedConfirmationSecretRequest()},
    runShit = fun(ctx, req): ImposeNextGeneratedConfirmationSecretRequest.Response {
        TestServerFiddling.nextGeneratedConfirmationSecret.set(req.secret.value)
        return ImposeNextGeneratedConfirmationSecretRequest.Response()
    }
)

//@RemoteProcedureFactory fun resetTestDatabase() = testProcedure(
//    {ResetTestDatabaseRequest()},
//    runShit = fun(ctx, req): GenericResponse {
//        DB.apsTestOnTestServer.recreate()
//        return GenericResponse()
//    }
//)

@Servant class ServeRecreateTestDatabaseSchema : BitchyProcedure() {
    override fun serve() {
        fuckDangerously(FuckDangerouslyParams(
            bpc = bpc, makeRequest = {RecreateTestDatabaseSchemaRequest()},
            runShit = fun(ctx, req): GenericResponse {
                springctx = AnnotationConfigApplicationContext(AppConfig::class.java) // New context -- new EMF
                enhanceDB()
                seed()
                return GenericResponse()
            }
        ))
    }

    fun seed() {
        backPlatform.userRepo.save(User(user = UserFields(email = "dasja@test.shit.ua", firstName = "Дася", lastName = "Админовна", profilePhone = "911", kind = UserKind.ADMIN, state = UserState.COOL, passwordHash = BCrypt.hashpw("dasja-secret", BCrypt.gensalt()), adminNotes = "", subscribedToAllCategories = false)))

        fun makeCategory(title: String, id: Long, children: List<UADocumentCategory> = listOf()) =
            UADocumentCategory(UADocumentCategoryFields(title = title, parent = null, children = children.toMutableList()))-{o->
                o.imposedIDToGenerate = id
            }

        val ids = const.uaDocumentCategoryID
        val root = makeCategory("ROOT", ids.root, listOf(
            makeCategory(t("TOTE", "Разное"), ids.misc),
            makeCategory(t("TOTE", "Экономические"), ids.economyGroup, listOf(
                makeCategory(t("TOTE", "Аудит"), ids.auditing),
                makeCategory(t("TOTE", "Банковское дело"), ids.banking),
                makeCategory(t("TOTE", "Биржевое дело"), ids.stockExchange),
                makeCategory(t("TOTE", "Бухгалтерский учет"), ids.accounting),
                makeCategory(t("TOTE", "Бюджетная система"), ids.budgetSystem),
                makeCategory(t("TOTE", "Валютное регулирование и контроль"), ids.currencyRegulation),
                makeCategory(t("TOTE", "Валютные отношения"), ids.currencyRelationships),
                makeCategory(t("TOTE", "Деньги и кредит"), ids.moneyAndCredit),
                makeCategory(t("TOTE", "Государственная служба"), ids.publicService),
                makeCategory(t("TOTE", "Государственное управление"), ids.publicAdministration),
                makeCategory(t("TOTE", "Государственные финансы"), ids.publicFinances),
                makeCategory(t("TOTE", "Делопроизводство, документоведение, документалистика"), ids.documentManagement),
                makeCategory(t("TOTE", "Эконометрика"), ids.econometrics),
                makeCategory(t("TOTE", "Экономика"), ids.economy),
                makeCategory(t("TOTE", "Экономика предприятий"), ids.enterpriseEconomics),
                makeCategory(t("TOTE", "Экономика труда и социально-трудовые отношения"), ids.laborEconomics),
                makeCategory(t("TOTE", "Экономическая кибернетика"), ids.economicCybernetics),
                makeCategory(t("TOTE", "Экономический анализ"), ids.economicAnalysis),
                makeCategory(t("TOTE", "Электронная коммерция"), ids.eCommerce),
                makeCategory(t("TOTE", "Связи с общественностью, PR"), ids.pr),
                makeCategory(t("TOTE", "Внешнеэкономическая деятельность, ВЭД"), ids.foreignTradeActivities),
                makeCategory(t("TOTE", "Инвестирование, инвестиционная деятельность"), ids.investment),
                makeCategory(t("TOTE", "Инновационная деятельность"), ids.innovativeActivity),
                makeCategory(t("TOTE", "Инновационный менеджмент"), ids.innovativeManagement),
                makeCategory(t("TOTE", "Казначейское дело"), ids.treasury),
                makeCategory(t("TOTE", "Контроллинг"), ids.control),
                makeCategory(t("TOTE", "Лесное хозяйство"), ids.forestry),
                makeCategory(t("TOTE", "Логистика"), ids.logistics),
                makeCategory(t("TOTE", "Макроэкономика, государственное регулирование экономики"), ids.macroeconomics),
                makeCategory(t("TOTE", "Маркетинг, рекламная деятельность"), ids.marketingAndAdvertisement),
                makeCategory(t("TOTE", "Менеджмент, управление персоналом"), ids.management),
                makeCategory(t("TOTE", "Таможенное дело"), ids.customs),
                makeCategory(t("TOTE", "Международная экономика и международные экономические отношения"), ids.internationalEconomics),
                makeCategory(t("TOTE", "Микроэкономика"), ids.microeconomics),
                makeCategory(t("TOTE", "Моделирование экономики"), ids.economicModeling),
                makeCategory(t("TOTE", "Налогообложение, налоги, налоговая система"), ids.taxes),
                makeCategory(t("TOTE", "Предпринимательство"), ids.entrepreneurship),
                makeCategory(t("TOTE", "Политэкономия, экономическая теория, история экономических учений"), ids.politicalEconomy),
                makeCategory(t("TOTE", "Ресторанно-гостиничный бизнес, бытовое обслуживание"), ids.restaurantHotelBusinessAndConsumerService),
                makeCategory(t("TOTE", "Рынок ценных бумаг"), ids.securitiesMarket),
                makeCategory(t("TOTE", "Размещение производительных сил, региональная экономика, экономическая география, РПС"), ids.locationOfProductiveForces),
                makeCategory(t("TOTE", "Сельское хозяйство и агропромышленный комплекс"), ids.agriculture),
                makeCategory(t("TOTE", "Стандартизация, управление качеством"), ids.standardizationAndQualityManagement),
                makeCategory(t("TOTE", "Статистика"), ids.statistics),
                makeCategory(t("TOTE", "Стратегический менеджмент"), ids.strategicManagement),
                makeCategory(t("TOTE", "Страхование, страховое дело"), ids.insurance),
                makeCategory(t("TOTE", "Товароведение и экспертиза"), ids.commodityAndExpertise),
                makeCategory(t("TOTE", "Торговля и коммерческая деятельность"), ids.tradeAndCommercialActivity),
                makeCategory(t("TOTE", "Туризм"), ids.tourism),
                makeCategory(t("TOTE", "Управление проектами"), ids.projectManagement),
                makeCategory(t("TOTE", "Управленческий учет"), ids.managementAccounting),
                makeCategory(t("TOTE", "Финансы"), ids.finance),
                makeCategory(t("TOTE", "Финансы предприятий"), ids.enterpriseFinance),
                makeCategory(t("TOTE", "Финансовый анализ"), ids.financialAnalysis),
                makeCategory(t("TOTE", "Финансовый менеджмент"), ids.financialManagement),
                makeCategory(t("TOTE", "Ценообразование"), ids.pricing)
            )),

            makeCategory(t("TOTE", "Естественные"), ids.naturalGroup, listOf(
                makeCategory(t("TOTE", "Астрономия"), ids.astronomy),
                makeCategory(t("TOTE", "Биология"), ids.biology),
                makeCategory(t("TOTE", "Военная подготовка"), ids.militaryFucking),
                makeCategory(t("TOTE", "География"), ids.geography),
                makeCategory(t("TOTE", "Геодезия"), ids.geodesy),
                makeCategory(t("TOTE", "Геология"), ids.geology),
                makeCategory(t("TOTE", "Экология"), ids.ecology),
                makeCategory(t("TOTE", "Математика"), ids.math),
                makeCategory(t("TOTE", "Медицина"), ids.medicine),
                makeCategory(t("TOTE", "Естествознание"), ids.naturalHistory),
                makeCategory(t("TOTE", "Фармацевтика"), ids.pharmaceuticals),
                makeCategory(t("TOTE", "Физика"), ids.physics),
                makeCategory(t("TOTE", "Химия"), ids.chemistry)
            )),

            makeCategory(t("TOTE", "Технические"), ids.technicalGroup, listOf(
                makeCategory(t("TOTE", "Авиация и космонавтика"), ids.aviationAndCosmonautics),
                makeCategory(t("TOTE", "Архитектура"), ids.architecture),
                makeCategory(t("TOTE", "Базы данных"), ids.databases),
                makeCategory(t("TOTE", "Строительство"), ids.construction),
                makeCategory(t("TOTE", "Электроника"), ids.electronics),
                makeCategory(t("TOTE", "Электротехника"), ids.electricalEngineering),
                makeCategory(t("TOTE", "Информатика и вычислительная техника"), ids.informaticsAndComputing),
                makeCategory(t("TOTE", "Информационная безопасность"), ids.informationSecurity),
                makeCategory(t("TOTE", "Информационно-аналитическая деятельность"), ids.informationAnalyticalActivity),
                makeCategory(t("TOTE", "Кибернетика"), ids.cybernetics),
                makeCategory(t("TOTE", "Чертежи"), ids.drawings),
                makeCategory(t("TOTE", "Программирование"), ids.programming),
                makeCategory(t("TOTE", "Проектирование"), ids.technicalDesign),
                makeCategory(t("TOTE", "Радиоэлектроника, радиотехника"), ids.radioEngineering),
                makeCategory(t("TOTE", "Теоретическая механика"), ids.theoreticalMechanics),
                makeCategory(t("TOTE", "Теория механизмов и машин (ТММ), детали машин (ДМ)"), ids.theoryOfMechanismsAndMachines),
                makeCategory(t("TOTE", "Теплотехника"), ids.heatEngineering),
                makeCategory(t("TOTE", "Технологии, система технологий"), ids.technologySystem),
                makeCategory(t("TOTE", "Технология машиностроения"), ids.engineeringTechnology),
                makeCategory(t("TOTE", "Технология приготовления пищи"), ids.cookingTechnology),
                makeCategory(t("TOTE", "Транспортное строительство"), ids.transportConstruction)
            )),

            makeCategory(t("TOTE", "Юридические"), ids.legalGroup, listOf(
                makeCategory(t("TOTE", "Адвокатура"), ids.advocacy),
                makeCategory(t("TOTE", "Административное право"), ids.administrativeLaw),
                makeCategory(t("TOTE", "Арбитражный процесс"), ids.arbitrationProceedings),
                makeCategory(t("TOTE", "Хозяйственное право"), ids.economicLaw),
                makeCategory(t("TOTE", "Экологическое право"), ids.environmentalLaw),
                makeCategory(t("TOTE", "Жилищное право"), ids.housingLaw),
                makeCategory(t("TOTE", "Земельное право"), ids.landLaw),
                makeCategory(t("TOTE", "История государства и права"), ids.historyOfStateAndLaw),
                makeCategory(t("TOTE", "Конституционное право"), ids.constitutionalLaw),
                makeCategory(t("TOTE", "Корпоративное право"), ids.corporateLaw),
                makeCategory(t("TOTE", "Криминалистика, экспертиза"), ids.forensics),
                makeCategory(t("TOTE", "Уголовное право, криминология"), ids.criminalLaw),
                makeCategory(t("TOTE", "Уголовный процесс"), ids.criminalProcess),
                makeCategory(t("TOTE", "Таможенное право"), ids.customsLaw),
                makeCategory(t("TOTE", "Международное право"), ids.internationalLaw),
                makeCategory(t("TOTE", "Муниципальное право"), ids.municipalLaw),
                makeCategory(t("TOTE", "Нотариат"), ids.notary),
                makeCategory(t("TOTE", "Предпринимательское право"), ids.businessLaw),
                makeCategory(t("TOTE", "Налоговое право"), ids.taxLaw),
                makeCategory(t("TOTE", "Право"), ids.law),
                makeCategory(t("TOTE", "Право интеллектуальной собственности"), ids.intellectualPropertyRights),
                makeCategory(t("TOTE", "Семейное право"), ids.familyLaw),
                makeCategory(t("TOTE", "Страховое право"), ids.insuranceLaw),
                makeCategory(t("TOTE", "Судебные и правоохранительные органы"), ids.judicialAndLawEnforcementAgencies),
                makeCategory(t("TOTE", "Судебно-медицинская экспертиза"), ids.forensicMedicalExamination),
                makeCategory(t("TOTE", "Теория государства и права"), ids.theoryOfStateAndLaw),
                makeCategory(t("TOTE", "Трудовое право"), ids.laborLaw),
                makeCategory(t("TOTE", "Финансовое право"), ids.financialLaw),
                makeCategory(t("TOTE", "Гражданское право"), ids.civilLaw)
            )),

            makeCategory(t("TOTE", "Гуманитарные"), ids.humanitarianGroup, listOf(
                makeCategory(t("TOTE", "Анализ банковской деятельности"), ids.analysisOfBankingActivities),
                makeCategory(t("TOTE", "Английский язык"), ids.english),
                makeCategory(t("TOTE", "Безопасность жизнедеятельности (БЖД)"), ids.lifeSafety),
                makeCategory(t("TOTE", "Дизайн"), ids.design),
                makeCategory(t("TOTE", "Дипломатия"), ids.diplomacy),
                makeCategory(t("TOTE", "Эстетика"), ids.aesthetics),
                makeCategory(t("TOTE", "Этика"), ids.ethics),
                makeCategory(t("TOTE", "Журналистика и издательское дело"), ids.journalismAndPublishing),
                makeCategory(t("TOTE", "История"), ids.history),
                makeCategory(t("TOTE", "Краеведение"), ids.localAreaStudies),
                makeCategory(t("TOTE", "Культурология"), ids.culture),
                makeCategory(t("TOTE", "Лингвистика"), ids.linguistics),
                makeCategory(t("TOTE", "Литература зарубежная"), ids.foreignLiterature),
                makeCategory(t("TOTE", "Литература русский"), ids.russianLiterature),
                makeCategory(t("TOTE", "Литература украинский"), ids.ukrainianLiterature),
                makeCategory(t("TOTE", "Логика"), ids.logic),
                makeCategory(t("TOTE", "Искусство и культура"), ids.artAndCulture),
                makeCategory(t("TOTE", "Немецкий язык"), ids.german),
                makeCategory(t("TOTE", "Педагогика"), ids.pedagogy),
                makeCategory(t("TOTE", "Политология"), ids.politicalScience),
                makeCategory(t("TOTE", "Психология"), ids.psychology),
                makeCategory(t("TOTE", "Религиоведение, религия и мифология"), ids.religion),
                makeCategory(t("TOTE", "Риторика"), ids.rhetoric),
                makeCategory(t("TOTE", "Русский язык"), ids.russian),
                makeCategory(t("TOTE", "Социальная работа"), ids.socialWork),
                makeCategory(t("TOTE", "Социология"), ids.sociology),
                makeCategory(t("TOTE", "Стилистика"), ids.stylistics),
                makeCategory(t("TOTE", "Украинский язык"), ids.ukrainian),
                makeCategory(t("TOTE", "Физкультура и спорт"), ids.sportsAndFucking),
                makeCategory(t("TOTE", "Филология"), ids.philology),
                makeCategory(t("TOTE", "Философия"), ids.philosophy),
                makeCategory(t("TOTE", "Фонетика"), ids.phonetics),
                makeCategory(t("TOTE", "Французский язык"), ids.french)
            ))
        ))
        root.imposedIDToGenerate = ids.root
        saveCategoryTree(root)
    }

    private fun saveCategoryTree(root: UADocumentCategory) {
        val savedRoot = backPlatform.uaDocumentCategoryRepo.save(root)
        for (child in root.category.children) {
            child.category.parent = savedRoot
            saveCategoryTree(child)
        }
    }
}

//@RemoteProcedureFactory fun resetTestDatabaseAlongWithTemplate() = testProcedure(
//    {ResetTestDatabaseAlongWithTemplateRequest()},
//    runShit = fun(ctx, req): GenericResponse {
//        val templateDB = DB.byNameOnTestServer(req.templateDB.value)
//
//        if (req.recreateTemplate.value) {
//            templateDB.recreate()
//        }
//
//        DB.apsTestOnTestServer.recreate(template = templateDB)
//        return GenericResponse()
//    }
//)

@RemoteProcedureFactory fun serveGetSentEmails() = testProcedure(
    {RequestMatumba()},
    runShit = fun(ctx, req): GetSentEmailsRequest.Response {
        return GetSentEmailsRequest.Response(EmailMatumba.sentEmails)
    }
)

@RemoteProcedureFactory fun serveClearSentEmails() = testProcedure(
    {RequestMatumba()},
    runShit = fun(ctx, req): GenericResponse {
        EmailMatumba.sentEmails.clear()
        return GenericResponse()
    }
)

//@RemoteProcedureFactory fun worldPoint() = testProcedure(
//    {WorldPointRequest()},
//    runShit = fun(ctx, req): GenericResponse {
//        val oldRejectAllRequests = TestServerFiddling.rejectAllRequestsNeedingDB
//        TestServerFiddling.rejectAllRequestsNeedingDB = true
//
//        try {
//            val snapshotDBName = "world_point_${req.pointName.value}"
//
//            val databaseToCreate: String; val databaseToUseAsTemplate: String
//            when (req.action.value) {
//                WorldPointRequest.Action.SAVE -> {
//                    databaseToCreate = snapshotDBName
//                    databaseToUseAsTemplate = DB.apsTestOnTestServer.name
//                }
//                WorldPointRequest.Action.RESTORE -> {
//                    databaseToCreate = DB.apsTestOnTestServer.name
//                    databaseToUseAsTemplate = snapshotDBName
//                }
//            }
//
//            DB.apsTestOnTestServer.close()
//            DB.postgresOnTestServer.joo {
//                tracingSQL("Recreate database") {it.execute(""""
//                    drop database if exists "$databaseToCreate";
//                    create database "$databaseToCreate" template = "$databaseToUseAsTemplate";
//                """)}
//            }
//        } finally {
//            TestServerFiddling.rejectAllRequestsNeedingDB = oldRejectAllRequests
//        }
//        return GenericResponse()
//    }
//)

val backendInstanceID = "" + UUID.randomUUID()

@RemoteProcedureFactory fun getSoftwareVersion() = testProcedure(
    {GetSoftwareVersionRequest()},
    logRequestJSON = false,
    runShit = fun(ctx, req): GetSoftwareVersionRequest.Response {
        val path = Paths.get("${const.file.APS_HOME}/front/out/front-enhanced.js")
        val attrs = Files.readAttributes(path, BasicFileAttributes::class.java)
        return GetSoftwareVersionRequest.Response(
            ctime = "" + Math.max(attrs.creationTime().toMillis(), attrs.lastModifiedTime().toMillis()),
            backendInstanceID = backendInstanceID)
    }
)

@RemoteProcedureFactory fun getGeneratedShit() = testProcedure(
    {RequestMatumba()},
    runShit = fun(ctx, req): GetGeneratedShitRequest.Response {
        return GetGeneratedShitRequest.Response(GodServlet::class.java.getResource("generated-shit.js").readText())
    }
)


@RemoteProcedureFactory fun openSourceCode() = testProcedure(
    {OpenSourceCodeRequest()},
    runShit = fun(ctx, req): OpenSourceCodeRequest.Response {
        val sourceLocation = req.sourceLocation.value.replace("\\", "/")
        val firstColon = sourceLocation.indexOf(':')
        var afterLine = sourceLocation.indexOf(':', firstColon + 1)
        if (firstColon == -1) bitch("I want a colon in source location")
        if (afterLine == -1) afterLine = sourceLocation.length

        val filePartEnd = firstColon
        val filePart = sourceLocation.substring(0, filePartEnd)
        val file =
            if (filePart.startsWith("APS/")) {
                const.file.APS_HOME + "/" + filePart.substring("APS/".length)
            } else run {
                for (f in File("${const.file.APS_HOME}/back/src").walkTopDown())
                    if (f.name == filePart) return@run f.absolutePath
                bitch("Obscure backend file: $filePart")
            }
        val line = sourceLocation.substring(firstColon + 1, afterLine)

        val pb = ProcessBuilder()
        val cmd = pb.command()
        cmd.addAll(listOf(bconst.ideaExe, const.file.APS_HOME, "--line", line, file))
        dlog("Executing external command:", cmd.joinToString(" "))
        pb.inheritIO()
        val proc = pb.start()
        val exitCode = proc.waitFor()
        dlog("External command finished with code", exitCode)

        return OpenSourceCodeRequest.Response(error = if (exitCode == 0) null else "Bad exit code: $exitCode")
    }
)

//@RemoteProcedureFactory fun testSetUserFields() = testProcedure(
//    {TestSetUserFieldsRequest()},
//    needsDB = true,
//    runShit = fun(ctx, req): GenericResponse {
//        var step = tracingSQL("Update user") {ctx.q
//            .update(USERS)
//            .set(USERS.ID, USERS.ID)
//        }
//
//        req.state.let {if (it.specified) step = step.set(USERS.STATE, it.value.name)}
//        req.profileRejectionReason.let {if (it.specified) step = step.set(USERS.PROFILE_REJECTION_REASON, it.value)}
//        req.phone.let {if (it.specified) step = step.set(USERS.PHONE, it.value)}
//        req.aboutMe.let {if (it.specified) step = step.set(USERS.ABOUT_ME, it.value)}
//        req.banReason.let {if (it.specified) step = step.set(USERS.BAN_REASON, it.value)}
//        req.profileUpdatedAt.let {if (it.specified) step = step.set(USERS.PROFILE_UPDATED_AT, stringToStamp(it.value))}
//        req.insertedAt.let {if (it.specified) step = step.set(USERS.INSERTED_AT, stringToStamp(it.value))}
//
//        step
//            .where(USERS.EMAIL.eq(req.email.value))
//            .execute()
//        return GenericResponse()
//    }
//)

@RemoteProcedureFactory fun fuckingRemoteProcedure() = testProcedure(
    {FuckingRemoteProcedureRequest()},
    needsDB = false,
    runShit = fun (ctx, req): JSONResponse {
        val rmap = _shittyObjectMapper.readValue(req.json.value, Map::class.java)
        val proc: String = cast(rmap["proc"])

        val res: Any? = run {when (proc) {
            "loadTestShit" -> frp_loadTestShit(rmap)
            "updateTestShit" -> frp_updateTestShit(rmap)
            "robotClickOnChrome" -> frp_robotClickOnChrome(rmap)
            "robotTypeTextCRIntoWindowTitledOpen" -> frp_robotTypeTextCRIntoWindowTitledOpen(rmap)
            "ping" -> frp_ping(rmap)
            "resetLastDownloadedFile" -> frp_resetLastDownloadedFile(rmap)
            "getLastDownloadedPieceOfShit" -> frp_getLastDownloadedPieceOfShit(rmap)
//            "executeSQL" -> frp_executeSQL(ctx, rmap)
            "luceneParseRussian" -> frp_luceneParseRussian(rmap)
            else -> wtf("proc: $proc")

        }}

        return JSONResponse(_shittyObjectMapper.writeValueAsString(res))
    }
)

fun frp_resetLastDownloadedFile(rmap: Map<*, *>) {
    BackGlobus.lastDownloadedPieceOfShit = null
}

fun frp_getLastDownloadedPieceOfShit(rmap: Map<*, *>): PieceOfShitDownload? {
    return BackGlobus.lastDownloadedPieceOfShit
}

fun frp_loadTestShit(rmap: Map<*, *>): String? {
    val id: String = cast(rmap["id"])
    val file = File("${const.file.APS_HOME}/front/test-shit/$id")
    return if (file.exists()) file.readText()
    else null
}

fun frp_updateTestShit(rmap: Map<*, *>) {
    val id: String = cast(rmap["id"])
    val newValue: String = cast(rmap["newValue"])
    serveHardenScreenHTMLRequest(HardenScreenHTMLRequest()-{o->
        o.assertionID = id
        o.html = newValue
    })
}

fun frp_robotClickOnChrome(rmap: Map<*, *>) {
    val hwnd =
        User32.INSTANCE.FindWindow(null, "APS UA - Google Chrome")
        ?: User32.INSTANCE.FindWindow(null, "Writer UA - Google Chrome")
        ?: bitch("No necessary Chrome window")
    User32.INSTANCE.SetForegroundWindow(hwnd) || bitch("Cannot bring Chrome to foreground")
    val origLocation = MouseInfo.getPointerInfo().location
    val robot = Robot()
    robot.mouseMove(600, 190) // Somewhere in page (or modal, so it won't be closed!) title
    robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)
    robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
    robot.mouseMove(origLocation.x, origLocation.y)
}

fun frp_robotTypeTextCRIntoWindowTitledOpen(rmap: Map<*, *>) {
    val text: String = cast(rmap["text"])

    (1..3).forEach {
        val hwnd = User32.INSTANCE.FindWindow(null, "Open")
        if (hwnd == null) {
            Thread.sleep(500)
            return@forEach
        }

        User32.INSTANCE.SetForegroundWindow(hwnd) || bitch("Cannot bring Open window to foreground")
        Thread.sleep(100)
        robotTypeTextCR(text)
        return
    }

    bitch("I'm sick of waiting for Open window")
}

@RemoteProcedureFactory fun ping() = publicProcedure(
    {GenericRequest()},
    needsDB = false,
    runShit = fun(ctx, req): GenericResponse {
        return GenericResponse()
    }
)

fun frp_ping(rmap: Map<*, *>): String {
    return "pong"
}

//fun frp_executeSQL(ctx: ProcedureContext, rmap: Map<*, *>) {
//    val descr: String = cast(rmap["descr"])
//    val sql: String = cast(rmap["sql"])
//    tracingSQL(descr) {ctx.q.execute(sql)}
//}

private fun robotTypeTextCR(text: String) {
    val robot = Robot()
    text.forEach {c->
        // dwarnStriking("Typing key: " + c)
        var holdShift = c.isLetter() && c.isUpperCase()

        val keyCode = when {
            c.isLetterOrDigit() -> c.toUpperCase().toInt()
            else -> when (c) {
                ' ' -> KeyEvent.VK_SPACE
                ':' -> {holdShift = true; KeyEvent.VK_SEMICOLON}
                '.' -> KeyEvent.VK_PERIOD
                '/' -> KeyEvent.VK_SLASH
                '\\' -> KeyEvent.VK_BACK_SLASH
                else -> wtf("Dunno how to type key `$c`")
            }
        }

        if (holdShift)
            robot.keyPress(KeyEvent.VK_SHIFT)

        try {
            robot.keyPress(keyCode)
            robot.keyRelease(keyCode)
        } finally {
            if (holdShift)
                robot.keyRelease(KeyEvent.VK_SHIFT)
        }
    }
    robot.keyPress(KeyEvent.VK_ENTER)
    robot.keyRelease(KeyEvent.VK_ENTER)
}

fun frp_luceneParseRussian(rmap: Map<*, *>): List<LuceneParseToken> {
    val text: String = cast(rmap["text"])
    return luceneParse(text, russianAnalyzer)
}

fun serveHardenScreenHTMLRequest(req: HardenScreenHTMLRequest) {
    val file = File("${const.file.APS_HOME}/front/test-shit/${req.assertionID}")
    if (file.exists()) {
        file.copyTo(File("${bconst.tempBakDir}/${req.assertionID}"), true)
    }
    file.writeText(req.html)
}

//@RemoteProcedureFactory
//fun serveTestCopyOrderFileToArea() = adminProcedure(
//    {TestCopyOrderFileToAreaRequest()},
//    runShit = fun(ctx, req): TestCopyOrderFileToAreaRequest.Response {
//        val protoOrderFile: JQUaOrderFilesRecord = selectUAOrderFile(ctx, req.orderFileID.value.toLong())
//        val area: JQUaOrderAreasRecord = selectUAOrderAreaByName(ctx, protoOrderFile.uaOrderId, req.areaName.value)
//
//        val orderFileID = UA_ORDER_FILES.let {t->
//            ctx.insertShit("Insert order file", t) {it
//                .set(t.UA_ORDER_ID, protoOrderFile.uaOrderId)
//                .set(t.FILE_ID, protoOrderFile.fileId)
//                .set(t.UA_ORDER_AREA_ID, area.id)
//                .set(t.SEEN_AS_FROM, protoOrderFile.seenAsFrom)
//                .returnID(t)
//            }
//        }
//
//        insertFileUserPermission(ctx, protoOrderFile.fileId, req.permissionForUserID.value.toLong())
//
//        return TestCopyOrderFileToAreaRequest.Response(orderFileID.toString())
//    }
//)










