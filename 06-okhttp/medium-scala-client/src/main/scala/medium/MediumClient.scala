package medium

import medium.MediumApiProtocol._
import medium.domainObjects._
import okhttp3._
import spray.json._

class MediumClient(clientId: String, clientSecret: String, var accessToken: Option[String] = None) {
  val client = new OkHttpClient()

  val baseApiUrl: HttpUrl = new HttpUrl.Builder()
    .scheme("https")
    .host("api.medium.com")
    .build()

  def getUser: User = accessToken match {
    case Some(at) =>
      val request = new Request.Builder()
        .header("Content-Type", "application/json")
        .header("Accept", "application/json")
        .header("Accept-Charset", "utf-8")
        .header("Authorization", s"Bearer $at")
        .url(baseApiUrl.resolve("/v1/me"))
        .get()
        .build()
      makeRequest[User](request)
    case _ => throw new MediumException("Please set access token")
  }

  def createPost(authorId: String, postRequest: PostRequest): Post = accessToken match {
    case Some(at) =>
      val httpUrl = baseApiUrl.resolve(s"/v1/users/$authorId/posts")
      val request = new Request.Builder()
        .header("Content-Type", "application/json")
        .header("Accept", "application/json")
        .header("Accept-Charset", "utf-8")
        .header("Authorization", s"Bearer $at")
        .url(httpUrl)
        .post(RequestBody.create(MediaType.parse("application/json"), postRequest.toJson.prettyPrint))
        .build()
      makeRequest[Post](request)
    case _ => throw new MediumException("Please set access token")
  }


  private def makeRequest[T](request: Request)(implicit p: JsonReader[T]): T= {
    val response = client.newCall(request).execute()
    val responseJson = response.body().string()
    println(s"Received response $responseJson")
    response match {
      case r if r.isSuccessful =>
        val jsValue: JsValue = responseJson.parseJson
        jsValue.asJsObject.getFields("data").headOption match {
          case Some(data) => data.convertTo[T]
          case _ => throw new MediumException(s"Received unexpected JSON response $responseJson")
        }
      case _ => throw new MediumException(s"Received HTTP error response code ${response.code()}")
    }
  }



}

object MediumClient {
  def apply(clientId: String, clientSecret: String): MediumClient = new MediumClient(clientId, clientSecret)

  def apply(clientId: String, clientSecret: String, accessToken: String): MediumClient = new MediumClient(clientId, clientSecret, Some(accessToken))
}

case class MediumException(message: String, cause: Throwable = null) extends RuntimeException(message, cause)

