import twitter
import re

api = twitter.Api(consumer_key='nofTSEsC8jT0MyUkUBfTnWNNO',
                    consumer_secret='7DAqM3KwlMAIszRGwdlF9nEbavJNs6vVGJjWIUiEJrOZWrURl1',
                    access_token_key='2375649307-0zNdNzC0ucpXSYjbst0wki27sQFJAvtpTraD1MT',
                    access_token_secret='DwUlmv11PdNRPQ93bIQeLIzSUsmAjlb3L09QcIPXcSDRf')

def tweet_handler(event, context):
    commit_message = event["head_commit"]["message"]
    url = event["repository"]["url"]
    print commit_message
    print url
    if re.search('new blog', commit_message, re.IGNORECASE):
        status = "%s at %s" % (commit_message, url)
        api.PostUpdate(status)
    else:
        print("Commit message was not about new blog so ignoring...")
