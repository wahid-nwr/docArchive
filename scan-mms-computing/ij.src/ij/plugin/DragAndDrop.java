/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImageJ;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.gui.Toolbar;
/*     */ import ij.io.DirectoryChooser;
/*     */ import ij.io.OpenDialog;
/*     */ import ij.io.Opener;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Point;
/*     */ import java.awt.datatransfer.DataFlavor;
/*     */ import java.awt.datatransfer.Transferable;
/*     */ import java.awt.dnd.DropTarget;
/*     */ import java.awt.dnd.DropTargetDragEvent;
/*     */ import java.awt.dnd.DropTargetDropEvent;
/*     */ import java.awt.dnd.DropTargetEvent;
/*     */ import java.awt.dnd.DropTargetListener;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.StringReader;
/*     */ import java.net.URLDecoder;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ 
/*     */ public class DragAndDrop
/*     */   implements PlugIn, DropTargetListener, Runnable
/*     */ {
/*     */   private Iterator iterator;
/*     */   private static boolean convertToRGB;
/*     */   private static boolean virtualStack;
/*     */   private boolean openAsVirtualStack;
/*     */ 
/*     */   public void run(String arg)
/*     */   {
/*  27 */     ImageJ ij = IJ.getInstance();
/*  28 */     ij.setDropTarget(null);
/*  29 */     new DropTarget(ij, this);
/*  30 */     new DropTarget(Toolbar.getInstance(), this);
/*  31 */     new DropTarget(ij.getStatusBar(), this);
/*     */   }
/*     */ 
/*     */   public void drop(DropTargetDropEvent dtde) {
/*  35 */     dtde.acceptDrop(1);
/*  36 */     DataFlavor[] flavors = null;
/*     */     try {
/*  38 */       Transferable t = dtde.getTransferable();
/*  39 */       this.iterator = null;
/*  40 */       flavors = t.getTransferDataFlavors();
/*  41 */       if (IJ.debugMode) IJ.log("DragAndDrop.drop: " + flavors.length + " flavors");
/*  42 */       for (int i = 0; i < flavors.length; i++) {
/*  43 */         if (IJ.debugMode) IJ.log("  flavor[" + i + "]: " + flavors[i].getMimeType());
/*  44 */         if (flavors[i].isFlavorJavaFileListType()) {
/*  45 */           Object data = t.getTransferData(DataFlavor.javaFileListFlavor);
/*  46 */           this.iterator = ((List)data).iterator();
/*  47 */           break;
/*  48 */         }if (flavors[i].isFlavorTextType()) {
/*  49 */           Object ob = t.getTransferData(flavors[i]);
/*  50 */           if ((ob instanceof String)) {
/*  51 */             String s = ob.toString().trim();
/*  52 */             if ((IJ.isLinux()) && (s.length() > 1) && (s.charAt(1) == 0))
/*  53 */               s = fixLinuxString(s);
/*  54 */             ArrayList list = new ArrayList();
/*  55 */             if ((s.indexOf("href=\"") != -1) || (s.indexOf("src=\"") != -1)) {
/*  56 */               s = parseHTML(s);
/*  57 */               if (IJ.debugMode) IJ.log("  url: " + s);
/*  58 */               list.add(s);
/*  59 */               this.iterator = list.iterator();
/*  60 */               break;
/*     */             }
/*  62 */             BufferedReader br = new BufferedReader(new StringReader(s));
/*     */             String tmp;
/*  64 */             while (null != (tmp = br.readLine())) {
/*  65 */               tmp = URLDecoder.decode(tmp.replaceAll("\\+", "%2b"), "UTF-8");
/*  66 */               if (tmp.startsWith("file://")) tmp = tmp.substring(7);
/*  67 */               if (IJ.debugMode) IJ.log("  content: " + tmp);
/*  68 */               if (tmp.startsWith("http://"))
/*  69 */                 list.add(s);
/*     */               else
/*  71 */                 list.add(new File(tmp));
/*     */             }
/*  73 */             this.iterator = list.iterator();
/*  74 */             break;
/*     */           }
/*     */         }
/*     */       }
/*  77 */       if (this.iterator != null) {
/*  78 */         Thread thread = new Thread(this, "DrawAndDrop");
/*  79 */         thread.setPriority(Math.max(thread.getPriority() - 1, 1));
/*  80 */         thread.start();
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/*  84 */       dtde.dropComplete(false);
/*  85 */       return;
/*     */     }
/*  87 */     dtde.dropComplete(true);
/*  88 */     if ((flavors == null) || (flavors.length == 0))
/*  89 */       if (IJ.isMacOSX()) {
/*  90 */         IJ.error("First drag and drop ignored. Please try again. You can avoid this\nproblem by dragging to the toolbar instead of the status bar.");
/*     */       }
/*     */       else
/*  93 */         IJ.error("Drag and drop failed");
/*     */   }
/*     */ 
/*     */   private String fixLinuxString(String s)
/*     */   {
/*  98 */     StringBuffer sb = new StringBuffer(200);
/*  99 */     for (int i = 0; i < s.length(); i += 2)
/* 100 */       sb.append(s.charAt(i));
/* 101 */     return new String(sb);
/*     */   }
/*     */ 
/*     */   private String parseHTML(String s) {
/* 105 */     if (IJ.debugMode) IJ.log("parseHTML:\n" + s);
/* 106 */     int index1 = s.indexOf("src=\"");
/* 107 */     if (index1 >= 0) {
/* 108 */       int index2 = s.indexOf("\"", index1 + 5);
/* 109 */       if (index2 > 0)
/* 110 */         return s.substring(index1 + 5, index2);
/*     */     }
/* 112 */     index1 = s.indexOf("href=\"");
/* 113 */     if (index1 >= 0) {
/* 114 */       int index2 = s.indexOf("\"", index1 + 6);
/* 115 */       if (index2 > 0)
/* 116 */         return s.substring(index1 + 6, index2);
/*     */     }
/* 118 */     return s;
/*     */   }
/*     */ 
/*     */   public void dragEnter(DropTargetDragEvent e) {
/* 122 */     IJ.showStatus("<<Drag and Drop>>");
/* 123 */     if (IJ.debugMode) IJ.log("DragEnter: " + e.getLocation());
/* 124 */     e.acceptDrag(1);
/* 125 */     this.openAsVirtualStack = false;
/*     */   }
/*     */ 
/*     */   public void dragOver(DropTargetDragEvent e) {
/* 129 */     if (IJ.debugMode) IJ.log("DragOver: " + e.getLocation());
/* 130 */     Point loc = e.getLocation();
/* 131 */     int buttonSize = Toolbar.getButtonSize();
/* 132 */     int width = IJ.getInstance().getSize().width;
/* 133 */     this.openAsVirtualStack = (width - loc.x <= buttonSize);
/* 134 */     if (this.openAsVirtualStack)
/* 135 */       IJ.showStatus("<<Open as Virtual Stack>>");
/*     */     else
/* 137 */       IJ.showStatus("<<Drag and Drop>>");
/*     */   }
/*     */ 
/*     */   public void dragExit(DropTargetEvent e) {
/* 141 */     IJ.showStatus("");
/*     */   }
/*     */   public void dropActionChanged(DropTargetDragEvent e) {
/*     */   }
/*     */   public void run() {
/* 146 */     Iterator iterator = this.iterator;
/* 147 */     while (iterator.hasNext()) {
/* 148 */       Object obj = iterator.next();
/* 149 */       if ((obj != null) && ((obj instanceof String)))
/* 150 */         openURL((String)obj);
/*     */       else
/* 152 */         openFile((File)obj);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void openURL(String url)
/*     */   {
/* 158 */     if (IJ.debugMode) IJ.log("DragAndDrop.openURL: " + url);
/* 159 */     if (url != null)
/* 160 */       IJ.open(url);
/*     */   }
/*     */ 
/*     */   public void openFile(File f)
/*     */   {
/* 165 */     if (IJ.debugMode) IJ.log("DragAndDrop.openFile: " + f); try
/*     */     {
/* 167 */       if (null == f) return;
/* 168 */       String path = f.getCanonicalPath();
/* 169 */       if (f.exists()) {
/* 170 */         if (f.isDirectory()) {
/* 171 */           openDirectory(f, path);
/*     */         } else {
/* 173 */           if ((this.openAsVirtualStack) && ((path.endsWith(".tif")) || (path.endsWith(".TIF"))))
/* 174 */             new FileInfoVirtualStack().run(path);
/*     */           else
/* 176 */             new Opener().openAndAddToRecent(path);
/* 177 */           OpenDialog.setLastDirectory(f.getParent() + File.separator);
/* 178 */           OpenDialog.setLastName(f.getName());
/*     */         }
/*     */       }
/* 181 */       else IJ.log("File not found: " + path); 
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 184 */       if (!"Macro canceled".equals(e.getMessage()))
/* 185 */         IJ.handleException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void openDirectory(File f, String path) {
/* 190 */     if (path == null) return;
/* 191 */     if ((!path.endsWith(File.separator)) && (!path.endsWith("/")))
/* 192 */       path = path + File.separator;
/* 193 */     String[] names = f.list();
/* 194 */     names = new FolderOpener().trimFileList(names);
/* 195 */     if (names == null)
/* 196 */       return;
/* 197 */     String msg = "Open all " + names.length + " images in \"" + f.getName() + "\" as a stack?";
/* 198 */     GenericDialog gd = new GenericDialog("Open Folder");
/* 199 */     gd.setInsets(10, 5, 0);
/* 200 */     gd.addMessage(msg);
/* 201 */     gd.setInsets(15, 35, 0);
/* 202 */     gd.addCheckbox("Convert to RGB", convertToRGB);
/* 203 */     gd.setInsets(0, 35, 0);
/* 204 */     gd.addCheckbox("Use Virtual Stack", virtualStack);
/* 205 */     gd.enableYesNoCancel();
/* 206 */     gd.showDialog();
/* 207 */     if (gd.wasCanceled()) return;
/* 208 */     if (gd.wasOKed()) {
/* 209 */       convertToRGB = gd.getNextBoolean();
/* 210 */       virtualStack = gd.getNextBoolean();
/* 211 */       String options = " sort";
/* 212 */       if (convertToRGB) options = options + " convert_to_rgb";
/* 213 */       if (virtualStack) options = options + " use";
/* 214 */       IJ.run("Image Sequence...", "open=[" + path + "]" + options);
/* 215 */       DirectoryChooser.setDefaultDirectory(path);
/*     */     } else {
/* 217 */       for (int k = 0; k < names.length; k++) {
/* 218 */         IJ.redirectErrorMessages();
/* 219 */         if (!names[k].startsWith("."))
/* 220 */           new Opener().open(path + names[k]);
/*     */       }
/*     */     }
/* 223 */     IJ.register(DragAndDrop.class);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.DragAndDrop
 * JD-Core Version:    0.6.2
 */