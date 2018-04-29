/*    */ package ij.plugin;
/*    */ 
/*    */ import ij.IJ;
/*    */ import ij.Menus;
/*    */ import ij.Prefs;
/*    */ import ij.util.StringSorter;
/*    */ import java.io.File;
/*    */ import java.util.Vector;
/*    */ 
/*    */ public class ClassChecker
/*    */   implements PlugIn
/*    */ {
/*    */   String[] paths;
/*    */   String[] names;
/*    */ 
/*    */   public void run(String arg)
/*    */   {
/* 14 */     deleteDuplicates();
/*    */   }
/*    */ 
/*    */   void deleteDuplicates()
/*    */   {
/* 19 */     getPathsAndNames();
/* 20 */     if ((this.paths == null) || (this.paths.length < 2)) return;
/* 21 */     String[] sortedNames = new String[this.names.length];
/* 22 */     for (int i = 0; i < this.names.length; i++)
/* 23 */       sortedNames[i] = this.names[i];
/* 24 */     StringSorter.sort(sortedNames);
/* 25 */     for (int i = 0; i < sortedNames.length - 1; i++)
/* 26 */       if (sortedNames[i].equals(sortedNames[(i + 1)]))
/* 27 */         delete(sortedNames[i]);
/*    */   }
/*    */ 
/*    */   void delete(String name)
/*    */   {
/* 32 */     String path1 = null; String path2 = null;
/*    */ 
/* 35 */     for (int i = 0; i < this.names.length; i++) {
/* 36 */       if ((path1 == null) && (this.names[i].equals(name))) {
/* 37 */         path1 = this.paths[i] + this.names[i];
/*    */       }
/* 39 */       else if ((path2 == null) && (this.names[i].equals(name))) {
/* 40 */         path2 = this.paths[i] + this.names[i];
/*    */       }
/*    */ 
/* 43 */       if ((path1 != null) && (path2 != null)) {
/* 44 */         File file1 = new File(path1);
/* 45 */         File file2 = new File(path2);
/* 46 */         if ((file1 == null) || (file2 == null)) return;
/* 47 */         long date1 = file1.lastModified();
/* 48 */         long date2 = file2.lastModified();
/* 49 */         if (date1 < date2) {
/* 50 */           write(path1);
/* 51 */           file1.delete(); break;
/*    */         }
/* 53 */         write(path2);
/* 54 */         file2.delete();
/*    */ 
/* 56 */         break;
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   void write(String path) {
/* 62 */     IJ.log("Deleting duplicate plugin: " + path);
/*    */   }
/*    */ 
/*    */   void getPathsAndNames()
/*    */   {
/* 68 */     String path = Menus.getPlugInsPath();
/* 69 */     if (path == null) return;
/* 70 */     File f = new File(path);
/* 71 */     String[] list = f.list();
/* 72 */     if (list == null) return;
/* 73 */     Vector v1 = new Vector(1000);
/* 74 */     Vector v2 = new Vector(1000);
/* 75 */     for (int i = 0; i < list.length; i++) {
/* 76 */       String name = list[i];
/* 77 */       if ((name.endsWith(".class")) || (name.endsWith(".jar"))) {
/* 78 */         v1.addElement(path);
/* 79 */         v2.addElement(name);
/*    */       } else {
/* 81 */         getSubdirectoryFiles(path, name, v1, v2);
/*    */       }
/*    */     }
/* 83 */     this.paths = new String[v1.size()];
/* 84 */     v1.copyInto((String[])this.paths);
/* 85 */     this.names = new String[v2.size()];
/* 86 */     v2.copyInto((String[])this.names);
/*    */   }
/*    */ 
/*    */   void getSubdirectoryFiles(String path, String dir, Vector v1, Vector v2)
/*    */   {
/* 92 */     if (dir.endsWith(".java")) return;
/* 93 */     File f = new File(path, dir);
/* 94 */     if (!f.isDirectory()) return;
/* 95 */     String[] list = f.list();
/* 96 */     if (list == null) return;
/* 97 */     dir = dir + Prefs.separator;
/* 98 */     for (int i = 0; i < list.length; i++) {
/* 99 */       String name = list[i];
/* 100 */       if ((name.endsWith(".class")) || (name.endsWith(".jar"))) {
/* 101 */         v1.addElement(path + dir);
/* 102 */         v2.addElement(name);
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.ClassChecker
 * JD-Core Version:    0.6.2
 */