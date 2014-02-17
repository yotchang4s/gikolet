package org.yotchang4s.gikolet.response

import android.view._
import android.widget._
import android.content.Context
import org.yotchang4s.ch2.board._
import org.yotchang4s.ch2.thread.Thread
import org.yotchang4s.gikolet.R
import org.yotchang4s.ch2.response.ResponseList
import org.yotchang4s.ch2.response.Response
import org.yotchang4s.ch2.response.Response
import java.util.regex.Pattern
import org.apache.commons.lang3.StringEscapeUtils
import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import android.text.Spanned

class ResponseAdapter(context: Context) extends BaseAdapter {
  private[this] val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE).asInstanceOf[LayoutInflater]

  private[this] var responses: List[Response] = Nil

  def setResponses(responses: List[Response]) = {
    this.responses = if (this.responses != null) responses else Nil
  }

  def getResponses: List[Response] = this.responses

  def getItem(position: Int) = getResponses(position)

  def getCount = getResponses.size

  def getItemId(position: Int) = {
    31 + position.hashCode * 31 + getResponses(position).hashCode * 31
  }

  override def getView(position: Int, convertView: View, parent: ViewGroup): View = {
    val view =
      if (convertView == null) {
        inflater.inflate(R.layout.responses_response, null, false)
      } else {
        convertView
      }
    val textView = view.asInstanceOf[TextView]

    val response = getResponses(position)

    val body = parseResponse(response)
    textView.setText(body)

    textView
  }

  private[this] val lowerOrUpperBrPtn = Pattern.compile("<br>", Pattern.CASE_INSENSITIVE)
  private[this] val anchorStartPtn = Pattern.compile("<a href=.+>", Pattern.CASE_INSENSITIVE)
  private[this] val anchorEndPtn = Pattern.compile("</a>", Pattern.CASE_INSENSITIVE)

  private def parseResponse(response: Response) = {
    val builder = new SpannableStringBuilder

    val s = new RelativeSizeSpan(0.8f)

    val number = response.identity.value._2.toString
    builder.append(number).append(' ')
    builder.append(response.name).append(' ')
    response.id.foreach(id => builder.append(id).append(' '))
    builder.append(response.date).append(' ')
    response.id.foreach(id => builder.append(id).append(' '))
    response.be.foreach(be => builder.append(be).append(' '))

    builder.setSpan(s, 0, builder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

    val lineSpacing = new RelativeSizeSpan(0.5f)

    val body = parseResponseBody(response.body)
    builder.append("\n\n")
    builder.setSpan(lineSpacing, builder.length - 2, builder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

    builder.append(body)
    val resultBody = builder

    resultBody
  }

  private def parseResponseBody(body: String) = {
    val returnBody = lowerOrUpperBrPtn.matcher(body).replaceAll("\n")
    val unescapeBody = StringEscapeUtils.unescapeXml(returnBody)
    val removeStartAnchorBody = anchorStartPtn.matcher(unescapeBody).replaceAll("")
    val removeEndAnchorBody = anchorStartPtn.matcher(removeStartAnchorBody).replaceAll("")

    val resultBody = removeEndAnchorBody

    resultBody
  }
}
