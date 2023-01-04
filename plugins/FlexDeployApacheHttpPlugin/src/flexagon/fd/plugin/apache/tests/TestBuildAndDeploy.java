package flexagon.fd.plugin.apache.tests;

import flexagon.fd.core.PropertyValue;
import flexagon.fd.core.plugin.AbstractPluginProvider;
import flexagon.fd.core.workflow.MockWorkflowExecutionContext;
import flexagon.fd.core.workflow.WorkflowExecutionContext;
import flexagon.fd.plugin.apache.http.ApacheHttpProperties;
import flexagon.fd.plugin.apache.http.operations.Build;
import flexagon.fd.plugin.apache.http.operations.Deploy;

import flexagon.ff.common.core.exceptions.FlexCheckedException;

import java.io.File;
import java.io.IOException;

import java.nio.file.FileVisitResult;
import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.TERMINATE;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.concurrent.ConcurrentHashMap;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TestBuildAndDeploy
{
  static String fileName1 = "TestFile1.txt";
  static String subFolder = "subFolder";
  @Rule
  public ExpectedException thrown = ExpectedException.none();


  @BeforeClass
  public static void prepTests()
    throws IOException
  {
    WorkflowExecutionContext context = new MockWorkflowExecutionContext();
    File file = new File(context.getTempDirectory() + File.separator + subFolder + File.separator + fileName1);
    file.getParentFile().mkdir();
    file.createNewFile();
  }

  @Test
  public void test()
    throws FlexCheckedException
  {
    WorkflowExecutionContext context = new MockWorkflowExecutionContext();
    clearFolders(context);
    AbstractPluginProvider plugin = new Build();
    plugin.setWorkflowExecutionContext(context);
    File file = new File(context.getTempDirectory() + File.separator + fileName1);
    try
    {
      file.createNewFile();
    }
    catch (Exception e)
    {
      Assert.fail(e.getLocalizedMessage());
      e.printStackTrace();
    }
    plugin.validate();
    plugin.execute();
    Assert.assertEquals("The wrong number of files were in the zip file.", 2, plugin.getWorkflowExecutionContext().getOutputMap().get(ApacheHttpProperties.FDAH_FILE_COUNT).getValue());
    String outputPath = context.getWorkingDirectory() + File.separator + "unZipped";
    doDeploy(outputPath);
    //System.exit(6);
    File deployedFile = new File(outputPath + File.separator + fileName1);
    Assert.assertEquals("Deployed file in root folder is missing.", true, deployedFile.exists());
    deployedFile = new File(outputPath + File.separator + subFolder);
    Assert.assertEquals("subFolder is missing.", true, deployedFile.exists());
    deployedFile = new File(outputPath + File.separator + subFolder + File.separator + fileName1);
    Assert.assertEquals("File in subFolder is missing.", true, deployedFile.exists());
  }

  @Test
  public void testWithInput()
    throws FlexCheckedException, IOException
  {

    AbstractPluginProvider plugin = new Build();


    ConcurrentHashMap<String, PropertyValue> inputs = new ConcurrentHashMap<String, PropertyValue>();
    inputs.put(ApacheHttpProperties.FDAH_PATH_TO_FILES, new PropertyValue(subFolder, PropertyValue.PropertyTypeEnum.String, false));

    WorkflowExecutionContext context = new MockWorkflowExecutionContext(inputs);
    plugin.setWorkflowExecutionContext(context);
    clearFolders(context);
    plugin.validate();
    plugin.execute();
    Assert.assertEquals("The wrong number of files were in the zip file.", 1, context.getOutputMap().get(ApacheHttpProperties.FDAH_FILE_COUNT).getValue());
    File artifactFile = new File(context.getArtifactsDirectory() + File.separator + ApacheHttpProperties.ARTIFACTS_ZIP);
    Assert.assertEquals("Artifact file is missing.", true, artifactFile.exists());

    //Now deploy the zip and make sure that the files look right.
    String outputPath = context.getWorkingDirectory() + File.separator + "unZipped";
    doDeploy(outputPath);

    File deployedFile = new File(outputPath + File.separator + fileName1);
    Assert.assertEquals("Deployed file is missing.", true, deployedFile.exists());
    deployedFile = new File(outputPath + File.separator + subFolder);
    Assert.assertEquals("subFolder shouldn't be here.", false, deployedFile.exists());
    deployedFile = new File(outputPath + File.separator + subFolder + File.separator + fileName1);
    Assert.assertEquals("File in subFolder shouldn't be here.", false, deployedFile.exists());
  }

  public void doDeploy(String outputPath)
    throws FlexCheckedException
  {
    Deploy plugin = new Deploy();
    MockWorkflowExecutionContext context = new MockWorkflowExecutionContext();

    context.addProperty(ApacheHttpProperties.FDAH_PATH_TO_DOCUMENT_ROOT, outputPath, PropertyValue.PropertyTypeEnum.String, false);
    plugin.setWorkflowExecutionContext(context);
    plugin.validate();
    plugin.execute();
  }

  @Test
  public void testBadPathOnDeployServer()
    throws FlexCheckedException
  {
    thrown.expect(FlexCheckedException.class);
    AbstractPluginProvider plugin = new Deploy();
    WorkflowExecutionContext context = new MockWorkflowExecutionContext();

    clearFolders(context);
    String pathToZip = context.getWorkingDirectory() + File.separator + "badPath";
    context.addProperty(ApacheHttpProperties.FDAH_PATH_TO_DOCUMENT_ROOT, pathToZip, PropertyValue.PropertyTypeEnum.String, false);
    plugin.setWorkflowExecutionContext(context);
    plugin.validate();
  }

  public void clearFolders(WorkflowExecutionContext context)
  {
    File[] files = new File[] { new File(context.getWorkingDirectory() + File.separator + "unZipped"), new File(context.getArtifactsDirectory()) };
    for (File file: files)
    {
      if (file.exists())
      {
        try
        {
          deleteFileOrFolder((file.toPath()));
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
      }
      file.mkdirs();
    }
  }

  private static void deleteFileOrFolder(final Path path)
    throws IOException
  {
    Files.walkFileTree(path, new SimpleFileVisitor<Path>()
    {
      @Override
      public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs)
        throws IOException
      {
        Files.delete(file);
        return CONTINUE;
      }

      @Override
      public FileVisitResult visitFileFailed(final Path file, final IOException e)
      {
        return handleException(e);
      }

      private FileVisitResult handleException(final IOException e)
      {
        e.printStackTrace(); // replace with more robust error handling
        return TERMINATE;
      }

      @Override
      public FileVisitResult postVisitDirectory(final Path dir, final IOException e)
        throws IOException
      {
        if (e != null)
          return handleException(e);
        Files.delete(dir);
        return CONTINUE;
      }
    });
  };

  @Test
  public void testMissingProperties()
    throws FlexCheckedException
  {
    thrown.expect(FlexCheckedException.class);
    AbstractPluginProvider plugin = new Deploy();
    WorkflowExecutionContext context = new MockWorkflowExecutionContext();
    plugin.setWorkflowExecutionContext(context);
    plugin.validate();
  }

  @Test
  public void testBadBuildPath()
    throws FlexCheckedException
  {
    thrown.expect(FlexCheckedException.class);
    AbstractPluginProvider plugin = new Build();

    ConcurrentHashMap<String, PropertyValue> inputs = new ConcurrentHashMap<String, PropertyValue>();
    inputs.put(ApacheHttpProperties.FDAH_PATH_TO_FILES, new PropertyValue("Bad Path HERE.", PropertyValue.PropertyTypeEnum.String, false));

    WorkflowExecutionContext context = new MockWorkflowExecutionContext(inputs);

    plugin.setWorkflowExecutionContext(context);
    plugin.validate();
  }

}
