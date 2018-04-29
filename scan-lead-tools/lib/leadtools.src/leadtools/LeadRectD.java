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
/*     */ @JsonPropertyOrder({"x", "y", "width", "height"})
/*     */ @XmlAccessorType(XmlAccessType.FIELD)
/*     */ @XmlRootElement
/*     */ public final class LeadRectD
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private static final String _cannotModifyEmptyRectMessage = "Cannot modify empty LeadRectD";
/*     */   private static final String _sizeCannotBeNegativeMessage = "Width or Height of LeadRectD cannot be negative";
/*  25 */   private static final LeadRectD _empty = new LeadRectD();
/*     */ 
/*     */   @JsonProperty("x")
/*     */   @SerializedName("x")
/*     */   @XmlElement(name="x")
/*     */   double _x;
/*     */ 
/*     */   @JsonProperty("y")
/*     */   @SerializedName("y")
/*     */   @XmlElement(name="y")
/*     */   double _y;
/*     */ 
/*     */   @JsonProperty("width")
/*     */   @SerializedName("width")
/*     */   @XmlElement(name="width")
/*     */   double _width;
/*     */ 
/*     */   @JsonProperty("height")
/*     */   @SerializedName("height")
/*     */   @XmlElement(name="height")
/*     */   double _height;
/*     */ 
/*  50 */   public LeadRectD(LeadPointD location, LeadSizeD size) { if ((location.isEmpty()) || (size.isEmpty())) {
/*  51 */       copyFrom(_empty);
/*  52 */       return;
/*     */     }
/*     */ 
/*  55 */     this._x = location._x;
/*  56 */     this._y = location._y;
/*  57 */     this._width = size._width;
/*  58 */     this._height = size._height; }
/*     */ 
/*     */   public LeadRectD()
/*     */   {
/*  62 */     this._x = 0.0D;
/*  63 */     this._y = 0.0D;
/*  64 */     this._width = 0.0D;
/*  65 */     this._height = 0.0D;
/*     */   }
/*     */ 
/*     */   public LeadRectD(double x, double y, double width, double height) {
/*  69 */     if ((width < 0.0D) || (height < 0.0D)) {
/*  70 */       throw new IllegalArgumentException("Width or Height of LeadRectD cannot be negative");
/*     */     }
/*  72 */     this._x = x;
/*  73 */     this._y = y;
/*  74 */     this._width = width;
/*  75 */     this._height = height;
/*     */   }
/*     */ 
/*     */   public LeadRectD(LeadPointD point1, LeadPointD point2) {
/*  79 */     if ((point1.isEmpty()) || (point2.isEmpty())) {
/*  80 */       copyFrom(_empty);
/*  81 */       return;
/*     */     }
/*     */ 
/*  84 */     this._x = Math.min(point1._x, point2._x);
/*  85 */     this._y = Math.min(point1._y, point2._y);
/*  86 */     this._width = Math.max(Math.max(point1._x, point2._x) - this._x, 0.0D);
/*  87 */     this._height = Math.max(Math.max(point1._y, point2._y) - this._y, 0.0D);
/*     */   }
/*     */ 
/*     */   public LeadRectD(LeadSizeD size) {
/*  91 */     if (size.isEmpty()) {
/*  92 */       copyFrom(_empty);
/*  93 */       return;
/*     */     }
/*     */ 
/*  96 */     this._width = size._width;
/*  97 */     this._height = size._height;
/*     */   }
/*     */ 
/*     */   public static LeadRectD create(double x, double y, double width, double height) {
/* 101 */     if ((width < 0.0D) || (height < 0.0D)) {
/* 102 */       throw new IllegalArgumentException("Width or Height of LeadRectD cannot be negative");
/*     */     }
/* 104 */     LeadRectD obj = new LeadRectD(x, y, width, height);
/* 105 */     return obj;
/*     */   }
/*     */ 
/*     */   public static LeadRectD fromLTRB(double left, double top, double right, double bottom)
/*     */   {
/*     */     double width;
/*     */     double x;
/*     */     double width;
/* 111 */     if (right >= left) {
/* 112 */       double x = left;
/* 113 */       width = right - left;
/*     */     }
/*     */     else {
/* 116 */       x = right;
/* 117 */       width = left - right;
/*     */     }
/*     */     double height;
/*     */     double y;
/*     */     double height;
/* 122 */     if (bottom >= top) {
/* 123 */       double y = top;
/* 124 */       height = bottom - top;
/*     */     }
/*     */     else {
/* 127 */       y = bottom;
/* 128 */       height = top - bottom;
/*     */     }
/*     */ 
/* 131 */     return new LeadRectD(x, y, width, height);
/*     */   }
/*     */ 
/*     */   public LeadRectD clone() {
/* 135 */     LeadRectD obj = new LeadRectD();
/* 136 */     obj._x = this._x;
/* 137 */     obj._y = this._y;
/* 138 */     obj._width = this._width;
/* 139 */     obj._height = this._height;
/* 140 */     return obj;
/*     */   }
/*     */ 
/*     */   private void copyFrom(LeadRectD rect) {
/* 144 */     this._x = rect._x;
/* 145 */     this._y = rect._y;
/* 146 */     this._width = rect._width;
/* 147 */     this._height = rect._height;
/*     */   }
/*     */ 
/*     */   public static LeadRectD getEmpty() {
/* 151 */     return _empty.clone();
/*     */   }
/*     */ 
/*     */   public boolean isEmpty() {
/* 155 */     return this._width < 0.0D;
/*     */   }
/*     */ 
/*     */   public LeadPointD getLocation() {
/* 159 */     if (isEmpty()) {
/* 160 */       return LeadPointD.getEmpty();
/*     */     }
/* 162 */     return new LeadPointD(this._x, this._y);
/*     */   }
/*     */   public void setLocation(LeadPointD value) {
/* 165 */     if (isEmpty()) {
/* 166 */       throw new IllegalStateException("Cannot modify empty LeadRectD");
/*     */     }
/* 168 */     this._x = value._x;
/* 169 */     this._y = value._y;
/*     */   }
/*     */ 
/*     */   public LeadSizeD getSize() {
/* 173 */     if (isEmpty()) {
/* 174 */       return LeadSizeD.getEmpty();
/*     */     }
/* 176 */     return new LeadSizeD(this._width, this._height);
/*     */   }
/*     */   public void setSize(LeadSizeD value) {
/* 179 */     if (value.isEmpty()) {
/* 180 */       copyFrom(_empty);
/* 181 */       return;
/*     */     }
/*     */ 
/* 184 */     if (isEmpty()) {
/* 185 */       throw new IllegalStateException("Cannot modify empty LeadRectD");
/*     */     }
/* 187 */     this._width = value._width;
/* 188 */     this._height = value._height;
/*     */   }
/*     */ 
/*     */   public double getX() {
/* 192 */     return this._x;
/*     */   }
/*     */   public void setX(double value) {
/* 195 */     if (isEmpty()) {
/* 196 */       throw new IllegalStateException("Cannot modify empty LeadRectD");
/*     */     }
/* 198 */     this._x = value;
/*     */   }
/*     */ 
/*     */   public double getY() {
/* 202 */     return this._y;
/*     */   }
/*     */   public void setY(double value) {
/* 205 */     if (isEmpty()) {
/* 206 */       throw new IllegalStateException("Cannot modify empty LeadRectD");
/*     */     }
/* 208 */     this._y = value;
/*     */   }
/*     */ 
/*     */   public double getWidth() {
/* 212 */     return this._width;
/*     */   }
/*     */   public void setWidth(double value) {
/* 215 */     if (isEmpty()) {
/* 216 */       throw new IllegalStateException("Cannot modify empty LeadRectD");
/*     */     }
/* 218 */     if (value < 0.0D) {
/* 219 */       throw new IllegalArgumentException("Width or Height of LeadRectD cannot be negative");
/*     */     }
/* 221 */     this._width = value;
/*     */   }
/*     */ 
/*     */   public double getHeight() {
/* 225 */     return this._height;
/*     */   }
/*     */   public void setHeight(double value) {
/* 228 */     if (isEmpty()) {
/* 229 */       throw new IllegalStateException("Cannot modify empty LeadRectD");
/*     */     }
/* 231 */     if (value < 0.0D) {
/* 232 */       throw new IllegalArgumentException("Width or Height of LeadRectD cannot be negative");
/*     */     }
/* 234 */     this._height = value;
/*     */   }
/*     */ 
/*     */   public double getLeft() {
/* 238 */     return this._x;
/*     */   }
/*     */ 
/*     */   public double getTop() {
/* 242 */     return this._y;
/*     */   }
/*     */ 
/*     */   public double getRight() {
/* 246 */     if (isEmpty()) {
/* 247 */       return (-1.0D / 0.0D);
/*     */     }
/* 249 */     return this._x + this._width;
/*     */   }
/*     */ 
/*     */   public double getBottom() {
/* 253 */     if (isEmpty()) {
/* 254 */       return (-1.0D / 0.0D);
/*     */     }
/* 256 */     return this._y + this._height;
/*     */   }
/*     */ 
/*     */   public LeadPointD getTopLeft() {
/* 260 */     return new LeadPointD(getLeft(), getTop());
/*     */   }
/*     */ 
/*     */   public LeadPointD getTopRight() {
/* 264 */     return new LeadPointD(getRight(), getTop());
/*     */   }
/*     */ 
/*     */   public LeadPointD getBottomLeft() {
/* 268 */     return new LeadPointD(getLeft(), getBottom());
/*     */   }
/*     */ 
/*     */   public LeadPointD getBottomRight() {
/* 272 */     return new LeadPointD(getRight(), getBottom());
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 277 */     if ((obj == null) || (obj.getClass() != LeadRectD.class)) {
/* 278 */       return false;
/*     */     }
/* 280 */     LeadRectD rect = (LeadRectD)obj;
/*     */ 
/* 282 */     if (isEmpty()) {
/* 283 */       return rect.isEmpty();
/*     */     }
/* 285 */     return (LeadDoubleTools.areClose(this._x, rect._x)) && (LeadDoubleTools.areClose(this._y, rect._y)) && (LeadDoubleTools.areClose(this._width, rect._width)) && (LeadDoubleTools.areClose(this._height, rect._height));
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 294 */     if (isEmpty()) {
/* 295 */       return "Empty";
/*     */     }
/* 297 */     return String.format("%1$s,%2$s,%3$s,%4$s", new Object[] { Double.valueOf(this._x), Double.valueOf(this._y), Double.valueOf(this._width), Double.valueOf(this._height) });
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 302 */     if (isEmpty()) {
/* 303 */       return 0;
/*     */     }
/* 305 */     Double x = Double.valueOf(this._x);
/* 306 */     Double y = Double.valueOf(this._y);
/* 307 */     Double width = Double.valueOf(this._width);
/* 308 */     Double height = Double.valueOf(this._height);
/* 309 */     return x.hashCode() ^ y.hashCode() ^ width.hashCode() ^ height.hashCode();
/*     */   }
/*     */ 
/*     */   public boolean containsPoint(LeadPointD point) {
/* 313 */     return contains(point._x, point._y);
/*     */   }
/*     */ 
/*     */   public boolean contains(double x, double y) {
/* 317 */     return (!isEmpty()) && (containsInternal(x, y));
/*     */   }
/*     */ 
/*     */   public boolean containsRect(LeadRectD rect) {
/* 321 */     return (!isEmpty()) && (!rect.isEmpty()) && (this._x <= rect._x) && (this._y <= rect._y) && (this._x + this._width >= rect._x + rect._width) && (this._y + this._height >= rect._y + rect._height);
/*     */   }
/*     */ 
/*     */   public boolean intersectsWith(LeadRectD rect) {
/* 325 */     return (!isEmpty()) && (!rect.isEmpty()) && (rect.getLeft() <= getRight()) && (rect.getRight() >= getLeft()) && (rect.getTop() <= getBottom()) && (rect.getBottom() >= getTop());
/*     */   }
/*     */ 
/*     */   public void intersect(LeadRectD rect) {
/* 329 */     if (!intersectsWith(rect)) {
/* 330 */       copyFrom(_empty);
/* 331 */       return;
/*     */     }
/*     */ 
/* 334 */     double left = Math.max(getLeft(), rect.getLeft());
/* 335 */     double top = Math.max(getTop(), rect.getTop());
/* 336 */     this._width = Math.max(Math.min(getRight(), rect.getRight()) - left, 0.0D);
/* 337 */     this._height = Math.max(Math.min(getBottom(), rect.getBottom()) - top, 0.0D);
/* 338 */     this._x = left;
/* 339 */     this._y = top;
/*     */   }
/*     */ 
/*     */   public static LeadRectD intersectRects(LeadRectD rect1, LeadRectD rect2) {
/* 343 */     LeadRectD res = rect1.clone();
/* 344 */     res.intersect(rect2);
/* 345 */     return res;
/*     */   }
/*     */ 
/*     */   public void union(LeadRectD rect) {
/* 349 */     if (isEmpty()) {
/* 350 */       copyFrom(rect);
/* 351 */       return;
/*     */     }
/*     */ 
/* 354 */     if (!rect.isEmpty()) {
/* 355 */       double left = Math.min(getLeft(), rect.getLeft());
/* 356 */       double top = Math.min(getTop(), rect.getTop());
/*     */ 
/* 358 */       if ((rect.getWidth() == (1.0D / 0.0D)) || (getWidth() == (1.0D / 0.0D))) {
/* 359 */         this._width = (1.0D / 0.0D);
/*     */       }
/*     */       else {
/* 362 */         double right = Math.max(getRight(), rect.getRight());
/* 363 */         this._width = Math.max(right - left, 0.0D);
/*     */       }
/*     */ 
/* 366 */       if ((rect.getHeight() == (1.0D / 0.0D)) || (getHeight() == (1.0D / 0.0D))) {
/* 367 */         this._height = (1.0D / 0.0D);
/*     */       }
/*     */       else {
/* 370 */         double bottom = Math.max(getBottom(), rect.getBottom());
/* 371 */         this._height = Math.max(bottom - top, 0.0D);
/*     */       }
/* 373 */       this._x = left;
/* 374 */       this._y = top;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static LeadRectD unionRects(LeadRectD rect1, LeadRectD rect2) {
/* 379 */     LeadRectD res = rect1.clone();
/* 380 */     res.union(rect2);
/* 381 */     return res;
/*     */   }
/*     */ 
/*     */   public void union(LeadPointD point) {
/* 385 */     union(new LeadRectD(point, point));
/*     */   }
/*     */ 
/*     */   public static LeadRectD union(LeadRectD rect, LeadPointD point) {
/* 389 */     LeadRectD res = rect.clone();
/* 390 */     res.union(new LeadRectD(point, point));
/* 391 */     return res;
/*     */   }
/*     */ 
/*     */   public void offset(double offsetX, double offsetY) {
/* 395 */     if (isEmpty()) {
/* 396 */       throw new IllegalStateException("Cannot modify empty LeadRectD");
/*     */     }
/* 398 */     this._x += offsetX;
/* 399 */     this._y += offsetY;
/*     */   }
/*     */ 
/*     */   public static LeadRectD offsetRect(LeadRectD rect, double offsetX, double offsetY) {
/* 403 */     LeadRectD res = rect.clone();
/* 404 */     res.offset(offsetX, offsetY);
/* 405 */     return res;
/*     */   }
/*     */ 
/*     */   public void inflateSize(LeadSizeD size) {
/* 409 */     inflate(size._width, size._height);
/*     */   }
/*     */ 
/*     */   public void inflate(double width, double height) {
/* 413 */     if (isEmpty()) {
/* 414 */       throw new IllegalStateException("Cannot modify empty LeadRectD");
/*     */     }
/* 416 */     this._x -= width;
/* 417 */     this._y -= height;
/* 418 */     this._width += width;
/* 419 */     this._width += width;
/* 420 */     this._height += height;
/* 421 */     this._height += height;
/*     */ 
/* 423 */     if ((this._width < 0.0D) || (this._height < 0.0D))
/* 424 */       copyFrom(_empty);
/*     */   }
/*     */ 
/*     */   public static LeadRectD inflateRects(LeadRectD rect, LeadSizeD size) {
/* 428 */     LeadRectD res = rect.clone();
/* 429 */     res.inflate(size._width, size._height);
/* 430 */     return res;
/*     */   }
/*     */ 
/*     */   public static LeadRectD inflateRect(LeadRectD rect, double width, double height) {
/* 434 */     LeadRectD res = rect.clone();
/* 435 */     res.inflate(width, height);
/* 436 */     return res;
/*     */   }
/*     */ 
/*     */   public static LeadRectD transformRect(LeadRectD rect, LeadMatrix matrix) {
/* 440 */     return LeadMatrix.LeadMatrixUtil.transformRect(rect, matrix);
/*     */   }
/*     */ 
/*     */   public void transform(LeadMatrix matrix) {
/* 444 */     copyFrom(LeadMatrix.LeadMatrixUtil.transformRect(clone(), matrix));
/*     */   }
/*     */ 
/*     */   public void scale(double scaleX, double scaleY) {
/* 448 */     if (isEmpty()) {
/* 449 */       return;
/*     */     }
/* 451 */     this._x *= scaleX;
/* 452 */     this._y *= scaleY;
/* 453 */     this._width *= scaleX;
/* 454 */     this._height *= scaleY;
/* 455 */     if (scaleX < 0.0D) {
/* 456 */       this._x += this._width;
/* 457 */       this._width *= -1.0D;
/*     */     }
/* 459 */     if (scaleY < 0.0D) {
/* 460 */       this._y += this._height;
/* 461 */       this._height *= -1.0D;
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean containsInternal(double x, double y) {
/* 466 */     return (x >= this._x) && (x - this._width <= this._x) && (y >= this._y) && (y - this._height <= this._y);
/*     */   }
/*     */ 
/*     */   public LeadRect toLeadRect() {
/* 470 */     if (!isEmpty()) {
/* 471 */       return new LeadRect(LeadDoubleTools.toInt(this._x), LeadDoubleTools.toInt(this._y), LeadDoubleTools.toInt(this._width), LeadDoubleTools.toInt(this._height));
/*     */     }
/* 473 */     return LeadRect.getEmpty();
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  26 */     _empty._x = (1.0D / 0.0D);
/*  27 */     _empty._y = (1.0D / 0.0D);
/*  28 */     _empty._width = (-1.0D / 0.0D);
/*  29 */     _empty._height = (-1.0D / 0.0D);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.LeadRectD
 * JD-Core Version:    0.6.2
 */