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
/*     */ public final class LeadRect
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */ 
/*     */   @JsonProperty("x")
/*     */   @SerializedName("x")
/*     */   @XmlElement(name="x")
/*     */   int _x;
/*     */ 
/*     */   @JsonProperty("y")
/*     */   @SerializedName("y")
/*     */   @XmlElement(name="y")
/*     */   int _y;
/*     */ 
/*     */   @JsonProperty("width")
/*     */   @SerializedName("width")
/*     */   @XmlElement(name="width")
/*     */   int _width;
/*     */ 
/*     */   @JsonProperty("height")
/*     */   @SerializedName("height")
/*     */   @XmlElement(name="height")
/*     */   int _height;
/*     */   transient int _left;
/*     */   transient int _top;
/*     */   transient int _right;
/*     */   transient int _bottom;
/*     */ 
/*     */   public static LeadRect getEmpty()
/*     */   {
/*  17 */     return new LeadRect(0, 0, 0, 0);
/*     */   }
/*     */ 
/*     */   public boolean isEmpty() {
/*  21 */     return (this._left == 0) && (this._top == 0) && (this._right == 0) && (this._bottom == 0);
/*     */   }
/*     */ 
/*     */   public LeadRect()
/*     */   {
/*     */   }
/*     */ 
/*     */   public LeadRect clone()
/*     */   {
/*  53 */     return new LeadRect(getX(), getY(), getWidth(), getHeight());
/*     */   }
/*     */ 
/*     */   public LeadRect(int left, int top, int width, int height) {
/*  57 */     this._left = left;
/*  58 */     this._top = top;
/*  59 */     this._right = (left + width);
/*  60 */     this._bottom = (top + height);
/*  61 */     update();
/*     */   }
/*     */ 
/*     */   public static LeadRect fromLTRB(int left, int top, int right, int bottom) {
/*  65 */     return new LeadRect(left, top, right - left, bottom - top);
/*     */   }
/*     */ 
/*     */   public static LeadRect normalize(LeadRect rect) {
/*  69 */     LeadRect result = rect.clone();
/*  70 */     result.normalize();
/*  71 */     return result;
/*     */   }
/*     */ 
/*     */   public void normalize() {
/*  75 */     if ((this._left > this._right) || (this._top > this._bottom)) {
/*  76 */       int left = Math.min(this._left, this._right);
/*  77 */       int top = Math.min(this._top, this._bottom);
/*  78 */       int right = Math.max(this._left, this._right);
/*  79 */       int bottom = Math.max(this._top, this._bottom);
/*     */ 
/*  81 */       this._left = left;
/*  82 */       this._top = top;
/*  83 */       this._right = right;
/*  84 */       this._bottom = bottom;
/*  85 */       update();
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getX() {
/*  90 */     return this._left;
/*     */   }
/*     */ 
/*     */   public void setX(int value) {
/*  94 */     int width = this._right - this._left;
/*  95 */     this._left = value;
/*  96 */     this._right = (this._left + width);
/*  97 */     update();
/*     */   }
/*     */ 
/*     */   public int getY() {
/* 101 */     return this._top;
/*     */   }
/*     */ 
/*     */   public void setY(int value) {
/* 105 */     int height = this._bottom - this._top;
/* 106 */     this._top = value;
/* 107 */     this._bottom = (this._top + height);
/* 108 */     update();
/*     */   }
/*     */ 
/*     */   public int getRight() {
/* 112 */     return this._right;
/*     */   }
/*     */   public void setRight(int value) {
/* 115 */     this._right = value;
/* 116 */     update();
/*     */   }
/*     */ 
/*     */   public int getBottom() {
/* 120 */     return this._bottom;
/*     */   }
/*     */   public void setBottom(int value) {
/* 123 */     this._bottom = value;
/* 124 */     update();
/*     */   }
/*     */ 
/*     */   public int getLeft() {
/* 128 */     return getX();
/*     */   }
/*     */   public void setLeft(int value) {
/* 131 */     setX(value);
/* 132 */     update();
/*     */   }
/*     */ 
/*     */   public int getTop() {
/* 136 */     return getY();
/*     */   }
/*     */   public void setTop(int value) {
/* 139 */     setY(value);
/* 140 */     update();
/*     */   }
/*     */ 
/*     */   public int getWidth() {
/* 144 */     return this._right - this._left;
/*     */   }
/*     */   public void setWidth(int value) {
/* 147 */     this._right = (this._left + value);
/* 148 */     update();
/*     */   }
/*     */ 
/*     */   public int getHeight() {
/* 152 */     return this._bottom - this._top;
/*     */   }
/*     */   public void setHeight(int value) {
/* 155 */     this._bottom = (this._top + value);
/* 156 */     update();
/*     */   }
/*     */ 
/*     */   public LeadRect(LeadPoint location, LeadSize size) {
/* 160 */     this._left = location._x;
/* 161 */     this._top = location._y;
/* 162 */     this._right = (location._x + size._width);
/* 163 */     this._bottom = (location._y + size._height);
/* 164 */     update();
/*     */   }
/*     */ 
/*     */   public LeadPoint getLocation() {
/* 168 */     return new LeadPoint(getLeft(), getTop());
/*     */   }
/*     */   public void setLocation(LeadPoint value) {
/* 171 */     setLeft(value._x);
/* 172 */     setTop(value._y);
/*     */   }
/*     */ 
/*     */   public LeadSize getSize() {
/* 176 */     return new LeadSize(getWidth(), getHeight());
/*     */   }
/*     */   public void setSize(LeadSize value) {
/* 179 */     setWidth(value._width);
/* 180 */     setHeight(value._height);
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 185 */     if ((obj == null) || (obj.getClass() != LeadRect.class)) {
/* 186 */       return false;
/*     */     }
/* 188 */     LeadRect rc = (LeadRect)obj;
/*     */ 
/* 190 */     return (rc.getX() == getX()) && (rc.getY() == getY()) && (rc.getWidth() == getWidth()) && (rc.getHeight() == getHeight());
/*     */   }
/*     */ 
/*     */   public boolean contains(int x, int y) {
/* 194 */     return (getX() <= x) && (x < getX() + getWidth()) && (getY() <= y) && (y < getY() + getHeight());
/*     */   }
/*     */ 
/*     */   public boolean contains(LeadPoint pt) {
/* 198 */     return contains(pt._x, pt._y);
/*     */   }
/*     */ 
/*     */   public boolean contains(LeadRect rect) {
/* 202 */     return (getX() <= rect.getX()) && (rect.getX() + rect.getWidth() <= getX() + getWidth()) && (getY() <= rect.getY()) && (rect.getY() + rect.getHeight() <= getY() + getHeight());
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 207 */     return getX() ^ (getY() << 13 | getY() >> 19) ^ (getWidth() << 26 | getWidth() >> 6) ^ (getHeight() << 7 | getHeight() >> 25);
/*     */   }
/*     */ 
/*     */   public void inflate(int width, int height) {
/* 211 */     setX(getX() - width);
/* 212 */     setY(getY() - height);
/* 213 */     setWidth(getWidth() + 2 * width);
/* 214 */     setHeight(getHeight() + 2 * height);
/*     */   }
/*     */ 
/*     */   public void inflate(LeadSize size) {
/* 218 */     inflate(size._width, size._height);
/*     */   }
/*     */ 
/*     */   public static LeadRect inflate(LeadRect rect, int x, int y) {
/* 222 */     LeadRect rectangle = rect.clone();
/* 223 */     rectangle.inflate(x, y);
/* 224 */     return rectangle;
/*     */   }
/*     */ 
/*     */   public void intersect(LeadRect rect) {
/* 228 */     LeadRect temp = new LeadRect(getX(), getY(), getWidth(), getHeight());
/* 229 */     LeadRect rectangle = intersect(rect, temp);
/*     */ 
/* 231 */     setX(rectangle.getX());
/* 232 */     setY(rectangle.getY());
/* 233 */     setWidth(rectangle.getWidth());
/* 234 */     setHeight(rectangle.getHeight());
/*     */   }
/*     */ 
/*     */   public static LeadRect intersect(LeadRect a, LeadRect b) {
/* 238 */     int x = Math.max(a.getX(), b.getX());
/* 239 */     int right = Math.min(a.getX() + a.getWidth(), b.getX() + b.getWidth());
/* 240 */     int y = Math.max(a.getY(), b.getY());
/* 241 */     int bottom = Math.min(a.getY() + a.getHeight(), b.getY() + b.getHeight());
/* 242 */     if ((right >= x) && (bottom >= y)) {
/* 243 */       return new LeadRect(x, y, right - x, bottom - y);
/*     */     }
/* 245 */     return getEmpty();
/*     */   }
/*     */ 
/*     */   public boolean intersectsWith(LeadRect rect) {
/* 249 */     return (rect.getX() < getX() + getWidth()) && (getX() < rect.getX() + rect.getWidth()) && (rect.getY() < getY() + getHeight()) && (getY() < rect.getY() + rect.getHeight());
/*     */   }
/*     */ 
/*     */   public static LeadRect union(LeadRect a, LeadRect b) {
/* 253 */     int x = Math.min(a.getX(), b.getX());
/* 254 */     int right = Math.max(a.getX() + a.getWidth(), b.getX() + b.getWidth());
/* 255 */     int y = Math.min(a.getY(), b.getY());
/* 256 */     int bottom = Math.max(a.getY() + a.getHeight(), b.getY() + b.getHeight());
/* 257 */     return new LeadRect(x, y, right - x, bottom - y);
/*     */   }
/*     */ 
/*     */   public void offset(LeadPoint pos) {
/* 261 */     offset(pos._x, pos._y);
/*     */   }
/*     */ 
/*     */   public void offset(int x, int y) {
/* 265 */     setX(getX() + x);
/* 266 */     setY(getY() + y);
/*     */   }
/*     */ 
/*     */   public static LeadRect create(int x, int y, int width, int height) {
/* 270 */     return new LeadRect(x, y, width, height);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 275 */     return String.format("%d,%d,%d,%d", new Object[] { Integer.valueOf(getX()), Integer.valueOf(getY()), Integer.valueOf(getWidth()), Integer.valueOf(getHeight()) });
/*     */   }
/*     */ 
/*     */   public LeadRectD toLeadRectD() {
/* 279 */     return new LeadRectD(getX(), getY(), getWidth(), getHeight());
/*     */   }
/*     */ 
/*     */   private void update() {
/* 283 */     this._x = this._left;
/* 284 */     this._y = this._top;
/* 285 */     this._width = (this._right - this._left);
/* 286 */     this._height = (this._bottom - this._top);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.LeadRect
 * JD-Core Version:    0.6.2
 */