package netscape.javascript;

import java.applet.Applet;
import java.applet.AppletContext;
import sun.plugin.javascript.JSContext;

public abstract class JSObject
{
  public abstract Object call(String paramString, Object[] paramArrayOfObject)
    throws JSException;

  public abstract Object eval(String paramString)
    throws JSException;

  public abstract Object getMember(String paramString)
    throws JSException;

  public abstract void setMember(String paramString, Object paramObject)
    throws JSException;

  public abstract void removeMember(String paramString)
    throws JSException;

  public abstract Object getSlot(int paramInt)
    throws JSException;

  public abstract void setSlot(int paramInt, Object paramObject)
    throws JSException;

  public static JSObject getWindow(Applet paramApplet)
    throws JSException
  {
    try
    {
      if (paramApplet != null)
      {
        String str = paramApplet.getParameter("MAYSCRIPT");
        AppletContext localAppletContext = paramApplet.getAppletContext();
        JSObject localJSObject = null;
        if ((localAppletContext instanceof JSContext))
        {
          JSContext localJSContext = (JSContext)localAppletContext;
          localJSObject = localJSContext.getJSObject();
        }
        if (localJSObject != null)
          return localJSObject;
      }
    }
    catch (Throwable localThrowable)
    {
      throw new JSException(6, localThrowable);
    }
    throw new JSException();
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     netscape.javascript.JSObject
 * JD-Core Version:    0.6.2
 */