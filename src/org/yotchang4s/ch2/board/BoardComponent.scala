package org.yotchang4s.ch2.board

import org.yotchang4s.ch2._
import org.yotchang4s.ch2.Ch2Config

trait BoardComponent {
  val board: BoardRepository

  trait BoardRepository {
    def findCategories(implicit config: Ch2Config): Either[Ch2Exception, List[Category]] 
  }
}