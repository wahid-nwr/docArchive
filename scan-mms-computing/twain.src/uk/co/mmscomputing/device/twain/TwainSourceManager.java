/*    */ package uk.co.mmscomputing.device.twain;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import java.util.Vector;
/*    */ import uk.co.mmscomputing.device.scanner.ScannerIOException;
/*    */ 
/*    */ public class TwainSourceManager
/*    */   implements TwainConstants
/*    */ {
/*    */   private TwainSource source;
/*    */ 
/*    */   TwainSourceManager(long paramLong)
/*    */   {
/* 10 */     this.source = new TwainSource(this, paramLong, false);
/* 11 */     this.source.getDefault();
/*    */   }
/*    */ 
/*    */   int getConditionCode() throws TwainIOException {
/* 15 */     byte[] arrayOfByte = new byte[4];
/* 16 */     int i = jtwain.callSourceManager(1, 8, 1, arrayOfByte);
/* 17 */     if (i != 0) {
/* 18 */       throw new TwainResultException("Cannot retrieve twain source manager's status.", i);
/*    */     }
/* 20 */     return jtwain.getINT16(arrayOfByte, 0);
/*    */   }
/*    */ 
/*    */   public void call(int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte) throws TwainIOException {
/* 24 */     int i = jtwain.callSourceManager(paramInt1, paramInt2, paramInt3, paramArrayOfByte);
/* 25 */     switch (i) { case 0:
/* 26 */       return;
/*    */     case 1:
/* 27 */       throw new TwainFailureException(getConditionCode());
/*    */     case 2:
/* 28 */       throw new TwainResultException.CheckStatus();
/*    */     case 3:
/* 29 */       throw new TwainResultException.Cancel();
/*    */     case 4:
/* 30 */       return;
/*    */     case 5:
/* 31 */       throw new TwainResultException.NotDSEvent();
/*    */     case 6:
/* 32 */       throw new TwainResultException.TransferDone();
/*    */     case 7:
/* 33 */       throw new TwainResultException.EndOfList();
/*    */     }
/*    */ 
/* 36 */     throw new TwainResultException("Failed to call source manager.", i);
/*    */   }
/*    */ 
/*    */   TwainSource getSource() {
/* 40 */     return this.source;
/*    */   }
/*    */   TwainSource selectSource() throws TwainIOException {
/* 43 */     this.source.checkState(3);
/* 44 */     this.source.setBusy(true);
/*    */     try {
/* 46 */       this.source.userSelect();
/* 47 */       return this.source;
/*    */     } catch (TwainResultException.Cancel localCancel) {
/* 49 */       return this.source;
/*    */     }
/*    */     finally
/*    */     {
/* 54 */       this.source.setBusy(false);
/*    */     }
/*    */   }
/*    */ 
/*    */   void getIdentities(Vector paramVector) throws ScannerIOException {
/* 59 */     this.source.checkState(3);
/* 60 */     this.source.setBusy(true);
/*    */     try {
/* 62 */       TwainIdentity localTwainIdentity = new TwainIdentity(this);
/* 63 */       localTwainIdentity.getFirst();
/* 64 */       paramVector.add(localTwainIdentity);
/*    */       while (true) {
/* 66 */         localTwainIdentity = new TwainIdentity(this);
/* 67 */         localTwainIdentity.getNext();
/* 68 */         paramVector.add(localTwainIdentity);
/*    */       }
/*    */     } catch (TwainResultException.EndOfList localEndOfList) {
/*    */     } catch (TwainIOException localTwainIOException) {
/* 72 */       System.out.println(getClass().getName() + ".getIdentities:\n\t" + localTwainIOException);
/*    */     } finally {
/* 74 */       this.source.setBusy(false);
/*    */     }
/*    */   }
/*    */ 
/*    */   TwainSource selectSource(String paramString) throws ScannerIOException {
/* 79 */     this.source.checkState(3);
/* 80 */     this.source.setBusy(true);
/*    */     try {
/* 82 */       this.source.select(paramString);
/* 83 */       return this.source;
/*    */     } finally {
/* 85 */       this.source.setBusy(false);
/*    */     }
/*    */   }
/*    */ 
/*    */   TwainSource openSource() throws TwainIOException {
/* 90 */     this.source.checkState(3);
/* 91 */     this.source.setBusy(true);
/*    */     try {
/* 93 */       this.source.open();
/* 94 */       if (!this.source.isDeviceOnline()) {
/* 95 */         this.source.close();
/* 96 */         throw new TwainIOException("Selected twain source is not online.");
/*    */       }
/* 98 */       this.source.setState(4);
/* 99 */       return this.source;
/*    */     } catch (TwainResultException.Cancel localCancel) {
/* 101 */       this.source.setBusy(false);
/* 102 */       return this.source;
/*    */     } catch (TwainIOException localTwainIOException) {
/* 104 */       this.source.setBusy(false);
/* 105 */       throw localTwainIOException;
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.device.twain.TwainSourceManager
 * JD-Core Version:    0.6.2
 */