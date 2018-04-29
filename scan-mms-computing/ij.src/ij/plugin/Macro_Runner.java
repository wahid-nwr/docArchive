/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImageJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.Menus;
/*     */ import ij.WindowManager;
/*     */ import ij.io.OpenDialog;
/*     */ import ij.macro.Interpreter;
/*     */ import ij.plugin.frame.Editor;
/*     */ import ij.plugin.frame.Recorder;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.lang.reflect.Method;
/*     */ 
/*     */ public class Macro_Runner
/*     */   implements PlugIn
/*     */ {
/*     */   public void run(String name)
/*     */   {
/*  17 */     Thread thread = Thread.currentThread();
/*  18 */     String threadName = thread.getName();
/*  19 */     if (!threadName.endsWith("Macro$"))
/*  20 */       thread.setName(threadName + "Macro$");
/*  21 */     String path = null;
/*  22 */     if (name.equals("")) {
/*  23 */       OpenDialog od = new OpenDialog("Run Macro...", path);
/*  24 */       String directory = od.getDirectory();
/*  25 */       name = od.getFileName();
/*  26 */       if (name != null) {
/*  27 */         path = directory + name;
/*  28 */         runMacroFile(path, null);
/*  29 */         if (Recorder.record)
/*  30 */           if (Recorder.scriptMode())
/*  31 */             Recorder.recordCall("IJ.runMacroFile(\"" + path + "\");");
/*     */           else
/*  33 */             Recorder.record("runMacro", path);
/*     */       }
/*     */     }
/*  36 */     else if (name.startsWith("JAR:")) {
/*  37 */       runMacroFromJar(name.substring(4), null);
/*  38 */     } else if (name.startsWith("ij.jar:")) {
/*  39 */       runMacroFromIJJar(name, null);
/*     */     } else {
/*  41 */       path = Menus.getPlugInsPath() + name;
/*  42 */       runMacroFile(path, null);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String runMacroFile(String name, String arg)
/*     */   {
/*  51 */     if (name.startsWith("ij.jar:"))
/*  52 */       return runMacroFromIJJar(name, arg);
/*  53 */     if (name.indexOf(".") == -1) name = name + ".txt";
/*  54 */     String name2 = name;
/*  55 */     boolean fullPath = (name.startsWith("/")) || (name.startsWith("\\")) || (name.indexOf(":\\") == 1);
/*  56 */     if (!fullPath) {
/*  57 */       String macrosDir = Menus.getMacrosPath();
/*  58 */       if (macrosDir != null)
/*  59 */         name2 = Menus.getMacrosPath() + name;
/*     */     }
/*  61 */     File file = new File(name2);
/*  62 */     int size = (int)file.length();
/*  63 */     if ((size <= 0) && (!fullPath) && (name2.endsWith(".txt"))) {
/*  64 */       String name3 = name2.substring(0, name2.length() - 4) + ".ijm";
/*  65 */       file = new File(name3);
/*  66 */       size = (int)file.length();
/*  67 */       if (size > 0) name2 = name3;
/*     */     }
/*  69 */     if ((size <= 0) && (!fullPath)) {
/*  70 */       file = new File(System.getProperty("user.dir") + File.separator + name);
/*  71 */       size = (int)file.length();
/*     */     }
/*     */ 
/*  74 */     if (size <= 0) {
/*  75 */       IJ.error("RunMacro", "Macro or script not found:\n \n" + name2);
/*  76 */       return null;
/*     */     }
/*     */     try {
/*  79 */       byte[] buffer = new byte[size];
/*  80 */       FileInputStream in = new FileInputStream(file);
/*  81 */       in.read(buffer, 0, size);
/*  82 */       String macro = new String(buffer, 0, size, "ISO8859_1");
/*  83 */       in.close();
/*  84 */       if (name.endsWith(".js")) {
/*  85 */         return runJavaScript(macro, arg);
/*     */       }
/*  87 */       return runMacro(macro, arg);
/*     */     }
/*     */     catch (Exception e) {
/*  90 */       IJ.error(e.getMessage());
/*  91 */     }return null;
/*     */   }
/*     */ 
/*     */   public String runMacro(String macro, String arg)
/*     */   {
/* 100 */     Interpreter interp = new Interpreter();
/*     */     try {
/* 102 */       return interp.run(macro, arg);
/*     */     } catch (Throwable e) {
/* 104 */       interp.abortMacro();
/* 105 */       IJ.showStatus("");
/* 106 */       IJ.showProgress(1.0D);
/* 107 */       ImagePlus imp = WindowManager.getCurrentImage();
/* 108 */       if (imp != null) imp.unlock();
/* 109 */       String msg = e.getMessage();
/* 110 */       if (((e instanceof RuntimeException)) && (msg != null) && (e.getMessage().equals("Macro canceled")))
/* 111 */         return "[aborted]";
/* 112 */       IJ.handleException(e);
/*     */     }
/* 114 */     return "[aborted]";
/*     */   }
/*     */ 
/*     */   public static String runMacroFromJar(String name, String arg)
/*     */   {
/* 125 */     String macro = null;
/*     */     try {
/* 127 */       ClassLoader pcl = IJ.getClassLoader();
/* 128 */       InputStream is = pcl.getResourceAsStream(name);
/* 129 */       if (is == null) {
/* 130 */         IJ.error("Macro Runner", "Unable to load \"" + name + "\" from jar file");
/* 131 */         return null;
/*     */       }
/* 133 */       InputStreamReader isr = new InputStreamReader(is);
/* 134 */       StringBuffer sb = new StringBuffer();
/* 135 */       char[] b = new char[8192];
/*     */       int n;
/* 137 */       while ((n = isr.read(b)) > 0)
/* 138 */         sb.append(b, 0, n);
/* 139 */       macro = sb.toString();
/* 140 */       is.close();
/*     */     } catch (IOException e) {
/* 142 */       IJ.error("Macro Runner", "" + e);
/*     */     }
/* 144 */     if (macro != null) {
/* 145 */       return new Macro_Runner().runMacro(macro, arg);
/*     */     }
/* 147 */     return null;
/*     */   }
/*     */ 
/*     */   public String runMacroFromIJJar(String name, String arg) {
/* 151 */     ImageJ ij = IJ.getInstance();
/*     */ 
/* 153 */     Class c = ij != null ? ij.getClass() : new ImageStack().getClass();
/* 154 */     name = name.substring(7);
/* 155 */     String macro = null;
/*     */     try {
/* 157 */       InputStream is = c.getResourceAsStream("/macros/" + name + ".txt");
/*     */ 
/* 159 */       if (is == null)
/* 160 */         return runMacroFile(name, arg);
/* 161 */       InputStreamReader isr = new InputStreamReader(is);
/* 162 */       StringBuffer sb = new StringBuffer();
/* 163 */       char[] b = new char[8192];
/*     */       int n;
/* 165 */       while ((n = isr.read(b)) > 0)
/* 166 */         sb.append(b, 0, n);
/* 167 */       macro = sb.toString();
/*     */     }
/*     */     catch (IOException e) {
/* 170 */       String msg = e.getMessage();
/* 171 */       if ((msg == null) || (msg.equals("")))
/* 172 */         msg = "" + e;
/* 173 */       IJ.showMessage("Macro Runner", msg);
/*     */     }
/* 175 */     if (macro != null) {
/* 176 */       return runMacro(macro, arg);
/*     */     }
/* 178 */     return null;
/*     */   }
/*     */ 
/*     */   public String runJavaScript(String script, String arg)
/*     */   {
/* 184 */     if (arg == null) arg = "";
/* 185 */     Object js = null;
/* 186 */     if ((IJ.isJava16()) && ((!IJ.isMacOSX()) || (IJ.is64Bit())))
/* 187 */       js = IJ.runPlugIn("JavaScriptEvaluator", "");
/*     */     else
/* 189 */       js = IJ.runPlugIn("JavaScript", "");
/* 190 */     if (js == null) IJ.error(Editor.JS_NOT_FOUND);
/* 191 */     script = Editor.getJSPrefix(arg) + script;
/*     */     try {
/* 193 */       Class c = js.getClass();
/* 194 */       Method m = c.getMethod("run", new Class[] { script.getClass(), arg.getClass() });
/* 195 */       s = (String)m.invoke(js, new Object[] { script, arg });
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*     */       String s;
/* 197 */       String msg = "" + e;
/* 198 */       if (msg.indexOf("NoSuchMethod") != 0)
/* 199 */         msg = "\"JavaScript.jar\" (http://imagej.nih.gov/ij/download/tools/JavaScript.jar)\nis outdated";
/* 200 */       IJ.error(msg);
/* 201 */       return null;
/*     */     }
/* 203 */     return null;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.Macro_Runner
 * JD-Core Version:    0.6.2
 */