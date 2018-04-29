package sun.plugin.extension;

import com.sun.deploy.ui.UIFactory;
import java.io.File;
import java.io.IOException;
import sun.plugin.resources.ResourceHandler;
import sun.plugin.util.Trace;

public class JavaExtensionInstaller
  implements ExtensionInstaller
{
  public boolean install(String paramString1, String paramString2, String paramString3)
    throws IOException
  {
    Trace.msgExtPrintln("optpkg.install.java.launch");
    String str1 = System.getProperty("java.home");
    String str2 = str1 + File.separator + "bin" + File.separator + "java -jar " + paramString2;
    String[] arrayOfString = new String[3];
    arrayOfString[0] = (str1 + File.separator + "bin" + File.separator + "java");
    arrayOfString[1] = "-jar";
    arrayOfString[2] = paramString2;
    Trace.msgExtPrintln("optpkg.install.java.launch.command", new Object[] { str2 });
    Process localProcess = Runtime.getRuntime().exec(arrayOfString);
    UIFactory.showInformationDialog(null, ResourceHandler.getMessage("optpkg.installer.launch.wait"), ResourceHandler.getMessage("optpkg.installer.launch.caption"));
    return true;
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.extension.JavaExtensionInstaller
 * JD-Core Version:    0.6.2
 */