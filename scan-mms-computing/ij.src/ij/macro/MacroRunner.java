/*     */ package ij.macro;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.Menus;
/*     */ import ij.WindowManager;
/*     */ import ij.gui.ImageCanvas;
/*     */ import ij.plugin.frame.Editor;
/*     */ import java.awt.PopupMenu;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileReader;
/*     */ 
/*     */ public class MacroRunner
/*     */   implements Runnable
/*     */ {
/*     */   private String macro;
/*     */   private Program pgm;
/*     */   private int address;
/*     */   private String name;
/*     */   private Thread thread;
/*     */   private String argument;
/*     */   private Editor editor;
/*     */ 
/*     */   public MacroRunner()
/*     */   {
/*     */   }
/*     */ 
/*     */   public MacroRunner(String macro)
/*     */   {
/*  28 */     this(macro, (Editor)null);
/*     */   }
/*     */ 
/*     */   public MacroRunner(String macro, Editor editor)
/*     */   {
/*  33 */     this.macro = macro;
/*  34 */     this.editor = editor;
/*  35 */     this.thread = new Thread(this, "Macro$");
/*  36 */     this.thread.setPriority(Math.max(this.thread.getPriority() - 2, 1));
/*  37 */     this.thread.start();
/*     */   }
/*     */ 
/*     */   public MacroRunner(String macro, String argument)
/*     */   {
/*  43 */     this.macro = macro;
/*  44 */     this.argument = argument;
/*  45 */     this.thread = new Thread(this, "Macro$");
/*  46 */     this.thread.setPriority(Math.max(this.thread.getPriority() - 2, 1));
/*  47 */     this.thread.start();
/*     */   }
/*     */ 
/*     */   public MacroRunner(File file)
/*     */   {
/*  52 */     int size = (int)file.length();
/*  53 */     if (size <= 0)
/*  54 */       return;
/*     */     try {
/*  56 */       StringBuffer sb = new StringBuffer(5000);
/*  57 */       BufferedReader r = new BufferedReader(new FileReader(file));
/*     */       while (true) {
/*  59 */         String s = r.readLine();
/*  60 */         if (s == null) {
/*     */           break;
/*     */         }
/*  63 */         sb.append(s + "\n");
/*     */       }
/*  65 */       r.close();
/*  66 */       this.macro = new String(sb);
/*     */     }
/*     */     catch (Exception e) {
/*  69 */       IJ.error(e.getMessage());
/*  70 */       return;
/*     */     }
/*  72 */     this.thread = new Thread(this, "Macro$");
/*  73 */     this.thread.setPriority(Math.max(this.thread.getPriority() - 2, 1));
/*  74 */     this.thread.start();
/*     */   }
/*     */ 
/*     */   public MacroRunner(Program pgm, int address, String name)
/*     */   {
/*  79 */     this(pgm, address, name, (String)null);
/*     */   }
/*     */ 
/*     */   public MacroRunner(Program pgm, int address, String name, String argument)
/*     */   {
/*  85 */     this.pgm = pgm;
/*  86 */     this.address = address;
/*  87 */     this.name = name;
/*  88 */     this.argument = argument;
/*  89 */     this.thread = new Thread(this, name + "_Macro$");
/*  90 */     this.thread.setPriority(Math.max(this.thread.getPriority() - 2, 1));
/*  91 */     this.thread.start();
/*     */   }
/*     */ 
/*     */   public MacroRunner(Program pgm, int address, String name, Editor editor)
/*     */   {
/*  96 */     this.pgm = pgm;
/*  97 */     this.address = address;
/*  98 */     this.name = name;
/*  99 */     this.editor = editor;
/* 100 */     this.thread = new Thread(this, name + "_Macro$");
/* 101 */     this.thread.setPriority(Math.max(this.thread.getPriority() - 2, 1));
/* 102 */     this.thread.start();
/*     */   }
/*     */ 
/*     */   public void runShortcut(Program pgm, int address, String name)
/*     */   {
/* 107 */     this.pgm = pgm;
/* 108 */     this.address = address;
/* 109 */     this.name = name;
/* 110 */     if (pgm.queueCommands) {
/* 111 */       run();
/*     */     } else {
/* 113 */       this.thread = new Thread(this, name + "_Macro$");
/* 114 */       this.thread.setPriority(Math.max(this.thread.getPriority() - 2, 1));
/* 115 */       this.thread.start();
/*     */     }
/*     */   }
/*     */ 
/*     */   public Thread getThread() {
/* 120 */     return this.thread;
/*     */   }
/*     */ 
/*     */   public void run() {
/* 124 */     Interpreter interp = new Interpreter();
/* 125 */     interp.argument = this.argument;
/* 126 */     if (this.editor != null)
/* 127 */       interp.setEditor(this.editor);
/*     */     try {
/* 129 */       if (this.pgm == null) {
/* 130 */         interp.run(this.macro);
/*     */       } else {
/* 132 */         if ("Popup Menu".equals(this.name)) {
/* 133 */           PopupMenu popup = Menus.getPopupMenu();
/* 134 */           if (popup != null) {
/* 135 */             ImagePlus imp = null;
/* 136 */             Object parent = popup.getParent();
/* 137 */             if ((parent instanceof ImageCanvas))
/* 138 */               imp = ((ImageCanvas)parent).getImage();
/* 139 */             if (imp != null)
/* 140 */               WindowManager.setTempCurrentImage(Thread.currentThread(), imp);
/*     */           }
/*     */         }
/* 143 */         interp.runMacro(this.pgm, this.address, this.name);
/*     */       }
/*     */     } catch (Throwable e) {
/* 146 */       interp.abortMacro();
/* 147 */       IJ.showStatus("");
/* 148 */       IJ.showProgress(1.0D);
/* 149 */       ImagePlus imp = WindowManager.getCurrentImage();
/* 150 */       if (imp != null) imp.unlock();
/* 151 */       String msg = e.getMessage();
/* 152 */       if (((e instanceof RuntimeException)) && (msg != null) && (e.getMessage().equals("Macro canceled")))
/* 153 */         return;
/* 154 */       IJ.handleException(e);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.macro.MacroRunner
 * JD-Core Version:    0.6.2
 */