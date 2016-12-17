// fast development turnaround when using sbt ~re-start
addSbtPlugin("io.spray" % "sbt-revolver" % "0.8.0")

// packaging
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.1.4")

// web
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.14")
addSbtPlugin("com.vmunier" % "sbt-web-scalajs" % "1.0.3")
addSbtPlugin("com.typesafe.sbt" % "sbt-twirl" % "1.3.0")

// build info
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.6.1")
