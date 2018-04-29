package sun.plugin.net.protocol.jar;

import com.sun.deploy.cache.Cache;
import com.sun.deploy.cache.CachedJarFile;
import com.sun.deploy.config.Config;
import com.sun.deploy.net.DownloadEngine;
import com.sun.deploy.net.URLEncoder;
import com.sun.deploy.util.URLUtil;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import sun.awt.AppContext;
import sun.net.www.protocol.jar.JarURLConnection;

public class CachedJarURLConnection extends JarURLConnection
{
  private URL jarFileURL = null;
  private URL jarFileURLOverride = null;
  private String entryName;
  private JarEntry jarEntry;
  private JarFile jarFile;
  private String contentType;
  private boolean useJarCache = false;
  private Map headerFields = new HashMap();

  public CachedJarURLConnection(URL paramURL, Handler paramHandler)
    throws MalformedURLException, IOException
  {
    super(paramURL, paramHandler);
    getJarFileURL();
    this.entryName = getEntryName();
  }

  public synchronized URL getJarFileURL()
  {
    if (this.jarFileURLOverride != null)
      return this.jarFileURLOverride;
    if (this.jarFileURL == null)
    {
      this.jarFileURL = super.getJarFileURL();
      try
      {
        this.jarFileURL = new URL(URLUtil.canonicalize(this.jarFileURL.toString()));
      }
      catch (MalformedURLException localMalformedURLException)
      {
      }
    }
    return this.jarFileURL;
  }

  public synchronized JarFile getJarFile()
    throws IOException
  {
    this.jarFile = getJarFileInternal();
    try
    {
      return (JarFile)AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Object run()
          throws Exception
        {
          if ((CachedJarURLConnection.this.jarFile instanceof CachedJarFile))
            try
            {
              return ((CachedJarFile)CachedJarURLConnection.this.jarFile).clone();
            }
            catch (CloneNotSupportedException localCloneNotSupportedException)
            {
              throw new IOException(localCloneNotSupportedException.getMessage());
            }
          String str = CachedJarURLConnection.this.jarFile.getName();
          if (new File(str).exists())
            return new JarFile(CachedJarURLConnection.this.jarFile.getName());
          return CachedJarURLConnection.this.jarFile;
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw new IOException(localPrivilegedActionException.getCause().getMessage());
    }
  }

  public synchronized JarFile getJarFileInternal()
    throws IOException
  {
    if (this.jarFile != null)
      return this.jarFile;
    if ((Cache.isCacheEnabled()) && (Cache.isSupportedProtocol(this.jarFileURL)))
    {
      String str1 = (String)AppContext.getAppContext().get(Config.getAppContextKeyPrefix() + this.jarFileURL.toString());
      if (str1 != null)
      {
        this.jarFileURLOverride = new URL(URLUtil.canonicalize(this.jarFileURL.toString() + "?" + URLEncoder.encode("version-id", "UTF-8") + "=" + URLEncoder.encode(str1, "UTF-8")));
        String str2 = Cache.getCacheEntryVersion(this.jarFileURL, null);
        if ((str2 != null) && (!str2.equals(str1)))
          this.jarFileURLOverride = new URL(this.jarFileURLOverride.toString() + "&" + URLEncoder.encode("current-version-id", "UTF-8") + "=" + URLEncoder.encode(str2, "UTF-8"));
      }
    }
    connect();
    this.jarFileURLOverride = null;
    return this.jarFile;
  }

  public JarEntry getJarEntry()
    throws IOException
  {
    connect();
    return this.jarEntry;
  }

  public String getHeaderField(String paramString)
  {
    String str = null;
    try
    {
      connect();
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
    List localList = (List)this.headerFields.get(paramString);
    if (localList != null)
      str = (String)localList.get(0);
    return str;
  }

  public void connect()
    throws IOException
  {
    if (!this.connected)
    {
      if ((Cache.isSupportedProtocol(this.jarFileURL)) && (Cache.isCacheEnabled()))
      {
        URL localURL = new URL(this.jarFileURL.getProtocol(), this.jarFileURL.getHost(), this.jarFileURL.getPort(), this.jarFileURL.getPath());
        String str = (String)AppContext.getAppContext().get(Config.getAppContextKeyPrefix() + localURL.toString());
        setUseCaches(false);
        super.connect();
        this.jarFile = DownloadEngine.getCachedJarFile(localURL, str);
        this.headerFields = DownloadEngine.getCachedHeaders(localURL, null, str, null, false);
        Cache.addLoadedResource(localURL, null, str);
        if (this.jarFile != null)
          this.useJarCache = true;
        else
          this.jarFile = super.getJarFile();
      }
      else
      {
        super.connect();
        this.jarFile = super.getJarFile();
      }
      if (this.entryName != null)
      {
        this.jarEntry = this.jarFile.getJarEntry(this.entryName);
        if (this.jarEntry == null)
          throw new FileNotFoundException("JAR entry " + this.entryName + " not found in " + this.jarFile.getName());
      }
      this.connected = true;
    }
  }

  public InputStream getInputStream()
    throws IOException
  {
    connect();
    if (!this.useJarCache)
      return super.getInputStream();
    InputStream localInputStream = null;
    if (this.entryName == null)
      throw new IOException("no entry name specified");
    if (this.jarEntry == null)
      throw new FileNotFoundException("JAR entry " + this.entryName + " not found in " + this.jarFile.getName());
    localInputStream = this.jarFile.getInputStream(this.jarEntry);
    return localInputStream;
  }

  public Object getContent()
    throws IOException
  {
    Object localObject = null;
    connect();
    if (!this.useJarCache)
      return super.getContent();
    if (this.entryName == null)
      localObject = getJarFile();
    else
      localObject = super.getContent();
    return localObject;
  }

  public String getContentType()
  {
    if (!this.connected)
      try
      {
        connect();
      }
      catch (IOException localIOException1)
      {
      }
    if (!this.useJarCache)
      return super.getContentType();
    if (this.contentType == null)
    {
      if (this.entryName == null)
        this.contentType = "x-java/jar";
      else
        try
        {
          connect();
          InputStream localInputStream = getJarFileInternal().getInputStream(this.jarEntry);
          this.contentType = guessContentTypeFromStream(new BufferedInputStream(localInputStream));
          localInputStream.close();
        }
        catch (IOException localIOException2)
        {
        }
      if (this.contentType == null)
        this.contentType = guessContentTypeFromName(this.entryName);
      if (this.contentType == null)
        this.contentType = "content/unknown";
    }
    return this.contentType;
  }

  public int getContentLength()
  {
    if (!this.connected)
      try
      {
        connect();
      }
      catch (IOException localIOException)
      {
      }
    if (!this.useJarCache)
      return super.getContentLength();
    if (this.jarEntry != null)
      return (int)this.jarEntry.getSize();
    return -1;
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.net.protocol.jar.CachedJarURLConnection
 * JD-Core Version:    0.6.2
 */