/*     */ package uk.co.mmscomputing.device.twain;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class TwainExtImageInfo
/*     */   implements TwainConstants
/*     */ {
/*     */   TwainSource source;
/*     */   byte[] buf;
/*     */   int[] attributes;
/*     */   Vector extInfos;
/*     */ 
/*     */   public TwainExtImageInfo(TwainSource paramTwainSource, int paramInt)
/*     */   {
/*  30 */     this.source = paramTwainSource;
/*  31 */     this.attributes = new int[1];
/*  32 */     this.attributes[0] = paramInt;
/*  33 */     initBuf();
/*     */   }
/*     */ 
/*     */   public TwainExtImageInfo(TwainSource paramTwainSource, int[] paramArrayOfInt) {
/*  37 */     this.source = paramTwainSource;
/*  38 */     this.attributes = paramArrayOfInt;
/*  39 */     initBuf();
/*     */   }
/*     */ 
/*     */   private void initBuf() {
/*  43 */     int i = this.attributes.length;
/*     */ 
/*  45 */     this.buf = new byte[4 + i * 12];
/*     */ 
/*  47 */     jtwain.setINT32(this.buf, 0, this.attributes.length);
/*  48 */     int j = 0; for (int k = 4; j < i; j++) {
/*  49 */       jtwain.setINT16(this.buf, k, this.attributes[j]); k += 2;
/*  50 */       jtwain.setINT16(this.buf, k, 0); k += 2;
/*  51 */       jtwain.setINT16(this.buf, k, 0); k += 2;
/*  52 */       jtwain.setINT16(this.buf, k, 0); k += 2;
/*  53 */       jtwain.setINT32(this.buf, k, 0); k += 4;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void get() throws TwainIOException {
/*  58 */     this.source.call(2, 267, 1, this.buf);
/*     */   }
/*     */   public Object getInfo() throws TwainIOException {
/*  61 */     return getInfo(this.attributes[0]);
/*     */   }
/*     */   public Object getInfo(int paramInt) throws TwainIOException {
/*  64 */     int i = 0;
/*  65 */     int j = this.attributes.length;
/*  66 */     while ((i < j) && 
/*  67 */       (this.attributes[i] != paramInt)) {
/*  66 */       i++;
/*     */     }
/*     */ 
/*  69 */     if (i == j) return null;
/*  70 */     int k = 4 + i * 12;
/*  71 */     int m = jtwain.getINT16(this.buf, k + 6);
/*  72 */     if (m != 0) throw new TwainFailureException(m);
/*  73 */     int n = jtwain.getINT16(this.buf, k + 2);
/*  74 */     int i1 = jtwain.getINT16(this.buf, k + 4);
/*  75 */     switch (n) {
/*     */     case 5:
/*  77 */       if (i1 == 1) {
/*  78 */         return new Integer(jtwain.getINT16(this.buf, k + 8));
/*     */       }
/*     */ 
/*     */       break;
/*     */     }
/*     */ 
/*  84 */     System.err.println(getClass().getName() + ".getInfo:\n\tDon't support type = " + n + " yet.");
/*     */ 
/*  86 */     return null;
/*     */   }
/*     */ 
/*     */   public String toString() {
/*  90 */     String str = "TwainExtImageInfo\n";
/*     */ 
/*  92 */     int i = this.attributes.length;
/*  93 */     int j = 0; for (int k = 4; j < i; j++) {
/*  94 */       str = str + "InfoID = 0x" + Integer.toHexString(jtwain.getINT16(this.buf, k)) + "\n"; k += 2;
/*  95 */       str = str + "ItemType = " + jtwain.getINT16(this.buf, k) + "\n"; k += 2;
/*  96 */       str = str + "NumItems = " + jtwain.getINT16(this.buf, k) + "\n"; k += 2;
/*  97 */       str = str + "CondCode = " + jtwain.getINT16(this.buf, k) + "\n"; k += 2;
/*  98 */       str = str + "Item = " + jtwain.getINT32(this.buf, k) + "\n"; k += 4;
/*     */     }
/* 100 */     return str;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.device.twain.TwainExtImageInfo
 * JD-Core Version:    0.6.2
 */