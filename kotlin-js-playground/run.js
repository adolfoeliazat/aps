require('source-map-support').install()

global.kotlin = require('./out/lib/kotlin.js')
require('./out/kotlin-js-playground.js')

const testName = process.argv[2]
console.log(`Running ${testName}`)
kotlin.modules['kotlin-js-playground'][testName]()

