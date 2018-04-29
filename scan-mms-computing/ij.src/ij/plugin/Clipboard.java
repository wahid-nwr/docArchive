/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImageJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.WindowManager;
/*     */ import ij.gui.Roi;
/*     */ import ij.plugin.frame.Editor;
/*     */ import ij.process.ColorProcessor;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.util.Tools;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Image;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.datatransfer.DataFlavor;
/*     */ import java.awt.datatransfer.Transferable;
/*     */ import java.awt.datatransfer.UnsupportedFlavorException;
/*     */ import java.awt.image.BufferedImage;
/*     */ 
/*     */ public class Clipboard
/*     */   implements PlugIn, Transferable
/*     */ {
/*     */   static java.awt.datatransfer.Clipboard clipboard;
/*     */ 
/*     */   public void run(String arg)
/*     */   {
/*  20 */     if (IJ.altKeyDown()) {
/*  21 */       if (arg.equals("copy"))
/*  22 */         arg = "scopy";
/*  23 */       else if (arg.equals("paste"))
/*  24 */         arg = "spaste";
/*     */     }
/*  26 */     if (arg.equals("copy"))
/*  27 */       copy(false);
/*  28 */     else if (arg.equals("paste"))
/*  29 */       paste();
/*  30 */     else if (arg.equals("cut"))
/*  31 */       copy(true);
/*  32 */     else if (arg.equals("scopy"))
/*  33 */       copyToSystem();
/*  34 */     else if (arg.equals("showsys"))
/*  35 */       showSystemClipboard();
/*  36 */     else if (arg.equals("show"))
/*  37 */       showInternalClipboard();
/*     */   }
/*     */ 
/*     */   void copy(boolean cut) {
/*  41 */     ImagePlus imp = WindowManager.getCurrentImage();
/*  42 */     if (imp != null)
/*  43 */       imp.copy(cut);
/*     */     else
/*  45 */       IJ.noImage();
/*     */   }
/*     */ 
/*     */   void paste() {
/*  49 */     if (ImagePlus.getClipboard() == null) {
/*  50 */       showSystemClipboard();
/*     */     } else {
/*  52 */       ImagePlus imp = WindowManager.getCurrentImage();
/*  53 */       if (imp != null)
/*  54 */         imp.paste();
/*     */       else
/*  56 */         showInternalClipboard();
/*     */     }
/*     */   }
/*     */ 
/*     */   void setup() {
/*  61 */     if (clipboard == null)
/*  62 */       clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
/*     */   }
/*     */ 
/*     */   void copyToSystem() {
/*  66 */     setup();
/*     */     try {
/*  68 */       clipboard.setContents(this, null); } catch (Throwable t) {
/*     */     }
/*     */   }
/*     */ 
/*     */   void showSystemClipboard() {
/*  73 */     setup();
/*  74 */     IJ.showStatus("Opening system clipboard...");
/*     */     try {
/*  76 */       Transferable transferable = clipboard.getContents(null);
/*  77 */       boolean imageSupported = transferable.isDataFlavorSupported(DataFlavor.imageFlavor);
/*  78 */       boolean textSupported = transferable.isDataFlavorSupported(DataFlavor.stringFlavor);
/*  79 */       if ((!imageSupported) && (IJ.isMacOSX()) && (!IJ.isJava16()))
/*     */       {
/*  81 */         Object mc = IJ.runPlugIn("MacClipboard", "");
/*  82 */         if ((mc != null) && ((mc instanceof ImagePlus)) && (((ImagePlus)mc).getImage() != null))
/*  83 */           return;
/*     */       }
/*  85 */       if (imageSupported) {
/*  86 */         Image img = (Image)transferable.getTransferData(DataFlavor.imageFlavor);
/*  87 */         if (img == null) {
/*  88 */           IJ.error("Unable to convert image on system clipboard");
/*  89 */           IJ.showStatus("");
/*  90 */           return;
/*     */         }
/*  92 */         int width = img.getWidth(null);
/*  93 */         int height = img.getHeight(null);
/*  94 */         BufferedImage bi = new BufferedImage(width, height, 1);
/*  95 */         Graphics g = bi.createGraphics();
/*  96 */         g.drawImage(img, 0, 0, null);
/*  97 */         g.dispose();
/*  98 */         WindowManager.checkForDuplicateName = true;
/*  99 */         new ImagePlus("Clipboard", bi).show();
/* 100 */       } else if (textSupported) {
/* 101 */         String text = (String)transferable.getTransferData(DataFlavor.stringFlavor);
/* 102 */         if (IJ.isMacintosh())
/* 103 */           text = Tools.fixNewLines(text);
/* 104 */         Editor ed = new Editor();
/* 105 */         ed.setSize(600, 300);
/* 106 */         ed.create("Clipboard", text);
/* 107 */         IJ.showStatus("");
/*     */       } else {
/* 109 */         IJ.error("Unable to find an image on the system clipboard");
/*     */       }
/*     */     } catch (Throwable e) { IJ.handleException(e); }
/*     */   }
/*     */ 
/*     */   public DataFlavor[] getTransferDataFlavors()
/*     */   {
/* 116 */     return new DataFlavor[] { DataFlavor.imageFlavor };
/*     */   }
/*     */ 
/*     */   public boolean isDataFlavorSupported(DataFlavor flavor) {
/* 120 */     return DataFlavor.imageFlavor.equals(flavor);
/*     */   }
/*     */ 
/*     */   public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
/* 124 */     if (!isDataFlavorSupported(flavor))
/* 125 */       throw new UnsupportedFlavorException(flavor);
/* 126 */     ImagePlus imp = WindowManager.getCurrentImage();
/* 127 */     if (imp != null)
/*     */     {
/* 129 */       if (imp.isComposite()) {
/* 130 */         ImageProcessor ip = new ColorProcessor(imp.getImage());
/* 131 */         ip.setRoi(imp.getRoi());
/*     */       } else {
/* 133 */         ip = imp.getProcessor();
/* 134 */       }ImageProcessor ip = ip.crop();
/* 135 */       int w = ip.getWidth();
/* 136 */       int h = ip.getHeight();
/* 137 */       IJ.showStatus(w + "x" + h + " image copied to system clipboard");
/* 138 */       Image img = IJ.getInstance().createImage(w, h);
/* 139 */       Graphics g = img.getGraphics();
/* 140 */       g.drawImage(ip.createImage(), 0, 0, null);
/* 141 */       g.dispose();
/* 142 */       return img;
/*     */     }
/*     */ 
/* 145 */     return null;
/*     */   }
/*     */ 
/*     */   void showInternalClipboard()
/*     */   {
/* 150 */     ImagePlus clipboard = ImagePlus.getClipboard();
/* 151 */     if (clipboard != null) {
/* 152 */       ImageProcessor ip = clipboard.getProcessor();
/* 153 */       ImagePlus imp2 = new ImagePlus("Clipboard", ip.duplicate());
/* 154 */       Roi roi = clipboard.getRoi();
/* 155 */       imp2.killRoi();
/* 156 */       if ((roi != null) && (roi.isArea()) && (roi.getType() != 0)) {
/* 157 */         roi = (Roi)roi.clone();
/* 158 */         roi.setLocation(0, 0);
/* 159 */         imp2.setRoi(roi);
/* 160 */         IJ.run(imp2, "Clear Outside", null);
/* 161 */         imp2.killRoi();
/*     */       }
/* 163 */       WindowManager.checkForDuplicateName = true;
/* 164 */       imp2.show();
/*     */     } else {
/* 166 */       IJ.error("The internal clipboard is empty.");
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.Clipboard
 * JD-Core Version:    0.6.2
 */