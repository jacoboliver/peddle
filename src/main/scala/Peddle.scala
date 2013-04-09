import com.twitter.finagle.{Http, Service}
import com.twitter.util.{Await, Future}
import java.net.InetSocketAddress
import org.jboss.netty.handler.codec.http._
import com.twitter.finagle.builder.{ClientBuilder, ServerBuilder}
import org.jboss.netty.handler.codec.http.{HttpRequest, HttpResponse, HttpHeaders}
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.base64.Base64;
import org.jboss.netty.util.CharsetUtil;
import java.net.InetSocketAddress
import util.Properties


object Peddle extends App {
  val bitcoin_port = Properties.envOrElse("BITCOIN_PORT", "8332").toInt
  val listen_port = Properties.envOrElse("LISTEN_PORT", "8080").toInt
  val bit_user = Properties.envOrElse("BITCOIN_USERNAME", "user")
  val bit_pass = Properties.envOrElse("BITCOIN_PASSWORD", "password")
  val authString = bit_user + ":" + bit_pass;

  val client: Service[HttpRequest, HttpResponse] =
    Http.newService("localhost:" + bitcoin_port)
    
  val service = new Service[HttpRequest, HttpResponse] {
    def apply(request: HttpRequest): Future[HttpResponse] = {
	    val authChannelBuffer = ChannelBuffers.copiedBuffer(authString, CharsetUtil.UTF_8);
	    val encodedAuthChannelBuffer = Base64.encode(authChannelBuffer);
	    request.addHeader(HttpHeaders.Names.AUTHORIZATION, "Basic " + encodedAuthChannelBuffer.toString(CharsetUtil.UTF_8));
      	client(request)
      }
  }

  val server = Http.serve(new InetSocketAddress(listen_port), service)
  Await.ready(server)
}