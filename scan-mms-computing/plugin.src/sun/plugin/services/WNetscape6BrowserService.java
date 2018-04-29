package sun.plugin.services;

import com.sun.deploy.net.cookie.CookieHandler;
import com.sun.deploy.net.offline.OfflineHandler;
import com.sun.deploy.net.proxy.BrowserProxyConfig;
import com.sun.deploy.net.proxy.ProxyHandler;
import com.sun.deploy.net.proxy.WNetscape6ProxyConfig;
import com.sun.deploy.security.BrowserAuthenticator;
import com.sun.deploy.security.BrowserKeystore;
import com.sun.deploy.security.CertStore;
import com.sun.deploy.security.MozillaSSLRootCertStore;
import com.sun.deploy.security.MozillaSigningRootCertStore;
import com.sun.deploy.services.WPlatformService;
import java.security.AccessController;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivilegedAction;
import java.util.HashMap;
import sun.plugin.net.cookie.WNetscape6CookieHandler;
import sun.plugin.net.proxy.PluginAutoProxyHandler;
import sun.plugin.net.proxy.WNetscape6BrowserProxyHandler;
import sun.plugin.viewer.context.PluginAppletContext;
import sun.plugin.viewer.context.PluginBeansContext;
import sun.plugin.viewer.context.WNetscape6AppletContext;

public final class WNetscape6BrowserService extends WPlatformService
  implements BrowserService
{
  private static HashMap nameMap = null;

  public CookieHandler getCookieHandler()
  {
    return new WNetscape6CookieHandler();
  }

  public BrowserProxyConfig getProxyConfig()
  {
    return new WNetscape6ProxyConfig();
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
    return new WNetscape6BrowserProxyHandler();
  }

  public CertStore getBrowserSigningRootCertStore()
  {
    if (BrowserKeystore.isJSSCryptoConfigured())
      return new MozillaSigningRootCertStore();
    return null;
  }

  public CertStore getBrowserSSLRootCertStore()
  {
    if (BrowserKeystore.isJSSCryptoConfigured())
      return new MozillaSSLRootCertStore();
    return null;
  }

  public CertStore getBrowserTrustedCertStore()
  {
    return null;
  }

  public KeyStore getBrowserClientAuthKeyStore()
  {
    if (BrowserKeystore.isJSSCryptoConfigured())
    {
      KeyStore localKeyStore = (KeyStore)AccessController.doPrivileged(new PrivilegedAction()
      {
        public Object run()
        {
          try
          {
            return KeyStore.getInstance("MozillaMy");
          }
          catch (KeyStoreException localKeyStoreException)
          {
            localKeyStoreException.printStackTrace();
          }
          return null;
        }
      });
      return localKeyStore;
    }
    return null;
  }

  public PluginAppletContext getAppletContext()
  {
    return new WNetscape6AppletContext();
  }

  public PluginBeansContext getBeansContext()
  {
    PluginBeansContext localPluginBeansContext = new PluginBeansContext();
    localPluginBeansContext.setPluginAppletContext(new WNetscape6AppletContext());
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
    return 6.0F;
  }

  public boolean isConsoleIconifiedOnClose()
  {
    return false;
  }

  public native boolean installBrowserEventListener();

  public BrowserAuthenticator getBrowserAuthenticator()
  {
    return new WNetscape6BrowserAuthenticator();
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
      nameMap.put("NodeList", "HTMLCollection");
      nameMap.put("HTMLOptionCollection", "HTMLCollection");
      nameMap.put("HTMLInsElement", "HTMLModElement");
      nameMap.put("HTMLDelElement", "HTMLModElement");
      nameMap.put("HTMLSpanElement", "HTMLElement");
    }
    return nameMap;
  }

  public OfflineHandler getOfflineHandler()
  {
    return null;
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.services.WNetscape6BrowserService
 * JD-Core Version:    0.6.2
 */