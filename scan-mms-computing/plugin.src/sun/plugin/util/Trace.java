package sun.plugin.util;

import com.sun.deploy.resources.ResourceManager;
import com.sun.deploy.ui.UIFactory;
import com.sun.deploy.util.TraceLevel;
import java.awt.Component;
import java.security.AccessController;
import java.text.MessageFormat;
import java.util.StringTokenizer;
import sun.plugin.resources.ResourceHandler;
import sun.security.action.GetBooleanAction;
import sun.security.action.GetPropertyAction;

public class Trace
{
  public static void reset()
  {
    if (((Boolean)AccessController.doPrivileged(new GetBooleanAction("javaplugin.trace"))).booleanValue())
    {
      String str1 = (String)AccessController.doPrivileged(new GetPropertyAction("javaplugin.trace.option"));
      if (str1 == null)
      {
        com.sun.deploy.util.Trace.setBasicTrace(true);
        com.sun.deploy.util.Trace.setCacheTrace(true);
        com.sun.deploy.util.Trace.setNetTrace(true);
        com.sun.deploy.util.Trace.setSecurityTrace(true);
        com.sun.deploy.util.Trace.setExtTrace(true);
        com.sun.deploy.util.Trace.setLiveConnectTrace(true);
      }
      else
      {
        StringTokenizer localStringTokenizer = new StringTokenizer(str1, "|");
        while (localStringTokenizer.hasMoreTokens())
        {
          String str2 = localStringTokenizer.nextToken();
          if ((str2 == null) || (str2.equalsIgnoreCase("all")))
          {
            com.sun.deploy.util.Trace.setBasicTrace(true);
            com.sun.deploy.util.Trace.setCacheTrace(true);
            com.sun.deploy.util.Trace.setNetTrace(true);
            com.sun.deploy.util.Trace.setSecurityTrace(true);
            com.sun.deploy.util.Trace.setExtTrace(true);
            com.sun.deploy.util.Trace.setLiveConnectTrace(true);
            break;
          }
          if (str2.equalsIgnoreCase("basic"))
          {
            com.sun.deploy.util.Trace.setBasicTrace(true);
          }
          else if (str2.equalsIgnoreCase("net"))
          {
            com.sun.deploy.util.Trace.setCacheTrace(true);
            com.sun.deploy.util.Trace.setNetTrace(true);
          }
          else if (str2.equalsIgnoreCase("security"))
          {
            com.sun.deploy.util.Trace.setSecurityTrace(true);
          }
          else if (str2.equalsIgnoreCase("ext"))
          {
            com.sun.deploy.util.Trace.setExtTrace(true);
          }
          else if (str2.equalsIgnoreCase("liveconnect"))
          {
            com.sun.deploy.util.Trace.setLiveConnectTrace(true);
          }
        }
      }
    }
  }

  public static boolean isEnabled()
  {
    return com.sun.deploy.util.Trace.isEnabled();
  }

  public static boolean isAutomationEnabled()
  {
    return com.sun.deploy.util.Trace.isAutomationEnabled();
  }

  public static void println(String paramString)
  {
    println(paramString, 2);
  }

  public static void println(String paramString, int paramInt)
  {
    if ((paramInt & 0x2) == 2)
    {
      if ((paramInt & 0xFF0) == 0)
        paramInt |= 16;
      switch (paramInt & 0xFF0)
      {
      case 16:
        com.sun.deploy.util.Trace.println(paramString, TraceLevel.BASIC);
        break;
      case 32:
        com.sun.deploy.util.Trace.println(paramString, TraceLevel.NETWORK);
        break;
      case 64:
        com.sun.deploy.util.Trace.println(paramString, TraceLevel.SECURITY);
        break;
      case 128:
        com.sun.deploy.util.Trace.println(paramString, TraceLevel.EXTENSIONS);
        break;
      case 256:
        com.sun.deploy.util.Trace.println(paramString, TraceLevel.LIVECONNECT);
        break;
      }
    }
  }

  public static void msgPrintln(String paramString)
  {
    msgPrintln(paramString, null, 2);
  }

  public static void msgPrintln(String paramString, int paramInt)
  {
    msgPrintln(paramString, null, paramInt);
  }

  public static void msgPrintln(String paramString, Object[] paramArrayOfObject)
  {
    msgPrintln(paramString, paramArrayOfObject, 2);
  }

  public static void msgPrintln(String paramString, Object[] paramArrayOfObject, int paramInt)
  {
    if ((paramInt & 0x2) == 2)
    {
      String str = null;
      if (paramArrayOfObject == null)
      {
        str = ResourceHandler.getMessage(paramString);
      }
      else
      {
        MessageFormat localMessageFormat = new MessageFormat(ResourceHandler.getMessage(paramString));
        str = localMessageFormat.format(paramArrayOfObject);
      }
      if ((paramInt & 0xFF0) == 0)
        paramInt |= 16;
      switch (paramInt & 0xFF0)
      {
      case 16:
        com.sun.deploy.util.Trace.println(str, TraceLevel.BASIC);
        break;
      case 32:
        com.sun.deploy.util.Trace.println(str, TraceLevel.NETWORK);
        break;
      case 64:
        com.sun.deploy.util.Trace.println(str, TraceLevel.SECURITY);
        break;
      case 128:
        com.sun.deploy.util.Trace.println(str, TraceLevel.EXTENSIONS);
        break;
      case 256:
        com.sun.deploy.util.Trace.println(str, TraceLevel.LIVECONNECT);
        break;
      }
    }
  }

  public static void printException(Throwable paramThrowable)
  {
    printException(null, paramThrowable);
  }

  public static void printException(Component paramComponent, Throwable paramThrowable)
  {
    printException(paramComponent, paramThrowable, null, null);
  }

  public static void printException(Throwable paramThrowable, String paramString1, String paramString2)
  {
    printException(null, paramThrowable, paramString1, paramString2);
  }

  public static void printException(Component paramComponent, Throwable paramThrowable, String paramString1, String paramString2)
  {
    printException(paramComponent, paramThrowable, paramString1, paramString2, true);
  }

  public static void printException(Component paramComponent, Throwable paramThrowable, String paramString1, String paramString2, boolean paramBoolean)
  {
    paramThrowable.printStackTrace();
    if ((paramBoolean) && (!isAutomationEnabled()))
    {
      if (paramString1 == null)
        paramString1 = ResourceManager.getMessage("dialogfactory.general_error");
      UIFactory.showExceptionDialog(paramComponent, paramThrowable, paramString1, paramString2);
    }
  }

  public static void netPrintln(String paramString)
  {
    println(paramString, 34);
  }

  public static void netPrintln(String paramString, int paramInt)
  {
    println(paramString, paramInt | 0x20);
  }

  public static void msgNetPrintln(String paramString)
  {
    msgPrintln(paramString, null, 34);
  }

  public static void msgNetPrintln(String paramString, int paramInt)
  {
    msgPrintln(paramString, null, paramInt | 0x20);
  }

  public static void msgNetPrintln(String paramString, Object[] paramArrayOfObject)
  {
    msgPrintln(paramString, paramArrayOfObject, 34);
  }

  public static void netPrintException(Throwable paramThrowable)
  {
    printException(null, paramThrowable, ResourceManager.getMessage("dialogfactory.net_error"), null, false);
  }

  public static void netPrintException(Throwable paramThrowable, String paramString1, String paramString2)
  {
    printException(null, paramThrowable, paramString1, paramString2, false);
  }

  public static void securityPrintln(String paramString)
  {
    println(paramString, 66);
  }

  public static void msgSecurityPrintln(String paramString)
  {
    msgPrintln(paramString, null, 66);
  }

  public static void msgSecurityPrintln(String paramString, Object[] paramArrayOfObject)
  {
    msgPrintln(paramString, paramArrayOfObject, 66);
  }

  public static void securityPrintException(Throwable paramThrowable)
  {
    printException(null, paramThrowable, ResourceManager.getMessage("dialogfactory.security_error"), null, true);
  }

  public static void securityPrintException(Throwable paramThrowable, String paramString1, String paramString2)
  {
    printException(null, paramThrowable, paramString1, paramString2, true);
  }

  public static void extPrintln(String paramString)
  {
    println(paramString, 130);
  }

  public static void extPrintln(String paramString, int paramInt)
  {
    println(paramString, paramInt | 0x80);
  }

  public static void msgExtPrintln(String paramString)
  {
    msgPrintln(paramString, null, 130);
  }

  public static void msgExtPrintln(String paramString, int paramInt)
  {
    msgPrintln(paramString, null, paramInt | 0x80);
  }

  public static void msgExtPrintln(String paramString, Object[] paramArrayOfObject)
  {
    msgPrintln(paramString, paramArrayOfObject, 130);
  }

  public static void extPrintException(Throwable paramThrowable)
  {
    printException(null, paramThrowable, ResourceManager.getMessage("dialogfactory.ext_error"), null, true);
  }

  public static void extPrintException(Throwable paramThrowable, String paramString1, String paramString2)
  {
    printException(null, paramThrowable, paramString1, paramString2, true);
  }

  public static void liveConnectPrintln(String paramString)
  {
    println(paramString, 258);
  }

  public static void liveConnectPrintln(String paramString, int paramInt)
  {
    println(paramString, paramInt | 0x100);
  }

  public static void msgLiveConnectPrintln(String paramString)
  {
    msgPrintln(paramString, null, 258);
  }

  public static void msgLiveConnectPrintln(String paramString, int paramInt)
  {
    msgPrintln(paramString, null, paramInt | 0x100);
  }

  public static void msgLiveConnectPrintln(String paramString, Object[] paramArrayOfObject)
  {
    msgPrintln(paramString, paramArrayOfObject, 258);
  }

  public static void liveConnectPrintException(Throwable paramThrowable)
  {
    printException(null, paramThrowable, null, null, false);
  }

  static
  {
    reset();
    boolean bool = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("javaplugin.automation"))).booleanValue();
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.util.Trace
 * JD-Core Version:    0.6.2
 */