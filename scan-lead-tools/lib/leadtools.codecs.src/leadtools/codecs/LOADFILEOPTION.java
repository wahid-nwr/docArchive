/*    */ package leadtools.codecs;
/*    */ 
/*    */ import leadtools.internal.IsInternal;
/*    */ 
/*    */ @IsInternal
/*    */ class LOADFILEOPTION
/*    */ {
/*    */   public int XResolution;
/*    */   public int YResolution;
/*    */   public int Flags;
/*    */   public int Passes;
/*    */   public int PageNumber;
/*    */   public int GlobalLoop;
/*    */   public long IFD;
/*    */   public int uRedScan;
/*    */   public int uGreenScan;
/*    */   public int uBlueScan;
/*    */   public long pDataCallbacks;
/*    */ 
/*    */   static enum ELO_FLAGS
/*    */   {
/*  9 */     ELO_REVERSEBITS(1), 
/* 10 */     ELO_GLOBALBACKGROUND(2), 
/* 11 */     ELO_GLOBALPALETTE(4), 
/* 12 */     ELO_GLOBALLOOP(8), 
/* 13 */     ELO_ROTATED(16), 
/* 14 */     ELO_IGNOREVIEWTRANSFORMS(32), 
/* 15 */     ELO_IGNORECOLORTRANSFORMS(64), 
/* 16 */     ELO_SIGNED(128), 
/* 17 */     ELO_DISABLEMMX(256), 
/* 18 */     ELO_DISABLEP3(512), 
/* 19 */     ELO_USEIFD(1024), 
/* 20 */     ELO_FORCECIELAB(2048), 
/* 21 */     ELO_USEBADJPEGPREDICTOR(4096), 
/* 22 */     ELO_IGNOREPHOTOMETRICINTERP(8192), 
/*    */ 
/* 28 */     ELO_FORCERGBFILE(16384), 
/* 29 */     ELO_MULTISPECTRALSCAN(32768), 
/* 30 */     ELO_LOADCORRUPTED(65536), 
/* 31 */     ELO_FORCE_EPS_THUMBNAIL(131072), 
/* 32 */     ELO_NITF_USE_MAX(262144), 
/* 33 */     ELO_NITF_USE_MONODARK(524288), 
/* 34 */     ELO_NITF_SHOW_OBJECT(1048576), 
/* 35 */     ELO_IGNOREVIEWPERSPECTIVE(2097152), 
/* 36 */     ELO_USEFASTCONVERSION(4194304), 
/* 37 */     ELO_FAST(8388608), 
/* 38 */     ELO_ALPHAINIT(16777216), 
/* 39 */     ELO_COLOR_COMPONENT_ONLY(33554432), 
/* 40 */     ELO_IGNORE_ADOBE_COLOR_TRANSFORM(67108864), 
/* 41 */     ELO_ALLOW13BITLZWCODE(134217728), 
/* 42 */     ELO_VECTOR_CONVERTED_UNITS(268435456), 
/* 43 */     ELO_LOADOLDJBIG2FILES(536870912), 
/*    */ 
/* 45 */     ELO_PREMULTIPLY_ALPHA(1073741824), 
/*    */ 
/* 47 */     ELO2_MULTITHREADED(1);
/*    */ 
/*    */     private int value;
/*    */ 
/*    */     private ELO_FLAGS(int value)
/*    */     {
/* 54 */       this.value = value;
/*    */     }
/*    */ 
/*    */     public int getValue()
/*    */     {
/* 59 */       return this.value;
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.LOADFILEOPTION
 * JD-Core Version:    0.6.2
 */