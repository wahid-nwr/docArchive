/*     */ package ij;
/*     */ 
/*     */ import ij.gui.HistogramWindow;
/*     */ import ij.gui.ImageWindow;
/*     */ import ij.gui.PlotWindow;
/*     */ import ij.macro.Interpreter;
/*     */ import ij.plugin.frame.Editor;
/*     */ import ij.plugin.frame.PlugInFrame;
/*     */ import ij.plugin.frame.Recorder;
/*     */ import ij.text.TextWindow;
/*     */ import java.awt.CheckboxMenuItem;
/*     */ import java.awt.Frame;
/*     */ import java.awt.Menu;
/*     */ import java.awt.MenuItem;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class WindowManager
/*     */ {
/*     */   public static boolean checkForDuplicateName;
/*  16 */   private static Vector imageList = new Vector();
/*  17 */   private static Vector nonImageList = new Vector();
/*     */   private static ImageWindow currentWindow;
/*     */   private static Frame frontWindow;
/*  20 */   private static Hashtable tempImageTable = new Hashtable();
/*     */ 
/*     */   public static void setCurrentWindow(ImageWindow win)
/*     */   {
/*  27 */     if ((win == null) || (win.isClosed()) || (win.getImagePlus() == null)) {
/*  28 */       return;
/*     */     }
/*  30 */     setWindow(win);
/*  31 */     tempImageTable.remove(Thread.currentThread());
/*  32 */     if ((win == currentWindow) || (imageList.size() == 0))
/*  33 */       return;
/*  34 */     if (currentWindow != null)
/*     */     {
/*  36 */       ImagePlus imp = currentWindow.getImagePlus();
/*  37 */       if (imp != null) {
/*  38 */         if (!Prefs.keepUndoBuffers)
/*  39 */           imp.trimProcessor();
/*  40 */         imp.saveRoi();
/*     */       }
/*     */     }
/*  43 */     Undo.reset();
/*  44 */     currentWindow = win;
/*  45 */     Menus.updateMenus();
/*  46 */     if ((Recorder.record) && (!IJ.isMacro()))
/*  47 */       Recorder.record("selectWindow", win.getImagePlus().getTitle());
/*     */   }
/*     */ 
/*     */   public static ImageWindow getCurrentWindow()
/*     */   {
/*  53 */     return currentWindow;
/*     */   }
/*     */ 
/*     */   static int getCurrentIndex() {
/*  57 */     return imageList.indexOf(currentWindow);
/*     */   }
/*     */ 
/*     */   public static ImagePlus getCurrentImage()
/*     */   {
/*  62 */     ImagePlus img = (ImagePlus)tempImageTable.get(Thread.currentThread());
/*     */ 
/*  64 */     if (img == null) {
/*  65 */       img = getActiveImage();
/*     */     }
/*  67 */     return img;
/*     */   }
/*     */ 
/*     */   public static void setTempCurrentImage(ImagePlus img)
/*     */   {
/*  75 */     if (img == null)
/*  76 */       tempImageTable.remove(Thread.currentThread());
/*     */     else
/*  78 */       tempImageTable.put(Thread.currentThread(), img);
/*     */   }
/*     */ 
/*     */   public static void setTempCurrentImage(Thread thread, ImagePlus img)
/*     */   {
/*  83 */     if (thread == null)
/*  84 */       throw new RuntimeException("thread==null");
/*  85 */     if (img == null)
/*  86 */       tempImageTable.remove(thread);
/*     */     else
/*  88 */       tempImageTable.put(thread, img);
/*     */   }
/*     */ 
/*     */   private static ImagePlus getActiveImage()
/*     */   {
/*  93 */     if (currentWindow != null)
/*  94 */       return currentWindow.getImagePlus();
/*  95 */     if ((frontWindow != null) && ((frontWindow instanceof ImageWindow)))
/*  96 */       return ((ImageWindow)frontWindow).getImagePlus();
/*  97 */     if (imageList.size() > 0) {
/*  98 */       ImageWindow win = (ImageWindow)imageList.elementAt(imageList.size() - 1);
/*  99 */       return win.getImagePlus();
/*     */     }
/* 101 */     return Interpreter.getLastBatchModeImage();
/*     */   }
/*     */ 
/*     */   public static int getWindowCount()
/*     */   {
/* 106 */     int count = imageList.size();
/* 107 */     return count;
/*     */   }
/*     */ 
/*     */   public static int getImageCount()
/*     */   {
/* 112 */     int count = imageList.size();
/* 113 */     count += Interpreter.getBatchModeImageCount();
/* 114 */     if ((count == 0) && (getCurrentImage() != null))
/* 115 */       count = 1;
/* 116 */     return count;
/*     */   }
/*     */ 
/*     */   public static Frame getFrontWindow()
/*     */   {
/* 121 */     return frontWindow;
/*     */   }
/*     */ 
/*     */   public static synchronized int[] getIDList()
/*     */   {
/* 127 */     int nWindows = imageList.size();
/* 128 */     int[] batchModeImages = Interpreter.getBatchModeImageIDs();
/* 129 */     int nBatchImages = batchModeImages.length;
/* 130 */     if (nWindows + nBatchImages == 0)
/* 131 */       return null;
/* 132 */     int[] list = new int[nWindows + nBatchImages];
/* 133 */     for (int i = 0; i < nBatchImages; i++)
/* 134 */       list[i] = batchModeImages[i];
/* 135 */     int index = 0;
/* 136 */     for (int i = nBatchImages; i < nBatchImages + nWindows; i++) {
/* 137 */       ImageWindow win = (ImageWindow)imageList.elementAt(index++);
/* 138 */       list[i] = win.getImagePlus().getID();
/*     */     }
/* 140 */     return list;
/*     */   }
/*     */ 
/*     */   public static synchronized Frame[] getNonImageWindows()
/*     */   {
/* 145 */     Frame[] list = new Frame[nonImageList.size()];
/* 146 */     nonImageList.copyInto((Frame[])list);
/* 147 */     return list;
/*     */   }
/*     */ 
/*     */   public static synchronized ImagePlus getImage(int imageID)
/*     */   {
/* 156 */     if (imageID > 0)
/* 157 */       imageID = getNthImageID(imageID);
/* 158 */     if ((imageID == 0) || (getImageCount() == 0))
/* 159 */       return null;
/* 160 */     ImagePlus imp2 = Interpreter.getBatchModeImage(imageID);
/* 161 */     if (imp2 != null)
/* 162 */       return imp2;
/* 163 */     ImagePlus imp = null;
/* 164 */     for (int i = 0; i < imageList.size(); i++) {
/* 165 */       ImageWindow win = (ImageWindow)imageList.elementAt(i);
/* 166 */       imp2 = win.getImagePlus();
/* 167 */       if (imageID == imp2.getID()) {
/* 168 */         imp = imp2;
/* 169 */         break;
/*     */       }
/*     */     }
/* 172 */     imp2 = getCurrentImage();
/* 173 */     if ((imp == null) && (imp2 != null) && (imp2.getID() == imageID))
/* 174 */       return imp2;
/* 175 */     return imp;
/*     */   }
/*     */ 
/*     */   public static synchronized int getNthImageID(int n)
/*     */   {
/* 181 */     if (n <= 0) return 0;
/* 182 */     if (Interpreter.isBatchMode()) {
/* 183 */       int[] list = getIDList();
/* 184 */       if (n > list.length) {
/* 185 */         return 0;
/*     */       }
/* 187 */       return list[(n - 1)];
/*     */     }
/* 189 */     if (n > imageList.size()) return 0;
/* 190 */     ImageWindow win = (ImageWindow)imageList.elementAt(n - 1);
/* 191 */     if (win != null) {
/* 192 */       return win.getImagePlus().getID();
/*     */     }
/* 194 */     return 0;
/*     */   }
/*     */ 
/*     */   public static synchronized ImagePlus getImage(String title)
/*     */   {
/* 201 */     int[] wList = getIDList();
/* 202 */     if (wList == null) return null;
/* 203 */     for (int i = 0; i < wList.length; i++) {
/* 204 */       ImagePlus imp = getImage(wList[i]);
/* 205 */       if ((imp != null) && (imp.getTitle().equals(title)))
/* 206 */         return imp;
/*     */     }
/* 208 */     return null;
/*     */   }
/*     */ 
/*     */   public static synchronized void addWindow(Frame win)
/*     */   {
/* 214 */     if (win == null)
/* 215 */       return;
/* 216 */     if ((win instanceof ImageWindow)) {
/* 217 */       addImageWindow((ImageWindow)win);
/*     */     } else {
/* 219 */       Menus.insertWindowMenuItem(win);
/* 220 */       nonImageList.addElement(win);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void addImageWindow(ImageWindow win) {
/* 225 */     ImagePlus imp = win.getImagePlus();
/* 226 */     if (imp == null) return;
/* 227 */     checkForDuplicateName(imp);
/* 228 */     imageList.addElement(win);
/* 229 */     Menus.addWindowMenuItem(imp);
/* 230 */     setCurrentWindow(win);
/*     */   }
/*     */ 
/*     */   static void checkForDuplicateName(ImagePlus imp) {
/* 234 */     if (checkForDuplicateName) {
/* 235 */       String name = imp.getTitle();
/* 236 */       if (isDuplicateName(name))
/* 237 */         imp.setTitle(getUniqueName(name));
/*     */     }
/* 239 */     checkForDuplicateName = false;
/*     */   }
/*     */ 
/*     */   static boolean isDuplicateName(String name) {
/* 243 */     int n = imageList.size();
/* 244 */     for (int i = 0; i < n; i++) {
/* 245 */       ImageWindow win = (ImageWindow)imageList.elementAt(i);
/* 246 */       String name2 = win.getImagePlus().getTitle();
/* 247 */       if (name.equals(name2))
/* 248 */         return true;
/*     */     }
/* 250 */     return false;
/*     */   }
/*     */ 
/*     */   public static String getUniqueName(String name)
/*     */   {
/* 255 */     String name2 = name;
/* 256 */     String extension = "";
/* 257 */     int len = name2.length();
/* 258 */     int lastDot = name2.lastIndexOf(".");
/* 259 */     if ((lastDot != -1) && (len - lastDot < 6) && (lastDot != len - 1)) {
/* 260 */       extension = name2.substring(lastDot, len);
/* 261 */       name2 = name2.substring(0, lastDot);
/*     */     }
/* 263 */     int lastDash = name2.lastIndexOf("-");
/* 264 */     len = name2.length();
/* 265 */     if ((lastDash != -1) && (len - lastDash < 4) && (lastDash < len - 1) && (Character.isDigit(name2.charAt(lastDash + 1))) && (name2.charAt(lastDash + 1) != '0'))
/* 266 */       name2 = name2.substring(0, lastDash);
/* 267 */     for (int i = 1; i <= 99; i++) {
/* 268 */       String name3 = name2 + "-" + i + extension;
/*     */ 
/* 270 */       if (!isDuplicateName(name3))
/* 271 */         return name3;
/*     */     }
/* 273 */     return name;
/*     */   }
/*     */ 
/*     */   public static String makeUniqueName(String name)
/*     */   {
/* 278 */     return isDuplicateName(name) ? getUniqueName(name) : name;
/*     */   }
/*     */ 
/*     */   public static synchronized void removeWindow(Frame win)
/*     */   {
/* 284 */     if ((win instanceof ImageWindow)) {
/* 285 */       removeImageWindow((ImageWindow)win);
/*     */     } else {
/* 287 */       int index = nonImageList.indexOf(win);
/* 288 */       ImageJ ij = IJ.getInstance();
/* 289 */       if (index >= 0)
/*     */       {
/* 291 */         Menus.removeWindowMenuItem(index);
/* 292 */         nonImageList.removeElement(win);
/*     */       }
/*     */     }
/* 295 */     setWindow(null);
/*     */   }
/*     */ 
/*     */   private static void removeImageWindow(ImageWindow win) {
/* 299 */     int index = imageList.indexOf(win);
/* 300 */     if (index == -1)
/* 301 */       return;
/* 302 */     if (imageList.size() > 1) {
/* 303 */       int newIndex = index - 1;
/* 304 */       if (newIndex < 0)
/* 305 */         newIndex = imageList.size() - 1;
/* 306 */       setCurrentWindow((ImageWindow)imageList.elementAt(newIndex));
/*     */     } else {
/* 308 */       currentWindow = null;
/* 309 */     }imageList.removeElementAt(index);
/* 310 */     setTempCurrentImage(null);
/* 311 */     int nonImageCount = nonImageList.size();
/* 312 */     if (nonImageCount > 0)
/* 313 */       nonImageCount++;
/* 314 */     Menus.removeWindowMenuItem(nonImageCount + index);
/* 315 */     Menus.updateMenus();
/* 316 */     Undo.reset();
/*     */   }
/*     */ 
/*     */   public static void setWindow(Frame win)
/*     */   {
/* 321 */     frontWindow = win;
/*     */   }
/*     */ 
/*     */   public static synchronized boolean closeAllWindows()
/*     */   {
/* 327 */     while (imageList.size() > 0) {
/* 328 */       if (!((ImageWindow)imageList.elementAt(0)).close())
/* 329 */         return false;
/* 330 */       IJ.wait(100);
/*     */     }
/* 332 */     Frame[] nonImages = getNonImageWindows();
/* 333 */     for (int i = 0; i < nonImages.length; i++) {
/* 334 */       Frame frame = nonImages[i];
/* 335 */       if ((frame != null) && ((frame instanceof Editor))) {
/* 336 */         ((Editor)frame).close();
/* 337 */         if (((Editor)frame).fileChanged())
/* 338 */           return false;
/* 339 */         IJ.wait(100);
/*     */       }
/*     */     }
/* 342 */     ImageJ ij = IJ.getInstance();
/* 343 */     if ((ij != null) && (ij.quitting()) && (IJ.getApplet() == null))
/* 344 */       return true;
/* 345 */     for (int i = 0; i < nonImages.length; i++) {
/* 346 */       Frame frame = nonImages[i];
/* 347 */       if (((frame instanceof PlugInFrame)) && (!(frame instanceof Editor)))
/* 348 */         ((PlugInFrame)frame).close();
/* 349 */       else if ((frame instanceof TextWindow)) {
/* 350 */         ((TextWindow)frame).close();
/*     */       }
/*     */       else {
/* 353 */         frame.dispose();
/*     */       }
/*     */     }
/* 356 */     return true;
/*     */   }
/*     */ 
/*     */   public static void putBehind()
/*     */   {
/* 361 */     if (IJ.debugMode) IJ.log("putBehind");
/* 362 */     if ((imageList.size() < 1) || (currentWindow == null))
/* 363 */       return;
/* 364 */     int index = imageList.indexOf(currentWindow);
/* 365 */     ImageWindow win = null;
/* 366 */     int count = 0;
/*     */     do {
/* 368 */       index--;
/* 369 */       if (index < 0) index = imageList.size() - 1;
/* 370 */       win = (ImageWindow)imageList.elementAt(index);
/* 371 */       count++; if (count == imageList.size()) return; 
/*     */     }
/* 372 */     while (((win instanceof HistogramWindow)) || ((win instanceof PlotWindow)));
/* 373 */     if (win == null) return;
/* 374 */     ImagePlus imp = win.getImagePlus();
/* 375 */     if (imp != null)
/* 376 */       IJ.selectWindow(imp.getID());
/*     */   }
/*     */ 
/*     */   public static ImagePlus getTempCurrentImage()
/*     */   {
/* 381 */     return (ImagePlus)tempImageTable.get(Thread.currentThread());
/*     */   }
/*     */ 
/*     */   public static Frame getFrame(String title)
/*     */   {
/* 387 */     for (int i = 0; i < nonImageList.size(); i++) {
/* 388 */       Frame frame = (Frame)nonImageList.elementAt(i);
/* 389 */       if (title.equals(frame.getTitle()))
/* 390 */         return frame;
/*     */     }
/* 392 */     int[] wList = getIDList();
/* 393 */     int len = wList != null ? wList.length : 0;
/* 394 */     for (int i = 0; i < len; i++) {
/* 395 */       ImagePlus imp = getImage(wList[i]);
/* 396 */       if ((imp != null) && 
/* 397 */         (imp.getTitle().equals(title))) {
/* 398 */         return imp.getWindow();
/*     */       }
/*     */     }
/* 401 */     return null;
/*     */   }
/*     */ 
/*     */   static synchronized void activateWindow(String menuItemLabel, MenuItem item)
/*     */   {
/* 407 */     for (int i = 0; i < nonImageList.size(); i++) {
/* 408 */       Frame win = (Frame)nonImageList.elementAt(i);
/* 409 */       String title = win.getTitle();
/* 410 */       if (menuItemLabel.equals(title)) {
/* 411 */         toFront(win);
/* 412 */         ((CheckboxMenuItem)item).setState(false);
/* 413 */         if ((Recorder.record) && (!IJ.isMacro()))
/* 414 */           Recorder.record("selectWindow", title);
/* 415 */         return;
/*     */       }
/*     */     }
/* 418 */     int lastSpace = menuItemLabel.lastIndexOf(' ');
/* 419 */     if (lastSpace > 0)
/* 420 */       menuItemLabel = menuItemLabel.substring(0, lastSpace);
/* 421 */     for (int i = 0; i < imageList.size(); i++) {
/* 422 */       ImageWindow win = (ImageWindow)imageList.elementAt(i);
/* 423 */       String title = win.getImagePlus().getTitle();
/* 424 */       if (menuItemLabel.equals(title)) {
/* 425 */         setCurrentWindow(win);
/* 426 */         toFront(win);
/* 427 */         int index = imageList.indexOf(win);
/* 428 */         int n = Menus.window.getItemCount();
/* 429 */         int start = 5 + Menus.windowMenuItems2;
/* 430 */         for (int j = start; j < n; j++) {
/* 431 */           MenuItem mi = Menus.window.getItem(j);
/* 432 */           ((CheckboxMenuItem)mi).setState(j - start == index);
/*     */         }
/* 434 */         break;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static synchronized void repaintImageWindows()
/*     */   {
/* 441 */     int[] list = getIDList();
/* 442 */     if (list == null) return;
/* 443 */     for (int i = 0; i < list.length; i++) {
/* 444 */       ImagePlus imp2 = getImage(list[i]);
/* 445 */       if (imp2 != null) {
/* 446 */         imp2.setTitle(imp2.getTitle());
/* 447 */         ImageWindow win = imp2.getWindow();
/* 448 */         if (win != null) win.repaint(); 
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   static void showList()
/*     */   {
/* 454 */     if (IJ.debugMode) {
/* 455 */       for (int i = 0; i < imageList.size(); i++) {
/* 456 */         ImageWindow win = (ImageWindow)imageList.elementAt(i);
/* 457 */         ImagePlus imp = win.getImagePlus();
/* 458 */         IJ.log(i + " " + imp.getTitle() + (win == currentWindow ? "*" : ""));
/*     */       }
/* 460 */       IJ.log(" ");
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void toFront(Frame frame) {
/* 465 */     if (frame == null) return;
/* 466 */     if (frame.getState() == 1)
/* 467 */       frame.setState(0);
/* 468 */     frame.toFront();
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.WindowManager
 * JD-Core Version:    0.6.2
 */