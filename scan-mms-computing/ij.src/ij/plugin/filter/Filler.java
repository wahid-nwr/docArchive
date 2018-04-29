/*     */ package ij.plugin.filter;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.gui.Arrow;
/*     */ import ij.gui.ImageCanvas;
/*     */ import ij.gui.Roi;
/*     */ import ij.gui.TextRoi;
/*     */ import ij.gui.Toolbar;
/*     */ import ij.measure.Measurements;
/*     */ import ij.measure.ResultsTable;
/*     */ import ij.process.ByteProcessor;
/*     */ import ij.process.ImageProcessor;
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Rectangle;
/*     */ 
/*     */ public class Filler
/*     */   implements PlugInFilter, Measurements
/*     */ {
/*     */   String arg;
/*     */   Roi roi;
/*     */   ImagePlus imp;
/*     */   int sliceCount;
/*     */   ImageProcessor mask;
/*     */   boolean isTextRoi;
/*     */ 
/*     */   public int setup(String arg, ImagePlus imp)
/*     */   {
/*  19 */     this.arg = arg;
/*  20 */     this.imp = imp;
/*  21 */     if (imp != null)
/*  22 */       this.roi = imp.getRoi();
/*  23 */     this.isTextRoi = ((this.roi != null) && ((this.roi instanceof TextRoi)));
/*  24 */     IJ.register(Filler.class);
/*  25 */     int baseCapabilities = 1055;
/*  26 */     if (arg.equals("clear")) {
/*  27 */       if ((this.roi != null) && (this.roi.getType() == 10)) {
/*  28 */         IJ.error("Clear", "Area selection required");
/*  29 */         return 4096;
/*     */       }
/*  31 */       if ((this.isTextRoi) || (isLineSelection())) {
/*  32 */         return baseCapabilities;
/*     */       }
/*  34 */       return IJ.setupDialog(imp, baseCapabilities + 64);
/*  35 */     }if (arg.equals("draw"))
/*  36 */       return IJ.setupDialog(imp, baseCapabilities);
/*  37 */     if (arg.equals("label")) {
/*  38 */       if (Analyzer.firstParticle < Analyzer.lastParticle) {
/*  39 */         return baseCapabilities - 1024;
/*     */       }
/*  41 */       return baseCapabilities;
/*  42 */     }if (arg.equals("outside"))
/*  43 */       return IJ.setupDialog(imp, baseCapabilities);
/*  44 */     if ((this.roi != null) && (this.roi.getType() == 10) && (arg.equals("fill"))) {
/*  45 */       IJ.error("Fill", "Area selection required");
/*  46 */       return 4096;
/*     */     }
/*  48 */     return IJ.setupDialog(imp, baseCapabilities + 64);
/*     */   }
/*     */ 
/*     */   public void run(ImageProcessor ip) {
/*  52 */     if (this.arg.equals("clear"))
/*  53 */       clear(ip);
/*  54 */     else if ((this.isTextRoi) && ((this.arg.equals("draw")) || (this.arg.equals("fill"))))
/*  55 */       draw(ip);
/*  56 */     else if (this.arg.equals("fill"))
/*  57 */       fill(ip);
/*  58 */     else if (this.arg.equals("draw"))
/*  59 */       draw(ip);
/*  60 */     else if (this.arg.equals("label"))
/*  61 */       label(ip);
/*  62 */     else if (this.arg.equals("outside"))
/*  63 */       clearOutside(ip);
/*     */   }
/*     */ 
/*     */   boolean isLineSelection() {
/*  67 */     return (this.roi != null) && (this.roi.isLine());
/*     */   }
/*     */ 
/*     */   boolean isStraightLine() {
/*  71 */     return (this.roi != null) && (this.roi.getType() == 5);
/*     */   }
/*     */ 
/*     */   public void clear(ImageProcessor ip) {
/*  75 */     ip.setColor(Toolbar.getBackgroundColor());
/*  76 */     if (isLineSelection()) {
/*  77 */       if ((isStraightLine()) && (this.roi.getStrokeWidth() > 1.0F))
/*  78 */         ip.fillPolygon(this.roi.getPolygon());
/*     */       else
/*  80 */         this.roi.drawPixels();
/*  81 */     } else if ((this.roi != null) && ((this.roi instanceof TextRoi)))
/*  82 */       ((TextRoi)this.roi).clear(ip);
/*     */     else
/*  84 */       ip.fill();
/*  85 */     ip.setColor(Toolbar.getForegroundColor());
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public void fill(ImageProcessor ip)
/*     */   {
/*  93 */     ip.setColor(Toolbar.getForegroundColor());
/*  94 */     if (isLineSelection()) {
/*  95 */       if ((isStraightLine()) && (this.roi.getStrokeWidth() > 1.0F) && (!(this.roi instanceof Arrow)))
/*  96 */         ip.fillPolygon(this.roi.getPolygon());
/*     */       else
/*  98 */         this.roi.drawPixels(ip);
/*     */     }
/* 100 */     else ip.fill();
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public void draw(ImageProcessor ip)
/*     */   {
/* 108 */     ip.setColor(Toolbar.getForegroundColor());
/* 109 */     this.roi.drawPixels(ip);
/* 110 */     if (IJ.altKeyDown())
/* 111 */       drawLabel(ip);
/*     */   }
/*     */ 
/*     */   public void label(ImageProcessor ip) {
/* 115 */     if (Analyzer.getCounter() == 0) {
/* 116 */       IJ.error("Label", "Measurement counter is zero");
/* 117 */       return;
/*     */     }
/* 119 */     if (Analyzer.firstParticle < Analyzer.lastParticle) {
/* 120 */       drawParticleLabels(ip);
/*     */     } else {
/* 122 */       ip.setColor(Toolbar.getForegroundColor());
/* 123 */       ImageCanvas ic = this.imp.getCanvas();
/* 124 */       if (ic != null) {
/* 125 */         double mag = ic.getMagnification();
/* 126 */         if (mag < 1.0D) {
/* 127 */           int lineWidth = 1;
/* 128 */           lineWidth = (int)(lineWidth / mag);
/* 129 */           ip.setLineWidth(lineWidth);
/*     */         }
/*     */       }
/* 132 */       this.roi.drawPixels(ip);
/* 133 */       ip.setLineWidth(1);
/* 134 */       drawLabel(ip);
/*     */     }
/*     */   }
/*     */ 
/*     */   void drawParticleLabels(ImageProcessor ip) {
/* 139 */     ResultsTable rt = ResultsTable.getResultsTable();
/* 140 */     int count = rt.getCounter();
/* 141 */     int first = Analyzer.firstParticle;
/* 142 */     int last = Analyzer.lastParticle;
/* 143 */     if ((count == 0) || (first >= count) || (last >= count))
/* 144 */       return;
/* 145 */     if (!rt.columnExists(6)) {
/* 146 */       IJ.error("Label", "\"Centroids\" required to label particles");
/* 147 */       return;
/*     */     }
/* 149 */     for (int i = first; i <= last; i++) {
/* 150 */       int x = (int)rt.getValueAsDouble(6, i);
/* 151 */       int y = (int)rt.getValueAsDouble(7, i);
/* 152 */       drawLabel(this.imp, ip, i + 1, new Rectangle(x, y, 0, 0));
/*     */     }
/*     */   }
/*     */ 
/*     */   void drawLabel(ImageProcessor ip) {
/* 157 */     int count = Analyzer.getCounter();
/* 158 */     if ((count > 0) && (this.roi != null))
/* 159 */       drawLabel(this.imp, ip, count, this.roi.getBounds());
/*     */   }
/*     */ 
/*     */   public void drawLabel(ImagePlus imp, ImageProcessor ip, int count, Rectangle r) {
/* 163 */     Color foreground = Toolbar.getForegroundColor();
/* 164 */     Color background = Toolbar.getBackgroundColor();
/* 165 */     if (foreground.equals(background)) {
/* 166 */       foreground = Color.black;
/* 167 */       background = Color.white;
/*     */     }
/* 169 */     int size = 9;
/* 170 */     ImageCanvas ic = imp.getCanvas();
/* 171 */     if (ic != null) {
/* 172 */       double mag = ic.getMagnification();
/* 173 */       if (mag < 1.0D)
/* 174 */         size = (int)(size / mag);
/*     */     }
/* 176 */     if ((size == 9) && (r.width > 50) && (r.height > 50))
/* 177 */       size = 12;
/* 178 */     ip.setFont(new Font("SansSerif", 0, size));
/* 179 */     String label = "" + count;
/* 180 */     int w = ip.getStringWidth(label);
/* 181 */     int x = r.x + r.width / 2 - w / 2;
/* 182 */     int y = r.y + r.height / 2 + Math.max(size / 2, 6);
/* 183 */     FontMetrics metrics = ip.getFontMetrics();
/* 184 */     int h = metrics.getHeight();
/* 185 */     ip.setColor(background);
/* 186 */     ip.setRoi(x - 1, y - h + 2, w + 1, h - 3);
/* 187 */     ip.fill();
/* 188 */     ip.resetRoi();
/* 189 */     ip.setColor(foreground);
/* 190 */     ip.drawString(label, x, y);
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public synchronized void clearOutside(ImageProcessor ip)
/*     */   {
/* 198 */     if (isLineSelection()) {
/* 199 */       IJ.error("\"Clear Outside\" does not work with line selections.");
/* 200 */       return;
/*     */     }
/* 202 */     this.sliceCount += 1;
/* 203 */     Rectangle r = ip.getRoi();
/* 204 */     if (this.mask == null)
/* 205 */       makeMask(ip, r);
/* 206 */     ip.setColor(Toolbar.getBackgroundColor());
/* 207 */     int stackSize = this.imp.getStackSize();
/* 208 */     if (stackSize > 1)
/* 209 */       ip.snapshot();
/* 210 */     ip.fill();
/* 211 */     ip.reset(this.mask);
/* 212 */     int width = ip.getWidth();
/* 213 */     int height = ip.getHeight();
/* 214 */     ip.setRoi(0, 0, r.x, height);
/* 215 */     ip.fill();
/* 216 */     ip.setRoi(r.x, 0, r.width, r.y);
/* 217 */     ip.fill();
/* 218 */     ip.setRoi(r.x, r.y + r.height, r.width, height - (r.y + r.height));
/* 219 */     ip.fill();
/* 220 */     ip.setRoi(r.x + r.width, 0, width - (r.x + r.width), height);
/* 221 */     ip.fill();
/* 222 */     ip.setRoi(r);
/* 223 */     if (this.sliceCount == stackSize) {
/* 224 */       ip.setColor(Toolbar.getForegroundColor());
/* 225 */       Roi roi = this.imp.getRoi();
/* 226 */       this.imp.killRoi();
/* 227 */       this.imp.updateAndDraw();
/* 228 */       this.imp.setRoi(roi);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void makeMask(ImageProcessor ip, Rectangle r) {
/* 233 */     this.mask = ip.getMask();
/* 234 */     if (this.mask == null) {
/* 235 */       this.mask = new ByteProcessor(r.width, r.height);
/* 236 */       this.mask.invert();
/*     */     }
/*     */     else {
/* 239 */       this.mask = this.mask.duplicate();
/*     */     }
/* 241 */     this.mask.invert();
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.filter.Filler
 * JD-Core Version:    0.6.2
 */