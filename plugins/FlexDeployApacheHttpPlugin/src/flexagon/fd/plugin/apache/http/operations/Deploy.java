package flexagon.fd.plugin.apache.http.operations;

import flexagon.fd.core.plugin.AbstractPluginProvider;
import flexagon.fd.core.plugin.PluginResult;
import flexagon.fd.plugin.apache.http.ApacheHttpProperties;
import flexagon.fd.plugin.utils.FlexUnzipUtils;

import flexagon.ff.common.core.exceptions.FlexCheckedException;
import flexagon.ff.common.core.logging.FlexLogger;

import java.io.File;

import java.util.ArrayList;
import java.util.Collection;

public class Deploy
  extends AbstractPluginProvider
{
  public Deploy()
  {
    super();
  }

  private static final String CLZ_NAM = Deploy.class.getName();
  private static final FlexLogger LOG = FlexLogger.getLogger(CLZ_NAM);

  /**
   * This operation will zip up files in the FD_TEMP_DIR (optionally just a subfolder inside of FD_TEMP_DR)
   * and put the output zip file in the FD_ARTIFACTS_DIR folder, which will be copied back to the server.
   * From there, the zipfile will be ready for the deploy operation.
   * @return
   * @throws FlexCheckedException
   */
  @Override
  public PluginResult execute()
    throws FlexCheckedException
  {
    String methodName = "execute";
    LOG.logInfoEntering(methodName);
    String source = getWorkflowExecutionContext().getArtifactsDirectory() + File.separator + ApacheHttpProperties.ARTIFACTS_ZIP;
    String destination = getStringCurrentInstancePropertyValue(ApacheHttpProperties.FDAH_PATH_TO_DOCUMENT_ROOT);
    LOG.logInfo("Unzipping {0} to (1}.", source, destination);
    FlexUnzipUtils.unZip(source, destination);
    LOG.logInfo(methodName, "Done!");

    LOG.logInfoExiting(methodName);
    return PluginResult.createPluginResult(getWorkflowExecutionContext());
  }

  @Override
  public void validate()
    throws FlexCheckedException
  {
    String methodName = "validate";
    LOG.logInfoEntering(methodName);
    Collection<String> requiredProperties = new ArrayList<String>();
    Collection<String> requiredInputs = new ArrayList<String>();

    requiredProperties.add(ApacheHttpProperties.FDAH_PATH_TO_DOCUMENT_ROOT);
    validateContext(requiredProperties, requiredInputs);


    //Let's validate that FDAH_PATH_TO_DOCUMENT_ROOT exists on the file system.
    String docRoot = getStringCurrentInstancePropertyValue(ApacheHttpProperties.FDAH_PATH_TO_DOCUMENT_ROOT);
    File file = new File(docRoot);
    if (!file.exists())
    {
      throw new FlexCheckedException(ApacheHttpProperties.FDAH_00002_INVALID_PATH_TO_DOCUMENT_ROOT,
                                     "The given " + ApacheHttpProperties.FDAH_PATH_TO_DOCUMENT_ROOT + ", [" + docRoot + "] was not found on the server.");
    }
    LOG.logInfoExiting(methodName);
  }

}
