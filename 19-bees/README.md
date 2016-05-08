Load testing with bees
---

Welcome to the nineteenth blog of [52-technologies-in-2016](https://github.com/shekhargulati/52-technologies-in-2016) blog series. This week I discovered a Python utility called [**beeswithmachineguns**](https://github.com/newsapps/beeswithmachineguns) that can load test a web application by launching many micro EC2 instances. In this short blog, I will cover how to get started with this utility.

> **From the [project site](https://github.com/newsapps/beeswithmachineguns#the-caveat-please-read): If you decide to use the Bees, please keep in mind the following important caveat: they are, more-or-less a distributed denial-of-service attack in a fancy package and, therefore, if you point them at any server you don’t own you will behaving unethically, have your Amazon Web Services account locked-out, and be liable in a court of law for any downtime you cause.**


## Installing `beeswithmachineguns`

Create a new Python virtual environment using the following commands.

```bash
$ virtualenv venv --python=python2.7
$ source venv/bin/activate
```

Now, you can check your Python installation by running `which python` to make sure it is pointing to the Python installed inside the virtual environment.

```bash
$ pip install beeswithmachineguns
```

The `beeswithmachineguns` makes use of `boto` and `paramiko` packages. [`boto`](https://github.com/boto/boto3) is the official Python client library to work with Amazon EC2 and [`paramiko`](http://www.paramiko.org/) is an implementation of SSH v2 protocol that `beeswithmachineguns` uses to open a SSH connection and load test your application.

To make sure, `beeswithmachineguns` is installed correctly please check that following command returns successfully.

```bash
$ bees -h
```
```
Usage:
bees COMMAND [options]

Bees with Machine Guns

A utility for arming (creating) many bees (small EC2 instances) to attack
(load test) targets (web applications).

commands:
  up      Start a batch of load testing servers.
  attack  Begin the attack on a specific url.
  down    Shutdown and deactivate the load testing servers.
  report  Report the status of the load testing servers.
```

## Running a load test

Before you can start using `beeswithmachineguns`, you have to setup AWS credentials in the `~/.aws/credentials` file.

```ini
[default]
aws_access_key_id = <YOUR_AWS_ACCESS_KEY>
aws_secret_access_key = <YOUR_AWS_ACCESS_SECRET>
```

You can also setup the default region in the `~/.aws/config` file.

```ini
[default]
region=us-east-1
```

Now, you can start using `bees` command-line tool. To spin up instances, you will use `bees up` command as shown below. We are starting one server using the `default` security group and `my-ssh-key` pem key. The `my-ssh-key.pem` should reside inside the `~/.ssh` directory. The server will be lauched inside the `us-east-1` availability zone. You can specify a different zone using the `--zone` option.

```bash
$ bees up --servers 1 --group default --key my-ssh-key
```
You will see following in the output.
```
New bees will use the "default" EC2 security group. Please note that port 22 (SSH) is not normally open on this group. You will need to use to the EC2 tools to open it before you will be able to attack.
Connecting to the hive.
Attempting to call up 1 bees.
Waiting for bees to load their machine guns...
.
.
.
.
Bee i-b8581a3f is ready for the attack.
The swarm has assembled 1 bees.
```

To check the status of your servers using the `bees report` command as shown below.

```bash
$ bees report
```

```
Read 1 bees from the roster.
Bee i-b8581a3f: running @ 54.100.99.121
```

Now, let's attack or load test a website. This is done using the `attack` command.

```bash
$ bees attack -n 1000 -c 25 -u https://my-website.com/
```

The command show above will make total 1000 connections using 25 concurrent connections.

Finally, you can shutdown the servers using the `down` command as shown below.

```bash
$ bees down
```

```
Read 1 bees from the roster.
Connecting to the hive.
Calling off the swarm.
Stood down 1 bees.
```

## Under the hood

`beeswithmachineguns` does something very simple. When you fire the `up` command, it use the `boto` API to launch the instances. The `up` command makes sure the instances are running before it returns. It does that by checking the instance state inside the `while` loop. It usually takes a minute or two before instances are in running state. You can SSH into instances only when they are in running state.

The `attack` command opens a SSH connection using the `paramiko` API using the `pem` key you specified in the `up` command. Once SSH connection is established, it uses Apache Benchmark tool to generate load on your web site. The parameters you passed with the `attack` command are fed to the `ab`. Finally, it collects the result of `ab` and return them back to the user.


----

That's all for this week. Please provide your valuable feedback by adding a comment to [https://github.com/shekhargulati/52-technologies-in-2016/issues/24](https://github.com/shekhargulati/52-technologies-in-2016/issues/24).

[![Analytics](https://ga-beacon.appspot.com/UA-59411913-2/shekhargulati/52-technologies-in-2016/19-bees)](https://github.com/igrigorik/ga-beacon)
