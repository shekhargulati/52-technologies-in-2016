package com.shekhargulati.fitman

import com.shekhargulati.fitman.api.WeightResource
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.filters.CommonFilters
import com.twitter.finatra.http.routing.HttpRouter
import com.twitter.finatra.http.{Controller, HttpServer}

object FitmanApp extends FitmanServer

class FitmanServer extends HttpServer {
  override protected def configureHttp(router: HttpRouter): Unit = {

    router
      .filter[CommonFilters]
      .add[HelloController]
      .add[WeightResource]
  }
}

class HelloController extends Controller {

  get("/hello") { request: Request =>
    "Fitman says hello"
  }

}



