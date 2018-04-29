/*    */ package leadtools;
/*    */ 
/*    */ public final class RasterImageChangedFlags
/*    */ {
/*  6 */   public static int NONE = 0;
/*  7 */   public static int DATA = 1;
/*  8 */   public static int SIZE = 2;
/*  9 */   public static int BITS_PER_PIXEL = 4;
/* 10 */   public static int VIEW_PERSPECTIVE = 8;
/* 11 */   public static int ORDER = 16;
/* 12 */   public static int PALETTE = 32;
/* 13 */   public static int LOOKUP_TABLE_PALETTE = 64;
/* 14 */   public static int REGION = 128;
/* 15 */   public static int RESOLUTION = 256;
/* 16 */   public static int LOW_HIGH_BIT = 512;
/* 17 */   public static int MIN_MAX_VALUE = 1024;
/* 18 */   public static int NO_REGION_CLIP = 2048;
/* 19 */   public static int PAINT_LOOKUP_TABLE = 4096;
/* 20 */   public static int PAINT_PARAMETERS = 8192;
/* 21 */   public static int DITHERING_METHOD = 16384;
/* 22 */   public static int USE_LOOKUP_TABLE = 32768;
/* 23 */   public static int USE_PAINT_LOOKUP_TABLE = 65536;
/* 24 */   public static int TRANSPARENT_COLOR = 131072;
/* 25 */   public static int OVERLAYS_INFO = 262144;
/* 26 */   public static int PLAY_PARAMETERS = 524288;
/*    */ 
/* 28 */   public static int PAGE = 2097152;
/* 29 */   public static int PAGE_COUNT = 4194304;
/* 30 */   public static int TAG = 8388608;
/* 31 */   public static int COMMENT = 16777216;
/* 32 */   public static int MARKER = 33554432;
/* 33 */   public static int GEOKEY = 67108864;
/* 34 */   public static int FORMAT = 134217728;
/* 35 */   public static int ANIMATION_PROPERTIES = 268435456;
/* 36 */   public static int PREMULTIPLY_ALPHA = 536870912;
/* 37 */   public static int LINK_IMAGE = 1073741824;
/*    */   private int intValue;
/*    */ 
/*    */   RasterImageChangedFlags(int value)
/*    */   {
/* 43 */     this.intValue = value;
/*    */   }
/*    */ 
/*    */   public int getValue() {
/* 47 */     return this.intValue;
/*    */   }
/*    */ 
/*    */   public static RasterImageChangedFlags forValue(int value)
/*    */   {
/* 52 */     return new RasterImageChangedFlags(value);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.RasterImageChangedFlags
 * JD-Core Version:    0.6.2
 */