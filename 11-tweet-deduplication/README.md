Tweet Deduplication
---

Welcome to the eleventh blog of [52 Technologies in 2016](https://github.com/shekhargulati/52-technologies-in-2016)  blog series. This week I decided to write a tweet deduplication library. The library will give you a stream of deduplicated tweets. The definition of duplicates can vary depending on the application. For my usecase, any tweet which has same text or talks about same URL is a duplicate.

`tweet-deduplication` library provides deduplication functionality over tweets. It uses [rx-tweet-stream](https://github.com/shekhargulati/rx-tweet-stream) to get a RxJava Observable over a twitter status stream. `rx-tweet-stream` wraps Twitter4J Streaming API to provide an Observable of tweets. `tweet-deduplication` API uses JDK 8, RxJava, and Twitter4J.

The definition of duplicate depends on the application. The default implementation assumes a tweet to be a duplicate if it is talking about same URL or if it has same text. URLs are normalized using the normalizations described in [RFC 3986](https://tools.ietf.org/html/rfc3986). This allows us to determine if two syntactically different URLs are equivalent for example `https://www.github.com/?` is same as `https://github.com`. URL , I will use [urlcleaner](https://github.com/shekhargulati/urlcleaner) library that I have written over the weekend as well.

Text comparison is done using SHA1 hash. Two tweets are considered same if their SHA1 hashes are same.

## Github repository

The code for `tweet-deduplication` is available on github: [tweet-deduplication](https://github.com/shekhargulati/tweet-deduplication).


Getting Started
--------

To use `tweet-deduplication` in your application, you have to add `tweet-deduplication` in your classpath. `tweet-deduplication` is available on Maven Central so you just need to add dependency to your favorite build tool as show below.

For Apache Maven users, please add following to your pom.xml.

```xml
<dependencies>
    <dependency>
        <groupId>com.shekhargulati.deduplication.tweet</groupId>
        <artifactId>tweet-deduplication</artifactId>
        <version>0.1.0</version>
        <type>jar</type>
    </dependency>
</dependencies>
```

Gradle users can add following to their build.gradle file.

```groovy
compile(group: 'com.shekhargulati.deduplication.tweet', name: 'tweet-deduplication', version: '0.1.0', ext: 'jar')
```

## Usage

The example shown below uses the Twitter4j environment variables

```
export twitter4j.debug=true
export twitter4j.oauth.consumerKey=*********************
export twitter4j.oauth.consumerSecret=******************************************
export twitter4j.oauth.accessToken=**************************************************
export twitter4j.oauth.accessTokenSecret=******************************************
```

```java
import com.shekhargulati.deduplication.tweet.TweetDeDuplicator;

TweetDeDuplicator tweetDeDuplicator = TweetDeDuplicator.getDeduplicatorWithInmemoryRepositories();
tweetDeDuplicator.deduplicate("Your Twitter Search Term").subscribe(System.out::println);
```

If you don't want to use environment variables, then you can use the overloaded method that allows you to pass configuration object.

```java
ConfigurationBuilder cb = new ConfigurationBuilder();
cb.setDebugEnabled(true)
        .setOAuthConsumerKey("*********************")
        .setOAuthConsumerSecret("******************************************")
        .setOAuthAccessToken("**************************************************")
        .setOAuthAccessTokenSecret("******************************************");

tweetDeDuplicator.deduplicate(cb.build(), "Your Twitter Search Term").subscribe(System.out::println);
```

You can deduplicate multiple search items at once as well. The code shown below will give you deduplicated content across `java`, `programming`, and `rxjava` search terms.

```java
import com.shekhargulati.deduplication.tweet.TweetDeDuplicator;

TweetDeDuplicator tweetDeDuplicator = TweetDeDuplicator.getDeduplicatorWithInmemoryRepositories();
tweetDeDuplicator.deduplicate("java","programming","rxjava").subscribe(System.out::println);
```

You can also deduplicate tweets for specify users as shown below. The code shown below will track users with id 1,2,3,or 4 and give you deduplicated stream across them. You can follow at max 200 users.

```java
import com.shekhargulati.deduplication.tweet.TweetDeDuplicator;

TweetDeDuplicator tweetDeDuplicator = TweetDeDuplicator.getDeduplicatorWithInmemoryRepositories();
tweetDeDuplicator.deduplicate(1,2,3,4).subscribe(System.out::println);
```

## Tweet deduplication in action

I tested the library on a trending hashtag and results were promising. It was able to clean most of the deduplicated tweets. The output shown below is output of a Tweet Observable with and without deduplication.

```
Tweets per minute without deduplication 99
Tweets per minute with deduplication 70
Tweets per minute without deduplication 98
Tweets per minute with deduplication 68
Tweets per minute without deduplication 85
Tweets per minute with deduplication 54
Tweets per minute without deduplication 75
Tweets per minute with deduplication 47
Tweets per minute without deduplication 83
Tweets per minute with deduplication 48
Tweets per minute without deduplication 79
Tweets per minute with deduplication 35
Tweets per minute without deduplication 101
Tweets per minute with deduplication 62
Tweets per minute without deduplication 72
Tweets per minute with deduplication 45
Tweets per minute without deduplication 82
Tweets per minute with deduplication 45
Tweets per minute without deduplication 85
Tweets per minute with deduplication 32
Tweets per minute without deduplication 82
Tweets per minute with deduplication 50
Tweets per minute without deduplication 85
Tweets per minute with deduplication 39
Tweets per minute without deduplication 86
Tweets per minute with deduplication 52
Tweets per minute without deduplication 78
Tweets per minute with deduplication 45
Tweets per minute without deduplication 72
Tweets per minute with deduplication 37
Tweets per minute without deduplication 86
Tweets per minute with deduplication 43
Tweets per minute without deduplication 74
Tweets per minute with deduplication 39
```

Code to achieve this data is shown below.

```java
Observable<Status> tweetsObs = TweetStream.of("DaylightSavingTime");
tweetsObs.buffer(1, TimeUnit.MINUTES).map(List::size).subscribe(count -> System.out.println(String.format("Tweets per minute without deduplication %d", count)));
tweetDeDuplicator.deduplicate(tweetsObs).buffer(1, TimeUnit.MINUTES).map(List::size).subscribe(count -> System.out.println(String.format("Tweets per minute with deduplication %d", count)));
```

License
-------

`tweet-deduplication` is licensed under the MIT License - see the `LICENSE` file for details.
