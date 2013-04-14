package peddle

import com.twitter.finagle.{SimpleFilter, Service}
import com.twitter.finagle.http.path._
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.http.Status._
import com.twitter.finagle.http.Version.Http11
import com.twitter.util.Future
import net.liftweb.json.JsonDSL._
import net.liftweb.json._
import org.jboss.netty.handler.codec.http._

class PeddleFilter extends SimpleFilter[HttpRequest, HttpResponse] {
  def apply(req: HttpRequest, service: Service[HttpRequest, HttpResponse]): Future[HttpResponse] = {

  	val bitcoinRequest = Request(new DefaultHttpRequest(Http11, HttpMethod.POST, "/"))
  	bitcoinRequest.addHeader(HttpHeaders.Names.CONTENT_TYPE, "application/x-www-form-urlencoded")

  	//match the incoming request
  	val request = Request(req.getUri)
 		request.method -> Path(request.path) match {
  	  case HttpMethod.GET -> Root / "accounts" => {
  	  	bitcoinRequest.contentString = """{"method": "listaccounts", "params": [] }"""
		    bitcoinRequest.addHeader(HttpHeaders.Names.CONTENT_LENGTH, bitcoinRequest.contentString.length.toString())
  	  	service(bitcoinRequest)
  	  }	
  	  case HttpMethod.POST -> Root / "accounts" => {
  	  	//create a new account
  	  	service(bitcoinRequest);
  	  }	
      case HttpMethod.GET -> Root / "accounts" / id / "addresses" => {
      	val json = ("method" -> "getaddressesbyaccount") ~ ("params" -> List(id))
		    bitcoinRequest.contentString = compact(render(json))
		    bitcoinRequest.addHeader(HttpHeaders.Names.CONTENT_LENGTH, bitcoinRequest.contentString.length.toString())
  	  	service(bitcoinRequest)
  	  }
      case HttpMethod.POST -> Root / "accounts" / id / "addresses" => {
      	service(bitcoinRequest)
  	  }
      case HttpMethod.GET -> Root / "accounts" / id / "transactions" => {
		    val json = ("method" -> "listtransactions") ~ ("params" -> List(id))
		    bitcoinRequest.contentString = compact(render(json))
		    bitcoinRequest.addHeader(HttpHeaders.Names.CONTENT_LENGTH, bitcoinRequest.contentString.length.toString())
  	  	service(bitcoinRequest)
  	  }
      case HttpMethod.POST -> Root / "transactions" => {
      	//create a new transaction (between two address)
  	  	service(bitcoinRequest)
  	  }
      case HttpMethod.GET -> Root / "transactions" / id => {
      	val json = ("method" -> "gettransaction") ~ ("params" -> List(id))
		    bitcoinRequest.contentString = compact(render(json))
		    bitcoinRequest.addHeader(HttpHeaders.Names.CONTENT_LENGTH, bitcoinRequest.contentString.length.toString())
  	  	service(bitcoinRequest)
  	  }
      case HttpMethod.POST -> Root / "transfer" => {
      	//create a transfer between accounts
  	  	service(bitcoinRequest)
  	  }
      case _ =>
        Future value Response(Http11, NotFound)
    }
  }
}