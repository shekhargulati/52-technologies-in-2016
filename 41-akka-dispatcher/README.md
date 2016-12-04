
Understanding Akka Dispatchers
---

Welcome to the forty-first post of [52-technologies-in-2016](https://github.com/shekhargulati/52-technologies-in-2016) blog series. This week I had to work on tuning a execution engine that is built using Akka. [Akka](http://akka.io/) is a toolkit and runtime for building highly concurrent, distributed and resilient message driven systems. This post assumes you already know Akka. Actor needs a dispatcher to perform its task. A dispatcher relies on executor to provide thread. There are two types of executors a dispatcher can have: 1) `fork-join-executor` 2) `thread-pool-executor`. In this post, we will understand how you can configure `fork-join-executor` and `thread-pool-executor` to meet your needs.

## A Simple Akka Based Execution Engine

Before we learn how to tune dispatcher, let's write a simple execution engine that executes a list of tasks as shown below.

```scala
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorSystem, Props}
import akka.pattern._
import akka.util.Timeout
import playground.dispatcher.tasks.{Status, Task}

import scala.concurrent.duration.{Duration, _}
import scala.concurrent.{Await, ExecutionContext, Future}

class TaskExecutionEngine(system: ActorSystem) {

  def run(tasks: List[Task]): List[Status] = {
    implicit val executionContext: ExecutionContext = system.dispatchers.defaultGlobalDispatcher
    val futures = tasks.map(task => {
      val actorRef = system.actorOf(Props[TaskActor])
      implicit val timeout: Timeout = Timeout(1, TimeUnit.MINUTES)
      (actorRef ? task).map(_.asInstanceOf[Status])
    })
    val statuses: List[Status] = Await.result(Future.sequence(futures), Duration.Inf)
    statuses
  }

}

object TaskExecutionEngine {
  def apply(): TaskExecutionEngine = new TaskExecutionEngine(ActorSystem("tasky"))
}

class TaskActor extends Actor {
  override def receive: Receive = {
    case task: Task =>
      val result = task()
      sender() ! result
  }
}


object tasks {

  trait Status

  case object success extends Status

  case class Failure(code: Int, message: String) extends Status

  type Task = () => Status

  class WaitTask(timeToWait: Duration = 5 seconds) extends Task {
    override def apply(): Status = {
      println(s"${LocalDateTime.now()} >> Executing WaitTask...")
      Thread.sleep(timeToWait.toMillis)
      println(s"${LocalDateTime.now()} >> Executed WaitTask...")
      success
    }
  }

  class CmdTask(cmd: String) extends Task {

    import sys.process._

    override def apply(): Status = {
      println(s"${LocalDateTime.now()} >> Executing CmdTask...")
      val exitCode = cmd.!
      println(s"${LocalDateTime.now()} >> Executed CmdTask...")
      if (exitCode == 0) success else Failure(exitCode, s"command $cmd failed")
    }
  }
}
```

Let's understand what the code snippet shown above does:

1. We created a `TaskExecutionEngine` class that defines a method `run`. `TaskExecutionEngine` takes an `ActorSystem` at construction time. We created the companion object that provides a newly created `ActorSystem` to the engine. The `run` method takes a list of tasks for execution and return list of task execution result. `Task` is a function with signature `() => Status`. `Status` is trait that has two implementations -- `success` or `Failure`. The `run` method sends a message to `TaskActor` for each task. We used ask pattern, which returns the Future. We used `Future.sequence` to convert a `List[Future[Status]]` to a single Future of statues `Future[List[Status]]`. Finally, we awaited for all futures to finish and return `List[Status]` to the client.

2. `TaskActor` is a simple Actor that executes a task and return the response back to the `sender`.

3. Next, we have two task implementations -- `WaitTask` and `CmdTask`. As their name suggests, `WaitTask` sleeps for the configured duration and `CmdTask` executes a command and returns success if exit code is 0 otherwise it returns `Failure`.


To execute this code, we have a simple client that creates few tasks and submit them to engine for execution.

```scala
import scala.concurrent.duration._

object Tasky extends App {
  val engine = TaskExecutionEngine()

  val statuses = engine.run(List(new WaitTask(5 seconds), new WaitTask(5 seconds), new WaitTask(5 seconds)))
  statuses.foreach(println(_))
  engine.shutdown()
}
```

The code shown above creates a list of tasks and give tasks to `TaskExecutionEngine` for execution. It finally prints the result of each task.

If you run the `Tasky` app you will see following output. All the three tasks start execution at the same time, then task execution happens, and finally all tasks are completed.

```
2016-12-04T22:40:44.280 >> Executing WaitTask...
2016-12-04T22:40:44.280 >> Executing WaitTask...
2016-12-04T22:40:44.280 >> Executing WaitTask...
2016-12-04T22:40:49.290 >> Executed WaitTask...
2016-12-04T22:40:49.290 >> Executed WaitTask...
2016-12-04T22:40:49.290 >> Executed WaitTask...
success
success
success
```

## The default dispatcher

A dispatcher is responsible for assigning threads to the Akka Actor when there are messages in the Actor's mailbox. Akka creates a default dispatcher that is shared by all the actors. A user can create multiple dispatchers and assign them to different actors. Dispatcher is governed by its configuration. If you don't create any dispatcher and don't override the default configuration then you are using the default dispatcher configuration specified in `reference.conf`.

```
default-dispatcher {
      type = "Dispatcher"

      executor = "fork-join-executor"

      fork-join-executor {
        parallelism-min = 8
        parallelism-factor = 3.0
        parallelism-max = 64
      }

      shutdown-timeout = 1s

      throughput = 5
}
```

In the configuration shown above:

1. `fork-join-executor.parallelism-min` defines minimum number of threads `fork-join-executor` managed ThreadPool can have. The default value is 8.

2. `fork-join-executor.parallelism-factor` defines a factor for calculating number of threads from available processors. The default value is 3.

3. `fork-join-executor.parallelism-max` defines maximum number of threads `fork-join-executor` managed ThreadPool can have. The default value is 64.

To understand this, let's run a quick experiment to see how many messages an actor can process in parallel. Change the client code to as shown below.

```scala
import scala.concurrent.duration._

object Tasky extends App {
  val engine = TaskExecutionEngine()
  val statuses = engine.run(1 to 100 map(_ => new WaitTask(5 seconds)) toList)
  statuses.foreach(println(_))
}
```

We are submitting 100 tasks to `TaskExecutionEngine`. On my machine, when I ran this code I saw that engine processed 24 messages in one go and rest other messages were put in the queue. The number 24 comes from multiplication of number of cores on your machine with `parallelism-factor`. My machine has 8 cores and default value of parallelism-factor is 3 so we got 24 threads (8x3).

> **On mac, you can find number of cores using the command `sysctl -n hw.ncpu`**.

Let's suppose my machine had only one core then according to the calculation mentioned above I could have got 1x3 i.e. 3 threads but instead we will get 8 threads because that's the minimum number of threads `default-dispatcher` can have.

## Configuring our own dispatcher

Let's now understand how we can provide our own dispatcher. Create a new file `application.conf` inside the `src/main/resources` directory as shown below.

```
task-dispatcher {
  type = "Dispatcher"
  executor = "fork-join-executor"
  fork-join-executor {
    parallelism-min = 100
    parallelism-max = 200
  }
}
```

In the configuration shown above, we have specified that we need minimum 100 threads. This will make 100 tasks execute in parallel.

We will have to update the `TaskExecutionEngine` code to use the `task-dispatcher` as shown below.

```scala
class TaskExecutionEngine(system: ActorSystem) {

  def run(tasks: List[Task]): List[Status] = {
    implicit val executionContext: ExecutionContext = system.dispatchers.lookup("task-dispatcher")
    val futures = tasks.map(task => {
      val actorRef = system.actorOf(Props[TaskActor].withDispatcher("task-dispatcher"))
      implicit val timeout: Timeout = Timeout(1, TimeUnit.MINUTES)
      (actorRef ? task).map(_.asInstanceOf[Status])
    })
    val statuses: List[Status] = Await.result(Future.sequence(futures), Duration.Inf)
    statuses
  }

}
```

If you run this code with the Tasky client that creates 100 tasks as shown below.

```scala
import playground.dispatcher.tasks._

import scala.concurrent.duration._

object Tasky extends App {
  val engine = TaskExecutionEngine()
  val statuses = engine.run((1 to 100) map (_ => new WaitTask(2 seconds)) toList)
  statuses.foreach(println(_))
  engine.shutdown()
}
```

You will notice that all 100 started at almost same time.  If you run this with default configuration 24(`parallelism-factor*parallelism-min`) tasks will be processed in parallel.

```
// part of output
2016-12-04T22:46:51.590 >> Executing WaitTask...
2016-12-04T22:46:51.588 >> Executing WaitTask...
2016-12-04T22:46:51.587 >> Executing WaitTask...
2016-12-04T22:46:51.590 >> Executing WaitTask...
2016-12-04T22:46:51.589 >> Executing WaitTask...
2016-12-04T22:46:51.590 >> Executing WaitTask...
2016-12-04T22:46:51.590 >> Executing WaitTask...
2016-12-04T22:46:51.589 >> Executing WaitTask...
2016-12-04T22:46:51.588 >> Executing WaitTask...
2016-12-04T22:46:53.592 >> Executed WaitTask...
2016-12-04T22:46:53.592 >> Executed WaitTask...
2016-12-04T22:46:53.592 >> Executed WaitTask...
2016-12-04T22:46:53.592 >> Executed WaitTask...
2016-12-04T22:46:53.595 >> Executed WaitTask...
2016-12-04T22:46:53.595 >> Executed WaitTask...
```

## Using `thread-pool-executor`

`fork-join-executor` allows you to have a static thread pool configuration where number of threads will be between `parallelism-min` and `parallelism-max` bounds. You can use `thread-pool-executor` if you want your thread pool to have dynamic nature.

```
task-dispatcher {
  type = "Dispatcher"
  executor = "thread-pool-executor"

  thread-pool-executor {
    fixed-pool-size = off

    core-pool-size-min = 8

    core-pool-size-factor = 3.0

    core-pool-size-max = 64

    max-pool-size-min = 8

    max-pool-size-factor = 3.0

    max-pool-size-max = 64

    task-queue-size = -1
  }
}
```

In the configuration shown above, we changed the executor to `thread-pool-executor`. This a

| Property                                | Description                              | Default |
| :-------------------------------------- | :--------------------------------------- | :------ |
| thread-pool-executor.fixed-pool-size    | Whether ThreadPool should be fixed pool or not | off     |
| thread-pool-executor.core-pool-size-min | Sets the minimum core thread pool size   | 8       |
| thread-pool-executor.core-pool-size-max | Sets the maximum core thread pool size   | 64      |
| thread-pool-executor.max-pool-size-min  | Sets the minimum max pool size           | 8       |
| thread-pool-executor.max-pool-size-max  | Sets the maximum value for max pool size | 64      |
| thread-pool-executor.task-queue-size    | Sets size of the queue used by thread pool executor. This value define how quick the pool size will grow when there are more thread requests than threads. | -1      |

`thread-pool-executor` differs from `fork-join-executor` in that it allows us to have a dynamic thread pool that can grow and shrink depending on the load. `thread-pool-executor` has `core-pool-size-*` and `max-pool-size-*` properties that control how `thread-pool-executor` grows. `core-pool-size-*` are used to define minimum number of threads that ThreadPool will have. This works similarly to how `fork-join-executor` works.

If you run the `Tasky` app using this configuration, you will see the same behavior as we observed with default values of `fork-join-executor`. `TaskExecutionEngine` will process 24 tasks at a time. This is because my machine has 8 cores and `core-pool-size-factor` is 3.0.

You might have noticed that we have also specified `max-pool-size-*` properties of ThreadPoolExecutor. So, you might think why ThreadPoolExecutor didn't created more threads as max value could go up to 64. The reason for it is the configuration property `thread-pool-executor.task-queue-size`. ThreadPoolExecutor automatically adjust the thread pool size according to the bounds set by `core-pool-size` and `max-pool-size` configuration properties. When a new task is submitted for execution and there are fewer than `core-pool-size` threads running, executor will create a new thread for the task execution. When you hit the limit of `core-pool-size`, a new thread will only be created when queue is full. The size of queue is governed by `task-queue-size` property. The default value of `task-queue-size` is -1. The value -1 means queue is unbounded and pool size will never increase beyond what's configured in `core-pool-size`.

Let's change the configuration to the one shown below.

```
task-dispatcher {
  type = "Dispatcher"
  executor = "thread-pool-executor"

  thread-pool-executor {
    core-pool-size-min = 8

    core-pool-size-max = 64

    max-pool-size-min = 100

    max-pool-size-max = 200

    task-queue-size = 20
  }
}
```

When you run the code now, you will see dynamic nature of `thread-pool-executor` in action. `thread-pool-executor` will create 24 threads as configured using `core-pool-size`. The first 24 tasks will be executed by these 24 threads. After the first 24 tasks, `thread-pool-executor` will wait till queue has 20 messages. Once queue is full, `thread-pool-executor` will use the `max-pool-size` configuration to create threads. This will go on until we reach 81 tasks. After that task-queue-size will go below 20 so remaining 19 tasks will be executed after threads are freed. 

----

That's all for this week.

Please provide your valuable feedback by adding a comment to [https://github.com/shekhargulati/52-technologies-in-2016/issues/67](https://github.com/shekhargulati/52-technologies-in-2016/issues/67).

[![Analytics](https://ga-beacon.appspot.com/UA-59411913-2/shekhargulati/52-technologies-in-2016/40-akka)](https://github.com/igrigorik/ga-beacon)
