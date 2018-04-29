/*      */ package ij.gui;
/*      */ 
/*      */ import ij.ImagePlus;
/*      */ import ij.measure.Calibration;
/*      */ import ij.process.ByteProcessor;
/*      */ import ij.process.ImageProcessor;
/*      */ import java.awt.BasicStroke;
/*      */ import java.awt.Color;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.Graphics2D;
/*      */ import java.awt.GraphicsConfiguration;
/*      */ import java.awt.Point;
/*      */ import java.awt.Polygon;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.Shape;
/*      */ import java.awt.Stroke;
/*      */ import java.awt.geom.AffineTransform;
/*      */ import java.awt.geom.Area;
/*      */ import java.awt.geom.CubicCurve2D.Double;
/*      */ import java.awt.geom.Ellipse2D.Double;
/*      */ import java.awt.geom.FlatteningPathIterator;
/*      */ import java.awt.geom.GeneralPath;
/*      */ import java.awt.geom.Line2D.Double;
/*      */ import java.awt.geom.PathIterator;
/*      */ import java.awt.geom.Point2D.Double;
/*      */ import java.awt.geom.QuadCurve2D.Double;
/*      */ import java.awt.geom.Rectangle2D;
/*      */ import java.awt.geom.Rectangle2D.Double;
/*      */ import java.awt.geom.RoundRectangle2D.Float;
/*      */ import java.awt.image.BufferedImage;
/*      */ import java.awt.image.DataBufferByte;
/*      */ import java.awt.image.Raster;
/*      */ import java.util.Vector;
/*      */ 
/*      */ public class ShapeRoi extends Roi
/*      */ {
/*      */   static final int NO_TYPE = 128;
/*      */   static final double MAXERROR = 0.001D;
/*      */   static final double FLATNESS = 0.1D;
/*      */   private static final int MAXPOLY = 10;
/*      */   private static final int OR = 0;
/*      */   private static final int AND = 1;
/*      */   private static final int XOR = 2;
/*      */   private static final int NOT = 3;
/*      */   private static final double SHAPE_TO_ROI = -1.0D;
/*      */   private Shape shape;
/*   51 */   private double maxerror = 0.001D;
/*      */ 
/*   56 */   private double flatness = 0.1D;
/*      */ 
/*   59 */   private int maxPoly = 10;
/*      */   private boolean flatten;
/*   69 */   private boolean forceTrace = false;
/*      */ 
/*   75 */   private boolean forceAngle = false;
/*      */   private Vector savedRois;
/*   78 */   private static Stroke defaultStroke = new BasicStroke();
/*      */ 
/*      */   public ShapeRoi(Roi r)
/*      */   {
/*   83 */     this(r, 0.1D, 0.001D, false, false, false, 10);
/*      */   }
/*      */ 
/*      */   public ShapeRoi(Shape s)
/*      */   {
/*   88 */     super(s.getBounds());
/*   89 */     AffineTransform at = new AffineTransform();
/*   90 */     at.translate(-this.x, -this.y);
/*   91 */     this.shape = new GeneralPath(at.createTransformedShape(s));
/*   92 */     this.type = 9;
/*      */   }
/*      */ 
/*      */   public ShapeRoi(int x, int y, Shape s)
/*      */   {
/*   97 */     super(x, y, s.getBounds().width, s.getBounds().height);
/*   98 */     this.shape = new GeneralPath(s);
/*   99 */     this.type = 9;
/*      */   }
/*      */ 
/*      */   ShapeRoi(Roi r, double flatness, double maxerror, boolean forceAngle, boolean forceTrace, boolean flatten, int maxPoly)
/*      */   {
/*  116 */     super(r.startX, r.startY, r.width, r.height);
/*  117 */     this.type = 9;
/*  118 */     this.flatness = flatness;
/*  119 */     this.maxerror = maxerror;
/*  120 */     this.forceAngle = forceAngle;
/*  121 */     this.forceTrace = forceTrace;
/*  122 */     this.maxPoly = maxPoly;
/*  123 */     this.flatten = flatten;
/*  124 */     this.shape = roiToShape((Roi)r.clone());
/*      */   }
/*      */ 
/*      */   public ShapeRoi(float[] shapeArray)
/*      */   {
/*  131 */     super(0, 0, null);
/*  132 */     this.shape = makeShapeFromArray(shapeArray);
/*  133 */     Rectangle r = this.shape.getBounds();
/*  134 */     this.x = r.x;
/*  135 */     this.y = r.y;
/*  136 */     this.width = r.width;
/*  137 */     this.height = r.height;
/*      */ 
/*  139 */     this.state = 3;
/*  140 */     this.oldX = this.x; this.oldY = this.y; this.oldWidth = this.width; this.oldHeight = this.height;
/*      */ 
/*  142 */     AffineTransform at = new AffineTransform();
/*  143 */     at.translate(-this.x, -this.y);
/*  144 */     this.shape = new GeneralPath(at.createTransformedShape(this.shape));
/*  145 */     this.flatness = 0.1D;
/*  146 */     this.maxerror = 0.001D;
/*  147 */     this.maxPoly = 10;
/*  148 */     this.flatten = false;
/*  149 */     this.type = 9;
/*      */   }
/*      */ 
/*      */   public synchronized Object clone()
/*      */   {
/*  154 */     ShapeRoi sr = (ShapeRoi)super.clone();
/*  155 */     sr.type = 9;
/*  156 */     sr.flatness = this.flatness;
/*  157 */     sr.maxerror = this.maxerror;
/*  158 */     sr.forceAngle = this.forceAngle;
/*  159 */     sr.forceTrace = this.forceTrace;
/*      */ 
/*  161 */     sr.setShape(cloneShape(this.shape));
/*  162 */     return sr;
/*      */   }
/*      */ 
/*      */   static Shape cloneShape(Shape rhs)
/*      */   {
/*  167 */     if (rhs == null) return null;
/*  168 */     if ((rhs instanceof Rectangle2D.Double)) return (Rectangle2D.Double)((Rectangle2D.Double)rhs).clone();
/*  169 */     if ((rhs instanceof Ellipse2D.Double)) return (Ellipse2D.Double)((Ellipse2D.Double)rhs).clone();
/*  170 */     if ((rhs instanceof Line2D.Double)) return (Line2D.Double)((Line2D.Double)rhs).clone();
/*  171 */     if ((rhs instanceof Polygon)) return new Polygon(((Polygon)rhs).xpoints, ((Polygon)rhs).ypoints, ((Polygon)rhs).npoints);
/*  172 */     if ((rhs instanceof GeneralPath)) return (GeneralPath)((GeneralPath)rhs).clone();
/*  173 */     return new GeneralPath();
/*      */   }
/*      */ 
/*      */   public ShapeRoi or(ShapeRoi sr)
/*      */   {
/*  184 */     return unaryOp(sr, 0);
/*      */   }
/*      */ 
/*      */   public ShapeRoi and(ShapeRoi sr)
/*      */   {
/*  191 */     return unaryOp(sr, 1);
/*      */   }
/*      */ 
/*      */   public ShapeRoi xor(ShapeRoi sr)
/*      */   {
/*  197 */     return unaryOp(sr, 2);
/*      */   }
/*      */ 
/*      */   public ShapeRoi not(ShapeRoi sr)
/*      */   {
/*  203 */     return unaryOp(sr, 3);
/*      */   }
/*      */   ShapeRoi unaryOp(ShapeRoi sr, int op) {
/*  206 */     AffineTransform at = new AffineTransform();
/*  207 */     at.translate(this.x, this.y);
/*  208 */     Area a1 = new Area(at.createTransformedShape(getShape()));
/*  209 */     at = new AffineTransform();
/*  210 */     at.translate(sr.x, sr.y);
/*  211 */     Area a2 = new Area(at.createTransformedShape(sr.getShape()));
/*      */     try {
/*  213 */       switch (op) { case 0:
/*  214 */         a1.add(a2); break;
/*      */       case 1:
/*  215 */         a1.intersect(a2); break;
/*      */       case 2:
/*  216 */         a1.exclusiveOr(a2); break;
/*      */       case 3:
/*  217 */         a1.subtract(a2); }
/*      */     } catch (Exception e) {
/*      */     }
/*  220 */     Rectangle r = a1.getBounds();
/*  221 */     at = new AffineTransform();
/*  222 */     at.translate(-r.x, -r.y);
/*  223 */     setShape(new GeneralPath(at.createTransformedShape(a1)));
/*  224 */     this.x = r.x;
/*  225 */     this.y = r.y;
/*  226 */     return this;
/*      */   }
/*      */ 
/*      */   private Shape roiToShape(Roi roi)
/*      */   {
/*  258 */     Shape shape = null;
/*  259 */     Rectangle r = roi.getBounds();
/*  260 */     int[] xCoords = null;
/*  261 */     int[] yCoords = null;
/*  262 */     int nCoords = 0;
/*  263 */     switch (roi.getType()) {
/*      */     case 5:
/*  265 */       Line line = (Line)roi;
/*  266 */       shape = new Line2D.Double(line.x1 - r.x, line.y1 - r.y, line.x2 - r.x, line.y2 - r.y);
/*  267 */       break;
/*      */     case 0:
/*  269 */       int arcSize = roi.getCornerDiameter();
/*  270 */       if (arcSize > 0)
/*  271 */         shape = new RoundRectangle2D.Float(0.0F, 0.0F, r.width, r.height, arcSize, arcSize);
/*      */       else
/*  273 */         shape = new Rectangle2D.Double(0.0D, 0.0D, r.width, r.height);
/*  274 */       break;
/*      */     case 1:
/*  276 */       Polygon p = roi.getPolygon();
/*  277 */       for (int i = 0; i < p.npoints; i++) {
/*  278 */         p.xpoints[i] -= r.x;
/*  279 */         p.ypoints[i] -= r.y;
/*      */       }
/*  281 */       shape = new Polygon(p.xpoints, p.ypoints, p.npoints);
/*  282 */       break;
/*      */     case 2:
/*  284 */       nCoords = ((PolygonRoi)roi).getNCoordinates();
/*  285 */       xCoords = ((PolygonRoi)roi).getXCoordinates();
/*  286 */       yCoords = ((PolygonRoi)roi).getYCoordinates();
/*  287 */       shape = new Polygon(xCoords, yCoords, nCoords);
/*  288 */       break;
/*      */     case 3:
/*      */     case 4:
/*  290 */       nCoords = ((PolygonRoi)roi).getNCoordinates();
/*  291 */       xCoords = ((PolygonRoi)roi).getXCoordinates();
/*  292 */       yCoords = ((PolygonRoi)roi).getYCoordinates();
/*  293 */       shape = new GeneralPath(0, nCoords);
/*  294 */       ((GeneralPath)shape).moveTo(xCoords[0], yCoords[0]);
/*  295 */       for (int i = 1; i < nCoords; i++)
/*  296 */         ((GeneralPath)shape).lineTo(xCoords[i], yCoords[i]);
/*  297 */       ((GeneralPath)shape).closePath();
/*  298 */       break;
/*      */     case 6:
/*      */     case 7:
/*      */     case 8:
/*  300 */       nCoords = ((PolygonRoi)roi).getNCoordinates();
/*  301 */       xCoords = ((PolygonRoi)roi).getXCoordinates();
/*  302 */       yCoords = ((PolygonRoi)roi).getYCoordinates();
/*  303 */       shape = new GeneralPath(1, nCoords);
/*  304 */       ((GeneralPath)shape).moveTo(xCoords[0], yCoords[0]);
/*  305 */       for (int i = 1; i < nCoords; i++)
/*  306 */         ((GeneralPath)shape).lineTo(xCoords[i], yCoords[i]);
/*  307 */       break;
/*      */     case 10:
/*  309 */       ImageProcessor mask = roi.getMask();
/*  310 */       byte[] maskPixels = (byte[])mask.getPixels();
/*  311 */       Rectangle maskBounds = roi.getBounds();
/*  312 */       int maskWidth = mask.getWidth();
/*  313 */       Area area = new Area();
/*  314 */       for (int y = 0; y < mask.getHeight(); y++) {
/*  315 */         int yOffset = y * maskWidth;
/*  316 */         for (int x = 0; x < maskWidth; x++) {
/*  317 */           if (maskPixels[(x + yOffset)] != 0)
/*  318 */             area.add(new Area(new Rectangle(x + maskBounds.x, y + maskBounds.y, 1, 1)));
/*      */         }
/*      */       }
/*  321 */       shape = area;
/*  322 */       break;
/*      */     case 9:
/*  323 */       shape = cloneShape(((ShapeRoi)roi).getShape());
/*  324 */       break;
/*      */     default:
/*  326 */       throw new IllegalArgumentException("Roi type not supported");
/*      */     }
/*      */ 
/*  329 */     if (shape != null) {
/*  330 */       this.x = roi.x;
/*  331 */       this.y = roi.y;
/*  332 */       Rectangle bounds = shape.getBounds();
/*  333 */       this.width = bounds.width;
/*  334 */       this.height = bounds.height;
/*  335 */       this.startX = this.x;
/*  336 */       this.startY = this.y;
/*      */     }
/*      */ 
/*  339 */     return shape;
/*      */   }
/*      */ 
/*      */   Shape makeShapeFromArray(float[] array)
/*      */   {
/*  344 */     if (array == null) return null;
/*  345 */     Shape s = new GeneralPath(0);
/*  346 */     int index = 0;
/*  347 */     float[] seg = new float[7];
/*      */     while (true) {
/*  349 */       int len = getSegment(array, seg, index);
/*  350 */       if (len < 0) break;
/*  351 */       index += len;
/*  352 */       int type = (int)seg[0];
/*  353 */       switch (type) {
/*      */       case 0:
/*  355 */         ((GeneralPath)s).moveTo(seg[1], seg[2]);
/*  356 */         break;
/*      */       case 1:
/*  358 */         ((GeneralPath)s).lineTo(seg[1], seg[2]);
/*  359 */         break;
/*      */       case 2:
/*  361 */         ((GeneralPath)s).quadTo(seg[1], seg[2], seg[3], seg[4]);
/*  362 */         break;
/*      */       case 3:
/*  364 */         ((GeneralPath)s).curveTo(seg[1], seg[2], seg[3], seg[4], seg[5], seg[6]);
/*  365 */         break;
/*      */       case 4:
/*  367 */         ((GeneralPath)s).closePath();
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  372 */     return s;
/*      */   }
/*      */ 
/*      */   private int getSegment(float[] array, float[] seg, int index) {
/*  376 */     int len = array.length;
/*  377 */     if (index >= len) return -1; seg[0] = array[(index++)];
/*  378 */     int type = (int)seg[0];
/*  379 */     if (type == 4) return 1;
/*  380 */     if (index >= len) return -1; seg[1] = array[(index++)];
/*  381 */     if (index >= len) return -1; seg[2] = array[(index++)];
/*  382 */     if ((type == 0) || (type == 1)) return 3;
/*  383 */     if (index >= len) return -1; seg[3] = array[(index++)];
/*  384 */     if (index >= len) return -1; seg[4] = array[(index++)];
/*  385 */     if (type == 2) return 5;
/*  386 */     if (index >= len) return -1; seg[5] = array[(index++)];
/*  387 */     if (index >= len) return -1; seg[6] = array[(index++)];
/*  388 */     if (type == 3) return 7;
/*  389 */     return -1;
/*      */   }
/*      */ 
/*      */   void saveRoi(Roi roi)
/*      */   {
/*  394 */     if (this.savedRois == null)
/*  395 */       this.savedRois = new Vector();
/*  396 */     this.savedRois.addElement(roi);
/*      */   }
/*      */ 
/*      */   public Roi[] getRois()
/*      */   {
/*  441 */     if (this.shape == null) return new Roi[0];
/*  442 */     if (this.savedRois != null)
/*  443 */       return getSavedRois();
/*  444 */     Vector rois = new Vector();
/*  445 */     if ((this.shape instanceof Rectangle2D.Double)) {
/*  446 */       Roi r = new Roi((int)((Rectangle2D.Double)this.shape).getX(), (int)((Rectangle2D.Double)this.shape).getY(), (int)((Rectangle2D.Double)this.shape).getWidth(), (int)((Rectangle2D.Double)this.shape).getHeight());
/*  447 */       rois.addElement(r);
/*  448 */     } else if ((this.shape instanceof Ellipse2D.Double)) {
/*  449 */       Roi r = new OvalRoi((int)((Ellipse2D.Double)this.shape).getX(), (int)((Ellipse2D.Double)this.shape).getY(), (int)((Ellipse2D.Double)this.shape).getWidth(), (int)((Ellipse2D.Double)this.shape).getHeight());
/*  450 */       rois.addElement(r);
/*  451 */     } else if ((this.shape instanceof Line2D.Double)) {
/*  452 */       Roi r = new Line((int)((Line2D.Double)this.shape).getX1(), (int)((Line2D.Double)this.shape).getY1(), (int)((Line2D.Double)this.shape).getX2(), (int)((Line2D.Double)this.shape).getY2());
/*  453 */       rois.addElement(r);
/*  454 */     } else if ((this.shape instanceof Polygon)) {
/*  455 */       Roi r = new PolygonRoi(((Polygon)this.shape).xpoints, ((Polygon)this.shape).ypoints, ((Polygon)this.shape).npoints, 2);
/*  456 */       rois.addElement(r);
/*  457 */     } else if ((this.shape instanceof GeneralPath))
/*      */     {
/*  459 */       PathIterator pIter;
/*      */       PathIterator pIter;
/*  459 */       if (this.flatten) pIter = getFlatteningPathIterator(this.shape, this.flatness); else
/*  460 */         pIter = this.shape.getPathIterator(new AffineTransform());
/*  461 */       parsePath(pIter, null, null, rois, null);
/*      */     }
/*  463 */     Roi[] array = new Roi[rois.size()];
/*  464 */     rois.copyInto((Roi[])array);
/*  465 */     return array;
/*      */   }
/*      */ 
/*      */   Roi[] getSavedRois() {
/*  469 */     Roi[] array = new Roi[this.savedRois.size()];
/*  470 */     this.savedRois.copyInto((Roi[])array);
/*  471 */     return array;
/*      */   }
/*      */ 
/*      */   public Roi shapeToRoi()
/*      */   {
/*  478 */     if ((this.shape == null) || (!(this.shape instanceof GeneralPath)))
/*  479 */       return null;
/*  480 */     PathIterator pIter = this.shape.getPathIterator(new AffineTransform());
/*  481 */     Vector rois = new Vector();
/*  482 */     double[] params = { -1.0D };
/*  483 */     if (!parsePath(pIter, params, null, rois, null))
/*  484 */       return null;
/*  485 */     if (rois.size() == 1) {
/*  486 */       return (Roi)rois.elementAt(0);
/*      */     }
/*  488 */     return null;
/*      */   }
/*      */ 
/*      */   private int guessType(int segments, boolean linesOnly, boolean curvesOnly, boolean closed)
/*      */   {
/*  501 */     closed = true;
/*  502 */     int roiType = 0;
/*  503 */     if (linesOnly)
/*  504 */       switch (segments) { case 0:
/*  505 */         roiType = 128; break;
/*      */       case 1:
/*  506 */         roiType = 128; break;
/*      */       case 2:
/*  507 */         roiType = closed ? 128 : 5; break;
/*      */       case 3:
/*  508 */         roiType = this.forceAngle ? 8 : closed ? 2 : 6; break;
/*      */       case 4:
/*  509 */         roiType = closed ? 0 : 6; break;
/*      */       default:
/*  511 */         if (segments <= 10)
/*  512 */           roiType = closed ? 2 : 6;
/*      */         else
/*  514 */           roiType = closed ? 3 : this.forceTrace ? 4 : 7;
/*  515 */         break;
/*      */       }
/*      */     else
/*  518 */       roiType = segments >= 2 ? 9 : 128;
/*  519 */     return roiType;
/*      */   }
/*      */ 
/*      */   private Roi createRoi(Vector xCoords, Vector yCoords, int roiType)
/*      */   {
/*  530 */     if (roiType == 128) return null;
/*  531 */     Roi roi = null;
/*  532 */     if ((xCoords.size() != yCoords.size()) || (xCoords.size() == 0)) return null;
/*      */ 
/*  534 */     int[] xPoints = new int[xCoords.size()];
/*  535 */     int[] yPoints = new int[yCoords.size()];
/*      */ 
/*  537 */     for (int i = 0; i < xPoints.length; i++) {
/*  538 */       xPoints[i] = (((Integer)xCoords.elementAt(i)).intValue() + this.x);
/*  539 */       yPoints[i] = (((Integer)yCoords.elementAt(i)).intValue() + this.y);
/*      */     }
/*      */ 
/*  542 */     int startX = 0;
/*  543 */     int startY = 0;
/*  544 */     int width = 0;
/*  545 */     int height = 0;
/*  546 */     switch (roiType) {
/*      */     case 9:
/*  548 */       roi = this; break;
/*      */     case 1:
/*  550 */       startX = xPoints[(xPoints.length - 4)];
/*  551 */       startY = yPoints[(yPoints.length - 3)];
/*  552 */       width = max(xPoints) - min(xPoints);
/*  553 */       height = max(yPoints) - min(yPoints);
/*  554 */       roi = new OvalRoi(startX, startY, width, height);
/*  555 */       break;
/*      */     case 0:
/*  557 */       startX = xPoints[0];
/*  558 */       startY = yPoints[0];
/*  559 */       width = max(xPoints) - min(xPoints);
/*  560 */       height = max(yPoints) - min(yPoints);
/*  561 */       roi = new Roi(startX, startY, width, height);
/*  562 */       break;
/*      */     case 5:
/*  563 */       roi = new Line(xPoints[0], yPoints[0], xPoints[1], yPoints[1]); break;
/*      */     case 2:
/*      */     case 3:
/*      */     case 4:
/*      */     case 6:
/*      */     case 7:
/*      */     case 8:
/*      */     default:
/*  565 */       int n = xPoints.length;
/*  566 */       roi = new PolygonRoi(xPoints, yPoints, n, roiType);
/*  567 */       if (roiType == 3) {
/*  568 */         double length = roi.getLength();
/*  569 */         double mag = this.ic != null ? this.ic.getMagnification() : 1.0D;
/*  570 */         length *= mag;
/*      */ 
/*  572 */         if (length / n >= 15.0D) {
/*  573 */           roi = new PolygonRoi(xPoints, yPoints, n, 2);
/*      */         }
/*      */       }
/*      */       break;
/*      */     }
/*      */ 
/*  579 */     return roi;
/*      */   }
/*      */ 
/*      */   public boolean contains(int x, int y)
/*      */   {
/*  588 */     if (this.shape == null) return false;
/*  589 */     return this.shape.contains(x - this.x, y - this.y);
/*      */   }
/*      */ 
/*      */   public double[] getFeretValues()
/*      */   {
/*  594 */     Roi[] rois = getRois();
/*  595 */     if ((rois != null) && (rois.length == 1))
/*  596 */       return rois[0].getFeretValues();
/*  597 */     double min = 1.7976931348623157E+308D; double diameter = 0.0D; double angle = 0.0D;
/*  598 */     int p1 = 0; int p2 = 0;
/*  599 */     double pw = 1.0D; double ph = 1.0D;
/*  600 */     if (this.imp != null) {
/*  601 */       Calibration cal = this.imp.getCalibration();
/*  602 */       pw = cal.pixelWidth;
/*  603 */       ph = cal.pixelHeight;
/*      */     }
/*  605 */     Shape shape = getShape();
/*  606 */     Shape s = null;
/*  607 */     Rectangle2D r = shape.getBounds2D();
/*  608 */     double cx = r.getX() + r.getWidth() / 2.0D;
/*  609 */     double cy = r.getY() + r.getHeight() / 2.0D;
/*  610 */     AffineTransform at = new AffineTransform();
/*  611 */     at.translate(cx, cy);
/*  612 */     for (int i = 0; i < 181; i++) {
/*  613 */       at.rotate(0.0174532925199433D);
/*  614 */       s = at.createTransformedShape(shape);
/*  615 */       r = s.getBounds2D();
/*  616 */       double max2 = Math.max(r.getWidth(), r.getHeight());
/*  617 */       if (max2 > diameter) {
/*  618 */         diameter = max2 * pw;
/*      */       }
/*      */ 
/*  621 */       double min2 = Math.min(r.getWidth(), r.getHeight());
/*  622 */       min = Math.min(min, min2);
/*      */     }
/*  624 */     if (pw != ph) {
/*  625 */       diameter = 0.0D;
/*  626 */       angle = 0.0D;
/*      */     }
/*  628 */     if (pw == ph) {
/*  629 */       min *= pw;
/*      */     } else {
/*  631 */       min = 0.0D;
/*  632 */       angle = 0.0D;
/*      */     }
/*  634 */     double[] a = new double[5];
/*  635 */     a[0] = diameter;
/*  636 */     a[1] = angle;
/*  637 */     a[2] = min;
/*  638 */     a[3] = 0.0D;
/*  639 */     a[4] = 0.0D;
/*  640 */     return a;
/*      */   }
/*      */ 
/*      */   public double getLength()
/*      */   {
/*  646 */     double length = 0.0D;
/*  647 */     Roi[] rois = getRois();
/*  648 */     if (rois != null) {
/*  649 */       for (int i = 0; i < rois.length; i++)
/*  650 */         length += rois[i].getLength();
/*      */     }
/*  652 */     return length;
/*      */   }
/*      */ 
/*      */   FlatteningPathIterator getFlatteningPathIterator(Shape s, double fl)
/*      */   {
/*  657 */     return (FlatteningPathIterator)s.getPathIterator(new AffineTransform(), fl);
/*      */   }
/*      */ 
/*      */   double cplength(CubicCurve2D.Double c)
/*      */   {
/*  662 */     double result = Math.sqrt(Math.pow(c.ctrlx1 - c.x1, 2.0D) + Math.pow(c.ctrly1 - c.y1, 2.0D));
/*  663 */     result += Math.sqrt(Math.pow(c.ctrlx2 - c.ctrlx1, 2.0D) + Math.pow(c.ctrly2 - c.ctrly1, 2.0D));
/*  664 */     result += Math.sqrt(Math.pow(c.x2 - c.ctrlx2, 2.0D) + Math.pow(c.y2 - c.ctrly2, 2.0D));
/*  665 */     return result;
/*      */   }
/*      */ 
/*      */   double qplength(QuadCurve2D.Double c)
/*      */   {
/*  670 */     double result = Math.sqrt(Math.pow(c.ctrlx - c.x1, 2.0D) + Math.pow(c.ctrly - c.y1, 2.0D));
/*  671 */     result += Math.sqrt(Math.pow(c.x2 - c.ctrlx, 2.0D) + Math.pow(c.y2 - c.ctrly, 2.0D));
/*  672 */     return result;
/*      */   }
/*      */ 
/*      */   double cclength(CubicCurve2D.Double c)
/*      */   {
/*  677 */     return Math.sqrt(Math.pow(c.x2 - c.x1, 2.0D) + Math.pow(c.y2 - c.y1, 2.0D));
/*      */   }
/*      */ 
/*      */   double qclength(QuadCurve2D.Double c) {
/*  681 */     return Math.sqrt(Math.pow(c.x2 - c.x1, 2.0D) + Math.pow(c.y2 - c.y1, 2.0D));
/*      */   }
/*      */ 
/*      */   double cBezLength(CubicCurve2D.Double c)
/*      */   {
/*  691 */     double l = 0.0D;
/*  692 */     double cl = cclength(c);
/*  693 */     double pl = cplength(c);
/*  694 */     if ((pl - cl) / 2.0D > this.maxerror)
/*      */     {
/*  696 */       CubicCurve2D.Double[] cc = cBezSplit(c);
/*  697 */       for (int i = 0; i < 2; i++) l += cBezLength(cc[i]);
/*  698 */       return l;
/*      */     }
/*  700 */     l = 0.5D * pl + 0.5D * cl;
/*  701 */     return l;
/*      */   }
/*      */ 
/*      */   double qBezLength(QuadCurve2D.Double c)
/*      */   {
/*  712 */     double l = 0.0D;
/*  713 */     double cl = qclength(c);
/*  714 */     double pl = qplength(c);
/*  715 */     if ((pl - cl) / 2.0D > this.maxerror)
/*      */     {
/*  717 */       QuadCurve2D.Double[] cc = qBezSplit(c);
/*  718 */       for (int i = 0; i < 2; i++) l += qBezLength(cc[i]);
/*  719 */       return l;
/*      */     }
/*  721 */     l = (2.0D * pl + cl) / 3.0D;
/*  722 */     return l;
/*      */   }
/*      */ 
/*      */   CubicCurve2D.Double[] cBezSplit(CubicCurve2D.Double c)
/*      */   {
/*  731 */     CubicCurve2D.Double[] cc = new CubicCurve2D.Double[2];
/*  732 */     for (int i = 0; i < 2; i++) cc[i] = new CubicCurve2D.Double();
/*  733 */     c.subdivide(cc[0], cc[1]);
/*  734 */     return cc;
/*      */   }
/*      */ 
/*      */   QuadCurve2D.Double[] qBezSplit(QuadCurve2D.Double c)
/*      */   {
/*  743 */     QuadCurve2D.Double[] cc = new QuadCurve2D.Double[2];
/*  744 */     for (int i = 0; i < 2; i++) cc[i] = new QuadCurve2D.Double();
/*  745 */     c.subdivide(cc[0], cc[1]);
/*  746 */     return cc;
/*      */   }
/*      */ 
/*      */   void scaleCoords(double[] c, double pw, double ph)
/*      */   {
/*  759 */     int k = c.length / 2;
/*  760 */     if (2 * k != c.length) return;
/*  761 */     for (int i = 0; i < c.length; i += 2)
/*      */     {
/*  763 */       c[i] *= pw;
/*  764 */       c[(i + 1)] *= ph;
/*      */     }
/*      */   }
/*      */ 
/*      */   Vector parseSegments(PathIterator pI) {
/*  769 */     Vector v = new Vector();
/*  770 */     if (parsePath(pI, null, v, null, null)) return v;
/*  771 */     return null;
/*      */   }
/*      */ 
/*      */   public float[] getShapeAsArray()
/*      */   {
/*  780 */     if (this.shape == null) return null;
/*      */ 
/*  783 */     PathIterator pIt = this.shape.getPathIterator(new AffineTransform());
/*  784 */     Vector h = new Vector();
/*  785 */     Vector s = new Vector();
/*  786 */     if (!parsePath(pIt, null, s, null, h)) return null;
/*  787 */     float[] result = new float[7 * s.size()];
/*      */ 
/*  790 */     int k = 0; int j = 0;
/*  791 */     int index = 0;
/*  792 */     for (int i = 0; i < s.size(); i++) {
/*  793 */       int segType = ((Integer)s.elementAt(i)).intValue();
/*      */       Point2D.Double p;
/*  794 */       switch (segType) { case 0:
/*      */       case 1:
/*  796 */         result[(index++)] = segType;
/*  797 */         p = (Point2D.Double)h.elementAt(j++);
/*  798 */         result[(index++)] = ((float)p.getX() + this.x); result[(index++)] = ((float)p.getY() + this.y);
/*  799 */         break;
/*      */       case 2:
/*  801 */         result[(index++)] = segType;
/*  802 */         p = (Point2D.Double)h.elementAt(j++);
/*  803 */         result[(index++)] = ((float)p.getX() + this.x); result[(index++)] = ((float)p.getY() + this.y);
/*  804 */         p = (Point2D.Double)h.elementAt(j++);
/*  805 */         result[(index++)] = ((float)p.getX() + this.x); result[(index++)] = ((float)p.getY() + this.y);
/*  806 */         break;
/*      */       case 3:
/*  808 */         result[(index++)] = segType;
/*  809 */         p = (Point2D.Double)h.elementAt(j++);
/*  810 */         result[(index++)] = ((float)p.getX() + this.x); result[(index++)] = ((float)p.getY() + this.y);
/*  811 */         p = (Point2D.Double)h.elementAt(j++);
/*  812 */         result[(index++)] = ((float)p.getX() + this.x); result[(index++)] = ((float)p.getY() + this.y);
/*  813 */         p = (Point2D.Double)h.elementAt(j++);
/*  814 */         result[(index++)] = ((float)p.getX() + this.x); result[(index++)] = ((float)p.getY() + this.y);
/*  815 */         break;
/*      */       case 4:
/*  817 */         result[(index++)] = segType;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  822 */     float[] result2 = new float[index];
/*  823 */     System.arraycopy(result, 0, result2, 0, result2.length);
/*  824 */     return result2;
/*      */   }
/*      */ 
/*      */   boolean parsePath(PathIterator pIter, double[] params, Vector segments, Vector rois, Vector handles)
/*      */   {
/*  887 */     boolean result = true;
/*  888 */     if (pIter == null) return false;
/*  889 */     double pw = 1.0D; double ph = 1.0D;
/*  890 */     if (this.imp != null) {
/*  891 */       Calibration cal = this.imp.getCalibration();
/*  892 */       pw = cal.pixelWidth;
/*  893 */       ph = cal.pixelHeight;
/*      */     }
/*  895 */     Vector xCoords = new Vector();
/*  896 */     Vector yCoords = new Vector();
/*  897 */     if (segments == null) segments = new Vector();
/*  898 */     if (handles == null) handles = new Vector();
/*      */ 
/*  900 */     if (params == null) params = new double[1];
/*  901 */     boolean shapeToRoi = params[0] == -1.0D;
/*  902 */     int subPaths = 0;
/*  903 */     int count = 0;
/*  904 */     int roiType = 0;
/*      */ 
/*  906 */     boolean closed = false;
/*  907 */     boolean linesOnly = true;
/*  908 */     boolean curvesOnly = true;
/*      */ 
/*  912 */     double sX = (0.0D / 0.0D);
/*  913 */     double sY = (0.0D / 0.0D);
/*  914 */     double x0 = (0.0D / 0.0D);
/*  915 */     double y0 = (0.0D / 0.0D);
/*  916 */     double usX = (0.0D / 0.0D);
/*  917 */     double usY = (0.0D / 0.0D);
/*  918 */     double ux0 = (0.0D / 0.0D);
/*  919 */     double uy0 = (0.0D / 0.0D);
/*  920 */     double pathLength = 0.0D;
/*      */ 
/*  922 */     boolean done = false;
/*  923 */     while (!done) {
/*  924 */       double[] coords = new double[6];
/*  925 */       double[] ucoords = new double[6];
/*  926 */       int segType = pIter.currentSegment(coords);
/*  927 */       segments.add(new Integer(segType));
/*  928 */       count++;
/*  929 */       System.arraycopy(coords, 0, ucoords, 0, coords.length);
/*  930 */       scaleCoords(coords, pw, ph);
/*      */       Shape curve;
/*  931 */       switch (segType) {
/*      */       case 0:
/*  933 */         if (subPaths > 0) {
/*  934 */           closed = ((int)ux0 == (int)usX) && ((int)uy0 == (int)usY);
/*  935 */           if ((closed) && ((int)ux0 != (int)usX) && ((int)uy0 != (int)usY)) {
/*  936 */             xCoords.add(new Integer(((Integer)xCoords.elementAt(0)).intValue()));
/*  937 */             yCoords.add(new Integer(((Integer)yCoords.elementAt(0)).intValue()));
/*      */           }
/*  939 */           if (rois != null) {
/*  940 */             roiType = guessType(count, linesOnly, curvesOnly, closed);
/*  941 */             Roi r = createRoi(xCoords, yCoords, roiType);
/*  942 */             if (r != null)
/*  943 */               rois.addElement(r);
/*      */           }
/*  945 */           xCoords = new Vector();
/*  946 */           yCoords = new Vector();
/*  947 */           count = 1;
/*      */         }
/*  949 */         subPaths++;
/*  950 */         usX = ucoords[0];
/*  951 */         usY = ucoords[1];
/*  952 */         ux0 = ucoords[0];
/*  953 */         uy0 = ucoords[1];
/*  954 */         sX = coords[0];
/*  955 */         sY = coords[1];
/*  956 */         x0 = coords[0];
/*  957 */         y0 = coords[1];
/*  958 */         handles.add(new Point2D.Double(ucoords[0], ucoords[1]));
/*  959 */         xCoords.add(new Integer((int)ucoords[0]));
/*  960 */         yCoords.add(new Integer((int)ucoords[1]));
/*  961 */         closed = false;
/*  962 */         break;
/*      */       case 1:
/*  964 */         linesOnly &= true;
/*  965 */         curvesOnly &= false;
/*  966 */         pathLength += Math.sqrt(Math.pow(y0 - coords[1], 2.0D) + Math.pow(x0 - coords[0], 2.0D));
/*  967 */         ux0 = ucoords[0];
/*  968 */         uy0 = ucoords[1];
/*  969 */         x0 = coords[0];
/*  970 */         y0 = coords[1];
/*  971 */         handles.add(new Point2D.Double(ucoords[0], ucoords[1]));
/*  972 */         xCoords.add(new Integer((int)ucoords[0]));
/*  973 */         yCoords.add(new Integer((int)ucoords[1]));
/*  974 */         closed = ((int)ux0 == (int)usX) && ((int)uy0 == (int)usY);
/*  975 */         break;
/*      */       case 2:
/*  977 */         linesOnly &= false;
/*  978 */         curvesOnly &= true;
/*  979 */         curve = new QuadCurve2D.Double(x0, y0, coords[0], coords[2], coords[2], coords[3]);
/*  980 */         pathLength += qBezLength((QuadCurve2D.Double)curve);
/*  981 */         ux0 = ucoords[2];
/*  982 */         uy0 = ucoords[3];
/*  983 */         x0 = coords[2];
/*  984 */         y0 = coords[3];
/*  985 */         handles.add(new Point2D.Double(ucoords[0], ucoords[1]));
/*  986 */         handles.add(new Point2D.Double(ucoords[2], ucoords[3]));
/*  987 */         xCoords.add(new Integer((int)ucoords[2]));
/*  988 */         yCoords.add(new Integer((int)ucoords[3]));
/*  989 */         closed = ((int)ux0 == (int)usX) && ((int)uy0 == (int)usY);
/*  990 */         break;
/*      */       case 3:
/*  992 */         linesOnly &= false;
/*  993 */         curvesOnly &= true;
/*  994 */         curve = new CubicCurve2D.Double(x0, y0, coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
/*  995 */         pathLength += cBezLength((CubicCurve2D.Double)curve);
/*  996 */         ux0 = ucoords[4];
/*  997 */         uy0 = ucoords[5];
/*  998 */         x0 = coords[4];
/*  999 */         y0 = coords[5];
/* 1000 */         handles.add(new Point2D.Double(ucoords[0], ucoords[1]));
/* 1001 */         handles.add(new Point2D.Double(ucoords[2], ucoords[3]));
/* 1002 */         handles.add(new Point2D.Double(ucoords[4], ucoords[5]));
/* 1003 */         xCoords.add(new Integer((int)ucoords[4]));
/* 1004 */         yCoords.add(new Integer((int)ucoords[5]));
/* 1005 */         closed = ((int)ux0 == (int)usX) && ((int)uy0 == (int)usY);
/* 1006 */         break;
/*      */       case 4:
/* 1008 */         if (((int)ux0 != (int)usX) && ((int)uy0 != (int)usY)) pathLength += Math.sqrt(Math.pow(x0 - sX, 2.0D) + Math.pow(y0 - sY, 2.0D));
/* 1009 */         closed = true;
/* 1010 */         break;
/*      */       }
/*      */ 
/* 1014 */       pIter.next();
/* 1015 */       done = (pIter.isDone()) || ((shapeToRoi) && (rois != null) && (rois.size() == 1));
/* 1016 */       if (done) {
/* 1017 */         if ((closed) && ((int)x0 != (int)sX) && ((int)y0 != (int)sY)) {
/* 1018 */           xCoords.add(new Integer(((Integer)xCoords.elementAt(0)).intValue()));
/* 1019 */           yCoords.add(new Integer(((Integer)yCoords.elementAt(0)).intValue()));
/*      */         }
/* 1021 */         if (rois != null) {
/* 1022 */           roiType = shapeToRoi ? 4 : guessType(count + 1, linesOnly, curvesOnly, closed);
/* 1023 */           Roi r = createRoi(xCoords, yCoords, roiType);
/* 1024 */           if (r != null)
/* 1025 */             rois.addElement(r);
/*      */         }
/*      */       }
/*      */     }
/* 1029 */     params[0] = pathLength;
/*      */ 
/* 1031 */     return result;
/*      */   }
/*      */ 
/*      */   public void draw(Graphics g)
/*      */   {
/* 1040 */     Color color = this.strokeColor != null ? this.strokeColor : ROIColor;
/* 1041 */     if (this.fillColor != null) color = this.fillColor;
/* 1042 */     g.setColor(color);
/* 1043 */     AffineTransform aTx = ((Graphics2D)g).getDeviceConfiguration().getDefaultTransform();
/* 1044 */     Graphics2D g2d = (Graphics2D)g;
/* 1045 */     if (this.stroke != null)
/* 1046 */       g2d.setStroke((this.ic != null) && (this.ic.getCustomRoi()) ? this.stroke : getScaledStroke());
/* 1047 */     this.mag = getMagnification();
/* 1048 */     int basex = 0; int basey = 0;
/* 1049 */     if (this.ic != null) {
/* 1050 */       Rectangle r = this.ic.getSrcRect();
/* 1051 */       basex = r.x; basey = r.y;
/*      */     }
/* 1053 */     aTx.setTransform(this.mag, 0.0D, 0.0D, this.mag, -basex * this.mag, -basey * this.mag);
/* 1054 */     aTx.translate(this.x, this.y);
/* 1055 */     if (this.fillColor != null)
/* 1056 */       g2d.fill(aTx.createTransformedShape(this.shape));
/*      */     else
/* 1058 */       g2d.draw(aTx.createTransformedShape(this.shape));
/* 1059 */     if (this.stroke != null) g2d.setStroke(defaultStroke);
/* 1060 */     if (Toolbar.getToolId() == 1)
/* 1061 */       drawRoiBrush(g);
/* 1062 */     if ((this.imp != null) && (this.imp.getRoi() != null)) showStatus();
/* 1063 */     if (this.updateFullWindow) {
/* 1064 */       this.updateFullWindow = false; this.imp.draw();
/*      */     }
/*      */   }
/*      */ 
/* 1068 */   public void drawRoiBrush(Graphics g) { g.setColor(ROIColor);
/* 1069 */     int size = Toolbar.getBrushSize();
/* 1070 */     if (size == 0) return;
/* 1071 */     int flags = this.ic.getModifiers();
/* 1072 */     if ((flags & 0x10) == 0) return;
/* 1073 */     size = (int)(size * this.mag);
/* 1074 */     Point p = this.ic.getCursorLoc();
/* 1075 */     int sx = this.ic.screenX(p.x);
/* 1076 */     int sy = this.ic.screenY(p.y);
/* 1077 */     g.drawOval(sx - size / 2, sy - size / 2, size, size);
/*      */   }
/*      */ 
/*      */   public void drawPixels(ImageProcessor ip)
/*      */   {
/* 1085 */     PathIterator pIter = this.shape.getPathIterator(new AffineTransform(), this.flatness);
/* 1086 */     float[] coords = new float[6];
/* 1087 */     float sx = 0.0F; float sy = 0.0F;
/* 1088 */     while (!pIter.isDone()) {
/* 1089 */       int segType = pIter.currentSegment(coords);
/* 1090 */       switch (segType) {
/*      */       case 0:
/* 1092 */         sx = coords[0];
/* 1093 */         sy = coords[1];
/* 1094 */         ip.moveTo(this.x + (int)sx, this.y + (int)sy);
/* 1095 */         break;
/*      */       case 1:
/* 1097 */         ip.lineTo(this.x + (int)coords[0], this.y + (int)coords[1]);
/* 1098 */         break;
/*      */       case 4:
/* 1100 */         ip.lineTo(this.x + (int)sx, this.y + (int)sy);
/* 1101 */         break;
/*      */       case 2:
/*      */       case 3:
/* 1104 */       }pIter.next();
/*      */     }
/*      */   }
/*      */ 
/*      */   public ImageProcessor getMask()
/*      */   {
/* 1111 */     if (this.shape == null) return null;
/* 1112 */     if ((this.cachedMask != null) && (this.cachedMask.getPixels() != null)) {
/* 1113 */       return this.cachedMask;
/*      */     }
/*      */ 
/* 1121 */     BufferedImage bi = new BufferedImage(this.width, this.height, 10);
/* 1122 */     Graphics2D g2d = bi.createGraphics();
/* 1123 */     g2d.setColor(Color.white);
/* 1124 */     g2d.fill(this.shape);
/* 1125 */     Raster raster = bi.getRaster();
/* 1126 */     DataBufferByte buffer = (DataBufferByte)raster.getDataBuffer();
/* 1127 */     byte[] mask = buffer.getData();
/* 1128 */     this.cachedMask = new ByteProcessor(this.width, this.height, mask, null);
/* 1129 */     return this.cachedMask;
/*      */   }
/*      */ 
/*      */   public Shape getShape()
/*      */   {
/* 1134 */     return this.shape;
/*      */   }
/*      */ 
/*      */   boolean setShape(Shape rhs)
/*      */   {
/* 1145 */     boolean result = true;
/* 1146 */     if (rhs == null) return false;
/* 1147 */     if (this.shape.equals(rhs)) return false;
/* 1148 */     this.shape = rhs;
/* 1149 */     this.type = 9;
/* 1150 */     Rectangle rect = this.shape.getBounds();
/* 1151 */     this.width = rect.width;
/* 1152 */     this.height = rect.height;
/* 1153 */     return true;
/*      */   }
/*      */ 
/*      */   private int min(int[] array)
/*      */   {
/* 1162 */     int val = array[0];
/* 1163 */     for (int i = 1; i < array.length; i++) val = Math.min(val, array[i]);
/* 1164 */     return val;
/*      */   }
/*      */ 
/*      */   private int max(int[] array)
/*      */   {
/* 1169 */     int val = array[0];
/* 1170 */     for (int i = 1; i < array.length; i++) val = Math.max(val, array[i]);
/* 1171 */     return val;
/*      */   }
/*      */ 
/*      */   static ShapeRoi getCircularRoi(int x, int y, int width)
/*      */   {
/* 1208 */     return new ShapeRoi(new OvalRoi(x - width / 2, y - width / 2, width, width));
/*      */   }
/*      */ 
/*      */   public int isHandle(int sx, int sy)
/*      */   {
/* 1213 */     return -1;
/*      */   }
/*      */ 
/*      */   public Polygon getConvexHull() {
/* 1217 */     Roi[] rois = getRois();
/* 1218 */     if ((rois != null) && (rois.length == 1)) {
/* 1219 */       return rois[0].getConvexHull();
/*      */     }
/* 1221 */     return null;
/*      */   }
/*      */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.gui.ShapeRoi
 * JD-Core Version:    0.6.2
 */