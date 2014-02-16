package org.yotchang4s.gikolet

import android.support.v4.app.Fragment
import android.util.Log
import android.widget.Toast
import org.yotchang4s.ch2.Ch2Exception
import org.yotchang4s.ch2.Ch2Exception._
import org.yotchang4s.android.ToastMaster
import android.app.Activity

trait AbstractFragment extends Fragment {

  protected val TAG: String = getClass.getSimpleName

  private[this] var _childFragment: Option[AbstractFragment] = None

  protected var _fragmentGlueProvider: Option[FragmentGlueProvider] = None

  protected def childFragment(childFragment: AbstractFragment) = _childFragment = Option(childFragment)
  protected def childFragment: Option[AbstractFragment] = _childFragment

  protected override def onAttach(activity: Activity) {
    super.onAttach(activity)

    if (!activity.isInstanceOf[FragmentGlueProvider]) {
      throw new ClassCastException(activity.getLocalClassName() + " must implements " + classOf[FragmentGlueProvider].getSimpleName())
    }
    _fragmentGlueProvider = Some(activity.asInstanceOf[FragmentGlueProvider])
  }

  protected override def onDetach {
    super.onDetach
    _fragmentGlueProvider = None
  }

  protected def fragmentGlueProvider = _fragmentGlueProvider

  protected[gikolet] def onBackPressed: Boolean = {
    val noStack = {
      var s = childFragment match {
        case Some(c) => c.onBackPressed
        case None => false
      }
      if (!s && getChildFragmentManager.getBackStackEntryCount() > 0) {
        getChildFragmentManager.popBackStack
        s = true
      }
      s
    }
    noStack
  }

  protected def error(resId: Int, t: Throwable) {
    error(getActivity.getString(resId), t)
  }

  protected def error(message: String, t: Throwable) {
    t match {
      case e: Ch2Exception =>
        e.errorType match {
          case IOError =>
            Log.w(TAG, e)
          case t =>
            Log.e(TAG, message, e)
        }
      case e =>
        Log.e(TAG, message, e)
    }

    ToastMaster.makeText(getActivity, message, Toast.LENGTH_SHORT).show
  }
}