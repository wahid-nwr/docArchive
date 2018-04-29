package sun.plugin.viewer.frame;

import com.sun.deploy.util.DeployAWTUtil;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import sun.awt.windows.WComponentPeer;
import sun.awt.windows.WEmbeddedFrame;
import sun.plugin.services.PlatformService;
import sun.plugin.util.Trace;
import sun.plugin.viewer.WNetscapePluginObject;

public class WNetscapeEmbeddedFrame extends WEmbeddedFrame
  implements WindowListener
{
  private int win_handle;
  private WNetscapePluginObject obj = null;
  private int handle;

  public WNetscapeEmbeddedFrame(int paramInt)
  {
    super(paramInt);
    this.win_handle = paramInt;
    setLayout(new BorderLayout());
    setBackground(Color.white);
    addWindowListener(this);
  }

  public void setJavaObject(WNetscapePluginObject paramWNetscapePluginObject)
  {
    this.obj = paramWNetscapePluginObject;
  }

  public void windowActivated(WindowEvent paramWindowEvent)
  {
  }

  public void windowClosing(WindowEvent paramWindowEvent)
  {
  }

  public void windowClosed(WindowEvent paramWindowEvent)
  {
    try
    {
      removeWindowListener(this);
      removeAll();
      dispose();
      WindowEvent localWindowEvent = new WindowEvent(this, 202);
      DeployAWTUtil.postEvent(this, localWindowEvent);
      this.obj = null;
    }
    catch (Throwable localThrowable)
    {
      Trace.printException(localThrowable);
    }
    finally
    {
      if (this.handle != 0)
        PlatformService.getService().signalEvent(this.handle);
    }
  }

  public void windowDeactivated(WindowEvent paramWindowEvent)
  {
  }

  public void windowDeiconified(WindowEvent paramWindowEvent)
  {
  }

  public void windowIconified(WindowEvent paramWindowEvent)
  {
  }

  public void windowOpened(WindowEvent paramWindowEvent)
  {
  }

  public void waitEvent(int paramInt)
  {
    PlatformService.getService().waitEvent(this.win_handle, paramInt);
  }

  public void setWaitingEvent(int paramInt)
  {
    this.handle = paramInt;
  }

  public void notifyModalBlocked(Dialog paramDialog, boolean paramBoolean)
  {
    super.notifyModalBlocked(paramDialog, paramBoolean);
    Field localField = null;
    long l = 0L;
    try
    {
      localField = (Field)AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Object run()
          throws Exception
        {
          Field localField = Component.class.getDeclaredField("peer");
          localField.setAccessible(true);
          return localField;
        }
      });
      WComponentPeer localWComponentPeer = (WComponentPeer)localField.get(paramDialog);
      l = localWComponentPeer.getHWnd();
    }
    catch (Exception localException)
    {
      Trace.printException(localException);
    }
    enableModeless(this.win_handle, paramBoolean, l);
  }

  public void activateEmbeddingTopLevel()
  {
    activateBrowserWindow(this.win_handle);
  }

  public void destroy()
  {
    final WNetscapeEmbeddedFrame localWNetscapeEmbeddedFrame = this;
    try
    {
      DeployAWTUtil.invokeLater(this, new Runnable()
      {
        public void run()
        {
          localWNetscapeEmbeddedFrame.setVisible(false);
          localWNetscapeEmbeddedFrame.setEnabled(false);
          WindowEvent localWindowEvent = new WindowEvent(localWNetscapeEmbeddedFrame, 202);
          DeployAWTUtil.postEvent(localWNetscapeEmbeddedFrame, localWindowEvent);
        }
      });
    }
    catch (Exception localException)
    {
    }
    assert (this.handle != 0);
    waitEvent(this.handle);
  }

  private native void enableModeless(int paramInt, boolean paramBoolean, long paramLong);

  private native void activateBrowserWindow(int paramInt);
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.viewer.frame.WNetscapeEmbeddedFrame
 * JD-Core Version:    0.6.2
 */