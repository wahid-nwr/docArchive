/*    */ package ij.process;
/*    */ 
/*    */ import java.awt.Color;
/*    */ import java.awt.image.IndexColorModel;
/*    */ 
/*    */ public class LUT extends IndexColorModel
/*    */   implements Cloneable
/*    */ {
/*    */   public double min;
/*    */   public double max;
/*    */ 
/*    */   public LUT(byte[] r, byte[] g, byte[] b)
/*    */   {
/* 11 */     this(8, 256, r, g, b);
/*    */   }
/*    */ 
/*    */   public LUT(int bits, int size, byte[] r, byte[] g, byte[] b) {
/* 15 */     super(bits, size, r, g, b);
/*    */   }
/*    */ 
/*    */   public LUT(IndexColorModel cm, double min, double max) {
/* 19 */     super(8, cm.getMapSize(), getReds(cm), getGreens(cm), getBlues(cm));
/* 20 */     this.min = min;
/* 21 */     this.max = max;
/*    */   }
/*    */ 
/*    */   static byte[] getReds(IndexColorModel cm) {
/* 25 */     byte[] reds = new byte[256]; cm.getReds(reds); return reds;
/*    */   }
/*    */ 
/*    */   static byte[] getGreens(IndexColorModel cm) {
/* 29 */     byte[] greens = new byte[256]; cm.getGreens(greens); return greens;
/*    */   }
/*    */ 
/*    */   static byte[] getBlues(IndexColorModel cm) {
/* 33 */     byte[] blues = new byte[256]; cm.getBlues(blues); return blues;
/*    */   }
/*    */ 
/*    */   public byte[] getBytes() {
/* 37 */     int size = getMapSize();
/* 38 */     if (size != 256) return null;
/* 39 */     byte[] bytes = new byte[768];
/* 40 */     for (int i = 0; i < 256; i++) bytes[i] = ((byte)getRed(i));
/* 41 */     for (int i = 0; i < 256; i++) bytes[(256 + i)] = ((byte)getGreen(i));
/* 42 */     for (int i = 0; i < 256; i++) bytes[(512 + i)] = ((byte)getBlue(i));
/* 43 */     return bytes;
/*    */   }
/*    */ 
/*    */   public LUT createInvertedLut() {
/* 47 */     int mapSize = getMapSize();
/* 48 */     byte[] reds = new byte[mapSize];
/* 49 */     byte[] greens = new byte[mapSize];
/* 50 */     byte[] blues = new byte[mapSize];
/* 51 */     byte[] reds2 = new byte[mapSize];
/* 52 */     byte[] greens2 = new byte[mapSize];
/* 53 */     byte[] blues2 = new byte[mapSize];
/* 54 */     getReds(reds);
/* 55 */     getGreens(greens);
/* 56 */     getBlues(blues);
/* 57 */     for (int i = 0; i < mapSize; i++) {
/* 58 */       reds2[i] = ((byte)(reds[(mapSize - i - 1)] & 0xFF));
/* 59 */       greens2[i] = ((byte)(greens[(mapSize - i - 1)] & 0xFF));
/* 60 */       blues2[i] = ((byte)(blues[(mapSize - i - 1)] & 0xFF));
/*    */     }
/* 62 */     return new LUT(8, mapSize, reds2, greens2, blues2);
/*    */   }
/*    */ 
/*    */   public static LUT createLutFromColor(Color color)
/*    */   {
/* 67 */     byte[] rLut = new byte[256];
/* 68 */     byte[] gLut = new byte[256];
/* 69 */     byte[] bLut = new byte[256];
/* 70 */     int red = color.getRed();
/* 71 */     int green = color.getGreen();
/* 72 */     int blue = color.getBlue();
/* 73 */     double rIncr = red / 255.0D;
/* 74 */     double gIncr = green / 255.0D;
/* 75 */     double bIncr = blue / 255.0D;
/* 76 */     for (int i = 0; i < 256; i++) {
/* 77 */       rLut[i] = ((byte)(int)(i * rIncr));
/* 78 */       gLut[i] = ((byte)(int)(i * gIncr));
/* 79 */       bLut[i] = ((byte)(int)(i * bIncr));
/*    */     }
/* 81 */     return new LUT(rLut, gLut, bLut);
/*    */   }
/*    */   public synchronized Object clone() {
/*    */     try {
/* 85 */       return super.clone(); } catch (CloneNotSupportedException e) {
/* 86 */     }return null;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.process.LUT
 * JD-Core Version:    0.6.2
 */