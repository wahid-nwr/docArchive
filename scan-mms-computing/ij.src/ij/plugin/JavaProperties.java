/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.Menus;
/*     */ import ij.Prefs;
/*     */ import ij.io.OpenDialog;
/*     */ import ij.text.TextWindow;
/*     */ import java.applet.Applet;
/*     */ import java.awt.Dimension;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Properties;
/*     */ 
/*     */ public class JavaProperties
/*     */   implements PlugIn
/*     */ {
/*  12 */   StringBuffer sb = new StringBuffer();
/*     */ 
/*     */   public void run(String arg) {
/*  15 */     show("java.version");
/*  16 */     show("java.vendor");
/*  17 */     if (IJ.isMacintosh()) show("mrj.version");
/*  18 */     show("os.name");
/*  19 */     show("os.version");
/*  20 */     show("os.arch");
/*  21 */     show("file.separator");
/*  22 */     show("path.separator");
/*     */ 
/*  24 */     String s = System.getProperty("line.separator");
/*     */ 
/*  26 */     String str2 = "";
/*  27 */     char ch1 = s.charAt(0);
/*     */     String str1;
/*     */     String str1;
/*  28 */     if (ch1 == '\r')
/*  29 */       str1 = "<cr>";
/*     */     else
/*  31 */       str1 = "<lf>";
/*  32 */     if (s.length() == 2) {
/*  33 */       char ch2 = s.charAt(1);
/*  34 */       if (ch2 == '\r')
/*  35 */         str2 = "<cr>";
/*     */       else
/*  37 */         str2 = "<lf>";
/*     */     }
/*  39 */     this.sb.append("  line.separator: " + str1 + str2 + "\n");
/*     */ 
/*  41 */     Applet applet = IJ.getApplet();
/*  42 */     if (applet != null) {
/*  43 */       this.sb.append("\n");
/*  44 */       this.sb.append("  code base: " + applet.getCodeBase() + "\n");
/*  45 */       this.sb.append("  document base: " + applet.getDocumentBase() + "\n");
/*  46 */       this.sb.append("  sample images dir: " + Prefs.getImagesURL() + "\n");
/*  47 */       TextWindow tw = new TextWindow("Properties", new String(this.sb), 400, 400);
/*  48 */       return;
/*     */     }
/*  50 */     this.sb.append("\n");
/*  51 */     show("user.name");
/*  52 */     show("user.home");
/*  53 */     show("user.dir");
/*  54 */     show("user.country");
/*  55 */     show("file.encoding");
/*  56 */     show("java.home");
/*  57 */     show("java.compiler");
/*  58 */     show("java.class.path");
/*  59 */     show("java.ext.dirs");
/*  60 */     show("java.io.tmpdir");
/*     */ 
/*  62 */     this.sb.append("\n");
/*  63 */     String userDir = System.getProperty("user.dir");
/*  64 */     String userHome = System.getProperty("user.home");
/*  65 */     String osName = System.getProperty("os.name");
/*  66 */     this.sb.append("  IJ.getVersion: " + IJ.getVersion() + "\n");
/*  67 */     this.sb.append("  IJ.isJava2: " + IJ.isJava2() + "\n");
/*  68 */     this.sb.append("  IJ.isJava15: " + IJ.isJava15() + "\n");
/*  69 */     this.sb.append("  IJ.isJava16: " + IJ.isJava16() + "\n");
/*  70 */     this.sb.append("  IJ.isLinux: " + IJ.isLinux() + "\n");
/*  71 */     this.sb.append("  IJ.isMacintosh: " + IJ.isMacintosh() + "\n");
/*  72 */     this.sb.append("  IJ.isMacOSX: " + IJ.isMacOSX() + "\n");
/*  73 */     this.sb.append("  IJ.isWindows: " + IJ.isWindows() + "\n");
/*  74 */     this.sb.append("  IJ.isVista: " + IJ.isVista() + "\n");
/*  75 */     this.sb.append("  IJ.is64Bit: " + IJ.is64Bit() + "\n");
/*  76 */     this.sb.append("  Menus.getPlugInsPath: " + Menus.getPlugInsPath() + "\n");
/*  77 */     this.sb.append("  Menus.getMacrosPath: " + Menus.getMacrosPath() + "\n");
/*  78 */     this.sb.append("  Prefs.getHomeDir: " + Prefs.getHomeDir() + "\n");
/*  79 */     this.sb.append("  Prefs.getThreads: " + Prefs.getThreads() + cores());
/*  80 */     this.sb.append("  Prefs.open100Percent: " + Prefs.open100Percent + "\n");
/*  81 */     this.sb.append("  Prefs.blackBackground: " + Prefs.blackBackground + "\n");
/*  82 */     this.sb.append("  Prefs.useJFileChooser: " + Prefs.useJFileChooser + "\n");
/*  83 */     this.sb.append("  Prefs.weightedColor: " + Prefs.weightedColor + "\n");
/*  84 */     this.sb.append("  Prefs.blackCanvas: " + Prefs.blackCanvas + "\n");
/*  85 */     this.sb.append("  Prefs.pointAutoMeasure: " + Prefs.pointAutoMeasure + "\n");
/*  86 */     this.sb.append("  Prefs.pointAutoNextSlice: " + Prefs.pointAutoNextSlice + "\n");
/*  87 */     this.sb.append("  Prefs.requireControlKey: " + Prefs.requireControlKey + "\n");
/*  88 */     this.sb.append("  Prefs.useInvertingLut: " + Prefs.useInvertingLut + "\n");
/*  89 */     this.sb.append("  Prefs.antialiasedTools: " + Prefs.antialiasedTools + "\n");
/*  90 */     this.sb.append("  Prefs.useInvertingLut: " + Prefs.useInvertingLut + "\n");
/*  91 */     this.sb.append("  Prefs.intelByteOrder: " + Prefs.intelByteOrder + "\n");
/*  92 */     this.sb.append("  Prefs.doubleBuffer: " + Prefs.doubleBuffer + "\n");
/*  93 */     this.sb.append("  Prefs.noPointLabels: " + Prefs.noPointLabels + "\n");
/*  94 */     this.sb.append("  Prefs.disableUndo: " + Prefs.disableUndo + "\n");
/*  95 */     this.sb.append("  Prefs.runSocketListener: " + Prefs.runSocketListener + "\n");
/*  96 */     this.sb.append("  Prefs dir: " + Prefs.getPrefsDir() + "\n");
/*  97 */     this.sb.append("  Current dir: " + OpenDialog.getDefaultDirectory() + "\n");
/*  98 */     this.sb.append("  Sample images dir: " + Prefs.getImagesURL() + "\n");
/*  99 */     Dimension d = IJ.getScreenSize();
/* 100 */     this.sb.append("  Screen size: " + d.width + "x" + d.height + "\n");
/* 101 */     System.gc();
/* 102 */     this.sb.append("  Memory in use: " + IJ.freeMemory() + "\n");
/* 103 */     if (IJ.altKeyDown())
/* 104 */       doFullDump();
/* 105 */     if (IJ.getInstance() == null)
/* 106 */       IJ.log(new String(this.sb));
/*     */     else
/* 108 */       new TextWindow("Properties", new String(this.sb), 400, 500);
/*     */   }
/*     */ 
/*     */   String cores() {
/* 112 */     int cores = Runtime.getRuntime().availableProcessors();
/* 113 */     if (cores == 1) {
/* 114 */       return " (1 core)\n";
/*     */     }
/* 116 */     return " (" + cores + " cores)\n";
/*     */   }
/*     */ 
/*     */   void show(String property) {
/* 120 */     String p = System.getProperty(property);
/* 121 */     if (p != null)
/* 122 */       this.sb.append("  " + property + ": " + p + "\n");
/*     */   }
/*     */ 
/*     */   void doFullDump() {
/* 126 */     this.sb.append("\n");
/* 127 */     this.sb.append("All Properties:\n");
/* 128 */     Properties props = System.getProperties();
/* 129 */     for (Enumeration en = props.keys(); en.hasMoreElements(); ) {
/* 130 */       String key = (String)en.nextElement();
/* 131 */       this.sb.append("  " + key + ": " + (String)props.get(key) + "\n");
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.JavaProperties
 * JD-Core Version:    0.6.2
 */