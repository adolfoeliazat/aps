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
            #hawait drpc({fun: 'danger_setUpTestUsers'})
            #hawait shitIntoDatabase()
            
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
                   'topNavItem.admin-heap.title': `Куча` } 
            }})
            
//            // Action
            #hawait testGlobal.topNavbarLinks['admin-heap'].click()
            #hawait art.uiStateAfterLiveStatusPolling({$tag: '52167671-bd47-4997-9a9d-742951263fa4', expected: {

            }})

        },
    }
    
    
    async function shitIntoDatabase() {
        await drpc({fun: 'danger_insert', table: 'support_threads', rows: [
            {"id":1,"inserted_at":"2014-01-13 11:12:54","updated_at":"2014-01-13 11:12:54","supportee_id":204,"topic":"Из какого они ведомства?"},
            {"id":2,"inserted_at":"2014-01-15 22:35:57","updated_at":"2014-01-15 22:35:57","supportee_id":204,"topic":"Он так и решил сделать"},
            {"id":3,"inserted_at":"2014-01-24 08:23:31","updated_at":"2014-01-24 08:23:31","supportee_id":205,"topic":"Те покачали головой"},
            {"id":4,"inserted_at":"2014-04-06 05:52:58","updated_at":"2014-04-06 05:52:58","supportee_id":304,"topic":"Где же тут могут быть ошибки?"},
            {"id":5,"inserted_at":"2014-06-15 13:01:56","updated_at":"2014-06-15 13:01:56","supportee_id":205,"topic":"В рубахе идти к инспектору!"},
            {"id":6,"inserted_at":"2014-07-13 20:15:21","updated_at":"2014-07-13 20:15:21","supportee_id":303,"topic":"Кто же эти люди?"},
            {"id":7,"inserted_at":"2014-07-21 03:04:04","updated_at":"2014-07-21 03:04:04","supportee_id":203,"topic":"Вас вызывают к инспектору!"},
            {"id":8,"inserted_at":"2014-08-02 10:02:43","updated_at":"2014-08-02 10:02:43","supportee_id":300,"topic":"Не разрешено, – сказал высокий"},
            {"id":9,"inserted_at":"2014-09-04 22:07:31","updated_at":"2014-09-04 22:07:31","supportee_id":300,"topic":"Тут ошибок не бывает"},
            {"id":10,"inserted_at":"2014-10-01 23:57:11","updated_at":"2014-10-01 23:57:11","supportee_id":305,"topic":"Мы не уполномочены давать объяснения"},
            {"id":11,"inserted_at":"2014-10-10 09:36:47","updated_at":"2014-10-10 09:36:47","supportee_id":204,"topic":"Идите в свою комнату и ждите"},
            {"id":12,"inserted_at":"2014-11-01 03:50:48","updated_at":"2014-11-01 03:50:48","supportee_id":203,"topic":"О чем они говорят?"},
            {"id":13,"inserted_at":"2014-11-17 13:52:48","updated_at":"2014-11-17 13:52:48","supportee_id":304,"topic":"Наконец-то!"},
            {"id":14,"inserted_at":"2014-12-09 14:23:13","updated_at":"2014-12-09 14:23:13","supportee_id":202,"topic":"Он и вас прикажет высечь, и нас тоже!"},
            {"id":15,"inserted_at":"2014-12-16 16:53:06","updated_at":"2014-12-16 16:53:06","supportee_id":301,"topic":"Такого случая еще не бывало"},
            {"id":16,"inserted_at":"2015-01-13 02:55:39","updated_at":"2015-01-13 02:55:39","supportee_id":204,"topic":"Но пока что он еще свободен"},
            {"id":17,"inserted_at":"2015-01-18 02:48:22","updated_at":"2015-01-18 02:48:22","supportee_id":203,"topic":"А я иначе и не думал"},
            {"id":18,"inserted_at":"2015-01-19 02:15:13","updated_at":"2015-01-19 02:15:13","supportee_id":300,"topic":"Ведь вы арестованы"},
            {"id":19,"inserted_at":"2015-01-22 22:45:20","updated_at":"2015-01-22 22:45:20","supportee_id":201,"topic":"Почему она не вошла?"},
            {"id":20,"inserted_at":"2015-01-25 09:40:19","updated_at":"2015-01-25 09:40:19","supportee_id":301,"topic":"Ничего не поделаешь!"},
            {"id":21,"inserted_at":"2015-02-05 03:36:54","updated_at":"2015-02-05 03:36:54","supportee_id":203,"topic":"Да кто вы такой?"},
            {"id":22,"inserted_at":"2015-02-05 17:09:20","updated_at":"2015-02-05 17:09:20","supportee_id":204,"topic":"Что вам, наконец, нужно?"},
            {"id":23,"inserted_at":"2015-05-04 14:21:29","updated_at":"2015-05-04 14:21:29","supportee_id":201,"topic":"Разве это так делается?"},
            {"id":24,"inserted_at":"2015-05-22 19:23:09","updated_at":"2015-05-22 19:23:09","supportee_id":201,"topic":"Хорошо, я не буду звонить!"},
            {"id":25,"inserted_at":"2015-07-06 22:44:41","updated_at":"2015-07-06 22:44:41","supportee_id":301,"topic":"Ты кто такой?"},
            {"id":26,"inserted_at":"2015-07-16 16:39:40","updated_at":"2015-07-16 16:39:40","supportee_id":305,"topic":"Почему особенно такие, как сегодня?"},
            {"id":27,"inserted_at":"2015-07-22 23:44:29","updated_at":"2015-07-22 23:44:29","supportee_id":202,"topic":"Вы чиновники?"},
            {"id":28,"inserted_at":"2015-08-25 06:50:38","updated_at":"2015-08-25 06:50:38","supportee_id":305,"topic":"Надо было прекратить это зрелище"},
            {"id":29,"inserted_at":"2015-09-02 15:04:55","updated_at":"2015-09-02 15:04:55","supportee_id":303,"topic":"Господи, твоя воля!"},
            {"id":30,"inserted_at":"2015-09-13 00:40:17","updated_at":"2015-09-13 00:40:17","supportee_id":203,"topic":"Мне можно сесть?"},
            {"id":31,"inserted_at":"2015-09-17 20:13:18","updated_at":"2015-09-17 20:13:18","supportee_id":303,"topic":"Можно мне позвонить ему?"},
            {"id":32,"inserted_at":"2015-11-03 15:47:34","updated_at":"2015-11-03 15:47:34","supportee_id":304,"topic":"Пустите меня черт побери!"},
            {"id":33,"inserted_at":"2015-11-07 03:56:54","updated_at":"2015-11-07 03:56:54","supportee_id":300,"topic":"Вот еще новости!"},
            {"id":34,"inserted_at":"2015-11-24 16:36:08","updated_at":"2015-11-24 16:36:08","supportee_id":301,"topic":"Да какое нам до них дело!"},
            {"id":35,"inserted_at":"2015-12-12 15:17:29","updated_at":"2015-12-12 15:17:29","supportee_id":203,"topic":"Не очень?"},
            {"id":36,"inserted_at":"2016-01-13 20:14:07","updated_at":"2016-01-13 20:14:07","supportee_id":302,"topic":"Да тут камни возопят!"},
            {"id":37,"inserted_at":"2016-02-17 09:01:47","updated_at":"2016-02-17 09:01:47","supportee_id":300,"topic":"Какой смысл?"},
            {"id":38,"inserted_at":"2016-04-01 21:46:40","updated_at":"2016-04-01 21:46:40","supportee_id":300,"topic":"Входите же!"},
            {"id":39,"inserted_at":"2016-05-09 01:47:41","updated_at":"2016-05-09 01:47:41","supportee_id":203,"topic":"Нужен черный сюртук, – сказали они"},
            {"id":40,"inserted_at":"2016-07-02 14:14:30","updated_at":"2016-07-02 14:14:30","supportee_id":303,"topic":"Человек, который, вероятно, моложе его!"},
            {"id":41,"inserted_at":"2016-07-04 13:55:51","updated_at":"2016-07-04 13:55:51","supportee_id":205,"topic":"Таков закон"},
            {"id":42,"inserted_at":"2016-07-20 13:52:19","updated_at":"2016-07-20 13:52:19","supportee_id":305,"topic":"Не привести ли в оправдание истинную причину?"},
            {"id":43,"inserted_at":"2016-08-13 07:15:29","updated_at":"2016-08-13 07:15:29","supportee_id":300,"topic":"Какое ведомство ведет дело?"},
            {"id":44,"inserted_at":"2016-09-08 01:11:39","updated_at":"2016-09-08 01:11:39","supportee_id":205,"topic":"То есть как – арестован?"},
            {"id":45,"inserted_at":"2016-09-15 12:02:06","updated_at":"2016-09-15 12:02:06","supportee_id":305,"topic":"Право, вы ведете себя хуже ребенка"},
            {"id":46,"inserted_at":"2016-10-03 23:13:29","updated_at":"2016-10-03 23:13:29","supportee_id":301,"topic":"Вы с ума сошли!"},
            {"id":47,"inserted_at":"2016-10-24 01:39:49","updated_at":"2016-10-24 01:39:49","supportee_id":302,"topic":"Мы на такие вопросы не отвечаем"},
            {"id":48,"inserted_at":"2016-11-06 12:29:59","updated_at":"2016-11-06 12:29:59","supportee_id":203,"topic":"Чего вы хотите?"},
            {"id":49,"inserted_at":"2016-11-18 01:23:36","updated_at":"2016-11-18 01:23:36","supportee_id":202,"topic":"Особенно такие, как сегодня"},
            {"id":50,"inserted_at":"2016-12-19 02:30:39","updated_at":"2016-12-19 02:30:39","supportee_id":205,"topic":"Смешные церемонии!"}
        ]})
    }
}
