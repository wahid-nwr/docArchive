package sun.plugin.viewer.context;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import netscape.javascript.JSObject;
import sun.applet.AppletPanel;

public class AxBridgeAppletContext
  implements PluginAppletContext
{
  public AudioClip getAudioClip(URL paramURL)
  {
    return null;
  }

  public Image getImage(URL paramURL)
  {
    return null;
  }

  public Applet getApplet(String paramString)
  {
    return null;
  }

  public Enumeration getApplets()
  {
    return null;
  }

  public void showDocument(URL paramURL)
  {
  }

  public void showDocument(URL paramURL, String paramString)
  {
  }

  public void showStatus(String paramString)
  {
  }

  public void setStream(String paramString, InputStream paramInputStream)
    throws IOException
  {
  }

  public InputStream getStream(String paramString)
  {
    return null;
  }

  public Iterator getStreamKeys()
  {
    return null;
  }

  public JSObject getJSObject()
  {
    return null;
  }

  public void addAppletPanelInContext(AppletPanel paramAppletPanel)
  {
  }

  public void removeAppletPanelFromContext(AppletPanel paramAppletPanel)
  {
  }

  public void setAppletContextHandle(int paramInt)
  {
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.viewer.context.AxBridgeAppletContext
 * JD-Core Version:    0.6.2
 */