/*    */ package leadtools.codecs;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public enum CodecsLoadImageFlags
/*    */ {
/*  5 */   NONE(0), 
/*  6 */   FIRST_PASS(1), 
/*  7 */   LAST_PASS(2), 
/*  8 */   FIRST_ROW(4), 
/*  9 */   LAST_ROW(8), 
/* 10 */   COMPRESSED(16);
/*    */ 
/*    */   private int intValue;
/*    */   private static HashMap<Integer, CodecsLoadImageFlags> mappings;
/*    */ 
/* 16 */   private static HashMap<Integer, CodecsLoadImageFlags> getMappings() { if (mappings == null)
/*    */     {
/* 18 */       synchronized (CodecsLoadImageFlags.class)
/*    */       {
/* 20 */         if (mappings == null)
/*    */         {
/* 22 */           mappings = new HashMap();
/*    */         }
/*    */       }
/*    */     }
/* 26 */     return mappings;
/*    */   }
/*    */ 
/*    */   private CodecsLoadImageFlags(int value)
/*    */   {
/* 31 */     this.intValue = value;
/* 32 */     getMappings().put(Integer.valueOf(value), this);
/*    */   }
/*    */ 
/*    */   public int getValue()
/*    */   {
/* 37 */     return this.intValue;
/*    */   }
/*    */ 
/*    */   public static CodecsLoadImageFlags forValue(int value)
/*    */   {
/* 42 */     return (CodecsLoadImageFlags)getMappings().get(Integer.valueOf(value));
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsLoadImageFlags
 * JD-Core Version:    0.6.2
 */