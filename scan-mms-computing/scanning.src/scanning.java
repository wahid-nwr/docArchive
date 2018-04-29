/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.plugin.PlugIn;
/*     */ import java.awt.Color;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Image;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.RenderingHints;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.RasterFormatException;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ import javax.imageio.ImageIO;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.JApplet;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JFileChooser;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JRootPane;
/*     */ import javax.swing.JSlider;
/*     */ import javax.swing.JToolBar;
/*     */ import javax.swing.border.LineBorder;
/*     */ import javax.swing.event.ChangeEvent;
/*     */ import javax.swing.event.ChangeListener;
/*     */ import javax.swing.event.MouseInputAdapter;
/*     */ import uk.co.mmscomputing.concurrent.Semaphore;
/*     */ import uk.co.mmscomputing.device.scanner.Scanner;
/*     */ import uk.co.mmscomputing.device.scanner.ScannerDevice;
/*     */ import uk.co.mmscomputing.device.scanner.ScannerIOException;
/*     */ import uk.co.mmscomputing.device.scanner.ScannerIOMetadata;
/*     */ import uk.co.mmscomputing.device.scanner.ScannerIOMetadata.Type;
/*     */ import uk.co.mmscomputing.device.scanner.ScannerListener;
/*     */ import uk.co.mmscomputing.device.twain.TwainSource;
/*     */ 
/*     */ public class scanning extends JApplet
/*     */   implements PlugIn, ScannerListener
/*     */ {
/* 102 */   private JToolBar jtoolbar = new JToolBar("Toolbar", 0);
/*     */   scanning.ImagePanel ipanel;
/* 104 */   Image im = null;
/*     */   BufferedImage imageforCrop;
/* 106 */   ImagePlus imp = null;
/*     */   int imageWidth;
/*     */   int imageHeight;
/*     */   private static final long serialVersionUID = 1L;
/* 110 */   Container content = null;
/* 111 */   private JPanel jContentPane = null;
/* 112 */   private JButton jButton = null;
/* 113 */   private JButton jButton1 = null;
/* 114 */   JCheckBox clipBox = null;
/* 115 */   JPanel crpdpanel = null;
/* 116 */   JPanel cpanel = null;
/* 117 */   private Scanner scanner = null;
/*     */   TwainSource ts;
/*     */   scanning.ImagePanel imagePanel;
/*     */   scanning.ImagePanel imagePanel2;
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/* 126 */     new scanning().setVisible(true);
/*     */   }
/*     */ 
/*     */   public void run(String arg0)
/*     */   {
/* 131 */     new scanning().setVisible(false);
/* 132 */     repaint();
/*     */   }
/*     */ 
/*     */   public scanning()
/*     */   {
/* 140 */     init();
/*     */     try {
/* 142 */       this.scanner = Scanner.getDevice();
/* 143 */       this.scanner.addListener(this);
/*     */     }
/*     */     catch (Exception e) {
/* 146 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void init()
/*     */   {
/* 159 */     setSize(1200, 600);
/* 160 */     setLayout(null);
/*     */ 
/* 162 */     setContentPane(getJContentPane());
/*     */   }
/*     */ 
/*     */   public void stop()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void destroy()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void start()
/*     */   {
/*     */   }
/*     */ 
/*     */   private JToolBar getJToolBar()
/*     */   {
/* 185 */     this.jtoolbar.add(getJButton1());
/* 186 */     this.jtoolbar.add(getJButton());
/*     */ 
/* 189 */     this.jtoolbar.setName("My Toolbar");
/* 190 */     this.jtoolbar.addSeparator();
/* 191 */     Rectangle r = new Rectangle(0, 0, 1024, 30);
/* 192 */     this.jtoolbar.setBounds(r);
/* 193 */     this.jtoolbar.setBackground(Color.white);
/* 194 */     return this.jtoolbar;
/*     */   }
/*     */ 
/*     */   private JPanel getJContentPane()
/*     */   {
/* 199 */     if (this.jContentPane == null)
/*     */     {
/* 201 */       this.jContentPane = new JPanel();
/* 202 */       this.jContentPane.setLayout(null);
/* 203 */       this.jContentPane.setBackground(Color.white);
/* 204 */       this.jContentPane.add(getJToolBar());
/*     */     }
/* 206 */     return this.jContentPane;
/*     */   }
/*     */ 
/*     */   private JButton getJButton()
/*     */   {
/* 211 */     if (this.jButton == null) {
/* 212 */       this.jButton = new JButton();
/* 213 */       this.jButton.setBounds(new Rectangle(4, 16, 131, 42));
/* 214 */       this.jButton.setText("Select Device");
/* 215 */       this.jButton.setBackground(Color.white);
/* 216 */       this.jButton.addActionListener(new ActionListener() {
/*     */         public void actionPerformed(ActionEvent e) {
/* 218 */           if (!scanning.this.scanner.isBusy()) {
/* 219 */             scanning.this.selectDevice();
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */ 
/* 225 */     return this.jButton;
/*     */   }
/*     */ 
/*     */   public void selectDevice()
/*     */   {
/*     */     try
/*     */     {
/* 233 */       this.scanner.select();
/*     */     } catch (ScannerIOException e1) {
/* 235 */       IJ.error(e1.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   private JButton getJButton1()
/*     */   {
/* 242 */     if (this.jButton1 == null) {
/* 243 */       this.jButton1 = new JButton();
/* 244 */       this.jButton1.setBounds(new Rectangle(35, 0, 30, 30));
/* 245 */       this.jButton1.setText("Scan");
/* 246 */       this.jButton1.setBackground(Color.white);
/* 247 */       this.jButton1.addActionListener(new ActionListener()
/*     */       {
/*     */         public void actionPerformed(ActionEvent e) {
/* 250 */           scanning.this.getScan();
/*     */         }
/*     */       });
/*     */     }
/* 254 */     return this.jButton1;
/*     */   }
/*     */ 
/*     */   public void getScan()
/*     */   {
/*     */     try
/*     */     {
/* 262 */       this.scanner.acquire();
/*     */     }
/*     */     catch (ScannerIOException e1)
/*     */     {
/* 266 */       IJ.showMessage("Access denied! \nTwain dialog maybe already opened!");
/* 267 */       e1.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public Image getImage()
/*     */   {
/* 275 */     Image image = this.imp.getImage();
/* 276 */     return image;
/*     */   }
/*     */ 
/*     */   public void update(ScannerIOMetadata.Type type, ScannerIOMetadata metadata)
/*     */   {
/* 285 */     if (type.equals(ScannerIOMetadata.ACQUIRED))
/*     */     {
/* 288 */       if (this.imp != null)
/*     */       {
/* 290 */         this.jContentPane.remove(this.ipanel);
/* 291 */         this.jContentPane.remove(this.cpanel);
/* 292 */         this.jContentPane.remove(this.crpdpanel);
/*     */       }
/*     */ 
/* 296 */       this.imp = new ImagePlus("Scan", metadata.getImage());
/*     */ 
/* 298 */       this.im = this.imp.getImage();
/*     */ 
/* 300 */       this.imagePanel = new scanning.ImagePanel(this.im);
/* 301 */       this.imagePanel.updateUI();
/*     */ 
/* 303 */       this.imagePanel.repaint();
/* 304 */       this.imagePanel.revalidate();
/*     */ 
/* 306 */       scanning.ClipMover mover = new scanning.ClipMover(this.imagePanel);
/* 307 */       this.imagePanel.addMouseListener(mover);
/* 308 */       this.imagePanel.addMouseMotionListener(mover);
/*     */ 
/* 310 */       this.ipanel = this.imagePanel.getPanel();
/*     */ 
/* 312 */       this.ipanel.setBorder(new LineBorder(Color.blue, 1));
/* 313 */       this.ipanel.setBorder(BorderFactory.createTitledBorder("Scanned Image"));
/* 314 */       this.ipanel.setBounds(0, 30, 600, 600);
/* 315 */       this.ipanel.repaint();
/* 316 */       this.ipanel.revalidate();
/* 317 */       this.ipanel.updateUI();
/* 318 */       this.jContentPane.add(this.ipanel);
/* 319 */       this.jContentPane.getRootPane().revalidate();
/* 320 */       this.jContentPane.updateUI();
/*     */ 
/* 322 */       this.cpanel = this.imagePanel.getUIPanel();
/* 323 */       this.cpanel.setBounds(700, 30, 300, 150);
/* 324 */       this.cpanel.repaint();
/* 325 */       this.cpanel.setBorder(new LineBorder(Color.blue, 1));
/* 326 */       this.cpanel.setBorder(BorderFactory.createTitledBorder("Cropping Image"));
/* 327 */       this.cpanel.setBackground(Color.white);
/* 328 */       this.jContentPane.add(this.cpanel);
/*     */ 
/* 331 */       this.jContentPane.repaint();
/* 332 */       this.jContentPane.revalidate();
/*     */ 
/* 337 */       metadata.setImage(null);
/*     */       try {
/* 339 */         new Semaphore(0, true).tryAcquire(2000L, null);
/*     */       } catch (InterruptedException e) {
/* 341 */         IJ.error(e.getMessage());
/*     */       }
/*     */ 
/*     */     }
/* 350 */     else if (type.equals(ScannerIOMetadata.NEGOTIATE)) {
/* 351 */       ScannerDevice device = metadata.getDevice();
/*     */       try {
/* 353 */         device.setResolution(100.0D);
/*     */       } catch (ScannerIOException e) {
/* 355 */         IJ.error(e.getMessage());
/*     */       }
/*     */ 
/*     */       try
/*     */       {
/* 360 */         device.setShowUserInterface(true);
/* 361 */         device.setResolution(100.0D); } catch (Exception e) {
/* 362 */         e.printStackTrace();
/*     */       }
/*     */ 
/*     */     }
/* 367 */     else if (type.equals(ScannerIOMetadata.STATECHANGE)) {
/* 368 */       System.out.println("Scanner State " + metadata.getStateStr());
/* 369 */       System.out.println("Scanner State " + metadata.getState());
/*     */ 
/* 371 */       if (metadata.getLastState() == 3) metadata.getState();
/*     */     }
/* 373 */     else if (type.equals(ScannerIOMetadata.EXCEPTION)) {
/* 374 */       IJ.error(metadata.getException().toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   class ClipMover extends MouseInputAdapter
/*     */   {
/*     */     scanning.ImagePanel cropping;
/*     */     Point offset;
/*     */     boolean dragging;
/*     */ 
/*     */     public ClipMover(scanning.ImagePanel c)
/*     */     {
/* 788 */       this.cropping = c;
/* 789 */       this.offset = new Point();
/* 790 */       this.dragging = false;
/*     */     }
/*     */ 
/*     */     public void mousePressed(MouseEvent e)
/*     */     {
/* 796 */       Point p = e.getPoint();
/*     */ 
/* 799 */       if (this.cropping.clip.contains(p))
/*     */       {
/* 801 */         this.offset.x = (p.x - this.cropping.clip.x);
/* 802 */         this.offset.y = (p.y - this.cropping.clip.y);
/* 803 */         this.dragging = true;
/*     */       }
/*     */     }
/*     */ 
/*     */     public void mouseReleased(MouseEvent e)
/*     */     {
/* 809 */       this.dragging = false;
/*     */     }
/*     */ 
/*     */     public void mouseDragged(MouseEvent e)
/*     */     {
/* 815 */       if (this.dragging)
/*     */       {
/* 817 */         ClipedPanel xcpanel = this.cropping.getClippedImg();
/*     */ 
/* 819 */         if (xcpanel != null)
/*     */         {
/* 821 */           scanning.this.jContentPane.remove(xcpanel);
/*     */         }
/*     */ 
/* 825 */         int x = e.getX() - this.offset.x;
/* 826 */         int y = e.getY() - this.offset.y;
/*     */ 
/* 830 */         if (this.cropping.isShowClip())
/* 831 */           this.cropping.setClip(x, y);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   class ImagePanel extends JPanel
/*     */   {
/*     */     private Image image;
/*     */     Image cimg;
/*     */     int imageWidth;
/*     */     int imageHeight;
/*     */     BufferedImage imageb;
/*     */     Dimension size;
/*     */     public Rectangle clip;
/*     */     boolean showClip;
/*     */     boolean clipedImg;
/* 399 */     JSlider slider1 = new JSlider(0, 80, 180, 80);
/*     */     ClipedPanel clipedPanel;
/*     */ 
/*     */     public boolean isShowClip()
/*     */     {
/* 395 */       return this.showClip;
/*     */     }
/*     */ 
/*     */     ImagePanel(Image image)
/*     */     {
/* 405 */       this.image = image;
/* 406 */       this.imageWidth = image.getWidth(null);
/* 407 */       this.imageHeight = image.getHeight(null);
/* 408 */       this.imageb = ((BufferedImage)image);
/* 409 */       this.size = new Dimension(this.imageb.getWidth(), this.imageb.getHeight());
/* 410 */       this.showClip = true;
/*     */     }
/*     */ 
/*     */     public Image getImage()
/*     */     {
/* 417 */       return this.image;
/*     */     }
/*     */ 
/*     */     public Image getCimg()
/*     */     {
/* 422 */       return this.cimg;
/*     */     }
/*     */ 
/*     */     protected void paintComponent(Graphics g)
/*     */     {
/* 429 */       super.paintComponent(g);
/* 430 */       Graphics2D g2 = (Graphics2D)g;
/* 431 */       g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
/* 432 */         RenderingHints.VALUE_ANTIALIAS_ON);
/* 433 */       int x = (getWidth() - this.size.width) / 2;
/* 434 */       int y = (getHeight() - this.size.height) / 2;
/*     */ 
/* 437 */       g2.drawImage(this.imageb, x, y, this);
/* 438 */       if (this.showClip)
/*     */       {
/* 440 */         if (this.clip == null)
/* 441 */           createClip(80, 80);
/* 442 */         g2.setPaint(Color.red);
/* 443 */         g2.draw(this.clip);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void setClip(int x, int y)
/*     */     {
/* 451 */       int x0 = (getWidth() - this.size.width) / 2;
/* 452 */       int y0 = (getHeight() - this.size.height) / 2;
/*     */ 
/* 454 */       if ((x < x0) || (x + this.clip.width > x0 + this.size.width) || 
/* 455 */         (y < y0) || (y + this.clip.height > y0 + this.size.height)) {
/* 456 */         return;
/*     */       }
/* 458 */       this.clip.setLocation(x, y);
/* 459 */       repaint();
/*     */ 
/* 461 */       clipImage();
/* 462 */       repaint();
/*     */     }
/*     */ 
/*     */     public Dimension getPreferredSize()
/*     */     {
/* 467 */       return this.size;
/*     */     }
/*     */ 
/*     */     private void createClip(int sx, int sy)
/*     */     {
/* 472 */       this.clip = new Rectangle(sx, sy);
/*     */ 
/* 474 */       this.clip.x = ((getWidth() - this.clip.width) / 2);
/* 475 */       this.clip.y = ((getHeight() - this.clip.height) / 2);
/*     */     }
/*     */ 
/*     */     public JPanel getCroppedPanel()
/*     */     {
/* 481 */       ClipedPanel cpanel = getClippedImg();
/*     */ 
/* 484 */       return cpanel;
/*     */     }
/*     */ 
/*     */     private void clipImage()
/*     */     {
/* 491 */       BufferedImage clipped = null;
/*     */       try
/*     */       {
/* 494 */         int w = this.clip.width;
/* 495 */         int h = this.clip.height;
/* 496 */         int x0 = (getWidth() - this.size.width) / 2;
/* 497 */         int y0 = (getHeight() - this.size.height) / 2;
/* 498 */         int x = this.clip.x - x0;
/* 499 */         int y = this.clip.y - y0;
/* 500 */         clipped = this.imageb.getSubimage(x, y, w, h);
/*     */ 
/* 503 */         this.cimg = clipped;
/*     */ 
/* 505 */         this.clipedPanel = new ClipedPanel(this.cimg);
/*     */ 
/* 507 */         scanning.this.crpdpanel = scanning.this.imagePanel.getCroppedPanel();
/*     */ 
/* 509 */         scanning.this.crpdpanel.setBounds(700, 200, 300, 300);
/* 510 */         scanning.this.crpdpanel.repaint();
/*     */ 
/* 512 */         scanning.this.crpdpanel.setBorder(new LineBorder(Color.red, 1));
/* 513 */         scanning.this.crpdpanel.setBorder(BorderFactory.createTitledBorder("Cropped Image"));
/* 514 */         scanning.this.crpdpanel.setBackground(Color.white);
/*     */ 
/* 516 */         scanning.this.jContentPane.add(scanning.this.crpdpanel);
/* 517 */         scanning.this.jContentPane.repaint();
/* 518 */         scanning.this.jContentPane.revalidate();
/*     */       }
/*     */       catch (RasterFormatException rfe)
/*     */       {
/* 523 */         System.out.println("raster format error: " + rfe.getMessage());
/*     */       }
/*     */     }
/*     */ 
/*     */     public ClipedPanel getClippedImg()
/*     */     {
/* 530 */       return this.clipedPanel;
/*     */     }
/*     */ 
/*     */     public JPanel getUIPanel()
/*     */     {
/* 538 */       JButton clip = new JButton("Crop image");
/* 539 */       clip.addActionListener(new ActionListener()
/*     */       {
/*     */         public void actionPerformed(ActionEvent e)
/*     */         {
/* 543 */           scanning.ImagePanel.this.repaint();
/* 544 */           if (scanning.ImagePanel.this.getClippedImg() != null)
/* 545 */             scanning.this.jContentPane.remove(scanning.ImagePanel.this.getClippedImg());
/* 546 */           if (scanning.ImagePanel.this.showClip)
/*     */           {
/* 548 */             scanning.ImagePanel.this.clipImage();
/* 549 */             scanning.ImagePanel.this.repaint();
/* 550 */             scanning.ImagePanel.this.clipedImg = true;
/*     */           }
/*     */           else
/*     */           {
/* 555 */             JOptionPane.showMessageDialog(null, "First Check Show clip Check Box!");
/*     */           }
/*     */         }
/*     */       });
/* 563 */       this.slider1.setPaintLabels(true);
/* 564 */       this.slider1.setPaintTicks(true);
/*     */ 
/* 566 */       this.slider1.addChangeListener(new ChangeListener()
/*     */       {
/*     */         public void stateChanged(ChangeEvent e)
/*     */         {
/* 570 */           scanning.ImagePanel.this.repaint();
/*     */ 
/* 572 */           int sx1 = scanning.ImagePanel.this.slider1.getValue();
/* 573 */           int sy1 = scanning.ImagePanel.this.slider1.getValue();
/* 574 */           scanning.ImagePanel.this.createClip(sx1, sy1);
/*     */ 
/* 576 */           scanning.ImagePanel.this.repaint();
/* 577 */           if (scanning.ImagePanel.this.getClippedImg() != null) {
/* 578 */             scanning.this.jContentPane.remove(scanning.ImagePanel.this.getClippedImg());
/*     */           }
/* 580 */           if (scanning.ImagePanel.this.isShowClip())
/*     */           {
/* 582 */             scanning.ImagePanel.this.clipImage();
/* 583 */             scanning.ImagePanel.this.repaint();
/*     */           }
/*     */           else
/*     */           {
/* 587 */             JOptionPane.showMessageDialog(null, "First Check Show clip Check Box!");
/*     */           }
/*     */         }
/*     */       });
/* 593 */       JButton save = new JButton("Save image");
/* 594 */       JButton upload = new JButton("Upload image");
/* 595 */       JButton saveDoc = new JButton("Save Document (Without Cropping)");
/*     */ 
/* 597 */       upload.addActionListener(new ActionListener()
/*     */       {
/*     */         public void actionPerformed(ActionEvent e)
/*     */         {
/* 603 */           if (scanning.ImagePanel.this.clipedImg)
/*     */           {
/* 606 */             scanning.ImagePanel.this.repaint();
/* 607 */             if (scanning.ImagePanel.this.getClippedImg() != null) {
/* 608 */               scanning.this.jContentPane.remove(scanning.ImagePanel.this.getClippedImg());
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/* 613 */           Image image = scanning.ImagePanel.this.getCimg();
/*     */ 
/* 615 */           BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), 1);
/* 616 */           bufferedImage.createGraphics().drawImage(image, 0, 0, null);
/* 617 */           ByteArrayOutputStream baos = new ByteArrayOutputStream();
/*     */           try {
/* 619 */             ImageIO.write(bufferedImage, "png", baos);
/*     */           } catch (IOException e1) {
/* 621 */             e1.printStackTrace();
/*     */           }
/* 623 */           InputStream is = new ByteArrayInputStream(baos.toByteArray());
/* 624 */           JOptionPane.showMessageDialog(null, "Server Settings need to be Configured.", "Upload Image", 1);
/*     */         }
/*     */       });
/* 631 */       save.addActionListener(new ActionListener()
/*     */       {
/*     */         public void actionPerformed(ActionEvent e)
/*     */         {
/* 637 */           if (scanning.ImagePanel.this.clipedImg)
/*     */           {
/* 640 */             scanning.ImagePanel.this.repaint();
/* 641 */             if (scanning.ImagePanel.this.getClippedImg() != null) {
/* 642 */               scanning.this.jContentPane.remove(scanning.ImagePanel.this.getClippedImg());
/*     */             }
/*     */           }
/*     */ 
/* 646 */           scanning.ImagePanel.this.saveImg();
/*     */         }
/*     */       });
/* 654 */       saveDoc.addActionListener(new ActionListener()
/*     */       {
/*     */         public void actionPerformed(ActionEvent e)
/*     */         {
/* 658 */           scanning.ImagePanel.this.repaint();
/*     */ 
/* 661 */           scanning.ImagePanel.this.saveDoc();
/* 662 */           scanning.ImagePanel.this.repaint();
/*     */         }
/*     */       });
/* 672 */       JPanel panel = new JPanel();
/*     */ 
/* 674 */       panel.add(clip);
/* 675 */       panel.add(this.slider1);
/* 676 */       this.slider1.setBackground(Color.white);
/* 677 */       panel.add(save);
/* 678 */       panel.add(upload);
/* 679 */       panel.add(saveDoc);
/* 680 */       panel.revalidate();
/* 681 */       return panel;
/*     */     }
/*     */ 
/*     */     public void saveImg()
/*     */     {
/* 691 */       Image image = getCimg();
/*     */ 
/* 693 */       BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), 1);
/* 694 */       bufferedImage.createGraphics().drawImage(image, 0, 0, null);
/* 695 */       JFileChooser chooser = new JFileChooser();
/*     */ 
/* 698 */       String[] e = ImageIO.getWriterFormatNames();
/* 699 */       for (int i = 0; i < e.length; i++)
/* 700 */         chooser.addChoosableFileFilter(null);
/* 701 */       int result = chooser.showSaveDialog(this);
/* 702 */       if (result == 0)
/*     */       {
/* 705 */         String ext = "JPG";
/* 706 */         File file = chooser.getSelectedFile();
/* 707 */         String name = file.getName();
/* 708 */         if (!name.endsWith(ext))
/* 709 */           file = new File(file.getParentFile(), name + "." + ext);
/*     */         try
/*     */         {
/* 712 */           ImageIO.write(bufferedImage, ext, file);
/*     */         }
/*     */         catch (IOException e1)
/*     */         {
/* 724 */           e1.printStackTrace();
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     public void saveDoc()
/*     */     {
/* 739 */       Image image = getImage();
/*     */ 
/* 741 */       BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), 10);
/* 742 */       bufferedImage.createGraphics().drawImage(image, 0, 0, null);
/* 743 */       JFileChooser chooser = new JFileChooser();
/*     */ 
/* 745 */       String[] e = ImageIO.getWriterFormatNames();
/* 746 */       for (int i = 0; i < e.length; i++)
/* 747 */         chooser.addChoosableFileFilter(null);
/* 748 */       int result = chooser.showSaveDialog(this);
/* 749 */       if (result == 0)
/*     */       {
/* 752 */         String ext = "JPG";
/*     */ 
/* 754 */         File file = chooser.getSelectedFile();
/* 755 */         String name = file.getName();
/* 756 */         if (!name.endsWith(ext))
/* 757 */           file = new File(file.getParentFile(), name + "." + ext);
/*     */         try
/*     */         {
/* 760 */           ImageIO.write(bufferedImage, ext, file);
/*     */         }
/*     */         catch (IOException e1)
/*     */         {
/* 768 */           e1.printStackTrace();
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     public ImagePanel getPanel()
/*     */     {
/* 775 */       return this;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/scanning.jar
 * Qualified Name:     scanning
 * JD-Core Version:    0.6.2
 */