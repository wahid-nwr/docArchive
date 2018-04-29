/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.IJ;
/*     */ import java.applet.Applet;
/*     */ import java.applet.AppletContext;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.URL;
/*     */ import java.net.URLClassLoader;
/*     */ 
/*     */ public class BrowserLauncher
/*     */   implements PlugIn
/*     */ {
/*     */   private static Class mrjFileUtilsClass;
/*     */   private static Method openURL;
/*     */   private static boolean error;
/*     */ 
/*     */   public void run(String theURL)
/*     */   {
/*  63 */     if (error) return;
/*  64 */     if ((theURL == null) || (theURL.equals("")))
/*  65 */       theURL = "http://imagej.nih.gov/ij";
/*  66 */     Applet applet = IJ.getApplet();
/*  67 */     if (applet != null) {
/*     */       try {
/*  69 */         applet.getAppletContext().showDocument(new URL(theURL), "_blank"); } catch (Exception e) {
/*     */       }
/*  71 */       return;
/*     */     }try {
/*  73 */       openURL(theURL);
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void openURL(String url)
/*     */     throws IOException
/*     */   {
/*  83 */     String errorMessage = "";
/*  84 */     if (IJ.isMacOSX()) {
/*  85 */       if (IJ.isJava16())
/*  86 */         IJ.runMacro("exec('open', '" + url + "')");
/*     */       else
/*     */         try {
/*  89 */           Method aMethod = mrjFileUtilsClass.getDeclaredMethod("sharedWorkspace", new Class[0]);
/*  90 */           Object aTarget = aMethod.invoke(mrjFileUtilsClass, new Object[0]);
/*  91 */           openURL.invoke(aTarget, new Object[] { new URL(url) });
/*     */         } catch (Exception e) {
/*  93 */           errorMessage = "" + e;
/*     */         }
/*     */     }
/*  96 */     else if (IJ.isWindows()) {
/*  97 */       String cmd = "rundll32 url.dll,FileProtocolHandler " + url;
/*  98 */       if (System.getProperty("os.name").startsWith("Windows 2000"))
/*  99 */         cmd = "rundll32 shell32.dll,ShellExec_RunDLL " + url;
/* 100 */       Process process = Runtime.getRuntime().exec(cmd);
/*     */       try
/*     */       {
/* 104 */         process.waitFor();
/* 105 */         process.exitValue();
/*     */       } catch (InterruptedException ie) {
/* 107 */         throw new IOException("InterruptedException while launching browser: " + ie.getMessage());
/*     */       }
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 114 */       String[] browsers = { "xdg-open", "netscape", "firefox", "konqueror", "mozilla", "opera", "epiphany", "lynx" };
/* 115 */       String browserName = null;
/*     */       try {
/* 117 */         for (int count = 0; (count < browsers.length) && (browserName == null); count++) {
/* 118 */           String[] c = { "which", browsers[count] };
/* 119 */           if (Runtime.getRuntime().exec(c).waitFor() == 0)
/* 120 */             browserName = browsers[count];
/*     */         }
/* 122 */         if (browserName == null)
/* 123 */           IJ.error("BrowserLauncher", "Could not find a browser");
/*     */         else
/* 125 */           Runtime.getRuntime().exec(new String[] { browserName, url });
/*     */       } catch (Exception e) {
/* 127 */         throw new IOException("Exception while launching browser: " + e.getMessage());
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void loadClasses()
/*     */   {
/* 137 */     if ((IJ.isMacOSX()) && (!IJ.isJava16()) && (IJ.getApplet() == null))
/*     */       try {
/* 139 */         if (new File("/System/Library/Java/com/apple/cocoa/application/NSWorkspace.class").exists()) {
/* 140 */           ClassLoader classLoader = new URLClassLoader(new URL[] { new File("/System/Library/Java").toURL() });
/* 141 */           mrjFileUtilsClass = Class.forName("com.apple.cocoa.application.NSWorkspace", true, classLoader);
/*     */         } else {
/* 143 */           mrjFileUtilsClass = Class.forName("com.apple.cocoa.application.NSWorkspace");
/* 144 */         }openURL = mrjFileUtilsClass.getDeclaredMethod("openURL", new Class[] { URL.class });
/*     */       } catch (Exception e) {
/* 146 */         IJ.log("BrowserLauncher" + e);
/* 147 */         error = true;
/*     */       }
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  58 */     loadClasses();
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.BrowserLauncher
 * JD-Core Version:    0.6.2
 */