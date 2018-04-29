/*     */ package leadtools.sane;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ 
/*     */ public enum SaneFrame
/*     */ {
/*     */   private static HashMap<Integer, SaneFrame> L;
/*     */   private int I;
/*     */ 
/*     */   public int getValue()
/*     */   {
/*  23 */     return this.I;
/*     */   }
/*     */ 
/*     */   private static HashMap<Integer, SaneFrame> j()
/*     */   {
/* 135 */     if (L == null)
/*     */     {
/* 155 */       synchronized (SaneFrame.class)
/*     */       {
/* 193 */         if (L == null) {
/*  37 */           L = new HashMap();
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  63 */     return L;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*     */     int tmp7_6 = 0; GRAY = new SaneFrame("GRAY", tmp7_6, tmp7_6);
/*     */     int tmp21_20 = 1; RGB = new SaneFrame("RGB", tmp21_20, tmp21_20);
/*     */     int tmp35_34 = 2; RED = new SaneFrame("RED", tmp35_34, tmp35_34);
/*     */     int tmp49_48 = 3; GREEN = new SaneFrame("GREEN", tmp49_48, tmp49_48);
/*     */     int tmp63_62 = 4;
/*     */   }
/*     */ 
/*     */   private SaneFrame(int value)
/*     */   {
/* 114 */     this.I = value;
/*     */ 
/*  56 */     j().put(Integer.valueOf(value), this);
/*     */   }
/*     */ 
/*     */   public static SaneFrame forValue(int value)
/*     */   {
/*  33 */     return (SaneFrame)j().get(Integer.valueOf(value));
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.sane.jar
 * Qualified Name:     leadtools.sane.SaneFrame
 * JD-Core Version:    0.6.2
 */