/*    */ package leadtools.imageprocessing;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public enum ScrambleCommandFlags
/*    */ {
/*  8 */   NONE(0), 
/*  9 */   ENCRYPT(1), 
/* 10 */   DECRYPT(2), 
/* 11 */   INTERSECT(4), 
/* 12 */   RESERVED3(8), 
/* 13 */   RESERVED4(16), 
/* 14 */   RESERVED5(32), 
/* 15 */   RESERVED6(64), 
/* 16 */   RESERVED7(128), 
/* 17 */   RESERVED8(256), 
/* 18 */   RESERVED9(512);
/*    */ 
/*    */   private int intValue;
/*    */   private static HashMap<Integer, ScrambleCommandFlags> mappings;
/*    */ 
/* 24 */   private static HashMap<Integer, ScrambleCommandFlags> getMappings() { if (mappings == null)
/*    */     {
/* 26 */       synchronized (ScrambleCommandFlags.class)
/*    */       {
/* 28 */         if (mappings == null)
/*    */         {
/* 30 */           mappings = new HashMap();
/*    */         }
/*    */       }
/*    */     }
/* 34 */     return mappings;
/*    */   }
/*    */ 
/*    */   private ScrambleCommandFlags(int value)
/*    */   {
/* 39 */     this.intValue = value;
/* 40 */     getMappings().put(Integer.valueOf(value), this);
/*    */   }
/*    */ 
/*    */   public int getValue()
/*    */   {
/* 45 */     return this.intValue;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.imageprocessing.ScrambleCommandFlags
 * JD-Core Version:    0.6.2
 */