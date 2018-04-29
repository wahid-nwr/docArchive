/*     */ package ij.io;
/*     */ 
/*     */ import ij.gui.Arrow;
/*     */ import ij.gui.EllipseRoi;
/*     */ import ij.gui.ImageRoi;
/*     */ import ij.gui.Line;
/*     */ import ij.gui.Overlay;
/*     */ import ij.gui.PolygonRoi;
/*     */ import ij.gui.Roi;
/*     */ import ij.gui.ShapeRoi;
/*     */ import ij.gui.TextRoi;
/*     */ import java.awt.BasicStroke;
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.Polygon;
/*     */ import java.awt.Rectangle;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ 
/*     */ public class RoiEncoder
/*     */ {
/*     */   static final int HEADER_SIZE = 64;
/*     */   static final int HEADER2_SIZE = 64;
/*     */   static final int VERSION = 221;
/*     */   private String path;
/*     */   private OutputStream f;
/*  20 */   private final int polygon = 0; private final int rect = 1; private final int oval = 2; private final int line = 3; private final int freeline = 4; private final int polyline = 5; private final int noRoi = 6; private final int freehand = 7; private final int traced = 8; private final int angle = 9; private final int point = 10;
/*     */   private byte[] data;
/*     */   private String roiName;
/*     */   private int roiNameSize;
/*     */ 
/*     */   public RoiEncoder(String path)
/*     */   {
/*  28 */     this.path = path;
/*     */   }
/*     */ 
/*     */   public RoiEncoder(OutputStream f)
/*     */   {
/*  33 */     this.f = f;
/*     */   }
/*     */ 
/*     */   public void write(Roi roi) throws IOException
/*     */   {
/*  38 */     if (this.f != null) {
/*  39 */       write(roi, this.f);
/*     */     } else {
/*  41 */       this.f = new FileOutputStream(this.path);
/*  42 */       write(roi, this.f);
/*  43 */       this.f.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static byte[] saveAsByteArray(Roi roi)
/*     */   {
/*  49 */     if (roi == null) return null;
/*  50 */     byte[] bytes = null;
/*     */     try {
/*  52 */       ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
/*  53 */       RoiEncoder encoder = new RoiEncoder(out);
/*  54 */       encoder.write(roi);
/*  55 */       out.close();
/*  56 */       bytes = out.toByteArray();
/*     */     } catch (IOException e) {
/*  58 */       return null;
/*     */     }
/*  60 */     return bytes;
/*     */   }
/*     */ 
/*     */   void write(Roi roi, OutputStream f) throws IOException {
/*  64 */     int roiType = roi.getType();
/*  65 */     int type = 1;
/*  66 */     int options = 0;
/*  67 */     this.roiName = roi.getName();
/*  68 */     if (this.roiName != null)
/*  69 */       this.roiNameSize = (this.roiName.length() * 2);
/*     */     else {
/*  71 */       this.roiNameSize = 0;
/*     */     }
/*  73 */     switch (roiType) { case 2:
/*  74 */       type = 0; break;
/*     */     case 3:
/*  75 */       type = 7; break;
/*     */     case 4:
/*  76 */       type = 8; break;
/*     */     case 1:
/*  77 */       type = 2; break;
/*     */     case 5:
/*  78 */       type = 3; break;
/*     */     case 6:
/*  79 */       type = 5; break;
/*     */     case 7:
/*  80 */       type = 4; break;
/*     */     case 8:
/*  81 */       type = 9; break;
/*     */     case 9:
/*  82 */       type = 1; break;
/*     */     case 10:
/*  83 */       type = 10; break;
/*     */     default:
/*  84 */       type = 1;
/*     */     }
/*     */ 
/*  87 */     if (roiType == 9) {
/*  88 */       saveShapeRoi(roi, type, f, options);
/*  89 */       return;
/*     */     }
/*     */ 
/*  92 */     int n = 0;
/*  93 */     int[] x = null; int[] y = null;
/*  94 */     if ((roi instanceof PolygonRoi)) {
/*  95 */       Polygon p = ((PolygonRoi)roi).getNonSplineCoordinates();
/*  96 */       n = p.npoints;
/*  97 */       x = p.xpoints;
/*  98 */       y = p.ypoints;
/*     */     }
/* 100 */     this.data = new byte[128 + n * 4 + this.roiNameSize];
/*     */ 
/* 102 */     Rectangle r = roi.getBounds();
/*     */ 
/* 104 */     this.data[0] = 73; this.data[1] = 111; this.data[2] = 117; this.data[3] = 116;
/* 105 */     putShort(4, 221);
/* 106 */     this.data[6] = ((byte)type);
/* 107 */     putShort(8, r.y);
/* 108 */     putShort(10, r.x);
/* 109 */     putShort(12, r.y + r.height);
/* 110 */     putShort(14, r.x + r.width);
/* 111 */     putShort(16, n);
/* 112 */     putInt(56, roi.getPosition());
/*     */ 
/* 114 */     if (type == 1) {
/* 115 */       int arcSize = roi.getCornerDiameter();
/* 116 */       if (arcSize > 0) {
/* 117 */         putShort(54, arcSize);
/*     */       }
/*     */     }
/* 120 */     if ((roi instanceof Line)) {
/* 121 */       Line l = (Line)roi;
/* 122 */       putFloat(18, l.x1);
/* 123 */       putFloat(22, l.y1);
/* 124 */       putFloat(26, l.x2);
/* 125 */       putFloat(30, l.y2);
/* 126 */       if ((roi instanceof Arrow)) {
/* 127 */         putShort(48, 2);
/* 128 */         if (((Arrow)roi).getDoubleHeaded())
/* 129 */           options |= 2;
/* 130 */         if (((Arrow)roi).getOutline())
/* 131 */           options |= 4;
/* 132 */         putShort(50, options);
/* 133 */         putByte(52, ((Arrow)roi).getStyle());
/* 134 */         putByte(53, (int)((Arrow)roi).getHeadSize());
/*     */       }
/*     */     }
/*     */ 
/* 138 */     if ((roi instanceof EllipseRoi)) {
/* 139 */       putShort(48, 3);
/* 140 */       double[] p = ((EllipseRoi)roi).getParams();
/* 141 */       putFloat(18, (float)p[0]);
/* 142 */       putFloat(22, (float)p[1]);
/* 143 */       putFloat(26, (float)p[2]);
/* 144 */       putFloat(30, (float)p[3]);
/* 145 */       putFloat(52, (float)p[4]);
/*     */     }
/*     */ 
/* 150 */     saveStrokeWidthAndColor(roi);
/* 151 */     if (((roi instanceof PolygonRoi)) && (((PolygonRoi)roi).isSplineFit())) {
/* 152 */       options |= 1;
/* 153 */       putShort(50, options);
/*     */     }
/*     */ 
/* 158 */     if ((n == 0) && ((roi instanceof TextRoi)))
/* 159 */       saveTextRoi((TextRoi)roi);
/* 160 */     else if ((n == 0) && ((roi instanceof ImageRoi)))
/* 161 */       saveImageRoi((ImageRoi)roi);
/*     */     else {
/* 163 */       putHeader2(roi, 64 + n * 4);
/*     */     }
/* 165 */     if (n > 0) {
/* 166 */       int base1 = 64;
/* 167 */       int base2 = base1 + 2 * n;
/* 168 */       for (int i = 0; i < n; i++) {
/* 169 */         putShort(base1 + i * 2, x[i]);
/* 170 */         putShort(base2 + i * 2, y[i]);
/*     */       }
/*     */     }
/*     */ 
/* 174 */     saveOverlayOptions(roi, options);
/* 175 */     f.write(this.data);
/*     */   }
/*     */ 
/*     */   void saveStrokeWidthAndColor(Roi roi) {
/* 179 */     BasicStroke stroke = roi.getStroke();
/* 180 */     if (stroke != null)
/* 181 */       putShort(34, (int)stroke.getLineWidth());
/* 182 */     Color strokeColor = roi.getStrokeColor();
/* 183 */     if (strokeColor != null)
/* 184 */       putInt(40, strokeColor.getRGB());
/* 185 */     Color fillColor = roi.getFillColor();
/* 186 */     if (fillColor != null)
/* 187 */       putInt(44, fillColor.getRGB());
/*     */   }
/*     */ 
/*     */   void saveShapeRoi(Roi roi, int type, OutputStream f, int options) throws IOException {
/* 191 */     float[] shapeArray = ((ShapeRoi)roi).getShapeAsArray();
/* 192 */     if (shapeArray == null) return;
/* 193 */     BufferedOutputStream bout = new BufferedOutputStream(f);
/* 194 */     Rectangle r = roi.getBounds();
/* 195 */     this.data = new byte[128 + shapeArray.length * 4 + this.roiNameSize];
/* 196 */     this.data[0] = 73; this.data[1] = 111; this.data[2] = 117; this.data[3] = 116;
/*     */ 
/* 198 */     putShort(4, 221);
/* 199 */     this.data[6] = ((byte)type);
/* 200 */     putShort(8, r.y);
/* 201 */     putShort(10, r.x);
/* 202 */     putShort(12, r.y + r.height);
/* 203 */     putShort(14, r.x + r.width);
/* 204 */     putInt(56, roi.getPosition());
/*     */ 
/* 206 */     putInt(36, shapeArray.length);
/* 207 */     saveStrokeWidthAndColor(roi);
/* 208 */     saveOverlayOptions(roi, options);
/*     */ 
/* 212 */     int base = 64;
/* 213 */     for (int i = 0; i < shapeArray.length; i++) {
/* 214 */       putFloat(base, shapeArray[i]);
/* 215 */       base += 4;
/*     */     }
/* 217 */     int hdr2Offset = 64 + shapeArray.length * 4;
/*     */ 
/* 219 */     putHeader2(roi, hdr2Offset);
/* 220 */     bout.write(this.data, 0, this.data.length);
/* 221 */     bout.flush();
/*     */   }
/*     */ 
/*     */   void saveOverlayOptions(Roi roi, int options) {
/* 225 */     Overlay proto = roi.getPrototypeOverlay();
/* 226 */     if (proto.getDrawLabels())
/* 227 */       options |= 8;
/* 228 */     if (proto.getDrawNames())
/* 229 */       options |= 16;
/* 230 */     if (proto.getDrawBackgrounds())
/* 231 */       options |= 32;
/* 232 */     Font font = proto.getLabelFont();
/* 233 */     if ((font != null) && (font.getStyle() == 1))
/* 234 */       options |= 64;
/* 235 */     putShort(50, options);
/*     */   }
/*     */ 
/*     */   void saveTextRoi(TextRoi roi) {
/* 239 */     Font font = roi.getCurrentFont();
/* 240 */     String fontName = font.getName();
/* 241 */     int size = font.getSize();
/* 242 */     int style = font.getStyle();
/* 243 */     String text = roi.getText();
/* 244 */     int fontNameLength = fontName.length();
/* 245 */     int textLength = text.length();
/* 246 */     int textRoiDataLength = 16 + fontNameLength * 2 + textLength * 2;
/* 247 */     byte[] data2 = new byte[128 + textRoiDataLength + this.roiNameSize];
/* 248 */     System.arraycopy(this.data, 0, data2, 0, 64);
/* 249 */     this.data = data2;
/* 250 */     putShort(48, 1);
/* 251 */     putInt(64, size);
/* 252 */     putInt(68, style);
/* 253 */     putInt(72, fontNameLength);
/* 254 */     putInt(76, textLength);
/* 255 */     for (int i = 0; i < fontNameLength; i++)
/* 256 */       putShort(80 + i * 2, fontName.charAt(i));
/* 257 */     for (int i = 0; i < textLength; i++)
/* 258 */       putShort(80 + fontNameLength * 2 + i * 2, text.charAt(i));
/* 259 */     int hdr2Offset = 64 + textRoiDataLength;
/*     */ 
/* 261 */     putHeader2(roi, hdr2Offset);
/*     */   }
/*     */ 
/*     */   void saveImageRoi(ImageRoi roi) {
/* 265 */     byte[] bytes = roi.getSerializedImage();
/* 266 */     int imageSize = bytes.length;
/* 267 */     byte[] data2 = new byte[128 + imageSize + this.roiNameSize];
/* 268 */     System.arraycopy(this.data, 0, data2, 0, 64);
/* 269 */     this.data = data2;
/* 270 */     putShort(48, 4);
/* 271 */     for (int i = 0; i < imageSize; i++)
/* 272 */       putByte(64 + i, bytes[i] & 0xFF);
/* 273 */     int hdr2Offset = 64 + imageSize;
/* 274 */     double opacity = roi.getOpacity();
/* 275 */     putByte(hdr2Offset + 31, (int)(opacity * 255.0D));
/* 276 */     putInt(hdr2Offset + 32, imageSize);
/* 277 */     putHeader2(roi, hdr2Offset);
/*     */   }
/*     */ 
/*     */   void putHeader2(Roi roi, int hdr2Offset)
/*     */   {
/* 282 */     putInt(60, hdr2Offset);
/* 283 */     putInt(hdr2Offset + 4, roi.getCPosition());
/* 284 */     putInt(hdr2Offset + 8, roi.getZPosition());
/* 285 */     putInt(hdr2Offset + 12, roi.getTPosition());
/* 286 */     Overlay proto = roi.getPrototypeOverlay();
/* 287 */     Color overlayLabelColor = proto.getLabelColor();
/* 288 */     if (overlayLabelColor != null)
/* 289 */       putInt(hdr2Offset + 24, overlayLabelColor.getRGB());
/* 290 */     Font font = proto.getLabelFont();
/* 291 */     if (font != null)
/* 292 */       putShort(hdr2Offset + 28, font.getSize());
/* 293 */     if (this.roiNameSize > 0)
/* 294 */       putName(roi, hdr2Offset);
/*     */   }
/*     */ 
/*     */   void putName(Roi roi, int hdr2Offset) {
/* 298 */     int offset = hdr2Offset + 64;
/* 299 */     int nameLength = this.roiNameSize / 2;
/* 300 */     putInt(hdr2Offset + 16, offset);
/* 301 */     putInt(hdr2Offset + 20, nameLength);
/* 302 */     for (int i = 0; i < nameLength; i++)
/* 303 */       putShort(offset + i * 2, this.roiName.charAt(i));
/*     */   }
/*     */ 
/*     */   void putByte(int base, int v) {
/* 307 */     this.data[base] = ((byte)v);
/*     */   }
/*     */ 
/*     */   void putShort(int base, int v) {
/* 311 */     this.data[base] = ((byte)(v >>> 8));
/* 312 */     this.data[(base + 1)] = ((byte)v);
/*     */   }
/*     */ 
/*     */   void putFloat(int base, float v) {
/* 316 */     int tmp = Float.floatToIntBits(v);
/* 317 */     this.data[base] = ((byte)(tmp >> 24));
/* 318 */     this.data[(base + 1)] = ((byte)(tmp >> 16));
/* 319 */     this.data[(base + 2)] = ((byte)(tmp >> 8));
/* 320 */     this.data[(base + 3)] = ((byte)tmp);
/*     */   }
/*     */ 
/*     */   void putInt(int base, int i) {
/* 324 */     this.data[base] = ((byte)(i >> 24));
/* 325 */     this.data[(base + 1)] = ((byte)(i >> 16));
/* 326 */     this.data[(base + 2)] = ((byte)(i >> 8));
/* 327 */     this.data[(base + 3)] = ((byte)i);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.io.RoiEncoder
 * JD-Core Version:    0.6.2
 */