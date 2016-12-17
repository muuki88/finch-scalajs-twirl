package web

import cats.data.EitherT
import cats.instances.all._
import io.circe.generic.auto._
import io.circe.scalajs._
import net.gutefrage.finch.models.{FetchError, Question}
import net.gutefrage.finch.html
import org.scalajs.dom
import org.scalajs.dom.document
import org.scalajs.dom.experimental._
import org.scalajs.jquery.jQuery

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.Thenable.Implicits._

object FrontendApp extends js.JSApp {

  val resultNode = document.getElementById("results")
  val errorNode = document.getElementById("errors")

  def main(): Unit = {
    jQuery("form").submit(onSubmit _)
  }

  def onSubmit(): Boolean = {
    jQuery(errorNode).empty()
    jQuery(resultNode).empty()

    val questionId = jQuery("#search-input").value().toString
    getQuestionId(questionId.toInt).onComplete {
      case scala.util.Success(Right(results)) => renderResults(results)
      case scala.util.Success(Left(err)) => renderError(err)
      case scala.util.Failure(err) => renderError(FetchError(err.getMessage))
    }

    false
  }

  def getQuestionId(id: Int): Future[Either[FetchError, Question]] =
    (for {
      response <- EitherT.right(
        Fetch
          .fetch(s"/api/question/$id",
                 RequestInit(
                   method = HttpMethod.POST
                 ))
          .toFuture)
      json <- EitherT(parseJson(response))
      result <- EitherT
        .fromEither[Future](decodeJs[Question](json))
        .leftMap(e => FetchError(e.getMessage))
    } yield result).value

  def parseJson(response: Response): Future[Either[FetchError, js.Any]] = {
    if (response.ok) response.json().map(Right.apply)
    else Future.successful(Left(FetchError(response.statusText)))
  }

  def renderResults(result: Question): Unit = {
    renderTemplate(resultNode, result)
  }

  def renderTemplate(targetNode: dom.Node, result: Question): Unit = {
    jQuery(targetNode).empty()
    jQuery(targetNode).append(html.results.render(result).toString)
  }

  def renderError(fetchError: FetchError): Unit = {
    jQuery(errorNode).append(html.error(fetchError).toString)
  }
}
