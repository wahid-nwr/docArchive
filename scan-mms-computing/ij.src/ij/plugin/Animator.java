/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.Prefs;
/*     */ import ij.WindowManager;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.gui.ImageWindow;
/*     */ import ij.gui.StackWindow;
/*     */ import ij.measure.Calibration;
/*     */ 
/*     */ public class Animator
/*     */   implements PlugIn
/*     */ {
/*  10 */   private static double animationRate = Prefs.getDouble("fps", 7.0D);
/*  11 */   private static int firstFrame = 0; private static int lastFrame = 0;
/*     */   private ImagePlus imp;
/*     */   private StackWindow swin;
/*     */   private int slice;
/*     */   private int nSlices;
/*     */ 
/*     */   public void run(String arg)
/*     */   {
/*  22 */     this.imp = WindowManager.getCurrentImage();
/*  23 */     if (this.imp == null) {
/*  24 */       IJ.noImage(); return;
/*  25 */     }this.nSlices = this.imp.getStackSize();
/*  26 */     if (this.nSlices < 2) {
/*  27 */       IJ.error("Stack required."); return;
/*  28 */     }ImageWindow win = this.imp.getWindow();
/*  29 */     if ((win == null) || (!(win instanceof StackWindow))) {
/*  30 */       if (arg.equals("next"))
/*  31 */         this.imp.setSlice(this.imp.getCurrentSlice() + 1);
/*  32 */       else if (arg.equals("previous"))
/*  33 */         this.imp.setSlice(this.imp.getCurrentSlice() - 1);
/*  34 */       if (win != null) this.imp.updateStatusbarValue();
/*  35 */       return;
/*     */     }
/*  37 */     this.swin = ((StackWindow)win);
/*  38 */     ImageStack stack = this.imp.getStack();
/*  39 */     this.slice = this.imp.getCurrentSlice();
/*  40 */     IJ.register(Animator.class);
/*     */ 
/*  42 */     if (arg.equals("options")) {
/*  43 */       doOptions();
/*  44 */       return;
/*     */     }
/*     */ 
/*  47 */     if (arg.equals("start")) {
/*  48 */       startAnimation();
/*  49 */       return;
/*     */     }
/*     */ 
/*  52 */     if (this.swin.getAnimate()) {
/*  53 */       stopAnimation();
/*     */     }
/*  55 */     if (arg.equals("stop")) {
/*  56 */       return;
/*     */     }
/*     */ 
/*  59 */     if (arg.equals("next")) {
/*  60 */       nextSlice();
/*  61 */       return;
/*     */     }
/*     */ 
/*  64 */     if (arg.equals("previous")) {
/*  65 */       previousSlice();
/*  66 */       return;
/*     */     }
/*     */ 
/*  69 */     if (arg.equals("set")) {
/*  70 */       setSlice();
/*  71 */       return;
/*     */     }
/*     */   }
/*     */ 
/*     */   void stopAnimation() {
/*  76 */     this.swin.setAnimate(false);
/*  77 */     IJ.wait(500 + (int)(1000.0D / animationRate));
/*  78 */     this.imp.unlock();
/*     */   }
/*     */ 
/*     */   void startAnimation() {
/*  82 */     int first = firstFrame; int last = lastFrame;
/*  83 */     if ((first < 1) || (first > this.nSlices) || (last < 1) || (last > this.nSlices)) {
/*  84 */       first = 1; last = this.nSlices;
/*  85 */     }if (this.swin.getAnimate()) {
/*  86 */       stopAnimation(); return;
/*  87 */     }this.imp.unlock();
/*  88 */     this.swin.setAnimate(true);
/*  89 */     long nextTime = System.currentTimeMillis();
/*  90 */     Thread.currentThread().setPriority(1);
/*  91 */     int sliceIncrement = 1;
/*  92 */     Calibration cal = this.imp.getCalibration();
/*  93 */     if (cal.fps != 0.0D)
/*  94 */       animationRate = cal.fps;
/*  95 */     if (animationRate < 0.1D)
/*  96 */       animationRate = 1.0D;
/*  97 */     int frames = this.imp.getNFrames();
/*  98 */     int slices = this.imp.getNSlices();
/*     */ 
/* 100 */     if ((this.imp.isDisplayedHyperStack()) && (frames > 1)) {
/* 101 */       int frame = this.imp.getFrame();
/* 102 */       first = 1;
/* 103 */       last = frames;
/* 104 */       while (this.swin.getAnimate()) {
/* 105 */         long time = System.currentTimeMillis();
/* 106 */         if (time < nextTime)
/* 107 */           IJ.wait((int)(nextTime - time));
/*     */         else
/* 109 */           Thread.yield();
/* 110 */         nextTime += ()(1000.0D / animationRate);
/* 111 */         frame += sliceIncrement;
/* 112 */         if (frame < first) {
/* 113 */           frame = first + 1;
/* 114 */           sliceIncrement = 1;
/*     */         }
/* 116 */         if (frame > last) {
/* 117 */           if (cal.loop) {
/* 118 */             frame = last - 1;
/* 119 */             sliceIncrement = -1;
/*     */           } else {
/* 121 */             frame = first;
/* 122 */             sliceIncrement = 1;
/*     */           }
/*     */         }
/* 125 */         this.imp.setPosition(this.imp.getChannel(), this.imp.getSlice(), frame);
/*     */       }
/* 127 */       return;
/*     */     }
/*     */ 
/* 130 */     if ((this.imp.isDisplayedHyperStack()) && (slices > 1)) {
/* 131 */       this.slice = this.imp.getSlice();
/* 132 */       first = 1;
/* 133 */       last = slices;
/* 134 */       while (this.swin.getAnimate()) {
/* 135 */         long time = System.currentTimeMillis();
/* 136 */         if (time < nextTime)
/* 137 */           IJ.wait((int)(nextTime - time));
/*     */         else
/* 139 */           Thread.yield();
/* 140 */         nextTime += ()(1000.0D / animationRate);
/* 141 */         this.slice += sliceIncrement;
/* 142 */         if (this.slice < first) {
/* 143 */           this.slice = (first + 1);
/* 144 */           sliceIncrement = 1;
/*     */         }
/* 146 */         if (this.slice > last) {
/* 147 */           if (cal.loop) {
/* 148 */             this.slice = (last - 1);
/* 149 */             sliceIncrement = -1;
/*     */           } else {
/* 151 */             this.slice = first;
/* 152 */             sliceIncrement = 1;
/*     */           }
/*     */         }
/* 155 */         this.imp.setPosition(this.imp.getChannel(), this.slice, this.imp.getFrame());
/*     */       }
/* 157 */       return;
/*     */     }
/*     */ 
/* 160 */     long startTime = System.currentTimeMillis();
/* 161 */     int count = 0;
/* 162 */     double fps = 0.0D;
/* 163 */     while (this.swin.getAnimate()) {
/* 164 */       long time = System.currentTimeMillis();
/* 165 */       count++;
/* 166 */       if (time > startTime + 1000L) {
/* 167 */         startTime = System.currentTimeMillis();
/* 168 */         fps = count;
/* 169 */         count = 0;
/*     */       }
/* 171 */       IJ.showStatus((int)(fps + 0.5D) + " fps");
/* 172 */       if (time < nextTime)
/* 173 */         IJ.wait((int)(nextTime - time));
/*     */       else
/* 175 */         Thread.yield();
/* 176 */       nextTime += ()(1000.0D / animationRate);
/* 177 */       this.slice += sliceIncrement;
/* 178 */       if (this.slice < first) {
/* 179 */         this.slice = (first + 1);
/* 180 */         sliceIncrement = 1;
/*     */       }
/* 182 */       if (this.slice > last) {
/* 183 */         if (cal.loop) {
/* 184 */           this.slice = (last - 1);
/* 185 */           sliceIncrement = -1;
/*     */         } else {
/* 187 */           this.slice = first;
/* 188 */           sliceIncrement = 1;
/*     */         }
/*     */       }
/* 191 */       this.swin.showSlice(this.slice);
/*     */     }
/*     */   }
/*     */ 
/*     */   void doOptions()
/*     */   {
/* 197 */     if ((firstFrame < 1) || (firstFrame > this.nSlices) || (lastFrame < 1) || (lastFrame > this.nSlices)) {
/* 198 */       firstFrame = 1; lastFrame = this.nSlices;
/* 199 */     }if (this.imp.isDisplayedHyperStack()) {
/* 200 */       int frames = this.imp.getNFrames();
/* 201 */       int slices = this.imp.getNSlices();
/* 202 */       firstFrame = 1;
/* 203 */       if (frames > 1)
/* 204 */         lastFrame = frames;
/* 205 */       else if (slices > 1)
/* 206 */         lastFrame = slices;
/*     */     }
/* 208 */     boolean start = !this.swin.getAnimate();
/* 209 */     Calibration cal = this.imp.getCalibration();
/* 210 */     if (cal.fps != 0.0D)
/* 211 */       animationRate = cal.fps;
/* 212 */     else if ((cal.frameInterval != 0.0D) && (cal.getTimeUnit().equals("sec")))
/* 213 */       animationRate = 1.0D / cal.frameInterval;
/* 214 */     int decimalPlaces = (int)animationRate == animationRate ? 0 : 3;
/* 215 */     GenericDialog gd = new GenericDialog("Animation Options");
/* 216 */     gd.addNumericField("Speed (0.1-1000 fps):", animationRate, decimalPlaces);
/* 217 */     if (!this.imp.isDisplayedHyperStack()) {
/* 218 */       gd.addNumericField("First Frame:", firstFrame, 0);
/* 219 */       gd.addNumericField("Last Frame:", lastFrame, 0);
/*     */     }
/* 221 */     gd.addCheckbox("Loop Back and Forth", cal.loop);
/* 222 */     gd.addCheckbox("Start Animation", start);
/* 223 */     gd.showDialog();
/* 224 */     if (gd.wasCanceled()) {
/* 225 */       if ((firstFrame == 1) && (lastFrame == this.nSlices)) {
/* 226 */         firstFrame = 0; lastFrame = 0;
/* 227 */       }return;
/*     */     }
/* 229 */     double speed = gd.getNextNumber();
/* 230 */     if (!this.imp.isDisplayedHyperStack()) {
/* 231 */       firstFrame = (int)gd.getNextNumber();
/* 232 */       lastFrame = (int)gd.getNextNumber();
/*     */     }
/* 234 */     if ((firstFrame == 1) && (lastFrame == this.nSlices)) {
/* 235 */       firstFrame = 0; lastFrame = 0;
/* 236 */     }cal.loop = gd.getNextBoolean();
/* 237 */     Calibration.setLoopBackAndForth(cal.loop);
/* 238 */     start = gd.getNextBoolean();
/* 239 */     if (speed > 1000.0D) speed = 1000.0D;
/*     */ 
/* 241 */     animationRate = speed;
/* 242 */     if (animationRate != 0.0D)
/* 243 */       cal.fps = animationRate;
/* 244 */     if ((start) && (!this.swin.getAnimate()))
/* 245 */       startAnimation();
/*     */   }
/*     */ 
/*     */   void nextSlice() {
/* 249 */     if (!this.imp.lock())
/* 250 */       return;
/* 251 */     boolean hyperstack = this.imp.isDisplayedHyperStack();
/* 252 */     int channels = this.imp.getNChannels();
/* 253 */     int slices = this.imp.getNSlices();
/* 254 */     int frames = this.imp.getNFrames();
/* 255 */     if ((hyperstack) && (channels > 1) && (((slices <= 1) && (frames <= 1)) || ((!IJ.controlKeyDown()) && (!IJ.spaceBarDown()) && (!IJ.altKeyDown())))) {
/* 256 */       int c = this.imp.getChannel() + 1;
/* 257 */       if (c > channels) c = channels;
/* 258 */       this.swin.setPosition(c, this.imp.getSlice(), this.imp.getFrame());
/* 259 */     } else if ((hyperstack) && (slices > 1) && ((frames <= 1) || (!IJ.altKeyDown()))) {
/* 260 */       int z = this.imp.getSlice() + 1;
/* 261 */       if (z > slices) z = slices;
/* 262 */       this.swin.setPosition(this.imp.getChannel(), z, this.imp.getFrame());
/* 263 */     } else if ((hyperstack) && (frames > 1)) {
/* 264 */       int t = this.imp.getFrame() + 1;
/* 265 */       if (t > frames) t = frames;
/* 266 */       this.swin.setPosition(this.imp.getChannel(), this.imp.getSlice(), t);
/*     */     } else {
/* 268 */       if (IJ.altKeyDown())
/* 269 */         this.slice += 10;
/*     */       else
/* 271 */         this.slice += 1;
/* 272 */       if (this.slice > this.nSlices)
/* 273 */         this.slice = this.nSlices;
/* 274 */       this.swin.showSlice(this.slice);
/*     */     }
/* 276 */     this.imp.updateStatusbarValue();
/* 277 */     this.imp.unlock();
/*     */   }
/*     */ 
/*     */   void previousSlice()
/*     */   {
/* 282 */     if (!this.imp.lock())
/* 283 */       return;
/* 284 */     boolean hyperstack = this.imp.isDisplayedHyperStack();
/* 285 */     int channels = this.imp.getNChannels();
/* 286 */     int slices = this.imp.getNSlices();
/* 287 */     int frames = this.imp.getNFrames();
/* 288 */     if ((hyperstack) && (channels > 1) && (((slices <= 1) && (frames <= 1)) || ((!IJ.controlKeyDown()) && (!IJ.spaceBarDown()) && (!IJ.altKeyDown())))) {
/* 289 */       int c = this.imp.getChannel() - 1;
/* 290 */       if (c < 1) c = 1;
/* 291 */       this.swin.setPosition(c, this.imp.getSlice(), this.imp.getFrame());
/* 292 */     } else if ((hyperstack) && (slices > 1) && ((frames <= 1) || (!IJ.altKeyDown()))) {
/* 293 */       int z = this.imp.getSlice() - 1;
/* 294 */       if (z < 1) z = 1;
/* 295 */       this.swin.setPosition(this.imp.getChannel(), z, this.imp.getFrame());
/* 296 */     } else if ((hyperstack) && (frames > 1)) {
/* 297 */       int t = this.imp.getFrame() - 1;
/* 298 */       if (t < 1) t = 1;
/* 299 */       this.swin.setPosition(this.imp.getChannel(), this.imp.getSlice(), t);
/*     */     } else {
/* 301 */       if (IJ.altKeyDown())
/* 302 */         this.slice -= 10;
/*     */       else
/* 304 */         this.slice -= 1;
/* 305 */       if (this.slice < 1)
/* 306 */         this.slice = 1;
/* 307 */       this.swin.showSlice(this.slice);
/*     */     }
/* 309 */     this.imp.updateStatusbarValue();
/* 310 */     this.imp.unlock();
/*     */   }
/*     */ 
/*     */   void setSlice() {
/* 314 */     GenericDialog gd = new GenericDialog("Set Slice");
/* 315 */     gd.addNumericField("Slice Number (1-" + this.nSlices + "):", this.slice, 0);
/* 316 */     gd.showDialog();
/* 317 */     if (!gd.wasCanceled()) {
/* 318 */       int n = (int)gd.getNextNumber();
/* 319 */       if (this.imp.isDisplayedHyperStack())
/* 320 */         this.imp.setPosition(n);
/*     */       else
/* 322 */         this.imp.setSlice(n);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static double getFrameRate()
/*     */   {
/* 328 */     return animationRate;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.Animator
 * JD-Core Version:    0.6.2
 */