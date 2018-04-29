/*     */ package ij.plugin.filter;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.Macro;
/*     */ import ij.gui.DialogListener;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.gui.Roi;
/*     */ import ij.io.OpenDialog;
/*     */ import ij.io.SaveDialog;
/*     */ import ij.plugin.TextReader;
/*     */ import ij.plugin.frame.Recorder;
/*     */ import ij.process.ByteProcessor;
/*     */ import ij.process.ColorProcessor;
/*     */ import ij.process.FloatProcessor;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.util.Tools;
/*     */ import java.awt.AWTEvent;
/*     */ import java.awt.Button;
/*     */ import java.awt.Checkbox;
/*     */ import java.awt.FlowLayout;
/*     */ import java.awt.Panel;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.TextArea;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ 
/*     */ public class Convolver
/*     */   implements ExtendedPlugInFilter, DialogListener, ActionListener
/*     */ {
/*     */   private ImagePlus imp;
/*     */   private int kw;
/*     */   private int kh;
/*     */   private boolean canceled;
/*     */   private float[] kernel;
/*     */   private boolean isLineRoi;
/*     */   private Button open;
/*     */   private Button save;
/*     */   private GenericDialog gd;
/*  24 */   private boolean normalize = true;
/*     */   private int nSlices;
/*  26 */   private int flags = 16867423;
/*  27 */   private int nPasses = 1;
/*     */   private boolean kernelError;
/*     */   private PlugInFilterRunner pfr;
/*     */   private Thread mainThread;
/*     */   private int pass;
/*  34 */   static String kernelText = "-1 -1 -1 -1 -1\n-1 -1 -1 -1 -1\n-1 -1 24 -1 -1\n-1 -1 -1 -1 -1\n-1 -1 -1 -1 -1\n";
/*  35 */   static boolean normalizeFlag = true;
/*     */ 
/*     */   public int setup(String arg, ImagePlus imp) {
/*  38 */     this.imp = imp;
/*  39 */     this.mainThread = Thread.currentThread();
/*  40 */     if (imp == null) {
/*  41 */       IJ.noImage(); return 4096;
/*  42 */     }if ((arg.equals("final")) && (imp.getRoi() == null)) {
/*  43 */       imp.getProcessor().resetMinAndMax();
/*  44 */       imp.updateAndDraw();
/*  45 */       return 4096;
/*     */     }
/*  47 */     IJ.resetEscape();
/*  48 */     Roi roi = imp.getRoi();
/*  49 */     this.isLineRoi = ((roi != null) && (roi.isLine()));
/*  50 */     this.nSlices = imp.getStackSize();
/*  51 */     if (imp.getStackSize() == 1)
/*  52 */       this.flags |= 262144;
/*     */     else
/*  54 */       this.flags |= 32768;
/*  55 */     imp.startTiming();
/*  56 */     return this.flags;
/*     */   }
/*     */ 
/*     */   public void run(ImageProcessor ip) {
/*  60 */     if (this.canceled) return;
/*  61 */     if (this.isLineRoi) ip.resetRoi();
/*  62 */     if (!this.kernelError)
/*  63 */       convolve(ip, this.kernel, this.kw, this.kh);
/*     */   }
/*     */ 
/*     */   public int showDialog(ImagePlus imp, String command, PlugInFilterRunner pfr) {
/*  67 */     this.gd = new GenericDialog("Convolver...", IJ.getInstance());
/*  68 */     this.gd.addTextAreas(kernelText, null, 10, 30);
/*  69 */     this.gd.addPanel(makeButtonPanel(this.gd));
/*  70 */     this.gd.addCheckbox("Normalize Kernel", normalizeFlag);
/*  71 */     this.gd.addPreviewCheckbox(pfr);
/*  72 */     this.gd.addDialogListener(this);
/*  73 */     this.gd.showDialog();
/*  74 */     if (this.gd.wasCanceled()) return 4096;
/*  75 */     this.pfr = pfr;
/*  76 */     return IJ.setupDialog(imp, this.flags);
/*     */   }
/*     */ 
/*     */   public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
/*  80 */     kernelText = gd.getNextText();
/*  81 */     normalizeFlag = gd.getNextBoolean();
/*  82 */     this.normalize = normalizeFlag;
/*  83 */     this.kernelError = (!decodeKernel(kernelText));
/*  84 */     if (!this.kernelError) {
/*  85 */       IJ.showStatus("Convolve: " + this.kw + "x" + this.kh + " kernel");
/*  86 */       return true;
/*     */     }
/*  88 */     return !gd.getPreviewCheckbox().getState();
/*     */   }
/*     */ 
/*     */   boolean decodeKernel(String text) {
/*  92 */     if ((Macro.getOptions() != null) && (!hasNewLine(text)))
/*  93 */       return decodeSquareKernel(text);
/*  94 */     String[] rows = Tools.split(text, "\n");
/*  95 */     this.kh = rows.length;
/*  96 */     if (this.kh == 0) return false;
/*  97 */     String[] values = Tools.split(rows[0]);
/*  98 */     this.kw = values.length;
/*  99 */     this.kernel = new float[this.kw * this.kh];
/* 100 */     boolean done = this.gd.wasOKed();
/* 101 */     int i = 0;
/* 102 */     for (int y = 0; y < this.kh; y++) {
/* 103 */       values = Tools.split(rows[y]);
/* 104 */       if (values.length != this.kw) {
/* 105 */         String err = "Row " + (y + 1) + " is not the same length as the first row";
/* 106 */         if (done)
/* 107 */           IJ.error("Convolver", err);
/*     */         else
/* 109 */           IJ.showStatus(err);
/* 110 */         return false;
/*     */       }
/* 112 */       for (int x = 0; x < this.kw; x++)
/* 113 */         this.kernel[(i++)] = ((float)Tools.parseDouble(values[x], 0.0D));
/*     */     }
/* 115 */     if (((this.kw & 0x1) != 1) || ((this.kh & 0x1) != 1)) {
/* 116 */       String err = "Kernel must have odd width and height. This one is " + this.kw + "x" + this.kh + ".";
/* 117 */       if (done)
/* 118 */         IJ.error("Convolver", err);
/*     */       else
/* 120 */         IJ.showStatus(err);
/* 121 */       return false;
/*     */     }
/* 123 */     return true;
/*     */   }
/*     */ 
/*     */   boolean hasNewLine(String text) {
/* 127 */     for (int i = 0; i < text.length(); i++) {
/* 128 */       if (text.charAt(i) == '\n') return true;
/*     */     }
/* 130 */     return false;
/*     */   }
/*     */ 
/*     */   boolean decodeSquareKernel(String text) {
/* 134 */     String[] values = Tools.split(text);
/* 135 */     int n = values.length;
/* 136 */     this.kw = ((int)Math.sqrt(n));
/* 137 */     this.kh = this.kw;
/* 138 */     n = this.kw * this.kh;
/* 139 */     this.kernel = new float[n];
/* 140 */     for (int i = 0; i < n; i++)
/* 141 */       this.kernel[i] = ((float)Tools.parseDouble(values[i]));
/* 142 */     if ((this.kw >= 3) && ((this.kw & 0x1) == 1)) {
/* 143 */       StringBuffer sb = new StringBuffer();
/* 144 */       int i = 0;
/* 145 */       for (int y = 0; y < this.kh; y++) {
/* 146 */         for (int x = 0; x < this.kw; x++) {
/* 147 */           sb.append("" + this.kernel[(i++)]);
/* 148 */           if (x < this.kw - 1) sb.append(" ");
/*     */         }
/* 150 */         sb.append("\n");
/*     */       }
/* 152 */       kernelText = new String(sb);
/* 153 */       this.gd.getTextArea1().setText(new String(sb));
/* 154 */       return true;
/*     */     }
/* 156 */     IJ.error("Kernel must be square with odd width. This one is " + this.kw + "x" + this.kh + ".");
/* 157 */     return false;
/*     */   }
/*     */ 
/*     */   Panel makeButtonPanel(GenericDialog gd)
/*     */   {
/* 163 */     Panel buttons = new Panel();
/* 164 */     buttons.setLayout(new FlowLayout(1, 5, 0));
/* 165 */     this.open = new Button("Open...");
/* 166 */     this.open.addActionListener(this);
/* 167 */     buttons.add(this.open);
/* 168 */     this.save = new Button("Save...");
/* 169 */     this.save.addActionListener(this);
/* 170 */     buttons.add(this.save);
/* 171 */     return buttons;
/*     */   }
/*     */ 
/*     */   public boolean convolve(ImageProcessor ip, float[] kernel, int kw, int kh)
/*     */   {
/* 177 */     if ((this.canceled) || (kw * kh != kernel.length)) return false;
/* 178 */     if (((kw & 0x1) != 1) || ((kh & 0x1) != 1))
/* 179 */       throw new IllegalArgumentException("Kernel width or height not odd (" + kw + "x" + kh + ")");
/* 180 */     boolean notFloat = !(ip instanceof FloatProcessor);
/* 181 */     ImageProcessor ip2 = ip;
/* 182 */     if (notFloat) {
/* 183 */       if ((ip2 instanceof ColorProcessor))
/* 184 */         throw new IllegalArgumentException("RGB images not supported");
/* 185 */       ip2 = ip2.convertToFloat();
/*     */     }
/* 187 */     if ((kw == 1) || (kh == 1))
/* 188 */       convolveFloat1D(ip2, kernel, kw, kh);
/*     */     else
/* 190 */       convolveFloat(ip2, kernel, kw, kh);
/* 191 */     if (notFloat) {
/* 192 */       if ((ip instanceof ByteProcessor))
/* 193 */         ip2 = ip2.convertToByte(false);
/*     */       else
/* 195 */         ip2 = ip2.convertToShort(false);
/* 196 */       ip.setPixels(ip2.getPixels());
/*     */     }
/* 198 */     return !this.canceled;
/*     */   }
/*     */ 
/*     */   public void setNormalize(boolean normalizeKernel) {
/* 202 */     this.normalize = normalizeKernel;
/*     */   }
/*     */ 
/*     */   public boolean convolveFloat(ImageProcessor ip, float[] kernel, int kw, int kh)
/*     */   {
/* 209 */     if (!(ip instanceof FloatProcessor))
/* 210 */       throw new IllegalArgumentException("FloatProcessor required");
/* 211 */     if (this.canceled) return false;
/* 212 */     int width = ip.getWidth();
/* 213 */     int height = ip.getHeight();
/* 214 */     Rectangle r = ip.getRoi();
/*     */ 
/* 218 */     int x1 = r.x;
/* 219 */     int y1 = r.y;
/* 220 */     int x2 = x1 + r.width;
/* 221 */     int y2 = y1 + r.height;
/* 222 */     int uc = kw / 2;
/* 223 */     int vc = kh / 2;
/* 224 */     float[] pixels = (float[])ip.getPixels();
/* 225 */     float[] pixels2 = (float[])ip.getSnapshotPixels();
/* 226 */     if (pixels2 == null)
/* 227 */       pixels2 = (float[])ip.getPixelsCopy();
/* 228 */     double scale = getScale(kernel);
/* 229 */     Thread thread = Thread.currentThread();
/* 230 */     boolean isMainThread = (thread == this.mainThread) || (thread.getName().indexOf("Preview") != -1);
/* 231 */     if (isMainThread) this.pass += 1;
/*     */ 
/* 235 */     int xedge = width - uc;
/* 236 */     int yedge = height - vc;
/* 237 */     long lastTime = System.currentTimeMillis();
/* 238 */     for (int y = y1; y < y2; y++) {
/* 239 */       long time = System.currentTimeMillis();
/* 240 */       if (time - lastTime > 100L) {
/* 241 */         lastTime = time;
/* 242 */         if (thread.isInterrupted()) return false;
/* 243 */         if (isMainThread) {
/* 244 */           if (IJ.escapePressed()) {
/* 245 */             this.canceled = true;
/* 246 */             ip.reset();
/* 247 */             ImageProcessor originalIp = this.imp.getProcessor();
/* 248 */             if (originalIp.getNChannels() > 1)
/* 249 */               originalIp.reset();
/* 250 */             return false;
/*     */           }
/* 252 */           showProgress((y - y1) / (y2 - y1));
/*     */         }
/*     */       }
/* 255 */       for (int x = x1; x < x2; x++) {
/* 256 */         if (this.canceled) return false;
/* 257 */         double sum = 0.0D;
/* 258 */         int i = 0;
/* 259 */         boolean edgePixel = (y < vc) || (y >= yedge) || (x < uc) || (x >= xedge);
/* 260 */         for (int v = -vc; v <= vc; v++) {
/* 261 */           int offset = x + (y + v) * width;
/* 262 */           for (int u = -uc; u <= uc; u++)
/* 263 */             if (edgePixel) {
/* 264 */               if (i >= kernel.length)
/* 265 */                 IJ.log("kernel index error: " + i);
/* 266 */               sum += getPixel(x + u, y + v, pixels2, width, height) * kernel[(i++)];
/*     */             } else {
/* 268 */               sum += pixels2[(offset + u)] * kernel[(i++)];
/*     */             }
/*     */         }
/* 271 */         pixels[(x + y * width)] = ((float)(sum * scale));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 276 */     return true;
/*     */   }
/*     */ 
/*     */   void convolveFloat1D(ImageProcessor ip, float[] kernel, int kw, int kh)
/*     */   {
/* 282 */     if (!(ip instanceof FloatProcessor))
/* 283 */       throw new IllegalArgumentException("FloatProcessor required");
/* 284 */     int width = ip.getWidth();
/* 285 */     int height = ip.getHeight();
/* 286 */     Rectangle r = ip.getRoi();
/* 287 */     int x1 = r.x;
/* 288 */     int y1 = r.y;
/* 289 */     int x2 = x1 + r.width;
/* 290 */     int y2 = y1 + r.height;
/* 291 */     int uc = kw / 2;
/* 292 */     int vc = kh / 2;
/* 293 */     float[] pixels = (float[])ip.getPixels();
/* 294 */     float[] pixels2 = (float[])ip.getSnapshotPixels();
/* 295 */     if (pixels2 == null)
/* 296 */       pixels2 = (float[])ip.getPixelsCopy();
/* 297 */     double scale = getScale(kernel);
/* 298 */     boolean vertical = kw == 1;
/*     */ 
/* 303 */     int xedge = width - uc;
/* 304 */     int yedge = height - vc;
/* 305 */     for (int y = y1; y < y2; y++)
/* 306 */       for (int x = x1; x < x2; x++) {
/* 307 */         double sum = 0.0D;
/* 308 */         int i = 0;
/* 309 */         if (vertical) {
/* 310 */           boolean edgePixel = (y < vc) || (y >= yedge);
/* 311 */           int offset = x + (y - vc) * width;
/* 312 */           for (int v = -vc; v <= vc; v++) {
/* 313 */             if (edgePixel)
/* 314 */               sum += getPixel(x + uc, y + v, pixels2, width, height) * kernel[(i++)];
/*     */             else
/* 316 */               sum += pixels2[(offset + uc)] * kernel[(i++)];
/* 317 */             offset += width;
/*     */           }
/*     */         } else {
/* 320 */           boolean edgePixel = (x < uc) || (x >= xedge);
/* 321 */           int offset = x + (y - vc) * width;
/* 322 */           for (int u = -uc; u <= uc; u++) {
/* 323 */             if (edgePixel)
/* 324 */               sum += getPixel(x + u, y + vc, pixels2, width, height) * kernel[(i++)];
/*     */             else
/* 326 */               sum += pixels2[(offset + u)] * kernel[(i++)];
/*     */           }
/*     */         }
/* 329 */         pixels[(x + y * width)] = ((float)(sum * scale));
/*     */       }
/*     */   }
/*     */ 
/*     */   double getScale(float[] kernel)
/*     */   {
/* 335 */     double scale = 1.0D;
/* 336 */     if (this.normalize) {
/* 337 */       double sum = 0.0D;
/* 338 */       for (int i = 0; i < kernel.length; i++)
/* 339 */         sum += kernel[i];
/* 340 */       if (sum != 0.0D)
/* 341 */         scale = (float)(1.0D / sum);
/*     */     }
/* 343 */     return scale;
/*     */   }
/*     */ 
/*     */   private float getPixel(int x, int y, float[] pixels, int width, int height) {
/* 347 */     if (x <= 0) x = 0;
/* 348 */     if (x >= width) x = width - 1;
/* 349 */     if (y <= 0) y = 0;
/* 350 */     if (y >= height) y = height - 1;
/* 351 */     return pixels[(x + y * width)];
/*     */   }
/*     */ 
/*     */   void save() {
/* 355 */     TextArea ta1 = this.gd.getTextArea1();
/* 356 */     ta1.selectAll();
/* 357 */     String text = ta1.getText();
/* 358 */     ta1.select(0, 0);
/* 359 */     if ((text == null) || (text.length() == 0))
/* 360 */       return;
/* 361 */     text = text + "\n";
/* 362 */     SaveDialog sd = new SaveDialog("Save as Text...", "kernel", ".txt");
/* 363 */     String name = sd.getFileName();
/* 364 */     if (name == null)
/* 365 */       return;
/* 366 */     String directory = sd.getDirectory();
/* 367 */     PrintWriter pw = null;
/*     */     try {
/* 369 */       FileOutputStream fos = new FileOutputStream(directory + name);
/* 370 */       BufferedOutputStream bos = new BufferedOutputStream(fos);
/* 371 */       pw = new PrintWriter(bos);
/*     */     }
/*     */     catch (IOException e) {
/* 374 */       IJ.error("" + e);
/* 375 */       return;
/*     */     }
/* 377 */     IJ.wait(250);
/* 378 */     pw.print(text);
/* 379 */     pw.close();
/*     */   }
/*     */ 
/*     */   void open() {
/* 383 */     OpenDialog od = new OpenDialog("Open Kernel...", "");
/* 384 */     String directory = od.getDirectory();
/* 385 */     String name = od.getFileName();
/* 386 */     if (name == null)
/* 387 */       return;
/* 388 */     String path = directory + name;
/* 389 */     TextReader tr = new TextReader();
/* 390 */     ImageProcessor ip = tr.open(path);
/* 391 */     if (ip == null)
/* 392 */       return;
/* 393 */     int width = ip.getWidth();
/* 394 */     int height = ip.getHeight();
/* 395 */     if (((width & 0x1) != 1) || ((height & 0x1) != 1)) {
/* 396 */       IJ.error("Convolver", "Kernel must be have odd width and height");
/* 397 */       return;
/*     */     }
/* 399 */     StringBuffer sb = new StringBuffer();
/* 400 */     boolean integers = true;
/* 401 */     for (int y = 0; y < height; y++) {
/* 402 */       for (int x = 0; x < width; x++) {
/* 403 */         double v = ip.getPixelValue(x, y);
/* 404 */         if ((int)v != v)
/* 405 */           integers = false;
/*     */       }
/*     */     }
/* 408 */     for (int y = 0; y < height; y++) {
/* 409 */       for (int x = 0; x < width; x++) {
/* 410 */         if (x != 0) sb.append(" ");
/* 411 */         double v = ip.getPixelValue(x, y);
/* 412 */         if (integers)
/* 413 */           sb.append(IJ.d2s(ip.getPixelValue(x, y), 0));
/*     */         else
/* 415 */           sb.append("" + ip.getPixelValue(x, y));
/*     */       }
/* 417 */       if (y != height - 1)
/* 418 */         sb.append("\n");
/*     */     }
/* 420 */     this.gd.getTextArea1().setText(new String(sb));
/*     */   }
/*     */ 
/*     */   public void setNPasses(int nPasses) {
/* 424 */     this.nPasses = nPasses;
/* 425 */     this.pass = 0;
/*     */   }
/*     */ 
/*     */   private void showProgress(double percent) {
/* 429 */     percent = (this.pass - 1) / this.nPasses + percent / this.nPasses;
/* 430 */     IJ.showProgress(percent);
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent e) {
/* 434 */     Object source = e.getSource();
/* 435 */     Recorder.disablePathRecording();
/* 436 */     if (source == this.save)
/* 437 */       save();
/* 438 */     else if (source == this.open)
/* 439 */       open();
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.filter.Convolver
 * JD-Core Version:    0.6.2
 */