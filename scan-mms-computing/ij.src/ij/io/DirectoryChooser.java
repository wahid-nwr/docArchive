/*     */ package ij.io;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.Macro;
/*     */ import ij.Prefs;
/*     */ import ij.plugin.frame.Recorder;
/*     */ import ij.util.Java2;
/*     */ import java.awt.EventQueue;
/*     */ import java.io.File;
/*     */ import javax.swing.JFileChooser;
/*     */ 
/*     */ public class DirectoryChooser
/*     */ {
/*     */   private String directory;
/*     */   private String title;
/*     */ 
/*     */   public DirectoryChooser(String title)
/*     */   {
/*  18 */     this.title = title;
/*  19 */     if (IJ.isMacOSX()) {
/*  20 */       getDirectoryUsingFileDialog(title);
/*     */     } else {
/*  22 */       String macroOptions = Macro.getOptions();
/*  23 */       if (macroOptions != null)
/*  24 */         this.directory = Macro.getValue(macroOptions, title, null);
/*  25 */       if (this.directory == null)
/*  26 */         if (EventQueue.isDispatchThread())
/*  27 */           getDirectoryUsingJFileChooserOnThisThread(title);
/*     */         else
/*  29 */           getDirectoryUsingJFileChooser(title);
/*     */     }
/*     */   }
/*     */ 
/*     */   void getDirectoryUsingJFileChooser(final String title)
/*     */   {
/*  36 */     Java2.setSystemLookAndFeel();
/*     */     try {
/*  38 */       EventQueue.invokeAndWait(new Runnable() {
/*     */         public void run() {
/*  40 */           JFileChooser chooser = new JFileChooser();
/*  41 */           chooser.setDialogTitle(title);
/*  42 */           chooser.setFileSelectionMode(1);
/*  43 */           String defaultDir = OpenDialog.getDefaultDirectory();
/*  44 */           if (defaultDir != null) {
/*  45 */             File f = new File(defaultDir);
/*  46 */             if (IJ.debugMode)
/*  47 */               IJ.log("DirectoryChooser,setSelectedFile: " + f);
/*  48 */             chooser.setSelectedFile(f);
/*     */           }
/*  50 */           chooser.setApproveButtonText("Select");
/*  51 */           if (chooser.showOpenDialog(null) == 0) {
/*  52 */             File file = chooser.getSelectedFile();
/*  53 */             DirectoryChooser.this.directory = file.getAbsolutePath();
/*  54 */             if (!DirectoryChooser.this.directory.endsWith(File.separator))
/*  55 */               DirectoryChooser.access$084(DirectoryChooser.this, File.separator);
/*  56 */             OpenDialog.setDefaultDirectory(DirectoryChooser.this.directory);
/*     */           }
/*     */         } } );
/*     */     }
/*     */     catch (Exception e) {
/*     */     }
/*     */   }
/*     */ 
/*     */   void getDirectoryUsingJFileChooserOnThisThread(String title) {
/*  65 */     Java2.setSystemLookAndFeel();
/*     */     try {
/*  67 */       JFileChooser chooser = new JFileChooser();
/*  68 */       chooser.setDialogTitle(title);
/*  69 */       chooser.setFileSelectionMode(1);
/*  70 */       String defaultDir = OpenDialog.getDefaultDirectory();
/*  71 */       if (defaultDir != null) {
/*  72 */         File f = new File(defaultDir);
/*  73 */         if (IJ.debugMode)
/*  74 */           IJ.log("DirectoryChooser,setSelectedFile: " + f);
/*  75 */         chooser.setSelectedFile(f);
/*     */       }
/*  77 */       chooser.setApproveButtonText("Select");
/*  78 */       if (chooser.showOpenDialog(null) == 0) {
/*  79 */         File file = chooser.getSelectedFile();
/*  80 */         this.directory = file.getAbsolutePath();
/*  81 */         if (!this.directory.endsWith(File.separator))
/*  82 */           this.directory += File.separator;
/*  83 */         OpenDialog.setDefaultDirectory(this.directory);
/*     */       }
/*     */     } catch (Exception e) {
/*     */     }
/*     */   }
/*     */ 
/*     */   void getDirectoryUsingFileDialog(String title) {
/*  90 */     boolean saveUseJFC = Prefs.useJFileChooser;
/*  91 */     Prefs.useJFileChooser = false;
/*  92 */     System.setProperty("apple.awt.fileDialogForDirectories", "true");
/*  93 */     String dir = null; String name = null;
/*  94 */     String defaultDir = OpenDialog.getDefaultDirectory();
/*  95 */     if (defaultDir != null) {
/*  96 */       File f = new File(defaultDir);
/*  97 */       dir = f.getParent();
/*  98 */       name = f.getName();
/*     */     }
/* 100 */     if (IJ.debugMode)
/* 101 */       IJ.log("DirectoryChooser: dir=\"" + dir + "\",  file=\"" + name + "\"");
/* 102 */     OpenDialog od = new OpenDialog(title, dir, name);
/* 103 */     if (od.getDirectory() == null)
/* 104 */       this.directory = null;
/*     */     else
/* 106 */       this.directory = (od.getDirectory() + od.getFileName() + "/");
/* 107 */     if (this.directory != null)
/* 108 */       OpenDialog.setDefaultDirectory(this.directory);
/* 109 */     System.setProperty("apple.awt.fileDialogForDirectories", "false");
/* 110 */     Prefs.useJFileChooser = saveUseJFC;
/*     */   }
/*     */ 
/*     */   public String getDirectory()
/*     */   {
/* 115 */     if (IJ.debugMode)
/* 116 */       IJ.log("DirectoryChooser.getDirectory: " + this.directory);
/* 117 */     if ((Recorder.record) && (!IJ.isMacOSX()))
/* 118 */       Recorder.recordPath(this.title, this.directory);
/* 119 */     return this.directory;
/*     */   }
/*     */ 
/*     */   public static void setDefaultDirectory(String dir)
/*     */   {
/* 124 */     if ((dir == null) || (new File(dir).isDirectory()))
/* 125 */       OpenDialog.setDefaultDirectory(dir);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.io.DirectoryChooser
 * JD-Core Version:    0.6.2
 */