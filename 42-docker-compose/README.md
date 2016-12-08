# Using Docker Compose with wait-for-it

Welcome to forty-second post of [52-technologies-in-2016](https://github.com/shekhargulati/52-technologies-in-2016) series. Today, we will learn about Docker Compose, a tool for defining and running multi-container Docker applications. If you are new to Docker then you can read week  [39 post](https://github.com/shekhargulati/52-technologies-in-2016/blob/master/39-docker/README.md) where I discussed basics of running a  Java application inside Docker containers. This post will start from where we left the last post so that we can understand the need for Docker Compose and the problems it solves. This post will also cover how to use Docker Compose with [wait-for-it](https://github.com/vishnubob/wait-for-it). `wait-for-it` is a simple bash utility to test and wait for the availability of TCP host and port. The need for `wait-for-it` arises when you want to make sure a container is up and running before another container. Let's suppose we have two containers — one running a web application and another running a database like mysql. Most of the times you would want MySQL container to be up and running before web application container starts(most applications try to connect to DB at startup). To handle such situations, you will need to use solutions like `wait-for-it`.

## The need for multiple containers

>  **A container should do one thing and do that well.**

In a real application, you will have one or more containers for each services and different services will work together to do the job. In the week 39 post, we had a Java application that was using a file based HSQL database to persist the data in a volume. In real applications, you will have a persistent databases like MySQL.  So, you will have two containers — one running our Java application and second running MySQL database. Let's see how we will connect multiple containers together.

Create a new network

```bash
$ docker network create 52-tech-blog
```

Run a new docker container in `52-tech-blog` network.

```shell
$ docker run -d --name db --net 52-tech-blog -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=taskman mysql
```

Now, we will start the application container in the same network `52-tech-blog` as shown below.

```shell
$ docker run -p 8080:8080 --net 52-tech-blog -e TASKMAN_DB_PASSWORD=password com.shekhargulati/taskman:1.0.0 --spring.profiles.active=docker
```

We have defined a environment variable `TASKMAN_DB_PASSWORD` that contains password to connect to the database.  Also, we specified a Spring profile `docker` so that our application connect to MySQL database.

Once application is created, you can access it at [http://localhost:8080/api/tasks](http://localhost:8080/api/tasks).

> **Please note `com.shekhargulati/taskman:1.0.0` is the image that we created in week 39 post. Please refer to that post in case you want to create that image. The container is built using following Dockerfile.**
>
> ```dockerfile
> FROM openjdk:8
> MAINTAINER "Shekhar Gulati"
> ENV APP_DIR /app
> ADD taskman.jar $APP_DIR/
> WORKDIR $APP_DIR
> EXPOSE 8080
> CMD ["java","-jar","taskman.jar","--spring.profiles.active=docker"]
> ```

## Docker Compose

The main problem with the workflow mentioned in previous section is that it is manual and error prone. Docker Compose makes it easy to compose multi-container applications together. It consists of two parts:

1. A YAML file where you document and configure all of the application dependencies like cache, database, queue. This file usually named `docker-compose.yml`.
2. A command-line tool that reads the docker-compose.yml file and launches the containers defined in the file. You can manage all the containers for a project using this tool.

## Install Docker Compose

To install Docker Compose on your machine, you can read installation instructions mentioned in the [Compose documentation](https://docs.docker.com/compose/install/).

## Writing a Compose file

We can automate the manual task of creating network and starting containers by defining them in the Docker Compose file.  Create a new file `docker-compose.yml` in your project root and define the services as shown below.

```yaml
version: '2'
services:
  db:
    image: mysql:latest
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: password
      MYSQL_DATABASE: taskman
  web:
    image: com.shekhargulati/taskman:1.0.0
    ports:
      - "8080:8080"
    depends_on:
      - db
    environment:
      - TASKMAN_DB_PASSWORD=password
```

As you can see above we have two services — `db` and `web`. In the `docker-compose.yml` shown above:

* we defined two services db and web
* db uses mysql:latest image and web uses `com.shekhargulati/taskman:1.0.0` image
* web container forwards the exposed port 8080 on the container to port 8080 on the host machine.
* `web`  depends on `db` so compose will start the mysql container before starting the web container. But, compose will not wait for `db` container to be accessible. So, if your application tries to connect with the database during startup there is a posibility that application is unable to connect to db. This will lead to application startup failure. We will see how to handle it in next section.
*  exposed enviroment variables for each container.

Let's use Docker compose to launch our multi container application. In the directory where you have `docker-compose.yml`, `Dockerfile` , and `taskman.jar` run the following command.

```
$ docker-compose up
```

This will build the web container and start the containers. On startup of web container, you will see exception as shown below. The reason is your web container tried to connect to db container but it was not yet fully started.

```
web_1  | com.mysql.jdbc.exceptions.jdbc4.CommunicationsException: Communications link failure
web_1  |
web_1  | The last packet sent successfully to the server was 0 milliseconds ago. The driver has not received any packets from the server.
web_1  | 	at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method) ~[na:1.8.0_102]
web_1  | 	at sun.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:62) ~[na:1.8.0_102]
web_1  | 	at sun.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45) ~[na:1.8.0_102]
web_1  | 	at java.lang.reflect.Constructor.newInstance(Constructor.java:423) ~[na:1.8.0_102]
web_1  | 	at com.mysql.jdbc.Util.handleNewInstance(Util.java:404) ~[mysql-connector-java-5.1.39.jar!/:5.1.39]
web_1  | 	at com.mysql.jdbc.SQLError.createCommunicationsException(SQLError.java:988) ~[mysql-connector-java-5.1.39.jar!/:5.1.39]
web_1  | 	at com.mysql.jdbc.MysqlIO.<init>(MysqlIO.java:341) ~[mysql-connector-java-5.1.39.jar!/:5.1.39]
web_1  | 	at com.mysql.jdbc.ConnectionImpl.coreConnect(ConnectionImpl.java:2251) ~[mysql-connector-java-5.1.39.jar!/:5.1.39]
web_1  | 	at com.mysql.jdbc.ConnectionImpl.connectOneTryOnly(ConnectionImpl.java:2284) ~[mysql-connector-java-5.1.39.jar!/:5.1.39]
web_1  | 	at com.mysql.jdbc.ConnectionImpl.createNewIO(ConnectionImpl.java:2083) ~[mysql-connector-java-5.1.39.jar!/:5.1.39]
web_1  | 	at com.mysql.jdbc.ConnectionImpl.<init>(ConnectionImpl.java:806) ~[mysql-connector-java-5.1.39.jar!/:5.1.39]
web_1  | 	at com.mysql.jdbc.JDBC4Connection.<init>(JDBC4Connection.java:47) ~[mysql-connector-java-5.1.39.jar!/:5.1.39]
```

This is a very common use case and it might feel surprising that Compose does not support it.  Compose team has maintained that they will not support this use case.

> To run compose in detached mode, you can use `docker-compose up -d`

## Using wait-for-it

In this section, I will show how to use Docker Compose with [wait-for-it](https://github.com/vishnubob/wait-for-it). It took me sometime to figure out how to use it so I am writing it down for developers who might also need to use wait-for-it someday.

To use it, first copy the [wait-for-it.sh](https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh) in your project directory. Then, change Dockerfile to as shown below.

```dockerfile
FROM openjdk:8
MAINTAINER "Shekhar Gulati"
ENV APP_DIR /app
ADD taskman.jar $APP_DIR/
ADD wait-for-it.sh $APP_DIR/
WORKDIR $APP_DIR
EXPOSE 8080
CMD ["java","-jar","taskman.jar","--spring.profiles.active=docker"]
```

Update `docker-compose.yml` to as shown below.

```yaml
version: '2'
services:
  db:
    image: mysql:latest
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: password
      MYSQL_DATABASE: taskman
  web:
    build: .
    entrypoint: ./wait-for-it.sh db:3306 --strict -- java -jar taskman.jar --spring.profiles.active=docker
    ports:
      - "8080:8080"
    depends_on:
      - db
    environment:
      - TASKMAN_DB_PASSWORD=password
```

Start the containers again using `docker-compose up` command. This time application should start fine. In the logs, you will see

```
web_1  | wait-for-it.sh: waiting 15 seconds for db:3306
```

This time application will start fine. There will be no exception in the logs. You can access application at http://localhost:8080/api/tasks.

Once you are done, you can stop and remove the containers and associated volumes using the command mentioned below.

```shell
$ docker-compose stop && docker-compose rm -vf
```

------

That's all for this week.

Please provide your valuable feedback by adding a comment to [https://github.com/shekhargulati/52-technologies-in-2016/issues/68](https://github.com/shekhargulati/52-technologies-in-2016/issues/68).

[![Analytics](https://ga-beacon.appspot.com/UA-59411913-2/shekhargulati/52-technologies-in-2016/40-akka)](https://github.com/igrigorik/ga-beacon)
