package sun.plugin.com;

import java.lang.reflect.Method;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;

final class WrapperJSObject extends JSObject
{
  private Object realObject;

  public WrapperJSObject(Object paramObject)
  {
    this.realObject = paramObject;
  }

  public String toString()
  {
    return this.realObject.toString();
  }

  public int hashCode()
  {
    return this.realObject.hashCode();
  }

  public boolean equals(Object paramObject)
  {
    if (paramObject == null)
      return false;
    if ((paramObject instanceof WrapperJSObject))
      return ((WrapperJSObject)paramObject).realObject.equals(this.realObject);
    return paramObject.equals(this.realObject);
  }

  public Object call(String paramString, Object[] paramArrayOfObject)
    throws JSException
  {
    Class[] arrayOfClass = null;
    if (paramArrayOfObject != null)
    {
      arrayOfClass = new Class[paramArrayOfObject.length];
      for (int i = 0; i < paramArrayOfObject.length; i++)
        arrayOfClass[i] = paramArrayOfObject[i].getClass();
    }
    try
    {
      Method localMethod = this.realObject.getClass().getDeclaredMethod(paramString, arrayOfClass);
      return localMethod.invoke(this.realObject, paramArrayOfObject);
    }
    catch (Exception localException)
    {
      throw new JSException(-1, localException);
    }
  }

  public Object eval(String paramString)
    throws JSException
  {
    return call("eval", new Object[] { paramString });
  }

  public Object getMember(String paramString)
    throws JSException
  {
    return call("getMember", new Object[] { paramString });
  }

  public void setMember(String paramString, Object paramObject)
    throws JSException
  {
    call("setMember", new Object[] { paramString, paramObject });
  }

  public void removeMember(String paramString)
    throws JSException
  {
    call("removeMember", new Object[] { paramString });
  }

  public Object getSlot(int paramInt)
    throws JSException
  {
    return call("getSlot", new Object[] { new Integer(paramInt) });
  }

  public void setSlot(int paramInt, Object paramObject)
    throws JSException
  {
    call("setSlot", new Object[] { new Integer(paramInt), paramObject });
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.com.WrapperJSObject
 * JD-Core Version:    0.6.2
 */