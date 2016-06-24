const askerName = 'Sailor'
relog(heyBackend_sayHelloToMe({askerName}))
heyBackend_changeYourStateTo({foo: 10, bar: 20})
relog('Its state is', heyBackend_whatsYourState())


await delay(3000)
relog('done')
