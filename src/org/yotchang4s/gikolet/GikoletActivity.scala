package org.yotchang4s.gikolet;

import android.os.Bundle
import android.view._
import android.widget._
import android.support.v4.app._
import android.util.Log
import net.simonvt.menudrawer._
import org.yotchang4s.android.Listeners._
import org.yotchang4s.gikolet.board._
import org.yotchang4s.gikolet.thread.ThreadsFragment
import org.yotchang4s.ch2.board.Board
import org.yotchang4s.android.ToastMaster
import org.yotchang4s.gikolet.response.ResponsesFragment
import org.yotchang4s.ch2.thread.Thread

class GikoletActivity extends FragmentActivity with FragmentGlueProvider {
  private[this] val TAG = getClass.getName

  private[this] var fragment: Fragment = null
  private[this] var menuDrawer: MenuDrawer = null

  private[this] var activeViewId = 0

  private[this] var finishWaitTime: Option[Long] = None

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)

    getActionBar.setDisplayHomeAsUpEnabled(true);

    menuDrawer = MenuDrawer.attach(this, MenuDrawer.Type.BEHIND, Position.LEFT, MenuDrawer.MENU_DRAG_WINDOW);
    menuDrawer.setContentView(R.layout.gikolet);
    menuDrawer.setMenuView(R.layout.menu_drawer);
    menuDrawer.setDrawerIndicatorEnabled(true);
    menuDrawer.setSlideDrawable(R.drawable.ic_drawer);

    findViewById(R.id.menuDrawerBoards).onClicks += { v =>
      changeFragment(classOf[BoardsFragment])()
      menuDrawerActiveViewChange(v)
    }

    findViewById(R.id.menuDrawerThreads).onClicks += { v =>
      changeFragment(classOf[ThreadsFragment])()
      menuDrawerActiveViewChange(v)
    }

    findViewById(R.id.menuDrawerResponses).onClicks += { v =>
      changeFragment(classOf[ResponsesFragment])()
      menuDrawerActiveViewChange(v)
    }

    Option(savedInstanceState) match {
      case Some(s) =>
        val activeFragmentClassName = savedInstanceState.getString(ArgumentKeys.MainFragmentClassName)
        val menuDrawerActiveViewId = savedInstanceState.getInt(ArgumentKeys.MenuDrawerActiveViewId)

        changeFragment(Class.forName(activeFragmentClassName).asInstanceOf[Class[Fragment]])()
        menuDrawerActiveViewChange(findViewById(menuDrawerActiveViewId))

      case None =>
        changeFragment(classOf[BoardsFragment])()
        menuDrawerActiveViewChange(findViewById(R.id.menuDrawerBoards))
    }
  }

  override def onDestroy() {
    super.onDestroy

    findViewById(R.id.menuDrawerBoards).onClicks.clear
  }

  protected override def onSaveInstanceState(savedInstanceState: Bundle) {
    super.onSaveInstanceState(savedInstanceState)

    savedInstanceState.putString(ArgumentKeys.MainFragmentClassName, fragment.getClass.getName)
    savedInstanceState.putInt(ArgumentKeys.MenuDrawerActiveViewId, activeViewId)
  }

  private def changeFragment[T <: Fragment](fragmentClass: Class[T])(showF: Fragment => Unit = null): T = {
    Log.i(TAG, "change fragment")
    val fm = getSupportFragmentManager
    val fragment = fm.findFragmentByTag(fragmentClass.getName)

    val tran = fm.beginTransaction

    if (this.fragment != null) {
      //tran.detach(this.fragment)
      tran.hide(this.fragment)
    }
    this.fragment = fragment match {
      case f: Fragment =>
        //tran.attach(f)
        if (showF != null) showF(f)
        tran.show(f)
        f
      case _ =>
        val f = Fragment.instantiate(this, fragmentClass.getName, null)
        f.setArguments(new Bundle)
        if (showF != null) showF(f)
        //tran.add(android.R.id.content, f, fragmentClass.getName)
        tran.add(android.R.id.content, f, fragmentClass.getName)
        f
    }

    tran.commit

    this.fragment.asInstanceOf[T]
  }

  def viewThreads(board: Board) {
    val f = changeFragment(classOf[ThreadsFragment]) { f =>
      val args = f.getArguments
      args.putSerializable(ArgumentKeys.board, board)
    }
    f.updateThreads

    menuDrawerActiveViewChange(findViewById(R.id.menuDrawerThreads))
  }

  def viewResponses(thread: Thread) {
    val f = changeFragment(classOf[ResponsesFragment]) { f =>
      val args = Option(f.getArguments).getOrElse(new Bundle)
      args.putSerializable(ArgumentKeys.thread, thread)

      if (f.getArguments == null) {
        f.setArguments(args)
      }
    }
    f.updateResponses

    menuDrawerActiveViewChange(findViewById(R.id.menuDrawerResponses))
  }

  private def menuDrawerActiveViewChange(v: View) {
    menuDrawer.setActiveView(v)
    menuDrawer.closeMenu
    activeViewId = v.getId
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    item.getItemId match {
      case android.R.id.home =>
        menuDrawer.toggleMenu
        true
      case _ => super.onOptionsItemSelected(item)
    }
  }

  protected[gikolet] override def onBackPressed {
    val callSuper = {
      val drawerState = menuDrawer.getDrawerState
      if (drawerState == MenuDrawer.STATE_OPEN || drawerState == MenuDrawer.STATE_OPENING) {
        menuDrawer.closeMenu
        false

      } else if (fragment != null && fragment.isInstanceOf[AbstractFragment]) {
        !fragment.asInstanceOf[AbstractFragment].onBackPressed
      } else {
        true
      }
    }
    if (callSuper) {
      finishWaitTime match {
        case Some(t) =>
          if (System.currentTimeMillis - t <= 2000L) {
            moveTaskToBack(true)
            finishWaitTime = None
          } else {
            finishWait
          }

        case None =>
          finishWait
      }

      def finishWait {
        finishWaitTime = Some(System.currentTimeMillis)
        ToastMaster.makeText(this, R.string.terminationNotice, Toast.LENGTH_SHORT).show
      }
    }
  }
}
