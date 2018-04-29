/*    */ package leadtools.internal;
/*    */ 
/*    */ import java.awt.Color;
/*    */ import java.lang.reflect.Field;
/*    */ import javax.xml.bind.DatatypeConverter;
/*    */ 
/*    */ class JavaPlatform
/*    */   implements ILeadPlatformImplementation
/*    */ {
/*    */   public byte[] fromBase64String(String data)
/*    */   {
/*  8 */     return DatatypeConverter.parseBase64Binary(data);
/*    */   }
/*    */ 
/*    */   public String toBase64String(byte[] data) {
/* 12 */     return DatatypeConverter.printBase64Binary(data);
/*    */   }
/*    */ 
/*    */   public int parseColor(String color) {
/* 16 */     if (color.startsWith("#")) {
/* 17 */       return (int)Long.parseLong(color.substring(1), 16);
/*    */     }
/*    */     Color clr;
/*    */     try
/*    */     {
/* 22 */       Field field = Color.class.getField(color.toLowerCase());
/* 23 */       clr = (Color)field.get(null);
/*    */     } catch (Exception e) {
/* 25 */       clr = Color.getColor(color);
/*    */     }
/*    */ 
/* 28 */     int argb = (clr.getAlpha() & 0xFF) << 24 | clr.getRGB();
/* 29 */     return argb;
/*    */   }
/*    */ 
/*    */   public ILeadTaskWorker createTaskWorker(ILeadTaskWorker.Worker worker) {
/* 33 */     return new JavaTaskWorker(worker);
/*    */   }
/*    */ 
/*    */   class JavaTaskWorker implements ILeadTaskWorker
/*    */   {
/*    */     private ILeadTaskWorker.Worker _worker;
/*    */     private Thread _thread;
/*    */ 
/*    */     public JavaTaskWorker(ILeadTaskWorker.Worker worker)
/*    */     {
/* 44 */       this._worker = worker;
/* 45 */       this._thread = new Thread(new JavaTask());
/*    */     }
/*    */ 
/*    */     public void start()
/*    */     {
/* 50 */       this._thread.start();
/*    */     }
/*    */ 
/*    */     public boolean isFinished()
/*    */     {
/* 55 */       return !this._thread.isAlive();
/*    */     }
/*    */     class JavaTask implements Runnable {
/*    */       JavaTask() {
/*    */       }
/*    */       public void run() {
/* 61 */         JavaPlatform.JavaTaskWorker.this._worker.onWorking();
/* 62 */         JavaPlatform.JavaTaskWorker.this._worker.onCompleted();
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.internal.JavaPlatform
 * JD-Core Version:    0.6.2
 */