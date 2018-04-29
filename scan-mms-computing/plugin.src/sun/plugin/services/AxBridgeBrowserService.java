package sun.plugin.services;

import com.sun.deploy.net.cookie.CookieHandler;
import com.sun.deploy.net.cookie.IExplorerCookieHandler;
import com.sun.deploy.net.offline.OfflineHandler;
import com.sun.deploy.net.proxy.BrowserProxyConfig;
import com.sun.deploy.net.proxy.ProxyHandler;
import com.sun.deploy.net.proxy.WIExplorerAutoProxyHandler;
import com.sun.deploy.net.proxy.WIExplorerProxyConfig;
import com.sun.deploy.security.BrowserAuthenticator;
import com.sun.deploy.security.CertStore;
import com.sun.deploy.security.WIExplorerBrowserAuthenticator;
import com.sun.deploy.security.WIExplorerSSLRootCertStore;
import com.sun.deploy.security.WIExplorerSigningCertStore;
import com.sun.deploy.security.WIExplorerSigningRootCertStore;
import com.sun.deploy.services.WPlatformService;
import java.security.AccessController;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivilegedAction;
import sun.plugin.viewer.context.AxBridgeAppletContext;
import sun.plugin.viewer.context.PluginAppletContext;
import sun.plugin.viewer.context.PluginBeansContext;

public final class AxBridgeBrowserService extends WPlatformService
  implements BrowserService
{
  public CookieHandler getCookieHandler()
  {
    return new IExplorerCookieHandler();
  }

  public BrowserProxyConfig getProxyConfig()
  {
    return new WIExplorerProxyConfig();
  }

  public ProxyHandler getSystemProxyHandler()
  {
    return null;
  }

  public ProxyHandler getAutoProxyHandler()
  {
    return new WIExplorerAutoProxyHandler();
  }

  public ProxyHandler getBrowserProxyHandler()
  {
    return null;
  }

  public CertStore getBrowserSigningRootCertStore()
  {
    return new WIExplorerSigningRootCertStore();
  }

  public CertStore getBrowserSSLRootCertStore()
  {
    return new WIExplorerSSLRootCertStore();
  }

  public CertStore getBrowserTrustedCertStore()
  {
    return new WIExplorerSigningCertStore();
  }

  public KeyStore getBrowserClientAuthKeyStore()
  {
    KeyStore localKeyStore = (KeyStore)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        try
        {
          return KeyStore.getInstance("WIExplorerMy");
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

  public PluginAppletContext getAppletContext()
  {
    return new AxBridgeAppletContext();
  }

  public PluginBeansContext getBeansContext()
  {
    PluginBeansContext localPluginBeansContext = new PluginBeansContext();
    localPluginBeansContext.setPluginAppletContext(new AxBridgeAppletContext());
    return localPluginBeansContext;
  }

  public boolean isIExplorer()
  {
    return true;
  }

  public boolean isNetscape()
  {
    return false;
  }

  public float getBrowserVersion()
  {
    return 6.0F;
  }

  public boolean isConsoleIconifiedOnClose()
  {
    return false;
  }

  public boolean installBrowserEventListener()
  {
    return false;
  }

  public BrowserAuthenticator getBrowserAuthenticator()
  {
    return new WIExplorerBrowserAuthenticator();
  }

  public String mapBrowserElement(String paramString)
  {
    return paramString;
  }

  public OfflineHandler getOfflineHandler()
  {
    return null;
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.services.AxBridgeBrowserService
 * JD-Core Version:    0.6.2
 */