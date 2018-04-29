/*     */ package leadtools;
/*     */ 
/*     */  enum LIB_ID
/*     */ {
/*  58 */   LT_LIBID_FILTER, 
/*  59 */   LT_LIBID_KRN, 
/*  60 */   LT_LIBID_DIS, 
/*  61 */   LT_LIBID_FIL, 
/*  62 */   LT_LIBID_ANN, 
/*  63 */   LT_LIBID_NTF, 
/*  64 */   LT_LIBID_JP2, 
/*  65 */   LT_LIBID_SGM, 
/*  66 */   LT_LIBID_FAX, 
/*  67 */   LT_LIBID_PDF, 
/*  68 */   LT_LIBID_OCR, 
/*  69 */   LT_LIBID_DOCWRT, 
/*  70 */   LT_LIBID_DOCWRTTTF, 
/*  71 */   LT_LIBID_DOCWRTEMF, 
/*  72 */   LT_LIBID_DOCWRTDOC, 
/*  73 */   LT_LIBID_DOCWRTPDF, 
/*  74 */   LT_LIBID_DOCWRTHTM, 
/*  75 */   LT_LIBID_DOCWRTRTF, 
/*  76 */   LT_LIBID_DOCWRTTXT, 
/*  77 */   LT_LIBID_DOCWRTDOCX, 
/*  78 */   LT_LIBID_DOCWRTXPS, 
/*  79 */   LT_LIBID_DOCWRTXLS, 
/*  80 */   LT_LIBID_VECTOR, 
/*  81 */   LT_LIBID_SVG, 
/*  82 */   LT_LIBID_CLR, 
/*  83 */   LT_LIBID_DRW, 
/*  84 */   LT_LIBID_DRWSKIA, 
/*  85 */   LT_LIBID_SPELLCHECKOS, 
/*  86 */   LT_LIBID_SPELLCHECKHUNSPELL, 
/*  87 */   LT_LIBID_MRC, 
/*  88 */   LT_LIBID_DIC, 
/*  89 */   LT_LIBID_DICTABLES, 
/*  90 */   LT_LIBID_IMGUTL, 
/*  91 */   LT_LIBID_IMGCOR, 
/*  92 */   LT_LIBID_IMGCLR, 
/*  93 */   LT_LIBID_IMGEFX, 
/*  94 */   LT_LIBID_IMGSFX, 
/*  95 */   LT_LIBID_BAR, 
/*  96 */   LT_LIBID_BARONED, 
/*  97 */   LT_LIBID_BARPDFREAD, 
/*  98 */   LT_LIBID_BARPDFWRITE, 
/*  99 */   LT_LIBID_BARDATAMATRIXREAD, 
/* 100 */   LT_LIBID_BARDATAMATRIXWRITE, 
/* 101 */   LT_LIBID_BARQRREAD, 
/* 102 */   LT_LIBID_BARQRWRITE, 
/* 103 */   LT_LIBID_BAR2DREAD, 
/* 104 */   LT_LIBID_BAR2DWRITE, 
/* 105 */   LT_LIBID_SANE, 
/* 106 */   LT_LIBID_CREDITCARDS, 
/* 107 */   LT_LIBID_LAST;
/*     */ 
/*     */   int getValue() {
/* 110 */     return ordinal();
/*     */   }
/*     */   static LIB_ID forValue(int value) {
/* 113 */     return values()[value];
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.LIB_ID
 * JD-Core Version:    0.6.2
 */