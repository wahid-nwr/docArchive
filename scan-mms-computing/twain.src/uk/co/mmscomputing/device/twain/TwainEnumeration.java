/*    */ package uk.co.mmscomputing.device.twain;
/*    */ 
/*    */ import java.util.Enumeration;
/*    */ import java.util.Vector;
/*    */ 
/*    */ class TwainEnumeration extends TwainContainer
/*    */ {
/*    */   int count;
/*    */   int currentIndex;
/*    */   int defaultIndex;
/* 21 */   Vector items = new Vector();
/*    */ 
/*    */   TwainEnumeration(int paramInt, byte[] paramArrayOfByte) {
/* 24 */     super(paramInt, paramArrayOfByte);
/* 25 */     this.count = jtwain.getINT32(paramArrayOfByte, 2);
/* 26 */     this.currentIndex = jtwain.getINT32(paramArrayOfByte, 6);
/* 27 */     this.defaultIndex = jtwain.getINT32(paramArrayOfByte, 10);
/*    */ 
/* 29 */     int i = 0; for (int j = 14; i < this.count; i++) {
/* 30 */       this.items.add(getObjectAt(paramArrayOfByte, j));
/* 31 */       j += TwainConstants.typeSizes[this.type];
/*    */     }
/*    */   }
/*    */ 
/* 35 */   int getType() { return 4; } 
/*    */   public Object[] getItems() {
/* 37 */     return this.items.toArray();
/*    */   }
/*    */   byte[] getBytes() {
/* 40 */     int i = this.items.size();
/* 41 */     int j = 14 + i * TwainConstants.typeSizes[this.type];
/* 42 */     byte[] arrayOfByte = new byte[j];
/* 43 */     jtwain.setINT16(arrayOfByte, 0, this.type);
/* 44 */     jtwain.setINT32(arrayOfByte, 2, i);
/* 45 */     jtwain.setINT32(arrayOfByte, 6, this.currentIndex);
/* 46 */     jtwain.setINT32(arrayOfByte, 10, this.defaultIndex);
/*    */ 
/* 48 */     int k = 0; for (int m = 14; k < i; k++) {
/* 49 */       setObjectAt(arrayOfByte, m, this.items.get(k));
/* 50 */       m += TwainConstants.typeSizes[this.type];
/*    */     }
/* 52 */     return arrayOfByte;
/*    */   }
/*    */ 
/*    */   public Object getCurrentValue() throws TwainIOException {
/* 56 */     return this.items.get(this.currentIndex);
/*    */   }
/*    */ 
/*    */   public void setCurrentValue(Object paramObject) throws TwainIOException {
/* 60 */     int i = this.items.size();
/* 61 */     for (int j = 0; j < i; j++) {
/* 62 */       Object localObject = this.items.get(j);
/* 63 */       if (paramObject.equals(localObject)) {
/* 64 */         this.currentIndex = j;
/* 65 */         return;
/*    */       }
/*    */     }
/* 68 */     throw new TwainIOException(getClass().getName() + ".setCurrentValue:\n\tCould not find " + paramObject.toString());
/*    */   }
/*    */ 
/*    */   public Object getDefaultValue() throws TwainIOException {
/* 72 */     return this.items.get(this.defaultIndex);
/*    */   }
/*    */ 
/*    */   public void setDefaultValue(Object paramObject) throws TwainIOException {
/* 76 */     int i = this.items.size();
/* 77 */     for (int j = 0; j < i; j++) {
/* 78 */       Object localObject = this.items.get(j);
/* 79 */       if (paramObject.equals(localObject)) {
/* 80 */         this.defaultIndex = j;
/* 81 */         return;
/*    */       }
/*    */     }
/* 84 */     throw new TwainIOException(getClass().getName() + ".setDefaultValue:\n\tCould not find " + paramObject.toString());
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 88 */     String str = super.toString();
/* 89 */     str = str + "count        = " + this.count + "\n";
/* 90 */     str = str + "currentIndex = " + this.currentIndex + "\n";
/* 91 */     str = str + "defaultIndex = " + this.defaultIndex + "\n";
/*    */ 
/* 93 */     Enumeration localEnumeration = this.items.elements();
/* 94 */     for (int i = 0; localEnumeration.hasMoreElements(); i++) {
/* 95 */       str = str + "items[" + i + "] = " + localEnumeration.nextElement() + "\n";
/*    */     }
/* 97 */     return str;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.device.twain.TwainEnumeration
 * JD-Core Version:    0.6.2
 */