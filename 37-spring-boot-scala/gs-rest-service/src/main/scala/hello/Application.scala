package hello

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

object Application extends App {

  SpringApplication.run(classOf[Application], args: _*)

}

@SpringBootApplication
class Application
