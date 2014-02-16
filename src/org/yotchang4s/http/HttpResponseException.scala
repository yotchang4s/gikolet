package org.yotchang4s.http

@serializable
@SerialVersionUID(1L)
class HttpResponseException(val httpResponse: HttpResponse, message: String = null, cause: Throwable = null) {

  def this(httpResponse: HttpResponse, cause: Throwable) = this(httpResponse, null, cause)
}
