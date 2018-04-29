package sun.plugin.com;

import java.lang.reflect.Array;
import java.text.NumberFormat;
import java.text.ParseException;
import netscape.javascript.JSObject;
import sun.plugin.resources.ResourceHandler;
import sun.plugin.util.Trace;

public class TypeConverter
{
  static Object[] convertObjectArray(Class[] paramArrayOfClass, Object[] paramArrayOfObject)
    throws IllegalArgumentException
  {
    if ((paramArrayOfObject == null) || (paramArrayOfObject.length == 0))
      return paramArrayOfObject;
    if (paramArrayOfClass.length != paramArrayOfObject.length)
      throw new IllegalArgumentException(ResourceHandler.getMessage("com.method.argCountInvalid"));
    Object[] arrayOfObject = new Object[paramArrayOfObject.length];
    for (int i = 0; i < paramArrayOfObject.length; i++)
      arrayOfObject[i] = convertObject(paramArrayOfClass[i], paramArrayOfObject[i]);
    return arrayOfObject;
  }

  public static Object convertObject(Class paramClass, Object paramObject)
    throws IllegalArgumentException
  {
    if (paramObject == null)
      return paramObject;
    Class localClass = paramObject.getClass();
    Trace.msgLiveConnectPrintln("com.field.needsConversion", new Object[] { localClass.getName(), paramClass.getName() });
    if (paramClass.isAssignableFrom(localClass))
      return paramObject;
    if (paramClass.equals(JSObject.class))
      return new WrapperJSObject(paramObject);
    if ((paramObject instanceof DispatchImpl))
      return ((DispatchImpl)paramObject).getWrappedObject();
    Object localObject1;
    if (paramClass == String.class)
    {
      if ((paramObject instanceof Number))
      {
        localObject1 = NumberFormat.getNumberInstance();
        try
        {
          return ((NumberFormat)localObject1).parse(paramObject.toString()).toString();
        }
        catch (ParseException localParseException)
        {
        }
      }
      return paramObject.toString();
    }
    if (paramClass.isArray())
    {
      if (localClass.isArray())
      {
        localObject1 = paramClass.getComponentType();
        int i = Array.getLength(paramObject);
        Object localObject2 = Array.newInstance((Class)localObject1, i);
        for (int j = 0; j < i; j++)
          Array.set(localObject2, j, convertObject((Class)localObject1, Array.get(paramObject, j)));
        return localObject2;
      }
    }
    else if ((paramClass.isPrimitive()) || (Number.class.isAssignableFrom(paramClass)) || (paramClass == Character.class) || (paramClass == Boolean.class))
    {
      localObject1 = paramClass.getName();
      boolean bool1 = paramObject instanceof Number;
      boolean bool2 = paramObject instanceof String;
      if ((((String)localObject1).equals("boolean")) || (((String)localObject1).equals("java.lang.Boolean")))
        return new Boolean(paramObject.toString());
      if ((((String)localObject1).equals("byte")) || (((String)localObject1).equals("java.lang.Byte")))
      {
        if (bool2)
          return Byte.valueOf((String)paramObject);
        if (bool1)
          return new Byte(((Number)paramObject).byteValue());
      }
      else if ((((String)localObject1).equals("short")) || (((String)localObject1).equals("java.lang.Short")))
      {
        if (bool2)
          return Short.valueOf((String)paramObject);
        if (bool1)
          return new Short(((Number)paramObject).shortValue());
      }
      else if ((((String)localObject1).equals("int")) || (((String)localObject1).equals("java.lang.Integer")))
      {
        if (bool2)
          return Integer.valueOf((String)paramObject);
        if (bool1)
          return new Integer(((Number)paramObject).intValue());
      }
      else if ((((String)localObject1).equals("long")) || (((String)localObject1).equals("java.lang.Long")))
      {
        if (bool2)
          return Long.valueOf((String)paramObject);
        if (bool1)
          return new Long(((Number)paramObject).longValue());
      }
      else if ((((String)localObject1).equals("float")) || (((String)localObject1).equals("java.lang.Float")))
      {
        if (bool2)
          return Float.valueOf((String)paramObject);
        if (bool1)
          return new Float(((Number)paramObject).floatValue());
      }
      else if ((((String)localObject1).equals("double")) || (((String)localObject1).equals("java.lang.Double")))
      {
        if (bool2)
          return Double.valueOf((String)paramObject);
        if (bool1)
          return new Double(((Number)paramObject).doubleValue());
      }
      else if ((((String)localObject1).equals("char")) || (((String)localObject1).equals("java.lang.Character")))
      {
        if (bool2)
          return new Character((char)Short.decode((String)paramObject).shortValue());
        if (bool1)
          return new Character((char)((Number)paramObject).shortValue());
      }
      else
      {
        return paramObject;
      }
    }
    throw new IllegalArgumentException(localClass.getName() + ResourceHandler.getMessage("com.field.typeInvalid") + paramClass.getName());
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.com.TypeConverter
 * JD-Core Version:    0.6.2
 */