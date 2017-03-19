package aps


object const {

    val moreableChunkSize = 10

    object text {
        val numberSign = t("#", "№")
        val na = t("N/A", "ХЗ")

        object symbols {
            val rightDoubleAngleQuotation = "»"
            val rightDoubleAngleQuotationSpaced = " » "
            val nl2 = "\n\n"
            val nbsp: String = "" + 0xa0.toChar()
            val emdash = "—"
            val endash = "–"
            val threeQuotes = "\"\"\""
            val times = "×"
        }

        object shebang {
            val defaultCancelButtonTitle = t("Nah", "Не надо")
        }
    }

    object msg {
        val noItems = t("TOTE", "Савсэм ничего нэт, да...")
        val serviceFuckedUp = t("Service is temporarily fucked up, sorry", "Сервис временно в жопе, просим прощения")
    }

    object orderArea {
        val admin = "Admin"
        val customer = "Customer"
        val writer = "Writer"
    }

    object windowMessage {
        val whatsUp = "What's up?"
        val fileForbidden = "Fucking file is forbidden"
    }

//    object common {
//        val minTitleLen = 5
//        val maxTitleLen = 100
//        val minDetailsLen = 5
//        val maxDetailsLen = 2000
//    }
//
//    object order {
//        val minTitleLen = common.minTitleLen
//        val maxTitleLen = common.maxTitleLen
//        val minDetailsLen = common.minDetailsLen
//        val maxDetailsLen = common.maxDetailsLen
//    }
//
//    object file {
//        val minTitleLen = common.minTitleLen
//        val maxTitleLen = common.maxTitleLen
//        val minDetailsLen = common.minDetailsLen
//        val maxDetailsLen = common.maxDetailsLen
//    }

    val topNavbarHeight = 50.0

    object urlq {
        object test {
            val test = "test"
            val testSuite = "testSuite"
            val stopOnAssertions = "stopOnAssertions"
            val dontStopOnCorrectAssertions  = "dontStopOnCorrectAssertions"
            val animateUserActions = "animateUserActions"
            val handPauses = "handPauses"
        }
    }

    object elementID {
        val dynamicFooter = "dynamicFooter"
        val cutLineContainer = "cutLineContainer"
    }

    object productName {
        val uaCustomer = "APS UA"
        val uaWriter = "Writer UA"
    }

    object file {
        val APS_HOME get()= getenv("APS_HOME") ?: die("I want APS_HOME environment variable")
        val GENERATOR_BAK_DIR get()= "c:/tmp/aps-bak" // TODO:vgrechka @unhardcode
        val TMPDIR get()= getenv("TMPDIR") ?: die("I want TMPDIR environment variable")
        val APS_CLOUD_BACK_HOST get() = getenv("APS_CLOUD_BACK_HOST") ?: die("I want APS_CLOUD_BACK_HOST environment variable")
        val APS_TEMP get()= "c:/tmp/aps-tmp" // TODO:vgrechka @unhardcode

        val testFiles get()= "$APS_HOME/back/testfiles"
    }

    object userID {
//        val testScenario = -10L
        val anonymousCustomer = -20L
        val anonymousWriter = -30L
    }

    object uaDocumentCategoryID {
        val root = -10L
        val misc = -20L

        val economyGroup = -30L
        val auditing = -40L
        val banking = -50L
        val stockExchange = -60L
        val accounting = -70L
        val budgetSystem = -80L
        val currencyRegulation = -90L
        val currencyRelationships = -100L
        val moneyAndCredit = -110L
        val publicService = -120L
        val publicAdministration = -130L
        val publicFinances = -140L
        val documentManagement = -150L
        val econometrics = -160L
        val economy = -170L
        val enterpriseEconomics = -180L
        val laborEconomics = -190L
        val economicCybernetics = -200L
        val economicAnalysis = -210L
        val eCommerce = -220L
        val pr = -230L
        val foreignTradeActivities = -240L
        val investment = -250L
        val innovativeActivity = -260L
        val innovativeManagement = -270L
        val treasury = -280L
        val control = -290L
        val forestry = -300L
        val logistics = -310L
        val macroeconomics = -320L
        val marketingAndAdvertisement = -330L
        val management = -340L
        val customs = -350L
        val internationalEconomics = -360L
        val microeconomics = -370L
        val economicModeling = -380L
        val taxes = -390L
        val entrepreneurship = -400L
        val politicalEconomy = -410L
        val restaurantHotelBusinessAndConsumerService = -420L
        val securitiesMarket = -430L
        val locationOfProductiveForces = -440L
        val agriculture = -450L
        val standardizationAndQualityManagement = -460L
        val statistics = -470L
        val strategicManagement = -480L
        val insurance = -490L
        val commodityAndExpertise = -500L
        val tradeAndCommercialActivity = -510L
        val tourism = -520L
        val projectManagement = -530L
        val managementAccounting = -540L
        val finance = -550L
        val enterpriseFinance = -560L
        val financialAnalysis = -570L
        val financialManagement = -580L
        val pricing = -590L
        val naturalGroup = -600L
        val astronomy = -610L
        val biology = -620L
        val militaryFucking = -630L
        val geography = -640L
        val geodesy = -650L
        val geology = -660L
        val ecology = -670L
        val math = -680L
        val medicine = -690L
        val naturalHistory = -700L
        val pharmaceuticals = -710L
        val physics = -720L
        val chemistry = -730L
        val technicalGroup = -740L
        val aviationAndCosmonautics = -750L
        val architecture = -760L
        val databases = -770L
        val construction = -780L
        val electronics = -790L
        val electricalEngineering = -800L
        val informaticsAndComputing = -810L
        val informationSecurity = -820L
        val informationAnalyticalActivity = -830L
        val cybernetics = -840L
        val drawings = -850L
        val programming = -860L
        val technicalDesign = -870L
        val radioEngineering = -880L
        val theoreticalMechanics = -890L
        val theoryOfMechanismsAndMachines = -900L
        val heatEngineering = -910L
        val technologySystem = -920L
        val engineeringTechnology = -930L
        val cookingTechnology = -940L
        val transportConstruction = -950L
        val legalGroup = -960L
        val advocacy = -970L
        val administrativeLaw = -980L
        val arbitrationProceedings = -990L
        val economicLaw = -1000L
        val environmentalLaw = -1010L
        val housingLaw = -1020L
        val landLaw = -1030L
        val historyOfStateAndLaw = -1040L
        val constitutionalLaw = -1050L
        val corporateLaw = -1060L
        val forensics = -1070L
        val criminalLaw = -1080L
        val criminalProcess = -1090L
        val customsLaw = -1100L
        val internationalLaw = -1110L
        val municipalLaw = -1120L
        val notary = -1130L
        val businessLaw = -1140L
        val taxLaw = -1150L
        val law = -1160L
        val intellectualPropertyRights = -1170L
        val familyLaw = -1180L
        val insuranceLaw = -1190L
        val judicialAndLawEnforcementAgencies = -1200L
        val forensicMedicalExamination = -1210L
        val theoryOfStateAndLaw = -1220L
        val laborLaw = -1230L
        val financialLaw = -1240L
        val civilLaw = -1250L
        val humanitarianGroup = -1260L
        val analysisOfBankingActivities = -1270L
        val english = -1280L
        val lifeSafety = -1290L
        val design = -1300L
        val diplomacy = -1310L
        val aesthetics = -1320L
        val ethics = -1330L
        val journalismAndPublishing = -1340L
        val history = -1350L
        val localAreaStudies = -1360L
        val culture = -1370L
        val linguistics = -1380L
        val foreignLiterature = -1390L
        val russianLiterature = -1400L
        val ukrainianLiterature = -1410L
        val logic = -1420L
        val artAndCulture = -1430L
        val german = -1440L
        val pedagogy = -1450L
        val politicalScience = -1460L
        val psychology = -1470L
        val religion = -1480L
        val rhetoric = -1490L
        val russian = -1500L
        val socialWork = -1510L
        val sociology = -1520L
        val stylistics = -1530L
        val ukrainian = -1540L
        val sportsAndFucking = -1550L
        val philology = -1560L
        val philosophy = -1570L
        val phonetics = -1580L
        val french = -1590L
    }
}


