package peddle

import com.twitter.finagle.{SimpleFilter, Service}
import com.twitter.finagle.http.path._
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.http.Status._
import com.twitter.finagle.http.Version._
import com.twitter.util.Future
import net.liftweb.json.JsonDSL._
import net.liftweb.json._
import org.jboss.netty.handler.codec.http._

class PeddleFilter extends SimpleFilter[HttpRequest, HttpResponse] {
  def apply(req: HttpRequest, service: Service[HttpRequest, HttpResponse]): Future[HttpResponse] = {

    val bitcoinRequest = Request(new DefaultHttpRequest(Http11, HttpMethod.POST, "/"))
    bitcoinRequest.addHeader(HttpHeaders.Names.CONTENT_TYPE, "application/x-www-form-urlencoded")

    val peddleResponse = Response(new DefaultHttpResponse(req.getProtocolVersion, HttpResponseStatus.OK))

    //match the incoming request
    val request = Request(req)
    request.method -> Path(request.path) match {
      case HttpMethod.GET -> Root / "accounts" => {
        bitcoinRequest.contentString = """{"method": "listaccounts", "params": [] }"""
        bitcoinRequest.addHeader(HttpHeaders.Names.CONTENT_LENGTH, bitcoinRequest.contentString.length.toString())
        service(bitcoinRequest) map { res => {
            val response = Response(res)
            val response_result = parse(response.contentString) \ "result"
            val accounts_json = ("accounts" -> List(response_result))
            peddleResponse.contentString = compact(render(accounts_json))
            peddleResponse
          }
        }
      } 
      case HttpMethod.POST -> Root / "accounts" => {
        val id = request.getParam("id")
        val json = ("method" -> "getaccountaddress") ~ ("params" -> List(id))
        bitcoinRequest.contentString = compact(render(json))
        bitcoinRequest.addHeader(HttpHeaders.Names.CONTENT_LENGTH, bitcoinRequest.contentString.length.toString())
        service(bitcoinRequest) map { res => {
            val accounts_json = ("accounts" -> List(id))
            peddleResponse.contentString = compact(render(accounts_json))
            peddleResponse
          }
        }
      } 
      case HttpMethod.GET -> Root / "accounts" / id / "addresses" => {
        val json = ("method" -> "getaddressesbyaccount") ~ ("params" -> List(id))
        bitcoinRequest.contentString = compact(render(json))
        bitcoinRequest.addHeader(HttpHeaders.Names.CONTENT_LENGTH, bitcoinRequest.contentString.length.toString())
        service(bitcoinRequest) map { res => {
            val response = Response(res)
            val response_result = parse(response.contentString) \ "result"
            val accounts_json = ("addresses" -> List(response_result))
            peddleResponse.contentString = compact(render(accounts_json))
            peddleResponse
          }
        }     
      }
      case HttpMethod.GET -> Root / "accounts" / id / "transactions" => {
        val json = ("method" -> "listtransactions") ~ ("params" -> List(id))
        bitcoinRequest.contentString = compact(render(json))
        bitcoinRequest.addHeader(HttpHeaders.Names.CONTENT_LENGTH, bitcoinRequest.contentString.length.toString())
        service(bitcoinRequest) map { res => {
            val response = Response(res)
            val response_result = parse(response.contentString) \ "result"
            val accounts_json = ("transactions" -> List(response_result))
            peddleResponse.contentString = compact(render(accounts_json))
            peddleResponse
          }
        }
      }
      case HttpMethod.POST -> Root / "transactions" => {
        val fromAccount = request.getParam("from")
        val toAccount = request.getParam("to")
        val amount = if (request.getParam("amount") != null) request.getParam("amount").toDouble else 0

        (fromAccount, toAccount, amount) match {
          case (null , toAccount, amount) => {
            val errorResponse = Response(Http11, BadRequest)
            errorResponse.contentString = compact(render(("message" -> "From Account missing") ~ ("param" -> "from")))
            Future.value(errorResponse)
          }
          case (fromAccount , null, amount) => {
            val errorResponse = Response(Http11, BadRequest)
            errorResponse.contentString = compact(render(("message" -> "To Account missing") ~ ("param" -> "to")))
            Future.value(errorResponse)
          }
          case (fromAccount , toAccount, 0) => {
            val errorResponse = Response(Http11, BadRequest)
            errorResponse.contentString = compact(render(("message" -> "Amount missing") ~ ("param" -> "amount")))
            Future.value(errorResponse)
          }
          case (fromAccount, toAccount, amount) => {
            val json = ("method" -> "move") ~ JObject(List(JField("params",JArray(List(JString(fromAccount), JString(toAccount), JDouble(amount))))))
            bitcoinRequest.contentString = compact(render(json))
            bitcoinRequest.addHeader(HttpHeaders.Names.CONTENT_LENGTH, bitcoinRequest.contentString.length.toString())
            service(bitcoinRequest) map { res => {
                val response = Response(res)
                val response_result = response.contentString
                peddleResponse.contentString = compact(render(("transaction" -> response_result)))
                peddleResponse
              }
            }
          }
        }
      }
      case HttpMethod.GET -> Root / "transactions" / id => {
        val json = ("method" -> "gettransaction") ~ ("params" -> List(id))
        bitcoinRequest.contentString = compact(render(json))
        bitcoinRequest.addHeader(HttpHeaders.Names.CONTENT_LENGTH, bitcoinRequest.contentString.length.toString())
        service(bitcoinRequest) map { res => {
            val response = Response(res)
            val response_result = parse(response.contentString) \ "result"
            val accounts_json = ("accounts" -> List(response_result))
            peddleResponse.contentString = compact(render(accounts_json))
            peddleResponse
          }
        }
      }
      case HttpMethod.POST -> Root / "transfer" => {
        val fromAccount = request.getParam("from")
        val toAccount = request.getParam("to")
        val amount = if (request.getParam("amount") != null) request.getParam("amount").toDouble else 0
        (fromAccount, toAccount, amount) match {
          case (null , toAccount, amount) => {
            val errorResponse = Response(Http11, BadRequest)
            errorResponse.contentString = compact(render(("message" -> "From Account missing") ~ ("param" -> "from")))
            Future.value(errorResponse)
          }
          case (fromAccount , null, amount) => {
            val errorResponse = Response(Http11, BadRequest)
            errorResponse.contentString = compact(render(("message" -> "To Account missing") ~ ("param" -> "to")))
            Future.value(errorResponse)
          }
          case (fromAccount, toAccount, 0) => {
            val errorResponse = Response(Http11, BadRequest)
            errorResponse.contentString = compact(render(("message" -> "Amount missing") ~ ("param" -> "amount")))
            Future.value(errorResponse)
          }
          case (fromAccount, toAccount, amount) => {
            val json = ("method" -> "move") ~ JObject(List(JField("params",JArray(List(JString(fromAccount), JString(toAccount), JDouble(amount))))))
            bitcoinRequest.contentString = compact(render(json))
            bitcoinRequest.addHeader(HttpHeaders.Names.CONTENT_LENGTH, bitcoinRequest.contentString.length.toString())
            service(bitcoinRequest) map { res => {
                val response = Response(res)
                val response_result = parse(response.contentString) \ "result"
                response_result match {
                case JBool(true) => {
                    peddleResponse.contentString = compact(render(("result" -> true)))
                    peddleResponse
                  }
                  case _ => {
                    val errorResponse = Response(Http11, BadRequest)
                    errorResponse.contentString = compact(render(("message" -> "Insufficient funds")))
                    errorResponse
                  }
                }
              }
            }
          }
        }
      }
      case _ =>
        Future value Response(Http11, NotFound)
    }
  }
}