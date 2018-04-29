/*    */ package uk.co.mmscomputing.device.twain;
/*    */ 
/*    */ import java.awt.Image;
/*    */ import java.awt.image.BufferedImage;
/*    */ import java.awt.image.DataBufferByte;
/*    */ import java.awt.image.IndexColorModel;
/*    */ import java.awt.image.Raster;
/*    */ import java.awt.image.WritableRaster;
/*    */ import uk.co.mmscomputing.imageio.bmp.BMPMetadata;
/*    */ 
/*    */ public class TwainBufferedImage extends BufferedImage
/*    */ {
/* 11 */   private static String[] propertyNames = { "iiometadata" };
/* 12 */   private BMPMetadata md = new BMPMetadata();
/*    */ 
/*    */   public TwainBufferedImage(int paramInt1, int paramInt2, int paramInt3) {
/* 15 */     super(paramInt1, paramInt2, paramInt3);
/*    */ 
/* 17 */     int i = 24;
/*    */ 
/* 19 */     this.md.setWidth(paramInt1);
/* 20 */     this.md.setHeight(paramInt2);
/* 21 */     this.md.setBitsPerPixel(i);
/* 22 */     this.md.setCompression(0);
/* 23 */     this.md.setImageSize((paramInt1 * i + 31 >> 5 << 2) * paramInt2);
/* 24 */     this.md.setXPixelsPerMeter(2953);
/* 25 */     this.md.setYPixelsPerMeter(2953);
/*    */   }
/*    */ 
/*    */   public TwainBufferedImage(int paramInt1, int paramInt2, int paramInt3, IndexColorModel paramIndexColorModel) {
/* 29 */     super(paramInt1, paramInt2, paramInt3, paramIndexColorModel);
/*    */ 
/* 31 */     this.md.setWidth(paramInt1);
/* 32 */     this.md.setHeight(paramInt2);
/*    */ 
/* 34 */     int i = 8;
/* 35 */     switch (paramIndexColorModel.getMapSize()) { case 2:
/* 36 */       i = 1; break;
/*    */     case 16:
/* 37 */       i = 4;
/*    */     }
/*    */ 
/* 40 */     this.md.setBitsPerPixel(i);
/* 41 */     this.md.setCompression(0);
/* 42 */     this.md.setXPixelsPerMeter(2953);
/* 43 */     this.md.setYPixelsPerMeter(2953);
/* 44 */     this.md.setIndexColorModel(paramIndexColorModel);
/* 45 */     this.md.setImageSize((paramInt1 * i + 31 >> 5 << 2) * paramInt2);
/*    */   }
/*    */ 
/*    */   public byte[] getBuffer() {
/* 49 */     WritableRaster localWritableRaster = getRaster();
/* 50 */     DataBufferByte localDataBufferByte = (DataBufferByte)localWritableRaster.getDataBuffer();
/* 51 */     return localDataBufferByte.getData();
/*    */   }
/*    */ 
/*    */   public void setXPixelsPerMeter(int paramInt) {
/* 55 */     this.md.setXPixelsPerMeter(paramInt);
/*    */   }
/*    */ 
/*    */   public void setYPixelsPerMeter(int paramInt) {
/* 59 */     this.md.setYPixelsPerMeter(paramInt);
/*    */   }
/*    */ 
/*    */   public void setPixelsPerMeter(int paramInt1, int paramInt2) {
/* 63 */     setXPixelsPerMeter(paramInt1);
/* 64 */     setYPixelsPerMeter(paramInt2);
/*    */   }
/*    */ 
/*    */   public Object getProperty(String paramString) {
/* 68 */     if (paramString.equals("iiometadata")) return this.md;
/* 69 */     if (paramString.equals("resolution")) {
/* 70 */       return "" + this.md.getXDotsPerInch();
/*    */     }
/* 72 */     return Image.UndefinedProperty;
/*    */   }
/*    */ 
/*    */   public String[] getPropertyNames() {
/* 76 */     return propertyNames;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.device.twain.TwainBufferedImage
 * JD-Core Version:    0.6.2
 */