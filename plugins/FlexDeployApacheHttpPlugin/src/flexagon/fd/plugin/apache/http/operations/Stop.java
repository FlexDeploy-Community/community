package flexagon.fd.plugin.apache.http.operations;

import flexagon.fd.core.plugin.AbstractPluginProvider;
import flexagon.fd.core.plugin.PluginResult;
import flexagon.fd.plugin.apache.http.ApacheHttpProperties;

import flexagon.ff.common.core.exceptions.FlexCheckedException;
import flexagon.ff.common.core.externalprocess.ExternalProcess;
import flexagon.ff.common.core.logging.FlexLogger;

import java.util.ArrayList;
import java.util.List;


public class Stop
  extends AbstractPluginProvider
{
  private static final String CLZ_NAM = Stop.class.getName();
  private static final FlexLogger LOG = FlexLogger.getLogger(CLZ_NAM);

  public Stop()
  {
    super();
  }

  /**
   * This operation starts a Linux Apache Http 2.4 Server
   * @return
   * @throws FlexCheckedException
   */
  @Override
  public PluginResult execute()
    throws FlexCheckedException
  {
    String methodName = "execute";
    LOG.logInfoEntering(methodName);
    ExternalProcess e = new ExternalProcess();
    e.setPrintEnvVariables(false);
    e.setPrintCommand(true);
    e.setWaitForProcess(true);
    e.setTimeout(getLongCurrentInstancePropertyValueOrDefault(ApacheHttpProperties.FDAH_START_TIMEOUT, 300000L));
    List<String> commands = new ArrayList<String>();
    commands.add("apachectl");
    commands.add("stop");
    e.setCommands(commands);
    e.execute();
    LOG.logInfoExiting(methodName);
    return PluginResult.createPluginResult(getWorkflowExecutionContext());
  }

  @Override
  public void validate()
    throws FlexCheckedException
  {
    String methodName = "validate";
    LOG.logInfoEntering(methodName);


    LOG.logInfoExiting(methodName);
  }

}
