package peddle

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Method, Request, Response, Status}
import com.twitter.util.Future
import net.liftweb.json.JsonDSL._
import net.liftweb.json._
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.base64.Base64;
import org.jboss.netty.handler.codec.http._
import org.jboss.netty.util.CharsetUtil
import org.specs2.mutable._

object PeddleFilterSpec extends Specification {	
  val dummyService = new Service[HttpRequest, HttpResponse] {
    def apply(req: HttpRequest) = {
      val request = Request(req)
      val response = Response(new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK))
      response.contentString = "{ \"result\" : true }"
      Future.value(response.httpResponse)
    }
  }

  "PeddleFilter" should {
    "return 200 OK on GET /accounts" in {
      val request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/accounts")


      val peddleFilter = new PeddleFilter

      val response = Response(peddleFilter(request, dummyService)())
      response.status must be equalTo Status.Ok
    }

    "return 200 OK on GET /accounts/{ID}/addresses" in {
      val request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/accounts/oliverj/addresses")


      val peddleFilter = new PeddleFilter

      val response = Response(peddleFilter(request, dummyService)())
      response.status must be equalTo Status.Ok
    }

    "return 200 OK on GET /accounts/{ID}/transactions" in {
      val request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/accounts/oliverj/transactions")


      val peddleFilter = new PeddleFilter

      val response = Response(peddleFilter(request, dummyService)())
      response.status must be equalTo Status.Ok
    }

    "return 200 OK on GET /transactions/{ID}" in {
      val request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/transactions/asdflkjasdf")


      val peddleFilter = new PeddleFilter

      val response = Response(peddleFilter(request, dummyService)())
      response.status must be equalTo Status.Ok
    }

    "return 200 OK on POST /accounts -d 'id={id}'" in {
      val request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/accounts")
      request.setHeader(HttpHeaders.Names.CONTENT_TYPE,"application/x-www-form-urlencoded")     
      val params = "id=oliverj"
      val cb = ChannelBuffers.copiedBuffer(params,CharsetUtil.UTF_8)
      request.setHeader(HttpHeaders.Names.CONTENT_LENGTH, cb.readableBytes())
      request.setContent(cb)

      val peddleFilter = new PeddleFilter

      val response = Response(peddleFilter(request, dummyService)())
      response.status must be equalTo Status.Ok
    }

    "return 200 OK on POST /transactions -d 'from={id}' -d 'to={id}' -d 'amount={amt}'" in {
      val request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/transactions")
      request.setHeader(HttpHeaders.Names.CONTENT_TYPE,"application/x-www-form-urlencoded")     
      val params = "from=oliverj&to=jacoboliver&amount=2"
      val cb = ChannelBuffers.copiedBuffer(params,CharsetUtil.UTF_8)
      request.setHeader(HttpHeaders.Names.CONTENT_LENGTH, cb.readableBytes())
      request.setContent(cb)

      val peddleFilter = new PeddleFilter

      val response = Response(peddleFilter(request, dummyService)())
      response.status must be equalTo Status.Ok
    }

    "return Bad Request on POST /transactions -d 'from={id}' -d 'amount={amt}'" in {
      val request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/transactions")
      request.setHeader(HttpHeaders.Names.CONTENT_TYPE,"application/x-www-form-urlencoded")     
      val params = "from=oliverj&amount=2"
      val cb = ChannelBuffers.copiedBuffer(params,CharsetUtil.UTF_8)
      request.setHeader(HttpHeaders.Names.CONTENT_LENGTH, cb.readableBytes())
      request.setContent(cb)

      val peddleFilter = new PeddleFilter

      val response = Response(peddleFilter(request, dummyService)())
      response.status must be equalTo Status.BadRequest
    }

    "return Bad Request on POST /transactions -d 'to={id}' -d 'amount={amt}'" in {
      val request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/transactions")
      request.setHeader(HttpHeaders.Names.CONTENT_TYPE,"application/x-www-form-urlencoded")     
      val params = "to=oliverj&amount=2"
      val cb = ChannelBuffers.copiedBuffer(params,CharsetUtil.UTF_8)
      request.setHeader(HttpHeaders.Names.CONTENT_LENGTH, cb.readableBytes())
      request.setContent(cb)

      val peddleFilter = new PeddleFilter

      val response = Response(peddleFilter(request, dummyService)())
      response.status must be equalTo Status.BadRequest
    }

    "return Bad Request on POST /transactions -d 'to={id}' -d from={id} -d 'amount=0'" in {
      val request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/transactions")
      request.setHeader(HttpHeaders.Names.CONTENT_TYPE,"application/x-www-form-urlencoded")     
      val params = "to=oliverj&from=jacobo&amount=0"
      val cb = ChannelBuffers.copiedBuffer(params,CharsetUtil.UTF_8)
      request.setHeader(HttpHeaders.Names.CONTENT_LENGTH, cb.readableBytes())
      request.setContent(cb)

      val peddleFilter = new PeddleFilter

      val response = Response(peddleFilter(request, dummyService)())
      response.status must be equalTo Status.BadRequest
    }

    "return 200 OK on POST /transfer -d 'from={id}' -d 'to={id}' -d 'amount={amt}'" in {
      val request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/transfer")
      request.setHeader(HttpHeaders.Names.CONTENT_TYPE,"application/x-www-form-urlencoded")     
      val params = "from=oliverj&to=jacoboliver&amount=2"
      val cb = ChannelBuffers.copiedBuffer(params,CharsetUtil.UTF_8)
      request.setHeader(HttpHeaders.Names.CONTENT_LENGTH, cb.readableBytes())
      request.setContent(cb)

      val peddleFilter = new PeddleFilter

      val response = Response(peddleFilter(request, dummyService)())
      response.status must be equalTo Status.Ok
    }

    "return Bad Request on POST /transfer -d 'from={id}' -d 'amount={amt}'" in {
      val request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/transfer")
      request.setHeader(HttpHeaders.Names.CONTENT_TYPE,"application/x-www-form-urlencoded")     
      val params = "from=oliverj&amount=2"
      val cb = ChannelBuffers.copiedBuffer(params,CharsetUtil.UTF_8)
      request.setHeader(HttpHeaders.Names.CONTENT_LENGTH, cb.readableBytes())
      request.setContent(cb)

      val peddleFilter = new PeddleFilter

      val response = Response(peddleFilter(request, dummyService)())
      response.status must be equalTo Status.BadRequest
    }

    "return Bad Request on POST /transfer -d 'to={id}' -d 'amount={amt}'" in {
      val request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/transfer")
      request.setHeader(HttpHeaders.Names.CONTENT_TYPE,"application/x-www-form-urlencoded")     
      val params = "to=oliverj&amount=2"
      val cb = ChannelBuffers.copiedBuffer(params,CharsetUtil.UTF_8)
      request.setHeader(HttpHeaders.Names.CONTENT_LENGTH, cb.readableBytes())
      request.setContent(cb)

      val peddleFilter = new PeddleFilter

      val response = Response(peddleFilter(request, dummyService)())
      response.status must be equalTo Status.BadRequest
    }

    "return Bad Request on POST /transfer -d 'to={id}' -d from={id} -d 'amount=0'" in {
      val request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/transfer")
      request.setHeader(HttpHeaders.Names.CONTENT_TYPE,"application/x-www-form-urlencoded")     
      val params = "to=oliverj&from=jacobo&amount=0"
      val cb = ChannelBuffers.copiedBuffer(params,CharsetUtil.UTF_8)
      request.setHeader(HttpHeaders.Names.CONTENT_LENGTH, cb.readableBytes())
      request.setContent(cb)

      val peddleFilter = new PeddleFilter

      val response = Response(peddleFilter(request, dummyService)())
      response.status must be equalTo Status.BadRequest
    }

    "return Not Found /not_found" in {
      val request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/not_found")


      val peddleFilter = new PeddleFilter

      val response = Response(peddleFilter(request, dummyService)())
      response.status must be equalTo Status.NotFound
    }
  }
}