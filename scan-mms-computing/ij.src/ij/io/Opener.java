/*      */ package ij.io;
/*      */ 
/*      */ import ij.CompositeImage;
/*      */ import ij.IJ;
/*      */ import ij.ImagePlus;
/*      */ import ij.ImageStack;
/*      */ import ij.LookUpTable;
/*      */ import ij.Menus;
/*      */ import ij.Prefs;
/*      */ import ij.gui.Roi;
/*      */ import ij.measure.ResultsTable;
/*      */ import ij.plugin.AVI_Reader;
/*      */ import ij.plugin.DICOM;
/*      */ import ij.plugin.frame.Editor;
/*      */ import ij.plugin.frame.Recorder;
/*      */ import ij.process.ImageConverter;
/*      */ import ij.process.ImageProcessor;
/*      */ import ij.text.TextWindow;
/*      */ import ij.util.Java2;
/*      */ import java.awt.EventQueue;
/*      */ import java.awt.Image;
/*      */ import java.awt.Toolkit;
/*      */ import java.awt.image.BufferedImage;
/*      */ import java.awt.image.ColorModel;
/*      */ import java.awt.image.IndexColorModel;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.FileNotFoundException;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.net.MalformedURLException;
/*      */ import java.net.URL;
/*      */ import java.net.URLConnection;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Locale;
/*      */ import java.util.zip.ZipEntry;
/*      */ import java.util.zip.ZipInputStream;
/*      */ import javax.imageio.ImageIO;
/*      */ import javax.swing.JFileChooser;
/*      */ 
/*      */ public class Opener
/*      */ {
/*      */   public static final int UNKNOWN = 0;
/*      */   public static final int TIFF = 1;
/*      */   public static final int DICOM = 2;
/*      */   public static final int FITS = 3;
/*      */   public static final int PGM = 4;
/*      */   public static final int JPEG = 5;
/*      */   public static final int GIF = 6;
/*      */   public static final int LUT = 7;
/*      */   public static final int BMP = 8;
/*      */   public static final int ZIP = 9;
/*      */   public static final int JAVA_OR_TEXT = 10;
/*      */   public static final int ROI = 11;
/*      */   public static final int TEXT = 12;
/*      */   public static final int PNG = 13;
/*      */   public static final int TIFF_AND_DICOM = 14;
/*      */   public static final int CUSTOM = 15;
/*      */   public static final int AVI = 16;
/*      */   public static final int OJJ = 17;
/*      */   public static final int TABLE = 18;
/*   33 */   public static final String[] types = { "unknown", "tif", "dcm", "fits", "pgm", "jpg", "gif", "lut", "bmp", "zip", "java/txt", "roi", "txt", "png", "t&d", "custom", "ojj", "table" };
/*      */ 
/*   35 */   private static String defaultDirectory = null;
/*      */   private static int fileType;
/*      */   private boolean error;
/*      */   private boolean isRGB48;
/*      */   private boolean silentMode;
/*      */   private String omDirectory;
/*      */   private File[] omFiles;
/*      */   private static boolean openUsingPlugins;
/*   47 */   private static boolean bioformats = (commands != null) && (commands.get("Bio-Formats Importer") != null);
/*      */ 
/*      */   public void open()
/*      */   {
/*   59 */     OpenDialog od = new OpenDialog("Open", "");
/*   60 */     String directory = od.getDirectory();
/*   61 */     String name = od.getFileName();
/*   62 */     if (name != null) {
/*   63 */       String path = directory + name;
/*   64 */       this.error = false;
/*   65 */       open(path);
/*   66 */       if (!this.error) Menus.addOpenRecentItem(path);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void openMultiple()
/*      */   {
/*   77 */     Java2.setSystemLookAndFeel();
/*      */     try
/*      */     {
/*   80 */       EventQueue.invokeAndWait(new Runnable() {
/*      */         public void run() {
/*   82 */           JFileChooser fc = new JFileChooser();
/*   83 */           fc.setMultiSelectionEnabled(true);
/*   84 */           File dir = null;
/*   85 */           String sdir = OpenDialog.getDefaultDirectory();
/*   86 */           if (sdir != null)
/*   87 */             dir = new File(sdir);
/*   88 */           if (dir != null)
/*   89 */             fc.setCurrentDirectory(dir);
/*   90 */           int returnVal = fc.showOpenDialog(IJ.getInstance());
/*   91 */           if (returnVal != 0)
/*   92 */             return;
/*   93 */           Opener.this.omFiles = fc.getSelectedFiles();
/*   94 */           if (Opener.this.omFiles.length == 0) {
/*   95 */             Opener.this.omFiles = new File[1];
/*   96 */             Opener.this.omFiles[0] = fc.getSelectedFile();
/*      */           }
/*   98 */           Opener.this.omDirectory = (fc.getCurrentDirectory().getPath() + File.separator);
/*      */         } } );
/*      */     } catch (Exception e) {
/*      */     }
/*  102 */     if (this.omDirectory == null) return;
/*  103 */     OpenDialog.setDefaultDirectory(this.omDirectory);
/*  104 */     for (int i = 0; i < this.omFiles.length; i++) {
/*  105 */       String path = this.omDirectory + this.omFiles[i].getName();
/*  106 */       open(path);
/*  107 */       if ((i == 0) && (Recorder.record))
/*  108 */         Recorder.recordPath("open", path);
/*  109 */       if ((i == 0) && (!this.error))
/*  110 */         Menus.addOpenRecentItem(path);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void open(String path)
/*      */   {
/*  118 */     boolean isURL = path.indexOf("://") > 0;
/*  119 */     if ((isURL) && (isText(path))) {
/*  120 */       openTextURL(path);
/*  121 */       return;
/*      */     }
/*  123 */     if ((path.endsWith(".jar")) || (path.endsWith(".class"))) {
/*  124 */       new PluginInstaller().install(path);
/*  125 */       return;
/*      */     }
/*  127 */     boolean fullPath = (path.startsWith("/")) || (path.startsWith("\\")) || (path.indexOf(":\\") == 1) || (isURL);
/*  128 */     if (!fullPath) {
/*  129 */       String defaultDir = OpenDialog.getDefaultDirectory();
/*  130 */       if (defaultDir != null)
/*  131 */         path = defaultDir + path;
/*      */       else
/*  133 */         path = new File(path).getAbsolutePath();
/*      */     }
/*  135 */     if (!this.silentMode) IJ.showStatus("Opening: " + path);
/*  136 */     long start = System.currentTimeMillis();
/*  137 */     ImagePlus imp = openImage(path);
/*  138 */     if ((imp == null) && (isURL))
/*  139 */       return;
/*  140 */     if (imp != null) {
/*  141 */       ij.WindowManager.checkForDuplicateName = true;
/*  142 */       if (this.isRGB48)
/*  143 */         openRGB48(imp);
/*      */       else
/*  145 */         imp.show(IJ.d2s((System.currentTimeMillis() - start) / 1000.0D, 3) + " seconds");
/*      */     } else {
/*  147 */       switch (fileType) {
/*      */       case 7:
/*  149 */         imp = (ImagePlus)IJ.runPlugIn("ij.plugin.LutLoader", path);
/*  150 */         if (imp.getWidth() != 0)
/*  151 */           imp.show(); break;
/*      */       case 11:
/*  154 */         IJ.runPlugIn("ij.plugin.RoiReader", path);
/*  155 */         break;
/*      */       case 10:
/*      */       case 12:
/*  157 */         if (IJ.altKeyDown()) {
/*  158 */           new TextWindow(path, 400, 450);
/*  159 */           IJ.setKeyUp(18);
/*      */         }
/*      */         else {
/*  162 */           File file = new File(path);
/*  163 */           int maxSize = 250000;
/*  164 */           long size = file.length();
/*  165 */           if (size >= 28000L) {
/*  166 */             String osName = System.getProperty("os.name");
/*  167 */             if ((osName.equals("Windows 95")) || (osName.equals("Windows 98")) || (osName.equals("Windows Me")))
/*  168 */               maxSize = 60000;
/*      */           }
/*  170 */           if (size < maxSize) {
/*  171 */             Editor ed = (Editor)IJ.runPlugIn("ij.plugin.frame.Editor", "");
/*  172 */             if (ed != null) ed.open(getDir(path), getName(path)); 
/*      */           }
/*  174 */           else { new TextWindow(path, 400, 450); } 
/*  175 */         }break;
/*      */       case 17:
/*  177 */         IJ.runPlugIn("ObjectJ_", path);
/*  178 */         break;
/*      */       case 18:
/*  180 */         openResultsTable(path);
/*  181 */         break;
/*      */       case 0:
/*  183 */         String msg = "File is not in a supported format, a reader\nplugin is not available, or it was not found.";
/*      */ 
/*  186 */         if (path != null) {
/*  187 */           if (path.length() > 64)
/*  188 */             path = new File(path).getName();
/*  189 */           if (path.length() <= 64) {
/*  190 */             if (IJ.redirectingErrorMessages())
/*  191 */               msg = msg + " \n   " + path;
/*      */             else
/*  193 */               msg = msg + " \n\t \n" + path;
/*      */           }
/*      */         }
/*  196 */         if (openUsingPlugins)
/*  197 */           msg = msg + "\n \nNOTE: The \"OpenUsingPlugins\" option is set.";
/*  198 */         IJ.wait(IJ.isMacro() ? 500 : 100);
/*  199 */         IJ.error("Opener", msg);
/*  200 */         this.error = true;
/*      */       case 1:
/*      */       case 2:
/*      */       case 3:
/*      */       case 4:
/*      */       case 5:
/*      */       case 6:
/*      */       case 8:
/*      */       case 9:
/*      */       case 13:
/*      */       case 14:
/*      */       case 15:
/*      */       case 16: }  }  } 
/*  207 */   private boolean isText(String path) { if ((path.endsWith(".txt")) || (path.endsWith(".ijm")) || (path.endsWith(".java")) || (path.endsWith(".js")) || (path.endsWith(".html")) || (path.endsWith(".htm")) || (path.endsWith("/")))
/*      */     {
/*  210 */       return true;
/*  211 */     }int lastSlash = path.lastIndexOf("/");
/*  212 */     if (lastSlash == -1) lastSlash = 0;
/*  213 */     int lastDot = path.lastIndexOf(".");
/*  214 */     if ((lastDot == -1) || (lastDot < lastSlash) || (path.length() - lastDot > 6)) {
/*  215 */       return true;
/*      */     }
/*  217 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean openAndAddToRecent(String path)
/*      */   {
/*  223 */     open(path);
/*  224 */     if (!this.error)
/*  225 */       Menus.addOpenRecentItem(path);
/*  226 */     return this.error;
/*      */   }
/*      */ 
/*      */   public ImagePlus openImage(String directory, String name)
/*      */   {
/*  235 */     FileOpener.setSilentMode(this.silentMode);
/*  236 */     if ((directory.length() > 0) && (!directory.endsWith("/")) && (!directory.endsWith("\\")))
/*  237 */       directory = directory + Prefs.separator;
/*  238 */     String path = directory + name;
/*  239 */     fileType = getFileType(path);
/*  240 */     if (IJ.debugMode)
/*  241 */       IJ.log("openImage: \"" + types[fileType] + "\", " + path);
/*      */     ImagePlus imp;
/*  242 */     switch (fileType) {
/*      */     case 1:
/*  244 */       imp = openTiff(directory, name);
/*  245 */       return imp;
/*      */     case 2:
/*  247 */       imp = (ImagePlus)IJ.runPlugIn("ij.plugin.DICOM", path);
/*  248 */       if (imp.getWidth() != 0) return imp; return null;
/*      */     case 14:
/*  251 */       imp = openTiff(directory, name);
/*  252 */       ImagePlus imp2 = (ImagePlus)IJ.runPlugIn("ij.plugin.DICOM", path);
/*  253 */       if ((imp != null) && (imp2 != null)) {
/*  254 */         imp.setProperty("Info", imp2.getProperty("Info"));
/*  255 */         imp.setCalibration(imp2.getCalibration());
/*      */       }
/*  257 */       if (imp == null) imp = imp2;
/*  258 */       return imp;
/*      */     case 3:
/*  260 */       imp = (ImagePlus)IJ.runPlugIn("ij.plugin.FITS_Reader", path);
/*  261 */       if (imp.getWidth() != 0) return imp; return null;
/*      */     case 4:
/*  263 */       imp = (ImagePlus)IJ.runPlugIn("ij.plugin.PGM_Reader", path);
/*  264 */       if (imp.getWidth() != 0) {
/*  265 */         if ((imp.getStackSize() == 3) && (imp.getBitDepth() == 16))
/*  266 */           imp = new CompositeImage(imp, 1);
/*  267 */         return imp;
/*      */       }
/*  269 */       return null;
/*      */     case 5:
/*      */     case 6:
/*  271 */       imp = openJpegOrGif(directory, name);
/*  272 */       if ((imp != null) && (imp.getWidth() != 0)) return imp; return null;
/*      */     case 13:
/*  274 */       imp = openUsingImageIO(directory + name);
/*  275 */       if ((imp != null) && (imp.getWidth() != 0)) return imp; return null;
/*      */     case 8:
/*  277 */       imp = (ImagePlus)IJ.runPlugIn("ij.plugin.BMP_Reader", path);
/*  278 */       if (imp.getWidth() != 0) return imp; return null;
/*      */     case 9:
/*  280 */       return openZip(path);
/*      */     case 16:
/*  282 */       AVI_Reader reader = (AVI_Reader)IJ.runPlugIn("ij.plugin.AVI_Reader", path);
/*  283 */       return reader.getImagePlus();
/*      */     case 0:
/*      */     case 12:
/*  286 */       int[] wrap = { fileType };
/*  287 */       imp = openWithHandleExtraFileTypes(path, wrap);
/*  288 */       fileType = wrap[0];
/*  289 */       return imp;
/*      */     case 7:
/*      */     case 10:
/*      */     case 11:
/*  291 */     case 15: } return null;
/*      */   }
/*      */ 
/*      */   public ImagePlus openImage(String path)
/*      */   {
/*  299 */     if ((path == null) || (path.equals("")))
/*  300 */       path = getPath();
/*  301 */     if (path == null) return null;
/*  302 */     ImagePlus img = null;
/*  303 */     if (path.indexOf("://") > 0)
/*  304 */       img = openURL(path);
/*      */     else
/*  306 */       img = openImage(getDir(path), getName(path));
/*  307 */     return img;
/*      */   }
/*      */ 
/*      */   public ImagePlus openImage(String path, int n)
/*      */   {
/*  312 */     if ((path == null) || (path.equals("")))
/*  313 */       path = getPath();
/*  314 */     if (path == null) return null;
/*  315 */     int type = getFileType(path);
/*  316 */     if (type != 1)
/*  317 */       throw new IllegalArgumentException("TIFF file require");
/*  318 */     return openTiff(path, n);
/*      */   }
/*      */ 
/*      */   String getPath() {
/*  322 */     OpenDialog od = new OpenDialog("Open", "");
/*  323 */     String dir = od.getDirectory();
/*  324 */     String name = od.getFileName();
/*  325 */     if (name == null) {
/*  326 */       return null;
/*      */     }
/*  328 */     return dir + name;
/*      */   }
/*      */ 
/*      */   public ImagePlus openURL(String url)
/*      */   {
/*      */     try
/*      */     {
/*  337 */       String name = "";
/*  338 */       int index = url.lastIndexOf('/');
/*  339 */       if (index == -1)
/*  340 */         index = url.lastIndexOf('\\');
/*  341 */       if (index > 0)
/*  342 */         name = url.substring(index + 1);
/*      */       else
/*  344 */         throw new MalformedURLException("Invalid URL: " + url);
/*  345 */       if (url.indexOf(" ") != -1)
/*  346 */         url = url.replaceAll(" ", "%20");
/*  347 */       URL u = new URL(url);
/*  348 */       IJ.showStatus("" + url);
/*  349 */       String lurl = url.toLowerCase(Locale.US);
/*  350 */       ImagePlus imp = null;
/*  351 */       if (lurl.endsWith(".tif")) {
/*  352 */         imp = openTiff(u.openStream(), name);
/*  353 */       } else if (lurl.endsWith(".zip")) {
/*  354 */         imp = openZipUsingUrl(u);
/*  355 */       } else if ((lurl.endsWith(".jpg")) || (lurl.endsWith(".gif"))) {
/*  356 */         imp = openJpegOrGifUsingURL(name, u);
/*  357 */       } else if ((lurl.endsWith(".dcm")) || (lurl.endsWith(".ima"))) {
/*  358 */         imp = (ImagePlus)IJ.runPlugIn("ij.plugin.DICOM", url);
/*  359 */         if ((imp != null) && (imp.getWidth() == 0)) imp = null; 
/*      */       }
/*  360 */       else if (lurl.endsWith(".png")) {
/*  361 */         imp = openPngUsingURL(name, u);
/*      */       } else {
/*  363 */         URLConnection uc = u.openConnection();
/*  364 */         String type = uc.getContentType();
/*  365 */         if ((type != null) && ((type.equals("image/jpeg")) || (type.equals("image/gif"))))
/*  366 */           imp = openJpegOrGifUsingURL(name, u);
/*  367 */         else if ((type != null) && (type.equals("image/png")))
/*  368 */           imp = openPngUsingURL(name, u);
/*      */         else
/*  370 */           imp = openWithHandleExtraFileTypes(url, new int[] { 0 });
/*      */       }
/*  372 */       IJ.showStatus("");
/*  373 */       return imp;
/*      */     } catch (Exception e) {
/*  375 */       String msg = e.getMessage();
/*  376 */       if ((msg == null) || (msg.equals("")))
/*  377 */         msg = "" + e;
/*  378 */       IJ.error("Open URL", msg + "\n \n" + url);
/*  379 */     }return null;
/*      */   }
/*      */ 
/*      */   void openTextURL(String url)
/*      */   {
/*  385 */     if ((url.endsWith(".pdf")) || (url.endsWith(".zip")))
/*  386 */       return;
/*  387 */     String text = IJ.openUrlAsString(url);
/*  388 */     String name = url.substring(7);
/*  389 */     int index = name.lastIndexOf("/");
/*  390 */     int len = name.length();
/*  391 */     if (index == len - 1)
/*  392 */       name = name.substring(0, len - 1);
/*  393 */     else if ((index != -1) && (index < len - 1))
/*  394 */       name = name.substring(index + 1);
/*  395 */     name = name.replaceAll("%20", " ");
/*  396 */     Editor ed = new Editor();
/*  397 */     ed.setSize(600, 300);
/*  398 */     ed.create(name, text);
/*  399 */     IJ.showStatus("");
/*      */   }
/*      */ 
/*      */   public ImagePlus openWithHandleExtraFileTypes(String path, int[] fileType)
/*      */   {
/*  404 */     ImagePlus imp = null;
/*  405 */     if (path.endsWith(".db"))
/*      */     {
/*  407 */       fileType[0] = 15;
/*  408 */       return null;
/*      */     }
/*  410 */     imp = (ImagePlus)IJ.runPlugIn("HandleExtraFileTypes", path);
/*  411 */     if (imp == null) return null;
/*  412 */     FileInfo fi = imp.getOriginalFileInfo();
/*  413 */     if (fi == null) {
/*  414 */       fi = new FileInfo();
/*  415 */       fi.width = imp.getWidth();
/*  416 */       fi.height = imp.getHeight();
/*  417 */       fi.directory = getDir(path);
/*  418 */       fi.fileName = getName(path);
/*  419 */       imp.setFileInfo(fi);
/*      */     }
/*  421 */     if ((imp.getWidth() > 0) && (imp.getHeight() > 0)) {
/*  422 */       fileType[0] = 15;
/*  423 */       return imp;
/*      */     }
/*  425 */     if (imp.getWidth() == -1)
/*  426 */       fileType[0] = 15;
/*  427 */     return null;
/*      */   }
/*      */ 
/*      */   ImagePlus openZipUsingUrl(URL url)
/*      */     throws IOException
/*      */   {
/*  433 */     URLConnection uc = url.openConnection();
/*  434 */     InputStream in = uc.getInputStream();
/*  435 */     ZipInputStream zis = new ZipInputStream(in);
/*  436 */     ZipEntry entry = zis.getNextEntry();
/*  437 */     if (entry == null) return null;
/*  438 */     String name = entry.getName();
/*  439 */     if ((!name.endsWith(".tif")) && (!name.endsWith(".dcm")))
/*  440 */       throw new IOException("This ZIP archive does not appear to contain a .tif or .dcm file\n" + name);
/*  441 */     if (name.endsWith(".dcm")) {
/*  442 */       return openDicomStack(zis, entry);
/*      */     }
/*  444 */     return openTiff(zis, name);
/*      */   }
/*      */ 
/*      */   ImagePlus openDicomStack(ZipInputStream zis, ZipEntry entry) throws IOException {
/*  448 */     ImagePlus imp = null;
/*  449 */     int count = 0;
/*  450 */     ImageStack stack = null;
/*      */     while (true) {
/*  452 */       if (count > 0) entry = zis.getNextEntry();
/*  453 */       if (entry == null) break;
/*  454 */       String name = entry.getName();
/*  455 */       ImagePlus imp2 = null;
/*  456 */       if (name.endsWith(".dcm")) {
/*  457 */         ByteArrayOutputStream out = new ByteArrayOutputStream();
/*  458 */         byte[] buf = new byte[4096];
/*  459 */         int byteCount = 0; int progress = 0;
/*      */         while (true) {
/*  461 */           int len = zis.read(buf);
/*  462 */           if (len < 0) break;
/*  463 */           out.write(buf, 0, len);
/*  464 */           byteCount += len;
/*      */         }
/*      */ 
/*  467 */         byte[] bytes = out.toByteArray();
/*  468 */         out.close();
/*  469 */         DICOM dcm = new DICOM(new ByteArrayInputStream(bytes));
/*  470 */         dcm.run(name);
/*  471 */         imp2 = dcm;
/*      */       }
/*  473 */       zis.closeEntry();
/*  474 */       if (imp2 != null) {
/*  475 */         count++;
/*  476 */         String label = imp2.getTitle();
/*  477 */         String info = (String)imp2.getProperty("Info");
/*  478 */         if (info != null) label = label + "\n" + info;
/*  479 */         if (count == 1) {
/*  480 */           imp = imp2;
/*  481 */           imp.getStack().setSliceLabel(label, 1);
/*      */         } else {
/*  483 */           stack = imp.getStack();
/*  484 */           stack.addSlice(label, imp2.getProcessor());
/*  485 */           imp.setStack(stack);
/*      */         }
/*      */       }
/*      */     }
/*  488 */     zis.close();
/*  489 */     IJ.showProgress(1.0D);
/*  490 */     if (count == 0)
/*  491 */       throw new IOException("This ZIP archive does not appear to contain any .dcm files");
/*  492 */     return imp;
/*      */   }
/*      */ 
/*      */   ImagePlus openJpegOrGifUsingURL(String title, URL url) {
/*  496 */     if (url == null) return null;
/*  497 */     Image img = Toolkit.getDefaultToolkit().createImage(url);
/*  498 */     if (img != null) {
/*  499 */       ImagePlus imp = new ImagePlus(title, img);
/*  500 */       return imp;
/*      */     }
/*  502 */     return null;
/*      */   }
/*      */ 
/*      */   ImagePlus openPngUsingURL(String title, URL url) {
/*  506 */     if (url == null) return null;
/*  507 */     Image img = null;
/*      */     try {
/*  509 */       img = ImageIO.read(url);
/*      */     } catch (IOException e) {
/*  511 */       IJ.log("" + e);
/*      */     }
/*  513 */     if (img != null) {
/*  514 */       ImagePlus imp = new ImagePlus(title, img);
/*  515 */       return imp;
/*      */     }
/*  517 */     return null;
/*      */   }
/*      */ 
/*      */   ImagePlus openJpegOrGif(String dir, String name) {
/*  521 */     ImagePlus imp = null;
/*  522 */     Image img = Toolkit.getDefaultToolkit().createImage(dir + name);
/*  523 */     if (img != null) {
/*      */       try {
/*  525 */         imp = new ImagePlus(name, img);
/*      */       } catch (IllegalStateException e) {
/*  527 */         return null;
/*      */       }
/*  529 */       if (imp.getType() == 4)
/*  530 */         convertGrayJpegTo8Bits(imp);
/*  531 */       FileInfo fi = new FileInfo();
/*  532 */       fi.fileFormat = 3;
/*  533 */       fi.fileName = name;
/*  534 */       fi.directory = dir;
/*  535 */       imp.setFileInfo(fi);
/*      */     }
/*  537 */     return imp;
/*      */   }
/*      */ 
/*      */   ImagePlus openUsingImageIO(String path) {
/*  541 */     ImagePlus imp = null;
/*  542 */     BufferedImage img = null;
/*  543 */     File f = new File(path);
/*      */     try {
/*  545 */       img = ImageIO.read(f);
/*      */     } catch (Exception e) {
/*  547 */       IJ.error("Open Using ImageIO", "" + e);
/*      */     }
/*  549 */     if (img == null) return null;
/*  550 */     imp = new ImagePlus(f.getName(), img);
/*  551 */     FileInfo fi = new FileInfo();
/*  552 */     fi.fileFormat = 9;
/*  553 */     fi.fileName = f.getName();
/*  554 */     fi.directory = (f.getParent() + File.separator);
/*  555 */     imp.setFileInfo(fi);
/*  556 */     return imp;
/*      */   }
/*      */ 
/*      */   public static void convertGrayJpegTo8Bits(ImagePlus imp)
/*      */   {
/*  561 */     ImageProcessor ip = imp.getProcessor();
/*  562 */     int width = ip.getWidth();
/*  563 */     int height = ip.getHeight();
/*  564 */     int[] pixels = (int[])ip.getPixels();
/*      */ 
/*  566 */     for (int y = 0; y < height; y++) {
/*  567 */       int offset = y * width;
/*  568 */       for (int x = 0; x < width; x++) {
/*  569 */         int c = pixels[(offset + x)];
/*  570 */         int r = (c & 0xFF0000) >> 16;
/*  571 */         int g = (c & 0xFF00) >> 8;
/*  572 */         int b = c & 0xFF;
/*  573 */         if ((r != g) || (g != b)) return;
/*      */       }
/*      */     }
/*  576 */     IJ.showStatus("Converting to 8-bit grayscale");
/*  577 */     new ImageConverter(imp).convertToGray8();
/*      */   }
/*      */ 
/*      */   boolean allSameSizeAndType(FileInfo[] info)
/*      */   {
/*  582 */     boolean sameSizeAndType = true;
/*  583 */     boolean contiguous = true;
/*  584 */     long startingOffset = info[0].getOffset();
/*  585 */     int size = info[0].width * info[0].height * info[0].getBytesPerPixel();
/*  586 */     for (int i = 1; i < info.length; i++) {
/*  587 */       sameSizeAndType &= ((info[i].fileType == info[0].fileType) && (info[i].width == info[0].width) && (info[i].height == info[0].height));
/*      */ 
/*  590 */       contiguous &= info[i].getOffset() == startingOffset + i * size;
/*      */     }
/*  592 */     if ((contiguous) && (info[0].fileType != 12)) {
/*  593 */       info[0].nImages = info.length;
/*      */     }
/*      */ 
/*  598 */     return sameSizeAndType;
/*      */   }
/*      */ 
/*      */   public ImagePlus openTiffStack(FileInfo[] info)
/*      */   {
/*  604 */     if ((info.length > 1) && (!allSameSizeAndType(info)))
/*  605 */       return null;
/*  606 */     FileInfo fi = info[0];
/*  607 */     if (fi.nImages > 1) {
/*  608 */       return new FileOpener(fi).open(false);
/*      */     }
/*  610 */     ColorModel cm = createColorModel(fi);
/*  611 */     ImageStack stack = new ImageStack(fi.width, fi.height, cm);
/*  612 */     Object pixels = null;
/*  613 */     long skip = fi.getOffset();
/*  614 */     int imageSize = fi.width * fi.height * fi.getBytesPerPixel();
/*  615 */     if (info[0].fileType == 13) {
/*  616 */       imageSize = (int)(fi.width * fi.height * 1.5D);
/*  617 */       if ((imageSize & 0x1) == 1) imageSize++; 
/*      */     }
/*  618 */     if (info[0].fileType == 8) {
/*  619 */       int scan = (int)Math.ceil(fi.width / 8.0D);
/*  620 */       imageSize = scan * fi.height;
/*      */     }
/*  622 */     long loc = 0L;
/*  623 */     int nChannels = 1;
/*      */     try {
/*  625 */       InputStream is = createInputStream(fi);
/*  626 */       ImageReader reader = new ImageReader(fi);
/*  627 */       IJ.resetEscape();
/*  628 */       for (int i = 0; i < info.length; i++) {
/*  629 */         nChannels = 1;
/*  630 */         Object[] channels = null;
/*  631 */         if (!this.silentMode)
/*  632 */           IJ.showStatus("Reading: " + (i + 1) + "/" + info.length);
/*  633 */         if (IJ.escapePressed()) {
/*  634 */           IJ.beep();
/*  635 */           IJ.showProgress(1.0D);
/*  636 */           return null;
/*      */         }
/*  638 */         if (info[i].compression >= 2) {
/*  639 */           fi.stripOffsets = info[i].stripOffsets;
/*  640 */           fi.stripLengths = info[i].stripLengths;
/*      */         }
/*  642 */         if ((info[i].samplesPerPixel > 1) && (info[i].getBytesPerPixel() != 3) && (info[i].getBytesPerPixel() != 6)) {
/*  643 */           nChannels = fi.samplesPerPixel;
/*  644 */           channels = new Object[nChannels];
/*  645 */           for (int c = 0; c < nChannels; c++) {
/*  646 */             pixels = reader.readPixels(is, c == 0 ? skip : 0L);
/*  647 */             channels[c] = pixels;
/*      */           }
/*      */         } else {
/*  650 */           pixels = reader.readPixels(is, skip);
/*  651 */         }if ((pixels == null) && (channels == null)) break;
/*  652 */         loc += imageSize * nChannels + skip;
/*  653 */         if (i < info.length - 1) {
/*  654 */           skip = info[(i + 1)].getOffset() - loc;
/*  655 */           if (info[(i + 1)].compression >= 2) skip = 0L;
/*  656 */           if (skip < 0L) {
/*  657 */             IJ.error("Opener", "Unexpected image offset");
/*  658 */             break;
/*      */           }
/*      */         }
/*  661 */         if (fi.fileType == 12) {
/*  662 */           Object[] pixels2 = (Object[])pixels;
/*  663 */           stack.addSlice(null, pixels2[0]);
/*  664 */           stack.addSlice(null, pixels2[1]);
/*  665 */           stack.addSlice(null, pixels2[2]);
/*  666 */           this.isRGB48 = true;
/*  667 */         } else if (nChannels > 1) {
/*  668 */           for (int c = 0; c < nChannels; c++)
/*  669 */             if (channels[c] != null)
/*  670 */               stack.addSlice(null, channels[c]);
/*      */         }
/*      */         else {
/*  673 */           stack.addSlice(null, pixels);
/*  674 */         }IJ.showProgress(i, info.length);
/*      */       }
/*  676 */       is.close();
/*      */     }
/*      */     catch (Exception e) {
/*  679 */       IJ.handleException(e);
/*      */     }
/*      */     catch (OutOfMemoryError e) {
/*  682 */       IJ.outOfMemory(fi.fileName);
/*  683 */       stack.deleteLastSlice();
/*  684 */       stack.deleteLastSlice();
/*      */     }
/*  686 */     IJ.showProgress(1.0D);
/*  687 */     if (stack.getSize() == 0)
/*  688 */       return null;
/*  689 */     if ((fi.fileType == 2) || (fi.fileType == 13) || (fi.fileType == 4) || (fi.fileType == 12))
/*      */     {
/*  691 */       ImageProcessor ip = stack.getProcessor(1);
/*  692 */       ip.resetMinAndMax();
/*  693 */       stack.update(ip);
/*      */     }
/*      */ 
/*  697 */     ImagePlus imp = new ImagePlus(fi.fileName, stack);
/*  698 */     new FileOpener(fi).setCalibration(imp);
/*  699 */     imp.setFileInfo(fi);
/*  700 */     int stackSize = stack.getSize();
/*  701 */     if ((nChannels > 1) && (stackSize % nChannels == 0)) {
/*  702 */       imp.setDimensions(nChannels, stackSize / nChannels, 1);
/*  703 */       imp = new CompositeImage(imp, 1);
/*  704 */       imp.setOpenAsHyperStack(true);
/*      */     }
/*  706 */     IJ.showProgress(1.0D);
/*  707 */     return imp;
/*      */   }
/*      */ 
/*      */   public ImagePlus openTiff(String directory, String name)
/*      */   {
/*  714 */     TiffDecoder td = new TiffDecoder(directory, name);
/*  715 */     if (IJ.debugMode) td.enableDebugging();
/*  716 */     FileInfo[] info = null;
/*      */     try { info = td.getTiffInfo();
/*      */     } catch (IOException e) {
/*  719 */       String msg = e.getMessage();
/*  720 */       if ((msg == null) || (msg.equals(""))) msg = "" + e;
/*  721 */       IJ.error("TiffDecoder", msg);
/*  722 */       return null;
/*      */     }
/*  724 */     if (info == null)
/*  725 */       return null;
/*  726 */     return openTiff2(info);
/*      */   }
/*      */ 
/*      */   public ImagePlus openTiff(String path, int n)
/*      */   {
/*  731 */     TiffDecoder td = new TiffDecoder(getDir(path), getName(path));
/*  732 */     if (IJ.debugMode) td.enableDebugging();
/*  733 */     FileInfo[] info = null;
/*      */     try { info = td.getTiffInfo();
/*      */     } catch (IOException e) {
/*  736 */       String msg = e.getMessage();
/*  737 */       if ((msg == null) || (msg.equals(""))) msg = "" + e;
/*  738 */       IJ.error("TiffDecoder", msg);
/*  739 */       return null;
/*      */     }
/*  741 */     if (info == null) return null;
/*  742 */     FileInfo fi = info[0];
/*  743 */     if ((info.length == 1) && (fi.nImages > 1)) {
/*  744 */       if ((n < 1) || (n > fi.nImages))
/*  745 */         throw new IllegalArgumentException("N out of 1-" + fi.nImages + " range");
/*  746 */       long size = fi.width * fi.height * fi.getBytesPerPixel();
/*  747 */       fi.longOffset = (fi.getOffset() + (n - 1) * (size + fi.gapBetweenImages));
/*  748 */       fi.offset = 0;
/*  749 */       fi.nImages = 1;
/*      */     } else {
/*  751 */       if ((n < 1) || (n > info.length))
/*  752 */         throw new IllegalArgumentException("N out of 1-" + info.length + " range");
/*  753 */       fi.longOffset = info[(n - 1)].getOffset();
/*  754 */       fi.offset = 0;
/*  755 */       fi.stripOffsets = info[(n - 1)].stripOffsets;
/*  756 */       fi.stripLengths = info[(n - 1)].stripLengths;
/*      */     }
/*  758 */     FileOpener fo = new FileOpener(fi);
/*  759 */     return fo.open(false);
/*      */   }
/*      */ 
/*      */   public ImagePlus openTiff(InputStream in, String name)
/*      */   {
/*  765 */     FileInfo[] info = null;
/*      */     try {
/*  767 */       TiffDecoder td = new TiffDecoder(in, name);
/*  768 */       if (IJ.debugMode) td.enableDebugging();
/*  769 */       info = td.getTiffInfo();
/*      */     } catch (FileNotFoundException e) {
/*  771 */       IJ.error("TiffDecoder", "File not found: " + e.getMessage());
/*  772 */       return null;
/*      */     } catch (Exception e) {
/*  774 */       IJ.error("TiffDecoder", "" + e);
/*  775 */       return null;
/*      */     }
/*  777 */     return openTiff2(info);
/*      */   }
/*      */ 
/*      */   public ImagePlus openZip(String path)
/*      */   {
/*  783 */     ImagePlus imp = null;
/*      */     try {
/*  785 */       ZipInputStream zis = new ZipInputStream(new FileInputStream(path));
/*  786 */       if (zis == null) return null;
/*  787 */       ZipEntry entry = zis.getNextEntry();
/*  788 */       if (entry == null) return null;
/*  789 */       String name = entry.getName();
/*  790 */       if (name.endsWith(".roi")) {
/*  791 */         zis.close();
/*  792 */         if (!this.silentMode)
/*  793 */           IJ.runMacro("roiManager(\"Open\", getArgument());", path);
/*  794 */         return null;
/*      */       }
/*  796 */       if (name.endsWith(".tif")) {
/*  797 */         imp = openTiff(zis, name);
/*  798 */       } else if (name.endsWith(".dcm")) {
/*  799 */         DICOM dcm = new DICOM(zis);
/*  800 */         dcm.run(name);
/*  801 */         imp = dcm;
/*      */       } else {
/*  803 */         zis.close();
/*  804 */         IJ.error("This ZIP archive does not appear to contain a \nTIFF (\".tif\") or DICOM (\".dcm\") file, or ROIs (\".roi\").");
/*  805 */         return null;
/*      */       }
/*      */     } catch (Exception e) {
/*  808 */       IJ.error("ZipDecoder", "" + e);
/*  809 */       return null;
/*      */     }
/*  811 */     File f = new File(path);
/*  812 */     FileInfo fi = imp.getOriginalFileInfo();
/*  813 */     fi.fileFormat = 7;
/*  814 */     fi.fileName = f.getName();
/*  815 */     fi.directory = (f.getParent() + File.separator);
/*  816 */     return imp;
/*      */   }
/*      */ 
/*      */   public ImagePlus deserialize(byte[] bytes)
/*      */   {
/*  821 */     ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
/*  822 */     TiffDecoder decoder = new TiffDecoder(stream, "Untitled");
/*  823 */     if (IJ.debugMode)
/*  824 */       decoder.enableDebugging();
/*  825 */     FileInfo[] info = null;
/*      */     try {
/*  827 */       info = decoder.getTiffInfo();
/*      */     } catch (IOException e) {
/*  829 */       return null;
/*      */     }
/*  831 */     FileOpener opener = new FileOpener(info[0]);
/*  832 */     ImagePlus imp = opener.open(false);
/*  833 */     if (imp == null)
/*  834 */       return null;
/*  835 */     imp.setTitle(info[0].fileName);
/*  836 */     imp = makeComposite(imp, info[0]);
/*  837 */     return imp;
/*      */   }
/*      */ 
/*      */   private ImagePlus makeComposite(ImagePlus imp, FileInfo fi) {
/*  841 */     int c = imp.getNChannels();
/*  842 */     boolean composite = (c > 1) && (fi.description != null) && (fi.description.indexOf("mode=") != -1);
/*  843 */     if ((c > 1) && ((imp.getOpenAsHyperStack()) || (composite)) && (!imp.isComposite()) && (imp.getType() != 4)) {
/*  844 */       int mode = 2;
/*  845 */       if (fi.description != null) {
/*  846 */         if (fi.description.indexOf("mode=composite") != -1)
/*  847 */           mode = 1;
/*  848 */         else if (fi.description.indexOf("mode=gray") != -1)
/*  849 */           mode = 3;
/*      */       }
/*  851 */       imp = new CompositeImage(imp, mode);
/*      */     }
/*  853 */     return imp;
/*      */   }
/*      */ 
/*      */   public String getName(String path)
/*      */   {
/*  858 */     int i = path.lastIndexOf('/');
/*  859 */     if (i == -1)
/*  860 */       i = path.lastIndexOf('\\');
/*  861 */     if (i > 0) {
/*  862 */       return path.substring(i + 1);
/*      */     }
/*  864 */     return path;
/*      */   }
/*      */ 
/*      */   public String getDir(String path) {
/*  868 */     int i = path.lastIndexOf('/');
/*  869 */     if (i == -1)
/*  870 */       i = path.lastIndexOf('\\');
/*  871 */     if (i > 0) {
/*  872 */       return path.substring(0, i + 1);
/*      */     }
/*  874 */     return "";
/*      */   }
/*      */ 
/*      */   ImagePlus openTiff2(FileInfo[] info) {
/*  878 */     if (info == null)
/*  879 */       return null;
/*  880 */     ImagePlus imp = null;
/*  881 */     if (IJ.debugMode)
/*  882 */       IJ.log(info[0].debugInfo);
/*  883 */     if (info.length > 1) {
/*  884 */       imp = openTiffStack(info);
/*  885 */       if (imp != null)
/*  886 */         return imp;
/*      */     }
/*  888 */     FileOpener fo = new FileOpener(info[0]);
/*  889 */     imp = fo.open(false);
/*  890 */     if (imp == null) return null;
/*  891 */     int[] offsets = info[0].stripOffsets;
/*  892 */     if ((offsets != null) && (offsets.length > 1) && (offsets[(offsets.length - 1)] < offsets[0]))
/*  893 */       IJ.run(imp, "Flip Vertically", "stack");
/*  894 */     imp = makeComposite(imp, info[0]);
/*  895 */     return imp;
/*      */   }
/*      */ 
/*      */   public Roi openRoi(String path)
/*      */   {
/*  900 */     Roi roi = null;
/*  901 */     RoiDecoder rd = new RoiDecoder(path);
/*      */     try { roi = rd.getRoi();
/*      */     } catch (IOException e) {
/*  904 */       IJ.error("RoiDecoder", e.getMessage());
/*  905 */       return null;
/*      */     }
/*  907 */     return roi;
/*      */   }
/*      */ 
/*      */   public static void openResultsTable(String path)
/*      */   {
/*      */     try {
/*  913 */       ResultsTable rt = ResultsTable.open(path);
/*  914 */       if (rt != null) rt.show("Results"); 
/*      */     }
/*  916 */     catch (IOException e) { IJ.error("Open Results", e.getMessage()); }
/*      */   }
/*      */ 
/*      */   public static String getFileFormat(String path)
/*      */   {
/*  921 */     if (!new File(path).exists()) {
/*  922 */       return "not found";
/*      */     }
/*  924 */     return types[new Opener().getFileType(path)];
/*      */   }
/*      */ 
/*      */   public int getFileType(String path)
/*      */   {
/*  932 */     if ((openUsingPlugins) && (!path.endsWith(".txt")) && (!path.endsWith(".java")))
/*  933 */       return 0;
/*  934 */     File file = new File(path);
/*  935 */     String name = file.getName();
/*      */ 
/*  937 */     byte[] buf = new byte[''];
/*      */     try {
/*  939 */       InputStream is = new FileInputStream(file);
/*  940 */       is.read(buf, 0, 132);
/*  941 */       is.close();
/*      */     } catch (IOException e) {
/*  943 */       return 0;
/*      */     }
/*      */ 
/*  946 */     int b0 = buf[0] & 0xFF; int b1 = buf[1] & 0xFF; int b2 = buf[2] & 0xFF; int b3 = buf[3] & 0xFF;
/*      */ 
/*  950 */     if ((buf[''] == 68) && (buf[''] == 73) && (buf[''] == 67) && (buf[''] == 77) && (((b0 == 73) && (b1 == 73)) || ((b0 == 77) && (b1 == 77))))
/*      */     {
/*  952 */       return 14;
/*      */     }
/*      */ 
/*  955 */     if (name.endsWith(".lsm"))
/*  956 */       return 0;
/*  957 */     if ((b0 == 73) && (b1 == 73) && (b2 == 42) && (b3 == 0) && ((!bioformats) || (!name.endsWith(".flex")))) {
/*  958 */       return 1;
/*      */     }
/*      */ 
/*  961 */     if ((b0 == 77) && (b1 == 77) && (b2 == 0) && (b3 == 42)) {
/*  962 */       return 1;
/*      */     }
/*      */ 
/*  965 */     if ((b0 == 255) && (b1 == 216) && (b2 == 255)) {
/*  966 */       return 5;
/*      */     }
/*      */ 
/*  969 */     if ((b0 == 71) && (b1 == 73) && (b2 == 70) && (b3 == 56)) {
/*  970 */       return 6;
/*      */     }
/*  972 */     name = name.toLowerCase(Locale.US);
/*      */ 
/*  975 */     if (((buf[''] == 68) && (buf[''] == 73) && (buf[''] == 67) && (buf[''] == 77)) || (name.endsWith(".dcm"))) {
/*  976 */       return 2;
/*      */     }
/*      */ 
/*  980 */     if (((b0 == 8) || (b0 == 2)) && (b1 == 0) && (b3 == 0) && (!name.endsWith(".spe")) && (!name.equals("fid"))) {
/*  981 */       return 2;
/*      */     }
/*      */ 
/*  984 */     if ((b0 == 80) && ((b1 == 49) || (b1 == 52) || (b1 == 50) || (b1 == 53) || (b1 == 51) || (b1 == 54)) && ((b2 == 10) || (b2 == 13) || (b2 == 32) || (b2 == 9))) {
/*  985 */       return 4;
/*      */     }
/*      */ 
/*  988 */     if (name.endsWith(".lut")) {
/*  989 */       return 7;
/*      */     }
/*      */ 
/*  992 */     if ((b0 == 137) && (b1 == 80) && (b2 == 78) && (b3 == 71)) {
/*  993 */       return 13;
/*      */     }
/*      */ 
/*  996 */     if (name.endsWith(".zip")) {
/*  997 */       return 9;
/*      */     }
/*      */ 
/* 1000 */     if (((b0 == 83) && (b1 == 73) && (b2 == 77) && (b3 == 80)) || (name.endsWith(".fts.gz")) || (name.endsWith(".fits.gz"))) {
/* 1001 */       return 3;
/*      */     }
/*      */ 
/* 1004 */     if ((name.endsWith(".java")) || (name.endsWith(".txt")) || (name.endsWith(".ijm")) || (name.endsWith(".js")) || (name.endsWith(".html"))) {
/* 1005 */       return 10;
/*      */     }
/*      */ 
/* 1008 */     if ((b0 == 73) && (b1 == 111)) {
/* 1009 */       return 11;
/*      */     }
/*      */ 
/* 1012 */     if (((b0 == 111) && (b1 == 106) && (b2 == 106) && (b3 == 0)) || (name.endsWith(".ojj"))) {
/* 1013 */       return 17;
/*      */     }
/*      */ 
/* 1016 */     if ((name.endsWith(".xls")) || (name.endsWith(".csv"))) {
/* 1017 */       return 18;
/*      */     }
/*      */ 
/* 1020 */     boolean isText = true;
/* 1021 */     for (int i = 0; i < 10; i++) {
/* 1022 */       int c = buf[i] & 0xFF;
/* 1023 */       if (((c < 32) && (c != 9) && (c != 10) && (c != 13)) || (c > 126)) {
/* 1024 */         isText = false;
/* 1025 */         break;
/*      */       }
/*      */     }
/* 1028 */     if (isText) {
/* 1029 */       return 12;
/*      */     }
/*      */ 
/* 1032 */     if (((b0 == 66) && (b1 == 77)) || (name.endsWith(".dib"))) {
/* 1033 */       return 8;
/*      */     }
/*      */ 
/* 1036 */     if (name.endsWith(".avi")) {
/* 1037 */       return 16;
/*      */     }
/* 1039 */     return 0;
/*      */   }
/*      */ 
/*      */   ColorModel createColorModel(FileInfo fi)
/*      */   {
/* 1044 */     if ((fi.fileType == 5) && (fi.lutSize > 0)) {
/* 1045 */       return new IndexColorModel(8, fi.lutSize, fi.reds, fi.greens, fi.blues);
/*      */     }
/* 1047 */     return LookUpTable.createGrayscaleColorModel(fi.whiteIsZero);
/*      */   }
/*      */ 
/*      */   InputStream createInputStream(FileInfo fi) throws IOException, MalformedURLException
/*      */   {
/* 1052 */     if (fi.inputStream != null)
/* 1053 */       return fi.inputStream;
/* 1054 */     if ((fi.url != null) && (!fi.url.equals(""))) {
/* 1055 */       return new URL(fi.url + fi.fileName).openStream();
/*      */     }
/* 1057 */     File f = new File(fi.directory + fi.fileName);
/* 1058 */     if ((f == null) || (f.isDirectory())) {
/* 1059 */       return null;
/*      */     }
/* 1061 */     InputStream is = new FileInputStream(f);
/* 1062 */     if (fi.compression >= 2)
/* 1063 */       is = new RandomAccessStream(is);
/* 1064 */     return is;
/*      */   }
/*      */ 
/*      */   void openRGB48(ImagePlus imp)
/*      */   {
/* 1070 */     this.isRGB48 = false;
/* 1071 */     int stackSize = imp.getStackSize();
/* 1072 */     imp.setDimensions(3, stackSize / 3, 1);
/* 1073 */     imp = new CompositeImage(imp, 1);
/* 1074 */     imp.show();
/*      */   }
/*      */ 
/*      */   public void setSilentMode(boolean mode)
/*      */   {
/* 1079 */     this.silentMode = mode;
/*      */   }
/*      */ 
/*      */   public static void setOpenUsingPlugins(boolean b)
/*      */   {
/* 1085 */     openUsingPlugins = b;
/*      */   }
/*      */ 
/*      */   public static boolean getOpenUsingPlugins()
/*      */   {
/* 1090 */     return openUsingPlugins;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*   46 */     Hashtable commands = Menus.getCommands();
/*      */   }
/*      */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.io.Opener
 * JD-Core Version:    0.6.2
 */