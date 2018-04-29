/*     */ package leadtools;
/*     */ 
/*     */ public final class LeadDoubleTools
/*     */ {
/*     */   public static final double NaN = (0.0D / 0.0D);
/*     */   public static final double POSITIVE_INFINITY = (1.0D / 0.0D);
/*     */   public static final double NEGATIVE_INFINITY = (-1.0D / 0.0D);
/*     */ 
/*     */   public static boolean areClose(double value1, double value2)
/*     */   {
/*   9 */     if (value1 == value2) {
/*  10 */       return true;
/*     */     }
/*  12 */     double val1 = (Math.abs(value1) + Math.abs(value2) + 10.0D) * 2.220446049250313E-16D;
/*  13 */     double val2 = value1 - value2;
/*  14 */     return (-val1 < val2) && (val1 > val2);
/*     */   }
/*     */ 
/*     */   public static boolean lessThan(double value1, double value2)
/*     */   {
/*  19 */     return (value1 < value2) && (!areClose(value1, value2));
/*     */   }
/*     */ 
/*     */   public static boolean greaterThan(double value1, double value2)
/*     */   {
/*  24 */     return (value1 > value2) && (!areClose(value1, value2));
/*     */   }
/*     */ 
/*     */   public static boolean lessThanOrClose(double value1, double value2)
/*     */   {
/*  29 */     return (value1 < value2) || (areClose(value1, value2));
/*     */   }
/*     */ 
/*     */   public static boolean greaterThanOrClose(double value1, double value2)
/*     */   {
/*  34 */     return (value1 > value2) || (areClose(value1, value2));
/*     */   }
/*     */ 
/*     */   public static boolean isOne(double value)
/*     */   {
/*  39 */     return Math.abs(value - 1.0D) < 2.220446049250313E-15D;
/*     */   }
/*     */ 
/*     */   public static boolean isZero(double value)
/*     */   {
/*  44 */     return Math.abs(value) < 2.220446049250313E-15D;
/*     */   }
/*     */ 
/*     */   public static boolean areClosePoints(LeadPointD point1, LeadPointD point2)
/*     */   {
/*  49 */     return (areClose(point1.getX(), point2.getX())) && (areClose(point1.getY(), point2.getY()));
/*     */   }
/*     */ 
/*     */   public static boolean areCloseSizes(LeadSizeD size1, LeadSizeD size2)
/*     */   {
/*  54 */     return (areClose(size1.getWidth(), size2.getWidth())) && (areClose(size1.getHeight(), size2.getHeight()));
/*     */   }
/*     */ 
/*     */   public static boolean areCloseRects(LeadRectD rect1, LeadRectD rect2)
/*     */   {
/*  59 */     if (rect1.isEmpty()) {
/*  60 */       return rect2.isEmpty();
/*     */     }
/*  62 */     return (!rect2.isEmpty()) && (areClose(rect1.getX(), rect2.getX())) && (areClose(rect1.getY(), rect2.getY())) && (areClose(rect1.getHeight(), rect2.getHeight())) && (areClose(rect1.getWidth(), rect2.getWidth()));
/*     */   }
/*     */ 
/*     */   public static boolean isBetweenZeroAndOne(double value)
/*     */   {
/*  67 */     return (greaterThanOrClose(value, 0.0D)) && (lessThanOrClose(value, 1.0D));
/*     */   }
/*     */ 
/*     */   public static int doubleToInt(double value)
/*     */   {
/*  72 */     if (0.0D >= value)
/*     */     {
/*  74 */       return (int)(value - 0.5D);
/*     */     }
/*  76 */     return (int)(value + 0.5D);
/*     */   }
/*     */ 
/*     */   public static boolean rectHasNaN(LeadRectD rect)
/*     */   {
/*  81 */     return (isNaN(rect.getX())) || (isNaN(rect.getY())) || (isNaN(rect.getHeight())) || (isNaN(rect.getWidth()));
/*     */   }
/*     */ 
/*     */   public static boolean isNaN(double value)
/*     */   {
/*  86 */     return value != value;
/*     */   }
/*     */ 
/*     */   public static boolean isInfinity(double value)
/*     */   {
/*  91 */     return (value == (1.0D / 0.0D)) || (value == (-1.0D / 0.0D));
/*     */   }
/*     */ 
/*     */   public static double normalizeAngle(double angle)
/*     */   {
/*  96 */     while (angle > 360.0D) angle -= 360.0D;
/*  97 */     while (angle < 0.0D) angle += 360.0D;
/*  98 */     return angle;
/*     */   }
/*     */ 
/*     */   public static int toInt(double val)
/*     */   {
/* 106 */     return val < 0.0D ? (int)(val - 0.5D) : (int)(val + 0.5D);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.LeadDoubleTools
 * JD-Core Version:    0.6.2
 */