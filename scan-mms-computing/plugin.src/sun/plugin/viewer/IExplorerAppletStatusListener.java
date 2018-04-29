package sun.plugin.viewer;

import com.sun.deploy.util.DeployAWTUtil;
import sun.plugin.AppletStatusListener;
import sun.plugin.viewer.frame.IExplorerEmbeddedFrame;

public class IExplorerAppletStatusListener
  implements AppletStatusListener
{
  private int handle = 0;
  protected IExplorerEmbeddedFrame frame = null;

  IExplorerAppletStatusListener(int paramInt)
  {
    this.handle = paramInt;
  }

  public void statusChanged(int paramInt)
  {
    if (paramInt == 3)
    {
      final IExplorerEmbeddedFrame localIExplorerEmbeddedFrame = this.frame;
      try
      {
        DeployAWTUtil.invokeLater(localIExplorerEmbeddedFrame, new Runnable()
        {
          public void run()
          {
            localIExplorerEmbeddedFrame.registerFocusListener();
            localIExplorerEmbeddedFrame.synthesizeWindowActivation(true);
          }
        });
      }
      catch (Exception localException)
      {
        localException.printStackTrace();
      }
    }
    notifyStatusChange(this.handle, paramInt);
  }

  public void setEmbeddedFrame(IExplorerEmbeddedFrame paramIExplorerEmbeddedFrame)
  {
    this.frame = paramIExplorerEmbeddedFrame;
  }

  private native void notifyStatusChange(int paramInt1, int paramInt2);
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.viewer.IExplorerAppletStatusListener
 * JD-Core Version:    0.6.2
 */