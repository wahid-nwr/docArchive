/*    */ package leadtools.codecs;
/*    */ 
/*    */ import leadtools.InvalidOperationException;
/*    */ import leadtools.LeadEvent;
/*    */ import leadtools.RasterImage;
/*    */ 
/*    */ public class CodecsOverlayData extends LeadEvent
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private String _fileName;
/*    */   private int _pageNumber;
/*    */   private boolean _info;
/*    */   private int _infoWidth;
/*    */   private int _infoHeight;
/*    */   private int _infoXResolution;
/*    */   private int _infoYResolution;
/*    */   private RasterImage _image;
/*    */ 
/*    */   public CodecsOverlayData(Object source, String fileName, int pageNumber, boolean info)
/*    */   {
/* 21 */     super(source);
/* 22 */     this._fileName = fileName;
/* 23 */     this._pageNumber = pageNumber;
/* 24 */     this._info = info;
/*    */   }
/*    */ 
/*    */   public CodecsOverlayData(Object source) {
/* 28 */     super(source);
/*    */   }
/*    */ 
/*    */   public String getFileName()
/*    */   {
/* 33 */     return this._fileName;
/*    */   }
/*    */ 
/*    */   public int getPageNumber()
/*    */   {
/* 38 */     return this._pageNumber;
/*    */   }
/*    */ 
/*    */   public boolean getInfo()
/*    */   {
/* 43 */     return this._info;
/*    */   }
/*    */ 
/*    */   public int getInfoWidth()
/*    */   {
/* 48 */     return this._infoWidth;
/*    */   }
/*    */ 
/*    */   public void setInfoWidth(int value) {
/* 52 */     this._infoWidth = value;
/*    */   }
/*    */ 
/*    */   public int getInfoHeight()
/*    */   {
/* 57 */     return this._infoHeight;
/*    */   }
/*    */ 
/*    */   public void setInfoHeight(int value) {
/* 61 */     this._infoHeight = value;
/*    */   }
/*    */ 
/*    */   public int getInfoXResolution()
/*    */   {
/* 66 */     return this._infoXResolution;
/*    */   }
/*    */ 
/*    */   public void setInfoXResolution(int value) {
/* 70 */     this._infoXResolution = value;
/*    */   }
/*    */ 
/*    */   public int getInfoYResolution()
/*    */   {
/* 75 */     return this._infoYResolution;
/*    */   }
/*    */ 
/*    */   public void setInfoYResolution(int value) {
/* 79 */     this._infoYResolution = value;
/*    */   }
/*    */ 
/*    */   public RasterImage getImage()
/*    */   {
/* 84 */     return this._image;
/*    */   }
/*    */ 
/*    */   public void setImage(RasterImage value) {
/* 88 */     if ((value != null) && (getInfo())) {
/* 89 */       throw new InvalidOperationException("Cannot set the image on info mode");
/*    */     }
/* 91 */     this._image = value;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsOverlayData
 * JD-Core Version:    0.6.2
 */