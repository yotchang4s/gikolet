package org.yotchang4s.ch2.board

import scala.collection._

import org.yotchang4s.ch2.Entity
import org.yotchang4s.ch2.Identity

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
}