package net.gutefrage.finch.models

case class Question(id: Int, title: String, body: String, user: User, answers: List[Answer])
