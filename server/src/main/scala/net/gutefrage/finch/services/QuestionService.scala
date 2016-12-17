package net.gutefrage.finch.services

import com.twitter.util.Future
import net.gutefrage.finch.models.{Answer, Question, User}

import scala.util.Random

/**
  * == Title ==
  *
  * @author muki
  */
class QuestionService(userService: UserService) {

  def genQuestion(id: Int): Future[Question] = {
    userService.get().map { users =>
      Question(
        id = id,
        title = randomSentence(max = 5, punctuation = "?"),
        body = (3 to 15).map(_ => randomSentence()).mkString(" "),
        user = Random.shuffle(users).head,
        answers = (0 to Random.nextInt(10))
          .map(_ => genAnswer(Random.shuffle(users).head))
          .toList
      )
    }
  }

  private def genAnswer(user: User): Answer = Answer(
    (2 to 10).map(_ => randomSentence()).mkString(" "),
    user
  )

  private def randomWord(): String = {
    // format: off
    val lipsumwords = Array(
      "a", "ac", "accumsan", "ad", "adipiscing", "aenean", "aliquam", "aliquet",
      "amet", "ante", "aptent", "arcu", "at", "auctor", "augue", "bibendum",
      "blandit", "class", "commodo", "condimentum", "congue", "consectetur",
      "consequat", "conubia", "convallis", "cras", "cubilia", "cum", "curabitur",
      "curae", "cursus", "dapibus", "diam", "dictum", "dictumst", "dignissim",
      "dis", "dolor", "donec", "dui", "duis", "egestas", "eget", "eleifend",
      "elementum", "elit", "enim", "erat", "eros", "est", "et", "etiam", "eu",
      "euismod", "facilisi", "facilisis", "fames", "faucibus", "felis",
      "fermentum", "feugiat", "fringilla", "fusce", "gravida", "habitant",
      "habitasse", "hac", "hendrerit", "himenaeos", "iaculis", "id", "imperdiet",
      "in", "inceptos", "integer", "interdum", "ipsum", "justo", "lacinia",
      "lacus", "laoreet", "lectus", "leo", "libero", "ligula", "litora",
      "lobortis", "lorem", "luctus", "maecenas", "magna", "magnis", "malesuada",
      "massa", "mattis", "mauris", "metus", "mi", "molestie", "mollis", "montes",
      "morbi", "mus", "nam", "nascetur", "natoque", "nec", "neque", "netus",
      "nibh", "nisi", "nisl", "non", "nostra", "nulla", "nullam", "nunc", "odio",
      "orci", "ornare", "parturient","pellentesque", "penatibus", "per",
      "pharetra", "phasellus", "placerat", "platea", "porta", "porttitor",
      "posuere", "potenti", "praesent", "pretium", "primis", "proin", "pulvinar",
      "purus", "quam", "quis", "quisque", "rhoncus", "ridiculus", "risus",
      "rutrum", "sagittis", "sapien", "scelerisque", "sed", "sem", "semper",
      "senectus", "sit", "sociis", "sociosqu", "sodales", "sollicitudin",
      "suscipit", "suspendisse", "taciti", "tellus", "tempor", "tempus",
      "tincidunt", "torquent", "tortor", "tristique", "turpis", "ullamcorper",
      "ultrices", "ultricies", "urna", "ut", "varius", "vehicula", "vel", "velit",
      "venenatis", "vestibulum", "vitae", "vivamus", "viverra", "volutpat",
      "vulputate"
    )
    // format: on

    val index = Random.nextInt(lipsumwords.length - 1)
    lipsumwords(index)
  }

  private def randomSentence(min: Int = 3,
                             max: Int = 15,
                             punctuation: String = ". "): String =
    (0 to Math.max(min, Random.nextInt(max)))
      .map(_ => randomWord())
      .mkString(" ") + punctuation
}
