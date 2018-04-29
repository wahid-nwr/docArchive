/*    */ package ij.plugin;
/*    */ 
/*    */ import ij.IJ;
/*    */ import ij.ImagePlus;
/*    */ import ij.WindowManager;
/*    */ import ij.gui.GenericDialog;
/*    */ import ij.plugin.frame.Recorder;
/*    */ import ij.process.ImageProcessor;
/*    */ import java.awt.Button;
/*    */ import java.awt.GridLayout;
/*    */ import java.awt.Insets;
/*    */ import java.awt.Panel;
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.awt.event.ActionListener;
/*    */ 
/*    */ public class LUT_Editor
/*    */   implements PlugIn, ActionListener
/*    */ {
/*    */   private ImagePlus imp;
/*    */   Button openButton;
/*    */   Button saveButton;
/*    */   Button resizeButton;
/*    */   Button invertButton;
/*    */   ColorPanel colorPanel;
/*    */   int bitDepth;
/*    */ 
/*    */   public void run(String args)
/*    */   {
/* 20 */     ImagePlus imp = WindowManager.getCurrentImage();
/* 21 */     if (imp == null) {
/* 22 */       IJ.showMessage("LUT Editor", "No images are open");
/* 23 */       return;
/*    */     }
/* 25 */     this.bitDepth = imp.getBitDepth();
/* 26 */     if (this.bitDepth == 24) {
/* 27 */       IJ.showMessage("LUT Editor", "RGB images do not use LUTs");
/* 28 */       return;
/*    */     }
/* 30 */     if (this.bitDepth != 8) {
/* 31 */       imp.getProcessor().resetMinAndMax();
/* 32 */       imp.updateAndDraw();
/*    */     }
/*    */ 
/* 35 */     this.colorPanel = new ColorPanel(imp);
/* 36 */     if (this.colorPanel.getMapSize() != 256) {
/* 37 */       IJ.showMessage("LUT Editor", "LUT must have 256 entries");
/* 38 */       return;
/*    */     }
/* 40 */     boolean recording = Recorder.record;
/* 41 */     Recorder.record = false;
/* 42 */     int red = 0; int green = 0; int blue = 0;
/* 43 */     GenericDialog gd = new GenericDialog("LUT Editor");
/* 44 */     Panel buttonPanel = new Panel(new GridLayout(4, 1, 0, 5));
/* 45 */     this.openButton = new Button("Open...");
/* 46 */     this.openButton.addActionListener(this);
/* 47 */     buttonPanel.add(this.openButton);
/* 48 */     this.saveButton = new Button("Save...");
/* 49 */     this.saveButton.addActionListener(this);
/* 50 */     buttonPanel.add(this.saveButton);
/* 51 */     this.resizeButton = new Button("Set...");
/* 52 */     this.resizeButton.addActionListener(this);
/* 53 */     buttonPanel.add(this.resizeButton);
/* 54 */     this.invertButton = new Button("Invert...");
/* 55 */     this.invertButton.addActionListener(this);
/* 56 */     buttonPanel.add(this.invertButton);
/* 57 */     Panel panel = new Panel();
/* 58 */     panel.add(this.colorPanel);
/* 59 */     panel.add(buttonPanel);
/* 60 */     gd.addPanel(panel, 10, new Insets(10, 0, 0, 0));
/* 61 */     gd.showDialog();
/* 62 */     Recorder.record = recording;
/* 63 */     if (gd.wasCanceled()) {
/* 64 */       this.colorPanel.cancelLUT();
/* 65 */       return;
/*    */     }
/* 67 */     this.colorPanel.applyLUT();
/*    */   }
/*    */   void save() {
/*    */     try {
/* 71 */       IJ.run("LUT..."); } catch (RuntimeException e) {
/*    */     }
/*    */   }
/*    */ 
/*    */   public void actionPerformed(ActionEvent e) {
/* 76 */     Object source = e.getSource();
/* 77 */     if (source == this.openButton)
/* 78 */       this.colorPanel.open();
/* 79 */     else if (source == this.saveButton)
/* 80 */       save();
/* 81 */     else if (source == this.resizeButton)
/* 82 */       this.colorPanel.resize();
/* 83 */     else if (source == this.invertButton)
/* 84 */       this.colorPanel.invert();
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.LUT_Editor
 * JD-Core Version:    0.6.2
 */