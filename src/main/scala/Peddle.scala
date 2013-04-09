package peddle

import com.twitter.finagle.{Http, Service}
import com.twitter.util.{Await, Future}
import java.net.InetSocketAddress
import org.jboss.netty.handler.codec.http._
import com.twitter.finagle.builder.{ClientBuilder, ServerBuilder}
import org.jboss.netty.handler.codec.http.{HttpRequest, HttpResponse, HttpHeaders}
import java.net.InetSocketAddress
import util.Properties


object Peddle extends App {
  val bitcoin_port = Properties.envOrElse("BITCOIN_PORT", "8332").toInt
  val listen_port = Properties.envOrElse("LISTEN_PORT", "8080").toInt

  val basicAuthFilter = new BasicAuthentication

  val client: Service[HttpRequest, HttpResponse] =
    Http.newService("localhost:" + bitcoin_port)
    
  val service: Service[HttpRequest, HttpResponse] =
    basicAuthFilter andThen client

  val server = Http.serve(new InetSocketAddress(listen_port), service)
  Await.ready(server)
}