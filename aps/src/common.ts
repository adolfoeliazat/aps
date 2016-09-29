/*
 * APS
 * 
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

#pragma instrument-ui-rendering

#import static 'into-u/utils-client'
import {registerHotFunction, t as uiT} from 'into-u/ui'

let t = uiT

// TODO:vgrechka Unhackify passing different t() from backend and frontend to common.ts    f478a3dc-66ad-4da5-b897-7d29d7c08c74 
//               Or maybe it's actually good enough...
export function setCommonT(explicitT) {
    t = explicitT
}

export const apsdata

const makeOrRemakeAPSCommonExportedShit = function hot$makeOrRemakeAPSCommonExportedShit() {

    apsdata = {
        userKindTitle(kind) {
            return lookup(kind, {
                customer: t(`TOTE`, 'Заказчик'),
                writer: t(`TOTE`, 'Писатель'),
                admin: t(`TOTE`, 'Админ'),
            })
        },
        
//        userStateValueToTitle() {
//            return {
//                'cool': t(`TOTE`, `Прохладный`),
//                'profile-approval-pending': t(`TOTE`, `Ждет аппрува профиля`),
//                'profile-rejected': t(`TOTE`, `Профиль завернут`),
//            }
//        },
        
        userStateValues() {
            return apsdata.userStates().map(x => x.value)
//            return keys(apsdata.userStateValueToTitle())
        },
        
        userStateTitle(state) {
            return apsdata.userStates().find(x => x.value === state).title
//            return apsdata.userStateValueToTitle()[state]
        },
        
        userFilters() {
            return [
                {value: 'ALL', title: t(`TOTE`, `Все`)},
                {value: 'COOL', title: t(`TOTE`, `Прохладные`)},
                {value: '2APPROVE', title: t(`TOTE`, `Ждут аппрува`)},
                {value: 'REJECTED', title: t(`TOTE`, `Завернутые`)},
                {value: 'BANNED', title: t(`TOTE`, `Забаненые`)},
            ]
        },
        
        userStates() {
            return [
                {value: 'COOL', title: t(`TOTE`, `Прохладный`)},
                {value: 'PROFILE_APPROVAL_PENDING', title: t(`TOTE`, `Ждет аппрува профиля`)},
                {value: 'PROFILE_REJECTED', title: t(`TOTE`, `Профиль завернут`)},
                {value: 'BANNED', title: t(`TOTE`, `Забанен`)},
            ]
        },
    }
    
}


registerHotFunction({fun: makeOrRemakeAPSCommonExportedShit})
makeOrRemakeAPSCommonExportedShit()

