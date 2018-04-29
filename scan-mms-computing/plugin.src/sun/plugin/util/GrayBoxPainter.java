package sun.plugin.util;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import sun.net.ProgressEvent;
import sun.net.ProgressListener;

public class GrayBoxPainter
  implements ProgressListener
{
  private Color boxBGColor = Color.white;
  private Color boxFGColor = Color.black;
  private String waitingMessage = null;
  private Image customImage;
  private URL customImageURL;
  private URL codebaseURL;
  private URL[] jarURLs = new URL[0];
  private HashMap downloadInProgressMap = new HashMap();
  private boolean progressBarEnabled = true;
  private Container container;
  private ThreadGroup threadGroup;
  private MediaTracker tracker;
  private boolean appletErrorOccurred = false;
  private Color progressColor;
  private boolean animationReady = false;
  private boolean progressBarReady = false;
  private boolean paintingSuspended = false;
  private GrayBoxPanel m_grayboxPanel = null;
  private int currentProgress = 0;
  private int maximumProgress = 10000;
  private int numJarTotal = 0;
  private int numberOfJarLoaded = 0;
  private ArrayList progressSourceFilterList = new ArrayList();

  public GrayBoxPainter(Container paramContainer)
  {
    this.container = paramContainer;
  }

  public synchronized void beginPainting(ThreadGroup paramThreadGroup)
  {
    this.threadGroup = paramThreadGroup;
    this.tracker = new MediaTracker(this.container);
    loadCustomImage();
    if (this.progressBarEnabled)
    {
      localObject = (ProgressMonitor)sun.net.ProgressMonitor.getDefault();
      ((ProgressMonitor)localObject).addProgressListener(this.threadGroup, this);
    }
    Object localObject = this;
    this.paintingSuspended = false;
    new Thread(new Runnable()
    {
      public void run()
      {
        try
        {
          Thread.sleep(1500L);
        }
        catch (Throwable localThrowable)
        {
        }
        finally
        {
          this.val$painter.setAnimationReady();
          this.val$painter.setProgressBarReady();
          this.val$painter.repaintGrayBox();
        }
      }
    }).start();
  }

  public synchronized void finishPainting()
  {
    if (this.m_grayboxPanel != null)
    {
      this.m_grayboxPanel.stop();
      this.container.remove(this.m_grayboxPanel);
      this.m_grayboxPanel = null;
    }
    if (this.progressBarEnabled)
    {
      ProgressMonitor localProgressMonitor = (ProgressMonitor)sun.net.ProgressMonitor.getDefault();
      localProgressMonitor.removeProgressListener(this.threadGroup, this);
    }
    this.paintingSuspended = false;
  }

  public void showLoadingError()
  {
    this.appletErrorOccurred = true;
    repaintGrayBox();
  }

  private void loadCustomImage()
  {
    if (this.customImageURL != null)
      try
      {
        this.customImage = Toolkit.getDefaultToolkit().getImage(this.customImageURL);
        this.tracker.addImage(this.customImage, 1);
        this.tracker.waitForID(1);
      }
      catch (InterruptedException localInterruptedException)
      {
        localInterruptedException.printStackTrace();
      }
  }

  public void setProgressFilter(URL paramURL, String paramString)
  {
    this.codebaseURL = paramURL;
    if (paramString != null)
    {
      ArrayList localArrayList = new ArrayList();
      URL[] arrayOfURL = new URL[0];
      StringTokenizer localStringTokenizer = new StringTokenizer(paramString, ",", false);
      while (localStringTokenizer.hasMoreTokens())
      {
        String str = localStringTokenizer.nextToken().trim();
        try
        {
          URL localURL = new URL(paramURL, str);
          localArrayList.add(localURL);
        }
        catch (MalformedURLException localMalformedURLException)
        {
          localMalformedURLException.printStackTrace();
        }
      }
      arrayOfURL = new URL[localArrayList.size()];
      int i = 0;
      Iterator localIterator = localArrayList.iterator();
      while (localIterator.hasNext())
      {
        arrayOfURL[i] = ((URL)localIterator.next());
        i++;
      }
      this.jarURLs = arrayOfURL;
      this.numJarTotal = arrayOfURL.length;
    }
  }

  public void setCustomImageURL(URL paramURL)
  {
    this.progressBarEnabled = false;
    this.customImageURL = paramURL;
  }

  public void progressStart(ProgressEvent paramProgressEvent)
  {
    synchronized (this.progressSourceFilterList)
    {
      if (this.progressSourceFilterList.contains(paramProgressEvent.getSource()))
        return;
      if (this.numJarTotal > 0)
        for (int i = 0; i < this.jarURLs.length; i++)
          if (paramProgressEvent.getURL().equals(this.jarURLs[i]))
          {
            this.progressSourceFilterList.add(paramProgressEvent.getSource());
            synchronized (this.downloadInProgressMap)
            {
              this.downloadInProgressMap.put(paramProgressEvent.getURL(), paramProgressEvent);
            }
            break;
          }
      else if (paramProgressEvent.getURL().toString().startsWith(this.codebaseURL.toString()))
        this.progressSourceFilterList.add(paramProgressEvent.getSource());
    }
  }

  public void progressUpdate(ProgressEvent paramProgressEvent)
  {
    synchronized (this.progressSourceFilterList)
    {
      if (this.progressSourceFilterList.size() == 0)
        progressStart(paramProgressEvent);
      if (!this.progressSourceFilterList.contains(paramProgressEvent.getSource()))
        return;
    }
    if (this.numJarTotal > 0)
    {
      synchronized (this.downloadInProgressMap)
      {
        this.downloadInProgressMap.put(paramProgressEvent.getURL(), paramProgressEvent);
        int i = this.maximumProgress / this.numJarTotal;
        int j = i * this.numberOfJarLoaded;
        Iterator localIterator = this.downloadInProgressMap.values().iterator();
        while (localIterator.hasNext())
        {
          ProgressEvent localProgressEvent = (ProgressEvent)localIterator.next();
          if (localProgressEvent.getExpected() != -1)
            j = (int)(j + i / localProgressEvent.getExpected() * localProgressEvent.getProgress());
          else
            j += i / 2;
        }
        this.currentProgress = j;
      }
      repaintGrayBox();
    }
  }

  public void progressFinish(ProgressEvent paramProgressEvent)
  {
    synchronized (this.progressSourceFilterList)
    {
      if (!this.progressSourceFilterList.contains(paramProgressEvent.getSource()))
        return;
      this.progressSourceFilterList.remove(paramProgressEvent.getSource());
    }
    if (paramProgressEvent.getProgress() == 0)
    {
      this.downloadInProgressMap.remove(paramProgressEvent.getURL());
      return;
    }
    if (this.numJarTotal > 0)
    {
      synchronized (this.downloadInProgressMap)
      {
        this.downloadInProgressMap.remove(paramProgressEvent.getURL());
        this.numberOfJarLoaded += 1;
        if (this.numJarTotal == this.numberOfJarLoaded)
        {
          this.currentProgress = this.maximumProgress;
        }
        else
        {
          int j = this.maximumProgress / this.numJarTotal;
          int k = j * this.numberOfJarLoaded;
          Iterator localIterator = this.downloadInProgressMap.values().iterator();
          while (localIterator.hasNext())
          {
            ProgressEvent localProgressEvent = (ProgressEvent)localIterator.next();
            if (localProgressEvent.getExpected() != -1)
              k = (int)(k + j / localProgressEvent.getExpected() * localProgressEvent.getProgress());
            else
              k += j / 2;
          }
          this.currentProgress = k;
        }
      }
    }
    else
    {
      int i = (this.maximumProgress - this.currentProgress) / 2;
      this.currentProgress += i;
    }
    repaintGrayBox();
  }

  public void setBoxBGColor(Color paramColor)
  {
    this.boxBGColor = paramColor;
  }

  public Color getBoxBGColor()
  {
    return this.boxBGColor;
  }

  public void setBoxFGColor(Color paramColor)
  {
    this.boxFGColor = paramColor;
  }

  public void setProgressColor(Color paramColor)
  {
    this.progressColor = paramColor;
  }

  public void setWaitingMessage(String paramString)
  {
    this.waitingMessage = paramString;
  }

  public void enableProgressBar(boolean paramBoolean)
  {
    this.progressBarEnabled = paramBoolean;
  }

  public synchronized void suspendPainting()
  {
    if (!this.paintingSuspended)
    {
      if (this.m_grayboxPanel != null)
      {
        this.m_grayboxPanel.stop();
        this.container.remove(this.m_grayboxPanel);
      }
      this.paintingSuspended = true;
      this.container.validate();
    }
  }

  public synchronized void resumePainting()
  {
    if (this.paintingSuspended)
    {
      if (this.m_grayboxPanel != null)
      {
        this.m_grayboxPanel.start();
        this.container.add(this.m_grayboxPanel, "Center");
      }
      this.paintingSuspended = false;
      this.container.validate();
    }
  }

  public void setAnimationReady()
  {
    this.animationReady = true;
  }

  public void setProgressBarReady()
  {
    this.progressBarReady = true;
  }

  private void repaintGrayBox()
  {
    paintGrayBox(this.container, this.container.getGraphics());
  }

  private synchronized GrayBoxPanel getGrayBoxPanel()
  {
    if (this.m_grayboxPanel == null)
    {
      this.m_grayboxPanel = new GrayBoxPanel(this.container);
      this.container.add(this.m_grayboxPanel, "Center");
      if (!this.appletErrorOccurred)
      {
        if (this.customImage == null)
        {
          this.m_grayboxPanel.setMaxProgressValue(this.maximumProgress);
          this.m_grayboxPanel.start();
        }
        else
        {
          this.m_grayboxPanel.setCustomImage(this.customImage);
        }
      }
      else
        this.m_grayboxPanel.setError();
      this.container.validate();
    }
    return this.m_grayboxPanel;
  }

  public synchronized void paintGrayBox(Container paramContainer, Graphics paramGraphics)
  {
    if ((this.paintingSuspended) || (paramGraphics == null))
      return;
    Dimension localDimension = this.container.getSize();
    if ((!this.animationReady) && (!this.appletErrorOccurred))
    {
      if ((localDimension.width > 0) && (localDimension.height > 0))
      {
        paramGraphics.setColor(Color.white);
        paramGraphics.fillRect(0, 0, localDimension.width, localDimension.height);
      }
      return;
    }
    if ((localDimension.width > 0) && (localDimension.height > 0))
    {
      GrayBoxPanel localGrayBoxPanel = getGrayBoxPanel();
      if (this.appletErrorOccurred)
      {
        localGrayBoxPanel.setError();
        localGrayBoxPanel.paint(paramGraphics);
      }
      else
      {
        localGrayBoxPanel.progress(this.currentProgress);
      }
    }
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.util.GrayBoxPainter
 * JD-Core Version:    0.6.2
 */