/*    */ package uk.co.mmscomputing.device.twain;
/*    */ 
/*    */ import java.util.Enumeration;
/*    */ import java.util.Vector;
/*    */ 
/*    */ class TwainArray extends TwainContainer
/*    */ {
/*    */   int count;
/* 16 */   Vector items = new Vector();
/*    */ 
/*    */   TwainArray(int paramInt, byte[] paramArrayOfByte) {
/* 19 */     super(paramInt, paramArrayOfByte);
/* 20 */     this.count = jtwain.getINT32(paramArrayOfByte, 2);
/*    */ 
/* 22 */     int i = 0; for (int j = 6; i < this.count; i++) {
/* 23 */       this.items.add(getObjectAt(paramArrayOfByte, j));
/* 24 */       j += TwainConstants.typeSizes[this.type];
/*    */     }
/*    */   }
/*    */ 
/*    */   int getType()
/*    */   {
/* 34 */     return 3;
/*    */   }
/*    */   byte[] getBytes() {
/* 37 */     int i = this.items.size();
/* 38 */     int j = 6 + i * TwainConstants.typeSizes[this.type];
/* 39 */     byte[] arrayOfByte = new byte[j];
/* 40 */     jtwain.setINT16(arrayOfByte, 0, this.type);
/* 41 */     jtwain.setINT32(arrayOfByte, 2, i);
/*    */ 
/* 43 */     int k = 0; for (int m = 6; k < i; k++) {
/* 44 */       setObjectAt(arrayOfByte, m, this.items.get(k));
/* 45 */       m += TwainConstants.typeSizes[this.type];
/*    */     }
/* 47 */     return arrayOfByte;
/*    */   }
/*    */ 
/*    */   public Object getCurrentValue() throws TwainIOException {
/* 51 */     throw new TwainIOException(getClass().getName() + ".getCurrentValue:\n\tnot applicable");
/*    */   }
/*    */ 
/*    */   public void setCurrentValue(Object paramObject) throws TwainIOException {
/* 55 */     throw new TwainIOException(getClass().getName() + ".setCurrentValue:\n\tnot applicable");
/*    */   }
/*    */ 
/*    */   public Object getDefaultValue() throws TwainIOException {
/* 59 */     throw new TwainIOException(getClass().getName() + ".getDefaultValue:\n\tnot applicable");
/*    */   }
/*    */ 
/*    */   public void setDefaultValue(Object paramObject) throws TwainIOException {
/* 63 */     throw new TwainIOException(getClass().getName() + ".setDefaultValue:\n\tnot applicable");
/*    */   }
/*    */   public Object[] getItems() {
/* 66 */     return this.items.toArray();
/*    */   }
/*    */   public String toString() {
/* 69 */     String str = super.toString();
/* 70 */     str = str + "count        = " + this.count + "\n";
/*    */ 
/* 72 */     Enumeration localEnumeration = this.items.elements();
/* 73 */     for (int i = 0; localEnumeration.hasMoreElements(); i++) {
/* 74 */       str = str + "items[" + i + "] = " + localEnumeration.nextElement() + "\n";
/*    */     }
/* 76 */     return str;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.device.twain.TwainArray
 * JD-Core Version:    0.6.2
 */