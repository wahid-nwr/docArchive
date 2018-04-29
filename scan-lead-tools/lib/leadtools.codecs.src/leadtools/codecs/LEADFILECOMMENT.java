/*    */ package leadtools.codecs;
/*    */ 
/*    */ import leadtools.internal.IsInternal;
/*    */ 
/*    */ @IsInternal
/*    */ class LEADFILECOMMENT
/*    */ {
/*    */   public int uType;
/*    */   public int uDataSize;
/*    */   public int uDataOffset;
/*    */ 
/*    */   public LEADFILECOMMENT(int type, int dataSize, int dataOffset)
/*    */   {
/* 13 */     this.uType = type;
/* 14 */     this.uDataSize = dataSize;
/* 15 */     this.uDataOffset = dataOffset;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.LEADFILECOMMENT
 * JD-Core Version:    0.6.2
 */