/*    */ package uk.co.mmscomputing.device.twain;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import uk.co.mmscomputing.device.scanner.Scanner;
/*    */ import uk.co.mmscomputing.device.scanner.ScannerIOException;
/*    */ import uk.co.mmscomputing.device.scanner.ScannerIOMetadata;
/*    */ import uk.co.mmscomputing.device.scanner.ScannerIOMetadata.Type;
/*    */ import uk.co.mmscomputing.device.scanner.ScannerListener;
/*    */ 
/*    */ public class ADFTwainExample
/*    */   implements ScannerListener
/*    */ {
/*    */   static ADFTwainExample app;
/*    */   Scanner scanner;
/* 21 */   int transferCount = 0;
/*    */ 
/*    */   public ADFTwainExample(String[] paramArrayOfString) throws ScannerIOException {
/* 24 */     this.scanner = Scanner.getDevice();
/* 25 */     this.scanner.addListener(this);
/* 26 */     this.scanner.acquire();
/*    */   }
/*    */ 
/*    */   public void update(ScannerIOMetadata.Type paramType, ScannerIOMetadata paramScannerIOMetadata)
/*    */   {
/*    */     Object localObject;
/* 30 */     if (paramType.equals(ScannerIOMetadata.ACQUIRED)) {
/* 31 */       localObject = paramScannerIOMetadata.getImage();
/* 32 */       this.transferCount += 1;
/* 33 */       System.out.println("Got image number: " + this.transferCount);
/*    */     }
/* 41 */     else if (paramType.equals(ScannerIOMetadata.NEGOTIATE)) {
/* 42 */       localObject = ((TwainIOMetadata)paramScannerIOMetadata).getSource();
/* 43 */       String str = ((TwainSource)localObject).getProductName();
/*    */ 
/* 45 */       ((TwainSource)localObject).setShowUI(false);
/*    */       try
/*    */       {
/* 48 */         ((TwainSource)localObject).getCapability(4385, 2).setCurrentValue(90.0D);
/*    */ 
/* 50 */         ((TwainSource)localObject).getCapability(4098).setCurrentValue(true);
/* 51 */         ((TwainSource)localObject).getCapability(4103).setCurrentValue(true);
/* 52 */         ((TwainSource)localObject).getCapability(1).setCurrentValue(this.transferCount);
/* 53 */         this.transferCount = ((TwainSource)localObject).getCapability(1).intValue();
/* 54 */         System.out.println("set transferCount: " + this.transferCount);
/*    */       } catch (Exception localException2) {
/* 56 */         System.out.println("CAP_FEEDERENABLED/CAP_AUTOFEED/CAP_XFERCOUNT: " + localException2.getMessage());
/*    */       }
/*    */ 
/*    */     }
/* 60 */     else if (paramType.equals(ScannerIOMetadata.STATECHANGE))
/*    */     {
/* 62 */       System.err.println(paramScannerIOMetadata.getStateStr() + " [" + paramScannerIOMetadata.getState() + "]");
/*    */ 
/* 64 */       if ((paramScannerIOMetadata.getLastState() == 4) && (paramScannerIOMetadata.getState() == 3)) {
/* 65 */         if (this.transferCount < 5)
/*    */           try {
/* 67 */             this.scanner.acquire();
/*    */           } catch (Exception localException1) {
/* 69 */             System.err.println(localException1);
/*    */           }
/*    */         else
/* 72 */           System.exit(0);
/*    */       }
/*    */     }
/* 75 */     else if (paramType.equals(ScannerIOMetadata.EXCEPTION)) {
/* 76 */       paramScannerIOMetadata.getException().printStackTrace();
/*    */     }
/*    */   }
/*    */ 
/*    */   public static void main(String[] paramArrayOfString) {
/*    */     try {
/* 82 */       app = new ADFTwainExample(paramArrayOfString);
/*    */     } catch (Exception localException) {
/* 84 */       localException.printStackTrace();
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.device.twain.ADFTwainExample
 * JD-Core Version:    0.6.2
 */