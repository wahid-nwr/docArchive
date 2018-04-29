/*    */ package leadtools;
/*    */ 
/*    */ import java.util.concurrent.ExecutorService;
/*    */ 
/*    */ public class RasterDefaults
/*    */ {
/*    */   private static ExecutorService _executorService;
/*    */ 
/*    */   public static void setResourceDirectory(LEADResourceDirectory resource, String directory)
/*    */   {
/*  7 */     ltkrn.SetResourceDirectory(resource.getValue(), directory);
/*    */   }
/*    */ 
/*    */   public static String getResourceDirectory(LEADResourceDirectory resource) {
/* 11 */     return ltkrn.GetResourceDirectory(resource.getValue());
/*    */   }
/*    */ 
/*    */   public static ExecutorService getExecutorService()
/*    */   {
/* 16 */     return _executorService;
/*    */   }
/*    */ 
/*    */   public static void setExecutorService(ExecutorService service) {
/* 20 */     _executorService = service;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.RasterDefaults
 * JD-Core Version:    0.6.2
 */