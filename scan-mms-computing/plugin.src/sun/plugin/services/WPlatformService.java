package sun.plugin.services;

public final class WPlatformService extends PlatformService
{
  public void waitEvent(int paramInt)
  {
    waitEvent(0, paramInt);
  }

  public native void signalEvent(int paramInt);

  public native void waitEvent(int paramInt1, int paramInt2);

  public native void dispatchNativeEvent();
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.services.WPlatformService
 * JD-Core Version:    0.6.2
 */