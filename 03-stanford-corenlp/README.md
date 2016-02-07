Sentiment Analysis in Scala with Stanford CoreNLP
-----

So far in this [series](https://github.com/shekhargulati/52-technologies-in-2016), we have looked at [finatra](../01-finatra) and [sbt](../02-sbt) open-source Scala projects. This week I decided to learn Stanford CoreNLP library for performing sentiment analysis of unstructured text in Scala.

Sentiment analysis or opinion mining is a field that uses natural language processing to analyze sentiments in a given text. It has applications in many domains ranging from marketing to customer service. Few years back, I wrote a simple Java application using [Naive Bayes classifier](https://en.wikipedia.org/wiki/Naive_Bayes_classifier) to determine whether people liked a movie or not based on sentiment analysis of tweets about a movie.

From the [Stanford CoreNLP website](http://stanfordnlp.github.io/CoreNLP/),

> **Stanford CoreNLP provides a set of natural language analysis tools. It can give the base forms of words, their parts of speech, whether they are names of companies, people, etc., normalize dates, times, and numeric quantities, and mark up the structure of sentences in terms of phrases and word dependencies, indicate which noun phrases refer to the same entities, indicate sentiment, extract open-class relations between mentions, etc.**

<center><img src="https://avatars1.githubusercontent.com/u/3046006" width="150"></center>

## Github repository

The code for today’s demo application is available on github: [sentiment-analyzer](./sentiment-analyzer).

## Getting Started

Start by creating a new directory `sentiment-analyzer` at a convenient location on your filesystem. This directory will house the source code of our application.

```bash
$ mkdir sentiment-analyzer
```

Create a new file `build.sbt` inside the `sentiment-analyzer` directory. `build.sbt` is the sbt build script.
> **If you are new to sbt, then [please refer to my earlier post on it](../02-sbt/README.md).**

Populate `build.sbt` with following contents.

```scala
name := "sentiment-analyzer"
description := "A demo application to showcase sentiment analysis using Stanford CoreNLP and Scala"
version  := "0.1.0"

scalaVersion := "2.11.7"

libraryDependencies += "edu.stanford.nlp" % "stanford-corenlp" % "3.5.2" artifacts (Artifact("stanford-corenlp", "models"), Artifact("stanford-corenlp"))
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.6" % "test"
```

One thing that you might not understand in the above mentioned build script is the usage of `artifacts`. `artifacts` is used when the dependency you have defined in your build script has published multiple artifacts. We have used `artifacts` above to tell sbt that we need to include both `stanford-corenlp` and `stanford-models` dependencies in our classpath. `stanford-corenlp` defines the core API that we will use in our code and `stanford-models` contains all the data model files that `stanford-corenlp` library uses underneath. `stanford-models` library is **378.1 MB** in size so sbt will take some time to download it.

Create a project layout for your Scala source and test files.

```bash
$ mkdir -p src/main/{scala,resources}
$ mkdir -p src/test/scala
```

## Writing SentimentAnalyzer

The main part of the application is to analyze text for sentiments. We will write a sentiment analyzer in Scala that uses `stanford-corenlp` API.

Let's start by writing a test case for positive sentiment. Create a new file `SentimentAnalyzerSpec.scala` inside `src/test/scala` directory. We are using `scalatest` to write our test cases.

```scala
import org.scalatest.{FunSpec, Matchers}

class SentimentAnalyzerSpec extends FunSpec with Matchers {

  describe("sentiment analyzer") {

    it("should return POSITIVE when input has positive emotion") {
      val input = "Scala is a great general purpose language."
      val sentiment = SentimentAnalyzer.mainSentiment(input)
      sentiment should be(Sentiment.POSITIVE)
    }
}
```

The test case shown above calls the `SentimentAnalyzer` API's `mainSentiment` method. If the sentiment returned by `SentimentAnalyzer` is `Sentiment.POSITIVE` then the test will pass. The `mainSentiment` method will return sentiment of the largest line of the text i.e. for input `Scala is a great general purpose language. I don't use it often.` will return `Sentiment.POSITIVE` as the longer line of the text `Scala is a great general purpose language` has positive emotion.

`Sentiment` is an enum that we have defined in our application.

```scala
object Sentiment extends Enumeration {
  type Sentiment = Value
  val POSITIVE, NEGATIVE, NEUTRAL = Value

  def toSentiment(sentiment: Int): Sentiment = sentiment match {
    case x if x == 0 || x == 1 => Sentiment.NEGATIVE
    case 2 => Sentiment.NEUTRAL
    case x if x == 3 || x == 4 => Sentiment.POSITIVE
  }
}
```

`Sentiment` is a Scala enum with a `toSentiment` method defined. The `toSentiment` method is used by `SentimentAnalyzer`(discussed below) to convert integer sentiment value returned by `stanford-corenlp` API to enum constant. The `stanford-corenlp` library gives sentiment of 0 or 1 when text has negative emotion, 2 when text is neutral, 3 or 4 when text has positive emotion.

Let's now discuss about `SentimentAnalyzer`. Full source code of `SentimentAnalyzer` is shown below.

```scala
import java.util.Properties

import com.shekhargulati.sentiment_analyzer.Sentiment.Sentiment
import edu.stanford.nlp.ling.CoreAnnotations
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations
import edu.stanford.nlp.pipeline.{Annotation, StanfordCoreNLP}
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations

import scala.collection.convert.wrapAll._

object SentimentAnalyzer {

  val props = new Properties()
  props.setProperty("annotators", "tokenize, ssplit, parse, sentiment")
  val pipeline: StanfordCoreNLP = new StanfordCoreNLP(props)

  def mainSentiment(input: String): Sentiment = Option(input) match {
    case Some(text) if !text.isEmpty => extractSentiment(text)
    case _ => throw new IllegalArgumentException("input can't be null or empty")
  }

  private def extractSentiment(text: String): Sentiment = {
    val (_, sentiment) = extractSentiments(text)
      .maxBy { case (sentence, _) => sentence.length }
    sentiment
  }

  def extractSentiments(text: String): List[(String, Sentiment)] = {
    val annotation: Annotation = pipeline.process(text)
    val sentences = annotation.get(classOf[CoreAnnotations.SentencesAnnotation])
    sentences
      .map(sentence => (sentence, sentence.get(classOf[SentimentCoreAnnotations.SentimentAnnotatedTree])))
      .map { case (sentence, tree) => (sentence.toString,Sentiment.toSentiment(RNNCoreAnnotations.getPredictedClass(tree))) }
      .toList
  }

}
```

The code shown above does the following:

1. Creates an instance of `StanfordCoreNLP`. `StanfordCoreNLP` internally constructs a pipeline that takes a text and returns various analyzed linguistic forms. The properties defines which all annotators will be used by the pipeline. For this application `tokenize, ssplit, parse, sentiment` annotators will be used.

2. The `mainSentiment` method checks if string is valid and if valid it calls the `extractSentiment` with the input text.

3. The `extractSentiment` method calls `maxBy` operation on a list of two value tuple returned by `extractSentiments`. The tuple contains a sentence and sentiment. The maxBy operation compares values based on sentence length i.e. a sentence with largest length will be used as main sentiment of text.

4. The `extractSentiments` method uses `StanfordCoreNLP` to process the text. The `process` method processes the text by running the pipeline on the input text. We then get all the sentence annotations from the `annotation`. As we have already imported `scala.collection.convert.wrapAll._` so we can call all the usual Scala collection methods on the `sentences` list. For each sentence annotation in the sentences annotation, we create a tuple of sentence text and sentiment value. Finally, we return the transformed tuple(tuple of sentence text and sentiment) list.

We can also write test cases for negative and neutral scenarios as shown below.

```scala
it("should return NEGATIVE when input has negative emotion") {
  val input = "Dhoni laments bowling, fielding errors in series loss"
  val sentiment = SentimentAnalyzer.mainSentiment(input)
  sentiment should be(Sentiment.NEGATIVE)
}

it("should return NEUTRAL when input has no emotion") {
  val input = "I am reading a book"
  val sentiment = SentimentAnalyzer.mainSentiment(input)
  sentiment should be(Sentiment.NEUTRAL)
}
```

We can also write another public method that just returns all the sentences and their sentiments as shown below.

```scala
def sentiment(input: String): List[(String, Sentiment)] = Option(input) match {
  case Some(text) if !text.isEmpty => extractSentiments(text)
  case _ => throw new IllegalArgumentException("input can't be null or empty")
}
```

That's all for this week. Please provide your valuable feedback by adding a comment to https://github.com/shekhargulati/52-technologies-in-2016/issues/5.

[![Analytics](https://ga-beacon.appspot.com/UA-59411913-2/shekhargulati/52-technologies-in-2016/03-stanford-corenlp)](https://github.com/igrigorik/ga-beacon)
