package org.yotchang4s.ch2.thread

import org.yotchang4s.ch2._
import org.yotchang4s.ch2.board.BoardId

case class ThreadId(value: (BoardId, String)) extends Identity[(BoardId, String)]

trait Thread extends Entity[ThreadId] {
  val subject: String
  val resCount: Int
}