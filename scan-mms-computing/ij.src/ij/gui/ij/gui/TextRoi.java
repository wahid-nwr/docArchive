/*     */ package ij.gui;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.WindowManager;
/*     */ import ij.macro.Interpreter;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.util.Java2;
/*     */ import ij.util.Tools;
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.geom.Rectangle2D;
/*     */ 
/*     */ public class TextRoi extends Roi
/*     */ {
/*     */   public static final int LEFT = 0;
/*     */   public static final int CENTER = 1;
/*     */   public static final int RIGHT = 2;
/*     */   static final int MAX_LINES = 50;
/*     */   private static final String line1 = "Enter text, then press";
/*     */   private static final String line2 = "ctrl+b to add to overlay";
/*     */   private static final String line3 = "or ctrl+d to draw.";
/*  18 */   private String[] theText = new String[50];
/*  19 */   private static String name = "SansSerif";
/*  20 */   private static int style = 0;
/*  21 */   private static int size = 18;
/*     */   private Font instanceFont;
/*  23 */   private static boolean newFont = true;
/*  24 */   private static boolean antialiasedText = true;
/*     */   private static int globalJustification;
/*     */   private int justification;
/*  27 */   private boolean antialiased = antialiasedText;
/*  28 */   private static boolean recordSetFont = true;
/*     */   private double previousMag;
/*  30 */   private boolean firstChar = true;
/*  31 */   private boolean firstMouseUp = true;
/*  32 */   private int cline = 0;
/*     */ 
/*     */   public TextRoi(int x, int y, String text)
/*     */   {
/*  36 */     this(x, y, text, null);
/*     */   }
/*     */ 
/*     */   public TextRoi(int x, int y, String text, Font font)
/*     */   {
/*  45 */     super(x, y, 1, 1);
/*  46 */     String[] lines = Tools.split(text, "\n");
/*  47 */     int count = Math.min(lines.length, 50);
/*  48 */     for (int i = 0; i < count; i++)
/*  49 */       this.theText[i] = lines[i];
/*  50 */     if (font == null) font = new Font(name, style, size);
/*  51 */     this.instanceFont = font;
/*  52 */     this.firstChar = false;
/*  53 */     if (IJ.debugMode) IJ.log("TextRoi: " + this.theText[0] + "  " + this.width + "," + this.height); 
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public TextRoi(int x, int y, String text, Font font, Color color)
/*     */   {
/*  58 */     super(x, y, 1, 1);
/*  59 */     if (font == null) font = new Font(name, style, size);
/*  60 */     this.instanceFont = font;
/*  61 */     IJ.error("TextRoi", "API has changed. See updated example at\nhttp://imagej.nih.gov/ij/macros/js/TextOverlay.js");
/*     */   }
/*     */ 
/*     */   public TextRoi(int x, int y, ImagePlus imp) {
/*  65 */     super(x, y, imp);
/*  66 */     ImageCanvas ic = imp.getCanvas();
/*  67 */     double mag = getMagnification();
/*  68 */     if (mag > 1.0D)
/*  69 */       mag = 1.0D;
/*  70 */     if (size < 12.0D / mag)
/*  71 */       size = (int)(12.0D / mag);
/*  72 */     this.theText[0] = "Enter text, then press";
/*  73 */     this.theText[1] = "ctrl+b to add to overlay";
/*  74 */     this.theText[2] = "or ctrl+d to draw.";
/*  75 */     if ((previousRoi != null) && ((previousRoi instanceof TextRoi))) {
/*  76 */       this.firstMouseUp = false;
/*     */ 
/*  78 */       previousRoi = null;
/*     */     }
/*  80 */     this.instanceFont = new Font(name, style, size);
/*  81 */     this.justification = globalJustification;
/*     */   }
/*     */ 
/*     */   public void addChar(char c)
/*     */   {
/*  87 */     if (this.imp == null) return;
/*  88 */     if ((c < ' ') && (c != '\b') && (c != '\n')) return;
/*  89 */     if (this.firstChar) {
/*  90 */       this.cline = 0;
/*  91 */       this.theText[this.cline] = new String("");
/*  92 */       for (int i = 1; i < 50; i++)
/*  93 */         this.theText[i] = null;
/*     */     }
/*  95 */     if (c == '\b')
/*     */     {
/*  97 */       if (this.theText[this.cline].length() > 0) {
/*  98 */         this.theText[this.cline] = this.theText[this.cline].substring(0, this.theText[this.cline].length() - 1);
/*  99 */       } else if (this.cline > 0) {
/* 100 */         this.theText[this.cline] = null;
/* 101 */         this.cline -= 1;
/*     */       }
/* 103 */       this.imp.draw(this.clipX, this.clipY, this.clipWidth, this.clipHeight);
/* 104 */       this.firstChar = false;
/* 105 */       return;
/* 106 */     }if (c == '\n')
/*     */     {
/* 108 */       if (this.cline < 49) this.cline += 1;
/* 109 */       this.theText[this.cline] = "";
/* 110 */       updateBounds(null);
/* 111 */       updateText();
/*     */     } else {
/* 113 */       char[] chr = { c };
/*     */       int tmp265_262 = this.cline;
/*     */       String[] tmp265_258 = this.theText; tmp265_258[tmp265_262] = (tmp265_258[tmp265_262] + new String(chr));
/* 115 */       updateBounds(null);
/* 116 */       updateText();
/* 117 */       this.firstChar = false;
/* 118 */       return;
/*     */     }
/*     */   }
/*     */ 
/*     */   Font getScaledFont() {
/* 123 */     if (this.nonScalable) {
/* 124 */       return this.instanceFont;
/*     */     }
/* 126 */     if (this.instanceFont == null)
/* 127 */       this.instanceFont = new Font(name, style, size);
/* 128 */     double mag = getMagnification();
/* 129 */     return this.instanceFont.deriveFont((float)(this.instanceFont.getSize() * mag));
/*     */   }
/*     */ 
/*     */   public void drawPixels(ImageProcessor ip)
/*     */   {
/* 135 */     ip.setFont(this.instanceFont);
/* 136 */     ip.setAntialiasedText(this.antialiased);
/* 137 */     FontMetrics metrics = ip.getFontMetrics();
/* 138 */     int fontHeight = metrics.getHeight();
/* 139 */     int descent = metrics.getDescent();
/* 140 */     int i = 0;
/* 141 */     int yy = 0;
/* 142 */     while ((i < 50) && (this.theText[i] != null))
/*     */     {
/*     */       int tw;
/* 143 */       switch (this.justification) {
/*     */       case 0:
/* 145 */         ip.drawString(this.theText[i], this.x, this.y + yy + fontHeight);
/* 146 */         break;
/*     */       case 1:
/* 148 */         tw = metrics.stringWidth(this.theText[i]);
/* 149 */         ip.drawString(this.theText[i], this.x + (this.width - tw) / 2, this.y + yy + fontHeight);
/* 150 */         break;
/*     */       case 2:
/* 152 */         tw = metrics.stringWidth(this.theText[i]);
/* 153 */         ip.drawString(this.theText[i], this.x + this.width - tw, this.y + yy + fontHeight);
/*     */       }
/*     */ 
/* 156 */       i++;
/* 157 */       yy += fontHeight;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void draw(Graphics g)
/*     */   {
/* 163 */     if (IJ.debugMode) IJ.log("draw: " + this.theText[0] + "  " + this.width + "," + this.height);
/* 164 */     if ((Interpreter.isBatchMode()) && (this.ic != null) && (this.ic.getDisplayList() != null)) return;
/* 165 */     if ((newFont) || (this.width == 1))
/* 166 */       updateBounds(g);
/* 167 */     super.draw(g);
/* 168 */     double mag = getMagnification();
/* 169 */     int sx = screenX(this.x);
/* 170 */     int sy = screenY(this.y);
/* 171 */     int swidth = (int)(this.width * mag);
/* 172 */     int sheight = (int)(this.height * mag);
/* 173 */     Rectangle r = null;
/* 174 */     r = g.getClipBounds();
/* 175 */     g.setClip(sx, sy, swidth, sheight);
/* 176 */     drawText(g);
/* 177 */     if (r != null) g.setClip(r.x, r.y, r.width, r.height); 
/*     */   }
/*     */ 
/*     */   public void drawOverlay(Graphics g)
/*     */   {
/* 181 */     drawText(g);
/*     */   }
/*     */ 
/*     */   void drawText(Graphics g) {
/* 185 */     g.setColor(this.strokeColor != null ? this.strokeColor : ROIColor);
/* 186 */     Java2.setAntialiasedText(g, this.antialiased);
/* 187 */     if ((newFont) || (this.width == 1))
/* 188 */       updateBounds(g);
/* 189 */     double mag = getMagnification();
/* 190 */     int sx = this.nonScalable ? this.x : screenX(this.x);
/* 191 */     int sy = this.nonScalable ? this.y : screenY(this.y);
/* 192 */     int sw = this.nonScalable ? this.width : (int)(getMagnification() * this.width);
/* 193 */     int sh = this.nonScalable ? this.height : (int)(getMagnification() * this.height);
/* 194 */     Font font = getScaledFont();
/* 195 */     FontMetrics metrics = g.getFontMetrics(font);
/* 196 */     int fontHeight = metrics.getHeight();
/* 197 */     int descent = metrics.getDescent();
/* 198 */     g.setFont(font);
/* 199 */     int i = 0;
/* 200 */     if (this.fillColor != null) {
/* 201 */       if (getStrokeWidth() < 10.0F) {
/* 202 */         Color saveFillColor = this.fillColor;
/* 203 */         setStrokeWidth(10.0F);
/* 204 */         this.fillColor = saveFillColor;
/*     */       }
/* 206 */       updateBounds(g);
/* 207 */       Color c = g.getColor();
/* 208 */       int alpha = this.fillColor.getAlpha();
/* 209 */       g.setColor(this.fillColor);
/* 210 */       Graphics2D g2d = (Graphics2D)g;
/* 211 */       g.fillRect(sx - 5, sy - 5, sw + 10, sh + 10);
/* 212 */       g.setColor(c);
/*     */     }
/* 214 */     while ((i < 50) && (this.theText[i] != null))
/*     */     {
/*     */       int tw;
/* 215 */       switch (this.justification) {
/*     */       case 0:
/* 217 */         g.drawString(this.theText[i], sx, sy + fontHeight - descent);
/* 218 */         break;
/*     */       case 1:
/* 220 */         tw = metrics.stringWidth(this.theText[i]);
/* 221 */         g.drawString(this.theText[i], sx + (sw - tw) / 2, sy + fontHeight - descent);
/* 222 */         break;
/*     */       case 2:
/* 224 */         tw = metrics.stringWidth(this.theText[i]);
/* 225 */         g.drawString(this.theText[i], sx + sw - tw, sy + fontHeight - descent);
/*     */       }
/*     */ 
/* 228 */       i++;
/* 229 */       sy += fontHeight;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String getFont()
/*     */   {
/* 243 */     return name;
/*     */   }
/*     */ 
/*     */   public static int getSize()
/*     */   {
/* 248 */     return size;
/*     */   }
/*     */ 
/*     */   public static int getStyle()
/*     */   {
/* 253 */     return style;
/*     */   }
/*     */ 
/*     */   public void setCurrentFont(Font font)
/*     */   {
/* 258 */     this.instanceFont = font;
/* 259 */     updateBounds(null);
/*     */   }
/*     */ 
/*     */   public Font getCurrentFont()
/*     */   {
/* 264 */     return this.instanceFont;
/*     */   }
/*     */ 
/*     */   public static boolean isAntialiased()
/*     */   {
/* 269 */     return antialiasedText;
/*     */   }
/*     */ 
/*     */   public void setAntialiased(boolean antialiased)
/*     */   {
/* 274 */     this.antialiased = antialiased;
/*     */   }
/*     */ 
/*     */   public boolean getAntialiased()
/*     */   {
/* 279 */     return this.antialiased;
/*     */   }
/*     */ 
/*     */   public static void setGlobalJustification(int justification)
/*     */   {
/* 284 */     if ((justification < 0) || (justification > 2))
/* 285 */       justification = 0;
/* 286 */     globalJustification = justification;
/* 287 */     ImagePlus imp = WindowManager.getCurrentImage();
/* 288 */     if (imp != null) {
/* 289 */       Roi roi = imp.getRoi();
/* 290 */       if ((roi instanceof TextRoi)) {
/* 291 */         ((TextRoi)roi).setJustification(justification);
/* 292 */         imp.draw();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static int getGlobalJustification()
/*     */   {
/* 299 */     return globalJustification;
/*     */   }
/*     */ 
/*     */   public void setJustification(int justification)
/*     */   {
/* 304 */     if ((justification < 0) || (justification > 2))
/* 305 */       justification = 0;
/* 306 */     this.justification = justification;
/*     */   }
/*     */ 
/*     */   public int getJustification()
/*     */   {
/* 311 */     return this.justification;
/*     */   }
/*     */ 
/*     */   public static void setFont(String fontName, int fontSize, int fontStyle)
/*     */   {
/* 317 */     setFont(fontName, fontSize, fontStyle, true);
/*     */   }
/*     */ 
/*     */   public static void setFont(String fontName, int fontSize, int fontStyle, boolean antialiased)
/*     */   {
/* 323 */     recordSetFont = true;
/* 324 */     name = fontName;
/* 325 */     size = fontSize;
/* 326 */     style = fontStyle;
/* 327 */     globalJustification = 0;
/* 328 */     antialiasedText = antialiased;
/* 329 */     newFont = true;
/* 330 */     ImagePlus imp = WindowManager.getCurrentImage();
/* 331 */     if (imp != null) {
/* 332 */       Roi roi = imp.getRoi();
/* 333 */       if ((roi instanceof TextRoi)) {
/* 334 */         ((TextRoi)roi).setAntialiased(antialiased);
/* 335 */         ((TextRoi)roi).setCurrentFont(new Font(name, style, size));
/* 336 */         imp.draw();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void handleMouseUp(int screenX, int screenY) {
/* 342 */     super.handleMouseUp(screenX, screenY);
/* 343 */     if (this.firstMouseUp) {
/* 344 */       updateBounds(null);
/* 345 */       updateText();
/* 346 */       this.firstMouseUp = false;
/*     */     }
/* 348 */     else if ((this.width < 5) || (this.height < 5)) {
/* 349 */       this.imp.killRoi();
/*     */     }
/*     */   }
/*     */ 
/*     */   void updateBounds(Graphics g)
/*     */   {
/* 356 */     if ((this.ic == null) || ((this.theText[0] != null) && (this.theText[0].equals("Enter text, then press"))))
/* 357 */       return;
/* 358 */     double mag = this.ic.getMagnification();
/* 359 */     if (this.nonScalable) mag = 1.0D;
/* 360 */     Font font = getScaledFont();
/* 361 */     newFont = false;
/* 362 */     boolean nullg = g == null;
/* 363 */     if (nullg) g = this.ic.getGraphics();
/* 364 */     Java2.setAntialiasedText(g, this.antialiased);
/* 365 */     FontMetrics metrics = g.getFontMetrics(font);
/* 366 */     int fontHeight = (int)(metrics.getHeight() / mag);
/* 367 */     int descent = metrics.getDescent();
/* 368 */     int i = 0; int nLines = 0;
/* 369 */     this.oldX = this.x;
/* 370 */     this.oldY = this.y;
/* 371 */     this.oldWidth = this.width;
/* 372 */     this.oldHeight = this.height;
/* 373 */     int newWidth = 10;
/* 374 */     while ((i < 50) && (this.theText[i] != null)) {
/* 375 */       nLines++;
/* 376 */       int w = (int)Math.round(stringWidth(this.theText[i], metrics, g) / mag);
/* 377 */       if (w > newWidth)
/* 378 */         newWidth = w;
/* 379 */       i++;
/*     */     }
/* 381 */     if (nullg) g.dispose();
/* 382 */     newWidth += 2;
/* 383 */     this.width = newWidth;
/* 384 */     switch (this.justification) {
/*     */     case 0:
/* 386 */       if ((this.xMax != 0) && (this.x + newWidth > this.xMax))
/* 387 */         this.x = (this.xMax - this.width); break;
/*     */     case 1:
/* 390 */       this.x = (this.oldX + this.oldWidth / 2 - newWidth / 2);
/* 391 */       break;
/*     */     case 2:
/* 393 */       this.x = (this.oldX + this.oldWidth - newWidth);
/*     */     }
/*     */ 
/* 396 */     this.height = (nLines * fontHeight + 2);
/* 397 */     if (this.yMax != 0) {
/* 398 */       if (this.height > this.yMax)
/* 399 */         this.height = this.yMax;
/* 400 */       if (this.y + this.height > this.yMax)
/* 401 */         this.y = (this.yMax - this.height);
/*     */     }
/*     */   }
/*     */ 
/*     */   void updateText()
/*     */   {
/* 407 */     if (this.imp != null) {
/* 408 */       updateClipRect();
/* 409 */       this.imp.draw(this.clipX, this.clipY, this.clipWidth, this.clipHeight);
/*     */     }
/*     */   }
/*     */ 
/*     */   double stringWidth(String s, FontMetrics metrics, Graphics g) {
/* 414 */     Rectangle2D r = metrics.getStringBounds(s, g);
/* 415 */     return r.getWidth();
/*     */   }
/*     */ 
/*     */   public String getMacroCode(ImageProcessor ip) {
/* 419 */     String code = "";
/* 420 */     if (recordSetFont) {
/* 421 */       String options = "";
/* 422 */       if (style == 1)
/* 423 */         options = options + "bold";
/* 424 */       if (style == 2)
/* 425 */         options = options + " italic";
/* 426 */       if (antialiasedText)
/* 427 */         options = options + " antialiased";
/* 428 */       if (options.equals(""))
/* 429 */         options = "plain";
/* 430 */       code = code + "setFont(\"" + name + "\", " + size + ", \"" + options + "\");\n";
/* 431 */       recordSetFont = false;
/*     */     }
/* 433 */     FontMetrics metrics = ip.getFontMetrics();
/* 434 */     int fontHeight = metrics.getHeight();
/* 435 */     String text = "";
/* 436 */     for (int i = 0; (i < 50) && 
/* 437 */       (this.theText[i] != null); i++)
/*     */     {
/* 438 */       text = text + this.theText[i];
/* 439 */       if (this.theText[(i + 1)] != null) text = text + "\\n";
/*     */     }
/* 441 */     code = code + "makeText(\"" + text + "\", " + this.x + ", " + (this.y + fontHeight) + ");\n";
/* 442 */     code = code + "//drawString(\"" + text + "\", " + this.x + ", " + (this.y + fontHeight) + ");\n";
/* 443 */     return code;
/*     */   }
/*     */ 
/*     */   public String getText() {
/* 447 */     String text = "";
/* 448 */     for (int i = 0; (i < 50) && 
/* 449 */       (this.theText[i] != null); i++)
/*     */     {
/* 450 */       text = text + this.theText[i] + "\n";
/*     */     }
/* 452 */     return text;
/*     */   }
/*     */ 
/*     */   public static void recordSetFont() {
/* 456 */     recordSetFont = true;
/*     */   }
/*     */ 
/*     */   public boolean isDrawingTool() {
/* 460 */     return true;
/*     */   }
/*     */ 
/*     */   public void clear(ImageProcessor ip) {
/* 464 */     if (this.instanceFont == null) {
/* 465 */       ip.fill();
/*     */     } else {
/* 467 */       ip.setFont(this.instanceFont);
/* 468 */       ip.setAntialiasedText(antialiasedText);
/* 469 */       int i = 0; int width = 0;
/* 470 */       while ((i < 50) && (this.theText[i] != null)) {
/* 471 */         int w = ip.getStringWidth(this.theText[i]);
/* 472 */         if (w > width)
/* 473 */           width = w;
/* 474 */         i++;
/*     */       }
/* 476 */       Rectangle r = ip.getRoi();
/* 477 */       if (width > r.width) {
/* 478 */         r.width = width;
/* 479 */         ip.setRoi(r);
/*     */       }
/* 481 */       ip.fill();
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized Object clone()
/*     */   {
/* 487 */     TextRoi tr = (TextRoi)super.clone();
/* 488 */     tr.theText = new String[50];
/* 489 */     for (int i = 0; i < 50; i++)
/* 490 */       tr.theText[i] = this.theText[i];
/* 491 */     return tr;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.gui.TextRoi
 * JD-Core Version:    0.6.2
 */