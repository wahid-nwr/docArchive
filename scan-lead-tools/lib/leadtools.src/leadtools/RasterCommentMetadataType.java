/*     */ package leadtools;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ 
/*     */ public enum RasterCommentMetadataType
/*     */ {
/*   5 */   ARTIST(0), 
/*   6 */   COPYRIGHT(1), 
/*   7 */   DATE_TIME(2), 
/*   8 */   DESCRIPTION(3), 
/*   9 */   HOST_COMPUTER(4), 
/*  10 */   MAKE(5), 
/*  11 */   MODEL(6), 
/*  12 */   NAME_OF_DOCUMENT(7), 
/*  13 */   NAME_OF_PAGE(8), 
/*  14 */   SOFTWARE(9), 
/*  15 */   PATIENT_NAME(10), 
/*  16 */   PATIENT_ID(11), 
/*  17 */   PATIENT_BIRTHDATE(12), 
/*  18 */   PATIENT_SEX(13), 
/*  19 */   STUDY_INSTANCE(14), 
/*  20 */   STUDY_DATE(15), 
/*  21 */   STUDY_TIME(16), 
/*  22 */   STUDY_REFERRING_PHYSICIAN(17), 
/*  23 */   SERIES_MODALITY(18), 
/*  24 */   SERIES_ID(19), 
/*  25 */   SERIES_NUMBER(20), 
/*  26 */   EXIF_VERSION(21), 
/*  27 */   DATE_TIME_ORIGINAL(22), 
/*  28 */   DATE_TIME_DIGITIZED(23), 
/*  29 */   SHUTTER_SPEED_VALUE(24), 
/*  30 */   APERTURE(25), 
/*  31 */   BRIGHTNESS(26), 
/*  32 */   EXPOSURE_BIAS(27), 
/*  33 */   MAX_APERTURE(28), 
/*  34 */   SUBJECT_DISTANCE(29), 
/*  35 */   METERING_MODE(30), 
/*  36 */   LIGHT_SOURCE(31), 
/*  37 */   FLASH(32), 
/*  38 */   FOCAL_LENGTH(33), 
/*  39 */   EXPOSURE_TIME(34), 
/*  40 */   FNUMBER(35), 
/*  41 */   MAKER_NOTE(36), 
/*  42 */   USER_COMMENT(37), 
/*  43 */   SUB_SEC_TIME(38), 
/*  44 */   SUB_SEC_TIME_ORIGINAL(39), 
/*  45 */   SUB_SEC_TIME_DIGITIZED(40), 
/*  46 */   SUPPORTED_FLASH_PIX_VERSION(158), 
/*  47 */   COLOR_SPACE(159), 
/*  48 */   EXPOSURE_PROGRAM(160), 
/*  49 */   SPECTRAL_SENSITIVITY(161), 
/*  50 */   ISO_SPEED_RATINGS(162), 
/*  51 */   OPTO_ELECTRIC_COEFFICIENT(163), 
/*  52 */   RELATED_SOUND_FILE(164), 
/*  53 */   FLASH_ENERGY(165), 
/*  54 */   SPATIAL_FREQUENCY_RESPONSE(166), 
/*  55 */   FOCAL_PLANE_XRESOLUTION(167), 
/*  56 */   FOCAL_PLANE_RESOLUTION_UNIT(245), 
/*  57 */   FOCAL_PLANE_YRESOLUTION(168), 
/*  58 */   SUBJECT_LOCATION(169), 
/*  59 */   EXPOSURE_INDEX(170), 
/*  60 */   SENSING_METHOD(171), 
/*  61 */   FILE_SOURCE(172), 
/*  62 */   SCENE_TYPE(173), 
/*  63 */   CFA_PATTERN(174), 
/*  64 */   SUBJECT_AREA(227), 
/*  65 */   CUSTOM_RENDERED(228), 
/*  66 */   EXPOSURE_MODE(229), 
/*  67 */   WHITE_BALANCE(230), 
/*  68 */   DIGITAL_ZOOM_RATIO(231), 
/*  69 */   FOCAL_LENGTH_IN_35MM_FILM(232), 
/*  70 */   SCENE_CAPTURE_TYPE(233), 
/*  71 */   GAIN_CONTROL(234), 
/*  72 */   CONTRAST(235), 
/*  73 */   SATURATION(236), 
/*  74 */   SHARPNESS(237), 
/*  75 */   DEVICE_SETTING_DESCRIPTION(238), 
/*  76 */   SUBJECT_DISTANCE_RANGE(239), 
/*  77 */   IMAGE_UNIQUE_ID(240), 
/*  78 */   GAMMA(246), 
/*  79 */   GPS_VERSION_ID(41), 
/*  80 */   GPS_LATITUDE_REF(42), 
/*  81 */   GPS_LATITUDE(43), 
/*  82 */   GPS_LONGITUDE_REF(44), 
/*  83 */   GPS_LONGITUDE(45), 
/*  84 */   GPS_ALTITUDE_REF(46), 
/*  85 */   GPS_ALTITUDE(47), 
/*  86 */   GPS_TIMESTAMP(48), 
/*  87 */   GPS_SATELLITES(49), 
/*  88 */   GPS_STATUS(50), 
/*  89 */   GPS_MEASURE_MODE(51), 
/*  90 */   GPS_DOP(52), 
/*  91 */   GPS_SPEED_REF(53), 
/*  92 */   GPS_SPEED(54), 
/*  93 */   GPS_TRACK_REF(55), 
/*  94 */   GPS_TRACK(56), 
/*  95 */   GPS_IMAGE_DIRECTION_REF(57), 
/*  96 */   GPS_IMAGE_DIRECTION(58), 
/*  97 */   GPS_MAP_DATUM(59), 
/*  98 */   GPS_DESTINATION_LATITUDE_REF(60), 
/*  99 */   GPS_DESTINATION_LATITUDE(61), 
/* 100 */   GPS_DESTINATION_LONGITUDE_REF(62), 
/* 101 */   GPS_DESTINATION_LONGITUDE(63), 
/* 102 */   GPS_DESTINATION_BEARING_REF(64), 
/* 103 */   GPS_DESTINATION_BEARING(65), 
/* 104 */   GPS_DESTINATION_DISTANCE_REF(66), 
/* 105 */   GPS_DESTINATION_DISTANCE(67), 
/* 106 */   GPS_PROCESSING_METHOD(241), 
/* 107 */   GPS_AREA_INFORMATION(242), 
/* 108 */   GPS_DATE_STAMP(243), 
/* 109 */   GPS_DIFFERENTIAL(244), 
/*     */ 
/* 111 */   TITLE(175), 
/* 112 */   DISCLAIMER(176), 
/* 113 */   WARNING(177), 
/* 114 */   MISC(178), 
/* 115 */   JPEG2000_BINARY(179), 
/* 116 */   JPEG2000_LATIN(180), 
/* 117 */   IPTC_FIRST(181), 
/* 118 */   IPTC_VERSION(181), 
/* 119 */   IPTC_OBJECT_TYPE_REFERENCE(182), 
/* 120 */   IPTC_OBJECT_ATTRIBUTE_REFERENCE(183), 
/* 121 */   IPTC_OBJECT_NAME(184), 
/* 122 */   IPTC_EDIT_STATUS(185), 
/* 123 */   IPTC_EDITORIAL_UPDATE(186), 
/* 124 */   IPTC_URGENCY(187), 
/* 125 */   IPTC_SUBJECT_REFERENCE(188), 
/* 126 */   IPTC_CATEGORY(189), 
/* 127 */   IPTC_SUPPLEMENTAL_CATEGORY(190), 
/* 128 */   IPTC_FIXTURE_IDENTIFIER(191), 
/* 129 */   IPTC_KEYWORDS(192), 
/* 130 */   IPTC_CONTENT_LOCATION_CODE(193), 
/* 131 */   IPTC_CONTENT_LOCATION_NAME(194), 
/* 132 */   IPTC_RELEASE_DATE(195), 
/* 133 */   IPTC_RELEASE_TIME(196), 
/* 134 */   IPTC_EXPIRATION_DATE(197), 
/* 135 */   IPTC_EXPIRATION_TIME(198), 
/* 136 */   IPTC_SPECIAL_INSTRUCTIONS(199), 
/* 137 */   IPTC_ACTION_ADVISED(200), 
/* 138 */   IPTC_REFERENCE_SERVICE(201), 
/* 139 */   IPTC_REFERENCE_DATE(202), 
/* 140 */   IPTC_REFERENCE_NUMBER(203), 
/* 141 */   IPTC_DATE_CREATED(204), 
/* 142 */   IPTC_TIME_CREATED(205), 
/* 143 */   IPTC_DIGITAL_CREATION_DATE(206), 
/* 144 */   IPTC_DIGITAL_CREATION_TIME(207), 
/* 145 */   IPTC_ORIGINATING_PROGRAM(208), 
/* 146 */   IPTC_PROGRAM_VERSION(209), 
/* 147 */   IPTC_OBJECT_CYCLE(210), 
/* 148 */   IPTC_BYLINE(211), 
/* 149 */   IPTC_BYLINE_TITLE(212), 
/* 150 */   IPTC_CITY(213), 
/* 151 */   IPTC_SUB_LOCATION(214), 
/* 152 */   IPTC_PROVINCE_STATE(215), 
/* 153 */   IPTC_PRIMARY_LOCATION_CODE(216), 
/* 154 */   IPTC_PRIMARY_LOCATION_NAME(217), 
/* 155 */   IPTC_ORIGINAL_TRANSMISSION_REFERENCE(218), 
/* 156 */   IPTC_HEADLINE(219), 
/* 157 */   IPTC_CREDIT(220), 
/* 158 */   IPTC_SOURCE(221), 
/* 159 */   IPTC_COPYRIGHT(222), 
/* 160 */   IPTC_CONTACT(223), 
/* 161 */   IPTC_CAPTION(224), 
/* 162 */   IPTC_AUTHOR(225), 
/* 163 */   IPTC_LANGUAGE_IDENTIFIER(226), 
/* 164 */   IPTC_LAST(226);
/*     */ 
/*     */   private int intValue;
/*     */   private static HashMap<Integer, RasterCommentMetadataType> mappings;
/*     */ 
/* 170 */   private static HashMap<Integer, RasterCommentMetadataType> getMappings() { if (mappings == null)
/*     */     {
/* 172 */       synchronized (RasterCommentMetadataType.class)
/*     */       {
/* 174 */         if (mappings == null)
/*     */         {
/* 176 */           mappings = new HashMap();
/*     */         }
/*     */       }
/*     */     }
/* 180 */     return mappings;
/*     */   }
/*     */ 
/*     */   private RasterCommentMetadataType(int value)
/*     */   {
/* 185 */     this.intValue = value;
/* 186 */     getMappings().put(Integer.valueOf(value), this);
/*     */   }
/*     */ 
/*     */   public int getValue()
/*     */   {
/* 191 */     return this.intValue;
/*     */   }
/*     */ 
/*     */   public static RasterCommentMetadataType forValue(int value)
/*     */   {
/* 196 */     return (RasterCommentMetadataType)getMappings().get(Integer.valueOf(value));
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.RasterCommentMetadataType
 * JD-Core Version:    0.6.2
 */