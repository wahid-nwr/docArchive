package sun.plugin.com;

public abstract interface Dispatcher
{
  public abstract Object invoke(Object paramObject, Object[] paramArrayOfObject)
    throws Exception;

  public abstract Class getReturnType();
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.com.Dispatcher
 * JD-Core Version:    0.6.2
 */