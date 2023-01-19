import flexagon.fd.model.pojos.rest.topology.integrations.SCMInstancePojo;
import flexagon.fd.model.pojos.rest.properties.PropertyValuePojo;
import flexagon.fd.model.pojos.rest.project.*;
import flexagon.fd.core.enums.SCMTypeEnum;
import flexagon.fd.core.enums.ProjectTypeEnum;
import flexagon.fd.model.pojos.rest.properties.PropertyValuePojo;
 
//Execute FLEXDEPLOY functions on an incoming webhook message
def functionName = "myFunction";
LOG.info("Running function: ${functionName}");
 
def REPO_NAME= PAYLOAD.repository.name;
LOG.info("REPO_NAME: " + REPO_NAME);
 
def scmInstance = new SCMInstancePojo();
scmInstance.setInstanceCode(REPO_NAME);
scmInstance.setInstanceName(REPO_NAME);
scmInstance.setScmType("GIT");
def props = [new PropertyValuePojo("FDGIT_URL", PAYLOAD.repository.git_url), new PropertyValuePojo("FDGIT_USER", "flexagon9"), new PropertyValuePojo("FDGIT_PASSWORD", null, 12345L)];
 
scmInstance.setProperties(props);
 
LOG.info(scmInstance.toString());
 
def scmId = FLEXDEPLOY.createSCMInstance(scmInstance);
LOG.info(scmId.toString());
 
def proj = new ProjectPojo();
proj.setProjectName(REPO_NAME);
proj.setApplicationId(880708L);//todo set to the folder in which you want the project to go.
proj.setPartialDeployment(true);//todo change if needed.
proj.setProjectType(ProjectTypeEnum.EBS);//todo change or delete if needed.
proj.setScmType(SCMTypeEnum.GIT);
 
def buildInfo = new ProjectBuildInfo();
def deployInfo = new ProjectDeployInfo();
 
buildInfo.setWorkflowId(566261L);//todo - set your workflow id
buildInfo.setInstanceId(566269L);//todo - set your instance id
 
deployInfo.setWorkflowId(566260L);//todo - set your workflow id
deployInfo.setInstanceIds([566269L]);//todo - set your instance id
 
proj.setBuildInfo(buildInfo);
proj.setDeployInfo(deployInfo);
 
proj.setMainStreamName("master");//todo set this, perhaps from the payload
 
def projectSCMPojo = new ProjectSCMPojo();
def projectSCMConfig = new ProjectSCMConfig();
projectSCMConfig.setInstanceId(scmId);
def scmConfigs = [new ProjectSCMConfigValue("BranchScript", "BranchName"), new ProjectSCMConfigValue("TagScript", "ProjectVersion"), new ProjectSCMConfigValue("SparseCheckoutFoldersScript", ""), new ProjectSCMConfigValue("CheckoutFolderScript", "")];
projectSCMConfig.setConfigValues(scmConfigs);
projectSCMConfig.setSourceNumber(1);
 
projectSCMPojo.setSources([projectSCMConfig]);
 
proj.setScmConfiguration(projectSCMPojo);
 
def projId = FLEXDEPLOY.createProject(proj);
LOG.info(projId.toString());
 
LOG.info("Updating Project Properties.");
//todo either remove this section or set appropriate properties
def properties = [
  new PropertyValuePojo("FDEBS_APPLICATION_SHORT_NAME", "XXHR"),
  new PropertyValuePojo("FDEBS_JAVA_ROOT_SOURCE_DIR", "java"),
  new PropertyValuePojo("FDEBS_JAVA_ROOT_DESTINATION_DIR", "\$XXHR_TOP/java"),
  new PropertyValuePojo("FDEBS_LOAD_JAVA_ROOT_DESTINATION_DIR", "\$XXHR_TOP/java"),
  new PropertyValuePojo("FDEBS_AOL_ROOT_DESTINATION_DIR", "\$XXHR_TOP/patch/115/import/us"),
  new PropertyValuePojo("FDEBS_FILE_PERMISSIONS", "755"),
  new PropertyValuePojo("FD_PARTIAL_FILE_EXCLUDES", "*/misc/*")
  ];
 
FLEXDEPLOY.updateProjectProperties(projId, properties);