/*
 * APS
 * 
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

import fs = require('fs')
import sh = require('shelljs')
import static 'into-u'

sh.config.fatal = true

makeCustomerSite({
    dir: 'ua-customer',
    title: 'APS UA',
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
                {glyph: 'book', content: `Free guidelines on successful essay topic selection and writing process`},
            ],
            secondary: [
                {glyph: '', content: `Custom essay, research paper, book report, term paper, precis, sketch, poetry analysis, data collection, thesis writing, SWOT analysis, lab reports, dissertations, reviews, speeches, presentations, case studies, courseworks, homeworks, assignments, creative writing, blog writing, capstone project, grant proposal, lab reports`},
                {glyph: '', content: `Plagiarism-free original papers written from scratch`},
                {glyph: '', content: `Proofreading and editing of written papers`},
                {glyph: '', content: `Choosing sources for your paper, providing with annotated bibliography upon request`},
                {glyph: '', content: `Free guidelines on successful essay topic selection and writing process`},
                {glyph: '', content: `Individual approach to every customer, no repetitions, free consulting on the paper content`},
                {glyph: '', content: `Free revisions till you are completely satisfied`},
                {glyph: '', content: `Meeting your deadline`},
                {glyph: '', content: `Security and confidentiality`},
            ],
        },
        
        featuresSection: {
            title: `Features`,
            primary1: [
                {glyph: 'pencil', content: `No plagiarism`},
                {glyph: 'star', content: `Only premium quality`},
                {glyph: 'list', content: `Free title page, outline, list\u00a0of\u00a0references`},
            ],
            primary2: [
                {glyph: 'credit-card', content: `One-time and life-time discounts to returning customers`},
                {glyph: 'diamond', content: `30-days money back guarantee`},
                {glyph: 'life-saver', content: `24/7 support`},
            ],
        },
        
        testimonials: {
            primary: [
                {name: 'Nicole', img: 'nicole.jpg', says: `Never expect such an urgent project could be accomplished overnight! I really appreciated the level of your writers and you treating the customers. I will recommend your services to my friends.`},
                {name: 'Miranda', img: 'miranda.jpg', says: `Wow!!! The paper got A+, for the first time in my student life I was graded so high! Thanx!`},
                {name: 'Mike P.', img: 'mike-p.jpg', says: `I was impressed when you writer sent me the copies of sources in one hour upon my request, though the paper was written over a month ago and I did not ask to make that at once. Carry on!`},
                {name: 'Joseph B.', img: 'joseph-b.jpg', says: `First I doubted I’d get anything of good quality, but I was up to the eyes in work and had no other choice. The paper proved to be authentic and came on time. Can I get the same writer for my next essay?`},
                {name: 'Mark C.', img: 'mark-c.jpg', says: `How come you are so smart in every subject, guys? I’ve always been a bright student, but I admit you write quicker and select the most up-to-date sources. I need to learn from you.`},
                {name: 'Linda R.', img: 'linda-r.jpg', says: `I would have never accomplished this research paper on my own! It was too challenging. You also explained some parts of the paper I did not understand. Excellent job!`},
            ],
        },
    }
})

function makeCustomerSite(def) {
    const root = `${__dirname}/../built/${def.dir}`
    sh.rm('-rf', root)
    sh.mkdir('-p', root)
    
    const vendor = `${__dirname}/../vendor`
    sh.cp(`${vendor}/jquery-2.2.4/jquery.min.js`, root)
    sh.cp('-r', `${vendor}/bootstrap-master`, root)
    sh.cp('-r', `${vendor}/font-awesome-4.6.3`, root)
    
    writePage({name: 'index', title: def.title, comp: div(
        diva({className: 'container'},
            pageHeader(def.home.welcomeSection.title, {size: 3}),
            markdown(dedent(def.home.welcomeSection.content)),
            
            pageHeader(def.home.featuresSection.title, {size: 3}),
            horizBulletsRow(def.home.featuresSection.primary1, {horizContentMargin: 40}),
            horizBulletsRow(def.home.featuresSection.primary2, {horizContentMargin: 40}),
            
            pageHeader(def.home.whoWeAreSection.title, {size: 3}),
            markdown(dedent(def.home.whoWeAreSection.content)),
            
            pageHeader(def.home.whatWeOfferSection.title, {size: 3}),
            horizBulletsRow(def.home.whatWeOfferSection.primary),
            ula({className: 'fa-ul', style: {marginLeft: 22}}, ...def.home.whatWeOfferSection.secondary.map(x =>
                lisa({marginBottom: 10}, glyph('star', {className: 'fa-li', style: {color: BLUE_GRAY_600}}), x.content)))
        ),
    )})
    
    function horizBulletsRow(items, {horizContentMargin=0}={}) {
        const colSize = 12 / items.length
        return diva({className: 'row', style: {marginBottom: 20}}, ...items.map(x =>
                   diva({className: 'col-md-' + colSize},
                       divsa({textAlign: 'center', marginBottom: 10}, glyph(x.glyph, {className: 'fa-2x', style: {color: BLUE_GRAY_600}})),
                       divsa({textAlign: 'center', margin: `0 ${horizContentMargin}px`}, x.content))))
    }
    
    function writePage({name, title, comp}) {
        fs.writeFileSync(`${root}/${name}.html`, `
            <!DOCTYPE html>
            <html lang="en">
                <head>
                    <meta charset="utf-8">
                    <meta http-equiv="X-UA-Compatible" content="IE=edge">
                    <meta name="viewport" content="width=device-width, initial-scale=1">
                    
                    ${ReactDOMServer.renderToStaticMarkup(React.createElement('title', {}, title))}
                    
                    <link href="bootstrap-master/css/bootstrap.min.css" rel="stylesheet">
                    <link rel="stylesheet" href="font-awesome-4.6.3/css/font-awesome.min.css">
                </head>
                <body style="padding-top: 50px;">
                    <nav class="navbar navbar-default navbar-fixed-top">
                      <div class="container-fluid">
                        <div class="navbar-header">
                          <a class="navbar-brand" href="#">APS</a>
                        </div>

                        <div class="collapse navbar-collapse" style="text-align: center;" id="bs-example-navbar-collapse-1">
                          <ul class="nav navbar-nav" style="float: none; display: inline-block; vertical-align: top;">
                            <li class="active"><a href="why.html">Why Us?</a></li>
                            <li><a href="prices.html">Prices</a></li>
                            <li><a href="samples.html">Sample Papers</a></li>
                            <li><a href="order.html">Order a Paper</a></li>
                            <li><a href="faq.html">FAQ</a></li>
                            <li><a href="contact.html">Contact Us</a></li>
                            <li><a href="blog.html">Writing Blog</a></li>
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
                          </div><!-- /.navbar-collapse -->
                      </div><!-- /.container-fluid -->
                    </nav>
                
                
                    ${ReactDOMServer.renderToStaticMarkup(comp)}

                    <script src="jquery.min.js"></script>
                    <script src="bootstrap-master/js/bootstrap.min.js"></script>
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

