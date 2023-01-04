//Execute FLEXDEPLOY functions on an incoming webhook message
def functionName = "BuildGitHub";
LOG.info("Running function: ${functionName}");
 
//Find and build projects from the payload
//If the QUERY_PARAMS include a projectId that single project will be built instead
//Lastly, the 3rd argument, when true, will create branches on the projects when the branch does not exist
GITHUB.buildProjects(PAYLOAD, QUERY_PARAMS, true);
GITHUB.buildPackages(PAYLOAD, QUERY_PARAMS, true);