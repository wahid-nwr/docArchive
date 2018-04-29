/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.Macro;
/*     */ import ij.Prefs;
/*     */ import ij.Undo;
/*     */ import ij.WindowManager;
/*     */ import ij.gui.Arrow;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.gui.ImageCanvas;
/*     */ import ij.gui.ImageRoi;
/*     */ import ij.gui.Line;
/*     */ import ij.gui.Overlay;
/*     */ import ij.gui.PointRoi;
/*     */ import ij.gui.PolygonRoi;
/*     */ import ij.gui.Roi;
/*     */ import ij.gui.RoiProperties;
/*     */ import ij.macro.Interpreter;
/*     */ import ij.plugin.frame.RoiManager;
/*     */ import ij.process.ImageProcessor;
/*     */ import java.awt.Color;
/*     */ import java.awt.Frame;
/*     */ import java.awt.Rectangle;
/*     */ 
/*     */ public class OverlayCommands
/*     */   implements PlugIn
/*     */ {
/*  13 */   private static int opacity = 100;
/*     */ 
/*  17 */   private static Roi defaultRoi = new Roi(0, 0, 1, 1);
/*     */ 
/*     */   public void run(String arg)
/*     */   {
/*  22 */     if (arg.equals("add"))
/*  23 */       addSelection();
/*  24 */     else if (arg.equals("image"))
/*  25 */       addImage(false);
/*  26 */     else if (arg.equals("image-roi"))
/*  27 */       addImage(true);
/*  28 */     else if (arg.equals("flatten"))
/*  29 */       flatten();
/*  30 */     else if (arg.equals("hide"))
/*  31 */       hide();
/*  32 */     else if (arg.equals("show"))
/*  33 */       show();
/*  34 */     else if (arg.equals("remove"))
/*  35 */       remove();
/*  36 */     else if (arg.equals("from"))
/*  37 */       fromRoiManager();
/*  38 */     else if (arg.equals("to"))
/*  39 */       toRoiManager();
/*  40 */     else if (arg.equals("options"))
/*  41 */       options();
/*     */   }
/*     */ 
/*     */   void addSelection() {
/*  45 */     ImagePlus imp = IJ.getImage();
/*  46 */     String macroOptions = Macro.getOptions();
/*  47 */     if ((macroOptions != null) && (IJ.macroRunning()) && (macroOptions.indexOf("remove") != -1)) {
/*  48 */       imp.setOverlay(null);
/*  49 */       return;
/*     */     }
/*  51 */     Roi roi = imp.getRoi();
/*  52 */     if ((roi == null) && (imp.getOverlay() != null)) {
/*  53 */       GenericDialog gd = new GenericDialog("No Selection");
/*  54 */       gd.addMessage("\"Overlay>Add\" requires a selection.");
/*  55 */       gd.setInsets(15, 40, 0);
/*  56 */       gd.addCheckbox("Remove existing overlay", false);
/*  57 */       gd.showDialog();
/*  58 */       if (gd.wasCanceled()) return;
/*  59 */       if (gd.getNextBoolean()) imp.setOverlay(null);
/*  60 */       return;
/*     */     }
/*  62 */     if (roi == null) {
/*  63 */       IJ.error("This command requires a selection.");
/*  64 */       return;
/*     */     }
/*  66 */     roi = (Roi)roi.clone();
/*  67 */     Overlay overlay = imp.getOverlay();
/*  68 */     if (!roi.isDrawingTool()) {
/*  69 */       if (roi.getStroke() == null)
/*  70 */         roi.setStrokeWidth(defaultRoi.getStrokeWidth());
/*  71 */       if ((roi.getStrokeColor() == null) || ((Line.getWidth() > 1) && (defaultRoi.getStrokeColor() != null)))
/*  72 */         roi.setStrokeColor(defaultRoi.getStrokeColor());
/*  73 */       if (roi.getFillColor() == null)
/*  74 */         roi.setFillColor(defaultRoi.getFillColor());
/*     */     }
/*  76 */     boolean setPos = defaultRoi.getPosition() != 0;
/*  77 */     if ((setPos) && (imp.getStackSize() > 1)) {
/*  78 */       if ((imp.isHyperStack()) || (imp.isComposite()))
/*  79 */         roi.setPosition(0, imp.getSlice(), imp.getFrame());
/*     */       else
/*  81 */         roi.setPosition(imp.getCurrentSlice());
/*     */     }
/*  83 */     int width = Line.getWidth();
/*  84 */     Rectangle bounds = roi.getBounds();
/*  85 */     boolean tooWide = width > Math.max(bounds.width, bounds.height) / 3.0D;
/*  86 */     if ((roi.getStroke() == null) && (width > 1) && (!tooWide)) {
/*  87 */       roi.setStrokeWidth(Line.getWidth());
/*     */     }
/*     */ 
/*  90 */     boolean points = ((roi instanceof PointRoi)) && (((PolygonRoi)roi).getNCoordinates() > 1);
/*     */ 
/*  92 */     if ((IJ.altKeyDown()) || ((IJ.macroRunning()) && (Macro.getOptions() != null))) {
/*  93 */       RoiProperties rp = new RoiProperties("Add to Overlay", roi);
/*  94 */       if (!rp.showDialog()) return;
/*     */     }
/*  96 */     String name = roi.getName();
/*  97 */     boolean newOverlay = (name != null) && (name.equals("new-overlay"));
/*  98 */     if ((overlay == null) || (newOverlay)) overlay = OverlayLabels.createOverlay();
/*  99 */     overlay.add(roi);
/* 100 */     defaultRoi = (Roi)roi.clone();
/* 101 */     defaultRoi.setPosition(setPos ? 1 : 0);
/* 102 */     imp.setOverlay(overlay);
/* 103 */     if ((points) || ((roi instanceof ImageRoi)) || ((roi instanceof Arrow))) imp.killRoi();
/* 104 */     Undo.setup(7, imp);
/*     */   }
/*     */ 
/*     */   void addImage(boolean createImageRoi) {
/* 108 */     ImagePlus imp = IJ.getImage();
/* 109 */     int[] wList = WindowManager.getIDList();
/* 110 */     if ((wList == null) || (wList.length < 2)) {
/* 111 */       IJ.error("Add Image...", "The command requires at least two open images.");
/* 112 */       return;
/*     */     }
/* 114 */     String[] titles = new String[wList.length];
/* 115 */     for (int i = 0; i < wList.length; i++) {
/* 116 */       ImagePlus imp2 = WindowManager.getImage(wList[i]);
/* 117 */       titles[i] = (imp2 != null ? imp2.getTitle() : "");
/*     */     }
/* 119 */     int x = 0; int y = 0;
/* 120 */     Roi roi = imp.getRoi();
/* 121 */     if ((roi != null) && (roi.isArea())) {
/* 122 */       Rectangle r = roi.getBounds();
/* 123 */       x = r.x; y = r.y;
/*     */     }
/* 125 */     int index = 0;
/* 126 */     if (wList.length == 2) {
/* 127 */       ImagePlus i1 = WindowManager.getImage(wList[0]);
/* 128 */       ImagePlus i2 = WindowManager.getImage(wList[1]);
/* 129 */       if ((i2.getWidth() < i1.getWidth()) && (i2.getHeight() < i1.getHeight()))
/* 130 */         index = 1;
/* 131 */     } else if (imp.getID() == wList[0]) {
/* 132 */       index = 1;
/*     */     }
/* 134 */     String title = createImageRoi ? "Create Image ROI" : "Add Image...";
/* 135 */     GenericDialog gd = new GenericDialog(title);
/* 136 */     if (createImageRoi) {
/* 137 */       gd.addChoice("Image:", titles, titles[index]);
/*     */     } else {
/* 139 */       gd.addChoice("Image to add:", titles, titles[index]);
/* 140 */       gd.addNumericField("X location:", x, 0);
/* 141 */       gd.addNumericField("Y location:", y, 0);
/*     */     }
/* 143 */     gd.addNumericField("Opacity (0-100%):", opacity, 0);
/*     */ 
/* 145 */     gd.showDialog();
/* 146 */     if (gd.wasCanceled())
/* 147 */       return;
/* 148 */     index = gd.getNextChoiceIndex();
/* 149 */     if (!createImageRoi) {
/* 150 */       x = (int)gd.getNextNumber();
/* 151 */       y = (int)gd.getNextNumber();
/*     */     }
/* 153 */     opacity = (int)gd.getNextNumber();
/*     */ 
/* 155 */     ImagePlus overlay = WindowManager.getImage(wList[index]);
/* 156 */     if (wList.length == 2) {
/* 157 */       ImagePlus i1 = WindowManager.getImage(wList[0]);
/* 158 */       ImagePlus i2 = WindowManager.getImage(wList[1]);
/* 159 */       if ((i2.getWidth() < i1.getWidth()) && (i2.getHeight() < i1.getHeight())) {
/* 160 */         imp = i1;
/* 161 */         overlay = i2;
/*     */       }
/*     */     }
/* 164 */     if (overlay == imp) {
/* 165 */       IJ.error("Add Image...", "Image to be added cannot be the same as\n\"" + imp.getTitle() + "\".");
/* 166 */       return;
/*     */     }
/* 168 */     if ((overlay.getWidth() > imp.getWidth()) && (overlay.getHeight() > imp.getHeight())) {
/* 169 */       IJ.error("Add Image...", "Image to be added cannnot be larger than\n\"" + imp.getTitle() + "\".");
/* 170 */       return;
/*     */     }
/* 172 */     if ((createImageRoi) && (x == 0) && (y == 0)) {
/* 173 */       x = imp.getWidth() / 2 - overlay.getWidth() / 2;
/* 174 */       y = imp.getHeight() / 2 - overlay.getHeight() / 2;
/*     */     }
/* 176 */     roi = new ImageRoi(x, y, overlay.getProcessor());
/* 177 */     roi.setName(overlay.getShortTitle());
/* 178 */     if (opacity != 100) ((ImageRoi)roi).setOpacity(opacity / 100.0D);
/* 179 */     if (createImageRoi) {
/* 180 */       imp.setRoi(roi);
/*     */     } else {
/* 182 */       Overlay overlayList = imp.getOverlay();
/* 183 */       if (overlayList == null) overlayList = new Overlay();
/* 184 */       overlayList.add(roi);
/* 185 */       imp.setOverlay(overlayList);
/* 186 */       Undo.setup(7, imp);
/*     */     }
/*     */   }
/*     */ 
/*     */   void hide() {
/* 191 */     ImagePlus imp = IJ.getImage();
/* 192 */     imp.setHideOverlay(true);
/* 193 */     RoiManager rm = RoiManager.getInstance();
/* 194 */     if (rm != null) rm.runCommand("show none"); 
/*     */   }
/*     */ 
/*     */   void show()
/*     */   {
/* 198 */     ImagePlus imp = IJ.getImage();
/* 199 */     imp.setHideOverlay(false);
/* 200 */     if (imp.getOverlay() == null) {
/* 201 */       RoiManager rm = RoiManager.getInstance();
/* 202 */       if ((rm != null) && (rm.getCount() > 1)) {
/* 203 */         if (!IJ.isMacro()) rm.toFront();
/* 204 */         rm.runCommand("show all with labels");
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   void remove() {
/* 210 */     ImagePlus imp = WindowManager.getCurrentImage();
/* 211 */     if (imp != null) imp.setOverlay(null);
/* 212 */     RoiManager rm = RoiManager.getInstance();
/* 213 */     if (rm != null) rm.runCommand("show none"); 
/*     */   }
/*     */ 
/*     */   void flatten()
/*     */   {
/* 217 */     ImagePlus imp = IJ.getImage();
/* 218 */     int flags = imp.isComposite() ? 0 : IJ.setupDialog(imp, 0);
/* 219 */     if (flags == 4096)
/* 220 */       return;
/* 221 */     if (flags == 32) {
/* 222 */       flattenStack(imp);
/*     */     } else {
/* 224 */       ImagePlus imp2 = imp.flatten();
/* 225 */       imp2.setTitle(WindowManager.getUniqueName(imp.getTitle()));
/* 226 */       imp2.show();
/*     */     }
/*     */   }
/*     */ 
/*     */   void flattenStack(ImagePlus imp) {
/* 231 */     Overlay overlay = imp.getOverlay();
/* 232 */     if ((overlay == null) || (!IJ.isJava16()) || (imp.getBitDepth() != 24)) {
/* 233 */       IJ.error("Flatten Stack", "An overlay, Java 1.6 and an RGB image are required.");
/* 234 */       return;
/*     */     }
/* 236 */     ImageStack stack = imp.getStack();
/* 237 */     for (int i = 1; i <= stack.getSize(); i++) {
/* 238 */       ImageProcessor ip = stack.getProcessor(i);
/* 239 */       Roi[] rois = overlay.toArray();
/* 240 */       for (int j = 0; j < rois.length; j++) {
/* 241 */         Roi roi = rois[j];
/* 242 */         int position = roi.getPosition();
/*     */ 
/* 250 */         if ((position == 0) || (position == i)) {
/* 251 */           ip.drawRoi(roi);
/*     */         }
/*     */       }
/*     */     }
/* 255 */     imp.setStack(stack);
/* 256 */     imp.setOverlay(null);
/*     */   }
/*     */ 
/*     */   void fromRoiManager() {
/* 260 */     ImagePlus imp = IJ.getImage();
/* 261 */     RoiManager rm = RoiManager.getInstance2();
/* 262 */     if (rm == null) {
/* 263 */       IJ.error("ROI Manager is not open");
/* 264 */       return;
/*     */     }
/* 266 */     Roi[] rois = rm.getRoisAsArray();
/* 267 */     if (rois.length == 0) {
/* 268 */       IJ.error("ROI Manager is empty");
/* 269 */       return;
/*     */     }
/* 271 */     Overlay overlay = OverlayLabels.createOverlay();
/* 272 */     for (int i = 0; i < rois.length; i++) {
/* 273 */       Roi roi = (Roi)rois[i].clone();
/* 274 */       if (!Prefs.showAllSliceOnly) {
/* 275 */         roi.setPosition(0);
/*     */       }
/*     */ 
/* 278 */       if ((roi.getStrokeColor() == null) || ((Line.getWidth() > 1) && (defaultRoi.getStrokeColor() != null)))
/* 279 */         roi.setStrokeColor(defaultRoi.getStrokeColor());
/* 280 */       if (roi.getFillColor() == null)
/* 281 */         roi.setFillColor(defaultRoi.getFillColor());
/* 282 */       overlay.add(roi);
/*     */     }
/* 284 */     imp.setOverlay(overlay);
/* 285 */     ImageCanvas ic = imp.getCanvas();
/* 286 */     if (ic != null) ic.setShowAllROIs(false);
/* 287 */     rm.setEditMode(imp, false);
/* 288 */     imp.killRoi();
/*     */   }
/*     */ 
/*     */   void toRoiManager() {
/* 292 */     ImagePlus imp = IJ.getImage();
/* 293 */     Overlay overlay = imp.getOverlay();
/* 294 */     if (overlay == null) {
/* 295 */       IJ.error("Overlay required");
/* 296 */       return;
/*     */     }
/* 298 */     RoiManager rm = RoiManager.getInstance();
/* 299 */     if (rm == null) {
/* 300 */       if ((Macro.getOptions() != null) && (Interpreter.isBatchMode()))
/* 301 */         rm = Interpreter.getBatchModeRoiManager();
/* 302 */       if (rm == null) {
/* 303 */         Frame frame = WindowManager.getFrame("ROI Manager");
/* 304 */         if (frame == null)
/* 305 */           IJ.run("ROI Manager...");
/* 306 */         frame = WindowManager.getFrame("ROI Manager");
/* 307 */         if ((frame == null) || (!(frame instanceof RoiManager)))
/* 308 */           return;
/* 309 */         rm = (RoiManager)frame;
/*     */       }
/*     */     }
/* 312 */     if ((overlay.size() >= 4) && (overlay.get(3).getPosition() != 0))
/* 313 */       Prefs.showAllSliceOnly = true;
/* 314 */     rm.runCommand("reset");
/* 315 */     for (int i = 0; i < overlay.size(); i++)
/* 316 */       rm.add(imp, overlay.get(i), i);
/* 317 */     rm.setEditMode(imp, true);
/* 318 */     if (rm.getCount() == overlay.size())
/* 319 */       imp.setOverlay(null);
/*     */   }
/*     */ 
/*     */   void options() {
/* 323 */     ImagePlus imp = WindowManager.getCurrentImage();
/* 324 */     Overlay overlay = null;
/* 325 */     Roi roi = null;
/* 326 */     if (imp != null) {
/* 327 */       overlay = imp.getOverlay();
/* 328 */       roi = imp.getRoi();
/* 329 */       if (roi != null)
/* 330 */         roi = (Roi)roi.clone();
/*     */     }
/* 332 */     if (roi == null)
/* 333 */       roi = defaultRoi;
/* 334 */     if (roi == null) {
/* 335 */       int size = imp != null ? imp.getWidth() : 512;
/* 336 */       roi = new Roi(0, 0, size / 4, size / 4);
/*     */     }
/* 338 */     if (!roi.isDrawingTool()) {
/* 339 */       if (roi.getStroke() == null)
/* 340 */         roi.setStrokeWidth(defaultRoi.getStrokeWidth());
/* 341 */       if ((roi.getStrokeColor() == null) || ((Line.getWidth() > 1) && (defaultRoi.getStrokeColor() != null)))
/* 342 */         roi.setStrokeColor(defaultRoi.getStrokeColor());
/* 343 */       if (roi.getFillColor() == null)
/* 344 */         roi.setFillColor(defaultRoi.getFillColor());
/*     */     }
/* 346 */     int width = Line.getWidth();
/* 347 */     Rectangle bounds = roi.getBounds();
/* 348 */     boolean tooWide = width > Math.max(bounds.width, bounds.height) / 3.0D;
/* 349 */     if ((roi.getStroke() == null) && (width > 1) && (!tooWide))
/* 350 */       roi.setStrokeWidth(Line.getWidth());
/* 351 */     if (roi.getStrokeColor() == null)
/* 352 */       roi.setStrokeColor(Roi.getColor());
/* 353 */     boolean points = ((roi instanceof PointRoi)) && (((PolygonRoi)roi).getNCoordinates() > 1);
/* 354 */     if (points) roi.setStrokeColor(Color.red);
/* 355 */     roi.setPosition(defaultRoi.getPosition());
/* 356 */     RoiProperties rp = new RoiProperties("Overlay Options", roi);
/* 357 */     if (!rp.showDialog()) return;
/* 358 */     defaultRoi = roi;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  18 */     defaultRoi.setStrokeColor(Roi.getColor());
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.OverlayCommands
 * JD-Core Version:    0.6.2
 */