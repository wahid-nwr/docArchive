package sun.plugin.viewer;

import com.sun.deploy.perf.DeployPerfUtil;
import com.sun.deploy.services.ServiceManager;
import com.sun.deploy.util.DeployAWTUtil;
import java.applet.Applet;
import java.applet.AppletContext;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Panel;
import sun.applet.AppletEvent;
import sun.applet.AppletListener;
import sun.applet.AppletPanel;
import sun.awt.AppContext;
import sun.awt.SunToolkit;
import sun.plugin.AppletStatusListener;
import sun.plugin.AppletViewer;
import sun.plugin.BeansApplet;
import sun.plugin.BeansViewer;
import sun.plugin.services.BrowserService;
import sun.plugin.util.Trace;
import sun.plugin.viewer.context.NetscapeAppletContext;
import sun.plugin.viewer.context.PluginAppletContext;
import sun.plugin.viewer.context.PluginBeansContext;
import sun.plugin.viewer.frame.WNetscapeEmbeddedFrame;

public class WNetscapePluginObject
  implements AppletListener, AppletStatusListener
{
  private WNetscapeEmbeddedFrame frame = null;
  protected AppletViewer panel = null;
  protected int id = -1;
  private int handle = 0;
  private int width = 0;
  private int height = 0;
  private String identifier = null;
  private int waitEvent = 0;
  private boolean destroyed = false;
  private int startCount = -1;
  private boolean initialized = false;

  WNetscapePluginObject(int paramInt, boolean paramBoolean, String paramString)
  {
    this.id = paramInt;
    this.identifier = paramString;
    this.panel = LifeCycleManager.getAppletPanel(paramString);
    Object localObject;
    BrowserService localBrowserService;
    if (!paramBoolean)
    {
      if (this.panel == null)
      {
        this.panel = new AppletViewer();
        this.panel.addAppletListener(this);
      }
      localObject = (PluginAppletContext)this.panel.getAppletContext();
      if (localObject == null)
      {
        localBrowserService = (BrowserService)ServiceManager.getService();
        localObject = localBrowserService.getAppletContext();
      }
      ((PluginAppletContext)localObject).setAppletContextHandle(paramInt);
      this.panel.setAppletContext((AppletContext)localObject);
    }
    else
    {
      if (this.panel == null)
        this.panel = new BeansViewer();
      localObject = (PluginBeansContext)this.panel.getAppletContext();
      if (localObject == null)
      {
        localBrowserService = (BrowserService)ServiceManager.getService();
        localObject = localBrowserService.getBeansContext();
      }
      ((PluginBeansContext)localObject).setAppletContextHandle(paramInt);
      this.panel.setAppletContext((AppletContext)localObject);
    }
  }

  private void setWaitEvent(int paramInt)
  {
    assert (paramInt != 0);
    assert (this.waitEvent == 0);
    this.waitEvent = paramInt;
  }

  private WNetscapeEmbeddedFrame createFrame(int paramInt)
  {
    DeployPerfUtil.put("START - Java   - ENV - create embedded browser frame (Mozilla:Windows)");
    WNetscapeEmbeddedFrame localWNetscapeEmbeddedFrame = new WNetscapeEmbeddedFrame(paramInt);
    Applet localApplet = this.panel.getApplet();
    if (localApplet != null)
      AppletPanel.changeFrameAppContext(localWNetscapeEmbeddedFrame, SunToolkit.targetToAppContext(localApplet));
    localWNetscapeEmbeddedFrame.setJavaObject(this);
    localWNetscapeEmbeddedFrame.setWaitingEvent(this.waitEvent);
    DeployPerfUtil.put("END   - Java   - ENV - create embedded browser frame (Mozilla:Windows)");
    return localWNetscapeEmbeddedFrame;
  }

  synchronized Frame setWindow(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (paramInt3 == 0)
      paramInt3 = 1;
    if (paramInt4 == 0)
      paramInt4 = 1;
    if (this.handle == paramInt1)
      return this.frame;
    if (this.frame != null)
      this.frame.destroy();
    this.frame = null;
    this.handle = paramInt1;
    if (paramInt1 != 0)
    {
      try
      {
        this.width = Integer.parseInt(getParameter("width"));
        this.height = Integer.parseInt(getParameter("height"));
      }
      catch (NumberFormatException localNumberFormatException)
      {
        setParameter("width", new Integer(paramInt3));
        setParameter("height", new Integer(paramInt4));
        this.width = paramInt3;
        this.height = paramInt4;
      }
      catch (Throwable localThrowable1)
      {
        Trace.printException(localThrowable1);
      }
      this.frame = createFrame(paramInt1);
      try
      {
        this.frame.setSize(paramInt3, paramInt4);
        this.frame.setVisible(true);
        this.frame.setEnabled(true);
      }
      catch (Throwable localThrowable2)
      {
        Trace.printException(localThrowable2);
      }
      initPlugin();
    }
    return this.frame;
  }

  private synchronized void initPlugin()
  {
    assert (this.panel != null);
    this.panel.addAppletStatusListener(this);
    if (this.frame != null)
    {
      try
      {
        Applet localApplet = this.panel.getApplet();
        if (localApplet != null)
          AppletPanel.changeFrameAppContext(this.frame, SunToolkit.targetToAppContext(localApplet));
        this.frame.add(this.panel);
        DeployAWTUtil.invokeLater(this.frame, new Runnable()
        {
          public void run()
          {
            WNetscapePluginObject.this.frame.setVisible(true);
          }
        });
      }
      catch (Exception localException)
      {
      }
      if (!this.initialized)
      {
        this.initialized = true;
        LifeCycleManager.checkLifeCycle(this.panel);
        new Initer(this.panel, this).start();
      }
    }
  }

  synchronized void startPlugin()
  {
    assert (this.panel != null);
    if (this.initialized == true)
    {
      LifeCycleManager.startAppletPanel(this.panel);
    }
    else
    {
      if (this.startCount < 0)
        this.startCount = 0;
      this.startCount += 1;
    }
  }

  synchronized void stopPlugin()
  {
    assert (this.panel != null);
    if (this.initialized == true)
      LifeCycleManager.stopAppletPanel(this.panel);
    else
      this.startCount -= 1;
  }

  void destroyPlugin()
  {
    destroyPlugin(0);
  }

  synchronized void destroyPlugin(int paramInt)
  {
    assert (this.panel != null);
    PluginAppletContext localPluginAppletContext = (PluginAppletContext)this.panel.getAppletContext();
    LifeCycleManager.destroyAppletPanel(this.identifier, this.panel);
    if (localPluginAppletContext != null)
    {
      localPluginAppletContext.setAppletContextHandle(0);
      ((NetscapeAppletContext)localPluginAppletContext).onClose();
    }
    if (this.frame != null)
      this.frame.destroy();
    this.id = 0;
    this.panel = null;
    this.frame = null;
    this.destroyed = true;
  }

  synchronized void setDocumentURL(String paramString)
  {
    assert (this.panel != null);
    try
    {
      notifyAll();
      this.panel.setDocumentBase(paramString);
      initPlugin();
    }
    catch (Throwable localThrowable)
    {
      Trace.printException(localThrowable);
    }
  }

  private Frame getFrame()
  {
    return this.frame;
  }

  void setFocus()
  {
    if (this.frame != null)
      this.frame.synthesizeWindowActivation(true);
  }

  void setFrameSize(int paramInt1, int paramInt2)
  {
    synchronized (this)
    {
      if ((paramInt1 > 0) && (paramInt2 > 0))
      {
        setParameter("width", new Integer(paramInt1));
        setParameter("height", new Integer(paramInt2));
        if (this.frame != null)
          this.frame.setSize(paramInt1, paramInt2);
        if (this.panel != null)
        {
          AppletViewer localAppletViewer = this.panel;
          if (localAppletViewer != null)
          {
            localAppletViewer.setSize(paramInt1, paramInt2);
            this.panel.setParameter("width", new Integer(paramInt1));
            this.panel.setParameter("height", new Integer(paramInt2));
          }
          Object localObject1 = this.panel.getViewedObject();
          if (localObject1 != null)
          {
            Applet localApplet;
            if ((localObject1 instanceof Applet))
            {
              localApplet = (Applet)localObject1;
            }
            else
            {
              Component localComponent = (Component)localObject1;
              localComponent.setSize(paramInt1, paramInt2);
              localApplet = (Applet)localComponent.getParent();
            }
            if (localApplet != null)
              localApplet.resize(paramInt1, paramInt2);
          }
        }
      }
    }
  }

  public Object getJavaObject()
  {
    Object localObject = null;
    if (this.panel != null)
      localObject = this.panel.getViewedObject();
    if ((localObject instanceof BeansApplet))
    {
      BeansApplet localBeansApplet = (BeansApplet)localObject;
      localObject = localBeansApplet.getBean();
    }
    return localObject;
  }

  int getLoadingStatus()
  {
    if (this.panel != null)
      return this.panel.getLoadingStatus();
    return 7;
  }

  public String getParameter(String paramString)
  {
    assert (this.panel != null);
    return this.panel.getParameter(paramString);
  }

  public void setParameter(String paramString, Object paramObject)
  {
    assert (this.panel != null);
    this.panel.setParameter(paramString, paramObject);
  }

  public void setBoxColors()
  {
    this.panel.setColorAndText();
  }

  public void appletStateChanged(AppletEvent paramAppletEvent)
  {
    AppletPanel localAppletPanel = (AppletPanel)paramAppletEvent.getSource();
    switch (paramAppletEvent.getID())
    {
    case 51236:
      if (this.frame != null)
      {
        Applet localApplet = localAppletPanel.getApplet();
        if (localApplet != null)
          AppletPanel.changeFrameAppContext(this.frame, SunToolkit.targetToAppContext(localApplet));
        else
          AppletPanel.changeFrameAppContext(this.frame, AppContext.getAppContext());
      }
      break;
    }
  }

  public void statusChanged(int paramInt)
  {
    if ((this.id != 0) && ((paramInt == 3) || (paramInt == 5)))
      notifyStatusChange(this.id, paramInt);
  }

  private native void notifyStatusChange(int paramInt1, int paramInt2);

  private class Initer extends Thread
  {
    AppletViewer that;
    WNetscapePluginObject obj;

    Initer(AppletViewer paramWNetscapePluginObject, WNetscapePluginObject arg3)
    {
      this.that = paramWNetscapePluginObject;
      Object localObject;
      this.obj = localObject;
    }

    public void run()
    {
      LifeCycleManager.initAppletPanel(this.that);
      synchronized (this.obj)
      {
        this.obj.initialized = true;
        if (this.obj.destroyed)
          return;
        if (this.obj.startCount > 0)
        {
          this.obj.startCount = 0;
          this.obj.startPlugin();
        }
        else if (this.obj.startCount == 0)
        {
          this.obj.startPlugin();
          this.obj.stopPlugin();
        }
      }
    }
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.viewer.WNetscapePluginObject
 * JD-Core Version:    0.6.2
 */