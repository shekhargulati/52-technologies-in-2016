Building `Read It Later` App with Python Newspaper Library
-----

Welcome to sixteenth week of [52 Technologies in 2016](https://github.com/shekhargulati/52-technologies-in-2016) blog series. This week I will show you how to build a simple yet working **Read It Later** application using Twitter likes (or favorites). I rely on my Twitter feed for my daily reading recommendations. I check Twitter several time during the day and whenever I find any article that interests me I like it so that I can read it later. In this tutorial, you will learn how to build `Read It Later` application using Python programming language. We will make use of article extraction to extract relevant content from the urls.

To build this application, we will use following Python libraries:

1. **Flask**: We will use `Flask` framework to take care of the web feature of the app i.e. handling requests and responding to use with reading recommendation.
2. **Tweepy**: `Tweepy` is a very easy to use library that we will use to talk to Twitter streaming API. We will subscribe to a user's Twitter stream so as soon as user likes a tweet our application will be notified.
3. **Newspaper**: [newspaper](https://github.com/codelucas/newspaper) is a great library to perform article extraction in Python. It makes use of popular Python libraries like `beautifulsoup4`, `lxml`, `nltk` to get the job done.

By the end of this tutorial, you will have a simple yet working application to view articles that you wanted to read later. The following is the screenshot of our application. As you can see below, we extracted main image, summary text, and title from the url.

![](images/stories.jpg)

> **This blog is part of my year long blog series [52 Technologies in 2016](https://github.com/shekhargulati/52-technologies-in-2016)**

## What is Newspaper?

In this tutorial, I will talk about a Python package called [Newspaper](http://newspaper.readthedocs.org/). Newspaper is an open source news full-text and article metadata extraction library written in Python 3. It has a very easy to use API that can help you get started in minutes. It can be used to extract the main text of an article, main image of an article, videos in an article, meta description, and meta tags in an article. Newspaper stands on strong shoulders of `beautifulsoup4`, `lxml`, and `nltk` libraries.

To extract text from a URL is as simple as shown below.

```python
>>> from newspaper import Article

>>> url = 'http://firstround.com/review/the-remarkable-advantage-of-abundant-thinking/'
>>> article = Article(url)
>>> article.build()

>>> article.title
'The Remarkable Advantage of Abundant Thinking'

>>> article.text.split('\n\n')[0]
"If you consider yourself to be ambitious, this has happened to you. Your alarm goes off, and you're ambushed by thoughts of the grind ahead; finding that needle in a haystack; denting the universe; the roller coaster that never ends and many more horrible but unfortunately apt cliches. Today, the groupthink in tech largely believes that you have to suffer and barely survive to succeed. But this is a trap, says sought-after executive coach Katia Verresen, who counsels leaders at Facebook, Stanford, Airbnb, Twitter, and a number of prominent startups."
```

## Prerequisite

To follow this blog, you need to have following on your machine:

1. **Python**: You can download Python executable for your operating system from [https://www.python.org/downloads/](https://www.python.org/downloads/). I will be using Python version `3.4.2`.

2. **Virtualenv**: Virtualenv tool allows you to create isolated Python environments without polluting the global Python installation. This allows you to use multiple Python versions easily on a single machine. Please refer to official documentation for [installation](https://virtualenv.pypa.io/en/latest/installation.html) instructions.

3. Create a new Twitter app [http://dev.twitter.com/apps](http://dev.twitter.com/apps) and note down `Consumer Key (API Key)`, `Consumer Secret (API Secret)`, `Access Token`, and `Access Token Secret`. We will use them to connect to Twitter API for a user.

## Github Repository

The code for demo application is available on github at [dailyreads](https://github.com/shekhargulati/dailyreads).

## Step 1: Setting up environment

We will start with setting up development environment so that we can build `dailyreads` application. I use Python `virtualenv` for most of my Python applications. It helps keep my project environment separate and does not pollute global Python installation.

Open a command-line terminal and navigate to a convenient directory on your file system. Make a new directory called `dailyreads` and change directory to it.

```bash
$ mkdir dailyreads && cd dailyreads
```

Once inside the `dailyreads` directory, create a Python 3 virtualenv and activate it.

```bash
$ virtualenv venv --python=python3
$ source venv/bin/activate
```

You can verify the Python installation by executing `which python` command. It should point to the Python installation inside the `venv` directory.

## Step 2: Download and install dependencies

As mentioned above, we will make use of `flask`, `tweepy`, and `newspaper` libraries. We can download all of them using `pip`. `pip` is package manager used to install and manage software packages written in Python.

```bash
$ pip install flask
$ pip install tweepy
$ pip install newspaper3k
```

This will download all the required dependencies and their transitive dependencies.  To view all the installed packages, you can use the `pip list` command.

## Step 3: Write twitter stream listener for user liked tweets

Create a new file `app.py` inside the `dailyreads` directory. The first task of our application is to listen to user's like tweet stream. We will make use of `Tweepy` library to connect to user tweet stream and select events that are of type `favorite` as shown below.

```python
from __future__ import absolute_import, print_function
from tweepy.streaming import StreamListener
from tweepy import OAuthHandler
from tweepy import Stream
import os
import json

consumer_key=os.getenv("TWITTER_CONSUMER_KEY")
consumer_secret=os.getenv("TWITTER_CONSUMER_SECRET")
access_token=os.getenv("TWITTER_ACCESS_TOKEN")
access_token_secret=os.getenv("TWITTER_ACCESS_SECRET")

class LikedTweetsListener(StreamListener):
    def on_data(self, data):
        tweet = json.loads(data)
        if 'event' in tweet and tweet['event'] == "favorite":
            print(tweet)
        return True

    def on_error(self, status):
        print("Error status received : {0}".format(status))

if __name__ == '__main__':
    l = LikedTweetsListener()
    auth = OAuthHandler(consumer_key, consumer_secret)
    auth.set_access_token(access_token, access_token_secret)

    stream = Stream(auth, l)
    stream.userstream()
```

In the code shown above, we did following:

1. We imported all the required classes and method that we will need to connect Twitter API.
2. We extracted twitter keys from the environment variables and set them as script variables. It is a good idea to use environment variables from the beginning to make sure you don't commit your keys to version control system and end up making them public.
3. We created a new `StreamListener` listener. `StreamListener` exposes several `on_*` methods that will be invoked when particular events happen. We have overridden `on_data` and `on_error` handlers. As is obvious from their names, `on_data` is called whenever new data is available and `on_error` is called whenever any exception happens. In the `on_data` method, we are only printing out events that are of type `favorite`. Twitter uses event type of favorite for like tweets as well.
4. Finally, we created an instance of `Stream` object and invoked`userstream` method on it.

You can run `app.py` using the `python app.py`. Make sure to set the environment variables before running the script.

```
export TWITTER_CONSUMER_KEY=*********************
export TWITTER_CONSUMER_SECRET=******************************************
export TWITTER_ACCESS_TOKEN=**************************************************
export TWITTER_ACCESS_SECRET=******************************************
```

## Step 4: Extract article content from the tweet

This is the main part of the application. Now, that we have handle to a liked tweet. We have to extract the content from it. We will use `newspaper` library to perform article extraction for us.

```python
from __future__ import absolute_import, print_function
from tweepy.streaming import StreamListener
from tweepy import OAuthHandler
from tweepy import Stream
import os

consumer_key=os.getenv("TWITTER_CONSUMER_KEY")
consumer_secret=os.getenv("TWITTER_CONSUMER_SECRET")
access_token=os.getenv("TWITTER_ACCESS_TOKEN")
access_token_secret=os.getenv("TWITTER_ACCESS_SECRET")

articles = []

class LikedTweetsListener(StreamListener):
    def on_data(self, data):
        tweet = json.loads(data)
        if 'event' in tweet and tweet['event'] == "favorite":
            liked_tweet = tweet["target_object"]
            liked_tweet_text = liked_tweet["text"]
            story_url = extract_url(liked_tweet)
            if story_url:
                article = extract_article(story_url)
                if article:
                    article['story_url'] = story_url
                    article['liked_on'] = time.time()
                    articles.append(article)
        return True

    def on_error(self, status):
        print("Error status received : {0}".format(status))


def extract_url(liked_tweet):
    url_entities = liked_tweet["entities"]["urls"]
    if url_entities and len(url_entities) > 0:
        return url_entities[0]['expanded_url']
    else:
        return None    


from newspaper import Article

def extract_article(story_url):
    article = Article(story_url)
    article.download()
    article.parse()
    title = article.title
    img = article.top_image
    publish_date = article.publish_date
    text = article.text.split('\n\n')[0] if article.text else ""
    return {
        'title':title,
        'img':img,
        'publish_date':publish_date,
        'text':text.encode('ascii','ignore')
    }
```

The `extract_article` method shown above does all the important work using the newspaper library. To use it, first import the `Article` class from the `newspaper` module. Then, you build the article by first instantiating it with the `url` and then calling `download` and `parse` methods. The `download` method downloads the page content and `parse` method extract the relevant information from the page. Finally, we build a `dict` object with all the relevant information and return it.

## Step 5: Render articles using Flask

Now, we will a build a simple web application that will render articles. This will also reside inside the `app.py` file.

```python
from flask import Flask, render_template

app = Flask(__name__)


@app.route("/")
def index():
    return render_template("index.html", articles=sorted(articles, key=lambda article: article["liked_on"], reverse=True))

if __name__ == '__main__':
    l = LikedTweetsListener()
    auth = OAuthHandler(consumer_key, consumer_secret)
    auth.set_access_token(access_token, access_token_secret)

    stream = Stream(auth, l)
    stream.userstream(async=True)

    app.run(debug=True)    
```


When the request will be made to `\` then we will render `index.html`. The `index.html` will use `articles` that we populated in the previous step.

I have used a [free Twitter bootstrap theme](http://startbootstrap.com/template-overviews/1-col-portfolio/) for styling purpose.

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">
    <title>Daily Reads</title>
    <link href="{{ url_for('static', filename='css/bootstrap.min.css') }}" rel="stylesheet">
    <link href="{{ url_for('static', filename='css/1-col-portfolio.css') }}" rel="stylesheet">
</head>

<body>
    <div class="container">

        {% for article in articles %}
            <div class="row">
            <div class="col-md-7">
                <a href="#">
                    <img class="img-responsive" src="{{ article.img }}" alt="">
                </a>
            </div>
            <div class="col-md-5">
                <h3>{{ article.title }}</h3>
                <p>{{ article.text.decode("utf-8") }}</p>
                <a class="btn btn-primary" href="{{ article.story_url }}" target="_blank">Read Full Article <span class="glyphicon glyphicon-chevron-right"></span></a>
            </div>
        </div>
        <hr><hr>
        {% endfor %}

    </div>
</body>

</html>
```

-----

That's all for this week.

Please provide your valuable feedback by posting a comment to [https://github.com/shekhargulati/52-technologies-in-2016/issues/20](https://github.com/shekhargulati/52-technologies-in-2016/issues/20).

[![Analytics](https://ga-beacon.appspot.com/UA-59411913-2/shekhargulati/52-technologies-in-2016/16-newspaper)](https://github.com/igrigorik/ga-beacon)
