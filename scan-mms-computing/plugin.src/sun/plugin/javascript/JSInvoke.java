package sun.plugin.javascript;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class JSInvoke
{
  private static Object invoke(Method paramMethod, Object paramObject, Object[] paramArrayOfObject)
    throws InvocationTargetException, IllegalAccessException
  {
    return paramMethod.invoke(paramObject, paramArrayOfObject);
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.javascript.JSInvoke
 * JD-Core Version:    0.6.2
 */