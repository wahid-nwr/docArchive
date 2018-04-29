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
/*    */ @JsonPropertyOrder({"value"})
/*    */ @XmlAccessorType(XmlAccessType.FIELD)
/*    */ @XmlRootElement
/*    */ public class LeadLengthD
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   @JsonProperty("value")
/*    */   @SerializedName("value")
/*    */   @XmlElement(name="value")
/*    */   private double _value;
/*    */ 
/*    */   public static LeadLengthD create(double value)
/*    */   {
/* 25 */     return new LeadLengthD(value);
/*    */   }
/*    */ 
/*    */   public LeadLengthD()
/*    */   {
/*    */   }
/*    */ 
/*    */   public LeadLengthD(double value)
/*    */   {
/* 34 */     this._value = value;
/*    */   }
/*    */ 
/*    */   public LeadLengthD clone()
/*    */   {
/* 39 */     return new LeadLengthD(this._value);
/*    */   }
/*    */ 
/*    */   public double getValue()
/*    */   {
/* 44 */     return this._value;
/*    */   }
/*    */ 
/*    */   public void setValue(double value)
/*    */   {
/* 49 */     this._value = value;
/*    */   }
/*    */ 
/*    */   public boolean equals(Object obj)
/*    */   {
/* 55 */     if ((obj == null) || (obj.getClass() != LeadLengthD.class))
/*    */     {
/* 57 */       return false;
/*    */     }
/* 59 */     LeadLengthD length = (LeadLengthD)obj;
/* 60 */     return length.getValue() == getValue();
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 66 */     return String.format("%1$s", new Object[] { Double.valueOf(this._value) });
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.LeadLengthD
 * JD-Core Version:    0.6.2
 */