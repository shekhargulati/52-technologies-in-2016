package hello

import java.util.concurrent.atomic.AtomicLong

import hello.GreetingController.Greeting
import org.springframework.web.bind.annotation.{RequestMapping, RequestParam, RestController}

import scala.beans.BeanProperty

@RestController
class GreetingController {

  val template: String = "Hello, %s!"
  val counter: AtomicLong = new AtomicLong()

  @RequestMapping(path = Array("/greeting"))
  def greeting(@RequestParam(value = "name", defaultValue = "World") name: String) =
    new Greeting(counter.incrementAndGet(), template.format(name))


}

object GreetingController {

  class Greeting(@BeanProperty var id: Long, @BeanProperty var content: String)

}
