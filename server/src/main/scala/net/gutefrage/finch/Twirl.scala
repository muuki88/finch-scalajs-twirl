package net.gutefrage.finch

import com.twitter.finagle.http.Response
import io.finch.Text
import io.finch.internal.{BufText, ToResponse}
import play.twirl.api.HtmlFormat

/**
  * == Twirl ToResponse instances ==
  *
  * Provides ToResponse instances for creating html responses from twirl templates.
  *
  */
object Twirl {
  implicit val twirlToResponse: ToResponse.Aux[HtmlFormat.Appendable,
                                               Text.Html] =
    ToResponse.instance[HtmlFormat.Appendable, Text.Html] {
      case (template, charset) =>
        val response = Response()
        response.setContentType(template.contentType)
        response.content = BufText(template.toString(), charset)

        response
    }

  /**
    * Renders a default error template when an exception occurs.
    */
  implicit val htmlExceptionToResponse: ToResponse.Aux[Exception, Text.Html] =
    ToResponse.instance[Exception, Text.Html] {
      case (exception, charset) =>
        twirlToResponse.apply(html.exception(exception), charset)
    }
}
