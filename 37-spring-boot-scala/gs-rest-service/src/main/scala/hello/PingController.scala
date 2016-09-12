package hello

import org.springframework.web.bind.annotation.{RequestMapping, RestController}

@RestController
class PingResource {

  @RequestMapping(path = Array("/ping"))
  def ping(): String = "pong"

}