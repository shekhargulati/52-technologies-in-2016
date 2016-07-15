//The application checks for GitHub Status and raises a Slack Notification if it is down
package main

import (
	"encoding/json"
	"fmt"
	"io/ioutil"
	"log"
	"net/http"
	"net/url"
	"time"
)

const (
	GitHubStatusAPIURL      = "https://status.github.com/api/last-message.json"
	SlackIncomingWebhookURL = "YOUR_SLACK_INCOMING_WEBHOOK_URL"
)

func check(e error) {
	if e != nil {
		log.Fatalf("Error in GitHubStatus application. Error : %v", e)
	}
}

//GitHubStatusLastMessage struct
type GitHubStatusLastMessage struct {
	Status   string    `json:"status"`
	Message  string    `json:"body"`
	CreateDT time.Time `json:"created_on"`
}

var githubStatus GitHubStatusLastMessage

//SlackMessage is the struct that we will post to Incoming Slack Webhook URL
type SlackMessage struct {
	Message string `json:"text"`
}

func main() {

	//Initiate the GET call to GitHub Status API at https://status.github.com/api/last-message.json
	resp, err := http.Get(GitHubStatusAPIURL)
	check(err)
	defer resp.Body.Close()

	//Read the Response and unmarshall it into GitHubStatusLastMessage struct
	body, _ := ioutil.ReadAll(resp.Body)
	err = json.Unmarshal(body, &githubStatus)
	check(err)

	//Check if the GitHub Status reported is major and raise the Alert
	//The Status is either good (green) , minor (yellow) or major (red)
	//if githubStatus.Status == "major" {
	//	raiseSlackNotification(githubStatus)
	//}
	raiseSlackNotification(githubStatus)
}

//raiseSlackNotification does a HTTP POST to the Incoming Webhook Integration in your Slack Team
func raiseSlackNotification(status GitHubStatusLastMessage) {
	const WebhookURL = SlackIncomingWebhookURL
	postParams := SlackMessage{fmt.Sprintf("Current Github Status : %v. Message : %v Reported at %v", status.Status, status.Message, status.CreateDT)}
	b, err := json.Marshal(postParams)
	if err != nil {
		log.Printf("Error in creating POST Message. Error : %v", err)
		return
	}
	v := url.Values{"payload": {string(b)}}
	_, err = http.PostForm(WebhookURL, v)
	if err != nil {
		log.Printf("Error in sending Slack Notification. Error : %v", err)
	}
}
