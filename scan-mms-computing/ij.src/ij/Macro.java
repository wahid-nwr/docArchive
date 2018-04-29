/*     */ package ij;
/*     */ 
/*     */ import ij.io.FileSaver;
/*     */ import ij.io.Opener;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Locale;
/*     */ 
/*     */ public class Macro
/*     */ {
/*     */   public static final String MACRO_CANCELED = "Macro canceled";
/*  21 */   private static Hashtable table = new Hashtable();
/*     */   static boolean abort;
/*     */ 
/*     */   public static boolean open(String path)
/*     */   {
/*  25 */     if ((path == null) || (path.equals(""))) {
/*  26 */       Opener o = new Opener();
/*  27 */       return true;
/*     */     }
/*  29 */     Opener o = new Opener();
/*  30 */     ImagePlus img = o.openImage(path);
/*  31 */     if (img == null)
/*  32 */       return false;
/*  33 */     img.show();
/*  34 */     return true;
/*     */   }
/*     */ 
/*     */   public static boolean saveAs(String path) {
/*  38 */     ImagePlus imp = WindowManager.getCurrentImage();
/*  39 */     if (imp == null)
/*  40 */       return false;
/*  41 */     FileSaver fs = new FileSaver(imp);
/*  42 */     if ((path == null) || (path.equals("")))
/*  43 */       return fs.saveAsTiff();
/*  44 */     if (imp.getStackSize() > 1) {
/*  45 */       return fs.saveAsTiffStack(path);
/*     */     }
/*  47 */     return fs.saveAsTiff(path);
/*     */   }
/*     */ 
/*     */   public static String getName(String path) {
/*  51 */     int i = path.lastIndexOf('/');
/*  52 */     if (i == -1)
/*  53 */       i = path.lastIndexOf('\\');
/*  54 */     if (i > 0) {
/*  55 */       return path.substring(i + 1);
/*     */     }
/*  57 */     return path;
/*     */   }
/*     */ 
/*     */   public static String getDir(String path) {
/*  61 */     int i = path.lastIndexOf('/');
/*  62 */     if (i == -1)
/*  63 */       i = path.lastIndexOf('\\');
/*  64 */     if (i > 0) {
/*  65 */       return path.substring(0, i + 1);
/*     */     }
/*  67 */     return "";
/*     */   }
/*     */ 
/*     */   public static void abort()
/*     */   {
/*  72 */     abort = true;
/*     */ 
/*  74 */     if (Thread.currentThread().getName().endsWith("Macro$")) {
/*  75 */       table.remove(Thread.currentThread());
/*  76 */       throw new RuntimeException("Macro canceled");
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String getOptions()
/*     */   {
/*  88 */     if (Thread.currentThread().getName().startsWith("Run$_")) {
/*  89 */       Object options = table.get(Thread.currentThread());
/*  90 */       return options + " ";
/*     */     }
/*  92 */     return null;
/*     */   }
/*     */ 
/*     */   public static void setOptions(String options)
/*     */   {
/*  98 */     if ((options == null) || (options.equals("")))
/*  99 */       table.remove(Thread.currentThread());
/*     */     else
/* 101 */       table.put(Thread.currentThread(), options);
/*     */   }
/*     */ 
/*     */   public static void setOptions(Thread thread, String options)
/*     */   {
/* 106 */     if (null == thread)
/* 107 */       throw new RuntimeException("Need a non-null thread instance");
/* 108 */     if (null == options)
/* 109 */       table.remove(thread);
/*     */     else
/* 111 */       table.put(thread, options);
/*     */   }
/*     */ 
/*     */   public static String getValue(String options, String key, String defaultValue) {
/* 115 */     key = trimKey(key);
/* 116 */     key = key + '=';
/* 117 */     int index = -1;
/*     */     do {
/* 119 */       index = options.indexOf(key, ++index);
/* 120 */       if (index < 0) return defaultValue; 
/*     */     }
/* 121 */     while ((index != 0) && (Character.isLetter(options.charAt(index - 1))));
/* 122 */     options = options.substring(index + key.length(), options.length());
/* 123 */     if (options.charAt(0) == '\'') {
/* 124 */       index = options.indexOf("'", 1);
/* 125 */       if (index < 0) {
/* 126 */         return defaultValue;
/*     */       }
/* 128 */       return options.substring(1, index);
/* 129 */     }if (options.charAt(0) == '[') {
/* 130 */       index = options.indexOf("]", 1);
/* 131 */       if (index < 0) {
/* 132 */         return defaultValue;
/*     */       }
/* 134 */       return options.substring(1, index);
/*     */     }
/*     */ 
/* 144 */     index = options.indexOf(" ");
/* 145 */     if (index < 0) {
/* 146 */       return defaultValue;
/*     */     }
/* 148 */     return options.substring(0, index);
/*     */   }
/*     */ 
/*     */   public static String trimKey(String key)
/*     */   {
/* 153 */     int index = key.indexOf(" ");
/* 154 */     if (index > -1)
/* 155 */       key = key.substring(0, index);
/* 156 */     index = key.indexOf(":");
/* 157 */     if (index > -1)
/* 158 */       key = key.substring(0, index);
/* 159 */     key = key.toLowerCase(Locale.US);
/* 160 */     return key;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.Macro
 * JD-Core Version:    0.6.2
 */