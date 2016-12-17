package net.gutefrage.finch.services

import io.catbird.util._
import cats.data.EitherT
import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.Future
import io.circe.parser.parse
import io.circe.generic.auto._
import net.gutefrage.finch.models.User

/**
  * Load example user profiles from randomuser.me
  */
class UserService {

  private case class RandomUserResults(results: List[User])

  private val hostname = "randomuser.me"
  private val client: Service[Request, Response] =
    Http.client.withTls(hostname).newService(s"$hostname:443")

  def get(): Future[List[User]] = {
    val req = Request("/api/", "results" -> "10")
    req.host = "api.randomuser.me"
    req.contentType = "application/json"
    (for {
      response <- EitherT.right(client(req))
      rawJson <- EitherT
        .fromEither[Future](parse(response.contentString))
        .leftMap(_ => List.empty[User])
      user <- EitherT
        .fromEither[Future](rawJson.as[RandomUserResults])
        .leftMap(_ => List.empty[User])
    } yield user.results).merge
  }

}
