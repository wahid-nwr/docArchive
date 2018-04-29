/*    */ package ij.plugin.frame;
/*    */ 
/*    */ import ij.IJ;
/*    */ import ij.Prefs;
/*    */ import ij.WindowManager;
/*    */ import ij.gui.GUI;
/*    */ import java.awt.BorderLayout;
/*    */ import java.awt.Canvas;
/*    */ import java.awt.Panel;
/*    */ import java.awt.Point;
/*    */ 
/*    */ public class ColorPicker extends PlugInFrame
/*    */ {
/*    */   static final String LOC_KEY = "cp.loc";
/*    */   static ColorPicker instance;
/*    */ 
/*    */   public ColorPicker()
/*    */   {
/* 16 */     super("CP");
/* 17 */     if (instance != null) {
/* 18 */       WindowManager.toFront(instance);
/* 19 */       return;
/*    */     }
/* 21 */     instance = this;
/* 22 */     WindowManager.addWindow(this);
/* 23 */     int colorWidth = 22;
/* 24 */     int colorHeight = 16;
/* 25 */     int columns = 5;
/* 26 */     int rows = 20;
/* 27 */     int width = columns * colorWidth;
/* 28 */     int height = rows * colorHeight;
/* 29 */     addKeyListener(IJ.getInstance());
/* 30 */     setLayout(new BorderLayout());
/* 31 */     ColorGenerator cg = new ColorGenerator(width, height, new int[width * height]);
/* 32 */     cg.drawColors(colorWidth, colorHeight, columns, rows);
/* 33 */     Canvas colorCanvas = new ColorCanvas(width, height, this, cg);
/* 34 */     Panel panel = new Panel();
/* 35 */     panel.add(colorCanvas);
/* 36 */     add(panel);
/* 37 */     setResizable(false);
/* 38 */     pack();
/* 39 */     Point loc = Prefs.getLocation("cp.loc");
/* 40 */     if (loc != null)
/* 41 */       setLocation(loc);
/*    */     else
/* 43 */       GUI.center(this);
/* 44 */     show();
/*    */   }
/*    */ 
/*    */   public void close() {
/* 48 */     super.close();
/* 49 */     instance = null;
/* 50 */     Prefs.saveLocation("cp.loc", getLocation());
/* 51 */     IJ.notifyEventListeners(2);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.frame.ColorPicker
 * JD-Core Version:    0.6.2
 */