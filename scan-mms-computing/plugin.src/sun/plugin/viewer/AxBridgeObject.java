package sun.plugin.viewer;

import com.sun.deploy.util.DeployAWTUtil;
import java.awt.Component;
import java.beans.BeanInfo;
import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;
import sun.plugin.AppletStatusListener;
import sun.plugin.AppletViewer;
import sun.plugin.BeansViewer;
import sun.plugin.com.BeanClass;
import sun.plugin.com.BeanCustomizer;
import sun.plugin.com.BeanDispatchImpl;
import sun.plugin.com.DispatchImpl;
import sun.plugin.com.event.ListenerProxy;
import sun.plugin.viewer.context.AxBridgeAppletContext;
import sun.plugin.viewer.frame.AxBridgeEmbeddedFrame;
import sun.plugin.viewer.frame.IExplorerEmbeddedFrame;

public class AxBridgeObject extends IExplorerPluginObject
  implements AppletStatusListener
{
  private BeanDispatchImpl dispImpl = null;
  private ListenerProxy listener = null;

  AxBridgeObject(int paramInt, boolean paramBoolean, String paramString)
  {
    super(paramInt, paramBoolean, paramString);
    AxBridgeAppletContext localAxBridgeAppletContext = new AxBridgeAppletContext();
    this.panel.setAppletContext(localAxBridgeAppletContext);
  }

  public void mayInit()
  {
    if ((this.bFrameReady == true) && (!this.bInit))
    {
      this.bInit = true;
      initPlugin();
    }
  }

  private synchronized void initPlugin()
  {
    assert (this.panel != null);
    this.panel.addAppletStatusListener(this);
    this.panel.initApplet();
    this.panel.appletStart();
  }

  public void destroyPlugin()
  {
    assert (this.panel != null);
    this.panel.removeAppletStatusListener(this);
    this.panel.appletStop();
    this.panel.appletDestroy();
  }

  public Object getDispatchObject()
  {
    Object localObject = getJavaObject();
    if ((this.dispImpl == null) && (localObject != null))
      this.dispImpl = new BeanDispatchImpl(localObject, this.id);
    return this.dispImpl;
  }

  public Object getCustomizer()
  {
    BeanInfo localBeanInfo = ((BeanClass)this.dispImpl.getTargetClass()).getBeanInfo();
    return new BeanCustomizer(localBeanInfo);
  }

  public void statusChanged(int paramInt)
  {
    Object localObject;
    if (paramInt == 2)
    {
      localObject = getDispatchObject();
      if (localObject != null)
      {
        this.listener = new ListenerProxy(this.id, (DispatchImpl)localObject);
        this.listener.register();
      }
    }
    else if (paramInt == 3)
    {
      localObject = (AxBridgeEmbeddedFrame)this.frame;
      try
      {
        DeployAWTUtil.invokeLater((Component)localObject, new Runnable()
        {
          public void run()
          {
            this.val$f.registerFocusListener();
            this.val$f.synthesizeWindowActivation(true);
          }
        });
      }
      catch (Exception localException)
      {
        localException.printStackTrace();
      }
    }
    notifyStatusChange(this.id, paramInt);
  }

  public Object save()
  {
    byte[] arrayOfByte = null;
    Object localObject = getJavaObject();
    ClassLoader localClassLoader = Thread.currentThread().getContextClassLoader();
    Thread.currentThread().setContextClassLoader(localObject.getClass().getClassLoader());
    if (getLoadingStatus() > 2)
      this.listener.unregister();
    try
    {
      ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
      XMLEncoder localXMLEncoder = new XMLEncoder(localByteArrayOutputStream);
      localXMLEncoder.writeObject(localObject);
      localXMLEncoder.close();
      arrayOfByte = localByteArrayOutputStream.toByteArray();
      localByteArrayOutputStream.close();
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    if (getLoadingStatus() > 2)
      this.listener.register();
    Thread.currentThread().setContextClassLoader(localClassLoader);
    return arrayOfByte;
  }

  public void load(Object paramObject)
  {
    try
    {
      byte[] arrayOfByte = (byte[])paramObject;
      ((BeansViewer)this.panel).setByteStream(arrayOfByte);
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }

  public IExplorerEmbeddedFrame createFrame(final int paramInt)
  {
    this.panel.createClassLoader();
    ThreadGroup localThreadGroup = this.panel.getAppletHandlerThread().getThreadGroup();
    final AxBridgeObject localAxBridgeObject = this;
    Thread localThread = new Thread(localThreadGroup, new Runnable()
    {
      public void run()
      {
        AxBridgeObject.this.frame = new AxBridgeEmbeddedFrame(paramInt, localAxBridgeObject);
        AxBridgeObject.this.frame.setBean(AxBridgeObject.this.panel);
      }
    });
    localThread.start();
    try
    {
      localThread.join();
    }
    catch (Exception localException)
    {
    }
    return this.frame;
  }

  private native void notifyStatusChange(int paramInt1, int paramInt2);
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.viewer.AxBridgeObject
 * JD-Core Version:    0.6.2
 */