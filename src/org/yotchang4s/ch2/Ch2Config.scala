package org.yotchang4s.ch2

import org.yotchang4s.http.HttpConfig

object Ch2Config {
  val defaultBoardUrl = "http://menu.2ch.net/bbsmenu.html"
}

trait Ch2Config extends HttpConfig {
  def boardUrl: Option[String] = Some(Ch2Config.defaultBoardUrl)
}
