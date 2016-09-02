enablePlugins(ScalaJSPlugin)

name := "aps-scala"
version := "0.1"
scalaVersion := "2.12.0-M5"

scalaJSUseRhino in Global := false
scalaJSOptimizerOptions ~= { _.withDisableOptimizer(true) }
jsDependencies += RuntimeDOM

crossTarget in fastOptJS := new java.io.File("E:/work/aps/aps/built/ua-writer")

