/*    */ package leadtools.codecs;
/*    */ 
/*    */ import leadtools.internal.IsInternal;
/*    */ 
/*    */ @IsInternal
/*    */ class LEADFILETAG
/*    */ {
/*    */   public short uTag;
/*    */   public short uType;
/*    */   public int uCount;
/*    */   public int uDataSize;
/*    */   public int uDataOffset;
/*    */ 
/*    */   LEADFILETAG(short tag, short type, int count, int dataSize, int dataOffset)
/*    */   {
/* 16 */     this.uTag = tag;
/* 17 */     this.uType = type;
/* 18 */     this.uCount = count;
/* 19 */     this.uDataSize = dataSize;
/* 20 */     this.uDataOffset = dataOffset;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.LEADFILETAG
 * JD-Core Version:    0.6.2
 */