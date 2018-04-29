/*    */ package leadtools.internal;
/*    */ 
/*    */ import android.app.AlertDialog;
/*    */ import android.app.AlertDialog.Builder;
/*    */ import android.content.Context;
/*    */ import android.graphics.Color;
/*    */ import android.os.AsyncTask;
/*    */ import android.os.AsyncTask.Status;
/*    */ import android.os.Looper;
/*    */ import android.util.Base64;
/*    */ 
/*    */ class AndroidPlatform
/*    */   implements ILeadPlatformImplementation
/*    */ {
/*    */   public static void showMessage(Object contextObj, String message, String title)
/*    */   {
/* 13 */     boolean threadHasLooper = threadHasLooper();
/* 14 */     if (!threadHasLooper) {
/* 15 */       Looper.prepare();
/*    */     }
/* 17 */     AlertDialog.Builder msgDlgBuilder = new AlertDialog.Builder((Context)contextObj);
/*    */ 
/* 19 */     msgDlgBuilder.setMessage(message);
/* 20 */     msgDlgBuilder.setTitle(title);
/* 21 */     msgDlgBuilder.setPositiveButton("OK", null);
/* 22 */     AlertDialog msgDlg = msgDlgBuilder.create();
/* 23 */     msgDlg.show();
/*    */ 
/* 25 */     if (!threadHasLooper) {
/* 26 */       Looper.loop();
/* 27 */       Looper.myLooper().quit();
/*    */     }
/*    */   }
/*    */ 
/* 31 */   private static boolean threadHasLooper() { return Looper.myLooper() != null; }
/*    */ 
/*    */   public byte[] fromBase64String(String data)
/*    */   {
/* 35 */     return Base64.decode(data, 0);
/*    */   }
/*    */ 
/*    */   public String toBase64String(byte[] data) {
/* 39 */     return Base64.encodeToString(data, 0);
/*    */   }
/*    */ 
/*    */   public int parseColor(String color) {
/* 43 */     return Color.parseColor(color);
/*    */   }
/*    */ 
/*    */   public ILeadTaskWorker createTaskWorker(ILeadTaskWorker.Worker worker) {
/* 47 */     return new AndroidTaskWorker(worker);
/*    */   }
/*    */   class AndroidTaskWorker implements ILeadTaskWorker {
/*    */     private ILeadTaskWorker.Worker _worker;
/*    */     private AndroidTask _task;
/*    */ 
/*    */     public AndroidTaskWorker(ILeadTaskWorker.Worker worker) {
/* 55 */       this._worker = worker;
/* 56 */       this._task = new AndroidTask();
/*    */     }
/*    */ 
/*    */     public void start() {
/* 60 */       this._task.execute(new Void[0]);
/*    */     }
/*    */ 
/*    */     public boolean isFinished() {
/* 64 */       return this._task.getStatus() == AsyncTask.Status.FINISHED;
/*    */     }
/*    */ 
/*    */     private class AndroidTask extends AsyncTask<Void, Void, Void> {
/*    */       public AndroidTask() {
/*    */       }
/*    */ 
/*    */       protected void onPreExecute() {
/*    */       }
/*    */ 
/*    */       protected Void doInBackground(Void[] arg0) {
/* 75 */         AndroidPlatform.AndroidTaskWorker.this._worker.onWorking();
/* 76 */         return null;
/*    */       }
/*    */ 
/*    */       protected void onPostExecute(Void result) {
/* 80 */         AndroidPlatform.AndroidTaskWorker.this._worker.onCompleted();
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.internal.AndroidPlatform
 * JD-Core Version:    0.6.2
 */