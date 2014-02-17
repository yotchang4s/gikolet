package org.yotchang4s.ch2.response

import java.io._
import scala.collection._
import org.yotchang4s.scala.Loan
import org.yotchang4s.scala.Loan._
import org.yotchang4s.ch2._
import org.yotchang4s.http.Http
import org.yotchang4s.ch2.thread.ThreadId
import java.util.regex.Pattern

private[ch2] trait ResponseComponentImpl extends ResponseComponent {

  class ResponseRepositoryImpl extends ResponseRepository {

    private val responseRegexPtn = Pattern.compile("""^(.*)<>(.*)<>(.*?)( ID:(.+?))?( BE:(.+?))?<> (.*)<>(.*)$""")

    def findResponses(threadId: ThreadId)(implicit config: Ch2Config): Either[Ch2Exception, (String, List[Response])] = {
      try {
        val http = new Http
        config.userAgent.foreach(http.userAgent(_))

        val boardId = threadId.value._1
        val threadKey = threadId.value._2

        val url = "http://" + boardId.host + "/" + boardId.name + "/dat/" + threadKey + ".dat"

        val response = http.get(url)

        var number = 1
        var subject: String = ""
        val responses = mutable.ListBuffer[Response]()
        for (reader <- Loan(new BufferedReader(response.asReader("MS932")))) {
          Iterator.continually(reader.readLine).takeWhile(_ != null).foreach { line =>
            val responseMatcher = responseRegexPtn.matcher(line)
            if (responseMatcher.find) {
              val name = responseMatcher.group(1)
              val emailAddress = responseMatcher.group(2)
              val date = responseMatcher.group(3)
              val id = Option(responseMatcher.group(5))
              val be = Option(responseMatcher.group(7))
              val body = responseMatcher.group(8)
              if (number == 1) { subject = responseMatcher.group(9) }
              val emailAddressOpt = if (emailAddress.isEmpty) None else Some(emailAddress)
              responses += new ResponseImpl(ResponseId(threadId, number), name, emailAddressOpt, date, id, be, body)
              number = number + 1
            }
          }
        }

        Right((subject, responses.toList))

      } catch {
        case e: IOException => Left(new Ch2Exception(Ch2Exception.IOError, e))
        case e: Exception => Left(new Ch2Exception(Ch2Exception.UnknownError, e))
      }
    }
  }
}

private[ch2] class ResponseImpl(
  val identity: ResponseId,
  val name: String,
  val emailAddress: Option[String],
  val date: String,
  val id: Option[String],
  val be: Option[String],
  val body: String) extends Response {
}
