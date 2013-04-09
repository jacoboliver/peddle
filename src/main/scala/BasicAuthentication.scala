package peddle

import org.jboss.netty.handler.codec.http.{HttpRequest, HttpResponse, HttpHeaders}
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.base64.Base64;
import org.jboss.netty.util.CharsetUtil;
import org.jboss.netty.handler.codec.base64.Base64;
import com.twitter.finagle.Filter
import com.twitter.finagle.Service
import com.twitter.util.Future
import util.Properties

class BasicAuthentication()
  extends Filter[HttpRequest, HttpResponse, HttpRequest, HttpResponse] 
{
  val username = Properties.envOrElse("BITCOIN_USERNAME", "user")
  val password = Properties.envOrElse("BITCOIN_PASSWORD", "password")

  def apply(request: HttpRequest, service: Service[HttpRequest, HttpResponse]): Future[HttpResponse] = {
    val authChannelBuffer = ChannelBuffers.copiedBuffer(username + ":" + password, CharsetUtil.UTF_8);
    val encodedAuthChannelBuffer = Base64.encode(authChannelBuffer);
    request.addHeader(HttpHeaders.Names.AUTHORIZATION, "Basic " + encodedAuthChannelBuffer.toString(CharsetUtil.UTF_8));
    service(request)
  }
}