/*    */ package leadtools;
/*    */ 
/*    */ import com.fasterxml.jackson.annotation.JsonAutoDetect;
/*    */ import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
/*    */ import com.fasterxml.jackson.annotation.JsonProperty;
/*    */ import com.fasterxml.jackson.annotation.JsonPropertyOrder;
/*    */ import com.google.gson.annotations.SerializedName;
/*    */ import java.io.Serializable;
/*    */ import javax.xml.bind.annotation.XmlAccessType;
/*    */ import javax.xml.bind.annotation.XmlAccessorType;
/*    */ import javax.xml.bind.annotation.XmlElement;
/*    */ import javax.xml.bind.annotation.XmlRootElement;
/*    */ 
/*    */ @JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY, creatorVisibility=JsonAutoDetect.Visibility.NONE, getterVisibility=JsonAutoDetect.Visibility.NONE, isGetterVisibility=JsonAutoDetect.Visibility.NONE, setterVisibility=JsonAutoDetect.Visibility.NONE)
/*    */ @JsonPropertyOrder({"x", "y"})
/*    */ @XmlAccessorType(XmlAccessType.FIELD)
/*    */ @XmlRootElement
/*    */ public final class LeadPoint
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   @JsonProperty("x")
/*    */   @SerializedName("x")
/*    */   @XmlElement(name="x")
/*    */   int _x;
/*    */ 
/*    */   @JsonProperty("y")
/*    */   @SerializedName("y")
/*    */   @XmlElement(name="y")
/*    */   int _y;
/*    */ 
/*    */   public static LeadPoint getEmpty()
/*    */   {
/* 27 */     return new LeadPoint();
/*    */   }
/*    */ 
/*    */   public boolean isEmpty() {
/* 31 */     return (this._x == 0) && (this._y == 0);
/*    */   }
/*    */ 
/*    */   public LeadPoint() {
/*    */   }
/*    */ 
/*    */   public static LeadPoint create(int x, int y) {
/* 38 */     return new LeadPoint(x, y);
/*    */   }
/*    */ 
/*    */   public LeadPoint(int x, int y) {
/* 42 */     this._x = x;
/* 43 */     this._y = y;
/*    */   }
/*    */ 
/*    */   public LeadPoint clone() {
/* 47 */     if (isEmpty()) {
/* 48 */       return new LeadPoint();
/*    */     }
/* 50 */     return new LeadPoint(this._x, this._y);
/*    */   }
/*    */ 
/*    */   public int getX() {
/* 54 */     return this._x;
/*    */   }
/*    */   public void setX(int value) {
/* 57 */     this._x = value;
/*    */   }
/*    */ 
/*    */   public int getY() {
/* 61 */     return this._y;
/*    */   }
/*    */   public void setY(int value) {
/* 64 */     this._y = value;
/*    */   }
/*    */ 
/*    */   public boolean equals(Object obj)
/*    */   {
/* 69 */     if ((obj == null) || (obj.getClass() != LeadPoint.class)) {
/* 70 */       return false;
/*    */     }
/* 72 */     LeadPoint point = (LeadPoint)obj;
/* 73 */     return (point._x == this._x) && (point._y == this._y);
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 78 */     return String.format("%d,%d", new Object[] { Integer.valueOf(this._x), Integer.valueOf(this._y) });
/*    */   }
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 83 */     return this._x ^ this._y;
/*    */   }
/*    */ 
/*    */   public LeadPointD toLeadPointD() {
/* 87 */     return new LeadPointD(this._x, this._y);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.LeadPoint
 * JD-Core Version:    0.6.2
 */