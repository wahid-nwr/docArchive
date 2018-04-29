package sun.plugin.viewer.frame;

import sun.plugin.util.Trace;
import sun.plugin.viewer.IExplorerPluginObject;

public class AxBridgeEmbeddedFrame extends IExplorerEmbeddedFrame
{
  public AxBridgeEmbeddedFrame(int paramInt, IExplorerPluginObject paramIExplorerPluginObject)
  {
    super(paramInt, paramIExplorerPluginObject);
  }

  public void registerFocusListener()
  {
    registerListeners();
  }

  protected boolean traverseOut(boolean paramBoolean)
  {
    Trace.println("Giving focus to next control");
    transferFocus(this.handle, paramBoolean);
    return true;
  }

  private native void transferFocus(int paramInt, boolean paramBoolean);
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.viewer.frame.AxBridgeEmbeddedFrame
 * JD-Core Version:    0.6.2
 */