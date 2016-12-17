package net.gutefrage.finch.models

import User._

case class User(name: UserName, email: String, login: Login, picture: Picture)

object User {
  case class UserName(first: String, last: String) {
    override def toString(): String = s"$first $last"
  }
  case class Login(username: String)
  case class Picture(thumbnail: String, medium: String)
}
