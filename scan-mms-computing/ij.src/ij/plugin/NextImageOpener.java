/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.gui.ImageWindow;
/*     */ import ij.gui.YesNoCancelDialog;
/*     */ import ij.io.FileInfo;
/*     */ import ij.io.FileSaver;
/*     */ import ij.io.Opener;
/*     */ import ij.util.StringSorter;
/*     */ import java.io.File;
/*     */ 
/*     */ public class NextImageOpener
/*     */   implements PlugIn
/*     */ {
/*  20 */   boolean forward = true;
/*  21 */   boolean closeCurrent = true;
/*     */   ImagePlus imp0;
/*     */ 
/*     */   public void run(String arg)
/*     */   {
/*  26 */     if ((arg.equals("backward")) || (IJ.altKeyDown())) this.forward = false;
/*  27 */     if (arg.equals("backwardsc")) {
/*  28 */       this.forward = false;
/*  29 */       this.closeCurrent = false;
/*     */     }
/*  31 */     if (arg.equals("forwardsc")) {
/*  32 */       this.forward = true;
/*  33 */       this.closeCurrent = false;
/*     */     }
/*     */ 
/*  37 */     this.imp0 = IJ.getImage();
/*     */ 
/*  39 */     String currentPath = getDirectory(this.imp0);
/*  40 */     if (IJ.debugMode) IJ.log("OpenNext.currentPath:" + currentPath);
/*  41 */     if (currentPath == null) {
/*  42 */       IJ.error("Next Image", "Directory information for \"" + this.imp0.getTitle() + "\" not found.");
/*  43 */       return;
/*     */     }
/*     */ 
/*  47 */     String nextPath = getNext(currentPath, getName(this.imp0), this.forward);
/*     */ 
/*  49 */     if (IJ.debugMode) IJ.log("OpenNext.nextPath:" + nextPath);
/*     */ 
/*  51 */     if (nextPath != null) {
/*  52 */       String rtn = open(nextPath);
/*  53 */       if (rtn == null)
/*  54 */         open(getNext(currentPath, new File(nextPath).getName(), this.forward));
/*     */     }
/*     */   }
/*     */ 
/*     */   String getDirectory(ImagePlus imp) {
/*  59 */     FileInfo fi = imp.getOriginalFileInfo();
/*  60 */     if (fi == null) return null;
/*  61 */     String dir = fi.openNextDir;
/*  62 */     if (dir == null) dir = fi.directory;
/*  63 */     return dir;
/*     */   }
/*     */ 
/*     */   String getName(ImagePlus imp) {
/*  67 */     String name = imp.getTitle();
/*  68 */     FileInfo fi = imp.getOriginalFileInfo();
/*  69 */     if (fi != null) {
/*  70 */       if (fi.openNextName != null)
/*  71 */         name = fi.openNextName;
/*  72 */       else if (fi.fileName != null)
/*  73 */         name = fi.fileName;
/*     */     }
/*  75 */     return name;
/*     */   }
/*     */ 
/*     */   String open(String nextPath) {
/*  79 */     ImagePlus imp2 = IJ.openImage(nextPath);
/*  80 */     if (imp2 == null) return null;
/*  81 */     String newTitle = imp2.getTitle();
/*  82 */     if (this.imp0.changes)
/*     */     {
/*  84 */       String name = this.imp0.getTitle();
/*     */       String msg;
/*     */       String msg;
/*  85 */       if (name.length() > 22)
/*  86 */         msg = "Save changes to\n\"" + name + "\"?";
/*     */       else
/*  88 */         msg = "Save changes to \"" + name + "\"?";
/*  89 */       YesNoCancelDialog d = new YesNoCancelDialog(this.imp0.getWindow(), "ImageJ", msg);
/*  90 */       if (d.cancelPressed())
/*  91 */         return "Canceled";
/*  92 */       if (d.yesPressed()) {
/*  93 */         FileSaver fs = new FileSaver(this.imp0);
/*  94 */         if (!fs.save())
/*  95 */           return "Canceled";
/*     */       }
/*  97 */       this.imp0.changes = false;
/*     */     }
/*  99 */     if ((imp2.isComposite()) || (imp2.isHyperStack())) {
/* 100 */       imp2.show();
/* 101 */       this.imp0.close();
/* 102 */       this.imp0 = imp2;
/*     */     } else {
/* 104 */       this.imp0.setStack(newTitle, imp2.getStack());
/* 105 */       this.imp0.setCalibration(imp2.getCalibration());
/* 106 */       this.imp0.setFileInfo(imp2.getOriginalFileInfo());
/* 107 */       this.imp0.setProperty("Info", imp2.getProperty("Info"));
/* 108 */       ImageWindow win = this.imp0.getWindow();
/* 109 */       if (win != null) win.repaint();
/*     */     }
/* 111 */     return "ok";
/*     */   }
/*     */ 
/*     */   String getNext(String path, String imageName, boolean forward)
/*     */   {
/* 116 */     File dir = new File(path);
/* 117 */     if (!dir.isDirectory()) return null;
/* 118 */     String[] names = dir.list();
/* 119 */     StringSorter.sort(names);
/* 120 */     int thisfile = -1;
/* 121 */     for (int i = 0; i < names.length; i++) {
/* 122 */       if (names[i].equals(imageName)) {
/* 123 */         thisfile = i;
/* 124 */         break;
/*     */       }
/*     */     }
/* 127 */     if (IJ.debugMode) IJ.log("OpenNext.thisfile:" + thisfile);
/* 128 */     if (thisfile == -1) return null;
/*     */ 
/* 131 */     int candidate = thisfile + 1;
/* 132 */     if (!forward) candidate = thisfile - 1;
/* 133 */     if (candidate < 0) candidate = names.length - 1;
/* 134 */     if (candidate == names.length) candidate = 0;
/*     */ 
/* 136 */     while (candidate != thisfile) {
/* 137 */       String nextPath = path + names[candidate];
/* 138 */       if (IJ.debugMode) IJ.log("OpenNext: " + candidate + "  " + names[candidate]);
/* 139 */       File nextFile = new File(nextPath);
/* 140 */       boolean canOpen = true;
/* 141 */       if ((names[candidate].startsWith(".")) || (nextFile.isDirectory()))
/* 142 */         canOpen = false;
/* 143 */       if (canOpen) {
/* 144 */         Opener o = new Opener();
/* 145 */         int type = o.getFileType(nextPath);
/* 146 */         if ((type == 0) || (type == 10) || (type == 11) || (type == 12))
/*     */         {
/* 148 */           canOpen = false;
/*     */         }
/*     */       }
/* 150 */       if (canOpen) {
/* 151 */         return nextPath;
/*     */       }
/* 153 */       if (forward)
/* 154 */         candidate += 1;
/*     */       else
/* 156 */         candidate -= 1;
/* 157 */       if (candidate < 0) candidate = names.length - 1;
/* 158 */       if (candidate == names.length) candidate = 0;
/*     */ 
/*     */     }
/*     */ 
/* 162 */     if (IJ.debugMode) IJ.log("OpenNext: Search failed");
/* 163 */     return null;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.NextImageOpener
 * JD-Core Version:    0.6.2
 */