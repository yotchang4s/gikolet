package org.yotchang4s.ch2.thread

import org.yotchang4s.ch2._
import org.yotchang4s.ch2.board.BoardId

trait ThreadComponent {
  val thread: ThreadRepository

  trait ThreadRepository {
    def findSubjects(boardId: BoardId)(implicit config: Ch2Config): Either[Ch2Exception, List[Thread]]
  }
}