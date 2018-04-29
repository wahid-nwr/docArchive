/*     */ package leadtools;
/*     */ 
/*     */ import com.fasterxml.jackson.annotation.JsonAutoDetect;
/*     */ import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
/*     */ import com.fasterxml.jackson.annotation.JsonProperty;
/*     */ import com.fasterxml.jackson.annotation.JsonPropertyOrder;
/*     */ import com.google.gson.annotations.SerializedName;
/*     */ import java.io.Serializable;
/*     */ import javax.xml.bind.annotation.XmlAccessType;
/*     */ import javax.xml.bind.annotation.XmlAccessorType;
/*     */ import javax.xml.bind.annotation.XmlElement;
/*     */ import javax.xml.bind.annotation.XmlRootElement;
/*     */ 
/*     */ @JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY, creatorVisibility=JsonAutoDetect.Visibility.NONE, getterVisibility=JsonAutoDetect.Visibility.NONE, isGetterVisibility=JsonAutoDetect.Visibility.NONE, setterVisibility=JsonAutoDetect.Visibility.NONE)
/*     */ @JsonPropertyOrder({"m11", "m12", "m21", "m22", "offsetX", "offsetY"})
/*     */ @XmlAccessorType(XmlAccessType.FIELD)
/*     */ @XmlRootElement
/*     */ public final class LeadMatrix
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private static final int IDENTITY = 0;
/*     */   private static final int TRANSLATION = 1;
/*     */   private static final int SCALING = 2;
/*     */   private static final int UNKNOWN = 4;
/* 169 */   private static transient LeadMatrix _identity = createIdentity();
/*     */ 
/*     */   @JsonProperty("m11")
/*     */   @SerializedName("m11")
/*     */   @XmlElement(name="m11")
/*     */   double _m11;
/*     */ 
/*     */   @JsonProperty("m12")
/*     */   @SerializedName("m12")
/*     */   @XmlElement(name="m12")
/*     */   double _m12;
/*     */ 
/*     */   @JsonProperty("m21")
/*     */   @SerializedName("m21")
/*     */   @XmlElement(name="m21")
/*     */   double _m21;
/*     */ 
/*     */   @JsonProperty("m22")
/*     */   @SerializedName("m22")
/*     */   @XmlElement(name="m22")
/*     */   double _m22;
/*     */ 
/*     */   @JsonProperty("offsetX")
/*     */   @SerializedName("offsetX")
/*     */   @XmlElement(name="offsetX")
/*     */   double _offsetX;
/*     */ 
/*     */   @JsonProperty("offsetY")
/*     */   @SerializedName("offsetY")
/*     */   @XmlElement(name="offsetY")
/*     */   double _offsetY;
/*     */   private transient int _type;
/*     */ 
/*     */   public LeadMatrix() {  } 
/* 201 */   public LeadMatrix(double m11, double m12, double m21, double m22, double offsetX, double offsetY) { this._m11 = m11;
/* 202 */     this._m12 = m12;
/* 203 */     this._m21 = m21;
/* 204 */     this._m22 = m22;
/* 205 */     this._offsetX = offsetX;
/* 206 */     this._offsetY = offsetY;
/* 207 */     this._type = 4;
/* 208 */     deriveMatrixType(); }
/*     */ 
/*     */   public LeadMatrix clone()
/*     */   {
/* 212 */     LeadMatrix obj = new LeadMatrix();
/*     */ 
/* 214 */     obj._m11 = this._m11;
/* 215 */     obj._m12 = this._m12;
/* 216 */     obj._m21 = this._m21;
/* 217 */     obj._m22 = this._m22;
/* 218 */     obj._offsetX = this._offsetX;
/* 219 */     obj._offsetY = this._offsetY;
/* 220 */     obj._type = this._type;
/* 221 */     return obj;
/*     */   }
/*     */ 
/*     */   private void copyFrom(LeadMatrix matrix) {
/* 225 */     this._m11 = matrix._m11;
/* 226 */     this._m12 = matrix._m12;
/* 227 */     this._m21 = matrix._m21;
/* 228 */     this._m22 = matrix._m22;
/* 229 */     this._offsetX = matrix._offsetX;
/* 230 */     this._offsetY = matrix._offsetY;
/* 231 */     this._type = matrix._type;
/*     */   }
/*     */ 
/*     */   public static LeadMatrix getIdentity() {
/* 235 */     return _identity.clone();
/*     */   }
/*     */ 
/*     */   public boolean isIdentity() {
/* 239 */     return (this._type == 0) || ((this._m11 == 1.0D) && (this._m12 == 0.0D) && (this._m21 == 0.0D) && (this._m22 == 1.0D) && (this._offsetX == 0.0D) && (this._offsetY == 0.0D));
/*     */   }
/*     */ 
/*     */   public double getDeterminant() {
/* 243 */     switch (this._type) {
/*     */     case 0:
/*     */     case 1:
/* 246 */       return 1.0D;
/*     */     case 2:
/*     */     case 3:
/* 250 */       return this._m11 * this._m22;
/*     */     }
/*     */ 
/* 253 */     return this._m11 * this._m22 - this._m12 * this._m21;
/*     */   }
/*     */ 
/*     */   public boolean hasInverse()
/*     */   {
/* 258 */     return !LeadDoubleTools.isZero(getDeterminant());
/*     */   }
/*     */ 
/*     */   public double getM11() {
/* 262 */     if (this._type == 0) {
/* 263 */       return 1.0D;
/*     */     }
/* 265 */     return this._m11;
/*     */   }
/*     */ 
/*     */   public void setM11(double value) {
/* 269 */     if (this._type == 0)
/*     */     {
/* 271 */       setMatrix(value, 0.0D, 0.0D, 1.0D, 0.0D, 0.0D, 2);
/* 272 */       return;
/*     */     }
/*     */ 
/* 275 */     this._m11 = value;
/* 276 */     if (this._type != 4)
/* 277 */       this._type |= 2;
/*     */   }
/*     */ 
/*     */   public double getM12()
/*     */   {
/* 282 */     if (this._type == 0) {
/* 283 */       return 0.0D;
/*     */     }
/* 285 */     return this._m12;
/*     */   }
/*     */ 
/*     */   public void setM12(double value)
/*     */   {
/* 290 */     if (this._type == 0)
/*     */     {
/* 292 */       setMatrix(1.0D, value, 0.0D, 1.0D, 0.0D, 0.0D, 4);
/* 293 */       return;
/*     */     }
/*     */ 
/* 296 */     this._m12 = value;
/* 297 */     this._type = 4;
/*     */   }
/*     */ 
/*     */   public double getM21()
/*     */   {
/* 302 */     if (this._type == 0) {
/* 303 */       return 0.0D;
/*     */     }
/* 305 */     return this._m21;
/*     */   }
/*     */ 
/*     */   public void setM21(double value)
/*     */   {
/* 310 */     if (this._type == 0)
/*     */     {
/* 312 */       setMatrix(1.0D, 0.0D, value, 1.0D, 0.0D, 0.0D, 4);
/* 313 */       return;
/*     */     }
/*     */ 
/* 316 */     this._m21 = value;
/* 317 */     this._type = 4;
/*     */   }
/*     */ 
/*     */   public double getM22()
/*     */   {
/* 322 */     if (this._type == 0) {
/* 323 */       return 1.0D;
/*     */     }
/* 325 */     return this._m22;
/*     */   }
/*     */ 
/*     */   public void setM22(double value)
/*     */   {
/* 330 */     if (this._type == 0)
/*     */     {
/* 332 */       setMatrix(1.0D, 0.0D, 0.0D, value, 0.0D, 0.0D, 2);
/* 333 */       return;
/*     */     }
/*     */ 
/* 336 */     this._m22 = value;
/* 337 */     if (this._type != 4)
/* 338 */       this._type |= 2;
/*     */   }
/*     */ 
/*     */   public double getOffsetX()
/*     */   {
/* 343 */     if (this._type == 0) {
/* 344 */       return 0.0D;
/*     */     }
/* 346 */     return this._offsetX;
/*     */   }
/*     */ 
/*     */   public void setOffsetX(double value)
/*     */   {
/* 351 */     if (this._type == 0)
/*     */     {
/* 353 */       setMatrix(1.0D, 0.0D, 0.0D, 1.0D, value, 0.0D, 1);
/* 354 */       return;
/*     */     }
/*     */ 
/* 357 */     this._offsetX = value;
/* 358 */     if (this._type != 4)
/* 359 */       this._type |= 1;
/*     */   }
/*     */ 
/*     */   public double getOffsetY()
/*     */   {
/* 364 */     if (this._type == 0) {
/* 365 */       return 0.0D;
/*     */     }
/* 367 */     return this._offsetY;
/*     */   }
/*     */ 
/*     */   public void setOffsetY(double value)
/*     */   {
/* 372 */     if (this._type == 0)
/*     */     {
/* 374 */       setMatrix(1.0D, 0.0D, 0.0D, 1.0D, 0.0D, value, 1);
/* 375 */       return;
/*     */     }
/*     */ 
/* 378 */     this._offsetY = value;
/* 379 */     if (this._type != 4)
/* 380 */       this._type |= 1;
/*     */   }
/*     */ 
/*     */   private boolean isDistinguishedIdentity()
/*     */   {
/* 385 */     return this._type == 0;
/*     */   }
/*     */ 
/*     */   public void setIdentity()
/*     */   {
/* 390 */     this._type = 0;
/*     */   }
/*     */ 
/*     */   public static LeadMatrix multiply(LeadMatrix trans1, LeadMatrix trans2)
/*     */   {
/* 395 */     return LeadMatrixUtil.multiplyMatrix(trans1, trans2);
/*     */   }
/*     */ 
/*     */   public void append(LeadMatrix matrix)
/*     */   {
/* 400 */     copyFrom(LeadMatrixUtil.multiplyMatrix(this, matrix));
/*     */   }
/*     */ 
/*     */   public void prepend(LeadMatrix matrix)
/*     */   {
/* 405 */     copyFrom(LeadMatrixUtil.multiplyMatrix(matrix, this));
/*     */   }
/*     */ 
/*     */   public void rotate(double angle)
/*     */   {
/* 410 */     angle = LeadDoubleTools.normalizeAngle(angle);
/* 411 */     copyFrom(LeadMatrixUtil.multiplyMatrix(this, createRotationRadians(angle * 0.0174532925199433D)));
/*     */   }
/*     */ 
/*     */   public void rotatePrepend(double angle)
/*     */   {
/* 416 */     angle = LeadDoubleTools.normalizeAngle(angle);
/* 417 */     copyFrom(LeadMatrixUtil.multiplyMatrix(createRotationRadians(angle * 0.0174532925199433D), this));
/*     */   }
/*     */ 
/*     */   public void rotateAt(double angle, double centerX, double centerY)
/*     */   {
/* 422 */     angle = LeadDoubleTools.normalizeAngle(angle);
/* 423 */     copyFrom(LeadMatrixUtil.multiplyMatrix(this, createRotationAtPointRadians(angle * 0.0174532925199433D, centerX, centerY)));
/*     */   }
/*     */ 
/*     */   public void rotateAtPrepend(double angle, double centerX, double centerY)
/*     */   {
/* 428 */     angle = LeadDoubleTools.normalizeAngle(angle);
/* 429 */     copyFrom(LeadMatrixUtil.multiplyMatrix(createRotationAtPointRadians(angle * 0.0174532925199433D, centerX, centerY), this));
/*     */   }
/*     */ 
/*     */   public void scale(double scaleX, double scaleY)
/*     */   {
/* 434 */     copyFrom(LeadMatrixUtil.multiplyMatrix(this, createScaling(scaleX, scaleY)));
/*     */   }
/*     */ 
/*     */   public void scalePrepend(double scaleX, double scaleY)
/*     */   {
/* 439 */     copyFrom(LeadMatrixUtil.multiplyMatrix(createScaling(scaleX, scaleY), this));
/*     */   }
/*     */ 
/*     */   public void scaleAt(double scaleX, double scaleY, double centerX, double centerY)
/*     */   {
/* 444 */     copyFrom(LeadMatrixUtil.multiplyMatrix(this, createScalingAtPoint(scaleX, scaleY, centerX, centerY)));
/*     */   }
/*     */ 
/*     */   public void scaleAtPrepend(double scaleX, double scaleY, double centerX, double centerY)
/*     */   {
/* 449 */     copyFrom(LeadMatrixUtil.multiplyMatrix(createScalingAtPoint(scaleX, scaleY, centerX, centerY), this));
/*     */   }
/*     */ 
/*     */   public void skew(double skewX, double skewY)
/*     */   {
/* 454 */     skewX = LeadDoubleTools.normalizeAngle(skewX);
/* 455 */     skewY = LeadDoubleTools.normalizeAngle(skewY);
/* 456 */     copyFrom(LeadMatrixUtil.multiplyMatrix(this, createSkewRadians(skewX * 0.0174532925199433D, skewY * 0.0174532925199433D)));
/*     */   }
/*     */ 
/*     */   public void skewPrepend(double skewX, double skewY)
/*     */   {
/* 461 */     skewX = LeadDoubleTools.normalizeAngle(skewX);
/* 462 */     skewY = LeadDoubleTools.normalizeAngle(skewY);
/* 463 */     copyFrom(LeadMatrixUtil.multiplyMatrix(createSkewRadians(skewX * 0.0174532925199433D, skewY * 0.0174532925199433D), this));
/*     */   }
/*     */ 
/*     */   public void translate(double offsetX, double offsetY)
/*     */   {
/* 468 */     if (this._type == 0)
/*     */     {
/* 470 */       setMatrix(1.0D, 0.0D, 0.0D, 1.0D, offsetX, offsetY, 1);
/* 471 */       return;
/*     */     }
/*     */ 
/* 474 */     if (this._type == 4)
/*     */     {
/* 476 */       this._offsetX += offsetX;
/* 477 */       this._offsetY += offsetY;
/* 478 */       return;
/*     */     }
/*     */ 
/* 481 */     this._offsetX += offsetX;
/* 482 */     this._offsetY += offsetY;
/* 483 */     this._type |= 1;
/*     */   }
/*     */ 
/*     */   public void translatePrepend(double offsetX, double offsetY)
/*     */   {
/* 488 */     copyFrom(LeadMatrixUtil.multiplyMatrix(createTranslation(offsetX, offsetY), this));
/*     */   }
/*     */ 
/*     */   public LeadPointD transformPoint(LeadPointD point)
/*     */   {
/* 493 */     LeadPointD result = multiplyPoint(point, false);
/* 494 */     return result;
/*     */   }
/*     */ 
/*     */   public LeadPointD transformVector(LeadPointD point)
/*     */   {
/* 499 */     LeadPointD result = multiplyPoint(point, true);
/* 500 */     return result;
/*     */   }
/*     */ 
/*     */   public LeadRectD transformRect(LeadRectD rect)
/*     */   {
/* 505 */     LeadRectD result = LeadMatrixUtil.transformRect(rect, this);
/* 506 */     return result;
/*     */   }
/*     */ 
/*     */   public void transformPoints(LeadPointD[] points)
/*     */   {
/* 511 */     if (points != null)
/*     */     {
/* 513 */       for (int i = 0; i < points.length; i++)
/* 514 */         points[i] = multiplyPoint(points[i], false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void invert()
/*     */   {
/* 520 */     double determinant = getDeterminant();
/*     */ 
/* 522 */     switch (this._type)
/*     */     {
/*     */     case 0:
/* 525 */       break;
/*     */     case 1:
/* 528 */       this._offsetX = (-this._offsetX);
/* 529 */       this._offsetY = (-this._offsetY);
/* 530 */       return;
/*     */     case 2:
/* 533 */       this._m11 = (1.0D / this._m11);
/* 534 */       this._m22 = (1.0D / this._m22);
/* 535 */       return;
/*     */     case 3:
/* 538 */       this._m11 = (1.0D / this._m11);
/* 539 */       this._m22 = (1.0D / this._m22);
/* 540 */       this._offsetX = (-this._offsetX * this._m11);
/* 541 */       this._offsetY = (-this._offsetY * this._m22);
/* 542 */       return;
/*     */     default:
/* 546 */       double val = 1.0D / determinant;
/* 547 */       setMatrix(this._m22 * val, -this._m12 * val, -this._m21 * val, this._m11 * val, (this._m21 * this._offsetY - this._offsetX * this._m22) * val, (this._offsetX * this._m12 - this._m11 * this._offsetY) * val, 4);
/* 548 */       break;
/*     */     }
/*     */   }
/*     */ 
/*     */   private LeadPointD multiplyPoint(LeadPointD point, boolean vector)
/*     */   {
/* 555 */     LeadPointD result = point.clone();
/*     */ 
/* 557 */     switch (this._type)
/*     */     {
/*     */     case 0:
/* 560 */       return result;
/*     */     case 1:
/* 563 */       if (!vector)
/*     */       {
/* 565 */         result.setX(result.getX() + this._offsetX);
/* 566 */         result.setY(result.getY() + this._offsetY);
/*     */       }
/* 568 */       return result;
/*     */     case 2:
/* 571 */       result.setX(result.getX() * this._m11);
/* 572 */       result.setY(result.getY() * this._m22);
/* 573 */       return result;
/*     */     case 3:
/* 576 */       result.setX(result.getX() * this._m11);
/* 577 */       result.setY(result.getY() * this._m22);
/* 578 */       if (!vector)
/*     */       {
/* 580 */         result.setX(result.getX() + this._offsetX);
/* 581 */         result.setY(result.getY() + this._offsetY);
/*     */       }
/* 583 */       return result;
/*     */     }
/*     */ 
/* 587 */     double yFac = result.getY() * this._m21;
/* 588 */     double xFac = result.getX() * this._m12;
/*     */ 
/* 590 */     if (!vector)
/*     */     {
/* 592 */       yFac += this._offsetX;
/* 593 */       xFac += this._offsetY;
/*     */     }
/*     */ 
/* 596 */     result.setX(result.getX() * this._m11);
/* 597 */     result.setX(result.getX() + yFac);
/* 598 */     result.setY(result.getY() * this._m22);
/* 599 */     result.setY(result.getY() + xFac);
/* 600 */     return result;
/*     */   }
/*     */ 
/*     */   private static LeadMatrix createRotationRadians(double angle)
/*     */   {
/* 607 */     return createRotationAtPointRadians(angle, 0.0D, 0.0D);
/*     */   }
/*     */ 
/*     */   private static LeadMatrix createRotationAtPointRadians(double angle, double centerX, double centerY)
/*     */   {
/* 612 */     LeadMatrix result = new LeadMatrix();
/* 613 */     double sin = Math.sin(angle);
/* 614 */     double cos = Math.cos(angle);
/* 615 */     double offsetX = centerX * (1.0D - cos) + centerY * sin;
/* 616 */     double offsetY = centerY * (1.0D - cos) - centerX * sin;
/* 617 */     result.setMatrix(cos, sin, -sin, cos, offsetX, offsetY, 4);
/* 618 */     return result;
/*     */   }
/*     */ 
/*     */   private static LeadMatrix createScalingAtPoint(double scaleX, double scaleY, double centerX, double centerY)
/*     */   {
/* 623 */     LeadMatrix result = new LeadMatrix();
/* 624 */     result.setMatrix(scaleX, 0.0D, 0.0D, scaleY, centerX - scaleX * centerX, centerY - scaleY * centerY, 3);
/* 625 */     return result;
/*     */   }
/*     */ 
/*     */   private static LeadMatrix createScaling(double scaleX, double scaleY)
/*     */   {
/* 630 */     LeadMatrix result = new LeadMatrix();
/* 631 */     result.setMatrix(scaleX, 0.0D, 0.0D, scaleY, 0.0D, 0.0D, 2);
/* 632 */     return result;
/*     */   }
/*     */ 
/*     */   private static LeadMatrix createSkewRadians(double skewX, double skewY)
/*     */   {
/* 637 */     LeadMatrix result = new LeadMatrix();
/* 638 */     result.setMatrix(1.0D, Math.tan(skewY), Math.tan(skewX), 1.0D, 0.0D, 0.0D, 4);
/* 639 */     return result;
/*     */   }
/*     */ 
/*     */   private static LeadMatrix createTranslation(double offsetX, double offsetY)
/*     */   {
/* 644 */     LeadMatrix result = new LeadMatrix();
/* 645 */     result.setMatrix(1.0D, 0.0D, 0.0D, 1.0D, offsetX, offsetY, 1);
/* 646 */     return result;
/*     */   }
/*     */ 
/*     */   private static LeadMatrix createIdentity()
/*     */   {
/* 651 */     LeadMatrix result = new LeadMatrix();
/* 652 */     result.setMatrix(1.0D, 0.0D, 0.0D, 1.0D, 0.0D, 0.0D, 0);
/* 653 */     return result;
/*     */   }
/*     */ 
/*     */   private void setMatrix(double m11, double m12, double m21, double m22, double offsetX, double offsetY, int type)
/*     */   {
/* 658 */     this._m11 = m11;
/* 659 */     this._m12 = m12;
/* 660 */     this._m21 = m21;
/* 661 */     this._m22 = m22;
/* 662 */     this._offsetX = offsetX;
/* 663 */     this._offsetY = offsetY;
/* 664 */     this._type = type;
/*     */   }
/*     */ 
/*     */   private void deriveMatrixType()
/*     */   {
/* 669 */     this._type = 0;
/* 670 */     if ((this._m21 != 0.0D) || (this._m12 != 0.0D))
/*     */     {
/* 672 */       this._type = 4;
/* 673 */       return;
/*     */     }
/*     */ 
/* 676 */     if ((this._m11 != 1.0D) || (this._m22 != 1.0D)) {
/* 677 */       this._type = 2;
/*     */     }
/* 679 */     if ((this._offsetX != 0.0D) || (this._offsetY != 0.0D)) {
/* 680 */       this._type |= 1;
/*     */     }
/* 682 */     if ((this._type & 0x3) == 0)
/* 683 */       this._type = 0;
/*     */   }
/*     */ 
/*     */   public static boolean equals(LeadMatrix matrix1, LeadMatrix matrix2)
/*     */   {
/* 688 */     if ((matrix1.isDistinguishedIdentity()) || (matrix2.isDistinguishedIdentity())) {
/* 689 */       return matrix1.isIdentity() == matrix2.isIdentity();
/*     */     }
/* 691 */     return (matrix1.getM11() == matrix2.getM11()) && (matrix1.getM12() == matrix2.getM12()) && (matrix1.getM21() == matrix2.getM21()) && (matrix1.getM22() == matrix2.getM22()) && (matrix1.getOffsetX() == matrix2.getOffsetX()) && (matrix1.getOffsetY() == matrix2.getOffsetY());
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 697 */     if ((obj == null) || (obj.getClass() != LeadMatrix.class))
/*     */     {
/* 699 */       return false;
/*     */     }
/* 701 */     LeadMatrix matrix = (LeadMatrix)obj;
/* 702 */     return (matrix.getM11() == getM11()) && (matrix.getM12() == getM12()) && (matrix.getM21() == getM21()) && (matrix.getM22() == getM22()) && (matrix.getOffsetX() == getOffsetX()) && (matrix.getOffsetY() == getOffsetY()) && (matrix._type == this._type);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 718 */     if (isIdentity())
/* 719 */       return "Identity";
/* 720 */     return String.format("%1$s,%2$s,%3$s,%4$s,%5$s,%6$s", new Object[] { Double.valueOf(this._m11), Double.valueOf(this._m12), Double.valueOf(this._m21), Double.valueOf(this._m22), Double.valueOf(this._offsetX), Double.valueOf(this._offsetY) });
/*     */   }
/*     */ 
/*     */   static class LeadMatrixUtil
/*     */   {
/*     */     public static LeadRectD transformRect(LeadRectD rect, LeadMatrix matrix)
/*     */     {
/*  23 */       LeadRectD result = rect.clone();
/*     */ 
/*  25 */       if (rect.isEmpty()) {
/*  26 */         return result;
/*     */       }
/*  28 */       int type = matrix._type;
/*  29 */       if (type == 0) {
/*  30 */         return result;
/*     */       }
/*  32 */       if ((type & 0x2) != 0) {
/*  33 */         result.setX(result.getX() * matrix.getM11());
/*  34 */         result.setY(result.getY() * matrix.getM22());
/*  35 */         result.setWidth(result.getWidth() * matrix.getM11());
/*  36 */         result.setHeight(result.getHeight() * matrix.getM22());
/*  37 */         if (result.getWidth() < 0.0D) {
/*  38 */           result.setX(result.getX() + result.getWidth());
/*  39 */           result.setWidth(-result.getWidth());
/*     */         }
/*     */ 
/*  42 */         if (result.getHeight() < 0.0D) {
/*  43 */           result.setY(result.getY() + result.getHeight());
/*  44 */           result.setHeight(-result.getHeight());
/*     */         }
/*     */       }
/*     */ 
/*  48 */       if ((type & 0x1) != 0) {
/*  49 */         result.setX(result.getX() + matrix.getOffsetX());
/*  50 */         result.setY(result.getY() + matrix.getOffsetY());
/*     */       }
/*     */ 
/*  53 */       if (type == 4) {
/*  54 */         LeadPointD topLeft = matrix.transformPoint(result.getTopLeft());
/*  55 */         LeadPointD topRight = matrix.transformPoint(result.getTopRight());
/*  56 */         LeadPointD bottomRight = matrix.transformPoint(result.getBottomRight());
/*  57 */         LeadPointD bottomLeft = matrix.transformPoint(result.getBottomLeft());
/*  58 */         result.setX(Math.min(Math.min(topLeft.getX(), topRight.getX()), Math.min(bottomRight.getX(), bottomLeft.getX())));
/*  59 */         result.setY(Math.min(Math.min(topLeft.getY(), topRight.getY()), Math.min(bottomRight.getY(), bottomLeft.getY())));
/*  60 */         result.setWidth(Math.max(Math.max(topLeft.getX(), topRight.getX()), Math.max(bottomRight.getX(), bottomLeft.getX())) - result.getX());
/*  61 */         result.setHeight(Math.max(Math.max(topLeft.getY(), topRight.getY()), Math.max(bottomRight.getY(), bottomLeft.getY())) - result.getY());
/*     */       }
/*     */ 
/*  64 */       return result;
/*     */     }
/*     */ 
/*     */     private static LeadMatrix multiplyMatrix(LeadMatrix matrix1, LeadMatrix matrix2) {
/*  68 */       int type1 = matrix1._type;
/*  69 */       int type2 = matrix2._type;
/*  70 */       LeadMatrix result = matrix1.clone();
/*     */ 
/*  72 */       if (type2 == 0) {
/*  73 */         return result;
/*     */       }
/*  75 */       if (type1 == 0) {
/*  76 */         result = matrix2.clone();
/*  77 */         return result;
/*     */       }
/*     */ 
/*  80 */       if (type2 == 1) {
/*  81 */         result._offsetX += matrix2._offsetX;
/*  82 */         result._offsetY += matrix2._offsetY;
/*  83 */         if (type1 != 4)
/*  84 */           LeadMatrix.access$076(result, 1);
/*  85 */         return result;
/*     */       }
/*     */ 
/*  88 */       if (type1 != 1) {
/*  89 */         int tempType1 = type1 << 4 | type2;
/*  90 */         int tempType2 = tempType1;
/*  91 */         switch (tempType2) {
/*     */         case 34:
/*  93 */           result._m11 *= matrix2._m11;
/*  94 */           result._m22 *= matrix2._m22;
/*  95 */           return result;
/*     */         case 35:
/*  98 */           result._m11 *= matrix2._m11;
/*  99 */           result._m22 *= matrix2._m22;
/* 100 */           result._offsetX = matrix2._offsetX;
/* 101 */           result._offsetY = matrix2._offsetY;
/* 102 */           result._type = 3;
/* 103 */           return result;
/*     */         case 36:
/* 106 */           break;
/*     */         default:
/* 109 */           switch (tempType2) {
/*     */           case 50:
/* 111 */             result._m11 *= matrix2._m11;
/* 112 */             result._m22 *= matrix2._m22;
/* 113 */             result._offsetX *= matrix2._m11;
/* 114 */             result._offsetY *= matrix2._m22;
/* 115 */             return result;
/*     */           case 51:
/* 118 */             result._m11 *= matrix2._m11;
/* 119 */             result._m22 *= matrix2._m22;
/* 120 */             result._offsetX = (matrix2._m11 * result._offsetX + matrix2._offsetX);
/* 121 */             result._offsetY = (matrix2._m22 * result._offsetY + matrix2._offsetY);
/* 122 */             return result;
/*     */           case 52:
/* 125 */             break;
/*     */           default:
/* 128 */             switch (tempType2) {
/*     */             case 66:
/*     */             case 67:
/*     */             case 68:
/* 132 */               break;
/*     */             default:
/* 135 */               return result;
/*     */             }
/*     */             break;
/*     */           }
/*     */ 
/*     */           break;
/*     */         }
/*     */ 
/* 143 */         result = new LeadMatrix(matrix1._m11 * matrix2._m11 + matrix1._m12 * matrix2._m21, matrix1._m11 * matrix2._m12 + matrix1._m12 * matrix2._m22, matrix1._m21 * matrix2._m11 + matrix1._m22 * matrix2._m21, matrix1._m21 * matrix2._m12 + matrix1._m22 * matrix2._m22, matrix1._offsetX * matrix2._m11 + matrix1._offsetY * matrix2._m21 + matrix2._offsetX, matrix1._offsetX * matrix2._m12 + matrix1._offsetY * matrix2._m22 + matrix2._offsetY);
/*     */ 
/* 150 */         return result;
/*     */       }
/*     */ 
/* 153 */       double offsetX = matrix1._offsetX;
/* 154 */       double offsetY = matrix1._offsetY;
/* 155 */       result = matrix2.clone();
/* 156 */       result._offsetX = (offsetX * matrix2._m11 + offsetY * matrix2._m21 + matrix2._offsetX);
/* 157 */       result._offsetY = (offsetX * matrix2._m12 + offsetY * matrix2._m22 + matrix2._offsetY);
/*     */ 
/* 159 */       if (type2 == 4) {
/* 160 */         result._type = 4;
/* 161 */         return result;
/*     */       }
/*     */ 
/* 164 */       result._type = 3;
/* 165 */       return result;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.LeadMatrix
 * JD-Core Version:    0.6.2
 */