package sun.plugin.viewer.context;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.lang.reflect.InvocationTargetException;
import java.net.SocketPermission;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import sun.applet.AppletPanel;
import sun.plugin.util.Trace;
import sun.plugin.viewer.AppletPanelCache;

public abstract class DefaultPluginAppletContext
  implements PluginAppletContext
{
  private ArrayList exported = new ArrayList();
  protected AppletPanel appletPanel;
  private static HashMap imageRefs = new HashMap();
  private static HashMap audioClipStore = new HashMap();
  private int persistStreamMaxSize = 65536;
  private static HashMap streamStore = new HashMap();

  public AudioClip getAudioClip(URL paramURL)
  {
    if (paramURL == null)
      return null;
    System.getSecurityManager().checkConnect(paramURL.getHost(), paramURL.getPort());
    SoftReference localSoftReference = null;
    synchronized (audioClipStore)
    {
      HashMap localHashMap = (HashMap)audioClipStore.get(this.appletPanel.getCodeBase());
      if (localHashMap == null)
      {
        localHashMap = new HashMap();
        audioClipStore.put(this.appletPanel.getCodeBase(), localHashMap);
      }
      localSoftReference = (SoftReference)localHashMap.get(paramURL);
      if ((localSoftReference == null) || (localSoftReference.get() == null))
      {
        localSoftReference = new SoftReference(AppletAudioClipFactory.createAudioClip(paramURL));
        localHashMap.put(paramURL, localSoftReference);
      }
    }
    ??? = (AudioClip)localSoftReference.get();
    Trace.msgPrintln("appletcontext.audio.loaded", new Object[] { paramURL });
    return ???;
  }

  public Image getImage(URL paramURL)
  {
    if (paramURL == null)
      return null;
    System.getSecurityManager().checkConnect(paramURL.getHost(), paramURL.getPort());
    SoftReference localSoftReference = null;
    synchronized (imageRefs)
    {
      localSoftReference = (SoftReference)imageRefs.get(paramURL);
      if ((localSoftReference == null) || (localSoftReference.get() == null))
      {
        localSoftReference = new SoftReference(AppletImageFactory.createImage(paramURL));
        imageRefs.put(paramURL, localSoftReference);
      }
    }
    ??? = (Image)localSoftReference.get();
    Trace.msgPrintln("appletcontext.image.loaded", new Object[] { paramURL });
    return ???;
  }

  public Applet getApplet(String paramString)
  {
    paramString = paramString.toLowerCase();
    Object[] arrayOfObject = AppletPanelCache.getAppletPanels();
    for (int i = 0; i < arrayOfObject.length; i++)
    {
      AppletPanel localAppletPanel = (AppletPanel)arrayOfObject[i];
      if ((localAppletPanel != null) && ((localAppletPanel.isActive()) || (this.appletPanel == localAppletPanel)))
      {
        String str = localAppletPanel.getParameter("name");
        if (str != null)
          str = str.toLowerCase();
        if ((paramString.equals(str)) && (localAppletPanel.getDocumentBase().equals(this.appletPanel.getDocumentBase())))
        {
          try
          {
            if (!checkConnect(this.appletPanel.getCodeBase().getHost(), localAppletPanel.getCodeBase().getHost()))
              return null;
          }
          catch (InvocationTargetException localInvocationTargetException)
          {
            showStatus(localInvocationTargetException.getTargetException().getMessage());
            return null;
          }
          catch (Exception localException)
          {
            showStatus(localException.getMessage());
            return null;
          }
          return localAppletPanel.getApplet();
        }
      }
    }
    return null;
  }

  public Enumeration getApplets()
  {
    Vector localVector = new Vector();
    Object[] arrayOfObject = AppletPanelCache.getAppletPanels();
    for (int i = 0; i < arrayOfObject.length; i++)
    {
      AppletPanel localAppletPanel = (AppletPanel)arrayOfObject[i];
      if ((localAppletPanel != null) && (localAppletPanel.isActive()) && (localAppletPanel.getDocumentBase().equals(this.appletPanel.getDocumentBase())))
        try
        {
          if (checkConnect(this.appletPanel.getCodeBase().getHost(), localAppletPanel.getCodeBase().getHost()))
            localVector.addElement(localAppletPanel.getApplet());
        }
        catch (InvocationTargetException localInvocationTargetException)
        {
          showStatus(localInvocationTargetException.getTargetException().getMessage());
        }
        catch (Exception localException)
        {
          showStatus(localException.getMessage());
        }
    }
    Applet localApplet = this.appletPanel.getApplet();
    if (!localVector.contains(localApplet))
      localVector.addElement(localApplet);
    return localVector.elements();
  }

  private boolean checkConnect(String paramString1, String paramString2)
    throws Exception
  {
    SocketPermission localSocketPermission1 = new SocketPermission(paramString1, "connect");
    SocketPermission localSocketPermission2 = new SocketPermission(paramString2, "connect");
    return localSocketPermission1.implies(localSocketPermission2);
  }

  public void showDocument(URL paramURL)
  {
    showDocument(paramURL, "_top");
  }

  public void showDocument(URL paramURL, String paramString)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 0; i < paramString.length(); i++)
    {
      char c = paramString.charAt(i);
      if ((Character.isLetterOrDigit(c)) || (c == '_'))
        localStringBuffer.append(c);
      else
        localStringBuffer.append('_');
    }
    doShowDocument(paramURL, localStringBuffer.toString());
  }

  public void doShowDocument(final URL paramURL, final String paramString)
  {
    DefaultPluginAppletContext localDefaultPluginAppletContext = this;
    Thread localThread = new Thread(new Runnable()
    {
      public void run()
      {
        try
        {
          netscape.javascript.JSObject localJSObject = DefaultPluginAppletContext.this.getJSObject();
          if (localJSObject != null)
          {
            Object[] arrayOfObject = new Object[2];
            arrayOfObject[0] = paramURL.toString();
            arrayOfObject[1] = paramString;
            localJSObject.call("open", arrayOfObject);
          }
        }
        catch (Throwable localThrowable)
        {
          localThrowable.printStackTrace();
        }
      }
    }
    , "showDocument Thread");
    localThread.start();
  }

  public void showStatus(String paramString)
  {
    if (paramString == null)
      return;
    int i = paramString.indexOf("\n");
    if (i != -1)
      doShowStatus(paramString.substring(0, i));
    else
      doShowStatus(paramString);
  }

  protected void doShowStatus(final String paramString)
  {
    final DefaultPluginAppletContext localDefaultPluginAppletContext = this;
    Thread localThread = new Thread(new Runnable()
    {
      public void run()
      {
        try
        {
          netscape.javascript.JSObject localJSObject = localDefaultPluginAppletContext.getJSObject();
          if (localJSObject != null)
            localJSObject.eval("function setStatus() { self.status='" + paramString + "';};" + "void(setTimeout(\"setStatus()\", 1500))");
        }
        catch (Throwable localThrowable)
        {
          localThrowable.printStackTrace();
        }
      }
    }
    , "showStatus Thread");
    localThread.start();
  }

  public void addAppletPanelInContext(AppletPanel paramAppletPanel)
  {
    this.appletPanel = paramAppletPanel;
    AppletPanelCache.add(paramAppletPanel);
  }

  public void removeAppletPanelFromContext(AppletPanel paramAppletPanel)
  {
    AppletPanelCache.remove(paramAppletPanel);
  }

  public void setStream(String paramString, InputStream paramInputStream)
    throws IOException
  {
    HashMap localHashMap = (HashMap)streamStore.get(this.appletPanel.getCodeBase());
    if (localHashMap == null)
    {
      localHashMap = new HashMap();
      streamStore.put(this.appletPanel.getCodeBase(), localHashMap);
    }
    synchronized (localHashMap)
    {
      if (paramInputStream != null)
      {
        byte[] arrayOfByte = (byte[])localHashMap.get(paramString);
        if (arrayOfByte == null)
        {
          int i = paramInputStream.available();
          if (i < this.persistStreamMaxSize)
          {
            arrayOfByte = new byte[i];
            paramInputStream.read(arrayOfByte, 0, i);
            localHashMap.put(paramString, arrayOfByte);
          }
          else
          {
            throw new IOException("Stream size exceeds the maximum limit");
          }
        }
        else
        {
          localHashMap.remove(paramString);
          setStream(paramString, paramInputStream);
        }
      }
      else
      {
        localHashMap.remove(paramString);
      }
    }
  }

  public InputStream getStream(String paramString)
  {
    ByteArrayInputStream localByteArrayInputStream = null;
    HashMap localHashMap = (HashMap)streamStore.get(this.appletPanel.getCodeBase());
    if (localHashMap != null)
      synchronized (localHashMap)
      {
        byte[] arrayOfByte = (byte[])localHashMap.get(paramString);
        if (arrayOfByte != null)
          localByteArrayInputStream = new ByteArrayInputStream(arrayOfByte);
      }
    return localByteArrayInputStream;
  }

  public Iterator getStreamKeys()
  {
    Iterator localIterator = null;
    HashMap localHashMap = (HashMap)streamStore.get(this.appletPanel.getCodeBase());
    if (localHashMap != null)
      synchronized (localHashMap)
      {
        localIterator = localHashMap.keySet().iterator();
      }
    return localIterator;
  }

  private String getNameFromURL(URL paramURL)
  {
    try
    {
      String str = paramURL.getFile();
      int i = str.lastIndexOf('/');
      return str.substring(i + 1);
    }
    catch (Throwable localThrowable)
    {
    }
    return null;
  }

  public void addJSObjectToExportedList(netscape.javascript.JSObject paramJSObject)
  {
    synchronized (this.exported)
    {
      this.exported.add(new SoftReference(paramJSObject));
    }
  }

  public void onClose()
  {
    synchronized (this.exported)
    {
      Iterator localIterator = this.exported.iterator();
      while (localIterator.hasNext())
      {
        SoftReference localSoftReference = (SoftReference)localIterator.next();
        if (localSoftReference != null)
        {
          netscape.javascript.JSObject localJSObject = (netscape.javascript.JSObject)localSoftReference.get();
          if ((localJSObject != null) && ((localJSObject instanceof sun.plugin.javascript.JSObject)))
            ((sun.plugin.javascript.JSObject)localJSObject).cleanup();
        }
      }
      this.exported.clear();
    }
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.viewer.context.DefaultPluginAppletContext
 * JD-Core Version:    0.6.2
 */