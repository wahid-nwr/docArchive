/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImageJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.Prefs;
/*     */ import ij.Undo;
/*     */ import ij.WindowManager;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.gui.ImageWindow;
/*     */ import ij.gui.NewImage;
/*     */ import ij.io.FileSaver;
/*     */ import ij.io.Opener;
/*     */ import ij.macro.Interpreter;
/*     */ import ij.plugin.frame.PlugInFrame;
/*     */ import ij.plugin.frame.Recorder;
/*     */ import ij.text.TextWindow;
/*     */ import java.applet.Applet;
/*     */ import java.awt.Frame;
/*     */ import java.io.File;
/*     */ 
/*     */ public class Commands
/*     */   implements PlugIn
/*     */ {
/*     */   public void run(String cmd)
/*     */   {
/*  17 */     if (cmd.equals("new")) {
/*  18 */       new NewImage();
/*  19 */     } else if (cmd.equals("open")) {
/*  20 */       if ((Prefs.useJFileChooser) && (!IJ.macroRunning()))
/*  21 */         new Opener().openMultiple();
/*     */       else
/*  23 */         new Opener().open();
/*  24 */     } else if (cmd.equals("close")) {
/*  25 */       close();
/*  26 */     } else if (cmd.equals("close-all")) {
/*  27 */       closeAll();
/*  28 */     } else if (cmd.equals("save")) {
/*  29 */       save();
/*  30 */     } else if (cmd.equals("ij")) {
/*  31 */       ImageJ ij = IJ.getInstance();
/*  32 */       if (ij != null) ij.toFront(); 
/*     */     }
/*  33 */     else if (cmd.equals("tab")) {
/*  34 */       WindowManager.putBehind();
/*  35 */     } else if (cmd.equals("quit")) {
/*  36 */       ImageJ ij = IJ.getInstance();
/*  37 */       if (ij != null) ij.quit(); 
/*     */     }
/*  38 */     else if (cmd.equals("revert")) {
/*  39 */       revert();
/*  40 */     } else if (cmd.equals("undo")) {
/*  41 */       undo();
/*  42 */     } else if (cmd.equals("startup")) {
/*  43 */       openStartupMacros();
/*     */     }
/*     */   }
/*     */ 
/*  47 */   void revert() { ImagePlus imp = WindowManager.getCurrentImage();
/*  48 */     if (imp != null)
/*  49 */       imp.revert();
/*     */     else
/*  51 */       IJ.noImage(); }
/*     */ 
/*     */   void save()
/*     */   {
/*  55 */     ImagePlus imp = WindowManager.getCurrentImage();
/*  56 */     if (imp != null) {
/*  57 */       if (imp.getStackSize() > 1) {
/*  58 */         imp.setIgnoreFlush(true);
/*  59 */         new FileSaver(imp).save();
/*  60 */         imp.setIgnoreFlush(false);
/*     */       } else {
/*  62 */         new FileSaver(imp).save();
/*     */       }
/*     */     } else IJ.noImage(); 
/*     */   }
/*     */ 
/*     */   void undo()
/*     */   {
/*  68 */     ImagePlus imp = WindowManager.getCurrentImage();
/*  69 */     if (imp != null)
/*  70 */       Undo.undo();
/*     */     else
/*  72 */       IJ.noImage();
/*     */   }
/*     */ 
/*     */   void close() {
/*  76 */     ImagePlus imp = WindowManager.getCurrentImage();
/*  77 */     Frame frame = WindowManager.getFrontWindow();
/*  78 */     if ((frame == null) || ((Interpreter.isBatchMode()) && ((frame instanceof ImageWindow))))
/*  79 */       closeImage(imp);
/*  80 */     else if ((frame instanceof PlugInFrame))
/*  81 */       ((PlugInFrame)frame).close();
/*  82 */     else if ((frame instanceof TextWindow))
/*  83 */       ((TextWindow)frame).close();
/*     */     else
/*  85 */       closeImage(imp);
/*     */   }
/*     */ 
/*     */   void closeAll() {
/*  89 */     int[] list = WindowManager.getIDList();
/*  90 */     if (list != null) {
/*  91 */       int imagesWithChanges = 0;
/*  92 */       for (int i = 0; i < list.length; i++) {
/*  93 */         ImagePlus imp = WindowManager.getImage(list[i]);
/*  94 */         if ((imp != null) && (imp.changes)) imagesWithChanges++;
/*     */       }
/*  96 */       if ((imagesWithChanges > 0) && (!IJ.macroRunning())) {
/*  97 */         GenericDialog gd = new GenericDialog("Close All");
/*  98 */         String msg = null;
/*  99 */         String pronoun = null;
/* 100 */         if (imagesWithChanges == 1) {
/* 101 */           msg = "There is one image";
/* 102 */           pronoun = "it";
/*     */         } else {
/* 104 */           msg = "There are " + imagesWithChanges + " images";
/* 105 */           pronoun = "they";
/*     */         }
/* 107 */         gd.addMessage(msg + " with unsaved changes. If you\nclick \"OK\" " + pronoun + " will be closed without being saved.");
/*     */ 
/* 109 */         gd.showDialog();
/* 110 */         if (gd.wasCanceled()) return;
/*     */       }
/* 112 */       for (int i = 0; i < list.length; i++) {
/* 113 */         ImagePlus imp = WindowManager.getImage(list[i]);
/* 114 */         if (imp != null) {
/* 115 */           imp.changes = false;
/* 116 */           imp.close();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   void closeImage(ImagePlus imp)
/*     */   {
/* 128 */     if (imp == null) {
/* 129 */       IJ.noImage();
/* 130 */       return;
/*     */     }
/* 132 */     imp.close();
/* 133 */     if ((Recorder.record) && (!IJ.isMacro())) {
/* 134 */       if (Recorder.scriptMode())
/* 135 */         Recorder.recordCall("imp.close();");
/*     */       else
/* 137 */         Recorder.record("close");
/* 138 */       Recorder.setCommand(null);
/*     */     }
/*     */   }
/*     */ 
/*     */   void openStartupMacros()
/*     */   {
/* 144 */     Applet applet = IJ.getApplet();
/* 145 */     if (applet != null) {
/* 146 */       IJ.run("URL...", "url=http://imagej.nih.gov/ij/applet/StartupMacros.txt");
/*     */     } else {
/* 148 */       String path = IJ.getDirectory("macros") + "/StartupMacros.txt";
/* 149 */       File f = new File(path);
/* 150 */       if (!f.exists())
/* 151 */         IJ.error("\"StartupMacros.txt\" not found in ImageJ/macros/");
/*     */       else
/* 153 */         IJ.open(path);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.Commands
 * JD-Core Version:    0.6.2
 */