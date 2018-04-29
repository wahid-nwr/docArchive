package sun.plugin.util;

import com.sun.deploy.cache.Cache;
import com.sun.deploy.config.Config;
import java.io.File;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;

public class UserProfile
{
  private static PluginConfig config = new PluginConfig();

  public static String getPropertyFile()
  {
    return config.getPropertiesFile();
  }

  public static String getLogDirectory()
  {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("javaplugin.outputfiles.path"));
    if ((str == null) || (str.trim().equals("")))
      str = Config.getLogDirectory();
    return str;
  }

  public static String getTempDirectory()
  {
    return Cache.getCacheDir().getPath() + File.separator + "tmp";
  }

  static
  {
    try
    {
      new File(getTempDirectory()).mkdirs();
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
    }
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.util.UserProfile
 * JD-Core Version:    0.6.2
 */