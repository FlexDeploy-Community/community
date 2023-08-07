import flexagon.fd.model2.pojo.WorkItemDataObject;
import java.util.Map;
import flexagon.ff.common.core.rest.FlexRESTClient;
import flexagon.fd.model.integration.its.impl.jira.JiraIssueTrackingSystem;
import flexagon.fd.model.integration.its.impl.ITSWorkItemImpl;

//This code will check all the Jira tickets that are associated to the builds in the current snapshot to see if their status is "DONE" if so, it will move on to the next gate or step.
//Replace all instances of "DONE" with the successful status you want to use from Jira.
//Replace <INSTANCE CODE HERE> with the instance code from your JIRA instance.
Map<String, Object> props = topology.getInstanceProperties("<INSTANCE CODE HERE>");

List<WorkItemDataObject> wos = FLEXDEPLOY.getWorkItemsForSnapshotVersions(SnapshotId);

JiraIssueTrackingSystem jira = new JiraIssueTrackingSystem();
jira.setProperties((Map<String, Serializable>) props);

//Remove this if check if you want to continue if there are no tickets instead of erroring out.
if(wos.size() == 0){
  throw new RuntimeException("No jira ticket was given for the builds of this snapshot. Must make the build on the projects screen and add a ticket, or associate one using a commit message.");
}


for(WorkItemDataObject wo : wos){
  String won = wo.getWorkItemNumber();
  ITSWorkItemImpl woi = ITSWorkItemImpl.createWorkItemPojo(won, jira);
  String status = jira.getWorkItemStatus(woi);
  LOG.info("Status was " + status);
  //If any ticket isn't in the "DONE" state, then keep checking later.
  //You would replace "DONE" with the status that you are checking for.
  //There are other checks besides status that you could perform as well.
  
  if(!"DONE".equals(status)){
    return "RUNNING";
  }
  
}
//If all tickets were "DONE" then we will get here, and continue on to the next gate.
return "SUCCESSFUL"
