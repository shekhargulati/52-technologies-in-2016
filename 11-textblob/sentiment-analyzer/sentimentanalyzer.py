from flask import Flask , jsonify, render_template, request
from textblob import TextBlob

app = Flask(__name__)

@app.route('/')
@app.route('/index')
def index():
	return render_template('index.html')

@app.route('/api/sentiment',methods=['POST'])
def sentiment():
	text = TextBlob(request.form['message'])
	response = {'polarity' : text.polarity , 'subjectivity' : text.subjectivity}
	return jsonify(response)

if __name__ == "__main__":
	app.run(debug=True)
