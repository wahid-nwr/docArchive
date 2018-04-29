/*     */ package ij.plugin;
/*     */ 
/*     */ import com.sun.tools.javac.Main;
/*     */ import ij.IJ;
/*     */ import ij.Menus;
/*     */ import ij.Prefs;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.io.OpenDialog;
/*     */ import ij.plugin.frame.Editor;
/*     */ import java.awt.Font;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FilenameFilter;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.Locale;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class Compiler
/*     */   implements PlugIn, FilenameFilter
/*     */ {
/*     */   private static final int TARGET14 = 0;
/*     */   private static final int TARGET15 = 1;
/*     */   private static final int TARGET16 = 2;
/*     */   private static final int TARGET17 = 3;
/*  17 */   private static final String[] targets = { "1.4", "1.5", "1.6", "1.7" };
/*     */   private static final String TARGET_KEY = "javac.target";
/*     */   private static Main javac;
/*     */   private static ByteArrayOutputStream output;
/*     */   private static String dir;
/*     */   private static String name;
/*     */   private static Editor errors;
/*     */   private static boolean generateDebuggingInfo;
/*  24 */   private static int target = (int)Prefs.get("javac.target", 1.0D);
/*     */ 
/*     */   public void run(String arg) {
/*  27 */     if (arg.equals("edit"))
/*  28 */       edit();
/*  29 */     else if (arg.equals("options"))
/*  30 */       showDialog();
/*     */     else
/*  32 */       compileAndRun(arg);
/*     */   }
/*     */ 
/*     */   void edit() {
/*  36 */     if (open("", "Open macro or plugin")) {
/*  37 */       Editor ed = (Editor)IJ.runPlugIn("ij.plugin.frame.Editor", "");
/*  38 */       if (ed != null) ed.open(dir, name); 
/*     */     }
/*     */   }
/*     */ 
/*     */   void compileAndRun(String path)
/*     */   {
/*  43 */     if (!open(path, "Compile and Run Plugin..."))
/*  44 */       return;
/*  45 */     if (name.endsWith(".class")) {
/*  46 */       runPlugin(name.substring(0, name.length() - 1));
/*  47 */       return;
/*     */     }
/*  49 */     if (!isJavac()) return;
/*  50 */     if (compile(dir + name))
/*  51 */       runPlugin(name);
/*     */   }
/*     */ 
/*     */   boolean isJavac() {
/*     */     try {
/*  56 */       if (javac == null) {
/*  57 */         output = new ByteArrayOutputStream(4096);
/*  58 */         javac = new Main();
/*     */       }
/*     */     } catch (NoClassDefFoundError e) {
/*  61 */       IJ.error("Unable to find the javac compiler, which comes with the Windows and \nLinux versions of ImageJ that include Java in the ImageJ/jre folder.\n \n   java.home: " + System.getProperty("java.home"));
/*     */ 
/*  66 */       return false;
/*     */     }
/*  68 */     return true;
/*     */   }
/*     */ 
/*     */   boolean compile(String path) {
/*  72 */     IJ.showStatus("compiling: " + path);
/*  73 */     output.reset();
/*  74 */     String classpath = getClassPath(path);
/*  75 */     Vector v = new Vector();
/*  76 */     if (generateDebuggingInfo)
/*  77 */       v.addElement("-g");
/*  78 */     if (IJ.isJava15()) {
/*  79 */       validateTarget();
/*  80 */       v.addElement("-source");
/*  81 */       v.addElement(targets[target]);
/*  82 */       v.addElement("-target");
/*  83 */       v.addElement(targets[target]);
/*  84 */       v.addElement("-Xlint:unchecked");
/*     */     }
/*  86 */     v.addElement("-deprecation");
/*  87 */     v.addElement("-classpath");
/*  88 */     v.addElement(classpath);
/*  89 */     v.addElement(path);
/*  90 */     String[] arguments = new String[v.size()];
/*  91 */     v.copyInto((String[])arguments);
/*  92 */     if (IJ.debugMode) {
/*  93 */       String str = "javac";
/*  94 */       for (int i = 0; i < arguments.length; i++)
/*  95 */         str = str + " " + arguments[i];
/*  96 */       IJ.log(str);
/*     */     }
/*  98 */     boolean compiled = Main.compile(arguments, new PrintWriter(output)) == 0;
/*  99 */     String s = output.toString();
/* 100 */     boolean errors = (!compiled) || (areErrors(s));
/* 101 */     if (errors)
/* 102 */       showErrors(s);
/*     */     else
/* 104 */       IJ.showStatus("done");
/* 105 */     return compiled;
/*     */   }
/*     */ 
/*     */   String getClassPath(String path)
/*     */   {
/* 112 */     long start = System.currentTimeMillis();
/* 113 */     StringBuffer sb = new StringBuffer();
/* 114 */     sb.append(System.getProperty("java.class.path"));
/* 115 */     File f = new File(path);
/* 116 */     if (f != null)
/* 117 */       sb.append(File.pathSeparator + f.getParent());
/* 118 */     String pluginsDir = Menus.getPlugInsPath();
/* 119 */     if (pluginsDir != null)
/* 120 */       addJars(pluginsDir, sb);
/* 121 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   void addJars(String path, StringBuffer sb)
/*     */   {
/* 126 */     String[] list = null;
/* 127 */     File f = new File(path);
/* 128 */     if ((f.exists()) && (f.isDirectory()))
/* 129 */       list = f.list();
/* 130 */     if (list == null) return;
/* 131 */     if (!path.endsWith(File.separator))
/* 132 */       path = path + File.separator;
/* 133 */     for (int i = 0; i < list.length; i++) {
/* 134 */       File f2 = new File(path + list[i]);
/* 135 */       if (f2.isDirectory()) {
/* 136 */         addJars(path + list[i], sb);
/* 137 */       } else if ((list[i].endsWith(".jar")) && ((list[i].indexOf("_") == -1) || (list[i].equals("loci_tools.jar")))) {
/* 138 */         sb.append(File.pathSeparator + path + list[i]);
/* 139 */         if (IJ.debugMode) IJ.log("javac: " + path + list[i]); 
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   boolean areErrors(String s)
/*     */   {
/* 145 */     boolean errors = (s != null) && (s.length() > 0);
/* 146 */     if ((errors) && (s.indexOf("1 warning") > 0) && (s.indexOf("[deprecation] show()") > 0)) {
/* 147 */       errors = false;
/*     */     }
/*     */ 
/* 150 */     return errors;
/*     */   }
/*     */ 
/*     */   void showErrors(String s) {
/* 154 */     if ((errors == null) || (!errors.isVisible())) {
/* 155 */       errors = (Editor)IJ.runPlugIn("ij.plugin.frame.Editor", "");
/* 156 */       errors.setFont(new Font("Monospaced", 0, 12));
/*     */     }
/* 158 */     if (errors != null)
/* 159 */       errors.display("Errors", s);
/* 160 */     IJ.showStatus("done (errors)");
/*     */   }
/*     */ 
/*     */   boolean open(String path, String msg)
/*     */   {
/*     */     String directory;
/*     */     String fileName;
/*     */     boolean okay;
/* 167 */     if (path.equals("")) {
/* 168 */       if (dir == null) dir = IJ.getDirectory("plugins");
/* 169 */       OpenDialog od = new OpenDialog(msg, dir, name);
/* 170 */       String directory = od.getDirectory();
/* 171 */       String fileName = od.getFileName();
/* 172 */       boolean okay = fileName != null;
/* 173 */       String lcName = okay ? fileName.toLowerCase(Locale.US) : null;
/* 174 */       if (okay)
/* 175 */         if (msg.startsWith("Compile")) {
/* 176 */           if ((!lcName.endsWith(".java")) && (!lcName.endsWith(".class"))) {
/* 177 */             IJ.error("File name must end with \".java\" or \".class\".");
/* 178 */             okay = false;
/*     */           }
/* 180 */         } else if ((!lcName.endsWith(".java")) && (!lcName.endsWith(".txt")) && (!lcName.endsWith(".ijm")) && (!lcName.endsWith(".js"))) {
/* 181 */           IJ.error("File name must end with \".java\", \".txt\" or \".js\".");
/* 182 */           okay = false;
/*     */         }
/*     */     }
/*     */     else {
/* 186 */       int i = path.lastIndexOf('/');
/* 187 */       if (i == -1)
/* 188 */         i = path.lastIndexOf('\\');
/*     */       String fileName;
/* 189 */       if (i > 0) {
/* 190 */         String directory = path.substring(0, i + 1);
/* 191 */         fileName = path.substring(i + 1);
/*     */       } else {
/* 193 */         directory = "";
/* 194 */         fileName = path;
/*     */       }
/* 196 */       okay = true;
/*     */     }
/* 198 */     if (okay) {
/* 199 */       name = fileName;
/* 200 */       dir = directory;
/* 201 */       Editor.setDefaultDirectory(dir);
/*     */     }
/* 203 */     return okay;
/*     */   }
/*     */ 
/*     */   public boolean accept(File dir, String name)
/*     */   {
/* 209 */     return (name.endsWith(".java")) || (name.endsWith(".macro")) || (name.endsWith(".txt"));
/*     */   }
/*     */ 
/*     */   void runPlugin(String name)
/*     */   {
/* 214 */     name = name.substring(0, name.length() - 5);
/* 215 */     new PlugInExecuter(name);
/*     */   }
/*     */ 
/*     */   public void showDialog() {
/* 219 */     validateTarget();
/* 220 */     GenericDialog gd = new GenericDialog("Compile and Run");
/* 221 */     gd.addChoice("Target: ", targets, targets[target]);
/* 222 */     gd.setInsets(15, 5, 0);
/* 223 */     gd.addCheckbox("Generate debugging info (javac -g)", generateDebuggingInfo);
/* 224 */     gd.addHelp("http://imagej.nih.gov/ij/docs/menus/edit.html#compiler");
/* 225 */     gd.showDialog();
/* 226 */     if (gd.wasCanceled()) return;
/* 227 */     target = gd.getNextChoiceIndex();
/* 228 */     generateDebuggingInfo = gd.getNextBoolean();
/* 229 */     validateTarget();
/*     */   }
/*     */ 
/*     */   void validateTarget() {
/* 233 */     if ((target < 0) || (target > 3))
/* 234 */       target = 1;
/* 235 */     if (((target > 2) && (!IJ.isJava17())) || ((target > 1) && (!IJ.isJava16())))
/* 236 */       target = 1;
/* 237 */     if (!IJ.isJava15())
/* 238 */       target = 0;
/* 239 */     Prefs.set("javac.target", target);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.Compiler
 * JD-Core Version:    0.6.2
 */