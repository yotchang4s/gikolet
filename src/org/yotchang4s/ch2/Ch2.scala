package org.yotchang4s.ch2

import org.yotchang4s.ch2.board.BoardComponentImpl
import org.yotchang4s.ch2.thread.ThreadComponentImpl

object Ch2 extends BoardComponentImpl with ThreadComponentImpl {
  val board = new BoardRepositoryImpl
  val thread = new ThreadRepositoryImpl
}