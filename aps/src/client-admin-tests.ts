/*
 * APS
 * 
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

import static 'into-u/utils-client into-u/ui ./stuff'

GENERATED_SHIT = require('./generated-shit')

module.exports = function({sim}) {
    const drpc = getDebugRPC()
        
    return {
        'UA Admin :: Misc :: 1 839c4909-e1d1-447a-9401-d1599d19598c': {
            async run() {
                const runKind = 'all'
                // const runKind = 'first'
                // const runKind = 'last'
                const slowly = false
                
                if (slowly) { setTestSpeed('slow'); art.respectArtPauses = true }
                if (runKind === 'first' || runKind === 'all') {
                    await art.resetTestDatabase({templateDB: 'test-template-ua-1', alsoRecreateTemplate: true})
                } else {
                    await art.resetTestDatabase({templateDB: 'test-template-didi'})
                }
                
                if (runKind === 'first' || runKind === 'all') {
                    #hawait todd1()
                    #hawait kafka1()
                    #hawait todd2()
                }
                if (runKind === 'last' || runKind === 'all') {
                    #hawait luke1()
                    #hawait todd3()
                    #hawait kafka2()
                }
                
                if (runKind === 'first' || runKind === 'all') {
                    openDebugConsole({runFunction: 'captureTestDB'})
                }
                
                
                async function todd1() {
                    #hawait drpc({fun: 'danger_clearSentEmails'})
                    
                    #hawait selectBrowserAndSignIn({$tag: '2855b4f4-63f0-4325-ad8a-f36fdf4e2f5a', clientKind: 'writer', browserName: 'todd1', email: 'todd@test.shit.ua', pausePointTitle: 'Todd, a support admin, comes to his workplace...'})
                    art.uiState({$tag: '866bef17-2783-40a5-860d-0d2f69966664', expected: '---generated-shit---'})
                    
                    testGlobal.controls['TopNavItem-admin-heap'].testShowHand({testActionHandOpts: {pointingFrom: 'right', dleft: -15, dtop: 0}})
                    #hawait art.pausePoint({title: 'There is some unassigned work, let’s take a look...', $tag: '20f801f5-657b-4176-bd1a-fb78f5af1811'})
                    testGlobal.controls['TopNavItem-admin-heap'].testHideHand()
                    
                    // Action
                    #hawait testGlobal.controls['TopNavItem-admin-heap'].testClick()
                    art.uiState({$tag: '1c3a4a15-4bc4-46f6-afd9-d23433c6d839', expected: '---generated-shit---'})
                    #hawait art.pausePoint({title: 'A lot of stuff, let’s do some scrolling...', $tag: 'a86e6b75-0140-4347-a961-bf9886937806', locus: 'top-right'})
                    #hawait art.scroll({origY: 0, destY: 'bottom'})
                    #hawait art.pausePoint({title: 'Clicking "Show More" button at the bottom...', $tag: 'dcf633b6-0cd6-43bc-81f8-5920ff60a795', locus: 'top-right'})
                    
                    // Action
                    #hawait testGlobal.controls['button-showMore'].testClick({testActionHandOpts: {pointingFrom: 'top'}})
                    #hawait art.uiState({$tag: 'edb9fdc5-841b-4232-baa2-dfdc39d8d02d', expected: '---generated-shit---'})
                    
                    #hawait art.scroll({origY: 'current', destY: 'bottom'})
                    #hawait art.pausePoint({title: 'Now admin chooses a thread to take', $tag: '3045e8f4-3b18-4681-91e4-bfaa2961f40d', locus: 'top-right'})
                    #hawait art.scroll({origY: 'current', destY: 320})
                    
                    // Action
                    #hawait testGlobal.controls['takeAndReply-308'].testClick()
                    
                    art.uiState({$tag: '404c23b9-e496-41b5-a105-71ae5c44ed5f', expected: '---generated-shit---'})
                    
                    // /*killme*/ setTestSpeed('slow'); art.respectArtPauses = true
                    testGlobal.controls['TopNavItem-admin-heap'].testShowHand({testActionHandOpts: {pointingFrom: 'right', dleft: -15, dtop: 0}})
                    #hawait art.pausePoint({title: 'Amount of work in heap decreased, since we’ve just taken one piece from it', $tag: '3ebb103e-7f0a-4669-9a29-2cbb2bbed460'})
                    testGlobal.controls['TopNavItem-admin-heap'].testHideHand()
                    testGlobal.controls['TopNavItem-support'].testShowHand({testActionHandOpts: {pointingFrom: 'right', dleft: -15, dtop: 0}})
                    #hawait art.pausePoint({title: 'We have three new messages contained in one thread to deal with. This badge will be hanging there until we address it by replying.\nBut first let’s go back to heap and take one more task...', $tag: '348c92fd-d749-4fad-8c4e-eef3754548cf'})
                    testGlobal.controls['TopNavItem-support'].testHideHand()
                    
                    // Action
                    #hawait testGlobal.controls['TopNavItem-admin-heap'].testClick()
                    art.uiState({$tag: '8fc8247d-2b0a-492b-a495-ee782f56eeb4', expected: '---generated-shit---'})
                    // raise('boom')
                    
                    // Action
                    #hawait testGlobal.controls['takeAndReply-12'].testClick()
                    art.uiState({$tag: '9dde55d6-0488-4ec6-9443-a96370c7fd4b', expected: '---generated-shit---'})

                    // /*killme*/ setTestSpeed('slow'); art.respectArtPauses = true
                    testGlobal.controls['TopNavItem-support'].testShowHand({testActionHandOpts: {pointingFrom: 'right', dleft: -15, dtop: 0}})
                    #hawait art.pausePoint({title: 'Numbers changed again. Now we have to deal with 4 support messages contained in 2 threads.\nBy clicking on Support menu we’ll see what those threads are...', $tag: '182c9720-f9ed-4afe-8c46-5b45c482e9a9'})
                    testGlobal.controls['TopNavItem-support'].testHideHand()
                    
                    // Action
                    #hawait testGlobal.controls['TopNavItem-support'].testClick()
                    art.uiState({$tag: '0020d0bf-5a2c-4287-a4b6-f1eca4c36ca3', expected: '---generated-shit---'})
                    
                    testGlobal.controls['chunk-0.thread-0.newMessages.link-andMore'].testShowHand({testActionHandOpts: {pointingFrom: 'left', dleft: -4, dtop: 2}})
                    #hawait art.pausePoint({title: 'To keep the list succinct, maximum two (most recent) new messages are showed per thread.\nIn order to see everything, simply switch to a particular thread. One way of doing which is via this link...', $tag: '27ba7948-693a-4c93-a054-16a534090567'})
                    testGlobal.controls['chunk-0.thread-0.newMessages.link-andMore'].testHideHand()
                    
                    // Action
                    #hawait testGlobal.controls['chunk-0.thread-0.newMessages.link-andMore'].testClick()
                    art.uiState({$tag: 'b82ede90-f409-4dee-b1d6-e66b56e51152', expected: '---generated-shit---'})


                    // /*killme*/ setTestSpeed('slow'); art.respectArtPauses = true
                    #hawait art.pausePoint({title: 'Changing ordering to show old messages first...', $tag: '1e77736a-3498-4b5d-8b1e-94c0f7dd6c56'})
                    // Action
                    #hawait testGlobal.controls['Select-ordering'].testSetValue({value: 'asc', testActionHandOpts: {pointingFrom: 'left', dtop: 48}})
                    art.uiState({$tag: '73952bab-8024-4351-8656-0966860aa31b', expected: '---generated-shit---'})
                    
                    #hawait art.pausePoint({title: 'Changing it back to conveniently show new stuff first', $tag: '79a2d7fc-5509-41fc-b606-b82bdccd5d68'})
                    // Action
                    #hawait testGlobal.controls['Select-ordering'].testSetValue({value: 'desc', testActionHandOpts: {pointingFrom: 'left', dtop: 30}})
                    art.uiState({$tag: '1920f57c-db76-40ff-9f85-8ebdd8faf2f3', expected: '---generated-shit---'})
                    
                    // /*killme*/ setTestSpeed('slow'); art.respectArtPauses = true
                    #hawait art.pausePoint({title: 'Will respond...', $tag: '3687412d-c52a-4bdf-bc8e-1c588df5da22'})
                    // Action
                    #hawait testGlobal.controls['button-plus'].testClick()
                    art.uiState({$tag: 'bd47917a-b212-4d03-8910-5af36b2f7ebc', expected: '---generated-shit---'})
                    
                    // Inputs
                    #hawait testGlobal.controls['Input-message'].testSetValue({value: 'Та я понял, что ты писатель... В чем дело-то?'})
                    // Action
                    #hawait drpc({fun: 'danger_imposeNextRequestTimestamp', timestamp: '2016/07/23 13:56:26'})
                    #hawait testGlobal.controls['button-primary'].testClick()
                    art.uiState({$tag: '0bb446ca-4e6e-4bcf-b9c9-687f1f3a2f29', expected: '---generated-shit---'})
                    
                    testGlobal.controls['TopNavItem-support'].testShowHand({testActionHandOpts: {pointingFrom: 'right', dleft: -15, dtop: 0}})
                    #hawait art.pausePoint({title: 'Admin replied, so "New" labels faded away and number of support items to address decreased', $tag: 'c5f3e1ee-0621-4d66-beb9-287847a95444'})
                    testGlobal.controls['TopNavItem-support'].testHideHand()
                }
                
                async function kafka1() {
                    #hawait selectBrowserAndSignIn({$tag: '24cb9937-d5eb-4b10-a8b0-19428e9db3ef', clientKind: 'writer', browserName: 'kafka1', email: 'kafka@test.shit.ua', pausePointTitle: 'Another user, Franz Kafka, who is a writer, comes into play...'})
                    art.uiState({$tag: '2fcf578d-3b0d-4f70-b3a1-7d4d892ecd91', expected: '---generated-shit---'})
                    
                    // /*killme*/ setTestSpeed('slow'); art.respectArtPauses = true
                    testGlobal.controls['TopNavItem-support'].testShowHand({testActionHandOpts: {pointingFrom: 'right', dleft: -15, dtop: 0}})
                    #hawait art.pausePoint({title: 'We have an unread message', $tag: 'ea6d6e4a-c4d6-489c-97c9-9adb34b284d7'})
                    testGlobal.controls['TopNavItem-support'].testHideHand()
                    
                    // Action
                    #hawait testGlobal.controls['TopNavItem-support'].testClick()
                    art.uiState({$tag: 'e03093b1-3834-45e9-9f7c-724536571ecc', expected: '---generated-shit---'})
                    
                    // /*killme*/ setTestSpeed('slow'); art.respectArtPauses = true
                    testGlobal.controls['chunk-0.thread-0.oldMessages.link-andMore'].testShowHand({testActionHandOpts: {pointingFrom: 'left', dleft: -5, dtop: 2}})
                    #hawait art.pausePoint({title: 'Let’s look at whole thread...', $tag: 'c36ed08f-1a1d-4307-b53b-bb93d503d535'})
                    testGlobal.controls['chunk-0.thread-0.oldMessages.link-andMore'].testHideHand()
                    
                    // Action
                    #hawait testGlobal.controls['chunk-0.thread-0.oldMessages.link-andMore'].testClick()
                    art.uiState({$tag: '627276cd-3bfe-4932-9e2f-e2845f6015f5', expected: '---generated-shit---'})
                    
                    #hawait art.pausePoint({title: 'Will reply...', $tag: 'ae5e858c-baea-4321-a024-4f07629c19c1'})

                    // Action
                    #hawait testGlobal.controls['button-plus'].testClick()
                    art.uiState({$tag: '3e196890-8de2-4003-8dc7-c34fbec270b2', expected: '---generated-shit---'})

                    // Inputs
                    #hawait testGlobal.controls['Input-message'].testSetValue({value: 'Ни в чем. Просто пописать. Я ж писатель, ага.'})
                    // Action
                    #hawait drpc({fun: 'danger_imposeNextRequestTimestamp', timestamp: '2016/07/23 14:08:11'})
                    #hawait testGlobal.controls['button-primary'].testClick()
                    art.uiState({$tag: '2afc55df-9079-4112-a7c0-da91803923c6', expected: '---generated-shit---'})
                }
                
                async function todd2() {
                    #hawait selectBrowserAndSignIn({$tag: 'dbfba9fb-790e-4598-a04e-9b2b6041c1bc', clientKind: 'writer', browserName: 'todd2', email: 'todd@test.shit.ua', pausePointTitle: 'Todd comes back to check stuff...'})
                    art.uiState({$tag: 'b8690567-c212-4877-aa44-8e45313187da', expected: '---generated-shit---'})
                    
                    testGlobal.controls['TopNavItem-support'].testShowHand({"testActionHandOpts":{"pointingFrom":"right","dleft":-12,"dtop":1}})
                    #hawait art.pausePoint({title: 'Two messages in two threads.\nOne was just added by Kafka. Second is old one we took but didnt’t bother to answer.', $tag: '27d35540-e345-4bd7-8c32-61e57b82c252'})
                    testGlobal.controls['TopNavItem-support'].testHideHand()

                    // Action
                    #hawait testGlobal.controls['TopNavItem-support'].testClick()
                    art.uiState({$tag: 'c2c7686c-7658-475a-9068-7ad00e47e5d3', expected: '---generated-shit---'})
                    
                    // /*killme*/ setTestSpeed('slow'); art.respectArtPauses = true
                    #hawait art.pausePoint({title: 'First let’s calm down Luke...', $tag: 'e51fe87e-f66b-4411-8028-a166d6c7e3d6'})
                    // Action
                    #hawait testGlobal.controls['chunk-0.thread-1.link-topic'].testClick()
                    art.uiState({$tag: '7010e87f-efb6-46cd-ab36-0c8f8537f9ea', expected: '---generated-shit---'})
                    // #hawait art.pausePoint({title: 'TODO: Describe situation', $tag: '9c5e6974-bd3f-4278-ad9f-776cfccd040d'})

                    // #hawait art.pausePoint({title: 'TODO: Describe action', $tag: '599e8c7c-30a9-45b4-a22c-c60ae7a4a21b'})
                    // Action
                    #hawait testGlobal.controls['button-plus'].testClick()
                    art.uiState({$tag: '116f0222-746e-4c95-a81d-d3b88325c2a3', expected: '---generated-shit---'})
                    // #hawait art.pausePoint({title: 'TODO: Describe situation', $tag: '36751d38-a5bf-4d68-93ba-8ac1e794e686'})


                    // Inputs
                    #hawait testGlobal.controls['Input-message'].testSetValue({value: 'Take it easy, Luke. We hear your pain and are working hard to find a solution to your problem.\n\n(In other words, fuck you :))'})
                    // Action
                    // #hawait art.pausePoint({title: 'TODO: Describe action', $tag: '8ba7f289-a636-43d6-818e-59ebc6ec4f5c'})
                    #hawait drpc({fun: 'danger_imposeNextRequestTimestamp', timestamp: '2016/07/27 15:52:46'})
                    #hawait testGlobal.controls['button-primary'].testClick()
                    art.uiState({$tag: '81d0cc52-74e1-4eb8-921b-e72969b577eb', expected: '---generated-shit---'})
                    // #hawait art.pausePoint({title: 'TODO: Describe situation', $tag: '58d87ddc-5a4d-43b2-a52d-864ab5679b5a'})

                    // Action
                    // #hawait art.pausePoint({title: 'TODO: Describe action', $tag: '4e9bd6bf-3026-4212-bebb-017edeab476d'})
                    #hawait drpc({fun: 'danger_imposeNextRequestTimestamp', timestamp: '2016/07/27 15:55:43'})
                    #hawait testGlobal.controls['TopNavItem-support'].testClick()
                    art.uiState({$tag: '907d4ca6-763a-455f-90fa-e42e28b9b906', expected: '---generated-shit---'})
                    // #hawait art.pausePoint({title: 'TODO: Describe situation', $tag: '4bfc7ec4-5464-4f38-9fc8-74a67dd6562d'})

                    // Action
                    // #hawait art.pausePoint({title: 'TODO: Describe action', $tag: 'ba4cacfd-bab8-4851-995c-a28e4d5113a5'})
                    #hawait drpc({fun: 'danger_imposeNextRequestTimestamp', timestamp: '2016/07/27 15:57:57'})
                    #hawait testGlobal.controls['chunk-0.thread-0.link-topic'].testClick()
                    art.uiState({$tag: '684e0bd4-57fc-4f61-a545-0e04c14e67de', expected: '---generated-shit---'})
                    // #hawait art.pausePoint({title: 'TODO: Describe situation', $tag: '3e980295-4582-4efe-86d0-8ff79c64e18c'})

                    // Action
                    // #hawait art.pausePoint({title: 'TODO: Describe action', $tag: '34c7086c-bfdf-41a7-902f-477e75e9ad44'})
                    #hawait drpc({fun: 'danger_imposeNextRequestTimestamp', timestamp: '2016/07/27 15:59:47'})
                    #hawait testGlobal.controls['button-plus'].testClick()
                    art.uiState({$tag: 'd51ea6a7-dac7-49f4-88d4-731a77d17b61', expected: '---generated-shit---'})
                    // #hawait art.pausePoint({title: 'TODO: Describe situation', $tag: 'fc3ce032-7279-46b8-b123-0253c5f5ebc6'})

                    // Inputs
                    #hawait testGlobal.controls['Input-message'].testSetValue({value: 'Ладно, тогда пиши себе. Я отвечать не буду. Но если, сцуко, слишком сильно наспамишь, то будем тебя банить, учти.'})
                    // Action
                    // #hawait art.pausePoint({title: 'TODO: Describe action', $tag: '115ce0cb-63e8-45d9-a128-986780c4c181'})
                    #hawait drpc({fun: 'danger_imposeNextRequestTimestamp', timestamp: '2016/07/27 16:02:27'})
                    #hawait testGlobal.controls['button-primary'].testClick()
                    art.uiState({$tag: '08e9ecdd-0424-45f0-b39a-644c4621c4af', expected: '---generated-shit---'})
                    // #hawait art.pausePoint({title: 'TODO: Describe situation', $tag: 'bfacb25b-1acd-40c0-856b-c0162647568c'})

                }
                
                async function luke1() {
                    #hawait selectBrowserAndSignIn({$tag: '10d399b1-6ac3-43dd-b21c-22f40fd11c48', clientKind: 'customer', browserName: 'luke1', email: 'luke@test.shit.ua', pausePointTitle: 'Now it’s Luke’s turn...'})
                    art.uiState({$tag: '0f95a4f1-b8c5-4e33-b1dd-81204d94f2d8', expected: '---generated-shit---'})
                    
                    // Action
                    // #hawait art.pausePoint({title: 'TODO: Describe action', $tag: '8089a90c-16e6-40ad-a668-ec4527614efd'})
                    #hawait drpc({fun: 'danger_imposeNextRequestTimestamp', timestamp: '2016/07/27 19:16:05'})
                    #hawait testGlobal.controls['TopNavItem-support'].testClick()
                    art.uiState({$tag: '10b64318-d180-4a84-9b2f-0a37e61b3d4b', expected: '---generated-shit---'})
                    // #hawait art.pausePoint({title: 'TODO: Describe situation', $tag: '7b4d2b2e-202b-4188-afa8-a8136d5f2bd6'})

                    // Action
                    // #hawait art.pausePoint({title: 'TODO: Describe action', $tag: '5aa09ff4-7e41-4fab-bc4f-bf3a1f3cd7e2'})
                    #hawait drpc({fun: 'danger_imposeNextRequestTimestamp', timestamp: '2016/07/27 19:45:25'})
                    #hawait testGlobal.controls['chunk-i000.thread-i000.link-topic'].testClick()
                    art.uiState({$tag: 'c0950b79-938e-4bc0-b133-42d8a27d005f', expected: '---generated-shit---'})
                    // #hawait art.pausePoint({title: 'TODO: Describe situation', $tag: 'd6984756-b1ae-447d-bff2-08be7bf99d60'})

                    // Action
                    // #hawait art.pausePoint({title: 'TODO: Describe action', $tag: '5e57ddc5-f3ce-48e4-aff8-d319b806c95d'})
                    #hawait drpc({fun: 'danger_imposeNextRequestTimestamp', timestamp: '2016/07/27 21:41:50'})
                    #hawait testGlobal.controls['button-edit'].testClick()
                    art.uiState({$tag: '4c776fc8-f555-47e2-a7e7-3b4a5216e8a6', expected: '---generated-shit---'})
                    // #hawait art.pausePoint({title: 'TODO: Describe situation', $tag: '49577eb3-936c-43e3-a4c4-7ed6a54a7ff9'})

                    // Inputs
                    #hawait testGlobal.controls['Select-status'].testSetValue({value: 'resolved'})
                    // Action
                    #hawait art.pausePoint({title: 'Luke discovers he’s fucked and submissively decides to mark the request as resolved', $tag: '4fabecfa-27a8-4b45-a745-ceac79c74464'})
                    #hawait drpc({fun: 'danger_imposeNextRequestTimestamp', timestamp: '2016/07/27 21:42:54'})
                    #hawait testGlobal.controls['button-primary'].testClick()
                    art.uiState({$tag: '99798601-7dca-4e9c-8136-5052756c685e', expected: '---generated-shit---'})
                    // #hawait art.pausePoint({title: 'TODO: Describe situation', $tag: '010ed3fa-bbc9-4e45-922e-c76de43bcbe2'})

                }
                
                async function todd3() {
                    // /*killme*/ setTestSpeed('slow'); art.respectArtPauses = true
                    #hawait selectBrowserAndSignIn({$tag: 'f118942e-2bef-4121-a27c-fe4f98eadcd4', clientKind: 'writer', browserName: 'todd3', email: 'todd@test.shit.ua', pausePointTitle: 'Todd Supportod should now see that Luke resolved his support request...'})
                    art.uiState({$tag: '8246a80c-8a31-4f80-bba0-b404c26a4c94', expected: '---generated-shit---'})
                    
                    // /*killme*/ setTestSpeed('slow'); art.respectArtPauses = true
                    testGlobal.controls['TopNavItem-support'].testShowHand({"testActionHandOpts":{"pointingFrom":"right","dleft":-14,"dtop":0}})
                    #hawait art.pausePoint({title: 'No badges here because all messages were addressed, either resolved or replied to', $tag: '02ee56a4-66ee-46e1-b5f1-aead1300d5bd'})
                    testGlobal.controls['TopNavItem-support'].testHideHand()

                    // Action
                    // #hawait art.pausePoint({title: 'TODO: Describe action', $tag: '7cf28c38-0748-4b2e-8ec2-c74db0de70e8'})
                    #hawait drpc({fun: 'danger_imposeNextRequestTimestamp', timestamp: '2016/07/28 16:20:31'})
                    #hawait testGlobal.controls['TopNavItem-support'].testClick()
                    art.uiState({$tag: 'fcc1f0b2-63b8-4b57-bf11-a27a1aa65415', expected: '---generated-shit---'})
                    // #hawait art.pausePoint({title: 'TODO: Describe situation', $tag: '5dc62ff5-4881-4538-9616-51be5200f4ee'})

                }
                
                async function kafka2() {
                    #hawait selectBrowserAndSignIn({$tag: 'c389d860-b30c-438e-bd79-c5223fffd794', clientKind: 'writer', browserName: 'kafka2', email: 'kafka@test.shit.ua', pausePointTitle: 'Kafka decides to introduce some spamming...'})
                    art.uiState({$tag: '7de769ba-dc47-4f82-acb8-84dd7bd18567', expected: '---generated-shit---'})
                    
                    // Action
                    // #hawait art.pausePoint({title: 'TODO: Describe action', $tag: 'c8ff4699-58c8-400d-a9f3-68e20a61ae93'})
                    #hawait drpc({fun: 'danger_imposeNextRequestTimestamp', timestamp: '2016/07/29 15:44:39'})
                    #hawait testGlobal.controls['TopNavItem-support'].testClick()
                    art.uiState({$tag: '7ffac211-0ffd-45c7-983d-a7da73a0824e', expected: '---generated-shit---'})
                    // #hawait art.pausePoint({title: 'TODO: Describe situation', $tag: '7151ff9e-5eab-4ca0-b472-cc6a8f910c96'})

                    // Action
                    // #hawait art.pausePoint({title: 'TODO: Describe action', $tag: '6bf785d2-fc23-4212-931e-9f0e30d3f076'})
                    #hawait drpc({fun: 'danger_imposeNextRequestTimestamp', timestamp: '2016/07/29 16:40:44'})
                    #hawait testGlobal.controls['chunk-i000.thread-i000.link-topic'].testClick()
                    art.uiState({$tag: 'c4fe7232-a724-4251-9ee0-ddd33ed7d0ba', expected: '---generated-shit---'})
                    // #hawait art.pausePoint({title: 'TODO: Describe situation', $tag: '0abc8d04-3ea6-450f-9800-34df01418d2f'})

                    // Action
                    // #hawait art.pausePoint({title: 'TODO: Describe action', $tag: '5a7399f9-9bd9-472a-8159-14faf4b72d8a'})
                    #hawait drpc({fun: 'danger_imposeNextRequestTimestamp', timestamp: '2016/07/29 16:51:36'})
                    #hawait testGlobal.controls['button-plus'].testClick()
                    art.uiState({$tag: 'b668806f-2f5b-4594-a364-49494b60cc7e', expected: '---generated-shit---'})
                    // #hawait art.pausePoint({title: 'TODO: Describe situation', $tag: 'c064ab71-4e8c-4072-800c-6f120a33554d'})

                    // Inputs
                    #hawait testGlobal.controls['Input-message'].testSetValue({value: 'Тупо спамлю 1'})
                    // Action
                    // #hawait art.pausePoint({title: 'TODO: Describe action', $tag: '5ad115fb-9574-47c1-b275-056c8d696799'})
                    #hawait drpc({fun: 'danger_imposeNextRequestTimestamp', timestamp: '2016/07/29 16:52:40'})
                    #hawait testGlobal.controls['button-primary'].testClick()
                    art.uiState({$tag: '2ee05f7a-7384-4d7f-b9b0-60ceef5e240b', expected: '---generated-shit---'})
                    // #hawait art.pausePoint({title: 'TODO: Describe situation', $tag: '96f1236d-23cd-4698-b8c3-0a80dc142860'})

                    // Action
                    // #hawait art.pausePoint({title: 'TODO: Describe action', $tag: 'c45122ec-99c9-480e-960c-764afd9c347d'})
                    #hawait drpc({fun: 'danger_imposeNextRequestTimestamp', timestamp: '2016/07/29 17:49:09'})
                    #hawait testGlobal.controls['button-plus'].testClick()
                    art.uiState({$tag: 'f6093fda-6e89-4c9d-8a6f-c63fe09c85cb', expected: '---generated-shit---'})
                    // #hawait art.pausePoint({title: 'TODO: Describe situation', $tag: '41540466-20e7-484c-87cf-6dc862d04105'})

                    // Inputs
                    #hawait testGlobal.controls['Input-message'].testSetValue({value: 'Тупо спамлю 2'})
                    // Action
                    // #hawait art.pausePoint({title: 'TODO: Describe action', $tag: 'a550629d-2762-42bf-9d3e-495faae2a91c'})
                    #hawait drpc({fun: 'danger_imposeNextRequestTimestamp', timestamp: '2016/07/29 17:50:55'})
                    #hawait testGlobal.controls['button-primary'].testClick()
                    art.uiState({$tag: 'c186f51e-f387-400b-95e0-3e41b3ec73ab', expected: '---generated-shit---'})
                    // #hawait art.pausePoint({title: 'TODO: Describe situation', $tag: '1895952e-ed3c-4577-a339-cc7c4ef5a74d'})

                    art.actionPlaceholder({$tag: 'ce417c62-f167-4a0d-aecc-8193057b138c'})
                }
            },
        }
    }
    
    
    async function selectBrowserAndSignIn({$tag, pausePointTitle, browserName, email, clientKind}) {
        CLIENT_KIND = clientKind
        sim.selectBrowser(browserName)
        #hawait sim.navigate('dashboard.html')
        
        if (pausePointTitle) {
            #hawait art.pausePoint({title: pausePointTitle, theme: 'blue', $tag})
        }
        
        if (clientKind === 'writer') {
            art.uiState({$tag: 'c70e18eb-516b-41cb-bf7a-bdf748595ad2', expected: {
                'Input-email': ``,
                  'Input-email.shame': `Input-email`,
                  'Input-password': ``,
                  'Input-password.shame': `Input-password`,
                  'button-primary.shame': `button-primary`,
                  'button-primary.title': `Войти`,
                  'link-createAccount.title': `Срочно создать!`,
                  'pageHeader.title': `Вход`,
                  'topNavLeft.TopNavItem-i000.shame': `TopNavItem-why`,
                  'topNavLeft.TopNavItem-i000.title': `Почему мы?`,
                  'topNavLeft.TopNavItem-i001.shame': `TopNavItem-prices`,
                  'topNavLeft.TopNavItem-i001.title': `Цены`,
                  'topNavLeft.TopNavItem-i002.shame': `TopNavItem-faq`,
                  'topNavLeft.TopNavItem-i002.title': `ЧаВо`,
                  'topNavRight.TopNavItem-i000.active': true,
                  'topNavRight.TopNavItem-i000.shame': `TopNavItem-sign-in`,
                  'topNavRight.TopNavItem-i000.title': `Вход`,
                  url: `http://aps-ua-writer.local:3022/sign-in.html`
            }})                
        } else if (clientKind === 'customer') {
            art.uiState({$tag: '0bd97815-b9b1-4900-80b1-e6f683c41926', expected: {
                 'Input-email': ``,
                  'Input-password': ``,
                  'TopNavItem-blog': { title: `Блог` },
                  'TopNavItem-contact': { title: `Связь` },
                  'TopNavItem-faq': { title: `ЧаВо` },
                  'TopNavItem-prices': { title: `Цены` },
                  'TopNavItem-samples': { title: `Примеры` },
                  'TopNavItem-sign-in': { active: true, title: `Вход` },
                  'TopNavItem-why': { title: `Почему мы?` },
                  'button-primary': { title: `Войти` },
                  'link-createAccount': { title: `Срочно создать!` },
                  pageHeader: `Вход`,
                  url: `http://aps-ua-writer.local:3022/sign-in.html`
            }})                
        } else {
            raise('WTF is the clientKind')
        }
        
        // Inputs
        #hawait testGlobal.controls['Input-email'].testSetValue({value: email})
        #hawait testGlobal.controls['Input-password'].testSetValue({value: 'secret'})
        // Action
        #hawait testGlobal.controls['button-primary'].testClick()
    }
    
}


// /*killme*/ setTestSpeed('slow'); art.respectArtPauses = true

