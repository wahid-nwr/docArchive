/*     */ package leadtools.internal;
/*     */ 
/*     */ import leadtools.RasterColor;
/*     */ 
/*     */ public class ColorParser
/*     */ {
/*     */   public static RasterColor parseColor(String color)
/*     */   {
/*  12 */     KnownColors.MatchColorResult res = new KnownColors.MatchColorResult();
/*  13 */     res.isNumericColor = false;
/*  14 */     res.isKnownColor = false;
/*  15 */     res.isRgb = false;
/*  16 */     res.isRgba = false;
/*     */ 
/*  18 */     String trimmedColor = KnownColors.matchColor(color, res);
/*  19 */     if ((res.isRgb) || (res.isRgba))
/*  20 */       return parseRgb(trimmedColor, res.isRgb);
/*  21 */     if ((!res.isKnownColor) && (!res.isNumericColor)) {
/*  22 */       throw new IllegalArgumentException("Invalid color format");
/*     */     }
/*  24 */     if (res.isNumericColor) {
/*  25 */       return parseHexColor(trimmedColor);
/*     */     }
/*  27 */     long knownColor = KnownColors.colorStringToKnownColor(trimmedColor);
/*  28 */     if (knownColor == 1L) {
/*  29 */       throw new IllegalArgumentException("Invalid color format");
/*     */     }
/*  31 */     return RasterColor.fromKnownColor(knownColor);
/*     */   }
/*     */ 
/*     */   private static RasterColor parseHexColor(String trimmedColor)
/*     */   {
/*  38 */     int num4 = 255;
/*  39 */     int length = trimmedColor.length();
/*     */     int num;
/*     */     int num3;
/*     */     int num2;
/*     */     int num;
/*  40 */     if (length > 7)
/*     */     {
/*  42 */       num4 = parseHexChar(trimmedColor.charAt(1)) * 16 + parseHexChar(trimmedColor.charAt(2));
/*  43 */       int num3 = parseHexChar(trimmedColor.charAt(3)) * 16 + parseHexChar(trimmedColor.charAt(4));
/*  44 */       int num2 = parseHexChar(trimmedColor.charAt(5)) * 16 + parseHexChar(trimmedColor.charAt(6));
/*  45 */       num = parseHexChar(trimmedColor.charAt(7)) * 16 + parseHexChar(trimmedColor.charAt(8));
/*     */     }
/*     */     else
/*     */     {
/*     */       int num;
/*  47 */       if (length > 5)
/*     */       {
/*  49 */         int num3 = parseHexChar(trimmedColor.charAt(1)) * 16 + parseHexChar(trimmedColor.charAt(2));
/*  50 */         int num2 = parseHexChar(trimmedColor.charAt(3)) * 16 + parseHexChar(trimmedColor.charAt(4));
/*  51 */         num = parseHexChar(trimmedColor.charAt(5)) * 16 + parseHexChar(trimmedColor.charAt(6));
/*     */       }
/*  53 */       else if (length > 4)
/*     */       {
/*  55 */         num4 = parseHexChar(trimmedColor.charAt(1));
/*  56 */         num4 += num4 * 16;
/*  57 */         int num3 = parseHexChar(trimmedColor.charAt(2));
/*  58 */         num3 += num3 * 16;
/*  59 */         int num2 = parseHexChar(trimmedColor.charAt(3));
/*  60 */         num2 += num2 * 16;
/*  61 */         int num = parseHexChar(trimmedColor.charAt(4));
/*  62 */         num += num * 16;
/*     */       }
/*     */       else
/*     */       {
/*  66 */         num3 = parseHexChar(trimmedColor.charAt(1));
/*  67 */         num3 += num3 * 16;
/*  68 */         num2 = parseHexChar(trimmedColor.charAt(2));
/*  69 */         num2 += num2 * 16;
/*  70 */         num = parseHexChar(trimmedColor.charAt(3));
/*  71 */         num += num * 16;
/*     */       }
/*     */     }
/*  73 */     return new RasterColor((byte)num4, (byte)num3, (byte)num2, (byte)num);
/*     */   }
/*     */ 
/*     */   private static int parseHexChar(char c) {
/*  77 */     int num = c;
/*  78 */     if ((num >= 48) && (num <= 57)) {
/*  79 */       return num - 48;
/*     */     }
/*  81 */     if ((num >= 97) && (num <= 102)) {
/*  82 */       return num - 97 + 10;
/*     */     }
/*  84 */     if ((num < 65) || (num > 70)) {
/*  85 */       throw new IllegalArgumentException("Invalid color format");
/*     */     }
/*  87 */     return num - 65 + 10;
/*     */   }
/*     */ 
/*     */   private static RasterColor parseRgb(String trimmedColor, boolean isRgb) {
/*  91 */     trimmedColor = trimmedColor.replace(" ", "");
/*  92 */     int len = trimmedColor.length();
/*  93 */     int paraIndex = isRgb ? 3 : 4;
/*  94 */     if ((trimmedColor.charAt(paraIndex) != '(') || (trimmedColor.charAt(len - 1) != ')')) throw new IllegalArgumentException("Invalid color format");
/*  95 */     if ((paraIndex + 1 >= len) || (len - paraIndex - 2 < 1)) throw new IllegalArgumentException("Invalid color format");
/*  96 */     trimmedColor = trimmedColor.substring(paraIndex + 1, len - paraIndex - 2);
/*  97 */     String[] strs = trimmedColor.split(",");
/*  98 */     if ((strs == null) || (strs.length != paraIndex)) throw new IllegalArgumentException("Invalid color format");
/*  99 */     int[] vals = { 0, 0, 0 };
/* 100 */     double a = 1.0D;
/* 101 */     for (int i = 0; i < 3; i++) {
/* 102 */       vals[i] = Integer.parseInt(strs[i], 10);
/* 103 */       if (vals[i] < 0) throw new IllegalArgumentException("Invalid color format");
/*     */     }
/*     */ 
/* 106 */     if (!isRgb)
/* 107 */       a = Double.parseDouble(strs[3]);
/* 108 */     if ((a < 0.0D) || (a > 1.0D)) throw new IllegalArgumentException("Invalid color format");
/* 109 */     return new RasterColor((int)(255.0D * a + 0.5D), vals[0], vals[1], vals[2]);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.internal.ColorParser
 * JD-Core Version:    0.6.2
 */