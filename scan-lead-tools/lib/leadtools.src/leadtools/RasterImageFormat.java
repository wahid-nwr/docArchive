/*     */ package leadtools;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ 
/*     */ public enum RasterImageFormat
/*     */ {
/*   7 */   UNKNOWN(0), 
/*   8 */   PCX(1), 
/*   9 */   GIF(2), 
/*  10 */   TIF(3), 
/*  11 */   TGA(4), 
/*  12 */   CMP(5), 
/*  13 */   BMP(6), 
/*  14 */   JPEG(10), 
/*  15 */   JPEG_RGB(182), 
/*  16 */   TIF_JPEG(11), 
/*  17 */   OS2(14), 
/*  18 */   WMF(15), 
/*  19 */   EPS(16), 
/*  20 */   TIFLZW(17), 
/*  21 */   JPEG_411(21), 
/*  22 */   TIF_JPEG_411(22), 
/*  23 */   JPEG_422(23), 
/*  24 */   TIF_JPEG_422(24), 
/*  25 */   JPEG_CMYK(391), 
/*  26 */   JPEG_CMYK_411(392), 
/*  27 */   JPEG_CMYK_422(393), 
/*  28 */   TIF_JPEG_CMYK(394), 
/*  29 */   TIF_JPEG_CMYK_411(395), 
/*  30 */   TIF_JPEG_CMYK_422(396), 
/*  31 */   CCITT(25), 
/*  32 */   LEAD1BIT(26), 
/*  33 */   CCITT_GROUP3_1DIM(27), 
/*  34 */   CCITT_GROUP3_2DIM(28), 
/*  35 */   CCITT_GROUP4(29), 
/*  36 */   ABC(32), 
/*  37 */   CALS(50), 
/*  38 */   MAC(51), 
/*  39 */   IMG(52), 
/*  40 */   MSP(53), 
/*  41 */   WPG(54), 
/*  42 */   RAS(55), 
/*  43 */   FLI(61), 
/*  44 */   CGM(62), 
/*  45 */   EPSTIFF(63), 
/*  46 */   EPSWMF(64), 
/*  47 */   FAX_G3_1D(66), 
/*  48 */   FAX_G3_2D(67), 
/*  49 */   FAX_G4(68), 
/*  50 */   WFX_G3_1D(69), 
/*  51 */   WFX_G4(70), 
/*  52 */   ICA_G3_1D(71), 
/*  53 */   ICA_G3_2D(72), 
/*  54 */   ICA_G4(73), 
/*  55 */   OS2_2(74), 
/*  56 */   PNG(75), 
/*  57 */   RAWICA_G3_1D(77), 
/*  58 */   RAWICA_G3_2D(78), 
/*  59 */   RAWICA_G4(79), 
/*  60 */   RAWICA_ABIC(184), 
/*  61 */   BMP_RLE(84), 
/*  62 */   TIF_CMYK(85), 
/*  63 */   TIFLZW_CMYK(86), 
/*  64 */   TIF_PACKBITS(87), 
/*  65 */   TIF_PACKBITS_CMYK(88), 
/*  66 */   TIF_DXF(176), 
/*  67 */   WIN_ICO(91), 
/*  68 */   WIN_CUR(92), 
/*  69 */   TIF_YCC(93), 
/*  70 */   TIFLZW_YCC(94), 
/*  71 */   TIF_PACKBITS_YCC(95), 
/*  72 */   EXIF(96), 
/*  73 */   EXIF_YCC(97), 
/*  74 */   EXIF_JPEG_422(98), 
/*  75 */   EXIF_JPEG(98), 
/*  76 */   EXIF_JPEG_411(101), 
/*  77 */   PBM_ASCII(102), 
/*  78 */   PBM_BINARY(103), 
/*  79 */   PGM_ASCII(104), 
/*  80 */   PGM_BINARY(105), 
/*  81 */   PPM_ASCII(106), 
/*  82 */   PPM_BINARY(107), 
/*  83 */   CUT(108), 
/*  84 */   XPM(109), 
/*  85 */   XBM(110), 
/*  86 */   IFF_ILBM(111), 
/*  87 */   IFF_CAT(112), 
/*  88 */   XWD(113), 
/*  89 */   CLP(114), 
/*  90 */   JBIG(115), 
/*  91 */   EMF(116), 
/*  92 */   ICA_IBM_MMR(117), 
/*  93 */   RAWICA_IBM_MMR(118), 
/*  94 */   ANI(119), 
/*  95 */   LASERDATA(121), 
/*  96 */   INTERGRAPH_RLE(122), 
/*  97 */   INTERGRAPH_VECTOR(123), 
/*  98 */   DWG(124), 
/*  99 */   CALS4(129), 
/* 100 */   CALS2(130), 
/* 101 */   CALS3(131), 
/* 102 */   XWD10(132), 
/* 103 */   XWD11(133), 
/* 104 */   FLC(134), 
/* 105 */   TIF_CMP(138), 
/* 106 */   TIF_JBIG(139), 
/* 107 */   TIF_UNKNOWN(141), 
/* 108 */   SGI(142), 
/* 109 */   SGI_RLE(143), 
/* 110 */   DWF(145), 
/* 111 */   RAS_PDF(146), 
/* 112 */   RAS_PDF_G3_1D(147), 
/* 113 */   RAS_PDF_G3_2D(148), 
/* 114 */   RAS_PDF_G4(149), 
/* 115 */   RAS_PDF_JPEG(150), 
/* 116 */   RAS_PDF_JPEG_422(151), 
/* 117 */   RAS_PDF_JPEG_411(152), 
/* 118 */   RAS_PDF_LZW(179), 
/* 119 */   RAS_PDF_JBIG2(188), 
/* 120 */   RAS_PDF_CMYK(333), 
/* 121 */   RAS_PDF_LZW_CMYK(334), 
/* 122 */   RAW(153), 
/* 123 */   TIF_CUSTOM(155), 
/* 124 */   RAW_RGB(156), 
/* 125 */   RAW_RLE4(157), 
/* 126 */   RAW_RLE8(158), 
/* 127 */   RAW_BITFIELDS(159), 
/* 128 */   RAW_PACKBITS(160), 
/* 129 */   RAW_JPEG(161), 
/* 130 */   FAX_G3_1D_NOEOL(162), 
/* 131 */   RAW_CCITT(162), 
/* 132 */   JP2(163), 
/* 133 */   J2K(164), 
/* 134 */   CMW(165), 
/* 135 */   TIF_J2K(166), 
/* 136 */   TIF_CMW(167), 
/* 137 */   MRC(168), 
/* 138 */   LEAD_MRC(314), 
/* 139 */   TIF_MRC(177), 
/* 140 */   TIF_LEAD_MRC(315), 
/* 141 */   GERBER(169), 
/* 142 */   WBMP(170), 
/* 143 */   JPEG_LAB(171), 
/* 144 */   JPEG_LAB_411(172), 
/* 145 */   JPEG_LAB_422(173), 
/* 146 */   GEOTIFF(174), 
/* 147 */   TIF_LEAD1BIT(175), 
/* 148 */   TIF_ABC(180), 
/* 149 */   NAP(181), 
/* 150 */   POSTSCRIPT(222), 
/* 151 */   SVG(247), 
/* 152 */   PTOCA(249), 
/* 153 */   SCT(250), 
/* 154 */   PCL(251), 
/* 155 */   AFP(252), 
/* 156 */   ICA_UNCOMPRESSED(253), 
/* 157 */   RAWICA_UNCOMPRESSED(254), 
/* 158 */   SHP(255), 
/* 159 */   SMP(256), 
/* 160 */   SMP_G3_1D(257), 
/* 161 */   SMP_G3_2D(258), 
/* 162 */   SMP_G4(259), 
/* 163 */   VWPG(260), 
/* 164 */   VWPG1(328), 
/* 165 */   CMX(261), 
/* 166 */   TGA_RLE(262), 
/* 167 */   RAS_RLE(288), 
/* 168 */   DXF_R12(58), 
/* 169 */   DXF_R13(290), 
/* 170 */   CLP_RLE(291), 
/* 171 */   FIT(295), 
/* 172 */   CIN(298), 
/* 173 */   EPSPOSTSCRIPT(300), 
/* 174 */   INTERGRAPH_CCITT_G4(301), 
/* 175 */   SFF(302), 
/* 176 */   IFF_ILBM_UNCOMPRESSED(303), 
/* 177 */   IFF_CAT_UNCOMPRESSED(304), 
/* 178 */   AFPICA_G3_1D(309), 
/* 179 */   AFPICA_G3_2D(310), 
/* 180 */   AFPICA_G4(311), 
/* 181 */   AFPICA_UNCOMPRESSED(312), 
/* 182 */   AFPICA_IBM_MMR(313), 
/* 183 */   AFPICA_ABIC(191), 
/* 184 */   PSD(76), 
/* 185 */   JBIG2(183), 
/* 186 */   CRW(296), 
/* 187 */   DWF_TEXT_AS_POLYLINE(297), 
/* 188 */   DCR(292), 
/* 189 */   DCS(266), 
/* 190 */   ECW(277), 
/* 191 */   KDC(135), 
/* 192 */   DRW(136), 
/* 193 */   PCD(57), 
/* 194 */   DXF(58), 
/* 195 */   KDC_120(263), 
/* 196 */   KDC_40(264), 
/* 197 */   KDC_50(265), 
/* 198 */   RTF_RASTER(305), 
/* 199 */   AWD(99), 
/* 200 */   ABIC(185), 
/* 201 */   ICA_ABIC(190), 
/* 202 */   TIF_ABIC(186), 
/* 203 */   TIF_JBIG2(187), 
/* 204 */   RAW_LZW(178), 
/* 205 */   PDF_LEAD_MRC(317), 
/* 206 */   TXT(316), 
/* 207 */   FPX(80), 
/* 208 */   FPX_SINGLE_COLOR(81), 
/* 209 */   FPX_JPEG(82), 
/* 210 */   FPX_JPEG_QFACTOR(83), 
/* 211 */   DICOM_GRAY(89), 
/* 212 */   DICOM_COLOR(90), 
/* 213 */   DICOM_RLE_GRAY(125), 
/* 214 */   DICOM_RLE_COLOR(126), 
/* 215 */   DICOM_JPEG_GRAY(127), 
/* 216 */   DICOM_JPEG_COLOR(128), 
/* 217 */   DICOM_JPEG_LS_GRAY(383), 
/* 218 */   DICOM_JPEG_LS_COLOR(384), 
/* 219 */   DICOM_J2K_GRAY(293), 
/* 220 */   DICOM_J2K_COLOR(294), 
/* 221 */   DICOM_JPX_GRAY(389), 
/* 222 */   DICOM_JPX_COLOR(390), 
/* 223 */   WMZ(307), 
/* 224 */   PCT(56), 
/* 225 */   SID(306), 
/* 226 */   TIFX_JBIG(269), 
/* 227 */   TIFX_JBIG_T43(270), 
/* 228 */   TIFX_JBIG_T43_ITULAB(271), 
/* 229 */   TIFX_JBIG_T43_GS(272), 
/* 230 */   TIFX_FAX_G4(273), 
/* 231 */   TIFX_FAX_G3_1D(274), 
/* 232 */   TIFX_FAX_G3_2D(275), 
/* 233 */   TIFX_JPEG(276), 
/* 234 */   HDP(318), 
/* 235 */   HDP_GRAY(319), 
/* 236 */   HDP_CMYK(320), 
/* 237 */   PNG_ICO(321), 
/* 238 */   DJVU(308), 
/* 239 */   TIF_ZIP(189), 
/* 240 */   XPS(322), 
/* 241 */   JPX(323), 
/* 242 */   XPS_JPEG(324), 
/* 243 */   XPS_JPEG_422(325), 
/* 244 */   XPS_JPEG_411(326), 
/* 245 */   MNG(327), 
/* 246 */   MNG_GRAY(329), 
/* 247 */   MNG_JNG(330), 
/* 248 */   MNG_JNG_411(331), 
/* 249 */   MNG_JNG_422(332), 
/* 250 */   MIF(335), 
/* 251 */   E00(336), 
/* 252 */   TDB(337), 
/* 253 */   TDB_VISTA(338), 
/* 254 */   SNP(339), 
/* 255 */   AFP_IM1(340), 
/* 256 */   XLS(341), 
/* 257 */   DOC(342), 
/* 258 */   ANZ(343), 
/* 259 */   PPT(344), 
/* 260 */   PPT_JPEG(345), 
/* 261 */   PPT_PNG(346), 
/* 262 */   JPM(347), 
/* 263 */   VFF(348), 
/* 264 */   PCLXL(349), 
/* 265 */   DOCX(350), 
/* 266 */   XLSX(351), 
/* 267 */   PPTX(352), 
/* 268 */   JXR(353), 
/* 269 */   JXR_GRAY(354), 
/* 270 */   JXR_CMYK(355), 
/* 271 */   JLS(356), 
/* 272 */   JXR_422(357), 
/* 273 */   JXR_420(358), 
/* 274 */   DCF_ARW(359), 
/* 275 */   DCF_RAF(360), 
/* 276 */   DCF_ORF(361), 
/* 277 */   DCF_CR2(362), 
/* 278 */   DCF_NEF(363), 
/* 279 */   DCF_RW2(364), 
/* 280 */   DCF_CASIO(365), 
/* 281 */   DCF_PENTAX(366), 
/* 282 */   JLS_LINE(367), 
/* 283 */   JLS_SAMPLE(368), 
/* 284 */   HTM(369), 
/* 285 */   MOB(370), 
/* 286 */   PUB(371), 
/* 287 */   ING(372), 
/* 288 */   ING_RLE(373), 
/* 289 */   ING_ADRLE(374), 
/* 290 */   ING_CG4(375), 
/* 291 */   DWFX(376), 
/* 292 */   ICA_JPEG(377), 
/* 293 */   ICA_JPEG_411(378), 
/* 294 */   ICA_JPEG_422(379), 
/* 295 */   PLT(137), 
/* 296 */   DCF_DNG(380), 
/* 297 */   RAW_FLATE(381), 
/* 298 */   RAW_RLE(382), 
/* 299 */   PST(385), 
/* 300 */   MSG(386), 
/* 301 */   EML(387), 
/* 302 */   RAS_PDF_JPX(388), 
/* 303 */   SVGZ(397);
/*     */ 
/*     */   private int intValue;
/*     */   private static HashMap<Integer, RasterImageFormat> mappings;
/*     */ 
/* 309 */   private static HashMap<Integer, RasterImageFormat> getMappings() { if (mappings == null)
/*     */     {
/* 311 */       synchronized (RasterImageFormat.class)
/*     */       {
/* 313 */         if (mappings == null)
/*     */         {
/* 315 */           mappings = new HashMap();
/*     */         }
/*     */       }
/*     */     }
/* 319 */     return mappings;
/*     */   }
/*     */ 
/*     */   private RasterImageFormat(int value)
/*     */   {
/* 324 */     this.intValue = value;
/* 325 */     getMappings().put(Integer.valueOf(value), this);
/*     */   }
/*     */ 
/*     */   public int getValue()
/*     */   {
/* 330 */     return this.intValue;
/*     */   }
/*     */ 
/*     */   public static RasterImageFormat forValue(int value)
/*     */   {
/* 335 */     return (RasterImageFormat)getMappings().get(Integer.valueOf(value));
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.RasterImageFormat
 * JD-Core Version:    0.6.2
 */