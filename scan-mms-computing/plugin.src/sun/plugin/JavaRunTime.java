package sun.plugin;

import com.sun.deploy.config.Config;
import com.sun.deploy.perf.DeployPerfUtil;
import com.sun.deploy.perf.NativePerfHelper;
import com.sun.deploy.util.ConsoleController;
import com.sun.deploy.util.ConsoleHelper;
import com.sun.deploy.util.ConsoleTraceListener;
import com.sun.deploy.util.ConsoleWindow;
import com.sun.deploy.util.DeploySysRun;
import com.sun.deploy.util.FileTraceListener;
import com.sun.deploy.util.LoggerTraceListener;
import com.sun.deploy.util.Trace;
import com.sun.deploy.util.TraceLevel;
import com.sun.deploy.util.TraceListener;
import java.io.File;
import java.io.PrintStream;
import java.security.AccessController;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.plugin.util.PluginConsoleController;
import sun.plugin.util.PluginSysUtil;
import sun.plugin.util.UserProfile;
import sun.security.action.GetBooleanAction;
import sun.security.action.GetPropertyAction;

public class JavaRunTime
{
  private static boolean traceInit = false;
  private static ConsoleWindow console = null;
  private static ConsoleTraceListener ctl = null;
  private static ConsoleController controller = null;

  protected static void initEnvironment(String paramString1, String paramString2, String paramString3)
  {
    if (DeployPerfUtil.isEnabled() == true)
      DeployPerfUtil.initialize(new NativePerfHelper());
    DeployPerfUtil.put("START - Java   - JVM - JavaRunTime.initEnvironment");
    DeployPerfUtil.put("START - Java   - JVM - JavaRunTime.initEnvironment - instantiate PluginSysUtil");
    DeploySysRun.setOverride(new PluginSysUtil());
    DeployPerfUtil.put("END   - Java   - JVM - JavaRunTime.initEnvironment - instantiate PluginSysUtil");
    DeployPerfUtil.put("START - Java   - JVM - JavaRunTime.initEnvironment - set user.home property");
    Properties localProperties = System.getProperties();
    localProperties.put("java.home", paramString1);
    if (paramString3 == null)
      localProperties.put("user.home", paramString1);
    else
      localProperties.put("user.home", paramString3);
    DeployPerfUtil.put("END   - Java   - JVM - JavaRunTime.initEnvironment - set user.home property");
    AppletViewer.loadPropertiesFiles();
    DeployPerfUtil.put("START - Java   - JVM - JavaRunTime.initEnvironment - setup trace redirect");
    Trace.redirectStdioStderr();
    DeployPerfUtil.put("END   - Java   - JVM - JavaRunTime.initEnvironment - setup trace redirect");
    DeployPerfUtil.put("END   - Java   - JVM - JavaRunTime.initEnvironment");
  }

  public static synchronized ConsoleWindow getJavaConsole()
  {
    initTraceEnvironment();
    if (console == null)
    {
      console = ConsoleWindow.create(controller);
      ctl.setConsole(console);
    }
    return console;
  }

  public static synchronized void initTraceEnvironment()
  {
    if (traceInit)
      return;
    traceInit = true;
    File localFile1 = new File(UserProfile.getLogDirectory());
    localFile1.mkdirs();
    controller = new PluginConsoleController();
    ctl = new ConsoleTraceListener(controller);
    Trace.addTraceListener(ctl);
    Object localObject1;
    Object localObject2;
    if (Config.getBooleanProperty("deployment.trace"))
      try
      {
        String str1 = Config.getProperty("deployment.trace.level");
        if ((str1 == null) || (str1.equals("")))
        {
          Trace.setBasicTrace(true);
          Trace.setNetTrace(true);
          Trace.setCacheTrace(true);
          Trace.setTempTrace(true);
          Trace.setSecurityTrace(true);
          Trace.setExtTrace(true);
          Trace.setLiveConnectTrace(true);
        }
        else
        {
          Trace.setInitialTraceLevel();
        }
        localObject1 = null;
        localObject2 = Config.getProperty("deployment.javapi.trace.filename");
        Object localObject3;
        if ((localObject2 != null) && (localObject2 != ""))
        {
          localObject1 = new File((String)localObject2);
          localObject3 = ((File)localObject1).getParentFile();
          if (localObject3 != null)
            ((File)localObject3).mkdirs();
          ((File)localObject1).createNewFile();
          if (!((File)localObject1).exists())
            localObject1 = null;
        }
        if (localObject1 == null)
        {
          localObject3 = (Boolean)AccessController.doPrivileged(new GetBooleanAction("javaplugin.outputfiles.overwrite"));
          if (((Boolean)localObject3).equals(Boolean.TRUE))
          {
            StringBuffer localStringBuffer = new StringBuffer();
            localStringBuffer.append(localFile1);
            localStringBuffer.append(File.separator);
            localStringBuffer.append("plugin");
            String str2 = (String)AccessController.doPrivileged(new GetPropertyAction("javaplugin.nodotversion"));
            localStringBuffer.append(str2);
            localStringBuffer.append(".trace");
            localObject1 = new File(localStringBuffer.toString());
          }
          else
          {
            localObject1 = Trace.createTempFile("plugin", ".trace", localFile1);
          }
        }
        if ((((File)localObject1).canWrite()) || ((!((File)localObject1).exists()) && (localFile1.canWrite())))
          Trace.addTraceListener(new FileTraceListener((File)localObject1, false));
        else
          Trace.println("can not write to trace file: " + localObject1, TraceLevel.BASIC);
      }
      catch (Exception localException1)
      {
        Trace.println("can not write to trace file", TraceLevel.BASIC);
        Trace.ignored(localException1);
      }
    if (Config.getBooleanProperty("deployment.log"))
      try
      {
        File localFile2 = null;
        localObject1 = Config.getProperty("deployment.javapi.log.filename");
        if ((localObject1 != null) && (localObject1 != ""))
        {
          localFile2 = new File((String)localObject1);
          localObject2 = localFile2.getParentFile();
          if (localObject2 != null)
            ((File)localObject2).mkdirs();
          localFile2.createNewFile();
          if (!localFile2.exists())
            localFile2 = null;
        }
        if (localFile2 == null)
          localFile2 = Trace.createTempFile("plugin", ".log", localFile1);
        localFile1.mkdirs();
        if ((localFile2.canWrite()) || ((!localFile2.exists()) && (localFile1.canWrite())))
        {
          localObject2 = new LoggerTraceListener("sun.plugin", localFile2.getPath());
          ((LoggerTraceListener)localObject2).getLogger().setLevel(Level.ALL);
          ((PluginConsoleController)controller).setLogger(((LoggerTraceListener)localObject2).getLogger());
          Trace.addTraceListener((TraceListener)localObject2);
        }
        else
        {
          Trace.println("can not write to log file: " + localFile2, TraceLevel.BASIC);
        }
      }
      catch (Exception localException2)
      {
        Trace.println("can not write to log file", TraceLevel.BASIC);
        Trace.ignored(localException2);
      }
    System.out.print(ConsoleHelper.displayVersion());
  }

  public static boolean isJavaConsoleVisible()
  {
    if (console == null)
      return false;
    return console.isConsoleVisible();
  }

  public static void showJavaConsole(boolean paramBoolean)
  {
    try
    {
      PluginSysUtil.invokeAndWait(new Runnable()
      {
        public void run()
        {
          ConsoleWindow localConsoleWindow = JavaRunTime.getJavaConsole();
          if (localConsoleWindow != null)
            localConsoleWindow.showConsole(this.val$visible);
        }
      });
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
    }
  }

  public static void showJavaConsoleLater(boolean paramBoolean)
  {
    try
    {
      PluginSysUtil.invokeLater(new Runnable()
      {
        public void run()
        {
          ConsoleWindow localConsoleWindow = JavaRunTime.getJavaConsole();
          if (localConsoleWindow != null)
            localConsoleWindow.showConsole(this.val$visible);
        }
      });
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
    }
  }

  public static void printToJavaConsole(String paramString)
  {
    ctl.print(paramString + "\n");
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.JavaRunTime
 * JD-Core Version:    0.6.2
 */