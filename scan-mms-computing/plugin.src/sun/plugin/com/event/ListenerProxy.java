package sun.plugin.com.event;

import java.beans.BeanInfo;
import java.beans.EventSetDescriptor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import sun.plugin.com.BeanClass;
import sun.plugin.com.DispatchImpl;

public class ListenerProxy
  implements InvocationHandler
{
  private COMEventHandler handler;
  private Object proxy;
  private DispatchImpl dispImpl;
  private boolean registerStatus = false;
  private Class[] lClasses = null;

  public ListenerProxy(int paramInt, DispatchImpl paramDispatchImpl)
  {
    this.dispImpl = paramDispatchImpl;
    BeanClass localBeanClass = (BeanClass)this.dispImpl.getTargetClass();
    this.handler = new COMEventHandler(paramInt, localBeanClass);
  }

  public Object invoke(Object paramObject, Method paramMethod, Object[] paramArrayOfObject)
    throws Throwable
  {
    if (this.lClasses == null)
      return null;
    int i = 0;
    for (int j = 0; j < this.lClasses.length; j++)
      if (paramMethod.getDeclaringClass() == this.lClasses[j])
      {
        i = 1;
        break;
      }
    if ((this.handler != null) && (i != 0))
    {
      this.handler.notify(paramArrayOfObject[0], paramMethod);
      return null;
    }
    return paramMethod.invoke(this.dispImpl, paramArrayOfObject);
  }

  public void unregister()
  {
    synchronized (this)
    {
      if (!this.registerStatus)
        return;
      try
      {
        Object localObject1 = this.dispImpl.getWrappedObject();
        BeanClass localBeanClass = (BeanClass)this.dispImpl.getTargetClass();
        BeanInfo localBeanInfo = localBeanClass.getBeanInfo();
        EventSetDescriptor[] arrayOfEventSetDescriptor = localBeanInfo.getEventSetDescriptors();
        Method[] arrayOfMethod = new Method[arrayOfEventSetDescriptor.length];
        Object[] arrayOfObject = { this.proxy };
        for (int i = 0; i < arrayOfEventSetDescriptor.length; i++)
        {
          arrayOfMethod[i] = arrayOfEventSetDescriptor[i].getRemoveListenerMethod();
          arrayOfMethod[i].invoke(localObject1, arrayOfObject);
        }
        this.proxy = null;
      }
      catch (Throwable localThrowable)
      {
        localThrowable.printStackTrace();
      }
      this.registerStatus = false;
    }
  }

  public void register()
  {
    synchronized (this)
    {
      if (this.registerStatus)
        return;
      try
      {
        Object localObject1 = this.dispImpl.getWrappedObject();
        BeanInfo localBeanInfo = ((BeanClass)this.dispImpl.getTargetClass()).getBeanInfo();
        EventSetDescriptor[] arrayOfEventSetDescriptor = localBeanInfo.getEventSetDescriptors();
        this.lClasses = new Class[arrayOfEventSetDescriptor.length];
        Method[] arrayOfMethod = new Method[arrayOfEventSetDescriptor.length];
        for (int i = 0; i < arrayOfEventSetDescriptor.length; i++)
        {
          this.lClasses[i] = arrayOfEventSetDescriptor[i].getListenerType();
          arrayOfMethod[i] = arrayOfEventSetDescriptor[i].getAddListenerMethod();
        }
        ClassLoader localClassLoader = localObject1.getClass().getClassLoader();
        this.proxy = Proxy.newProxyInstance(localClassLoader, this.lClasses, this);
        Object[] arrayOfObject = { this.proxy };
        for (int j = 0; j < arrayOfEventSetDescriptor.length; j++)
          arrayOfMethod[j].invoke(localObject1, arrayOfObject);
      }
      catch (Throwable localThrowable)
      {
        localThrowable.printStackTrace();
      }
      this.registerStatus = true;
    }
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.com.event.ListenerProxy
 * JD-Core Version:    0.6.2
 */