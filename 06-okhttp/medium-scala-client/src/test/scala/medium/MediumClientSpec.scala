package medium

import medium.domainObjects.PostRequest
import okhttp3.mockwebserver.{MockResponse, MockWebServer}
import org.scalatest.{BeforeAndAfterEach, FunSpec, Matchers}

class MediumClientSpec extends FunSpec with Matchers with BeforeAndAfterEach {

  var server: MockWebServer = _

  override protected def beforeEach(): Unit = {
    server = new MockWebServer()
  }

  override protected def afterEach(): Unit = {
    server.shutdown()
  }

  describe("MediumClientSpec") {

    it("should get details of an authenticated user") {
      val json =
        """
          |{
          |  "data": {
          |    "id": "123",
          |    "username": "shekhargulati",
          |    "name": "Shekhar Gulati",
          |    "url": "https://medium.com/@shekhargulati",
          |    "imageUrl": "https://cdn-images-1.medium.com/fit/c/200/200/1*pC-eYQUV-iP2Y10_LgGvwA.jpeg"
          |  }
          |}
        """.stripMargin

      server.enqueue(new MockResponse()
        .setBody(json)
        .setHeader("Content-Type", "application/json")
        .setHeader("charset", "utf-8"))
      server.start()

      val medium = new MediumClient("test_client_id", "test_client_secret", Some("access_token")) {
        override val baseApiUrl = server.url("/v1/me")
      }
      val user = medium.getUser
      user should have(
        'id ("123"),
        'username ("shekhargulati"),
        'name ("Shekhar Gulati"),
        'url ("https://medium.com/@shekhargulati"),
        'imageUrl ("https://cdn-images-1.medium.com/fit/c/200/200/1*pC-eYQUV-iP2Y10_LgGvwA.jpeg")
      )
    }

    it("should publish a new post") {
      val responsJson =
        """
          |{
          | "data": {
          |   "id": "e6f36a",
          |   "title": "Liverpool FC",
          |   "authorId": "5303d74c64f66366f00cb9b2a94f3251bf5",
          |   "tags": ["football", "sport", "Liverpool"],
          |   "url": "https://medium.com/@majelbstoat/liverpool-fc-e6f36a",
          |   "canonicalUrl": "http://jamietalbot.com/posts/liverpool-fc",
          |   "publishStatus": "public",
          |   "publishedAt": 1442286338435,
          |   "license": "all-rights-reserved",
          |   "licenseUrl": "https://medium.com/policy/9db0094a1e0f"
          | }
          |}
        """.stripMargin

      server.enqueue(new MockResponse()
        .setBody(responsJson)
        .setHeader("Content-Type", "application/json")
        .setHeader("charset", "utf-8"))
      server.start()
      val medium = new MediumClient("test_client_id", "test_client_secret", Some("access_token")) {
        override val baseApiUrl = server.url("/v1/users/123/posts")
      }

      val content =
        """
          |# Hello World
          |Hello how are you?
          |## What's up today?
          |Writing REST client for Medium API
        """.stripMargin
      val post = medium.createPost("123", PostRequest("Liverpool FC", "html", content))

      post.id should be("e6f36a")
    }

  }

}