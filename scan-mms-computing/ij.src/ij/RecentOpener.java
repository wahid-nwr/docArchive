/*    */ package ij;
/*    */ 
/*    */ import ij.io.Opener;
/*    */ import java.awt.Menu;
/*    */ import java.awt.MenuItem;
/*    */ 
/*    */ public class RecentOpener
/*    */   implements Runnable
/*    */ {
/*    */   private String path;
/*    */ 
/*    */   RecentOpener(String path)
/*    */   {
/* 11 */     this.path = path;
/* 12 */     Thread thread = new Thread(this, "RecentOpener");
/* 13 */     thread.start();
/*    */   }
/*    */ 
/*    */   public void run()
/*    */   {
/* 18 */     Opener o = new Opener();
/* 19 */     o.open(this.path);
/* 20 */     Menu menu = Menus.openRecentMenu;
/* 21 */     int n = menu.getItemCount();
/* 22 */     int index = 0;
/* 23 */     for (int i = 0; i < n; i++) {
/* 24 */       if (menu.getItem(i).getLabel().equals(this.path)) {
/* 25 */         index = i;
/* 26 */         break;
/*    */       }
/*    */     }
/* 29 */     if (index > 0) {
/* 30 */       MenuItem item = menu.getItem(index);
/* 31 */       menu.remove(index);
/* 32 */       menu.insert(item, 0);
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.RecentOpener
 * JD-Core Version:    0.6.2
 */