/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImageJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.Prefs;
/*     */ import ij.WindowManager;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.gui.ImageWindow;
/*     */ import ij.gui.NonBlockingGenericDialog;
/*     */ import ij.gui.ProgressBar;
/*     */ import ij.io.OpenDialog;
/*     */ import ij.process.ImageProcessor;
/*     */ import java.awt.Button;
/*     */ import java.awt.Choice;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.FlowLayout;
/*     */ import java.awt.Panel;
/*     */ import java.awt.Point;
/*     */ import java.awt.TextArea;
/*     */ import java.awt.TextField;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.ItemEvent;
/*     */ import java.awt.event.ItemListener;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class BatchProcesser
/*     */   implements PlugIn, ActionListener, ItemListener, Runnable
/*     */ {
/*     */   private static final String MACRO_FILE_NAME = "BatchMacro.ijm";
/*  15 */   private static final String[] formats = { "TIFF", "8-bit TIFF", "JPEG", "GIF", "PNG", "PGM", "BMP", "FITS", "Text Image", "ZIP", "Raw" };
/*  16 */   private static String format = Prefs.get("batch.format", formats[0]);
/*  17 */   private static final String[] code = { "[Select from list]", "Add Border", "Convert to RGB", "Crop", "Gaussian Blur", "Invert", "Label", "Timestamp", "Max Dimension", "Measure", "Resize", "Scale", "Show File Info", "Unsharp Mask" };
/*     */ 
/*  33 */   private String macro = "";
/*     */   private int testImage;
/*     */   private Button input;
/*     */   private Button output;
/*     */   private Button open;
/*     */   private Button save;
/*     */   private Button test;
/*     */   private TextField inputDir;
/*     */   private TextField outputDir;
/*     */   private GenericDialog gd;
/*     */   private Thread thread;
/*     */   private ImagePlus virtualStack;
/*     */ 
/*     */   public void run(String arg)
/*     */   {
/*  42 */     if (arg.equals("stack")) {
/*  43 */       this.virtualStack = IJ.getImage();
/*  44 */       if (this.virtualStack.getStackSize() == 1) {
/*  45 */         error("This command requires a stack or virtual stack.");
/*  46 */         return;
/*     */       }
/*     */     }
/*  49 */     String macroPath = IJ.getDirectory("macros") + "BatchMacro.ijm";
/*  50 */     this.macro = IJ.openAsString(macroPath);
/*  51 */     if ((this.macro == null) || (this.macro.startsWith("Error: "))) {
/*  52 */       IJ.showStatus(this.macro.substring(7) + ": " + macroPath);
/*  53 */       this.macro = "";
/*     */     }
/*  55 */     if (!showDialog()) return;
/*  56 */     String inputPath = null;
/*  57 */     if (this.virtualStack == null) {
/*  58 */       inputPath = this.inputDir.getText();
/*  59 */       if (inputPath.equals("")) {
/*  60 */         error("Please choose an input folder");
/*  61 */         return;
/*     */       }
/*  63 */       inputPath = addSeparator(inputPath);
/*  64 */       File f1 = new File(inputPath);
/*  65 */       if ((!f1.exists()) || (!f1.isDirectory())) {
/*  66 */         error("Input does not exist or is not a folder\n \n" + inputPath);
/*  67 */         return;
/*     */       }
/*     */     }
/*  70 */     String outputPath = this.outputDir.getText();
/*  71 */     outputPath = addSeparator(outputPath);
/*  72 */     File f2 = new File(outputPath);
/*  73 */     if ((!outputPath.equals("")) && ((!f2.exists()) || (!f2.isDirectory()))) {
/*  74 */       error("Output does not exist or is not a folder\n \n" + outputPath);
/*  75 */       return;
/*     */     }
/*  77 */     if (this.macro.equals("")) {
/*  78 */       error("There is no macro code in the text area");
/*  79 */       return;
/*     */     }
/*  81 */     ImageJ ij = IJ.getInstance();
/*  82 */     if (ij != null) ij.getProgressBar().setBatchMode(true);
/*  83 */     IJ.resetEscape();
/*  84 */     if (this.virtualStack != null)
/*  85 */       processVirtualStack(outputPath);
/*     */     else
/*  87 */       processFolder(inputPath, outputPath);
/*  88 */     IJ.showProgress(1, 1);
/*  89 */     if (this.virtualStack == null)
/*  90 */       Prefs.set("batch.input", this.inputDir.getText());
/*  91 */     Prefs.set("batch.output", this.outputDir.getText());
/*  92 */     Prefs.set("batch.format", format);
/*  93 */     this.macro = this.gd.getTextArea1().getText();
/*  94 */     if (!this.macro.equals(""))
/*  95 */       IJ.saveString(this.macro, IJ.getDirectory("macros") + "BatchMacro.ijm");
/*     */   }
/*     */ 
/*     */   boolean showDialog() {
/*  99 */     validateFormat();
/* 100 */     this.gd = new NonBlockingGenericDialog("Batch Process");
/* 101 */     addPanels(this.gd);
/* 102 */     this.gd.setInsets(15, 0, 5);
/* 103 */     this.gd.addChoice("Output Format:", formats, format);
/* 104 */     this.gd.setInsets(0, 0, 5);
/* 105 */     this.gd.addChoice("Add Macro Code:", code, code[0]);
/* 106 */     this.gd.setInsets(15, 10, 0);
/* 107 */     Dimension screen = IJ.getScreenSize();
/* 108 */     this.gd.addTextAreas(this.macro, null, screen.width <= 600 ? 10 : 15, 60);
/* 109 */     addButtons(this.gd);
/* 110 */     this.gd.setOKLabel("Process");
/* 111 */     Vector choices = this.gd.getChoices();
/* 112 */     Choice choice = (Choice)choices.elementAt(1);
/* 113 */     choice.addItemListener(this);
/* 114 */     this.gd.showDialog();
/* 115 */     format = this.gd.getNextChoice();
/* 116 */     this.macro = this.gd.getNextText();
/* 117 */     return !this.gd.wasCanceled();
/*     */   }
/*     */ 
/*     */   void processVirtualStack(String outputPath) {
/* 121 */     ImageStack stack = this.virtualStack.getStack();
/* 122 */     int n = stack.getSize();
/* 123 */     int index = 0;
/* 124 */     for (int i = 1; (i <= n) && 
/* 125 */       (!IJ.escapePressed()); i++)
/*     */     {
/* 126 */       IJ.showProgress(i, n);
/* 127 */       ImageProcessor ip = stack.getProcessor(i);
/* 128 */       if (ip == null) return;
/* 129 */       ImagePlus imp = new ImagePlus("", ip);
/* 130 */       if (!this.macro.equals("")) {
/* 131 */         WindowManager.setTempCurrentImage(imp);
/* 132 */         String str = IJ.runMacro("i=" + index++ + ";" + this.macro, "");
/* 133 */         if ((str != null) && (str.equals("[aborted]"))) break;
/*     */       }
/* 135 */       if (!outputPath.equals("")) {
/* 136 */         if ((format.equals("8-bit TIFF")) || (format.equals("GIF"))) {
/* 137 */           if (imp.getBitDepth() == 24)
/* 138 */             IJ.run(imp, "8-bit Color", "number=256");
/*     */           else
/* 140 */             IJ.run(imp, "8-bit", "");
/*     */         }
/* 142 */         IJ.saveAs(imp, format, outputPath + pad(i));
/*     */       }
/* 144 */       imp.close();
/*     */     }
/* 146 */     if ((outputPath != null) && (!outputPath.equals("")))
/* 147 */       IJ.run("Image Sequence...", "open=[" + outputPath + "]" + " use");
/*     */   }
/*     */ 
/*     */   String pad(int n) {
/* 151 */     String str = "" + n;
/* 152 */     while (str.length() < 5)
/* 153 */       str = "0" + str;
/* 154 */     return str;
/*     */   }
/*     */ 
/*     */   void processFolder(String inputPath, String outputPath)
/*     */   {
/* 159 */     String[] list = new File(inputPath).list();
/* 160 */     int index = 0;
/* 161 */     for (int i = 0; (i < list.length) && 
/* 162 */       (!IJ.escapePressed()); i++)
/*     */     {
/* 163 */       String path = inputPath + list[i];
/* 164 */       if (IJ.debugMode) IJ.log(i + ": " + path);
/* 165 */       if (!new File(path).isDirectory())
/*     */       {
/* 167 */         if ((!list[i].startsWith(".")) && (!list[i].endsWith(".avi")) && (!list[i].endsWith(".AVI")))
/*     */         {
/* 169 */           IJ.showProgress(i + 1, list.length);
/* 170 */           ImagePlus imp = IJ.openImage(path);
/* 171 */           if (imp != null) {
/* 172 */             if (!this.macro.equals("")) {
/* 173 */               WindowManager.setTempCurrentImage(imp);
/* 174 */               String str = IJ.runMacro("i=" + index++ + ";" + this.macro, "");
/* 175 */               if ((str != null) && (str.equals("[aborted]"))) break;
/*     */             }
/* 177 */             if (!outputPath.equals("")) {
/* 178 */               if ((format.equals("8-bit TIFF")) || (format.equals("GIF"))) {
/* 179 */                 if (imp.getBitDepth() == 24)
/* 180 */                   IJ.run(imp, "8-bit Color", "number=256");
/*     */                 else
/* 182 */                   IJ.run(imp, "8-bit", "");
/*     */               }
/* 184 */               IJ.saveAs(imp, format, outputPath + list[i]);
/*     */             }
/* 186 */             imp.close(); }  } 
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/* 191 */   String addSeparator(String path) { if (path.equals("")) return path;
/* 192 */     if ((!path.endsWith("/")) && (!path.endsWith("\\")))
/* 193 */       path = path + File.separator;
/* 194 */     return path; }
/*     */ 
/*     */   void validateFormat()
/*     */   {
/* 198 */     boolean validFormat = false;
/* 199 */     for (int i = 0; i < formats.length; i++) {
/* 200 */       if (format.equals(formats[i])) {
/* 201 */         validFormat = true;
/* 202 */         break;
/*     */       }
/*     */     }
/* 205 */     if (!validFormat) format = formats[0]; 
/*     */   }
/*     */ 
/*     */   void addPanels(GenericDialog gd)
/*     */   {
/* 209 */     Panel p = new Panel();
/* 210 */     p.setLayout(new FlowLayout(1, 5, 0));
/* 211 */     if (this.virtualStack == null) {
/* 212 */       this.input = new Button("Input...");
/* 213 */       this.input.addActionListener(this);
/* 214 */       p.add(this.input);
/* 215 */       this.inputDir = new TextField(Prefs.get("batch.input", ""), 45);
/* 216 */       p.add(this.inputDir);
/* 217 */       gd.addPanel(p);
/*     */     }
/* 219 */     p = new Panel();
/* 220 */     p.setLayout(new FlowLayout(1, 5, 0));
/* 221 */     this.output = new Button("Output...");
/* 222 */     this.output.addActionListener(this);
/* 223 */     p.add(this.output);
/* 224 */     this.outputDir = new TextField(Prefs.get("batch.output", ""), 45);
/* 225 */     p.add(this.outputDir);
/* 226 */     gd.addPanel(p);
/*     */   }
/*     */ 
/*     */   void addButtons(GenericDialog gd) {
/* 230 */     Panel p = new Panel();
/* 231 */     p.setLayout(new FlowLayout(1, 5, 0));
/* 232 */     this.test = new Button("Test");
/* 233 */     this.test.addActionListener(this);
/* 234 */     p.add(this.test);
/* 235 */     this.open = new Button("Open...");
/* 236 */     this.open.addActionListener(this);
/* 237 */     p.add(this.open);
/* 238 */     this.save = new Button("Save...");
/* 239 */     this.save.addActionListener(this);
/* 240 */     p.add(this.save);
/* 241 */     gd.addPanel(p);
/*     */   }
/*     */ 
/*     */   public void itemStateChanged(ItemEvent e) {
/* 245 */     Choice choice = (Choice)e.getSource();
/* 246 */     String item = choice.getSelectedItem();
/* 247 */     String code = null;
/* 248 */     if (item.equals("Convert to RGB"))
/* 249 */       code = "run(\"RGB Color\");\n";
/* 250 */     else if (item.equals("Measure"))
/* 251 */       code = "run(\"Measure\");\n";
/* 252 */     else if (item.equals("Resize"))
/* 253 */       code = "run(\"Size...\", \"width=512 height=512 interpolation=Bicubic\");\n";
/* 254 */     else if (item.equals("Scale"))
/* 255 */       code = "scale=1.5;\nw=getWidth*scale; h=getHeight*scale;\nrun(\"Size...\", \"width=w height=h interpolation=Bilinear\");\n";
/* 256 */     else if (item.equals("Label"))
/* 257 */       code = "setFont(\"SansSerif\", 18, \"antialiased\");\nsetColor(\"red\");\ndrawString(\"Hello\", 20, 30);\n";
/* 258 */     else if (item.equals("Timestamp"))
/* 259 */       code = openMacroFromJar("TimeStamp.ijm");
/* 260 */     else if (item.equals("Crop"))
/* 261 */       code = "makeRectangle(getWidth/4, getHeight/4, getWidth/2, getHeight/2);\nrun(\"Crop\");\n";
/* 262 */     else if (item.equals("Add Border"))
/* 263 */       code = "border=25;\nw=getWidth+border*2; h=getHeight+border*2;\nrun(\"Canvas Size...\", \"width=w height=h position=Center zero\");\n";
/* 264 */     else if (item.equals("Invert"))
/* 265 */       code = "run(\"Invert\");\n";
/* 266 */     else if (item.equals("Gaussian Blur"))
/* 267 */       code = "run(\"Gaussian Blur...\", \"sigma=2\");\n";
/* 268 */     else if (item.equals("Unsharp Mask"))
/* 269 */       code = "run(\"Unsharp Mask...\", \"radius=1 mask=0.60\");\n";
/* 270 */     else if (item.equals("Show File Info"))
/* 271 */       code = "path=File.directory+File.name;\ndate=File.dateLastModified(path);\nsize=File.length(path);\nprint(i+\", \"+getTitle+\", \"+date+\", \"+size);\n";
/* 272 */     else if (item.equals("Max Dimension"))
/* 273 */       code = "max=2048;\nw=getWidth; h=getHeight;\nsize=maxOf(w,h);\nif (size>max) {\n  scale = max/size;\n  w*=scale; h*=scale;\n  run(\"Size...\", \"width=w height=h interpolation=Bicubic average\");\n}";
/* 274 */     if (code != null) {
/* 275 */       TextArea ta = this.gd.getTextArea1();
/* 276 */       ta.insert(code, ta.getCaretPosition());
/* 277 */       if (IJ.isMacOSX()) ta.requestFocus(); 
/*     */     }
/*     */   }
/*     */ 
/*     */   String openMacroFromJar(String name)
/*     */   {
/* 282 */     ImageJ ij = IJ.getInstance();
/* 283 */     Class c = ij != null ? ij.getClass() : new ImageStack().getClass();
/* 284 */     String macro = null;
/*     */     try {
/* 286 */       InputStream is = c.getResourceAsStream("/macros/" + name);
/* 287 */       if (is == null) return null;
/* 288 */       InputStreamReader isr = new InputStreamReader(is);
/* 289 */       StringBuffer sb = new StringBuffer();
/* 290 */       char[] b = new char[8192];
/*     */       int n;
/* 292 */       while ((n = isr.read(b)) > 0)
/* 293 */         sb.append(b, 0, n);
/* 294 */       macro = sb.toString();
/*     */     }
/*     */     catch (IOException e) {
/* 297 */       return null;
/*     */     }
/* 299 */     return macro;
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent e) {
/* 303 */     Object source = e.getSource();
/* 304 */     if (source == this.input) {
/* 305 */       String path = IJ.getDirectory("Input Folder");
/* 306 */       if (path == null) return;
/* 307 */       this.inputDir.setText(path);
/* 308 */       if (IJ.isMacOSX()) {
/* 309 */         this.gd.setVisible(false); this.gd.setVisible(true);
/*     */       } } else if (source == this.output) {
/* 311 */       String path = IJ.getDirectory("Output Folder");
/* 312 */       if (path == null) return;
/* 313 */       this.outputDir.setText(path);
/* 314 */       if (IJ.isMacOSX()) {
/* 315 */         this.gd.setVisible(false); this.gd.setVisible(true);
/*     */       } } else if (source == this.test) {
/* 317 */       this.thread = new Thread(this, "BatchTest");
/* 318 */       this.thread.setPriority(Math.max(this.thread.getPriority() - 2, 1));
/* 319 */       this.thread.start();
/* 320 */     } else if (source == this.open) {
/* 321 */       open();
/* 322 */     } else if (source == this.save) {
/* 323 */       save();
/*     */     }
/*     */   }
/*     */ 
/* 327 */   void open() { String text = IJ.openAsString("");
/* 328 */     if (text == null) return;
/* 329 */     if (text.startsWith("Error: ")) {
/* 330 */       error(text.substring(7));
/*     */     }
/* 332 */     else if (text.length() > 30000)
/* 333 */       error("File is too large");
/*     */     else
/* 335 */       this.gd.getTextArea1().setText(text);
/*     */   }
/*     */ 
/*     */   void save()
/*     */   {
/* 340 */     this.macro = this.gd.getTextArea1().getText();
/* 341 */     if (!this.macro.equals(""))
/* 342 */       IJ.saveString(this.macro, "");
/*     */   }
/*     */ 
/*     */   void error(String msg) {
/* 346 */     IJ.error("Batch Processer", msg);
/*     */   }
/*     */ 
/*     */   public void run() {
/* 350 */     TextArea ta = this.gd.getTextArea1();
/*     */ 
/* 352 */     String macro = ta.getText();
/* 353 */     if (macro.equals("")) {
/* 354 */       error("There is no macro code in the text area");
/* 355 */       return;
/*     */     }
/* 357 */     ImagePlus imp = null;
/* 358 */     if (this.virtualStack != null)
/* 359 */       imp = getVirtualStackImage();
/*     */     else
/* 361 */       imp = getFolderImage();
/* 362 */     if (imp == null) return;
/* 363 */     WindowManager.setTempCurrentImage(imp);
/* 364 */     String str = IJ.runMacro("i=0;" + macro, "");
/* 365 */     Point loc = new Point(10, 30);
/* 366 */     if (this.testImage != 0) {
/* 367 */       ImagePlus imp2 = WindowManager.getImage(this.testImage);
/* 368 */       if (imp2 != null) {
/* 369 */         ImageWindow win = imp2.getWindow();
/* 370 */         if (win != null) loc = win.getLocation();
/* 371 */         imp2.changes = false;
/* 372 */         imp2.close();
/*     */       }
/*     */     }
/* 375 */     imp.show();
/* 376 */     ImageWindow iw = imp.getWindow();
/* 377 */     if (iw != null) iw.setLocation(loc);
/* 378 */     this.testImage = imp.getID();
/*     */   }
/*     */ 
/*     */   ImagePlus getVirtualStackImage() {
/* 382 */     ImagePlus imp = this.virtualStack.createImagePlus();
/* 383 */     imp.setProcessor("", this.virtualStack.getProcessor().duplicate());
/* 384 */     return imp;
/*     */   }
/*     */ 
/*     */   ImagePlus getFolderImage() {
/* 388 */     String inputPath = this.inputDir.getText();
/* 389 */     inputPath = addSeparator(inputPath);
/* 390 */     File f1 = new File(inputPath);
/* 391 */     if ((!f1.exists()) || (!f1.isDirectory())) {
/* 392 */       error("Input does not exist or is not a folder\n \n" + inputPath);
/* 393 */       return null;
/*     */     }
/* 395 */     String[] list = new File(inputPath).list();
/* 396 */     String name = list[0];
/* 397 */     if ((name.startsWith(".")) && (list.length > 1)) name = list[1];
/* 398 */     String path = inputPath + name;
/* 399 */     setDirAndName(path);
/* 400 */     return IJ.openImage(path);
/*     */   }
/*     */ 
/*     */   void setDirAndName(String path) {
/* 404 */     File f = new File(path);
/* 405 */     OpenDialog.setLastDirectory(f.getParent() + File.separator);
/* 406 */     OpenDialog.setLastName(f.getName());
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.BatchProcesser
 * JD-Core Version:    0.6.2
 */