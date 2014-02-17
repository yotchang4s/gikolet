package org.yotchang4s.ch2.board

import scala.collection._
import org.yotchang4s.ch2._
import org.yotchang4s.ch2.thread.Thread

case class BoardId(host: String, name: String) extends Identity[(String, String)] {
  val value = (host, name)
}

case class CategoryId(value: String) extends Identity[String]

trait Category extends Entity[CategoryId] {
  val name = identity.value

  def getBoards: immutable.List[Board]
}

trait Board extends Entity[BoardId] {
  val name: String

  def threads(implicit ch2: Ch2, config: Ch2Config): Either[Ch2Exception, List[Thread]]
}