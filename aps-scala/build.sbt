enablePlugins(ScalaJSPlugin)

name := "aps-scala"
version := "0.1"
scalaVersion := "2.11.8"

scalaJSUseRhino in Global := false
scalaJSOptimizerOptions ~= { _.withDisableOptimizer(true) }
jsDependencies += RuntimeDOM

crossTarget in fastOptJS := new java.io.File("E:/work/aps/aps/built/ua-writer")

EclipseKeys.withSource := true

