// Function Script - Create Approvable Tasks in Teams Channels (Outgoing Webhook)

//todo Set the link in the following line to point to an incoming webhook that you created where the approval action will be received.
String message = MICROSOFTTEAMS.makeTaskCreatedMessageForWebhook(EVENT.payload,"https://<urlToYourFlexDeployServerOrExternalProxy>/flexdeploy/webhooks/v1/<uri setup in Incoming Webhook>");

String webhookUrlBI = "https://flexagon.webhook.office.com/webhookb2/cb7f2430-...9ee6-98a7eae43f9f"; //todo set your webhook url here.
String webhookUrlMule = "https://flexagon.webhook.office.com/webhookb2/cb7f2430-9...-98a7eae43f9f";
String webhookUrlSF = "https://flexagon.webhook.office.com/webhookb2/cb7f2430-96dc-4....-98a7eae43f9f";

if(condition for BI Approval){
MICROSOFTTEAMS.sendTeamsWebhookMessage(webhookUrlBI,message);
} else if(condition for Mule Approval){
MICROSOFTTEAMS.sendTeamsWebhookMessage(webhookUrlMule,message);
} else if(condition for SF Approval){
MICROSOFTTEAMS.sendTeamsWebhookMessage(webhookUrlSF,message);
}
