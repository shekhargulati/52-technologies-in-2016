Building "Bootiful" Scala Web Applications with Spring Boot
---

Welcome to the thirty-seventh post of [52-technologies-in-2016](https://github.com/shekhargulati/52-technologies-in-2016) blog series. This week I started work on a project where I decided to use [Spring Boot](http://projects.spring.io/spring-boot/). Scala is my preferred programming language so I decided to use Scala and Spring Boot together. The reason I decided to use Spring Boot Scala combo is because web frameworks in Scala community are over-complicated and over-engineered. They just don't feel natural and lacks good documentation. More often than not they make you unproductive as you spend time fighting with the framework rather than working on your business problem. On the other hand, I find [Spring Boot](http://projects.spring.io/spring-boot/) productive and matching my taste. Spring Boot documentation and community support helps you in case you are struck. Spring Boot lives up to its vision as mentioned on its [website](http://projects.spring.io/spring-boot/).

> **Takes an opinionated view of building production-ready Spring applications. Spring Boot favors convention over configuration and is designed to get you up and running as quickly as possible.**

In this post, I will quickly show you how to use Spring Boot with Scala by converting Spring Boot's official [*Building a RESTful Web Service*](https://spring.io/guides/gs/rest-service/) guide to Scala.

## Prerequisite

To work through this post, you will need following installed on your machine.

1. Your favorite IDE. I use IntelliJ community edition and it provides everything you need for Java and Scala development
2. [JDK 1.8](http://www.oracle.com/technetwork/java/javase/downloads/index.html) or later
3. [Gradle 2.3 or above](http://www.gradle.org/downloads)
4. [Scala 2.11.8](http://www.scala-lang.org/download/)

## What you will build?

You’ll build a service that will accept HTTP GET requests at:

```
http://localhost:8080/greeting
```

and respond with a JSON representation of a greeting:

```json
{"id":1,"content":"Hello, World!"}
```

You can customize the greeting with an optional `name` parameter in the query string:

```
http://localhost:8080/greeting?name=User
```

The `name` parameter value overrides the default value of "World" and is reflected in the response:

```json
{"id":1,"content":"Hello, User!"}
```

## Step 1: Create a Scala Gradle project

We will be using Gradle as our build tool. Spring Boot has good support for Gradle. Navigate to a convenient location on your file system and create a new directory to house your application source code.

```bash
$ mkdir gs-rest-service && cd gs-rest-service
```

Now, we will use Gradle init plugin to bootstrap a Scala project by typing the command shown below.

```bash
$ gradle init --type scala-library
```

The created Scala project has following features:

* Uses the **scala** plugin
* Uses the **jcenter** dependency repository
* Uses **Scala 2.11.8**
* Uses **ScalaTest** for testing
* Has directories in the **conventional locations** for source code
* Uses the **Zinc** Scala compiler by default

You can read more about init plugin in the official [documentation](https://docs.gradle.org/current/userguide/build_init_plugin.html).

Now, you can import the project in your favorite IDE.

## Step 2: Bootify the project

Now, that our project is ready. Let's add Spring Boot to the project. Open the `build.gradle` file and copy the content mentioned below to it.

```groovy
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.springframework.boot:spring-boot-gradle-plugin:1.4.0.RELEASE")
    }
}

apply plugin: 'scala'
apply plugin: 'spring-boot'

jar {
    baseName = 'gs-rest-service'
    version = '0.1.0-SNAPSHOT'
}

repositories {
    jcenter()
}

dependencies {
    compile 'org.scala-lang:scala-library:2.11.8'
    compile("org.springframework.boot:spring-boot-starter-web")

    testCompile 'junit:junit:4.12'
    testCompile 'org.springframework.boot:spring-boot-starter-test'
}
```

In the `build.gradle` shown above we did following:

1. We applied `scala` plugin so that Gradle treat this project as a Scala project.
2. We applied `spring-boot` plugin. `spring-boot` plugin provides features like creating a single executable jar, searches for main method to flag as a runnable class, built-in dependency resolver that sets the version number to match Spring Boot dependencies.
3. We defined the name of the jar and version.
4. We added Scala and Spring Boot dependencies to the `dependencies` section.


## Step 3: Writing test

We follow TDD and write our test first. Spring Boot provides very good support for testing via Spring MVC Test infrastructure. Inside `src/test/scala` create a new project `hello` and create a test with following content.

```scala
package hello

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@RunWith(classOf[SpringRunner])
@SpringBootTest
@AutoConfigureMockMvc
class GreetingControllerTest {

  @Autowired
  var mockMvc: MockMvc = _

  @Test
  def helloWorldMessageWhenNameParameterIsNotSet(): Unit = {
    mockMvc.perform(get("/greeting"))
      .andExpect(status().isOk)
      .andExpect(MockMvcResultMatchers.content().json("""{"id":1,"content":"Hello, World!"}"""))
  }

  @Test
  def helloUserWhenNameParameterIsSetToUser(): Unit = {
    mockMvc.perform(get("/greeting").param("name","User"))
      .andExpect(status().isOk)
      .andExpect(MockMvcResultMatchers.content().json("""{"id":2,"content":"Hello, User!"}"""))
  }

}
```

To learn more about Spring Boot testing support refer to [testing guide](https://spring.io/guides/gs/testing-web/).

## Step 4: Create GreetingController

Now, we will write `GreetingController` that will provide the REST API. Create a new package `hello` inside the `src/main/scala` directory and populate it with following code.

```scala
package hello

import java.util.concurrent.atomic.AtomicLong

import hello.GreetingController.Greeting
import org.springframework.web.bind.annotation.{RequestMapping, RequestParam, RestController}

import scala.beans.BeanProperty

@RestController
class GreetingController {

  val template: String = "Hello, %s"
  val counter: AtomicLong = new AtomicLong()

  @RequestMapping(path = Array("/greeting"))
  def greeting(@RequestParam(value = "name", defaultValue = "World") name: String) =
    new Greeting(counter.incrementAndGet(), template.format(name))


}
```

## Step 5: Create a resource representation

Create a companion object of `GreetingController` that will hold the `Greeting` representation.

```scala
object GreetingController {

  class Greeting(@BeanProperty var id: Long, @BeanProperty var content: String)

}
```

Scala classes does not follow Java bean conventions. So, you have annotate class variables with `@BeanProperty` annotation.

## Step 6: Make the application executable

Spring Boot applications are normally packaged as executable JARs. To make an executable JAR, your JAR should have a class with a `main` method.

```scala
package hello

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

object Application extends App {

  SpringApplication.run(classOf[Application], args: _*)

}

@SpringBootApplication
class Application
```

## Step 7: Run the application

Execute the command shown below to run the Spring Boot application.

```bash
$ ./gradlew bootRun
```

This will start the application at port 8080. You can access your app at http://localhost:8080/greeting/

Test the application by making a cURL request.

```bash
$ curl -i http://localhost:8080/greeting?name=Shekhar
```
```
HTTP/1.1 200
Content-Type: application/json;charset=UTF-8
Transfer-Encoding: chunked
Date: Sun, 11 Sep 2016 20:53:26 GMT

{"id":4,"content":"Hello, Shekhar"}
```

-----

That's all for this week.

Please provide your valuable feedback by posting a comment to [https://github.com/shekhargulati/52-technologies-in-2016/issues/55](https://github.com/shekhargulati/52-technologies-in-2016/issues/55).

[![Analytics](https://ga-beacon.appspot.com/UA-59411913-2/shekhargulati/52-technologies-in-2016/37-spring-boot-scala)](https://github.com/igrigorik/ga-beacon)
