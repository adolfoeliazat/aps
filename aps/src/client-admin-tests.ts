/*
 * APS
 * 
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

import static 'into-u/utils-client into-u/ui ./stuff ./test-stuff'

module.exports = function({sim}) {
    const drpc = getDebugRPC()
        
    return {
        async 'UA Admin :: Misc :: 1 839c4909-e1d1-447a-9401-d1599d19598c'() {
            #hawait drpc({fun: 'danger_clearSentEmails'})
            #hawait drpc({fun: 'danger_resetAndUseTestDatabase'})
            // #hawait drpc({fun: 'danger_shitIntoDatabase'})
            
            sim.selectBrowser('todd')
            
            #hawait sim.navigate('dashboard.html')
            
            art.uiState({$tag: 'c70e18eb-516b-41cb-bf7a-bdf748595ad2', expected: {
                url: `http://aps-ua-writer.local:3022/sign-in.html`,
                pageHeader: `Вход`,
                inputs: { email: { value: `` }, password: { value: `` } },
                errorLabels: {},
                errorBanner: undefined,
                displayLabels: {} 
            }})                
            
            // Inputs
            #hawait testGlobal.inputs.email.setValue('todd@test.shit.ua')
            #hawait testGlobal.inputs.password.setValue('secret')
            // Action
            #hawait testGlobal.buttons.primary.click()
            #hawait art.shitSpinsForMax({$tag: '271f3603-1982-4804-b064-b718ee444160', max: 2000})
            #hawait art.uiStateAfterLiveStatusPolling({$tag: '866bef17-2783-40a5-860d-0d2f69966664', expected: {
                url: `http://aps-ua-writer.local:3022/dashboard.html`,
                pageHeader: `Панель`,
                inputs: {},
                errorLabels: {},
                errorBanner: undefined,
                displayLabels: {},
                pageData: 
                 { 'topNavItem.admin-my-tasks.title': `Мои задачи`,
                   'topNavItem.admin-heap.title': `Куча`,
                   } 
            }})
            
            // Action
//            #hawait testGlobal.topNavbarLinks['admin-heap'].click()
//            #hawait art.uiStateAfterLiveStatusPolling({$tag: '52167671-bd47-4997-9a9d-742951263fa4', expected: {
//
//            }})

        },
    }
    
}
