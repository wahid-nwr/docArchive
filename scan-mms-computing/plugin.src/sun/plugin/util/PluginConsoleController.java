package sun.plugin.util;

import com.sun.deploy.cache.Cache;
import com.sun.deploy.net.proxy.DynamicProxyManager;
import com.sun.deploy.resources.ResourceManager;
import com.sun.deploy.security.CertificateHostnameVerifier;
import com.sun.deploy.security.TrustDecider;
import com.sun.deploy.security.X509DeployTrustManager;
import com.sun.deploy.services.ServiceManager;
import com.sun.deploy.util.ConsoleController;
import com.sun.deploy.util.ConsoleHelper;
import com.sun.deploy.util.LoggerTraceListener;
import com.sun.deploy.util.Trace;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.security.AccessController;
import java.security.Policy;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.plugin.ClassLoaderInfo;
import sun.plugin.WJcovUtil;
import sun.plugin.services.BrowserService;
import sun.security.action.GetPropertyAction;

public class PluginConsoleController
  implements ConsoleController
{
  private boolean onWindows = false;
  private boolean isMozilla = false;
  private boolean iconifiedOnClose = false;
  private Logger logger = null;

  public PluginConsoleController()
  {
    try
    {
      String str1 = (String)AccessController.doPrivileged(new GetPropertyAction("os.name"));
      if (str1.indexOf("Windows") != -1)
        this.onWindows = true;
      String str2 = (String)AccessController.doPrivileged(new GetPropertyAction("mozilla.workaround", "false"));
      if ((str2 != null) && (str2.equalsIgnoreCase("true")))
        this.isMozilla = true;
      BrowserService localBrowserService = (BrowserService)ServiceManager.getService();
      this.iconifiedOnClose = localBrowserService.isConsoleIconifiedOnClose();
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }

  public boolean isIconifiedOnClose()
  {
    return this.iconifiedOnClose;
  }

  public boolean isDoubleBuffered()
  {
    return (this.onWindows) || (this.isMozilla != true);
  }

  public boolean isDumpStackSupported()
  {
    return true;
  }

  public String dumpAllStacks()
  {
    return ConsoleHelper.dumpAllStacks();
  }

  public ThreadGroup getMainThreadGroup()
  {
    return PluginSysUtil.getPluginThreadGroup().getParent();
  }

  public boolean isSecurityPolicyReloadSupported()
  {
    return true;
  }

  public void reloadSecurityPolicy()
  {
    Policy localPolicy = Policy.getPolicy();
    localPolicy.refresh();
  }

  public boolean isProxyConfigReloadSupported()
  {
    return true;
  }

  public void reloadProxyConfig()
  {
    DynamicProxyManager.reset();
  }

  public boolean isDumpClassLoaderSupported()
  {
    return true;
  }

  public String dumpClassLoaders()
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    PrintStream localPrintStream = new PrintStream(localByteArrayOutputStream);
    ClassLoaderInfo.dumpClassLoaderCache(localPrintStream);
    return new String(localByteArrayOutputStream.toByteArray());
  }

  public boolean isClearClassLoaderSupported()
  {
    return true;
  }

  public void clearClassLoaders()
  {
    Cache.clearLoadedResources();
    ClassLoaderInfo.clearClassLoaderCache();
    TrustDecider.reset();
    X509DeployTrustManager.reset();
    CertificateHostnameVerifier.reset();
  }

  public boolean isLoggingSupported()
  {
    return true;
  }

  public void setLogger(Logger paramLogger)
  {
    this.logger = paramLogger;
  }

  public Logger getLogger()
  {
    return this.logger;
  }

  public boolean toggleLogging()
  {
    if (this.logger == null)
    {
      localObject = new File(UserProfile.getLogDirectory());
      File localFile = Trace.createTempFile("plugin", ".log", (File)localObject);
      LoggerTraceListener localLoggerTraceListener = new LoggerTraceListener("sun.plugin", localFile.getPath());
      this.logger = localLoggerTraceListener.getLogger();
    }
    Object localObject = this.logger.getLevel();
    if (localObject == Level.OFF)
      localObject = Level.ALL;
    else
      localObject = Level.OFF;
    this.logger.setLevel((Level)localObject);
    return localObject == Level.ALL;
  }

  public boolean isJCovSupported()
  {
    boolean bool = false;
    if (this.onWindows)
    {
      String str = System.getProperty("javaplugin.vm.options");
      bool = str.indexOf("-Xrunjcov") != -1;
    }
    return bool;
  }

  public boolean dumpJCovData()
  {
    return WJcovUtil.dumpJcovData();
  }

  public String getProductName()
  {
    return ResourceManager.getString("product.javapi.name", System.getProperty("java.version"));
  }

  public void invokeLater(Runnable paramRunnable)
  {
    PluginSysUtil.invokeLater(paramRunnable);
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.util.PluginConsoleController
 * JD-Core Version:    0.6.2
 */