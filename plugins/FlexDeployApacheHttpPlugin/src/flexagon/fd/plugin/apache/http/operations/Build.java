package flexagon.fd.plugin.apache.http.operations;

import flexagon.fd.core.PropertyValue;
import flexagon.fd.core.plugin.AbstractPluginProvider;
import flexagon.fd.core.plugin.PluginResult;
import flexagon.fd.plugin.apache.http.ApacheHttpProperties;
import flexagon.fd.plugin.utils.FlexZipUtils;

import flexagon.ff.common.core.exceptions.FlexCheckedException;
import flexagon.ff.common.core.logging.FlexLogger;

import java.io.File;


public class Build
  extends AbstractPluginProvider
{
  private static final String CLZ_NAM = Build.class.getName();
  private static final FlexLogger LOG = FlexLogger.getLogger(CLZ_NAM);

  public Build()
  {
    super();
  }

  /**
   * This operation zips up files in the FD_TEMP_DIR (optionally just a subfolder inside of FD_TEMP_DR)
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
    String source = getWorkflowExecutionContext().getTempDirectory() + getRelativePathToFiles();
    String destination = getWorkflowExecutionContext().getArtifactsDirectory() + File.separator + ApacheHttpProperties.ARTIFACTS_ZIP;

    LOG.logInfo("Zipping {0} into an artifact at (1}.", source, destination);
    int zipped = new FlexZipUtils(source, new File(destination)).zipFolder();
    getWorkflowExecutionContext().getOutputMap().put(ApacheHttpProperties.FDAH_FILE_COUNT, new PropertyValue(zipped, PropertyValue.PropertyTypeEnum.Integer, false));
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
    //Let's validate that if a relative path is given, it exists on the file system.
    String relativePath = getRelativePathToFiles();
    if (relativePath != "")
    {
      File file = new File(getWorkflowExecutionContext().getTempDirectory() + relativePath);
      if (!file.exists())
      {
        throw new FlexCheckedException(ApacheHttpProperties.FDAH_00001_INVALID_PATH, "The given relative path, [" + relativePath + "] was not found in the FD_TEMP_DIR, or was not a relative path.");
      }
    }
    LOG.logInfoExiting(methodName);
  }

  /**
   * @return a non-null string that starts with a File.seperator with the given path,
   * or a blank String if no relative path is provided.
   */
  private String getRelativePathToFiles()
  {
    String methodName = "getRelativePathToFiles";
    LOG.logInfoEntering(methodName);
    String relativePath = getStringInput(ApacheHttpProperties.FDAH_PATH_TO_FILES);
    if (relativePath != null)
    {
      if (!relativePath.startsWith(File.separator))
      {
        relativePath = File.separator + relativePath;
      }
    }
    else
    {
      relativePath = "";
    }
    LOG.logInfoExiting(methodName);
    return relativePath;
  }


}
