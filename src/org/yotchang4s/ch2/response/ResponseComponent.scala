package org.yotchang4s.ch2.response

import org.yotchang4s.ch2._
import org.yotchang4s.ch2.thread.ThreadId

trait ResponseComponent {
  val response: ResponseRepository

  trait ResponseRepository {
    def findResponses(threadId: ThreadId)(implicit config: Ch2Config): Either[Ch2Exception, (String, List[Response])]
  }
}