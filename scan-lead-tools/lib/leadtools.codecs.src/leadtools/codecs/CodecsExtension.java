/*    */ package leadtools.codecs;
/*    */ 
/*    */ import java.util.UUID;
/*    */ 
/*    */ public final class CodecsExtension
/*    */ {
/*    */   private String _Name;
/*    */   private long _DataLength;
/*    */   private byte[] _Data;
/*    */   private UUID _Clsid;
/*    */   private byte _ucDefault;
/*    */ 
/*    */   public String getName()
/*    */   {
/* 15 */     return this._Name;
/*    */   }
/*    */ 
/*    */   public byte[] getData()
/*    */   {
/* 20 */     return this._Data;
/*    */   }
/*    */ 
/*    */   public long getDataLength()
/*    */   {
/* 25 */     return this._DataLength;
/*    */   }
/*    */ 
/*    */   public UUID getClsid()
/*    */   {
/* 30 */     if (this._Clsid != null) {
/* 31 */       return this._Clsid;
/*    */     }
/* 33 */     return null;
/*    */   }
/*    */ 
/*    */   public byte getUCDefault()
/*    */   {
/* 38 */     return this._ucDefault;
/*    */   }
/*    */ 
/*    */   public CodecsExtension clone()
/*    */   {
/* 46 */     CodecsExtension varCopy = new CodecsExtension();
/*    */ 
/* 48 */     varCopy._Name = new String(this._Name);
/* 49 */     varCopy._DataLength = this._DataLength;
/* 50 */     varCopy._Clsid = this._Clsid;
/* 51 */     varCopy._ucDefault = this._ucDefault;
/* 52 */     if (this._Data != null) {
/* 53 */       varCopy._Data = ((byte[])this._Data.clone());
/*    */     }
/* 55 */     return varCopy;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsExtension
 * JD-Core Version:    0.6.2
 */