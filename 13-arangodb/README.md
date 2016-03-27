ArangoDB: Polyglot Persistence Without Cost
------

Welcome to thirteenth week of [52 Technologies in 2016](https://github.com/shekhargulati/52-technologies-in-2016) blog series. This week we will learn about ArangoDB. [ArangoDB](https://www.arangodb.com/) is an open source NoSQL database that provides flexible data model. You can use ArangoDB to model data using combination of document, graph, and key value data modeling styles. Last few years, polyglot persistence has become mainstream. Polyglot persistence as described by [Martin Fowler](http://martinfowler.com/articles/nosql-intro-original.pdf),

> **Polyglot Persistence is using multiple data storage technologies, chosen based upon the way data is being used by individual applications or components of single application.**

When I was working as a developer advocate, I gave few talks on building Polyglot persistence applications. In my talk, I showcased how you can build a location aware Job search application using MongoDB, MySQL, Neo4j, and Redis. MongoDB was used to store Job data and provide location aware search using its geospatial indexes, MySQL was used to store user data, Redis was used to act as cache and session store, and Neo4j was used to recommend jobs "People who applied to this job also applied to these jobs". Using ArangoDB, we can develop the full application using a single database.

One of the issues with building Polyglot persistent applications is the maintenance cost associated with managing multiple databases. ArangoDB can help us minimize the maintenance cost as we have to interact with only one database to meet our application needs.

NoSQL databases are broadly classified into Key Value, Document, Graph, and Columnar datastore.

| Data Model     | Description     |Examples|
| :------------- | :------------- |:------------- |
| Key Value       | Key-value stores use a Map as their fundamental data model| Redis, Amazon Dynamo DB|
| Document       | Stores data as document where everything related to a database object is encapsulated together| MongoDB , CouchDB|
| Graph       | Uses graph structures for semantic queries with nodes, edges and properties to represent and store data| Neo4j, TitanDB |
| Columnar       | Organize data into column families| HBase, Cassandra|

ArangoDB falls in a category of its own as it supports multiple data modeling styles. This makes it more generic and suitable for a lot of use cases. To understand how ArangoDB supports multiple data model styles, we have to understand how it stores data. ArangoDB is a document oriented database where in documents are stored in collections just like any other document oriented database(eg. MongoDB). It acts as key value store as each document is uniquely identified by a key. Documents can be connected to each other allowing you to treat ArangoDB as a graph database.

Apart from being multi model, ArangoDB also has many other features that makes it a good choice for your next application:

1. Support for nested documents.
2. Supports transaction across multiple documents and collections.
3. Support for ACID transactions.
4. Inbuilt support for setting up a sharded cluster using user defined or automatically chosen key.
5. Supports Master/Slave and Master/Master replication.
6. Fully functional web interface for administration.
7. Support for joins to combine data across multiple collections.

## Getting started with ArangoDB

ArangoDB provides distribution for most operating systems. You can download distribution from [downloads page](https://www.arangodb.com/download/).

If you are using mac, then you can install ArangoDB using `brew` package manager.

```bash
$ brew update && brew install arangodb
```

## Start ArangoDB server

Once ArangoDB is installed, you can start it using the `arangod` executable.

```bash
$ /usr/local/sbin/arangod
```

This will start the server and you will see output as shown below. I have removed part of the console output for brevity.

```
2016-03-26T05:56:58Z [11192] INFO Authentication is turned off
2016-03-26T05:56:58Z [11192] INFO ArangoDB (version 2.8.6 [darwin]) is ready for business. Have fun!
```

You can view the web console at [http://localhost:8529/](http://localhost:8529/). ArangoDB provides a fully functional web console which you can use to create databases, collections, perform queries, and administration. In this tutorial, we will use command-line shell `arangosh`.

## Connect to ArangoDB server using `arangosh`

There are multiple ways you can connect with ArangoDB. You can connect to database server using the web console or command-line shell or using your favorite programming language through database driver. We will use `arangosh`, a command-line shell written in JavaScript. It is very similar in use to MongoDB shell so if you have used MongoDB then you will feel home.

```bash
$ arangosh
```

By default, you will be connected to ArangoDB server running on your machine at default server endpoint `tcp://127.0.0.1:8529`. . If your database is running on a different host then you can specify it using the `--server.endpoint` configuration option.

You will be connected to default database `_system`. You can specify name of your database using `server.database` option.

```bash
$ arangosh --server.database localjobs
```

There are many other options that you can pass while launching `arangosh`. You can view all options using `arangosh --help` command..

Let's customize our shell so that it greets us with a voice based welcome message. You can enable it using the `voice` option shown below. If you want to customize look and feel of `arangosh` prompt, then you can use prompt option.

```bash
→ arangosh --voice --prompt "ArangoDB >> %d -> " --quiet

ArangoDB >> _system ->
```

`%d` is used to signify database you are currently connected to. As you can see, we are connected to `_system` database.

Once you are inside the shell, you can perform operations against the database using the `db` object. If you just type `db` and press enter, it will show details of the connected database.

```bash
arangosh [_system]> db
[object ArangoDatabase "_system"]
```

You can view version of the database using `version` function.

```
arangosh [_system]> db._version()
2.8.6
```

To view all methods that you can call on `db` object, type db and press `TAB` key. I am showing few of the methods for brevity.

```
arangosh [_system]> db.
db._collections()               db._name()
db._createDatabase()            db._queues
db._help()                      db._version()
db._id()                        db.toString()
db._index()
```

## Create `localjobs` database

In this tutorial, I will showcase how you can use ArangoDB to build location aware job search application. The main entity of our application is Job. Job contains details like title, skills, and location of the job.

We will create a new database `localjobs` to store application data.

```
arangosh [_system]> db._createDatabase("localjobs")
true
```

If database is successfully created then you will see `true` in the response else you will see exception stack trace with the reason why database was not created. For example, if you try to create database with same name twice, you will get following error.

```
stacktrace: ArangoError: duplicate name
    at ArangoDatabase._createDatabase (/usr/local/Cellar/arangodb/2.8.6/share/arangodb/js/client/modules/org/arangodb/arango-database.js:863:11)
    at <shell command>:1:8
```

Now, that we have created `localjobs` database we can connect to it using the `_useDatabase` method.

```
arangosh [_system]> db._useDatabase("localjobs")
true

arangosh [localjobs]>
```

As you can see above, `arangosh` prompt is now pointing to `localjobs` database.


## Import data using `arangoimp`

ArangoDB comes with a bulk importer `arangoimp` that you can use to import data in either JSON, TSV, or CSV format.

We will import 159 `json` documents that are stored in the `jobs.json` file. I mined this data couple of years back using the LinkedIn API. A single JSON document looks like as shown below.

```json
{
  "company": {
    "id": "21836",
    "name": "CyberCoders"
  },
  "title": "Ruby on Rails Engineer - PostgreSQL, MongoDB, Redis, HTML, CSS",
  "location": [
    33.978622,
    -118.404471
  ],
  "skills": [
    "mongodb"
  ],
  "address": "12181 W. Bluff Creek Dr. Suite 1E, Playa Vista, CA, United States",
  "experience": 19
}
```

As you can see, we have details about the company that has posted the job, job title, skills required, location in the form of latitude and longitude, and address of the job posting.

To import json data file you will run the following command. You can download `jobs.json` from the [repository](https://raw.githubusercontent.com/shekhargulati/52-technologies-in-2016/master/13-arangodb/localjobs/jobs.json). We are running the `arangoimp` command from inside the same directory that contains `jobs.json` file.

```bash
$ arangoimp --server.database localjobs --file "jobs.json" --type json --collection "jobs" --create-collection true --create-collection-type "document"
```

The command shown above will transfer `jobs.json` to the `localjobs` database, create a new document collection `jobs`, import the data into `jobs` collection, and print a status summary as shown below.

```
Connected to ArangoDB 'tcp://127.0.0.1:8529', version 2.8.6, database: 'localjobs', username: 'root'
----------------------------------------
database:         localjobs
collection:       jobs
create:           yes
file:             jobs.json
type:             json
connect timeout:  5
request timeout:  1200
----------------------------------------
Starting JSON import...
2016-03-26T09:27:39Z [12348] INFO processed 32767 bytes (3.0%) of input file
2016-03-26T09:27:39Z [12348] INFO processed 36712 bytes (92.0%) of input file

created:          159
warnings/errors:  0
updated/replaced: 0
ignored:          0
```

Similar to `arangosh`, you can specify additional options like which server endpoint to connect. You can view all options using the `arangoimp --help`. One option that you might find useful when importing very large files is `progress`. It allows you to view the progress of import process.

```
$ arangoimp --server.database localjobs --file "jobs.json" --type json --collection "jobs" --create-collection true --create-collection-type "document" --progress true
```

You can also pipe data from another command to `arangoimp` as shown below.

```
$ cat jobs.json | arangoimp --server.database localjobs --file - --type json --collection "jobs1" --create-collection true --create-collection-type "document"
```

You have to make sure to use `--file -` so that data is read from stdin.

If you want to create a collection manually then you can use following command.

```js
arangosh [localjobs]> db._create("users")
[ArangoCollection 1195802007, "users" (type document, status loaded)]
```

The full signature of `_create` method is `db._create(collection-name, properties)`. You can specify  properties while creating a collection like `waitForSync`,`journalSize`, etc. When you make an insert query to ArangoDB, it does not wait for data to sync to disk. You can change this default behavior by using `waitForSync` property. To learn about all properties you can refer to [documentation](https://docs.arangodb.com/Collections/DatabaseMethods.html).

```js
arangosh [localjobs]> db._create("users",{waitForSync: true})
```

You can view all properties for a collection as shown below.

```js
arangosh [localjobs]> db.users.properties()
{
  "doCompact" : true,
  "journalSize" : 33554432,
  "isSystem" : false,
  "isVolatile" : false,
  "waitForSync" : true,
  "keyOptions" : {
    "type" : "traditional",
    "allowUserKeys" : true
  },
  "indexBuckets" : 8
}
```

## Perform simple queries

Let's perform some queries to fetch data from the `jobs` collection.

You can view any document in `jobs` collection by using the `any` method.

```js
arangosh [localjobs]> db.jobs.any()
{
  "_id" : "jobs/1956683195",
  "_key" : "1956683195",
  "_rev" : "1956683195",
  "experience" : 5,
  "title" : "Web Developer - HTML, CSS, JavaScript",
  "address" : "12181 W. Bluff Creek Dr. Suite 1E, Playa Vista, CA, United States",
  "location" : [
    33.978622,
    -118.404471
  ],
  "skills" : [
    "node.js"
  ],
  "company" : {
    "id" : "21836",
    "name" : "CyberCoders"
  }
}
```

To print all the title of jobs, you can execute following command.

```js
arangosh [localjobs]> var allJobs = db.jobs.all()

arangosh [localjobs]> while(allJobs.hasNext()) print(allJobs.next().title)
```

`db.jobs` returns a cursor. We can iterate over cursor using `hasNext` and `next` methods.

You can write pagination logic using `skip` and limit methods of a cursor.

```js
arangosh [localjobs]> db.jobs.all().limit(2)
SimpleQueryAll(jobs).limit(2)
arangosh [localjobs]> db.jobs.all().skip(2).count(true)
157
```

To find count of all the documents in the database you can use `count` method.

```js
arangosh [localjobs]> db.jobs.count()
159
```

## Performing advance queries with AQL

ArangoDB has its own query language AQL (ArangoDB Query Language) that allows you to perform advance queries. You use `_query` method to execute AQL queries.

To find all jobs with where company name is `Expedia`

```js
var q = `FOR j IN jobs FILTER j.company.name == "Expedia" RETURN j`
var c = db._query(q)
while(c.hasNext())print(c.next())
```

You can store the result in an array.

```js
var q = `FOR j IN jobs FILTER j.company.name == "Expedia" RETURN j`
var result = db._query(q).toArray()
```

You can define your own projections as well. Let's suppose you only want to return title and company name.

```js
arangosh [localjobs]> var q = `FOR j IN jobs FILTER j.company.name == "Expedia" RETURN {title:j.title, companyName: j.company.name, experience: j.experience}`

arangosh [localjobs]> var result = db._query(q).toArray()

arangosh [localjobs]> result
[
  {
  "title" : "Development Manager",
  "companyName" : "Expedia",
  "experience" : 13
  },
  {
    "title" : "SDE II - Ruby Software Engineer",
    "companyName" : "Expedia",
    "experience" : 5
  },
....
]
```

By default, result is unsorted. You can specify the sorting clause using the `SORT` as shown below.

```js
arangosh [localjobs]> var q = `FOR j IN jobs FILTER j.company.name == "Expedia" SORT j.experience RETURN {title:j.title, companyName: j.company.name, experience: j.experience}`

arangosh [localjobs]> var result = db._query(q).toArray()

arangosh [localjobs]> result
[
  {
    "title" : "SDE I - Ruby Software Engineer",
    "companyName" : "Expedia",
    "experience" : 4
  },
  {
    "title" : "SDE II - Ruby Software Engineer",
    "companyName" : "Expedia",
    "experience" : 5
  },
  {
    "title" : "Sr SDE - Senior Ruby Software Engineer",
    "companyName" : "Expedia",
    "experience" : 6
  },
  ...
]
```

AQL also supports grouping and aggregation using `COLLECT` and `AGGREGATE` operators. You can group all the skills together as shown below.

```js
var q = `FOR j IN jobs COLLECT allskills=j.skills RETURN allskills`
var result = db._query(q).toArray()
result.reduce(function(a,b){ return a.concat(b);})
```

You can refer to [documentation](https://docs.arangodb.com/AqlExamples/Grouping.html) to learn more about grouping.

## Performing location aware queries

From the [docs](https://docs.arangodb.com/IndexHandling/Geo.html),

> **ArangoDB uses Hilbert curves to implement geo-spatial indexes. A geo-spatial index assumes that the latitude is between -90 and 90 degree and the longitude is between -180 and 180 degree. A geo index will ignore all documents which do not fulfill these requirements.**

Before you can perform location aware queries, you have to create geo index.


```js
arangosh [localjobs]> db.jobs.ensureIndex({type:"geo",fields:["location"]})
```
```js
{
  "id" : "jobs/2073206203",
  "type" : "geo1",
  "fields" : [
    "location"
  ],
  "geoJson" : false,
  "constraint" : false,
  "unique" : false,
  "ignoreNull" : true,
  "sparse" : true,
  "isNewlyCreated" : true,
  "code" : 201
}
```

Now, you can perform near queries. Let's suppose I want to find 2 jobs near to Gurgon, Haryana, India. As you can see below, jobs from Gurgaon are only returned.

```js
arangosh [localjobs]> db.jobs.near(28.481216, 77.019135).limit(2).toArray().map(function(j){return {title:j.title,location:j.location,address:j.address}})
```
```js
[
  {
    "title" : "Sr SDE - Senior Ruby Software Engineer",
    "location" : [
      28.464956,
      77.064564
    ],
    "address" : "Sector 29, Gurgaon, Delhi, India"
  },
  {
    "title" : "SDE II - Ruby Software Engineer",
    "location" : [
      28.464956,
      77.064564
    ],
    "address" : "Sector 29, Gurgaon, Delhi, India"
  }
]
```

Let's suppose, I want jobs near San Jose California then I can query as follows.

```js
arangosh [localjobs]> db.jobs.near(37.3394444, -121.8938889).limit(5).toArray().map(function(j){return {title:j.title,location:j.location,address:j.address}})
```
```js
[
  {
    "title" : "Director of Cloud",
    "location" : [
      37.33085,
      -121.887947
    ],
    "address" : "302 South Market Street, San Jose, CA, United States"
  },
  {
    "title" : "Senior Ruby Developer-Ruby on Rails Engineer (RoR, MongoDB, Rails, Sinatra)",
    "location" : [
      37.391467,
      -121.977033
    ],
    "address" : "4200 Great America Parkway, Santa Clara, CA, United States"
  },
  {
    "title" : "MTS - Hadoop Distributed File System",
    "location" : [
      37.390284,
      -122.031936
    ],
    "address" : "455 West Maude Avenue, Sunnyvale, CA, United States"
  },
  {
    "title" : "Solutions Engineer - Big Data",
    "location" : [
      37.390284,
      -122.031936
    ],
    "address" : "455 West Maude Avenue, Sunnyvale, CA, United States"
  },
  {
    "title" : "Product Evangelist, Hadoop",
    "location" : [
      37.417223,
      -122.025112
    ],
    "address" : "701 1st Avenue, Sunnyvale, CA, United States"
  }
]
```

## Conclusion

That's all for this week. ArangoDB is a powerful database that provides multi model capabilities. You can refer to its [documentation](https://docs.arangodb.com/) to learn about other capabilities. I will revisit ArangoDB later in this series to showcase how we can build a Java Spring application with it. We will also look at its graph data model support.

Please provide your valuable feedback by posting a comment to [https://github.com/shekhargulati/52-technologies-in-2016/issues/17](https://github.com/shekhargulati/52-technologies-in-2016/issues/17).

[![Analytics](https://ga-beacon.appspot.com/UA-59411913-2/shekhargulati/52-technologies-in-2016/13-arangodb)](https://github.com/igrigorik/ga-beacon)
