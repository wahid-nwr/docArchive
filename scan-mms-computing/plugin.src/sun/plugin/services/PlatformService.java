package sun.plugin.services;

import java.security.AccessController;
import sun.plugin.util.Trace;
import sun.security.action.GetPropertyAction;

public abstract class PlatformService
{
  private static PlatformService ps = null;

  public void signalEvent(int paramInt)
  {
  }

  public void waitEvent(int paramInt)
  {
  }

  public void waitEvent(int paramInt1, int paramInt2)
  {
    waitEvent(paramInt2);
  }

  public void dispatchNativeEvent()
  {
  }

  public static synchronized PlatformService getService()
  {
    if (ps == null)
    {
      String str1 = (String)AccessController.doPrivileged(new GetPropertyAction("os.name"));
      try
      {
        String str2 = null;
        if (str1.indexOf("Windows") != -1)
          str2 = "sun.plugin.services.WPlatformService";
        else
          str2 = "sun.plugin.services.MPlatformService";
        Class localClass = Class.forName(str2);
        if (localClass != null)
        {
          Object localObject = localClass.newInstance();
          if ((localObject instanceof PlatformService))
            ps = (PlatformService)localObject;
        }
      }
      catch (Exception localException)
      {
        Trace.printException(localException);
      }
    }
    return ps;
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.services.PlatformService
 * JD-Core Version:    0.6.2
 */