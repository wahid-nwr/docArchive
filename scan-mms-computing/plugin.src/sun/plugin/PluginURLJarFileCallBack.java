package sun.plugin;

import com.sun.deploy.cache.DeployCacheHandler;
import com.sun.deploy.config.Config;
import com.sun.deploy.net.DownloadEngine;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Enumeration;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import sun.awt.AppContext;
import sun.net.www.protocol.jar.URLJarFileCallBack;

public class PluginURLJarFileCallBack
  implements URLJarFileCallBack
{
  private static int BUF_SIZE = 8192;

  public JarFile retrieve(final URL paramURL)
    throws IOException
  {
    JarFile localJarFile = null;
    final URLConnection localURLConnection = paramURL.openConnection();
    try
    {
      localJarFile = (JarFile)AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Object run()
          throws IOException
        {
          URL localURL = new URL(paramURL.getProtocol(), paramURL.getHost(), paramURL.getPort(), paramURL.getPath());
          String str = (String)AppContext.getAppContext().get(Config.getAppContextKeyPrefix() + localURL.toString());
          localURLConnection.setRequestProperty("accept-encoding", "pack200-gzip, gzip");
          Object localObject2;
          if (DeployCacheHandler.isResourceCacheable(paramURL.toString(), localURLConnection))
          {
            localObject1 = null;
            try
            {
              localObject2 = new byte[PluginURLJarFileCallBack.BUF_SIZE];
              localObject1 = new BufferedInputStream(localURLConnection.getInputStream());
              while (((InputStream)localObject1).read((byte[])localObject2) != -1);
            }
            finally
            {
              if (localObject1 != null)
                ((InputStream)localObject1).close();
            }
            localObject2 = DownloadEngine.getCachedJarFile(localURL, str);
            if (localObject2 != null)
              return localObject2;
          }
          Object localObject1 = DownloadEngine.getJarFileWithoutCache(localURL, null, str, null);
          if (paramURL.toString().toUpperCase().endsWith(".JARJAR"))
          {
            localObject2 = new JarFile((File)localObject1, false);
            Enumeration localEnumeration = ((JarFile)localObject2).entries();
            ZipEntry localZipEntry = null;
            int i = 0;
            while (localEnumeration.hasMoreElements())
            {
              localZipEntry = (ZipEntry)localEnumeration.nextElement();
              if (!localZipEntry.getName().toUpperCase().startsWith("META-INF"))
              {
                if (!localZipEntry.toString().toUpperCase().endsWith(".JAR"))
                  throw new IOException("Invalid entry in jarjar file");
                i++;
                if (i > 1)
                  break;
              }
            }
            if (i > 1)
              throw new IOException("Multiple JAR files inside JARJAR file");
            BufferedInputStream localBufferedInputStream = null;
            BufferedOutputStream localBufferedOutputStream = null;
            try
            {
              byte[] arrayOfByte = new byte[PluginURLJarFileCallBack.BUF_SIZE];
              int j = 0;
              File localFile = File.createTempFile("jar_cache", null);
              localFile.deleteOnExit();
              localBufferedInputStream = new BufferedInputStream(((JarFile)localObject2).getInputStream(localZipEntry));
              localBufferedOutputStream = new BufferedOutputStream(new FileOutputStream(localFile));
              j = 0;
              while ((j = localBufferedInputStream.read(arrayOfByte)) != -1)
                localBufferedOutputStream.write(arrayOfByte, 0, j);
              localBufferedOutputStream.close();
              localBufferedOutputStream = null;
              localObject2 = new JarFile(localFile, false);
            }
            finally
            {
              if (localBufferedInputStream != null)
                localBufferedInputStream.close();
              if (localBufferedOutputStream != null)
                localBufferedOutputStream.close();
            }
            return localObject2;
          }
          return new JarFile((File)localObject1, false);
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw ((IOException)localPrivilegedActionException.getException());
    }
    return localJarFile;
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.PluginURLJarFileCallBack
 * JD-Core Version:    0.6.2
 */