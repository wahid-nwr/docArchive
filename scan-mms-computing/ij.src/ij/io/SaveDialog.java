/*     */ package ij.io;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImageJ;
/*     */ import ij.Macro;
/*     */ import ij.Prefs;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.macro.Interpreter;
/*     */ import ij.plugin.frame.Recorder;
/*     */ import ij.util.Java2;
/*     */ import java.awt.EventQueue;
/*     */ import java.awt.FileDialog;
/*     */ import java.awt.Font;
/*     */ import java.awt.Frame;
/*     */ import java.io.File;
/*     */ import javax.swing.JFileChooser;
/*     */ import javax.swing.JOptionPane;
/*     */ 
/*     */ public class SaveDialog
/*     */ {
/*     */   private String dir;
/*     */   private String name;
/*     */   private String title;
/*     */   private String ext;
/*     */ 
/*     */   public SaveDialog(String title, String defaultName, String extension)
/*     */   {
/*  26 */     this.title = title;
/*  27 */     this.ext = extension;
/*  28 */     if (isMacro())
/*  29 */       return;
/*  30 */     String defaultDir = OpenDialog.getDefaultDirectory();
/*  31 */     defaultName = setExtension(defaultName, extension);
/*  32 */     if (Prefs.useJFileChooser)
/*  33 */       jSave(title, defaultDir, defaultName);
/*     */     else
/*  35 */       save(title, defaultDir, defaultName);
/*  36 */     if ((this.name != null) && (this.dir != null))
/*  37 */       OpenDialog.setDefaultDirectory(this.dir);
/*  38 */     IJ.showStatus(title + ": " + this.dir + this.name);
/*     */   }
/*     */ 
/*     */   public SaveDialog(String title, String defaultDir, String defaultName, String extension)
/*     */   {
/*  44 */     this.title = title;
/*  45 */     this.ext = extension;
/*  46 */     if (isMacro())
/*  47 */       return;
/*  48 */     defaultName = setExtension(defaultName, extension);
/*  49 */     if (Prefs.useJFileChooser)
/*  50 */       jSave(title, defaultDir, defaultName);
/*     */     else
/*  52 */       save(title, defaultDir, defaultName);
/*  53 */     IJ.showStatus(title + ": " + this.dir + this.name);
/*     */   }
/*     */ 
/*     */   boolean isMacro() {
/*  57 */     String macroOptions = Macro.getOptions();
/*  58 */     if (macroOptions != null) {
/*  59 */       String path = Macro.getValue(macroOptions, this.title, null);
/*  60 */       if (path == null)
/*  61 */         path = Macro.getValue(macroOptions, "path", null);
/*  62 */       if ((path != null) && (path.indexOf(".") == -1) && (!new File(path).exists()))
/*     */       {
/*  64 */         if (path.startsWith("&")) path = path.substring(1);
/*  65 */         Interpreter interp = Interpreter.getInstance();
/*  66 */         String path2 = interp != null ? interp.getStringVariable(path) : null;
/*  67 */         if (path2 != null) path = path2;
/*     */       }
/*  69 */       if (path != null) {
/*  70 */         Opener o = new Opener();
/*  71 */         this.dir = o.getDir(path);
/*  72 */         this.name = o.getName(path);
/*  73 */         return true;
/*     */       }
/*     */     }
/*  76 */     return false;
/*     */   }
/*     */ 
/*     */   public static String setExtension(String name, String extension) {
/*  80 */     if ((name == null) || (extension == null) || (extension.length() == 0))
/*  81 */       return name;
/*  82 */     int dotIndex = name.lastIndexOf(".");
/*  83 */     if ((dotIndex >= 0) && (name.length() - dotIndex <= 5)) {
/*  84 */       if ((dotIndex + 1 < name.length()) && (Character.isDigit(name.charAt(dotIndex + 1))))
/*  85 */         name = name + extension;
/*     */       else
/*  87 */         name = name.substring(0, dotIndex) + extension;
/*     */     }
/*  89 */     else name = name + extension;
/*  90 */     return name;
/*     */   }
/*     */ 
/*     */   void jSave(String title, String defaultDir, String defaultName)
/*     */   {
/*  95 */     Java2.setSystemLookAndFeel();
/*  96 */     if (EventQueue.isDispatchThread())
/*  97 */       jSaveDispatchThread(title, defaultDir, defaultName);
/*     */     else
/*  99 */       jSaveInvokeAndWait(title, defaultDir, defaultName);
/*     */   }
/*     */ 
/*     */   void jSaveDispatchThread(String title, String defaultDir, String defaultName)
/*     */   {
/* 105 */     JFileChooser fc = new JFileChooser();
/* 106 */     fc.setDialogTitle(title);
/* 107 */     if (defaultDir != null) {
/* 108 */       File f = new File(defaultDir);
/* 109 */       if (f != null)
/* 110 */         fc.setCurrentDirectory(f);
/*     */     }
/* 112 */     if (defaultName != null)
/* 113 */       fc.setSelectedFile(new File(defaultName));
/* 114 */     int returnVal = fc.showSaveDialog(IJ.getInstance());
/* 115 */     if (returnVal != 0) {
/* 116 */       Macro.abort(); return;
/* 117 */     }File f = fc.getSelectedFile();
/* 118 */     if (f.exists()) {
/* 119 */       int ret = JOptionPane.showConfirmDialog(fc, "The file " + f.getName() + " already exists. \nWould you like to replace it?", "Replace?", 0, 2);
/*     */ 
/* 123 */       if (ret != 0) f = null;
/*     */     }
/* 125 */     if (f == null) {
/* 126 */       Macro.abort();
/*     */     } else {
/* 128 */       this.dir = (fc.getCurrentDirectory().getPath() + File.separator);
/* 129 */       this.name = fc.getName(f);
/* 130 */       if (noExtension(this.name))
/* 131 */         this.name = setExtension(this.name, this.ext);
/*     */     }
/*     */   }
/*     */ 
/*     */   void jSaveInvokeAndWait(final String title, final String defaultDir, final String defaultName)
/*     */   {
/*     */     try
/*     */     {
/* 139 */       EventQueue.invokeAndWait(new Runnable() {
/*     */         public void run() {
/* 141 */           JFileChooser fc = new JFileChooser();
/* 142 */           fc.setDialogTitle(title);
/* 143 */           if (defaultDir != null) {
/* 144 */             File f = new File(defaultDir);
/* 145 */             if (f != null)
/* 146 */               fc.setCurrentDirectory(f);
/*     */           }
/* 148 */           if (defaultName != null)
/* 149 */             fc.setSelectedFile(new File(defaultName));
/* 150 */           int returnVal = fc.showSaveDialog(IJ.getInstance());
/* 151 */           if (returnVal != 0) {
/* 152 */             Macro.abort(); return;
/* 153 */           }File f = fc.getSelectedFile();
/* 154 */           if (f.exists()) {
/* 155 */             int ret = JOptionPane.showConfirmDialog(fc, "The file " + f.getName() + " already exists. \nWould you like to replace it?", "Replace?", 0, 2);
/*     */ 
/* 159 */             if (ret != 0) f = null;
/*     */           }
/* 161 */           if (f == null) {
/* 162 */             Macro.abort();
/*     */           } else {
/* 164 */             SaveDialog.this.dir = (fc.getCurrentDirectory().getPath() + File.separator);
/* 165 */             SaveDialog.this.name = fc.getName(f);
/* 166 */             if (SaveDialog.this.noExtension(SaveDialog.this.name))
/* 167 */               SaveDialog.this.name = SaveDialog.setExtension(SaveDialog.this.name, SaveDialog.this.ext);
/*     */           }
/*     */         } } );
/*     */     }
/*     */     catch (Exception e) {
/*     */     }
/*     */   }
/*     */ 
/*     */   void save(String title, String defaultDir, String defaultName) {
/* 176 */     ImageJ ij = IJ.getInstance();
/* 177 */     Frame parent = ij != null ? ij : new Frame();
/* 178 */     FileDialog fd = new FileDialog(parent, title, 1);
/* 179 */     if (defaultName != null)
/* 180 */       fd.setFile(defaultName);
/* 181 */     if (defaultDir != null)
/* 182 */       fd.setDirectory(defaultDir);
/* 183 */     fd.show();
/* 184 */     this.name = fd.getFile();
/* 185 */     String origName = this.name;
/* 186 */     if (noExtension(this.name)) {
/* 187 */       this.name = setExtension(this.name, this.ext);
/* 188 */       boolean dialog = (this.name != null) && (!this.name.equals(origName)) && (IJ.isMacOSX()) && (!IJ.isMacro());
/* 189 */       if (dialog) {
/* 190 */         File f = new File(fd.getDirectory() + getFileName());
/* 191 */         if (!f.exists()) dialog = false;
/*     */       }
/* 193 */       if (dialog) {
/* 194 */         Font font = new Font("SansSerif", 1, 12);
/* 195 */         GenericDialog gd = new GenericDialog("Replace File?");
/* 196 */         gd.addMessage("\"" + this.name + "\" already exists.\nDo you want to replace it?", font);
/* 197 */         gd.addMessage("To avoid this dialog, enable\n\"Show all filename extensions\"\nin Finder Preferences.");
/*     */ 
/* 199 */         gd.setOKLabel("Replace");
/* 200 */         gd.showDialog();
/* 201 */         if (gd.wasCanceled())
/* 202 */           this.name = null;
/*     */       }
/*     */     }
/* 205 */     if (IJ.debugMode) IJ.log(origName + "->" + this.name);
/* 206 */     this.dir = fd.getDirectory();
/* 207 */     if (this.name == null)
/* 208 */       Macro.abort();
/* 209 */     fd.dispose();
/* 210 */     if (ij == null)
/* 211 */       parent.dispose();
/*     */   }
/*     */ 
/*     */   private boolean noExtension(String name) {
/* 215 */     if (name == null) return false;
/* 216 */     int dotIndex = name.indexOf(".");
/* 217 */     return (dotIndex == -1) || (name.length() - dotIndex > 5);
/*     */   }
/*     */ 
/*     */   public String getDirectory()
/*     */   {
/* 222 */     OpenDialog.setLastDirectory(this.dir);
/* 223 */     return this.dir;
/*     */   }
/*     */ 
/*     */   public String getFileName()
/*     */   {
/* 228 */     if (this.name != null) {
/* 229 */       if (Recorder.record)
/* 230 */         Recorder.recordPath(this.title, this.dir + this.name);
/* 231 */       OpenDialog.setLastName(this.name);
/*     */     }
/* 233 */     return this.name;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.io.SaveDialog
 * JD-Core Version:    0.6.2
 */