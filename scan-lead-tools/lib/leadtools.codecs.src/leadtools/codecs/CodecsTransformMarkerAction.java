/*    */ package leadtools.codecs;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public enum CodecsTransformMarkerAction
/*    */ {
/*  5 */   DEFAULT(0), 
/*  6 */   IGNORE(1), 
/*  7 */   ABORT(2);
/*    */ 
/*    */   private int intValue;
/*    */   private static HashMap<Integer, CodecsTransformMarkerAction> mappings;
/*    */ 
/* 13 */   private static HashMap<Integer, CodecsTransformMarkerAction> getMappings() { if (mappings == null)
/*    */     {
/* 15 */       synchronized (CodecsTransformMarkerAction.class)
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
/*    */   private CodecsTransformMarkerAction(int value)
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
/*    */   public static CodecsTransformMarkerAction forValue(int value)
/*    */   {
/* 39 */     return (CodecsTransformMarkerAction)getMappings().get(Integer.valueOf(value));
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsTransformMarkerAction
 * JD-Core Version:    0.6.2
 */