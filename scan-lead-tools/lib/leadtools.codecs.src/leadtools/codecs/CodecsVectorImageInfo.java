/*    */ package leadtools.codecs;
/*    */ 
/*    */ public class CodecsVectorImageInfo
/*    */ {
/*    */   private FILEINFO _fileInfo;
/*    */ 
/*    */   CodecsVectorImageInfo(FILEINFO fileInfo)
/*    */   {
/*  8 */     this._fileInfo = fileInfo;
/*    */   }
/*    */ 
/*    */   public boolean isVectorFile() {
/* 12 */     return this._fileInfo.bIsVectorFile;
/*    */   }
/*    */ 
/*    */   public double getParallelogramMinX() {
/* 16 */     return this._fileInfo.VectorParallelogram_Min_x;
/*    */   }
/*    */ 
/*    */   public double getParallelogramMinY() {
/* 20 */     return this._fileInfo.VectorParallelogram_Min_y;
/*    */   }
/*    */ 
/*    */   public double getParallelogramMinZ() {
/* 24 */     return this._fileInfo.VectorParallelogram_Min_z;
/*    */   }
/*    */ 
/*    */   public double getParallelogramMaxX() {
/* 28 */     return this._fileInfo.VectorParallelogram_Max_x;
/*    */   }
/*    */ 
/*    */   public double getParallelogramMaxY() {
/* 32 */     return this._fileInfo.VectorParallelogram_Max_y;
/*    */   }
/*    */ 
/*    */   public double getParallelogramMaxZ() {
/* 36 */     return this._fileInfo.VectorParallelogram_Max_z;
/*    */   }
/*    */ 
/*    */   public CodecsVectorUnit getUnit() {
/* 40 */     return CodecsVectorUnit.forValue(this._fileInfo.VectorParallelogram_Unit);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsVectorImageInfo
 * JD-Core Version:    0.6.2
 */