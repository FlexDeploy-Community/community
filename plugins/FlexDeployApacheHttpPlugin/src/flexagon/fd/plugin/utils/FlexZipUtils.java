package flexagon.fd.plugin.utils;

import flexagon.ff.common.core.exceptions.FlexRuntimeException;
import flexagon.ff.common.core.logging.FlexMessageOnlyLogger;
import flexagon.ff.common.core.utils.FlexCommonUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

//usages:
/*
 *
 * To zip a folder with its contents
 * new FlexZipUtils("C:\\test\\maven\\simple-project\\target\\classes", new File("/temp/test.zip")).zipFolder();
 *
 */

public class FlexZipUtils
{

  private List<String> fileList = new ArrayList<String>();
  private File mOutputZipFile; // output Zip File
  private String mSource; // SourceFolder path

  public FlexZipUtils(String pSource, File pOutputZipFile)
  {
    this.mSource = pSource;
    this.mOutputZipFile = pOutputZipFile;
  }

  public int zipFolder()
  {
    generateFileList(new File(mSource));
    byte[] buffer = new byte[1024];
    FileOutputStream fos = null;
    ZipOutputStream zos = null;
    try
    {
      fos = new FileOutputStream(mOutputZipFile);
      zos = new ZipOutputStream(fos);

      FileInputStream in = null;

      for (String file: this.fileList)
      {
        FlexMessageOnlyLogger.singleton().log("File Added : " + file);
        ZipEntry ze;

        ze = new ZipEntry(file); //just the file, no paths at all ever.
        zos.putNextEntry(ze);
        try
        {
          in = new FileInputStream(new File(mSource + File.separator + file).getAbsolutePath());
          int len;
          while ((len = in.read(buffer)) > 0)
          {
            zos.write(buffer, 0, len);
          }
        }
        finally
        {
          FlexCommonUtils.close(in);
        }

      }

      zos.closeEntry();

    }
    catch (IOException e)
    {
      throw new FlexRuntimeException("Error Zipping folder " + mSource + " to zip file " + mOutputZipFile.getAbsolutePath(), e);
    }
    finally
    {
      FlexCommonUtils.close(zos);
    }
    return fileList.size();
  }

  //This walks the file system to find the files and directories to add to the zip.
  private void generateFileList(File node)
  {

    // add files only
    if (node.isFile())
    {
      fileList.add(generateZipEntry(node.getAbsolutePath()));
    }

    if (node.isDirectory())
    {
      String[] subNote = node.list();
      for (String filename: subNote)
      {
        generateFileList(new File(node, filename));
      }
    }
  }

  private String generateZipEntry(String file)
  {
    return file.substring(mSource.length() + 1, file.length());
  }
}

