package sun.plugin.util;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import sun.net.ProgressEvent;
import sun.net.ProgressListener;
import sun.net.ProgressSource;

public class ProgressMonitor extends sun.net.ProgressMonitor
{
  private ArrayList progressSourceList = new ArrayList();
  private HashMap threadGroupListenerMap = new HashMap();

  public ArrayList getProgressSources()
  {
    ArrayList localArrayList = new ArrayList();
    try
    {
      synchronized (this.progressSourceList)
      {
        Iterator localIterator = this.progressSourceList.iterator();
        while (localIterator.hasNext())
        {
          ProgressSource localProgressSource = (ProgressSource)localIterator.next();
          localArrayList.add((ProgressSource)localProgressSource.clone());
        }
      }
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      localCloneNotSupportedException.printStackTrace();
    }
    return localArrayList;
  }

  public int getProgressUpdateThreshold()
  {
    return 65536;
  }

  public boolean shouldMeterInput(URL paramURL, String paramString)
  {
    Thread localThread = Thread.currentThread();
    ThreadGroup localThreadGroup = localThread.getThreadGroup();
    ProgressListener localProgressListener = null;
    synchronized (this.threadGroupListenerMap)
    {
      localProgressListener = (ProgressListener)this.threadGroupListenerMap.get(new Integer(localThreadGroup.hashCode()));
    }
    if (localProgressListener == null)
      return false;
    ??? = paramURL.getProtocol();
    return ((((String)???).equalsIgnoreCase("http")) || (((String)???).equalsIgnoreCase("https")) || (((String)???).equalsIgnoreCase("file"))) && (paramString.equalsIgnoreCase("GET"));
  }

  public void registerSource(ProgressSource paramProgressSource)
  {
    synchronized (this.progressSourceList)
    {
      if (this.progressSourceList.contains(paramProgressSource))
        return;
      this.progressSourceList.add(paramProgressSource);
    }
    ??? = Thread.currentThread();
    ThreadGroup localThreadGroup = ((Thread)???).getThreadGroup();
    ProgressListener localProgressListener = null;
    synchronized (this.threadGroupListenerMap)
    {
      localProgressListener = (ProgressListener)this.threadGroupListenerMap.get(new Integer(localThreadGroup.hashCode()));
    }
    if (localProgressListener != null)
    {
      ??? = new ProgressEvent(paramProgressSource, paramProgressSource.getURL(), paramProgressSource.getMethod(), paramProgressSource.getContentType(), paramProgressSource.getState(), paramProgressSource.getProgress(), paramProgressSource.getExpected());
      localProgressListener.progressStart((ProgressEvent)???);
    }
  }

  public void unregisterSource(ProgressSource paramProgressSource)
  {
    synchronized (this.progressSourceList)
    {
      if (!this.progressSourceList.contains(paramProgressSource))
        return;
      paramProgressSource.close();
      this.progressSourceList.remove(paramProgressSource);
    }
    ??? = Thread.currentThread();
    ThreadGroup localThreadGroup = ((Thread)???).getThreadGroup();
    ProgressListener localProgressListener = null;
    synchronized (this.threadGroupListenerMap)
    {
      localProgressListener = (ProgressListener)this.threadGroupListenerMap.get(new Integer(localThreadGroup.hashCode()));
    }
    if (localProgressListener != null)
    {
      ??? = new ProgressEvent(paramProgressSource, paramProgressSource.getURL(), paramProgressSource.getMethod(), paramProgressSource.getContentType(), paramProgressSource.getState(), paramProgressSource.getProgress(), paramProgressSource.getExpected());
      localProgressListener.progressFinish((ProgressEvent)???);
    }
  }

  public void updateProgress(ProgressSource paramProgressSource)
  {
    synchronized (this.progressSourceList)
    {
      if (!this.progressSourceList.contains(paramProgressSource))
        return;
    }
    ??? = Thread.currentThread();
    ThreadGroup localThreadGroup = ((Thread)???).getThreadGroup();
    ProgressListener localProgressListener = null;
    synchronized (this.threadGroupListenerMap)
    {
      localProgressListener = (ProgressListener)this.threadGroupListenerMap.get(new Integer(localThreadGroup.hashCode()));
    }
    if (localProgressListener != null)
    {
      ??? = new ProgressEvent(paramProgressSource, paramProgressSource.getURL(), paramProgressSource.getMethod(), paramProgressSource.getContentType(), paramProgressSource.getState(), paramProgressSource.getProgress(), paramProgressSource.getExpected());
      localProgressListener.progressUpdate((ProgressEvent)???);
    }
  }

  public void addProgressListener(ThreadGroup paramThreadGroup, ProgressListener paramProgressListener)
  {
    Trace.msgPrintln("progress.listener.added", new Object[] { paramProgressListener });
    synchronized (this.threadGroupListenerMap)
    {
      ProgressListener localProgressListener = (ProgressListener)this.threadGroupListenerMap.get(new Integer(paramThreadGroup.hashCode()));
      localProgressListener = EventMulticaster.add(localProgressListener, paramProgressListener);
      this.threadGroupListenerMap.put(new Integer(paramThreadGroup.hashCode()), localProgressListener);
    }
  }

  public void removeProgressListener(ThreadGroup paramThreadGroup, ProgressListener paramProgressListener)
  {
    Trace.msgPrintln("progress.listener.removed", new Object[] { paramProgressListener });
    synchronized (this.threadGroupListenerMap)
    {
      ProgressListener localProgressListener = (ProgressListener)this.threadGroupListenerMap.get(new Integer(paramThreadGroup.hashCode()));
      localProgressListener = EventMulticaster.remove(localProgressListener, paramProgressListener);
      if (localProgressListener != null)
        this.threadGroupListenerMap.put(new Integer(paramThreadGroup.hashCode()), localProgressListener);
      else
        this.threadGroupListenerMap.remove(new Integer(paramThreadGroup.hashCode()));
    }
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.util.ProgressMonitor
 * JD-Core Version:    0.6.2
 */