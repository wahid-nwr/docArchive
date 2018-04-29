/*     */ package leadtools;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.Paths;
/*     */ import java.util.Locale;
/*     */ 
/*     */ public class Platform
/*     */ {
/*  10 */   public static int VERSION_MAJOR = 19;
/*  11 */   public static int VERSION_MINOR = 0;
/*     */ 
/*  13 */   private static String _osName = null;
/*  14 */   private static String _arch = null;
/*     */ 
/* 102 */   private static String _libPath = null;
/*     */   private static boolean _isDebugKernel;
/*     */   private static boolean _isWindows;
/*     */   private static boolean _isLinux;
/*     */   private static boolean _isAndroid;
/*     */   private static boolean _isMac;
/*     */ 
/*     */   public static boolean isLibraryLoaded(LTLibrary library)
/*     */   {
/*  41 */     synchronized (Platform.class) {
/*  42 */       for (LIB_ID libId : library.dependencies) {
/*  43 */         if (!LTLibrary.isLoaded(libId))
/*  44 */           return false;
/*     */       }
/*  46 */       return true;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void loadLibrary(LTLibrary library) throws Exception {
/*  51 */     synchronized (Platform.class) {
/*  52 */       for (LIB_ID libId : library.dependencies)
/*  53 */         if (!LTLibrary.isLoaded(libId)) {
/*  54 */           loadLibrary(libId);
/*     */ 
/*  58 */           LTLibrary.setLoaded(libId, true);
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void setLoadedLibrary(LTLibrary library)
/*     */   {
/*  65 */     synchronized (Platform.class) {
/*  66 */       for (LIB_ID libId : library.dependencies)
/*  67 */         LTLibrary.setLoaded(libId, true);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void loadLibrary(LIB_ID libId) throws Exception {
/*  72 */     if (libId == LIB_ID.LT_LIBID_KRN)
/*     */     {
/*  74 */       if (!LTLibrary.isLoaded(libId)) {
/*  75 */         loadInitialKernel();
/*  76 */         return;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  81 */     if ((libId == LIB_ID.LT_LIBID_DIS) && (
/*  82 */       (!isWindows()) || ((isLinux()) && (VERSION_MAJOR < 19)))) {
/*  83 */       return;
/*     */     }
/*     */ 
/*  87 */     String fullName = null;
/*  88 */     String libName = ltkrn.GetLibName(0, libId.getValue(), -1);
/*     */ 
/*  90 */     if ((libName != null) && (libName.length() > 0)) {
/*  91 */       fullName = ltkrn.GetLibFullPath(libName);
/*  92 */       if (fullName != null) {
/*  93 */         System.load(fullName);
/*     */       }
/*     */       else
/*  96 */         throw new IllegalStateException(new StringBuilder().append("Cannot load library ").append(libId.toString()).toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String getLibPath()
/*     */   {
/* 105 */     if (LTLibrary.isLoaded(LIB_ID.LT_LIBID_KRN)) {
/* 106 */       return RasterDefaults.getResourceDirectory(LEADResourceDirectory.LIBRARIES);
/*     */     }
/* 108 */     return _libPath;
/*     */   }
/*     */ 
/*     */   public static void setLibPath(String value)
/*     */   {
/* 114 */     if (LTLibrary.isLoaded(LIB_ID.LT_LIBID_KRN))
/* 115 */       RasterDefaults.setResourceDirectory(LEADResourceDirectory.LIBRARIES, value);
/*     */     else
/* 117 */       _libPath = value;
/*     */   }
/*     */ 
/*     */   private static void loadInitialKernel()
/*     */     throws Exception
/*     */   {
/* 138 */     String[] kernelNames = null;
/*     */ 
/* 140 */     if (_isWindows) {
/* 141 */       kernelNames = new String[1];
/* 142 */       kernelNames[0] = new StringBuilder().append("ltkrn").append(is64Bit() ? "x.dll" : "u.dll").toString();
/*     */     }
/* 144 */     else if (_isAndroid) {
/* 145 */       kernelNames = new String[1];
/* 146 */       kernelNames[0] = "libleadtools.so";
/*     */     }
/* 148 */     else if (_isLinux) {
/* 149 */       kernelNames = new String[2];
/* 150 */       String kernelName = "libltkrn";
/* 151 */       kernelNames[0] = new StringBuilder().append(kernelName).append(".so.").append(Integer.toString(VERSION_MAJOR)).toString();
/* 152 */       kernelNames[1] = new StringBuilder().append(kernelName).append(".so").toString();
/*     */     }
/*     */ 
/* 155 */     if (kernelNames != null)
/*     */     {
/* 157 */       int kernelNameCount = kernelNames.length;
/*     */ 
/* 159 */       Exception exception = null;
/* 160 */       Boolean isLoaded = Boolean.valueOf(false);
/* 161 */       for (int i = 0; (i < kernelNameCount) && (!isLoaded.booleanValue()); i++) {
/* 162 */         String kernelName = kernelNames[i];
/*     */ 
/* 164 */         boolean isFullPath = false;
/*     */         Path path;
/*     */         Path path;
/* 165 */         if ((_libPath != null) && (_libPath.length() > 0)) {
/* 166 */           isFullPath = true;
/* 167 */           path = Paths.get(_libPath, new String[] { kernelName });
/*     */         }
/*     */         else {
/* 170 */           path = Paths.get(kernelName, new String[0]);
/* 171 */           isFullPath = false;
/*     */         }
/*     */ 
/* 174 */         if (path.toFile().exists()) {
/* 175 */           String filePath = path.toAbsolutePath().toString();
/*     */           try
/*     */           {
/* 178 */             if (isFullPath)
/* 179 */               System.load(filePath);
/*     */             else {
/* 181 */               System.loadLibrary(filePath);
/*     */             }
/*     */ 
/* 184 */             isLoaded = Boolean.valueOf(true);
/* 185 */             if (isFullPath)
/* 186 */               RasterDefaults.setResourceDirectory(LEADResourceDirectory.LIBRARIES, _libPath);
/*     */           }
/*     */           catch (Exception ex)
/*     */           {
/* 190 */             if (exception == null) {
/* 191 */               exception = ex;
/*     */             }
/* 193 */             if (i == kernelNameCount - 1)
/*     */             {
/* 195 */               throw exception;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     } else {
/* 201 */       throw new IllegalStateException("Cannot load the LEADTOOLS kernel");
/*     */     }
/*     */   }
/*     */ 
/*     */   public static boolean isDebugKernel()
/*     */   {
/* 208 */     return _isDebugKernel;
/*     */   }
/*     */ 
/*     */   public static void setDebugKernel(boolean value) {
/* 212 */     _isDebugKernel = value;
/*     */   }
/*     */ 
/*     */   public static final boolean isWindows()
/*     */   {
/* 218 */     return _isWindows;
/*     */   }
/*     */ 
/*     */   public static final boolean isLinux()
/*     */   {
/* 224 */     return _isLinux;
/*     */   }
/*     */ 
/*     */   public static final boolean isAndroid()
/*     */   {
/* 230 */     return _isAndroid;
/*     */   }
/*     */ 
/*     */   public static boolean isMac()
/*     */   {
/* 236 */     return _isMac;
/*     */   }
/*     */ 
/*     */   public static final boolean is64Bit() {
/* 240 */     String model = System.getProperty("sun.arch.data.model", System.getProperty("com.ibm.vm.bitmode"));
/*     */ 
/* 242 */     if ((model != null) && (model.equals("64"))) {
/* 243 */       return true;
/*     */     }
/* 245 */     if ((_arch == "x86_64") || (_arch == "ia64") || (_arch == "ppc64") || (_arch == "sparcv9") || (_arch == "amd64"))
/*     */     {
/* 250 */       return true;
/*     */     }
/* 252 */     return false;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  17 */     if (_osName == null)
/*     */     {
/*  19 */       _osName = System.getProperty("os.name", "generic");
/*  20 */       if (_osName.startsWith("Linux"))
/*     */       {
/*  22 */         if (System.getProperty("java.vm.name").toLowerCase(Locale.ENGLISH).equals("dalvik"))
/*  23 */           _isAndroid = true;
/*     */         else
/*  25 */           _isLinux = true;
/*     */       }
/*  27 */       else if ((_osName.startsWith("Mac")) || (_osName.startsWith("Darwin")))
/*     */       {
/*  29 */         _isMac = true;
/*     */       }
/*  31 */       else if (_osName.startsWith("Windows"))
/*     */       {
/*  33 */         _isWindows = true;
/*     */       }
/*     */ 
/*  36 */       _arch = System.getProperty("os.arch").toLowerCase().trim();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.Platform
 * JD-Core Version:    0.6.2
 */