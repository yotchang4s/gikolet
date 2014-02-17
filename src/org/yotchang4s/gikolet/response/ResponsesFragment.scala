package org.yotchang4s.gikolet.response

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
import org.yotchang4s.ch2.thread.Thread

class ResponsesFragment extends AbstractFragment {

  private[this] var listView: Option[ListView] = None
  private[this] var responseAdapter: Option[ResponseAdapter] = None

  setRetainInstance(true)

  protected override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    val view = inflater.inflate(R.layout.responses, container, false)

    val lv = view.findViewById(R.id.responseListview).asInstanceOf[ListView]

    lv.onItemClicks += { (parent, view, position, id) =>
    }

    listView = Some(lv)

    view
  }

  protected override def onDestroyView {
    super.onDestroyView

    listView.foreach(_.onItemClicks.clear)
    listView = None
  }

  def updateResponses {

    val thread = getArguments match {
      case null =>
        None
      case x =>
        val thread = x.getSerializable(ArgumentKeys.thread).asInstanceOf[Thread]
        Some(thread)
    }

    responseAdapter.foreach { a =>
      a.setResponses(Nil)
      a.notifyDataSetChanged
    }

    import scala.concurrent._
    import ExecutionContext.Implicits.global

    val f = future {
      thread match {
        case Some(t) => Ch2.response.findResponses(t.identity)
        case None => Left(new Ch2Exception(UnknownError))
      }
    }

    f.onSuccess {
      case Right(t) =>
        listView.foreach { v =>
          import scala.collection.convert.WrapAsJava._
          val adapter = new ResponseAdapter(getActivity.getApplicationContext)

          adapter.setResponses(t._2)
          adapter.notifyDataSetChanged

          responseAdapter = Some(adapter)

          v.setAdapter(adapter)
        }
      case Left(e) =>
        error(R.string.accessFailure, e);

    }(new UIExecutionContext())
  }
}