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
/*     */ @JsonPropertyOrder({"width", "height"})
/*     */ @XmlAccessorType(XmlAccessType.FIELD)
/*     */ @XmlRootElement
/*     */ public final class LeadSizeD
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private static final String _cannotModifyEmptySizeMessage = "Cannot modify empty LeadSizeD";
/*     */   private static final String _sizeCannotBeNegativeMessage = "Width or Height of LeadSizeD cannot be negative";
/*  21 */   private static final LeadSizeD _empty = new LeadSizeD();
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
/*     */   public LeadSizeD() {  } 
/*  39 */   public LeadSizeD clone() { LeadSizeD obj = new LeadSizeD();
/*  40 */     obj._width = this._width;
/*  41 */     obj._height = this._height;
/*  42 */     return obj; }
/*     */ 
/*     */   public LeadSizeD(double width, double height)
/*     */   {
/*  46 */     if ((width < 0.0D) || (height < 0.0D)) {
/*  47 */       throw new IllegalArgumentException("Width or Height of LeadSizeD cannot be negative");
/*     */     }
/*  49 */     this._width = width;
/*  50 */     this._height = height;
/*     */   }
/*     */ 
/*     */   public static LeadSizeD create(double width, double height) {
/*  54 */     if ((width < 0.0D) || (height < 0.0D)) {
/*  55 */       throw new IllegalArgumentException("Width or Height of LeadSizeD cannot be negative");
/*     */     }
/*  57 */     LeadSizeD obj = new LeadSizeD(width, height);
/*  58 */     return obj;
/*     */   }
/*     */ 
/*     */   public static LeadSizeD getEmpty() {
/*  62 */     return _empty.clone();
/*     */   }
/*     */ 
/*     */   public boolean isEmpty() {
/*  66 */     return this._width < 0.0D;
/*     */   }
/*     */ 
/*     */   public double getWidth() {
/*  70 */     return this._width;
/*     */   }
/*     */   public void setWidth(double value) {
/*  73 */     if (isEmpty()) {
/*  74 */       throw new IllegalStateException("Cannot modify empty LeadSizeD");
/*     */     }
/*  76 */     if (value < 0.0D) {
/*  77 */       throw new IllegalArgumentException("Width or Height of LeadSizeD cannot be negative");
/*     */     }
/*  79 */     this._width = value;
/*     */   }
/*     */ 
/*     */   public double getHeight() {
/*  83 */     return this._height;
/*     */   }
/*     */   public void setHeight(double value) {
/*  86 */     if (isEmpty()) {
/*  87 */       throw new IllegalStateException("Cannot modify empty LeadSizeD");
/*     */     }
/*  89 */     if (value < 0.0D) {
/*  90 */       throw new IllegalArgumentException("Width or Height of LeadSizeD cannot be negative");
/*     */     }
/*  92 */     this._height = value;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/*  97 */     if ((obj == null) || (obj.getClass() != LeadSizeD.class)) {
/*  98 */       return false;
/*     */     }
/* 100 */     LeadSizeD sz = (LeadSizeD)obj;
/*     */ 
/* 102 */     if (isEmpty()) {
/* 103 */       return sz.isEmpty();
/*     */     }
/* 105 */     return (LeadDoubleTools.areClose(this._width, sz._width)) && (LeadDoubleTools.areClose(this._height, sz._height));
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 110 */     if (isEmpty()) {
/* 111 */       return 0;
/*     */     }
/* 113 */     Double w = Double.valueOf(this._width);
/* 114 */     Double h = Double.valueOf(this._height);
/* 115 */     return w.hashCode() ^ h.hashCode();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 120 */     if (isEmpty()) {
/* 121 */       return "Empty";
/*     */     }
/* 123 */     return String.format("%1$s,%2$s", new Object[] { Double.valueOf(this._width), Double.valueOf(this._height) });
/*     */   }
/*     */ 
/*     */   public LeadSize toLeadSize() {
/* 127 */     if (!isEmpty()) {
/* 128 */       return new LeadSize(LeadDoubleTools.toInt(this._width), LeadDoubleTools.toInt(this._height));
/*     */     }
/* 130 */     return LeadSize.getEmpty().clone();
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  22 */     _empty._width = (-1.0D / 0.0D);
/*  23 */     _empty._height = (-1.0D / 0.0D);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.LeadSizeD
 * JD-Core Version:    0.6.2
 */