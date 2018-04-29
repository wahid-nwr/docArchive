/*    */ package ij.plugin.frame;
/*    */ 
/*    */ import ij.IJ;
/*    */ import ij.ImagePlus;
/*    */ import ij.WindowManager;
/*    */ import ij.gui.GUI;
/*    */ import ij.gui.Roi;
/*    */ import ij.plugin.PlugIn;
/*    */ import java.awt.Choice;
/*    */ import java.awt.FlowLayout;
/*    */ import java.awt.Frame;
/*    */ import java.awt.Label;
/*    */ import java.awt.Panel;
/*    */ import java.awt.event.ItemEvent;
/*    */ import java.awt.event.ItemListener;
/*    */ 
/*    */ public class PasteController extends PlugInFrame
/*    */   implements PlugIn, ItemListener
/*    */ {
/*    */   private Panel panel;
/*    */   private Choice pasteMode;
/*    */   private static Frame instance;
/*    */ 
/*    */   public PasteController()
/*    */   {
/* 17 */     super("Paste Control");
/* 18 */     if (instance != null) {
/* 19 */       WindowManager.toFront(instance);
/* 20 */       return;
/*    */     }
/* 22 */     WindowManager.addWindow(this);
/* 23 */     instance = this;
/* 24 */     IJ.register(PasteController.class);
/* 25 */     setLayout(new FlowLayout(1, 2, 5));
/*    */ 
/* 27 */     add(new Label(" Transfer Mode:"));
/* 28 */     this.pasteMode = new Choice();
/* 29 */     this.pasteMode.addItem("Copy");
/* 30 */     this.pasteMode.addItem("Blend");
/* 31 */     this.pasteMode.addItem("Difference");
/* 32 */     this.pasteMode.addItem("Transparent-white");
/* 33 */     this.pasteMode.addItem("Transparent-zero");
/* 34 */     this.pasteMode.addItem("AND");
/* 35 */     this.pasteMode.addItem("OR");
/* 36 */     this.pasteMode.addItem("XOR");
/* 37 */     this.pasteMode.addItem("Add");
/* 38 */     this.pasteMode.addItem("Subtract");
/* 39 */     this.pasteMode.addItem("Multiply");
/* 40 */     this.pasteMode.addItem("Divide");
/* 41 */     this.pasteMode.addItem("Min");
/* 42 */     this.pasteMode.addItem("Max");
/* 43 */     this.pasteMode.select("Copy");
/* 44 */     this.pasteMode.addItemListener(this);
/* 45 */     add(this.pasteMode);
/* 46 */     Roi.setPasteMode(0);
/*    */ 
/* 48 */     pack();
/* 49 */     GUI.center(this);
/* 50 */     setResizable(false);
/* 51 */     show();
/*    */   }
/*    */ 
/*    */   public void itemStateChanged(ItemEvent e) {
/* 55 */     int index = this.pasteMode.getSelectedIndex();
/* 56 */     int mode = 0;
/* 57 */     switch (index) { case 0:
/* 58 */       mode = 0; break;
/*    */     case 1:
/* 59 */       mode = 7; break;
/*    */     case 2:
/* 60 */       mode = 8; break;
/*    */     case 3:
/* 61 */       mode = 2; break;
/*    */     case 4:
/* 62 */       mode = 14; break;
/*    */     case 5:
/* 63 */       mode = 9; break;
/*    */     case 6:
/* 64 */       mode = 10; break;
/*    */     case 7:
/* 65 */       mode = 11; break;
/*    */     case 8:
/* 66 */       mode = 3; break;
/*    */     case 9:
/* 67 */       mode = 4; break;
/*    */     case 10:
/* 68 */       mode = 5; break;
/*    */     case 11:
/* 69 */       mode = 6; break;
/*    */     case 12:
/* 70 */       mode = 12; break;
/*    */     case 13:
/* 71 */       mode = 13;
/*    */     }
/* 73 */     Roi.setPasteMode(mode);
/* 74 */     if (Recorder.record)
/* 75 */       Recorder.record("setPasteMode", this.pasteMode.getSelectedItem());
/* 76 */     ImagePlus imp = WindowManager.getCurrentImage();
/*    */   }
/*    */ 
/*    */   public void close() {
/* 80 */     super.close();
/* 81 */     instance = null;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.frame.PasteController
 * JD-Core Version:    0.6.2
 */