/*     */ package leadtools.sane;
/*     */ 
/*     */ import java.awt.Point;
/*     */ import java.awt.color.ColorSpace;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.ColorModel;
/*     */ import java.awt.image.ComponentColorModel;
/*     */ import java.awt.image.DataBuffer;
/*     */ import java.awt.image.DataBufferByte;
/*     */ import java.awt.image.Raster;
/*     */ import java.awt.image.WritableRaster;
/*     */ import leadtools.RasterImage;
/*     */ 
/*     */ public abstract class SaneDevice
/*     */ {
/*     */   public abstract SaneOptionDescriptor[] getOptionDescriptors();
/*     */ 
/*     */   public abstract void open();
/*     */ 
/*     */   public abstract String getOptionValue(String paramString);
/*     */ 
/*     */   public abstract RasterImage acquireImage();
/*     */ 
/*     */   abstract byte[] acquireData(SaneParameters paramSaneParameters);
/*     */ 
/*     */   public abstract void cancel();
/*     */ 
/*     */   public abstract SaneParameters getParameters();
/*     */ 
/*     */   public abstract DeviceInfo getInfo();
/*     */ 
/*     */   public abstract void start();
/*     */ 
/*     */   public abstract SaneOptionDescriptor getOptionDescriptor(int paramInt);
/*     */ 
/*     */   public abstract int read(byte[] paramArrayOfByte, int paramInt);
/*     */ 
/*     */   public BufferedImage acquireBufferedImage()
/*     */   {
/*     */     SaneParameters a;
/*  89 */     byte[] a = acquireData(a = getParameters());
/*     */ 
/*  14 */     int a = 8;
/*     */     SaneParameters tmp15_14 = a;
/*     */     SaneParameters tmp16_15 = tmp15_14;
/*     */     SaneParameters tmp17_16 = tmp16_15;
/*     */     SaneParameters tmp17_15 = tmp15_14; int a = tmp17_16.getDepth();
/*     */ 
/*  82 */     int a = tmp17_15
/*  82 */       .getWidth();
/*     */ 
/* 183 */     int a = tmp17_16
/* 183 */       .getHeight();
/*     */ 
/* 186 */     int a = tmp17_15
/* 186 */       .getBytesPerLine();
/*     */ 
/* 167 */     SaneFrame a = tmp16_15
/* 167 */       .getFormat();
/*     */ 
/*   3 */     if (a == 1)
/*     */     {
/* 150 */       if (a == SaneFrame.GRAY)
/*     */       {
/* 113 */         BufferedImage a = new BufferedImage(a, a, 1);
/*     */         int a;
/*     */         int a;
/*  94 */         int a = a * a;
/*     */ 
/* 182 */         int a = a / a;
/*     */         int tmp106_105 = a; int a = 1 << tmp106_105 - a % tmp106_105 - 1;
/*     */ 
/*  98 */         int a = (a[(a + a)] & a) == 0 ? 16777215 : 0;
/*     */ 
/* 115 */         a.setRGB(a++, a, a);
/*     */ 
/*  22 */         a++;
/*     */ 
/* 197 */         return a;
/*     */       }
/* 112 */       BufferedImage a = new BufferedImage(a, a, 1);
/*     */ 
/* 176 */       int a = 3;
/*     */       int a;
/*     */       int a;
/*  95 */       int a = a * a;
/*     */ 
/* 152 */       int a = a / a * a;
/*     */       int tmp220_219 = a; int a = 1 << tmp220_219 - a % tmp220_219 - 1;
/*     */ 
/*  51 */       int a = (a[(a + a)] & a) != 0 ? 16711680 : 0;
/*     */ 
/*  13 */       int a = (a[(a + a + 1)] & a) != 0 ? 65280 : 0;
/*     */ 
/* 105 */       int a = (a[(a + a + 2)] & a) != 0 ? 255 : 0;
/*     */ 
/*  91 */       a++; a.setRGB(a, a, a | a | a);
/*     */ 
/* 181 */       a++;
/*     */ 
/*   1 */       return a;
/*     */     }
/*     */ 
/* 171 */     ColorSpace a = null;
/*     */ 
/*  58 */     int[] a = null;
/*     */ 
/* 107 */     int a = a / a;
/*     */ 
/*  46 */     if (a == SaneFrame.GRAY)
/*     */     {
/*  38 */       a = ColorSpace.getInstance(1003);
/*     */       int[] tmp363_361 = new int[1];
/*     */       int tmp365_364 = 0; tmp363_361[tmp365_364] = tmp365_364; a = tmp363_361;
/*     */     }
/*     */     else
/*     */     {
/* 179 */       a = ColorSpace.getInstance(1004);
/*     */       int[] tmp383_381 = new int[3];
/*     */       int tmp385_384 = 0; tmp383_381[tmp385_384] = tmp385_384;
/*     */       int[] tmp387_383 = tmp383_381;
/*     */       int tmp389_388 = 1; tmp387_383[tmp389_388] = (tmp389_388 * a);
/*     */       int[] tmp394_387 = tmp387_383;
/*     */       int tmp396_395 = 2; tmp394_387[tmp396_395] = (tmp396_395 * a); a = tmp394_387;
/*     */     }
/*     */     byte[] tmp408_407 = a;
/*     */     DataBuffer a;
/*     */     int tmp441_440 = 0; WritableRaster a = Raster.createInterleavedRaster(
/* 204 */       a = new DataBufferByte(tmp408_407, tmp408_407.length), a.getWidth(), a.getHeight(), a.getBytesPerLine(), a * a.length, a, new Point(tmp441_440, tmp441_440));
/*     */     int tmp458_457 = 0;
/*     */     int tmp459_458 = tmp458_457; ColorModel a = new ComponentColorModel(a, tmp459_458, tmp458_457, 1, tmp459_458);
/*     */ 
/* 196 */     return new BufferedImage(a, a, false, null);
/*     */   }
/*     */ 
/*     */   public abstract void setOptionValue(String paramString1, String paramString2);
/*     */ 
/*     */   public abstract void close();
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.sane.jar
 * Qualified Name:     leadtools.sane.SaneDevice
 * JD-Core Version:    0.6.2
 */