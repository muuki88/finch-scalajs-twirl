package net.gutefrage.finch

import com.twitter.finagle.Http
import com.twitter.finagle.http.Response
import com.twitter.io.{Buf, Reader}
import com.twitter.server.TwitterServer
import com.twitter.util.Await
import com.typesafe.config.ConfigFactory
import io.circe.generic.auto._
import io.finch._
import io.finch.circe._
import net.gutefrage.finch.Twirl._
import net.gutefrage.finch.models._
import org.slf4j.bridge.SLF4JBridgeHandler

object WebServer extends TwitterServer {

  private val config = ConfigFactory.load()
  private val interface = config.getString("http.interface")
  private val port = config.getInt("http.port")

  // admin interface stats
  private val indexRequests = statsReceiver.counter("app/index.html")

  // services
  val userService = new services.UserService
  val questionService = new services.QuestionService(userService)

  // ---- Finch Rest API ----

  // "public" because a web-assets jar is generated with a public path configured. See build.sbt
  val static: Endpoint[Buf] = get("assets" :: strings) {
    (segments: Seq[String]) =>
      val path = segments.mkString("/")
      Reader
        .readAll(
          Reader.fromStream(getClass.getResourceAsStream(s"/public/$path")))
        .map { buf =>
          Ok(buf).withHeader(getContentType(path))
        }
  }

  val index: Endpoint[Response] = get(/) {
    indexRequests.incr()
    Ok(html.index("Finch rocks!")).toResponse[Text.Html]()
  }

  // trigger the html exception page
  val exceptionEndpoint: Endpoint[Response] = get("error") {
    val error: Output[play.twirl.api.HtmlFormat.Appendable] =
      InternalServerError(new Exception("Oops. Something went wrong."))
    error.toResponse[Text.Html]()
  }

  val question: Endpoint[Question] = post("api" :: "question" :: int) {
    id: Int =>
      questionService.genQuestion(id).map(Ok)
  }

  val api = (static :+: question :+: exceptionEndpoint :+: index).handle {
    case e: Exception => InternalServerError(e)
  }
  // ---- -------------- ----

  premain {
    SLF4JBridgeHandler.removeHandlersForRootLogger()
    SLF4JBridgeHandler.install()
  }

  def main(): Unit = {

    val server =
      Http.server
        .withStatsReceiver(statsReceiver)
        .serve(s"$interface:$port", api.toServiceAs[Application.Json])
    closeOnExit(server)
    Await.ready(adminHttpServer)
  }

  def getContentType(assetPath: String): (String, String) = {
    val contentType = if (assetPath.endsWith(".js")) {
      "application/javascript"
    } else if (assetPath.endsWith(".css")) {
      "text/css"
    } else {
      "text/plain"
    }
    "Content-Type" -> contentType
  }
}
