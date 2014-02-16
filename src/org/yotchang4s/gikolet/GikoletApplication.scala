package org.yotchang4s.gikolet

import android.app.Application
import android.preference.PreferenceManager

class GikoletApplication extends Application {
  override def onCreate {
    GikoletConfig.sharedPreferences(PreferenceManager.getDefaultSharedPreferences(this));

    val packageManager = getPackageManager
    val packageInfo = packageManager.getPackageInfo(getPackageName, 0)

    val appName = getString(R.string.appName)
    val versionName = packageInfo.versionName

    GikoletConfig.config.userAgent("Monazilla/1.00 (" + appName + "/" + versionName + ")")
  }

  override def onTerminate {
    GikoletConfig.sharedPreferences(null);
  }
}