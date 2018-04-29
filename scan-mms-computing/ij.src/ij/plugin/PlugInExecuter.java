/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImageJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.WindowManager;
/*     */ 
/*     */ class PlugInExecuter
/*     */   implements Runnable
/*     */ {
/*     */   private String plugin;
/*     */   private Thread thread;
/*     */ 
/*     */   PlugInExecuter(String plugin)
/*     */   {
/* 253 */     this.plugin = plugin;
/* 254 */     this.thread = new Thread(this, plugin);
/* 255 */     this.thread.setPriority(Math.max(this.thread.getPriority() - 2, 1));
/* 256 */     this.thread.start();
/*     */   }
/*     */ 
/*     */   public void run() {
/*     */     try {
/* 261 */       IJ.resetEscape();
/* 262 */       IJ.runPlugIn("ij.plugin.ClassChecker", "");
/* 263 */       ImageJ ij = IJ.getInstance();
/* 264 */       if (ij != null) ij.runUserPlugIn(this.plugin, this.plugin, "", true); 
/*     */     }
/* 266 */     catch (Throwable e) { IJ.showStatus("");
/* 267 */       IJ.showProgress(1.0D);
/* 268 */       ImagePlus imp = WindowManager.getCurrentImage();
/* 269 */       if (imp != null) imp.unlock();
/* 270 */       String msg = e.getMessage();
/* 271 */       if (((e instanceof RuntimeException)) && (msg != null) && (e.getMessage().equals("Macro canceled")))
/* 272 */         return;
/* 273 */       IJ.handleException(e);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.PlugInExecuter
 * JD-Core Version:    0.6.2
 */