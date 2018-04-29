package sun.plugin.viewer;

import com.sun.deploy.perf.DeployPerfUtil;
import com.sun.deploy.services.ServiceManager;
import java.applet.Applet;
import java.awt.Frame;
import sun.applet.AppletEvent;
import sun.applet.AppletListener;
import sun.applet.AppletPanel;
import sun.awt.AppContext;
import sun.awt.SunToolkit;
import sun.plugin.AppletViewer;
import sun.plugin.BeansViewer;
import sun.plugin.com.DispatchImpl;
import sun.plugin.services.BrowserService;
import sun.plugin.viewer.context.IExplorerAppletContext;
import sun.plugin.viewer.context.PluginAppletContext;
import sun.plugin.viewer.frame.IExplorerEmbeddedFrame;

public class IExplorerPluginObject
  implements AppletListener
{
  protected IExplorerAppletStatusListener ieasl = null;
  protected IExplorerEmbeddedFrame frame = null;
  protected AppletViewer panel = null;
  protected int id = -1;
  private int width = 0;
  private int height = 0;
  private String identifier = null;
  boolean bFrameReady = false;
  boolean bContainerReady = false;
  boolean bInit = false;

  IExplorerPluginObject(int paramInt, boolean paramBoolean, String paramString)
  {
    this.id = paramInt;
    this.identifier = paramString;
    if (!paramBoolean)
    {
      this.panel = LifeCycleManager.getAppletPanel(paramString);
      if (this.panel == null)
      {
        this.panel = new AppletViewer();
        this.panel.addAppletListener(this);
      }
      PluginAppletContext localPluginAppletContext = (PluginAppletContext)this.panel.getAppletContext();
      if (localPluginAppletContext == null)
      {
        BrowserService localBrowserService = (BrowserService)ServiceManager.getService();
        localPluginAppletContext = localBrowserService.getAppletContext();
      }
      localPluginAppletContext.setAppletContextHandle(paramInt);
      this.panel.setAppletContext(localPluginAppletContext);
    }
    else if (this.panel == null)
    {
      this.panel = new BeansViewer();
    }
  }

  public IExplorerEmbeddedFrame createFrame(int paramInt)
  {
    DeployPerfUtil.put("START - Java   - ENV - create embedded browser frame (IE)");
    this.frame = new IExplorerEmbeddedFrame(paramInt, this);
    Applet localApplet = this.panel.getApplet();
    if (localApplet != null)
      AppletPanel.changeFrameAppContext(this.frame, SunToolkit.targetToAppContext(localApplet));
    this.frame.setBean(this.panel);
    this.ieasl = new IExplorerAppletStatusListener(this.id);
    this.ieasl.setEmbeddedFrame(this.frame);
    DeployPerfUtil.put("END   - Java   - ENV - create embedded browser frame (IE)");
    return this.frame;
  }

  private synchronized void initPlugin()
  {
    assert (this.panel != null);
    this.panel.addAppletStatusListener(this.ieasl);
    LifeCycleManager.checkLifeCycle(this.panel);
    new Initer(this.panel, this).start();
  }

  public void destroyPlugin()
  {
    assert (this.panel != null);
    if (this.bInit)
    {
      PluginAppletContext localPluginAppletContext = (PluginAppletContext)this.panel.getAppletContext();
      LifeCycleManager.stopAppletPanel(this.panel);
      this.panel.readyToQuit = false;
      LifeCycleManager.destroyAppletPanel(this.identifier, this.panel);
      this.panel.removeAppletStatusListener(null);
      ((IExplorerAppletContext)localPluginAppletContext).onClose();
      if (localPluginAppletContext != null)
        localPluginAppletContext.setAppletContextHandle(0);
    }
  }

  public void mayInit()
  {
    if ((this.bFrameReady == true) && (this.bContainerReady == true) && (!this.bInit))
    {
      this.bInit = true;
      initPlugin();
    }
  }

  public void containerReady()
  {
    this.bContainerReady = true;
    mayInit();
  }

  public void frameReady()
  {
    this.bFrameReady = true;
    mayInit();
  }

  public void preRefresh()
  {
    if (this.panel != null)
      this.panel.preRefresh();
  }

  public AppletViewer getPanel()
  {
    return this.panel;
  }

  protected Frame getFrame()
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
    if (this.frame != null)
      this.frame.setFrameSize(paramInt1, paramInt2);
  }

  public Object getJavaObject()
  {
    Object localObject = null;
    if (this.panel != null)
      localObject = this.panel.getViewedObject();
    return localObject;
  }

  public Object getDispatchObject()
  {
    return new DispatchImpl(getJavaObject(), this.id);
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

  private class Initer extends Thread
  {
    AppletViewer that;
    IExplorerPluginObject obj;

    Initer(AppletViewer paramIExplorerPluginObject, IExplorerPluginObject arg3)
    {
      this.that = paramIExplorerPluginObject;
      Object localObject;
      this.obj = localObject;
    }

    public void run()
    {
      LifeCycleManager.initAppletPanel(this.that);
      LifeCycleManager.startAppletPanel(this.that);
    }
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.viewer.IExplorerPluginObject
 * JD-Core Version:    0.6.2
 */