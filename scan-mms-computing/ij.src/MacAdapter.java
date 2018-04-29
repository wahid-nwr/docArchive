/*    */ import com.apple.eawt.Application;
/*    */ import com.apple.eawt.ApplicationEvent;
/*    */ import com.apple.eawt.ApplicationListener;
/*    */ import ij.Executer;
/*    */ import ij.IJ;
/*    */ import ij.io.Opener;
/*    */ import ij.plugin.PlugIn;
/*    */ import java.util.Vector;
/*    */ 
/*    */ public class MacAdapter
/*    */   implements PlugIn, ApplicationListener, Runnable
/*    */ {
/* 11 */   static Vector paths = new Vector();
/*    */ 
/*    */   public void run(String paramString) {
/* 14 */     Application localApplication = new Application();
/* 15 */     localApplication.setEnabledPreferencesMenu(true);
/* 16 */     localApplication.addApplicationListener(this);
/*    */   }
/*    */ 
/*    */   public void handleAbout(ApplicationEvent paramApplicationEvent) {
/* 20 */     IJ.run("About ImageJ...");
/* 21 */     paramApplicationEvent.setHandled(true);
/*    */   }
/*    */ 
/*    */   public void handleOpenFile(ApplicationEvent paramApplicationEvent) {
/* 25 */     paths.add(paramApplicationEvent.getFilename());
/* 26 */     Thread localThread = new Thread(this, "Open");
/* 27 */     localThread.setPriority(localThread.getPriority() - 1);
/* 28 */     localThread.start();
/*    */   }
/*    */ 
/*    */   public void handlePreferences(ApplicationEvent paramApplicationEvent) {
/* 32 */     IJ.error("The ImageJ preferences are in the Edit>Options menu.");
/*    */   }
/*    */ 
/*    */   public void handleQuit(ApplicationEvent paramApplicationEvent) {
/* 36 */     new Executer("Quit", null);
/*    */   }
/*    */ 
/*    */   public void run()
/*    */   {
/* 41 */     if (paths.size() > 0)
/* 42 */       new Opener().openAndAddToRecent((String)paths.remove(0));
/*    */   }
/*    */ 
/*    */   public void handleOpenApplication(ApplicationEvent paramApplicationEvent)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void handleReOpenApplication(ApplicationEvent paramApplicationEvent)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void handlePrintFile(ApplicationEvent paramApplicationEvent)
/*    */   {
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     MacAdapter
 * JD-Core Version:    0.6.2
 */