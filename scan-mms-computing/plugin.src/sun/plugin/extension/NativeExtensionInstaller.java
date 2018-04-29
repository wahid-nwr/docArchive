package sun.plugin.extension;

import com.sun.deploy.ui.UIFactory;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import sun.plugin.resources.ResourceHandler;
import sun.plugin.util.Trace;

public class NativeExtensionInstaller
  implements ExtensionInstaller
{
  public boolean install(String paramString1, String paramString2, String paramString3)
    throws IOException, InterruptedException
  {
    Trace.msgExtPrintln("optpkg.install.native.launch");
    String str1 = null;
    try
    {
      JarFile localJarFile = new JarFile(paramString2);
      Manifest localManifest = localJarFile.getManifest();
      Attributes localAttributes = localManifest.getMainAttributes();
      String str2 = localAttributes.getValue(Attributes.Name.EXTENSION_INSTALLATION);
      if (str2 != null)
        str2 = str2.trim();
      InputStream localInputStream = localJarFile.getInputStream(localJarFile.getEntry(str2));
      BufferedInputStream localBufferedInputStream = new BufferedInputStream(localInputStream);
      str1 = ExtensionUtils.getTempDir() + File.separator + str2;
      FileOutputStream localFileOutputStream = new FileOutputStream(str1);
      BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(localFileOutputStream);
      ExtensionUtils.copy(localBufferedInputStream, localBufferedOutputStream);
      localBufferedInputStream.close();
      localInputStream.close();
      localBufferedOutputStream.close();
      localFileOutputStream.close();
      String str3 = System.getProperty("os.name");
      String str4 = System.getProperty("java.home");
      if (str3.indexOf("Windows") == -1)
      {
        localProcess = Runtime.getRuntime().exec("chmod 755 " + str1);
        localProcess.waitFor();
        Object localObject1;
        File localFile2;
        File localFile5;
        try
        {
          localFile1 = new File(str4);
          localObject1 = Runtime.getRuntime().exec(str1, null, localFile1);
          int i = ((Process)localObject1).waitFor();
          if (i != 0)
          {
            Trace.msgExtPrintln("optpkg.install.native.launch.fail.0", new Object[] { str1 });
            boolean bool3 = false;
            File localFile4;
            return bool3;
          }
        }
        catch (SecurityException localSecurityException)
        {
          File localFile1;
          Trace.msgExtPrintln("optpkg.install.native.launch.fail.1", new Object[] { str4 });
          Trace.securityPrintException(localSecurityException);
        }
        finally
        {
        }
        boolean bool2 = false;
        if (str1 != null)
        {
          localObject1 = new File(str1);
          if (((File)localObject1).exists())
            ((File)localObject1).delete();
        }
        return bool2;
      }
      Process localProcess = Runtime.getRuntime().exec(str1);
      UIFactory.showInformationDialog(null, ResourceHandler.getMessage("optpkg.installer.launch.wait"), ResourceHandler.getMessage("optpkg.installer.launch.caption"));
      boolean bool1 = true;
      File localFile3;
      return bool1;
    }
    finally
    {
      if (str1 != null)
      {
        File localFile6 = new File(str1);
        if (localFile6.exists())
          localFile6.delete();
      }
    }
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.extension.NativeExtensionInstaller
 * JD-Core Version:    0.6.2
 */