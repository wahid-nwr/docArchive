/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImageJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.Prefs;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.gui.ProgressBar;
/*     */ import ij.process.ImageProcessor;
/*     */ import java.awt.Button;
/*     */ import java.awt.FlowLayout;
/*     */ import java.awt.Panel;
/*     */ import java.awt.TextField;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.io.File;
/*     */ 
/*     */ public class BatchConverter
/*     */   implements PlugIn, ActionListener
/*     */ {
/*  13 */   private static final String[] formats = { "TIFF", "8-bit TIFF", "JPEG", "GIF", "PNG", "PGM", "BMP", "FITS", "Text Image", "ZIP", "Raw" };
/*  14 */   private static String format = formats[0];
/*     */ 
/*  16 */   private static double scale = 1.0D;
/*  17 */   private static int interpolationMethod = 1;
/*  18 */   private String[] methods = ImageProcessor.getInterpolationMethods();
/*     */   private Button input;
/*     */   private Button output;
/*     */   private TextField inputDir;
/*     */   private TextField outputDir;
/*     */   private GenericDialog gd;
/*     */ 
/*     */   public void run(String arg)
/*     */   {
/*  24 */     if (!showDialog()) return;
/*  25 */     String inputPath = this.inputDir.getText();
/*  26 */     if (inputPath.equals("")) {
/*  27 */       IJ.error("Batch Converter", "Please choose an input folder");
/*  28 */       return;
/*     */     }
/*  30 */     String outputPath = this.outputDir.getText();
/*  31 */     if (outputPath.equals("")) {
/*  32 */       IJ.error("Batch Converter", "Please choose an output folder");
/*  33 */       return;
/*     */     }
/*  35 */     File f1 = new File(inputPath);
/*  36 */     if ((!f1.exists()) || (!f1.isDirectory())) {
/*  37 */       IJ.error("Batch Converter", "Input does not exist or is not a folder\n \n" + inputPath);
/*  38 */       return;
/*     */     }
/*  40 */     File f2 = new File(outputPath);
/*  41 */     if ((!outputPath.equals("")) && ((!f2.exists()) || (!f2.isDirectory()))) {
/*  42 */       IJ.error("Batch Converter", "Output does not exist or is not a folder\n \n" + outputPath);
/*  43 */       return;
/*     */     }
/*  45 */     String[] list = new File(inputPath).list();
/*  46 */     ImageJ ij = IJ.getInstance();
/*  47 */     if (ij != null) ij.getProgressBar().setBatchMode(true);
/*  48 */     IJ.resetEscape();
/*  49 */     for (int i = 0; (i < list.length) && 
/*  50 */       (!IJ.escapePressed()); i++)
/*     */     {
/*  51 */       if (IJ.debugMode) IJ.log(i + "  " + list[i]);
/*  52 */       String path = inputPath + list[i];
/*  53 */       if (!new File(path).isDirectory())
/*     */       {
/*  55 */         if ((!list[i].startsWith(".")) && (!list[i].endsWith(".avi")) && (!list[i].endsWith(".AVI")))
/*     */         {
/*  57 */           IJ.showProgress(i + 1, list.length);
/*  58 */           ImagePlus imp = IJ.openImage(path);
/*  59 */           if (imp != null)
/*     */           {
/*  67 */             if (scale != 1.0D) {
/*  68 */               int width = (int)(scale * imp.getWidth());
/*  69 */               int height = (int)(scale * imp.getHeight());
/*  70 */               ImageProcessor ip = imp.getProcessor();
/*  71 */               ip.setInterpolationMethod(interpolationMethod);
/*  72 */               imp.setProcessor(null, ip.resize(width, height, true));
/*     */             }
/*  74 */             if ((format.equals("8-bit TIFF")) || (format.equals("GIF"))) {
/*  75 */               if (imp.getBitDepth() == 24)
/*  76 */                 IJ.run(imp, "8-bit Color", "number=256");
/*     */               else
/*  78 */                 IJ.run(imp, "8-bit", "");
/*     */             }
/*  80 */             IJ.saveAs(imp, format, outputPath + list[i]);
/*  81 */             imp.close();
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*  83 */     IJ.showProgress(1, 1);
/*  84 */     Prefs.set("batch.input", this.inputDir.getText());
/*  85 */     Prefs.set("batch.output", this.outputDir.getText());
/*     */   }
/*     */ 
/*     */   boolean showDialog() {
/*  89 */     this.gd = new GenericDialog("Batch Convert");
/*  90 */     addPanels(this.gd);
/*  91 */     this.gd.setInsets(15, 0, 5);
/*  92 */     this.gd.addChoice("Output Format: ", formats, format);
/*  93 */     this.gd.addChoice("Interpolation:", this.methods, this.methods[interpolationMethod]);
/*     */ 
/*  95 */     this.gd.addNumericField("Scale Factor: ", scale, 2);
/*  96 */     this.gd.setOKLabel("Convert");
/*  97 */     this.gd.showDialog();
/*  98 */     format = this.gd.getNextChoice();
/*  99 */     interpolationMethod = this.gd.getNextChoiceIndex();
/*     */ 
/* 101 */     scale = this.gd.getNextNumber();
/* 102 */     return !this.gd.wasCanceled();
/*     */   }
/*     */ 
/*     */   void addPanels(GenericDialog gd) {
/* 106 */     Panel p = new Panel();
/* 107 */     p.setLayout(new FlowLayout(1, 5, 0));
/* 108 */     this.input = new Button("Input...");
/* 109 */     this.input.addActionListener(this);
/* 110 */     p.add(this.input);
/* 111 */     this.inputDir = new TextField(Prefs.get("batch.input", ""), 45);
/* 112 */     p.add(this.inputDir);
/* 113 */     gd.addPanel(p);
/* 114 */     p = new Panel();
/* 115 */     p.setLayout(new FlowLayout(1, 5, 0));
/* 116 */     this.output = new Button("Output...");
/* 117 */     this.output.addActionListener(this);
/* 118 */     p.add(this.output);
/* 119 */     this.outputDir = new TextField(Prefs.get("batch.output", ""), 45);
/* 120 */     p.add(this.outputDir);
/* 121 */     gd.addPanel(p);
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent e) {
/* 125 */     Object source = e.getSource();
/* 126 */     String s = source == this.input ? "Input" : "Output";
/* 127 */     String path = IJ.getDirectory(s + " Folder");
/* 128 */     if (path == null) return;
/* 129 */     if (source == this.input)
/* 130 */       this.inputDir.setText(path);
/*     */     else
/* 132 */       this.outputDir.setText(path);
/* 133 */     if (IJ.isMacOSX()) {
/* 134 */       this.gd.setVisible(false); this.gd.setVisible(true);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.BatchConverter
 * JD-Core Version:    0.6.2
 */