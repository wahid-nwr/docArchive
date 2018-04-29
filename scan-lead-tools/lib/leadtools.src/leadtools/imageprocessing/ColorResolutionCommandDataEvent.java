/*    */ package leadtools.imageprocessing;
/*    */ 
/*    */ import leadtools.LeadEvent;
/*    */ import leadtools.RasterImage;
/*    */ 
/*    */ public class ColorResolutionCommandDataEvent extends LeadEvent
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private RasterImage _image;
/*    */   private int _lines;
/*    */   private byte[] _data;
/*    */   private boolean _cancel;
/*    */ 
/*    */   public ColorResolutionCommandDataEvent(Object source, RasterImage image, int lines, byte[] data)
/*    */   {
/* 17 */     super(source);
/* 18 */     this._image = image;
/* 19 */     this._lines = lines;
/* 20 */     this._data = data;
/* 21 */     this._cancel = false;
/*    */   }
/*    */ 
/*    */   public RasterImage getImage()
/*    */   {
/* 27 */     return this._image;
/*    */   }
/*    */ 
/*    */   public int getLines()
/*    */   {
/* 32 */     return this._lines;
/*    */   }
/*    */ 
/*    */   public byte[] getData()
/*    */   {
/* 37 */     return this._data;
/*    */   }
/*    */ 
/*    */   public byte[] getDataArray()
/*    */   {
/* 53 */     return this._data;
/*    */   }
/*    */ 
/*    */   public boolean getCancel()
/*    */   {
/* 58 */     return this._cancel;
/*    */   }
/*    */ 
/*    */   public void setCancel(boolean value) {
/* 62 */     this._cancel = value;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.imageprocessing.ColorResolutionCommandDataEvent
 * JD-Core Version:    0.6.2
 */