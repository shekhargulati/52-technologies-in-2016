Using Docker Containers As Cron Jobs
----

Welcome to the fortieth post of [52-technologies-in-2016](https://github.com/shekhargulati/52-technologies-in-2016) blog series. This week I was working on a problem that required cron jobs. The use case was that after user registers with the application, we will create a cron job that will track his/her social activities. We will have one container per user. I wanted to keep cron jobs to work in a different process from the main application so that different concerns of the application don't intermingle. In my view, containers provide the right abstraction to solve this use case. The added advantage that we achieve by using Docker containers is that we can configure their restart policy. This means if a container goes down for some reason it will be restarted automatically.  I kept these containers dumb so the only thing container had to do is to make an HTTP request to fetch the data and store that in the database. In this post, I will share how I did it.

> **This post assumes you know how to work with Docker containers. In case you are new to Docker, you can read [my getting started post on Docker](https://github.com/shekhargulati/52-technologies-in-2016/blob/master/39-docker/README.md).**

## Creating a container for cron jobs

Navigate to a convenient location on your filesystem and create a directory for this project.

```bash
$ mkdir cron-example && cd cron-example
```

Next, we will create a cron job that will execute a Python script every minute to check Github API status. Create a file with name `crontab` that will specify what you want to do as shown below.

```bash
* * * * * root /usr/local/bin/python /app/app.py >> /var/log/cron.log 2>&1
# You need an empty line at the end of the file so that it is considered a valid cron file.
```

Cron job will be executed by `root` user as that is the only user we have inside the container.

The Python script is very simple as shown below. It uses Python requests API to make a GET request. Create a new file `app.py` inside the `cron-example` directory.

```python
import requests

r = requests.get('https://status.github.com/api/status.json')
print(r.text)
```

### Writing a Dockerfile

Now, we will create a `Dockerfile` that will build the required container.

```docker
FROM python:2.7
MAINTAINER "Shekhar Gulati"
RUN apt-get update -y
RUN apt-get install cron -yqq
COPY crontab /etc/cron.d/github-status-cron
RUN chmod 0644 /etc/cron.d/github-status-cron
RUN touch /var/log/cron.log
ENV APP_DIR /app
COPY app.py requirements.txt $APP_DIR/
WORKDIR $APP_DIR
RUN pip install -r requirements.txt
CMD cron && tail -f /var/log/cron.log
```

Let's understand what we did above:

1. We used `FROM` command to specify our base image. We used `python:2.7` as our base image as we want to execute a Python script.

2. Next, we installed `cron` package using the `RUN` command. Earlier, debian images used to have cron package installed but now we will have to install it ourselves.

3. Next, we copied the `crontab` file from our local system to the container `cron.d` directory using `COPY` command.

4. Then, we made cron job executable using the `RUN` command.

5. Next, we created the log file for the cron job.

6. Then, we copied  `app.py` and `requirements.txt` to the `APP_DIR`. Then, we installed our application dependencies using `pip install`. This will install `requests` Python library. The dependencies are specified in `requirements.txt` file.

7. Finally, we specified the command that container will use on startup using `CMD` command.

To build the image, run the following command from within `cron-example` directory.

```
$ docker build -t cron-example .
```

This will build the image. You can verify that your image exists by running following command.

```
$ docker images |grep cron-example
```
```
cron-example        latest              8febf10b92b2        2 minutes ago       696.9 MB
```

You can run the cron container using the command shown below. Every one minute you will see a line that prints status of Github API.

```
$ docker run -it cron-example
```
```
{"status":"good","last_updated":"2016-10-19T03:27:03Z"}
{"status":"good","last_updated":"2016-10-19T03:28:01Z"}
{"status":"good","last_updated":"2016-10-19T03:28:56Z"}
...
```

## Using environment variables

Once you have move beyond a simple cron job, a common requirement you might have is to access environment variables in your Python script. Environment variables could contain information about database or other configuration data. Let's see what will happen if we have try to access environment variables in our Python script.

Change the `app.py` to following.

```python
import requests
import os

print(os.environ['MY_ENV_VARIABLE'])

r = requests.get('https://status.github.com/api/status.json')
print(r.text)
```

Now, build the image again. This time it will be very quick to build the image.

```
$ docker build -t cron-example .
```

Now, run the container using the command shown below. Note that we are passing environment variable using the `-e` option.

```
$ docker run -it -e MY_ENV_VARIABLE=hello cron-example
```

You will have to wait a minute to see the output. After a minute, you will see following:

```
Traceback (most recent call last):
  File "/app/app.py", line 4, in <module>
    print(os.environ['MY_ENV_VARIABLE'])
  File "/usr/local/lib/python2.7/UserDict.py", line 40, in __getitem__
    raise KeyError(key)
KeyError: 'MY_ENV_VARIABLE'
```

As you can see from the output shown above, our Python script couldn't find environment variable `MY_ENV_VARIABLE`. The reason is cron job does not pass environment variables to the Python script.

To stop the container, press `CTRL+C`.

To fix this, we will have to create a bash script that will pass the environment variables to the cron job as shown below. Create a script `run-crond.sh` as shown below.

```bash
#!/bin/bash

env | egrep '^MY' | cat - /tmp/my_cron > /etc/cron.d/github-status-cron

cron && tail -f /var/log/cron.log
```

We will also have to update Dockerfile so that it runs the bash script at startup rather than cron.

```docker
FROM python:2.7
MAINTAINER "Shekhar Gulati"
RUN apt-get update -y
RUN apt-get install cron -yqq
COPY crontab /tmp/my_cron
COPY run-crond.sh run-crond.sh
RUN chmod -v +x /run-crond.sh
RUN touch /var/log/cron.log
ENV APP_DIR /app
COPY app.py requirements.txt $APP_DIR/
WORKDIR $APP_DIR
RUN pip install -r requirements.txt
# Run the command on container startup
CMD ["/run-crond.sh"]
```

Now, build the image again.

```
$ docker build -t cron-example .
```

Now, run the container using the command shown below. This time environment variable will be correctly passed to the Python script and you will see `hello` printed each time as well.

```
$ docker run -it -e MY_ENV_VARIABLE=hello cron-example
```
```
hello
{"status":"good","last_updated":"2016-10-19T03:58:53Z"}
hello
{"status":"good","last_updated":"2016-10-19T03:59:43Z"}
```

----

That's all for this week.

Please provide your valuable feedback by adding a comment to [https://github.com/shekhargulati/52-technologies-in-2016/issues/63](https://github.com/shekhargulati/52-technologies-in-2016/issues/63).

[![Analytics](https://ga-beacon.appspot.com/UA-59411913-2/shekhargulati/52-technologies-in-2016/40-docker-cron)](https://github.com/igrigorik/ga-beacon)
