package loadtests

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class AccessHomePageSimulation extends Simulation {

  val blogHttpConf = http
    .baseURL("https://my-shekharblogs.rhcloud.com")
    .acceptHeader("text/html")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116")
    .acceptLanguageHeader("en-US,en;q=0.8,pt;q=0.6")

  val scenario1 = scenario("Access Home Page")
    .exec(
      http("GetHomePageRequest")
        .get("/")
        .check(status.is(_ => 200))
    )
    .pause(1)

  setUp(
    scenario1.inject(rampUsers(100) over 10)
  ).protocols(blogHttpConf)

}
