import requests
import os

print(os.environ['MY_ENV_VARIABLE'])

r = requests.get('https://status.github.com/api/status.json')
print(r.text)
