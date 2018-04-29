/*    */ package uk.co.mmscomputing.device.twain;
/*    */ 
/*    */ import java.awt.image.RenderedImage;
/*    */ import java.io.File;
/*    */ import java.io.PrintStream;
/*    */ import javax.imageio.ImageIO;
/*    */ import uk.co.mmscomputing.device.scanner.Scanner;
/*    */ import uk.co.mmscomputing.device.scanner.ScannerIOException;
/*    */ import uk.co.mmscomputing.device.scanner.ScannerIOMetadata;
/*    */ import uk.co.mmscomputing.device.scanner.ScannerIOMetadata.Type;
/*    */ import uk.co.mmscomputing.device.scanner.ScannerListener;
/*    */ 
/*    */ public class TwainExample
/*    */   implements ScannerListener
/*    */ {
/*    */   static TwainExample app;
/*    */   Scanner scanner;
/*    */ 
/*    */   public TwainExample(String[] paramArrayOfString)
/*    */     throws ScannerIOException
/*    */   {
/* 20 */     this.scanner = Scanner.getDevice();
/* 21 */     this.scanner.addListener(this);
/* 22 */     this.scanner.acquire();
/*    */   }
/*    */ 
/*    */   public void update(ScannerIOMetadata.Type paramType, ScannerIOMetadata paramScannerIOMetadata)
/*    */   {
/*    */     Object localObject;
/* 26 */     if (paramType.equals(ScannerIOMetadata.ACQUIRED)) {
/* 27 */       localObject = paramScannerIOMetadata.getImage();
/* 28 */       System.out.println("Have an image now!");
/*    */       try {
/* 30 */         ImageIO.write((RenderedImage)localObject, "png", new File("mmsc_image.png"));
/*    */       } catch (Exception localException) {
/* 32 */         localException.printStackTrace();
/*    */       }
/* 34 */     } else if (paramType.equals(ScannerIOMetadata.NEGOTIATE)) {
/* 35 */       localObject = paramScannerIOMetadata.getDevice();
/*    */     }
/* 43 */     else if (paramType.equals(ScannerIOMetadata.STATECHANGE)) {
/* 44 */       System.err.println(paramScannerIOMetadata.getStateStr());
/* 45 */       if (paramScannerIOMetadata.isFinished())
/* 46 */         System.exit(0);
/*    */     }
/* 48 */     else if (paramType.equals(ScannerIOMetadata.EXCEPTION)) {
/* 49 */       paramScannerIOMetadata.getException().printStackTrace();
/*    */     }
/*    */   }
/*    */ 
/*    */   public static void main(String[] paramArrayOfString) {
/*    */     try {
/* 55 */       app = new TwainExample(paramArrayOfString);
/*    */     } catch (Exception localException) {
/* 57 */       localException.printStackTrace();
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.device.twain.TwainExample
 * JD-Core Version:    0.6.2
 */