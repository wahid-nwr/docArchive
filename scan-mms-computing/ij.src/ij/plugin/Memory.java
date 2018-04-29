/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.Prefs;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.util.Tools;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ 
/*     */ public class Memory
/*     */   implements PlugIn
/*     */ {
/*     */   String s;
/*     */   int index1;
/*     */   int index2;
/*     */   File f;
/*     */   boolean fileMissing;
/*     */   boolean sixtyFourBit;
/*     */ 
/*     */   public void run(String arg)
/*     */   {
/*  17 */     changeMemoryAllocation();
/*     */   }
/*     */ 
/*     */   void changeMemoryAllocation()
/*     */   {
/*  23 */     IJ.maxMemory();
/*  24 */     int max = (int)(getMemorySetting() / 1048576L);
/*  25 */     boolean unableToSet = max == 0;
/*  26 */     if (max == 0) max = (int)(maxMemory() / 1048576L);
/*  27 */     String title = "Memory " + (IJ.is64Bit() ? "(64-bit)" : "(32-bit)");
/*  28 */     GenericDialog gd = new GenericDialog(title);
/*  29 */     gd.addNumericField("Maximum memory:", max, 0, 5, "MB");
/*  30 */     gd.addNumericField("Parallel threads for stacks:", Prefs.getThreads(), 0, 5, "");
/*  31 */     gd.setInsets(12, 0, 0);
/*  32 */     gd.addCheckbox("Keep multiple undo buffers", Prefs.keepUndoBuffers);
/*  33 */     gd.setInsets(12, 0, 0);
/*  34 */     gd.addCheckbox("Run garbage collector on status bar click", !Prefs.noClickToGC);
/*  35 */     gd.addHelp("http://imagej.nih.gov/ij/docs/menus/edit.html#memory");
/*  36 */     gd.showDialog();
/*  37 */     if (gd.wasCanceled()) return;
/*  38 */     int max2 = (int)gd.getNextNumber();
/*  39 */     Prefs.setThreads((int)gd.getNextNumber());
/*  40 */     Prefs.keepUndoBuffers = gd.getNextBoolean();
/*  41 */     Prefs.noClickToGC = !gd.getNextBoolean();
/*  42 */     if (gd.invalidNumber()) {
/*  43 */       IJ.showMessage("Memory", "The number entered was invalid.");
/*  44 */       return;
/*     */     }
/*  46 */     if ((unableToSet) && (max2 != max)) {
/*  47 */       showError(); return;
/*  48 */     }if ((max2 < 256) && (IJ.isMacOSX())) max2 = 256;
/*  49 */     if ((max2 < 32) && (IJ.isWindows())) max2 = 32;
/*  50 */     if (max2 == max) return;
/*  51 */     int limit = IJ.isWindows() ? 1600 : 1700;
/*  52 */     String OSXInfo = "";
/*  53 */     if (IJ.isMacOSX()) {
/*  54 */       OSXInfo = "\n \nOn Max OS X, use\n/Applications/Utilities/Java/Java Preferences\nto switch to a 64-bit version of Java. You may\nalso need to run \"ImageJ64\" instead of \"ImageJ\".";
/*     */     }
/*     */ 
/*  58 */     if ((max2 >= limit) && (!IJ.is64Bit())) {
/*  59 */       if (!IJ.showMessageWithCancel(title, "Note: setting the memory limit to a value\ngreater than " + limit + "MB on a 32-bit system\n" + "may cause ImageJ to fail to start. The title of\n" + "the Edit>Options>Memory & Threads dialog\n" + "box changes to \"Memory (64-bit)\" when ImageJ\n" + "is running on a 64-bit version of Java." + OSXInfo));
/*  67 */       return;
/*     */     }
/*     */     try {
/*  70 */       String s2 = this.s.substring(this.index2);
/*  71 */       if (s2.startsWith("g"))
/*  72 */         s2 = "m" + s2.substring(1);
/*  73 */       String s3 = this.s.substring(0, this.index1) + max2 + s2;
/*  74 */       FileOutputStream fos = new FileOutputStream(this.f);
/*  75 */       PrintWriter pw = new PrintWriter(fos);
/*  76 */       pw.print(s3);
/*  77 */       pw.close();
/*     */     } catch (IOException e) {
/*  79 */       String error = e.getMessage();
/*  80 */       if ((error == null) || (error.equals(""))) error = "" + e;
/*  81 */       String name = IJ.isMacOSX() ? "Info.plist" : "ImageJ.cfg";
/*  82 */       String msg = "Unable to update the file \"" + name + "\".\n" + " \n" + "\"" + error + "\"";
/*     */ 
/*  86 */       if (IJ.isVista())
/*  87 */         msg = msg + "\n \nOn Windows Vista, ImageJ must be installed in a directory that\nthe user can write to, such as \"Desktop\" or \"Documents\"";
/*  88 */       IJ.showMessage("Memory", msg);
/*  89 */       return;
/*     */     }
/*  91 */     String hint = "";
/*  92 */     if ((IJ.isWindows()) && (max2 > 640) && (max2 > max))
/*  93 */       hint = "\nDelete the \"ImageJ.cfg\" file, located in the ImageJ folder,\nif ImageJ fails to start.";
/*  94 */     IJ.showMessage("Memory", "The new " + max2 + "MB limit will take effect after ImageJ is restarted." + hint);
/*     */   }
/*     */ 
/*     */   public long getMemorySetting() {
/*  98 */     if (IJ.getApplet() != null) return 0L;
/*  99 */     long max = 0L;
/* 100 */     if (IJ.isMacOSX()) {
/* 101 */       if (IJ.is64Bit())
/* 102 */         max = getMemorySetting("ImageJ64.app/Contents/Info.plist");
/* 103 */       if (max == 0L)
/* 104 */         max = getMemorySetting("ImageJ.app/Contents/Info.plist");
/*     */     }
/*     */     else {
/* 107 */       max = getMemorySetting("ImageJ.cfg");
/* 108 */     }return max;
/*     */   }
/*     */ 
/*     */   void showError() {
/* 112 */     int max = (int)(maxMemory() / 1048576L);
/* 113 */     String msg = "ImageJ is unable to change the memory limit. For \nmore information, refer to the installation notes at\n \n    http://imagej.nih.gov/ij/docs/install/\n \n";
/*     */ 
/* 118 */     if (this.fileMissing) {
/* 119 */       if (IJ.isMacOSX())
/* 120 */         msg = msg + "The ImageJ application (ImageJ.app) was not found.\n \n";
/* 121 */       else if (IJ.isWindows())
/* 122 */         msg = msg + "ImageJ.cfg not found.\n \n";
/* 123 */       this.fileMissing = false;
/*     */     }
/* 125 */     if (max > 0)
/* 126 */       msg = msg + "Current limit: " + max + "MB";
/* 127 */     if (IJ.isVista())
/* 128 */       msg = msg + "\n \nOn Windows Vista, ImageJ must be installed in a directory that\nthe user can write to, such as \"Desktop\" or \"Documents\"";
/* 129 */     IJ.showMessage("Memory", msg);
/*     */   }
/*     */ 
/*     */   long getMemorySetting(String file) {
/* 133 */     String path = Prefs.getHomeDir() + File.separator + file;
/* 134 */     this.f = new File(path);
/* 135 */     if (!this.f.exists()) {
/* 136 */       this.fileMissing = true;
/* 137 */       return 0L;
/*     */     }
/* 139 */     long max = 0L;
/*     */     try {
/* 141 */       int size = (int)this.f.length();
/* 142 */       byte[] buffer = new byte[size];
/* 143 */       FileInputStream in = new FileInputStream(this.f);
/* 144 */       in.read(buffer, 0, size);
/* 145 */       this.s = new String(buffer, 0, size, "ISO8859_1");
/* 146 */       in.close();
/* 147 */       this.index1 = this.s.indexOf("-mx");
/* 148 */       if (this.index1 == -1) this.index1 = this.s.indexOf("-Xmx");
/* 149 */       if (this.index1 == -1) return 0L;
/* 150 */       if (this.s.charAt(this.index1 + 1) == 'X') this.index1 += 4; else this.index1 += 3;
/*     */ 
/* 152 */       while ((this.index2 < this.s.length() - 1) && (Character.isDigit(this.s.charAt(++this.index2))));
/* 153 */       String s2 = this.s.substring(this.index1, this.index2);
/* 154 */       max = ()Tools.parseDouble(s2, 0.0D) * 1024L * 1024L;
/* 155 */       if ((this.index2 < this.s.length()) && (this.s.charAt(this.index2) == 'g'))
/* 156 */         max *= 1024L;
/*     */     }
/*     */     catch (Exception e) {
/* 159 */       IJ.log("" + e);
/* 160 */       return 0L;
/*     */     }
/* 162 */     return max;
/*     */   }
/*     */ 
/*     */   public long maxMemory()
/*     */   {
/* 167 */     return Runtime.getRuntime().maxMemory();
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.Memory
 * JD-Core Version:    0.6.2
 */