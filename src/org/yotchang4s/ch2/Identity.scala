package org.yotchang4s.ch2

@serializable
trait Identity[+A] extends Serializable {
  val value: A
}