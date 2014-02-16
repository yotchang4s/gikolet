package org.yotchang4s.ch2.thread

import java.io._
import scala.collection._
import org.yotchang4s.scala.Loan
import org.yotchang4s.scala.Loan._
import org.yotchang4s.ch2._
import org.yotchang4s.http.Http
import org.yotchang4s.ch2.board.BoardId

private[ch2] trait ThreadComponentImpl extends ThreadComponent {

  class ThreadRepositoryImpl extends ThreadRepository {

    private val sugjectRegex = """^([0-9]+).dat<>(.+) \(([1-9][0-9]*)\)$""".r

    def findSubjects(boardId: BoardId)(implicit config: Ch2Config): Either[Ch2Exception, List[Thread]] = {
      try {
        val http = new Http
        config.userAgent.foreach(http.userAgent(_))

        val url = "http://" + boardId.host + "/" + boardId.name + "/subject.txt"

        val response = http.get(url)

        val subjects = mutable.ListBuffer[Thread]()
        for (reader <- Loan(new BufferedReader(response.asReader("MS932")))) {
          Iterator.continually(reader.readLine).takeWhile(_ != null).foreach { line =>
            line match {
              case sugjectRegex(id, name, resCount) =>
                subjects += new ThreadImpl(ThreadId(boardId, id), name, resCount.toInt)
            }
          }
        }
        Right(subjects.toList)

      } catch {
        case e: IOException => Left(new Ch2Exception(Ch2Exception.IOError, e))
        case e: Exception => Left(new Ch2Exception(Ch2Exception.UnknownError, e))
      }
    }
  }
}

private[ch2] class ThreadImpl(val identity: ThreadId, val subject: String, val resCount: Int) extends Thread {
}
