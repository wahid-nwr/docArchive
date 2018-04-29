/*    */ package leadtools.codecs;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public enum CodecsExtensionListFlags
/*    */ {
/*  5 */   NONE(0), 
/*  6 */   STAMP(1), 
/*  7 */   AUDIO(2);
/*    */ 
/*    */   private int intValue;
/*    */   private static HashMap<Integer, CodecsExtensionListFlags> mappings;
/*    */ 
/* 13 */   private static HashMap<Integer, CodecsExtensionListFlags> getMappings() { if (mappings == null)
/*    */     {
/* 15 */       synchronized (CodecsExtensionListFlags.class)
/*    */       {
/* 17 */         if (mappings == null)
/*    */         {
/* 19 */           mappings = new HashMap();
/*    */         }
/*    */       }
/*    */     }
/* 23 */     return mappings;
/*    */   }
/*    */ 
/*    */   private CodecsExtensionListFlags(int value)
/*    */   {
/* 28 */     this.intValue = value;
/* 29 */     getMappings().put(Integer.valueOf(value), this);
/*    */   }
/*    */ 
/*    */   public int getValue()
/*    */   {
/* 34 */     return this.intValue;
/*    */   }
/*    */ 
/*    */   public static CodecsExtensionListFlags forValue(int value)
/*    */   {
/* 39 */     return (CodecsExtensionListFlags)getMappings().get(Integer.valueOf(value));
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsExtensionListFlags
 * JD-Core Version:    0.6.2
 */