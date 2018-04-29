package sun.plugin.util;

import com.sun.deploy.config.Config;
import java.io.File;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;

final class PluginConfig
{
  private static final String JAVAPI = "javapi";
  private static final String CACHE_VERSION = "v1.0";

  PluginConfig()
  {
    new File(Config.getUserHome()).mkdirs();
    new File(Config.getSystemHome()).mkdirs();
    new File(getLogDirectory()).mkdirs();
    new File(getSecurityDirectory()).mkdirs();
    new File(getUserExtensionDirectory()).mkdirs();
    new File(new File(getPropertiesFile()).getParent()).mkdirs();
  }

  public String getJavaHome()
  {
    return Config.getJavaHome();
  }

  public String getUserHome()
  {
    return Config.getUserHome();
  }

  public String getSystemHome()
  {
    return Config.getSystemHome();
  }

  public String getPropertiesFile()
  {
    return getUserHome() + File.separator + Config.getPropertiesFilename();
  }

  public String getUserExtensionDirectory()
  {
    return Config.getUserExtensionDirectory();
  }

  public String getSecurityDirectory()
  {
    return getUserHome() + File.separator + "security";
  }

  public String getLogDirectory()
  {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("javaplugin.outputfiles.path"));
    if ((str == null) || (str.trim().equals("")))
      str = Config.getLogDirectory();
    return str;
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.util.PluginConfig
 * JD-Core Version:    0.6.2
 */