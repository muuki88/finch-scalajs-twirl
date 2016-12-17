package net.gutefrage.finch

import java.text.SimpleDateFormat

import fastparse.core.Parsed.Success
import org.scalatest._

class QueryParserSpec extends FlatSpec with Matchers {

  val dateFormat = new SimpleDateFormat("dd.MM.yyyy")
  val parser = new QueryParser(dateFormat.parse)

  "The QueryParser" should "parse a full text search" in {
    val Success(result, _) = parser("some search terms")
    result should contain only(Word("some"), Word("search"), Word("terms"))
  }

  it should "parse full text and user" in {
    val Success(result, _) = parser("some text user=normal")
    result should contain only(Word("some"), Word("text"), UserName("normal"))
  }

  it should "parse full text and tag definition" in {
    val Success(result, _) = parser("some text tag=test")
    result should contain only(Word("some"), Word("text"), Tags(WordList(List(Word("test")))))
  }

  it should "parse full text and tags definition" in {
    val Success(result, _) = parser("some text tags=test1,test2")
    result should contain only(Word("some"), Word("text"), Tags(WordList(List(Word("test1"), Word("test2")))))
  }

  "The QueryParser completion" should "provide valid suggestions" in {
    parser.suggestions("u") should contain only "user"
    parser.suggestions("user") should be(empty)
    parser.suggestions("t") should contain only "tag"
    parser.suggestions("tag") should be(empty)
  }

}
