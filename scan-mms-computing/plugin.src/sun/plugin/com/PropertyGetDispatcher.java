package sun.plugin.com;

import java.lang.reflect.Field;
import sun.plugin.util.Trace;

public class PropertyGetDispatcher
  implements Dispatcher
{
  private Field field = null;

  public PropertyGetDispatcher(Field paramField)
  {
    this.field = paramField;
  }

  public Object invoke(Object paramObject, Object[] paramArrayOfObject)
    throws Exception
  {
    Object localObject = null;
    if (this.field != null)
    {
      Trace.msgLiveConnectPrintln("com.field.get", new Object[] { this.field });
      localObject = this.field.get(paramObject);
    }
    return localObject;
  }

  public Class getReturnType()
  {
    return this.field.getType();
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.com.PropertyGetDispatcher
 * JD-Core Version:    0.6.2
 */