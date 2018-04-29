package sun.plugin.com.event;

import java.lang.reflect.Method;

public abstract interface COMEventListener
{
  public abstract void notify(Object paramObject, Method paramMethod)
    throws Throwable;
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.com.event.COMEventListener
 * JD-Core Version:    0.6.2
 */