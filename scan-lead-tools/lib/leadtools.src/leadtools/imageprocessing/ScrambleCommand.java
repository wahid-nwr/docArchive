/*    */ package leadtools.imageprocessing;
/*    */ 
/*    */ import leadtools.LeadRect;
/*    */ import leadtools.RasterImage;
/*    */ import leadtools.RasterImageChangedFlags;
/*    */ import leadtools.ltkrn;
/*    */ 
/*    */ public class ScrambleCommand extends RasterCommand
/*    */ {
/*    */   private LeadRect _rc;
/*    */   private int _key;
/*    */   private int _flags;
/*    */ 
/*    */   public String toString()
/*    */   {
/* 17 */     return "Scramble";
/*    */   }
/*    */ 
/*    */   public ScrambleCommand()
/*    */   {
/* 22 */     this._rc = LeadRect.getEmpty();
/* 23 */     this._key = 0;
/* 24 */     this._flags = ScrambleCommandFlags.NONE.getValue();
/*    */   }
/*    */ 
/*    */   public ScrambleCommand(LeadRect rc, int key, int flags)
/*    */   {
/* 29 */     this._rc = rc;
/* 30 */     this._key = key;
/* 31 */     this._flags = flags;
/*    */   }
/*    */ 
/*    */   public LeadRect getRectangle()
/*    */   {
/* 36 */     return this._rc;
/*    */   }
/*    */ 
/*    */   public void setRectangle(LeadRect value)
/*    */   {
/* 41 */     this._rc = value;
/*    */   }
/*    */ 
/*    */   public int getKey()
/*    */   {
/* 46 */     return this._key;
/*    */   }
/*    */ 
/*    */   public void setKey(int value)
/*    */   {
/* 51 */     this._key = value;
/*    */   }
/*    */ 
/*    */   public int getFlags()
/*    */   {
/* 56 */     return this._flags;
/*    */   }
/*    */ 
/*    */   public void setFlags(int value)
/*    */   {
/* 61 */     this._flags = value;
/*    */   }
/*    */ 
/*    */   protected int runCommand(RasterImage image, long bitmap, int[] changedFlags)
/*    */   {
/*    */     try
/*    */     {
/* 69 */       LeadRect rc = this._rc;
/* 70 */       if (rc == null) {
/* 71 */         rc = new LeadRect(0, 0, image.getWidth(), image.getHeight());
/*    */       }
/* 73 */       return ltkrn.ScrambleBitmap(bitmap, rc.getLeft(), rc.getTop(), rc.getWidth(), rc.getHeight(), this._key, this._flags);
/*    */     }
/*    */     finally
/*    */     {
/* 77 */       changedFlags[0] |= RasterImageChangedFlags.DATA;
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.imageprocessing.ScrambleCommand
 * JD-Core Version:    0.6.2
 */