/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

require('source-map-support').install()

const KOMMON_HOME = process.env['INTO_KOMMON_HOME']
if (!KOMMON_HOME) throw Error('I want INTO_KOMMON_HOME environment variable')

// global.kotlin = require(`${KOMMON_HOME}/lib/kotlin/1.1-m02-eap/kotlin-1.1-m02-eap-hacked.js`)
global.kotlin = require(`./out/lib/kotlin.js`)
require(`${KOMMON_HOME}/js/out/into-kommon-js-enhanced.js`)
require('./out/front-enhanced.js')

const mainObjectName = process.argv[2]
const mainObject = kotlin.modules.front.aps.front[mainObjectName]
const argv = process.argv.slice(3)
if (typeof mainObject === 'function') mainObject(argv)
else mainObject.runShit(argv)

