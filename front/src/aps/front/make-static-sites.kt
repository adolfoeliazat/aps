/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*

object MakeStaticSites {
    val kindaDirname = "e:/work/aps/aps/lib"
    val fs = js("require('fs')")
    val sh = js("require('shelljs')")
    val legacyStuff = js("require('e:/work/aps/aps/lib/stuff')")
    val legacyClient = js("require('e:/work/aps/aps/lib/client')")

    var t: (dynamic) -> dynamic = js("undefined")

    fun runShit() {
        js("require('e:/work/foundation/u')")

        sh.config.fatal = true
        js("Error").stackTraceLimit = js("Infinity")

        makeWriterSite(json("lang" to "ua"))
        makeCustomerSite(json("lang" to "ua"))
    }

    fun makeWriterSite(arg: dynamic) {
        // {lang}
        val lang = arg.lang

        val _t = jshit.utils.makeT(lang)
        t = run {
            fun anon(ignored: dynamic): dynamic {
                // ...args
                val args = js("Array.prototype.slice.call(arguments)")

                if (js("typeof args[0]") == "object" && args[0].`$sourceLocation`) {
                    args.shift()
                }
                return _t.apply(null, args)
            }
            ::anon
        }
        // imposeClientT(t)

        val root = "${kindaDirname}/../built/${lang}-writer"
        sh.rm("-rf", root)
        sh.mkdir("-p", root)

        val vendor = "${kindaDirname}/../vendor"
        sh.cp("${vendor}/jquery-2.2.4/jquery.min.js", root)
        sh.cp("-r", "${vendor}/bootstrap-3.3.6", root)
        sh.cp("-r", "${vendor}/font-awesome-4.6.3", root)
        sh.cp("${kindaDirname}/../asset/*", root)
        sh.cp("${kindaDirname}/../lib/bundle.js", root)
        sh.cp("-r", "e:/work/aps/front/out", "$root/kotlin")

        val tabTitle = t(json("en" to "Writer", "ua" to "Writer UA"))

        fun writePage(def: dynamic) {
            genericWritePage(global.Object.assign(def, json("root" to root, "tabTitle" to tabTitle, "lang" to lang, "clientKind" to "writer", "t" to t)))
        }

        writePage(json("name" to "index", // For Writer site
            "comp" to jshit.diva(json(),
            jshit.diva(json("className" to "container"),
                jshit.pageHeader(t(json("en" to "Welcome to Writer", "ua" to "Приветствуем на Писце"))),
                markdownPiece(json(
                    "en" to """
                        Hey there! Listening to music, but can’t follow your favorite song, because there’s a research paper you’ve got to get done by tomorrow?

                        Sitting in front of your laptop, desperately typing the keywords in your search engine browser and wondering why AcademicPaperServed came up with an idea of giving written assignments to the needy students like yourself?

                        Can’t be effective in the workplace, because there’s a persuasive essay you’ve got to turn in early next week?

                        Now relax. You’ve just reached the right destination! At AcademicPaperServed writing papers is really very easy!

                        Professional custom papers writing service, research paper, essay and term paper writing service from experienced writers at affordable price.
                        """,
                    "ua" to """
                        Hey there! Listening to music, but can’t follow your favorite song, because there’s a research paper you’ve got to get done by tomorrow?

                        Sitting in front of your laptop, desperately typing the keywords in your search engine browser and wondering why AcademicPaperServed came up with an idea of giving written assignments to the needy students like yourself?

                        Can’t be effective in the workplace, because there’s a persuasive essay you’ve got to turn in early next week?

                        Now relax. You’ve just reached the right destination! At AcademicPaperServed writing papers is really very easy!

                        Professional custom papers writing service, research paper, essay and term paper writing service from experienced writers at affordable price.
                        """)),

                jshit.pageHeader(t(json("en" to "Features", "ua" to "Фичи"))),
                horizBulletsRow(jsArrayOf(
                    json("glyph" to "pencil", "en" to "No plagiarism", "ua" to "No plagiarism"),
                    json("glyph" to "star", "en" to "Only premium quality", "ua" to "Only premium quality"),
                    json("glyph" to "list", "en" to "Free title page, outline, list${jshit.nbsp}of${jshit.nbsp}references", "ua" to "Free title page, outline, list${jshit.nbsp}of${jshit.nbsp}references")
                ), json("horizContentMargin" to 40)),
                horizBulletsRow(jsArrayOf(
                    json("glyph" to "gi-piggy-bank", "en" to "One-time and life-time discounts to returning customers", "ua" to "One-time and life-time discounts to returning customers"),
                    json("glyph" to "credit-card", "en" to "30-days money back guarantee", "ua" to "30-days money back guarantee"),
                    json("glyph" to "life-saver", "en" to "24/7 support", "ua" to "24/7 support")
                ), json("horizContentMargin" to 40)),

                jshit.pageHeader(t(json("en" to "Who We Are", "ua" to "Кто мы"))),
                markdownPiece(json(
                    "en" to """
                        AcademicPaperServed is an experienced custom writing service, consisting of expert writers to deliver written assignments, essays, research papers, term papers, theses, book reports, etc to English-speaking clients worldwide. Our team of professional writers focuses on the highest quality of all types of academic papers. Thesis writing is a challenge to many students. With our assistance it will not be a problem anymore!

                        Our writers are versed in various fields of academic writing, so if you do not find a suitable category on this page, feel free to contact our Support Center to find out availability of writing help in your area of interest.

                        We have access to most reliable and complete online libraries to make your research or essay unique.

                            AcademicPaperServed team consists of expert academic writers providing you with free guidelines, helping with topic selection, proofreading, editing and formatting even if you want to get your essay done overnight! We guarantee premium quality writing with urgent projects.
                        """,
                    "ua" to """
                        AcademicPaperServed is an experienced custom writing service, consisting of expert writers to deliver written assignments, essays, research papers, term papers, theses, book reports, etc to English-speaking clients worldwide. Our team of professional writers focuses on the highest quality of all types of academic papers. Thesis writing is a challenge to many students. With our assistance it will not be a problem anymore!

                        Our writers are versed in various fields of academic writing, so if you do not find a suitable category on this page, feel free to contact our Support Center to find out availability of writing help in your area of interest.

                        We have access to most reliable and complete online libraries to make your research or essay unique.

                            AcademicPaperServed team consists of expert academic writers providing you with free guidelines, helping with topic selection, proofreading, editing and formatting even if you want to get your essay done overnight! We guarantee premium quality writing with urgent projects.
                        """)),

                renderTestimonials("writer"),

                jshit.pageHeader(t(json("en" to "What We Offer", "ua" to "Мы предлагаем"))),
                horizBulletsRow(jsArrayOf(
                    json("glyph" to "envira", "en" to "Custom essay, research, thesis writing", "ua" to "Custom essay, research, thesis writing"),
                    json("glyph" to "rocket", "en" to "Plagiarism-free original papers written from scratch", "ua" to "Plagiarism-free original papers written from scratch"),
                    json("glyph" to "bomb", "en" to "Proofreading and editing of written papers", "ua" to "Proofreading and editing of written papers"),
                    json("glyph" to "book", "en" to "Free guidelines on essay topic selection and writing process", "ua" to "Free guidelines on essay topic selection and writing process")
                )),
                locBullets(jsArrayOf(
                    json("en" to "Custom essay, research paper, book report, term paper, precis, sketch, poetry analysis, data collection, thesis writing, SWOT analysis, lab reports, dissertations, reviews, speeches, presentations, case studies, courseworks, homeworks, assignments, creative writing, blog writing, capstone project, grant proposal, lab reports", "ua" to "Custom essay, research paper, book report, term paper, precis, sketch, poetry analysis, data collection, thesis writing, SWOT analysis, lab reports, dissertations, reviews, speeches, presentations, case studies, courseworks, homeworks, assignments, creative writing, blog writing, capstone project, grant proposal, lab reports"),
                    json("en" to "Plagiarism-free original papers written from scratch", "ua" to "Plagiarism-free original papers written from scratch"),
                    json("en" to "Proofreading and editing of written papers", "ua" to "Proofreading and editing of written papers"),
                    json("en" to "Choosing sources for your paper, providing with annotated bibliography upon request", "ua" to "Choosing sources for your paper, providing with annotated bibliography upon request"),
                    json("en" to "Free guidelines on successful essay topic selection and writing process", "ua" to "Free guidelines on successful essay topic selection and writing process"),
                    json("en" to "Individual approach to every customer, no repetitions, free consulting on the paper content", "ua" to "Individual approach to every customer, no repetitions, free consulting on the paper content"),
                    json("en" to "Free revisions till you are completely satisfied", "ua" to "Free revisions till you are completely satisfied"),
                    json("en" to "Meeting your deadline", "ua" to "Meeting your deadline"),
                    json("en" to "Security and confidentiality", "ua" to "Security and confidentiality")
                ))))
        ))

        writePage(json("name" to "why", "highlightedItem" to "why", // For Writer site
            "comp" to jshit.diva(json(),
            jshit.diva.apply(null, js("[]").concat(json("className" to "container"),
                jshit.pageHeader(t(json("en" to "Why Writer?", "ua" to "Почему Писец?"))),
                jsArrayOf(json(
                "title" to json("en" to "We care about each customer’s academic success", "ua" to "We care about each customer’s academic success"),
                "content" to json(
                "en" to "According to the statistics, almost 90% of current U.S. undergraduate and graduate students work either full-time or part-time, while they study. Often, they have little time to complete all of their essays and term papers by themselves, as writing is really time consuming. Others simply don't think of writing as the most pleasurable activity. All students aim for graduation. AcademicPaperServed is here to help them reach this goal.",
                "ua" to "According to the statistics, almost 90% of current U.S. undergraduate and graduate students work either full-time or part-time, while they study. Often, they have little time to complete all of their essays and term papers by themselves, as writing is really time consuming. Others simply don't think of writing as the most pleasurable activity. All students aim for graduation. AcademicPaperServed is here to help them reach this goal."
            )
            ),
                json(
                    "title" to json("en" to "We make a very strong commitment to quality", "ua" to "We make a very strong commitment to quality"),
                    "content" to json(
                    "en" to "Quality is one of the major benefits of our custom writing service. Every AcademicPaperServed writer is experienced in the special field of studies. As a result, all of the customer's most sophisticated requirements are met by the professional freelance writer with a deep knowledge of the custom paper topic. Our custom essay writers possess Bachelor's / Master's degrees in the related fields of knowledge.",
                    "ua" to "Quality is one of the major benefits of our custom writing service. Every AcademicPaperServed writer is experienced in the special field of studies. As a result, all of the customer's most sophisticated requirements are met by the professional freelance writer with a deep knowledge of the custom paper topic. Our custom essay writers possess Bachelor's / Master's degrees in the related fields of knowledge."
                )
                ),
                json(
                    "title" to json("en" to "We show an individual approach", "ua" to "We show an individual approach"),
                    "content" to json(
                    "en" to """
                    To Every Customer and Every Project we deal with.

                        With us customers are sure to get their high school, college and university papers tailored exactly to their requirements and needs. No need to worry about quality! Your professor will be completely satisfied. We follow paper details specifically, provide with relevant sources from our library and/or use definite sources (books, journal and magazine articles, encyclopedia excerpts, peer reviewed studies, etc.) requested by the customer.

                    Our team is aware of students’ concerns and issues. Any academic achievement becomes joyful and easy with our team. You are sure to get your writer’s comments on the paper content, advice on choosing a suitable topic, primary and secondary sources selection, ways of presenting it in pubic if needed, etc. You are a getting a complex academic help along with the written coherent text which you, surely, deserve!

                    24/7 support will provide you with prompt unrivalled assistance in every matter of your concern and makes sure you’ll get excellent quality paper!

                    AcademicPaperServed team helps English speaking customers from all over the world with anything an academic writing implies, from research ideas to styles and design of your paper. We are strictly following the rules of academic presentation combining it with modern and creative approaches. You are sure to get your project written in a plain, clear language with smooth transitions and coherent rendering.
                    """,
                    "ua" to """
                    To Every Customer and Every Project we deal with.

                        With us customers are sure to get their high school, college and university papers tailored exactly to their requirements and needs. No need to worry about quality! Your professor will be completely satisfied. We follow paper details specifically, provide with relevant sources from our library and/or use definite sources (books, journal and magazine articles, encyclopedia excerpts, peer reviewed studies, etc.) requested by the customer.

                    Our team is aware of students’ concerns and issues. Any academic achievement becomes joyful and easy with our team. You are sure to get your writer’s comments on the paper content, advice on choosing a suitable topic, primary and secondary sources selection, ways of presenting it in pubic if needed, etc. You are a getting a complex academic help along with the written coherent text which you, surely, deserve!

                    24/7 support will provide you with prompt unrivalled assistance in every matter of your concern and makes sure you’ll get excellent quality paper!

                    AcademicPaperServed team helps English speaking customers from all over the world with anything an academic writing implies, from research ideas to styles and design of your paper. We are strictly following the rules of academic presentation combining it with modern and creative approaches. You are sure to get your project written in a plain, clear language with smooth transitions and coherent rendering.
                    """
                )
                ),
                json(
                    "title" to json("en" to "We hire only expert writers", "ua" to "We hire only expert writers"),
                    "content" to json(
                    "en" to """
                    We involve only experienced staff in the writing process! Each member of the AcademicPaperServed writing team has at least 2 years + experience in academic research field.

                    You are sure to get your custom paper done by a native English speaker, who is versed in the area of your research, formats your paper according to the latest requirements of citation styles (MLA, APA, Harvard, Chicago, Turabian, Oxford and others) and delivers with no delays.

                    US-based customer support will offer samples of various academic papers for your consideration.

                    We’ve been dealing with ALL types of academic papers and we are always glad to offer you help with any essay (like scholarship essay, admission essay, application essay, entrance essay, personal statement) and research paper type at any academic level and in every possible discipline. If you want your paper to be well-organized and logically constructed, choose us!
                    """,
                    "ua" to """
                    We involve only experienced staff in the writing process! Each member of the AcademicPaperServed writing team has at least 2 years + experience in academic research field.

                    You are sure to get your custom paper done by a native English speaker, who is versed in the area of your research, formats your paper according to the latest requirements of citation styles (MLA, APA, Harvard, Chicago, Turabian, Oxford and others) and delivers with no delays.

                    US-based customer support will offer samples of various academic papers for your consideration.

                    We’ve been dealing with ALL types of academic papers and we are always glad to offer you help with any essay (like scholarship essay, admission essay, application essay, entrance essay, personal statement) and research paper type at any academic level and in every possible discipline. If you want your paper to be well-organized and logically constructed, choose us!
                    """
                )
                ),
                json(
                    "title" to json("en" to "Plagiarism-free and full-of-creativity zone", "ua" to "Plagiarism-free and full-of-creativity zone"),
                    "content" to json(
                    "en" to """
                    Our writers get paid only when they use their own thoughts and ideas, elucidated through high-quality research and writing proficiency. For that reason, the following is not acceptable under any situation: copy-pasting from websites, copying from offline books, texts, and journals, basing one’s work on the ideas/structure of others or previous pieces of work. All papers are checked against millions of hard copy sources, billions of web pages, and countless pieces of work of other researchers. The papers are checked with the most up-to-date plagiarism detection software prior to being delivered to customer. There is totally no chance of delivering a plagiarized paper.

                    At AcademicPaperServed all writers apply a creative approach to every essay. Each custom paper is written in compliance with the requests and desires of the customer.

                        Be assured our friendly and knowledgeable staff will provide you with immediate, top-quality, and US-based customer support. All responses are personalized to the needs of the student.
                    """,
                    "ua" to """
                    Our writers get paid only when they use their own thoughts and ideas, elucidated through high-quality research and writing proficiency. For that reason, the following is not acceptable under any situation: copy-pasting from websites, copying from offline books, texts, and journals, basing one’s work on the ideas/structure of others or previous pieces of work. All papers are checked against millions of hard copy sources, billions of web pages, and countless pieces of work of other researchers. The papers are checked with the most up-to-date plagiarism detection software prior to being delivered to customer. There is totally no chance of delivering a plagiarized paper.

                    At AcademicPaperServed all writers apply a creative approach to every essay. Each custom paper is written in compliance with the requests and desires of the customer.

                        Be assured our friendly and knowledgeable staff will provide you with immediate, top-quality, and US-based customer support. All responses are personalized to the needs of the student.
                    """
                )
                )
                ).map{section: dynamic ->
            jshit.diva(json(),
                jshit.diva(json(), h3Smaller(t(section.title))),
                jshit.diva(json(), jshit.markdown(jshit.utils.dedent(t(section.content)))))}
            ))
            )
        ))

        writePage(json("name" to "prices", "highlightedItem" to "prices", // For Writer site
            "comp" to jshit.diva(json(),
            jshit.diva(json("className" to "container"),
                jshit.pageHeader(t(json("en" to "Our Prices", "ua" to "Наши цены"))),
                markdownPiece(json(
                    "en" to """
                    Здесь такие идут расценки для писателей.
                    """,
                    "ua" to """
                    Здесь такие идут расценки для писателей.
                    """
                )),

                jshit.pageHeader(t(json("en" to "Pricing Policy", "ua" to "Pricing Policy"))),
                markdownPiece(json(
                    "en" to """
                    Prices at AcademicPaperServed are set to meet the average point on the current custom writing market. This enables us to choose experts writers as freelance members in the team, who are able to meet the highest demands at faculties teaching in English.

                    Our support is daily queried on how one can buy an essay or research paper on a certain topic. AcademicPaperServed does sell pre-written papers, each assignment is made individually and each research is written from scratch. Top-notch writing quality is only achieved through effective communication of the writer and the customer, be it an order on a blog article, high school debate project or a music review.

                    Each quote we give to our customer is based primarily on the delivery date opted, type of an academic written assignment, number of pages and the amount of discount your membership is liable to.

                    You may find out your quote in one minute! Simply press the Order Your Custom Essay Now tab at the top of the page marked in green. Our Customer Support employee will advise you on the price and terms of delivery.
                    """,
                    "ua" to """
                    Prices at AcademicPaperServed are set to meet the average point on the current custom writing market. This enables us to choose experts writers as freelance members in the team, who are able to meet the highest demands at faculties teaching in English.

                    Our support is daily queried on how one can buy an essay or research paper on a certain topic. AcademicPaperServed does sell pre-written papers, each assignment is made individually and each research is written from scratch. Top-notch writing quality is only achieved through effective communication of the writer and the customer, be it an order on a blog article, high school debate project or a music review.

                    Each quote we give to our customer is based primarily on the delivery date opted, type of an academic written assignment, number of pages and the amount of discount your membership is liable to.

                    You may find out your quote in one minute! Simply press the Order Your Custom Essay Now tab at the top of the page marked in green. Our Customer Support employee will advise you on the price and terms of delivery.
                    """
                )),

                jshit.pageHeader(t(json("en" to "Bonuses", "ua" to "Bonuses"))),
                markdownPiece(json(
                    "en" to "Ordering a paper at AcademicPaperServed, you also get:",
                    "ua" to "Ordering a paper at AcademicPaperServed, you also get:"
                )),
                locBullets(jsArrayOf(
                json("en" to "Free Title Page", "ua" to "Free Title Page"),
                json("en" to "Free Outline", "ua" to "Free Outline"),
                json("en" to "Free List of References", "ua" to "Free List of References"),
                json("en" to "Free Plagiarism Report (upon additional request)", "ua" to "Free Plagiarism Report (upon additional request)")
            )),

            jshit.pageHeader(t(json("en" to "Discount Policy", "ua" to "Discount Policy"))),
            markdownPiece(json(
                "en" to "Please note that each AcademicPaperServed customer is eligible for one-time and life-time discounts. One-time discount is granted to each new customer registered in our system and makes 5% off the first order total. Lifetime discount is provided to each returning customer depending on the total number of pages (on multiple papers) ordered since the moment of registration, and namely:",
                "ua" to "Please note that each AcademicPaperServed customer is eligible for one-time and life-time discounts. One-time discount is granted to each new customer registered in our system and makes 5% off the first order total. Lifetime discount is provided to each returning customer depending on the total number of pages (on multiple papers) ordered since the moment of registration, and namely:"
            )),
            locBullets(jsArrayOf(
            json("en" to "More than 50 pages${jshit.mdash}5%", "ua" to "Более 50 страниц ${jshit.ndash} 5%"),
            json("en" to "More than 100 pages${jshit.mdash}10%", "ua" to "Более 100 страниц ${jshit.ndash} 10%"),
            json("en" to "More than 150 pages${jshit.mdash}15%", "ua" to "Более 150 страниц ${jshit.ndash} 15%"),
            json("en" to "More than 200 pages${jshit.mdash}30%", "ua" to "Более 200 страниц ${jshit.ndash} 30%")
            )),
            markdownPiece(json(
                "en" to "We’re sure that at AcademicPaperServed we employ a fair discount policy. We respect each certain customer and hope to establish long-term cooperation with him/her. Since customers are our most valued asset, we put a lot of effort to retaining and satisfying them through our flexible lifetime discount policy.",
                "ua" to "We’re sure that at AcademicPaperServed we employ a fair discount policy. We respect each certain customer and hope to establish long-term cooperation with him/her. Since customers are our most valued asset, we put a lot of effort to retaining and satisfying them through our flexible lifetime discount policy."
            ))
            )
            )
        ))

        writePage(json("name" to "faq", "highlightedItem" to "faq", // For Writer site
            "comp" to jshit.diva(json(),
            jshit.diva.apply(null, js("[]").concat(json("className" to "container"),
                jshit.pageHeader(t(json("en" to "FAQ", "ua" to "Частые вопросы"))),
                jsArrayOf(json(
                "title" to json("en" to "How does AcademicPaperServed work?", "ua" to "How does AcademicPaperServed work?"),
                "content" to json(
                "en" to "As soon as you order a custom essay, term paper, research paper or book report your order is reviewed and then the most appropriate writer is assigned, who then takes responsibility to complete it. After your paper is ready, it is send to you via e-mail.",
                "ua" to "As soon as you order a custom essay, term paper, research paper or book report your order is reviewed and then the most appropriate writer is assigned, who then takes responsibility to complete it. After your paper is ready, it is send to you via e-mail."
            )
            ),
                json(
                    "title" to json("en" to "I have had unpleasant experiences with other custom essay companies. How does AcademicPaperServed differ from fraud services?", "ua" to "I have had unpleasant experiences with other custom essay companies. How does AcademicPaperServed differ from fraud services?"),
                    "content" to json(
                    "en" to "Sorry to say, but we often hear from our clients, that they have been deceived by unknown and disreputable essay writing companies that harm the reputation of legitimate writing companies. On the contrary, our service has done its best to earn the trust of our clients by offering quality custom papers at reasonable price.",
                    "ua" to "Sorry to say, but we often hear from our clients, that they have been deceived by unknown and disreputable essay writing companies that harm the reputation of legitimate writing companies. On the contrary, our service has done its best to earn the trust of our clients by offering quality custom papers at reasonable price."
                )
                ),
                json(
                    "title" to json("en" to "How safe is your service? Is there any risk to place an order online?", "ua" to "How safe is your service? Is there any risk to place an order online?"),
                    "content" to json(
                    "en" to "It is totally safe. Hundreds of our customers make orders and buy custom writing services at AcademicPaperServed daily. All of your transactions are processed securely and encrypted by PayPal. It is impossible to make an unauthorized transaction using your credit card.",
                    "ua" to "It is totally safe. Hundreds of our customers make orders and buy custom writing services at AcademicPaperServed daily. All of your transactions are processed securely and encrypted by PayPal. It is impossible to make an unauthorized transaction using your credit card."
                )
                ),
                json(
                    "title" to json("en" to "What are your policies concerning the paper format and citation?", "ua" to "What are your policies concerning the paper format and citation?"),
                    "content" to json(
                    "en" to """
                    Our writers use Microsoft Word .doc format by default (Times New Roman, 12, double-spaced, ~ 250-300 words/page). However, any format specified by the customer is available. The same concerns citation style.,

                    If not chosen, the writer chooses a citation style most appropriate for the written assignment in process. The latest edition with newest formatting style and its rules is applied, unless indicated otherwise.
                    """,
                    "ua" to """
                    Our writers use Microsoft Word .doc format by default (Times New Roman, 12, double-spaced, ~ 250-300 words/page). However, any format specified by the customer is available. The same concerns citation style.

                    If not chosen, the writer chooses a citation style most appropriate for the written assignment in process. The latest edition with newest formatting style and its rules is applied, unless indicated otherwise.
                    """
                )
                ),
                json(
                    "title" to json("en" to "What if I don’t like my paper and it does not meet the requirements?", "ua" to "What if I don’t like my paper and it does not meet the requirements?"),
                    "content" to json(
                    "en" to "After you have ordered a paper, you can be confident that it will meet your expectations. Our writers will scrupulously adhere to your exact instructions to write a custom, first-rate academic paper. The team of writers works directly from customers’ instructions, and our clients get what they ask for. Still, there are moments when a customer can feel the writer has missed any of his/her requirements. In this case the customer should request a free revision. The writer will improve the paper and include all the instructions and will not stop working until the client is happy.",
                    "ua" to "After you have ordered a paper, you can be confident that it will meet your expectations. Our writers will scrupulously adhere to your exact instructions to write a custom, first-rate academic paper. The team of writers works directly from customers’ instructions, and our clients get what they ask for. Still, there are moments when a customer can feel the writer has missed any of his/her requirements. In this case the customer should request a free revision. The writer will improve the paper and include all the instructions and will not stop working until the client is happy."
                )
                ),
                json(
                    "title" to json("en" to "Does your service provide refunds?", "ua" to "Does your service provide refunds?"),
                    "content" to json(
                    "en" to "Our aim is total customer satisfaction. According to our monthly data we have an acceptable 95% approval rating among our customers. Our writers work hard to do what is best for our customers. If the final version of the paper does not meet customer’s specified criteria, he/she can ask for a revision within thirty days. The writer will add any missing requirements and deliver the revision in 24 hours. If the customer provides us with evidence that the final revised version is still lacking some of the specified requirements, s/he can request a refund within three days after the completion of the order.",
                    "ua" to "Our aim is total customer satisfaction. According to our monthly data we have an acceptable 95% approval rating among our customers. Our writers work hard to do what is best for our customers. If the final version of the paper does not meet customer’s specified criteria, he/she can ask for a revision within thirty days. The writer will add any missing requirements and deliver the revision in 24 hours. If the customer provides us with evidence that the final revised version is still lacking some of the specified requirements, s/he can request a refund within three days after the completion of the order."
                )
                ),
                json(
                    "title" to json("en" to "What if my paper is found to be plagiarized?", "ua" to "What if my paper is found to be plagiarized?"),
                    "content" to json(
                    "en" to "In fact, it is impossible. The writer gets paid only when he uses his own thoughts and ideas, elucidated through high-quality research/ and writing proficiency. For that reason, the following is not acceptable under any situation: copy-pasting from websites, copying from offline books, texts, and journals, basing one’s work on the ideas/structure of others or previous pieces of work. All papers are checked against millions of hard copy sources, billions of web pages, and countless pieces of work of other researchers. The papers are checked with most up to date anti-plagiarism software before sending any paper to customer. There is totally no chance of delivering a plagiarized paper.",
                    "ua" to "In fact, it is impossible. The writer gets paid only when he uses his own thoughts and ideas, elucidated through high-quality research/ and writing proficiency. For that reason, the following is not acceptable under any situation: copy-pasting from websites, copying from offline books, texts, and journals, basing one’s work on the ideas/structure of others or previous pieces of work. All papers are checked against millions of hard copy sources, billions of web pages, and countless pieces of work of other researchers. The papers are checked with most up to date anti-plagiarism software before sending any paper to customer. There is totally no chance of delivering a plagiarized paper."
                )
                ),
                json(
                    "title" to json("en" to "Can I check my order status?", "ua" to "Can I check my order status?"),
                    "content" to json(
                    "en" to "In order to track the order status the customer can communicate with the writer or upload supplementary files or instructions.",
                    "ua" to "In order to track the order status the customer can communicate with the writer or upload supplementary files or instructions."
                )
                ),
                json(
                    "title" to json("en" to "Can I contact the writer?", "ua" to "Can I contact the writer?"),
                    "content" to json(
                    "en" to "Certainly, there is a personal message board on each private account. A customer can easily post a message to the assigned writer, and he/she will reply to almost immediately. If the writer encounters any questions, he will contact the customer the in same way.",
                    "ua" to "Certainly, there is a personal message board on each private account. A customer can easily post a message to the assigned writer, and he/she will reply to almost immediately. If the writer encounters any questions, he will contact the customer the in same way."
                )
                ),
                json(
                    "title" to json("en" to "Can I ever find my ordered paper being available to the public?", "ua" to "Can I ever find my ordered paper being available to the public?"),
                    "content" to json(
                    "en" to "Our service guarantees that the paper that has been written by our writers will never be available to the public. All papers are stored under an exclusively developed security system.",
                    "ua" to "Our service guarantees that the paper that has been written by our writers will never be available to the public. All papers are stored under an exclusively developed security system."
                )
                ),
                json(
                    "title" to json("en" to "How can I make sure the writer has understood my assignment correctly?", "ua" to "How can I make sure the writer has understood my assignment correctly?"),
                    "content" to json(
                    "en" to """
                    When you start an ordering process, please, make sure you provide your writer with all necessary info, instructions, guidelines, additional materials for study, your class notes if any, and your personal preferences as to the writing style and paper formatting. It will surely facilitate the writing process and advance your chances in getting a higher grade.

                    We specifically created an easy-to-use interface for your communication with the writer and support center in the process of paper writing.

                    AcademicPaperServed writers’ team is highly flexible and is ready to consider every order on a personal basis. We value our customers and set communication as a priority. We will appreciate your help in giving explicit order details once your order is made.
                    """,
                    "ua" to """
                    When you start an ordering process, please, make sure you provide your writer with all necessary info, instructions, guidelines, additional materials for study, your class notes if any, and your personal preferences as to the writing style and paper formatting. It will surely facilitate the writing process and advance your chances in getting a higher grade.

                    We specifically created an easy-to-use interface for your communication with the writer and support center in the process of paper writing.

                    AcademicPaperServed writers’ team is highly flexible and is ready to consider every order on a personal basis. We value our customers and set communication as a priority. We will appreciate your help in giving explicit order details once your order is made.
                    """
                )
                ),
                json(
                    "title" to json("en" to "What if my professor requires to hand in all copies of sources used?", "ua" to "What if my professor requires to hand in all copies of sources used?"),
                    "content" to json(
                    "en" to "The writers will provide you with the necessary sources used to write your paper if you request it in advance. Sometimes we use paid online libraries providing access per day/hour, in this case it is extremely difficult to come back there again and copy-paste material. Some libraries have copyright protection software, so cannot always copy the text for you. However, your timely request for the sources package will give us direction in a library choice. ",
                    "ua" to "The writers will provide you with the necessary sources used to write your paper if you request it in advance. Sometimes we use paid online libraries providing access per day/hour, in this case it is extremely difficult to come back there again and copy-paste material. Some libraries have copyright protection software, so cannot always copy the text for you. However, your timely request for the sources package will give us direction in a library choice. "
                )
                )
                ).map{section: dynamic ->
            jshit.diva(json(),
                jshit.diva(json(), markdownPiece("> " + t(section.title))),
                jshit.diva(json("style" to json("marginBottom" to 20, "marginTop" to -5)), jshit.markdown(jshit.utils.dedent(t(section.content)))))}
            ))
            )
        ))

        writeDynamicPages(legacyClient.writerDynamicPageNames(), ::writePage)
    }

    fun writeDynamicPages(names: dynamic, writePage: dynamic) {
        names.forEach{name: dynamic ->
            writePage(json("name" to name, "highlightedItem" to "",
                "comp" to wholePageTicker()))}
    }

    fun wholePageTicker(): dynamic {
        return jshit.rawHtml("""
    <div class="container">
    <div style="display: flex; align-items: center; justify-content: center; position: absolute; left: 0px; top: 200px; width: 100%;">
    <span style="margin-left: 10">${t(json("en" to "Breathe slowly...", "ua" to "Дышите глубоко..."))}</span>
    <div id="wholePageTicker" class="progressTicker" style="background-color: ${Color.BLUE_GRAY_600}; width: 14px; height: 28px; margin-left: 10px; margin-top: -5px"></div>
    </div>
    </div>""")
    }

    fun makeCustomerSite(arg: dynamic) {
        // {lang}
        val lang = arg.lang

        val _t = jshit.utils.makeT(lang)
        t = run {
            fun anon(ignored: dynamic): dynamic {
                // ...args
                val args = js("Array.prototype.slice.call(arguments)")

                if (js("typeof args[0]") == "object" && args[0].`$sourceLocation`) {
                    args.shift()
                }
                return _t.apply(null, args)
            }
            ::anon
        }
        // imposeClientT(t)

        val root = "${kindaDirname}/../built/${lang}-customer"
        sh.rm("-rf", root)
        sh.mkdir("-p", root)

        val vendor = "${kindaDirname}/../vendor"
        sh.cp("${vendor}/jquery-2.2.4/jquery.min.js", root)
        sh.cp("-r", "${vendor}/bootstrap-3.3.6", root)
        sh.cp("-r", "${vendor}/font-awesome-4.6.3", root)
        sh.cp("${kindaDirname}/../asset/*", root)
        sh.cp("${kindaDirname}/../lib/bundle.js", root)
        sh.cp("-r", "e:/work/aps/front/out", "$root/kotlin")

        val tabTitle = t(json("en" to "APS", "ua" to "APS UA"))

        fun writePage(def: dynamic) {
            genericWritePage(global.Object.assign(def, json("root" to root, "tabTitle" to tabTitle, "lang" to lang, "clientKind" to "customer", "t" to t)))
        }

        writePage(json("name" to "index", // For Customer site
            "comp" to jshit.diva(json(),
                jshit.diva(json("className" to "container"),
                    jshit.pageHeader(t(json("en" to "Welcome to AcademicPaperServed", "ua" to "Welcome to AcademicPaperServed UA"))),
                    markdownPiece(json("en" to """
                Hey there! Listening to music, but can’t follow your favorite song, because there’s a research paper you’ve got to get done by tomorrow?

                Sitting in front of your laptop, desperately typing the keywords in your search engine browser and wondering why AcademicPaperServed came up with an idea of giving written assignments to the needy students like yourself?

                Can’t be effective in the workplace, because there’s a persuasive essay you’ve got to turn in early next week?

                Now relax. You’ve just reached the right destination! At AcademicPaperServed writing papers is really very easy!

                Professional custom papers writing service, research paper, essay and term paper writing service from experienced writers at affordable price.
                """,
                        "ua" to """
                Hey there! Listening to music, but can’t follow your favorite song, because there’s a research paper you’ve got to get done by tomorrow?

                Sitting in front of your laptop, desperately typing the keywords in your search engine browser and wondering why AcademicPaperServed came up with an idea of giving written assignments to the needy students like yourself?

                Can’t be effective in the workplace, because there’s a persuasive essay you’ve got to turn in early next week?

                Now relax. You’ve just reached the right destination! At AcademicPaperServed writing papers is really very easy!

                Professional custom papers writing service, research paper, essay and term paper writing service from experienced writers at affordable price.
                """)),

                    jshit.pageHeader(t(json("en" to "Features", "ua" to "Features"))),
                    horizBulletsRow(jsArrayOf(
                        json("glyph" to "pencil", "en" to "No plagiarism", "ua" to "No plagiarism"),
                        json("glyph" to "star", "en" to "Only premium quality", "ua" to "Only premium quality"),
                        json("glyph" to "list", "en" to "Free title page, outline, list${jshit.nbsp}of${jshit.nbsp}references", "ua" to "Free title page, outline, list${jshit.nbsp}of${jshit.nbsp}references")
                        ), json("horizContentMargin" to 40)),
                    horizBulletsRow(jsArrayOf(
                        json("glyph" to "gi-piggy-bank", "en" to "One-time and life-time discounts to returning customers", "ua" to "One-time and life-time discounts to returning customers"),
                        json("glyph" to "credit-card", "en" to "30-days money back guarantee", "ua" to "30-days money back guarantee"),
                        json("glyph" to "life-saver", "en" to "24/7 support", "ua" to "24/7 support")
                        ), json("horizContentMargin" to 40)),

                    jshit.pageHeader(t(json("en" to "Who We Are", "ua" to "Who We Are"))),
                    markdownPiece(json("en" to """
            AcademicPaperServed is an experienced custom writing service, consisting of expert writers to deliver written assignments, essays, research papers, term papers, theses, book reports, etc to English-speaking clients worldwide. Our team of professional writers focuses on the highest quality of all types of academic papers. Thesis writing is a challenge to many students. With our assistance it will not be a problem anymore!

            Our writers are versed in various fields of academic writing, so if you do not find a suitable category on this page, feel free to contact our Support Center to find out availability of writing help in your area of interest.

            We have access to most reliable and complete online libraries to make your research or essay unique.

                AcademicPaperServed team consists of expert academic writers providing you with free guidelines, helping with topic selection, proofreading, editing and formatting even if you want to get your essay done overnight! We guarantee premium quality writing with urgent projects.
            """,
                        "ua" to """
            AcademicPaperServed is an experienced custom writing service, consisting of expert writers to deliver written assignments, essays, research papers, term papers, theses, book reports, etc to English-speaking clients worldwide. Our team of professional writers focuses on the highest quality of all types of academic papers. Thesis writing is a challenge to many students. With our assistance it will not be a problem anymore!

            Our writers are versed in various fields of academic writing, so if you do not find a suitable category on this page, feel free to contact our Support Center to find out availability of writing help in your area of interest.

            We have access to most reliable and complete online libraries to make your research or essay unique.

                AcademicPaperServed team consists of expert academic writers providing you with free guidelines, helping with topic selection, proofreading, editing and formatting even if you want to get your essay done overnight! We guarantee premium quality writing with urgent projects.
            """)),

                    renderTestimonials("customer"),

                    jshit.pageHeader(t(json("en" to "What We Offer", "ua" to "What We Offer"))),
                    horizBulletsRow(jsArrayOf(
                        json("glyph" to "envira", "en" to "Custom essay, research, thesis writing", "ua" to "Custom essay, research, thesis writing"),
                        json("glyph" to "rocket", "en" to "Plagiarism-free original papers written from scratch", "ua" to "Plagiarism-free original papers written from scratch"),
                        json("glyph" to "bomb", "en" to "Proofreading and editing of written papers", "ua" to "Proofreading and editing of written papers"),
                        json("glyph" to "book", "en" to "Free guidelines on essay topic selection and writing process", "ua" to "Free guidelines on essay topic selection and writing process")
                        )),
                    locBullets(jsArrayOf(
                        json("en" to "Custom essay, research paper, book report, term paper, precis, sketch, poetry analysis, data collection, thesis writing, SWOT analysis, lab reports, dissertations, reviews, speeches, presentations, case studies, courseworks, homeworks, assignments, creative writing, blog writing, capstone project, grant proposal, lab reports", "ua" to "Custom essay, research paper, book report, term paper, precis, sketch, poetry analysis, data collection, thesis writing, SWOT analysis, lab reports, dissertations, reviews, speeches, presentations, case studies, courseworks, homeworks, assignments, creative writing, blog writing, capstone project, grant proposal, lab reports"),
                        json("en" to "Plagiarism-free original papers written from scratch", "ua" to "Plagiarism-free original papers written from scratch"),
                        json("en" to "Proofreading and editing of written papers", "ua" to "Proofreading and editing of written papers"),
                        json("en" to "Choosing sources for your paper, providing with annotated bibliography upon request", "ua" to "Choosing sources for your paper, providing with annotated bibliography upon request"),
                        json("en" to "Free guidelines on successful essay topic selection and writing process", "ua" to "Free guidelines on successful essay topic selection and writing process"),
                        json("en" to "Individual approach to every customer, no repetitions, free consulting on the paper content", "ua" to "Individual approach to every customer, no repetitions, free consulting on the paper content"),
                        json("en" to "Free revisions till you are completely satisfied", "ua" to "Free revisions till you are completely satisfied"),
                        json("en" to "Meeting your deadline", "ua" to "Meeting your deadline"),
                        json("en" to "Security and confidentiality", "ua" to "Security and confidentiality")
                        ))
                )
                )
            ))

        writePage(json("name" to "why", "highlightedItem" to "why", // For Customer site
            "comp" to jshit.diva(json(),
                jshit.diva.apply(null, js("[]").concat(json("className" to "container"),
                    jshit.pageHeader(t(json("en" to "Why AcademicPaperServed?", "ua" to "Why AcademicPaperServed UA?"))),
                    jsArrayOf(json(
                        "title" to json("en" to "We care about each customer’s academic success", "ua" to "We care about each customer’s academic success"),
                        "content" to json(
                            "en" to "According to the statistics, almost 90% of current U.S. undergraduate and graduate students work either full-time or part-time, while they study. Often, they have little time to complete all of their essays and term papers by themselves, as writing is really time consuming. Others simply don't think of writing as the most pleasurable activity. All students aim for graduation. AcademicPaperServed is here to help them reach this goal.",
                            "ua" to "According to the statistics, almost 90% of current U.S. undergraduate and graduate students work either full-time or part-time, while they study. Often, they have little time to complete all of their essays and term papers by themselves, as writing is really time consuming. Others simply don't think of writing as the most pleasurable activity. All students aim for graduation. AcademicPaperServed is here to help them reach this goal."
                        )
                    ),
                        json(
                            "title" to json("en" to "We make a very strong commitment to quality", "ua" to "We make a very strong commitment to quality"),
                            "content" to json(
                                "en" to "Quality is one of the major benefits of our custom writing service. Every AcademicPaperServed writer is experienced in the special field of studies. As a result, all of the customer's most sophisticated requirements are met by the professional freelance writer with a deep knowledge of the custom paper topic. Our custom essay writers possess Bachelor's / Master's degrees in the related fields of knowledge.",
                                "ua" to "Quality is one of the major benefits of our custom writing service. Every AcademicPaperServed writer is experienced in the special field of studies. As a result, all of the customer's most sophisticated requirements are met by the professional freelance writer with a deep knowledge of the custom paper topic. Our custom essay writers possess Bachelor's / Master's degrees in the related fields of knowledge."
                            )
                        ),
                        json(
                            "title" to json("en" to "We show an individual approach", "ua" to "We show an individual approach"),
                            "content" to json(
                                "en" to """
                To Every Customer and Every Project we deal with.

                    With us customers are sure to get their high school, college and university papers tailored exactly to their requirements and needs. No need to worry about quality! Your professor will be completely satisfied. We follow paper details specifically, provide with relevant sources from our library and/or use definite sources (books, journal and magazine articles, encyclopedia excerpts, peer reviewed studies, etc.) requested by the customer.

                Our team is aware of students’ concerns and issues. Any academic achievement becomes joyful and easy with our team. You are sure to get your writer’s comments on the paper content, advice on choosing a suitable topic, primary and secondary sources selection, ways of presenting it in pubic if needed, etc. You are a getting a complex academic help along with the written coherent text which you, surely, deserve!

                24/7 support will provide you with prompt unrivalled assistance in every matter of your concern and makes sure you’ll get excellent quality paper!

                AcademicPaperServed team helps English speaking customers from all over the world with anything an academic writing implies, from research ideas to styles and design of your paper. We are strictly following the rules of academic presentation combining it with modern and creative approaches. You are sure to get your project written in a plain, clear language with smooth transitions and coherent rendering.
                """,
                                "ua" to """
                To Every Customer and Every Project we deal with.

                    With us customers are sure to get their high school, college and university papers tailored exactly to their requirements and needs. No need to worry about quality! Your professor will be completely satisfied. We follow paper details specifically, provide with relevant sources from our library and/or use definite sources (books, journal and magazine articles, encyclopedia excerpts, peer reviewed studies, etc.) requested by the customer.

                Our team is aware of students’ concerns and issues. Any academic achievement becomes joyful and easy with our team. You are sure to get your writer’s comments on the paper content, advice on choosing a suitable topic, primary and secondary sources selection, ways of presenting it in pubic if needed, etc. You are a getting a complex academic help along with the written coherent text which you, surely, deserve!

                24/7 support will provide you with prompt unrivalled assistance in every matter of your concern and makes sure you’ll get excellent quality paper!

                AcademicPaperServed team helps English speaking customers from all over the world with anything an academic writing implies, from research ideas to styles and design of your paper. We are strictly following the rules of academic presentation combining it with modern and creative approaches. You are sure to get your project written in a plain, clear language with smooth transitions and coherent rendering.
                """
                            )
                        ),
                        json(
                            "title" to json("en" to "We hire only expert writers", "ua" to "We hire only expert writers"),
                            "content" to json(
                                "en" to """
                We involve only experienced staff in the writing process! Each member of the AcademicPaperServed writing team has at least 2 years + experience in academic research field.

                You are sure to get your custom paper done by a native English speaker, who is versed in the area of your research, formats your paper according to the latest requirements of citation styles (MLA, APA, Harvard, Chicago, Turabian, Oxford and others) and delivers with no delays.

                US-based customer support will offer samples of various academic papers for your consideration.

                We’ve been dealing with ALL types of academic papers and we are always glad to offer you help with any essay (like scholarship essay, admission essay, application essay, entrance essay, personal statement) and research paper type at any academic level and in every possible discipline. If you want your paper to be well-organized and logically constructed, choose us!
                """,
                                "ua" to """
                We involve only experienced staff in the writing process! Each member of the AcademicPaperServed writing team has at least 2 years + experience in academic research field.

                You are sure to get your custom paper done by a native English speaker, who is versed in the area of your research, formats your paper according to the latest requirements of citation styles (MLA, APA, Harvard, Chicago, Turabian, Oxford and others) and delivers with no delays.

                US-based customer support will offer samples of various academic papers for your consideration.

                We’ve been dealing with ALL types of academic papers and we are always glad to offer you help with any essay (like scholarship essay, admission essay, application essay, entrance essay, personal statement) and research paper type at any academic level and in every possible discipline. If you want your paper to be well-organized and logically constructed, choose us!
                """
                                )
                        ),
                        json(
                            "title" to json("en" to "Plagiarism-free and full-of-creativity zone", "ua" to "Plagiarism-free and full-of-creativity zone"),
                            "content" to json(
                                "en" to """
                Our writers get paid only when they use their own thoughts and ideas, elucidated through high-quality research and writing proficiency. For that reason, the following is not acceptable under any situation: copy-pasting from websites, copying from offline books, texts, and journals, basing one’s work on the ideas/structure of others or previous pieces of work. All papers are checked against millions of hard copy sources, billions of web pages, and countless pieces of work of other researchers. The papers are checked with the most up-to-date plagiarism detection software prior to being delivered to customer. There is totally no chance of delivering a plagiarized paper.

                At AcademicPaperServed all writers apply a creative approach to every essay. Each custom paper is written in compliance with the requests and desires of the customer.

                    Be assured our friendly and knowledgeable staff will provide you with immediate, top-quality, and US-based customer support. All responses are personalized to the needs of the student.
                """,
                                "ua" to """
                Our writers get paid only when they use their own thoughts and ideas, elucidated through high-quality research and writing proficiency. For that reason, the following is not acceptable under any situation: copy-pasting from websites, copying from offline books, texts, and journals, basing one’s work on the ideas/structure of others or previous pieces of work. All papers are checked against millions of hard copy sources, billions of web pages, and countless pieces of work of other researchers. The papers are checked with the most up-to-date plagiarism detection software prior to being delivered to customer. There is totally no chance of delivering a plagiarized paper.

                At AcademicPaperServed all writers apply a creative approach to every essay. Each custom paper is written in compliance with the requests and desires of the customer.

                    Be assured our friendly and knowledgeable staff will provide you with immediate, top-quality, and US-based customer support. All responses are personalized to the needs of the student.
                """
                                )
                        )
                        ).map{section: dynamic ->
                        jshit.diva(json(),
                            jshit.diva(json(), h3Smaller(t(section.title))),
                            jshit.diva(json(), jshit.markdown(jshit.utils.dedent(t(section.content)))))}
                ))
            )
        ))

        val priceTableRows = jsArrayOf()
        for (dopt in jsArrayToList(legacyStuff.deliveryOptions())) {
            var firstRowForDopt = true
            for (top in jsArrayToList(legacyStuff.typesOfPaper(lang))) {
                val curr = lang
                priceTableRows.push(jsArrayOf(
                    if (firstRowForDopt) legacyStuff.deliveryOptionTitle(dopt, lang) else "",
                    legacyStuff.typeOfPaperTitle(top, lang),
                    legacyStuff.moneyTitleWithCurrency(legacyStuff.priceForDeliveryOptionAndTypeOfPaper(dopt, top), curr, lang)))
                firstRowForDopt = false
            }
        }

        writePage(json("name" to "prices", "highlightedItem" to "prices", // For Customer site
            "comp" to jshit.diva(json(),
                jshit.diva(json("className" to "container"),
                    jshit.pageHeader(t(json("en" to "Our Prices", "ua" to "Наши цены"))),
                    jshit.el("table", json("className" to "table table-hover table-condensed"),
                        jshit.el("thead", json(),
                            jshit.el("tr", json(),
                                jshit.el("th", json(), t(json("en" to "Delivery Option", "ua" to "Срочность"))),
                                jshit.el("th", json(), t(json("en" to "Type of Paper", "ua" to "Тип работы"))),
                                jshit.el("th", json(), t(json("en" to "Price", "ua" to "Цена")))
                                )),
                        jshit.el.apply(null, js("[]").concat("tbody", json(),
                            priceTableRows.map{row: dynamic -> jshit.el("tr", json(),
                                jshit.el("td", json(), row[0]),
                                jshit.el("td", json(), row[1]),
                                jshit.el("td", json(), row[2])
                                )}))),

                        jshit.pageHeader(t(json("en" to "Pricing Policy", "ua" to "Pricing Policy"))),
                        markdownPiece(json(
                            "en" to """
                                Prices at AcademicPaperServed are set to meet the average point on the current custom writing market. This enables us to choose experts writers as freelance members in the team, who are able to meet the highest demands at faculties teaching in English.

                                Our support is daily queried on how one can buy an essay or research paper on a certain topic. AcademicPaperServed does sell pre-written papers, each assignment is made individually and each research is written from scratch. Top-notch writing quality is only achieved through effective communication of the writer and the customer, be it an order on a blog article, high school debate project or a music review.

                                Each quote we give to our customer is based primarily on the delivery date opted, type of an academic written assignment, number of pages and the amount of discount your membership is liable to.

                                You may find out your quote in one minute! Simply press the Order Your Custom Essay Now tab at the top of the page marked in green. Our Customer Support employee will advise you on the price and terms of delivery.
                                """,
                            "ua" to """
                                Prices at AcademicPaperServed are set to meet the average point on the current custom writing market. This enables us to choose experts writers as freelance members in the team, who are able to meet the highest demands at faculties teaching in English.

                                Our support is daily queried on how one can buy an essay or research paper on a certain topic. AcademicPaperServed does sell pre-written papers, each assignment is made individually and each research is written from scratch. Top-notch writing quality is only achieved through effective communication of the writer and the customer, be it an order on a blog article, high school debate project or a music review.

                                Each quote we give to our customer is based primarily on the delivery date opted, type of an academic written assignment, number of pages and the amount of discount your membership is liable to.

                                You may find out your quote in one minute! Simply press the Order Your Custom Essay Now tab at the top of the page marked in green. Our Customer Support employee will advise you on the price and terms of delivery.
                                """
                        )),

                        jshit.pageHeader(t(json("en" to "Bonuses", "ua" to "Bonuses"))),
                        markdownPiece(json(
                            "en" to "Ordering a paper at AcademicPaperServed, you also get:",
                            "ua" to "Ordering a paper at AcademicPaperServed, you also get:"
                            )),
                        locBullets(jsArrayOf(
                            json("en" to "Free Title Page", "ua" to "Free Title Page"),
                            json("en" to "Free Outline", "ua" to "Free Outline"),
                            json("en" to "Free List of References", "ua" to "Free List of References"),
                            json("en" to "Free Plagiarism Report (upon additional request)", "ua" to "Free Plagiarism Report (upon additional request)")
                            )),

                        jshit.pageHeader(t(json("en" to "Discount Policy", "ua" to "Discount Policy"))),
                        markdownPiece(json(
                            "en" to "Please note that each AcademicPaperServed customer is eligible for one-time and life-time discounts. One-time discount is granted to each new customer registered in our system and makes 5% off the first order total. Lifetime discount is provided to each returning customer depending on the total number of pages (on multiple papers) ordered since the moment of registration, and namely:",
                            "ua" to "Please note that each AcademicPaperServed customer is eligible for one-time and life-time discounts. One-time discount is granted to each new customer registered in our system and makes 5% off the first order total. Lifetime discount is provided to each returning customer depending on the total number of pages (on multiple papers) ordered since the moment of registration, and namely:"
                            )),
                        locBullets(jsArrayOf(
                            json("en" to "More than 50 pages${jshit.mdash}5%", "ua" to "Более 50 страниц ${jshit.ndash} 5%"),
                            json("en" to "More than 100 pages${jshit.mdash}10%", "ua" to "Более 100 страниц ${jshit.ndash} 10%"),
                            json("en" to "More than 150 pages${jshit.mdash}15%", "ua" to "Более 150 страниц ${jshit.ndash} 15%"),
                            json("en" to "More than 200 pages${jshit.mdash}30%", "ua" to "Более 200 страниц ${jshit.ndash} 30%")
                            )),
                        markdownPiece(json(
                            "en" to "We’re sure that at AcademicPaperServed we employ a fair discount policy. We respect each certain customer and hope to establish long-term cooperation with him/her. Since customers are our most valued asset, we put a lot of effort to retaining and satisfying them through our flexible lifetime discount policy.",
                            "ua" to "We’re sure that at AcademicPaperServed we employ a fair discount policy. We respect each certain customer and hope to establish long-term cooperation with him/her. Since customers are our most valued asset, we put a lot of effort to retaining and satisfying them through our flexible lifetime discount policy."
                            ))
                        )
                )
            ))

        val sampleItems = t(json(
            "en" to jsArrayOf(
                json("title" to "Sample APA paper", "href" to "apa-sample.doc"),
                json("title" to "Sample MLA research paper", "href" to "mla-sample.doc"),
                json("title" to "Sample Harvard-style paper", "href" to "harvard-sample.doc"),
                json("title" to "Sample Chicago paper", "href" to "chicago-sample.doc"),
                json("title" to "Sample Turabian paper", "href" to "turabian-sample.doc")
                ),
            "ua" to jsArrayOf(
                json("title" to "Пример реферата", "href" to "ua_essay-sample.doc"),
                json("title" to "Пример курсовой работы", "href" to "ua_course-sample.doc"),
                json("title" to "Пример дипломной работы", "href" to "ua_graduate-sample.doc")
                )
        ))

        writePage(json("name" to "samples", "highlightedItem" to "samples", // For Customer site
            "comp" to jshit.diva(json(),
                jshit.diva(json("className" to "container"),
                    jshit.pageHeader(t(json("en" to "Sample Papers", "ua" to "Примеры работ"))),
                    hrefBullets(sampleItems)
                    )
            )
        ))

            writePage(json("name" to "faq", "highlightedItem" to "faq", // For Customer site
            "comp" to jshit.diva(json(),
                jshit.diva.apply(null, js("[]").concat(json("className" to "container"),
                    jshit.pageHeader(t(json("en" to "FAQ", "ua" to "FAQ"))),
                    jsArrayOf(json(
                        "title" to json("en" to "How does AcademicPaperServed work?", "ua" to "How does AcademicPaperServed work?"),
                        "content" to json(
                            "en" to "As soon as you order a custom essay, term paper, research paper or book report your order is reviewed and then the most appropriate writer is assigned, who then takes responsibility to complete it. After your paper is ready, it is send to you via e-mail.",
                            "ua" to "As soon as you order a custom essay, term paper, research paper or book report your order is reviewed and then the most appropriate writer is assigned, who then takes responsibility to complete it. After your paper is ready, it is send to you via e-mail."
                        )
                    ),
                        json(
                            "title" to json("en" to "I have had unpleasant experiences with other custom essay companies. How does AcademicPaperServed differ from fraud services?", "ua" to "I have had unpleasant experiences with other custom essay companies. How does AcademicPaperServed differ from fraud services?"),
                            "content" to json(
                                "en" to "Sorry to say, but we often hear from our clients, that they have been deceived by unknown and disreputable essay writing companies that harm the reputation of legitimate writing companies. On the contrary, our service has done its best to earn the trust of our clients by offering quality custom papers at reasonable price.",
                                "ua" to "Sorry to say, but we often hear from our clients, that they have been deceived by unknown and disreputable essay writing companies that harm the reputation of legitimate writing companies. On the contrary, our service has done its best to earn the trust of our clients by offering quality custom papers at reasonable price."
                            )
                        ),
                        json(
                            "title" to json("en" to "How safe is your service? Is there any risk to place an order online?", "ua" to "How safe is your service? Is there any risk to place an order online?"),
                            "content" to json(
                                "en" to "It is totally safe. Hundreds of our customers make orders and buy custom writing services at AcademicPaperServed daily. All of your transactions are processed securely and encrypted by PayPal. It is impossible to make an unauthorized transaction using your credit card.",
                                "ua" to "It is totally safe. Hundreds of our customers make orders and buy custom writing services at AcademicPaperServed daily. All of your transactions are processed securely and encrypted by PayPal. It is impossible to make an unauthorized transaction using your credit card."
                            )
                        ),
                        json(
                            "title" to json("en" to "What are your policies concerning the paper format and citation?", "ua" to "What are your policies concerning the paper format and citation?"),
                            "content" to json(
                                "en" to """
                Our writers use Microsoft Word .doc format by default (Times New Roman, 12, double-spaced, ~ 250-300 words/page). However, any format specified by the customer is available. The same concerns citation style.,

                If not chosen, the writer chooses a citation style most appropriate for the written assignment in process. The latest edition with newest formatting style and its rules is applied, unless indicated otherwise.
                """,
                                "ua" to """
                Our writers use Microsoft Word .doc format by default (Times New Roman, 12, double-spaced, ~ 250-300 words/page). However, any format specified by the customer is available. The same concerns citation style.

                If not chosen, the writer chooses a citation style most appropriate for the written assignment in process. The latest edition with newest formatting style and its rules is applied, unless indicated otherwise.
                """
                            )
                        ),
                        json(
                            "title" to json("en" to "What if I don’t like my paper and it does not meet the requirements?", "ua" to "What if I don’t like my paper and it does not meet the requirements?"),
                            "content" to json(
                                "en" to "After you have ordered a paper, you can be confident that it will meet your expectations. Our writers will scrupulously adhere to your exact instructions to write a custom, first-rate academic paper. The team of writers works directly from customers’ instructions, and our clients get what they ask for. Still, there are moments when a customer can feel the writer has missed any of his/her requirements. In this case the customer should request a free revision. The writer will improve the paper and include all the instructions and will not stop working until the client is happy.",
                                "ua" to "After you have ordered a paper, you can be confident that it will meet your expectations. Our writers will scrupulously adhere to your exact instructions to write a custom, first-rate academic paper. The team of writers works directly from customers’ instructions, and our clients get what they ask for. Still, there are moments when a customer can feel the writer has missed any of his/her requirements. In this case the customer should request a free revision. The writer will improve the paper and include all the instructions and will not stop working until the client is happy."
                            )
                        ),
                        json(
                            "title" to json("en" to "Does your service provide refunds?", "ua" to "Does your service provide refunds?"),
                            "content" to json(
                                "en" to "Our aim is total customer satisfaction. According to our monthly data we have an acceptable 95% approval rating among our customers. Our writers work hard to do what is best for our customers. If the final version of the paper does not meet customer’s specified criteria, he/she can ask for a revision within thirty days. The writer will add any missing requirements and deliver the revision in 24 hours. If the customer provides us with evidence that the final revised version is still lacking some of the specified requirements, s/he can request a refund within three days after the completion of the order.",
                                "ua" to "Our aim is total customer satisfaction. According to our monthly data we have an acceptable 95% approval rating among our customers. Our writers work hard to do what is best for our customers. If the final version of the paper does not meet customer’s specified criteria, he/she can ask for a revision within thirty days. The writer will add any missing requirements and deliver the revision in 24 hours. If the customer provides us with evidence that the final revised version is still lacking some of the specified requirements, s/he can request a refund within three days after the completion of the order."
                            )
                        ),
                        json(
                            "title" to json("en" to "What if my paper is found to be plagiarized?", "ua" to "What if my paper is found to be plagiarized?"),
                            "content" to json(
                                "en" to "In fact, it is impossible. The writer gets paid only when he uses his own thoughts and ideas, elucidated through high-quality research/ and writing proficiency. For that reason, the following is not acceptable under any situation: copy-pasting from websites, copying from offline books, texts, and journals, basing one’s work on the ideas/structure of others or previous pieces of work. All papers are checked against millions of hard copy sources, billions of web pages, and countless pieces of work of other researchers. The papers are checked with most up to date anti-plagiarism software before sending any paper to customer. There is totally no chance of delivering a plagiarized paper.",
                                "ua" to "In fact, it is impossible. The writer gets paid only when he uses his own thoughts and ideas, elucidated through high-quality research/ and writing proficiency. For that reason, the following is not acceptable under any situation: copy-pasting from websites, copying from offline books, texts, and journals, basing one’s work on the ideas/structure of others or previous pieces of work. All papers are checked against millions of hard copy sources, billions of web pages, and countless pieces of work of other researchers. The papers are checked with most up to date anti-plagiarism software before sending any paper to customer. There is totally no chance of delivering a plagiarized paper."
                            )
                        ),
                        json(
                            "title" to json("en" to "Can I check my order status?", "ua" to "Can I check my order status?"),
                            "content" to json(
                                "en" to "In order to track the order status the customer can communicate with the writer or upload supplementary files or instructions.",
                                "ua" to "In order to track the order status the customer can communicate with the writer or upload supplementary files or instructions."
                            )
                        ),
                        json(
                            "title" to json("en" to "Can I contact the writer?", "ua" to "Can I contact the writer?"),
                            "content" to json(
                                "en" to "Certainly, there is a personal message board on each private account. A customer can easily post a message to the assigned writer, and he/she will reply to almost immediately. If the writer encounters any questions, he will contact the customer the in same way.",
                                "ua" to "Certainly, there is a personal message board on each private account. A customer can easily post a message to the assigned writer, and he/she will reply to almost immediately. If the writer encounters any questions, he will contact the customer the in same way."
                            )
                        ),
                        json(
                            "title" to json("en" to "Can I ever find my ordered paper being available to the public?", "ua" to "Can I ever find my ordered paper being available to the public?"),
                            "content" to json(
                                "en" to "Our service guarantees that the paper that has been written by our writers will never be available to the public. All papers are stored under an exclusively developed security system.",
                                "ua" to "Our service guarantees that the paper that has been written by our writers will never be available to the public. All papers are stored under an exclusively developed security system."
                            )
                        ),
                        json(
                            "title" to json("en" to "How can I make sure the writer has understood my assignment correctly?", "ua" to "How can I make sure the writer has understood my assignment correctly?"),
                            "content" to json(
                                "en" to """
                When you start an ordering process, please, make sure you provide your writer with all necessary info, instructions, guidelines, additional materials for study, your class notes if any, and your personal preferences as to the writing style and paper formatting. It will surely facilitate the writing process and advance your chances in getting a higher grade.

                We specifically created an easy-to-use interface for your communication with the writer and support center in the process of paper writing.

                AcademicPaperServed writers’ team is highly flexible and is ready to consider every order on a personal basis. We value our customers and set communication as a priority. We will appreciate your help in giving explicit order details once your order is made.
                """,
                                "ua" to """
                When you start an ordering process, please, make sure you provide your writer with all necessary info, instructions, guidelines, additional materials for study, your class notes if any, and your personal preferences as to the writing style and paper formatting. It will surely facilitate the writing process and advance your chances in getting a higher grade.

                We specifically created an easy-to-use interface for your communication with the writer and support center in the process of paper writing.

                AcademicPaperServed writers’ team is highly flexible and is ready to consider every order on a personal basis. We value our customers and set communication as a priority. We will appreciate your help in giving explicit order details once your order is made.
                """
                            )
                        ),
                        json(
                            "title" to json("en" to "What if my professor requires to hand in all copies of sources used?", "ua" to "What if my professor requires to hand in all copies of sources used?"),
                            "content" to json(
                                "en" to "The writers will provide you with the necessary sources used to write your paper if you request it in advance. Sometimes we use paid online libraries providing access per day/hour, in this case it is extremely difficult to come back there again and copy-paste material. Some libraries have copyright protection software, so cannot always copy the text for you. However, your timely request for the sources package will give us direction in a library choice. ",
                                "ua" to "The writers will provide you with the necessary sources used to write your paper if you request it in advance. Sometimes we use paid online libraries providing access per day/hour, in this case it is extremely difficult to come back there again and copy-paste material. Some libraries have copyright protection software, so cannot always copy the text for you. However, your timely request for the sources package will give us direction in a library choice. "
                            )
                        )
                        ).map{section: dynamic ->
                        jshit.diva(json(),
                            jshit.diva(json(), markdownPiece("> " + t(section.title))),
                            jshit.diva(json("style" to json("marginBottom" to 20, "marginTop" to -5)), jshit.markdown(jshit.utils.dedent(t(section.content)))))}
                ))
            )
        ))

            writePage(json("name" to "contact", "highlightedItem" to "contact", // For Customer site
            "comp" to jshit.diva(json(),
                jshit.diva(json("className" to "container"),
                    jshit.pageHeader(t(json("en" to "Contact Us", "ua" to "Contact Us"))),
                    markdownPiece(json(
                        "en" to """
                Thank you for visiting AcademicPaperServed website! We will be happy to answer any questions, give a quote, and select a writer for your paper at any academic level. We value each customer and strive to achieve best results through communication process. Customer support representative is ready to advise you on the ordering process, choosing your delivery option and selecting an appropriate writer.
                """,
                        "ua" to """
                Thank you for visiting AcademicPaperServed website! We will be happy to answer any questions, give a quote, and select a writer for your paper at any academic level. We value each customer and strive to achieve best results through communication process. Customer support representative is ready to advise you on the ordering process, choosing your delivery option and selecting an appropriate writer.
                """
                    )),

                    jshit.diva(json("style" to json("marginBottom" to 10)), t(json("en" to "AcademicPaperServed headquarter:", "ua" to "AcademicPaperServed headquarter:"))),
                    jshit.diva(json("style" to json("whiteSpace" to "pre", "fontFamily" to "monospace")), tdedent(json(
                        "en" to """
                DP World Inc
                624 W Kristina Ln
                    Round Lake, IL 60073
                United States
                    """,
                        "ua" to """
                DP World Inc
                624 W Kristina Ln
                    Round Lake, IL 60073
                United States
                    """
                    )))
                )
            )
        ))

            val blogItems = t(json(
            "en" to jsArrayOf(
                json(
                    "listTitle" to "Why Would I Order a Research Paper Online?",
                    "title" to "Being a Student is not Easy",
                    "slug" to "why-order",
                    "content" to """
            Thousands of students find themselves torn between multiple obligations. They badly need good grades, but when it comes to writing essays and term papers, the situation becomes even more problematic. Remember, how hard you try to write a paper. You spend hours, trying to choose the best topic. You need to write a paper that is grammatically and stylistically correct. You need to present your argument logically and convincingly.

            No wonder that, in these moments, the only thing you want is to have someone write a good custom essay for you. When you no longer feel capable of producing quality essays and term papers, all you need is to know that a qualified professional will help you to deal with your custom essay. You need to know that your essays and term papers will be submitted on time. You also need to know that your custom essay will be academically sound.

            What to do? This is a difficult question, but we have a good answer. Order a research paper, and you will forget about your writing troubles. When you have difficulty writing a custom essay, you can order a research paper that will meet the standards of advanced academic writing. You can have your essays and term papers written by an educated professional, which will increase your chances to earn a good grade. If you are tired of looking for good topics and arguments to write a paper, ordering a research paper is the best solution to your writing problems. Feel free to spend your time with family and friends, while professional writers are working on your custom essay!

            Order a research paper, and you will see how your life becoming easy and self-fulfilling!
            """
                ),
                json(
                    "listTitle" to "High School Debate Topics in the United States",
                    "title" to "High School Debate Topics in the United States",
                    "slug" to "debate-topics",
                    "content" to """
            These days high school debate (from the Latin “debatum” meaning “to reach an argument between two opposite viewpoints”) is undoubtedly one of the most popular forms of academic discussions. High school debate topics in the United States are diverse and selected with purpose to involve students into two essential processes: argumentative thinking and communication. All modern US secondary and high schools use debate as an integral element of classroom activity, as a form of knowledge appraisal, testing and skill gap detection. High school debate topics generally relate to students’ research activity (development of information management skills) and to the educational activity (development of capability to work individually and in team; development of leadership qualities).

            Modern debate topics for high school students are selected with purpose to teach them how to analyze different problems (past and current) thoroughly and to recognize tolerance and respect as the highest values to be considered at problem discussion. High school debate topics must encourage and motivate students to search for truth rather than just to practice rhetoric. In other words, competitiveness and hunger for victory should not be dominant over readiness and willingness to understand and explore the subject matter.

            Debate topics for high school students should not stimulate the use of any double standards, but should promote tolerance to other men’s standpoints and search for common values, considering the difference of debate participants.

            High school debate topics should never be used as propaganda of any political parties and actions, advertisement of organizations and infrastructures, exaltation of some particular idea or personality.

            Educators admit the fact that sometimes it is very difficult to choose the right high school debate topic for classroom activity. The topic must possess a set of specific properties to be able to transform a classroom discussion into a debate, and namely: it should be formulated in a way that would reflect the modern students’ lifestyles and meet their expectations (school debate topic must catch students’ attention); it must be somehow relative to students’ area of interests and current research/study theme; it must be up to date and must consider further development, real-life implementation and alternative solutions.

            So, what classroom debate topics may possess these characteristics? Our online survey finds that the most popular high school debate topics in the United States are:

            * Should your class be considered for a field trip this year?
            * Should school uniform become obligatory?
            * Should slow achievers be permitted to have a job such as baby-sitting or mowing yards?
            * Should you be allowed to spend your own money in the way you want?
            * Should you be allowed to come to the class with extraordinary hair styles and excessive piercing?
            * Should you be allowed to watch R-rated movies?
            * Should your parents permit you to attend sleep-over parties?
            * Should your parents require that you do chores around the house?
            * Should you wear formal clothes for special occasions if your parents require it from you and you do not feel like doing this?
            * Should you be permitted to have any hobby you want?
            * Should your parents permit you to get a tattoo?
            * Should you be allowed to purchase alcohol and cigarettes before you are 21?
            * Should light drugs like marihuana be legalized?
            * Should you be permitted to bring your pet to the classroom?

            However, besides classroom debate, many US high schools organize formal academic debate for which high school debate topics, different from those used in the class, are chosen. During such formal debates students are divided in two groups, the assigned instructor provides a student debate topic and the group of couches (usually representatives of the academic staff and their invited colleagues from other high schools) judge the debate. In this case high school debate resembles some internal oratory contest or an interesting game. The formal debate should adhere to some ethical norms and all three parties – debaters, instructors and couches – must follow these norms which are, in fact, mutual respect and tolerance, sincerity, honesty and cultural exchange of ideas.

            At discussion of high school debate topics debaters should keep away from attacking their opponents and should argue in a friendly manner. It is required that debaters use precise data and facts when constructing an argument and do not state anything that does not fall within the limits of their own expertise and knowledge base. It is forbidden for debaters to misrepresent facts, examples and opinions. Debaters must listen to their opponents attentively and must not misrepresent their statements in further discussions.

            For formal academic debates topics for high school students are usually chosen by the debate organizers. The instructors should make sure that debaters are cogent to the school debate topic and do not go to debris.

            Our research shows that formal high school debate topics widely used in the United States are rather politics and business oriented. High school debate topics evidence that modern US society is very much concerned about President Bush’s governance, conflicts in the Middle East, gun control, threats to liberty and democracy etc. To exemplify this observation, let us view the Michigan Education Report and look at the program of 2005 High School Debate Workshop hosted by the Mackinac Center for Public Policy, one of the leading America’s academic debate organizers and councilors. The workshop was attended by more than 300 high school debaters and their instructors. During the workshop the high school debate topic was: “Resolved: That the United States federal government should substantially decrease its authority either to detain without charge or to search without probable cause”. As an opening of this student debate topic the quotation excerpted from the National Federation of State High School Associations Report was used: “The American Constitution was designed to limit and constrain the use of power in order to protect liberty. But as the founders knew, and as has become even clearer in modern times, liberty can be threatened even by well-meaning people. . . The greatest dangers to liberty lurk in insidious encroachment by men of zeal, well-meaning but without understanding” ([http://www.mackinac.org/pubs/mer/article.aspx?ID=7480](http://www.mackinac.org/pubs/mer/article.aspx?ID=7480)).

            We are using this example as an argument to the above statement that formal high school debate topics substantially reflect current concerns of present-day America’s society. The Mackinac High School Debate Workshop of 2005 was considered to be the best debate program in ten years.

            Other less prominent examples show that the most popular formal high school debate topics in the United States are:

            * Should wearing a helmet at biking be mandatory?
            * Does the US President do a good job?
            * Should the Pledge of Allegiance be recited in schools on a daily basis?
            * Is beauty only skin deep?
            * Does our society have a right to put convicts to death?
            * Should cloning be outlawed?
            * Should animals be used for scientific research and experiments?

            So, as we can see, high school debate topics substantially reflect the key questions that both the US policy-makers and average citizens put themselves every now and then. Putting some actual and commonplace topic at stake, educators involve their students in modeling of real-life situations and teach them to be able to argumentatively defend their own standpoints and to honestly attack the opposite viewpoints. And no secret that these abilities are sometimes more important in real life than any theoretical knowledge obtained during high school lectures.
            """
                ),
                json(
                    "listTitle" to "How to Choose a Good Informative Speech Topic",
                    "title" to "How to Choose a Good Informative Speech Topic",
                    "slug" to "speech-topc",
                    "content" to """
            Informative speaking is one of the most essential components of high school and college students’ activities. In fact, it offers students a good opportunity to practice their writing, organizing, speaking and researching skills. Informative speaking helps students learn how to discover, manage and present information clearly. Another great advantage of informative speaking consists in allowing young people to overcome public speaking barriers and to develop their own unique public speaking style that will undoubtedly be of use in professional careers.

            Specialists claim that to make a successful informative speech, it is of primary importance to choose a good informative speech topic. There are a great many various strategies aimed at helping choose informative speech topics that will definitely work. It is highly recommended that a student selects informative speech topic that is first of all interesting and exciting to him/her. Only interesting informative speech topics will allow a person to be enthusiastic and to attract the audience’s attention while delivering the speech. Interesting informative speech topic will also motivate a student to research the subject matter closely and to gain some advanced knowledge about the issue at hand. It is suggested that a public speaker makes sure that his/her informative speech topic is appropriate for the situation and that it is an excellent match for interests and desires of the target audience. Another suggestion is that a speaker makes sure that his/her informative speech topic is specific enough. The latter suggestion is explained by the fact that a speaker is usually limited in speech time and will not be able to cover any informative speech topic quite fully.

            Those of you who have ever been public speakers before must know how hard it is to find the best topic for an informative speech. Those who only plan of making a public speech some day must always keep in mind that it is not easy to find a good informative speech topic. Below we will try to provide you with some important tips as for how to choose a proper informative speech topic and make your future speech a real success.

            So, where can you get ideas for good informative speech topics? In fact, everywhere! First of all just calm down, take a seat and think about what type of informative speech you are going to make. There are four major types of informative speeches: speeches about objects, about processes, about concepts and about events. Now that you have taken your time and chosen your informative speech type, you can begin searching for a good informative speech topic. If you plan to speak about objects, you must focus on things that either already exist in this world, or are about to be introduced to the mankind. Now spend some time for brainstorming. Potential sources of information you will use for your speech preparation are: public or high school libraries, Internet and scientific journals. The list of possible interesting topics for informative speech about objects includes: a bike, a scuba, surgical lasers, the Central Intelligence Agency, the lemmings etc.

            If you choose to speak about the process, you must focus on patterns of action. In other words, your speech should be a “how-to” guide. The sources of inspiration stay the same: libraries, online databases, journals. You can, however, expand your search by studying laboratory reports and forum/conference briefings. Here is the list of possible interesting topics for informative speech about the processes: how the World Wide Web works, how to write a rival-beating resume, how to use scuba/bike etc. Remember, once you decide to speak about the process, your search key words must be “HOW TO”.

            But you prefer to speak about events? Ok, no problems! Here you should focus on things that already happened, are currently happening or are supposed to happen in the future. To tell you the truth, it is one of the most difficult types of informative speech. It is very hard to find a good informative speech topic if you want to speak about events. As a matter of fact, most of historical events may be well-known to your audience (especially if you speak in front of the history or political sciences’ class) and your listeners will be bored hearing about them once again. So, the suggestion is that you try to find some unique and publicly unknown information about this or that famous event in order to make your informative speech interesting. Always limit your speech focus to those aspects which can easily be adapted for your audience and further discussion. Look at the list of good informative speech topics in regards to events and try to understand how to choose the best informative speech topic: new vision of Kennedy’s murder, the 1963 Civil Rights March on Washington, the Battle of the Bulge, the World Series, newly discovered facts about Marilyn Monroe etc. But keep in mind – you will probably have to burn the midnight oil in order to prepare a great and attractive speech for your informative speech topic.

            Informative speeches about concepts usually relate to ideas, beliefs and theories. Many public speakers find this type of informative speech very interesting, since it allows them to use their abstract thinking and to develop creativity. However, upon deciding to make a speech about the concept, take care to be comprehensible and clear while presenting your selected concept. Such informative speeches often employ a persuasive tone. All the data and facts used within the limits of your informative speech topic must be based on argumentative and unbiased information. To develop your informative speech topic, go to statistics reports, thematic books and journals and try to find numerous expert opinions about the single subject matter. The list of potential informative speech topics is as follows: the philosophy of Buddhism, feminism, the Big Bang theory etc.

            Nevertheless, in many cases you may not have an assigned informative speech topic or you may be asked to prepare a free informative speech topic. First thing you should do is…relax and take it easy! Then shoot up and start dancing! We are not kidding. This may be your once-in-a-lifetime chance to express your creative mindset and artistic skills. Now you can choose any informative speech topic you like. But still do not forget – the more you know about your topic, the better speech you will make. You must be ready to answer the audience’s questions which can be very provocative in a way. So, show them your best!

            Now you are a generator of topic ideas for informative college or high school speech. You already know where to look for information. And prior to doing a research, think about your own interests. If you like arts, try to relate your informative speech topic to arts. Look at the list of free art-related informative speech topics below and adjust your thinking accordingly:

            * Graffiti is a unique expression of youth sub-culture.
            * Why did Malevich’s “Black Square” become world-recognized?
            * Is it possible to combine classical music and rap?
            * Cyber punk as a new literature trend.
            * How to promote your fiction writing online etc, etc.

            And you have much broader horizons to use! Just switch on your imagination! Still not sure which free informative speech topic will be good enough for you? No problems, we will give you some more useful tips!

            **Tip 1.** Think about your recent classes and remember which classroom debates you have experienced (if any). The cause of the debate may be a good informative speech topic.

            **Tip 2.** Talk to your friends and peers and try to find out what interests them most of all.

            **Tip 3.** Take surf on diverse online forums and discussion boards. Each of them definitely has a thread highlighting youngsters’ interests and hobbies. Use them as your informative speech topic’s ideas.

            And our last tip summarizing all the above described things is – like your informative speech topic and make sure to research it as deeply as possible! And maybe some day you will become a prominent advisor on interesting informative speech topics’ selection!
            """
                ),
                json(
                    "listTitle" to "How to Choose a Good Research or Essay Topic",
                    "title" to "How to Choose a Good Research or Essay Topic",
                    "slug" to "essay-topic",
                    "content" to """
            Henry Miller once said, ‘Writing, like life itself, is a voyage of discovery’. In this life we are guided by our parents, teachers and mentors who constantly teach us how to properly plan our steps in order to make as few errors as possible. Life is driven by other men’s experience. AcademicPaperServed is designed specifically to smoothly guide you through unruly and impertinent ocean of college research topics, topics for high school debate, informative speech topics, essay topics etc. Over quite a long period of time we have been observing the situations when students could not reveal their actual writing potential due to wrongly chosen college and high school research, essay or debate topics. One of the key goals of EasyWriting.Org is to teach you how to choose a good high school debate, informative speech, and college essay or research topic - a topic that will work and satisfy your own, your audience and your instructor’s aspirations and expectations! We’d like to share with you the best practice of putting a pen to the paper successfully. Our suggestions are based on experience of people who reached the highest goal of effective writing and not on some common practice of some abstract personalities!

            > Writing is not a preplanned recitation of what you know; writing Is thinking.
            >
            > ${jshit.mdash}Donald Murray

            Why not perceive writing as an exciting opportunity to make meaning out of your experiences and ideas, to help you think more clearly and independently and to establish new understandings in order to make new connections? In other words, change your current attitude towards writing!

            > The two most engaging powers of an author are to make new things familiar and familiar things new.
            >
            > ${jshit.mdash}Dr. Samuel Johnson

            Be sure you understand your assignment well and do not hesitate overloading your instructor with as many questions as possible to get an in-depth and insightful understanding of what to write about! Feel the power of being able to inform others, while learning something new at the same time! Isn’t it exciting to deliver via obtaining? Millions of men worldwide dream of having such a power. Think of what you can lose if you don’t try to love writing.

            > It's good to rub and polish our brain against that of others.
            >
            > ${jshit.mdash}Montaigne

            When choosing a good topic for your informative speech, high school debate, college essay or research, brainstorm your topic ideas first! Take a sheet of paper and a pen, draw a line in the center of the paper, and write questions on the left and answers on the right. Ask yourself:

            * What you are interested in
            * Which topic has the greatest potential to inspire your creative thinking
            * What you feel strongly about
            * What you are more competent about
            * What you want to learn more about
            * Whether you have recently watched anything interesting and attention-grabbing on TV

            C’mon! Polish your brain! Open your eyes! What if a good argumentative essay topic is hiding in your fridge? Maybe there is a good informative speech topic hanging in your wardrobe?
            """
                )
                ),
            "ua" to jsArrayOf(
                json(
                    "listTitle" to "Why Would I Order a Research Paper Online?",
                    "title" to "Being a Student is not Easy",
                    "slug" to "why-order",
                    "content" to """
            Thousands of students find themselves torn between multiple obligations. They badly need good grades, but when it comes to writing essays and term papers, the situation becomes even more problematic. Remember, how hard you try to write a paper. You spend hours, trying to choose the best topic. You need to write a paper that is grammatically and stylistically correct. You need to present your argument logically and convincingly.

            No wonder that, in these moments, the only thing you want is to have someone write a good custom essay for you. When you no longer feel capable of producing quality essays and term papers, all you need is to know that a qualified professional will help you to deal with your custom essay. You need to know that your essays and term papers will be submitted on time. You also need to know that your custom essay will be academically sound.

            What to do? This is a difficult question, but we have a good answer. Order a research paper, and you will forget about your writing troubles. When you have difficulty writing a custom essay, you can order a research paper that will meet the standards of advanced academic writing. You can have your essays and term papers written by an educated professional, which will increase your chances to earn a good grade. If you are tired of looking for good topics and arguments to write a paper, ordering a research paper is the best solution to your writing problems. Feel free to spend your time with family and friends, while professional writers are working on your custom essay!

            Order a research paper, and you will see how your life becoming easy and self-fulfilling!
            """
                ),
                json(
                    "listTitle" to "High School Debate Topics in the United States",
                    "title" to "High School Debate Topics in the United States",
                    "slug" to "debate-topics",
                    "content" to """
            These days high school debate (from the Latin “debatum” meaning “to reach an argument between two opposite viewpoints”) is undoubtedly one of the most popular forms of academic discussions. High school debate topics in the United States are diverse and selected with purpose to involve students into two essential processes: argumentative thinking and communication. All modern US secondary and high schools use debate as an integral element of classroom activity, as a form of knowledge appraisal, testing and skill gap detection. High school debate topics generally relate to students’ research activity (development of information management skills) and to the educational activity (development of capability to work individually and in team; development of leadership qualities).

            Modern debate topics for high school students are selected with purpose to teach them how to analyze different problems (past and current) thoroughly and to recognize tolerance and respect as the highest values to be considered at problem discussion. High school debate topics must encourage and motivate students to search for truth rather than just to practice rhetoric. In other words, competitiveness and hunger for victory should not be dominant over readiness and willingness to understand and explore the subject matter.

            Debate topics for high school students should not stimulate the use of any double standards, but should promote tolerance to other men’s standpoints and search for common values, considering the difference of debate participants.

            High school debate topics should never be used as propaganda of any political parties and actions, advertisement of organizations and infrastructures, exaltation of some particular idea or personality.

            Educators admit the fact that sometimes it is very difficult to choose the right high school debate topic for classroom activity. The topic must possess a set of specific properties to be able to transform a classroom discussion into a debate, and namely: it should be formulated in a way that would reflect the modern students’ lifestyles and meet their expectations (school debate topic must catch students’ attention); it must be somehow relative to students’ area of interests and current research/study theme; it must be up to date and must consider further development, real-life implementation and alternative solutions.

            So, what classroom debate topics may possess these characteristics? Our online survey finds that the most popular high school debate topics in the United States are:

            * Should your class be considered for a field trip this year?
            * Should school uniform become obligatory?
            * Should slow achievers be permitted to have a job such as baby-sitting or mowing yards?
            * Should you be allowed to spend your own money in the way you want?
            * Should you be allowed to come to the class with extraordinary hair styles and excessive piercing?
            * Should you be allowed to watch R-rated movies?
            * Should your parents permit you to attend sleep-over parties?
            * Should your parents require that you do chores around the house?
            * Should you wear formal clothes for special occasions if your parents require it from you and you do not feel like doing this?
            * Should you be permitted to have any hobby you want?
            * Should your parents permit you to get a tattoo?
            * Should you be allowed to purchase alcohol and cigarettes before you are 21?
            * Should light drugs like marihuana be legalized?
            * Should you be permitted to bring your pet to the classroom?

            However, besides classroom debate, many US high schools organize formal academic debate for which high school debate topics, different from those used in the class, are chosen. During such formal debates students are divided in two groups, the assigned instructor provides a student debate topic and the group of couches (usually representatives of the academic staff and their invited colleagues from other high schools) judge the debate. In this case high school debate resembles some internal oratory contest or an interesting game. The formal debate should adhere to some ethical norms and all three parties – debaters, instructors and couches – must follow these norms which are, in fact, mutual respect and tolerance, sincerity, honesty and cultural exchange of ideas.

            At discussion of high school debate topics debaters should keep away from attacking their opponents and should argue in a friendly manner. It is required that debaters use precise data and facts when constructing an argument and do not state anything that does not fall within the limits of their own expertise and knowledge base. It is forbidden for debaters to misrepresent facts, examples and opinions. Debaters must listen to their opponents attentively and must not misrepresent their statements in further discussions.

            For formal academic debates topics for high school students are usually chosen by the debate organizers. The instructors should make sure that debaters are cogent to the school debate topic and do not go to debris.

            Our research shows that formal high school debate topics widely used in the United States are rather politics and business oriented. High school debate topics evidence that modern US society is very much concerned about President Bush’s governance, conflicts in the Middle East, gun control, threats to liberty and democracy etc. To exemplify this observation, let us view the Michigan Education Report and look at the program of 2005 High School Debate Workshop hosted by the Mackinac Center for Public Policy, one of the leading America’s academic debate organizers and councilors. The workshop was attended by more than 300 high school debaters and their instructors. During the workshop the high school debate topic was: “Resolved: That the United States federal government should substantially decrease its authority either to detain without charge or to search without probable cause”. As an opening of this student debate topic the quotation excerpted from the National Federation of State High School Associations Report was used: “The American Constitution was designed to limit and constrain the use of power in order to protect liberty. But as the founders knew, and as has become even clearer in modern times, liberty can be threatened even by well-meaning people. . . The greatest dangers to liberty lurk in insidious encroachment by men of zeal, well-meaning but without understanding” ([http://www.mackinac.org/pubs/mer/article.aspx?ID=7480](http://www.mackinac.org/pubs/mer/article.aspx?ID=7480)).

            We are using this example as an argument to the above statement that formal high school debate topics substantially reflect current concerns of present-day America’s society. The Mackinac High School Debate Workshop of 2005 was considered to be the best debate program in ten years.

            Other less prominent examples show that the most popular formal high school debate topics in the United States are:

            * Should wearing a helmet at biking be mandatory?
            * Does the US President do a good job?
            * Should the Pledge of Allegiance be recited in schools on a daily basis?
            * Is beauty only skin deep?
            * Does our society have a right to put convicts to death?
            * Should cloning be outlawed?
            * Should animals be used for scientific research and experiments?

            So, as we can see, high school debate topics substantially reflect the key questions that both the US policy-makers and average citizens put themselves every now and then. Putting some actual and commonplace topic at stake, educators involve their students in modeling of real-life situations and teach them to be able to argumentatively defend their own standpoints and to honestly attack the opposite viewpoints. And no secret that these abilities are sometimes more important in real life than any theoretical knowledge obtained during high school lectures.
            """
                ),
                json(
                    "listTitle" to "How to Choose a Good Informative Speech Topic",
                    "title" to "How to Choose a Good Informative Speech Topic",
                    "slug" to "speech-topc",
                    "content" to """
            Informative speaking is one of the most essential components of high school and college students’ activities. In fact, it offers students a good opportunity to practice their writing, organizing, speaking and researching skills. Informative speaking helps students learn how to discover, manage and present information clearly. Another great advantage of informative speaking consists in allowing young people to overcome public speaking barriers and to develop their own unique public speaking style that will undoubtedly be of use in professional careers.

            Specialists claim that to make a successful informative speech, it is of primary importance to choose a good informative speech topic. There are a great many various strategies aimed at helping choose informative speech topics that will definitely work. It is highly recommended that a student selects informative speech topic that is first of all interesting and exciting to him/her. Only interesting informative speech topics will allow a person to be enthusiastic and to attract the audience’s attention while delivering the speech. Interesting informative speech topic will also motivate a student to research the subject matter closely and to gain some advanced knowledge about the issue at hand. It is suggested that a public speaker makes sure that his/her informative speech topic is appropriate for the situation and that it is an excellent match for interests and desires of the target audience. Another suggestion is that a speaker makes sure that his/her informative speech topic is specific enough. The latter suggestion is explained by the fact that a speaker is usually limited in speech time and will not be able to cover any informative speech topic quite fully.

            Those of you who have ever been public speakers before must know how hard it is to find the best topic for an informative speech. Those who only plan of making a public speech some day must always keep in mind that it is not easy to find a good informative speech topic. Below we will try to provide you with some important tips as for how to choose a proper informative speech topic and make your future speech a real success.

            So, where can you get ideas for good informative speech topics? In fact, everywhere! First of all just calm down, take a seat and think about what type of informative speech you are going to make. There are four major types of informative speeches: speeches about objects, about processes, about concepts and about events. Now that you have taken your time and chosen your informative speech type, you can begin searching for a good informative speech topic. If you plan to speak about objects, you must focus on things that either already exist in this world, or are about to be introduced to the mankind. Now spend some time for brainstorming. Potential sources of information you will use for your speech preparation are: public or high school libraries, Internet and scientific journals. The list of possible interesting topics for informative speech about objects includes: a bike, a scuba, surgical lasers, the Central Intelligence Agency, the lemmings etc.

            If you choose to speak about the process, you must focus on patterns of action. In other words, your speech should be a “how-to” guide. The sources of inspiration stay the same: libraries, online databases, journals. You can, however, expand your search by studying laboratory reports and forum/conference briefings. Here is the list of possible interesting topics for informative speech about the processes: how the World Wide Web works, how to write a rival-beating resume, how to use scuba/bike etc. Remember, once you decide to speak about the process, your search key words must be “HOW TO”.

            But you prefer to speak about events? Ok, no problems! Here you should focus on things that already happened, are currently happening or are supposed to happen in the future. To tell you the truth, it is one of the most difficult types of informative speech. It is very hard to find a good informative speech topic if you want to speak about events. As a matter of fact, most of historical events may be well-known to your audience (especially if you speak in front of the history or political sciences’ class) and your listeners will be bored hearing about them once again. So, the suggestion is that you try to find some unique and publicly unknown information about this or that famous event in order to make your informative speech interesting. Always limit your speech focus to those aspects which can easily be adapted for your audience and further discussion. Look at the list of good informative speech topics in regards to events and try to understand how to choose the best informative speech topic: new vision of Kennedy’s murder, the 1963 Civil Rights March on Washington, the Battle of the Bulge, the World Series, newly discovered facts about Marilyn Monroe etc. But keep in mind – you will probably have to burn the midnight oil in order to prepare a great and attractive speech for your informative speech topic.

            Informative speeches about concepts usually relate to ideas, beliefs and theories. Many public speakers find this type of informative speech very interesting, since it allows them to use their abstract thinking and to develop creativity. However, upon deciding to make a speech about the concept, take care to be comprehensible and clear while presenting your selected concept. Such informative speeches often employ a persuasive tone. All the data and facts used within the limits of your informative speech topic must be based on argumentative and unbiased information. To develop your informative speech topic, go to statistics reports, thematic books and journals and try to find numerous expert opinions about the single subject matter. The list of potential informative speech topics is as follows: the philosophy of Buddhism, feminism, the Big Bang theory etc.

            Nevertheless, in many cases you may not have an assigned informative speech topic or you may be asked to prepare a free informative speech topic. First thing you should do is…relax and take it easy! Then shoot up and start dancing! We are not kidding. This may be your once-in-a-lifetime chance to express your creative mindset and artistic skills. Now you can choose any informative speech topic you like. But still do not forget – the more you know about your topic, the better speech you will make. You must be ready to answer the audience’s questions which can be very provocative in a way. So, show them your best!

            Now you are a generator of topic ideas for informative college or high school speech. You already know where to look for information. And prior to doing a research, think about your own interests. If you like arts, try to relate your informative speech topic to arts. Look at the list of free art-related informative speech topics below and adjust your thinking accordingly:

            * Graffiti is a unique expression of youth sub-culture.
            * Why did Malevich’s “Black Square” become world-recognized?
            * Is it possible to combine classical music and rap?
            * Cyber punk as a new literature trend.
            * How to promote your fiction writing online etc, etc.

            And you have much broader horizons to use! Just switch on your imagination! Still not sure which free informative speech topic will be good enough for you? No problems, we will give you some more useful tips!

            **Tip 1.** Think about your recent classes and remember which classroom debates you have experienced (if any). The cause of the debate may be a good informative speech topic.

            **Tip 2.** Talk to your friends and peers and try to find out what interests them most of all.

            **Tip 3.** Take surf on diverse online forums and discussion boards. Each of them definitely has a thread highlighting youngsters’ interests and hobbies. Use them as your informative speech topic’s ideas.

            And our last tip summarizing all the above described things is – like your informative speech topic and make sure to research it as deeply as possible! And maybe some day you will become a prominent advisor on interesting informative speech topics’ selection!
            """
                ),
                json(
                    "listTitle" to "How to Choose a Good Research or Essay Topic",
                    "title" to "How to Choose a Good Research or Essay Topic",
                    "slug" to "essay-topic",
                    "content" to """
            Henry Miller once said, ‘Writing, like life itself, is a voyage of discovery’. In this life we are guided by our parents, teachers and mentors who constantly teach us how to properly plan our steps in order to make as few errors as possible. Life is driven by other men’s experience. AcademicPaperServed is designed specifically to smoothly guide you through unruly and impertinent ocean of college research topics, topics for high school debate, informative speech topics, essay topics etc. Over quite a long period of time we have been observing the situations when students could not reveal their actual writing potential due to wrongly chosen college and high school research, essay or debate topics. One of the key goals of EasyWriting.Org is to teach you how to choose a good high school debate, informative speech, and college essay or research topic - a topic that will work and satisfy your own, your audience and your instructor’s aspirations and expectations! We’d like to share with you the best practice of putting a pen to the paper successfully. Our suggestions are based on experience of people who reached the highest goal of effective writing and not on some common practice of some abstract personalities!

            > Writing is not a preplanned recitation of what you know; writing Is thinking.
            >
            > ${jshit.mdash}Donald Murray

            Why not perceive writing as an exciting opportunity to make meaning out of your experiences and ideas, to help you think more clearly and independently and to establish new understandings in order to make new connections? In other words, change your current attitude towards writing!

            > The two most engaging powers of an author are to make new things familiar and familiar things new.
            >
            > ${jshit.mdash}Dr. Samuel Johnson

            Be sure you understand your assignment well and do not hesitate overloading your instructor with as many questions as possible to get an in-depth and insightful understanding of what to write about! Feel the power of being able to inform others, while learning something new at the same time! Isn’t it exciting to deliver via obtaining? Millions of men worldwide dream of having such a power. Think of what you can lose if you don’t try to love writing.

            > It's good to rub and polish our brain against that of others.
            >
            > ${jshit.mdash}Montaigne

            When choosing a good topic for your informative speech, high school debate, college essay or research, brainstorm your topic ideas first! Take a sheet of paper and a pen, draw a line in the center of the paper, and write questions on the left and answers on the right. Ask yourself:

            * What you are interested in
            * Which topic has the greatest potential to inspire your creative thinking
            * What you feel strongly about
            * What you are more competent about
            * What you want to learn more about
            * Whether you have recently watched anything interesting and attention-grabbing on TV

            C’mon! Polish your brain! Open your eyes! What if a good argumentative essay topic is hiding in your fridge? Maybe there is a good informative speech topic hanging in your wardrobe?
            """
                )
                )
            ))

            for (item in blogItems) {
            writePage(json("name" to "blog-${item.slug}", "highlightedItem" to "blog", // For Customer site
                "comp" to jshit.diva(json(),
                    jshit.diva(json("className" to "container"),
                        jshit.pageHeader(item.title),
                        markdownPiece(item.content)
                    )
                )
            ))
        }

        writePage(json("name" to "blog", "highlightedItem" to "blog", // For Customer site
            "comp" to jshit.diva(json(),
                jshit.diva(json("className" to "container"),
                    jshit.pageHeader(t(json("en" to "Writing Blog", "ua" to "Писательский Блог"))),
                    hrefBullets(blogItems.map{x: dynamic -> (json("title" to x.listTitle, "href" to "blog-${x.slug}.html"))})
                    )
            )
        ))

        writeDynamicPages(legacyClient.customerDynamicPageNames(), ::writePage)
    }

    fun genericWritePage(arg: dynamic) {
        // {name, comp, css="", js="", highlightedItem, root, tabTitle, lang, clientKind, t}
        val name = arg.name; val comp = arg.comp; val highlightedItem = arg.highlightedItem; val t = arg.t
        val root = arg.root; val tabTitle = arg.tabTitle; val lang = arg.lang; val clientKind = arg.clientKind
        val css = if (arg.css) arg.css else ""
        val js = if (arg.js) arg.js else ""

        fs.writeFileSync("${root}/${name}.html", """
    <!DOCTYPE html>
    <html lang="en" style="position: relative; min-height: 100%;">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    ${jshit.ReactDOMServer.renderToStaticMarkup(jshit.React.createElement("title", json(), tabTitle))}

    <link href="bootstrap-3.3.6/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="font-awesome-4.6.3/css/font-awesome.min.css">
    <style>
        /* @ctx css */

        .navbar-default .navbar-nav>.open>a, .navbar-default .navbar-nav>.open>a:focus, .navbar-default .navbar-nav>.open>a {
        background: none;
    }

    @media (min-width: 768px) {
        .padding-left-to-center-720 {
        padding-left: 0px;
    }
    }

    @media (min-width: 992px) {
        .padding-left-to-center-720 {
        padding-left: 110px; /* (940-720)/2 */
    }
    }

    @media (min-width: 1200px) {
        .padding-left-to-center-720 {
        padding-left: 210px; /* (1140-720)/2 */
    }
    }

        .progressTicker {
            animation-name: progressTicker;
            animation-duration: 500ms;
            animation-iteration-count: infinite;

            -webkit-animation-name: progressTicker;
            -webkit-animation-duration: 500ms;
            -webkit-animation-iteration-count: infinite;
        }

    @keyframes progressTicker {
        0% {
            opacity: 1;
        }

        100% {
            opacity: 0;
        }
    }
    @-webkit-keyframes progressTicker {
        0% {
            opacity: 1;
        }

        100% {
            opacity: 0;
        }
    }

    @media (min-width: 768px) {
        /* TODO:vgrechka Testimonials style for 768px */
    }

    @media (min-width: 992px) {
        #testimonials-window {
        width: 970px;
    }
        .testimonials-item {
        width: 485px;
    }
    }

    @media (min-width: 1200px) {
        #testimonials-window {
        width: 1170px;
    }
        .testimonials-item {
        width: 585px;
    }
    }

    #testimonials-window {
        overflow: hidden;
        margin-right: -15px;
        margin-left: -15px;
        position: relative;
    }

    #testimonials-strip {
        width: 10000px;
        margin-left: -0px;
    }

        .testimonials-item {
        display: inline-block;
        vertical-align: top;
        padding-right: 15px;
        padding-left: 15px;
    }

    #testimonials-right {
        cursor: pointer;
        color: #cfd8dc;
    }

    #testimonials-right:hover {
        color: #546e7a;
    }


        .aniMinimize {
            animation-name: aniMinimize;
            animation-duration: 500ms;
            animation-iteration-count: 1;
            animation-fill-mode: forwards;
        }
    @keyframes aniMinimize {
        0% {
            opacity: 1;
            transform: scale(1);
        }

        100% {
            opacity: 0;
            transform: scale(0.25);
        }
    }

        .aniFadeIn {
            animation-name: aniFadeIn;
            animation-duration: 500ms;
            animation-iteration-count: 1;
            animation-fill-mode: forwards;
        }
    @keyframes aniFadeIn {
        0% {
            opacity: 0;
        }

        100% {
            opacity: 1;
        }
    }

        .aniFadeOut {
            animation-name: aniFadeOut;
            animation-duration: 500ms;
            animation-iteration-count: 1;
            animation-fill-mode: forwards;
        }
    @keyframes aniFadeOut {
        0% {
            opacity: 1;
        }

        100% {
            opacity: 0;
        }
    }

        .aniBlinkingFast {
            animation-name: aniBlinkingFast;
            animation-duration: 250ms;
            animation-iteration-count: infinite;
        }
    @keyframes aniBlinkingFast {
        0% {
            opacity: 0;
        }

        100% {
            opacity: 1;
        }
    }

        .hoverPointerUnderline {
            cursor: pointer;
        }

        .hoverPointerUnderline:hover {
        cursor: pointer;
        text-decoration: underline;
    }

        .btn-default:focus {border-color: #ccc; outline-color: transparent;}
        .btn-default:active {border-color: #ccc; outline-color: transparent;}
        .btn-default:focus:active {border-color: #8c8c8c; outline-color: transparent;}

    </style>

    <script>
        LANG = '${lang}'
    CLIENT_KIND = '${clientKind}'
    setFavicon('${if (clientKind == "customer") "favicon-customer.ico" else "favicon-writer.ico"}')

    function setFavicon(src) {
        var link = document.createElement('link')
        link.id = 'favicon'
        link.rel = 'shortcut icon'
        link.href = src
        document.head.appendChild(link)
    }
    </script>
    </head>
    <body style="padding-top: 50px; padding-bottom: 40px; overflow-y: scroll;">
    <div id="topNavbarContainer">
    ${jshit.ReactDOMServer.renderToStaticMarkup(legacyClient.renderTopNavbar(json("clientKind" to clientKind, "highlightedItem" to highlightedItem, "t" to t)))}
    </div>

    <div id="root" style="min-height: calc(100vh - 28px - 50px);">
    <div id="staticShit" style="display: none;">
    <!-- BEGIN CONTENT -->
    ${jshit.ReactDOMServer.renderToStaticMarkup(comp)}
    <!-- END CONTENT -->
    </div>

    <div id="ticker" style="display: none;">${jshit.ReactDOMServer.renderToStaticMarkup(wholePageTicker())}</div>

    <script>
        if (localStorage.getItem('token')) {
            document.getElementById('ticker').style.display = ''
        } else {
            document.getElementById('staticShit').style.display = ''
            window.staticShitIsRenderedStatically = true
        }
    </script>
    </div>

    <div id="footer" style="">
    <div style="background-color: #f8f8f8; border: 1px solid #e7e7e7; color: #333; font-family: Helvetica Neue, Helvetica, Arial, sans-serif; font-size: 12px; padding: 5px 15px; height: 28px;">
    ${
        if (clientKind == "customer") """
            © Copyright 2015-2016 AcademicPaperServed. All rights reserved
            """
        else if (clientKind == "writer") """
            © Copyright 2015-2016 Writer UA. All rights reserved
            """
        else wtf("clientKind")
        }
    </div>
    <div id="underFooter">
    </div>
    </div>

    <script src="jquery.min.js"></script>
    <!-- <script src="jquery-hack.js"></script> -->
    <script src="bootstrap-hack.js"></script>
    <!-- <script src="bootstrap-3.3.6/js/bootstrap.min.js"></script> -->

    <script src="kotlin/lib/kotlin.js"></script>
    <script src="kotlin/front-enhanced.js"></script>

    <!-- <script src="aps-scala-fastopt.js"></script> -->

    <script>
        var testimonials

    initStaticShit()


    function initStaticShit() {
        testimonials = Testimonials()
    }

    function disposeStaticShit() {
        testimonials.dispose()
    }

    function Testimonials() {
        var numSlides = 3,
        numFrames = 60,
        autoIgnitionPeriod = 15000,
        rightArrowAppearsDelay = 200,
        shiftFractions,
        slide,
        autoIgnitionTimeoutHandle,
        magic,
        disposed

        if (!$('#testimonials-window').length) {
            return {
                dispose() {}
            }
        }

        window.testimonialsIncarnationID = window.testimonialsIncarnationID || 0
        ++window.testimonialsIncarnationID

        calcTrajectory()

        $('.testimonials-item').slice(0, 2).clone().appendTo($('#testimonials-strip'))
        $('#testimonials-right').on('click', ignite)

        var mqLarge = window.matchMedia('(min-width: 1200px)')
        var mqMedium = window.matchMedia('(min-width: 992px)')
        mqLarge.addListener(onMediaChange)
        mqMedium.addListener(onMediaChange)

        onMediaChange()

        return {
            dispose() {
                mqLarge.removeListener(onMediaChange)
                mqMedium.removeListener(onMediaChange)
                disposed = true
            }
        }


        function calcTrajectory() {
            var plot = [
            'o                             ',
            '  o                           ',
            '       o                      ',
            '                   o          ',
            '               o              ',
            '                  o           ',
            '                o             ',
            '                 o            ',
            '------------------------------',
            '                 x            ']

            var numShiftFractions,  posFor100
            for (var i = 0; i < plot.length; ++i) {
            if (plot[i].charAt(0) === '-') {
                numShiftFractions = i
                posFor100 = plot[i + 1].indexOf('x')
            }
        }

            shiftFractions = []
            for (var i = 0; i < numShiftFractions; ++i) {
            var pos = plot[i].indexOf('o')
            shiftFractions.push(pos / posFor100)
        }
        }

        function onMediaChange() {
            console.log('Testimonials onMediaChange ' + testimonialsIncarnationID)
            clearTimeout(autoIgnitionTimeoutHandle)
            slide = 0
            $('#testimonials-strip').css('margin-left', '')
            $('.testimonials-item').css('margin-right', '').css('visibility', '')

            magic = undefined
            if (window.matchMedia('(min-width: 1200px)').matches) {
                console.log('Screen is large')
                magic = 970 + 200
            } else if (window.matchMedia('(min-width: 992px)').matches) {
                console.log('Screen is medium')
                magic = 970
            }

            if (!magic) {
                console.log('No testimonial sliding for you')
                $('#testimonials-right').hide()
                var items = $('.testimonials-item')
                $(items[items.length - 1]).hide()
                $(items[items.length - 2]).hide()
                return
            }

            var items = $('.testimonials-item')
            $(items[items.length - 1]).show()
            $(items[items.length - 2]).show()
            $('#testimonials-right').show()
            scheduleAutoIgnition()
        }

        function scheduleAutoIgnition() {
            autoIgnitionTimeoutHandle = setTimeout(ignite, autoIgnitionPeriod)
        }

        function ignite() {
            if (disposed) return
            console.log('Igniting testimonials ' + testimonialsIncarnationID)
            clearTimeout(autoIgnitionTimeoutHandle)
            $('#testimonials-right').hide()

            var elementToEnlargeRight = $($('.testimonials-item')[slide * 2 + 3])
            elementToEnlargeRight.css('margin-right', '300px')
            var elementToHideOnHitFloor = $($('.testimonials-item')[slide * 2 + 1])

            var framesDone = 0
            requestAnimationFrame(step)

            function step() {
                var maxShift = magic

                var shiftFractionIdxMiddle = (shiftFractions.length - 1) * framesDone / (numFrames - 1)
                var shiftFractionIdx1 = Math.floor(shiftFractionIdxMiddle)
                var shiftFractionIdx2 = Math.ceil(shiftFractionIdxMiddle)
                var shiftFractionFraction = shiftFractionIdxMiddle - shiftFractionIdx1
                var shiftFraction1 = shiftFractions[shiftFractionIdx1]
                var shiftFraction2 = shiftFractions[shiftFractionIdx2]
                var shiftFraction = shiftFraction1 + (shiftFraction2 - shiftFraction1) * shiftFractionFraction
                var shift = maxShift * shiftFraction
                var offset = slide * magic + shift

                $('#testimonials-strip').css('margin-left', -offset + 'px')
                if (shift >= maxShift) {
                    elementToHideOnHitFloor.css('visibility', 'hidden')
                }

                if (++framesDone === numFrames) {
                    if (++slide === numSlides) {
                        slide = 0
                    }

                    elementToEnlargeRight.css('margin-right', '')
                    elementToHideOnHitFloor.css('visibility', '')

                    setTimeout(function() {
                        if (disposed) return
                        $('#testimonials-right').show()
                        scheduleAutoIgnition()
                    }, rightArrowAppearsDelay)
                } else {
                    requestAnimationFrame(step)
                }
            }
        }
    }
    </script>

    <script src="bundle.js"></script>
    <script>igniteShit()</script>
    </body>
    </html>
    """)
    }

    fun renderTestimonials(clientKind: dynamic): dynamic {
        return jshit.diva(json(),
            jshit.pageHeader(t(json("en" to "What People Say", "ua" to "Что о нас говорят"))),
            jshit.diva(json("id" to "testimonials-window"),
                jshit.diva.apply(null, js("[]").concat(json("id" to "testimonials-strip"), jsArrayOf(
                    json("name" to json("en" to "Nicole", "ua" to "Nicole"), "img" to "nicole.jpg", "says" to json(
                        "en" to "Never expect such an urgent project could be accomplished overnight! I really appreciated the level of your writers and you treating the customers. I will recommend your services to my friends.",
                        "ua" to "Never expect such an urgent project could be accomplished overnight! I really appreciated the level of your writers and you treating the customers. I will recommend your services to my friends.")),
                    json("name" to json("en" to "Miranda", "ua" to "Miranda"), "img" to "miranda.jpg", "says" to json(
                        "en" to "Wow!!! The paper got A+, for the first time in my student life I was graded so high! Thanx!",
                        "ua" to "Wow!!! The paper got A+, for the first time in my student life I was graded so high! Thanx!")),
                    json("name" to json("en" to "Mike P.", "ua" to "Mike P."), "img" to "mike-p.jpg", "says" to json(
                        "en" to "I was impressed when you writer sent me the copies of sources in one hour upon my request, though the paper was written over a month ago and I did not ask to make that at once. Carry on!",
                        "ua" to "I was impressed when you writer sent me the copies of sources in one hour upon my request, though the paper was written over a month ago and I did not ask to make that at once. Carry on!")),
                    json("name" to json("en" to "Joseph B.", "ua" to "Joseph B."), "img" to "joseph-b.jpg", "says" to json(
                        "en" to "First I doubted I’d get anything of good quality, but I was up${jshit.nbsp}to the eyes in work and had no other choice. The paper${jshit.nbsp}proved to be authentic and came on time. Can I get${jshit.nbsp}the same writer for my next essay?",
                        "ua" to "First I doubted I’d get anything of good quality, but I was up${jshit.nbsp}to the eyes in work and had no other choice. The paper${jshit.nbsp}proved to be authentic and came on time. Can I get${jshit.nbsp}the same writer for my next essay?")),
                    json("name" to json("en" to "Mark C.", "ua" to "Mark C."), "img" to "mark-c.jpg", "says" to json(
                        "en" to "How come you are so smart in every subject, guys? I’ve always been a bright student, but I admit you write quicker and select the most up-to-date sources. I need to learn from you.",
                        "ua" to "How come you are so smart in every subject, guys? I’ve always been a bright student, but I admit you write quicker and select the most up-to-date sources. I need to learn from you.")),
                    json("name" to json("en" to "Linda R.", "ua" to "Linda R."), "img" to "linda-r.jpg", "says" to json(
                        "en" to "I would have never accomplished this research paper on my own! It was too challenging. You also explained some parts of the paper I did not understand. Excellent job!",
                        "ua" to "I would have never accomplished this research paper on my own! It was too challenging. You also explained some parts of the paper I did not understand. Excellent job!"))
                    ).map{item: dynamic ->
                    jshit.diva(json("className" to "testimonials-item"),
                        jshit.diva(json("className" to "media"),
                            jshit.diva(json("className" to "media-left"),
                                jshit.img(item.img, json("className" to "media-object"))),
                            jshit.diva(json("className" to "media-body"),
                                jshit.h4a(json("className" to "media-heading"), t(item.name)),
                                jshit.spana(json(), t(item.says)))))})),

                jshit.diva(json("style" to json("display" to "flex", "alignItems" to "center", "position" to "absolute", "width" to 20, "right" to 0, "top" to 0, "height" to "100%")),
                    jshit.glyph("chevron-right", json("id" to "testimonials-right", "className" to "fa-2x")))),

            jshit.rawHtml("")
            )
    }

    fun h3Smaller(it: dynamic): dynamic {
        return jshit.h3a(json(), jshit.spana(json("style" to json("fontSize" to "80%")), it))
    }

    fun bullets(items: dynamic): dynamic {
        return jshit.ula.apply(null, js("[]").concat(json("className" to "fa-ul", "style" to json("marginLeft" to 22)), items.map{item: dynamic ->
            jshit.lia(json("style" to json("marginBottom" to 10)), jshit.glyph("star", json("className" to "fa-li", "style" to json("color" to Color.BLUE_GRAY_600))), item)}))
    }

    fun locBullets(items: dynamic): dynamic {
        return bullets(items.map{x: dynamic -> t(x)})
    }

    fun hrefBullets(items: dynamic): dynamic {
        return bullets(items.map{x: dynamic -> jshit.aa(json("href" to x.href), x.title)})
    }

    fun tdedent(ss: dynamic): dynamic {
        return t(jshit.utils.omapo(ss, jshit.utils.dedent))
    }

    fun markdownPiece(_content: dynamic): dynamic {
        var content = _content
        if (js("typeof content") == "object") {
            content = t(content)
        }
        return jshit.markdown(jshit.utils.dedent(content))
    }

    fun horizBulletsRow(items: dynamic, opts: dynamic = js("({})")): dynamic {
        // {horizContentMargin=0}={}
        val horizContentMargin = if (opts.horizContentMargin) opts.horizContentMargin else 0

        val colSize = js("12 / items.length")
        return jshit.diva.apply(null, js("[]").concat(json("className" to "row", "style" to json("marginBottom" to 20)), items.map{x: dynamic ->
            jshit.diva(json("className" to "col-md-" + colSize),
                jshit.diva(json("style" to json("textAlign" to "center", "marginBottom" to 10)), jshit.glyph(x.glyph, json("className" to "fa-2x", "style" to json("color" to Color.BLUE_GRAY_600)))),
                jshit.diva(json("style" to json("textAlign" to "center", "margin" to "0 ${horizContentMargin}px")), t(x)))}))
    }

    fun crashForDebuggingSake_randomly() {
        if (jshit.random(1) == 0) return jshit.utils.clog("Not crashing for now")

        jshit.utils.clog("Receive some shit on stdout")
        global.process.stderr.write("More on stderr\n")
        jshit.utils.clog("Stdout shit continues on another line")
        global.process.stderr.write("And to stderr again\n")
        jshit.utils.clog("Can you see all this shit in DevUI?")
        global.process.exit(1)
    }

}






