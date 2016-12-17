# Finch with Scala.js

This is a simple example application showing how you can integrate [Finch](https://github.com/finagle/finch) 
with [Scala.js](https://www.scala-js.org/) and [Twirl](https://github.com/playframework/twirl).

The application contains three directories:
* `server` Finch application (server side)
* `client` Scala.js application (client side)
* `shared` Scala code that you want to share between the server and the client

## Run the application
```shell
$ sbt
> ~re-start
$ open http://0.0.0.0:8080
```

## Features

The application uses the [sbt-web-scalajs](https://github.com/vmunier/sbt-web-scalajs) sbt plugin and the [scalajs-scripts](https://github.com/vmunier/scalajs-scripts) library.

- `compile`, `run`, `re-start` trigger the Scala.js fastOptJS command
- `~compile`, `~run`, `~re-start` continuous compilation is also available
- Production archives (e.g. using `universal:packageBin`) contain the optimised javascript
- Source maps
  - Open your browser dev tool to set breakpoints or to see the guilty line of code when an exception is thrown
  - Source Maps is _disabled in production_ by default to prevent your users from seeing the source files. But it can easily be enabled in production too by setting `emitSourceMaps in fullOptJS := true` in the Scala.js projects.

## Cleaning

The root project aggregates all the other projects by default.
Use this root project, called `finch-scalajs-example`, to clean all the projects at once.
```shell
$ sbt
> finch-scalajs-example/clean
```

## Classpath during development

The assets (js files, sourcemaps, etc.) are added to the classpath during development thanks to the following lines:
```
WebKeys.packagePrefix in Assets := "public/",
managedClasspath in Runtime += (packageBin in Assets).value
```

Note that `packageBin in Assets` also executes any tasks appended to `pipelineStages`, e.g. `gzip`.
You may want to avoid executing tasks under `pipelineStages` during development, because it could take long to execute.

In that case, in order to still have access to the assets under `WebKeys.packagePrefix in Assets` during development, you can use the following code instead:
```
lazy val server = (project in file("server")).settings(
...
WebKeys.packagePrefix in Assets := "public/",
WebKeys.exportedMappings in Assets ++= (for ((file, path) <- (mappings in Assets).value)
  yield file -> ((WebKeys.packagePrefix in Assets).value + path)),
...
)
```
## Acknowledgment

Thanks for the [akka-http-with-scalajs-example](https://github.com/vmunier/akka-http-with-scalajs-example) project
on which this project is based upon.
