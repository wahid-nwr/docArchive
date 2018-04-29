package sun.plugin.util;

import com.sun.deploy.util.DeploySysAction;
import com.sun.deploy.util.DeploySysRun;
import com.sun.deploy.util.DeployUIManager;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.InvocationEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import sun.awt.AppContext;
import sun.awt.SunToolkit;
import sun.security.action.GetPropertyAction;

public final class PluginSysUtil extends DeploySysRun
{
  private static EventQueue pluginEventQueue = null;
  private static ThreadGroup pluginThreadGroup = null;
  private static ClassLoader pluginSysClassLoader = null;

  public static synchronized ThreadGroup getPluginThreadGroup()
  {
    if (pluginThreadGroup == null)
    {
      pluginSysClassLoader = Thread.currentThread().getContextClassLoader();
      pluginThreadGroup = new ThreadGroup(Thread.currentThread().getThreadGroup(), "Plugin Thread Group");
      createAppContext(pluginThreadGroup);
      try
      {
        Thread localThread = new Thread(pluginThreadGroup, new Runnable()
        {
          public void run()
          {
            DeployUIManager.setLookAndFeel();
          }
        });
        localThread.start();
        localThread.join();
      }
      catch (InterruptedException localInterruptedException)
      {
      }
    }
    return pluginThreadGroup;
  }

  public static void invokeAndWait(Runnable paramRunnable)
    throws InterruptedException, InvocationTargetException
  {
    if (EventQueue.isDispatchThread())
      throw new Error("Cannot call invokeAndWait from the event dispatcher thread");
    Object local1AWTInvocationLock = new Object()
    {
    };
    InvocationEvent localInvocationEvent = new InvocationEvent(Toolkit.getDefaultToolkit(), paramRunnable, local1AWTInvocationLock, true);
    synchronized (local1AWTInvocationLock)
    {
      pluginEventQueue.postEvent(localInvocationEvent);
      local1AWTInvocationLock.wait();
    }
    ??? = localInvocationEvent.getException();
    if (??? != null)
      throw new InvocationTargetException((Throwable)???);
  }

  public static void invokeLater(Runnable paramRunnable)
  {
    pluginEventQueue.postEvent(new InvocationEvent(Toolkit.getDefaultToolkit(), paramRunnable));
  }

  public static Thread createPluginSysThread(Runnable paramRunnable)
  {
    Thread localThread = new Thread(pluginThreadGroup, paramRunnable);
    localThread.setContextClassLoader(pluginSysClassLoader);
    return localThread;
  }

  public static Thread createPluginSysThread(Runnable paramRunnable, String paramString)
  {
    Thread localThread = new Thread(pluginThreadGroup, paramRunnable, paramString);
    localThread.setContextClassLoader(pluginSysClassLoader);
    return localThread;
  }

  public Object delegate(DeploySysAction paramDeploySysAction)
    throws Exception
  {
    return execute(paramDeploySysAction);
  }

  public static Object execute(DeploySysAction paramDeploySysAction)
    throws Exception
  {
    if (Thread.currentThread().getThreadGroup().equals(pluginThreadGroup))
      return paramDeploySysAction.execute();
    SysExecutionThread localSysExecutionThread = new SysExecutionThread(paramDeploySysAction);
    localSysExecutionThread.setContextClassLoader(pluginSysClassLoader);
    if (SwingUtilities.isEventDispatchThread())
    {
      synchronized (localSysExecutionThread.syncObject)
      {
        final DummyDialog localDummyDialog = new DummyDialog(null, true);
        localSysExecutionThread.theDummy = localDummyDialog;
        localDummyDialog.addWindowListener(new WindowAdapter()
        {
          public void windowOpened(WindowEvent paramAnonymousWindowEvent)
          {
            this.val$t.start();
          }

          public void windowClosing(WindowEvent paramAnonymousWindowEvent)
          {
            localDummyDialog.setVisible(false);
          }
        });
        Rectangle localRectangle = new Rectangle(new Point(0, 0), Toolkit.getDefaultToolkit().getScreenSize());
        if (!isOnWindows())
          localDummyDialog.setLocation(localRectangle.x + localRectangle.width / 2 - 50, localRectangle.y + localRectangle.height / 2);
        else
          localDummyDialog.setLocation(-100, -100);
        localDummyDialog.setResizable(false);
        localDummyDialog.toBack();
        localDummyDialog.setVisible(true);
        try
        {
          localSysExecutionThread.syncObject.wait();
        }
        catch (InterruptedException localInterruptedException2)
        {
        }
        finally
        {
          localDummyDialog.setVisible(false);
        }
      }
    }
    else
    {
      localSysExecutionThread.start();
      try
      {
        localSysExecutionThread.join();
      }
      catch (InterruptedException localInterruptedException1)
      {
      }
    }
    if (localSysExecutionThread.exception != null)
      throw localSysExecutionThread.exception;
    return localSysExecutionThread.result;
  }

  private static void createAppContext(ThreadGroup paramThreadGroup)
  {
    AppContextCreatorThread localAppContextCreatorThread = new AppContextCreatorThread(paramThreadGroup);
    synchronized (localAppContextCreatorThread.synObject)
    {
      localAppContextCreatorThread.start();
      try
      {
        localAppContextCreatorThread.synObject.wait();
      }
      catch (InterruptedException localInterruptedException)
      {
      }
    }
  }

  private static boolean isOnWindows()
  {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("os.name"));
    return str.indexOf("Windows") != -1;
  }

  private static class AppContextCreatorThread extends Thread
  {
    Object synObject = new Object();

    public AppContextCreatorThread(ThreadGroup paramThreadGroup)
    {
      super("AppContext Creator Thread");
    }

    public void run()
    {
      synchronized (this.synObject)
      {
        AppContext localAppContext = SunToolkit.createNewAppContext();
        PluginSysUtil.access$002((EventQueue)localAppContext.get(AppContext.EVENT_QUEUE_KEY));
        this.synObject.notifyAll();
      }
    }
  }

  private static class DummyDialog extends JDialog
  {
    private ThreadGroup _unsecureGroup = Thread.currentThread().getThreadGroup();

    DummyDialog(Frame paramFrame, boolean paramBoolean)
    {
      super(paramBoolean);
    }

    public void secureHide()
    {
      new Thread(this._unsecureGroup, new Runnable()
      {
        public void run()
        {
          PluginSysUtil.DummyDialog.this.setVisible(false);
        }
      }).start();
    }
  }

  static class SysExecutionThread extends Thread
  {
    Exception exception = null;
    Object result = null;
    DeploySysAction action = null;
    Object syncObject = new Object();
    PluginSysUtil.DummyDialog theDummy = null;

    public SysExecutionThread(DeploySysAction paramDeploySysAction)
    {
      super("SysExecutionThead");
      this.action = paramDeploySysAction;
    }

    public void run()
    {
      try
      {
        this.result = this.action.execute();
      }
      catch (Exception )
      {
        this.exception = ???;
      }
      finally
      {
        if (this.theDummy != null)
          this.theDummy.secureHide();
        synchronized (this.syncObject)
        {
          this.syncObject.notifyAll();
        }
      }
    }
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.util.PluginSysUtil
 * JD-Core Version:    0.6.2
 */