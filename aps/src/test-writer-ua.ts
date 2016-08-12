/*
 * APS
 * 
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

#pragma instrument-ui-rendering

import {TestCommon} from './test-common'
import static 'into-u/utils-client into-u/ui ./stuff'

GENERATED_SHIT = require('./generated-shit')

module.exports = function({sim}) {
    const drpc = getDebugRPC()
    
    const common = TestCommon({sim})
    
    return {
        'UA Writer :: Sign Up :: 1 cca87b63-eedd-407d-a681-814f128b1822': {
            async run() {
                const slowly = false
                
                if (slowly) { setTestSpeed('slow'); art.respectArtPauses = true }
                await art.resetTestDatabase({templateDB: 'test-template-ua-1', alsoRecreateTemplate: true})
                
                #hawait vovchok1()
                
                async function vovchok1() {
                    #hawait common.selectBrowser({$tag: '6152786a-7a29-41d0-bf96-8e35ac4e2773',
                        clientKind: 'writer', browserName: 'vovchok1',
                        navigationDescription: t('Marko Vovchok, an eager writer wannabe, comes to our site')})
                    
                    
//                    art.uiState({$tag: 'e44a44a7-fe84-42a6-ac80-a6aee4ac87ac', expected: '---generated-shit---'})
//                    
//                    // Action
//                    art.pushStepDescription(s{kind: 'navigation', long: t('Support admin clicks on "Heap" in top navbar')})
//                    #hawait testGlobal.controls['TopNavItem-admin-heap'].testClick()
//                    art.pushStepDescription(s{kind: 'state', long: t('Support admin sees a whole lot of unassigned support threads')})
//                    art.uiState({$tag: '18f3d2e5-6c4a-4ceb-bee2-7e677aa30816', expected: '---generated-shit---'})
                }
            }
        },
    }
}


