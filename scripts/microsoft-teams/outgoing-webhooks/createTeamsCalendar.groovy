// 6.5.0.9+
//This works with client_credentials selected on the Teams messaging instance
import groovy.json.JsonSlurper;
import org.json.JSONObject;

def slurper = new groovy.json.JsonSlurper()

JSONObject body = slurper.parseText("""{
  "subject": "Let's go for lunch",
  "body": {
    "contentType": "HTML",
    "content": "Does noon work for you?"
  },
  "start": {
      "dateTime": "2023-09-15T12:00:00",
      "timeZone": "Pacific Standard Time"
  },
  "end": {
      "dateTime": "2023-09-15T14:00:00",
      "timeZone": "Pacific Standard Time"
  },
  "location":{
      "displayName":"Harry's Bar"
  },
  "attendees": [
  ],
  "allowNewTimeProposals": true
}""");

MICROSOFTTEAMS.createCalendarEvent("TEAMSKARL",body,"email.address@calendar.domain")
