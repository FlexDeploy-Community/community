//Microsoft Teams Incoming Webhooks are validated and processed Synchronously.
//Behind the scenes, the JWT token is validated and the message is checked for unauthorized modification.
//The user that clicked the button is matched to a FlexDeploy user account behind the scenes using the JWT Token found in the HTTP Headers.
//If the matched user is authorized to approve/reject the linked FlexDeploy task, the task is approved or rejected 
//and the card is updated to indicate that the task was processed. 

MICROSOFTTEAMS.updateTask(QUERY_PARAMS,HTTP_HEADERS,"<Teams Messaging Integration Account Code>");//todo fill in your Teams Account Code.
