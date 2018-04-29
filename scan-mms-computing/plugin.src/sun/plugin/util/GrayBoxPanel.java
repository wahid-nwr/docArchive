package sun.plugin.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

class GrayBoxPanel extends Container
  implements MouseListener, ComponentListener
{
  private static final String ERROR_IMAGE_FILE = "sun/plugin/util/graybox_error.gif";
  private static final Color LOADING_BORDER = new Color(153, 153, 153);
  private static final Color ERROR_BORDER = new Color(204, 204, 204);
  private static final Color BACKGROUND_COLOR = Color.white;
  private static Image ERROR_IMAGE = null;
  private AnimationPanel m_panel = null;
  private Container m_parent;
  private int m_maxValue;
  private Image m_image = null;
  private boolean m_error = false;

  public GrayBoxPanel(Container paramContainer)
  {
    this.m_parent = paramContainer;
    setBackground(Color.white);
    setForeground(Color.white);
    setLayout(new BorderLayout());
  }

  public void setCustomImage(Image paramImage)
  {
    setImage(paramImage);
  }

  private synchronized Image getErrorImage()
  {
    if (ERROR_IMAGE == null)
    {
      Toolkit localToolkit = Toolkit.getDefaultToolkit();
      ERROR_IMAGE = localToolkit.createImage(ClassLoader.getSystemResource("sun/plugin/util/graybox_error.gif"));
      MediaTracker localMediaTracker = new MediaTracker(this);
      localMediaTracker.addImage(ERROR_IMAGE, 0);
      try
      {
        localMediaTracker.waitForID(0);
      }
      catch (InterruptedException localInterruptedException)
      {
      }
    }
    return ERROR_IMAGE;
  }

  public void setError()
  {
    if (this.m_error)
      return;
    this.m_error = true;
    setImage(getErrorImage());
  }

  public void setMaxProgressValue(int paramInt)
  {
    this.m_maxValue = paramInt;
  }

  public void progress(int paramInt)
  {
    if (this.m_panel != null)
      this.m_panel.setProgressValue(paramInt / this.m_maxValue);
    else
      paint(this.m_parent.getGraphics());
  }

  public void start()
  {
    Dimension localDimension = this.m_parent.getSize();
    if (this.m_panel == null)
    {
      this.m_panel = new AnimationPanel(localDimension);
      this.m_panel.setCursor(new Cursor(12));
      this.m_panel.addMouseListener(this);
      add(this.m_panel, "Center");
    }
    this.m_panel.startAnimation();
    this.m_parent.addComponentListener(this);
  }

  public void stop()
  {
    if (this.m_panel != null)
      this.m_panel.stopAnimation();
    this.m_parent.removeComponentListener(this);
  }

  public void paint(Graphics paramGraphics)
  {
    Dimension localDimension = this.m_parent.getSize();
    if (this.m_panel != null)
    {
      this.m_panel.repaint();
    }
    else
    {
      paramGraphics.setColor(BACKGROUND_COLOR);
      paramGraphics.fillRect(0, 0, localDimension.width, localDimension.height);
      if ((localDimension.width > 24) && (localDimension.height > 24))
      {
        if (this.m_error)
          drawImage(paramGraphics, getErrorImage(), 4, 4);
        else
          drawImage(paramGraphics, this.m_image, 1, 1);
      }
      else if (!this.m_error)
        drawImage(paramGraphics, this.m_image, 0, 0);
    }
    if ((localDimension.width > 24) && (localDimension.height > 24))
      drawBorder(paramGraphics, localDimension);
  }

  private void drawImage(Graphics paramGraphics, Image paramImage, int paramInt1, int paramInt2)
  {
    paramGraphics.drawImage(paramImage, paramInt1, paramInt2, BACKGROUND_COLOR, null);
  }

  private void drawBorder(Graphics paramGraphics, Dimension paramDimension)
  {
    Color localColor1 = this.m_error ? ERROR_BORDER : LOADING_BORDER;
    Color localColor2 = paramGraphics.getColor();
    paramGraphics.setColor(localColor1);
    paramGraphics.drawRect(0, 0, paramDimension.width - 1, paramDimension.height - 1);
    paramGraphics.setColor(localColor2);
  }

  private synchronized void setImage(Image paramImage)
  {
    if (this.m_panel != null)
    {
      this.m_panel.stopAnimation();
      remove(this.m_panel);
      this.m_panel = null;
    }
    this.m_image = paramImage;
    paint(this.m_parent.getGraphics());
  }

  public void mouseClicked(MouseEvent paramMouseEvent)
  {
  }

  public void mouseEntered(MouseEvent paramMouseEvent)
  {
  }

  public void mouseExited(MouseEvent paramMouseEvent)
  {
  }

  public void mousePressed(MouseEvent paramMouseEvent)
  {
  }

  public void mouseReleased(MouseEvent paramMouseEvent)
  {
  }

  public void componentHidden(ComponentEvent paramComponentEvent)
  {
  }

  public void componentMoved(ComponentEvent paramComponentEvent)
  {
  }

  public void componentResized(ComponentEvent paramComponentEvent)
  {
    Dimension localDimension = this.m_parent.getSize();
    setSize(localDimension);
    if (this.m_panel != null)
      this.m_panel.setSize(localDimension);
  }

  public void componentShown(ComponentEvent paramComponentEvent)
  {
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.util.GrayBoxPanel
 * JD-Core Version:    0.6.2
 */