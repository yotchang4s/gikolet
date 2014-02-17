package org.yotchang4s.ch2.response

import org.yotchang4s.ch2._
import org.yotchang4s.ch2.thread.ThreadId

case class ResponseId(value: (ThreadId, Int)) extends Identity[(ThreadId, Int)]

trait Response extends Entity[ResponseId] {
  val name: String
  val date: String
  val id: Option[String]
  val be: Option[String]
  val body: String
}

class ResponseList(val title: String) {
  private[this] var _responses: List[Response] = Nil

  def responses(responses: List[Response]) = _responses = responses
  def responses: List[Response] = _responses
}