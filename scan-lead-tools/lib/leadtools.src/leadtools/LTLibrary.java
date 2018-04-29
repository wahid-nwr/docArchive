/*    */ package leadtools;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ 
/*    */ public enum LTLibrary
/*    */ {
/*  9 */   LEADTOOLS(new LIB_ID[][] { { LIB_ID.LT_LIBID_KRN, LIB_ID.LT_LIBID_DIS } }), 
/* 10 */   IMAGE_PROCESSING_COLOR(new LIB_ID[][] { LEADTOOLS.dependencies, { LIB_ID.LT_LIBID_IMGUTL, LIB_ID.LT_LIBID_IMGCLR } }), 
/* 11 */   IMAGE_PROCESSING_CORE(new LIB_ID[][] { LEADTOOLS.dependencies, { LIB_ID.LT_LIBID_IMGUTL, LIB_ID.LT_LIBID_IMGCOR } }), 
/* 12 */   IMAGE_PROCESSING_EFFECTS(new LIB_ID[][] { LEADTOOLS.dependencies, { LIB_ID.LT_LIBID_IMGUTL, LIB_ID.LT_LIBID_IMGEFX } }), 
/* 13 */   BARCODE(new LIB_ID[][] { IMAGE_PROCESSING_CORE.dependencies, { LIB_ID.LT_LIBID_BAR } }), 
/* 14 */   CODECS(new LIB_ID[][] { LEADTOOLS.dependencies, { LIB_ID.LT_LIBID_FIL } }), 
/* 15 */   DICOM(new LIB_ID[][] { LEADTOOLS.dependencies, { LIB_ID.LT_LIBID_DIC } }), 
/* 16 */   PDF(new LIB_ID[][] { CODECS.dependencies, { LIB_ID.LT_LIBID_CLR, LIB_ID.LT_LIBID_FAX, LIB_ID.LT_LIBID_PDF } }), 
/* 17 */   SANE(new LIB_ID[][] { LEADTOOLS.dependencies, { LIB_ID.LT_LIBID_SANE } }), 
/* 18 */   SVG(new LIB_ID[][] { CODECS.dependencies, IMAGE_PROCESSING_COLOR.dependencies, IMAGE_PROCESSING_EFFECTS.dependencies, { LIB_ID.LT_LIBID_DRW, LIB_ID.LT_LIBID_VECTOR, LIB_ID.LT_LIBID_SVG } }), 
/* 19 */   FORMS_DOCUMENT_WRITERS(new LIB_ID[][] { IMAGE_PROCESSING_CORE.dependencies, SVG.dependencies, { LIB_ID.LT_LIBID_ANN, LIB_ID.LT_LIBID_DOCWRTTTF, LIB_ID.LT_LIBID_DOCWRTEMF, LIB_ID.LT_LIBID_DOCWRT } }), 
/* 20 */   FORMS_OCR(new LIB_ID[][] { FORMS_DOCUMENT_WRITERS.dependencies, { LIB_ID.LT_LIBID_OCR } }), 
/* 21 */   DOCUMENTS(new LIB_ID[][] { SVG.dependencies, PDF.dependencies }), 
/* 22 */   DOCUMENTS_CONVERTERS(new LIB_ID[][] { DOCUMENTS.dependencies, FORMS_OCR.dependencies }), 
/* 23 */   CREDIT_CARDS(new LIB_ID[][] { LEADTOOLS.dependencies, { LIB_ID.LT_LIBID_CREDITCARDS } });
/*    */ 
/* 34 */   private static boolean[] _loaded = new boolean[LIB_ID.LT_LIBID_LAST.getValue()];
/*    */   LIB_ID[] dependencies;
/*    */ 
/*    */   static boolean isLoaded(LIB_ID libId)
/*    */   {
/* 36 */     return _loaded[libId.getValue()];
/*    */   }
/*    */   static void setLoaded(LIB_ID libId, boolean value) {
/* 39 */     _loaded[libId.getValue()] = value;
/*    */   }
/*    */ 
/*    */   private LTLibrary(LIB_ID[][] deps)
/*    */   {
/* 45 */     ArrayList libs = new ArrayList();
/* 46 */     for (LIB_ID[] depsArray : deps) {
/* 47 */       for (LIB_ID lib : depsArray) {
/* 48 */         if (!libs.contains(lib)) {
/* 49 */           libs.add(lib);
/*    */         }
/*    */       }
/*    */     }
/* 53 */     this.dependencies = ((LIB_ID[])libs.toArray(new LIB_ID[0]));
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.LTLibrary
 * JD-Core Version:    0.6.2
 */