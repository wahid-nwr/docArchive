/*    */ package uk.co.mmscomputing.device.twain;
/*    */ 
/*    */ public class TwainOneValue extends TwainContainer
/*    */ {
/*    */   Object item;
/*    */ 
/*    */   TwainOneValue(int paramInt, byte[] paramArrayOfByte)
/*    */   {
/* 16 */     super(paramInt, paramArrayOfByte);
/* 17 */     this.item = get32BitObjectAt(paramArrayOfByte, 2);
/*    */   }
/*    */   int getType() {
/* 20 */     return 5;
/*    */   }
/*    */   byte[] getBytes() {
/* 23 */     byte[] arrayOfByte = new byte[6];
/* 24 */     jtwain.setINT16(arrayOfByte, 0, this.type);
/* 25 */     set32BitObjectAt(arrayOfByte, 2, this.item);
/* 26 */     return arrayOfByte;
/*    */   }
/*    */   public Object getCurrentValue() throws TwainIOException {
/* 29 */     return this.item; } 
/* 30 */   public void setCurrentValue(Object paramObject) throws TwainIOException { this.item = paramObject; } 
/*    */   public Object getDefaultValue() throws TwainIOException {
/* 32 */     return this.item; } 
/* 33 */   public void setDefaultValue(Object paramObject) throws TwainIOException { this.item = paramObject; }
/*    */ 
/*    */   public Object[] getItems() {
/* 36 */     Object[] arrayOfObject = new Object[1];
/* 37 */     arrayOfObject[0] = this.item;
/* 38 */     return arrayOfObject;
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 42 */     String str = super.toString();
/* 43 */     str = str + "item         = " + this.item + "\n";
/* 44 */     return str;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.device.twain.TwainOneValue
 * JD-Core Version:    0.6.2
 */