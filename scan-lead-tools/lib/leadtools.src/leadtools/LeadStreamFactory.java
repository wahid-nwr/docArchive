/*    */ package leadtools;
/*    */ 
/*    */ import java.io.InputStream;
/*    */ import java.io.OutputStream;
/*    */ import java.io.RandomAccessFile;
/*    */ import java.net.URI;
/*    */ 
/*    */ public final class LeadStreamFactory
/*    */ {
/*    */   public static ILeadStream create(String fileName)
/*    */   {
/* 13 */     return new LeadFileStream(fileName);
/*    */   }
/*    */ 
/*    */   public static ILeadStream create(URI uri) {
/* 17 */     return new LeadURIStream(uri);
/*    */   }
/*    */ 
/*    */   public static ILeadStream create(InputStream stream, boolean freeStream) {
/* 21 */     return new LeadStream(stream, freeStream);
/*    */   }
/*    */ 
/*    */   public static ILeadStream create(OutputStream stream, boolean freeStream) {
/* 25 */     return new LeadStream(stream, freeStream);
/*    */   }
/*    */ 
/*    */   public static ILeadStream create(byte[] buffer) {
/* 29 */     return new LeadBufferStream(buffer);
/*    */   }
/*    */ 
/*    */   public static ILeadStream create(RandomAccessFile file, boolean freeStream) {
/* 33 */     return new LeadStream(file, freeStream);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.LeadStreamFactory
 * JD-Core Version:    0.6.2
 */