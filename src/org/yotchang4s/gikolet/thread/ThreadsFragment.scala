package org.yotchang4s.gikolet.thread

import scala.concurrent._
import android.os.Bundle
import android.util.Log
import android.view._
import android.widget.AbsListView._
import android.widget._

import org.yotchang4s.android._
import org.yotchang4s.android.Listeners._
import org.yotchang4s.ch2._
import org.yotchang4s.ch2.board._
import org.yotchang4s.ch2.Ch2Exception.UnknownError

import org.yotchang4s.gikolet._
import org.yotchang4s.gikolet.GikoletConfig.config

class ThreadsFragment extends AbstractFragment {

  private[this] var listView: Option[ListView] = None
  private[this] var threadAdapter: Option[ThreadAdapter] = None

  setRetainInstance(true)

  protected override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    val view = inflater.inflate(R.layout.threads, container, false)

    val lv = view.findViewById(R.id.threadListview).asInstanceOf[ListView]

    lv.onItemClicks += { (parent, view, position, id) =>
      for {
        a <- threadAdapter
        p <- fragmentGlueProvider
      } {
        p.viewResponses(a.getThreads(position))
      }
    }

    listView = Some(lv)

    view
  }

  protected override def onDestroyView {
    super.onDestroyView

    listView.foreach(_.onItemClicks.clear)
    listView = None
  }

  def updateThreads {

    val board = getArguments match {
      case null =>
        None
      case x =>
        val board = x.getSerializable(ArgumentKeys.board).asInstanceOf[Board]
        Some(board)
    }

    threadAdapter.foreach { a =>
      a.setThreads(Nil)
      a.notifyDataSetChanged
    }

    import scala.concurrent._
    import ExecutionContext.Implicits.global

    val f = future {
      board match {
        case Some(b) => Ch2.thread.findSubjects(b.identity)
        case None => Left(new Ch2Exception(UnknownError))
      }
    }

    f.onSuccess {
      case Right(t) =>
        listView.foreach { v =>
          import scala.collection.convert.WrapAsJava._
          val adapter = new ThreadAdapter(getActivity.getApplicationContext)

          adapter.setThreads(t)
          adapter.notifyDataSetChanged

          threadAdapter = Some(adapter)

          v.setAdapter(adapter)
        }
      case Left(e) =>
        error(R.string.accessFailure, e);

    }(new UIExecutionContext())
  }
}