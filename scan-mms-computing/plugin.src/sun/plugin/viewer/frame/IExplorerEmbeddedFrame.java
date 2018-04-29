package sun.plugin.viewer.frame;

import com.sun.deploy.util.DeployAWTUtil;
import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.Beans;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import sun.awt.windows.WComponentPeer;
import sun.awt.windows.WEmbeddedFrame;
import sun.plugin.AppletViewer;
import sun.plugin.services.PlatformService;
import sun.plugin.util.Trace;
import sun.plugin.viewer.IExplorerPluginObject;

public class IExplorerEmbeddedFrame extends WEmbeddedFrame
  implements WindowListener
{
  private int waitingEvent = 0;
  private IExplorerPluginObject pluginObj = null;
  private Component c;
  protected int handle = 0;

  public IExplorerEmbeddedFrame(int paramInt, IExplorerPluginObject paramIExplorerPluginObject)
  {
    super(paramInt);
    this.handle = paramInt;
    this.pluginObj = paramIExplorerPluginObject;
    setLayout(new BorderLayout());
    addWindowListener(this);
  }

  private void setWaitEvent(int paramInt)
  {
    assert (this.waitingEvent == 0);
    assert (paramInt != 0);
    this.waitingEvent = paramInt;
  }

  private void setComponent(Component paramComponent)
  {
    if (paramComponent != null)
    {
      Dimension localDimension = paramComponent.getSize();
      add(paramComponent);
      if ((localDimension.width != 0) && (localDimension.height != 0))
        setSize(localDimension);
      if (Beans.isInstanceOf(paramComponent, Applet.class))
      {
        Applet localApplet = (Applet)Beans.getInstanceOf(paramComponent, Applet.class);
        localApplet.start();
      }
    }
  }

  public void setBean(final Object paramObject)
  {
    try
    {
      DeployAWTUtil.invokeAndWait(this, new Runnable()
      {
        public void run()
        {
          try
          {
            IExplorerEmbeddedFrame.this.c = ((Component)Beans.getInstanceOf(paramObject, Component.class));
          }
          catch (ClassCastException localClassCastException)
          {
            return;
          }
          if (IExplorerEmbeddedFrame.this.c != null)
            IExplorerEmbeddedFrame.this.setComponent(IExplorerEmbeddedFrame.this.c);
        }
      });
    }
    catch (Exception localException)
    {
      System.out.println("Failed to setBean() : ");
      localException.printStackTrace();
    }
  }

  public void destroy()
  {
    final IExplorerEmbeddedFrame localIExplorerEmbeddedFrame = this;
    AppletViewer localAppletViewer;
    if ((this.c instanceof AppletViewer))
      localAppletViewer = (AppletViewer)this.c;
    else
      localAppletViewer = null;
    try
    {
      DeployAWTUtil.invokeLater(this, new Runnable()
      {
        public void run()
        {
          localIExplorerEmbeddedFrame.setVisible(false);
          localIExplorerEmbeddedFrame.setEnabled(false);
          WindowEvent localWindowEvent = new WindowEvent(localIExplorerEmbeddedFrame, 202);
          DeployAWTUtil.postEvent(localIExplorerEmbeddedFrame, localWindowEvent);
        }
      });
    }
    catch (Exception localException)
    {
    }
    assert (this.waitingEvent != 0);
    PlatformService.getService().waitEvent(this.handle, this.waitingEvent);
    if (localAppletViewer != null)
      synchronized (localAppletViewer.appletQuitLock)
      {
        localAppletViewer.readyToQuit = true;
        localAppletViewer.appletQuitLock.notify();
      }
  }

  public void setFrameSize(final int paramInt1, final int paramInt2)
  {
    if ((paramInt1 > 0) && (paramInt2 > 0))
      try
      {
        synchronized (this)
        {
          if ((this.c instanceof AppletViewer))
          {
            AppletViewer localAppletViewer = (AppletViewer)this.c;
            localAppletViewer.setParameter("width", Integer.toString(paramInt1));
            localAppletViewer.setParameter("height", Integer.toString(paramInt2));
          }
        }
        DeployAWTUtil.invokeLater(this, new Runnable()
        {
          public void run()
          {
            IExplorerEmbeddedFrame.this.setSize(paramInt1, paramInt2);
            if (IExplorerEmbeddedFrame.this.c != null)
            {
              IExplorerEmbeddedFrame.this.c.setBounds(0, 0, paramInt1, paramInt2);
              if ((IExplorerEmbeddedFrame.this.c instanceof AppletViewer))
              {
                AppletViewer localAppletViewer = (AppletViewer)IExplorerEmbeddedFrame.this.c;
                localAppletViewer.appletResize(paramInt1, paramInt2);
                Applet localApplet = localAppletViewer.getApplet();
                if (localApplet != null)
                  localApplet.resize(paramInt1, paramInt2);
              }
            }
          }
        });
        this.pluginObj.frameReady();
      }
      catch (Throwable localThrowable)
      {
        Trace.printException(localThrowable);
      }
  }

  public void windowActivated(WindowEvent paramWindowEvent)
  {
  }

  public void windowClosing(WindowEvent paramWindowEvent)
  {
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

  public void windowClosed(WindowEvent paramWindowEvent)
  {
    try
    {
      removeWindowListener(this);
      removeAll();
      dispose();
      this.c = null;
      this.handle = 0;
    }
    catch (Throwable localThrowable)
    {
      Trace.printException(localThrowable);
    }
    finally
    {
      if (this.waitingEvent != 0)
        PlatformService.getService().signalEvent(this.waitingEvent);
    }
  }

  public void registerFocusListener()
  {
    registerListeners();
  }

  protected boolean traverseOut(boolean paramBoolean)
  {
    transferFocus(this.handle, paramBoolean);
    return true;
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
    enableModeless(this.handle, paramBoolean, l);
  }

  public void activateEmbeddingTopLevel()
  {
    activateBrowserWindow(this.handle);
  }

  private native void transferFocus(int paramInt, boolean paramBoolean);

  private native void activateBrowserWindow(int paramInt);

  public native void enableModeless(int paramInt, boolean paramBoolean, long paramLong);
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.viewer.frame.IExplorerEmbeddedFrame
 * JD-Core Version:    0.6.2
 */