package org.yotchang4s.ch2

import Ch2Exception._

object Ch2Exception {
  sealed trait ErrorType
  case object ApplicationError extends ErrorType
  case object IOError extends ErrorType
  case object UnknownError extends ErrorType
  case object NoImplements extends ErrorType
}

@serializable
@SerialVersionUID(1L)
class Ch2Exception(val errorType: ErrorType, message: String = null, cause: Throwable = null)
  extends Exception(message, cause) {

  def this(errorType: ErrorType, cause: Throwable) = this(UnknownError, null, cause)
}
