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

class GikoletActivity extends FragmentActivity with FragmentGlueProvider {
  private[this] val TAG = getClass.getName

  private[this] var fragment: Fragment = null
  private[this] var menuDrawer: MenuDrawer = null

  private[this] var activeViewId = 0

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

  private def changeFragment[T <: Fragment](fragmentClass: Class[T])(showF: Fragment => Unit = null) {
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
        if (showF != null) showF(f)
        //tran.add(android.R.id.content, f, fragmentClass.getName)
        tran.add(android.R.id.content, f, fragmentClass.getName)
        f
    }

    tran.commit
  }

  def viewThreads(board: Board) {
    changeFragment(classOf[ThreadsFragment]) { f =>
      val args = Option(f.getArguments).getOrElse(new Bundle)
      args.putSerializable(ArgumentKeys.board, board)

      if (f.getArguments == null) {
        f.setArguments(args)
      }
    }
    menuDrawerActiveViewChange(findViewById(R.id.menuDrawerThreads))
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
      moveTaskToBack(true)
    }
  }
}
