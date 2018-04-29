/*     */ package ij.plugin.filter;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.Prefs;
/*     */ import ij.Undo;
/*     */ import ij.WindowManager;
/*     */ import ij.gui.DialogListener;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.gui.ImageWindow;
/*     */ import ij.gui.Line;
/*     */ import ij.gui.Roi;
/*     */ import ij.process.FloatProcessor;
/*     */ import ij.process.ImageProcessor;
/*     */ import java.awt.AWTEvent;
/*     */ import java.awt.Checkbox;
/*     */ import java.awt.Rectangle;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ 
/*     */ public class PlugInFilterRunner
/*     */   implements Runnable, DialogListener
/*     */ {
/*     */   private String command;
/*     */   private Object theFilter;
/*     */   private ImagePlus imp;
/*     */   private int flags;
/*     */   private boolean snapshotDone;
/*     */   private boolean previewCheckboxOn;
/*     */   private boolean bgPreviewOn;
/*     */   private boolean bgKeepPreview;
/*     */   private Thread previewThread;
/*     */   private GenericDialog gd;
/*     */   private Checkbox previewCheckbox;
/*     */   private long previewTime;
/*     */   private boolean ipChanged;
/*     */   private int processedAsPreview;
/*     */   private Hashtable slicesForThread;
/*     */   private Hashtable roisForThread;
/*  29 */   Hashtable sliceForThread = new Hashtable();
/*     */   private int nPasses;
/*     */   private int pass;
/*     */   private boolean doStack;
/*     */ 
/*     */   public PlugInFilterRunner(Object theFilter, String command, String arg)
/*     */   {
/*  44 */     this.theFilter = theFilter;
/*  45 */     this.command = command;
/*  46 */     this.imp = WindowManager.getCurrentImage();
/*  47 */     this.flags = ((PlugInFilter)theFilter).setup(arg, this.imp);
/*  48 */     if ((this.flags & 0x1000) != 0) return;
/*  49 */     if (!checkImagePlus(this.imp, this.flags, command)) return;
/*  50 */     if ((this.flags & 0x200) != 0)
/*  51 */       this.imp = null;
/*  52 */     Roi roi = null;
/*  53 */     if (this.imp != null) {
/*  54 */       roi = this.imp.getRoi();
/*  55 */       if (roi != null) roi.endPaste();
/*  56 */       if (!this.imp.lock()) return;
/*  57 */       this.nPasses = ((this.flags & 0x2000) != 0 ? this.imp.getProcessor().getNChannels() : 1);
/*     */     }
/*  59 */     if ((theFilter instanceof ExtendedPlugInFilter)) {
/*  60 */       this.flags = ((ExtendedPlugInFilter)theFilter).showDialog(this.imp, command, this);
/*  61 */       if (this.snapshotDone)
/*  62 */         Undo.setup(1, this.imp);
/*  63 */       boolean keepPreviewFlag = (this.flags & 0x1000000) != 0;
/*  64 */       if ((keepPreviewFlag) && (this.imp != null) && (this.previewThread != null) && (this.ipChanged) && (this.previewCheckbox != null) && (this.previewCheckboxOn))
/*     */       {
/*  66 */         this.bgKeepPreview = true;
/*  67 */         waitForPreviewDone();
/*  68 */         this.processedAsPreview = this.imp.getCurrentSlice();
/*     */       } else {
/*  70 */         killPreview();
/*  71 */         this.previewTime = 0L;
/*     */       }
/*     */     }
/*  74 */     if ((this.flags & 0x1000) != 0) {
/*  75 */       if (this.imp != null) this.imp.unlock();
/*  76 */       return;
/*  77 */     }if (this.imp == null) {
/*  78 */       ((PlugInFilter)theFilter).run(null);
/*  79 */       return;
/*     */     }
/*     */ 
/*  82 */     int slices = this.imp.getStackSize();
/*     */ 
/*  84 */     if ((this.flags & 0x40000) != 0)
/*  85 */       this.flags &= -32769;
/*  86 */     this.doStack = ((slices > 1) && ((this.flags & 0x20) != 0));
/*  87 */     this.imp.startTiming();
/*  88 */     if ((this.doStack) || (this.processedAsPreview == 0)) {
/*  89 */       IJ.showStatus(command + (this.doStack ? " (Stack)..." : "..."));
/*  90 */       ImageProcessor ip = this.imp.getProcessor();
/*  91 */       this.pass = 0;
/*  92 */       if (!this.doStack) {
/*  93 */         FloatProcessor fp = null;
/*  94 */         prepareProcessor(ip, this.imp);
/*  95 */         announceSliceNumber(this.imp.getCurrentSlice());
/*  96 */         if ((theFilter instanceof ExtendedPlugInFilter))
/*  97 */           ((ExtendedPlugInFilter)theFilter).setNPasses(this.nPasses);
/*  98 */         if ((this.flags & 0x80) == 0) {
/*  99 */           boolean disableUndo = (Prefs.disableUndo) || ((this.flags & 0x100) != 0);
/* 100 */           if (!disableUndo) {
/* 101 */             ip.snapshot();
/* 102 */             this.snapshotDone = true;
/*     */           }
/*     */         }
/* 105 */         processOneImage(ip, fp, this.snapshotDone);
/* 106 */         if ((this.flags & 0x80) == 0) {
/* 107 */           if (this.snapshotDone)
/* 108 */             Undo.setup(1, this.imp);
/*     */           else
/* 110 */             Undo.reset();
/*     */         }
/* 112 */         if (((this.flags & 0x80) == 0) && ((this.flags & 0x20000) == 0))
/* 113 */           ip.resetBinaryThreshold();
/*     */       } else {
/* 115 */         Undo.reset();
/* 116 */         IJ.resetEscape();
/* 117 */         int slicesToDo = this.processedAsPreview != 0 ? slices - 1 : slices;
/* 118 */         this.nPasses *= slicesToDo;
/* 119 */         if ((theFilter instanceof ExtendedPlugInFilter))
/* 120 */           ((ExtendedPlugInFilter)theFilter).setNPasses(this.nPasses);
/* 121 */         int threads = 1;
/* 122 */         if ((this.flags & 0x8000) != 0) {
/* 123 */           threads = Prefs.getThreads();
/* 124 */           if (threads > slicesToDo) threads = slicesToDo;
/* 125 */           if (threads > 1) this.slicesForThread = new Hashtable(threads - 1);
/*     */         }
/* 127 */         int startSlice = 1;
/* 128 */         for (int i = 1; i < threads; i++) {
/* 129 */           int endSlice = slicesToDo * i / threads;
/* 130 */           if ((this.processedAsPreview != 0) && (this.processedAsPreview <= endSlice)) endSlice++;
/* 131 */           Thread bgThread = new Thread(this, command + " " + startSlice + "-" + endSlice);
/* 132 */           this.slicesForThread.put(bgThread, new int[] { startSlice, endSlice });
/* 133 */           bgThread.start();
/*     */ 
/* 135 */           startSlice = endSlice + 1;
/*     */         }
/*     */ 
/* 138 */         processStack(startSlice, slices);
/* 139 */         if (this.slicesForThread != null) {
/* 140 */           while (this.slicesForThread.size() > 0) {
/* 141 */             Thread theThread = (Thread)this.slicesForThread.keys().nextElement();
/*     */             try {
/* 143 */               theThread.join(); } catch (InterruptedException e) {
/*     */             }
/* 145 */             this.slicesForThread.remove(theThread);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 150 */     if (((this.flags & 0x10000) != 0) && (!IJ.escapePressed()))
/* 151 */       ((PlugInFilter)theFilter).setup("final", this.imp);
/* 152 */     if (IJ.escapePressed()) {
/* 153 */       IJ.showStatus(command + " INTERRUPTED");
/* 154 */       IJ.showProgress(1, 1);
/*     */     } else {
/* 156 */       IJ.showTime(this.imp, this.imp.getStartTime() - this.previewTime, command + ": ", this.doStack ? slices : 1);
/* 157 */     }IJ.showProgress(1.0D);
/* 158 */     if (this.ipChanged) {
/* 159 */       this.imp.changes = true;
/* 160 */       this.imp.updateAndDraw();
/*     */     }
/* 162 */     ImageWindow win = this.imp.getWindow();
/* 163 */     if (win != null) {
/* 164 */       win.running = false;
/* 165 */       win.running2 = false;
/*     */     }
/* 167 */     this.imp.unlock();
/*     */   }
/*     */ 
/*     */   private void processStack(int firstSlice, int endSlice)
/*     */   {
/* 176 */     ImageStack stack = this.imp.getStack();
/* 177 */     ImageProcessor ip = stack.getProcessor(firstSlice);
/* 178 */     prepareProcessor(ip, this.imp);
/* 179 */     ip.setLineWidth(Line.getWidth());
/* 180 */     FloatProcessor fp = null;
/* 181 */     int slices = this.imp.getNSlices();
/* 182 */     for (int i = firstSlice; i <= endSlice; i++)
/* 183 */       if (i != this.processedAsPreview) {
/* 184 */         announceSliceNumber(i);
/* 185 */         ip.setPixels(stack.getPixels(i));
/* 186 */         ip.setSliceNumber(i);
/* 187 */         processOneImage(ip, fp, false);
/* 188 */         if (IJ.escapePressed()) { IJ.beep(); break;
/*     */         }
/*     */       }
/*     */   }
/*     */ 
/*     */   private void prepareProcessor(ImageProcessor ip, ImagePlus imp)
/*     */   {
/* 196 */     ImageProcessor mask = imp.getMask();
/* 197 */     Roi roi = imp.getRoi();
/* 198 */     if ((roi != null) && (roi.isArea()))
/* 199 */       ip.setRoi(roi);
/*     */     else
/* 201 */       ip.setRoi((Roi)null);
/* 202 */     if (imp.getStackSize() > 1) {
/* 203 */       ImageProcessor ip2 = imp.getProcessor();
/* 204 */       double min1 = ip2.getMinThreshold();
/* 205 */       double max1 = ip2.getMaxThreshold();
/* 206 */       double min2 = ip.getMinThreshold();
/* 207 */       double max2 = ip.getMaxThreshold();
/* 208 */       if ((min1 != -808080.0D) && ((min1 != min2) || (max1 != max2)))
/* 209 */         ip.setThreshold(min1, max1, 2);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void processOneImage(ImageProcessor ip, FloatProcessor fp, boolean snapshotDone)
/*     */   {
/* 226 */     if ((this.flags & 0x40000) != 0) {
/* 227 */       processImageUsingThreads(ip, fp, snapshotDone);
/* 228 */       return;
/*     */     }
/* 230 */     Thread thread = Thread.currentThread();
/* 231 */     boolean convertToFloat = ((this.flags & 0x2000) != 0) && (!(ip instanceof FloatProcessor));
/* 232 */     boolean doMasking = ((this.flags & 0x40) != 0) && (ip.getMask() != null);
/* 233 */     if ((!snapshotDone) && ((doMasking) || (((this.flags & 0x4000) != 0) && (!convertToFloat)))) {
/* 234 */       ip.snapshot();
/* 235 */       this.snapshotDone = true;
/*     */     }
/* 237 */     if (convertToFloat) {
/* 238 */       for (int i = 0; i < ip.getNChannels(); i++) {
/* 239 */         fp = ip.toFloat(i, fp);
/* 240 */         fp.setSliceNumber(ip.getSliceNumber());
/* 241 */         if (thread.isInterrupted()) return;
/* 242 */         if ((this.flags & 0x4000) != 0) fp.snapshot();
/* 243 */         if (this.doStack) IJ.showProgress(this.pass / this.nPasses);
/* 244 */         ((PlugInFilter)this.theFilter).run(fp);
/* 245 */         if (thread.isInterrupted()) return;
/*     */ 
/* 247 */         this.pass += 1;
/* 248 */         if ((this.flags & 0x80) == 0) {
/* 249 */           this.ipChanged = true;
/* 250 */           ip.setPixels(i, fp);
/*     */         }
/*     */       }
/*     */     } else {
/* 254 */       if ((this.flags & 0x80) == 0) this.ipChanged = true;
/* 255 */       if (this.doStack) IJ.showProgress(this.pass / this.nPasses);
/* 256 */       ((PlugInFilter)this.theFilter).run(ip);
/* 257 */       this.pass += 1;
/*     */     }
/* 259 */     if (thread.isInterrupted()) return;
/* 260 */     if (doMasking)
/* 261 */       ip.reset(ip.getMask());
/*     */   }
/*     */ 
/*     */   private void processImageUsingThreads(ImageProcessor ip, FloatProcessor fp, boolean snapshotDone) {
/* 265 */     Thread thread = Thread.currentThread();
/* 266 */     boolean convertToFloat = ((this.flags & 0x2000) != 0) && (!(ip instanceof FloatProcessor));
/* 267 */     boolean doMasking = ((this.flags & 0x40) != 0) && (ip.getMask() != null);
/* 268 */     if ((!snapshotDone) && ((doMasking) || (((this.flags & 0x4000) != 0) && (!convertToFloat)))) {
/* 269 */       ip.snapshot();
/* 270 */       this.snapshotDone = true;
/*     */     }
/* 272 */     if (convertToFloat) {
/* 273 */       for (int i = 0; i < ip.getNChannels(); i++) {
/* 274 */         fp = ip.toFloat(i, fp);
/* 275 */         fp.setSliceNumber(ip.getSliceNumber());
/* 276 */         if (thread.isInterrupted()) return;
/* 277 */         if ((this.flags & 0x4000) != 0) fp.snapshot();
/* 278 */         if (this.doStack) IJ.showProgress(this.pass / this.nPasses);
/* 279 */         processChannelUsingThreads(fp);
/* 280 */         if (thread.isInterrupted()) return;
/*     */ 
/* 282 */         if ((this.flags & 0x80) == 0) {
/* 283 */           this.ipChanged = true;
/* 284 */           ip.setPixels(i, fp);
/*     */         }
/*     */       }
/*     */     } else {
/* 288 */       if ((this.flags & 0x80) == 0) this.ipChanged = true;
/* 289 */       if (this.doStack) IJ.showProgress(this.pass / this.nPasses);
/* 290 */       processChannelUsingThreads(ip);
/*     */     }
/* 292 */     if (thread.isInterrupted()) return;
/* 293 */     if (doMasking)
/* 294 */       ip.reset(ip.getMask());
/*     */   }
/*     */ 
/*     */   private void processChannelUsingThreads(ImageProcessor ip) {
/* 298 */     ImageProcessor mask = ip.getMask();
/* 299 */     Rectangle roi = ip.getRoi();
/* 300 */     int threads = Prefs.getThreads();
/* 301 */     if (threads > roi.height) threads = roi.height;
/* 302 */     if (threads > 1) this.roisForThread = new Hashtable(threads - 1);
/* 303 */     int y1 = roi.y;
/* 304 */     for (int i = 1; i < threads; i++) {
/* 305 */       int y2 = roi.y + roi.height * i / threads - 1;
/* 306 */       Thread bgThread = new Thread(this, this.command + " " + y1 + "-" + y2);
/* 307 */       Rectangle roi2 = new Rectangle(roi.x, y1, roi.width, y2 - y1 + 1);
/* 308 */       this.roisForThread.put(bgThread, duplicateProcessor(ip, roi2));
/* 309 */       bgThread.start();
/*     */ 
/* 311 */       y1 = y2 + 1;
/*     */     }
/*     */ 
/* 314 */     ip.setRoi(new Rectangle(roi.x, y1, roi.width, roi.y + roi.height - y1));
/* 315 */     ((PlugInFilter)this.theFilter).run(ip);
/* 316 */     this.pass += 1;
/* 317 */     if (this.roisForThread != null) {
/* 318 */       while (this.roisForThread.size() > 0) {
/* 319 */         Thread theThread = (Thread)this.roisForThread.keys().nextElement();
/*     */         try {
/* 321 */           theThread.join(); } catch (InterruptedException e) {
/*     */         }
/* 323 */         this.roisForThread.remove(theThread);
/*     */       }
/*     */     }
/* 326 */     this.roisForThread = null;
/* 327 */     ip.setMask(mask);
/* 328 */     ip.setRoi(roi);
/*     */   }
/*     */ 
/*     */   ImageProcessor duplicateProcessor(ImageProcessor ip, Rectangle roi) {
/* 332 */     ImageProcessor ip2 = (ImageProcessor)ip.clone();
/* 333 */     ip2.setRoi(roi);
/* 334 */     return ip2;
/*     */   }
/*     */ 
/*     */   private boolean checkImagePlus(ImagePlus imp, int flags, String cmd)
/*     */   {
/* 341 */     boolean imageRequired = (flags & 0x200) == 0;
/* 342 */     if ((imageRequired) && (imp == null)) {
/* 343 */       IJ.noImage(); return false;
/* 344 */     }if (imageRequired) {
/* 345 */       if (imp.getProcessor() == null) {
/* 346 */         wrongType(flags, cmd); return false;
/* 347 */       }int type = imp.getType();
/* 348 */       switch (type) {
/*     */       case 0:
/* 350 */         if ((flags & 0x1) == 0) {
/* 351 */           wrongType(flags, cmd); return false;
/*     */         }break;
/*     */       case 3:
/* 354 */         if ((flags & 0x2) == 0) {
/* 355 */           wrongType(flags, cmd); return false;
/*     */         }break;
/*     */       case 1:
/* 358 */         if ((flags & 0x4) == 0) {
/* 359 */           wrongType(flags, cmd); return false;
/*     */         }break;
/*     */       case 2:
/* 362 */         if ((flags & 0x8) == 0) {
/* 363 */           wrongType(flags, cmd); return false;
/*     */         }break;
/*     */       case 4:
/* 366 */         if ((flags & 0x10) == 0) {
/* 367 */           wrongType(flags, cmd); return false;
/*     */         }break;
/*     */       }
/* 370 */       if (((flags & 0x400) != 0) && (imp.getRoi() == null)) {
/* 371 */         IJ.error(cmd, "This command requires a selection"); return false;
/* 372 */       }if (((flags & 0x800) != 0) && (imp.getStackSize() == 1)) {
/* 373 */         IJ.error(cmd, "This command requires a stack"); return false;
/*     */       }
/*     */     }
/* 375 */     return true;
/*     */   }
/*     */ 
/*     */   static void wrongType(int flags, String cmd)
/*     */   {
/* 381 */     String s = "\"" + cmd + "\" requires an image of type:\n \n";
/* 382 */     if ((flags & 0x1) != 0) s = s + "\t8-bit grayscale\n";
/* 383 */     if ((flags & 0x2) != 0) s = s + "\t8-bit color\n";
/* 384 */     if ((flags & 0x4) != 0) s = s + "\t16-bit grayscale\n";
/* 385 */     if ((flags & 0x8) != 0) s = s + "\t32-bit (float) grayscale\n";
/* 386 */     if ((flags & 0x10) != 0) s = s + "\tRGB color\n";
/* 387 */     IJ.error(s);
/*     */   }
/*     */ 
/*     */   private void announceSliceNumber(int slice)
/*     */   {
/* 394 */     synchronized (this.sliceForThread) {
/* 395 */       Integer number = new Integer(slice);
/* 396 */       this.sliceForThread.put(Thread.currentThread(), number);
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getSliceNumber()
/*     */   {
/* 404 */     synchronized (this.sliceForThread) {
/* 405 */       Integer number = (Integer)this.sliceForThread.get(Thread.currentThread());
/* 406 */       return number == null ? -1 : number.intValue();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/* 413 */     Thread thread = Thread.currentThread();
/*     */     try {
/* 415 */       if (thread == this.previewThread) {
/* 416 */         runPreview();
/* 417 */       } else if ((this.roisForThread != null) && (this.roisForThread.containsKey(thread))) {
/* 418 */         ImageProcessor ip = (ImageProcessor)this.roisForThread.get(thread);
/* 419 */         ((PlugInFilter)this.theFilter).run(ip);
/* 420 */         ip.setPixels(null);
/* 421 */         ip.setSnapshotPixels(null);
/* 422 */       } else if ((this.slicesForThread != null) && (this.slicesForThread.containsKey(thread))) {
/* 423 */         int[] range = (int[])this.slicesForThread.get(thread);
/* 424 */         processStack(range[0], range[1]);
/*     */       } else {
/* 426 */         IJ.error("PlugInFilterRunner internal error:\nunsolicited background thread");
/*     */       }
/*     */     } catch (Exception err) { if (thread == this.previewThread) {
/* 429 */         this.gd.previewRunning(false);
/* 430 */         IJ.wait(100);
/* 431 */         this.previewCheckbox.setState(false);
/* 432 */         this.bgPreviewOn = false;
/* 433 */         this.previewThread = null;
/*     */       }
/* 435 */       String msg = "" + err;
/* 436 */       if (msg.indexOf("Macro canceled") == -1) {
/* 437 */         IJ.beep();
/* 438 */         IJ.log("ERROR: " + msg + "\nin " + thread.getName() + "\nat " + err.getStackTrace()[0] + "\nfrom " + err.getStackTrace()[1]);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void runPreview()
/*     */   {
/* 446 */     if (IJ.debugMode) IJ.log("preview thread started; imp=" + this.imp.getTitle());
/* 447 */     Thread thread = Thread.currentThread();
/* 448 */     ImageProcessor ip = this.imp.getProcessor();
/* 449 */     Roi originalRoi = this.imp.getRoi();
/* 450 */     FloatProcessor fp = null;
/* 451 */     prepareProcessor(ip, this.imp);
/* 452 */     announceSliceNumber(this.imp.getCurrentSlice());
/* 453 */     if ((!this.snapshotDone) && ((this.flags & 0x80) == 0)) {
/* 454 */       ip.snapshot();
/* 455 */       this.snapshotDone = true;
/*     */     }
/* 457 */     boolean previewDataOk = false;
/* 458 */     while (this.bgPreviewOn) {
/* 459 */       if (this.previewCheckboxOn) this.gd.previewRunning(true);
/*     */ 
/* 461 */       if (this.imp.getRoi() != originalRoi) {
/* 462 */         this.imp.setRoi(originalRoi);
/* 463 */         if ((originalRoi != null) && (originalRoi.isArea()))
/* 464 */           ip.setRoi(originalRoi);
/*     */         else
/* 466 */           ip.setRoi((Roi)null);
/*     */       }
/* 468 */       if (this.ipChanged)
/* 469 */         ip.reset();
/* 470 */       this.ipChanged = false;
/* 471 */       previewDataOk = false;
/* 472 */       long startTime = System.currentTimeMillis();
/* 473 */       this.pass = 0;
/* 474 */       if ((this.theFilter instanceof ExtendedPlugInFilter))
/* 475 */         ((ExtendedPlugInFilter)this.theFilter).setNPasses(this.nPasses);
/* 476 */       if (!thread.isInterrupted())
/*     */       {
/* 478 */         processOneImage(ip, fp, true);
/* 479 */         IJ.showProgress(1.0D);
/* 480 */         if (!thread.isInterrupted())
/*     */         {
/* 482 */           previewDataOk = true;
/* 483 */           this.previewTime = (System.currentTimeMillis() - startTime);
/* 484 */           this.imp.updateAndDraw();
/* 485 */           if (IJ.debugMode) IJ.log("preview processing done"); 
/*     */         }
/*     */       }
/* 487 */       this.gd.previewRunning(false);
/* 488 */       IJ.showStatus("");
/* 489 */       synchronized (this) {
/* 490 */         if (!this.bgPreviewOn)
/*     */           break;
/*     */         try {
/* 493 */           wait();
/*     */         } catch (InterruptedException e) {
/* 495 */           previewDataOk = false;
/*     */         }
/*     */       }
/*     */     }
/* 499 */     if (thread.isInterrupted())
/* 500 */       previewDataOk = false;
/* 501 */     if ((!previewDataOk) || (!this.bgKeepPreview)) {
/* 502 */       this.imp.setRoi(originalRoi);
/* 503 */       if (this.ipChanged) {
/* 504 */         ip.reset();
/* 505 */         this.ipChanged = false;
/*     */       }
/*     */     }
/* 508 */     this.imp.updateAndDraw();
/* 509 */     this.sliceForThread.remove(thread);
/*     */   }
/*     */ 
/*     */   private void killPreview()
/*     */   {
/* 515 */     if (this.previewThread == null)
/*     */       return;
/*     */     Enumeration en;
/* 517 */     synchronized (this) {
/* 518 */       this.previewThread.interrupt();
/* 519 */       this.bgPreviewOn = false;
/* 520 */       if (this.roisForThread != null) {
/* 521 */         for (en = this.roisForThread.keys(); en.hasMoreElements(); ) {
/* 522 */           Thread thread = (Thread)en.nextElement();
/* 523 */           thread.interrupt();
/*     */         }
/*     */       }
/*     */     }
/* 527 */     waitForPreviewDone();
/*     */   }
/*     */ 
/*     */   private void waitForPreviewDone()
/*     */   {
/* 532 */     if (this.previewThread.isAlive()) try {
/* 533 */         this.previewThread.setPriority(Thread.currentThread().getPriority());
/*     */       } catch (Exception e) {
/*     */       } synchronized (this) {
/* 536 */       this.bgPreviewOn = false;
/* 537 */       notify();
/*     */     }try {
/* 539 */       this.previewThread.join(); } catch (InterruptedException e) {
/*     */     }
/* 541 */     this.previewThread = null;
/*     */   }
/*     */ 
/*     */   public void setDialog(GenericDialog gd)
/*     */   {
/* 548 */     if ((gd != null) && (this.imp != null)) {
/* 549 */       this.previewCheckbox = gd.getPreviewCheckbox();
/* 550 */       if (this.previewCheckbox != null) {
/* 551 */         gd.addDialogListener(this);
/* 552 */         this.gd = gd;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean dialogItemChanged(GenericDialog gd, AWTEvent e)
/*     */   {
/* 569 */     if ((this.previewCheckbox == null) || (this.imp == null)) return true;
/* 570 */     this.previewCheckboxOn = this.previewCheckbox.getState();
/* 571 */     if ((this.previewCheckboxOn) && (this.previewThread == null)) {
/* 572 */       this.bgPreviewOn = true;
/* 573 */       this.previewThread = new Thread(this, this.command + " Preview");
/* 574 */       int priority = Thread.currentThread().getPriority() - 2;
/* 575 */       if (priority < 1) priority = 1;
/* 576 */       this.previewThread.setPriority(priority);
/* 577 */       this.previewThread.start();
/* 578 */       if (IJ.debugMode) IJ.log(this.command + " Preview thread was started");
/* 579 */       return true;
/*     */     }
/* 581 */     if (this.previewThread != null) {
/* 582 */       if (!this.previewCheckboxOn) {
/* 583 */         killPreview();
/* 584 */         return true;
/*     */       }
/* 586 */       this.previewThread.interrupt();
/*     */     }
/* 588 */     return true;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.filter.PlugInFilterRunner
 * JD-Core Version:    0.6.2
 */