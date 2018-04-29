/*    */ package ij.io;
/*    */ 
/*    */ public class BitBuffer
/*    */ {
/*    */   private int currentByte;
/*    */   private int currentBit;
/*    */   private byte[] byteBuffer;
/*    */   private int eofByte;
/*    */   private int[] backMask;
/*    */   private int[] frontMask;
/*    */   private boolean eofFlag;
/*    */ 
/*    */   public BitBuffer(byte[] byteBuffer)
/*    */   {
/* 18 */     this.byteBuffer = byteBuffer;
/* 19 */     this.currentByte = 0;
/* 20 */     this.currentBit = 0;
/* 21 */     this.eofByte = byteBuffer.length;
/* 22 */     this.backMask = new int[] { 0, 1, 3, 7, 15, 31, 63, 127 };
/*    */ 
/* 24 */     this.frontMask = new int[] { 0, 128, 192, 224, 240, 248, 252, 254 };
/*    */   }
/*    */ 
/*    */   public int getBits(int bitsToRead)
/*    */   {
/* 29 */     if (bitsToRead == 0)
/* 30 */       return 0;
/* 31 */     if (this.eofFlag)
/* 32 */       return -1;
/* 33 */     int toStore = 0;
/* 34 */     while ((bitsToRead != 0) && (!this.eofFlag)) {
/* 35 */       if (bitsToRead >= 8 - this.currentBit) {
/* 36 */         if (this.currentBit == 0) {
/* 37 */           toStore <<= 8;
/* 38 */           int cb = this.byteBuffer[this.currentByte];
/* 39 */           toStore += (cb < 0 ? 256 + cb : cb);
/* 40 */           bitsToRead -= 8;
/* 41 */           this.currentByte += 1;
/*    */         } else {
/* 43 */           toStore <<= 8 - this.currentBit;
/* 44 */           toStore += (this.byteBuffer[this.currentByte] & this.backMask[(8 - this.currentBit)]);
/* 45 */           bitsToRead -= 8 - this.currentBit;
/* 46 */           this.currentBit = 0;
/* 47 */           this.currentByte += 1;
/*    */         }
/*    */       } else {
/* 50 */         toStore <<= bitsToRead;
/* 51 */         int cb = this.byteBuffer[this.currentByte];
/* 52 */         cb = cb < 0 ? 256 + cb : cb;
/* 53 */         toStore += ((cb & 255 - this.frontMask[this.currentBit]) >> 8 - (this.currentBit + bitsToRead));
/* 54 */         this.currentBit += bitsToRead;
/* 55 */         bitsToRead = 0;
/*    */       }
/* 57 */       if (this.currentByte == this.eofByte) {
/* 58 */         this.eofFlag = true;
/* 59 */         return toStore;
/*    */       }
/*    */     }
/* 62 */     return toStore;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.io.BitBuffer
 * JD-Core Version:    0.6.2
 */