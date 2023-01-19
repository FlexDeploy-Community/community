//This provider match script for Microsoft Teams validates that it is a message from a card that FlexDeploy created.
//Additional security checks are performed after match and before completing processing of approval tasks.
//These additional checks are in regards to the security that Microsoft provides on the incoming message, and the users and groups setup in FlexDeploy.

return HTTP_HEADERS.containsKey("flex-sync-webhook") && "TeamsTask".equals(HTTP_HEADERS.get("flex-sync-webhook"));