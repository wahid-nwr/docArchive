/*    */ package ij.io;
/*    */ 
/*    */ import ij.IJ;
/*    */ import ij.Menus;
/*    */ import java.io.BufferedInputStream;
/*    */ import java.io.DataInputStream;
/*    */ import java.io.EOFException;
/*    */ import java.io.File;
/*    */ import java.io.FileInputStream;
/*    */ import java.io.FileOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.net.URL;
/*    */ import java.net.URLConnection;
/*    */ 
/*    */ class PluginInstaller
/*    */ {
/*    */   boolean install(String path)
/*    */   {
/* 14 */     boolean isURL = path.startsWith("http://");
/* 15 */     byte[] data = null;
/* 16 */     String name = path;
/* 17 */     if (isURL) {
/* 18 */       URL url = null;
/*    */       try {
/* 20 */         url = new URL(path);
/*    */       } catch (Exception e) {
/* 22 */         IJ.error("" + e);
/* 23 */         return false;
/*    */       }
/* 25 */       int index = path.lastIndexOf("/");
/* 26 */       if ((index != -1) && (index <= path.length() - 1))
/* 27 */         name = path.substring(index + 1);
/* 28 */       data = download(url);
/*    */     } else {
/* 30 */       File f = new File(path);
/* 31 */       name = f.getName();
/* 32 */       data = download(f);
/*    */     }
/* 34 */     if (data == null) return false;
/* 35 */     SaveDialog sd = new SaveDialog("Save Plugin...", Menus.getPlugInsPath(), name, null);
/* 36 */     String name2 = sd.getFileName();
/* 37 */     if (name2 == null) return false;
/* 38 */     String dir = sd.getDirectory();
/* 39 */     if (!savePlugin(new File(dir, name), data))
/* 40 */       return false;
/* 41 */     Menus.updateImageJMenus();
/* 42 */     return true;
/*    */   }
/*    */ 
/*    */   boolean savePlugin(File f, byte[] data) {
/*    */     try {
/* 47 */       FileOutputStream out = new FileOutputStream(f);
/* 48 */       out.write(data, 0, data.length);
/* 49 */       out.close();
/*    */     } catch (IOException e) {
/* 51 */       IJ.error("Plugin Installer", "" + e);
/* 52 */       return false;
/*    */     }
/* 54 */     return true;
/*    */   }
/*    */ 
/*    */   byte[] download(URL url) {
/*    */     byte[] data;
/*    */     try {
/* 60 */       URLConnection uc = url.openConnection();
/* 61 */       int len = uc.getContentLength();
/* 62 */       IJ.showStatus("Downloading " + url.getFile());
/* 63 */       InputStream in = uc.getInputStream();
/* 64 */       data = new byte[len];
/* 65 */       int n = 0;
/* 66 */       while (n < len) {
/* 67 */         int count = in.read(data, n, len - n);
/* 68 */         if (count < 0)
/* 69 */           throw new EOFException();
/* 70 */         n += count;
/* 71 */         IJ.showProgress(n, len);
/*    */       }
/* 73 */       in.close();
/*    */     } catch (IOException e) {
/* 75 */       return null;
/*    */     }
/* 77 */     return data;
/*    */   }
/*    */ 
/*    */   byte[] download(File f) {
/* 81 */     if (!f.exists()) {
/* 82 */       IJ.error("Plugin Installer", "File not found: " + f);
/* 83 */       return null;
/*    */     }
/* 85 */     byte[] data = null;
/*    */     try {
/* 87 */       int len = (int)f.length();
/* 88 */       InputStream in = new BufferedInputStream(new FileInputStream(f));
/* 89 */       DataInputStream dis = new DataInputStream(in);
/* 90 */       data = new byte[len];
/* 91 */       dis.readFully(data);
/* 92 */       dis.close();
/*    */     }
/*    */     catch (Exception e) {
/* 95 */       IJ.error("Plugin Installer", "" + e);
/* 96 */       data = null;
/*    */     }
/* 98 */     return data;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.io.PluginInstaller
 * JD-Core Version:    0.6.2
 */