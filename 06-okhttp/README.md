Building A Lightweight Scala REST API Client with OkHttp
----

Welcome to the sixth blog of [52-technologies-in-2016](https://github.com/shekhargulati/52-technologies-in-2016) blog series. In this blog, we will learn how to write Scala REST API client for [Medium](https://medium.com/)'s REST API using [OkHttp](https://github.com/square/okhttp) library. [REST](https://en.wikipedia.org/wiki/Representational_state_transfer) APIs have become a standard method of communication between two devices over a network. Most applications expose their REST API that developers can use to get work with an application programmatically. For example, if I have to build a realtime opinion mining application then I can use Twitter or Facebook REST APIs to get hold of their data and build my application. To work with an application REST APIs, you either can write your own client or you can use one of the language specific client provided by the application. Last few weeks, I have started using [Medium](https://medium.com/) for posting non-technical blogs. Medium is a blog publishing platform created by Twitter co-founder <a href="https://en.wikipedia.org/wiki/Evan_Williams_(Internet_entrepreneur)">Evan Williams</a>. Evan Williams is the same guy who earlier created <a href="https://en.wikipedia.org/wiki/Blogger_(service)">Blogger</a>, which was bought by Google in 2003.

Medium exposed their REST API to the external world last year. The API is simple and allows you to do operations like submitting a post, getting details of the authenticated user, getting publications for a user, etc. You can read about Medium API documentation in their [Github repository](https://github.com/Medium/medium-api-docs). Medium officially provides REST API clients for [Node.js](https://github.com/Medium/medium-sdk-nodejs), [Python](https://github.com/Medium/medium-sdk-python), and [Go](https://github.com/Medium/medium-sdk-go) programming languages. I couldn't find Scala client for Medium REST API so I decided to write my own client using OkHttp.

## What is OkHttp?

[OkHttp](https://square.github.io/okhttp/) is an open source Java HTTP client library focussed on efficiency. It is written by folks at [Square](https://squareup.com/). It supports [SPDY](https://developers.google.com/speed/spdy/), [HTTP/2](https://http2.github.io/), and [WebSocket](https://tools.ietf.org/html/rfc6455) protocols.

OkHttp API is very easy to use. You just have to add its dependency to your classpath and then you can start using it to build your clients.

According to [OkHttp documentation](https://square.github.io/okhttp/),

> OkHttp is an HTTP client that’s efficient by default:
* HTTP/2 support allows all requests to the same host to share a socket.
* Connection pooling reduces request latency (if HTTP/2 isn’t available).
* Transparent GZIP shrinks download sizes.
* Response caching avoids the network completely for repeat requests.

## Why are you using a Java library?

I know you must be thinking why I am using a Java library to build a Scala REST API client. Like most Scala developers, I thought of using a Scala library instead. But, as I started looking into which Scala library should I use I didn't find any single winner. If you search for "Scala REST client", you will land up on this [StackOverFlow question](https://stackoverflow.com/questions/12334476/simple-and-concise-http-client-library-for-scala). It suggests four libraries Dispatch, Scalaz http, spray-client, Play WS. Let's discuss why I didn't used them one by one.

1. [Dispatch](https://github.com/dispatch/reboot): It is a Scala wrapper around Ning's Async Http Client. The project doesn't look very active with last commit on [May 30, 2015](https://github.com/dispatch/reboot/commits/0.11.3). The [travis-ci build](https://travis-ci.org/dispatch/reboot) is also broken so I am not sure if this project is actively maintained.

2. [Scalaz](https://github.com/scalaz/scalaz/) http: Scalaz is an extension to the core Scala library for functional programming. They used to have an HTTP client. They [dropped http module from Scalaz in version 7](https://stackoverflow.com/questions/25482520/what-happened-to-the-scalaz-http-module).

3. [spray-client](http://spray.io/documentation/1.2.2/spray-client/):  It provides high-level HTTP client functionality by adding another logic layer on top of the relatively basic spray-can HTTP Client APIs. spray-client depends on many other spray projects and Akka. I didn't wanted to use a library that depends on so many other libraries.

4. [Play WS](https://www.playframework.com/documentation/2.5.x/ScalaWS): Play WS is part of the Scala's Play web framework. It can used in standalone mode but it also depends on [many other libraries](http://mvnrepository.com/artifact/com.typesafe.play/play-ws_2.11/2.4.6). It also looked very heavy weight for something simple. So, I decided not to use it as well.

## Why OkHttp?

My reasons for going with OkHttp are:

1. It has only one dependency Okio. [Okio](https://github.com/square/okio) is a library that complements java.io and java.nio to make it much easier to access, store, and process your data.
2. It has very good testing support. It provides [scriptable web server](https://github.com/square/okhttp/tree/master/mockwebserver) for testing HTTP client. This makes it easy to test whether your client is doing the right thing without depending on the network.
3. OkHttp is one of the few libraries that is designed up front for efficiency.
4. Stable and actively developed by Square. Last commit was 15 hours ago.
5. API is very simple and intuitive to use. It comes with good defaults and works like a charm.

Although, OkHttp is a Java library but it works great with Scala. I know it might not be the Scala way but sometimes we have to become pragmatic and choose the right tool for the job. There is also an OkHttp Scala wrapper called [Communicator](https://github.com/Taig/Communicator) that one can use.

## Github repository

The code for today’s application is available on github: [medium-scala-client](./medium-scala-client). In this blog, I will only cover couple of REST endpoints. You can view the full source of [medium-scala-sdk here](https://github.com/shekhargulati/medium-scala-sdk).

## Getting Started

Start by creating a new directory `medium-scala-client` at a convenient location on your filesystem. This directory will house the source code of our client.

```bash
$ mkdir medium-scala-client
```

Create a new file `build.sbt` inside the `medium-scala-client` directory. `build.sbt` is the sbt build script.
> **If you are new to sbt, then [please refer to my earlier post on it](../02-sbt/README.md).**

Populate `build.sbt` with following contents.

```scala
name := "medium-scala-client"

version := "1.0"

description := "Scala client for Medium.com REST API"

scalaVersion := "2.11.7"

libraryDependencies += "com.squareup.okhttp3" % "okhttp" % "3.0.1"

libraryDependencies += "io.spray" %% "spray-json" % "1.3.2"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.6" % "test"

libraryDependencies += "com.squareup.okhttp3" % "mockwebserver" % "3.0.1" % "test"
```

In the build script shown above, you can see that we have only added two compile time dependencies -- `okhttp` and `spray-json`. [spray-json](https://github.com/spray/spray-json) is a lightweight, clean, and efficient library to work with JSON in Scala. It has no dependencies. We will use it to convert our domain objects into JSON and vice-versa. `scalatest` and `mockwebserver` are added for testing.

Create a project layout for your Scala source and test files.

```bash
$ mkdir -p src/main/{scala,resources}
$ mkdir -p src/test/scala
```

## Getting the authenticated user's details

Let's start with implementing the REST endpoint to get details of an authenticated user. To get the details of a user, we have to make an HTTP GET request.

```
GET https://api.medium.com/v1/me
```

We will start with writing a test. Create a new package `medium` inside the `src/test/scala`. After creating the package, create a Scala class `MediumClientSpec`. Populate the `MediumClientSpec` with following contents.

```scala
package medium

import okhttp3.mockwebserver.MockWebServer
import org.scalatest.{BeforeAndAfterEach, FunSpec, Matchers}

class MediumClientSpec extends FunSpec with Matchers  with BeforeAndAfterEach{

  var server: MockWebServer = _

  override protected def beforeEach(): Unit = {
    server = new MockWebServer()
  }

  override protected def afterEach(): Unit = {
    server.shutdown()
  }
}
```

The code shown above does the following:

1. We created a new class `MediumClientSpec` that extended `FunSpec`, `Matchers`, and `BeforeAndAfterEach` traits. These are part of `scalatest` library.
2. We override two methods of `BeforeAndAfterEach` trait. `beforeEach` will make sure that `MockWebServer` instance is created before each test case is executed. `MockWebServer` is a scriptable web server. You can configure it to return mock responses for your requests. It works very similarly to any mocking framework. You first set your expectations, then run the application code, and finally verify that expected requests were made.
3. `afterEach` will make sure that server is shutdown after each test.

Add the following test case to the `MediumClientSpec`. This code should be added after the `afterEach` method.

```scala
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
}
```

Let's understand the code show above:

1. We created a json that will be returned by `MockWebServer` when GET request is made to `https://api.medium.com/v1/me`.
2. Then, we set up the server with a mock response. We set the body to the json created in step 1. Also, we added HTTP headers that will be passed in the response.
3. Next, we started the mock web server so that it can accept test requests.
4. Then, we created an instance of MediumClient(that we will create later in the blog). We have to set the URL returned by our server in the client so that it makes requests to the mock server instead of hitting the actual Medium API. This is the reason we have overridden `baseApiUrl` value of `MediumClient`.
5. Finally, we called the `getUser` method of `MediumClient` and asserted its response.

Now that we have written our test case we should start working on the implementation of MediumClient. Create a new package `medium` inside `src/main/scala`. Then, create a new Scala class `MediumClient` inside the `medium` package.

```scala
package medium

class MediumClient(clientId: String, clientSecret: String, var accessToken: Option[String] = None)

object MediumClient {
  def apply(clientId: String, clientSecret: String): MediumClient = new MediumClient(clientId, clientSecret)

  def apply(clientId: String, clientSecret: String, accessToken: String): MediumClient = new MediumClient(clientId, clientSecret, Some(accessToken))
}

case class MediumException(message: String, cause: Throwable = null) extends RuntimeException(message, cause)
```

The code shown above does the following:

1. We created a new Scala class `MediumClient`. The primary constructor of MediumClient takes three arguments -- `clientId`, `clientSecret`, and `accessToken`. The clientId and clientSecret are created for you when you create new Medium application [http://medium.com/me/applications](http://medium.com/me/applications). Using the clientId and clientSecret, users can generate `accessToken`. You have to pass `accessToken` in each request to the Medium API.
2. Then, we created a companion object to the `MediumClient`. It provides factory methods to easily construct `MediumClient` instances.
3. `MediumException` is a runtime exception that we will throw when client will not be able to process user requests.

Create an instance of `OkHttpClient` inside the `MediumClient` class as shown below. `OkHttpClient` is used to send HTTP requests and read HTTP responses. When you create the `OkHttpClient` instance using the default constructor then an `OkHttpClient` instance is created using the default values. You can also create an instance configured using other values by using the `OkHttpClient.Builder` API. We also created another value `baseApiUrl` of type `okhttp3.HttpUrl`. This will store the base URL of the Medium API i.e. `https://api.medium.com`.

```scala
import okhttp3.{HttpUrl, OkHttpClient}

class MediumClient(clientId: String, clientSecret: String, var accessToken: Option[String] = None) {
  val client = new OkHttpClient()

  val baseApiUrl: HttpUrl = new HttpUrl.Builder()
    .scheme("https")
    .host("api.medium.com")
    .build()

}
```

Now, we will write the `getUser` method that will make an HTTP GET request to the Medium API to fetch the user details. User is determined using the `accessToken`. If access token is not set then it will throw `MediumException`.

```scala
def getUser: User = accessToken match {
  case Some(at) => ???
  case _ => throw new MediumException("Please set access token")
}
```

> The Scala syntax `???` lets you write a not yet implemented method. This allows you to write code that compiles. But, if you run this code, then it will thrown an exception.

The code shown above needs `User` to compile. Create a new Scala object `domainObjects`. The `domainObjects.scala` will house all our domain objects like `User`, `Post`, etc. Create a case class for `User` inside it as shown below.

```scala
package medium

object domainObjects {

  case class User(id: String, username: String, name: String, url: String, imageUrl: String)

}
```

As shown above, we created a User case class with five fields inside the `domainObjects` Scala object.

After creating the `domainObjects` Scala object, add its import in the `MediumClient` so that code can compile.

```scala
import domainObjects._

class MediumClient(clientId: String, clientSecret: String, var accessToken: Option[String] = None)
```

Now, let's write code to replace `???` with actual implementation inside the `getUser` method.

```scala
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

private def makeRequest[T](request: Request)(implicit p: JsonReader[T]): T = ???
```

In the code shown above:

1. We created request using the OkHttp `Request.Builder` API. We set the required headers in the request and set url of the request to `/v1/me`. `HttpUrl.resolve` method resolves the url against the baseApiUrl. So, the full url will become `https://api.medium.com/v1/me`. OkHttp understands which HTTP method to use by looking at the request. As you can see above, we called the `get` method of the request builder. This constructs an immutable `okhttp3.Request` object.
2. Once request is created, we passed the request to `makeRequest` method. This method will process any request be it GET or POST or DELETE and return the domain object.

Now, we will implement `makeRequest` method. `makeRequest` method makes use of `OkHttpClient` instance to create a new `Call`. To make the HTTP call, we first called `newCall` method on `OkHttpClient` instance. The `newCall` returns `okhttp3.Call` object. OkHttp uses `Call` to model the task of satisfying your request through however many intermediate requests and responses are necessary. Calls can be executed in synchronous or asynchronous manner. In the code shown below, we called the `execute` method to make a synchronous HTTP GET call.

```scala
private def makeRequest[T](request: Request)(implicit p: JsonReader[T]): T= {
  val response = client.newCall(request).execute()
  val responseJson = response.body().string()
  println(s"Received response $responseJson")
  ???
}
```
If you run the test method now, it will render the `json` response we have set in the test.

```javascript
Received response
{
  "data": {
    "id": "123",
    "username": "shekhargulati",
    "name": "Shekhar Gulati",
    "url": "https://medium.com/@shekhargulati",
    "imageUrl": "https://cdn-images-1.medium.com/fit/c/200/200/1*pC-eYQUV-iP2Y10_LgGvwA.jpeg"
  }
}
```

Now, let's take a look at the last bit of code required to convert json into `User` object. To convert json into User object, we will make use of `spray-json` library.

To use `spray-json`, we have to first add few imports so that relevant elements are added in the scope of  our `MediumClient`.

```scala
import spray.json._
import DefaultJsonProtocol._
```

After adding the imports, you can convert the json string into User object as shown below.

```scala
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
```

The code shown above will not compile as you have to bring implicit values in scope that provide `JsonFormat[User]` instances for User.

Create a new object `MediumApiProtocol` that will define a `JsonFormat` to convert `User` into JSON.

```scala
package medium

import medium.domainObjects.User
import spray.json.DefaultJsonProtocol

object MediumApiProtocol extends DefaultJsonProtocol{

  implicit val userFormat = jsonFormat5(User)

}
```

Now, code will compile and test case will pass.

## Posting a blog on Medium

Let's now implement method that will create a post on Medium. To create a post, we have to use HTTP POST method as we are creating a resource on the server. Let's write a test method, that will test the post creation.

```scala
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
```

Next, add `PostRequest` and `Post` case classes to `domainObjects.scala`.

```scala
package medium

object domainObjects {

  case class User(id: String, username: String, name: String, url: String, imageUrl: String)

  case class PostRequest(title: String, contentFormat: String, content: String, tags: Array[String] = Array(), canonicalUrl: Option[String] = None, publishStatus: String = "public", license: String = "all-rights-reserved")

  case class Post(id: String, publicationId: Option[String] = None, title: String, authorId: String, tags: Array[String], url: String, canonicalUrl: String, publishStatus: String, publishedAt: Long, license: String, licenseUrl: String)

}
```

Write the JSON formatter in `MediumApiProtocol` as shown below.

```scala
package medium

import medium.domainObjects._
import spray.json._

object MediumApiProtocol extends DefaultJsonProtocol{

  implicit val userFormat = jsonFormat5(User)

  implicit val postRequestFormat = jsonFormat7(PostRequest)

  implicit val postFormat = jsonFormat11(Post)

}
```

Now, we will write `createPost` that will create a Medium post.

```scala
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
```

Now, compile the code and run the test case. Both test cases will pass now.

## Conclusion

This week we learnt how to write REST API using OkHttp library. We covered how to make HTTP GET and POST requests using OkHttp. OkHttp supports all HTTP methods like head, delete, put, etc. You can also use OKHttp to [make asynchronous calls](https://github.com/square/okhttp/wiki/Recipes#asynchronous-get). You can refer to [OkHttp documentation](https://github.com/square/okhttp/wiki) for more details.


That's all for this week. Please provide your valuable feedback by adding a comment to [https://github.com/shekhargulati/52-technologies-in-2016/issues/8](https://github.com/shekhargulati/52-technologies-in-2016/issues/8).

[![Analytics](https://ga-beacon.appspot.com/UA-59411913-2/shekhargulati/52-technologies-in-2016/06-okhttp)](https://github.com/igrigorik/ga-beacon)
