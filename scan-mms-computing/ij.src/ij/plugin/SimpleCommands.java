/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.Undo;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.io.Opener;
/*     */ 
/*     */ public class SimpleCommands
/*     */   implements PlugIn
/*     */ {
/*     */   static String searchArg;
/*  13 */   private static String[] choices = { "Locked Image", "Clipboard", "Undo Buffer" };
/*  14 */   private static int choiceIndex = 0;
/*     */ 
/*     */   public void run(String arg) {
/*  17 */     if (arg.equals("search"))
/*  18 */       search();
/*  19 */     else if (arg.equals("import"))
/*  20 */       Opener.openResultsTable("");
/*  21 */     else if (arg.equals("rename"))
/*  22 */       rename();
/*  23 */     else if (arg.equals("reset"))
/*  24 */       reset();
/*  25 */     else if (arg.equals("about"))
/*  26 */       aboutPluginsHelp();
/*  27 */     else if (arg.equals("install"))
/*  28 */       installation();
/*  29 */     else if (arg.equals("remove"))
/*  30 */       removeStackLabels();
/*     */   }
/*     */ 
/*     */   void reset() {
/*  34 */     GenericDialog gd = new GenericDialog("");
/*  35 */     gd.addChoice("Reset:", choices, choices[choiceIndex]);
/*  36 */     gd.showDialog();
/*  37 */     if (gd.wasCanceled()) return;
/*  38 */     choiceIndex = gd.getNextChoiceIndex();
/*  39 */     switch (choiceIndex) { case 0:
/*  40 */       unlock(); break;
/*     */     case 1:
/*  41 */       resetClipboard(); break;
/*     */     case 2:
/*  42 */       resetUndo(); }
/*     */   }
/*     */ 
/*     */   void unlock()
/*     */   {
/*  47 */     ImagePlus imp = IJ.getImage();
/*  48 */     boolean wasUnlocked = imp.lockSilently();
/*  49 */     if (wasUnlocked) {
/*  50 */       IJ.showStatus("\"" + imp.getTitle() + "\" is not locked");
/*     */     } else {
/*  52 */       IJ.showStatus("\"" + imp.getTitle() + "\" is now unlocked");
/*  53 */       IJ.beep();
/*     */     }
/*  55 */     imp.unlock();
/*     */   }
/*     */ 
/*     */   void resetClipboard() {
/*  59 */     ImagePlus.resetClipboard();
/*  60 */     IJ.showStatus("Clipboard reset");
/*     */   }
/*     */ 
/*     */   void resetUndo() {
/*  64 */     Undo.setup(0, null);
/*  65 */     IJ.showStatus("Undo reset");
/*     */   }
/*     */ 
/*     */   void rename() {
/*  69 */     ImagePlus imp = IJ.getImage();
/*  70 */     GenericDialog gd = new GenericDialog("Rename");
/*  71 */     gd.addStringField("Title:", imp.getTitle(), 30);
/*  72 */     gd.showDialog();
/*  73 */     if (gd.wasCanceled()) {
/*  74 */       return;
/*     */     }
/*  76 */     imp.setTitle(gd.getNextString());
/*     */   }
/*     */ 
/*     */   void search() {
/*  80 */     searchArg = IJ.runMacroFile("ij.jar:Search", searchArg);
/*     */   }
/*     */ 
/*     */   void installation() {
/*  84 */     String url = "http://imagej.nih.gov/ij/docs/install/";
/*  85 */     if (IJ.isMacintosh())
/*  86 */       url = url + "osx.html";
/*  87 */     else if (IJ.isWindows())
/*  88 */       url = url + "windows.html";
/*  89 */     else if (IJ.isLinux())
/*  90 */       url = url + "linux.html";
/*  91 */     IJ.runPlugIn("ij.plugin.BrowserLauncher", url);
/*     */   }
/*     */ 
/*     */   void aboutPluginsHelp() {
/*  95 */     IJ.showMessage("\"About Plugins\" Submenu", "Plugins packaged as JAR files can add entries\nto this submenu. There is an example at\n \nhttp://imagej.nih.gov/ij/plugins/jar-demo.html");
/*     */   }
/*     */ 
/*     */   void removeStackLabels()
/*     */   {
/* 102 */     ImagePlus imp = IJ.getImage();
/* 103 */     int size = imp.getStackSize();
/* 104 */     if (size == 1) {
/* 105 */       IJ.error("Stack required");
/*     */     } else {
/* 107 */       ImageStack stack = imp.getStack();
/* 108 */       for (int i = 1; i <= size; i++)
/* 109 */         stack.setSliceLabel(null, i);
/* 110 */       imp.repaintWindow();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.SimpleCommands
 * JD-Core Version:    0.6.2
 */