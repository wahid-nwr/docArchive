package sun.plugin.com;

import java.lang.reflect.Method;
import sun.plugin.javascript.JSClassLoader;
import sun.plugin.util.Trace;

public class MethodDispatcher
  implements Dispatcher
{
  private Method method = null;

  public MethodDispatcher(Method paramMethod)
  {
    this.method = paramMethod;
  }

  public Object invoke(Object paramObject, Object[] paramArrayOfObject)
    throws Exception
  {
    Object localObject = null;
    if ((this.method != null) && (paramObject != null))
    {
      Trace.msgLiveConnectPrintln("com.method.invoke", new Object[] { this.method });
      Class localClass = paramObject.getClass();
      Object[] arrayOfObject = TypeConverter.convertObjectArray(this.method.getParameterTypes(), paramArrayOfObject);
      localObject = JSClassLoader.invoke(this.method, paramObject, arrayOfObject);
    }
    return localObject;
  }

  public Class getReturnType()
  {
    return this.method.getReturnType();
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.com.MethodDispatcher
 * JD-Core Version:    0.6.2
 */