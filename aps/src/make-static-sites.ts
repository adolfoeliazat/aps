/*
 * APS
 * 
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

import fs = require('fs')
import sh = require('shelljs')
import static 'into-u ./stuff'

sh.config.fatal = true
Error.stackTraceLimit = Infinity

makeCustomerSite({lang: 'en'})
makeCustomerSite({lang: 'ua'})

0&& makeCustomerSite({
    lang: 'en',

    home: {
        welcomeSection: {
            title: `Welcome to AcademicPaperServed`,
            content: `
                Hey there! Listening to music, but can’t follow your favorite song, because there’s a research paper you’ve got to get done by tomorrow?
                    
                Sitting in front of your laptop, desperately typing the keywords in your search engine browser and wondering why AcademicPaperServed came up with an idea of giving written assignments to the needy students like yourself?
                    
                Can’t be effective in the workplace, because there’s a persuasive essay you’ve got to turn in early next week?
                    
                Now relax. You’ve just reached the right destination! At AcademicPaperServed writing papers is really very easy!
                
                Professional custom papers writing service, research paper, essay and term paper writing service from experienced writers at affordable price.
            `
        },
        
        whoWeAreSection: {
            title: `Who We Are`,
            content: `
                AcademicPaperServed is an experienced custom writing service, consisting of expert writers to deliver written assignments, essays, research papers, term papers, theses, book reports, etc to English-speaking clients worldwide. Our team of professional writers focuses on the highest quality of all types of academic papers. Thesis writing is a challenge to many students. With our assistance it will not be a problem anymore!
                
                Our writers are versed in various fields of academic writing, so if you do not find a suitable category on this page, feel free to contact our Support Center to find out availability of writing help in your area of interest.
                
                We have access to most reliable and complete online libraries to make your research or essay unique.
                
                AcademicPaperServed team consists of expert academic writers providing you with free guidelines, helping with topic selection, proofreading, editing and formatting even if you want to get your essay done overnight! We guarantee premium quality writing with urgent projects.            
            `
        },
        
        whatWeOfferSection: {
            title: `What We Offer`,
            primary: [
                {glyph: 'envira', content: `Custom essay, research, thesis writing`},
                {glyph: 'rocket', content: `Plagiarism-free original papers written from scratch`},
                {glyph: 'bomb', content: `Proofreading and editing of written papers`},
                {glyph: 'book', content: `Free guidelines on essay topic selection and writing process`},
            ],
            secondary: [
                {content: `Custom essay, research paper, book report, term paper, precis, sketch, poetry analysis, data collection, thesis writing, SWOT analysis, lab reports, dissertations, reviews, speeches, presentations, case studies, courseworks, homeworks, assignments, creative writing, blog writing, capstone project, grant proposal, lab reports`},
                {content: `Plagiarism-free original papers written from scratch`},
                {content: `Proofreading and editing of written papers`},
                {content: `Choosing sources for your paper, providing with annotated bibliography upon request`},
                {content: `Free guidelines on successful essay topic selection and writing process`},
                {content: `Individual approach to every customer, no repetitions, free consulting on the paper content`},
                {content: `Free revisions till you are completely satisfied`},
                {content: `Meeting your deadline`},
                {content: `Security and confidentiality`},
            ],
        },
        
        featuresSection: {
            title: `Features`,
            primary1: [
                {glyph: 'pencil', content: `No plagiarism`},
                {glyph: 'star', content: `Only premium quality`},
                {glyph: 'list', content: `Free title page, outline, list${nbsp}of${nbsp}references`},
            ],
            primary2: [
                {glyph: 'gi-piggy-bank', content: `One-time and life-time discounts to returning customers`},
                {glyph: 'credit-card', content: `30-days money back guarantee`},
                {glyph: 'life-saver', content: `24/7 support`},
            ],
        },
        
        testimonials: {
            title: `What People Say`,
            items: [
                {name: 'Nicole', img: 'nicole.jpg', says: `Never expect such an urgent project could be accomplished overnight! I really appreciated the level of your writers and you treating the customers. I will recommend your services to my friends.`},
                {name: 'Miranda', img: 'miranda.jpg', says: `Wow!!! The paper got A+, for the first time in my student life I was graded so high! Thanx!`},
                {name: 'Mike P.', img: 'mike-p.jpg', says: `I was impressed when you writer sent me the copies of sources in one hour upon my request, though the paper was written over a month ago and I did not ask to make that at once. Carry on!`},
                {name: 'Joseph B.', img: 'joseph-b.jpg', says: `First I doubted I’d get anything of good quality, but I was up${nbsp}to the eyes in work and had no other choice. The paper${nbsp}proved to be authentic and came on time. Can I get${nbsp}the same writer for my next essay?`},
                {name: 'Mark C.', img: 'mark-c.jpg', says: `How come you are so smart in every subject, guys? I’ve always been a bright student, but I admit you write quicker and select the most up-to-date sources. I need to learn from you.`},
                {name: 'Linda R.', img: 'linda-r.jpg', says: `I would have never accomplished this research paper on my own! It was too challenging. You also explained some parts of the paper I did not understand. Excellent job!`},
            ],
        },
    },
    
    why: {
        title: `Why AcademicPaperServed?`,
        sections: [
            {
                title: `We care about each customer’s academic success`,
                content: `According to the statistics, almost 90% of current U.S. undergraduate and graduate students work either full-time or part-time, while they study. Often, they have little time to complete all of their essays and term papers by themselves, as writing is really time consuming. Others simply don't think of writing as the most pleasurable activity. All students aim for graduation. AcademicPaperServed is here to help them reach this goal.`
            },
            {
                title: `We make a very strong commitment to quality`,
                content: `Quality is one of the major benefits of our custom writing service. Every AcademicPaperServed writer is experienced in the special field of studies. As a result, all of the customer's most sophisticated requirements are met by the professional freelance writer with a deep knowledge of the custom paper topic. Our custom essay writers possess Bachelor's / Master's degrees in the related fields of knowledge.`
            },
            {
                title: `We show an individual approach`,
                content: `
                    To Every Customer and Every Project we deal with.
                    
                    With us customers are sure to get their high school, college and university papers tailored exactly to their requirements and needs. No need to worry about quality! Your professor will be completely satisfied. We follow paper details specifically, provide with relevant sources from our library and/or use definite sources (books, journal and magazine articles, encyclopedia excerpts, peer reviewed studies, etc.) requested by the customer.
                    
                    Our team is aware of students’ concerns and issues. Any academic achievement becomes joyful and easy with our team. You are sure to get your writer’s comments on the paper content, advice on choosing a suitable topic, primary and secondary sources selection, ways of presenting it in pubic if needed, etc. You are a getting a complex academic help along with the written coherent text which you, surely, deserve!
                    
                    24/7 support will provide you with prompt unrivalled assistance in every matter of your concern and makes sure you’ll get excellent quality paper!
                    
                    AcademicPaperServed team helps English speaking customers from all over the world with anything an academic writing implies, from research ideas to styles and design of your paper. We are strictly following the rules of academic presentation combining it with modern and creative approaches. You are sure to get your project written in a plain, clear language with smooth transitions and coherent rendering.
                `
            },
            {
                title: `We hire only expert writers`,
                content: `
                We involve only experienced staff in the writing process! Each member of the AcademicPaperServed writing team has at least 2 years + experience in academic research field.
                
                You are sure to get your custom paper done by a native English speaker, who is versed in the area of your research, formats your paper according to the latest requirements of citation styles (MLA, APA, Harvard, Chicago, Turabian, Oxford and others) and delivers with no delays.
                
                US-based customer support will offer samples of various academic papers for your consideration.
                
                We’ve been dealing with ALL types of academic papers and we are always glad to offer you help with any essay (like scholarship essay, admission essay, application essay, entrance essay, personal statement) and research paper type at any academic level and in every possible discipline. If you want your paper to be well-organized and logically constructed, choose us!
                `
            },
            {
                title: `Plagiarism-free and full-of-creativity zone`,
                content: `
                    Our writers get paid only when they use their own thoughts and ideas, elucidated through high-quality research and writing proficiency. For that reason, the following is not acceptable under any situation: copy-pasting from websites, copying from offline books, texts, and journals, basing one’s work on the ideas/structure of others or previous pieces of work. All papers are checked against millions of hard copy sources, billions of web pages, and countless pieces of work of other researchers. The papers are checked with the most up-to-date plagiarism detection software prior to being delivered to customer. There is totally no chance of delivering a plagiarized paper.
                    
                    At AcademicPaperServed all writers apply a creative approach to every essay. Each custom paper is written in compliance with the requests and desires of the customer.
                    
                    Be assured our friendly and knowledgeable staff will provide you with immediate, top-quality, and US-based customer support. All responses are personalized to the needs of the student.
                `
            },
        ],
        conclusion: `With AcademicPaperServed writing papers is not a pain in the neck! With AcademicPaperServed writing papers is as enjoyable as dancing or watching your favorite movie! Just offload your written assignment to AcademicPaperServed and dedicate your free time to things that really make you happy!`
    },
    
    prices: {
        title: `Our Prices`,
//        deliveryOptionTitles: {
//            '8d': `8+ days`,
//            '7d': `6-7 days`,
//            '5d': `4-5 days`,
//            '3d': `2-3 days`,
//            '24h': `24 hours`,
//            '12h': `12 hours`,
//        },
//        paperTypeTitles: {
//            'essay': `Essay or Research`,
//            'bibliography': `Annotated Bibliography`,
//            'editing': `Proofreading and Editing`,
//        },
//        table: [
//            {
//                deliveryOption: '8d',
//                items: [
//                    {paperType: 'essay', price: 14.99},
//                    {paperType: 'bibliography', price: 10.99},
//                    {paperType: 'editing', price: 3.99},
//                ]
//            },
//            {
//                deliveryOption: '7d',
//                items: [
//                    {paperType: 'essay', price: 14.99},
//                    {paperType: 'bibliography', price: 10.99},
//                    {paperType: 'editing', price: 3.99},
//                ]
//            },
//            {
//                deliveryOption: '5d',
//                items: [
//                    {paperType: 'essay', price: 14.99},
//                    {paperType: 'bibliography', price: 10.99},
//                    {paperType: 'editing', price: 3.99},
//                ]
//            },
//            {
//                deliveryOption: '3d',
//                items: [
//                    {paperType: 'essay', price: 14.99},
//                    {paperType: 'bibliography', price: 10.99},
//                    {paperType: 'editing', price: 3.99},
//                ]
//            },
//            {
//                deliveryOption: '24h',
//                items: [
//                    {paperType: 'essay', price: 14.99},
//                    {paperType: 'bibliography', price: 10.99},
//                    {paperType: 'editing', price: 3.99},
//                ]
//            },
//            {
//                deliveryOption: '12h',
//                items: [
//                    {paperType: 'essay', price: 14.99},
//                    {paperType: 'bibliography', price: 10.99},
//                    {paperType: 'editing', price: 3.99},
//                ]
//            },
//        ],
        
        pricingPolicySection: {
            title: `Pricing Policy`,
            content: `
                Prices at AcademicPaperServed are set to meet the average point on the current custom writing market. This enables us to choose experts writers as freelance members in the team, who are able to meet the highest demands at faculties teaching in English.
                
                Our support is daily queried on how one can buy an essay or research paper on a certain topic. AcademicPaperServed does sell pre-written papers, each assignment is made individually and each research is written from scratch. Top-notch writing quality is only achieved through effective communication of the writer and the customer, be it an order on a blog article, high school debate project or a music review.
                
                Each quote we give to our customer is based primarily on the delivery date opted, type of an academic written assignment, number of pages and the amount of discount your membership is liable to.
                
                You may find out your quote in one minute! Simply press the Order Your Custom Essay Now tab at the top of the page marked in green. Our Customer Support employee will advise you on the price and terms of delivery. 
            `
        },
        
        bonusesSection: {
            title: `Bonuses`,
            content: `Ordering a paper at AcademicPaperServed, you also get:`,
            items: [
                `Free Title Page`,
                `Free Outline`,
                `Free List of References`,
                `Free Plagiarism Report (upon additional request)`,
            ]
        },
        
        discountPolicySection: {
            title: `Discount Policy`,
            contentBeforeItems: `
                Please note that each AcademicPaperServed customer is eligible for one-time and life-time discounts. One-time discount is granted to each new customer registered in our system and makes 5% off the first order total. Lifetime discount is provided to each returning customer depending on the total number of pages (on multiple papers) ordered since the moment of registration, and namely:
            `,
            items: [
                `More than 50 pages${mdash}5%`,
                `More than 100 pages${mdash}10%`,
                `More than 150 pages${mdash}15%`,
                `More than 200 pages${mdash}30`,
            ],
            contentAfterItems: `
                We’re sure that at AcademicPaperServed we employ a fair discount policy. We respect each certain customer and hope to establish long-term cooperation with him/her. Since customers are our most valued asset, we put a lot of effort to retaining and satisfying them through our flexible lifetime discount policy.
            `
        },
    },
})

function makeCustomerSite({lang}) {
    const def = {}
    
    const root = `${__dirname}/../built/${lang}-customer`
    sh.rm('-rf', root)
    sh.mkdir('-p', root)
    
    const vendor = `${__dirname}/../vendor`
    sh.cp(`${vendor}/jquery-2.2.4/jquery.min.js`, root)
    sh.cp('-r', `${vendor}/bootstrap-master`, root)
    sh.cp('-r', `${vendor}/font-awesome-4.6.3`, root)
    sh.cp(`${__dirname}/../asset/*`, root)
    
    const tabTitle = {en: `APS`, ua: `APS UA`}[lang]
    
    writePage({name: 'index',
        comp: div(
            diva({className: 'container'},
                pageHeader({en: `Welcome to AcademicPaperServed`, ua: `Welcome to AcademicPaperServed UA`}),
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
                
                pageHeader({en: `Features`, ua: `Features`}),
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
                
                pageHeader({en: `Who We Are`, ua: `Who We Are`}),
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
                
                pageHeader({en: `What People Say`, ua: `What People Say`}),
                diva({id: 'testimonials-window'},
                    diva({id: 'testimonials-strip'}, ...[
                        {name: {en: 'Nicole', ua: 'Nicole'}, img: 'nicole.jpg', says: {
                             en: `Never expect such an urgent project could be accomplished overnight! I really appreciated the level of your writers and you treating the customers. I will recommend your services to my friends.`,
                             ua: `Never expect such an urgent project could be accomplished overnight! I really appreciated the level of your writers and you treating the customers. I will recommend your services to my friends.`}},
                        {name: 'Miranda', img: 'miranda.jpg', says: {
                             en: `Wow!!! The paper got A+, for the first time in my student life I was graded so high! Thanx!`,
                             ua: `Wow!!! The paper got A+, for the first time in my student life I was graded so high! Thanx!`}},
                        {name: 'Mike P.', img: 'mike-p.jpg', says: {
                             en: `I was impressed when you writer sent me the copies of sources in one hour upon my request, though the paper was written over a month ago and I did not ask to make that at once. Carry on!`,
                             ua: `I was impressed when you writer sent me the copies of sources in one hour upon my request, though the paper was written over a month ago and I did not ask to make that at once. Carry on!`}},
                        {name: 'Joseph B.', img: 'joseph-b.jpg', says: {
                             en: `First I doubted I’d get anything of good quality, but I was up${nbsp}to the eyes in work and had no other choice. The paper${nbsp}proved to be authentic and came on time. Can I get${nbsp}the same writer for my next essay?`,
                             ua: `First I doubted I’d get anything of good quality, but I was up${nbsp}to the eyes in work and had no other choice. The paper${nbsp}proved to be authentic and came on time. Can I get${nbsp}the same writer for my next essay?`}},
                        {name: 'Mark C.', img: 'mark-c.jpg', says: {
                             en: `How come you are so smart in every subject, guys? I’ve always been a bright student, but I admit you write quicker and select the most up-to-date sources. I need to learn from you.`,
                             ua: `How come you are so smart in every subject, guys? I’ve always been a bright student, but I admit you write quicker and select the most up-to-date sources. I need to learn from you.`}},
                        {name: 'Linda R.', img: 'linda-r.jpg', says: {
                             en: `I would have never accomplished this research paper on my own! It was too challenging. You also explained some parts of the paper I did not understand. Excellent job!`,
                             ua: `I would have never accomplished this research paper on my own! It was too challenging. You also explained some parts of the paper I did not understand. Excellent job!`}},
                        ].map(item =>
                            diva({className: 'testimonials-item'},
                                diva({className: 'media'},
                                    diva({className: 'media-left'},
                                        img(item.img, {className: 'media-object'})),
                                    diva({className: 'media-body'},
                                        h4a({className: 'media-heading'}, item.name[lang]),
                                        span(item.says[lang])))))),
                                        
                    divsa({display: 'flex', alignItems: 'center', position: 'absolute', width: 20, right: 0, top: 0, height: '100%'},
                        glyph('chevron-right', {id: 'testimonials-right', className: 'fa-2x'}))),
                
                pageHeader({en: `What We Offer`, ua: `What We Offer`}),
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
        
        css: `
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
        `,
        
        js: `
            !function initTestimonials() {
              var numSlides = 3,
                  numFrames = 60,
                  autoIgnitionPeriod = 15000,
                  rightArrowAppearsDelay = 200,
                  shiftFractions,
                  slide,
                  autoIgnitionTimeoutHandle,
                  magic
              
              calcTrajectory()
              
              $('.testimonials-item').slice(0, 2).clone().appendTo($('#testimonials-strip'))
              $('#testimonials-right').on('click', ignite)
              
              var mqLarge = window.matchMedia('(min-width: 1200px)')
              var mqMedium = window.matchMedia('(min-width: 992px)')
              mqLarge.addListener(onMediaChange)
              mqMedium.addListener(onMediaChange)
              onMediaChange()
              
              
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
                              $('#testimonials-right').show()
                              scheduleAutoIgnition()
                          }, rightArrowAppearsDelay)
                      } else {
                          requestAnimationFrame(step)
                      }
                  }
              }
            }()                    
        `
    })
    
    writePage({name: 'why', activeNav: 'why',
        comp: div(
            diva({className: 'container'},
                pageHeader({en: `Why AcademicPaperServed?`, ua: `Why AcademicPaperServed UA?`}),
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
                    divsa({},
                        divsa({}, h3Smaller(section.title[lang])),
                        divsa({}, markdown(dedent(section.content[lang])))))
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
    
    writePage({name: 'prices', activeNav: 'prices',
        comp: div(
            diva({className: 'container'},
                pageHeader({en: `Our Prices`, ua: `Наши цены`}),
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
                            
                pageHeader({en: `Pricing Policy`, ua: `Pricing Policy`}),
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
                
                pageHeader({en: `Bonuses`, ua: `Bonuses`}),
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
                
                pageHeader({en: `Discount Policy`, ua: `Discount Policy`}),
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
    
    const sampleItems = {
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
    }[lang]
    
    writePage({name: 'samples', activeNav: 'samples',
        comp: div(
            diva({className: 'container'},
                pageHeader({en: `Sample Papers`, ua: `Примеры работ`}),
                hrefBullets(sampleItems),
            )
        )
    })
            
    writePage({name: 'faq', activeNav: 'faq',
        comp: div(
            diva({className: 'container'},
                pageHeader({en: `FAQ`, ua: `FAQ`}),
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
                    divsa({},
                        divsa({}, h3Smaller(section.title[lang])),
                        divsa({}, markdown(dedent(section.content[lang])))))
            )
        )
    })
    
    
    function h3Smaller(it) {
        return h3(spansa({fontSize: '80%'}, it))
    }
    
    function bullets(items) {
        return ula({className: 'fa-ul', style: {marginLeft: 22}}, ...items.map(item =>
                   lisa({marginBottom: 10}, glyph('star', {className: 'fa-li', style: {color: BLUE_GRAY_600}}), item)))
    }
    
    function locBullets(items) {
        return bullets(items.map(x => x[lang]))
    }
    
    function hrefBullets(items) {
        return bullets(items.map(x => aa({href: x.href}, x.title)))
    }
    
    function t(ss) {
        return ss[lang]
    }
    
    function pageHeader(title) {
        return diva({className: 'page-header', style: {marginTop: 30}},
                   el('h3', {}, title[lang]))
    }
    
    function markdownPiece(content) {
        return markdown(dedent(content[lang]))
    }
    
    function horizBulletsRow(items, {horizContentMargin=0}={}) {
        const colSize = 12 / items.length
        return diva({className: 'row', style: {marginBottom: 20}}, ...items.map(x =>
                   diva({className: 'col-md-' + colSize},
                       divsa({textAlign: 'center', marginBottom: 10}, glyph(x.glyph, {className: 'fa-2x', style: {color: BLUE_GRAY_600}})),
                       divsa({textAlign: 'center', margin: `0 ${horizContentMargin}px`}, x[lang]))))
    }
    
    function writePage({name, comp, css='', js='', activeNav}) {
        fs.writeFileSync(`${root}/${name}.html`, `
            <!DOCTYPE html>
            <html lang="en">
                <head>
                    <meta charset="utf-8">
                    <meta http-equiv="X-UA-Compatible" content="IE=edge">
                    <meta name="viewport" content="width=device-width, initial-scale=1">
                    
                    ${ReactDOMServer.renderToStaticMarkup(React.createElement('title', {}, tabTitle))}
                    
                    <link href="bootstrap-master/css/bootstrap.min.css" rel="stylesheet">
                    <link rel="stylesheet" href="font-awesome-4.6.3/css/font-awesome.min.css">
                    <style>${css}</style>
                </head>
                <body style="padding-top: 50px;">
                    <nav class="navbar navbar-default navbar-fixed-top">
                        <div class="container-fluid">
                            <div class="navbar-header">
                              <a class="navbar-brand" href="/">APS</a>
                            </div>

                            <div class="collapse navbar-collapse" style="text-align: center;" id="bs-example-navbar-collapse-1">
                                <ul class="nav navbar-nav" style="float: none; display: inline-block; vertical-align: top;">
                                    <li ${activeNav === 'why' ? `class="active"` : ``}><a href="why.html">Why Us?</a></li>
                                    <li ${activeNav === 'prices' ? `class="active"` : ``}><a href="prices.html">Prices</a></li>
                                    <li ${activeNav === 'samples' ? `class="active"` : ``}><a href="samples.html">Sample Papers</a></li>
                                    <li ${activeNav === 'order' ? `class="active"` : ``}><a href="order.html">Order a Paper</a></li>
                                    <li ${activeNav === 'faq' ? `class="active"` : ``}><a href="faq.html">FAQ</a></li>
                                    <li ${activeNav === 'contact' ? `class="active"` : ``}><a href="contact.html">Contact Us</a></li>
                                    <li ${activeNav === 'blog' ? `class="active"` : ``}><a href="blog.html">Writing Blog</a></li>
                                </ul>
                                <ul class="nav navbar-nav navbar-right">
                                    <li><a href="sign-in.html">Sign In</a></li>
                                    <!--
                                    <li class="dropdown">
                                        <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Dropdown <span class="caret"></span></a>
                                        <ul class="dropdown-menu">
                                            <li><a href="#">Action</a></li>
                                            <li><a href="#">Another action</a></li>
                                            <li><a href="#">Something else here</a></li>
                                            <li role="separator" class="divider"></li>
                                            <li><a href="#">Separated link</a></li>
                                        </ul>
                                    </li>
                                    -->
                                </ul>
                              </div> <!-- /.navbar-collapse -->
                        </div> <!-- /.container-fluid -->
                    </nav>
                
                    ${ReactDOMServer.renderToStaticMarkup(comp)}

                    <script src="jquery.min.js"></script>
                    <script src="bootstrap-master/js/bootstrap.min.js"></script>
                    <script>${js}</script>
                </body>
            </html>
        `)
    }
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

