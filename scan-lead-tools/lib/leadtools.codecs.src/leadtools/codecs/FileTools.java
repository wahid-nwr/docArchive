/*    */ package leadtools.codecs;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.FileInputStream;
/*    */ import java.io.FileNotFoundException;
/*    */ import java.io.FileOutputStream;
/*    */ import java.io.IOException;
/*    */ import leadtools.ILeadStream;
/*    */ import leadtools.L_ERROR;
/*    */ import leadtools.LeadSeekOrigin;
/*    */ import leadtools.internal.IsInternal;
/*    */ 
/*    */ @IsInternal
/*    */ final class FileTools
/*    */ {
/*    */   public static int writeStreamToFile(ILeadStream stream, String fileName)
/*    */   {
/* 19 */     long pos = 0L;
/*    */     FileOutputStream fs;
/*    */     try
/*    */     {
/* 23 */       fs = new FileOutputStream(fileName);
/*    */     } catch (FileNotFoundException e) {
/* 25 */       return L_ERROR.ERROR_FILE_CREATE.getValue();
/*    */     }
/*    */     try
/*    */     {
/* 29 */       byte[] buffer = new byte[32768];
/*    */       do
/*    */       {
/* 34 */         bytes = stream.read(buffer, buffer.length);
/* 35 */         if (bytes > 0)
/*    */         {
/* 37 */           fs.write(buffer, 0, bytes);
/*    */         }
/*    */       }
/* 40 */       while (bytes > 0);
/* 41 */       return L_ERROR.SUCCESS.getValue();
/*    */     }
/*    */     catch (IOException e)
/*    */     {
/*    */       int bytes;
/* 43 */       return L_ERROR.ERROR_FILE_WRITE.getValue();
/*    */     }
/*    */     finally
/*    */     {
/*    */       try {
/* 48 */         fs.close();
/*    */       } catch (IOException e) {
/*    */       }
/* 51 */       stream.seek(LeadSeekOrigin.BEGIN, pos);
/*    */     }
/*    */   }
/*    */ 
/*    */   public static int readFileToStream(File srcFile, ILeadStream stream)
/*    */   {
/* 57 */     long pos = 0L;
/*    */     FileInputStream fs;
/*    */     try {
/* 61 */       fs = new FileInputStream(srcFile);
/*    */     } catch (FileNotFoundException e) {
/* 63 */       return L_ERROR.ERROR_FILE_OPEN.getValue();
/*    */     }
/*    */     try
/*    */     {
/* 67 */       byte[] buffer = new byte[32768];
/*    */       do
/*    */       {
/* 72 */         bytes = fs.read(buffer, 0, buffer.length);
/* 73 */         if (bytes > 0)
/*    */         {
/* 75 */           stream.write(buffer, bytes);
/*    */         }
/*    */       }
/* 78 */       while (bytes > 0);
/* 79 */       return L_ERROR.SUCCESS.getValue();
/*    */     }
/*    */     catch (IOException e)
/*    */     {
/*    */       int bytes;
/* 81 */       return L_ERROR.ERROR_FILE_READ.getValue();
/*    */     }
/*    */     finally
/*    */     {
/*    */       try {
/* 86 */         fs.close();
/*    */       } catch (IOException e) {
/*    */       }
/* 89 */       stream.seek(LeadSeekOrigin.BEGIN, pos);
/*    */     }
/*    */   }
/*    */ 
/*    */   public static void safeDeleteFile(String fileName) {
/* 94 */     if (new File(fileName).isFile())
/*    */     {
/*    */       try
/*    */       {
/* 98 */         new File(fileName).delete();
/*    */       }
/*    */       catch (RuntimeException e)
/*    */       {
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.FileTools
 * JD-Core Version:    0.6.2
 */