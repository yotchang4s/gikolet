package org.yotchang4s.ch2.board

import java.io._
import scala.collection._
import org.yotchang4s.scala.Loan
import org.yotchang4s.scala.Loan._
import org.yotchang4s.ch2._
import org.yotchang4s.ch2.thread.Thread
import org.yotchang4s.http.Http
import java.util.regex.Pattern

private[ch2] trait BoardComponentImpl extends BoardComponent {

  class BoardRepositoryImpl extends BoardRepository {

    private val categoryRegex = Pattern.compile("""^<BR><BR><B>(.+)</B><BR>$""")
    private val boardRegex = Pattern.compile("""^<A HREF=http://(.+)/(.+)/>(.+)</A>(<br>)?$""")

    def findCategories(implicit config: Ch2Config): Either[Ch2Exception, List[Category]] = {
      try {
        val http = new Http
        config.userAgent.foreach(http.userAgent(_))

        val url = config.boardUrl.getOrElse(Ch2Config.defaultBoardUrl)

        val response = http.get(url)

        var nowCategory: String = null
        val categories = mutable.LinkedHashMap[String, mutable.ListBuffer[Board]]()

        for (reader <- Loan(new BufferedReader(response.asReader("MS932")))) {
          Iterator.continually(reader.readLine).takeWhile(null !=).foreach { line =>
            val categoryMacher = categoryRegex.matcher(line)
            if (categoryMacher.find) {
              nowCategory = categoryMacher.group(1)

            } else {
              val boardMatcher = boardRegex.matcher(line)
              if (nowCategory != null && boardMatcher.find) {
                val host = boardMatcher.group(1)
                val key = boardMatcher.group(2)
                val name = boardMatcher.group(3)

                val cs = categories.get(nowCategory) match {
                  case Some(x) => x
                  case None =>
                    val l = new mutable.ListBuffer[Board]
                    categories += (nowCategory -> l)
                    l
                }
                cs += new BoardImpl(BoardId(host, key), name)

              } else if(line.isEmpty){
                nowCategory = null
              }
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
  val name: String) extends Board {

  def threads(implicit ch2: Ch2, config: Ch2Config): Either[Ch2Exception, List[Thread]] = {
    ch2.thread.findSubjects(identity)
  }
}
