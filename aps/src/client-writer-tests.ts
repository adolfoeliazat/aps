/*
 * APS
 * 
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

import static 'into-u/utils-client into-u/ui ./stuff'

module.exports = function({sim}) {
    const drpc = getDebugRPC()
        
    return {
        async 'UA Writer :: Sign Up :: 1 b583c010-f383-4635-a826-3d2bb79f0806'() {
            // art.respectArtPauses = false
            
            #hawait drpc({fun: 'danger_clearSentEmails'})
            #hawait drpc({fun: 'danger_killUser', email: 'fred.red@test.shit.ua'})
            #hawait drpc({fun: 'danger_setUpTestUsers'})
            
            // ---------- Browser: fred ----------
            
            sim.selectBrowser('fred')
            
            #hawait drpc({fun: 'danger_fixNextGeneratedPassword', password: 'b34b80fb-ae50-4456-8557-399366fe45e4'})
            
            #hawait sim.navigate('dashboard.html')
            art.uiState({$tag: '20059334-7dff-4922-8bf5-ac07999d892d', expected: {
                url: `http://aps-ua-writer.local:3022/sign-in.html`,
                pageHeader: `Вход`,
                inputs: { email: { value: `` }, password: { value: `` } },
                errorLabels: {},
                errorBanner: undefined,
                displayLabels: {} 
            }})
            
            #hawait art.pausePoint({title: 'Before clicking "Create account" link', $tag: 'ee90e201-9429-4577-aea9-0277e66979ad'})
            #hawait testGlobal.links.createAccount.click()
            #hawait art.linkBlinksForMax({$tag: '67abf17a-ab93-4891-a844-54b6baa0c387', name: 'createAccount', max: 2000})
            art.uiState({$tag: 'b1a53c66-21db-42e5-8b0b-4d430b7b4ea6', expected: {
                url: `http://aps-ua-writer.local:3022/sign-up.html`,
                pageHeader: `Регистрация`,
                inputs: 
                 { email: { value: `` },
                   firstName: { value: `` },
                   lastName: { value: `` },
                   agreeTerms: { value: false } },
                errorLabels: {},
                errorBanner: undefined,
                displayLabels: {} 
            }})            
            #hawait art.pausePoint({title: 'After clicking "Create account" link', $tag: '8cf1b341-6f24-4c5b-89e8-aff163a505f5'})
            
            #hawait drpc({fun: 'danger_imposeNextRequestTimestamp', timestamp: '2016-07-03 13:21:36'})
            
            // Inputs
            #hawait testGlobal.inputs.email.setValue('fred.red@test.shit.ua')
            #hawait testGlobal.inputs.firstName.setValue('Фред')
            #hawait testGlobal.inputs.lastName.setValue('Ред')
            #hawait testGlobal.inputs.agreeTerms.setValue(true)
            
            // Action
            #hawait testGlobal.buttons.primary.click()
            #hawait art.shitSpinsForMax({$tag: '39df3f4b-5ca0-4929-bae7-ec1d3bd008ed', max: 2000})
            
            #hawait art.sentEmails({$tag: '024f202c-ee75-44ed-ac26-44154d4caf13', descr: 'Email with password', expected: [
                { to: `Фред Ред <fred.red@test.shit.ua>`,
                    subject: `Пароль для Writer UA`,
                    html: dedent(`
                        Привет, Фред!<br><br>
                        Вот твой пароль: b34b80fb-ae50-4456-8557-399366fe45e4
                        <br><br>
                        <a href="http://aps-ua-writer.local:3022/sign-in.html">http://aps-ua-writer.local:3022/sign-in.html</a>`) } 
            ]})
            art.uiState({$tag: '24ca0059-e2e9-4fc4-9056-ede17e586029', expected: {
                url: `http://aps-ua-writer.local:3022/sign-in.html`,
                pageHeader: `Вход`,
                inputs: { email: { value: `` }, password: { value: `` } },
                errorLabels: {},
                errorBanner: undefined,
                displayLabels: {} 
            }})            
            art.textSomewhere({$tag: 'bad7019b-a1d3-432c-a376-a872f5b27506', expected: 'Все круто. Теперь у тебя есть аккаунт. Пароль мы отправили письмом.'})

            #hawait art.pausePoint({title: 'Before entering password received by email', $tag: '51076622-8afd-45f5-9fda-a55b98380410'})
            // Inputs
            #hawait testGlobal.inputs.email.setValue('fred.red@test.shit.ua')
            #hawait testGlobal.inputs.password.setValue('b34b80fb-ae50-4456-8557-399366fe45e4')
            // Action
            #hawait testGlobal.buttons.primary.click()
            #hawait art.shitSpinsForMax({$tag: 'd880053c-0f24-46ec-8c47-c635e91d6a39', max: 2000})

            art.uiState({$tag: '4d88eed7-d800-4a00-bfea-6b011329eaf0', expected: {
                url: `http://aps-ua-writer.local:3022/profile.html`,
                pageHeader: `Профиль`,
                inputs: { phone: { value: `` } },
                errorLabels: {},
                errorBanner: undefined,
                displayLabels: {},
                pageData: 
                 { 'topNavItem.profile.title': `Профиль`,
                   'topNavItem.support.title': `Поддержка` } 
            }})                        
            art.textSomewhere({$tag: 'bad7019b-a1d3-432c-a376-a872f5b27506', expected: 'Фред'})
            
            art.noTextSomewhere({$tag: '4d0713f8-ccfb-4d05-b064-3987492852a5', unexpected: 'Мои заказы'})
            art.noTextSomewhere({$tag: 'a3e73a3e-8ed7-4a69-b748-e955ae4fd606', unexpected: 'Аукцион'})
            
            // Inputs
            #hawait testGlobal.inputs.phone.setValue('')
            // Action
            #hawait testGlobal.buttons.primary.click()
            #hawait art.shitSpinsForMax({$tag: 'fbe5bc76-cf5a-4ed4-90af-a815784cfd1e', max: 2000})
            art.uiState({$tag: '80db2840-cf3b-428e-8f7a-3a447f94d93a', expected: {
                url: `http://aps-ua-writer.local:3022/profile.html`,
                pageHeader: `Профиль`,
                inputs: { phone: { value: `` } },
                errorLabels: { phone: { title: `Телефон обязателен` } },
                errorBanner: `Пожалуйста, исправьте ошибки ниже`,
                displayLabels: {},
                pageData: 
                { 'topNavItem.profile.title': `Профиль`,
                  'topNavItem.support.title': `Поддержка` }
            }})

            // Inputs
            #hawait testGlobal.inputs.phone.setValue('adsfasdf')
            // Action
            #hawait testGlobal.buttons.primary.click()
            #hawait art.shitSpinsForMax({$tag: '2d6f5c02-1eae-49cb-9c5a-0509a4f29e05', max: 2000})
            art.uiState({$tag: '24d5e9b2-0dac-40d6-94e8-57d0cfe00c9b', expected: {
                url: `http://aps-ua-writer.local:3022/profile.html`,
                pageHeader: `Профиль`,
                inputs: { phone: { value: `adsfasdf` } },
                errorLabels: { phone: { title: `Странный телефон какой-то` } },
                errorBanner: `Пожалуйста, исправьте ошибки ниже`,
                displayLabels: {},
                pageData: 
                { 'topNavItem.profile.title': `Профиль`,
                  'topNavItem.support.title': `Поддержка` }
            }})
            
            #hawait drpc({fun: 'danger_imposeNextRequestTimestamp', timestamp: '2016-07-03 13:24:51'})
            // Inputs
            #hawait testGlobal.inputs.phone.setValue('123-45-67')
            // Action
            #hawait testGlobal.buttons.primary.click()
            #hawait art.shitSpinsForMax({$tag: 'e804da7e-6d1e-4fe4-a40e-e7697cb23622', max: 2000})
            
            art.uiState({$tag: '47a1c72b-4813-4dcd-a2fc-25ca3b739a92', expected: {
                url: `http://aps-ua-writer.local:3022/profile.html`,
                pageHeader: `Профиль`,
                inputs: {},
                errorLabels: {},
                errorBanner: undefined,
                displayLabels: 
                 { first_name: { content: `Фред` },
                   last_name: { content: `Ред` },
                   email: { content: `fred.red@test.shit.ua` },
                   phone: { content: `123-45-67` },
                   inserted_at: { content: `03/07/2016 16:21:36 (Киев)` },
                   profile_updated_at: { content: `03/07/2016 16:24:51 (Киев)` } },
                   pageData: 
                   { 'topNavItem.profile.title': `Профиль`,
                     'topNavItem.support.title': `Поддержка` }
            }})
            
            #hawait art.pausePoint({title: 'Before clicking "Support" link', $tag: 'c7d7eed5-df24-4db3-a361-9da4470c4bd1'})
            // Action
            #hawait testGlobal.links.support.click()
            #hawait art.linkBlinksForMax({$tag: 'eceeb886-f96e-4baa-a0c1-e75cc79d4e84', name: 'support', max: 2000})
            art.textSomewhere({$tag: '35738e20-16f0-4657-bdc1-60ba524d011b', expected: 'Запросов в поддержку не было. Чтобы добавить, нажми плюсик вверху.'})
            art.uiState({$tag: '0f630ccd-9936-4d27-ac1c-4d391a184e79', expected: {
                url: `http://aps-ua-writer.local:3022/support.html`,
                pageHeader: `Поддержка`,
                inputs: {},
                errorLabels: {},
                errorBanner: undefined,
                displayLabels: {},
                pageData: 
                { 'topNavItem.profile.title': `Профиль`,
                  'topNavItem.support.title': `Поддержка` }
            }})                
            
            #hawait art.pausePoint({title: 'Before clicking plus', $tag: '328b20bf-9fa3-4633-8ee3-fdd80d712bfb'})
            // Action
            #hawait testGlobal.buttons.plus.click()
            art.uiState({$tag: 'b990c804-6621-4b49-879e-57caffc7bcce', expected: {
                url: `http://aps-ua-writer.local:3022/support.html`,
                pageHeader: `Поддержка`,
                inputs: { topic: { value: `` }, message: { value: `` } },
                errorLabels: {},
                errorBanner: undefined,
                displayLabels: {},
                pageData: 
                { 'topNavItem.profile.title': `Профиль`,
                  'topNavItem.support.title': `Поддержка` }
            }})

            // Inputs
            #hawait testGlobal.inputs.topic.setValue('')
            #hawait testGlobal.inputs.message.setValue('')
            // Action
            #hawait testGlobal.buttons.primary.click()
            #hawait art.shitSpinsForMax({$tag: '40894d9d-cc5f-486c-a1c5-213283b754fe', max: 2000})
            art.uiState({$tag: '1c53f5c2-ed8b-4157-a4ef-400c1617f16d', expected: {
                url: `http://aps-ua-writer.local:3022/support.html`,
                pageHeader: `Поддержка`,
                inputs: { topic: { value: `` }, message: { value: `` } },
                errorLabels: 
                 { topic: { title: `Поле обязательно` },
                   message: { title: `Поле обязательно` } },
                errorBanner: `Пожалуйста, исправьте ошибки ниже`,
                displayLabels: {},
                pageData: 
                { 'topNavItem.profile.title': `Профиль`,
                  'topNavItem.support.title': `Поддержка` }
            }})
            
            #hawait drpc({fun: 'danger_imposeNextID', id: 312})
            #hawait drpc({fun: 'danger_imposeNextRequestTimestamp', timestamp: '2016-07-03 13:30:45'})

            // Inputs
            #hawait testGlobal.inputs.topic.setValue('Заапрувьте меня')
            #hawait testGlobal.inputs.message.setValue('И побыстрее давайте!')
            // Action
            #hawait testGlobal.buttons.primary.click()
            #hawait art.shitSpinsForMax({$tag: '08a79fdd-1e1e-48b4-8f80-f2b1695ee096', max: 2000})
            art.uiState({$tag: '848839fb-f059-4cc3-87bf-3875e6deff0f', expected: {
                url: `http://aps-ua-writer.local:3022/support.html?thread=312`,
                    pageHeader: `Запрос в поддержку № 312`,
                    inputs: {},
                    errorLabels: {},
                    errorBanner: undefined,
                    displayLabels: {},
                    pageData: 
                     { supportThreadMessages: 
                        [ { '$$type': `supportThreadMessage`,
                            from: `Фред Ред`,
                            to: `В рельсу`,
                            timestamp: `03/07/2016 16:30:45`,
                            message: `И побыстрее давайте!` } ],
                       'topNavItem.profile.title': `Профиль`,
                       'topNavItem.support.title': `Поддержка` } 
            }})

            // Action
            #hawait testGlobal.buttons.plus.click()
            art.uiState({$tag: '01cb87af-fe6c-42fe-a490-5943a5bb5d1d', expected: {
                url: `http://aps-ua-writer.local:3022/support.html?thread=312`,
                    pageHeader: `Запрос в поддержку № 312`,
                    inputs: { message: { value: `` } },
                    errorLabels: {},
                    errorBanner: undefined,
                    displayLabels: {},
                    pageData: 
                     { supportThreadMessages: 
                        [ { '$$type': `supportThreadMessage`,
                            from: `Фред Ред`,
                            to: `В рельсу`,
                            timestamp: `03/07/2016 16:30:45`,
                            message: `И побыстрее давайте!` } ],
                            'topNavItem.profile.title': `Профиль`,
                            'topNavItem.support.title': `Поддержка` } 
            }})
            
            // Inputs
            await testGlobal.inputs.message.setValue('')
            // Action
            #hawait testGlobal.buttons.primary.click()
            #hawait art.shitSpinsForMax({$tag: '644b634f-e8a3-4c4d-be61-69970a4082b0', max: 2000})
            art.uiState({$tag: '24f268a1-d56c-4f1f-a10f-4d637ff4e2b4', expected: {
                url: `http://aps-ua-writer.local:3022/support.html?thread=312`,
                    pageHeader: `Запрос в поддержку № 312`,
                    inputs: { message: { value: `` } },
                    errorLabels: { message: { title: `Поле обязательно` } },
                    errorBanner: `Пожалуйста, исправьте ошибки ниже`,
                    displayLabels: {},
                    pageData: 
                     { supportThreadMessages: 
                        [ { '$$type': `supportThreadMessage`,
                            from: `Фред Ред`,
                            to: `В рельсу`,
                            timestamp: `03/07/2016 16:30:45`,
                            message: `И побыстрее давайте!` } ],
                            'topNavItem.profile.title': `Профиль`,
                            'topNavItem.support.title': `Поддержка` } 
            }})
            
            #hawait drpc({fun: 'danger_imposeNextRequestTimestamp', timestamp: '2016-07-03 13:33:17'})
            
            // Inputs
            await testGlobal.inputs.message.setValue('Че за фигня? Где админы? Почему так долго все?')
            // Action
            #hawait testGlobal.buttons.primary.click()
            #hawait art.shitSpinsForMax({$tag: '0133f044-47fc-419a-8aed-93350d909fb5', max: 2000})
            art.uiState({$tag: 'de45a710-a428-4e2c-ac2f-e918850e4dd8', expected: {
                url: `http://aps-ua-writer.local:3022/support.html?thread=312`,
                    pageHeader: `Запрос в поддержку № 312`,
                    inputs: {},
                    errorLabels: {},
                    errorBanner: undefined,
                    displayLabels: {},
                    pageData: 
                     { supportThreadMessages: 
                        [ { '$$type': `supportThreadMessage`,
                            from: `Фред Ред`,
                            to: `В рельсу`,
                            timestamp: `03/07/2016 16:33:17`,
                            message: `Че за фигня? Где админы? Почему так долго все?` },
                          { '$$type': `supportThreadMessage`,
                            from: `Фред Ред`,
                            to: `В рельсу`,
                            timestamp: `03/07/2016 16:30:45`,
                            message: `И побыстрее давайте!` } ],
                            'topNavItem.profile.title': `Профиль`,
                            'topNavItem.support.title': `Поддержка` } 
            }})


            // ---------- Browser: todd ----------
            
            sim.selectBrowser('todd')
            #hawait sim.navigate('dashboard.html')
            #hawait art.pausePoint({title: 'Now Todd from support comes into play', $tag: '4dbe962a-9a7d-4d43-bcc2-7e1e2f8785a8'})
            
            art.uiState({$tag: '6cfa0fe3-e86b-4d25-a9fb-751faba69643', expected: {
                url: `http://aps-ua-writer.local:3022/sign-in.html`,
                pageHeader: `Вход`,
                inputs: { email: { value: `` }, password: { value: `` } },
                errorLabels: {},
                errorBanner: undefined,
                displayLabels: {},
                pageData: {} 
            }})                
            

            // Inputs
            #hawait testGlobal.inputs.email.setValue('todd@test.shit.ua')
            #hawait testGlobal.inputs.password.setValue('toddsecret')
            // Action
            #hawait testGlobal.buttons.primary.click()
            #hawait art.shitSpinsForMax({$tag: 'aea4767a-629e-4249-9687-ea0feccee7fd', max: 2000})
            #hawait art.uiStateAfterLiveStatusPolling({$tag: '5b27f321-4fc1-4507-8452-255178aaa493', expected: {
                url: `http://aps-ua-writer.local:3022/dashboard.html`,
                pageHeader: `Панель`,
                inputs: {},
                errorLabels: {},
                errorBanner: undefined,
                displayLabels: {},
                pageData: 
                 { 'topNavItem.admin-my-tasks.title': `Мои задачи`,
                   'topNavItem.admin-heap.title': `Куча`,
                   'topNavItem.admin-heap.badge': `1` } 
            }})
            // art.respectArtPauses = true; setTestSpeed('slow')
    //                #hawait art.pausePoint({title: `
    //                    Todd is registered as  admin user with "support" role,
    //                    top navigation bar reflects what he’s supposed to do`, $tag: '0d27f99c-d89b-44cf-a65c-ed65789d206d'})

            // Action
            art.preventRestoringURLAfterTest()
            #hawait testGlobal.topNavbarLinks['admin-heap'].click()
            #hawait art.linkBlinksForMax({$tag: '1a2c9122-6a9f-49fc-8565-659cd221eef0', kind: 'topNavbarLink', name: 'admin-heap', max: 2000})
            #hawait art.uiStateAfterLiveStatusPolling({$tag: '4f2775bf-bd29-48b0-baea-a730fdbe4d49', expected: {

            }})
            
            // @ctx test

            // ---------- Browser: fred ----------
            
            // sim.selectBrowser('fred')
            
            
            //art.boom('1fbfe070-6d05-4fdf-9bf1-f250cfb7089a')

            // @ctx test
            
            // @ctx templates
            // #hawait art.pausePoint({title: '', $tag: ''})
        },
    }
}
