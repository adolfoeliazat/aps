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
            content: `
                AcademicPaperServed is an experienced custom writing service, consisting of expert writers to deliver written assignments, essays, research papers, term papers, theses, book reports, etc to English-speaking clients worldwide. Our team of professional writers focuses on the highest quality of all types of academic papers. Thesis writing is a challenge to many students. With our assistance it will not be a problem anymore!
                Our writers are versed in various fields of academic writing, so if you do not find a suitable category on this page, feel free to contact our Support Center to find out availability of writing help in your area of interest.
                We have access to most reliable and complete online libraries to make your research or essay unique.
                academicpaperserved team consists of expert academic writers providing you with free guidelines, helping with topic selection, proofreading, editing and formatting even if you want to get your essay done overnight! We guarantee premium quality writing with urgent projects.            
            `
        },
        
        whatWeOfferSection: {
            primary: [
                {glyph: 'envira', title: `Custom essay / research / thesis writing`},
                {glyph: 'rocket', title: `Plagiarism-free original papers written from scratch`},
                {glyph: 'bomb', title: `Proofreading and editing of written papers`},
                {glyph: 'book', title: `Free guidelines on successful essay topic selection and writing process`},
            ],
            secondary: [
                {glyph: '', title: `Custom essay / research paper / book report / term paper/ precis / sketch / poetry analysis / data collection / thesis writing / SWOT analysis / lab reports / dissertations / reviews / speeches / presentations / case studies / courseworks / homeworks / assignments / creative writing / blog writing / capstone project / grant proposal / lab reports`},
                {glyph: '', title: `Plagiarism-free original papers written from scratch`},
                {glyph: '', title: `Proofreading and editing of written papers`},
                {glyph: '', title: `Choosing sources for your paper, providing with annotated bibliography upon request`},
                {glyph: '', title: `Free guidelines on successful essay topic selection and writing process`},
                {glyph: '', title: `Individual Approach to Every Customer, no Repetitions, Free Consulting on the Paper Content`},
                {glyph: '', title: `Free Revisions till You are Completely Satisfied`},
                {glyph: '', title: `Meeting Your Deadline`},
                {glyph: '', title: `Security and Confidentiality`},
            ],
        },
        
        featuresSection: {
            primary: [
                {glyph: 'pencil', title: `No plagiarism!`},
                {glyph: 'star', title: `Only Premium Quality!`},
                {glyph: 'list', title: `Free title page / outline / list of references!`},
                {glyph: 'credit-card', title: `One-time and life-time discounts to returning customers!`},
                {glyph: 'diamond', title: `30-days money back guarantee!`},
                {glyph: 'life-saver', title: `24/7 support!`},
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

//crashForDebuggingSake_randomly()
//clog('OK')


function makeCustomerSite(def) {
    const root = `${__dirname}/../built/${def.dir}`
    sh.rm('-rf', root)
    sh.mkdir('-p', root)
    
    writePage('index', div(
        div('hello4'),
        div('world'),
    ))
    
    
    function writePage(name, comp) {
        fs.writeFileSync(`${root}/${name}.html`, ReactDOMServer.renderToStaticMarkup(comp))
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

