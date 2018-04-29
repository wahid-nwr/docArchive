package sun.plugin.net.proxy;

import com.sun.deploy.net.proxy.DynamicProxyManager;
import com.sun.java.browser.net.ProxyServiceProvider;
import java.net.URL;
import java.util.List;
import java.util.ListIterator;

public class PluginProxyServiceProvider
  implements ProxyServiceProvider
{
  public com.sun.java.browser.net.ProxyInfo[] getProxyInfo(URL paramURL)
  {
    com.sun.deploy.net.proxy.ProxyInfo localProxyInfo = null;
    List localList = DynamicProxyManager.getProxyInfo(paramURL);
    ListIterator localListIterator = localList.listIterator();
    com.sun.java.browser.net.ProxyInfo[] arrayOfProxyInfo = new com.sun.java.browser.net.ProxyInfo[localList.size()];
    for (int i = 0; localListIterator.hasNext(); i++)
    {
      localProxyInfo = (com.sun.deploy.net.proxy.ProxyInfo)localListIterator.next();
      if (localProxyInfo.isProxyUsed())
      {
        if (localProxyInfo.isSocksUsed())
          arrayOfProxyInfo[i] = new PluginProxyInfo(localProxyInfo.getSocksProxy(), localProxyInfo.getSocksPort(), true);
        else
          arrayOfProxyInfo[i] = new PluginProxyInfo(localProxyInfo.getProxy(), localProxyInfo.getPort(), false);
      }
      else
        arrayOfProxyInfo[i] = null;
    }
    return arrayOfProxyInfo;
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.net.proxy.PluginProxyServiceProvider
 * JD-Core Version:    0.6.2
 */