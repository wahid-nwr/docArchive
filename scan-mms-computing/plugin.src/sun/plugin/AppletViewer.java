package sun.plugin;

import com.sun.deploy.cache.Cache;
import com.sun.deploy.cache.DeployCacheHandler;
import com.sun.deploy.config.Config;
import com.sun.deploy.net.cookie.DeployCookieSelector;
import com.sun.deploy.net.offline.DeployOfflineManager;
import com.sun.deploy.net.proxy.DeployProxySelector;
import com.sun.deploy.perf.DeployPerfUtil;
import com.sun.deploy.resources.ResourceManager;
import com.sun.deploy.security.DeployAuthenticator;
import com.sun.deploy.services.ServiceManager;
import com.sun.deploy.ui.UIFactory;
import com.sun.deploy.util.ConsoleHelper;
import com.sun.deploy.util.DeploySysAction;
import com.sun.deploy.util.DeploySysRun;
import com.sun.deploy.util.URLUtil;
import com.sun.deploy.util.UpdateCheck;
import com.sun.java.browser.net.ProxyService;
import java.applet.Applet;
import java.applet.AppletContext;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import javax.swing.ImageIcon;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;
import sun.applet.AppletClassLoader;
import sun.applet.AppletEvent;
import sun.applet.AppletListener;
import sun.applet.AppletPanel;
import sun.awt.AppContext;
import sun.net.www.MimeTable;
import sun.net.www.protocol.jar.URLJarFile;
import sun.plugin.cache.CacheUpdateHelper;
import sun.plugin.cache.JarCacheUtil;
import sun.plugin.extension.ExtensionInstallationImpl;
import sun.plugin.javascript.JSContext;
import sun.plugin.net.proxy.PluginProxyServiceProvider;
import sun.plugin.perf.PluginRollup;
import sun.plugin.resources.ResourceHandler;
import sun.plugin.security.ActivatorSecurityManager;
import sun.plugin.security.JDK11ClassFileTransformer;
import sun.plugin.security.PluginClassLoader;
import sun.plugin.services.BrowserService;
import sun.plugin.util.GrayBoxPainter;
import sun.plugin.util.PluginSysUtil;
import sun.plugin.util.UserProfile;
import sun.plugin.viewer.context.PluginAppletContext;

public class AppletViewer extends AppletPanel
  implements WindowListener
{
  private GrayBoxPainter grayBoxPainter = new GrayBoxPainter(this);
  private GrayBoxListener grayBoxListener = null;
  private String customBoxMessage = null;
  private boolean dumpPerf = false;
  private boolean loading_first_time = true;
  private boolean preloading = false;
  private volatile boolean stopped = false;
  private static Frame dummyFrame = new Frame();
  private static boolean initialized = false;
  public static String theVersion = "1.1";
  private URL documentURL = null;
  protected URL baseURL = null;
  protected HashMap atts = new HashMap();
  private ClassLoaderInfo cli = null;
  private static boolean fShowException = false;
  public volatile boolean readyToQuit = true;
  public Object appletQuitLock = new Object();
  private AppletEventListener appletEventListener = new AppletEventListener(null);
  private Object syncInit = new Object();
  private boolean bInit = false;
  private static final String VERSION_TAG = "version=";
  private HashMap jarVersionMap = new HashMap();
  private HashMap preloadJarMap = new HashMap();
  private ArrayList newStyleJarList = new ArrayList();
  private static final String PRELOAD = "preload";
  private boolean docbaseInit = false;
  private Object docBaseSyncObj = new Object();
  protected boolean codeBaseInit = false;
  private String classLoaderCacheKey = null;
  private AppletStatusListener statusListener = null;
  private PluginAppletContext appletContext;
  private InputStream is;

  public static void loadPropertiesFiles()
  {
    DeployPerfUtil.put("START - Java   - JVM - AppletViewer.loadPropertiesFiles");
    try
    {
      File localFile = new File(UserProfile.getPropertyFile());
      localFile.getParentFile().mkdirs();
    }
    catch (Throwable localThrowable)
    {
      sun.plugin.util.Trace.printException(localThrowable);
    }
    DeployPerfUtil.put("END   - Java   - JVM - AppletViewer.loadPropertiesFiles");
  }

  public static void setStartTime(long paramLong)
  {
    String str = System.getProperty("sun.perflog");
    if (str != null)
      try
      {
        Class localClass = Class.forName("sun.misc.PerformanceLogger");
        if (localClass != null)
        {
          Class[] arrayOfClass = new Class[2];
          arrayOfClass[0] = String.class;
          arrayOfClass[1] = Long.TYPE;
          Method localMethod = localClass.getMethod("setStartTime", arrayOfClass);
          if (localMethod != null)
          {
            Object[] arrayOfObject = new Object[2];
            arrayOfObject[0] = "Java Plug-in load time";
            arrayOfObject[1] = new Long(paramLong);
            localMethod.invoke(null, arrayOfObject);
          }
        }
      }
      catch (Exception localException)
      {
      }
  }

  public static void initEnvironment(int paramInt, long paramLong)
  {
    if (initialized)
      return;
    setStartTime(paramLong);
    initEnvironment(paramInt);
  }

  public static void initEnvironment(int paramInt)
  {
    if (initialized)
      return;
    initialized = true;
    DeployPerfUtil.put("START - Java   - ENV - AppletViewer.initEnvironment");
    DeployPerfUtil.put("START - Java   - ENV - AppletViewer.initEnvironment - PluginSysUtil.getPluginThreadGroup");
    PluginSysUtil.getPluginThreadGroup();
    DeployPerfUtil.put("END   - Java   - ENV - AppletViewer.initEnvironment - PluginSysUtil.getPluginThreadGroup");
    try
    {
      DeployPerfUtil.put("START - Java   - ENV - AppletViewer.initEnvironment - set initial proxy");
      ProxyService.setProvider(new PluginProxyServiceProvider());
      DeployPerfUtil.put("END   - Java   - ENV - AppletViewer.initEnvironment - set initial proxy");
    }
    catch (Exception localException1)
    {
    }
    DeployPerfUtil.put("START - Java   - ENV - AppletViewer.initEnvironment - ServiceManager.setService");
    ServiceManager.setService(paramInt);
    DeployPerfUtil.put("END   - Java   - ENV - AppletViewer.initEnvironment - ServiceManager.setService");
    try
    {
      ImageIcon localImageIcon = ImageIcon.class;
    }
    catch (Throwable localThrowable1)
    {
    }
    try
    {
      DeployPerfUtil.put("START - Java   - ENV - AppletViewer.initEnvironment - JDK11ClassFileTransformer.init");
      JDK11ClassFileTransformer.init();
      DeployPerfUtil.put("END   - Java   - ENV - AppletViewer.initEnvironment - JDK11ClassFileTransformer.init");
    }
    catch (Throwable localThrowable2)
    {
      localThrowable2.printStackTrace();
    }
    DeployPerfUtil.put("START - Java   - ENV - AppletViewer.initEnvironment - load deploy.properties");
    Properties localProperties1 = Config.getProperties();
    Properties localProperties2 = new Properties(System.getProperties());
    localProperties2.put("acl.read", "+");
    localProperties2.put("acl.read.default", "");
    localProperties2.put("acl.write", "+");
    localProperties2.put("acl.write.default", "");
    localProperties2.put("browser.version", theVersion);
    localProperties2.put("browser.vendor", "Sun Microsystems, Inc.");
    localProperties2.put("http.agent", "Mozilla/4.0 (" + System.getProperty("os.name") + " " + System.getProperty("os.version") + ")");
    localProperties2.put("sun.net.http.errorstream.enableBuffering", "true");
    localProperties2.put("package.restrict.access.sun", "true");
    localProperties2.put("package.restrict.access.com.sun.deploy", "true");
    localProperties2.put("package.restrict.access.org.mozilla.jss", "true");
    localProperties2.put("package.restrict.access.netscape", "false");
    localProperties2.put("package.restrict.definition.java", "true");
    localProperties2.put("package.restrict.definition.sun", "true");
    localProperties2.put("package.restrict.definition.netscape", "true");
    localProperties2.put("package.restrict.definition.com.sun.deploy", "true");
    localProperties2.put("package.restrict.definition.org.mozilla.jss", "true");
    localProperties2.put("java.version.applet", "true");
    localProperties2.put("java.vendor.applet", "true");
    localProperties2.put("java.vendor.url.applet", "true");
    localProperties2.put("java.class.version.applet", "true");
    localProperties2.put("os.name.applet", "true");
    localProperties2.put("os.version.applet", "true");
    localProperties2.put("os.arch.applet", "true");
    localProperties2.put("file.separator.applet", "true");
    localProperties2.put("path.separator.applet", "true");
    localProperties2.put("line.separator.applet", "true");
    String str = localProperties2.getProperty("java.protocol.handler.pkgs");
    if (str != null)
      localProperties2.put("java.protocol.handler.pkgs", str + "|sun.plugin.net.protocol|com.sun.deploy.net.protocol");
    else
      localProperties2.put("java.protocol.handler.pkgs", "sun.plugin.net.protocol|com.sun.deploy.net.protocol");
    DeployPerfUtil.put("START - Java   - ENV - AppletViewer.initEnvironment - URLConnection.setDefaultAllowUserInteraction");
    URLConnection.setDefaultAllowUserInteraction(true);
    DeployPerfUtil.put("END   - Java   - ENV - AppletViewer.initEnvironment - URLConnection.setDefaultAllowUserInteraction");
    if (localProperties2.get("https.protocols") == null)
    {
      localObject1 = new StringBuffer();
      if (Config.getBooleanProperty("deployment.security.TLSv1"))
        ((StringBuffer)localObject1).append("TLSv1");
      if (Config.getBooleanProperty("deployment.security.SSLv3"))
      {
        if (((StringBuffer)localObject1).length() != 0)
          ((StringBuffer)localObject1).append(",");
        ((StringBuffer)localObject1).append("SSLv3");
      }
      if (Config.getBooleanProperty("deployment.security.SSLv2Hello"))
      {
        if (((StringBuffer)localObject1).length() != 0)
          ((StringBuffer)localObject1).append(",");
        ((StringBuffer)localObject1).append("SSLv2Hello");
      }
      localProperties2.put("https.protocols", ((StringBuffer)localObject1).toString());
    }
    localProperties2.put("http.auth.serializeRequests", "true");
    DeployPerfUtil.put("START - Java   - ENV - AppletViewer.initEnvironment - JavaRunTime.initTraceEnvironment");
    JavaRunTime.initTraceEnvironment();
    DeployPerfUtil.put("END   - Java   - ENV - AppletViewer.initEnvironment - JavaRunTime.initTraceEnvironment");
    Object localObject1 = Config.getProperty("deployment.console.startup.mode");
    DeployPerfUtil.put("START - Java   - ENV - AppletViewer.initEnvironment - display JavaConsole");
    if ("SHOW".equalsIgnoreCase((String)localObject1))
    {
      JavaRunTime.showJavaConsole(true);
    }
    else if (!"DISABLE".equalsIgnoreCase((String)localObject1))
    {
      localObject2 = (BrowserService)ServiceManager.getService();
      if (((BrowserService)localObject2).isConsoleIconifiedOnClose())
        JavaRunTime.showJavaConsole(false);
    }
    DeployPerfUtil.put("END   - Java   - ENV - AppletViewer.initEnvironment - display JavaConsole");
    if ("true".equalsIgnoreCase(localProperties2.getProperty("deployment.javapi.lifecycle.exception", "false")))
      fShowException = true;
    Object localObject2 = localProperties2.getProperty("sun.net.client.defaultConnectTimeout", "120000");
    localProperties2.put("sun.net.client.defaultConnectTimeout", localObject2);
    DeployPerfUtil.put("START - Java   - ENV - AppletViewer.initEnvironment - install extension package");
    Object localObject3;
    try
    {
      Class localClass = Class.forName("sun.misc.ExtensionDependency");
      if (localClass != null)
      {
        localObject3 = new Class[1];
        localObject3[0] = Class.forName("sun.misc.ExtensionInstallationProvider");
        Method localMethod = localClass.getMethod("addExtensionInstallationProvider", (Class[])localObject3);
        if (localMethod != null)
        {
          Object[] arrayOfObject = new Object[1];
          arrayOfObject[0] = new ExtensionInstallationImpl();
          localMethod.invoke(null, arrayOfObject);
        }
        else
        {
          sun.plugin.util.Trace.msgPrintln("optpkg.install.error.nomethod");
        }
      }
      else
      {
        sun.plugin.util.Trace.msgPrintln("optpkg.install.error.noclass");
      }
    }
    catch (Throwable localThrowable3)
    {
      sun.plugin.util.Trace.printException(localThrowable3);
    }
    DeployPerfUtil.put("END   - Java   - ENV - AppletViewer.initEnvironment - install extension package");
    localProperties2.remove("proxyHost");
    localProperties2.remove("proxyPort");
    localProperties2.remove("http.proxyHost");
    localProperties2.remove("http.proxyPort");
    localProperties2.remove("https.proxyHost");
    localProperties2.remove("https.proxyPort");
    localProperties2.remove("ftpProxyHost");
    localProperties2.remove("ftpProxyPort");
    localProperties2.remove("ftpProxySet");
    localProperties2.remove("gopherProxyHost");
    localProperties2.remove("gopherProxyPort");
    localProperties2.remove("gopherProxySet");
    localProperties2.remove("socksProxyHost");
    localProperties2.remove("socksProxyPort");
    if ("true".equalsIgnoreCase(localProperties2.getProperty("javaplugin.proxy.authentication", "true")))
    {
      DeployPerfUtil.put("START - Java   - ENV - AppletViewer.initEnvironment - enable proxy/web server authentication");
      Authenticator.setDefault(new DeployAuthenticator());
      DeployPerfUtil.put("END   - Java   - ENV - AppletViewer.initEnvironment - enable proxy/web server authentication");
    }
    System.setProperties(localProperties2);
    DeployPerfUtil.put("END   - Java   - ENV - AppletViewer.initEnvironment - load deploy.properties");
    System.out.println("");
    DeployPerfUtil.put("START - Java   - ENV - AppletViewer.initEnvironment - DeployProxySelector.reset");
    DeployProxySelector.reset();
    DeployPerfUtil.put("END   - Java   - ENV - AppletViewer.initEnvironment - DeployProxySelector.reset");
    DeployPerfUtil.put("START - Java   - ENV - AppletViewer.initEnvironment - DeployCookieSelector.reset");
    DeployCookieSelector.reset();
    DeployPerfUtil.put("END   - Java   - ENV - AppletViewer.initEnvironment - DeployCookieSelector.reset");
    DeployPerfUtil.put("START - Java   - ENV - AppletViewer.initEnvironment - DeployOfflineManager.reset");
    DeployOfflineManager.reset();
    DeployPerfUtil.put("END   - Java   - ENV - AppletViewer.initEnvironment - DeployOfflineManager.reset");
    System.out.println("");
    DeployPerfUtil.put("START - Java   - ENV - AppletViewer.initEnvironment - DeployCacheHandler.reset");
    Cache localCache = Cache.class;
    DeployCacheHandler.reset();
    DeployPerfUtil.put("END   - Java   - ENV - AppletViewer.initEnvironment - DeployCacheHandler.reset");
    System.out.println("");
    try
    {
      DeployPerfUtil.put("START - Java   - ENV - AppletViewer.initEnvironment - prep MIME types table");
      localObject3 = MimeTable.getDefaultTable();
      DeployPerfUtil.put("END   - Java   - ENV - AppletViewer.initEnvironment - prep MIME types table");
    }
    catch (Throwable localThrowable4)
    {
      sun.plugin.util.Trace.printException(localThrowable4);
    }
    DeployPerfUtil.put("START - Java   - ENV - AppletViewer.initEnvironment - ProgressMonitor.setDefault");
    sun.net.ProgressMonitor.setDefault(new sun.plugin.util.ProgressMonitor());
    DeployPerfUtil.put("END   - Java   - ENV - AppletViewer.initEnvironment - ProgressMonitor.setDefault");
    DeployPerfUtil.put("START - Java   - ENV - AppletViewer.initEnvironment - install security manager");
    ActivatorSecurityManager localActivatorSecurityManager = new ActivatorSecurityManager();
    System.setSecurityManager(localActivatorSecurityManager);
    DeployPerfUtil.put("END   - Java   - ENV - AppletViewer.initEnvironment - install security manager");
    DeployPerfUtil.put("START - Java   - ENV - AppletViewer.initEnvironment - ConsoleHelper.displayHelp");
    System.out.println(ConsoleHelper.displayHelp());
    DeployPerfUtil.put("END   - Java   - ENV - AppletViewer.initEnvironment - ConsoleHelper.displayHelp");
    DeployPerfUtil.put("START - Java   - ENV - AppletViewer.initEnvironment - Config.validateSystemCacheDirectory");
    Config.validateSystemCacheDirectory();
    DeployPerfUtil.put("END   - Java   - ENV - AppletViewer.initEnvironment - Config.validateSystemCacheDirectory");
    DeployPerfUtil.put("START - Java   - ENV - AppletViewer.initEnvironment - URLJarFile.setCallBack");
    URLJarFile.setCallBack(new PluginURLJarFileCallBack());
    DeployPerfUtil.put("END   - Java   - ENV - AppletViewer.initEnvironment - URLJarFile.setCallBack");
    if (System.getProperty("os.name").indexOf("Windows") != -1)
    {
      DeployPerfUtil.put("START - Java   - ENV - AppletViewer.initEnvironment - show update message");
      UpdateCheck.showDialog();
      DeployPerfUtil.put("END   - Java   - ENV - AppletViewer.initEnvironment - show update message");
    }
    DeployPerfUtil.put("START - Java   - ENV - AppletViewer.initEnvironment - upgrade cache");
    try
    {
      DeploySysRun.execute(new DeploySysAction()
      {
        public Object execute()
          throws Exception
        {
          if ((Config.getBooleanProperty("deployment.javapi.cache.update")) && (CacheUpdateHelper.updateCache()))
          {
            Config.setBooleanProperty("deployment.javapi.cache.update", false);
            Config.storeIfDirty();
          }
          return null;
        }
      });
    }
    catch (Exception localException2)
    {
      sun.plugin.util.Trace.printException(localException2);
    }
    DeployPerfUtil.put("END   - Java   - ENV - AppletViewer.initEnvironment - upgrade cache");
    DeployPerfUtil.put("END   - Java   - ENV - AppletViewer.initEnvironment");
  }

  public void appletInit()
  {
    if (!Config.isConfigValid())
    {
      UIFactory.showErrorDialog(null, ResourceManager.getString("launcherrordialog.brief.message.applet"), ResourceManager.getString("enterprize.cfg.mandatory.applet", Config.getEnterprizeString()), ResourceManager.getString("error.default.title.applet"));
      return;
    }
    if (createClassLoader())
      initApplet();
  }

  private void initJarVersionMap()
  {
    int i = 1;
    String str1 = getParameter("archive_" + i);
    if (str1 != null)
      while (str1 != null)
      {
        localObject1 = new StringTokenizer(str1, ",", false);
        localObject2 = null;
        str3 = null;
        int j = 0;
        while (((StringTokenizer)localObject1).hasMoreTokens())
        {
          String str2 = ((StringTokenizer)localObject1).nextToken().trim();
          if (localObject2 == null)
            localObject2 = str2;
          else if (str2.toLowerCase().startsWith("version="))
            str3 = str2.substring("version=".length());
          else if (str2.toLowerCase().equals("preload"))
            j = 1;
        }
        if (localObject2 != null)
        {
          if (j != 0)
            this.preloadJarMap.put(localObject2, str3);
          this.jarVersionMap.put(localObject2, str3);
          this.newStyleJarList.add(localObject2);
        }
        i++;
        str1 = getParameter("archive_" + i);
      }
    Object localObject1 = getParameter("cache_archive");
    Object localObject2 = getParameter("cache_version");
    String str3 = getParameter("cache_archive_ex");
    try
    {
      this.jarVersionMap = JarCacheUtil.getJarsWithVersion((String)localObject1, (String)localObject2, str3);
    }
    catch (Exception localException)
    {
      sun.plugin.util.Trace.printException(localException, ResourceHandler.getMessage("cache.error.text"), ResourceHandler.getMessage("cache.error.caption"));
    }
    if (str3 != null)
    {
      StringTokenizer localStringTokenizer = new StringTokenizer(str3, ",", false);
      while (localStringTokenizer.hasMoreTokens())
      {
        String str4 = localStringTokenizer.nextToken().trim();
        int k = str4.indexOf(';');
        if (k != -1)
        {
          String str5 = str4.substring(k);
          if (str5.toLowerCase().indexOf("preload") != -1)
          {
            String str6 = str4.substring(0, k);
            this.preloadJarMap.put(str6, null);
          }
        }
      }
    }
  }

  private void storeJarVersionMapInAppContext()
  {
    Iterator localIterator = this.jarVersionMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str1 = (String)localIterator.next();
      String str2 = (String)this.jarVersionMap.get(str1);
      URL localURL = null;
      try
      {
        localURL = new URL(getCodeBase(), str1);
      }
      catch (MalformedURLException localMalformedURLException)
      {
        com.sun.deploy.util.Trace.ignoredException(localMalformedURLException);
      }
      if (localURL != null)
        AppContext.getAppContext().put(Config.getAppContextKeyPrefix() + localURL.toString(), str2);
    }
  }

  public boolean createClassLoader()
  {
    DeployPerfUtil.put("START - Java   - ENV - AppletViewer.createClassLoader");
    addAppletListener(this.appletEventListener);
    initJarVersionMap();
    URL localURL = getCodeBase();
    if (localURL == null)
      return false;
    try
    {
      if (!this.jarVersionMap.isEmpty())
        JarCacheUtil.verifyJarVersions(localURL, getClassLoaderCacheKey(), this.jarVersionMap);
    }
    catch (Exception localException)
    {
      sun.plugin.util.Trace.printException(localException, ResourceHandler.getMessage("cache.error.text"), ResourceHandler.getMessage("cache.error.caption"));
    }
    this.cli = ClassLoaderInfo.find(localURL, getClassLoaderCacheKey());
    this.cli.addReference();
    this.appletContext.addAppletPanelInContext(this);
    synchronized (AppletViewer.class)
    {
      super.init();
    }
    DeployPerfUtil.put("END   - Java   - ENV - AppletViewer.createClassLoader");
    return true;
  }

  public void initApplet()
  {
    DeployPerfUtil.put("START - Java   - ENV - AppletViewer.initApplet");
    Thread localThread = getAppletHandlerThread();
    if (!this.bInit)
    {
      String str = getParameter("image");
      if (str != null)
        try
        {
          URL localURL = new URL(getCodeBase(), str);
          this.grayBoxPainter.setCustomImageURL(localURL);
        }
        catch (MalformedURLException localMalformedURLException)
        {
          localMalformedURLException.printStackTrace();
        }
      this.grayBoxPainter.setProgressFilter(getCodeBase(), getJarFiles());
      this.grayBoxPainter.beginPainting(localThread.getThreadGroup());
      this.grayBoxListener = new GrayBoxListener(this, this.customBoxMessage);
      addMouseListener(this.grayBoxListener);
    }
    else
    {
      this.grayBoxPainter.resumePainting();
    }
    sun.plugin.util.Trace.msgPrintln("applet.progress.load");
    sendEvent(1);
    sun.plugin.util.Trace.msgPrintln("applet.progress.init");
    sendEvent(2);
    synchronized (this.syncInit)
    {
      this.bInit = true;
    }
    DeployPerfUtil.put("END   - Java   - ENV - AppletViewer.initApplet");
  }

  public void appletStart()
  {
    synchronized (this.syncInit)
    {
      if (!this.bInit)
        return;
    }
    if (this.stopped)
    {
      this.grayBoxPainter.suspendPainting();
      this.stopped = false;
    }
    sun.plugin.util.Trace.msgPrintln("applet.progress.start");
    try
    {
      DeployPerfUtil.write(new PluginRollup());
      sun.plugin.util.Trace.println("completed perf rollup");
      this.dumpPerf = true;
    }
    catch (IOException localIOException)
    {
    }
    sendEvent(3);
  }

  public void appletStop()
  {
    this.stopped = true;
    synchronized (this.syncInit)
    {
      if (!this.bInit)
        return;
    }
    if (this.grayBoxPainter != null)
      this.grayBoxPainter.suspendPainting();
    if (this.status == 1)
    {
      sun.plugin.util.Trace.msgPrintln("applet.progress.stoploading");
      stopLoading();
    }
    sun.plugin.util.Trace.msgPrintln("applet.progress.stop");
    sendEvent(4);
  }

  public void appletDestroy()
  {
    appletDestroy(1000L);
  }

  public void appletDestroy(long paramLong)
  {
    this.stopped = true;
    synchronized (this.syncInit)
    {
      if (!this.bInit)
        return;
    }
    if (this.grayBoxPainter != null)
    {
      this.grayBoxPainter.finishPainting();
      this.grayBoxPainter = null;
    }
    if (this.grayBoxListener != null)
    {
      removeMouseListener(this.grayBoxListener);
      this.grayBoxListener = null;
    }
    removeAppletListener(this.appletEventListener);
    this.appletEventListener = null;
    this.appletContext.removeAppletPanelFromContext(this);
    ??? = getAppletHandlerThread();
    sun.plugin.util.Trace.msgPrintln("applet.progress.findinfo.0");
    this.cli.removeReference();
    this.cli = null;
    sun.plugin.util.Trace.msgPrintln("applet.progress.findinfo.1");
    Runnable local2 = new Runnable()
    {
      public void run()
      {
        AppletViewer.this.onPrivateClose(5000);
      }
    };
    Thread localThread = new Thread(local2);
    localThread.start();
    try
    {
      sun.plugin.util.Trace.msgPrintln("applet.progress.joining");
      if (??? != null)
        ((Thread)???).join(paramLong);
      sun.plugin.util.Trace.msgPrintln("applet.progress.joined");
    }
    catch (InterruptedException localInterruptedException)
    {
    }
  }

  protected void onPrivateClose(int paramInt)
  {
    sun.plugin.util.Trace.msgPrintln("applet.progress.destroy");
    sendEvent(5);
    sun.plugin.util.Trace.msgPrintln("applet.progress.dispose");
    sendEvent(0);
    synchronized (this.appletQuitLock)
    {
      while (this.readyToQuit != true)
        try
        {
          this.appletQuitLock.wait();
        }
        catch (InterruptedException localInterruptedException)
        {
        }
    }
    sun.plugin.util.Trace.msgPrintln("applet.progress.quit");
    sendEvent(6);
    this.appletContext = null;
  }

  public void preRefresh()
  {
    Cache.clearLoadedResources();
    if (this.cli != null)
      ClassLoaderInfo.markNotCachable(getCodeBase(), getClassLoaderCacheKey());
  }

  public String getParameter(String paramString)
  {
    paramString = paramString.toLowerCase(Locale.ENGLISH);
    synchronized (this.atts)
    {
      String str = (String)this.atts.get(paramString);
      if (str != null)
        str = trimWhiteSpaces(str);
      return str;
    }
  }

  public void setParameter(String paramString, Object paramObject)
  {
    paramString = paramString.toLowerCase(Locale.ENGLISH);
    synchronized (this.atts)
    {
      this.atts.put(paramString, trimWhiteSpaces(paramObject.toString()));
    }
  }

  private String trimWhiteSpaces(String paramString)
  {
    if (paramString == null)
      return paramString;
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 0; i < paramString.length(); i++)
    {
      char c = paramString.charAt(i);
      if ((c != '\n') && (c != '\f') && (c != '\r') && (c != '\t'))
        localStringBuffer.append(c);
    }
    return localStringBuffer.toString().trim();
  }

  public void setDocumentBase(String paramString)
  {
    if (!this.docbaseInit)
    {
      String str = URLUtil.canonicalize(paramString);
      try
      {
        this.documentURL = new URL(canonicalizeDocumentURL(str));
      }
      catch (Throwable localThrowable)
      {
        localThrowable.printStackTrace();
      }
      this.docbaseInit = true;
      synchronized (this.docBaseSyncObj)
      {
        this.docBaseSyncObj.notifyAll();
      }
    }
  }

  public String canonicalizeDocumentURL(String paramString)
  {
    int i = -1;
    int j = paramString.indexOf('#');
    int k = paramString.indexOf('?');
    if ((k != -1) && (j != -1))
      i = Math.min(j, k);
    else if (j != -1)
      i = j;
    else if (k != -1)
      i = k;
    String str;
    if (i == -1)
      str = paramString;
    else
      str = paramString.substring(0, i);
    StringBuffer localStringBuffer = new StringBuffer(str);
    int m = localStringBuffer.toString().indexOf("|");
    if (m >= 0)
      localStringBuffer.setCharAt(m, ':');
    if (i != -1)
      localStringBuffer.append(paramString.substring(i));
    return localStringBuffer.toString();
  }

  public URL getDocumentBase()
  {
    Object localObject1 = new Object();
    synchronized (localObject1)
    {
      if (!this.docbaseInit)
      {
        BrowserService localBrowserService = (BrowserService)ServiceManager.getService();
        if ((localBrowserService.isNetscape()) && (localBrowserService.getBrowserVersion() >= 5.0F))
        {
          try
          {
            synchronized (this.docBaseSyncObj)
            {
              while (!this.docbaseInit)
                this.docBaseSyncObj.wait(0L);
            }
          }
          catch (InterruptedException localInterruptedException)
          {
            localInterruptedException.printStackTrace();
          }
        }
        else
        {
          JSContext localJSContext = (JSContext)getAppletContext();
          try
          {
            JSObject localJSObject1 = localJSContext.getJSObject();
            if (localJSObject1 == null)
              throw new JSException("Unable to obtain Window object");
            JSObject localJSObject2 = (JSObject)localJSObject1.getMember("document");
            if (localJSObject2 == null)
              throw new JSException("Unable to obtain Document object");
            String str1 = (String)localJSObject2.getMember("URL");
            String str2 = URLUtil.canonicalize(str1);
            this.documentURL = new URL(canonicalizeDocumentURL(str2));
          }
          catch (Throwable localThrowable)
          {
            sun.plugin.util.Trace.println(localThrowable.getMessage());
            return null;
          }
          this.docbaseInit = true;
        }
      }
    }
    return this.documentURL;
  }

  public URL getCodeBase()
  {
    Object localObject1 = new Object();
    synchronized (localObject1)
    {
      if (!this.codeBaseInit)
      {
        String str1 = getParameter("java_codebase");
        if (str1 == null)
          str1 = getParameter("codebase");
        URL localURL = getDocumentBase();
        if (localURL == null)
          return null;
        if (str1 != null)
        {
          if ((!str1.equals(".")) && (!str1.endsWith("/")))
            str1 = str1 + "/";
          str1 = URLUtil.canonicalize(str1);
          try
          {
            this.baseURL = new URL(localURL, str1);
          }
          catch (MalformedURLException localMalformedURLException1)
          {
          }
        }
        if (this.baseURL == null)
        {
          String str2 = localURL.toString();
          int i = str2.indexOf('?');
          if (i > 0)
            str2 = str2.substring(0, i);
          i = str2.lastIndexOf('/');
          if ((i > -1) && (i < str2.length() - 1))
            try
            {
              this.baseURL = new URL(URLUtil.canonicalize(str2.substring(0, i + 1)));
            }
            catch (MalformedURLException localMalformedURLException2)
            {
            }
          if (this.baseURL == null)
            this.baseURL = localURL;
        }
        this.codeBaseInit = true;
      }
    }
    return this.baseURL;
  }

  public int getWidth()
  {
    String str = getParameter("width");
    if (str != null)
      return Integer.valueOf(str).intValue();
    return 0;
  }

  public int getHeight()
  {
    String str = getParameter("height");
    if (str != null)
      return Integer.valueOf(str).intValue();
    return 0;
  }

  public boolean hasInitialFocus()
  {
    if ((isJDK11Applet()) || (isJDK12Applet()))
      return false;
    String str = getParameter("initial_focus");
    if ((str != null) && (str.toLowerCase().equals("false")))
      return false;
    return !Config.getInstance().isNativeModalDialogUp();
  }

  public String getCode()
  {
    String str1 = getParameter("classid");
    String str2 = null;
    if (str1 != null)
    {
      int i = str1.indexOf("java:");
      if (i > -1)
      {
        str2 = str1.substring(5 + i);
        if ((str2 != null) || (!str2.equals("")))
          return str2;
      }
    }
    str2 = getParameter("java_code");
    if (str2 == null)
      str2 = getParameter("code");
    return str2;
  }

  public boolean isLegacyLifeCycle()
  {
    String str = getParameter("legacy_lifecycle");
    return (str != null) && (str.equalsIgnoreCase("true"));
  }

  public String getClassLoaderCacheKey()
  {
    String str1 = getParameter("classloader-policy");
    if ((str1 != null) && (str1.equals("classic")))
      return super.getClassLoaderCacheKey();
    if (this.classLoaderCacheKey == null)
    {
      StringBuffer localStringBuffer = new StringBuffer();
      localStringBuffer.append(getCodeBase());
      String str2 = getJarFiles();
      if (str2 != null)
      {
        localStringBuffer.append(",");
        localStringBuffer.append(str2);
      }
      this.classLoaderCacheKey = localStringBuffer.toString();
    }
    return this.classLoaderCacheKey;
  }

  private static synchronized String getJarsInCacheArchiveEx(String paramString)
  {
    if (paramString == null)
      return null;
    String str1 = "";
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, ",", false);
    int i = localStringTokenizer.countTokens();
    for (int j = 0; j < i; j++)
    {
      String str2 = localStringTokenizer.nextToken().trim();
      int k = str2.indexOf(";");
      if (k != -1)
      {
        String str3 = str2.substring(0, k);
        str1 = str1 + str3;
        str1 = str1 + (j != i - 1 ? "," : "");
      }
    }
    return str1;
  }

  public String getJarFiles()
  {
    StringBuffer localStringBuffer = null;
    if (!this.newStyleJarList.isEmpty())
    {
      localObject = this.newStyleJarList.iterator();
      while (((Iterator)localObject).hasNext())
      {
        if (localStringBuffer == null)
          localStringBuffer = new StringBuffer();
        str1 = (String)((Iterator)localObject).next();
        localStringBuffer.append(str1);
        if (((Iterator)localObject).hasNext())
          localStringBuffer.append(",");
      }
      return addJarFileToPath(localStringBuffer == null ? null : localStringBuffer.toString(), null);
    }
    Object localObject = getParameter("archive");
    String str1 = getParameter("java_archive");
    String str2 = getParameter("cache_archive");
    String str3 = getParameter("cache_archive_ex");
    String str4 = null;
    if (str3 != null)
    {
      int i = str3.indexOf(";");
      if (i != -1)
        str4 = getJarsInCacheArchiveEx(str3);
      else
        str4 = str3;
    }
    return addJarFileToPath(str4, addJarFileToPath(str2, addJarFileToPath(str1, (String)localObject)));
  }

  private String addJarFileToPath(String paramString1, String paramString2)
  {
    if ((paramString1 == null) && (paramString2 == null))
      return null;
    if ((paramString1 == null) && (paramString2 != null))
      return paramString2;
    if ((paramString1 != null) && (paramString2 == null))
      return paramString1;
    return paramString1 + "," + paramString2;
  }

  private void loadLocalJarFiles(PluginClassLoader paramPluginClassLoader, String paramString)
  {
    File localFile = new File(paramString);
    if (localFile.exists())
    {
      String[] arrayOfString = localFile.list(new FilenameFilter()
      {
        public boolean accept(File paramAnonymousFile, String paramAnonymousString)
        {
          return paramAnonymousString.endsWith(".jar");
        }
      });
      for (int i = 0; i < arrayOfString.length; i++)
        try
        {
          URL localURL = new File(paramString + File.separator + arrayOfString[i]).toURI().toURL();
          paramPluginClassLoader.addLocalJar(localURL);
        }
        catch (MalformedURLException localMalformedURLException)
        {
          localMalformedURLException.printStackTrace();
        }
    }
  }

  protected void setupAppletAppContext()
  {
    storeJarVersionMapInAppContext();
    AppContext.getAppContext().put("deploy.trust.decider.app.name", getName());
  }

  protected void loadJarFiles(AppletClassLoader paramAppletClassLoader)
    throws IOException, InterruptedException
  {
    if (this.loading_first_time)
    {
      this.loading_first_time = false;
      try
      {
        this.preloading = true;
        JarCacheUtil.preload(getCodeBase(), this.preloadJarMap);
        this.preloading = false;
      }
      catch (Exception localException)
      {
        sun.plugin.util.Trace.printException(localException, ResourceHandler.getMessage("cache.error.text"), ResourceHandler.getMessage("cache.error.caption"));
      }
    }
    String str1 = getJarFiles();
    try
    {
      this.cli.lock();
      String str2;
      if ((!this.cli.getLocalJarsLoaded()) && ((paramAppletClassLoader instanceof PluginClassLoader)))
      {
        localObject1 = File.separator;
        str2 = System.getProperty("java.home") + (String)localObject1 + "lib" + (String)localObject1 + "applet";
        loadLocalJarFiles((PluginClassLoader)paramAppletClassLoader, str2);
        if (Config.getOSName().equalsIgnoreCase("Windows"))
        {
          String str3 = Config.getSystemHome() + (String)localObject1 + "Lib" + (String)localObject1 + "Untrusted";
          loadLocalJarFiles((PluginClassLoader)paramAppletClassLoader, str3);
        }
        this.cli.setLocalJarsLoaded(true);
      }
      if (str1 == null)
        return;
      Object localObject1 = new StringTokenizer(str1, ",", false);
      while (((StringTokenizer)localObject1).hasMoreTokens())
      {
        str2 = ((StringTokenizer)localObject1).nextToken().trim();
        if (!this.cli.hasJar(str2))
          this.cli.addJar(str2);
      }
      super.loadJarFiles(paramAppletClassLoader);
    }
    finally
    {
      this.cli.unlock();
    }
  }

  public String getSerializedObject()
  {
    String str = getParameter("java_object");
    if (str == null)
      str = getParameter("object");
    return str;
  }

  public Applet getApplet()
  {
    Applet localApplet = super.getApplet();
    if (localApplet != null)
    {
      if ((localApplet instanceof BeansApplet))
        return null;
      return localApplet;
    }
    return null;
  }

  public Object getViewedObject()
  {
    Applet localApplet = super.getApplet();
    if ((localApplet instanceof BeansApplet))
      return ((BeansApplet)localApplet).bean;
    return localApplet;
  }

  public void setAppletContext(AppletContext paramAppletContext)
  {
    if (paramAppletContext == null)
      throw new IllegalArgumentException("AppletContext");
    if (this.appletContext != null)
      this.appletContext.removeAppletPanelFromContext(this);
    this.appletContext = ((PluginAppletContext)paramAppletContext);
  }

  public AppletContext getAppletContext()
  {
    return this.appletContext;
  }

  public void setColorAndText()
  {
    Color localColor = null;
    String str1 = getParameter("boxbgcolor");
    if (str1 != null)
    {
      localColor = createColor("boxbgcolor", str1);
      if (localColor != null)
        this.grayBoxPainter.setBoxBGColor(localColor);
    }
    setBackground(this.grayBoxPainter.getBoxBGColor());
    String str2 = getParameter("boxfgcolor");
    if (str2 != null)
    {
      localColor = createColor("boxfgcolor", str2);
      if (localColor != null)
        this.grayBoxPainter.setBoxFGColor(localColor);
    }
    String str3 = getParameter("progresscolor");
    if (str3 != null)
    {
      localColor = createColor("progresscolor", str3);
      if (localColor != null)
        this.grayBoxPainter.setProgressColor(localColor);
    }
    this.customBoxMessage = getParameter("boxmessage");
    if (this.customBoxMessage != null)
      this.grayBoxPainter.setWaitingMessage(this.customBoxMessage);
    else
      this.grayBoxPainter.setWaitingMessage(getWaitingMessage());
    String str4 = getParameter("progressbar");
    if (str4 != null)
      this.grayBoxPainter.enableProgressBar(new Boolean(str4).booleanValue());
  }

  private Color createColor(String paramString1, String paramString2)
  {
    if ((paramString2 != null) && (paramString2.indexOf(",") != -1))
    {
      StringTokenizer localStringTokenizer = new StringTokenizer(paramString2, ",");
      if (localStringTokenizer.countTokens() == 3)
      {
        int i = 0;
        int j = 0;
        int k = 0;
        int m = 0;
        while (localStringTokenizer.hasMoreTokens())
        {
          String str = (String)localStringTokenizer.nextElement();
          switch (i)
          {
          case 0:
            if (!str.trim().equals(""))
              j = new Integer(str.trim()).intValue();
            break;
          case 1:
            if (!str.trim().equals(""))
              k = new Integer(str.trim()).intValue();
            break;
          case 2:
            if (!str.trim().equals(""))
              m = new Integer(str.trim()).intValue();
            break;
          }
          i++;
        }
        return new Color(j, k, m);
      }
      sun.plugin.util.Trace.msgPrintln("applet_viewer.color_tag", new Object[] { paramString1 });
      return null;
    }
    if (paramString2 != null)
      try
      {
        return Color.decode(paramString2);
      }
      catch (NumberFormatException localNumberFormatException)
      {
        if (paramString2.equalsIgnoreCase("red"))
          return Color.red;
        if (paramString2.equalsIgnoreCase("yellow"))
          return Color.yellow;
        if (paramString2.equalsIgnoreCase("black"))
          return Color.black;
        if (paramString2.equalsIgnoreCase("blue"))
          return Color.blue;
        if ((paramString2.equalsIgnoreCase("cyan")) || (paramString2.equalsIgnoreCase("aqua")))
          return Color.cyan;
        if (paramString2.equalsIgnoreCase("darkGray"))
          return Color.darkGray;
        if (paramString2.equalsIgnoreCase("gray"))
          return Color.gray;
        if ((paramString2.equalsIgnoreCase("lightGray")) || (paramString2.equalsIgnoreCase("silver")))
          return Color.lightGray;
        if ((paramString2.equalsIgnoreCase("green")) || (paramString2.equalsIgnoreCase("lime")))
          return Color.green;
        if ((paramString2.equalsIgnoreCase("magenta")) || (paramString2.equalsIgnoreCase("fuchsia")))
          return Color.magenta;
        if (paramString2.equalsIgnoreCase("orange"))
          return Color.orange;
        if (paramString2.equalsIgnoreCase("pink"))
          return Color.pink;
        if (paramString2.equalsIgnoreCase("white"))
          return Color.white;
        if (paramString2.equalsIgnoreCase("maroon"))
          return new Color(128, 0, 0);
        if (paramString2.equalsIgnoreCase("purple"))
          return new Color(128, 0, 128);
        if (paramString2.equalsIgnoreCase("navy"))
          return new Color(0, 0, 128);
        if (paramString2.equalsIgnoreCase("teal"))
          return new Color(0, 128, 128);
        if (paramString2.equalsIgnoreCase("olive"))
          return new Color(128, 128, 0);
      }
    return null;
  }

  public void paint(Graphics paramGraphics)
  {
    Dimension localDimension = getSize();
    if ((localDimension.width > 0) && (localDimension.height > 0) && ((this.status == 1) || (this.status == 2) || (this.status == 7)))
      paintForegrnd(paramGraphics);
    else
      super.paint(paramGraphics);
  }

  public Color getForeground()
  {
    Color localColor = super.getForeground();
    if (null == localColor)
      localColor = Color.BLACK;
    return localColor;
  }

  public void paintForegrnd(Graphics paramGraphics)
  {
    if (this.grayBoxPainter != null)
      this.grayBoxPainter.paintGrayBox(this, paramGraphics);
  }

  public String getWaitingMessage()
  {
    if (this.status == 7)
      return getMessage("failed");
    MessageFormat localMessageFormat = new MessageFormat(getMessage("loading"));
    return localMessageFormat.format(new Object[] { getHandledType() });
  }

  protected void load(InputStream paramInputStream)
  {
    this.is = paramInputStream;
  }

  protected Applet createApplet(AppletClassLoader paramAppletClassLoader)
    throws ClassNotFoundException, IllegalAccessException, IOException, InstantiationException, InterruptedException
  {
    if (this.is == null)
      return super.createApplet(paramAppletClassLoader);
    AppletObjectInputStream localAppletObjectInputStream = new AppletObjectInputStream(this.is, paramAppletClassLoader);
    Object localObject1 = localAppletObjectInputStream.readObject();
    Applet localApplet = (Applet)localObject1;
    this.doInit = false;
    if (Thread.interrupted())
    {
      try
      {
        this.status = 0;
        localApplet = null;
        showAppletStatus("death");
      }
      finally
      {
        Thread.currentThread().interrupt();
      }
      return null;
    }
    this.is = null;
    return localApplet;
  }

  public String getName()
  {
    String str = getParameter("name");
    if (str != null)
      return str;
    str = getCode();
    int i;
    if (str != null)
    {
      i = str.lastIndexOf(".class");
      if (i != -1)
        str = str.substring(0, i);
    }
    else
    {
      str = getSerializedObject();
      if (str != null)
      {
        i = str.lastIndexOf(".ser");
        if (i != -1)
          str = str.substring(0, i);
      }
    }
    return str;
  }

  public static int getAcceleratorKey(String paramString)
  {
    return ResourceHandler.getAcceleratorKey(paramString);
  }

  protected String getHandledType()
  {
    return getMessage("java_applet");
  }

  public void addAppletStatusListener(AppletStatusListener paramAppletStatusListener)
  {
    this.statusListener = paramAppletStatusListener;
  }

  public void removeAppletStatusListener(AppletStatusListener paramAppletStatusListener)
  {
    this.statusListener = null;
  }

  public void setStatus(int paramInt)
  {
    this.status = paramInt;
  }

  public void showAppletLog(String paramString)
  {
    super.showAppletLog(paramString);
  }

  public boolean isStopped()
  {
    return this.stopped;
  }

  public void showAppletStatus(String paramString)
  {
    if ((paramString != null) && (!paramString.equals("")) && (!paramString.equals("\n")))
    {
      String str = getName();
      MessageFormat localMessageFormat = new MessageFormat(getMessage("status_applet"));
      if ((str != null) && (!paramString.equals("")))
        getAppletContext().showStatus(localMessageFormat.format(new Object[] { str, paramString }));
      else
        getAppletContext().showStatus(localMessageFormat.format(new Object[] { paramString, "" }));
      if (this.grayBoxPainter != null)
        if (this.status == 7)
          this.grayBoxPainter.showLoadingError();
        else if (this.status >= 3)
          this.grayBoxPainter.suspendPainting();
      if (this.statusListener != null)
        this.statusListener.statusChanged(this.status);
    }
  }

  public void setDoInit(boolean paramBoolean)
  {
    this.doInit = paramBoolean;
  }

  public static String getMessage(String paramString)
  {
    return ResourceHandler.getMessage(paramString);
  }

  public static String[] getMessageArray(String paramString)
  {
    return ResourceHandler.getMessageArray(paramString);
  }

  protected AppletClassLoader createClassLoader(URL paramURL)
  {
    return ClassLoaderInfo.find(paramURL, getClassLoaderCacheKey()).getLoader();
  }

  protected void showAppletException(Throwable paramThrowable)
  {
    super.showAppletException(paramThrowable);
    sun.plugin.util.Trace.msgPrintln("exception", new Object[] { paramThrowable.toString() });
    if (fShowException)
      sun.plugin.util.Trace.printException(paramThrowable);
    if (this.grayBoxPainter != null)
      this.grayBoxPainter.showLoadingError();
  }

  public void showStatusText(String paramString)
  {
    getAppletContext().showStatus(paramString);
  }

  public void update(Graphics paramGraphics)
  {
    Dimension localDimension = getSize();
    if ((localDimension.width > 0) && (localDimension.height > 0) && ((this.status == 1) || (this.status == 2) || (this.status == 7)))
      paintForegrnd(paramGraphics);
    else
      super.update(paramGraphics);
  }

  public int getLoadingStatus()
  {
    return this.status;
  }

  public void windowActivated(WindowEvent paramWindowEvent)
  {
  }

  public void windowClosed(WindowEvent paramWindowEvent)
  {
  }

  public void windowClosing(WindowEvent paramWindowEvent)
  {
  }

  public void windowDeactivated(WindowEvent paramWindowEvent)
  {
  }

  public void windowDeiconified(WindowEvent paramWindowEvent)
  {
  }

  public void windowIconified(WindowEvent paramWindowEvent)
  {
  }

  public void windowOpened(WindowEvent paramWindowEvent)
  {
  }

  private static class AppletEventListener
    implements AppletListener
  {
    public void appletStateChanged(AppletEvent paramAppletEvent)
    {
      switch (paramAppletEvent.getID())
      {
      case 51234:
        AppletViewer localAppletViewer = (AppletViewer)paramAppletEvent.getSource();
        if (localAppletViewer != null)
        {
          Object localObject = localAppletViewer.getViewedObject();
          if ((localObject instanceof Component))
          {
            localAppletViewer.setSize(localAppletViewer.getSize());
            ((Component)localObject).setSize(localAppletViewer.getSize());
            localAppletViewer.validate();
          }
        }
        break;
      }
    }
  }

  static class GrayBoxListener
    implements MouseListener, ActionListener
  {
    private PopupMenu popup;
    private MenuItem open_console;
    private MenuItem about_java;
    private String msg = null;
    private AppletViewer av;

    GrayBoxListener(AppletViewer paramAppletViewer, String paramString)
    {
      this.msg = paramString;
      this.av = paramAppletViewer;
    }

    private PopupMenu getPopupMenu()
    {
      if (this.popup == null)
      {
        Font localFont1 = this.av.getFont();
        Font localFont2 = localFont1.deriveFont(11.0F);
        this.popup = new PopupMenu();
        this.open_console = new MenuItem(ResourceHandler.getMessage("dialogfactory.menu.open_console"));
        this.open_console.setFont(localFont2);
        this.about_java = new MenuItem(ResourceHandler.getMessage("dialogfactory.menu.about_java"));
        this.about_java.setFont(localFont2);
        this.open_console.addActionListener(this);
        this.about_java.addActionListener(this);
        this.popup.add(this.open_console);
        this.popup.add("-");
        this.popup.add(this.about_java);
        this.av.add(this.popup);
      }
      return this.popup;
    }

    public void mouseEntered(MouseEvent paramMouseEvent)
    {
      if (this.msg != null)
        this.av.showStatusText(this.msg);
      else
        this.av.showStatusText(this.av.getWaitingMessage());
    }

    public void mouseExited(MouseEvent paramMouseEvent)
    {
    }

    public void mousePressed(MouseEvent paramMouseEvent)
    {
      if ((paramMouseEvent.isPopupTrigger()) && (this.av.getLoadingStatus() == 7))
        getPopupMenu().show(paramMouseEvent.getComponent(), paramMouseEvent.getX(), paramMouseEvent.getY());
    }

    public void mouseReleased(MouseEvent paramMouseEvent)
    {
      if ((paramMouseEvent.isPopupTrigger()) && (this.av.getLoadingStatus() == 7))
        getPopupMenu().show(paramMouseEvent.getComponent(), paramMouseEvent.getX(), paramMouseEvent.getY());
    }

    public void mouseClicked(MouseEvent paramMouseEvent)
    {
    }

    public void actionPerformed(ActionEvent paramActionEvent)
    {
      if (paramActionEvent.getSource() == this.open_console)
        JavaRunTime.showJavaConsoleLater(true);
      else if (paramActionEvent.getSource() == this.about_java)
        UIFactory.showAboutJavaDialog();
    }
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.AppletViewer
 * JD-Core Version:    0.6.2
 */