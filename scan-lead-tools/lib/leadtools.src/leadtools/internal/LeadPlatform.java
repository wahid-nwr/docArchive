/*    */ package leadtools.internal;
/*    */ 
/*    */ import leadtools.Platform;
/*    */ 
/*    */ public class LeadPlatform
/*    */ {
/*  9 */   private static ILeadPlatformImplementation _platform = create();
/*    */ 
/*    */   private static ILeadPlatformImplementation create() {
/* 12 */     if (Platform.isAndroid()) {
/* 13 */       return createAndroidPlatform();
/*    */     }
/*    */ 
/* 16 */     return createJavaPlatform();
/*    */   }
/*    */ 
/*    */   private static ILeadPlatformImplementation createAndroidPlatform() {
/* 20 */     return new AndroidPlatform();
/*    */   }
/*    */   private static ILeadPlatformImplementation createJavaPlatform() {
/* 23 */     return new JavaPlatform();
/*    */   }
/*    */ 
/*    */   public static ILeadTaskWorker createTaskWorker(ILeadTaskWorker.Worker worker)
/*    */   {
/* 29 */     return _platform.createTaskWorker(worker);
/*    */   }
/*    */ 
/*    */   public static byte[] fromBase64String(String data) {
/* 33 */     return _platform.fromBase64String(data);
/*    */   }
/*    */ 
/*    */   public static String toBase64String(byte[] data) {
/* 37 */     return _platform.toBase64String(data);
/*    */   }
/*    */ 
/*    */   public static int parseColor(String color) {
/* 41 */     return _platform.parseColor(color);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.internal.LeadPlatform
 * JD-Core Version:    0.6.2
 */