package sun.plugin.net.proxy;

import com.sun.deploy.net.proxy.AbstractBrowserProxyHandler;

public final class WNetscape6BrowserProxyHandler extends AbstractBrowserProxyHandler
{
  protected native String findProxyForURL(String paramString);
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.net.proxy.WNetscape6BrowserProxyHandler
 * JD-Core Version:    0.6.2
 */