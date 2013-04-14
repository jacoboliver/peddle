package peddle

import com.twitter.finagle.builder.{ClientBuilder, ServerBuilder}
import com.twitter.finagle.{Http, Service}
import com.twitter.util.{Await, Future}
import org.jboss.netty.handler.codec.http.{HttpRequest, HttpResponse, HttpHeaders}
import java.net.InetSocketAddress
import util.Properties


object Peddle extends App {
  val bitcoin_port = Properties.envOrElse("BITCOIN_PORT", "8332").toInt
  val listen_port = Properties.envOrElse("LISTEN_PORT", "8080").toInt

  //filter api requests into bitcoin json-rpc
  val peddleFilter = new PeddleFilter

  //add basic authentication header
  val basicAuthFilter = new BasicAuthentication

  //communicate with the bitcoin via json-rpc
  val client: Service[HttpRequest, HttpResponse] =
    Http.newService("localhost:" + bitcoin_port)
    
  val service: Service[HttpRequest, HttpResponse] =
    peddleFilter andThen basicAuthFilter andThen client

  val server = Http.serve(new InetSocketAddress(listen_port), service)
  Await.ready(server)
}