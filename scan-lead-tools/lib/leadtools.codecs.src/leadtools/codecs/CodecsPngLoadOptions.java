/*    */ package leadtools.codecs;
/*    */ 
/*    */ public class CodecsPngLoadOptions
/*    */ {
/*    */   private CodecsOptions _owner;
/*    */ 
/*    */   CodecsPngLoadOptions(CodecsOptions owner)
/*    */   {
/*  9 */     this._owner = owner;
/*    */   }
/*    */ 
/*    */   CodecsPngLoadOptions copy(CodecsOptions owner) {
/* 13 */     CodecsPngLoadOptions copy = new CodecsPngLoadOptions(owner);
/* 14 */     copy.setTrnsChunk(getTrnsChunk());
/*    */ 
/* 16 */     return copy;
/*    */   }
/*    */ 
/*    */   public byte[] getTrnsChunk()
/*    */   {
/* 21 */     return this._owner.getThreadData().aPNGTRNSData;
/*    */   }
/*    */ 
/*    */   public void setTrnsChunk(byte[] chunk)
/*    */   {
/* 26 */     this._owner.getThreadData().aPNGTRNSData = chunk;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsPngLoadOptions
 * JD-Core Version:    0.6.2
 */