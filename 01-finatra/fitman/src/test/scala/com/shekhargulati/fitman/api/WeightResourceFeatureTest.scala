package com.shekhargulati.fitman.api

import com.shekhargulati.fitman.FitmanServer
import com.twitter.finagle.http.Status
import com.twitter.finatra.http.test.EmbeddedHttpServer
import com.twitter.inject.server.FeatureTest

class WeightResourceFeatureTest extends FeatureTest {
  override val server = new EmbeddedHttpServer(
    twitterServer = new FitmanServer
  )

  "WeightResource" should {

    "Save user weight when POST request is made" in {
      server.httpPost(
        path = "/weights",
        postBody =
          """
            |{
            |"user":"shekhar",
            |"weight":85,
            |"status":"Feeling great!!!"
            |}
          """.stripMargin,
        andExpect = Status.Created,
        withLocation = "/weights/shekhar"
      )
    }

    "List all weights for a user when GET request is made" in {
      val response = server.httpPost(
        path = "/weights",
        postBody =
          """
            |{
            |"user":"test_user_1",
            |"weight":80,
            |"posted_at" : "2016-01-03T14:34:06.871Z"
            |}
          """.stripMargin,
        andExpect = Status.Created
      )

      server.httpGetJson[List[Weight]](
        path = response.location.get,
        andExpect = Status.Ok,
        withJsonBody =
          """
            |[
            |  {
            |    "user" : "test_user_1",
            |    "weight" : 80,
            |    "posted_at" : "2016-01-03T14:34:06.871Z"
            |  }
            |]
          """.stripMargin
      )
    }

    "Bad request when user is not present in request" in {
      server.httpPost(
        path = "/weights",
        postBody =
          """
            |{
            |"weight":85
            |}
          """.stripMargin,
        andExpect = Status.BadRequest
      )
    }

    "Bad request when data not in range" in {
      server.httpPost(
        path = "/weights",
        postBody =
          """
            |{
            |"user":"testing12345678910908980898978798797979789",
            |"weight":250
            |}
          """.stripMargin,
        andExpect = Status.BadRequest,
        withErrors = Seq(
          "user: size [42] is not between 1 and 25",
          "weight: [250] is not between 25 and 200"
        )
      )
    }
  }
}