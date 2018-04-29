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
/*    */ @JsonPropertyOrder({"width", "height"})
/*    */ @XmlAccessorType(XmlAccessType.FIELD)
/*    */ @XmlRootElement
/*    */ public final class LeadSize
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   @JsonProperty("width")
/*    */   @SerializedName("width")
/*    */   @XmlElement(name="width")
/*    */   int _width;
/*    */ 
/*    */   @JsonProperty("height")
/*    */   @SerializedName("height")
/*    */   @XmlElement(name="height")
/*    */   int _height;
/*    */ 
/*    */   public static LeadSize getEmpty()
/*    */   {
/* 18 */     return new LeadSize(0, 0);
/*    */   }
/*    */ 
/*    */   public boolean isEmpty() {
/* 22 */     return (this._width == 0) && (this._height == 0);
/*    */   }
/*    */ 
/*    */   public static LeadSize create(int width, int height)
/*    */   {
/* 35 */     return new LeadSize(width, height);
/*    */   }
/*    */ 
/*    */   public LeadSize() {
/*    */   }
/*    */ 
/*    */   public LeadSize(int width, int height) {
/* 42 */     this._width = width;
/* 43 */     this._height = height;
/*    */   }
/*    */ 
/*    */   public int getWidth() {
/* 47 */     return this._width;
/*    */   }
/*    */   public void setWidth(int value) {
/* 50 */     this._width = value;
/*    */   }
/*    */ 
/*    */   public int getHeight() {
/* 54 */     return this._height;
/*    */   }
/*    */   public void setHeight(int value) {
/* 57 */     this._height = value;
/*    */   }
/*    */ 
/*    */   public boolean equals(Object obj)
/*    */   {
/* 62 */     if ((obj == null) || (obj.getClass() != LeadSize.class)) {
/* 63 */       return false;
/*    */     }
/* 65 */     LeadSize size = (LeadSize)obj;
/* 66 */     return (size._width == this._width) && (size._height == this._height);
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 71 */     return String.format("%d,%d", new Object[] { Integer.valueOf(this._width), Integer.valueOf(this._height) });
/*    */   }
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 76 */     return this._width ^ this._height;
/*    */   }
/*    */ 
/*    */   public LeadSize clone() {
/* 80 */     return new LeadSize(this._width, this._height);
/*    */   }
/*    */ 
/*    */   public LeadSizeD toLeadSizeD() {
/* 84 */     return new LeadSizeD(this._width, this._height);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.LeadSize
 * JD-Core Version:    0.6.2
 */