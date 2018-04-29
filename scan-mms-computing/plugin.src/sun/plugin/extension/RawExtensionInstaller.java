package sun.plugin.extension;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import sun.plugin.util.Trace;

public class RawExtensionInstaller
  implements ExtensionInstaller
{
  public boolean install(String paramString1, String paramString2, String paramString3)
    throws IOException
  {
    Trace.msgExtPrintln("optpkg.install.raw.launch");
    File localFile1 = new File(paramString2);
    File localFile2 = new File(paramString3 + File.separatorChar + localFile1.getName());
    Trace.msgExtPrintln("optpkg.install.raw.copy", new Object[] { localFile1, localFile2 });
    FileInputStream localFileInputStream = new FileInputStream(localFile1);
    BufferedInputStream localBufferedInputStream = new BufferedInputStream(localFileInputStream);
    FileOutputStream localFileOutputStream = new FileOutputStream(localFile2);
    BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(localFileOutputStream);
    ExtensionUtils.copy(localBufferedInputStream, localBufferedOutputStream);
    localBufferedInputStream.close();
    localFileInputStream.close();
    localBufferedOutputStream.close();
    localFileOutputStream.close();
    return true;
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.extension.RawExtensionInstaller
 * JD-Core Version:    0.6.2
 */