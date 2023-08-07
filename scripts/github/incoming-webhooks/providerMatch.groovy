// perform checks and functions to ensure an incoming message is valid and matches this provider
LOG.fine("Evaluating GitHub for incoming message");
def match = false;
def gitHubSecret = 'REPLACE_ME';//todo put your secret here.
 
// validating based on GitHub secret
if (HTTP_HEADERS['user-agent'] && HTTP_HEADERS['user-agent'].toLowerCase().contains('github-hookshot'))
{
    //generate hmac string, be sure to replace with your github secret
    def HMAC_RESULT = HMAC.generateHmacSHA1(FLX_PRISTINE_PAYLOAD, gitHubSecret);
    def RECEIVED_HMAC = HTTP_HEADERS['x-hub-signature'];
     
    match = RECEIVED_HMAC && RECEIVED_HMAC.contains(HMAC_RESULT);
}
 
LOG.fine("GitHub provider is a match: ${match}");
return match;