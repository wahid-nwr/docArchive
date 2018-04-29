/*     */ package leadtools.sane;
/*     */ 
/*     */ import leadtools.InvalidOperationException;
/*     */ 
/*     */ public abstract class SaneSession
/*     */ {
/*     */   private static ISaneAuthorizationCallback I;
/*     */ 
/*     */   static AuthorizationInfo invokeAuthorizationCallback(String resource)
/*     */   {
/*  32 */     if (I == null)
/*     */     {
/*  63 */       throw new InvalidOperationException("AuthorizationCallback is null");
/*     */     }
/*     */ 
/* 199 */     return I.onAuthorization(resource);
/*     */   }
/*     */ 
/*     */   public abstract SaneDevice getDevice(String paramString);
/*     */ 
/*     */   public abstract void start();
/*     */ 
/*     */   public static ISaneAuthorizationCallback getAuthorizationCallback()
/*     */   {
/* 193 */     return I;
/*     */   }
/*     */ 
/*     */   public static void setAuthorizationCallback(ISaneAuthorizationCallback callback)
/*     */   {
/* 133 */     I = callback;
/*     */   }
/*     */ 
/*     */   public abstract void stop();
/*     */ 
/*     */   public abstract SaneDevice[] getDevices();
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.sane.jar
 * Qualified Name:     leadtools.sane.SaneSession
 * JD-Core Version:    0.6.2
 */