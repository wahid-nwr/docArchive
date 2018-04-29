/*     */ package leadtools.imageprocessing;
/*     */ 
/*     */ import java.util.Vector;
/*     */ import leadtools.RasterException;
/*     */ import leadtools.RasterImage;
/*     */ import leadtools.RasterImageChangedEvent;
/*     */ import leadtools.RasterImageChangedFlags;
/*     */ import leadtools.RasterImageProgressEvent;
/*     */ import leadtools.RasterImageProgressListener;
/*     */ import leadtools.ltkrn;
/*     */ 
/*     */ public abstract class RasterCommand
/*     */   implements IRasterCommand
/*     */ {
/*     */   private static final int SUCCESS = 1;
/*     */   private static final int SUCCESS_ABORT = 2;
/*     */   private static final int ERROR_USER_ABORT = 0;
/*     */   private Vector<RasterImageProgressListener> _progressListeners;
/*     */ 
/*     */   protected RasterCommand()
/*     */   {
/*  23 */     this._progressListeners = new Vector();
/*     */   }
/*     */ 
/*     */   public boolean getUseCopyStatusCallback()
/*     */   {
/*  30 */     return false;
/*     */   }
/*     */ 
/*     */   public final boolean hasProgress()
/*     */   {
/*  35 */     return this._progressListeners.size() > 0;
/*     */   }
/*     */ 
/*     */   public void onProgress(RasterCommandProgressEvent event)
/*     */   {
/*     */   }
/*     */ 
/*     */   protected int runCommand(RasterImage image, long bitmapHandle, int[] flags)
/*     */   {
/*  45 */     flags[0] = RasterImageChangedFlags.NONE;
/*  46 */     return 0;
/*     */   }
/*     */ 
/*     */   public int run(RasterImage image)
/*     */   {
/*  51 */     Object callbackObj = null;
/*     */ 
/*  53 */     if (hasProgress())
/*     */     {
/*  55 */       callbackObj = ltkrn.setstatuscallback(this);
/*     */     }
/*     */ 
/*  66 */     int[] changedFlags = { RasterImageChangedFlags.NONE };
/*     */     try
/*     */     {
/*  69 */       int ret = runCommand(image, image.getCurrentBitmapHandle(), changedFlags);
/*     */ 
/*  71 */       if ((ret != 1) && (ret != 2) && (ret != 0)) {
/*  72 */         RasterException.checkErrorCode(ret);
/*     */       }
/*     */ 
/*  76 */       if (callbackObj != null)
/*     */       {
/*  82 */         ltkrn.setstatuscallback(callbackObj);
/*     */       }
/*     */ 
/*  85 */       image.updateCurrentBitmapHandle();
/*     */ 
/*  90 */       if (ltkrn.IsNewLinkImage(image.getCurrentBitmapHandle())) {
/*  91 */         changedFlags[0] |= RasterImageChangedFlags.LINK_IMAGE;
/*     */       }
/*  93 */       if (changedFlags[0] != RasterImageChangedFlags.NONE)
/*  94 */         image.onChanged(new RasterImageChangedEvent(image, changedFlags[0]));
/*     */     }
/*     */     finally
/*     */     {
/*  76 */       if (callbackObj != null)
/*     */       {
/*  82 */         ltkrn.setstatuscallback(callbackObj);
/*     */       }
/*     */ 
/*  85 */       image.updateCurrentBitmapHandle();
/*     */ 
/*  90 */       if (ltkrn.IsNewLinkImage(image.getCurrentBitmapHandle())) {
/*  91 */         changedFlags[0] |= RasterImageChangedFlags.LINK_IMAGE;
/*     */       }
/*  93 */       if (changedFlags[0] != RasterImageChangedFlags.NONE) {
/*  94 */         image.onChanged(new RasterImageChangedEvent(image, changedFlags[0]));
/*     */       }
/*     */     }
/*  97 */     return changedFlags[0];
/*     */   }
/*     */ 
/*     */   public void addProgressListener(RasterImageProgressListener listener)
/*     */   {
/* 102 */     if (this._progressListeners.contains(listener))
/* 103 */       return;
/* 104 */     this._progressListeners.addElement(listener);
/*     */   }
/*     */ 
/*     */   public void removeProgressListener(RasterImageProgressListener listener)
/*     */   {
/* 109 */     if (this._progressListeners.contains(listener))
/* 110 */       this._progressListeners.removeElement(listener);
/*     */   }
/*     */ 
/*     */   public int onProgress(int percent)
/*     */   {
/* 116 */     int ret = 1;
/*     */ 
/* 118 */     if (hasProgress())
/*     */     {
/* 121 */       Vector vtemp = (Vector)this._progressListeners.clone();
/* 122 */       for (int x = 0; x < vtemp.size(); x++)
/*     */       {
/* 124 */         RasterImageProgressListener target = null;
/* 125 */         target = (RasterImageProgressListener)vtemp.elementAt(x);
/* 126 */         target.RasterImageProgressAlert(new RasterImageProgressEvent(this, percent));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 135 */     return ret;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.imageprocessing.RasterCommand
 * JD-Core Version:    0.6.2
 */