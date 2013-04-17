package peddle

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Method, Status}
import com.twitter.util.Future
import org.jboss.netty.buffer.ChannelBuffers
import org.jboss.netty.handler.codec.base64.Base64
import org.jboss.netty.handler.codec.http._
import org.jboss.netty.util.CharsetUtil
import org.specs2.mutable._

object BasicAuthenticationSpec extends Specification {	
  val dummyService = new Service[HttpRequest, HttpResponse] {
    def apply(request: HttpRequest) = {
      val response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK)
      Future.value(response)
    }
  }

  "BasicAuthenticationFilter" should {
    "add Authorization header" in {
      val request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/")

      val username = "user"
      val password = "password"
      val authChannelBuffer = ChannelBuffers.copiedBuffer(username + ":" + password, CharsetUtil.UTF_8)
      val encodedAuthChannelBuffer = Base64.encode(authChannelBuffer)

      val basicAuthFilter = new BasicAuthentication

      val response = basicAuthFilter(request, dummyService)()
      request.getHeader(HttpHeaders.Names.AUTHORIZATION) must be equalTo("Basic " + encodedAuthChannelBuffer.toString(CharsetUtil.UTF_8))
    }
  }
}