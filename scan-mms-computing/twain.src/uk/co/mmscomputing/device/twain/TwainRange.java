/*    */ package uk.co.mmscomputing.device.twain;
/*    */ 
/*    */ class TwainRange extends TwainContainer
/*    */ {
/*    */   private Object minValue;
/*    */   private Object maxValue;
/*    */   private Object stepSize;
/*    */   private Object defaultValue;
/*    */   private Object currentValue;
/*    */ 
/*    */   TwainRange(int paramInt, byte[] paramArrayOfByte)
/*    */   {
/* 24 */     super(paramInt, paramArrayOfByte);
/* 25 */     this.minValue = get32BitObjectAt(paramArrayOfByte, 2);
/* 26 */     this.maxValue = get32BitObjectAt(paramArrayOfByte, 6);
/* 27 */     this.stepSize = get32BitObjectAt(paramArrayOfByte, 10);
/* 28 */     this.defaultValue = get32BitObjectAt(paramArrayOfByte, 14);
/* 29 */     this.currentValue = get32BitObjectAt(paramArrayOfByte, 18);
/*    */   }
/*    */   int getType() {
/* 32 */     return 6;
/*    */   }
/*    */   byte[] getBytes() {
/* 35 */     byte[] arrayOfByte = new byte[22];
/* 36 */     jtwain.setINT16(arrayOfByte, 0, this.type);
/* 37 */     set32BitObjectAt(arrayOfByte, 2, this.minValue);
/* 38 */     set32BitObjectAt(arrayOfByte, 6, this.maxValue);
/* 39 */     set32BitObjectAt(arrayOfByte, 10, this.stepSize);
/* 40 */     set32BitObjectAt(arrayOfByte, 14, this.defaultValue);
/* 41 */     set32BitObjectAt(arrayOfByte, 18, this.currentValue);
/* 42 */     return arrayOfByte;
/*    */   }
/*    */   public Object getCurrentValue() throws TwainIOException {
/* 45 */     return this.currentValue; } 
/* 46 */   public void setCurrentValue(Object paramObject) throws TwainIOException { this.currentValue = paramObject; } 
/*    */   public Object getDefaultValue() throws TwainIOException {
/* 48 */     return this.defaultValue; } 
/* 49 */   public void setDefaultValue(Object paramObject) throws TwainIOException { this.defaultValue = paramObject; }
/*    */ 
/*    */   public Object[] getItems() {
/* 52 */     Object[] arrayOfObject = new Object[1];
/* 53 */     arrayOfObject[0] = this.currentValue;
/* 54 */     return arrayOfObject;
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 58 */     String str = super.toString();
/* 59 */     str = str + "minValue     = " + this.minValue + "\n";
/* 60 */     str = str + "maxValue     = " + this.maxValue + "\n";
/* 61 */     str = str + "stepSize     = " + this.stepSize + "\n";
/* 62 */     str = str + "defaultValue = " + this.defaultValue + "\n";
/* 63 */     str = str + "currentValue = " + this.currentValue + "\n";
/* 64 */     return str;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.device.twain.TwainRange
 * JD-Core Version:    0.6.2
 */