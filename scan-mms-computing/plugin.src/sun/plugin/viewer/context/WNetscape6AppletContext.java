package sun.plugin.viewer.context;

import java.net.URL;

public class WNetscape6AppletContext extends NetscapeAppletContext
{
  public void doShowDocument(URL paramURL, String paramString)
  {
    if (this.instance >= 0)
      nativeShowDocument(this.instance, paramURL.toString(), paramString);
  }

  public void doShowStatus(String paramString)
  {
    if (this.instance >= 0)
      nativeShowStatus(this.instance, paramString);
  }

  private native void nativeShowDocument(int paramInt, String paramString1, String paramString2);

  private native void nativeShowStatus(int paramInt, String paramString);
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.viewer.context.WNetscape6AppletContext
 * JD-Core Version:    0.6.2
 */