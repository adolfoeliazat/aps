/*
 * APS
 * 
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

import fs = require('fs')
import sh = require('shelljs')
#import static 'into-u ./stuff ./client'

let t

sh.config.fatal = true
Error.stackTraceLimit = Infinity

makeWriterSite({lang: 'ua'})
// makeCustomerSite({lang: 'en'})
makeCustomerSite({lang: 'ua'})


function makeWriterSite({lang}) {
    const _t = makeT(lang)
    t = function(...args) {
        if (typeof args[0] === 'object' && args[0].$sourceLocation) {
            args.shift()
        }
        return _t(...args)
    }
    // imposeClientT(t)
    
    const root = `${__dirname}/../built/${lang}-writer`
    sh.rm('-rf', root)
    sh.mkdir('-p', root)
    
    const vendor = `${__dirname}/../vendor`
    sh.cp(`${vendor}/jquery-2.2.4/jquery.min.js`, root)
    sh.cp('-r', `${vendor}/bootstrap-master`, root)
    sh.cp('-r', `${vendor}/font-awesome-4.6.3`, root)
    sh.cp(`${__dirname}/../asset/*`, root)
    sh.cp(`${__dirname}/../lib/bundle.js`, root)
    
    const tabTitle = t({en: `Writer`, ua: `Writer UA`})
    
    writePage({name: 'index', // For Writer site
        comp: div(
            diva({className: 'container'},
                pageHeader(t({en: `Welcome to Writer`, ua: `Приветствуем на Писце`})),
                markdownPiece({en: `
                                   Hey there! Listening to music, but can’t follow your favorite song, because there’s a research paper you’ve got to get done by tomorrow?
                                   
                                   Sitting in front of your laptop, desperately typing the keywords in your search engine browser and wondering why AcademicPaperServed came up with an idea of giving written assignments to the needy students like yourself?
                                       
                                   Can’t be effective in the workplace, because there’s a persuasive essay you’ve got to turn in early next week?
                                       
                                   Now relax. You’ve just reached the right destination! At AcademicPaperServed writing papers is really very easy!
                                   
                                   Professional custom papers writing service, research paper, essay and term paper writing service from experienced writers at affordable price.                    
                               `,
                               ua: `
                                   Hey there! Listening to music, but can’t follow your favorite song, because there’s a research paper you’ve got to get done by tomorrow?
                                   
                                   Sitting in front of your laptop, desperately typing the keywords in your search engine browser and wondering why AcademicPaperServed came up with an idea of giving written assignments to the needy students like yourself?
                                       
                                   Can’t be effective in the workplace, because there’s a persuasive essay you’ve got to turn in early next week?
                                       
                                   Now relax. You’ve just reached the right destination! At AcademicPaperServed writing papers is really very easy!
                                   
                                   Professional custom papers writing service, research paper, essay and term paper writing service from experienced writers at affordable price.                    
                               `}),
                
                pageHeader(t({en: `Features`, ua: `Фичи`})),
                horizBulletsRow([
                    {glyph: 'pencil', en: `No plagiarism`, ua: `No plagiarism`},
                    {glyph: 'star', en: `Only premium quality`, ua: `Only premium quality`},
                    {glyph: 'list', en: `Free title page, outline, list${nbsp}of${nbsp}references`, ua: `Free title page, outline, list${nbsp}of${nbsp}references`},
                    ], {horizContentMargin: 40}),
                horizBulletsRow([
                    {glyph: 'gi-piggy-bank', en: `One-time and life-time discounts to returning customers`, ua: `One-time and life-time discounts to returning customers`},
                    {glyph: 'credit-card', en: `30-days money back guarantee`, ua: `30-days money back guarantee`},
                    {glyph: 'life-saver', en: `24/7 support`, ua: `24/7 support`},
                ], {horizContentMargin: 40}),
                
                pageHeader(t({en: `Who We Are`, ua: `Кто мы`})),
                markdownPiece({en: `
                                   AcademicPaperServed is an experienced custom writing service, consisting of expert writers to deliver written assignments, essays, research papers, term papers, theses, book reports, etc to English-speaking clients worldwide. Our team of professional writers focuses on the highest quality of all types of academic papers. Thesis writing is a challenge to many students. With our assistance it will not be a problem anymore!
                                   
                                   Our writers are versed in various fields of academic writing, so if you do not find a suitable category on this page, feel free to contact our Support Center to find out availability of writing help in your area of interest.
                                   
                                   We have access to most reliable and complete online libraries to make your research or essay unique.
                                   
                                   AcademicPaperServed team consists of expert academic writers providing you with free guidelines, helping with topic selection, proofreading, editing and formatting even if you want to get your essay done overnight! We guarantee premium quality writing with urgent projects.            
                               `,
                               ua: `
                                   AcademicPaperServed is an experienced custom writing service, consisting of expert writers to deliver written assignments, essays, research papers, term papers, theses, book reports, etc to English-speaking clients worldwide. Our team of professional writers focuses on the highest quality of all types of academic papers. Thesis writing is a challenge to many students. With our assistance it will not be a problem anymore!
                                   
                                   Our writers are versed in various fields of academic writing, so if you do not find a suitable category on this page, feel free to contact our Support Center to find out availability of writing help in your area of interest.
                                   
                                   We have access to most reliable and complete online libraries to make your research or essay unique.
                                   
                                   AcademicPaperServed team consists of expert academic writers providing you with free guidelines, helping with topic selection, proofreading, editing and formatting even if you want to get your essay done overnight! We guarantee premium quality writing with urgent projects.            
                               `}),
                               
                renderTestimonials('writer'),
                
                pageHeader(t({en: `What We Offer`, ua: `Мы предлагаем`})),
                horizBulletsRow([
                    {glyph: 'envira', en: `Custom essay, research, thesis writing`, ua: `Custom essay, research, thesis writing`},
                    {glyph: 'rocket', en: `Plagiarism-free original papers written from scratch`, ua: `Plagiarism-free original papers written from scratch`},
                    {glyph: 'bomb', en: `Proofreading and editing of written papers`, ua: `Proofreading and editing of written papers`},
                    {glyph: 'book', en: `Free guidelines on essay topic selection and writing process`, ua: `Free guidelines on essay topic selection and writing process`},
                ]),
                locBullets([
                    {en: `Custom essay, research paper, book report, term paper, precis, sketch, poetry analysis, data collection, thesis writing, SWOT analysis, lab reports, dissertations, reviews, speeches, presentations, case studies, courseworks, homeworks, assignments, creative writing, blog writing, capstone project, grant proposal, lab reports`, ua: `Custom essay, research paper, book report, term paper, precis, sketch, poetry analysis, data collection, thesis writing, SWOT analysis, lab reports, dissertations, reviews, speeches, presentations, case studies, courseworks, homeworks, assignments, creative writing, blog writing, capstone project, grant proposal, lab reports`},
                    {en: `Plagiarism-free original papers written from scratch`, ua: `Plagiarism-free original papers written from scratch`},
                    {en: `Proofreading and editing of written papers`, ua: `Proofreading and editing of written papers`},
                    {en: `Choosing sources for your paper, providing with annotated bibliography upon request`, ua: `Choosing sources for your paper, providing with annotated bibliography upon request`},
                    {en: `Free guidelines on successful essay topic selection and writing process`, ua: `Free guidelines on successful essay topic selection and writing process`},
                    {en: `Individual approach to every customer, no repetitions, free consulting on the paper content`, ua: `Individual approach to every customer, no repetitions, free consulting on the paper content`},
                    {en: `Free revisions till you are completely satisfied`, ua: `Free revisions till you are completely satisfied`},
                    {en: `Meeting your deadline`, ua: `Meeting your deadline`},
                    {en: `Security and confidentiality`, ua: `Security and confidentiality`},
                ])
            ),
        ),
    })
    
    writePage({name: 'why', highlightedItem: 'why', // For Writer site
        comp: div(
            diva({className: 'container'},
                pageHeader(t({en: `Why Writer?`, ua: `Почему Писец?`})),
                ...[{
                        title: {en: `We care about each customer’s academic success`, ua: `We care about each customer’s academic success`},
                        content: {
                            en: `According to the statistics, almost 90% of current U.S. undergraduate and graduate students work either full-time or part-time, while they study. Often, they have little time to complete all of their essays and term papers by themselves, as writing is really time consuming. Others simply don't think of writing as the most pleasurable activity. All students aim for graduation. AcademicPaperServed is here to help them reach this goal.`,
                            ua: `According to the statistics, almost 90% of current U.S. undergraduate and graduate students work either full-time or part-time, while they study. Often, they have little time to complete all of their essays and term papers by themselves, as writing is really time consuming. Others simply don't think of writing as the most pleasurable activity. All students aim for graduation. AcademicPaperServed is here to help them reach this goal.`
                        }
                    },
                    {
                        title: {en: `We make a very strong commitment to quality`, ua: `We make a very strong commitment to quality`},
                        content: {
                            en: `Quality is one of the major benefits of our custom writing service. Every AcademicPaperServed writer is experienced in the special field of studies. As a result, all of the customer's most sophisticated requirements are met by the professional freelance writer with a deep knowledge of the custom paper topic. Our custom essay writers possess Bachelor's / Master's degrees in the related fields of knowledge.`,
                            ua: `Quality is one of the major benefits of our custom writing service. Every AcademicPaperServed writer is experienced in the special field of studies. As a result, all of the customer's most sophisticated requirements are met by the professional freelance writer with a deep knowledge of the custom paper topic. Our custom essay writers possess Bachelor's / Master's degrees in the related fields of knowledge.`
                        }
                    },
                    {
                        title: {en: `We show an individual approach`, ua: `We show an individual approach`},
                        content: {
                            en: `
                                To Every Customer and Every Project we deal with.
                                
                                With us customers are sure to get their high school, college and university papers tailored exactly to their requirements and needs. No need to worry about quality! Your professor will be completely satisfied. We follow paper details specifically, provide with relevant sources from our library and/or use definite sources (books, journal and magazine articles, encyclopedia excerpts, peer reviewed studies, etc.) requested by the customer.
                                
                                Our team is aware of students’ concerns and issues. Any academic achievement becomes joyful and easy with our team. You are sure to get your writer’s comments on the paper content, advice on choosing a suitable topic, primary and secondary sources selection, ways of presenting it in pubic if needed, etc. You are a getting a complex academic help along with the written coherent text which you, surely, deserve!
                                
                                24/7 support will provide you with prompt unrivalled assistance in every matter of your concern and makes sure you’ll get excellent quality paper!
                                
                                AcademicPaperServed team helps English speaking customers from all over the world with anything an academic writing implies, from research ideas to styles and design of your paper. We are strictly following the rules of academic presentation combining it with modern and creative approaches. You are sure to get your project written in a plain, clear language with smooth transitions and coherent rendering.
                            `,
                            ua: `
                                To Every Customer and Every Project we deal with.
                                
                                With us customers are sure to get their high school, college and university papers tailored exactly to their requirements and needs. No need to worry about quality! Your professor will be completely satisfied. We follow paper details specifically, provide with relevant sources from our library and/or use definite sources (books, journal and magazine articles, encyclopedia excerpts, peer reviewed studies, etc.) requested by the customer.
                                
                                Our team is aware of students’ concerns and issues. Any academic achievement becomes joyful and easy with our team. You are sure to get your writer’s comments on the paper content, advice on choosing a suitable topic, primary and secondary sources selection, ways of presenting it in pubic if needed, etc. You are a getting a complex academic help along with the written coherent text which you, surely, deserve!
                                
                                24/7 support will provide you with prompt unrivalled assistance in every matter of your concern and makes sure you’ll get excellent quality paper!
                                
                                AcademicPaperServed team helps English speaking customers from all over the world with anything an academic writing implies, from research ideas to styles and design of your paper. We are strictly following the rules of academic presentation combining it with modern and creative approaches. You are sure to get your project written in a plain, clear language with smooth transitions and coherent rendering.
                            `
                        }
                    },
                    {
                        title: {en: `We hire only expert writers`, ua: `We hire only expert writers`},
                        content: {
                            en: `
                                We involve only experienced staff in the writing process! Each member of the AcademicPaperServed writing team has at least 2 years + experience in academic research field.
                                
                                You are sure to get your custom paper done by a native English speaker, who is versed in the area of your research, formats your paper according to the latest requirements of citation styles (MLA, APA, Harvard, Chicago, Turabian, Oxford and others) and delivers with no delays.
                                
                                US-based customer support will offer samples of various academic papers for your consideration.
                                
                                We’ve been dealing with ALL types of academic papers and we are always glad to offer you help with any essay (like scholarship essay, admission essay, application essay, entrance essay, personal statement) and research paper type at any academic level and in every possible discipline. If you want your paper to be well-organized and logically constructed, choose us!
                            `,
                            ua: `
                                We involve only experienced staff in the writing process! Each member of the AcademicPaperServed writing team has at least 2 years + experience in academic research field.
                                
                                You are sure to get your custom paper done by a native English speaker, who is versed in the area of your research, formats your paper according to the latest requirements of citation styles (MLA, APA, Harvard, Chicago, Turabian, Oxford and others) and delivers with no delays.
                                
                                US-based customer support will offer samples of various academic papers for your consideration.
                                
                                We’ve been dealing with ALL types of academic papers and we are always glad to offer you help with any essay (like scholarship essay, admission essay, application essay, entrance essay, personal statement) and research paper type at any academic level and in every possible discipline. If you want your paper to be well-organized and logically constructed, choose us!
                            `,
                        }
                    },
                    {
                        title: {en: `Plagiarism-free and full-of-creativity zone`, ua: `Plagiarism-free and full-of-creativity zone`},
                        content: {
                            en: `
                                Our writers get paid only when they use their own thoughts and ideas, elucidated through high-quality research and writing proficiency. For that reason, the following is not acceptable under any situation: copy-pasting from websites, copying from offline books, texts, and journals, basing one’s work on the ideas/structure of others or previous pieces of work. All papers are checked against millions of hard copy sources, billions of web pages, and countless pieces of work of other researchers. The papers are checked with the most up-to-date plagiarism detection software prior to being delivered to customer. There is totally no chance of delivering a plagiarized paper.
                                
                                At AcademicPaperServed all writers apply a creative approach to every essay. Each custom paper is written in compliance with the requests and desires of the customer.
                                
                                Be assured our friendly and knowledgeable staff will provide you with immediate, top-quality, and US-based customer support. All responses are personalized to the needs of the student.
                            `,
                            ua: `
                                Our writers get paid only when they use their own thoughts and ideas, elucidated through high-quality research and writing proficiency. For that reason, the following is not acceptable under any situation: copy-pasting from websites, copying from offline books, texts, and journals, basing one’s work on the ideas/structure of others or previous pieces of work. All papers are checked against millions of hard copy sources, billions of web pages, and countless pieces of work of other researchers. The papers are checked with the most up-to-date plagiarism detection software prior to being delivered to customer. There is totally no chance of delivering a plagiarized paper.
                                
                                At AcademicPaperServed all writers apply a creative approach to every essay. Each custom paper is written in compliance with the requests and desires of the customer.
                                
                                Be assured our friendly and knowledgeable staff will provide you with immediate, top-quality, and US-based customer support. All responses are personalized to the needs of the student.
                            `,
                        } 
                    },
                ].map(section => 
                    diva({},
                        diva({}, h3Smaller(t(section.title))),
                        diva({}, markdown(dedent(t(section.content))))))
            )
        )
    })
    
    writePage({name: 'prices', highlightedItem: 'prices', // For Writer site
        comp: div(
            diva({className: 'container'},
                pageHeader(t({en: `Our Prices`, ua: `Наши цены`})),
                markdownPiece({
                    en: `
                        Здесь такие идут расценки для писателей.
                    `,
                    ua: `
                        Здесь такие идут расценки для писателей.
                    `
                }),
                            
                pageHeader(t({en: `Pricing Policy`, ua: `Pricing Policy`})),
                markdownPiece({
                    en: `
                        Prices at AcademicPaperServed are set to meet the average point on the current custom writing market. This enables us to choose experts writers as freelance members in the team, who are able to meet the highest demands at faculties teaching in English.
                            
                        Our support is daily queried on how one can buy an essay or research paper on a certain topic. AcademicPaperServed does sell pre-written papers, each assignment is made individually and each research is written from scratch. Top-notch writing quality is only achieved through effective communication of the writer and the customer, be it an order on a blog article, high school debate project or a music review.
                            
                        Each quote we give to our customer is based primarily on the delivery date opted, type of an academic written assignment, number of pages and the amount of discount your membership is liable to.
                            
                        You may find out your quote in one minute! Simply press the Order Your Custom Essay Now tab at the top of the page marked in green. Our Customer Support employee will advise you on the price and terms of delivery. 
                    `,
                    ua: `
                        Prices at AcademicPaperServed are set to meet the average point on the current custom writing market. This enables us to choose experts writers as freelance members in the team, who are able to meet the highest demands at faculties teaching in English.
                            
                        Our support is daily queried on how one can buy an essay or research paper on a certain topic. AcademicPaperServed does sell pre-written papers, each assignment is made individually and each research is written from scratch. Top-notch writing quality is only achieved through effective communication of the writer and the customer, be it an order on a blog article, high school debate project or a music review.
                            
                        Each quote we give to our customer is based primarily on the delivery date opted, type of an academic written assignment, number of pages and the amount of discount your membership is liable to.
                            
                        You may find out your quote in one minute! Simply press the Order Your Custom Essay Now tab at the top of the page marked in green. Our Customer Support employee will advise you on the price and terms of delivery. 
                    `
                }),
                
                pageHeader(t({en: `Bonuses`, ua: `Bonuses`})),
                markdownPiece({
                    en: `Ordering a paper at AcademicPaperServed, you also get:`,
                    ua: `Ordering a paper at AcademicPaperServed, you also get:`,
                }),
                locBullets([
                    {en: `Free Title Page`, ua: `Free Title Page`},
                    {en: `Free Outline`, ua: `Free Outline`},
                    {en: `Free List of References`, ua: `Free List of References`},
                    {en: `Free Plagiarism Report (upon additional request)`, ua: `Free Plagiarism Report (upon additional request)`},
                ]),
                
                pageHeader(t({en: `Discount Policy`, ua: `Discount Policy`})),
                markdownPiece({
                    en: `Please note that each AcademicPaperServed customer is eligible for one-time and life-time discounts. One-time discount is granted to each new customer registered in our system and makes 5% off the first order total. Lifetime discount is provided to each returning customer depending on the total number of pages (on multiple papers) ordered since the moment of registration, and namely:`,
                    ua: `Please note that each AcademicPaperServed customer is eligible for one-time and life-time discounts. One-time discount is granted to each new customer registered in our system and makes 5% off the first order total. Lifetime discount is provided to each returning customer depending on the total number of pages (on multiple papers) ordered since the moment of registration, and namely:`,
                }),
                locBullets([
                    {en: `More than 50 pages${mdash}5%`, ua: `Более 50 страниц ${ndash} 5%`},
                    {en: `More than 100 pages${mdash}10%`, ua: `Более 100 страниц ${ndash} 10%`},
                    {en: `More than 150 pages${mdash}15%`, ua: `Более 150 страниц ${ndash} 15%`},
                    {en: `More than 200 pages${mdash}30%`, ua: `Более 200 страниц ${ndash} 30%`},
                ]),
                markdownPiece({
                    en: `We’re sure that at AcademicPaperServed we employ a fair discount policy. We respect each certain customer and hope to establish long-term cooperation with him/her. Since customers are our most valued asset, we put a lot of effort to retaining and satisfying them through our flexible lifetime discount policy.`,
                    ua: `We’re sure that at AcademicPaperServed we employ a fair discount policy. We respect each certain customer and hope to establish long-term cooperation with him/her. Since customers are our most valued asset, we put a lot of effort to retaining and satisfying them through our flexible lifetime discount policy.`,
                }),
            )
        )
    })
    
    writePage({name: 'faq', highlightedItem: 'faq', // For Writer site
        comp: div(
            diva({className: 'container'},
                pageHeader(t({en: `FAQ`, ua: `Частые вопросы`})),
                ...[{
                        title: {en: `How does AcademicPaperServed work?`, ua: `How does AcademicPaperServed work?`},
                        content: {
                            en: `As soon as you order a custom essay, term paper, research paper or book report your order is reviewed and then the most appropriate writer is assigned, who then takes responsibility to complete it. After your paper is ready, it is send to you via e-mail.`,
                            ua: `As soon as you order a custom essay, term paper, research paper or book report your order is reviewed and then the most appropriate writer is assigned, who then takes responsibility to complete it. After your paper is ready, it is send to you via e-mail.`
                        }
                    },
                    {
                        title: {en: `I have had unpleasant experiences with other custom essay companies. How does AcademicPaperServed differ from fraud services?`, ua: `I have had unpleasant experiences with other custom essay companies. How does AcademicPaperServed differ from fraud services?`},
                        content: {
                            en: `Sorry to say, but we often hear from our clients, that they have been deceived by unknown and disreputable essay writing companies that harm the reputation of legitimate writing companies. On the contrary, our service has done its best to earn the trust of our clients by offering quality custom papers at reasonable price.`,
                            ua: `Sorry to say, but we often hear from our clients, that they have been deceived by unknown and disreputable essay writing companies that harm the reputation of legitimate writing companies. On the contrary, our service has done its best to earn the trust of our clients by offering quality custom papers at reasonable price.`
                        }
                    },
                    {
                        title: {en: `How safe is your service? Is there any risk to place an order online?`, ua: `How safe is your service? Is there any risk to place an order online?`},
                        content: {
                            en: `It is totally safe. Hundreds of our customers make orders and buy custom writing services at AcademicPaperServed daily. All of your transactions are processed securely and encrypted by PayPal. It is impossible to make an unauthorized transaction using your credit card.`,
                            ua: `It is totally safe. Hundreds of our customers make orders and buy custom writing services at AcademicPaperServed daily. All of your transactions are processed securely and encrypted by PayPal. It is impossible to make an unauthorized transaction using your credit card.`
                        }
                    },
                    {
                        title: {en: `What are your policies concerning the paper format and citation?`, ua: `What are your policies concerning the paper format and citation?`},
                        content: {
                            en: `
                                Our writers use Microsoft Word .doc format by default (Times New Roman, 12, double-spaced, ~ 250-300 words/page). However, any format specified by the customer is available. The same concerns citation style.,
                                
                                If not chosen, the writer chooses a citation style most appropriate for the written assignment in process. The latest edition with newest formatting style and its rules is applied, unless indicated otherwise.
                            `,
                            ua: `
                                Our writers use Microsoft Word .doc format by default (Times New Roman, 12, double-spaced, ~ 250-300 words/page). However, any format specified by the customer is available. The same concerns citation style.
                                
                                If not chosen, the writer chooses a citation style most appropriate for the written assignment in process. The latest edition with newest formatting style and its rules is applied, unless indicated otherwise.
                            `
                        }
                    },
                    {
                        title: {en: `What if I don’t like my paper and it does not meet the requirements?`, ua: `What if I don’t like my paper and it does not meet the requirements?`},
                        content: {
                            en: `After you have ordered a paper, you can be confident that it will meet your expectations. Our writers will scrupulously adhere to your exact instructions to write a custom, first-rate academic paper. The team of writers works directly from customers’ instructions, and our clients get what they ask for. Still, there are moments when a customer can feel the writer has missed any of his/her requirements. In this case the customer should request a free revision. The writer will improve the paper and include all the instructions and will not stop working until the client is happy.`,
                            ua: `After you have ordered a paper, you can be confident that it will meet your expectations. Our writers will scrupulously adhere to your exact instructions to write a custom, first-rate academic paper. The team of writers works directly from customers’ instructions, and our clients get what they ask for. Still, there are moments when a customer can feel the writer has missed any of his/her requirements. In this case the customer should request a free revision. The writer will improve the paper and include all the instructions and will not stop working until the client is happy.`
                        }
                    },
                    {
                        title: {en: `Does your service provide refunds?`, ua: `Does your service provide refunds?`},
                        content: {
                            en: `Our aim is total customer satisfaction. According to our monthly data we have an acceptable 95% approval rating among our customers. Our writers work hard to do what is best for our customers. If the final version of the paper does not meet customer’s specified criteria, he/she can ask for a revision within thirty days. The writer will add any missing requirements and deliver the revision in 24 hours. If the customer provides us with evidence that the final revised version is still lacking some of the specified requirements, s/he can request a refund within three days after the completion of the order.`,
                            ua: `Our aim is total customer satisfaction. According to our monthly data we have an acceptable 95% approval rating among our customers. Our writers work hard to do what is best for our customers. If the final version of the paper does not meet customer’s specified criteria, he/she can ask for a revision within thirty days. The writer will add any missing requirements and deliver the revision in 24 hours. If the customer provides us with evidence that the final revised version is still lacking some of the specified requirements, s/he can request a refund within three days after the completion of the order.`
                        }
                    },
                    {
                        title: {en: `What if my paper is found to be plagiarized?`, ua: `What if my paper is found to be plagiarized?`},
                        content: {
                            en: `In fact, it is impossible. The writer gets paid only when he uses his own thoughts and ideas, elucidated through high-quality research/ and writing proficiency. For that reason, the following is not acceptable under any situation: copy-pasting from websites, copying from offline books, texts, and journals, basing one’s work on the ideas/structure of others or previous pieces of work. All papers are checked against millions of hard copy sources, billions of web pages, and countless pieces of work of other researchers. The papers are checked with most up to date anti-plagiarism software before sending any paper to customer. There is totally no chance of delivering a plagiarized paper.`,
                            ua: `In fact, it is impossible. The writer gets paid only when he uses his own thoughts and ideas, elucidated through high-quality research/ and writing proficiency. For that reason, the following is not acceptable under any situation: copy-pasting from websites, copying from offline books, texts, and journals, basing one’s work on the ideas/structure of others or previous pieces of work. All papers are checked against millions of hard copy sources, billions of web pages, and countless pieces of work of other researchers. The papers are checked with most up to date anti-plagiarism software before sending any paper to customer. There is totally no chance of delivering a plagiarized paper.`
                        }
                    },
                    {
                        title: {en: `Can I check my order status?`, ua: `Can I check my order status?`},
                        content: {
                            en: `In order to track the order status the customer can communicate with the writer or upload supplementary files or instructions.`,
                            ua: `In order to track the order status the customer can communicate with the writer or upload supplementary files or instructions.`
                        }
                    },
                    {
                        title: {en: `Can I contact the writer?`, ua: `Can I contact the writer?`},
                        content: {
                            en: `Certainly, there is a personal message board on each private account. A customer can easily post a message to the assigned writer, and he/she will reply to almost immediately. If the writer encounters any questions, he will contact the customer the in same way.`,
                            ua: `Certainly, there is a personal message board on each private account. A customer can easily post a message to the assigned writer, and he/she will reply to almost immediately. If the writer encounters any questions, he will contact the customer the in same way.`
                        }
                    },
                    {
                        title: {en: `Can I ever find my ordered paper being available to the public?`, ua: `Can I ever find my ordered paper being available to the public?`},
                        content: {
                            en: `Our service guarantees that the paper that has been written by our writers will never be available to the public. All papers are stored under an exclusively developed security system.`,
                            ua: `Our service guarantees that the paper that has been written by our writers will never be available to the public. All papers are stored under an exclusively developed security system.`
                        }
                    },
                    {
                        title: {en: `How can I make sure the writer has understood my assignment correctly?`, ua: `How can I make sure the writer has understood my assignment correctly?`},
                        content: {
                            en: `
                                When you start an ordering process, please, make sure you provide your writer with all necessary info, instructions, guidelines, additional materials for study, your class notes if any, and your personal preferences as to the writing style and paper formatting. It will surely facilitate the writing process and advance your chances in getting a higher grade.
                                    
                                We specifically created an easy-to-use interface for your communication with the writer and support center in the process of paper writing. 
                                    
                                AcademicPaperServed writers’ team is highly flexible and is ready to consider every order on a personal basis. We value our customers and set communication as a priority. We will appreciate your help in giving explicit order details once your order is made.
                            `,
                            ua: `
                                When you start an ordering process, please, make sure you provide your writer with all necessary info, instructions, guidelines, additional materials for study, your class notes if any, and your personal preferences as to the writing style and paper formatting. It will surely facilitate the writing process and advance your chances in getting a higher grade.
                                    
                                We specifically created an easy-to-use interface for your communication with the writer and support center in the process of paper writing. 
                                    
                                AcademicPaperServed writers’ team is highly flexible and is ready to consider every order on a personal basis. We value our customers and set communication as a priority. We will appreciate your help in giving explicit order details once your order is made.
                            `
                        }
                    },
                    {
                        title: {en: `What if my professor requires to hand in all copies of sources used?`, ua: `What if my professor requires to hand in all copies of sources used?`},
                        content: {
                            en: `The writers will provide you with the necessary sources used to write your paper if you request it in advance. Sometimes we use paid online libraries providing access per day/hour, in this case it is extremely difficult to come back there again and copy-paste material. Some libraries have copyright protection software, so cannot always copy the text for you. However, your timely request for the sources package will give us direction in a library choice. `,
                            ua: `The writers will provide you with the necessary sources used to write your paper if you request it in advance. Sometimes we use paid online libraries providing access per day/hour, in this case it is extremely difficult to come back there again and copy-paste material. Some libraries have copyright protection software, so cannot always copy the text for you. However, your timely request for the sources package will give us direction in a library choice. `
                        }
                    },
                ].map(section => 
                    diva({},
                        diva({}, markdownPiece('> ' + t(section.title))),
                        diva({style: {marginBottom: 20, marginTop: -5}}, markdown(dedent(t(section.content))))))
            )
        )
    })
    
    writeDynamicPages(writerDynamicPageNames(), writePage)
    
    function writePage(def) {
        genericWritePage(asn(def, {root, tabTitle, lang, clientKind: 'writer', t}))
    }
}

function writeDynamicPages(names, writePage) {
    names.forEach(name =>
        writePage({name: name, highlightedItem: '',
            comp: wholePageTicker()}))
}

function wholePageTicker() {
    return rawHtml(`
        <div class="container">
            <div style="display: flex; align-items: center; justify-content: center; position: absolute; left: 0px; top: 200px; width: 100%;">
                <span style="margin-left: 10">${t({en: 'Breathe slowly...', ua: 'Дышите глубоко...'})}</span>
                <div id="wholePageTicker" class="progressTicker" style="background-color: ${BLUE_GRAY_600}; width: 14px; height: 28px; margin-left: 10px; margin-top: -5px"></div>
            </div>
        </div>`)
}

function makeCustomerSite({lang}) {
    const _t = makeT(lang)
    t = function(...args) {
        if (typeof args[0] === 'object' && args[0].$sourceLocation) {
            args.shift()
        }
        return _t(...args)
    }
    // imposeClientT(t)
    
    const root = `${__dirname}/../built/${lang}-customer`
    sh.rm('-rf', root)
    sh.mkdir('-p', root)
    
    const vendor = `${__dirname}/../vendor`
    sh.cp(`${vendor}/jquery-2.2.4/jquery.min.js`, root)
    sh.cp('-r', `${vendor}/bootstrap-master`, root)
    sh.cp('-r', `${vendor}/font-awesome-4.6.3`, root)
    sh.cp(`${__dirname}/../asset/*`, root)
    sh.cp(`${__dirname}/../lib/bundle.js`, root)
    
    const tabTitle = t({en: `APS`, ua: `APS UA`})
    
    writePage({name: 'index', // For Customer site
        comp: div(
            diva({className: 'container'},
                pageHeader(t({en: `Welcome to AcademicPaperServed`, ua: `Welcome to AcademicPaperServed UA`})),
                markdownPiece({en: `
                                   Hey there! Listening to music, but can’t follow your favorite song, because there’s a research paper you’ve got to get done by tomorrow?
                                   
                                   Sitting in front of your laptop, desperately typing the keywords in your search engine browser and wondering why AcademicPaperServed came up with an idea of giving written assignments to the needy students like yourself?
                                       
                                   Can’t be effective in the workplace, because there’s a persuasive essay you’ve got to turn in early next week?
                                       
                                   Now relax. You’ve just reached the right destination! At AcademicPaperServed writing papers is really very easy!
                                   
                                   Professional custom papers writing service, research paper, essay and term paper writing service from experienced writers at affordable price.                    
                               `,
                               ua: `
                                   Hey there! Listening to music, but can’t follow your favorite song, because there’s a research paper you’ve got to get done by tomorrow?
                                   
                                   Sitting in front of your laptop, desperately typing the keywords in your search engine browser and wondering why AcademicPaperServed came up with an idea of giving written assignments to the needy students like yourself?
                                       
                                   Can’t be effective in the workplace, because there’s a persuasive essay you’ve got to turn in early next week?
                                       
                                   Now relax. You’ve just reached the right destination! At AcademicPaperServed writing papers is really very easy!
                                   
                                   Professional custom papers writing service, research paper, essay and term paper writing service from experienced writers at affordable price.                    
                               `}),
                
                pageHeader(t({en: `Features`, ua: `Features`})),
                horizBulletsRow([
                    {glyph: 'pencil', en: `No plagiarism`, ua: `No plagiarism`},
                    {glyph: 'star', en: `Only premium quality`, ua: `Only premium quality`},
                    {glyph: 'list', en: `Free title page, outline, list${nbsp}of${nbsp}references`, ua: `Free title page, outline, list${nbsp}of${nbsp}references`},
                    ], {horizContentMargin: 40}),
                horizBulletsRow([
                    {glyph: 'gi-piggy-bank', en: `One-time and life-time discounts to returning customers`, ua: `One-time and life-time discounts to returning customers`},
                    {glyph: 'credit-card', en: `30-days money back guarantee`, ua: `30-days money back guarantee`},
                    {glyph: 'life-saver', en: `24/7 support`, ua: `24/7 support`},
                ], {horizContentMargin: 40}),
                
                pageHeader(t({en: `Who We Are`, ua: `Who We Are`})),
                markdownPiece({en: `
                                   AcademicPaperServed is an experienced custom writing service, consisting of expert writers to deliver written assignments, essays, research papers, term papers, theses, book reports, etc to English-speaking clients worldwide. Our team of professional writers focuses on the highest quality of all types of academic papers. Thesis writing is a challenge to many students. With our assistance it will not be a problem anymore!
                                   
                                   Our writers are versed in various fields of academic writing, so if you do not find a suitable category on this page, feel free to contact our Support Center to find out availability of writing help in your area of interest.
                                   
                                   We have access to most reliable and complete online libraries to make your research or essay unique.
                                   
                                   AcademicPaperServed team consists of expert academic writers providing you with free guidelines, helping with topic selection, proofreading, editing and formatting even if you want to get your essay done overnight! We guarantee premium quality writing with urgent projects.            
                               `,
                               ua: `
                                   AcademicPaperServed is an experienced custom writing service, consisting of expert writers to deliver written assignments, essays, research papers, term papers, theses, book reports, etc to English-speaking clients worldwide. Our team of professional writers focuses on the highest quality of all types of academic papers. Thesis writing is a challenge to many students. With our assistance it will not be a problem anymore!
                                   
                                   Our writers are versed in various fields of academic writing, so if you do not find a suitable category on this page, feel free to contact our Support Center to find out availability of writing help in your area of interest.
                                   
                                   We have access to most reliable and complete online libraries to make your research or essay unique.
                                   
                                   AcademicPaperServed team consists of expert academic writers providing you with free guidelines, helping with topic selection, proofreading, editing and formatting even if you want to get your essay done overnight! We guarantee premium quality writing with urgent projects.            
                               `}),
                
                renderTestimonials('customer'),
                
                pageHeader(t({en: `What We Offer`, ua: `What We Offer`})),
                horizBulletsRow([
                    {glyph: 'envira', en: `Custom essay, research, thesis writing`, ua: `Custom essay, research, thesis writing`},
                    {glyph: 'rocket', en: `Plagiarism-free original papers written from scratch`, ua: `Plagiarism-free original papers written from scratch`},
                    {glyph: 'bomb', en: `Proofreading and editing of written papers`, ua: `Proofreading and editing of written papers`},
                    {glyph: 'book', en: `Free guidelines on essay topic selection and writing process`, ua: `Free guidelines on essay topic selection and writing process`},
                ]),
                locBullets([
                    {en: `Custom essay, research paper, book report, term paper, precis, sketch, poetry analysis, data collection, thesis writing, SWOT analysis, lab reports, dissertations, reviews, speeches, presentations, case studies, courseworks, homeworks, assignments, creative writing, blog writing, capstone project, grant proposal, lab reports`, ua: `Custom essay, research paper, book report, term paper, precis, sketch, poetry analysis, data collection, thesis writing, SWOT analysis, lab reports, dissertations, reviews, speeches, presentations, case studies, courseworks, homeworks, assignments, creative writing, blog writing, capstone project, grant proposal, lab reports`},
                    {en: `Plagiarism-free original papers written from scratch`, ua: `Plagiarism-free original papers written from scratch`},
                    {en: `Proofreading and editing of written papers`, ua: `Proofreading and editing of written papers`},
                    {en: `Choosing sources for your paper, providing with annotated bibliography upon request`, ua: `Choosing sources for your paper, providing with annotated bibliography upon request`},
                    {en: `Free guidelines on successful essay topic selection and writing process`, ua: `Free guidelines on successful essay topic selection and writing process`},
                    {en: `Individual approach to every customer, no repetitions, free consulting on the paper content`, ua: `Individual approach to every customer, no repetitions, free consulting on the paper content`},
                    {en: `Free revisions till you are completely satisfied`, ua: `Free revisions till you are completely satisfied`},
                    {en: `Meeting your deadline`, ua: `Meeting your deadline`},
                    {en: `Security and confidentiality`, ua: `Security and confidentiality`},
                ])
            ),
        ),
    })
    
    writePage({name: 'why', highlightedItem: 'why', // For Customer site
        comp: div(
            diva({className: 'container'},
                pageHeader(t({en: `Why AcademicPaperServed?`, ua: `Why AcademicPaperServed UA?`})),
                ...[{
                        title: {en: `We care about each customer’s academic success`, ua: `We care about each customer’s academic success`},
                        content: {
                            en: `According to the statistics, almost 90% of current U.S. undergraduate and graduate students work either full-time or part-time, while they study. Often, they have little time to complete all of their essays and term papers by themselves, as writing is really time consuming. Others simply don't think of writing as the most pleasurable activity. All students aim for graduation. AcademicPaperServed is here to help them reach this goal.`,
                            ua: `According to the statistics, almost 90% of current U.S. undergraduate and graduate students work either full-time or part-time, while they study. Often, they have little time to complete all of their essays and term papers by themselves, as writing is really time consuming. Others simply don't think of writing as the most pleasurable activity. All students aim for graduation. AcademicPaperServed is here to help them reach this goal.`
                        }
                    },
                    {
                        title: {en: `We make a very strong commitment to quality`, ua: `We make a very strong commitment to quality`},
                        content: {
                            en: `Quality is one of the major benefits of our custom writing service. Every AcademicPaperServed writer is experienced in the special field of studies. As a result, all of the customer's most sophisticated requirements are met by the professional freelance writer with a deep knowledge of the custom paper topic. Our custom essay writers possess Bachelor's / Master's degrees in the related fields of knowledge.`,
                            ua: `Quality is one of the major benefits of our custom writing service. Every AcademicPaperServed writer is experienced in the special field of studies. As a result, all of the customer's most sophisticated requirements are met by the professional freelance writer with a deep knowledge of the custom paper topic. Our custom essay writers possess Bachelor's / Master's degrees in the related fields of knowledge.`
                        }
                    },
                    {
                        title: {en: `We show an individual approach`, ua: `We show an individual approach`},
                        content: {
                            en: `
                                To Every Customer and Every Project we deal with.
                                
                                With us customers are sure to get their high school, college and university papers tailored exactly to their requirements and needs. No need to worry about quality! Your professor will be completely satisfied. We follow paper details specifically, provide with relevant sources from our library and/or use definite sources (books, journal and magazine articles, encyclopedia excerpts, peer reviewed studies, etc.) requested by the customer.
                                
                                Our team is aware of students’ concerns and issues. Any academic achievement becomes joyful and easy with our team. You are sure to get your writer’s comments on the paper content, advice on choosing a suitable topic, primary and secondary sources selection, ways of presenting it in pubic if needed, etc. You are a getting a complex academic help along with the written coherent text which you, surely, deserve!
                                
                                24/7 support will provide you with prompt unrivalled assistance in every matter of your concern and makes sure you’ll get excellent quality paper!
                                
                                AcademicPaperServed team helps English speaking customers from all over the world with anything an academic writing implies, from research ideas to styles and design of your paper. We are strictly following the rules of academic presentation combining it with modern and creative approaches. You are sure to get your project written in a plain, clear language with smooth transitions and coherent rendering.
                            `,
                            ua: `
                                To Every Customer and Every Project we deal with.
                                
                                With us customers are sure to get their high school, college and university papers tailored exactly to their requirements and needs. No need to worry about quality! Your professor will be completely satisfied. We follow paper details specifically, provide with relevant sources from our library and/or use definite sources (books, journal and magazine articles, encyclopedia excerpts, peer reviewed studies, etc.) requested by the customer.
                                
                                Our team is aware of students’ concerns and issues. Any academic achievement becomes joyful and easy with our team. You are sure to get your writer’s comments on the paper content, advice on choosing a suitable topic, primary and secondary sources selection, ways of presenting it in pubic if needed, etc. You are a getting a complex academic help along with the written coherent text which you, surely, deserve!
                                
                                24/7 support will provide you with prompt unrivalled assistance in every matter of your concern and makes sure you’ll get excellent quality paper!
                                
                                AcademicPaperServed team helps English speaking customers from all over the world with anything an academic writing implies, from research ideas to styles and design of your paper. We are strictly following the rules of academic presentation combining it with modern and creative approaches. You are sure to get your project written in a plain, clear language with smooth transitions and coherent rendering.
                            `
                        }
                    },
                    {
                        title: {en: `We hire only expert writers`, ua: `We hire only expert writers`},
                        content: {
                            en: `
                                We involve only experienced staff in the writing process! Each member of the AcademicPaperServed writing team has at least 2 years + experience in academic research field.
                                
                                You are sure to get your custom paper done by a native English speaker, who is versed in the area of your research, formats your paper according to the latest requirements of citation styles (MLA, APA, Harvard, Chicago, Turabian, Oxford and others) and delivers with no delays.
                                
                                US-based customer support will offer samples of various academic papers for your consideration.
                                
                                We’ve been dealing with ALL types of academic papers and we are always glad to offer you help with any essay (like scholarship essay, admission essay, application essay, entrance essay, personal statement) and research paper type at any academic level and in every possible discipline. If you want your paper to be well-organized and logically constructed, choose us!
                            `,
                            ua: `
                                We involve only experienced staff in the writing process! Each member of the AcademicPaperServed writing team has at least 2 years + experience in academic research field.
                                
                                You are sure to get your custom paper done by a native English speaker, who is versed in the area of your research, formats your paper according to the latest requirements of citation styles (MLA, APA, Harvard, Chicago, Turabian, Oxford and others) and delivers with no delays.
                                
                                US-based customer support will offer samples of various academic papers for your consideration.
                                
                                We’ve been dealing with ALL types of academic papers and we are always glad to offer you help with any essay (like scholarship essay, admission essay, application essay, entrance essay, personal statement) and research paper type at any academic level and in every possible discipline. If you want your paper to be well-organized and logically constructed, choose us!
                            `,
                        }
                    },
                    {
                        title: {en: `Plagiarism-free and full-of-creativity zone`, ua: `Plagiarism-free and full-of-creativity zone`},
                        content: {
                            en: `
                                Our writers get paid only when they use their own thoughts and ideas, elucidated through high-quality research and writing proficiency. For that reason, the following is not acceptable under any situation: copy-pasting from websites, copying from offline books, texts, and journals, basing one’s work on the ideas/structure of others or previous pieces of work. All papers are checked against millions of hard copy sources, billions of web pages, and countless pieces of work of other researchers. The papers are checked with the most up-to-date plagiarism detection software prior to being delivered to customer. There is totally no chance of delivering a plagiarized paper.
                                
                                At AcademicPaperServed all writers apply a creative approach to every essay. Each custom paper is written in compliance with the requests and desires of the customer.
                                
                                Be assured our friendly and knowledgeable staff will provide you with immediate, top-quality, and US-based customer support. All responses are personalized to the needs of the student.
                            `,
                            ua: `
                                Our writers get paid only when they use their own thoughts and ideas, elucidated through high-quality research and writing proficiency. For that reason, the following is not acceptable under any situation: copy-pasting from websites, copying from offline books, texts, and journals, basing one’s work on the ideas/structure of others or previous pieces of work. All papers are checked against millions of hard copy sources, billions of web pages, and countless pieces of work of other researchers. The papers are checked with the most up-to-date plagiarism detection software prior to being delivered to customer. There is totally no chance of delivering a plagiarized paper.
                                
                                At AcademicPaperServed all writers apply a creative approach to every essay. Each custom paper is written in compliance with the requests and desires of the customer.
                                
                                Be assured our friendly and knowledgeable staff will provide you with immediate, top-quality, and US-based customer support. All responses are personalized to the needs of the student.
                            `,
                        } 
                    },
                ].map(section => 
                    diva({},
                        diva({}, h3Smaller(t(section.title))),
                        diva({}, markdown(dedent(t(section.content))))))
            )
        )
    })
    
    const priceTableRows = []
    for (const dopt of deliveryOptions()) {
        let firstRowForDopt = true
        for (const top of typesOfPaper(lang)) {
            const curr = lang
            priceTableRows.push([
                firstRowForDopt ? deliveryOptionTitle(dopt, lang) : '',
                typeOfPaperTitle(top, lang),
                moneyTitleWithCurrency(priceForDeliveryOptionAndTypeOfPaper(dopt, top), curr, lang)])
            firstRowForDopt = false
        }
    }
    
    writePage({name: 'prices', highlightedItem: 'prices', // For Customer site
        comp: div(
            diva({className: 'container'},
                pageHeader(t({en: `Our Prices`, ua: `Наши цены`})),
                el('table', {className: 'table table-hover table-condensed'},
                    el('thead', {},
                        el('tr', {},
                            el('th', {}, t({en: 'Delivery Option', ua: 'Срочность'})),
                            el('th', {}, t({en: 'Type of Paper', ua: 'Тип работы'})),
                            el('th', {}, t({en: 'Price', ua: 'Цена'})),
                        )),
                    el('tbody', {},
                        ...priceTableRows.map(row => el('tr', {},
                            el('td', {}, row[0]),
                            el('td', {}, row[1]),
                            el('td', {}, row[2]),
                            )))),
                            
                pageHeader(t({en: `Pricing Policy`, ua: `Pricing Policy`})),
                markdownPiece({
                    en: `
                        Prices at AcademicPaperServed are set to meet the average point on the current custom writing market. This enables us to choose experts writers as freelance members in the team, who are able to meet the highest demands at faculties teaching in English.
                            
                        Our support is daily queried on how one can buy an essay or research paper on a certain topic. AcademicPaperServed does sell pre-written papers, each assignment is made individually and each research is written from scratch. Top-notch writing quality is only achieved through effective communication of the writer and the customer, be it an order on a blog article, high school debate project or a music review.
                            
                        Each quote we give to our customer is based primarily on the delivery date opted, type of an academic written assignment, number of pages and the amount of discount your membership is liable to.
                            
                        You may find out your quote in one minute! Simply press the Order Your Custom Essay Now tab at the top of the page marked in green. Our Customer Support employee will advise you on the price and terms of delivery. 
                    `,
                    ua: `
                        Prices at AcademicPaperServed are set to meet the average point on the current custom writing market. This enables us to choose experts writers as freelance members in the team, who are able to meet the highest demands at faculties teaching in English.
                            
                        Our support is daily queried on how one can buy an essay or research paper on a certain topic. AcademicPaperServed does sell pre-written papers, each assignment is made individually and each research is written from scratch. Top-notch writing quality is only achieved through effective communication of the writer and the customer, be it an order on a blog article, high school debate project or a music review.
                            
                        Each quote we give to our customer is based primarily on the delivery date opted, type of an academic written assignment, number of pages and the amount of discount your membership is liable to.
                            
                        You may find out your quote in one minute! Simply press the Order Your Custom Essay Now tab at the top of the page marked in green. Our Customer Support employee will advise you on the price and terms of delivery. 
                    `
                }),
                
                pageHeader(t({en: `Bonuses`, ua: `Bonuses`})),
                markdownPiece({
                    en: `Ordering a paper at AcademicPaperServed, you also get:`,
                    ua: `Ordering a paper at AcademicPaperServed, you also get:`,
                }),
                locBullets([
                    {en: `Free Title Page`, ua: `Free Title Page`},
                    {en: `Free Outline`, ua: `Free Outline`},
                    {en: `Free List of References`, ua: `Free List of References`},
                    {en: `Free Plagiarism Report (upon additional request)`, ua: `Free Plagiarism Report (upon additional request)`},
                ]),
                
                pageHeader(t({en: `Discount Policy`, ua: `Discount Policy`})),
                markdownPiece({
                    en: `Please note that each AcademicPaperServed customer is eligible for one-time and life-time discounts. One-time discount is granted to each new customer registered in our system and makes 5% off the first order total. Lifetime discount is provided to each returning customer depending on the total number of pages (on multiple papers) ordered since the moment of registration, and namely:`,
                    ua: `Please note that each AcademicPaperServed customer is eligible for one-time and life-time discounts. One-time discount is granted to each new customer registered in our system and makes 5% off the first order total. Lifetime discount is provided to each returning customer depending on the total number of pages (on multiple papers) ordered since the moment of registration, and namely:`,
                }),
                locBullets([
                    {en: `More than 50 pages${mdash}5%`, ua: `Более 50 страниц ${ndash} 5%`},
                    {en: `More than 100 pages${mdash}10%`, ua: `Более 100 страниц ${ndash} 10%`},
                    {en: `More than 150 pages${mdash}15%`, ua: `Более 150 страниц ${ndash} 15%`},
                    {en: `More than 200 pages${mdash}30%`, ua: `Более 200 страниц ${ndash} 30%`},
                ]),
                markdownPiece({
                    en: `We’re sure that at AcademicPaperServed we employ a fair discount policy. We respect each certain customer and hope to establish long-term cooperation with him/her. Since customers are our most valued asset, we put a lot of effort to retaining and satisfying them through our flexible lifetime discount policy.`,
                    ua: `We’re sure that at AcademicPaperServed we employ a fair discount policy. We respect each certain customer and hope to establish long-term cooperation with him/her. Since customers are our most valued asset, we put a lot of effort to retaining and satisfying them through our flexible lifetime discount policy.`,
                }),
            )
        )
    })
    
    const sampleItems = t({
        en: [
            {title: 'Sample APA paper', href: 'apa-sample.doc'},
            {title: 'Sample MLA research paper', href: 'mla-sample.doc'},
            {title: 'Sample Harvard-style paper', href: 'harvard-sample.doc'},
            {title: 'Sample Chicago paper', href: 'chicago-sample.doc'},
            {title: 'Sample Turabian paper', href: 'turabian-sample.doc'},
        ],
        ua: [
            {title: 'Пример реферата', href: 'ua_essay-sample.doc'},
            {title: 'Пример курсовой работы', href: 'ua_course-sample.doc'},
            {title: 'Пример дипломной работы', href: 'ua_graduate-sample.doc'},
        ],
    })
    
    writePage({name: 'samples', highlightedItem: 'samples', // For Customer site
        comp: div(
            diva({className: 'container'},
                pageHeader(t({en: `Sample Papers`, ua: `Примеры работ`})),
                hrefBullets(sampleItems),
            )
        )
    })
            
    writePage({name: 'faq', highlightedItem: 'faq', // For Customer site
        comp: div(
            diva({className: 'container'},
                pageHeader(t({en: `FAQ`, ua: `FAQ`})),
                ...[{
                        title: {en: `How does AcademicPaperServed work?`, ua: `How does AcademicPaperServed work?`},
                        content: {
                            en: `As soon as you order a custom essay, term paper, research paper or book report your order is reviewed and then the most appropriate writer is assigned, who then takes responsibility to complete it. After your paper is ready, it is send to you via e-mail.`,
                            ua: `As soon as you order a custom essay, term paper, research paper or book report your order is reviewed and then the most appropriate writer is assigned, who then takes responsibility to complete it. After your paper is ready, it is send to you via e-mail.`
                        }
                    },
                    {
                        title: {en: `I have had unpleasant experiences with other custom essay companies. How does AcademicPaperServed differ from fraud services?`, ua: `I have had unpleasant experiences with other custom essay companies. How does AcademicPaperServed differ from fraud services?`},
                        content: {
                            en: `Sorry to say, but we often hear from our clients, that they have been deceived by unknown and disreputable essay writing companies that harm the reputation of legitimate writing companies. On the contrary, our service has done its best to earn the trust of our clients by offering quality custom papers at reasonable price.`,
                            ua: `Sorry to say, but we often hear from our clients, that they have been deceived by unknown and disreputable essay writing companies that harm the reputation of legitimate writing companies. On the contrary, our service has done its best to earn the trust of our clients by offering quality custom papers at reasonable price.`
                        }
                    },
                    {
                        title: {en: `How safe is your service? Is there any risk to place an order online?`, ua: `How safe is your service? Is there any risk to place an order online?`},
                        content: {
                            en: `It is totally safe. Hundreds of our customers make orders and buy custom writing services at AcademicPaperServed daily. All of your transactions are processed securely and encrypted by PayPal. It is impossible to make an unauthorized transaction using your credit card.`,
                            ua: `It is totally safe. Hundreds of our customers make orders and buy custom writing services at AcademicPaperServed daily. All of your transactions are processed securely and encrypted by PayPal. It is impossible to make an unauthorized transaction using your credit card.`
                        }
                    },
                    {
                        title: {en: `What are your policies concerning the paper format and citation?`, ua: `What are your policies concerning the paper format and citation?`},
                        content: {
                            en: `
                                Our writers use Microsoft Word .doc format by default (Times New Roman, 12, double-spaced, ~ 250-300 words/page). However, any format specified by the customer is available. The same concerns citation style.,
                                
                                If not chosen, the writer chooses a citation style most appropriate for the written assignment in process. The latest edition with newest formatting style and its rules is applied, unless indicated otherwise.
                            `,
                            ua: `
                                Our writers use Microsoft Word .doc format by default (Times New Roman, 12, double-spaced, ~ 250-300 words/page). However, any format specified by the customer is available. The same concerns citation style.
                                
                                If not chosen, the writer chooses a citation style most appropriate for the written assignment in process. The latest edition with newest formatting style and its rules is applied, unless indicated otherwise.
                            `
                        }
                    },
                    {
                        title: {en: `What if I don’t like my paper and it does not meet the requirements?`, ua: `What if I don’t like my paper and it does not meet the requirements?`},
                        content: {
                            en: `After you have ordered a paper, you can be confident that it will meet your expectations. Our writers will scrupulously adhere to your exact instructions to write a custom, first-rate academic paper. The team of writers works directly from customers’ instructions, and our clients get what they ask for. Still, there are moments when a customer can feel the writer has missed any of his/her requirements. In this case the customer should request a free revision. The writer will improve the paper and include all the instructions and will not stop working until the client is happy.`,
                            ua: `After you have ordered a paper, you can be confident that it will meet your expectations. Our writers will scrupulously adhere to your exact instructions to write a custom, first-rate academic paper. The team of writers works directly from customers’ instructions, and our clients get what they ask for. Still, there are moments when a customer can feel the writer has missed any of his/her requirements. In this case the customer should request a free revision. The writer will improve the paper and include all the instructions and will not stop working until the client is happy.`
                        }
                    },
                    {
                        title: {en: `Does your service provide refunds?`, ua: `Does your service provide refunds?`},
                        content: {
                            en: `Our aim is total customer satisfaction. According to our monthly data we have an acceptable 95% approval rating among our customers. Our writers work hard to do what is best for our customers. If the final version of the paper does not meet customer’s specified criteria, he/she can ask for a revision within thirty days. The writer will add any missing requirements and deliver the revision in 24 hours. If the customer provides us with evidence that the final revised version is still lacking some of the specified requirements, s/he can request a refund within three days after the completion of the order.`,
                            ua: `Our aim is total customer satisfaction. According to our monthly data we have an acceptable 95% approval rating among our customers. Our writers work hard to do what is best for our customers. If the final version of the paper does not meet customer’s specified criteria, he/she can ask for a revision within thirty days. The writer will add any missing requirements and deliver the revision in 24 hours. If the customer provides us with evidence that the final revised version is still lacking some of the specified requirements, s/he can request a refund within three days after the completion of the order.`
                        }
                    },
                    {
                        title: {en: `What if my paper is found to be plagiarized?`, ua: `What if my paper is found to be plagiarized?`},
                        content: {
                            en: `In fact, it is impossible. The writer gets paid only when he uses his own thoughts and ideas, elucidated through high-quality research/ and writing proficiency. For that reason, the following is not acceptable under any situation: copy-pasting from websites, copying from offline books, texts, and journals, basing one’s work on the ideas/structure of others or previous pieces of work. All papers are checked against millions of hard copy sources, billions of web pages, and countless pieces of work of other researchers. The papers are checked with most up to date anti-plagiarism software before sending any paper to customer. There is totally no chance of delivering a plagiarized paper.`,
                            ua: `In fact, it is impossible. The writer gets paid only when he uses his own thoughts and ideas, elucidated through high-quality research/ and writing proficiency. For that reason, the following is not acceptable under any situation: copy-pasting from websites, copying from offline books, texts, and journals, basing one’s work on the ideas/structure of others or previous pieces of work. All papers are checked against millions of hard copy sources, billions of web pages, and countless pieces of work of other researchers. The papers are checked with most up to date anti-plagiarism software before sending any paper to customer. There is totally no chance of delivering a plagiarized paper.`
                        }
                    },
                    {
                        title: {en: `Can I check my order status?`, ua: `Can I check my order status?`},
                        content: {
                            en: `In order to track the order status the customer can communicate with the writer or upload supplementary files or instructions.`,
                            ua: `In order to track the order status the customer can communicate with the writer or upload supplementary files or instructions.`
                        }
                    },
                    {
                        title: {en: `Can I contact the writer?`, ua: `Can I contact the writer?`},
                        content: {
                            en: `Certainly, there is a personal message board on each private account. A customer can easily post a message to the assigned writer, and he/she will reply to almost immediately. If the writer encounters any questions, he will contact the customer the in same way.`,
                            ua: `Certainly, there is a personal message board on each private account. A customer can easily post a message to the assigned writer, and he/she will reply to almost immediately. If the writer encounters any questions, he will contact the customer the in same way.`
                        }
                    },
                    {
                        title: {en: `Can I ever find my ordered paper being available to the public?`, ua: `Can I ever find my ordered paper being available to the public?`},
                        content: {
                            en: `Our service guarantees that the paper that has been written by our writers will never be available to the public. All papers are stored under an exclusively developed security system.`,
                            ua: `Our service guarantees that the paper that has been written by our writers will never be available to the public. All papers are stored under an exclusively developed security system.`
                        }
                    },
                    {
                        title: {en: `How can I make sure the writer has understood my assignment correctly?`, ua: `How can I make sure the writer has understood my assignment correctly?`},
                        content: {
                            en: `
                                When you start an ordering process, please, make sure you provide your writer with all necessary info, instructions, guidelines, additional materials for study, your class notes if any, and your personal preferences as to the writing style and paper formatting. It will surely facilitate the writing process and advance your chances in getting a higher grade.
                                    
                                We specifically created an easy-to-use interface for your communication with the writer and support center in the process of paper writing. 
                                    
                                AcademicPaperServed writers’ team is highly flexible and is ready to consider every order on a personal basis. We value our customers and set communication as a priority. We will appreciate your help in giving explicit order details once your order is made.
                            `,
                            ua: `
                                When you start an ordering process, please, make sure you provide your writer with all necessary info, instructions, guidelines, additional materials for study, your class notes if any, and your personal preferences as to the writing style and paper formatting. It will surely facilitate the writing process and advance your chances in getting a higher grade.
                                    
                                We specifically created an easy-to-use interface for your communication with the writer and support center in the process of paper writing. 
                                    
                                AcademicPaperServed writers’ team is highly flexible and is ready to consider every order on a personal basis. We value our customers and set communication as a priority. We will appreciate your help in giving explicit order details once your order is made.
                            `
                        }
                    },
                    {
                        title: {en: `What if my professor requires to hand in all copies of sources used?`, ua: `What if my professor requires to hand in all copies of sources used?`},
                        content: {
                            en: `The writers will provide you with the necessary sources used to write your paper if you request it in advance. Sometimes we use paid online libraries providing access per day/hour, in this case it is extremely difficult to come back there again and copy-paste material. Some libraries have copyright protection software, so cannot always copy the text for you. However, your timely request for the sources package will give us direction in a library choice. `,
                            ua: `The writers will provide you with the necessary sources used to write your paper if you request it in advance. Sometimes we use paid online libraries providing access per day/hour, in this case it is extremely difficult to come back there again and copy-paste material. Some libraries have copyright protection software, so cannot always copy the text for you. However, your timely request for the sources package will give us direction in a library choice. `
                        }
                    },
                ].map(section => 
                    diva({},
                        diva({}, markdownPiece('> ' + t(section.title))),
                        diva({style: {marginBottom: 20, marginTop: -5}}, markdown(dedent(t(section.content))))))
            )
        )
    })
    
    writePage({name: 'contact', highlightedItem: 'contact', // For Customer site
        comp: div(
            diva({className: 'container'},
                pageHeader(t({en: `Contact Us`, ua: `Contact Us`})),
                markdownPiece({
                    en: `
                        Thank you for visiting AcademicPaperServed website! We will be happy to answer any questions, give a quote, and select a writer for your paper at any academic level. We value each customer and strive to achieve best results through communication process. Customer support representative is ready to advise you on the ordering process, choosing your delivery option and selecting an appropriate writer.
                    `,
                    ua: `
                        Thank you for visiting AcademicPaperServed website! We will be happy to answer any questions, give a quote, and select a writer for your paper at any academic level. We value each customer and strive to achieve best results through communication process. Customer support representative is ready to advise you on the ordering process, choosing your delivery option and selecting an appropriate writer.
                    `
                }),
                
                diva({style: {marginBottom: 10}}, t({en: 'AcademicPaperServed headquarter:', ua: 'AcademicPaperServed headquarter:'})),
                diva({style: {whiteSpace: 'pre', fontFamily: 'monospace'}}, tdedent({
                    en: `
                        DP World Inc
                        624 W Kristina Ln
                        Round Lake, IL 60073
                        United States
                    `,
                    ua: `
                        DP World Inc
                        624 W Kristina Ln
                        Round Lake, IL 60073
                        United States
                    `
                }))
            )
        )
    })
    
    const blogItems = t({
        en: [
            {
                listTitle: 'Why Would I Order a Research Paper Online?',
                title: 'Being a Student is not Easy',
                slug: 'why-order',
                content: `
                    Thousands of students find themselves torn between multiple obligations. They badly need good grades, but when it comes to writing essays and term papers, the situation becomes even more problematic. Remember, how hard you try to write a paper. You spend hours, trying to choose the best topic. You need to write a paper that is grammatically and stylistically correct. You need to present your argument logically and convincingly. 
                        
                    No wonder that, in these moments, the only thing you want is to have someone write a good custom essay for you. When you no longer feel capable of producing quality essays and term papers, all you need is to know that a qualified professional will help you to deal with your custom essay. You need to know that your essays and term papers will be submitted on time. You also need to know that your custom essay will be academically sound.
                        
                    What to do? This is a difficult question, but we have a good answer. Order a research paper, and you will forget about your writing troubles. When you have difficulty writing a custom essay, you can order a research paper that will meet the standards of advanced academic writing. You can have your essays and term papers written by an educated professional, which will increase your chances to earn a good grade. If you are tired of looking for good topics and arguments to write a paper, ordering a research paper is the best solution to your writing problems. Feel free to spend your time with family and friends, while professional writers are working on your custom essay! 
                    
                    Order a research paper, and you will see how your life becoming easy and self-fulfilling!
                `
            },
            {
                listTitle: 'High School Debate Topics in the United States',
                title: 'High School Debate Topics in the United States',
                slug: 'debate-topics',
                content: `
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
                `
            },
            {
                listTitle: 'How to Choose a Good Informative Speech Topic',
                title: 'How to Choose a Good Informative Speech Topic',
                slug: 'speech-topc',
                content: `
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
                `
            },
            {
                listTitle: 'How to Choose a Good Research or Essay Topic',
                title: 'How to Choose a Good Research or Essay Topic',
                slug: 'essay-topic',
                content: `
                    Henry Miller once said, ‘Writing, like life itself, is a voyage of discovery’. In this life we are guided by our parents, teachers and mentors who constantly teach us how to properly plan our steps in order to make as few errors as possible. Life is driven by other men’s experience. AcademicPaperServed is designed specifically to smoothly guide you through unruly and impertinent ocean of college research topics, topics for high school debate, informative speech topics, essay topics etc. Over quite a long period of time we have been observing the situations when students could not reveal their actual writing potential due to wrongly chosen college and high school research, essay or debate topics. One of the key goals of EasyWriting.Org is to teach you how to choose a good high school debate, informative speech, and college essay or research topic - a topic that will work and satisfy your own, your audience and your instructor’s aspirations and expectations! We’d like to share with you the best practice of putting a pen to the paper successfully. Our suggestions are based on experience of people who reached the highest goal of effective writing and not on some common practice of some abstract personalities!
                        
                    > Writing is not a preplanned recitation of what you know; writing Is thinking.
                    >
                    > ${mdash}Donald Murray
                        
                    Why not perceive writing as an exciting opportunity to make meaning out of your experiences and ideas, to help you think more clearly and independently and to establish new understandings in order to make new connections? In other words, change your current attitude towards writing!
                        
                    > The two most engaging powers of an author are to make new things familiar and familiar things new.
                    >
                    > ${mdash}Dr. Samuel Johnson
                        
                    Be sure you understand your assignment well and do not hesitate overloading your instructor with as many questions as possible to get an in-depth and insightful understanding of what to write about! Feel the power of being able to inform others, while learning something new at the same time! Isn’t it exciting to deliver via obtaining? Millions of men worldwide dream of having such a power. Think of what you can lose if you don’t try to love writing.
                        
                    > It's good to rub and polish our brain against that of others.
                    >
                    > ${mdash}Montaigne
                        
                    When choosing a good topic for your informative speech, high school debate, college essay or research, brainstorm your topic ideas first! Take a sheet of paper and a pen, draw a line in the center of the paper, and write questions on the left and answers on the right. Ask yourself:
                        
                    * What you are interested in
                    * Which topic has the greatest potential to inspire your creative thinking
                    * What you feel strongly about
                    * What you are more competent about
                    * What you want to learn more about
                    * Whether you have recently watched anything interesting and attention-grabbing on TV
                        
                    C’mon! Polish your brain! Open your eyes! What if a good argumentative essay topic is hiding in your fridge? Maybe there is a good informative speech topic hanging in your wardrobe?
                `
            },
        ],
        ua: [
            {
                listTitle: 'Why Would I Order a Research Paper Online?',
                title: 'Being a Student is not Easy',
                slug: 'why-order',
                content: `
                    Thousands of students find themselves torn between multiple obligations. They badly need good grades, but when it comes to writing essays and term papers, the situation becomes even more problematic. Remember, how hard you try to write a paper. You spend hours, trying to choose the best topic. You need to write a paper that is grammatically and stylistically correct. You need to present your argument logically and convincingly. 
                        
                    No wonder that, in these moments, the only thing you want is to have someone write a good custom essay for you. When you no longer feel capable of producing quality essays and term papers, all you need is to know that a qualified professional will help you to deal with your custom essay. You need to know that your essays and term papers will be submitted on time. You also need to know that your custom essay will be academically sound.
                        
                    What to do? This is a difficult question, but we have a good answer. Order a research paper, and you will forget about your writing troubles. When you have difficulty writing a custom essay, you can order a research paper that will meet the standards of advanced academic writing. You can have your essays and term papers written by an educated professional, which will increase your chances to earn a good grade. If you are tired of looking for good topics and arguments to write a paper, ordering a research paper is the best solution to your writing problems. Feel free to spend your time with family and friends, while professional writers are working on your custom essay! 
                    
                    Order a research paper, and you will see how your life becoming easy and self-fulfilling!
                `
            },
            {
                listTitle: 'High School Debate Topics in the United States',
                title: 'High School Debate Topics in the United States',
                slug: 'debate-topics',
                content: `
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
                `
            },
            {
                listTitle: 'How to Choose a Good Informative Speech Topic',
                title: 'How to Choose a Good Informative Speech Topic',
                slug: 'speech-topc',
                content: `
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
                `
            },
            {
                listTitle: 'How to Choose a Good Research or Essay Topic',
                title: 'How to Choose a Good Research or Essay Topic',
                slug: 'essay-topic',
                content: `
                    Henry Miller once said, ‘Writing, like life itself, is a voyage of discovery’. In this life we are guided by our parents, teachers and mentors who constantly teach us how to properly plan our steps in order to make as few errors as possible. Life is driven by other men’s experience. AcademicPaperServed is designed specifically to smoothly guide you through unruly and impertinent ocean of college research topics, topics for high school debate, informative speech topics, essay topics etc. Over quite a long period of time we have been observing the situations when students could not reveal their actual writing potential due to wrongly chosen college and high school research, essay or debate topics. One of the key goals of EasyWriting.Org is to teach you how to choose a good high school debate, informative speech, and college essay or research topic - a topic that will work and satisfy your own, your audience and your instructor’s aspirations and expectations! We’d like to share with you the best practice of putting a pen to the paper successfully. Our suggestions are based on experience of people who reached the highest goal of effective writing and not on some common practice of some abstract personalities!
                        
                    > Writing is not a preplanned recitation of what you know; writing Is thinking.
                    >
                    > ${mdash}Donald Murray
                        
                    Why not perceive writing as an exciting opportunity to make meaning out of your experiences and ideas, to help you think more clearly and independently and to establish new understandings in order to make new connections? In other words, change your current attitude towards writing!
                        
                    > The two most engaging powers of an author are to make new things familiar and familiar things new.
                    >
                    > ${mdash}Dr. Samuel Johnson
                        
                    Be sure you understand your assignment well and do not hesitate overloading your instructor with as many questions as possible to get an in-depth and insightful understanding of what to write about! Feel the power of being able to inform others, while learning something new at the same time! Isn’t it exciting to deliver via obtaining? Millions of men worldwide dream of having such a power. Think of what you can lose if you don’t try to love writing.
                        
                    > It's good to rub and polish our brain against that of others.
                    >
                    > ${mdash}Montaigne
                        
                    When choosing a good topic for your informative speech, high school debate, college essay or research, brainstorm your topic ideas first! Take a sheet of paper and a pen, draw a line in the center of the paper, and write questions on the left and answers on the right. Ask yourself:
                        
                    * What you are interested in
                    * Which topic has the greatest potential to inspire your creative thinking
                    * What you feel strongly about
                    * What you are more competent about
                    * What you want to learn more about
                    * Whether you have recently watched anything interesting and attention-grabbing on TV
                        
                    C’mon! Polish your brain! Open your eyes! What if a good argumentative essay topic is hiding in your fridge? Maybe there is a good informative speech topic hanging in your wardrobe?
                `
            },
        ],
    })
    
    for (const item of blogItems) {
        writePage({name: `blog-${item.slug}`, highlightedItem: 'blog', // For Customer site
            comp: div(
                diva({className: 'container'},
                    pageHeader(item.title),
                    markdownPiece(item.content)
                )
            )
        })
    }
    
    writePage({name: 'blog', highlightedItem: 'blog', // For Customer site
        comp: div(
            diva({className: 'container'},
                pageHeader(t({en: `Writing Blog`, ua: `Писательский Блог`})),
                hrefBullets(blogItems.map(x => ({title: x.listTitle, href: `blog-${x.slug}.html`}))),
            )
        )
    })
    
    writeDynamicPages(customerDynamicPageNames(), writePage)
    
    function writePage(def) {
        genericWritePage(asn(def, {root, tabTitle, lang, clientKind: 'customer', t}))
    }
    
}

function genericWritePage({name, comp, css='', js='', highlightedItem, root, tabTitle, lang, clientKind, t}) {
    fs.writeFileSync(`${root}/${name}.html`, `
        <!DOCTYPE html>
        <html lang="en" style="position: relative; min-height: 100%;">
            <head>
                <meta charset="utf-8">
                <meta http-equiv="X-UA-Compatible" content="IE=edge">
                <meta name="viewport" content="width=device-width, initial-scale=1">
                
                ${ReactDOMServer.renderToStaticMarkup(React.createElement('title', {}, tabTitle))}
                
                <link href="bootstrap-master/css/bootstrap.min.css" rel="stylesheet">
                <link rel="stylesheet" href="font-awesome-4.6.3/css/font-awesome.min.css">
                <style>
                    /* @ctx css */
        
                    button:disabled {
                        cursor: default !important;
                    }
                    input:disabled {
                        cursor: default !important;
                    }
                    select:disabled {
                        cursor: default !important;
                    }
        
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
                    setFavicon('${clientKind === 'customer' ? 'favicon-customer.ico' : 'favicon-writer.ico'}')
        
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
                    ${ReactDOMServer.renderToStaticMarkup(renderTopNavbar({clientKind, highlightedItem, t}))}
                </div>
            
                <div id="root">
                    <div id="staticShit" style="display: none;">
                        <!-- BEGIN CONTENT -->
                        ${ReactDOMServer.renderToStaticMarkup(comp)}
                        <!-- END CONTENT -->
                    </div>
        
                    <div id="ticker" style="display: none;">${ReactDOMServer.renderToStaticMarkup(wholePageTicker())}</div>
        
                    <script>
                        if (localStorage.getItem('token')) {
                            document.getElementById('ticker').style.display = ''
                        } else {
                            document.getElementById('staticShit').style.display = ''
                            window.staticShitIsRenderedStatically = true
                        }
                    </script>
                </div>
                                    
                <div id="footer" style="background-color: #f8f8f8; border: 1px solid #e7e7e7; position: absolute; left: 0px; bottom: 0px; width: 100%; color: #333; font-family: Helvetica Neue, Helvetica, Arial, sans-serif; font-size: 12px; padding: 5px 15px; height: 28px;">
                    ${run(_=> {
                         if (clientKind === 'customer') return `
                            © Copyright 2015-2016 AcademicPaperServed. All rights reserved
                         `
                    else if (clientKind === 'writer') return `
                            © Copyright 2015-2016 Writer UA. All rights reserved
                         `
                    })}
                </div>

                <script src="jquery.min.js"></script>
                <!-- <script src="jquery-hack.js"></script> -->
                <script src="bootstrap-hack.js"></script>
                <!-- <script src="bootstrap-master/js/bootstrap.min.js"></script> -->
                            
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
    `)
}

function renderTestimonials(clientKind) {
    return div(
        pageHeader(t({en: `What People Say`, ua: `Что о нас говорят`})),
        diva({id: 'testimonials-window'},
            diva({id: 'testimonials-strip'}, ...[
                {name: {en: 'Nicole', ua: 'Nicole'}, img: 'nicole.jpg', says: {
                     en: `Never expect such an urgent project could be accomplished overnight! I really appreciated the level of your writers and you treating the customers. I will recommend your services to my friends.`,
                     ua: `Never expect such an urgent project could be accomplished overnight! I really appreciated the level of your writers and you treating the customers. I will recommend your services to my friends.`}},
                {name: {en: 'Miranda', ua: 'Miranda'}, img: 'miranda.jpg', says: {
                     en: `Wow!!! The paper got A+, for the first time in my student life I was graded so high! Thanx!`,
                     ua: `Wow!!! The paper got A+, for the first time in my student life I was graded so high! Thanx!`}},
                {name: {en: 'Mike P.', ua: 'Mike P.'}, img: 'mike-p.jpg', says: {
                     en: `I was impressed when you writer sent me the copies of sources in one hour upon my request, though the paper was written over a month ago and I did not ask to make that at once. Carry on!`,
                     ua: `I was impressed when you writer sent me the copies of sources in one hour upon my request, though the paper was written over a month ago and I did not ask to make that at once. Carry on!`}},
                {name: {en: 'Joseph B.', ua: 'Joseph B.'}, img: 'joseph-b.jpg', says: {
                     en: `First I doubted I’d get anything of good quality, but I was up${nbsp}to the eyes in work and had no other choice. The paper${nbsp}proved to be authentic and came on time. Can I get${nbsp}the same writer for my next essay?`,
                     ua: `First I doubted I’d get anything of good quality, but I was up${nbsp}to the eyes in work and had no other choice. The paper${nbsp}proved to be authentic and came on time. Can I get${nbsp}the same writer for my next essay?`}},
                {name: {en: 'Mark C.', ua: 'Mark C.'}, img: 'mark-c.jpg', says: {
                     en: `How come you are so smart in every subject, guys? I’ve always been a bright student, but I admit you write quicker and select the most up-to-date sources. I need to learn from you.`,
                     ua: `How come you are so smart in every subject, guys? I’ve always been a bright student, but I admit you write quicker and select the most up-to-date sources. I need to learn from you.`}},
                {name: {en: 'Linda R.', ua: 'Linda R.'}, img: 'linda-r.jpg', says: {
                     en: `I would have never accomplished this research paper on my own! It was too challenging. You also explained some parts of the paper I did not understand. Excellent job!`,
                     ua: `I would have never accomplished this research paper on my own! It was too challenging. You also explained some parts of the paper I did not understand. Excellent job!`}},
                ].map(item =>
                    diva({className: 'testimonials-item'},
                        diva({className: 'media'},
                            diva({className: 'media-left'},
                                img(item.img, {className: 'media-object'})),
                            diva({className: 'media-body'},
                                h4a({className: 'media-heading'}, t(item.name)),
                                span(t(item.says))))))),
                                
            diva({style: {display: 'flex', alignItems: 'center', position: 'absolute', width: 20, right: 0, top: 0, height: '100%'}},
                glyph('chevron-right', {id: 'testimonials-right', className: 'fa-2x'}))),
                
        rawHtml(``),
    )
}

function h3Smaller(it) {
    return h3(spana({style: {fontSize: '80%'}}, it))
}

function bullets(items) {
    return ula({className: 'fa-ul', style: {marginLeft: 22}}, ...items.map(item =>
               lia({style: {marginBottom: 10}}, glyph('star', {className: 'fa-li', style: {color: BLUE_GRAY_600}}), item)))
}

function locBullets(items) {
    return bullets(items.map(x => t(x)))
}

function hrefBullets(items) {
    return bullets(items.map(x => aa({href: x.href}, x.title)))
}

function tdedent(ss) {
    return t(omapo(ss, dedent))
}

function markdownPiece(content) {
    if (typeof content === 'object') {
        content = t(content)
    }
    return markdown(dedent(content))
}

function horizBulletsRow(items, {horizContentMargin=0}={}) {
    const colSize = 12 / items.length
    return diva({className: 'row', style: {marginBottom: 20}}, ...items.map(x =>
               diva({className: 'col-md-' + colSize},
                   diva({style: {textAlign: 'center', marginBottom: 10}}, glyph(x.glyph, {className: 'fa-2x', style: {color: BLUE_GRAY_600}})),
                   diva({style: {textAlign: 'center', margin: `0 ${horizContentMargin}px`}}, t(x)))))
}

function crashForDebuggingSake_randomly() {
    if (random(1) === 0) return clog('Not crashing for now')
        
    clog('Receive some shit on stdout')
    process.stderr.write('More on stderr\n')
    clog('Stdout shit continues on another line')
    process.stderr.write('And to stderr again\n')
    clog('Can you see all this shit in DevUI?')
    process.exit(1)
}

