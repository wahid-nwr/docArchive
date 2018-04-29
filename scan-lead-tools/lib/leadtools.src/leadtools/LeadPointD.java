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
/*     */ @JsonPropertyOrder({"x", "y"})
/*     */ @XmlAccessorType(XmlAccessType.FIELD)
/*     */ @XmlRootElement
/*     */ public final class LeadPointD
/*     */   implements Serializable
/*     */ {
/*     */   private static final String _cannotModifyEmptyPointMessage = "Cannot modify empty LeadPointD";
/*     */   private static final long serialVersionUID = 1L;
/*  21 */   private static final LeadPointD _empty = new LeadPointD();
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
/*  36 */   public static LeadPointD getEmpty() { return _empty.clone(); }
/*     */ 
/*     */   public LeadPointD()
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean isEmpty() {
/*  43 */     return (Double.isNaN(this._x)) && (Double.isNaN(this._y));
/*     */   }
/*     */ 
/*     */   public LeadPointD(double x, double y) {
/*  47 */     this._x = x;
/*  48 */     this._y = y;
/*     */   }
/*     */ 
/*     */   public static LeadPointD create(double x, double y) {
/*  52 */     LeadPointD obj = new LeadPointD(x, y);
/*  53 */     return obj;
/*     */   }
/*     */ 
/*     */   public LeadPointD clone() {
/*  57 */     LeadPointD obj = new LeadPointD();
/*  58 */     obj._x = this._x;
/*  59 */     obj._y = this._y;
/*  60 */     return obj;
/*     */   }
/*     */ 
/*     */   public double getX() {
/*  64 */     return this._x;
/*     */   }
/*     */   public void setX(double value) {
/*  67 */     if (isEmpty()) {
/*  68 */       throw new IllegalStateException("Cannot modify empty LeadPointD");
/*     */     }
/*  70 */     this._x = value;
/*     */   }
/*     */ 
/*     */   public double getY() {
/*  74 */     return this._y;
/*     */   }
/*     */   public void setY(double value) {
/*  77 */     if (isEmpty()) {
/*  78 */       throw new IllegalStateException("Cannot modify empty LeadPointD");
/*     */     }
/*  80 */     this._y = value;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/*  85 */     if ((obj == null) || (obj.getClass() != LeadPointD.class)) {
/*  86 */       return false;
/*     */     }
/*  88 */     LeadPointD pt = (LeadPointD)obj;
/*     */ 
/*  90 */     if (isEmpty()) {
/*  91 */       return pt.isEmpty();
/*     */     }
/*  93 */     return (LeadDoubleTools.areClose(this._x, pt._x)) && (LeadDoubleTools.areClose(this._y, pt._y));
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/*  98 */     if (isEmpty()) {
/*  99 */       return "Empty";
/*     */     }
/* 101 */     return String.format("%1$s,%2$s", new Object[] { Double.valueOf(this._x), Double.valueOf(this._y) });
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 106 */     Double x = Double.valueOf(this._x);
/* 107 */     Double y = Double.valueOf(this._y);
/* 108 */     return x.hashCode() ^ y.hashCode();
/*     */   }
/*     */ 
/*     */   public void offset(double offsetX, double offsetY) {
/* 112 */     this._x += offsetX;
/* 113 */     this._y += offsetY;
/*     */   }
/*     */ 
/*     */   public static LeadPointD multiply(LeadPointD point, LeadMatrix matrix) {
/* 117 */     return matrix.transformPoint(point);
/*     */   }
/*     */ 
/*     */   public LeadPoint toLeadPoint() {
/* 121 */     if (!isEmpty()) {
/* 122 */       return new LeadPoint(LeadDoubleTools.toInt(this._x), LeadDoubleTools.toInt(this._y));
/*     */     }
/* 124 */     return LeadPoint.getEmpty();
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  22 */     _empty._x = (0.0D / 0.0D);
/*  23 */     _empty._y = (0.0D / 0.0D);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.LeadPointD
 * JD-Core Version:    0.6.2
 */