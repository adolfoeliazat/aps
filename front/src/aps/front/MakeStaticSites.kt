/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*
import into.kommon.*

object MakeStaticSites {
    class LS(val en: String, val ua: String)
    class Section(val title: LS, val content: LS)
    class HorizBulletItem(val glyph: String, val title: LS)
    class HrefBulletItem(val href: String, val title: String)
    class Testimonial(val name: LS, val img: String, val says: LS)
    class BlogItem(val listTitle: String, val title: String, val slug: String, val content: String)

    val React = aps.nodeRequire("react")
    val ReactDOMServer = aps.nodeRequire("react-dom/server")
    val fs = aps.nodeRequire("fs")
    val sh = aps.nodeRequire("shelljs")
    val minimist = aps.nodeRequire("minimist")

    lateinit var mode: Mode
    lateinit var lang: Language
    lateinit var out: String
    val DEPS_JS = "$APS_TEMP/deps.js"

//    @JsName("runShit")
//    fun runShit(argv: Array<String>) = async {
//        try {
//            console.log("pussy")
//            await(delay(3000))
//            console.log("boobs")
//            await(delay(3000))
//            console.log("ok")
//        } catch (e: dynamic) {
//            println(e.stack)
//        }
//    }

    @JsName("runShit")
    fun runShit(argv: Array<String>) = async {
        try {
            puidPrefix = "MakeStaticSites-"
            aps.global.React = React
            sh.config.fatal = true
            js("Error").stackTraceLimit = js("Infinity")

            val margv = minimist(argv)
            val modeString = margv.mode ?: wtf("Gimme fucking --mode")
            mode = when (modeString) {
                "debug" -> Mode.DEBUG
                "prod" -> Mode.PROD
                else -> wtf("modeString: $modeString")
            }
            println("Mode: $mode")

            val outString: String? = margv.out
            out = outString ?: "$APS_HOME/front/out/static"
            println("Out directory: $out")

            await(browserifyShit())
            await(makeWriterSite(Language.UA))
            await(makeCustomerSite(Language.UA, Currency.UAH))

            println("COOL")
        } catch (e: dynamic) { // run.js won't get it, because it's in promise
            println(e.stack)
        }
    }

    fun t(ls: LS): String = when (lang) {
        Language.EN -> ls.en
        Language.UA -> ls.ua
    }

    fun t(en: String, ua: String): String = t(LS(en, ua))

    fun makeWriterSite(lang: Language): Promisoid<Unit> = async {
        val siteName = "writer-${lang.name.toLowerCase()}"
        print("Making $siteName... ")
        this.lang = lang

        val root = "$out/$siteName"
        await(remakeDirAndCopyShit(root))

        val tabTitle = t(en="Writer", ua="Writer UA")

        fun writePage(name: String, highlightedItem: String? = null, content: ToReactElementable) {
            genericWritePage(name=name,
                             highlightedItem=highlightedItem,
                             root=root,
                             tabTitle=tabTitle,
                             lang=lang,
                             clientKind=ClientKind.UA_WRITER,
                             content=kdiv(className="container"){o->
                                 o- content
                             }.toReactElement())
        }

        writePage(name="index", content=kdiv{o->
            o- pageHeader(t(en="Welcome to Writer", ua="Приветствуем на Писце"))
            o- markdownPiece(
                en = """
                    Hey there! Listening to music, but can’t follow your favorite song, because there’s a research paper you’ve got to get done by tomorrow?

                    Sitting in front of your laptop, desperately typing the keywords in your search engine browser and wondering why AcademicPaperServed came up with an idea of giving written assignments to the needy students like yourself?

                    Can’t be effective in the workplace, because there’s a persuasive essay you’ve got to turn in early next week?

                    Now relax. You’ve just reached the right destination! At AcademicPaperServed writing papers is really very easy!

                    Professional custom papers writing service, research paper, essay and term paper writing service from experienced writers at affordable price.
                """,
                ua = """
                    Hey there! Listening to music, but can’t follow your favorite song, because there’s a research paper you’ve got to get done by tomorrow?

                    Sitting in front of your laptop, desperately typing the keywords in your search engine browser and wondering why AcademicPaperServed came up with an idea of giving written assignments to the needy students like yourself?

                    Can’t be effective in the workplace, because there’s a persuasive essay you’ve got to turn in early next week?

                    Now relax. You’ve just reached the right destination! At AcademicPaperServed writing papers is really very easy!

                    Professional custom papers writing service, research paper, essay and term paper writing service from experienced writers at affordable price.
                """)

            o- pageHeader(t(en="Features", ua="Фичи"))
            o- horizBulletsRow(listOf(
                HorizBulletItem("pencil", LS(en="No plagiarism", ua="No plagiarism")),
                HorizBulletItem("star", LS(en="Only premium quality", ua="Only premium quality")),
                HorizBulletItem("list", LS(en="Free title page, outline, list${symbols.nbsp}of${symbols.nbsp}references", ua="Free title page, outline, list${symbols.nbsp}of${symbols.nbsp}references"))
            ), horizContentMargin = 40)
            o- horizBulletsRow(listOf(
                HorizBulletItem("gi-piggy-bank", LS(en="One-time and life-time discounts to returning customers", ua="One-time and life-time discounts to returning customers")),
                HorizBulletItem("credit-card", LS(en="30-days money back guarantee", ua="30-days money back guarantee")),
                HorizBulletItem("life-saver", LS(en="24/7 support", ua="24/7 support"))
            ), horizContentMargin = 40)

            o- pageHeader(t(en="Who We Are", ua="Кто мы"))
            o- markdownPiece(
                en = """
                    AcademicPaperServed is an experienced custom writing service, consisting of expert writers to deliver written assignments, essays, research papers, term papers, theses, book reports, etc to English-speaking clients worldwide. Our team of professional writers focuses on the highest quality of all types of academic papers. Thesis writing is a challenge to many students. With our assistance it will not be a problem anymore!

                    Our writers are versed in various fields of academic writing, so if you do not find a suitable category on this page, feel free to contact our Support Center to find out availability of writing help in your area of interest.

                    We have access to most reliable and complete online libraries to make your research or essay unique.

                    AcademicPaperServed team consists of expert academic writers providing you with free guidelines, helping with topic selection, proofreading, editing and formatting even if you want to get your essay done overnight! We guarantee premium quality writing with urgent projects.
                """,
                ua = """
                    AcademicPaperServed is an experienced custom writing service, consisting of expert writers to deliver written assignments, essays, research papers, term papers, theses, book reports, etc to English-speaking clients worldwide. Our team of professional writers focuses on the highest quality of all types of academic papers. Thesis writing is a challenge to many students. With our assistance it will not be a problem anymore!

                    Our writers are versed in various fields of academic writing, so if you do not find a suitable category on this page, feel free to contact our Support Center to find out availability of writing help in your area of interest.

                    We have access to most reliable and complete online libraries to make your research or essay unique.

                    AcademicPaperServed team consists of expert academic writers providing you with free guidelines, helping with topic selection, proofreading, editing and formatting even if you want to get your essay done overnight! We guarantee premium quality writing with urgent projects.
                """)

            o- renderTestimonials(ClientKind.UA_WRITER)

            o- pageHeader(t(en="What We Offer", ua="Мы предлагаем"))
            o- horizBulletsRow(listOf(
                HorizBulletItem("envira", LS(en="Custom essay, research, thesis writing", ua="Custom essay, research, thesis writing")),
                HorizBulletItem("rocket", LS(en="Plagiarism-free original papers written from scratch", ua="Plagiarism-free original papers written from scratch")),
                HorizBulletItem("bomb", LS(en="Proofreading and editing of written papers", ua="Proofreading and editing of written papers")),
                HorizBulletItem("book", LS(en="Free guidelines on essay topic selection and writing process", ua="Free guidelines on essay topic selection and writing process"))
            ))
            o- locBullets(listOf(
                LS(en="Custom essay, research paper, book report, term paper, precis, sketch, poetry analysis, data collection, thesis writing, SWOT analysis, lab reports, dissertations, reviews, speeches, presentations, case studies, courseworks, homeworks, assignments, creative writing, blog writing, capstone project, grant proposal, lab reports", ua="Custom essay, research paper, book report, term paper, precis, sketch, poetry analysis, data collection, thesis writing, SWOT analysis, lab reports, dissertations, reviews, speeches, presentations, case studies, courseworks, homeworks, assignments, creative writing, blog writing, capstone project, grant proposal, lab reports"),
                LS(en="Plagiarism-free original papers written from scratch", ua="Plagiarism-free original papers written from scratch"),
                LS(en="Proofreading and editing of written papers", ua="Proofreading and editing of written papers"),
                LS(en="Choosing sources for your paper, providing with annotated bibliography upon request", ua="Choosing sources for your paper, providing with annotated bibliography upon request"),
                LS(en="Free guidelines on successful essay topic selection and writing process", ua="Free guidelines on successful essay topic selection and writing process"),
                LS(en="Individual approach to every customer, no repetitions, free consulting on the paper content", ua="Individual approach to every customer, no repetitions, free consulting on the paper content"),
                LS(en="Free revisions till you are completely satisfied", ua="Free revisions till you are completely satisfied"),
                LS(en="Meeting your deadline", ua="Meeting your deadline"),
                LS(en="Security and confidentiality", ua="Security and confidentiality")
            ))
        })

        writePage(name="why", highlightedItem="why", content=kdiv{o->
            o- pageHeader(t(en="Why Writer?", ua="Почему Писец?"))

            fun add(section: Section) =o- kdiv{o->
                o- h3Smaller(t(section.title))
                o- markdown(dedent(t(section.content)))
            }

            add(Section(
                title = LS(en="We care about each customer’s academic success", ua="We care about each customer’s academic success"),
                content = LS(
                    en = "According to the statistics, almost 90% of current U.S. undergraduate and graduate students work either full-time or part-time, while they study. Often, they have little time to complete all of their essays and term papers by themselves, as writing is really time consuming. Others simply don't think of writing as the most pleasurable activity. All students aim for graduation. AcademicPaperServed is here to help them reach this goal.",
                    ua = "According to the statistics, almost 90% of current U.S. undergraduate and graduate students work either full-time or part-time, while they study. Often, they have little time to complete all of their essays and term papers by themselves, as writing is really time consuming. Others simply don't think of writing as the most pleasurable activity. All students aim for graduation. AcademicPaperServed is here to help them reach this goal.")))

            add(Section(
                title = LS(en="We make a very strong commitment to quality", ua="We make a very strong commitment to quality"),
                content = LS(
                    en = "Quality is one of the major benefits of our custom writing service. Every AcademicPaperServed writer is experienced in the special field of studies. As a result, all of the customer's most sophisticated requirements are met by the professional freelance writer with a deep knowledge of the custom paper topic. Our custom essay writers possess Bachelor's / Master's degrees in the related fields of knowledge.",
                    ua = "Quality is one of the major benefits of our custom writing service. Every AcademicPaperServed writer is experienced in the special field of studies. As a result, all of the customer's most sophisticated requirements are met by the professional freelance writer with a deep knowledge of the custom paper topic. Our custom essay writers possess Bachelor's / Master's degrees in the related fields of knowledge.")))

            add(Section(
                title = LS(en="We show an individual approach", ua="We show an individual approach"),
                content = LS(
                    en = """
                        To Every Customer and Every Project we deal with.

                        With us customers are sure to get their high school, college and university papers tailored exactly to their requirements and needs. No need to worry about quality! Your professor will be completely satisfied. We follow paper details specifically, provide with relevant sources from our library and/or use definite sources (books, journal and magazine articles, encyclopedia excerpts, peer reviewed studies, etc.) requested by the customer.

                        Our team is aware of students’ concerns and issues. Any academic achievement becomes joyful and easy with our team. You are sure to get your writer’s comments on the paper content, advice on choosing a suitable topic, primary and secondary sources selection, ways of presenting it in pubic if needed, etc. You are a getting a complex academic help along with the written coherent text which you, surely, deserve!

                        24/7 support will provide you with prompt unrivalled assistance in every matter of your concern and makes sure you’ll get excellent quality paper!

                        AcademicPaperServed team helps English speaking customers from all over the world with anything an academic writing implies, from research ideas to styles and design of your paper. We are strictly following the rules of academic presentation combining it with modern and creative approaches. You are sure to get your project written in a plain, clear language with smooth transitions and coherent rendering.
                    """,
                    ua = """
                        To Every Customer and Every Project we deal with.

                        With us customers are sure to get their high school, college and university papers tailored exactly to their requirements and needs. No need to worry about quality! Your professor will be completely satisfied. We follow paper details specifically, provide with relevant sources from our library and/or use definite sources (books, journal and magazine articles, encyclopedia excerpts, peer reviewed studies, etc.) requested by the customer.

                        Our team is aware of students’ concerns and issues. Any academic achievement becomes joyful and easy with our team. You are sure to get your writer’s comments on the paper content, advice on choosing a suitable topic, primary and secondary sources selection, ways of presenting it in pubic if needed, etc. You are a getting a complex academic help along with the written coherent text which you, surely, deserve!

                        24/7 support will provide you with prompt unrivalled assistance in every matter of your concern and makes sure you’ll get excellent quality paper!

                        AcademicPaperServed team helps English speaking customers from all over the world with anything an academic writing implies, from research ideas to styles and design of your paper. We are strictly following the rules of academic presentation combining it with modern and creative approaches. You are sure to get your project written in a plain, clear language with smooth transitions and coherent rendering.
                    """)))

            add(Section(
                title = LS(en="We hire only expert writers", ua="We hire only expert writers"),
                content = LS(
                    en = """
                        We involve only experienced staff in the writing process! Each member of the AcademicPaperServed writing team has at least 2 years + experience in academic research field.

                        You are sure to get your custom paper done by a native English speaker, who is versed in the area of your research, formats your paper according to the latest requirements of citation styles (MLA, APA, Harvard, Chicago, Turabian, Oxford and others) and delivers with no delays.

                        US-based customer support will offer samples of various academic papers for your consideration.

                        We’ve been dealing with ALL types of academic papers and we are always glad to offer you help with any essay (like scholarship essay, admission essay, application essay, entrance essay, personal statement) and research paper type at any academic level and in every possible discipline. If you want your paper to be well-organized and logically constructed, choose us!
                    """,
                    ua = """
                        We involve only experienced staff in the writing process! Each member of the AcademicPaperServed writing team has at least 2 years + experience in academic research field.

                        You are sure to get your custom paper done by a native English speaker, who is versed in the area of your research, formats your paper according to the latest requirements of citation styles (MLA, APA, Harvard, Chicago, Turabian, Oxford and others) and delivers with no delays.

                        US-based customer support will offer samples of various academic papers for your consideration.

                        We’ve been dealing with ALL types of academic papers and we are always glad to offer you help with any essay (like scholarship essay, admission essay, application essay, entrance essay, personal statement) and research paper type at any academic level and in every possible discipline. If you want your paper to be well-organized and logically constructed, choose us!
                    """)))

            add(Section(
                title = LS(en="Plagiarism-free and full-of-creativity zone", ua="Plagiarism-free and full-of-creativity zone"),
                content = LS(
                    en = """
                        Our writers get paid only when they use their own thoughts and ideas, elucidated through high-quality research and writing proficiency. For that reason, the following is not acceptable under any situation: copy-pasting from websites, copying from offline books, texts, and journals, basing one’s work on the ideas/structure of others or previous pieces of work. All papers are checked against millions of hard copy sources, billions of web pages, and countless pieces of work of other researchers. The papers are checked with the most up-to-date plagiarism detection software prior to being delivered to customer. There is totally no chance of delivering a plagiarized paper.

                        At AcademicPaperServed all writers apply a creative approach to every essay. Each custom paper is written in compliance with the requests and desires of the customer.

                        Be assured our friendly and knowledgeable staff will provide you with immediate, top-quality, and US-based customer support. All responses are personalized to the needs of the student.
                    """,
                    ua = """
                        Our writers get paid only when they use their own thoughts and ideas, elucidated through high-quality research and writing proficiency. For that reason, the following is not acceptable under any situation: copy-pasting from websites, copying from offline books, texts, and journals, basing one’s work on the ideas/structure of others or previous pieces of work. All papers are checked against millions of hard copy sources, billions of web pages, and countless pieces of work of other researchers. The papers are checked with the most up-to-date plagiarism detection software prior to being delivered to customer. There is totally no chance of delivering a plagiarized paper.

                        At AcademicPaperServed all writers apply a creative approach to every essay. Each custom paper is written in compliance with the requests and desires of the customer.

                        Be assured our friendly and knowledgeable staff will provide you with immediate, top-quality, and US-based customer support. All responses are personalized to the needs of the student.
                    """)))
        })

        writePage(name="prices", highlightedItem="prices", content=kdiv{o->
            o- pageHeader(t(en="Our Prices", ua="Наши цены"))
            o- markdownPiece(
                en = """
                    Здесь такие идут расценки для писателей.
                """,
                ua = """
                    Здесь такие идут расценки для писателей.
                """)

            o- pageHeader(t(en="Pricing Policy", ua="Pricing Policy"))
            o- markdownPiece(
                en = """
                    Prices at AcademicPaperServed are set to meet the average point on the current custom writing market. This enables us to choose experts writers as freelance members in the team, who are able to meet the highest demands at faculties teaching in English.

                    Our support is daily queried on how one can buy an essay or research paper on a certain topic. AcademicPaperServed does sell pre-written papers, each assignment is made individually and each research is written from scratch. Top-notch writing quality is only achieved through effective communication of the writer and the customer, be it an order on a blog article, high school debate project or a music review.

                    Each quote we give to our customer is based primarily on the delivery date opted, type of an academic written assignment, number of pages and the amount of discount your membership is liable to.

                    You may find out your quote in one minute! Simply press the Order Your Custom Essay Now tab at the top of the page marked in green. Our Customer Support employee will advise you on the price and terms of delivery.
                """,
                ua = """
                    Prices at AcademicPaperServed are set to meet the average point on the current custom writing market. This enables us to choose experts writers as freelance members in the team, who are able to meet the highest demands at faculties teaching in English.

                    Our support is daily queried on how one can buy an essay or research paper on a certain topic. AcademicPaperServed does sell pre-written papers, each assignment is made individually and each research is written from scratch. Top-notch writing quality is only achieved through effective communication of the writer and the customer, be it an order on a blog article, high school debate project or a music review.

                    Each quote we give to our customer is based primarily on the delivery date opted, type of an academic written assignment, number of pages and the amount of discount your membership is liable to.

                    You may find out your quote in one minute! Simply press the Order Your Custom Essay Now tab at the top of the page marked in green. Our Customer Support employee will advise you on the price and terms of delivery.
                """)

            o- pageHeader(t(en="Bonuses", ua="Bonuses"))
            o- markdownPiece(
                en = "Ordering a paper at AcademicPaperServed, you also get:",
                ua = "Ordering a paper at AcademicPaperServed, you also get:")

            o- locBullets(listOf(
                LS(en="Free Title Page", ua="Free Title Page"),
                LS(en="Free Outline", ua="Free Outline"),
                LS(en="Free List of References", ua="Free List of References"),
                LS(en="Free Plagiarism Report (upon additional request)", ua="Free Plagiarism Report (upon additional request)")))

            o- pageHeader(t(en="Discount Policy", ua="Discount Policy"))
            o- markdownPiece(
                en = "Please note that each AcademicPaperServed customer is eligible for one-time and life-time discounts. One-time discount is granted to each new customer registered in our system and makes 5% off the first order total. Lifetime discount is provided to each returning customer depending on the total number of pages (on multiple papers) ordered since the moment of registration, and namely:",
                ua = "Please note that each AcademicPaperServed customer is eligible for one-time and life-time discounts. One-time discount is granted to each new customer registered in our system and makes 5% off the first order total. Lifetime discount is provided to each returning customer depending on the total number of pages (on multiple papers) ordered since the moment of registration, and namely:")

            o- locBullets(listOf(
                LS(en="More than 50 pages${symbols.mdash}5%", ua="Более 50 страниц ${symbols.ndash} 5%"),
                LS(en="More than 100 pages${symbols.mdash}10%", ua="Более 100 страниц ${symbols.ndash} 10%"),
                LS(en="More than 150 pages${symbols.mdash}15%", ua="Более 150 страниц ${symbols.ndash} 15%"),
                LS(en="More than 200 pages${symbols.mdash}30%", ua="Более 200 страниц ${symbols.ndash} 30%")))

            o- markdownPiece(
                en = "We’re sure that at AcademicPaperServed we employ a fair discount policy. We respect each certain customer and hope to establish long-term cooperation with him/her. Since customers are our most valued asset, we put a lot of effort to retaining and satisfying them through our flexible lifetime discount policy.",
                ua = "We’re sure that at AcademicPaperServed we employ a fair discount policy. We respect each certain customer and hope to establish long-term cooperation with him/her. Since customers are our most valued asset, we put a lot of effort to retaining and satisfying them through our flexible lifetime discount policy." )
        })

        writePage(name="faq", highlightedItem="faq", content=kdiv{o->
            o- pageHeader(t(en="FAQ", ua="Частые вопросы"))

            fun add(section: Section) =o- kdiv{o->
                o- markdownPiece("> " + t(section.title))
                o- kdiv(marginBottom=20, marginTop=-5){o->
                    o- markdown(dedent(t(section.content)))
                }
            }

            add(Section(
                title = LS(en="How does AcademicPaperServed work?", ua="How does AcademicPaperServed work?"),
                content = LS(
                    en = "As soon as you order a custom essay, term paper, research paper or book report your order is reviewed and then the most appropriate writer is assigned, who then takes responsibility to complete it. After your paper is ready, it is send to you via e-mail.",
                    ua = "As soon as you order a custom essay, term paper, research paper or book report your order is reviewed and then the most appropriate writer is assigned, who then takes responsibility to complete it. After your paper is ready, it is send to you via e-mail.")))

            add(Section(
                title = LS(en="I have had unpleasant experiences with other custom essay companies. How does AcademicPaperServed differ from fraud services?", ua="I have had unpleasant experiences with other custom essay companies. How does AcademicPaperServed differ from fraud services?"),
                content = LS(
                    en = "Sorry to say, but we often hear from our clients, that they have been deceived by unknown and disreputable essay writing companies that harm the reputation of legitimate writing companies. On the contrary, our service has done its best to earn the trust of our clients by offering quality custom papers at reasonable price.",
                    ua = "Sorry to say, but we often hear from our clients, that they have been deceived by unknown and disreputable essay writing companies that harm the reputation of legitimate writing companies. On the contrary, our service has done its best to earn the trust of our clients by offering quality custom papers at reasonable price.")))

            add(Section(
                title = LS(en="How safe is your service? Is there any risk to place an order online?", ua="How safe is your service? Is there any risk to place an order online?"),
                content = LS(
                    en = "It is totally safe. Hundreds of our customers make orders and buy custom writing services at AcademicPaperServed daily. All of your transactions are processed securely and encrypted by PayPal. It is impossible to make an unauthorized transaction using your credit card.",
                    ua = "It is totally safe. Hundreds of our customers make orders and buy custom writing services at AcademicPaperServed daily. All of your transactions are processed securely and encrypted by PayPal. It is impossible to make an unauthorized transaction using your credit card.")))

            add(Section(
                title = LS(en="What are your policies concerning the paper format and citation?", ua="What are your policies concerning the paper format and citation?"),
                content = LS(
                    en = """
                        Our writers use Microsoft Word .doc format by default (Times New Roman, 12, double-spaced, ~ 250-300 words/page). However, any format specified by the customer is available. The same concerns citation style.,

                        If not chosen, the writer chooses a citation style most appropriate for the written assignment in process. The latest edition with newest formatting style and its rules is applied, unless indicated otherwise.
                    """,
                    ua = """
                        Our writers use Microsoft Word .doc format by default (Times New Roman, 12, double-spaced, ~ 250-300 words/page). However, any format specified by the customer is available. The same concerns citation style.

                        If not chosen, the writer chooses a citation style most appropriate for the written assignment in process. The latest edition with newest formatting style and its rules is applied, unless indicated otherwise.
                    """)))

            add(Section(
                title = LS(en="What if I don’t like my paper and it does not meet the requirements?", ua="What if I don’t like my paper and it does not meet the requirements?"),
                content = LS(
                    en = "After you have ordered a paper, you can be confident that it will meet your expectations. Our writers will scrupulously adhere to your exact instructions to write a custom, first-rate academic paper. The team of writers works directly from customers’ instructions, and our clients get what they ask for. Still, there are moments when a customer can feel the writer has missed any of his/her requirements. In this case the customer should request a free revision. The writer will improve the paper and include all the instructions and will not stop working until the client is happy.",
                    ua = "After you have ordered a paper, you can be confident that it will meet your expectations. Our writers will scrupulously adhere to your exact instructions to write a custom, first-rate academic paper. The team of writers works directly from customers’ instructions, and our clients get what they ask for. Still, there are moments when a customer can feel the writer has missed any of his/her requirements. In this case the customer should request a free revision. The writer will improve the paper and include all the instructions and will not stop working until the client is happy.")))

            add(Section(
                title = LS(en="Does your service provide refunds?", ua="Does your service provide refunds?"),
                content = LS(
                    en = "Our aim is total customer satisfaction. According to our monthly data we have an acceptable 95% approval rating among our customers. Our writers work hard to do what is best for our customers. If the final version of the paper does not meet customer’s specified criteria, he/she can ask for a revision within thirty days. The writer will add any missing requirements and deliver the revision in 24 hours. If the customer provides us with evidence that the final revised version is still lacking some of the specified requirements, s/he can request a refund within three days after the completion of the order.",
                    ua = "Our aim is total customer satisfaction. According to our monthly data we have an acceptable 95% approval rating among our customers. Our writers work hard to do what is best for our customers. If the final version of the paper does not meet customer’s specified criteria, he/she can ask for a revision within thirty days. The writer will add any missing requirements and deliver the revision in 24 hours. If the customer provides us with evidence that the final revised version is still lacking some of the specified requirements, s/he can request a refund within three days after the completion of the order.")))

            add(Section(
                title = LS(en="What if my paper is found to be plagiarized?", ua="What if my paper is found to be plagiarized?"),
                content = LS(
                    en = "In fact, it is impossible. The writer gets paid only when he uses his own thoughts and ideas, elucidated through high-quality research/ and writing proficiency. For that reason, the following is not acceptable under any situation: copy-pasting from websites, copying from offline books, texts, and journals, basing one’s work on the ideas/structure of others or previous pieces of work. All papers are checked against millions of hard copy sources, billions of web pages, and countless pieces of work of other researchers. The papers are checked with most up to date anti-plagiarism software before sending any paper to customer. There is totally no chance of delivering a plagiarized paper.",
                    ua = "In fact, it is impossible. The writer gets paid only when he uses his own thoughts and ideas, elucidated through high-quality research/ and writing proficiency. For that reason, the following is not acceptable under any situation: copy-pasting from websites, copying from offline books, texts, and journals, basing one’s work on the ideas/structure of others or previous pieces of work. All papers are checked against millions of hard copy sources, billions of web pages, and countless pieces of work of other researchers. The papers are checked with most up to date anti-plagiarism software before sending any paper to customer. There is totally no chance of delivering a plagiarized paper.")))

            add(Section(
                title = LS(en="Can I check my order status?", ua="Can I check my order status?"),
                content = LS(
                    en = "In order to track the order status the customer can communicate with the writer or upload supplementary files or instructions.",
                    ua = "In order to track the order status the customer can communicate with the writer or upload supplementary files or instructions.")))

            add(Section(
                title = LS(en="Can I contact the writer?", ua="Can I contact the writer?"),
                content = LS(
                    en = "Certainly, there is a personal message board on each private account. A customer can easily post a message to the assigned writer, and he/she will reply to almost immediately. If the writer encounters any questions, he will contact the customer the in same way.",
                    ua = "Certainly, there is a personal message board on each private account. A customer can easily post a message to the assigned writer, and he/she will reply to almost immediately. If the writer encounters any questions, he will contact the customer the in same way.")))

            add(Section(
                title = LS(en="Can I ever find my ordered paper being available to the public?", ua="Can I ever find my ordered paper being available to the public?"),
                content = LS(
                    en = "Our service guarantees that the paper that has been written by our writers will never be available to the public. All papers are stored under an exclusively developed security system.",
                    ua = "Our service guarantees that the paper that has been written by our writers will never be available to the public. All papers are stored under an exclusively developed security system.")))

            add(Section(
                title = LS(en="How can I make sure the writer has understood my assignment correctly?", ua="How can I make sure the writer has understood my assignment correctly?"),
                content = LS(
                    en = """
                        When you start an ordering process, please, make sure you provide your writer with all necessary info, instructions, guidelines, additional materials for study, your class notes if any, and your personal preferences as to the writing style and paper formatting. It will surely facilitate the writing process and advance your chances in getting a higher grade.

                        We specifically created an easy-to-use interface for your communication with the writer and support center in the process of paper writing.

                        AcademicPaperServed writers’ team is highly flexible and is ready to consider every order on a personal basis. We value our customers and set communication as a priority. We will appreciate your help in giving explicit order details once your order is made.
                    """,
                    ua = """
                        When you start an ordering process, please, make sure you provide your writer with all necessary info, instructions, guidelines, additional materials for study, your class notes if any, and your personal preferences as to the writing style and paper formatting. It will surely facilitate the writing process and advance your chances in getting a higher grade.

                        We specifically created an easy-to-use interface for your communication with the writer and support center in the process of paper writing.

                        AcademicPaperServed writers’ team is highly flexible and is ready to consider every order on a personal basis. We value our customers and set communication as a priority. We will appreciate your help in giving explicit order details once your order is made.
                    """)))

            add(Section(
                title = LS(en="What if my professor requires to hand in all copies of sources used?", ua="What if my professor requires to hand in all copies of sources used?"),
                content = LS(
                    en = "The writers will provide you with the necessary sources used to write your paper if you request it in advance. Sometimes we use paid online libraries providing access per day/hour, in this case it is extremely difficult to come back there again and copy-paste material. Some libraries have copyright protection software, so cannot always copy the text for you. However, your timely request for the sources package will give us direction in a library choice. ",
                    ua = "The writers will provide you with the necessary sources used to write your paper if you request it in advance. Sometimes we use paid online libraries providing access per day/hour, in this case it is extremely difficult to come back there again and copy-paste material. Some libraries have copyright protection software, so cannot always copy the text for you. However, your timely request for the sources package will give us direction in a library choice. ")))
        })

        for (name in writerDynamicPageNames()) {
            writePage(name=name, content=wholePageTicker().toToReactElementable())
        }

        println("DONE")
    }

    fun browserifyShit(): Promisoid<Unit> = async {
        sh.rm("-f", DEPS_JS)

        val entryStream = js("new (require('stream')).Readable")
        entryStream.push("""
            global.lodash = require('lodash')
            global.React = require('react')
            global.ReactDOM = require('react-dom')
            global.ReactDOMServer = require('react-dom/server')
            global.moment = require('moment-timezone')
            require('moment/locale/ru')
            global.Tether = require('tether')
            global.markdownIt = require('markdown-it')

            ${if (mode == Mode.DEBUG) """
                global.superagent = require('superagent')
                global.nodeUtil = require('util')
                global.JsDiff = require('diff')
                global.deepEql = require('deep-eql')
            """ else ""}
        """)
        entryStream.push(null) // EOF

        val bro = (aps.nodeRequire("browserify"))(json(
            "entries" to jsArrayOf(entryStream),
            "cache" to json(),
            "packageCache" to json(),
            "debug" to true
        ))

        aps.process.stdout.write("Browserifying shit... ")
        awaitNative<Unit>(aps.newNativePromise {resolve: dynamic, reject: dynamic ->
            bro.bundle {err: dynamic, buf: dynamic ->
                if (err != null) {
                    reject(err)
                } else {
                    val code = buf.toString()
                    fs.writeFileSync(DEPS_JS, code)
                    resolve()
                }
            }
        })

        println("DONE")
    }

    fun remakeDirAndCopyShit(root: String): Promisoid<Unit> = async {
        sh.rm("-rf", root)
        sh.mkdir("-p", root)

        val nodeModules = "$APS_HOME/node_modules"
        sh.cp("$nodeModules/jquery/dist/jquery.min.js", root)
        sh.cp("-r", "$nodeModules/bootstrap/dist", "$root/bootstrap")
        sh.cp("$APS_HOME/front/static/hack/bootstrap-3.3.7-hacked.js", "$root/bootstrap/js")
        sh.cp("$APS_HOME/front/static/hack/bootstrap-datetimepicker-4.17.43-hacked.js", root)
        sh.cp("$APS_HOME/front/static/hack/bootstrap-datetimepicker-4.17.43-hacked.css", root)
        sh.mkdir("$root/font-awesome")
        sh.cp("-r", "$nodeModules/font-awesome/css", "$root/font-awesome")
        sh.cp("-r", "$nodeModules/font-awesome/fonts", "$root/font-awesome")
        sh.cp("$APS_HOME/front/static/asset/*", root)
        sh.cp(DEPS_JS, root)

        // TODO:vgrechka @duplication cb0e7275-0ce9-4819-9d5d-fdea8a37dfda
        sh.cp("$APS_HOME/front/out/front-enhanced.js", root)
        sh.cp("$APS_HOME/front/out/front.js.map", root)
        sh.cp("$APS_HOME/front/out/lib/kotlin.js", root)
    }

    fun wholePageTicker(): ReactElement {
        return rawHTML("""
            <div class="container">
            <div style="display: flex; align-items: center; justify-content: center; position: absolute; left: 0px; top: 200px; width: 100%;">
            <span style="margin-left: 10">${t(en="Breathe slowly...", ua="Дышите глубоко...")}</span>
            <div id="wholePageTicker" class="progressTicker" style="background-color: ${Color.BLUE_GRAY_600}; width: 14px; height: 28px; margin-left: 10px; margin-top: -5px"></div>
            </div>
            </div>""")
    }

    fun makeCustomerSite(lang: Language, currency: Currency): Promisoid<Unit> = async {
        val siteName = "customer-${lang.name.toLowerCase()}"
        print("Making $siteName... ")
        this.lang = lang

        val root = "$out/$siteName"
        await(remakeDirAndCopyShit(root))

        val tabTitle = t(en="APS", ua="APS UA")

        fun writePage(name: String, highlightedItem: String? = null, content: ToReactElementable) {
            genericWritePage(name=name,
                             highlightedItem=highlightedItem,
                             root=root,
                             tabTitle=tabTitle,
                             lang=lang,
                             clientKind=ClientKind.UA_CUSTOMER,
                             content=kdiv(className="container"){o->
                                 o- content
                             }.toReactElement())
        }

        writePage(name="index", content=kdiv{o->
            o- pageHeader(t(en="Welcome to AcademicPaperServed", ua="Welcome to AcademicPaperServed UA"))
            o- markdownPiece(
                en = """
                    Hey there! Listening to music, but can’t follow your favorite song, because there’s a research paper you’ve got to get done by tomorrow?

                    Sitting in front of your laptop, desperately typing the keywords in your search engine browser and wondering why AcademicPaperServed came up with an idea of giving written assignments to the needy students like yourself?

                    Can’t be effective in the workplace, because there’s a persuasive essay you’ve got to turn in early next week?

                    Now relax. You’ve just reached the right destination! At AcademicPaperServed writing papers is really very easy!

                    Professional custom papers writing service, research paper, essay and term paper writing service from experienced writers at affordable price.
                """,
                ua = """
                    Hey there! Listening to music, but can’t follow your favorite song, because there’s a research paper you’ve got to get done by tomorrow?

                    Sitting in front of your laptop, desperately typing the keywords in your search engine browser and wondering why AcademicPaperServed came up with an idea of giving written assignments to the needy students like yourself?

                    Can’t be effective in the workplace, because there’s a persuasive essay you’ve got to turn in early next week?

                    Now relax. You’ve just reached the right destination! At AcademicPaperServed writing papers is really very easy!

                    Professional custom papers writing service, research paper, essay and term paper writing service from experienced writers at affordable price.
                """)

            o- pageHeader(t(en="Features", ua="Features"))
            o- horizBulletsRow(listOf(
                HorizBulletItem("pencil", LS(en="No plagiarism", ua="No plagiarism")),
                HorizBulletItem("star", LS(en="Only premium quality", ua="Only premium quality")),
                HorizBulletItem("list", LS(en="Free title page, outline, list${symbols.nbsp}of${symbols.nbsp}references", ua="Free title page, outline, list${symbols.nbsp}of${symbols.nbsp}references"))
            ), horizContentMargin = 40)
            o- horizBulletsRow(listOf(
                HorizBulletItem("gi-piggy-bank", LS(en="One-time and life-time discounts to returning customers", ua="One-time and life-time discounts to returning customers")),
                HorizBulletItem("credit-card", LS(en="30-days money back guarantee", ua="30-days money back guarantee")),
                HorizBulletItem("life-saver", LS(en="24/7 support", ua="24/7 support"))
            ), horizContentMargin = 40)

            o- pageHeader(t(en="Who We Are", ua="Who We Are"))
            o- markdownPiece(
                en = """
                    AcademicPaperServed is an experienced custom writing service, consisting of expert writers to deliver written assignments, essays, research papers, term papers, theses, book reports, etc to English-speaking clients worldwide. Our team of professional writers focuses on the highest quality of all types of academic papers. Thesis writing is a challenge to many students. With our assistance it will not be a problem anymore!

                    Our writers are versed in various fields of academic writing, so if you do not find a suitable category on this page, feel free to contact our Support Center to find out availability of writing help in your area of interest.

                    We have access to most reliable and complete online libraries to make your research or essay unique.

                    AcademicPaperServed team consists of expert academic writers providing you with free guidelines, helping with topic selection, proofreading, editing and formatting even if you want to get your essay done overnight! We guarantee premium quality writing with urgent projects.
                """,
                ua = """
                    AcademicPaperServed is an experienced custom writing service, consisting of expert writers to deliver written assignments, essays, research papers, term papers, theses, book reports, etc to English-speaking clients worldwide. Our team of professional writers focuses on the highest quality of all types of academic papers. Thesis writing is a challenge to many students. With our assistance it will not be a problem anymore!

                    Our writers are versed in various fields of academic writing, so if you do not find a suitable category on this page, feel free to contact our Support Center to find out availability of writing help in your area of interest.

                    We have access to most reliable and complete online libraries to make your research or essay unique.

                    AcademicPaperServed team consists of expert academic writers providing you with free guidelines, helping with topic selection, proofreading, editing and formatting even if you want to get your essay done overnight! We guarantee premium quality writing with urgent projects.
                """)

            o- renderTestimonials(ClientKind.UA_CUSTOMER)

            o- pageHeader(t(en="What We Offer", ua="What We Offer"))
            o- horizBulletsRow(listOf(
                HorizBulletItem("envira", LS(en="Custom essay, research, thesis writing", ua="Custom essay, research, thesis writing")),
                HorizBulletItem("rocket", LS(en="Plagiarism-free original papers written from scratch", ua="Plagiarism-free original papers written from scratch")),
                HorizBulletItem("bomb", LS(en="Proofreading and editing of written papers", ua="Proofreading and editing of written papers")),
                HorizBulletItem("book", LS(en="Free guidelines on essay topic selection and writing process", ua="Free guidelines on essay topic selection and writing process"))
            ))
            o- locBullets(listOf(
                LS(en="Custom essay, research paper, book report, term paper, precis, sketch, poetry analysis, data collection, thesis writing, SWOT analysis, lab reports, dissertations, reviews, speeches, presentations, case studies, courseworks, homeworks, assignments, creative writing, blog writing, capstone project, grant proposal, lab reports", ua="Custom essay, research paper, book report, term paper, precis, sketch, poetry analysis, data collection, thesis writing, SWOT analysis, lab reports, dissertations, reviews, speeches, presentations, case studies, courseworks, homeworks, assignments, creative writing, blog writing, capstone project, grant proposal, lab reports"),
                LS(en="Plagiarism-free original papers written from scratch", ua="Plagiarism-free original papers written from scratch"),
                LS(en="Proofreading and editing of written papers", ua="Proofreading and editing of written papers"),
                LS(en="Choosing sources for your paper, providing with annotated bibliography upon request", ua="Choosing sources for your paper, providing with annotated bibliography upon request"),
                LS(en="Free guidelines on successful essay topic selection and writing process", ua="Free guidelines on successful essay topic selection and writing process"),
                LS(en="Individual approach to every customer, no repetitions, free consulting on the paper content", ua="Individual approach to every customer, no repetitions, free consulting on the paper content"),
                LS(en="Free revisions till you are completely satisfied", ua="Free revisions till you are completely satisfied"),
                LS(en="Meeting your deadline", ua="Meeting your deadline"),
                LS(en="Security and confidentiality", ua="Security and confidentiality")
            ))
        })

        writePage(name="why", highlightedItem="why", content=kdiv{o->
            o- pageHeader(t(en="Why AcademicPaperServed?", ua="Why AcademicPaperServed UA?"))

            fun add(section: Section) =o- kdiv{o->
                o- h3Smaller(t(section.title))
                o- markdown(dedent(t(section.content)))
            }

            add(Section(
                title = LS(en="We care about each customer’s academic success", ua="We care about each customer’s academic success"),
                content = LS(
                    en = "According to the statistics, almost 90% of current U.S. undergraduate and graduate students work either full-time or part-time, while they study. Often, they have little time to complete all of their essays and term papers by themselves, as writing is really time consuming. Others simply don't think of writing as the most pleasurable activity. All students aim for graduation. AcademicPaperServed is here to help them reach this goal.",
                    ua = "According to the statistics, almost 90% of current U.S. undergraduate and graduate students work either full-time or part-time, while they study. Often, they have little time to complete all of their essays and term papers by themselves, as writing is really time consuming. Others simply don't think of writing as the most pleasurable activity. All students aim for graduation. AcademicPaperServed is here to help them reach this goal.")))

            add(Section(
                title = LS(en="We make a very strong commitment to quality", ua="We make a very strong commitment to quality"),
                content = LS(
                    en = "Quality is one of the major benefits of our custom writing service. Every AcademicPaperServed writer is experienced in the special field of studies. As a result, all of the customer's most sophisticated requirements are met by the professional freelance writer with a deep knowledge of the custom paper topic. Our custom essay writers possess Bachelor's / Master's degrees in the related fields of knowledge.",
                    ua = "Quality is one of the major benefits of our custom writing service. Every AcademicPaperServed writer is experienced in the special field of studies. As a result, all of the customer's most sophisticated requirements are met by the professional freelance writer with a deep knowledge of the custom paper topic. Our custom essay writers possess Bachelor's / Master's degrees in the related fields of knowledge.")))

            add(Section(
                title = LS(en="We show an individual approach", ua="We show an individual approach"),
                content = LS(
                    en = """
                        To Every Customer and Every Project we deal with.

                        With us customers are sure to get their high school, college and university papers tailored exactly to their requirements and needs. No need to worry about quality! Your professor will be completely satisfied. We follow paper details specifically, provide with relevant sources from our library and/or use definite sources (books, journal and magazine articles, encyclopedia excerpts, peer reviewed studies, etc.) requested by the customer.

                        Our team is aware of students’ concerns and issues. Any academic achievement becomes joyful and easy with our team. You are sure to get your writer’s comments on the paper content, advice on choosing a suitable topic, primary and secondary sources selection, ways of presenting it in pubic if needed, etc. You are a getting a complex academic help along with the written coherent text which you, surely, deserve!

                        24/7 support will provide you with prompt unrivalled assistance in every matter of your concern and makes sure you’ll get excellent quality paper!

                        AcademicPaperServed team helps English speaking customers from all over the world with anything an academic writing implies, from research ideas to styles and design of your paper. We are strictly following the rules of academic presentation combining it with modern and creative approaches. You are sure to get your project written in a plain, clear language with smooth transitions and coherent rendering.
                    """,
                    ua = """
                        To Every Customer and Every Project we deal with.

                        With us customers are sure to get their high school, college and university papers tailored exactly to their requirements and needs. No need to worry about quality! Your professor will be completely satisfied. We follow paper details specifically, provide with relevant sources from our library and/or use definite sources (books, journal and magazine articles, encyclopedia excerpts, peer reviewed studies, etc.) requested by the customer.

                        Our team is aware of students’ concerns and issues. Any academic achievement becomes joyful and easy with our team. You are sure to get your writer’s comments on the paper content, advice on choosing a suitable topic, primary and secondary sources selection, ways of presenting it in pubic if needed, etc. You are a getting a complex academic help along with the written coherent text which you, surely, deserve!

                        24/7 support will provide you with prompt unrivalled assistance in every matter of your concern and makes sure you’ll get excellent quality paper!

                        AcademicPaperServed team helps English speaking customers from all over the world with anything an academic writing implies, from research ideas to styles and design of your paper. We are strictly following the rules of academic presentation combining it with modern and creative approaches. You are sure to get your project written in a plain, clear language with smooth transitions and coherent rendering.
                    """)))

            add(Section(
                title = LS(en="We hire only expert writers", ua="We hire only expert writers"),
                content = LS(
                    en = """
                        We involve only experienced staff in the writing process! Each member of the AcademicPaperServed writing team has at least 2 years + experience in academic research field.

                        You are sure to get your custom paper done by a native English speaker, who is versed in the area of your research, formats your paper according to the latest requirements of citation styles (MLA, APA, Harvard, Chicago, Turabian, Oxford and others) and delivers with no delays.

                        US-based customer support will offer samples of various academic papers for your consideration.

                        We’ve been dealing with ALL types of academic papers and we are always glad to offer you help with any essay (like scholarship essay, admission essay, application essay, entrance essay, personal statement) and research paper type at any academic level and in every possible discipline. If you want your paper to be well-organized and logically constructed, choose us!
                    """,
                    ua = """
                        We involve only experienced staff in the writing process! Each member of the AcademicPaperServed writing team has at least 2 years + experience in academic research field.

                        You are sure to get your custom paper done by a native English speaker, who is versed in the area of your research, formats your paper according to the latest requirements of citation styles (MLA, APA, Harvard, Chicago, Turabian, Oxford and others) and delivers with no delays.

                        US-based customer support will offer samples of various academic papers for your consideration.

                        We’ve been dealing with ALL types of academic papers and we are always glad to offer you help with any essay (like scholarship essay, admission essay, application essay, entrance essay, personal statement) and research paper type at any academic level and in every possible discipline. If you want your paper to be well-organized and logically constructed, choose us!
                    """)))

            add(Section(
                title = LS(en="Plagiarism-free and full-of-creativity zone", ua="Plagiarism-free and full-of-creativity zone"),
                content = LS(
                    en = """
                        Our writers get paid only when they use their own thoughts and ideas, elucidated through high-quality research and writing proficiency. For that reason, the following is not acceptable under any situation: copy-pasting from websites, copying from offline books, texts, and journals, basing one’s work on the ideas/structure of others or previous pieces of work. All papers are checked against millions of hard copy sources, billions of web pages, and countless pieces of work of other researchers. The papers are checked with the most up-to-date plagiarism detection software prior to being delivered to customer. There is totally no chance of delivering a plagiarized paper.

                        At AcademicPaperServed all writers apply a creative approach to every essay. Each custom paper is written in compliance with the requests and desires of the customer.

                        Be assured our friendly and knowledgeable staff will provide you with immediate, top-quality, and US-based customer support. All responses are personalized to the needs of the student.
                    """,
                    ua = """
                        Our writers get paid only when they use their own thoughts and ideas, elucidated through high-quality research and writing proficiency. For that reason, the following is not acceptable under any situation: copy-pasting from websites, copying from offline books, texts, and journals, basing one’s work on the ideas/structure of others or previous pieces of work. All papers are checked against millions of hard copy sources, billions of web pages, and countless pieces of work of other researchers. The papers are checked with the most up-to-date plagiarism detection software prior to being delivered to customer. There is totally no chance of delivering a plagiarized paper.

                        At AcademicPaperServed all writers apply a creative approach to every essay. Each custom paper is written in compliance with the requests and desires of the customer.

                        Be assured our friendly and knowledgeable staff will provide you with immediate, top-quality, and US-based customer support. All responses are personalized to the needs of the student.
                    """ )))
        })

        val priceTableRows = mutableListOf<List<String>>()
        for (dopt in DeliveryOption.values()) {
            val paperTypes: Array<out PaperType> = when (lang) {
                Language.EN -> PaperTypeEN.values()
                Language.UA -> PaperTypeRU.values()
            }
            var first = true
            for (top in paperTypes) {
                val curr = lang
                priceTableRows.add(listOf(
                    if (first) dopt.title else "",
                    top.title,
                    moneyTitleWithCurrency(top.price(dopt), currency, lang)
                ))
                first = false
            }
        }

        writePage(name="prices", highlightedItem="prices", content=kdiv{o->
            o- pageHeader(t(en="Pricing Policy", ua="Pricing Policy"))
            o- markdownPiece(
                en = """
                    Prices at AcademicPaperServed are set to meet the average point on the current custom writing market. This enables us to choose experts writers as freelance members in the team, who are able to meet the highest demands at faculties teaching in English.

                    Our support is daily queried on how one can buy an essay or research paper on a certain topic. AcademicPaperServed does sell pre-written papers, each assignment is made individually and each research is written from scratch. Top-notch writing quality is only achieved through effective communication of the writer and the customer, be it an order on a blog article, high school debate project or a music review.

                    Each quote we give to our customer is based primarily on the delivery date opted, type of an academic written assignment, number of pages and the amount of discount your membership is liable to.

                    You may find out your quote in one minute! Simply press the Order Your Custom Essay Now tab at the top of the page marked in green. Our Customer Support employee will advise you on the price and terms of delivery.
                """,
                ua = """
                    Prices at AcademicPaperServed are set to meet the average point on the current custom writing market. This enables us to choose experts writers as freelance members in the team, who are able to meet the highest demands at faculties teaching in English.

                    Our support is daily queried on how one can buy an essay or research paper on a certain topic. AcademicPaperServed does sell pre-written papers, each assignment is made individually and each research is written from scratch. Top-notch writing quality is only achieved through effective communication of the writer and the customer, be it an order on a blog article, high school debate project or a music review.

                    Each quote we give to our customer is based primarily on the delivery date opted, type of an academic written assignment, number of pages and the amount of discount your membership is liable to.

                    You may find out your quote in one minute! Simply press the Order Your Custom Essay Now tab at the top of the page marked in green. Our Customer Support employee will advise you on the price and terms of delivery.
                """)

            o- pageHeader(t(en="Bonuses", ua="Bonuses"))
            o- markdownPiece(
                en = "Ordering a paper at AcademicPaperServed, you also get:",
                ua = "Ordering a paper at AcademicPaperServed, you also get:")
            o- locBullets(listOf(
                LS(en="Free Title Page", ua="Free Title Page"),
                LS(en="Free Outline", ua="Free Outline"),
                LS(en="Free List of References", ua="Free List of References"),
                LS(en="Free Plagiarism Report (upon additional request)", ua="Free Plagiarism Report (upon additional request)")))

            o- pageHeader(t(en="Discount Policy", ua="Discount Policy"))
            o- markdownPiece(
                en = "Please note that each AcademicPaperServed customer is eligible for one-time and life-time discounts. One-time discount is granted to each new customer registered in our system and makes 5% off the first order total. Lifetime discount is provided to each returning customer depending on the total number of pages (on multiple papers) ordered since the moment of registration, and namely:",
                ua = "Please note that each AcademicPaperServed customer is eligible for one-time and life-time discounts. One-time discount is granted to each new customer registered in our system and makes 5% off the first order total. Lifetime discount is provided to each returning customer depending on the total number of pages (on multiple papers) ordered since the moment of registration, and namely:")
            o- locBullets(listOf(
                LS(en="More than 50 pages${symbols.mdash}5%", ua="Более 50 страниц ${symbols.ndash} 5%"),
                LS(en="More than 100 pages${symbols.mdash}10%", ua="Более 100 страниц ${symbols.ndash} 10%"),
                LS(en="More than 150 pages${symbols.mdash}15%", ua="Более 150 страниц ${symbols.ndash} 15%"),
                LS(en="More than 200 pages${symbols.mdash}30%", ua="Более 200 страниц ${symbols.ndash} 30%")))
            o- markdownPiece(
                en = "We’re sure that at AcademicPaperServed we employ a fair discount policy. We respect each certain customer and hope to establish long-term cooperation with him/her. Since customers are our most valued asset, we put a lot of effort to retaining and satisfying them through our flexible lifetime discount policy.",
                ua = "We’re sure that at AcademicPaperServed we employ a fair discount policy. We respect each certain customer and hope to establish long-term cooperation with him/her. Since customers are our most valued asset, we put a lot of effort to retaining and satisfying them through our flexible lifetime discount policy.")

            o- pageHeader(t(en="Our Prices", ua="Наши цены"))
            o- el("table", json("className" to "table table-hover table-condensed"),
                  el("thead", json(),
                     el("tr", json(),
                        el("th", json(), t(en="Delivery Option", ua="Срочность").asReactElement()),
                        el("th", json(), t(en="Type of Paper", ua="Тип работы").asReactElement()),
                        el("th", json(), t(en="Price", ua="Цена").asReactElement())
                     )),
                  el("tbody", json(),
                     *priceTableRows.map{row ->
                         el("tr", json(),
                            el("td", json(), row[0].asReactElement()),
                            el("td", json(), row[1].asReactElement()),
                            el("td", json(), row[2].asReactElement())
                         )
                     }.toTypedArray()
                  )
            )
        })

        val sampleItems = when (lang) {
            Language.EN -> listOf(
                HrefBulletItem(title = "Sample APA paper", href = "apa-sample.doc"),
                HrefBulletItem(title = "Sample MLA research paper", href = "mla-sample.doc"),
                HrefBulletItem(title = "Sample Harvard-style paper", href = "harvard-sample.doc"),
                HrefBulletItem(title = "Sample Chicago paper", href = "chicago-sample.doc"),
                HrefBulletItem(title = "Sample Turabian paper", href = "turabian-sample.doc")
            )
            Language.UA -> listOf(
                HrefBulletItem(title = "Пример реферата", href = "ua_essay-sample.doc"),
                HrefBulletItem(title = "Пример курсовой работы", href = "ua_course-sample.doc"),
                HrefBulletItem(title = "Пример дипломной работы", href = "ua_graduate-sample.doc")
            )
        }

        writePage(name="samples", highlightedItem="samples", content=kdiv{o->
            o- pageHeader(t(en="Sample Papers", ua="Примеры работ"))
            o- hrefBullets(sampleItems)
        })

        writePage(name="faq", highlightedItem="faq", content=kdiv{o->
            o- pageHeader(t(en="FAQ", ua="FAQ"))

            fun add(section: Section) =o- kdiv{o->
                o- markdownPiece("> " + t(section.title))
                o- kdiv(marginBottom=20, marginTop=-5){o->
                    o- markdown(dedent(t(section.content)))
                }
            }

            add(Section(
                title = LS(en="How does AcademicPaperServed work?", ua="How does AcademicPaperServed work?"),
                content = LS(
                    en = "As soon as you order a custom essay, term paper, research paper or book report your order is reviewed and then the most appropriate writer is assigned, who then takes responsibility to complete it. After your paper is ready, it is send to you via e-mail.",
                    ua = "As soon as you order a custom essay, term paper, research paper or book report your order is reviewed and then the most appropriate writer is assigned, who then takes responsibility to complete it. After your paper is ready, it is send to you via e-mail.")))

            add(Section(
                title = LS(en="I have had unpleasant experiences with other custom essay companies. How does AcademicPaperServed differ from fraud services?", ua="I have had unpleasant experiences with other custom essay companies. How does AcademicPaperServed differ from fraud services?"),
                content = LS(
                    en = "Sorry to say, but we often hear from our clients, that they have been deceived by unknown and disreputable essay writing companies that harm the reputation of legitimate writing companies. On the contrary, our service has done its best to earn the trust of our clients by offering quality custom papers at reasonable price.",
                    ua = "Sorry to say, but we often hear from our clients, that they have been deceived by unknown and disreputable essay writing companies that harm the reputation of legitimate writing companies. On the contrary, our service has done its best to earn the trust of our clients by offering quality custom papers at reasonable price.")))

            add(Section(
                title = LS(en="How safe is your service? Is there any risk to place an order online?", ua="How safe is your service? Is there any risk to place an order online?"),
                content = LS(
                    en = "It is totally safe. Hundreds of our customers make orders and buy custom writing services at AcademicPaperServed daily. All of your transactions are processed securely and encrypted by PayPal. It is impossible to make an unauthorized transaction using your credit card.",
                    ua = "It is totally safe. Hundreds of our customers make orders and buy custom writing services at AcademicPaperServed daily. All of your transactions are processed securely and encrypted by PayPal. It is impossible to make an unauthorized transaction using your credit card.")))

            add(Section(
                title = LS(en="What are your policies concerning the paper format and citation?", ua="What are your policies concerning the paper format and citation?"),
                content = LS(
                    en = """
                        Our writers use Microsoft Word .doc format by default (Times New Roman, 12, double-spaced, ~ 250-300 words/page). However, any format specified by the customer is available. The same concerns citation style.,

                        If not chosen, the writer chooses a citation style most appropriate for the written assignment in process. The latest edition with newest formatting style and its rules is applied, unless indicated otherwise.
                    """,
                    ua = """
                        Our writers use Microsoft Word .doc format by default (Times New Roman, 12, double-spaced, ~ 250-300 words/page). However, any format specified by the customer is available. The same concerns citation style.

                        If not chosen, the writer chooses a citation style most appropriate for the written assignment in process. The latest edition with newest formatting style and its rules is applied, unless indicated otherwise.
                    """)))

            add(Section(
                title = LS(en="What if I don’t like my paper and it does not meet the requirements?", ua="What if I don’t like my paper and it does not meet the requirements?"),
                content = LS(
                    en = "After you have ordered a paper, you can be confident that it will meet your expectations. Our writers will scrupulously adhere to your exact instructions to write a custom, first-rate academic paper. The team of writers works directly from customers’ instructions, and our clients get what they ask for. Still, there are moments when a customer can feel the writer has missed any of his/her requirements. In this case the customer should request a free revision. The writer will improve the paper and include all the instructions and will not stop working until the client is happy.",
                    ua = "After you have ordered a paper, you can be confident that it will meet your expectations. Our writers will scrupulously adhere to your exact instructions to write a custom, first-rate academic paper. The team of writers works directly from customers’ instructions, and our clients get what they ask for. Still, there are moments when a customer can feel the writer has missed any of his/her requirements. In this case the customer should request a free revision. The writer will improve the paper and include all the instructions and will not stop working until the client is happy.")))

            add(Section(
                title = LS(en="Does your service provide refunds?", ua="Does your service provide refunds?"),
                content = LS(
                    en = "Our aim is total customer satisfaction. According to our monthly data we have an acceptable 95% approval rating among our customers. Our writers work hard to do what is best for our customers. If the final version of the paper does not meet customer’s specified criteria, he/she can ask for a revision within thirty days. The writer will add any missing requirements and deliver the revision in 24 hours. If the customer provides us with evidence that the final revised version is still lacking some of the specified requirements, s/he can request a refund within three days after the completion of the order.",
                    ua = "Our aim is total customer satisfaction. According to our monthly data we have an acceptable 95% approval rating among our customers. Our writers work hard to do what is best for our customers. If the final version of the paper does not meet customer’s specified criteria, he/she can ask for a revision within thirty days. The writer will add any missing requirements and deliver the revision in 24 hours. If the customer provides us with evidence that the final revised version is still lacking some of the specified requirements, s/he can request a refund within three days after the completion of the order.")))

            add(Section(
                title = LS(en="What if my paper is found to be plagiarized?", ua="What if my paper is found to be plagiarized?"),
                content = LS(
                    en = "In fact, it is impossible. The writer gets paid only when he uses his own thoughts and ideas, elucidated through high-quality research/ and writing proficiency. For that reason, the following is not acceptable under any situation: copy-pasting from websites, copying from offline books, texts, and journals, basing one’s work on the ideas/structure of others or previous pieces of work. All papers are checked against millions of hard copy sources, billions of web pages, and countless pieces of work of other researchers. The papers are checked with most up to date anti-plagiarism software before sending any paper to customer. There is totally no chance of delivering a plagiarized paper.",
                    ua = "In fact, it is impossible. The writer gets paid only when he uses his own thoughts and ideas, elucidated through high-quality research/ and writing proficiency. For that reason, the following is not acceptable under any situation: copy-pasting from websites, copying from offline books, texts, and journals, basing one’s work on the ideas/structure of others or previous pieces of work. All papers are checked against millions of hard copy sources, billions of web pages, and countless pieces of work of other researchers. The papers are checked with most up to date anti-plagiarism software before sending any paper to customer. There is totally no chance of delivering a plagiarized paper.")))

            add(Section(
                title = LS(en="Can I check my order status?", ua="Can I check my order status?"),
                content = LS(
                    en = "In order to track the order status the customer can communicate with the writer or upload supplementary files or instructions.",
                    ua = "In order to track the order status the customer can communicate with the writer or upload supplementary files or instructions.")))

            add(Section(
                title = LS(en="Can I contact the writer?", ua="Can I contact the writer?"),
                content = LS(
                    en = "Certainly, there is a personal message board on each private account. A customer can easily post a message to the assigned writer, and he/she will reply to almost immediately. If the writer encounters any questions, he will contact the customer the in same way.",
                    ua = "Certainly, there is a personal message board on each private account. A customer can easily post a message to the assigned writer, and he/she will reply to almost immediately. If the writer encounters any questions, he will contact the customer the in same way.")))

            add(Section(
                title = LS(en="Can I ever find my ordered paper being available to the public?", ua="Can I ever find my ordered paper being available to the public?"),
                content = LS(
                    en = "Our service guarantees that the paper that has been written by our writers will never be available to the public. All papers are stored under an exclusively developed security system.",
                    ua = "Our service guarantees that the paper that has been written by our writers will never be available to the public. All papers are stored under an exclusively developed security system.")))

            add(Section(
                title = LS(en="How can I make sure the writer has understood my assignment correctly?", ua="How can I make sure the writer has understood my assignment correctly?"),
                content = LS(
                    en = """
                        When you start an ordering process, please, make sure you provide your writer with all necessary info, instructions, guidelines, additional materials for study, your class notes if any, and your personal preferences as to the writing style and paper formatting. It will surely facilitate the writing process and advance your chances in getting a higher grade.

                        We specifically created an easy-to-use interface for your communication with the writer and support center in the process of paper writing.

                        AcademicPaperServed writers’ team is highly flexible and is ready to consider every order on a personal basis. We value our customers and set communication as a priority. We will appreciate your help in giving explicit order details once your order is made.
                    """,
                    ua = """
                        When you start an ordering process, please, make sure you provide your writer with all necessary info, instructions, guidelines, additional materials for study, your class notes if any, and your personal preferences as to the writing style and paper formatting. It will surely facilitate the writing process and advance your chances in getting a higher grade.

                        We specifically created an easy-to-use interface for your communication with the writer and support center in the process of paper writing.

                        AcademicPaperServed writers’ team is highly flexible and is ready to consider every order on a personal basis. We value our customers and set communication as a priority. We will appreciate your help in giving explicit order details once your order is made.
                    """)))

            add(Section(
                title = LS(en="What if my professor requires to hand in all copies of sources used?", ua="What if my professor requires to hand in all copies of sources used?"),
                content = LS(
                    en = "The writers will provide you with the necessary sources used to write your paper if you request it in advance. Sometimes we use paid online libraries providing access per day/hour, in this case it is extremely difficult to come back there again and copy-paste material. Some libraries have copyright protection software, so cannot always copy the text for you. However, your timely request for the sources package will give us direction in a library choice. ",
                    ua = "The writers will provide you with the necessary sources used to write your paper if you request it in advance. Sometimes we use paid online libraries providing access per day/hour, in this case it is extremely difficult to come back there again and copy-paste material. Some libraries have copyright protection software, so cannot always copy the text for you. However, your timely request for the sources package will give us direction in a library choice. ")))

        })

        writePage(name="contact", highlightedItem="contact", content=kdiv{o->
            o- pageHeader(t(en="Contact Us", ua="Contact Us"))
            o- markdownPiece(
                en = """
                    Thank you for visiting AcademicPaperServed website! We will be happy to answer any questions, give a quote, and select a writer for your paper at any academic level. We value each customer and strive to achieve best results through communication process. Customer support representative is ready to advise you on the ordering process, choosing your delivery option and selecting an appropriate writer.
                """,
                ua = """
                    Thank you for visiting AcademicPaperServed website! We will be happy to answer any questions, give a quote, and select a writer for your paper at any academic level. We value each customer and strive to achieve best results through communication process. Customer support representative is ready to advise you on the ordering process, choosing your delivery option and selecting an appropriate writer.
                """)

            o- kdiv(marginBottom=10){o->
                o- t(en="AcademicPaperServed headquarter:", ua="AcademicPaperServed headquarter:")
            }

            o- kdiv(whiteSpace="pre", fontFamily="monospace"){o->
                o- tdedent(
                    en = """
                        DP World Inc
                        624 W Kristina Ln
                        Round Lake, IL 60073
                        United States
                    """,
                    ua = """
                        DP World Inc
                        624 W Kristina Ln
                        Round Lake, IL 60073
                        United States
                    """)
            }
        })

        val blogItems = when (lang) {
            Language.EN -> listOf(
                BlogItem(
                    listTitle = "Why Would I Order a Research Paper Online?",
                    title = "Being a Student is not Easy",
                    slug = "why-order",
                    content = readStatic("blog/customer-en/why-order.md")
                ),
                BlogItem(
                    listTitle = "High School Debate Topics in the United States",
                    title = "High School Debate Topics in the United States",
                    slug = "debate-topics",
                    content = readStatic("blog/customer-en/debate-topics.md")
                ),
                BlogItem(
                    listTitle = "How to Choose a Good Informative Speech Topic",
                    title = "How to Choose a Good Informative Speech Topic",
                    slug = "speech-topic",
                    content = readStatic("blog/customer-en/speech-topic.md")
                ),
                BlogItem(
                    listTitle = "How to Choose a Good Research or Essay Topic",
                    title = "How to Choose a Good Research or Essay Topic",
                    slug = "essay-topic",
                    content = readStatic("blog/customer-en/essay-topic.md")
                )
            )

            Language.UA -> listOf(
                BlogItem(
                    listTitle = "Why Would I Order a Research Paper Online?",
                    title = "Being a Student is not Easy",
                    slug = "why-order",
                    content = readStatic("blog/customer-ua/why-order.md")
                ),
                BlogItem(
                    listTitle = "High School Debate Topics in the United States",
                    title = "High School Debate Topics in the United States",
                    slug = "debate-topics",
                    content = readStatic("blog/customer-ua/debate-topics.md")
                ),
                BlogItem(
                    listTitle = "How to Choose a Good Informative Speech Topic",
                    title = "How to Choose a Good Informative Speech Topic",
                    slug = "speech-topic",
                    content = readStatic("blog/customer-ua/speech-topic.md")
                ),
                BlogItem(
                    listTitle = "How to Choose a Good Research or Essay Topic",
                    title = "How to Choose a Good Research or Essay Topic",
                    slug = "essay-topic",
                    content = readStatic("blog/customer-ua/essay-topic.md")
                )
            )
        }

        for (item in blogItems) {
            writePage(name="blog-${item.slug}", highlightedItem="blog", content=kdiv{o->
                o- pageHeader(item.title)
                o- markdownPiece(item.content)
            })
        }

        writePage(name="blog", highlightedItem="blog", content=kdiv{o->
            o- pageHeader(t(en="Writing Blog", ua="Писательский Блог"))
            o- hrefBullets(blogItems.map{x ->
                HrefBulletItem(title = x.listTitle, href = "blog-${x.slug}.html")})
        })

        for (name in customerDynamicPageNames()) {
            writePage(name=name, content=wholePageTicker().toToReactElementable())
        }

        println("DONE")
    }

    fun genericWritePage(name: String, highlightedItem: String?, content: ReactElement, root: String, tabTitle: String, lang: Language, clientKind: ClientKind) {
        fs.writeFileSync("${root}/${name}.html", """
    <!DOCTYPE html>
    <html lang="en" style="position: relative; min-height: 100%;">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <meta http-equiv="cache-control" content="max-age=0" />
        <meta http-equiv="cache-control" content="no-cache" />
        <meta http-equiv="expires" content="0" />
        <meta http-equiv="expires" content="Tue, 01 Jan 1980 1:00:00 GMT" />
        <meta http-equiv="pragma" content="no-cache" />

    ${renderToStaticMarkup(React.createElement("title", json(), tabTitle))}

    <link href="bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="font-awesome/css/font-awesome.min.css">
    <link href="bootstrap-datetimepicker-4.17.43-hacked.css" rel="stylesheet">

    <style>
        ${readStatic("style.css")}
    </style>

    <script>
    LANG = '${lang}'
    CLIENT_KIND = '${clientKind.name}'
    setFavicon('${if (clientKind == ClientKind.UA_CUSTOMER) "favicon-customer.ico" else "favicon-writer.ico"}')

    function setFavicon(src) {
        var link = document.createElement('link')
        link.id = 'favicon'
        link.rel = 'shortcut icon'
        link.href = src
        document.head.appendChild(link)
    }
    </script>
    </head>
    <body style="padding-top: 50px; padding-bottom: 0px; overflow-y: scroll;">
    <div id="topNavbarContainer">
    ${renderToStaticMarkup(renderTopNavbar(clientKind, {en, ua -> t(en, ua)}, highlightedItem = highlightedItem, rightNavbarItemAStyle = Style(display = "none")))}
    </div>

    <div id="root" style="min-height: calc(100vh - 28px - 50px);">
    <div id="staticShit" style="display: none;">
    <!-- BEGIN CONTENT -->
    ${renderToStaticMarkup(content)}
    <!-- END CONTENT -->
    </div>

    <div id="ticker" style="display: none;">${renderToStaticMarkup(wholePageTicker())}</div>

    <script src="jquery.min.js"></script>

    <script>
        window.storageLocalForStaticContent = localStorage

        function dlog(...args) {
            console.log.apply(console.log, args)
        }

        function displayInitialShit() {
            if (window.storageLocalForStaticContent.getItem('token')) {
                dlog('displayInitialShit -- token')
                document.getElementById('ticker').style.display = ''
            } else {
                dlog('displayInitialShit -- no token')
                makeSignInNavbarLinkVisible()
                document.getElementById('staticShit').style.display = ''
                window.staticShitIsRenderedStatically = true
            }
        }

        function makeSignInNavbarLinkVisible() {
            $('a[href="sign-in.html"]').css('display', '')
        }

        ${if (mode == Mode.PROD) """
            displayInitialShit()
        """ else """
            if (!/test=|testSuite=/.test(location.href)) {
                displayInitialShit()
            }
        """}
    </script>
    </div> <!-- /#root -->

    <div id="footer" style="position: relative;">
    <div style="background-color: #f8f8f8; border: 1px solid #e7e7e7; color: #333; font-family: Helvetica Neue, Helvetica, Arial, sans-serif; font-size: 12px; padding-top: 5px; padding-bottom: 5px; height: 28px;">
        <div class="container">
            ${when (clientKind) {
                ClientKind.UA_CUSTOMER -> "© Copyright 2015-2016 AcademicPaperServed. All rights reserved"
                ClientKind.UA_WRITER -> "© Copyright 2015-2016 Writer UA. All rights reserved"
            }}
        </div>
    </div>
    <div id="$ELID_UNDER_FOOTER">
    </div>
    </div>

    <script>if (typeof global === 'undefined') global = window</script>

    <script src="deps.js"></script>
    <script src="bootstrap/js/bootstrap-3.3.7-hacked.js"></script>
    <script src="bootstrap-datetimepicker-4.17.43-hacked.js"></script>
    <script src="kotlin.js"></script>

    <script>
        ${readStatic("testimonials.js")}
    </script>

    <script>
        global = window
        Kotlin = kotlin

        ${if (mode == Mode.DEBUG) """
            // TODO:vgrechka Think about DANGEROUS_TOKEN. How it should be included into client, etc.
            DANGEROUS_TOKEN = '${aps.process.env.APS_DANGEROUS_TOKEN}'
        """ else ""}

        global.MODE = '$mode'
        global.DB = 'bmix_fuckingAround_apsdevua'

        const scriptSuffix = ${if (mode == Mode.DEBUG) "'?' + Date.now()" else "''"}

        Promise.resolve()
        .then(_=> loadScript('front-enhanced.js' + scriptSuffix))
        .then(_=> {
            kot = Kotlin.modules.front
            F = kot.aps.front
            kot.aps.front.ignite()
        })

        function loadScript(src) {
            return new Promise((resolve, reject) => {
                const script = document.createElement('script')
                script.type = 'text/javascript'
                script.async = true
                script.onload = _=> {
                    dlog('Loaded ' + src)
                    resolve()
                }
                script.src = src
                document.getElementsByTagName('head')[0].appendChild(script)
            })
        }
    </script>
    </body>
    </html>
    """)
    }

    fun renderTestimonials(clientKind: ClientKind): ReactElement {
        return Shitus.diva(json(),
                           pageHeader(t(en="What People Say", ua="Что о нас говорят")),
                           Shitus.diva(json("id" to "testimonials-window"),
                Shitus.diva.apply(null, js("[]").concat(json("id" to "testimonials-strip"), listOf(
                    Testimonial(name = LS(en="Nicole", ua="Nicole"), img = "nicole.jpg", says = LS(
                        en="Never expected such an urgent project could be accomplished overnight! I really appreciated the level of your writers and you treating the customers. I will recommend your services to my friends.",
                        ua="Never expected such an urgent project could be accomplished overnight! I really appreciated the level of your writers and you treating the customers. I will recommend your services to my friends.")),
                    Testimonial(name = LS(en="Miranda", ua="Miranda"), img = "miranda.jpg", says = LS(
                        en="Wow!!! The paper got A+, for the first time in my student life I was graded so high! Thanx!",
                        ua="Wow!!! The paper got A+, for the first time in my student life I was graded so high! Thanx!")),
                    Testimonial(name = LS(en="Mike P.", ua="Mike P."), img = "mike-p.jpg", says = LS(
                        en="I was impressed when you writer sent me the copies of sources in one hour upon my request, though the paper was written over a month ago and I did not ask to make that at once. Carry on!",
                        ua="I was impressed when you writer sent me the copies of sources in one hour upon my request, though the paper was written over a month ago and I did not ask to make that at once. Carry on!")),
                    Testimonial(name = LS(en="Joseph B.", ua="Joseph B."), img = "joseph-b.jpg", says = LS(
                        en="First I doubted I’d get anything of good quality, but I was up${symbols.nbsp}to the eyes in work and had no other choice. The paper${symbols.nbsp}proved to be authentic and came on time. Can I get${symbols.nbsp}the same writer for my next essay?",
                        ua="First I doubted I’d get anything of good quality, but I was up${symbols.nbsp}to the eyes in work and had no other choice. The paper${symbols.nbsp}proved to be authentic and came on time. Can I get${symbols.nbsp}the same writer for my next essay?")),
                    Testimonial(name = LS(en="Mark C.", ua="Mark C."), img = "mark-c.jpg", says = LS(
                        en="How come you are so smart in every subject, guys? I’ve always been a bright student, but I admit you write quicker and select the most up-to-date sources. I need to learn from you.",
                        ua="How come you are so smart in every subject, guys? I’ve always been a bright student, but I admit you write quicker and select the most up-to-date sources. I need to learn from you.")),
                    Testimonial(name = LS(en="Linda R.", ua="Linda R."), img = "linda-r.jpg", says = LS(
                        en="I would have never accomplished this research paper on my own! It was too challenging. You also explained some parts of the paper I did not understand. Excellent job!",
                        ua="I would have never accomplished this research paper on my own! It was too challenging. You also explained some parts of the paper I did not understand. Excellent job!"))
                    ).map{item ->
                    Shitus.diva(json("className" to "testimonials-item"),
                        Shitus.diva(json("className" to "media"),
                            Shitus.diva(json("className" to "media-left"),
                                img(item.img, json("className" to "media-object"))),
                            Shitus.diva(json("className" to "media-body"),
                                Shitus.h4a(json("className" to "media-heading"), t(item.name)),
                                Shitus.spana(json(), t(item.says)))))}.toJSArray())),

                Shitus.diva(json("style" to json("display" to "flex", "alignItems" to "center", "position" to "absolute", "width" to 20, "right" to 0, "top" to 0, "height" to "100%")),
                    Shitus.glyph("chevron-right", json("id" to "testimonials-right", "className" to "fa-2x")))),

                           rawHTML("")
            )
    }

    fun h3Smaller(it: String): ReactElement {
        return Shitus.h3a(json(), Shitus.spana(json("style" to json("fontSize" to "80%")), it))
    }

    fun bullets(items: List<ReactElement>): ReactElement {
        return Shitus.ula.apply(null, js("[]").concat(json("className" to "fa-ul", "style" to json("marginLeft" to 22)), items.map{item ->
            Shitus.lia(json("style" to json("marginBottom" to 10)), Shitus.glyph("star", json("className" to "fa-li", "style" to json("color" to Color.BLUE_GRAY_600))), item)}.toJSArray()))
    }

    fun locBullets(items: List<LS>): ReactElement {
        return bullets(items.map{x -> t(x).asDynamicReactElement()})
    }


    fun hrefBullets(items: List<HrefBulletItem>): ReactElement {
        return bullets(items.map{x -> Shitus.aa(json("href" to x.href), x.title)})
    }

    fun tdedent(en: String, ua: String): String {
        return dedent(t(en, ua))
    }

    fun markdownPiece(en: String, ua: String): ToReactElementable {
        return markdownPiece(t(en, ua))
    }

    fun markdownPiece(content: String): ToReactElementable {
        return markdown(dedent(content.replace("{{mdash}}", symbols.mdash)))
    }


    fun horizBulletsRow(items: List<HorizBulletItem>, horizContentMargin: Int = 0): ReactElement {
        val colSize = 12 / items.size
        return Shitus.diva.apply(null, js("[]").concat(json("className" to "row", "style" to json("marginBottom" to 20)), items.map{x ->
            Shitus.diva(json("className" to "col-md-" + colSize),
                Shitus.diva(json("style" to json("textAlign" to "center", "marginBottom" to 10)), Shitus.glyph(x.glyph, json("className" to "fa-2x", "style" to json("color" to Color.BLUE_GRAY_600)))),
                Shitus.diva(json("style" to json("textAlign" to "center", "margin" to "0 ${horizContentMargin}px")), t(x.title)))}.toJSArray()))
    }

    fun readStatic(file: String): String {
        return fs.readFileSync("$APS_HOME/front/static/$file", "utf8")
    }

    fun renderToStaticMarkup(el: ReactElement): String {
        val html: String = ReactDOMServer.renderToStaticMarkup(el)
        // For textual diffing
        return html.replace(Regex("style=\"[^\"]*\"")) {mr->
            mr.value.replace(":", ": ").replace(";", "; ").replace(Regex("; \"$"), ";\"")
        }
    }

}



















