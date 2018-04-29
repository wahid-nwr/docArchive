/*    */ package leadtools.codecs;
/*    */ 
/*    */ import leadtools.internal.IsInternal;
/*    */ 
/*    */ @IsInternal
/*    */ abstract class Tools
/*    */ {
/*    */   public static boolean isFlagged(int flags, int flag)
/*    */   {
/* 14 */     return (flags & flag) == flag;
/*    */   }
/*    */ 
/*    */   public static int setFlag1(int flags, int flag, boolean set)
/*    */   {
/* 19 */     if (set)
/* 20 */       flags |= flag;
/*    */     else {
/* 22 */       flags &= (flag ^ 0xFFFFFFFF);
/*    */     }
/* 24 */     return flags;
/*    */   }
/*    */ 
/*    */   public static boolean isNullOrEmpty(String path)
/*    */   {
/* 78 */     if (path == null)
/* 79 */       return true;
/* 80 */     if (path.equals(""))
/* 81 */       return true;
/* 82 */     return false;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.Tools
 * JD-Core Version:    0.6.2
 */