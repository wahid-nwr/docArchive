package sun.plugin.com;

import java.lang.reflect.Field;
import sun.plugin.util.Trace;

public class PropertySetDispatcher
  implements Dispatcher
{
  private Field field = null;

  public PropertySetDispatcher(Field paramField)
  {
    this.field = paramField;
  }

  public Object invoke(Object paramObject, Object[] paramArrayOfObject)
    throws Exception
  {
    if (this.field != null)
    {
      Trace.msgLiveConnectPrintln("com.field.set", new Object[] { this.field });
      Object localObject = TypeConverter.convertObject(this.field.getType(), paramArrayOfObject[0]);
      this.field.set(paramObject, localObject);
    }
    return null;
  }

  public Class getReturnType()
  {
    return null;
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.com.PropertySetDispatcher
 * JD-Core Version:    0.6.2
 */