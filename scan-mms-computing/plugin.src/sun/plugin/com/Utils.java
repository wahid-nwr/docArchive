package sun.plugin.com;

import java.lang.reflect.Array;
import sun.plugin.javascript.ocx.JSObject;

public final class Utils
{
  public static Object convertReturn(Class paramClass, Object paramObject, int paramInt)
  {
    Object localObject1 = paramObject;
    if (localObject1 == null)
    {
      if (paramClass.equals(String.class))
        localObject1 = new NullString();
      else if (paramClass.isArray())
        localObject1 = new Object[0];
    }
    else
    {
      if (!paramClass.isArray())
        return convertRetVal(paramClass, paramObject, paramInt);
      Object localObject2 = paramObject.getClass().getComponentType();
      if (((Class)localObject2).isPrimitive())
        return paramObject;
      int i = Array.getLength(paramObject);
      if (requiresUnWrapping((Class)localObject2))
        localObject2 = DispatchClient.class;
      else
        localObject2 = DispatchImpl.class;
      localObject1 = Array.newInstance((Class)localObject2, i);
      for (int j = 0; j < i; j++)
        Array.set(localObject1, j, convertReturn((Class)localObject2, Array.get(paramObject, j), paramInt));
    }
    return localObject1;
  }

  private static boolean requiresWrapping(Class paramClass)
  {
    return (!Number.class.isAssignableFrom(paramClass)) && (paramClass != String.class) && (paramClass != Character.class) && (paramClass != Boolean.class);
  }

  private static boolean requiresUnWrapping(Class paramClass)
  {
    return JSObject.class.isAssignableFrom(paramClass);
  }

  private static Object convertRetVal(Class paramClass, Object paramObject, int paramInt)
  {
    if ((paramClass.isPrimitive()) || (paramClass == String.class))
      return paramObject;
    if (requiresUnWrapping(paramClass))
      return ((JSObject)paramObject).getDispatchClient();
    return new DispatchImpl(paramObject, paramInt);
  }

  public static Object[] convertArgs(Object[] paramArrayOfObject, int paramInt)
  {
    Object[] arrayOfObject = new Object[paramArrayOfObject.length];
    for (int i = 0; i < paramArrayOfObject.length; i++)
      if (paramArrayOfObject[i] != null)
        arrayOfObject[i] = convertArg(paramArrayOfObject[i], paramInt);
    return arrayOfObject;
  }

  public static Object convertArg(Object paramObject, int paramInt)
  {
    if (requiresUnWrapping(paramObject.getClass()))
      return ((JSObject)paramObject).getDispatchClient();
    if (requiresWrapping(paramObject.getClass()))
    {
      assert (!(paramObject instanceof DispatchImpl));
      return new DispatchImpl(paramObject, paramInt);
    }
    return paramObject;
  }

  public static int getType(Class paramClass)
  {
    if (paramClass == Void.TYPE)
      return 0;
    if (paramClass == Boolean.TYPE)
      return 1;
    if (paramClass == Byte.TYPE)
      return 2;
    if (paramClass == Character.TYPE)
      return 3;
    if (paramClass == Short.TYPE)
      return 4;
    if (paramClass == Integer.TYPE)
      return 5;
    if (paramClass == Long.TYPE)
      return 6;
    if (paramClass == Float.TYPE)
      return 7;
    if (paramClass == Double.TYPE)
      return 8;
    if (paramClass == String.class)
      return 9;
    if (paramClass.isArray())
      return 10;
    return 11;
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.com.Utils
 * JD-Core Version:    0.6.2
 */