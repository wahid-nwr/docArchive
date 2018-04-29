/*    */ package leadtools.codecs;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public enum CodecsOverlayCallbackMode
/*    */ {
/*  5 */   LOAD_CALL(1), 
/*  6 */   CALL_LOAD(2), 
/*  7 */   CALL_ONLY(3), 
/*  8 */   LOAD_ONLY(4);
/*    */ 
/*    */   private int intValue;
/*    */   private static HashMap<Integer, CodecsOverlayCallbackMode> mappings;
/*    */ 
/* 14 */   private static HashMap<Integer, CodecsOverlayCallbackMode> getMappings() { if (mappings == null)
/*    */     {
/* 16 */       synchronized (CodecsOverlayCallbackMode.class)
/*    */       {
/* 18 */         if (mappings == null)
/*    */         {
/* 20 */           mappings = new HashMap();
/*    */         }
/*    */       }
/*    */     }
/* 24 */     return mappings;
/*    */   }
/*    */ 
/*    */   private CodecsOverlayCallbackMode(int value)
/*    */   {
/* 29 */     this.intValue = value;
/* 30 */     getMappings().put(Integer.valueOf(value), this);
/*    */   }
/*    */ 
/*    */   public int getValue()
/*    */   {
/* 35 */     return this.intValue;
/*    */   }
/*    */ 
/*    */   public static CodecsOverlayCallbackMode forValue(int value)
/*    */   {
/* 40 */     return (CodecsOverlayCallbackMode)getMappings().get(Integer.valueOf(value));
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsOverlayCallbackMode
 * JD-Core Version:    0.6.2
 */