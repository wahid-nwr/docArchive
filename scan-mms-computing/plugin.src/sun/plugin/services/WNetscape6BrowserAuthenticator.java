package sun.plugin.services;

import com.sun.deploy.security.AbstractBrowserAuthenticator;
import java.net.PasswordAuthentication;
import java.net.URL;

public final class WNetscape6BrowserAuthenticator extends AbstractBrowserAuthenticator
{
  public PasswordAuthentication getAuthentication(String paramString1, String paramString2, int paramInt, String paramString3, String paramString4, URL paramURL, boolean paramBoolean)
  {
    return getPAFromCharArray(getBrowserAuthentication(paramString1, paramString2, paramInt, paramString3, paramString4));
  }

  private native char[] getBrowserAuthentication(String paramString1, String paramString2, int paramInt, String paramString3, String paramString4);
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.services.WNetscape6BrowserAuthenticator
 * JD-Core Version:    0.6.2
 */