/*    */ package leadtools.imageprocessing;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public enum RotateCommandFlags
/*    */ {
/*  8 */   NONE(0), 
/*  9 */   RESIZE(1), 
/* 10 */   RESAMPLE(2), 
/* 11 */   BICUBIC(4);
/*    */ 
/*    */   private int intValue;
/*    */   private static HashMap<Integer, RotateCommandFlags> mappings;
/*    */ 
/*    */   private static HashMap<Integer, RotateCommandFlags> getMappings() {
/* 18 */     if (mappings == null)
/*    */     {
/* 20 */       synchronized (RotateCommandFlags.class)
/*    */       {
/* 22 */         if (mappings == null)
/*    */         {
/* 24 */           mappings = new HashMap();
/*    */         }
/*    */       }
/*    */     }
/* 28 */     return mappings;
/*    */   }
/*    */ 
/*    */   private RotateCommandFlags(int value)
/*    */   {
/* 33 */     this.intValue = value;
/* 34 */     getMappings().put(Integer.valueOf(value), this);
/*    */   }
/*    */ 
/*    */   public int getValue()
/*    */   {
/* 39 */     return this.intValue;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.imageprocessing.RotateCommandFlags
 * JD-Core Version:    0.6.2
 */