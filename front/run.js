/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

require('source-map-support').install()

global.kotlin = require('./out/lib/kotlin.js')
require('./out/front-enhanced.js')

const mainObjectName = process.argv[2]
kotlin.modules.front.aps.front[mainObjectName].runShit(process.argv.slice(3))

