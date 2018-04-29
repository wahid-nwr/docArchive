/*     */ package leadtools.codecs;
/*     */ 
/*     */ import java.util.Map;
/*     */ import leadtools.L_ERROR;
/*     */ import leadtools.LeadSize;
/*     */ import leadtools.RasterException;
/*     */ import leadtools.RasterExceptionCode;
/*     */ import leadtools.RasterImageFormat;
/*     */ 
/*     */ public class CodecsOptions
/*     */ {
/*  12 */   private THREADDATA _threadData = new THREADDATA();
/*  13 */   private LOADFILEOPTION _loadFileOption = new LOADFILEOPTION();
/*  14 */   private SAVEFILEOPTION _saveFileOption = new SAVEFILEOPTION();
/*  15 */   private int _loadFlags = 0;
/*  16 */   private int _saveFlags = 0;
/*     */   private CodecsLoadOptions _load;
/*     */   private CodecsSaveOptions _save;
/*  21 */   private CodecsAbcOptions abc = new CodecsAbcOptions(this);
/*  22 */   private CodecsAnzOptions anz = new CodecsAnzOptions(this);
/*  23 */   private CodecsDocOptions doc = new CodecsDocOptions(this);
/*  24 */   private CodecsEcwOptions ecw = new CodecsEcwOptions(this);
/*  25 */   private CodecsEpsOptions eps = new CodecsEpsOptions(this);
/*  26 */   private CodecsFpxOptions fpx = new CodecsFpxOptions(this);
/*  27 */   private CodecsGifOptions gif = new CodecsGifOptions(this);
/*  28 */   private CodecsJpegOptions jpeg = new CodecsJpegOptions(this);
/*  29 */   private CodecsJbigOptions jbig = new CodecsJbigOptions(this);
/*  30 */   private CodecsJbig2Options jbig2 = new CodecsJbig2Options(this);
/*  31 */   private CodecsJpeg2000Options jpeg2000 = new CodecsJpeg2000Options(this);
/*  32 */   private CodecsPcdOptions pcd = new CodecsPcdOptions(this);
/*  33 */   private CodecsPdfOptions pdf = new CodecsPdfOptions(this);
/*  34 */   private CodecsPngOptions png = new CodecsPngOptions(this);
/*  35 */   private CodecsPtokaOptions ptoka = new CodecsPtokaOptions(this);
/*  36 */   private CodecsPstOptions pst = new CodecsPstOptions(this);
/*  37 */   private CodecsRasterizeDocumentOptions rasterizeDocument = new CodecsRasterizeDocumentOptions(this);
/*  38 */   private CodecsRawOptions raw = new CodecsRawOptions(this);
/*  39 */   private CodecsRtfOptions rtf = new CodecsRtfOptions(this);
/*  40 */   private CodecsTiffOptions tiff = new CodecsTiffOptions(this);
/*  41 */   private CodecsTxtOptions txt = new CodecsTxtOptions(this);
/*  42 */   private CodecsVectorOptions vector = new CodecsVectorOptions(this);
/*  43 */   private CodecsVffOptions vff = new CodecsVffOptions(this);
/*  44 */   private CodecsWmfOptions wmf = new CodecsWmfOptions(this);
/*  45 */   private CodecsXlsOptions xls = new CodecsXlsOptions(this);
/*  46 */   private CodecsXpsOptions xps = new CodecsXpsOptions(this);
/*     */ 
/*     */   CodecsOptions() {
/*  49 */     if ((this._threadData == null) || (this._loadFileOption == null) || (this._saveFileOption == null))
/*     */     {
/*  51 */       free();
/*  52 */       throw new RasterException(RasterExceptionCode.NO_MEMORY);
/*     */     }
/*     */ 
/*  55 */     ltfil.InitThreadData(this._threadData);
/*     */ 
/*  57 */     this._threadData.szPDFInitDir = CodecsPdfOptions.getGlobalPdfInitialPath();
/*     */ 
/*  59 */     int ret = resetLoadFileOption(false);
/*  60 */     if (ret != L_ERROR.SUCCESS.getValue()) {
/*  61 */       free();
/*  62 */       RasterException.checkErrorCode(ret);
/*     */     }
/*     */ 
/*  65 */     ret = resetSaveFileOption(false);
/*  66 */     if (ret != L_ERROR.SUCCESS.getValue()) {
/*  67 */       free();
/*  68 */       RasterException.checkErrorCode(ret);
/*     */     }
/*     */ 
/*  71 */     this._load = new CodecsLoadOptions(this);
/*  72 */     this._save = new CodecsSaveOptions(this);
/*     */   }
/*     */ 
/*     */   void dispose() {
/*  76 */     free();
/*     */   }
/*     */ 
/*     */   protected void finalize() throws Throwable {
/*  80 */     free();
/*     */   }
/*     */ 
/*     */   private void free() {
/*  84 */     if (this._threadData != null)
/*     */     {
/*  86 */       this._threadData = null;
/*     */     }
/*     */ 
/*  89 */     if (this._loadFileOption != null) {
/*  90 */       this._loadFileOption = null;
/*     */     }
/*     */ 
/*  93 */     if (this._saveFileOption != null)
/*  94 */       this._saveFileOption = null;
/*     */   }
/*     */ 
/*     */   public CodecsOptions clone()
/*     */   {
/*  99 */     CodecsOptions dest = new CodecsOptions();
/*     */ 
/* 101 */     ltfil.CopyThreadData(dest._threadData, this._threadData);
/* 102 */     ltfil.CopyLoadFileOption(dest._loadFileOption, this._loadFileOption);
/* 103 */     ltfil.CopySaveFileOption(dest._saveFileOption, this._saveFileOption);
/*     */ 
/* 105 */     dest._loadFlags = this._loadFlags;
/* 106 */     dest._saveFlags = this._saveFlags;
/*     */ 
/* 108 */     dest.setAbc(getAbc().copy(dest));
/* 109 */     dest.setAnz(getAnz().copy(dest));
/* 110 */     dest.setDoc(getDoc().copy(dest));
/* 111 */     dest.setEcw(getEcw().copy(dest));
/* 112 */     dest.setEps(getEps().copy(dest));
/* 113 */     dest.setFpx(getFpx().copy(dest));
/* 114 */     dest.setGif(getGif().copy(dest));
/* 115 */     dest.setJpeg(getJpeg().copy(dest));
/* 116 */     dest.setJpeg2000(getJpeg2000().copy(dest));
/* 117 */     dest.setJbig(getJbig().copy(dest));
/* 118 */     dest.setJbig2(getJbig2().copy(dest));
/* 119 */     dest.setPcd(getPcd().copy(dest));
/* 120 */     dest.setPdf(getPdf().copy(dest));
/* 121 */     dest.setPng(getPng().copy(dest));
/* 122 */     dest.setPtoka(getPtoka().copy(dest));
/* 123 */     dest.setPst(getPst().copy(dest));
/* 124 */     dest.setRasterizeDocument(getRasterizeDocument().copy(dest));
/* 125 */     dest.setRaw(getRaw().copy(dest));
/* 126 */     dest.setRtf(getRtf().copy(dest));
/* 127 */     dest.setTiff(getTiff().copy(dest));
/* 128 */     dest.setTxt(getTxt().copy(dest));
/* 129 */     dest.setVector(getVector().copy(dest));
/* 130 */     dest.setVff(getVff().copy(dest));
/* 131 */     dest.setWmf(getWmf().copy(dest));
/* 132 */     dest.setXls(getXls().copy(dest));
/* 133 */     dest.setXps(getXps().copy(dest));
/*     */ 
/* 135 */     dest.getLoad().setMarkers(getLoad().getMarkers());
/* 136 */     dest.getLoad().setTags(getLoad().getTags());
/* 137 */     dest.getLoad().setComments(getLoad().getComments());
/* 138 */     dest.getLoad().setGeoKeys(getLoad().getGeoKeys());
/* 139 */     dest.getLoad().setAllocateImage(getLoad().getAllocateImage());
/* 140 */     dest.getLoad().setStoreDataInImage(getLoad().getStoreDataInImage());
/* 141 */     dest.getLoad().setFormat(getLoad().getFormat());
/* 142 */     dest.getLoad().setAllPages(getLoad().getAllPages());
/*     */ 
/* 144 */     dest.getSave().setMarkers(getSave().getMarkers());
/* 145 */     dest.getSave().setTags(getSave().getTags());
/* 146 */     dest.getSave().setGeoKeys(getSave().getGeoKeys());
/* 147 */     dest.getSave().setComments(getSave().getComments());
/* 148 */     dest.getSave().setRetrieveDataFromImage(getSave().getRetrieveDataFromImage());
/*     */ 
/* 150 */     return dest;
/*     */   }
/*     */ 
/*     */   final THREADDATA getThreadData() {
/* 154 */     return this._threadData;
/*     */   }
/*     */ 
/*     */   final LOADFILEOPTION getLoadFileOption() {
/* 158 */     return this._loadFileOption;
/*     */   }
/*     */ 
/*     */   final int resetLoadFileOption(boolean throwException) {
/* 162 */     int ret = ltfil.GetDefaultLoadFileOption(this._loadFileOption);
/* 163 */     if ((ret != L_ERROR.SUCCESS.getValue()) && (throwException)) {
/* 164 */       RasterException.checkErrorCode(ret);
/*     */     }
/* 166 */     return ret;
/*     */   }
/*     */ 
/*     */   final SAVEFILEOPTION getSaveFileOption() {
/* 170 */     return this._saveFileOption;
/*     */   }
/*     */ 
/*     */   final int resetSaveFileOption(boolean throwException) {
/* 174 */     int ret = ltfil.GetDefaultSaveFileOption(this._saveFileOption);
/* 175 */     if ((ret != L_ERROR.SUCCESS.getValue()) && (throwException))
/* 176 */       RasterException.checkErrorCode(ret);
/* 177 */     this._saveFileOption.Flags2 |= 16;
/*     */ 
/* 179 */     return ret;
/*     */   }
/*     */ 
/*     */   final int getLoadFlags() {
/* 183 */     return this._loadFlags;
/*     */   }
/*     */ 
/*     */   final void setLoadFlags(int value) {
/* 187 */     this._loadFlags = value;
/*     */   }
/*     */ 
/*     */   final int getSaveFlags() {
/* 191 */     return this._saveFlags;
/*     */   }
/*     */ 
/*     */   final void setSaveFlags(int value) {
/* 195 */     this._saveFlags = value;
/*     */   }
/*     */ 
/*     */   final LeadSize getLoadResolution(RasterImageFormat format) {
/* 199 */     int[] params = new int[2];
/* 200 */     int ret = ltfil.GetLoadResolution(format.getValue(), params, this._loadFileOption);
/*     */ 
/* 202 */     if (ret == L_ERROR.ERROR_FEATURE_NOT_SUPPORTED.getValue()) {
/* 203 */       return LeadSize.getEmpty();
/*     */     }
/* 205 */     RasterException.checkErrorCode(ret);
/* 206 */     return new LeadSize(params[0], params[1]);
/*     */   }
/*     */ 
/*     */   final void setLoadResolution(RasterImageFormat format, LeadSize resolution)
/*     */   {
/* 217 */     int ret = ltfil.SetLoadResolution(format.getValue(), resolution.getWidth(), resolution.getHeight());
/*     */ 
/* 219 */     RasterException.checkErrorCode(ret);
/*     */   }
/*     */ 
/*     */   final int getRealQualityFactor(RasterImageFormat format, int bitsPerPixel) {
/* 223 */     switch (1.$SwitchMap$leadtools$RasterImageFormat[format.ordinal()]) {
/*     */     case 1:
/* 225 */       return getPng().getSave().getQualityFactor();
/*     */     case 2:
/*     */     case 3:
/* 229 */       return getAbc().getSave().getQualityFactor().getValue();
/*     */     case 4:
/*     */     case 5:
/* 233 */       if (getJpeg().getSave().getCmpQualityFactorPredefined() != CodecsCmpQualityFactorPredefined.CUSTOM) {
/* 234 */         return getJpeg().getSave().getCmpQualityFactorPredefined().getValue();
/*     */       }
/*     */ 
/* 237 */       return getJpeg().getSave().getQualityFactor();
/*     */     case 6:
/* 239 */       return getEcw().getSave().getQualityFactor();
/*     */     case 7:
/* 242 */       return getXps().getSave().getPngQualityFactor();
/*     */     case 8:
/*     */     case 9:
/*     */     case 10:
/* 247 */       return getXps().getSave().getJpegQualityFactor();
/*     */     case 11:
/*     */     case 12:
/*     */     case 13:
/*     */     case 14:
/*     */     case 15:
/*     */     case 16:
/*     */     case 17:
/*     */     case 18:
/*     */     case 19:
/*     */     case 20:
/*     */     case 21:
/*     */     case 22:
/*     */     case 23:
/*     */     case 24:
/*     */     case 25:
/*     */     case 26:
/*     */     case 27:
/*     */     case 28:
/*     */     case 29:
/*     */     case 30:
/*     */     case 31:
/* 270 */       if (bitsPerPixel == 16)
/* 271 */         return 0;
/* 272 */       return getJpeg().getSave().getQualityFactor();
/*     */     case 32:
/*     */     case 33:
/*     */     case 34:
/*     */     case 35:
/*     */     case 36:
/* 279 */       return getJpeg().getSave().getQualityFactor();
/*     */     case 37:
/*     */     case 38:
/*     */     case 39:
/* 284 */       return getJpeg().getSave().getQualityFactor();
/*     */     case 40:
/*     */     case 41:
/*     */     case 42:
/*     */     case 43:
/*     */     case 44:
/* 291 */       return getJpeg().getSave().getQualityFactor();
/*     */     }
/*     */ 
/* 294 */     return 0;
/*     */   }
/*     */ 
/*     */   final void use()
/*     */   {
/* 299 */     RasterException.checkErrorCode(ltfil.SetThreadData(this._threadData));
/*     */   }
/*     */ 
/*     */   void writeXml(Map<String, String> dic) {
/* 303 */     getLoad().writeXml(dic);
/* 304 */     getSave().writeXml(dic);
/*     */ 
/* 306 */     getAbc().getLoad().writeXml(dic);
/* 307 */     getAbc().getSave().writeXml(dic);
/*     */     try {
/* 309 */       getEcw().getLoad().writeXml(dic);
/* 310 */       getEcw().getSave().writeXml(dic); } catch (Exception ex) {
/*     */     }
/* 312 */     getEps().getLoad().writeXml(dic);
/*     */ 
/* 315 */     getGif().getLoad().writeXml(dic);
/* 316 */     getGif().getSave().writeXml(dic);
/* 317 */     getJbig().getLoad().writeXml(dic);
/* 318 */     getJbig2().getLoad().writeXml(dic);
/* 319 */     getJbig2().getSave().writeXml(dic);
/* 320 */     getJpeg().getLoad().writeXml(dic);
/* 321 */     getJpeg().getSave().writeXml(dic);
/* 322 */     getJpeg2000().getLoad().writeXml(dic);
/* 323 */     getJpeg2000().getSave().writeXml(dic);
/* 324 */     getPcd().getLoad().writeXml(dic);
/* 325 */     getPdf().getLoad().writeXml(dic);
/* 326 */     getPdf().getSave().writeXml(dic);
/* 327 */     getPng().getSave().writeXml(dic);
/* 328 */     getPtoka().getLoad().writeXml(dic);
/* 329 */     getRaw().getSave().writeXml(dic);
/* 330 */     getRtf().getLoad().writeXml(dic);
/* 331 */     getTiff().getLoad().writeXml(dic);
/* 332 */     getTiff().getSave().writeXml(dic);
/* 333 */     getWmf().getLoad().writeXml(dic);
/* 334 */     getTxt().getLoad().writeXml(dic);
/* 335 */     getXps().getSave().writeXml(dic);
/* 336 */     getXls().getLoad().writeXml(dic);
/* 337 */     getDoc().getLoad().writeXml(dic);
/* 338 */     getRasterizeDocument().getLoad().writeXml(dic);
/* 339 */     getAnz().getLoad().writeXml(dic);
/* 340 */     getVff().getLoad().writeXml(dic);
/* 341 */     getVector().getLoad().writeXml(dic);
/*     */   }
/*     */ 
/*     */   void readXml(Map<String, String> dic) {
/* 345 */     getLoad().readXml(dic);
/* 346 */     getSave().readXml(dic);
/*     */ 
/* 348 */     getAbc().getLoad().readXml(dic);
/* 349 */     getAbc().getSave().readXml(dic);
/*     */     try {
/* 351 */       getEcw().getLoad().readXml(dic);
/* 352 */       getEcw().getSave().readXml(dic); } catch (Exception ex) {
/*     */     }
/* 354 */     getEps().getLoad().readXml(dic);
/*     */ 
/* 357 */     getGif().getLoad().readXml(dic);
/* 358 */     getGif().getSave().readXml(dic);
/* 359 */     getJbig().getLoad().readXml(dic);
/* 360 */     getJbig2().getLoad().readXml(dic);
/* 361 */     getJbig2().getSave().readXml(dic);
/* 362 */     getJpeg().getLoad().readXml(dic);
/* 363 */     getJpeg().getSave().readXml(dic);
/* 364 */     getJpeg2000().getLoad().readXml(dic);
/* 365 */     getJpeg2000().getSave().readXml(dic);
/* 366 */     getPcd().getLoad().readXml(dic);
/* 367 */     getPdf().getLoad().readXml(dic);
/* 368 */     getPdf().getSave().readXml(dic);
/* 369 */     getPng().getSave().readXml(dic);
/* 370 */     getPtoka().getLoad().readXml(dic);
/* 371 */     getRaw().getSave().readXml(dic);
/* 372 */     getRtf().getLoad().readXml(dic);
/* 373 */     getTiff().getLoad().readXml(dic);
/* 374 */     getTiff().getSave().readXml(dic);
/* 375 */     getWmf().getLoad().readXml(dic);
/* 376 */     getTxt().getLoad().readXml(dic);
/* 377 */     getXps().getSave().readXml(dic);
/* 378 */     getXls().getLoad().readXml(dic);
/* 379 */     getDoc().getLoad().readXml(dic);
/* 380 */     getRasterizeDocument().getLoad().readXml(dic);
/* 381 */     getAnz().getLoad().readXml(dic);
/* 382 */     getVff().getLoad().readXml(dic);
/* 383 */     getVector().getLoad().readXml(dic);
/*     */   }
/*     */ 
/*     */   public CodecsLoadOptions getLoad()
/*     */   {
/* 388 */     return this._load;
/*     */   }
/*     */ 
/*     */   public CodecsSaveOptions getSave() {
/* 392 */     return this._save;
/*     */   }
/*     */ 
/*     */   public CodecsAbcOptions getAbc() {
/* 396 */     return this.abc;
/*     */   }
/*     */ 
/*     */   public CodecsAnzOptions getAnz() {
/* 400 */     return this.anz;
/*     */   }
/*     */ 
/*     */   public CodecsDocOptions getDoc() {
/* 404 */     return this.doc;
/*     */   }
/*     */ 
/*     */   public CodecsEcwOptions getEcw() {
/* 408 */     return this.ecw;
/*     */   }
/*     */ 
/*     */   public CodecsEpsOptions getEps() {
/* 412 */     return this.eps;
/*     */   }
/*     */ 
/*     */   public CodecsFpxOptions getFpx() {
/* 416 */     return this.fpx;
/*     */   }
/*     */ 
/*     */   public CodecsGifOptions getGif() {
/* 420 */     return this.gif;
/*     */   }
/*     */ 
/*     */   public CodecsJbigOptions getJbig() {
/* 424 */     return this.jbig;
/*     */   }
/*     */ 
/*     */   public CodecsJbig2Options getJbig2() {
/* 428 */     return this.jbig2;
/*     */   }
/*     */ 
/*     */   public CodecsJpegOptions getJpeg() {
/* 432 */     return this.jpeg;
/*     */   }
/*     */ 
/*     */   public CodecsJpeg2000Options getJpeg2000() {
/* 436 */     return this.jpeg2000;
/*     */   }
/*     */ 
/*     */   public CodecsPcdOptions getPcd() {
/* 440 */     return this.pcd;
/*     */   }
/*     */ 
/*     */   public CodecsPdfOptions getPdf() {
/* 444 */     return this.pdf;
/*     */   }
/*     */ 
/*     */   public CodecsPngOptions getPng() {
/* 448 */     return this.png;
/*     */   }
/*     */ 
/*     */   public CodecsPstOptions getPst() {
/* 452 */     return this.pst;
/*     */   }
/*     */ 
/*     */   public CodecsPtokaOptions getPtoka() {
/* 456 */     return this.ptoka;
/*     */   }
/*     */ 
/*     */   public CodecsRawOptions getRaw() {
/* 460 */     return this.raw;
/*     */   }
/*     */ 
/*     */   public CodecsRtfOptions getRtf() {
/* 464 */     return this.rtf;
/*     */   }
/*     */ 
/*     */   public CodecsTiffOptions getTiff() {
/* 468 */     return this.tiff;
/*     */   }
/*     */ 
/*     */   public CodecsRasterizeDocumentOptions getRasterizeDocument() {
/* 472 */     return this.rasterizeDocument;
/*     */   }
/*     */ 
/*     */   public CodecsTxtOptions getTxt() {
/* 476 */     return this.txt;
/*     */   }
/*     */ 
/*     */   public CodecsVectorOptions getVector() {
/* 480 */     return this.vector;
/*     */   }
/*     */ 
/*     */   public CodecsVffOptions getVff() {
/* 484 */     return this.vff;
/*     */   }
/*     */ 
/*     */   public CodecsWmfOptions getWmf() {
/* 488 */     return this.wmf;
/*     */   }
/*     */ 
/*     */   public CodecsXlsOptions getXls() {
/* 492 */     return this.xls;
/*     */   }
/*     */ 
/*     */   public CodecsXpsOptions getXps() {
/* 496 */     return this.xps;
/*     */   }
/*     */ 
/*     */   private void setAbc(CodecsAbcOptions abc)
/*     */   {
/* 506 */     this.abc = abc;
/*     */   }
/*     */ 
/*     */   private void setAnz(CodecsAnzOptions anz) {
/* 510 */     this.anz = anz;
/*     */   }
/*     */ 
/*     */   private void setDoc(CodecsDocOptions doc) {
/* 514 */     this.doc = doc;
/*     */   }
/*     */ 
/*     */   private void setEcw(CodecsEcwOptions ecw) {
/* 518 */     this.ecw = ecw;
/*     */   }
/*     */ 
/*     */   private void setEps(CodecsEpsOptions eps) {
/* 522 */     this.eps = eps;
/*     */   }
/*     */ 
/*     */   private void setFpx(CodecsFpxOptions fpx) {
/* 526 */     this.fpx = fpx;
/*     */   }
/*     */ 
/*     */   private void setGif(CodecsGifOptions gif) {
/* 530 */     this.gif = gif;
/*     */   }
/*     */ 
/*     */   private void setJpeg(CodecsJpegOptions jpeg) {
/* 534 */     this.jpeg = jpeg;
/*     */   }
/*     */ 
/*     */   private void setJbig(CodecsJbigOptions jbig) {
/* 538 */     this.jbig = jbig;
/*     */   }
/*     */ 
/*     */   private void setJbig2(CodecsJbig2Options jbig2) {
/* 542 */     this.jbig2 = jbig2;
/*     */   }
/*     */ 
/*     */   private void setJpeg2000(CodecsJpeg2000Options jpeg2000) {
/* 546 */     this.jpeg2000 = jpeg2000;
/*     */   }
/*     */ 
/*     */   private void setPcd(CodecsPcdOptions pcd) {
/* 550 */     this.pcd = pcd;
/*     */   }
/*     */ 
/*     */   private void setPdf(CodecsPdfOptions pdf) {
/* 554 */     this.pdf = pdf;
/*     */   }
/*     */ 
/*     */   private void setPng(CodecsPngOptions png) {
/* 558 */     this.png = png;
/*     */   }
/*     */ 
/*     */   private void setPtoka(CodecsPtokaOptions ptoka) {
/* 562 */     this.ptoka = ptoka;
/*     */   }
/*     */ 
/*     */   private void setPst(CodecsPstOptions pst) {
/* 566 */     this.pst = pst;
/*     */   }
/*     */ 
/*     */   private void setRasterizeDocument(CodecsRasterizeDocumentOptions rasterizeDocument)
/*     */   {
/* 571 */     this.rasterizeDocument = rasterizeDocument;
/*     */   }
/*     */ 
/*     */   private void setRaw(CodecsRawOptions raw) {
/* 575 */     this.raw = raw;
/*     */   }
/*     */ 
/*     */   private void setRtf(CodecsRtfOptions rtf) {
/* 579 */     this.rtf = rtf;
/*     */   }
/*     */ 
/*     */   private void setTiff(CodecsTiffOptions tiff) {
/* 583 */     this.tiff = tiff;
/*     */   }
/*     */ 
/*     */   private void setTxt(CodecsTxtOptions txt) {
/* 587 */     this.txt = txt;
/*     */   }
/*     */ 
/*     */   private void setVector(CodecsVectorOptions vector) {
/* 591 */     this.vector = vector;
/*     */   }
/*     */ 
/*     */   private void setVff(CodecsVffOptions vff) {
/* 595 */     this.vff = vff;
/*     */   }
/*     */ 
/*     */   private void setWmf(CodecsWmfOptions wmf) {
/* 599 */     this.wmf = wmf;
/*     */   }
/*     */ 
/*     */   private void setXls(CodecsXlsOptions xls) {
/* 603 */     this.xls = xls;
/*     */   }
/*     */ 
/*     */   private void setXps(CodecsXpsOptions xps) {
/* 607 */     this.xps = xps;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsOptions
 * JD-Core Version:    0.6.2
 */