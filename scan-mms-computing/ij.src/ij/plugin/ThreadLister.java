/*    */ package ij.plugin;
/*    */ 
/*    */ import ij.IJ;
/*    */ import ij.text.TextWindow;
/*    */ import java.io.CharArrayWriter;
/*    */ import java.io.PrintWriter;
/*    */ 
/*    */ public class ThreadLister
/*    */   implements PlugIn
/*    */ {
/*    */   public void run(String arg)
/*    */   {
/* 15 */     if (IJ.getApplet() != null)
/* 16 */       return;
/* 17 */     CharArrayWriter caw = new CharArrayWriter();
/* 18 */     PrintWriter pw = new PrintWriter(caw);
/*    */     try {
/* 20 */       listAllThreads(pw);
/* 21 */       new TextWindow("Threads", caw.toString(), 420, 420);
/*    */     }
/*    */     catch (Exception e)
/*    */     {
/*    */     }
/*    */   }
/*    */ 
/*    */   private static void print_thread_info(PrintWriter out, Thread t, String indent)
/*    */   {
/* 30 */     if (t == null) return;
/* 31 */     out.print(indent + "Thread: " + t.getName() + "  Priority: " + t.getPriority() + (t.isDaemon() ? " Daemon" : "") + (t.isAlive() ? "" : " Not Alive") + "\n");
/*    */   }
/*    */ 
/*    */   private static void list_group(PrintWriter out, ThreadGroup g, String indent)
/*    */   {
/* 40 */     if (g == null) return;
/* 41 */     int num_threads = g.activeCount();
/* 42 */     int num_groups = g.activeGroupCount();
/* 43 */     Thread[] threads = new Thread[num_threads];
/* 44 */     ThreadGroup[] groups = new ThreadGroup[num_groups];
/*    */ 
/* 46 */     g.enumerate(threads, false);
/* 47 */     g.enumerate(groups, false);
/*    */ 
/* 49 */     out.println(indent + "Thread Group: " + g.getName() + "  Max Priority: " + g.getMaxPriority() + (g.isDaemon() ? " Daemon" : "") + "\n");
/*    */ 
/* 53 */     for (int i = 0; i < num_threads; i++)
/* 54 */       print_thread_info(out, threads[i], indent + "    ");
/* 55 */     for (int i = 0; i < num_groups; i++)
/* 56 */       list_group(out, groups[i], indent + "    ");
/*    */   }
/*    */ 
/*    */   public static void listAllThreads(PrintWriter out)
/*    */   {
/* 66 */     ThreadGroup current_thread_group = Thread.currentThread().getThreadGroup();
/*    */ 
/* 69 */     ThreadGroup root_thread_group = current_thread_group;
/* 70 */     ThreadGroup parent = root_thread_group.getParent();
/* 71 */     while (parent != null) {
/* 72 */       root_thread_group = parent;
/* 73 */       parent = parent.getParent();
/*    */     }
/*    */ 
/* 77 */     list_group(out, root_thread_group, "");
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.ThreadLister
 * JD-Core Version:    0.6.2
 */