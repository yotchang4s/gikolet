package org.yotchang4s.ch2

import org.yotchang4s.ch2.board.BoardComponentImpl
import org.yotchang4s.ch2.thread.ThreadComponentImpl
import org.yotchang4s.ch2.response.ResponseComponentImpl
import org.yotchang4s.ch2.board.BoardComponent
import org.yotchang4s.ch2.thread.ThreadComponent
import org.yotchang4s.ch2.response.ResponseComponent
import org.yotchang4s.ch2.response.ResponseComponentImpl

object Ch2 extends Ch2 with BoardComponentImpl with ThreadComponentImpl with ResponseComponentImpl {
  val board = new BoardRepositoryImpl
  val thread = new ThreadRepositoryImpl
  val response = new ResponseRepositoryImpl
}

trait Ch2 extends BoardComponent with ThreadComponent with ResponseComponent