package sun.plugin.cache;

import com.sun.deploy.net.DownloadEngine;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import sun.plugin.ClassLoaderInfo;
import sun.plugin.resources.ResourceHandler;
import sun.plugin.util.Trace;

public class JarCacheUtil
{
  public static synchronized void verifyJarVersions(URL paramURL, String paramString, HashMap paramHashMap)
    throws IOException, JarCacheVersionException
  {
    int i = 0;
    Iterator localIterator = paramHashMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str1 = (String)localIterator.next();
      String str2 = (String)paramHashMap.get(str1);
      URL localURL = new URL(paramURL, str1);
      Trace.msgNetPrintln("cache.version_checking", new Object[] { str1, str2 });
      if ((str2 != null) && (DownloadEngine.getCachedJarFile(localURL, str2) == null))
        i = 1;
    }
    if (i == 1)
      ClassLoaderInfo.markNotCachable(paramURL, paramString);
  }

  public static HashMap getJarsWithVersion(String paramString1, String paramString2, String paramString3)
    throws JarCacheVersionException
  {
    HashMap localHashMap = new HashMap();
    StringTokenizer localStringTokenizer1;
    StringTokenizer localStringTokenizer2;
    String str1;
    String str2;
    if ((paramString1 != null) && (paramString2 != null))
    {
      localStringTokenizer1 = new StringTokenizer(paramString1, ",", false);
      int i = localStringTokenizer1.countTokens();
      localStringTokenizer2 = new StringTokenizer(paramString2, ",", false);
      int j = localStringTokenizer2.countTokens();
      if (i != j)
        throw new JarCacheVersionException(ResourceHandler.getMessage("cache.version_attrib_error"));
      while (localStringTokenizer1.hasMoreTokens())
      {
        str1 = localStringTokenizer1.nextToken().trim();
        str2 = localStringTokenizer2.nextToken().trim();
        localHashMap.put(str1, str2);
      }
    }
    if (paramString3 != null)
    {
      localStringTokenizer1 = new StringTokenizer(paramString3, ",", false);
      while (localStringTokenizer1.hasMoreTokens())
      {
        String str3 = localStringTokenizer1.nextToken().trim();
        localStringTokenizer2 = new StringTokenizer(str3, ";", false);
        str1 = localStringTokenizer2.nextToken().trim();
        while (localStringTokenizer2.hasMoreTokens())
        {
          str2 = localStringTokenizer2.nextToken().trim();
          if (Pattern.matches("\\p{XDigit}{1,4}\\.\\p{XDigit}{1,4}\\.\\p{XDigit}{1,4}\\.\\p{XDigit}{1,4}", str2))
            localHashMap.put(str1, str2);
        }
      }
    }
    return localHashMap;
  }

  public static synchronized void preload(URL paramURL, HashMap paramHashMap)
    throws IOException
  {
    Iterator localIterator = paramHashMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str1 = (String)localIterator.next();
      String str2 = (String)paramHashMap.get(str1);
      URL localURL1 = new URL(paramURL, str1);
      URL localURL2 = null;
      if (str2 != null)
        localURL2 = new URL("jar:" + localURL1.toString() + "?version-id=" + str2 + "!/");
      else
        localURL2 = new URL("jar:" + localURL1.toString() + "!/");
      JarURLConnection localJarURLConnection = (JarURLConnection)localURL2.openConnection();
      localJarURLConnection.getContentLength();
    }
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.cache.JarCacheUtil
 * JD-Core Version:    0.6.2
 */