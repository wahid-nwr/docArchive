package sun.plugin.com;

import java.applet.Applet;
import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.SocketPermission;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Policy;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.util.PropertyPermission;
import sun.net.www.ParseUtil;
import sun.plugin.javascript.ocx.JSObject;
import sun.plugin.liveconnect.JavaScriptProtectionDomain;
import sun.plugin.security.PluginClassLoader;
import sun.plugin.util.Trace;
import sun.plugin.viewer.context.IExplorerAppletContext;

public class DispatchImpl
  implements Dispatch
{
  JavaClass targetClass = null;
  Object targetObj = null;
  int handle = 0;
  int wndHandle = 0;
  AccessControlContext context = null;
  boolean isBridge = false;

  public DispatchImpl(Object paramObject, int paramInt)
  {
    this.targetObj = paramObject;
    this.handle = paramInt;
  }

  public Object invoke(final int paramInt1, final int paramInt2, final Object[] paramArrayOfObject)
    throws Exception
  {
    try
    {
      if (this.isBridge)
        return invokeImpl(paramInt1, paramInt2, paramArrayOfObject);
      if (this.context == null)
        this.context = createContext();
      return AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Object run()
          throws Exception
        {
          return DispatchImpl.this.invokeImpl(paramInt1, paramInt2, paramArrayOfObject);
        }
      }
      , this.context);
    }
    catch (Exception localException)
    {
      Object localObject = localException.getCause();
      if (localObject == null)
        localObject = localException;
      Trace.liveConnectPrintException((Throwable)localObject);
      throw new Exception(((Throwable)localObject).toString());
    }
  }

  public AccessControlContext createContext()
  {
    try
    {
      ProtectionDomain[] arrayOfProtectionDomain = new ProtectionDomain[1];
      ProtectionDomain localProtectionDomain = (ProtectionDomain)AccessController.doPrivileged(new PrivilegedAction()
      {
        public Object run()
        {
          return DispatchImpl.this.targetObj.getClass().getProtectionDomain();
        }
      });
      CodeSource localCodeSource = null;
      URL localURL = null;
      if (localProtectionDomain != null)
        localCodeSource = localProtectionDomain.getCodeSource();
      if (localCodeSource != null)
        localURL = localCodeSource.getLocation();
      arrayOfProtectionDomain[0] = getJSProtectionDomain(localURL, this.targetObj.getClass());
      return new AccessControlContext(arrayOfProtectionDomain);
    }
    catch (Exception localException)
    {
      Trace.liveConnectPrintException(localException);
    }
    return null;
  }

  public Object invokeImpl(int paramInt1, int paramInt2, Object[] paramArrayOfObject)
    throws Exception
  {
    Object localObject = null;
    Dispatcher localDispatcher = null;
    try
    {
      if (paramArrayOfObject != null)
        convertParams(paramArrayOfObject);
      localDispatcher = this.targetClass.getDispatcher(paramInt1, paramInt2, paramArrayOfObject);
      if (localDispatcher != null)
      {
        localObject = localDispatcher.invoke(this.targetObj, paramArrayOfObject);
        if (localObject != null)
          localObject = Utils.convertReturn(localDispatcher.getReturnType(), localObject, this.handle);
      }
      return localObject;
    }
    catch (Throwable localThrowable1)
    {
      Throwable localThrowable2 = localThrowable1.getCause();
      if (localThrowable2 == null)
        localThrowable2 = localThrowable1;
      Trace.liveConnectPrintException(localThrowable2);
      throw new Exception(localThrowable2.toString());
    }
  }

  public Object getWrappedObject()
  {
    return this.targetObj;
  }

  public JavaClass getTargetClass()
  {
    if ((this.targetClass == null) && (this.targetObj != null))
      this.targetClass = new JavaClass(this.targetObj.getClass());
    return this.targetClass;
  }

  public int getReturnType(int paramInt1, int paramInt2, Object[] paramArrayOfObject)
  {
    Class localClass = null;
    try
    {
      Dispatcher localDispatcher = null;
      if (paramArrayOfObject != null)
        convertParams(paramArrayOfObject);
      localDispatcher = this.targetClass.getDispatcher(paramInt1, paramInt2, paramArrayOfObject);
      if (localDispatcher != null)
        localClass = localDispatcher.getReturnType();
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      Trace.liveConnectPrintException(localInvocationTargetException);
      localClass = null;
    }
    return Utils.getType(localClass);
  }

  public int getIdForName(final String paramString)
    throws Exception
  {
    try
    {
      if (this.isBridge)
        return getIdForNameImpl(paramString);
      if (this.context == null)
        this.context = createContext();
      Integer localInteger = (Integer)AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Object run()
          throws Exception
        {
          return new Integer(DispatchImpl.this.getIdForNameImpl(paramString));
        }
      }
      , this.context);
      return localInteger.intValue();
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
    }
    return -1;
  }

  public int getIdForNameImpl(String paramString)
    throws Exception
  {
    int i = -1;
    if ((this.targetClass == null) && (this.targetObj != null))
      this.targetClass = new JavaClass(this.targetObj.getClass());
    if (this.targetClass != null)
      i = this.targetClass.getIdForName(paramString);
    return i;
  }

  private void convertParams(Object[] paramArrayOfObject)
  {
    for (int i = 0; i < paramArrayOfObject.length; i++)
      if ((paramArrayOfObject[i] != null) && ((paramArrayOfObject[i] instanceof DispatchImpl)))
      {
        paramArrayOfObject[i] = ((DispatchImpl)paramArrayOfObject[i]).getWrappedObject();
      }
      else if ((paramArrayOfObject[i] != null) && ((paramArrayOfObject[i] instanceof DispatchClient)))
      {
        JSObject localJSObject = null;
        if (!this.isBridge)
        {
          localJSObject = new JSObject((DispatchClient)paramArrayOfObject[i]);
          localJSObject.setIExplorerAppletContext((IExplorerAppletContext)((Applet)this.targetObj).getAppletContext());
        }
        else
        {
          localJSObject = new JSObject((DispatchClient)paramArrayOfObject[i], this.handle);
        }
        paramArrayOfObject[i] = localJSObject;
      }
  }

  public static ProtectionDomain getJSProtectionDomain(URL paramURL, Class paramClass)
    throws MalformedURLException
  {
    Policy localPolicy = (Policy)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        return Policy.getPolicy();
      }
    });
    CodeSource localCodeSource = new CodeSource(null, (Certificate[])null);
    PermissionCollection localPermissionCollection = localPolicy.getPermissions(localCodeSource);
    localPermissionCollection.add(new PropertyPermission("http.agent", "read"));
    if (paramURL != null)
    {
      String str1 = null;
      Permission localPermission;
      try
      {
        localPermission = paramURL.openConnection().getPermission();
      }
      catch (IOException localIOException)
      {
        localPermission = null;
      }
      if ((localPermission instanceof FilePermission))
      {
        str1 = localPermission.getName();
      }
      else if ((localPermission == null) && (paramURL.getProtocol().equals("file")))
      {
        str1 = paramURL.getFile().replace('/', File.separatorChar);
        str1 = ParseUtil.decode(str1);
      }
      else if ((localPermission instanceof SocketPermission))
      {
        String str2 = paramURL.getHost();
        localPermissionCollection.add(new SocketPermission(str2, "connect,accept"));
      }
      if ((str1 != null) && ((paramClass.getClassLoader() instanceof PluginClassLoader)))
      {
        if (str1.endsWith(File.separator))
        {
          str1 = str1 + "-";
        }
        else
        {
          int i = str1.lastIndexOf(File.separatorChar);
          if (i != -1)
            str1 = str1.substring(0, i + 1) + "-";
        }
        localPermissionCollection.add(new FilePermission(str1, "read"));
        localPermissionCollection.add(new SocketPermission("localhost", "connect,accept"));
        AccessController.doPrivileged(new PrivilegedAction()
        {
          public Object run()
          {
            try
            {
              String str = InetAddress.getLocalHost().getHostName();
              this.val$pc.add(new SocketPermission(str, "connect,accept"));
            }
            catch (UnknownHostException localUnknownHostException)
            {
            }
            return null;
          }
        });
      }
    }
    return new JavaScriptProtectionDomain(localPermissionCollection);
  }

  public String toString()
  {
    if (this.targetObj != null)
      return this.targetObj.toString();
    return null;
  }

  public int getWindowHandle()
  {
    if (this.wndHandle == 0)
      this.wndHandle = getWindowHandle(this.handle);
    return this.wndHandle;
  }

  protected void setBridge()
  {
    this.isBridge = true;
  }

  native int getWindowHandle(int paramInt);
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.com.DispatchImpl
 * JD-Core Version:    0.6.2
 */