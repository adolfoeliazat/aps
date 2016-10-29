/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

require('source-map-support').install()

const KOMMON_HOME = process.env['INTO_KOMMON_HOME']
if (!KOMMON_HOME) throw Error('I want INTO_KOMMON_HOME environment variable')

global.kotlin = require(`${KOMMON_HOME}/lib/kotlin/1.1-m02-eap/kotlin-1.1-m02-eap-hacked.js`)
require(`${KOMMON_HOME}/js/out/into-kommon-js-enhanced.js`)
require('./out/front-enhanced.js')

const mainObjectName = process.argv[2]
kotlin.modules.front.aps.front[mainObjectName].runShit(process.argv.slice(3))

