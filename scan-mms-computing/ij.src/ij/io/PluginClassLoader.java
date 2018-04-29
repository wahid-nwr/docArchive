/*    */ package ij.io;
/*    */ 
/*    */ import ij.IJ;
/*    */ import java.io.File;
/*    */ import java.net.MalformedURLException;
/*    */ import java.net.URI;
/*    */ import java.net.URL;
/*    */ import java.net.URLClassLoader;
/*    */ 
/*    */ public class PluginClassLoader extends URLClassLoader
/*    */ {
/*    */   protected String path;
/*    */ 
/*    */   public PluginClassLoader(String path)
/*    */   {
/* 29 */     super(new URL[0], IJ.class.getClassLoader());
/* 30 */     init(path);
/*    */   }
/*    */ 
/*    */   public PluginClassLoader(String path, boolean callSuper)
/*    */   {
/* 35 */     super(new URL[0], Thread.currentThread().getContextClassLoader());
/* 36 */     init(path);
/*    */   }
/*    */ 
/*    */   void init(String path) {
/* 40 */     this.path = path;
/*    */ 
/* 43 */     File f = new File(path);
/*    */     try
/*    */     {
/* 46 */       addURL(f.toURI().toURL());
/*    */     } catch (MalformedURLException e) {
/* 48 */       IJ.log("PluginClassLoader: " + e);
/*    */     }
/* 50 */     String[] list = f.list();
/* 51 */     if (list == null)
/* 52 */       return;
/* 53 */     for (int i = 0; i < list.length; i++)
/* 54 */       if (!list[i].equals(".rsrc"))
/*    */       {
/* 56 */         f = new File(path, list[i]);
/* 57 */         if (f.isDirectory())
/*    */         {
/*    */           try {
/* 60 */             addURL(f.toURI().toURL());
/*    */           } catch (MalformedURLException e) {
/* 62 */             IJ.log("PluginClassLoader: " + e);
/*    */           }
/* 64 */           String[] innerlist = f.list();
/* 65 */           if (innerlist != null)
/* 66 */             for (int j = 0; j < innerlist.length; j++) {
/* 67 */               File g = new File(f, innerlist[j]);
/* 68 */               if (g.isFile()) addJAR(g); 
/*    */             }
/*    */         }
/* 71 */         else { addJAR(f); }
/*    */       }
/*    */   }
/*    */ 
/*    */   private void addJAR(File f) {
/* 76 */     if ((f.getName().endsWith(".jar")) || (f.getName().endsWith(".zip")))
/*    */       try {
/* 78 */         addURL(f.toURI().toURL());
/*    */       } catch (MalformedURLException e) {
/* 80 */         IJ.log("PluginClassLoader: " + e);
/*    */       }
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.io.PluginClassLoader
 * JD-Core Version:    0.6.2
 */