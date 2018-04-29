/*     */ package ij.io;
/*     */ 
/*     */ import ij.ImagePlus;
/*     */ import ij.gui.Arrow;
/*     */ import ij.gui.EllipseRoi;
/*     */ import ij.gui.ImageRoi;
/*     */ import ij.gui.Line;
/*     */ import ij.gui.OvalRoi;
/*     */ import ij.gui.Overlay;
/*     */ import ij.gui.PointRoi;
/*     */ import ij.gui.PolygonRoi;
/*     */ import ij.gui.Roi;
/*     */ import ij.gui.ShapeRoi;
/*     */ import ij.gui.TextRoi;
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.Rectangle;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ 
/*     */ public class RoiDecoder
/*     */ {
/*     */   public static final int VERSION_OFFSET = 4;
/*     */   public static final int TYPE = 6;
/*     */   public static final int TOP = 8;
/*     */   public static final int LEFT = 10;
/*     */   public static final int BOTTOM = 12;
/*     */   public static final int RIGHT = 14;
/*     */   public static final int N_COORDINATES = 16;
/*     */   public static final int X1 = 18;
/*     */   public static final int Y1 = 22;
/*     */   public static final int X2 = 26;
/*     */   public static final int Y2 = 30;
/*     */   public static final int STROKE_WIDTH = 34;
/*     */   public static final int SHAPE_ROI_SIZE = 36;
/*     */   public static final int STROKE_COLOR = 40;
/*     */   public static final int FILL_COLOR = 44;
/*     */   public static final int SUBTYPE = 48;
/*     */   public static final int OPTIONS = 50;
/*     */   public static final int ARROW_STYLE = 52;
/*     */   public static final int ELLIPSE_ASPECT_RATIO = 52;
/*     */   public static final int ARROW_HEAD_SIZE = 53;
/*     */   public static final int ROUNDED_RECT_ARC_SIZE = 54;
/*     */   public static final int POSITION = 56;
/*     */   public static final int HEADER2_OFFSET = 60;
/*     */   public static final int COORDINATES = 64;
/*     */   public static final int C_POSITION = 4;
/*     */   public static final int Z_POSITION = 8;
/*     */   public static final int T_POSITION = 12;
/*     */   public static final int NAME_OFFSET = 16;
/*     */   public static final int NAME_LENGTH = 20;
/*     */   public static final int OVERLAY_LABEL_COLOR = 24;
/*     */   public static final int OVERLAY_FONT_SIZE = 28;
/*     */   public static final int AVAILABLE_BYTE1 = 30;
/*     */   public static final int IMAGE_OPACITY = 31;
/*     */   public static final int IMAGE_SIZE = 32;
/*     */   public static final int TEXT = 1;
/*     */   public static final int ARROW = 2;
/*     */   public static final int ELLIPSE = 3;
/*     */   public static final int IMAGE = 4;
/*     */   public static final int SPLINE_FIT = 1;
/*     */   public static final int DOUBLE_HEADED = 2;
/*     */   public static final int OUTLINE = 4;
/*     */   public static final int OVERLAY_LABELS = 8;
/*     */   public static final int OVERLAY_NAMES = 16;
/*     */   public static final int OVERLAY_BACKGROUNDS = 32;
/*     */   public static final int OVERLAY_BOLD = 64;
/*  92 */   private final int polygon = 0; private final int rect = 1; private final int oval = 2; private final int line = 3; private final int freeline = 4; private final int polyline = 5; private final int noRoi = 6; private final int freehand = 7; private final int traced = 8; private final int angle = 9; private final int point = 10;
/*     */   private byte[] data;
/*     */   private String path;
/*     */   private InputStream is;
/*     */   private String name;
/*     */   private int size;
/*     */ 
/*     */   public RoiDecoder(String path)
/*     */   {
/* 103 */     this.path = path;
/*     */   }
/*     */ 
/*     */   public RoiDecoder(byte[] bytes, String name)
/*     */   {
/* 108 */     this.is = new ByteArrayInputStream(bytes);
/* 109 */     this.name = name;
/* 110 */     this.size = bytes.length;
/*     */   }
/*     */ 
/*     */   public Roi getRoi() throws IOException
/*     */   {
/* 115 */     if (this.path != null) {
/* 116 */       File f = new File(this.path);
/* 117 */       this.size = ((int)f.length());
/* 118 */       if ((!this.path.endsWith(".roi")) && (this.size > 5242880))
/* 119 */         throw new IOException("This is not an ROI or file size>5MB)");
/* 120 */       this.name = f.getName();
/* 121 */       this.is = new FileInputStream(this.path);
/*     */     }
/* 123 */     this.data = new byte[this.size];
/*     */ 
/* 125 */     int total = 0;
/* 126 */     while (total < this.size)
/* 127 */       total += this.is.read(this.data, total, this.size - total);
/* 128 */     this.is.close();
/* 129 */     if ((getByte(0) != 73) || (getByte(1) != 111))
/* 130 */       throw new IOException("This is not an ImageJ ROI");
/* 131 */     int version = getShort(4);
/* 132 */     int type = getByte(6);
/* 133 */     int subtype = getShort(48);
/* 134 */     int top = getShort(8);
/* 135 */     int left = getShort(10);
/* 136 */     int bottom = getShort(12);
/* 137 */     int right = getShort(14);
/* 138 */     int width = right - left;
/* 139 */     int height = bottom - top;
/* 140 */     int n = getShort(16);
/* 141 */     int options = getShort(50);
/* 142 */     int position = getInt(56);
/* 143 */     int hdr2Offset = getInt(60);
/* 144 */     int channel = 0; int slice = 0; int frame = 0;
/* 145 */     int overlayLabelColor = 0;
/* 146 */     int overlayFontSize = 0;
/* 147 */     int imageOpacity = 0;
/* 148 */     int imageSize = 0;
/*     */ 
/* 150 */     if ((hdr2Offset > 0) && (hdr2Offset + 32 + 4 <= this.size)) {
/* 151 */       channel = getInt(hdr2Offset + 4);
/* 152 */       slice = getInt(hdr2Offset + 8);
/* 153 */       frame = getInt(hdr2Offset + 12);
/* 154 */       overlayLabelColor = getInt(hdr2Offset + 24);
/* 155 */       overlayFontSize = getShort(hdr2Offset + 28);
/* 156 */       imageOpacity = getByte(hdr2Offset + 31);
/* 157 */       imageSize = getInt(hdr2Offset + 32);
/*     */     }
/*     */ 
/* 160 */     if ((this.name != null) && (this.name.endsWith(".roi")))
/* 161 */       this.name = this.name.substring(0, this.name.length() - 4);
/* 162 */     boolean isComposite = getInt(36) > 0;
/*     */ 
/* 164 */     Roi roi = null;
/* 165 */     if (isComposite) {
/* 166 */       roi = getShapeRoi();
/* 167 */       if (version >= 218) getStrokeWidthAndColor(roi);
/* 168 */       roi.setPosition(position);
/* 169 */       if ((channel > 0) || (slice > 0) || (frame > 0))
/* 170 */         roi.setPosition(channel, slice, frame);
/* 171 */       decodeOverlayOptions(roi, version, options, overlayLabelColor, overlayFontSize);
/* 172 */       return roi;
/*     */     }
/*     */ 
/* 175 */     switch (type) {
/*     */     case 1:
/* 177 */       roi = new Roi(left, top, width, height);
/* 178 */       int arcSize = getShort(54);
/* 179 */       if (arcSize > 0)
/* 180 */         roi.setCornerDiameter(arcSize); break;
/*     */     case 2:
/* 183 */       roi = new OvalRoi(left, top, width, height);
/* 184 */       break;
/*     */     case 3:
/* 186 */       int x1 = (int)getFloat(18);
/* 187 */       int y1 = (int)getFloat(22);
/* 188 */       int x2 = (int)getFloat(26);
/* 189 */       int y2 = (int)getFloat(30);
/* 190 */       if (subtype == 2) {
/* 191 */         roi = new Arrow(x1, y1, x2, y2);
/* 192 */         ((Arrow)roi).setDoubleHeaded((options & 0x2) != 0);
/* 193 */         ((Arrow)roi).setOutline((options & 0x4) != 0);
/* 194 */         int style = getByte(52);
/* 195 */         if ((style >= 0) && (style <= 3))
/* 196 */           ((Arrow)roi).setStyle(style);
/* 197 */         int headSize = getByte(53);
/* 198 */         if ((headSize >= 0) && (style <= 30))
/* 199 */           ((Arrow)roi).setHeadSize(headSize);
/*     */       } else {
/* 201 */         roi = new Line(x1, y1, x2, y2);
/*     */       }
/* 203 */       break;
/*     */     case 0:
/*     */     case 4:
/*     */     case 5:
/*     */     case 7:
/*     */     case 8:
/*     */     case 9:
/*     */     case 10:
/* 208 */       if (n != 0) {
/* 209 */         int[] x = new int[n];
/* 210 */         int[] y = new int[n];
/* 211 */         int base1 = 64;
/* 212 */         int base2 = base1 + 2 * n;
/*     */ 
/* 214 */         for (int i = 0; i < n; i++) {
/* 215 */           int xtmp = getShort(base1 + i * 2);
/* 216 */           if (xtmp < 0) xtmp = 0;
/* 217 */           int ytmp = getShort(base2 + i * 2);
/* 218 */           if (ytmp < 0) ytmp = 0;
/* 219 */           x[i] = (left + xtmp);
/* 220 */           y[i] = (top + ytmp);
/*     */         }
/*     */ 
/* 223 */         if (type == 10) {
/* 224 */           roi = new PointRoi(x, y, n);
/*     */         }
/*     */         else
/*     */         {
/*     */           int roiType;
/*     */           int roiType;
/* 228 */           if (type == 0) {
/* 229 */             roiType = 2;
/* 230 */           } else if (type == 7) {
/* 231 */             int roiType = 3;
/* 232 */             if (subtype == 3) {
/* 233 */               double ex1 = getFloat(18);
/* 234 */               double ey1 = getFloat(22);
/* 235 */               double ex2 = getFloat(26);
/* 236 */               double ey2 = getFloat(30);
/* 237 */               double aspectRatio = getFloat(52);
/* 238 */               roi = new EllipseRoi(ex1, ey1, ex2, ey2, aspectRatio);
/* 239 */               break;
/*     */             }
/*     */           }
/*     */           else
/*     */           {
/*     */             int roiType;
/* 241 */             if (type == 8) {
/* 242 */               roiType = 4;
/*     */             }
/*     */             else
/*     */             {
/*     */               int roiType;
/* 243 */               if (type == 5) {
/* 244 */                 roiType = 6;
/*     */               }
/*     */               else
/*     */               {
/*     */                 int roiType;
/* 245 */                 if (type == 4) {
/* 246 */                   roiType = 7;
/*     */                 }
/*     */                 else
/*     */                 {
/*     */                   int roiType;
/* 247 */                   if (type == 9)
/* 248 */                     roiType = 8;
/*     */                   else
/* 250 */                     roiType = 3; 
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/* 251 */           roi = new PolygonRoi(x, y, n, roiType); } 
/* 252 */       }break;
/*     */     case 6:
/*     */     default:
/* 254 */       throw new IOException("Unrecognized ROI type: " + type);
/*     */     }
/* 256 */     roi.setName(getRoiName());
/*     */ 
/* 259 */     if (version >= 218) {
/* 260 */       getStrokeWidthAndColor(roi);
/* 261 */       boolean splineFit = (options & 0x1) != 0;
/* 262 */       if ((splineFit) && ((roi instanceof PolygonRoi))) {
/* 263 */         ((PolygonRoi)roi).fitSpline();
/*     */       }
/*     */     }
/* 266 */     if ((version >= 218) && (subtype == 1)) {
/* 267 */       roi = getTextRoi(roi);
/*     */     }
/* 269 */     if ((version >= 221) && (subtype == 4)) {
/* 270 */       roi = getImageRoi(roi, imageOpacity, imageSize);
/*     */     }
/* 272 */     roi.setPosition(position);
/* 273 */     if ((channel > 0) || (slice > 0) || (frame > 0))
/* 274 */       roi.setPosition(channel, slice, frame);
/* 275 */     decodeOverlayOptions(roi, version, options, overlayLabelColor, overlayFontSize);
/* 276 */     return roi;
/*     */   }
/*     */ 
/*     */   void decodeOverlayOptions(Roi roi, int version, int options, int color, int fontSize) {
/* 280 */     Overlay proto = new Overlay();
/* 281 */     proto.drawLabels((options & 0x8) != 0);
/* 282 */     proto.drawNames((options & 0x10) != 0);
/* 283 */     proto.drawBackgrounds((options & 0x20) != 0);
/* 284 */     if (version >= 220)
/* 285 */       proto.setLabelColor(new Color(color));
/* 286 */     boolean bold = (options & 0x40) != 0;
/* 287 */     if ((fontSize > 0) || (bold)) {
/* 288 */       proto.setLabelFont(new Font("SansSerif", bold ? 1 : 0, fontSize));
/*     */     }
/* 290 */     roi.setPrototypeOverlay(proto);
/*     */   }
/*     */ 
/*     */   void getStrokeWidthAndColor(Roi roi) {
/* 294 */     int strokeWidth = getShort(34);
/* 295 */     if (strokeWidth > 0)
/* 296 */       roi.setStrokeWidth(strokeWidth);
/* 297 */     int strokeColor = getInt(40);
/* 298 */     if (strokeColor != 0) {
/* 299 */       int alpha = strokeColor >> 24 & 0xFF;
/* 300 */       roi.setStrokeColor(new Color(strokeColor, alpha != 255));
/*     */     }
/* 302 */     int fillColor = getInt(44);
/* 303 */     if (fillColor != 0) {
/* 304 */       int alpha = fillColor >> 24 & 0xFF;
/* 305 */       roi.setFillColor(new Color(fillColor, alpha != 255));
/*     */     }
/*     */   }
/*     */ 
/*     */   public Roi getShapeRoi() throws IOException {
/* 310 */     int type = getByte(6);
/* 311 */     if (type != 1)
/* 312 */       throw new IllegalArgumentException("Invalid composite ROI type");
/* 313 */     int top = getShort(8);
/* 314 */     int left = getShort(10);
/* 315 */     int bottom = getShort(12);
/* 316 */     int right = getShort(14);
/* 317 */     int width = right - left;
/* 318 */     int height = bottom - top;
/* 319 */     int n = getInt(36);
/*     */ 
/* 321 */     ShapeRoi roi = null;
/* 322 */     float[] shapeArray = new float[n];
/* 323 */     int base = 64;
/* 324 */     for (int i = 0; i < n; i++) {
/* 325 */       shapeArray[i] = getFloat(base);
/* 326 */       base += 4;
/*     */     }
/* 328 */     roi = new ShapeRoi(shapeArray);
/* 329 */     roi.setName(getRoiName());
/* 330 */     return roi;
/*     */   }
/*     */ 
/*     */   Roi getTextRoi(Roi roi) {
/* 334 */     Rectangle r = roi.getBounds();
/* 335 */     int hdrSize = 64;
/* 336 */     int size = getInt(hdrSize);
/* 337 */     int style = getInt(hdrSize + 4);
/* 338 */     int nameLength = getInt(hdrSize + 8);
/* 339 */     int textLength = getInt(hdrSize + 12);
/* 340 */     char[] name = new char[nameLength];
/* 341 */     char[] text = new char[textLength];
/* 342 */     for (int i = 0; i < nameLength; i++)
/* 343 */       name[i] = ((char)getShort(hdrSize + 16 + i * 2));
/* 344 */     for (int i = 0; i < textLength; i++)
/* 345 */       text[i] = ((char)getShort(hdrSize + 16 + nameLength * 2 + i * 2));
/* 346 */     Font font = new Font(new String(name), style, size);
/* 347 */     Roi roi2 = new TextRoi(r.x, r.y, new String(text), font);
/* 348 */     roi2.setStrokeColor(roi.getStrokeColor());
/* 349 */     roi2.setFillColor(roi.getFillColor());
/* 350 */     roi2.setName(getRoiName());
/* 351 */     return roi2;
/*     */   }
/*     */ 
/*     */   Roi getImageRoi(Roi roi, int opacity, int size) {
/* 355 */     if (size <= 0)
/* 356 */       return roi;
/* 357 */     Rectangle r = roi.getBounds();
/* 358 */     byte[] bytes = new byte[size];
/* 359 */     for (int i = 0; i < size; i++)
/* 360 */       bytes[i] = ((byte)getByte(64 + i));
/* 361 */     ImagePlus imp = new Opener().deserialize(bytes);
/* 362 */     ImageRoi roi2 = new ImageRoi(r.x, r.y, imp.getProcessor());
/* 363 */     roi2.setOpacity(opacity / 255.0D);
/* 364 */     return roi2;
/*     */   }
/*     */ 
/*     */   String getRoiName() {
/* 368 */     String fileName = this.name;
/* 369 */     int hdr2Offset = getInt(60);
/* 370 */     if (hdr2Offset == 0)
/* 371 */       return fileName;
/* 372 */     int offset = getInt(hdr2Offset + 16);
/* 373 */     int length = getInt(hdr2Offset + 20);
/* 374 */     if ((offset == 0) || (length == 0))
/* 375 */       return fileName;
/* 376 */     if (offset + length * 2 > this.size)
/* 377 */       return fileName;
/* 378 */     char[] name = new char[length];
/* 379 */     for (int i = 0; i < length; i++)
/* 380 */       name[i] = ((char)getShort(offset + i * 2));
/* 381 */     return new String(name);
/*     */   }
/*     */ 
/*     */   int getByte(int base) {
/* 385 */     return this.data[base] & 0xFF;
/*     */   }
/*     */ 
/*     */   int getShort(int base) {
/* 389 */     int b0 = this.data[base] & 0xFF;
/* 390 */     int b1 = this.data[(base + 1)] & 0xFF;
/* 391 */     int n = (short)((b0 << 8) + b1);
/* 392 */     if (n < -5000)
/* 393 */       n = (b0 << 8) + b1;
/* 394 */     return n;
/*     */   }
/*     */ 
/*     */   int getInt(int base) {
/* 398 */     int b0 = this.data[base] & 0xFF;
/* 399 */     int b1 = this.data[(base + 1)] & 0xFF;
/* 400 */     int b2 = this.data[(base + 2)] & 0xFF;
/* 401 */     int b3 = this.data[(base + 3)] & 0xFF;
/* 402 */     return (b0 << 24) + (b1 << 16) + (b2 << 8) + b3;
/*     */   }
/*     */ 
/*     */   float getFloat(int base) {
/* 406 */     return Float.intBitsToFloat(getInt(base));
/*     */   }
/*     */ 
/*     */   public static Roi openFromByteArray(byte[] bytes)
/*     */   {
/* 411 */     Roi roi = null;
/*     */     try {
/* 413 */       RoiDecoder decoder = new RoiDecoder(bytes, null);
/* 414 */       roi = decoder.getRoi();
/*     */     } catch (IOException e) {
/* 416 */       return null;
/*     */     }
/* 418 */     return roi;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.io.RoiDecoder
 * JD-Core Version:    0.6.2
 */