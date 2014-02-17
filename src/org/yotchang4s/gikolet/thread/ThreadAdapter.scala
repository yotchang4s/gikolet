package org.yotchang4s.gikolet.thread

import android.view._
import android.widget._
import android.content.Context
import org.yotchang4s.ch2.board._
import org.yotchang4s.ch2.thread.Thread
import org.yotchang4s.gikolet.R
import org.apache.commons.lang3.StringEscapeUtils
import java.text.SimpleDateFormat
import java.util.Date

class ThreadAdapter(context: Context) extends BaseAdapter {
  private[this] val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE).asInstanceOf[LayoutInflater]

  private[this] var threads: List[Thread] = Nil
  private[this] val format: SimpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日(E) kk:mm:ss")

  def setThreads(threads: List[Thread]) {
    this.threads = if (threads != null) threads else Nil
  }

  def getThreads: List[Thread] = threads

  def getItem(position: Int) = getThreads(position)

  def getCount = getThreads.size

  def getItemId(position: Int) = {
    31 + position.hashCode * 31 + getThreads(position).hashCode * 31
  }

  override def getView(position: Int, convertView: View, parent: ViewGroup): View = {
    val viewGroup =
      if (convertView == null) {
        inflater.inflate(R.layout.threads_thread, null, false)
      } else {
        convertView
      }

    val threadTitleTextView = viewGroup.findViewById(R.id.threadTitle).asInstanceOf[TextView]
    val threadCreatedDateTextView = viewGroup.findViewById(R.id.threadCreatedDate).asInstanceOf[TextView]
    val threadCountTextView = viewGroup.findViewById(R.id.threadResponseCount).asInstanceOf[TextView]

    val thread = getThreads(position)

    threadTitleTextView.setText(StringEscapeUtils.unescapeXml(thread.subject))

    val date = new Date((thread.identity.value._2 + "000").toLong)
    val dateString = format.format(date)
    threadCreatedDateTextView.setText(dateString)

    threadCountTextView.setText(thread.resCount.toString)

    viewGroup
  }
}
