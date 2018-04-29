/*    */ package leadtools.codecs;
/*    */ 
/*    */ import leadtools.internal.IsInternal;
/*    */ 
/*    */ @IsInternal
/*    */ class THREADHEADER
/*    */ {
/*  9 */   public Object leadThread_hThread = new Object();
/* 10 */   public Object leadThread_hSetThreadEvent = new Object();
/*    */   public int nRetCode;
/*    */   public int pThreadData;
/* 14 */   public Object hEventYield = new Object();
/* 15 */   public Object hEventThis = new Object();
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.THREADHEADER
 * JD-Core Version:    0.6.2
 */