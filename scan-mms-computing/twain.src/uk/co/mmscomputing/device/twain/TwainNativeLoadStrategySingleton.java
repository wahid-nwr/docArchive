/*    */ package uk.co.mmscomputing.device.twain;
/*    */ 
/*    */ public class TwainNativeLoadStrategySingleton
/*    */ {
/*    */   private TwainINativeLoadStrategy nativeLoadStrategy;
/*    */ 
/*    */   public TwainNativeLoadStrategySingleton()
/*    */   {
/* 11 */     this.nativeLoadStrategy = new TwainDefaultNativeLoadStrategy();
/*    */   }
/*    */ 
/*    */   public static TwainNativeLoadStrategySingleton getInstance()
/*    */   {
/* 21 */     return TwainNativeLoadStrategyInstance.instance;
/*    */   }
/*    */ 
/*    */   public TwainINativeLoadStrategy getNativeLoadStrategy() {
/* 25 */     return this.nativeLoadStrategy;
/*    */   }
/*    */ 
/*    */   public void setNativeLoadStrategy(TwainINativeLoadStrategy paramTwainINativeLoadStrategy) {
/* 29 */     this.nativeLoadStrategy = paramTwainINativeLoadStrategy;
/*    */   }
/*    */ 
/*    */   private static class TwainNativeLoadStrategyInstance
/*    */   {
/* 17 */     public static TwainNativeLoadStrategySingleton instance = new TwainNativeLoadStrategySingleton();
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.device.twain.TwainNativeLoadStrategySingleton
 * JD-Core Version:    0.6.2
 */