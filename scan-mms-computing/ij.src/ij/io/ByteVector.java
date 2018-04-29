/*      */ package ij.io;
/*      */ 
/*      */ class ByteVector
/*      */ {
/*      */   private byte[] data;
/*      */   private int size;
/*      */ 
/*      */   public ByteVector()
/*      */   {
/*  986 */     this.data = new byte[10];
/*  987 */     this.size = 0;
/*      */   }
/*      */ 
/*      */   public ByteVector(int initialSize) {
/*  991 */     this.data = new byte[initialSize];
/*  992 */     this.size = 0;
/*      */   }
/*      */ 
/*      */   public ByteVector(byte[] byteBuffer) {
/*  996 */     this.data = byteBuffer;
/*  997 */     this.size = 0;
/*      */   }
/*      */ 
/*      */   public void add(byte x) {
/* 1001 */     if (this.size >= this.data.length) {
/* 1002 */       doubleCapacity();
/* 1003 */       add(x);
/*      */     } else {
/* 1005 */       this.data[(this.size++)] = x;
/*      */     }
/*      */   }
/*      */ 
/* 1009 */   public int size() { return this.size; }
/*      */ 
/*      */   public void add(byte[] array)
/*      */   {
/* 1013 */     int length = array.length;
/* 1014 */     while (this.data.length - this.size < length)
/* 1015 */       doubleCapacity();
/* 1016 */     System.arraycopy(array, 0, this.data, this.size, length);
/* 1017 */     this.size += length;
/*      */   }
/*      */ 
/*      */   void doubleCapacity()
/*      */   {
/* 1022 */     byte[] tmp = new byte[this.data.length * 2 + 1];
/* 1023 */     System.arraycopy(this.data, 0, tmp, 0, this.data.length);
/* 1024 */     this.data = tmp;
/*      */   }
/*      */ 
/*      */   public void clear() {
/* 1028 */     this.size = 0;
/*      */   }
/*      */ 
/*      */   public byte[] toByteArray() {
/* 1032 */     byte[] bytes = new byte[this.size];
/* 1033 */     System.arraycopy(this.data, 0, bytes, 0, this.size);
/* 1034 */     return bytes;
/*      */   }
/*      */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.io.ByteVector
 * JD-Core Version:    0.6.2
 */