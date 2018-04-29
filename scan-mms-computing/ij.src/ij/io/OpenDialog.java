/*     */ package ij.io;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.Macro;
/*     */ import ij.Prefs;
/*     */ import ij.macro.Interpreter;
/*     */ import ij.plugin.frame.Recorder;
/*     */ import ij.util.Java2;
/*     */ import java.awt.EventQueue;
/*     */ import java.awt.FileDialog;
/*     */ import java.awt.Frame;
/*     */ import java.io.File;
/*     */ import javax.swing.JFileChooser;
/*     */ 
/*     */ public class OpenDialog
/*     */ {
/*     */   private String dir;
/*     */   private String name;
/*     */   private boolean recordPath;
/*     */   private static String defaultDirectory;
/*     */   private static Frame sharedFrame;
/*     */   private String title;
/*     */   private static String lastDir;
/*     */   private static String lastName;
/*     */ 
/*     */   public OpenDialog(String title, String path)
/*     */   {
/*  30 */     String macroOptions = Macro.getOptions();
/*  31 */     if ((macroOptions != null) && ((path == null) || (path.equals("")))) {
/*  32 */       path = Macro.getValue(macroOptions, title, path);
/*  33 */       if ((path == null) || (path.equals("")))
/*  34 */         path = Macro.getValue(macroOptions, "path", path);
/*  35 */       if (((path == null) || (path.equals(""))) && (title != null) && (title.equals("Open As String")))
/*  36 */         path = Macro.getValue(macroOptions, "OpenAsString", path);
/*  37 */       path = lookupPathVariable(path);
/*     */     }
/*  39 */     if ((path == null) || (path.equals(""))) {
/*  40 */       if (Prefs.useJFileChooser)
/*  41 */         jOpen(title, getDefaultDirectory(), null);
/*     */       else
/*  43 */         open(title, getDefaultDirectory(), null);
/*  44 */       if (this.name != null) defaultDirectory = this.dir;
/*  45 */       this.title = title;
/*  46 */       this.recordPath = true;
/*     */     } else {
/*  48 */       decodePath(path);
/*  49 */       this.recordPath = IJ.macroRunning();
/*     */     }
/*  51 */     IJ.register(OpenDialog.class);
/*     */   }
/*     */ 
/*     */   public OpenDialog(String title, String defaultDir, String defaultName)
/*     */   {
/*  57 */     String path = null;
/*  58 */     String macroOptions = Macro.getOptions();
/*  59 */     if (macroOptions != null)
/*  60 */       path = Macro.getValue(macroOptions, title, path);
/*  61 */     if (path != null) {
/*  62 */       decodePath(path);
/*     */     } else {
/*  64 */       if (Prefs.useJFileChooser)
/*  65 */         jOpen(title, defaultDir, defaultName);
/*     */       else
/*  67 */         open(title, defaultDir, defaultName);
/*  68 */       this.title = title;
/*  69 */       this.recordPath = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String lookupPathVariable(String path) {
/*  74 */     if ((path != null) && (path.indexOf(".") == -1) && (!new File(path).exists())) {
/*  75 */       if (path.startsWith("&")) path = path.substring(1);
/*  76 */       Interpreter interp = Interpreter.getInstance();
/*  77 */       String path2 = interp != null ? interp.getStringVariable(path) : null;
/*  78 */       if (path2 != null) path = path2;
/*     */     }
/*  80 */     return path;
/*     */   }
/*     */ 
/*     */   void jOpen(String title, String path, String fileName)
/*     */   {
/*  85 */     Java2.setSystemLookAndFeel();
/*  86 */     if (EventQueue.isDispatchThread())
/*  87 */       jOpenDispatchThread(title, path, fileName);
/*     */     else
/*  89 */       jOpenInvokeAndWait(title, path, fileName);
/*     */   }
/*     */ 
/*     */   void jOpenDispatchThread(String title, String path, String fileName)
/*     */   {
/*  95 */     JFileChooser fc = new JFileChooser();
/*  96 */     fc.setDialogTitle(title);
/*  97 */     File fdir = null;
/*  98 */     if (path != null)
/*  99 */       fdir = new File(path);
/* 100 */     if (fdir != null)
/* 101 */       fc.setCurrentDirectory(fdir);
/* 102 */     if (fileName != null)
/* 103 */       fc.setSelectedFile(new File(fileName));
/* 104 */     int returnVal = fc.showOpenDialog(IJ.getInstance());
/* 105 */     if (returnVal != 0) {
/* 106 */       Macro.abort(); return;
/* 107 */     }File file = fc.getSelectedFile();
/* 108 */     if (file == null) {
/* 109 */       Macro.abort(); return;
/* 110 */     }this.name = file.getName();
/* 111 */     this.dir = (fc.getCurrentDirectory().getPath() + File.separator);
/*     */   }
/*     */ 
/*     */   void jOpenInvokeAndWait(final String title, final String path, final String fileName)
/*     */   {
/*     */     try {
/* 117 */       EventQueue.invokeAndWait(new Runnable() {
/*     */         public void run() {
/* 119 */           JFileChooser fc = new JFileChooser();
/* 120 */           fc.setDialogTitle(title);
/* 121 */           File fdir = null;
/* 122 */           if (path != null)
/* 123 */             fdir = new File(path);
/* 124 */           if (fdir != null)
/* 125 */             fc.setCurrentDirectory(fdir);
/* 126 */           if (fileName != null)
/* 127 */             fc.setSelectedFile(new File(fileName));
/* 128 */           int returnVal = fc.showOpenDialog(IJ.getInstance());
/* 129 */           if (returnVal != 0) {
/* 130 */             Macro.abort(); return;
/* 131 */           }File file = fc.getSelectedFile();
/* 132 */           if (file == null) {
/* 133 */             Macro.abort(); return;
/* 134 */           }OpenDialog.this.name = file.getName();
/* 135 */           OpenDialog.this.dir = (fc.getCurrentDirectory().getPath() + File.separator);
/*     */         } } );
/*     */     }
/*     */     catch (Exception e) {
/*     */     }
/*     */   }
/*     */ 
/*     */   void open(String title, String path, String fileName) {
/* 143 */     Frame parent = IJ.getInstance();
/* 144 */     if (parent == null) {
/* 145 */       if (sharedFrame == null) sharedFrame = new Frame();
/* 146 */       parent = sharedFrame;
/*     */     }
/* 148 */     FileDialog fd = new FileDialog(parent, title);
/* 149 */     if (path != null)
/* 150 */       fd.setDirectory(path);
/* 151 */     if (fileName != null) {
/* 152 */       fd.setFile(fileName);
/*     */     }
/* 154 */     fd.show();
/* 155 */     this.name = fd.getFile();
/* 156 */     if (this.name == null) {
/* 157 */       if (IJ.isMacOSX())
/* 158 */         System.setProperty("apple.awt.fileDialogForDirectories", "false");
/* 159 */       Macro.abort();
/*     */     } else {
/* 161 */       this.dir = fd.getDirectory();
/*     */     }
/*     */   }
/*     */ 
/* 165 */   void decodePath(String path) { int i = path.lastIndexOf('/');
/* 166 */     if (i == -1)
/* 167 */       i = path.lastIndexOf('\\');
/* 168 */     if (i > 0) {
/* 169 */       this.dir = path.substring(0, i + 1);
/* 170 */       this.name = path.substring(i + 1);
/*     */     } else {
/* 172 */       this.dir = "";
/* 173 */       this.name = path;
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getDirectory()
/*     */   {
/* 179 */     lastDir = this.dir;
/* 180 */     return this.dir;
/*     */   }
/*     */ 
/*     */   public String getFileName()
/*     */   {
/* 185 */     if ((Recorder.record) && (this.recordPath))
/* 186 */       Recorder.recordPath(this.title, this.dir + this.name);
/* 187 */     lastName = this.name;
/* 188 */     return this.name;
/*     */   }
/*     */ 
/*     */   public static String getDefaultDirectory()
/*     */   {
/* 194 */     if (defaultDirectory == null)
/* 195 */       defaultDirectory = Prefs.getDefaultDirectory();
/* 196 */     return defaultDirectory;
/*     */   }
/*     */ 
/*     */   public static void setDefaultDirectory(String defaultDir)
/*     */   {
/* 201 */     defaultDirectory = defaultDir;
/* 202 */     if (!defaultDirectory.endsWith(File.separator))
/* 203 */       defaultDirectory += File.separator;
/*     */   }
/*     */ 
/*     */   public static String getLastDirectory()
/*     */   {
/* 210 */     return lastDir;
/*     */   }
/*     */ 
/*     */   public static void setLastDirectory(String dir)
/*     */   {
/* 215 */     lastDir = dir;
/*     */   }
/*     */ 
/*     */   public static String getLastName()
/*     */   {
/* 222 */     return lastName;
/*     */   }
/*     */ 
/*     */   public static void setLastName(String name)
/*     */   {
/* 227 */     lastName = name;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.io.OpenDialog
 * JD-Core Version:    0.6.2
 */