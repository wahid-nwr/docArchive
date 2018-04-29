/*     */ package leadtools.sane;
/*     */ 
/*     */ public class SaneOptionDescriptor
/*     */ {
/*     */   private String C;
/*     */   private int h;
/*     */   private SaneUnit k;
/*     */   private String l;
/*     */   private int a;
/*     */   private SaneConstraint L;
/*     */   private String D;
/*     */   private SaneValueType I;
/*     */ 
/*     */   public int getSize()
/*     */   {
/* 182 */     return this.h;
/*     */   }
/*     */ 
/*     */   SaneOptionDescriptor(String name, String title, String desc, int type, int unit, int size, int cap, SaneConstraint constraint)
/*     */   {
/*  62 */     this(name, title, desc, SaneValueType.forValue(type), SaneUnit.forValue(unit), size, cap, constraint);
/*     */   }
/*     */ 
/*     */   public SaneConstraint getConstraint()
/*     */   {
/* 192 */     return this.L;
/*     */   }
/*     */ 
/*     */   public SaneUnit getUnit()
/*     */   {
/* 113 */     return this.k;
/*     */   }
/*     */ 
/*     */   SaneOptionDescriptor(String arg1, String arg2, String arg3, SaneValueType arg4, SaneUnit arg5, int arg6, int arg7, SaneConstraint constraint)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload 8
/*     */     //   2: aload_0
/*     */     //   3: dup_x1
/*     */     //   4: iload 7
/*     */     //   6: iload 6
/*     */     //   8: aload_0
/*     */     //   9: dup_x1
/*     */     //   10: aload 5
/*     */     //   12: aload 4
/*     */     //   14: aload_0
/*     */     //   15: dup_x1
/*     */     //   16: aload_3
/*     */     //   17: aload_2
/*     */     //   18: aload_0
/*     */     //   19: dup_x1
/*     */     //   20: aload_1
/*     */     //   21: aload_0
/*     */     //   22: invokespecial 4	java/lang/Object:<init>	()V
/*     */     //   25: putfield 5	leadtools/sane/SaneOptionDescriptor:l	Ljava/lang/String;
/*     */     //   28: putfield 6	leadtools/sane/SaneOptionDescriptor:C	Ljava/lang/String;
/*     */     //   31: putfield 7	leadtools/sane/SaneOptionDescriptor:D	Ljava/lang/String;
/*     */     //   34: putfield 8	leadtools/sane/SaneOptionDescriptor:I	Lleadtools/sane/SaneValueType;
/*     */     //   37: putfield 9	leadtools/sane/SaneOptionDescriptor:k	Lleadtools/sane/SaneUnit;
/*     */     //   40: putfield 10	leadtools/sane/SaneOptionDescriptor:h	I
/*     */     //   43: putfield 11	leadtools/sane/SaneOptionDescriptor:a	I
/*     */     //   46: putfield 12	leadtools/sane/SaneOptionDescriptor:L	Lleadtools/sane/SaneConstraint;
/*     */     //   49: return
/*     */   }
/*     */ 
/*     */   public String getDesc()
/*     */   {
/* 102 */     return this.D;
/*     */   }
/*     */ 
/*     */   public String getTitle()
/*     */   {
/*  54 */     return this.C;
/*     */   }
/*     */ 
/*     */   public int getCap()
/*     */   {
/* 115 */     return this.a;
/*     */   }
/*     */ 
/*     */   public SaneValueType getType()
/*     */   {
/* 186 */     return this.I;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  96 */     return this.l;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.sane.jar
 * Qualified Name:     leadtools.sane.SaneOptionDescriptor
 * JD-Core Version:    0.6.2
 */