package sun.plugin.util;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

public class AnimationPanel extends Canvas
  implements Runnable
{
  private static final String LOGO_IMAGE_LARGE = "sun/plugin/util/JavaCupLogo-161.png";
  private static final String WATERMARK_IMAGE = "sun/plugin/util/java-watermark.gif";
  private static final double TWO_PI = 6.283185307179586D;
  private static final double HALF_PI = 1.570796326794897D;
  private static final Color clrBorder = new Color(153, 153, 153);
  private static final Color clrBg1 = new Color(255, 255, 255, 0);
  private static final Color clrBg2 = Color.white;
  private static final Color clrRay1 = Color.white;
  private static final Color clrRay2 = new Color(180, 180, 180);
  private static final Color clrProgBg = Color.white;
  private static final Color clrProgBorder = Color.lightGray;
  private static final Color clrProgBar = new Color(231, 111, 0);
  private static final int MAX_INNER_DIAMETER = 169;
  private static final int MAX_OUTER_DIAMETER = 304;
  private int numRays;
  private int rayStart;
  private int rayEnd;
  private int rayWidth;
  private int progressWidth;
  private int progressHeight;
  private int progressGap;
  private int progressYOff;
  private Polygon[] burstPoints;
  private BufferedImage background;
  private Image logo;
  private Image backbuffer;
  private float burstProgress;
  private float loadingProgress = 0.0F;
  private int width;
  private int height;
  private boolean showBurst;
  private boolean showLogoAndBar;
  private boolean showWatermark;
  private long startTime = System.currentTimeMillis();
  public static boolean animationThreadRunning = false;

  public AnimationPanel(Dimension paramDimension)
  {
    this.width = paramDimension.width;
    this.height = paramDimension.height;
  }

  public void setProgressValue(float paramFloat)
  {
    this.loadingProgress = paramFloat;
  }

  private void initDimensions(int paramInt1, int paramInt2)
  {
    int i = Math.min(paramInt1, paramInt2);
    this.numRays = 18;
    this.showBurst = (i >= 25);
    this.showLogoAndBar = (i >= 170);
    this.showWatermark = (i >= 400);
    int k;
    int j;
    if (this.showWatermark)
    {
      k = 169;
      j = 304;
    }
    else if (this.showLogoAndBar)
    {
      j = i - 8;
      if (j > 304)
      {
        j = 304;
        k = 169;
      }
      else
      {
        k = (int)(j / 1.8D);
      }
    }
    else
    {
      j = i - 4;
      k = (int)(i * 0.35D);
      if (i < 100)
        this.numRays = 9;
      else
        this.numRays = 12;
    }
    this.rayStart = (k / 2);
    this.rayEnd = (j / 2);
    this.rayWidth = Math.max(j / 40 + 1, 2);
  }

  private void initBackgroundImage(GraphicsConfiguration paramGraphicsConfiguration, int paramInt1, int paramInt2)
  {
    this.background = paramGraphicsConfiguration.createCompatibleImage(paramInt1, paramInt2);
    Graphics2D localGraphics2D = this.background.createGraphics();
    Toolkit localToolkit = Toolkit.getDefaultToolkit();
    Image localImage = localToolkit.createImage(ClassLoader.getSystemResource("sun/plugin/util/java-watermark.gif"));
    MediaTracker localMediaTracker = new MediaTracker(this);
    localMediaTracker.addImage(localImage, 0);
    try
    {
      localMediaTracker.waitForID(0);
    }
    catch (InterruptedException localInterruptedException)
    {
    }
    int i = localImage.getWidth(null);
    int j = localImage.getHeight(null);
    int k = 0;
    while (k < paramInt2)
    {
      int m = 0;
      while (m < paramInt1)
      {
        localGraphics2D.drawImage(localImage, m, k, null);
        m += i;
      }
      k += j;
    }
    localGraphics2D.setPaint(new GradientPaint(0.0F, 0.0F, clrBg1, paramInt1 / 2, paramInt2 / 2, clrBg2, true));
    localGraphics2D.fillRect(0, 0, paramInt1, paramInt2);
    localGraphics2D.fillRect(0, 0, paramInt1, paramInt2);
    localGraphics2D.fillRect(0, 0, paramInt1, paramInt2);
    localGraphics2D.fillRect(0, 0, paramInt1, paramInt2);
    localGraphics2D.dispose();
  }

  private void initLogoImage(GraphicsConfiguration paramGraphicsConfiguration)
  {
    String str = "sun/plugin/util/JavaCupLogo-161.png";
    int i = !this.showWatermark ? 1 : 0;
    Toolkit localToolkit = Toolkit.getDefaultToolkit();
    Image localImage = localToolkit.createImage(ClassLoader.getSystemResource(str));
    MediaTracker localMediaTracker = new MediaTracker(this);
    localMediaTracker.addImage(localImage, 0);
    try
    {
      localMediaTracker.waitForID(0);
    }
    catch (InterruptedException localInterruptedException)
    {
    }
    if (i != 0)
    {
      int j = localImage.getWidth(null);
      int k = localImage.getHeight(null);
      int m = this.rayStart * 2 - 8;
      int n = (int)(m / k * j);
      this.logo = paramGraphicsConfiguration.createCompatibleImage(n, m);
      Graphics2D localGraphics2D = (Graphics2D)this.logo.getGraphics();
      localGraphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
      localGraphics2D.drawImage(localImage, 0, 0, n, m, Color.white, null);
      localGraphics2D.dispose();
    }
    else
    {
      this.logo = localImage;
    }
    this.progressWidth = (this.logo.getWidth(null) * 2);
    this.progressHeight = 8;
    this.progressGap = 2;
    this.progressYOff = (this.rayStart + 10);
  }

  private void initBurst(int paramInt1, int paramInt2)
  {
    this.burstPoints = new Polygon[this.numRays];
    for (int i = 0; i < this.numRays; i++)
    {
      Polygon localPolygon = new Polygon();
      double d1 = i / this.numRays * 6.283185307179586D - 1.570796326794897D;
      double d2 = d1 + 1.570796326794897D;
      double d3 = Math.cos(d1);
      double d4 = Math.sin(d1);
      int j = paramInt1 + (int)(d3 * this.rayStart);
      int k = paramInt2 + (int)(d4 * this.rayStart);
      int m = paramInt1 + (int)(d3 * this.rayEnd);
      int n = paramInt2 + (int)(d4 * this.rayEnd);
      int i1 = (int)(Math.cos(d2) * this.rayWidth);
      int i2 = (int)(Math.sin(d2) * this.rayWidth);
      localPolygon.addPoint(j - i1, k - i2);
      localPolygon.addPoint(m, n);
      localPolygon.addPoint(j + i1, k + i2);
      this.burstPoints[i] = localPolygon;
    }
  }

  public void paint(Graphics paramGraphics)
  {
    if (!animationThreadRunning)
      doPaint(paramGraphics);
  }

  public void doPaint(Graphics paramGraphics)
  {
    int i = getWidth();
    int j = getHeight();
    if ((i == 0) || (j == 0))
      return;
    int k = i / 2;
    int m = j / 2;
    if ((this.backbuffer == null) || (i != this.width) || (j != this.height))
    {
      localObject = getGraphicsConfiguration();
      this.backbuffer = createImage(i, j);
      initDimensions(i, j);
      if (this.showWatermark)
        initBackgroundImage((GraphicsConfiguration)localObject, i, j);
      if (this.showLogoAndBar)
        initLogoImage((GraphicsConfiguration)localObject);
      initBurst(k, m);
      this.width = i;
      this.height = j;
    }
    Object localObject = (Graphics2D)this.backbuffer.getGraphics();
    renderBackground((Graphics2D)localObject, i, j);
    if (this.showBurst)
      renderBurst((Graphics2D)localObject, k, m);
    if (this.showLogoAndBar)
    {
      renderLogo((Graphics2D)localObject, k, m);
      renderProgress((Graphics2D)localObject, k - this.progressWidth / 2, m + this.progressYOff, this.progressWidth, this.progressHeight, this.progressGap);
    }
    ((Graphics2D)localObject).setColor(clrBorder);
    ((Graphics2D)localObject).drawRect(0, 0, i - 1, j - 1);
    ((Graphics2D)localObject).dispose();
    paramGraphics.drawImage(this.backbuffer, 0, 0, null);
  }

  public void update(Graphics paramGraphics)
  {
    paint(paramGraphics);
  }

  private void renderBackground(Graphics2D paramGraphics2D, int paramInt1, int paramInt2)
  {
    if (this.showWatermark)
    {
      paramGraphics2D.drawImage(this.background, 0, 0, null);
    }
    else
    {
      paramGraphics2D.setColor(Color.white);
      paramGraphics2D.fillRect(0, 0, paramInt1, paramInt2);
    }
  }

  private void renderBurst(Graphics2D paramGraphics2D, int paramInt1, int paramInt2)
  {
    paramGraphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    int i = this.burstPoints.length;
    int j = this.rayStart + (this.rayEnd - this.rayStart) / 2;
    for (int k = 0; k < i; k++)
    {
      double d1 = k / i;
      double d2 = d1 * 6.283185307179586D - 1.570796326794897D;
      double d3 = Math.cos(d2);
      double d4 = Math.sin(d2);
      double d5;
      if (d1 > this.burstProgress)
        d5 = 1.0D - (d1 - this.burstProgress);
      else
        d5 = this.burstProgress - d1;
      double d6 = j + d5 * this.rayEnd;
      float f1 = paramInt1 + (float)(d3 * this.rayStart);
      float f2 = paramInt2 + (float)(d4 * this.rayStart);
      float f3 = paramInt1 + (float)(d3 * d6);
      float f4 = paramInt2 + (float)(d4 * d6);
      GradientPaint localGradientPaint = new GradientPaint(f1, f2, clrRay1, f3, f4, clrRay2);
      paramGraphics2D.setPaint(localGradientPaint);
      paramGraphics2D.fillPolygon(this.burstPoints[k]);
    }
    paramGraphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
  }

  private void renderLogo(Graphics2D paramGraphics2D, int paramInt1, int paramInt2)
  {
    paramGraphics2D.drawImage(this.logo, paramInt1 - this.logo.getWidth(null) / 2, paramInt2 - this.logo.getHeight(null) / 2, null);
  }

  private void renderProgress(Graphics2D paramGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    paramGraphics2D.setColor(clrProgBg);
    paramGraphics2D.fillRect(paramInt1, paramInt2, paramInt3, paramInt4);
    paramGraphics2D.setColor(clrProgBorder);
    paramGraphics2D.drawRect(paramInt1, paramInt2, paramInt3, paramInt4);
    paramGraphics2D.setColor(clrProgBar);
    paramGraphics2D.fillRect(paramInt1 + paramInt5, paramInt2 + paramInt5, (int)(paramInt3 * this.loadingProgress) - (paramInt5 + 1), paramInt4 - (paramInt5 + 1));
  }

  public void startAnimation()
  {
    synchronized (this)
    {
      if (animationThreadRunning)
        return;
      animationThreadRunning = true;
    }
    new Thread(this).start();
  }

  public void stopAnimation()
  {
    synchronized (this)
    {
      animationThreadRunning = false;
    }
  }

  public void run()
  {
    while (true)
    {
      try
      {
        Thread.sleep(50L);
      }
      catch (Exception localException)
      {
      }
      long l = System.currentTimeMillis() - this.startTime;
      this.burstProgress = ((float)(l % 3000L) / 3000.0F);
      synchronized (this)
      {
        if (!animationThreadRunning)
          break;
      }
      if (isShowing())
        doPaint(getGraphics());
    }
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.util.AnimationPanel
 * JD-Core Version:    0.6.2
 */