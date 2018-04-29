/*    */ package leadtools;
/*    */ 
/*    */ public final class RasterMetadataRational
/*    */ {
/*    */   private int _numerator;
/*    */   private int _denominator;
/*    */ 
/*    */   public RasterMetadataRational getEmpty()
/*    */   {
/* 11 */     return new RasterMetadataRational(0, 0);
/*    */   }
/*    */ 
/*    */   public RasterMetadataRational(int numerator, int denominator)
/*    */   {
/* 16 */     init(numerator, denominator);
/*    */   }
/*    */ 
/*    */   public void init(int numerator, int denominator)
/*    */   {
/* 21 */     this._numerator = numerator;
/* 22 */     this._denominator = denominator;
/*    */   }
/*    */ 
/*    */   public int getNumerator()
/*    */   {
/* 27 */     return this._numerator;
/*    */   }
/*    */ 
/*    */   public void setNumerator(int value) {
/* 31 */     this._numerator = value;
/*    */   }
/*    */ 
/*    */   public int getDenominator()
/*    */   {
/* 36 */     return this._denominator;
/*    */   }
/*    */ 
/*    */   public void setDenominator(int value) {
/* 40 */     this._denominator = value;
/*    */   }
/*    */ 
/*    */   public RasterMetadataRational clone()
/*    */   {
/* 45 */     return new RasterMetadataRational(this._numerator, this._denominator);
/*    */   }
/*    */ 
/*    */   public void fillIntArray(int[] array)
/*    */   {
/* 50 */     array[0] = this._numerator;
/* 51 */     array[1] = this._denominator;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.RasterMetadataRational
 * JD-Core Version:    0.6.2
 */