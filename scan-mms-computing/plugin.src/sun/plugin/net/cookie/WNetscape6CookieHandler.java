package sun.plugin.net.cookie;

import com.sun.deploy.net.cookie.CookieHandler;
import com.sun.deploy.net.cookie.CookieUnavailableException;
import java.net.URL;
import sun.plugin.viewer.AppletPanelCache;

public final class WNetscape6CookieHandler
  implements CookieHandler
{
  public void setCookieInfo(URL paramURL, String paramString)
    throws CookieUnavailableException
  {
    if (!AppletPanelCache.hasValidInstance())
      throw new CookieUnavailableException("Cookie service is not available for " + paramURL);
    nativeSetCookieInfo(paramURL.toString(), paramString);
  }

  public String getCookieInfo(URL paramURL)
    throws CookieUnavailableException
  {
    if (!AppletPanelCache.hasValidInstance())
      throw new CookieUnavailableException("Cookie service is not available for " + paramURL);
    return nativeGetCookieInfo(paramURL.toString());
  }

  public native void nativeSetCookieInfo(String paramString1, String paramString2);

  public native String nativeGetCookieInfo(String paramString);
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.net.cookie.WNetscape6CookieHandler
 * JD-Core Version:    0.6.2
 */