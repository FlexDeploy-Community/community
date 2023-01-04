package flexagon.fd.plugin.utils;

import flexagon.ff.common.core.exceptions.FlexRuntimeException;
import flexagon.ff.common.core.logging.FlexMessageOnlyLogger;
import flexagon.ff.common.core.utils.FlexCommonUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FlexUnzipUtils
{
  private FlexUnzipUtils()
  {

  }

  /**
   * @param zipFile input is the zip file to unzip.
   * @param outputFolder is the folder to unzip the file to.
   */
  public static void unZip(String zipFile, String outputFolder)
  {
    byte[] buffer = new byte[1024];
    ZipInputStream zis = null;
    FileOutputStream fos = null;
    File folder = new File(outputFolder);
    try
    {
      zis = new ZipInputStream(new FileInputStream(zipFile));
      //get the zip file content

      //get the zipped file list entry
      ZipEntry ze = zis.getNextEntry();

      while (ze != null)
      {

        String fileName = ze.getName();
        File newFile = new File(outputFolder + File.separator + fileName);

        FlexMessageOnlyLogger.singleton().log("file unzip : " + newFile.getAbsoluteFile());

        //create all non-existing folders
        //else you will hit FileNotFoundException for compressed folder
        new File(newFile.getParent()).mkdirs();

        fos = new FileOutputStream(newFile);

        int len;
        while ((len = zis.read(buffer)) > 0)
        {
          fos.write(buffer, 0, len);
        }

        FlexCommonUtils.close(fos);
        ze = zis.getNextEntry();
      }

      zis.closeEntry();


    }
    catch (IOException e)
    {
      throw new FlexRuntimeException("Error Unzipping " + zipFile + " to folder " + folder.getAbsolutePath(), e);
    }
    finally
    {
      FlexCommonUtils.close(fos);
      FlexCommonUtils.close(zis);
    }
  }
}
