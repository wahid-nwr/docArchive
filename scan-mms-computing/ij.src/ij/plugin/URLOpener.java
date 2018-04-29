/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.CompositeImage;
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.Prefs;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.io.FileInfo;
/*     */ import ij.io.Opener;
/*     */ import ij.plugin.frame.Editor;
/*     */ import ij.plugin.frame.Recorder;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.net.URL;
/*     */ 
/*     */ public class URLOpener
/*     */   implements PlugIn
/*     */ {
/*  17 */   private static String url = "http://imagej.nih.gov/ij/images/clown.gif";
/*     */ 
/*     */   public void run(String urlOrName)
/*     */   {
/*  24 */     if (!urlOrName.equals("")) {
/*  25 */       if (urlOrName.endsWith("StartupMacros.txt")) {
/*  26 */         openTextFile(urlOrName, true);
/*     */       } else {
/*  28 */         String url = Prefs.getImagesURL() + urlOrName;
/*  29 */         ImagePlus imp = new ImagePlus(url);
/*  30 */         if (Recorder.record)
/*  31 */           Recorder.recordCall("imp = IJ.openImage(\"" + url + "\");");
/*  32 */         if (imp.getType() == 4)
/*  33 */           Opener.convertGrayJpegTo8Bits(imp);
/*  34 */         ij.WindowManager.checkForDuplicateName = true;
/*  35 */         FileInfo fi = imp.getOriginalFileInfo();
/*  36 */         if ((fi != null) && (fi.fileType == 12)) {
/*  37 */           imp = new CompositeImage(imp, 1);
/*  38 */         } else if ((imp.getNChannels() > 1) && (fi != null) && (fi.description != null) && (fi.description.indexOf("mode=") != -1)) {
/*  39 */           int mode = 2;
/*  40 */           if (fi.description.indexOf("mode=composite") != -1)
/*  41 */             mode = 1;
/*  42 */           else if (fi.description.indexOf("mode=gray") != -1)
/*  43 */             mode = 3;
/*  44 */           imp = new CompositeImage(imp, mode);
/*     */         }
/*  46 */         if ((fi != null) && ((fi.url == null) || (fi.url.length() == 0))) {
/*  47 */           fi.url = url;
/*  48 */           imp.setFileInfo(fi);
/*     */         }
/*  50 */         imp.show();
/*     */       }
/*  52 */       return;
/*     */     }
/*     */ 
/*  55 */     GenericDialog gd = new GenericDialog("Enter a URL");
/*  56 */     gd.addMessage("Enter URL of an image, macro or web page");
/*  57 */     gd.addStringField("URL:", url, 45);
/*  58 */     gd.showDialog();
/*  59 */     if (gd.wasCanceled())
/*  60 */       return;
/*  61 */     url = gd.getNextString();
/*  62 */     url = url.trim();
/*  63 */     if (url.indexOf("://") == -1)
/*  64 */       url = "http://" + url;
/*  65 */     if (url.endsWith("/")) {
/*  66 */       IJ.runPlugIn("ij.plugin.BrowserLauncher", url.substring(0, url.length() - 1));
/*  67 */     } else if ((url.endsWith(".html")) || (url.endsWith(".htm")) || (url.indexOf(".html#") > 0) || (noExtension(url))) {
/*  68 */       IJ.runPlugIn("ij.plugin.BrowserLauncher", url);
/*  69 */     } else if ((url.endsWith(".txt")) || (url.endsWith(".ijm")) || (url.endsWith(".js")) || (url.endsWith(".java"))) {
/*  70 */       openTextFile(url, false);
/*  71 */     } else if ((url.endsWith(".jar")) || (url.endsWith(".class"))) {
/*  72 */       IJ.open(url);
/*     */     } else {
/*  74 */       IJ.showStatus("Opening: " + url);
/*  75 */       ImagePlus imp = new ImagePlus(url);
/*  76 */       ij.WindowManager.checkForDuplicateName = true;
/*  77 */       FileInfo fi = imp.getOriginalFileInfo();
/*  78 */       if ((fi != null) && (fi.fileType == 12))
/*  79 */         imp = new CompositeImage(imp, 1);
/*  80 */       imp.show();
/*  81 */       IJ.showStatus("");
/*     */     }
/*  83 */     IJ.register(URLOpener.class);
/*     */   }
/*     */ 
/*     */   boolean noExtension(String url) {
/*  87 */     int lastSlash = url.lastIndexOf("/");
/*  88 */     if (lastSlash == -1) lastSlash = 0;
/*  89 */     int lastDot = url.lastIndexOf(".");
/*  90 */     if ((lastDot == -1) || (lastDot < lastSlash) || (url.length() - lastDot > 6)) {
/*  91 */       return true;
/*     */     }
/*  93 */     return false;
/*     */   }
/*     */ 
/*     */   void openTextFile(String urlString, boolean install) {
/*  97 */     StringBuffer sb = null;
/*     */     try {
/*  99 */       URL url = new URL(urlString);
/* 100 */       InputStream in = url.openStream();
/* 101 */       BufferedReader br = new BufferedReader(new InputStreamReader(in));
/* 102 */       sb = new StringBuffer();
/*     */       String line;
/* 104 */       while ((line = br.readLine()) != null)
/* 105 */         sb.append(line + "\n");
/* 106 */       in.close();
/*     */     } catch (IOException e) {
/* 108 */       if ((!install) || (!urlString.endsWith("StartupMacros.txt")))
/* 109 */         IJ.error("URL Opener", "" + e);
/* 110 */       sb = null;
/*     */     }
/* 112 */     if (sb != null)
/* 113 */       if (install) {
/* 114 */         new MacroInstaller().install(new String(sb));
/*     */       } else {
/* 116 */         int index = urlString.lastIndexOf("/");
/* 117 */         if ((index != -1) && (index <= urlString.length() - 1))
/* 118 */           urlString = urlString.substring(index + 1);
/* 119 */         new Editor().create(urlString, new String(sb));
/*     */       }
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.URLOpener
 * JD-Core Version:    0.6.2
 */