package org.yotchang4s.gikolet.thread

import android.view._
import android.widget._
import android.content.Context
import org.yotchang4s.ch2.board._
import org.yotchang4s.ch2.thread.Thread

import org.yotchang4s.gikolet.R

class ThreadAdapter(context: Context) extends BaseAdapter {
  private[this] val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE).asInstanceOf[LayoutInflater]

  private[this] var threads: List[Thread] = Nil

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
    val view =
      if (convertView == null) {
        inflater.inflate(R.layout.threads_thread, null, false)
      } else {
        convertView
      }
    val textView = view.asInstanceOf[TextView]

    val thread = getThreads(position)

    textView.setText(thread.subject)

    textView
  }
}
