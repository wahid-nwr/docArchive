package sun.plugin.viewer.context;

import java.net.URL;
import java.util.ArrayList;
import sun.plugin.AppletViewer;

public class IExplorerAppletContext extends DefaultPluginAppletContext
{
  private int handle = 0;
  private ArrayList locked = new ArrayList();

  public void setAppletContextHandle(int paramInt)
  {
    this.handle = paramInt;
  }

  public int getAppletContextHandle()
  {
    return this.handle;
  }

  public void doShowDocument(URL paramURL, String paramString)
  {
    if (this.handle > 0)
    {
      Object localObject;
      String str1;
      if (paramURL.getProtocol().equals("javascript"))
      {
        localObject = new StringBuffer();
        str1 = paramURL.toString();
        for (int i = 0; i < str1.length(); i++)
        {
          char c = str1.charAt(i);
          if ((c == '\'') || (c == '"') || (c == '\\'))
            ((StringBuffer)localObject).append('\\');
          ((StringBuffer)localObject).append(c);
        }
        String str2 = "javascript:window.open('" + localObject + "', '" + paramString + "')";
        nativeInvokeScript(this.handle, 1, "execScript", new Object[] { str2 });
      }
      else
      {
        localObject = new Object[2];
        str1 = paramURL.toString();
        if ((str1.startsWith("file:/")) && (str1.length() > 6) && (str1.charAt(6) != '/'))
          str1 = "file:///" + str1.substring(6);
        localObject[0] = str1;
        localObject[1] = paramString;
        nativeInvokeScript(this.handle, 1, "open", (Object[])localObject);
      }
    }
  }

  public void doShowStatus(String paramString)
  {
    boolean bool = ((AppletViewer)this.appletPanel).isStopped();
    if ((this.handle > 0) && (!bool))
    {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramString;
      nativeInvokeScript(this.handle, 4, "status", arrayOfObject);
    }
  }

  private native void nativeInvokeScript(int paramInt1, int paramInt2, String paramString, Object[] paramArrayOfObject);

  public void onClose()
  {
    super.onClose();
    this.handle = 0;
  }

  public synchronized netscape.javascript.JSObject getJSObject()
  {
    sun.plugin.javascript.ocx.JSObject localJSObject = getJSObject(this.handle);
    localJSObject.setIExplorerAppletContext(this);
    return localJSObject;
  }

  private native sun.plugin.javascript.ocx.JSObject getJSObject(int paramInt);

  public void addJSObjectToLockedList(netscape.javascript.JSObject paramJSObject)
  {
    synchronized (this.locked)
    {
      this.locked.add(paramJSObject);
    }
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.viewer.context.IExplorerAppletContext
 * JD-Core Version:    0.6.2
 */