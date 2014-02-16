package org.yotchang4s.ch2.board

import java.io._

import scala.collection._

import org.yotchang4s.scala.Loan
import org.yotchang4s.scala.Loan._
import org.yotchang4s.ch2._
import org.yotchang4s.http.Http

private[ch2] trait BoardComponentImpl extends BoardComponent {

  class BoardRepositoryImpl extends BoardRepository {

    private val categoryRegex = """^<BR><BR><B>(.+)</B><BR>$""".r
    private val boardRegex = """^<A HREF=http://(.+)/(.+)/>(.+)</A><br>$""".r

    def findCategories(implicit config: Ch2Config): Either[Ch2Exception, List[Category]] = {
      try {
        val http = new Http
        config.userAgent.foreach(http.userAgent(_))

        val url = config.boardUrl.getOrElse(Ch2Config.defaultBoardUrl)

        val response = http.get(url)

        var nowCategory: String = null
        val categories = mutable.LinkedHashMap[String, mutable.ListBuffer[Board]]()

        for (reader <- Loan(new BufferedReader(response.asReader("MS932")))) {
          Iterator.continually(reader.readLine).takeWhile(_ != null).foreach { line =>
            line match {
              case categoryRegex(category) =>
                nowCategory = category

              case boardRegex(host, key, name) if (nowCategory != null) =>
                val cs = categories.get(nowCategory) match {
                  case Some(x) => x
                  case None =>
                    val l = new mutable.ListBuffer[Board]
                    categories += (nowCategory -> l)
                    l
                }
                cs += new BoardImpl(BoardId(host, key), name)
              case x =>
                nowCategory = null
            }
          }
        }
        val cs = for (c <- categories) yield {
          new CategoryImpl(CategoryId(c._1), c._2.toList)
        }
        Right(cs.toList)

      } catch {
        case e: IOException => Left(new Ch2Exception(Ch2Exception.IOError, e))
        case e: Exception => Left(new Ch2Exception(Ch2Exception.UnknownError, e))
      }
    }
  }
}

private[ch2] class CategoryImpl(val identity: CategoryId, boards: immutable.List[Board]) extends Category {
  def getBoards = boards
}

private[ch2] class BoardImpl(
  val identity: BoardId,
  val name: String) extends Board
