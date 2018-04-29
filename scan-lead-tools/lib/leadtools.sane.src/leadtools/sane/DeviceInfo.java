/*     */ package leadtools.sane;
/*     */ 
/*     */ public class DeviceInfo
/*     */ {
/*     */   private String a;
/*     */   private String L;
/*     */   private String D;
/*     */   private String I;
/*     */ 
/*     */   DeviceInfo(String arg1, String arg2, String arg3, String type)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload 4
/*     */     //   2: aload_0
/*     */     //   3: dup_x1
/*     */     //   4: aload_3
/*     */     //   5: aload_2
/*     */     //   6: aload_0
/*     */     //   7: dup_x1
/*     */     //   8: aload_1
/*     */     //   9: aload_0
/*     */     //   10: invokespecial 1	java/lang/Object:<init>	()V
/*     */     //   13: putfield 2	leadtools/sane/DeviceInfo:D	Ljava/lang/String;
/*     */     //   16: putfield 3	leadtools/sane/DeviceInfo:I	Ljava/lang/String;
/*     */     //   19: putfield 4	leadtools/sane/DeviceInfo:L	Ljava/lang/String;
/*     */     //   22: putfield 5	leadtools/sane/DeviceInfo:a	Ljava/lang/String;
/*     */     //   25: return
/*     */   }
/*     */ 
/*     */   public String getType()
/*     */   {
/*  23 */     return this.a;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 100 */     return this.D;
/*     */   }
/*     */ 
/*     */   public String getVendor()
/*     */   {
/*  96 */     return this.I;
/*     */   }
/*     */ 
/*     */   public String getModel()
/*     */   {
/*  56 */     return this.L;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.sane.jar
 * Qualified Name:     leadtools.sane.DeviceInfo
 * JD-Core Version:    0.6.2
 */