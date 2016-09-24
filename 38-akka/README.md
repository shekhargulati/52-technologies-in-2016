Actor System Termination on JVM Shutdown
----

Welcome to the thirty-eight post of [52-technologies-in-2016](https://github.com/shekhargulati/52-technologies-in-2016) blog series. In my day job, I work on a backend system that uses Akka. [Akka](http://akka.io/) is a toolkit and runtime for building highly concurrent, distributed and resilient message driven systems. I will write an in-depth Akka tutorial some other week. This week I will talk about a specific problem that I was trying to solve. We have two applications that talk over each other via [Akka remoting](http://doc.akka.io/docs/akka/current/scala/remoting.html). First application can shutdown the second application programmatically by sending a message to the second application ActorSystem.  Shutdown here means you can exit the JVM. To make sure we do a clean shutdown, we added JVM shutdown hook that terminates the ActorSystem.

This was implemented as shown below.

```scala
package playground

import akka.actor.{Actor, ActorRef, ActorSystem, Props, Terminated}
import akka.dispatch.MonitorableThreadFactory
import playground.App1ControlActor.Stop

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object App1 extends App {

  private val system: ActorSystem = ActorSystem("app1-akka-system")

  private val actor: ActorRef = system.actorOf(Props[App1ControlActor])

  Runtime.getRuntime.addShutdownHook(MonitorableThreadFactory("monitoring-thread-factory", false,
    Some(Thread.currentThread().getContextClassLoader)).newThread(new Runnable {
    override def run(): Unit = {
      val terminate: Future[Terminated] = system.terminate()
      Await.result(terminate, Duration("10 seconds"))
    }
  }))

  actor ! Stop

}


class App1ControlActor extends Actor {

  import App1ControlActor._

  override def receive: Receive = {
    case Stop =>
      println("Stopping application")
      System.exit(1)
  }


}

object App1ControlActor {

  case object Stop

}
```

In the code shown above, we did the following:

1. We created an ActorSystem with name `app1-akka-system`. This ActorSystem was then used to create an Actor `App1ControlActor`.
2. We registered a JVM shutdown hook that terminates the ActorSystem. To terminate the `ActorSystem`, we made a call to `system.terminate` method. The `system.terminate` give back a future. We waited 10 seconds for future to finish.
3. We send the `Stop` message to `App1ControlActor`. The actor shutdown the JVM by calling `System.exit`.

If you will run the Scala application shown above it will run fine. You will not see any exception. This code was working in production system for more than a year. The output that you will see in the console is shown below.

```
Stopping application

Process finished with exit code 1
```

This week I was asked to improve logging of this code. I just had to add a log statement on ActorSystem termination. This looked easy so I added `registerOnTermination` call. The `registerOnTermination` register a block of code (callback) to run after `ActorSystem.shutdown` has been issued and all actors in this actor system have been stopped.

```scala
object App1 extends App {

  private val system: ActorSystem = ActorSystem("app1-akka-system")

  private val actor: ActorRef = system.actorOf(Props[App1ControlActor])

  Runtime.getRuntime.addShutdownHook(MonitorableThreadFactory("monitoring-thread-factory", false,
    Some(Thread.currentThread().getContextClassLoader)).newThread(new Runnable {
    override def run(): Unit = {
      val terminate: Future[Terminated] = system.terminate()
      Await.result(terminate, Duration("10 seconds"))
    }
  }))

  system.registerOnTermination {
    println("ActorSystem terminated")
  }

  actor ! Stop


}
```

Run the code again. You will notice that `ActorSystem terminated` was never printed.

This made me wonder if termination was successful. Also, I tried to understand why I don't see any exception in the console.

I was not sure why we are using `MonitorableThreadFactory` so I removed it and created a thread manually as shown below.

```scala
object App1 extends App {

  private val system: ActorSystem = ActorSystem("app1-akka-system")

  private val actor: ActorRef = system.actorOf(Props[App1ControlActor])

  Runtime.getRuntime.addShutdownHook(new Thread(new Runnable {
    override def run(): Unit = {
      val terminate: Future[Terminated] = system.terminate()
      Await.result(terminate, Duration("10 seconds"))
    }
  }))

  system.registerOnTermination {
    println("ActorSystem terminated")
  }

  actor ! Stop


}
```
When you run this code, you will see exception in the console. The future timeout after 10 seconds. The reason we were not seeing exception with `MonitorableThreadFactory` is that we were using the default Noop uncaught exception handler which was not logging exception scenarios.

```
Stopping application
Exception in thread "Thread-0" java.util.concurrent.TimeoutException: Futures timed out after [10 seconds]
	at scala.concurrent.impl.Promise$DefaultPromise.ready(Promise.scala:219)
	at scala.concurrent.impl.Promise$DefaultPromise.result(Promise.scala:223)
	at scala.concurrent.Await$$anonfun$result$1.apply(package.scala:190)
	at scala.concurrent.BlockContext$DefaultBlockContext$.blockOn(BlockContext.scala:53)
	at scala.concurrent.Await$.result(package.scala:190)
	at playground.App1$$anon$1.run(App1.scala:19)
	at java.lang.Thread.run(Thread.java:745)
```

To make sure I give sufficient time to ActorSystem for termination I changed duration to `Duration.Inf`. It made system never terminate.

This looked to us a deadlock where `ActorSystem` waits for all actors to terminate and Actor is waiting for system to exit.

To solve this use case, we scheduled the JVM exit using the ActorSystem `scheduler`. This meant `App1ControlActor` can successfully shut down and does not have to wait for JVM to exit. Hence, avoiding dead lock.

```scala
class App1ControlActor extends Actor {

  import App1ControlActor._

  override def receive: Receive = {
    case Stop =>
      println("Stopping application")
      implicit val executionContext: ExecutionContext = context.system.dispatcher
      context.system.scheduler.scheduleOnce(Duration.Zero)(System.exit(1))
  }


}
```


If you run the code now, it will work fine. The console will show correct output as shown below.

```
Stopping application
ActorSystem terminated
```

-----

That's all for this week.

Please provide your valuable feedback by posting a comment to [https://github.com/shekhargulati/52-technologies-in-2016/issues/57](https://github.com/shekhargulati/52-technologies-in-2016/issues/57).

[![Analytics](https://ga-beacon.appspot.com/UA-59411913-2/shekhargulati/52-technologies-in-2016/38-akka)](https://github.com/igrigorik/ga-beacon)
