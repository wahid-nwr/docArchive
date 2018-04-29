package sun.plugin.security;

import com.sun.deploy.resources.ResourceManager;
import com.sun.deploy.ui.UIFactory;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.applet.AppletSecurity;
import sun.awt.AppContext;
import sun.plugin.util.Trace;

public class ActivatorSecurityManager extends AppletSecurity
{
  public void checkAwtEventQueueAccess()
  {
    try
    {
      super.checkAwtEventQueueAccess();
      return;
    }
    catch (SecurityException localSecurityException)
    {
      checkSecurityAccess("accessEventQueue");
    }
  }

  public void checkSecurityAccess(String paramString)
  {
    if ((paramString != null) && (paramString.equals("java")))
      return;
    super.checkSecurityAccess(paramString);
  }

  public void checkPrintJobAccess()
  {
    try
    {
      super.checkPrintJobAccess();
      return;
    }
    catch (SecurityException localSecurityException)
    {
      new CheckPrint_1_2();
    }
  }

  void showPrintDialog()
  {
    AppContext localAppContext = AppContext.getAppContext();
    String str1 = ResourceManager.getString("plugin.print.title");
    String str2 = ResourceManager.getString("plugin.print.message");
    String str3 = ResourceManager.getString("plugin.print.always");
    String str4 = (String)localAppContext.get("sun.plugin.security.printDialog");
    int i = 0;
    if ((!Trace.isAutomationEnabled()) && (str4 == null))
    {
      i = UIFactory.showApiDialog(null, null, str1, str2, null, null, str3, false);
    }
    else
    {
      Trace.msgSecurityPrintln("securitymgr.automation.printing");
      i = 0;
    }
    if (i == 2)
      localAppContext.put("sun.plugin.security.printDialog", "skip");
    else if (i != 0)
      throw new SecurityException("checkPrintJobAccess");
  }

  public Class[] getExecutionStackContext()
  {
    return super.getClassContext();
  }

  private class CheckPrint_1_2
    implements PrivilegedAction
  {
    CheckPrint_1_2()
    {
      AccessController.doPrivileged(this);
    }

    public Object run()
    {
      ActivatorSecurityManager.this.showPrintDialog();
      return null;
    }
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.security.ActivatorSecurityManager
 * JD-Core Version:    0.6.2
 */