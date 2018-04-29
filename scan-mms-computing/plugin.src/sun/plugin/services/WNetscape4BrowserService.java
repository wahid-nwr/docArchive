package sun.plugin.services;

import com.sun.deploy.net.cookie.CookieHandler;
import com.sun.deploy.net.offline.OfflineHandler;
import com.sun.deploy.net.proxy.BrowserProxyConfig;
import com.sun.deploy.net.proxy.ProxyHandler;
import com.sun.deploy.net.proxy.WNetscape4ProxyConfig;
import com.sun.deploy.security.BrowserAuthenticator;
import com.sun.deploy.security.CertStore;
import com.sun.deploy.services.WPlatformService;
import java.security.KeyStore;
import java.util.HashMap;
import sun.plugin.net.cookie.Netscape4CookieHandler;
import sun.plugin.net.proxy.PluginAutoProxyHandler;
import sun.plugin.viewer.context.NetscapeAppletContext;
import sun.plugin.viewer.context.PluginAppletContext;
import sun.plugin.viewer.context.PluginBeansContext;

public final class WNetscape4BrowserService extends WPlatformService
  implements BrowserService
{
  private static HashMap nameMap = null;

  public CookieHandler getCookieHandler()
  {
    return new Netscape4CookieHandler();
  }

  public BrowserProxyConfig getProxyConfig()
  {
    return new WNetscape4ProxyConfig();
  }

  public ProxyHandler getSystemProxyHandler()
  {
    return null;
  }

  public ProxyHandler getAutoProxyHandler()
  {
    return new PluginAutoProxyHandler();
  }

  public ProxyHandler getBrowserProxyHandler()
  {
    return null;
  }

  public CertStore getBrowserSigningRootCertStore()
  {
    return null;
  }

  public CertStore getBrowserSSLRootCertStore()
  {
    return null;
  }

  public CertStore getBrowserTrustedCertStore()
  {
    return null;
  }

  public KeyStore getBrowserClientAuthKeyStore()
  {
    return null;
  }

  public PluginAppletContext getAppletContext()
  {
    return new NetscapeAppletContext();
  }

  public PluginBeansContext getBeansContext()
  {
    PluginBeansContext localPluginBeansContext = new PluginBeansContext();
    localPluginBeansContext.setPluginAppletContext(new NetscapeAppletContext());
    return localPluginBeansContext;
  }

  public boolean isIExplorer()
  {
    return false;
  }

  public boolean isNetscape()
  {
    return true;
  }

  public float getBrowserVersion()
  {
    return 4.0F;
  }

  public boolean isConsoleIconifiedOnClose()
  {
    return false;
  }

  public native boolean installBrowserEventListener();

  public BrowserAuthenticator getBrowserAuthenticator()
  {
    return null;
  }

  public String mapBrowserElement(String paramString)
  {
    String str = (String)getNameMap().get(paramString);
    return str != null ? str : paramString;
  }

  private static synchronized HashMap getNameMap()
  {
    if (nameMap == null)
    {
      nameMap = new HashMap();
      nameMap.put("self.document.forms", "ns4.HTMLFormCollection");
      nameMap.put("self.document.links", "ns4.HTMLAnchorCollection");
      nameMap.put("self.document.images", "ns4.HTMLImageCollection");
      nameMap.put("self.document.applets", "ns4.HTMLAppletCollection");
      nameMap.put("self.document.anchors", "ns4.HTMLAnchorCollection");
    }
    return nameMap;
  }

  public OfflineHandler getOfflineHandler()
  {
    return null;
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.services.WNetscape4BrowserService
 * JD-Core Version:    0.6.2
 */