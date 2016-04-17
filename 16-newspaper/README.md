Building Your "Read It Later" App using Python and Newspaper Library
---

Welcome to sixteenth week of [52 Technologies in 2016](https://github.com/shekhargulati/52-technologies-in-2016) blog series. This week I will show you how you can build a simple yet working **Read It Later** application. I rely on my Tweet feed for my daily reading recommendations. I check twitter several time during the day and whenever I find any article that interests me I like it so that I can read it later. In this tutorial, you will learn how we can use a Python library [newspaper](https://github.com/codelucas/newspaper) to perform article extraction and build our simple app.

Once application is developed, you will be able to see your Twitter favorites on the application index page.

![](images/stories.jpg)

## Step 1: Setting up environment

I use Python `virtualenv` for most of my Python applications. It helps me keep my project environment separate and does not pollute global Python installation.

Open a command-line terminal and navigate to a convenient directory on your file system. Make a directory `dailyreads` and change directory to it.


```bash
$ mkdir dailyreads && cd dailyreads
```

Once inside the dailyreads directory, create a Python 3 virtualenv and activate it.

```bash
$ virtualenv venv --python=python3
$ source venv/bin/activate
```

You can verify the Python installation by executing `which python` command. It should point to the Python installation inside the `venv` directory.

## Step 2: Download the dependencies

This application will make use of flask to build a simple web application. We will use `tweepy` to work with Twitter stream API. Finally, we will use `newspaper` library to perform article extraction.

```bash
$ pip install flask
$ pip install tweepy
$ pip install newspaper3k
```

This will download all the required `newspaper3k` library and its transitive dependencies.

## Step 3: Write twitter stream listener for user liked tweets

We will start by writing a Python application that will subscribe to the Twitter User Stream and print out all the events of type `favorite`.

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

## Step 4: Extract article content from the url_for

Now, we will extract article content from the tweet using the `newspaper` library.

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


When the request will be made to `\` then we will render `index.html`. The `index.html` will use `articles` that we populated in the previous step. I have used a [free Twitter bootstrap theme](http://startbootstrap.com/template-overviews/1-col-portfolio/) for styling purpose.

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
