/*    */ package ij.io;
/*    */ 
/*    */ import ij.IJ;
/*    */ import ij.measure.Calibration;
/*    */ import ij.process.ByteProcessor;
/*    */ import ij.process.ImageProcessor;
/*    */ import ij.process.ShortProcessor;
/*    */ import java.io.DataOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.PrintWriter;
/*    */ 
/*    */ public class TextEncoder
/*    */ {
/*    */   private ImageProcessor ip;
/*    */   private Calibration cal;
/*    */   private int precision;
/*    */ 
/*    */   public TextEncoder(ImageProcessor ip, Calibration cal, int precision)
/*    */   {
/* 16 */     this.ip = ip;
/* 17 */     this.cal = cal;
/* 18 */     this.precision = precision;
/*    */   }
/*    */ 
/*    */   public void write(DataOutputStream out) throws IOException
/*    */   {
/* 23 */     PrintWriter pw = new PrintWriter(out);
/* 24 */     boolean calibrated = (this.cal != null) && (this.cal.calibrated());
/* 25 */     if (calibrated)
/* 26 */       this.ip.setCalibrationTable(this.cal.getCTable());
/*    */     else
/* 28 */       this.ip.setCalibrationTable(null);
/* 29 */     boolean intData = (!calibrated) && (((this.ip instanceof ByteProcessor)) || ((this.ip instanceof ShortProcessor)));
/* 30 */     int width = this.ip.getWidth();
/* 31 */     int height = this.ip.getHeight();
/* 32 */     int inc = height / 20;
/* 33 */     if (inc < 1) inc = 1;
/*    */ 
/* 36 */     for (int y = 0; y < height; y++) {
/* 37 */       for (int x = 0; x < width; x++) {
/* 38 */         double value = this.ip.getPixelValue(x, y);
/* 39 */         if (intData)
/* 40 */           pw.print((int)value);
/*    */         else
/* 42 */           pw.print(IJ.d2s(value, this.precision));
/* 43 */         if (x != width - 1)
/* 44 */           pw.print("\t");
/*    */       }
/* 46 */       pw.println();
/* 47 */       if (y % inc == 0) IJ.showProgress(y / height);
/*    */     }
/* 49 */     pw.close();
/* 50 */     IJ.showProgress(1.0D);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.io.TextEncoder
 * JD-Core Version:    0.6.2
 */