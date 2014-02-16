package org.yotchang4s.gikolet.board

import scala.concurrent._
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view._
import android.widget.AbsListView._
import android.widget._
import org.yotchang4s.android._
import org.yotchang4s.android.Listeners._
import org.yotchang4s.ch2._
import org.yotchang4s.ch2.board._
import org.yotchang4s.gikolet.GikoletConfig.config
import org.yotchang4s.gikolet._

class BoardsFragment extends AbstractFragment {

  private[this] var currentFuture: Option[(Future[Either[Ch2Exception, List[Category]]], () => Boolean)] = None

  private[this] var gridView: Option[GridViewV16] = None
  private[this] var categoryAdapter: Option[BoardAdapter] = None
  private[this] var currentCategory: Option[Category] = None

  setRetainInstance(true)

  protected override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    val view = inflater.inflate(R.layout.boards, container, false)

    val gw = view.findViewById(R.id.boardsGridview).asInstanceOf[GridViewV16]

    gw.onItemClicks += { (parent, view, position, id) =>
      for {
        a <- categoryAdapter
      } {
        currentCategory match {
          case Some(c) =>
            for {
              p <- fragmentGlueProvider
              i <- a.getCategoryIndex
            } {
              p.viewThreads(a.getCategories(i).getBoards(position))
            }
          case None =>
            currentCategory = Some(a.getCategories(position))

            a.setCategoryIndex(Some(position))
            a.notifyDataSetChanged
        }
      }
    }

    gridView = Some(gw)

    view
  }

  protected override def onDestroyView {
    super.onDestroyView

    gridView.foreach(_.onItemClicks.clear)
    gridView = None
  }

  protected[gikolet] override def onBackPressed = {
    val result =
      for {
        a <- categoryAdapter
        c <- currentCategory
      } yield {
        currentCategory = None

        a.setCategoryIndex(None)
        a.notifyDataSetChanged
        true
      }
    result match {
      case Some(b) => b
      case None => false
    }
  }

  protected override def onActivityCreated(savedInstanceState: Bundle) {
    super.onActivityCreated(savedInstanceState)

    updateBoards
  }

  private def updateBoards {

    import scala.concurrent._
    import ExecutionContext.Implicits.global

    val f = future {
      Ch2.board.findCategories
    }

    f.onSuccess {
      case Right(c) =>
        gridView.foreach { v =>
          import scala.collection.convert.WrapAsJava._
          val adapter = new BoardAdapter(getActivity.getApplicationContext)

          adapter.setCategories(c)
          adapter.setCategoryIndex(None)
          adapter.notifyDataSetChanged

          categoryAdapter = Some(adapter)

          v.setAdapter(adapter)
        }
      case Left(e) =>
        error(R.string.accessFailure, e);

    }(new UIExecutionContext())
  }
}