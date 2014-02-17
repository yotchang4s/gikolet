package org.yotchang4s.gikolet

import org.yotchang4s.ch2.board.Board
import org.yotchang4s.ch2.thread.Thread

trait FragmentGlueProvider {
  def viewThreads(board: Board)
  def viewResponses(thread: Thread)
}