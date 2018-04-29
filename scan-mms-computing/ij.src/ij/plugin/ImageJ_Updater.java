/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImageJ;
/*     */ import ij.Menus;
/*     */ import ij.Prefs;
/*     */ import ij.gui.GenericDialog;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.EOFException;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.lang.reflect.Field;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class ImageJ_Updater
/*     */   implements PlugIn
/*     */ {
/*     */   public void run(String arg)
/*     */   {
/*  15 */     if (arg.equals("menus")) {
/*  16 */       updateMenus(); return;
/*  17 */     }if (IJ.getApplet() != null) return;
/*     */ 
/*  21 */     URL url = getClass().getResource("/ij/IJ.class");
/*  22 */     String ij_jar = url == null ? null : url.toString().replaceAll("%20", " ");
/*  23 */     if ((ij_jar == null) || (!ij_jar.startsWith("jar:file:"))) {
/*  24 */       error("Could not determine location of ij.jar");
/*  25 */       return;
/*     */     }
/*  27 */     int exclamation = ij_jar.indexOf('!');
/*  28 */     ij_jar = ij_jar.substring(9, exclamation);
/*  29 */     if (IJ.debugMode) IJ.log("Updater: " + ij_jar);
/*  30 */     File file = new File(ij_jar);
/*  31 */     if (!file.exists()) {
/*  32 */       error("File not found: " + file.getPath());
/*  33 */       return;
/*     */     }
/*  35 */     if (!file.canWrite()) {
/*  36 */       String msg = "No write access: " + file.getPath();
/*  37 */       if (IJ.isVista()) msg = msg + "\n \nOn Windows Vista, ImageJ must be installed in a directory that\nthe user can write to, such as \"Desktop\" or \"Documents\"";
/*  38 */       error(msg);
/*  39 */       return;
/*     */     }
/*  41 */     String[] list = openUrlAsList("http://imagej.nih.gov/ij/download/jars/list.txt");
/*  42 */     int count = list.length + 2;
/*  43 */     String[] versions = new String[count];
/*  44 */     String[] urls = new String[count];
/*  45 */     String uv = getUpgradeVersion();
/*  46 */     if (uv == null) return;
/*  47 */     versions[0] = ("v" + uv);
/*  48 */     urls[0] = "http://imagej.nih.gov/ij/upgrade/ij.jar";
/*  49 */     if (versions[0] == null) return;
/*  50 */     for (int i = 1; i < count - 1; i++) {
/*  51 */       String version = list[(i - 1)];
/*  52 */       versions[i] = version.substring(0, version.length() - 1);
/*  53 */       urls[i] = ("http://imagej.nih.gov/ij/download/jars/ij" + version.substring(1, 2) + version.substring(3, 6) + ".jar");
/*     */     }
/*     */ 
/*  56 */     versions[(count - 1)] = "daily build";
/*  57 */     urls[(count - 1)] = "http://imagej.nih.gov/ij/ij.jar";
/*  58 */     int choice = showDialog(versions);
/*  59 */     if (choice == -1) return;
/*  60 */     if ((!versions[choice].startsWith("daily")) && (versions[choice].compareTo("v1.39") < 0) && (Menus.getCommands().get("ImageJ Updater") == null))
/*     */     {
/*  62 */       String msg = "This command is not available in versions of ImageJ prior\nto 1.39 so you will need to install the plugin version at\n<http://imagej.nih.gov/ij/plugins/imagej-updater.html>.";
/*     */ 
/*  65 */       if (!IJ.showMessageWithCancel("Update ImageJ", msg))
/*  66 */         return;
/*     */     }
/*  68 */     byte[] jar = getJar(urls[choice]);
/*  69 */     if (jar == null) {
/*  70 */       error("Unable to download ij.jar from " + urls[choice]);
/*  71 */       return;
/*     */     }
/*     */ 
/*  74 */     if (version().compareTo("1.37v") >= 0) {
/*  75 */       Prefs.savePreferences();
/*     */     }
/*  77 */     saveJar(file, jar);
/*  78 */     if (choice < count - 1)
/*  79 */       new File(IJ.getDirectory("macros") + "functions.html").delete();
/*  80 */     System.exit(0);
/*     */   }
/*     */ 
/*     */   int showDialog(String[] versions) {
/*  84 */     GenericDialog gd = new GenericDialog("ImageJ Updater");
/*  85 */     gd.addChoice("Upgrade To:", versions, versions[0]);
/*  86 */     String msg = "You are currently running v" + version() + ".\n" + " \n" + "If you click \"OK\", ImageJ will quit\n" + "and you will be running the upgraded\n" + "version after you restart ImageJ.\n";
/*     */ 
/*  92 */     gd.addMessage(msg);
/*  93 */     gd.showDialog();
/*  94 */     if (gd.wasCanceled()) {
/*  95 */       return -1;
/*     */     }
/*  97 */     return gd.getNextChoiceIndex();
/*     */   }
/*     */ 
/*     */   String getUpgradeVersion() {
/* 101 */     String url = "http://imagej.nih.gov/ij/notes.html";
/* 102 */     String notes = openUrlAsString(url, 20);
/* 103 */     if (notes == null) {
/* 104 */       error("Unable to connect to http://imagej.nih.gov/ij. You\nmay need to use the Edit>Options>Proxy Settings\ncommand to configure ImageJ to use a proxy server.");
/*     */ 
/* 107 */       return null;
/*     */     }
/* 109 */     int index = notes.indexOf("Version ");
/* 110 */     if (index == -1) {
/* 111 */       error("Release notes are not in the expected format");
/* 112 */       return null;
/*     */     }
/* 114 */     String version = notes.substring(index + 8, index + 13);
/* 115 */     return version;
/*     */   }
/*     */ 
/*     */   String openUrlAsString(String address, int maxLines) {
/*     */     StringBuffer sb;
/*     */     try {
/* 121 */       URL url = new URL(address);
/* 122 */       InputStream in = url.openStream();
/* 123 */       BufferedReader br = new BufferedReader(new InputStreamReader(in));
/* 124 */       sb = new StringBuffer();
/* 125 */       int count = 0;
/*     */       String line;
/* 127 */       while (((line = br.readLine()) != null) && (count++ < maxLines))
/* 128 */         sb.append(line + "\n");
/* 129 */       in.close(); } catch (IOException e) {
/* 130 */       sb = null;
/* 131 */     }return sb != null ? new String(sb) : null;
/*     */   }
/*     */   byte[] getJar(String address) {
/* 136 */     boolean gte133 = version().compareTo("1.33u") >= 0;
/*     */     byte[] data;
/*     */     try {
/* 138 */       URL url = new URL(address);
/* 139 */       IJ.showStatus("Connecting to http://imagej.nih.gov/ij");
/* 140 */       URLConnection uc = url.openConnection();
/* 141 */       int len = uc.getContentLength();
/* 142 */       if (len <= 0)
/* 143 */         return null;
/* 144 */       String name = address.endsWith("ij/ij.jar") ? "daily build" : "ij.jar";
/* 145 */       IJ.showStatus("Downloading ij.jar (" + IJ.d2s(len / 1048576.0D, 1) + "MB)");
/* 146 */       InputStream in = uc.getInputStream();
/* 147 */       data = new byte[len];
/* 148 */       int n = 0;
/* 149 */       while (n < len) {
/* 150 */         int count = in.read(data, n, len - n);
/* 151 */         if (count < 0)
/* 152 */           throw new EOFException();
/* 153 */         n += count;
/* 154 */         if (gte133) IJ.showProgress(n, len);
/*     */       }
/* 156 */       in.close();
/*     */     } catch (IOException e) {
/* 158 */       return null;
/*     */     }
/* 160 */     return data;
/*     */   }
/*     */ 
/*     */   void saveJar(File f, byte[] data)
/*     */   {
/*     */     try
/*     */     {
/* 182 */       FileOutputStream out = new FileOutputStream(f);
/* 183 */       out.write(data, 0, data.length);
/* 184 */       out.close();
/*     */     } catch (IOException e) {
/*     */     }
/*     */   }
/*     */ 
/*     */   String[] openUrlAsList(String address) {
/* 190 */     IJ.showStatus("Connecting to http://imagej.nih.gov/ij");
/* 191 */     Vector v = new Vector();
/*     */     try {
/* 193 */       URL url = new URL(address);
/* 194 */       InputStream in = url.openStream();
/* 195 */       BufferedReader br = new BufferedReader(new InputStreamReader(in));
/*     */       while (true)
/*     */       {
/* 198 */         String line = br.readLine();
/* 199 */         if (line == null) break;
/* 200 */         if (!line.equals("")) v.addElement(line);
/*     */       }
/* 202 */       br.close(); } catch (Exception e) {
/*     */     }
/* 204 */     String[] lines = new String[v.size()];
/* 205 */     v.copyInto((String[])lines);
/* 206 */     IJ.showStatus("");
/* 207 */     return lines;
/*     */   }
/*     */ 
/*     */   String version()
/*     */   {
/* 213 */     String version = "";
/*     */     try {
/* 215 */       Class ijClass = ImageJ.class;
/* 216 */       Field field = ijClass.getField("VERSION");
/* 217 */       version = (String)field.get(ijClass); } catch (Exception ex) {
/*     */     }
/* 219 */     return version;
/*     */   }
/*     */ 
/*     */   boolean isMac() {
/* 223 */     String osname = System.getProperty("os.name");
/* 224 */     return osname.startsWith("Mac");
/*     */   }
/*     */ 
/*     */   void error(String msg) {
/* 228 */     IJ.error("ImageJ Updater", msg);
/*     */   }
/*     */ 
/*     */   void updateMenus() {
/* 232 */     if (IJ.debugMode) {
/* 233 */       long start = System.currentTimeMillis();
/* 234 */       Menus.updateImageJMenus();
/* 235 */       IJ.log("Refresh Menus: " + (System.currentTimeMillis() - start) + " ms");
/*     */     } else {
/* 237 */       Menus.updateImageJMenus();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.ImageJ_Updater
 * JD-Core Version:    0.6.2
 */