package sun.plugin.javascript.ocx;

import netscape.javascript.JSException;
import sun.plugin.com.DispatchClient;
import sun.plugin.util.Trace;
import sun.plugin.viewer.context.IExplorerAppletContext;

public class JSObject extends sun.plugin.javascript.JSObject
{
  public static final int DISPATCH_METHOD = 1;
  public static final int DISPATCH_PROPERTYGET = 2;
  public static final int DISPATCH_PROPERTYPUT = 4;
  private DispatchClient dispClient = null;
  private int handle = 0;
  private String infoString = null;
  private IExplorerAppletContext aac;
  private boolean lockThis = false;
  private boolean released = false;

  public JSObject(DispatchClient paramDispatchClient)
  {
    this.dispClient = paramDispatchClient;
  }

  public JSObject(DispatchClient paramDispatchClient, int paramInt)
  {
    this.dispClient = paramDispatchClient;
    this.handle = paramInt;
  }

  public void setIExplorerAppletContext(IExplorerAppletContext paramIExplorerAppletContext)
  {
    this.aac = paramIExplorerAppletContext;
    this.handle = paramIExplorerAppletContext.getAppletContextHandle();
    paramIExplorerAppletContext.addJSObjectToExportedList(this);
    if (this.lockThis)
      paramIExplorerAppletContext.addJSObjectToLockedList(this);
    this.aac = paramIExplorerAppletContext;
  }

  public void finalize()
  {
    cleanup();
  }

  public synchronized void cleanup()
  {
    if (!this.released)
    {
      this.dispClient.release(this.handle);
      this.released = true;
    }
  }

  public synchronized Object invoke(String paramString, Object[] paramArrayOfObject, int paramInt)
    throws JSException
  {
    checkValidity();
    Trace.msgLiveConnectPrintln("com.method.jsinvoke", new Object[] { paramString });
    Object localObject = this.dispClient.invoke(this.handle, paramString, paramInt, paramArrayOfObject);
    if ((localObject != null) && ((localObject instanceof JSObject)))
      ((JSObject)localObject).setIExplorerAppletContext(this.aac);
    return localObject;
  }

  public Object call(String paramString, Object[] paramArrayOfObject)
    throws JSException
  {
    return invoke(paramString, paramArrayOfObject, 1);
  }

  public synchronized Object eval(String paramString)
    throws JSException
  {
    Object[] arrayOfObject = new Object[1];
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 0; i < paramString.length(); i++)
    {
      char c = paramString.charAt(i);
      if ((c == '\'') || (c == '"') || (c == '\\'))
        localStringBuffer.append('\\');
      localStringBuffer.append(c);
    }
    arrayOfObject[0] = ("evalIntermediateValueToReturn=0;evalIntermediateValueToReturn=eval('" + localStringBuffer.toString() + "');");
    Object localObject = this;
    String str = toString();
    if (str.indexOf("Window") == -1)
      localObject = this.aac.getJSObject();
    try
    {
      ((netscape.javascript.JSObject)localObject).call("execScript", arrayOfObject);
    }
    catch (JSException localJSException1)
    {
      throw new JSException("Failure to evaluate " + paramString);
    }
    try
    {
      return ((netscape.javascript.JSObject)localObject).getMember("evalIntermediateValueToReturn");
    }
    catch (JSException localJSException2)
    {
    }
    return null;
  }

  public Object getMember(String paramString)
    throws JSException
  {
    return invoke(paramString, null, 2);
  }

  public void setMember(String paramString, Object paramObject)
    throws JSException
  {
    Object[] arrayOfObject = { paramObject };
    invoke(paramString, arrayOfObject, 4);
  }

  public void removeMember(String paramString)
    throws JSException
  {
    throw new JSException("removeMember does not support " + toString() + "." + paramString);
  }

  public Object getSlot(int paramInt)
    throws JSException
  {
    Object[] arrayOfObject = new Object[2];
    arrayOfObject[0] = new Integer(paramInt);
    arrayOfObject[1] = new Integer(paramInt);
    return invoke("item", arrayOfObject, 1);
  }

  public void setSlot(int paramInt, Object paramObject)
    throws JSException
  {
    Object[] arrayOfObject = new Object[2];
    arrayOfObject[0] = new Integer(paramInt);
    arrayOfObject[1] = paramObject;
    invoke("add", arrayOfObject, 1);
  }

  public String toString()
  {
    if (this.infoString == null)
    {
      this.infoString = this.dispClient.getDispType(this.handle);
      if (this.infoString == null)
        this.infoString = objectToString();
      else
        this.infoString = ("[object " + this.infoString + "]");
    }
    return this.infoString;
  }

  private String objectToString()
  {
    Object localObject = null;
    String str = null;
    try
    {
      localObject = invoke("toString", null, 1);
    }
    catch (JSException localJSException)
    {
      localObject = "[object JSObject]";
    }
    if (localObject != null)
      str = localObject.toString();
    return str;
  }

  private void checkValidity()
    throws JSException
  {
    if (this.released)
      throw new JSException("Native DOM object has been released");
  }

  public DispatchClient getDispatchClient()
  {
    return this.dispClient;
  }

  public void lock()
  {
    if (this.aac == null)
      this.lockThis = true;
    else
      this.aac.addJSObjectToLockedList(this);
  }

  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof JSObject))
      return false;
    JSObject localJSObject = (JSObject)paramObject;
    if (localJSObject.handle != this.handle)
      return false;
    return this.dispClient.equals(this.handle, localJSObject.dispClient);
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.javascript.ocx.JSObject
 * JD-Core Version:    0.6.2
 */