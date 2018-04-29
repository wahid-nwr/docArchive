package sun.plugin.liveconnect;

import java.io.FilePermission;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.SocketPermission;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.util.PropertyPermission;
import sun.plugin.util.Trace;

public class SecureInvocation
{
  private static Object ConstructObject(Class paramClass, final Constructor paramConstructor, final Object[] paramArrayOfObject, final String paramString, final boolean paramBoolean1, final boolean paramBoolean2)
    throws Exception
  {
    try
    {
      return AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Object run()
          throws Exception
        {
          try
          {
            SecureInvocation.checkLiveConnectCaller(this.val$clazz, paramString, paramBoolean1);
            ProtectionDomain[] arrayOfProtectionDomain = new ProtectionDomain[1];
            if (!paramBoolean2)
              arrayOfProtectionDomain[0] = SecureInvocation.getDefaultProtectionDomain(paramString);
            else
              arrayOfProtectionDomain[0] = SecureInvocation.access$200();
            AccessControlContext localAccessControlContext = new AccessControlContext(arrayOfProtectionDomain);
            return AccessController.doPrivileged(new PrivilegedConstructObjectAction(paramConstructor, paramArrayOfObject), localAccessControlContext);
          }
          catch (Exception localException)
          {
            Trace.liveConnectPrintException(localException);
            throw localException;
          }
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw localPrivilegedActionException;
    }
  }

  private static Object CallMethod(Class paramClass, final Object paramObject, final Method paramMethod, final Object[] paramArrayOfObject, final String paramString, final boolean paramBoolean1, final boolean paramBoolean2)
    throws Exception
  {
    try
    {
      return AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Object run()
          throws Exception
        {
          try
          {
            SecureInvocation.checkLiveConnectCaller(this.val$clazz, paramString, paramBoolean1);
            ProtectionDomain[] arrayOfProtectionDomain = new ProtectionDomain[1];
            if (!paramBoolean2)
              arrayOfProtectionDomain[0] = SecureInvocation.getDefaultProtectionDomain(paramString);
            else
              arrayOfProtectionDomain[0] = SecureInvocation.access$200();
            AccessControlContext localAccessControlContext = new AccessControlContext(arrayOfProtectionDomain);
            return AccessController.doPrivileged(new PrivilegedCallMethodAction(paramMethod, paramObject, paramArrayOfObject), localAccessControlContext);
          }
          catch (Exception localException)
          {
            Trace.liveConnectPrintException(localException);
            throw localException;
          }
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw localPrivilegedActionException;
    }
  }

  private static Object GetField(Class paramClass, final Object paramObject, final Field paramField, final String paramString, final boolean paramBoolean1, final boolean paramBoolean2)
    throws Exception
  {
    try
    {
      return AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Object run()
          throws Exception
        {
          try
          {
            SecureInvocation.checkLiveConnectCaller(this.val$clazz, paramString, paramBoolean1);
            ProtectionDomain[] arrayOfProtectionDomain = new ProtectionDomain[1];
            if (!paramBoolean2)
              arrayOfProtectionDomain[0] = SecureInvocation.getDefaultProtectionDomain(paramString);
            else
              arrayOfProtectionDomain[0] = SecureInvocation.access$200();
            AccessControlContext localAccessControlContext = new AccessControlContext(arrayOfProtectionDomain);
            return AccessController.doPrivileged(new PrivilegedGetFieldAction(paramField, paramObject), localAccessControlContext);
          }
          catch (Exception localException)
          {
            Trace.liveConnectPrintException(localException);
            throw localException;
          }
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw localPrivilegedActionException;
    }
  }

  private static void SetField(Class paramClass, final Object paramObject1, final Field paramField, final Object paramObject2, final String paramString, final boolean paramBoolean1, final boolean paramBoolean2)
    throws Exception
  {
    try
    {
      AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Object run()
          throws Exception
        {
          try
          {
            SecureInvocation.checkLiveConnectCaller(this.val$clazz, paramString, paramBoolean1);
            ProtectionDomain[] arrayOfProtectionDomain = new ProtectionDomain[1];
            if (!paramBoolean2)
              arrayOfProtectionDomain[0] = SecureInvocation.getDefaultProtectionDomain(paramString);
            else
              arrayOfProtectionDomain[0] = SecureInvocation.access$200();
            AccessControlContext localAccessControlContext = new AccessControlContext(arrayOfProtectionDomain);
            AccessController.doPrivileged(new PrivilegedSetFieldAction(paramField, paramObject1, paramObject2), localAccessControlContext);
            return null;
          }
          catch (Exception localException)
          {
            Trace.liveConnectPrintException(localException);
            throw localException;
          }
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw localPrivilegedActionException;
    }
  }

  private static void checkLiveConnectCaller(Class paramClass, String paramString, boolean paramBoolean)
    throws OriginNotAllowedException, MalformedURLException
  {
    if (paramBoolean)
    {
      Trace.msgLiveConnectPrintln("liveconnect.UniversalBrowserRead.enabled");
      return;
    }
    ProtectionDomain localProtectionDomain = paramClass.getProtectionDomain();
    CodeSource localCodeSource = localProtectionDomain.getCodeSource();
    if (localCodeSource == null)
    {
      Trace.msgLiveConnectPrintln("liveconnect.java.system");
      return;
    }
    URL localURL1 = localCodeSource.getLocation();
    URL localURL2 = null;
    if (paramString != null)
      try
      {
        localURL2 = new URL(paramString);
      }
      catch (MalformedURLException localMalformedURLException)
      {
        localMalformedURLException.printStackTrace();
        return;
      }
    if ((localURL1 != null) && (localURL2 != null) && (localURL1.getProtocol().equalsIgnoreCase(localURL2.getProtocol())) && (localURL1.getHost().equalsIgnoreCase(localURL2.getHost())) && (localURL1.getPort() == localURL2.getPort()))
    {
      Trace.msgLiveConnectPrintln("liveconnect.same.origin");
      return;
    }
    throw new OriginNotAllowedException("JavaScript is not from the same origin as the Java code, caller=" + localURL2 + ", callee=" + localURL1);
  }

  private static ProtectionDomain getDefaultProtectionDomain(String paramString)
    throws MalformedURLException
  {
    Trace.msgLiveConnectPrintln("liveconnect.default.policy", new Object[] { paramString });
    URL localURL = null;
    if (paramString != null)
      try
      {
        localURL = new URL(paramString);
      }
      catch (MalformedURLException localMalformedURLException)
      {
      }
    Policy localPolicy = Policy.getPolicy();
    CodeSource localCodeSource = new CodeSource(localURL, (Certificate[])null);
    PermissionCollection localPermissionCollection = localPolicy.getPermissions(localCodeSource);
    localPermissionCollection.add(new PropertyPermission("http.agent", "read"));
    if ((localURL == null) || (localURL.getProtocol().equals("file")))
    {
      localPermissionCollection.add(new FilePermission("<<ALL FILES>>", "read"));
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
    else
    {
      final String str = localURL.getHost();
      AccessController.doPrivileged(new PrivilegedAction()
      {
        public Object run()
        {
          this.val$pc.add(new SocketPermission(str, "connect,accept"));
          return null;
        }
      });
    }
    return new JavaScriptProtectionDomain(localPermissionCollection);
  }

  private static ProtectionDomain getTrustedProtectionDomain()
  {
    Trace.msgLiveConnectPrintln("liveconnect.UniversalJavaPermission.enabled");
    Permissions localPermissions = new Permissions();
    localPermissions.add(new AllPermission());
    return new JavaScriptProtectionDomain(localPermissions);
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.liveconnect.SecureInvocation
 * JD-Core Version:    0.6.2
 */