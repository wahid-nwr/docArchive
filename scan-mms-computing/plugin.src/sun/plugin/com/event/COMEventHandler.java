package sun.plugin.com.event;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.lang.reflect.Method;
import sun.plugin.com.BeanClass;
import sun.plugin.com.Utils;

public class COMEventHandler
  implements COMEventListener
{
  private int handle;
  private BeanClass bClass;
  private static final String propertyChange = "propertyChange";
  private static final String vetoableChange = "vetoableChange";

  COMEventHandler(int paramInt, BeanClass paramBeanClass)
  {
    this.handle = paramInt;
    this.bClass = paramBeanClass;
  }

  public void notify(Object paramObject, Method paramMethod)
    throws Throwable
  {
    if (paramMethod.getName().equals("propertyChange"))
      propertyChangeHandler((PropertyChangeEvent)paramObject);
    else if (paramMethod.getName().equals("vetoableChange"))
      vetoableChangeHandler((PropertyChangeEvent)paramObject);
    int i = this.bClass.getEventId(paramMethod.getName());
    if (i != -1)
      notifyEvent(this.handle, i, paramObject, paramMethod);
  }

  private void notifyEvent(int paramInt1, int paramInt2, Object paramObject, Method paramMethod)
  {
    Class[] arrayOfClass = paramMethod.getParameterTypes();
    Object localObject = Utils.convertReturn(arrayOfClass[0], paramObject, paramInt1);
    nativeNotifyEvent(paramInt1, paramInt2, new Object[] { localObject });
  }

  private void propertyChangeHandler(PropertyChangeEvent paramPropertyChangeEvent)
  {
    String str = paramPropertyChangeEvent.getPropertyName();
    int i = this.bClass.getPropertyId(str);
    if (i != -1)
      nativeNotifyProperty(this.handle, i);
  }

  private void vetoableChangeHandler(PropertyChangeEvent paramPropertyChangeEvent)
    throws PropertyVetoException
  {
    String str = paramPropertyChangeEvent.getPropertyName();
    int i = 1;
    int j = this.bClass.getPropertyId(str);
    if ((j != -1) && (!nativeNotifyVetoable(this.handle, j)))
      throw new PropertyVetoException(str, paramPropertyChangeEvent);
  }

  private native boolean nativeNotifyVetoable(int paramInt1, int paramInt2);

  private native void nativeNotifyProperty(int paramInt1, int paramInt2);

  private native void nativeNotifyEvent(int paramInt1, int paramInt2, Object[] paramArrayOfObject);
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.com.event.COMEventHandler
 * JD-Core Version:    0.6.2
 */