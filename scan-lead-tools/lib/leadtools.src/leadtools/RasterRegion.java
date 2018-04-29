/*     */ package leadtools;
/*     */ 
/*     */ public class RasterRegion
/*     */ {
/*     */   long _leadRegion;
/*     */ 
/*     */   public RasterRegion()
/*     */   {
/*   8 */     this._leadRegion = 0L;
/*     */   }
/*     */ 
/*     */   public RasterRegion(LeadRect rect)
/*     */   {
/*  13 */     this._leadRegion = ltkrn.CreateLeadRectRgn(rect);
/*     */   }
/*     */ 
/*     */   public RasterRegion(long leadRegion, boolean makeCopy)
/*     */   {
/*  18 */     long tempLeadRegion = 0L;
/*     */ 
/*  20 */     if ((makeCopy) && (leadRegion != 0L))
/*     */     {
/*  22 */       tempLeadRegion = ltkrn.CopyLeadRgn(leadRegion);
/*     */     }
/*     */     else
/*     */     {
/*  26 */       tempLeadRegion = leadRegion;
/*     */     }
/*     */ 
/*  29 */     this._leadRegion = tempLeadRegion;
/*     */   }
/*     */ 
/*     */   public synchronized void dispose() {
/*  33 */     finalize();
/*     */   }
/*     */ 
/*     */   protected void finalize() {
/*  37 */     if (this._leadRegion != 0L) {
/*  38 */       ltkrn.DeleteLeadRgn(this._leadRegion);
/*  39 */       this._leadRegion = 0L;
/*     */     }
/*     */     try {
/*  42 */       super.finalize();
/*     */     }
/*     */     catch (Throwable e) {
/*  45 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public RasterRegion clone()
/*     */   {
/*  51 */     return new RasterRegion(this._leadRegion, true);
/*     */   }
/*     */ 
/*     */   long cloneLeadRegion()
/*     */   {
/*  56 */     if (this._leadRegion == 0L)
/*  57 */       return 0L;
/*  58 */     long tempLeadRegion = ltkrn.CopyLeadRgn(this._leadRegion);
/*  59 */     if (tempLeadRegion == 0L)
/*  60 */       throw new RasterException(RasterExceptionCode.NO_MEMORY);
/*  61 */     return tempLeadRegion;
/*     */   }
/*     */ 
/*     */   public LeadRect getBounds()
/*     */   {
/*  66 */     LeadRect rect = new LeadRect(0, 0, 0, 0);
/*  67 */     int ret = ltkrn.GetLeadRgnBounds(this._leadRegion, rect);
/*  68 */     RasterException.checkErrorCode(ret);
/*  69 */     return rect;
/*     */   }
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/*  74 */     return ltkrn.IsLeadRgnEmpty(this._leadRegion);
/*     */   }
/*     */ 
/*     */   public void makeEmpty()
/*     */   {
/*  80 */     if (this._leadRegion != 0L)
/*     */     {
/*  82 */       ltkrn.DeleteLeadRgn(this._leadRegion);
/*  83 */       this._leadRegion = 0L;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void combine(LeadRect rect, RasterRegionCombineMode combineMode)
/*     */   {
/*  89 */     switch (1.$SwitchMap$leadtools$RasterRegionCombineMode[combineMode.ordinal()])
/*     */     {
/*     */     case 1:
/*     */     case 2:
/*     */     case 3:
/*     */     case 4:
/*     */     case 5:
/*  97 */       break;
/*     */     case 6:
/*     */     case 7:
/*     */     default:
/* 102 */       throw new IllegalArgumentException(combineMode + " combine mode is not supported by this method");
/*     */     }
/*     */ 
/* 105 */     long[] pRegions = { this._leadRegion };
/* 106 */     int ret = ltkrn.CombineLeadRgnRect(pRegions, rect, combineMode.getValue());
/* 107 */     RasterException.checkErrorCode(ret);
/* 108 */     this._leadRegion = pRegions[0];
/*     */   }
/*     */ 
/*     */   public void combine(RasterRegion region, RasterRegionCombineMode combineMode)
/*     */   {
/* 134 */     switch (1.$SwitchMap$leadtools$RasterRegionCombineMode[combineMode.ordinal()])
/*     */     {
/*     */     case 1:
/*     */     case 2:
/*     */     case 3:
/*     */     case 4:
/*     */     case 5:
/* 142 */       break;
/*     */     case 6:
/*     */     case 7:
/*     */     default:
/* 147 */       throw new IllegalArgumentException(combineMode + " combine mode is not supported by this method");
/*     */     }
/*     */ 
/* 150 */     if (region == null) throw new RuntimeException("Error: null argument (region)");
/*     */ 
/* 152 */     long[] pRegions = { this._leadRegion, region._leadRegion };
/* 153 */     int ret = ltkrn.CombineLeadRgn(pRegions, combineMode.getValue());
/* 154 */     RasterException.checkErrorCode(ret);
/* 155 */     this._leadRegion = pRegions[0];
/*     */   }
/*     */ 
/*     */   public boolean isVisible(LeadPoint point)
/*     */   {
/* 160 */     return ltkrn.IsPtInRgn(this._leadRegion, point.getX(), point.getY());
/*     */   }
/*     */ 
/*     */   public void clip(LeadRect rect)
/*     */   {
/* 174 */     if (this._leadRegion == 0L) {
/* 175 */       return;
/*     */     }
/* 177 */     int ret = ltkrn.ClipLeadRgnRect(this._leadRegion, rect);
/* 178 */     RasterException.checkErrorCode(ret);
/*     */   }
/*     */ 
/*     */   public void transform(RasterRegionXForm xform)
/*     */   {
/* 183 */     if (xform == null) throw new RuntimeException("Error: argument null (xform)");
/*     */ 
/* 185 */     if (this._leadRegion == 0L) return;
/*     */ 
/* 187 */     if ((xform.getXScalarDenominator() == 0) || (xform.getYScalarDenominator() == 0))
/*     */     {
/* 189 */       throw new RuntimeException("RasterRegionXForm.XScalarDenominator and RasterRegionXForm.YScalarDenominator cannot be zero");
/*     */     }
/*     */ 
/* 192 */     int ret = ltkrn.TransformLeadRgn(this._leadRegion, xform);
/* 193 */     RasterException.checkErrorCode(ret);
/*     */   }
/*     */ 
/*     */   public RasterRegion(byte[] data)
/*     */   {
/* 198 */     this._leadRegion = 0L;
/* 199 */     setData(data);
/*     */   }
/*     */ 
/*     */   public byte[] getData()
/*     */   {
/* 204 */     return ltkrn.GetLeadRgnData(this._leadRegion);
/*     */   }
/*     */ 
/*     */   public void setData(byte[] data)
/*     */   {
/* 209 */     long[] pRegions = { this._leadRegion };
/* 210 */     int ret = ltkrn.SetLeadRgnData(pRegions, data);
/* 211 */     RasterException.checkErrorCode(ret);
/* 212 */     this._leadRegion = pRegions[0];
/*     */   }
/*     */ 
/*     */   public int[] toSegments()
/*     */   {
/* 218 */     int[][] segments = new int[1][];
/* 219 */     int ret = ltkrn.RegionToSegments(this._leadRegion, segments);
/* 220 */     RasterException.checkErrorCode(ret);
/* 221 */     return segments[0];
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.RasterRegion
 * JD-Core Version:    0.6.2
 */