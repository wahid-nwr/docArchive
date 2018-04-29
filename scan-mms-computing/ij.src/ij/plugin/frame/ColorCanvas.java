/*     */ package ij.plugin.frame;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.gui.ColorChooser;
/*     */ import ij.gui.Toolbar;
/*     */ import java.awt.Canvas;
/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Frame;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.event.MouseListener;
/*     */ import java.awt.event.MouseMotionListener;
/*     */ import java.util.Vector;
/*     */ 
/*     */ class ColorCanvas extends Canvas
/*     */   implements MouseListener, MouseMotionListener
/*     */ {
/*     */   int width;
/*     */   int height;
/*     */   Vector colors;
/*     */   boolean background;
/*     */   long mouseDownTime;
/*     */   ColorGenerator ip;
/*     */   Frame frame;
/*     */ 
/*     */   public ColorCanvas(int width, int height, Frame frame, ColorGenerator ip)
/*     */   {
/* 207 */     this.width = width; this.height = height;
/* 208 */     this.frame = frame;
/* 209 */     this.ip = ip;
/* 210 */     addMouseListener(this);
/* 211 */     addMouseMotionListener(this);
/* 212 */     addKeyListener(IJ.getInstance());
/* 213 */     setSize(width, height);
/*     */   }
/*     */ 
/*     */   public Dimension getPreferredSize() {
/* 217 */     return new Dimension(this.width, this.height);
/*     */   }
/*     */ 
/*     */   public void update(Graphics g) {
/* 221 */     paint(g);
/*     */   }
/*     */ 
/*     */   public void paint(Graphics g) {
/* 225 */     g.drawImage(this.ip.createImage(), 0, 0, null);
/*     */   }
/*     */ 
/*     */   public void mousePressed(MouseEvent e)
/*     */   {
/* 230 */     this.ip.setLineWidth(1);
/* 231 */     if (Toolbar.getToolId() == 13)
/* 232 */       IJ.setTool(0);
/* 233 */     Rectangle flipperRect = new Rectangle(86, 268, 18, 18);
/* 234 */     Rectangle resetRect = new Rectangle(86, 294, 18, 18);
/* 235 */     Rectangle foreground1Rect = new Rectangle(9, 266, 45, 10);
/* 236 */     Rectangle foreground2Rect = new Rectangle(9, 276, 23, 25);
/* 237 */     Rectangle background1Rect = new Rectangle(33, 302, 45, 10);
/* 238 */     Rectangle background2Rect = new Rectangle(56, 277, 23, 25);
/* 239 */     int x = e.getX();
/* 240 */     int y = e.getY();
/* 241 */     long difference = System.currentTimeMillis() - this.mouseDownTime;
/* 242 */     boolean doubleClick = difference <= 250L;
/* 243 */     this.mouseDownTime = System.currentTimeMillis();
/* 244 */     if (flipperRect.contains(x, y)) {
/* 245 */       Color c = Toolbar.getBackgroundColor();
/* 246 */       Toolbar.setBackgroundColor(Toolbar.getForegroundColor());
/* 247 */       Toolbar.setForegroundColor(c);
/* 248 */     } else if (resetRect.contains(x, y)) {
/* 249 */       Toolbar.setForegroundColor(new Color(0));
/* 250 */       Toolbar.setBackgroundColor(new Color(16777215));
/* 251 */     } else if ((background1Rect.contains(x, y)) || (background2Rect.contains(x, y))) {
/* 252 */       this.background = true;
/* 253 */       if (doubleClick) editColor();
/* 254 */       this.ip.refreshForeground();
/* 255 */       this.ip.refreshBackground();
/* 256 */     } else if ((foreground1Rect.contains(x, y)) || (foreground2Rect.contains(x, y))) {
/* 257 */       this.background = false;
/* 258 */       if (doubleClick) editColor();
/* 259 */       this.ip.refreshBackground();
/* 260 */       this.ip.refreshForeground();
/*     */     }
/* 263 */     else if (doubleClick) {
/* 264 */       editColor();
/*     */     } else {
/* 266 */       setDrawingColor(x, y, this.background);
/*     */     }
/* 268 */     if (this.background) {
/* 269 */       this.ip.refreshForeground();
/* 270 */       this.ip.refreshBackground();
/*     */     } else {
/* 272 */       this.ip.refreshBackground();
/* 273 */       this.ip.refreshForeground();
/*     */     }
/* 275 */     repaint();
/*     */   }
/*     */ 
/*     */   public void mouseMoved(MouseEvent e) {
/* 279 */     int x = e.getX();
/* 280 */     int y = e.getY();
/* 281 */     int p = this.ip.getPixel(x, y);
/* 282 */     int r = (p & 0xFF0000) >> 16;
/* 283 */     int g = (p & 0xFF00) >> 8;
/* 284 */     int b = p & 0xFF;
/* 285 */     IJ.showStatus("red=" + pad(r) + ", green=" + pad(g) + ", blue=" + pad(b));
/*     */   }
/*     */ 
/*     */   String pad(int n)
/*     */   {
/* 290 */     String str = "" + n;
/* 291 */     while (str.length() < 3)
/* 292 */       str = "0" + str;
/* 293 */     return str;
/*     */   }
/*     */ 
/*     */   void setDrawingColor(int x, int y, boolean setBackground) {
/* 297 */     int p = this.ip.getPixel(x, y);
/* 298 */     int r = (p & 0xFF0000) >> 16;
/* 299 */     int g = (p & 0xFF00) >> 8;
/* 300 */     int b = p & 0xFF;
/* 301 */     Color c = new Color(r, g, b);
/* 302 */     if (setBackground) {
/* 303 */       Toolbar.setBackgroundColor(c);
/* 304 */       if (Recorder.record)
/* 305 */         Recorder.record("setBackgroundColor", c.getRed(), c.getGreen(), c.getBlue());
/*     */     } else {
/* 307 */       Toolbar.setForegroundColor(c);
/* 308 */       if (Recorder.record)
/* 309 */         Recorder.record("setForegroundColor", c.getRed(), c.getGreen(), c.getBlue());
/*     */     }
/*     */   }
/*     */ 
/*     */   void editColor() {
/* 314 */     Color c = this.background ? Toolbar.getBackgroundColor() : Toolbar.getForegroundColor();
/* 315 */     ColorChooser cc = new ColorChooser((this.background ? "Background" : "Foreground") + " Color", c, false, this.frame);
/* 316 */     c = cc.getColor();
/* 317 */     if (this.background)
/* 318 */       Toolbar.setBackgroundColor(c);
/*     */     else
/* 320 */       Toolbar.setForegroundColor(c);
/*     */   }
/*     */ 
/*     */   public void refreshColors() {
/* 324 */     this.ip.refreshBackground();
/* 325 */     this.ip.refreshForeground();
/* 326 */     repaint();
/*     */   }
/*     */ 
/*     */   public void mouseReleased(MouseEvent e)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void mouseExited(MouseEvent e)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void mouseClicked(MouseEvent e)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void mouseEntered(MouseEvent e)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void mouseDragged(MouseEvent e)
/*     */   {
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.frame.ColorCanvas
 * JD-Core Version:    0.6.2
 */