/*     */ package ij.gui;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImageJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import java.awt.Scrollbar;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.AdjustmentEvent;
/*     */ import java.awt.event.AdjustmentListener;
/*     */ import java.awt.event.MouseWheelEvent;
/*     */ import java.awt.event.MouseWheelListener;
/*     */ 
/*     */ public class StackWindow extends ImageWindow
/*     */   implements Runnable, AdjustmentListener, ActionListener, MouseWheelListener
/*     */ {
/*     */   protected Scrollbar sliceSelector;
/*     */   protected ScrollbarWithLabel cSelector;
/*     */   protected ScrollbarWithLabel zSelector;
/*     */   protected ScrollbarWithLabel tSelector;
/*     */   protected Thread thread;
/*     */   protected volatile boolean done;
/*     */   protected volatile int slice;
/*     */   private ScrollbarWithLabel animationSelector;
/*     */   boolean hyperStack;
/*  18 */   int nChannels = 1; int nSlices = 1; int nFrames = 1;
/*  19 */   int c = 1; int z = 1; int t = 1;
/*     */ 
/*     */   public StackWindow(ImagePlus imp)
/*     */   {
/*  23 */     this(imp, null);
/*     */   }
/*     */ 
/*     */   public StackWindow(ImagePlus imp, ImageCanvas ic) {
/*  27 */     super(imp, ic);
/*  28 */     addScrollbars(imp);
/*  29 */     addMouseWheelListener(this);
/*  30 */     if ((this.sliceSelector == null) && (getClass().getName().indexOf("Image5D") != -1)) {
/*  31 */       this.sliceSelector = new Scrollbar();
/*     */     }
/*  33 */     pack();
/*  34 */     ic = imp.getCanvas();
/*  35 */     if (ic != null) ic.setMaxBounds();
/*  36 */     show();
/*  37 */     int previousSlice = imp.getCurrentSlice();
/*  38 */     if ((previousSlice > 1) && (previousSlice <= imp.getStackSize()))
/*  39 */       imp.setSlice(previousSlice);
/*     */     else
/*  41 */       imp.setSlice(1);
/*  42 */     this.thread = new Thread(this, "zSelector");
/*  43 */     this.thread.start();
/*     */   }
/*     */ 
/*     */   void addScrollbars(ImagePlus imp) {
/*  47 */     ImageStack s = imp.getStack();
/*  48 */     int stackSize = s.getSize();
/*  49 */     this.nSlices = stackSize;
/*  50 */     this.hyperStack = imp.getOpenAsHyperStack();
/*  51 */     imp.setOpenAsHyperStack(false);
/*  52 */     int[] dim = imp.getDimensions();
/*  53 */     int nDimensions = 2 + (dim[2] > 1 ? 1 : 0) + (dim[3] > 1 ? 1 : 0) + (dim[4] > 1 ? 1 : 0);
/*  54 */     if ((nDimensions <= 3) && (dim[2] != this.nSlices)) this.hyperStack = false;
/*  55 */     if (this.hyperStack) {
/*  56 */       this.nChannels = dim[2];
/*  57 */       this.nSlices = dim[3];
/*  58 */       this.nFrames = dim[4];
/*     */     }
/*     */ 
/*  61 */     if (this.nSlices == stackSize) this.hyperStack = false;
/*  62 */     if (this.nChannels * this.nSlices * this.nFrames != stackSize) this.hyperStack = false;
/*  63 */     if ((this.cSelector != null) || (this.zSelector != null) || (this.tSelector != null))
/*  64 */       removeScrollbars();
/*  65 */     ImageJ ij = IJ.getInstance();
/*  66 */     if (this.nChannels > 1) {
/*  67 */       this.cSelector = new ScrollbarWithLabel(this, 1, 1, 1, this.nChannels + 1, 'c');
/*  68 */       add(this.cSelector);
/*  69 */       if (ij != null) this.cSelector.addKeyListener(ij);
/*  70 */       this.cSelector.addAdjustmentListener(this);
/*  71 */       this.cSelector.setFocusable(false);
/*  72 */       this.cSelector.setUnitIncrement(1);
/*  73 */       this.cSelector.setBlockIncrement(1);
/*     */     }
/*  75 */     if (this.nSlices > 1) {
/*  76 */       char label = (this.nChannels > 1) || (this.nFrames > 1) ? 'z' : 't';
/*  77 */       if ((stackSize == dim[2]) && (imp.isComposite())) label = 'c';
/*  78 */       this.zSelector = new ScrollbarWithLabel(this, 1, 1, 1, this.nSlices + 1, label);
/*  79 */       if (label == 't') this.animationSelector = this.zSelector;
/*  80 */       add(this.zSelector);
/*  81 */       if (ij != null) this.zSelector.addKeyListener(ij);
/*  82 */       this.zSelector.addAdjustmentListener(this);
/*  83 */       this.zSelector.setFocusable(false);
/*  84 */       int blockIncrement = this.nSlices / 10;
/*  85 */       if (blockIncrement < 1) blockIncrement = 1;
/*  86 */       this.zSelector.setUnitIncrement(1);
/*  87 */       this.zSelector.setBlockIncrement(blockIncrement);
/*  88 */       this.sliceSelector = this.zSelector.bar;
/*     */     }
/*  90 */     if (this.nFrames > 1) {
/*  91 */       this.animationSelector = (this.tSelector = new ScrollbarWithLabel(this, 1, 1, 1, this.nFrames + 1, 't'));
/*  92 */       add(this.tSelector);
/*  93 */       if (ij != null) this.tSelector.addKeyListener(ij);
/*  94 */       this.tSelector.addAdjustmentListener(this);
/*  95 */       this.tSelector.setFocusable(false);
/*  96 */       int blockIncrement = this.nFrames / 10;
/*  97 */       if (blockIncrement < 1) blockIncrement = 1;
/*  98 */       this.tSelector.setUnitIncrement(1);
/*  99 */       this.tSelector.setBlockIncrement(blockIncrement);
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void adjustmentValueChanged(AdjustmentEvent e) {
/* 104 */     if ((!this.running2) || (this.imp.isHyperStack())) {
/* 105 */       if (e.getSource() == this.cSelector) { this.c = this.cSelector.getValue();
/* 107 */         if ((this.c != this.imp.getChannel()) || (e.getAdjustmentType() != 5));
/* 108 */       } else if (e.getSource() == this.zSelector) {
/* 109 */         this.z = this.zSelector.getValue();
/* 110 */         int slice = this.hyperStack ? this.imp.getSlice() : this.imp.getCurrentSlice();
/* 111 */         if ((this.z == slice) && (e.getAdjustmentType() == 5)) return; 
/*     */       }
/* 112 */       else if (e.getSource() == this.tSelector) {
/* 113 */         this.t = this.tSelector.getValue();
/* 114 */         if ((this.t == this.imp.getFrame()) && (e.getAdjustmentType() == 5)) return;
/*     */       }
/* 116 */       updatePosition();
/* 117 */       notify();
/*     */     }
/*     */   }
/*     */ 
/*     */   void updatePosition() {
/* 122 */     this.slice = ((this.t - 1) * this.nChannels * this.nSlices + (this.z - 1) * this.nChannels + this.c);
/* 123 */     this.imp.updatePosition(this.c, this.z, this.t);
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent e) {
/*     */   }
/*     */ 
/*     */   public void mouseWheelMoved(MouseWheelEvent event) {
/* 130 */     synchronized (this) {
/* 131 */       int rotation = event.getWheelRotation();
/* 132 */       if (this.hyperStack) {
/* 133 */         if (rotation > 0)
/* 134 */           IJ.run(this.imp, "Next Slice [>]", "");
/* 135 */         else if (rotation < 0)
/* 136 */           IJ.run(this.imp, "Previous Slice [<]", "");
/*     */       } else {
/* 138 */         int slice = this.imp.getCurrentSlice() + rotation;
/* 139 */         if (slice < 1)
/* 140 */           slice = 1;
/* 141 */         else if (slice > this.imp.getStack().getSize())
/* 142 */           slice = this.imp.getStack().getSize();
/* 143 */         this.imp.setSlice(slice);
/* 144 */         this.imp.updateStatusbarValue();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean close() {
/* 150 */     if (!super.close())
/* 151 */       return false;
/* 152 */     synchronized (this) {
/* 153 */       this.done = true;
/* 154 */       notify();
/*     */     }
/* 156 */     return true;
/*     */   }
/*     */ 
/*     */   public void showSlice(int index)
/*     */   {
/* 161 */     if ((this.imp != null) && (index >= 1) && (index <= this.imp.getStackSize()))
/* 162 */       this.imp.setSlice(index);
/*     */   }
/*     */ 
/*     */   public void updateSliceSelector()
/*     */   {
/* 167 */     if ((this.hyperStack) || (this.zSelector == null)) return;
/* 168 */     int stackSize = this.imp.getStackSize();
/* 169 */     int max = this.zSelector.getMaximum();
/* 170 */     if (max != stackSize + 1)
/* 171 */       this.zSelector.setMaximum(stackSize + 1);
/* 172 */     this.zSelector.setValue(this.imp.getCurrentSlice());
/*     */   }
/*     */ 
/*     */   public void run() {
/* 176 */     while (!this.done) {
/* 177 */       synchronized (this) {
/*     */         try { wait(); } catch (InterruptedException e) {
/*     */         }
/*     */       }
/* 181 */       if (this.done) return;
/* 182 */       if (this.slice > 0) {
/* 183 */         int s = this.slice;
/* 184 */         this.slice = 0;
/* 185 */         if (s != this.imp.getCurrentSlice())
/* 186 */           this.imp.setSlice(s);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public String createSubtitle() {
/* 192 */     String subtitle = super.createSubtitle();
/* 193 */     if (!this.hyperStack) return subtitle;
/* 194 */     String s = "";
/* 195 */     int[] dim = this.imp.getDimensions();
/* 196 */     int channels = dim[2]; int slices = dim[3]; int frames = dim[4];
/* 197 */     if (channels > 1) {
/* 198 */       s = s + "c:" + this.imp.getChannel() + "/" + channels;
/* 199 */       if ((slices > 1) || (frames > 1)) s = s + " ";
/*     */     }
/* 201 */     if (slices > 1) {
/* 202 */       s = s + "z:" + this.imp.getSlice() + "/" + slices;
/* 203 */       if (frames > 1) s = s + " ";
/*     */     }
/* 205 */     if (frames > 1)
/* 206 */       s = s + "t:" + this.imp.getFrame() + "/" + frames;
/* 207 */     if (this.running2) return s;
/* 208 */     int index = subtitle.indexOf(";");
/* 209 */     if (index != -1) {
/* 210 */       int index2 = subtitle.indexOf("(");
/* 211 */       if ((index2 >= 0) && (index2 < index) && (subtitle.length() > index2 + 4) && (!subtitle.substring(index2 + 1, index2 + 4).equals("ch:"))) {
/* 212 */         index = index2;
/* 213 */         s = s + " ";
/*     */       }
/* 215 */       subtitle = subtitle.substring(index, subtitle.length());
/*     */     } else {
/* 217 */       subtitle = "";
/* 218 */     }return s + subtitle;
/*     */   }
/*     */ 
/*     */   public boolean isHyperStack() {
/* 222 */     return this.hyperStack;
/*     */   }
/*     */ 
/*     */   public void setPosition(int channel, int slice, int frame) {
/* 226 */     if ((this.cSelector != null) && (channel != this.c)) {
/* 227 */       this.c = channel;
/* 228 */       this.cSelector.setValue(channel);
/*     */     }
/* 230 */     if ((this.zSelector != null) && (slice != this.z)) {
/* 231 */       this.z = slice;
/* 232 */       this.zSelector.setValue(slice);
/*     */     }
/* 234 */     if ((this.tSelector != null) && (frame != this.t)) {
/* 235 */       this.t = frame;
/* 236 */       this.tSelector.setValue(frame);
/*     */     }
/* 238 */     updatePosition();
/* 239 */     if (this.slice > 0) {
/* 240 */       int s = this.slice;
/* 241 */       this.slice = 0;
/* 242 */       if (s != this.imp.getCurrentSlice())
/* 243 */         this.imp.setSlice(s);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean validDimensions() {
/* 248 */     int c = this.imp.getNChannels();
/* 249 */     int z = this.imp.getNSlices();
/* 250 */     int t = this.imp.getNFrames();
/* 251 */     if ((c != this.nChannels) || (z != this.nSlices) || (t != this.nFrames) || (c * z * t != this.imp.getStackSize())) {
/* 252 */       return false;
/*     */     }
/* 254 */     return true;
/*     */   }
/*     */ 
/*     */   public void setAnimate(boolean b) {
/* 258 */     if ((this.running2 != b) && (this.animationSelector != null))
/* 259 */       this.animationSelector.updatePlayPauseIcon();
/* 260 */     this.running2 = b;
/*     */   }
/*     */ 
/*     */   public boolean getAnimate() {
/* 264 */     return this.running2;
/*     */   }
/*     */ 
/*     */   public int getNScrollbars() {
/* 268 */     int n = 0;
/* 269 */     if (this.cSelector != null) n++;
/* 270 */     if (this.zSelector != null) n++;
/* 271 */     if (this.tSelector != null) n++;
/* 272 */     return n;
/*     */   }
/*     */ 
/*     */   void removeScrollbars() {
/* 276 */     if (this.cSelector != null) {
/* 277 */       remove(this.cSelector);
/* 278 */       this.cSelector.removeAdjustmentListener(this);
/* 279 */       this.cSelector = null;
/*     */     }
/* 281 */     if (this.zSelector != null) {
/* 282 */       remove(this.zSelector);
/* 283 */       this.zSelector.removeAdjustmentListener(this);
/* 284 */       this.zSelector = null;
/*     */     }
/* 286 */     if (this.tSelector != null) {
/* 287 */       remove(this.tSelector);
/* 288 */       this.tSelector.removeAdjustmentListener(this);
/* 289 */       this.tSelector = null;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.gui.StackWindow
 * JD-Core Version:    0.6.2
 */