organization in ThisBuild := "net.gutefrage.finch"
version in ThisBuild := "1.0"
isSnapshot in ThisBuild := version.value.endsWith("-SNAPSHOT")

val scalaV = "2.11.8"
val finchVersion = "0.11.1"
val finagleVersion = "6.40.0" // 6.41.0 is out, but com.twitter.io.Buf has incompatible changes
val twitterServerVersion = "1.25.0"
val bootstrapVersion = "3.3.7-1"
val circeVersion = "0.6.1" // version for finch
val scalajsVersion = "0.6.14"

lazy val server = (project in file("server"))
  .settings(
    scalaVersion := scalaV,
    scalaJSProjects := Seq(client),
    pipelineStages in Assets := Seq(scalaJSPipeline),
    // triggers scalaJSPipeline when using compile or continuous compilation
    compile in Compile := ((compile in Compile) dependsOn scalaJSPipeline).value,
    libraryDependencies ++= Seq(
      "com.github.finagle" %% "finch-core" % finchVersion,
      "com.github.finagle" %% "finch-circe" % finchVersion,
      "com.twitter" %% "finagle-stats" % finagleVersion,
      "com.twitter" %% "twitter-server" % twitterServerVersion,
      "io.catbird" %% "catbird-finagle" % "0.9.0",
      "com.typesafe" % "config" % "1.3.1",
      // logging
      "org.slf4j" % "jul-to-slf4j" % "1.7.7",
      "ch.qos.logback" % "logback-core" % "1.1.7",
      "ch.qos.logback" % "logback-classic" % "1.1.7",
      // asset dependencies and handling
      "org.webjars" % "bootstrap" % bootstrapVersion,
      "org.webjars" % "font-awesome" % "4.7.0"
    ),
    // Frontend depdendency configuration
    WebKeys.packagePrefix in Assets := "public/",
    managedClasspath in Runtime += (packageBin in Assets).value,
    // Packaging
    topLevelDirectory := None // Don't add a root folder to the archive
  )
  .enablePlugins(SbtWeb, SbtTwirl, JavaAppPackaging)
  .dependsOn(sharedJvm)

lazy val client = (project in file("client"))
  .settings(
    scalaVersion := scalaV,
    persistLauncher := true,
    persistLauncher in Test := false,
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.9.1",
      "be.doeraene" %%% "scalajs-jquery" % "0.9.1"
    ),
    jsDependencies ++= Seq(
      "org.webjars" % "jquery" % "2.1.3" / "2.1.3/jquery.js",
      "org.webjars" % "bootstrap" % bootstrapVersion / "bootstrap.js" minified "bootstrap.min.js" dependsOn "jquery.js"
    )
  )
  .enablePlugins(ScalaJSPlugin, ScalaJSWeb)
  .dependsOn(sharedJs)

lazy val shared = (crossProject.crossType(CrossType.Pure) in file("shared"))
  .enablePlugins(SbtTwirl, BuildInfoPlugin)
  .settings(
    scalaVersion := scalaV,
    // Find a better way to add this source folder
    sourceDirectories in (Compile, TwirlKeys.compileTemplates) += (baseDirectory.value.getParentFile / "src" / "main" / "twirl"),
    libraryDependencies ++= Seq(
      // twirl assets handling
      "com.vmunier" %% "scalajs-scripts" % "1.1.0",
      "io.circe" %%% "circe-core" % circeVersion,
      "io.circe" %%% "circe-parser" % circeVersion,
      "io.circe" %%% "circe-generic" % circeVersion,
      "org.scalatest" %% "scalatest" % "3.0.1" % "test"
    ),
    // build info
    buildInfoOptions += BuildInfoOption.BuildTime,
    buildInfoKeys := Seq[BuildInfoKey](
      name,
      version,
      scalaVersion,
      "finchVersion" -> finchVersion,
      "finagleVersion" -> finagleVersion,
      "twitterServerVersion" -> twitterServerVersion,
      "scalajsVersion" -> scalajsVersion
    ),
    buildInfoPackage := "net.gutefrage.finch.build"
  )
  .jsSettings(
    libraryDependencies ++= Seq(
      "io.circe" %%% "circe-scalajs" % circeVersion
    )
  )
  .jsConfigure(_ enablePlugins ScalaJSWeb)

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

// loads the server project at sbt startup
onLoad in Global := (Command
  .process("project server", _: State)) compose (onLoad in Global).value
