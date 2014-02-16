package org.yotchang4s.gikolet

import org.yotchang4s.ch2.Ch2Config
import android.content.SharedPreferences

object GikoletConfig {
  private val httpProxyHostKey = "httpProxyHostKey"
  private val httpProxyPortKey = "httpProxyPort"
  private val httpProxyUserKey = "httpProxyUser"
  private val httpProxyPasswordKey = "httpProxyPassword"

  private var _gikoletConfig: GikoletConfig = null

  def sharedPreferences(sharedPreferences: SharedPreferences) {
    _gikoletConfig = GikoletConfig(sharedPreferences)
  }

  implicit def config: GikoletConfig = _gikoletConfig
}

case class GikoletConfig(sharedPreferences: SharedPreferences) extends Ch2Config {

  import GikoletConfig._

  private var _userAgent: Option[String] = None

  def httpProxyHost: Option[String] = getString(httpProxyHostKey)
  def httpProxyHost(httpProxyHost: String) { putString(httpProxyHostKey, httpProxyHost) }

  def httpProxyPort: Option[Int] = {
    sharedPreferences.getInt(httpProxyPortKey, -1) match {
      case x if (x < 0) => None
      case x => Some(x)
    }
  }

  def httpProxyPort(httpProxyPort: Int) {
    val editor = sharedPreferences.edit
    if (httpProxyPort < 0) {
      editor.remove(httpProxyPortKey)
    } else {
      editor.putInt(httpProxyPortKey, httpProxyPort)
    }
    editor.commit
  }

  def httpProxyUser: Option[String] = getString(httpProxyUserKey)
  def httpProxyUser(httpProxyUser: String) { putString(httpProxyUserKey, httpProxyUser) }

  def httpProxyPassword: Option[String] = getString(httpProxyPasswordKey)
  def httpProxyPassword(httpProxyPassword: String) { putString(httpProxyPasswordKey, httpProxyPassword) }

  def userAgent: Option[String] = _userAgent
  def userAgent(userAgent: String) = _userAgent = Option(userAgent)

  private def putString(key: String, value: String) {
    val editor = sharedPreferences.edit
    if (value == null) {
      editor.remove(key)
    } else {
      editor.putString(key, value)
    }
    editor.commit
  }

  private def getString(key: String): Option[String] = {
    Option(sharedPreferences.getString(key, null))
  }
}