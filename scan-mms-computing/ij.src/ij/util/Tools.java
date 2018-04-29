/*     */ package ij.util;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.StringReader;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class Tools
/*     */ {
/*   9 */   public static final char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
/*     */ 
/*     */   public static String c2hex(Color c)
/*     */   {
/*  13 */     int i = c.getRGB();
/*  14 */     char[] buf7 = new char[7];
/*  15 */     buf7[0] = '#';
/*  16 */     for (int pos = 6; pos >= 1; pos--) {
/*  17 */       buf7[pos] = hexDigits[(i & 0xF)];
/*  18 */       i >>>= 4;
/*     */     }
/*  20 */     return new String(buf7);
/*     */   }
/*     */ 
/*     */   public static String f2hex(float f)
/*     */   {
/*  25 */     int i = Float.floatToIntBits(f);
/*  26 */     char[] buf9 = new char[9];
/*  27 */     buf9[0] = '#';
/*  28 */     for (int pos = 8; pos >= 1; pos--) {
/*  29 */       buf9[pos] = hexDigits[(i & 0xF)];
/*  30 */       i >>>= 4;
/*     */     }
/*  32 */     return new String(buf9);
/*     */   }
/*     */ 
/*     */   public static double[] getMinMax(double[] a) {
/*  36 */     double min = 1.7976931348623157E+308D;
/*  37 */     double max = -1.797693134862316E+308D;
/*     */ 
/*  39 */     for (int i = 0; i < a.length; i++) {
/*  40 */       double value = a[i];
/*  41 */       if (value < min)
/*  42 */         min = value;
/*  43 */       if (value > max)
/*  44 */         max = value;
/*     */     }
/*  46 */     double[] minAndMax = new double[2];
/*  47 */     minAndMax[0] = min;
/*  48 */     minAndMax[1] = max;
/*  49 */     return minAndMax;
/*     */   }
/*     */ 
/*     */   public static double[] getMinMax(float[] a) {
/*  53 */     double min = 1.7976931348623157E+308D;
/*  54 */     double max = -1.797693134862316E+308D;
/*     */ 
/*  56 */     for (int i = 0; i < a.length; i++) {
/*  57 */       double value = a[i];
/*  58 */       if (value < min)
/*  59 */         min = value;
/*  60 */       if (value > max)
/*  61 */         max = value;
/*     */     }
/*  63 */     double[] minAndMax = new double[2];
/*  64 */     minAndMax[0] = min;
/*  65 */     minAndMax[1] = max;
/*  66 */     return minAndMax;
/*     */   }
/*     */ 
/*     */   public static double[] toDouble(float[] a)
/*     */   {
/*  71 */     int len = a.length;
/*  72 */     double[] d = new double[len];
/*  73 */     for (int i = 0; i < len; i++)
/*  74 */       d[i] = a[i];
/*  75 */     return d;
/*     */   }
/*     */ 
/*     */   public static float[] toFloat(double[] a)
/*     */   {
/*  80 */     int len = a.length;
/*  81 */     float[] f = new float[len];
/*  82 */     for (int i = 0; i < len; i++)
/*  83 */       f[i] = ((float)a[i]);
/*  84 */     return f;
/*     */   }
/*     */ 
/*     */   public static String fixNewLines(String s)
/*     */   {
/*  89 */     char[] chars = s.toCharArray();
/*  90 */     for (int i = 0; i < chars.length; i++)
/*  91 */       if (chars[i] == '\r') chars[i] = '\n';
/*  92 */     return new String(chars);
/*     */   }
/*     */ 
/*     */   public static double parseDouble(String s, double defaultValue)
/*     */   {
/* 106 */     if (s == null) return defaultValue; try
/*     */     {
/* 108 */       defaultValue = Double.parseDouble(s); } catch (NumberFormatException e) {
/*     */     }
/* 110 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public static double parseDouble(String s)
/*     */   {
/* 122 */     return parseDouble(s, (0.0D / 0.0D));
/*     */   }
/*     */ 
/*     */   public static int getDecimalPlaces(double n1, double n2)
/*     */   {
/* 127 */     if ((Math.round(n1) == n1) && (Math.round(n2) == n2)) {
/* 128 */       return 0;
/*     */     }
/* 130 */     n1 = Math.abs(n1);
/* 131 */     n2 = Math.abs(n2);
/* 132 */     double n = (n1 < n2) && (n1 > 0.0D) ? n1 : n2;
/* 133 */     double diff = Math.abs(n2 - n1);
/* 134 */     if ((diff > 0.0D) && (diff < n)) n = diff;
/* 135 */     int digits = 2;
/* 136 */     if (n < 100.0D) digits = 3;
/* 137 */     if (n < 0.1D) digits = 4;
/* 138 */     if (n < 0.01D) digits = 5;
/* 139 */     if (n < 0.001D) digits = 6;
/* 140 */     if (n < 0.0001D) digits = 7;
/* 141 */     return digits;
/*     */   }
/*     */ 
/*     */   public static String[] split(String str)
/*     */   {
/* 148 */     return split(str, " \t\n\r");
/*     */   }
/*     */ 
/*     */   public static String[] split(String str, String delim)
/*     */   {
/* 154 */     if (delim.equals("\n"))
/* 155 */       return splitLines(str);
/* 156 */     StringTokenizer t = new StringTokenizer(str, delim);
/* 157 */     int tokens = t.countTokens();
/*     */     String[] strings;
/* 159 */     if (tokens > 0) {
/* 160 */       String[] strings = new String[tokens];
/* 161 */       for (int i = 0; i < tokens; i++)
/* 162 */         strings[i] = t.nextToken();
/*     */     } else {
/* 164 */       strings = new String[1];
/* 165 */       strings[0] = str;
/* 166 */       tokens = 1;
/*     */     }
/* 168 */     return strings;
/*     */   }
/*     */ 
/*     */   static String[] splitLines(String str) {
/* 172 */     Vector v = new Vector();
/*     */     try {
/* 174 */       BufferedReader br = new BufferedReader(new StringReader(str));
/*     */       while (true)
/*     */       {
/* 177 */         String line = br.readLine();
/* 178 */         if (line == null) break;
/* 179 */         v.addElement(line);
/*     */       }
/* 181 */       br.close(); } catch (Exception e) {
/*     */     }
/* 183 */     String[] lines = new String[v.size()];
/* 184 */     v.copyInto((String[])lines);
/* 185 */     return lines;
/*     */   }
/*     */ 
/*     */   public static void quicksort(double[] a, int[] index)
/*     */   {
/* 191 */     quicksort(a, index, 0, a.length - 1);
/*     */   }
/*     */ 
/*     */   public static void quicksort(double[] a, int[] index, int left, int right) {
/* 195 */     if (right <= left) return;
/* 196 */     int i = partition(a, index, left, right);
/* 197 */     quicksort(a, index, left, i - 1);
/* 198 */     quicksort(a, index, i + 1, right);
/*     */   }
/*     */ 
/*     */   private static int partition(double[] a, int[] index, int left, int right)
/*     */   {
/* 204 */     int i = left - 1;
/* 205 */     int j = right;
/*     */     while (true)
/* 207 */       if (a[(++i)] >= a[right])
/*     */       {
/* 209 */         while (a[right] < a[(--j)])
/* 210 */           if (j == left) break;
/* 211 */         if (i >= j) break;
/* 212 */         exch(a, index, i, j);
/*     */       }
/* 214 */     exch(a, index, i, right);
/* 215 */     return i;
/*     */   }
/*     */ 
/*     */   private static void exch(double[] a, int[] index, int i, int j)
/*     */   {
/* 220 */     double swap = a[i];
/* 221 */     a[i] = a[j];
/* 222 */     a[j] = swap;
/* 223 */     int b = index[i];
/* 224 */     index[i] = index[j];
/* 225 */     index[j] = b;
/*     */   }
/*     */ 
/*     */   public static void quicksort(String[] a, int[] index)
/*     */   {
/* 230 */     quicksort(a, index, 0, a.length - 1);
/*     */   }
/*     */ 
/*     */   public static void quicksort(String[] a, int[] index, int left, int right) {
/* 234 */     if (right <= left) return;
/* 235 */     int i = partition(a, index, left, right);
/* 236 */     quicksort(a, index, left, i - 1);
/* 237 */     quicksort(a, index, i + 1, right);
/*     */   }
/*     */ 
/*     */   private static int partition(String[] a, int[] index, int left, int right)
/*     */   {
/* 243 */     int i = left - 1;
/* 244 */     int j = right;
/*     */     while (true)
/* 246 */       if (a[(++i)].compareToIgnoreCase(a[right]) >= 0)
/*     */       {
/* 248 */         while (a[right].compareToIgnoreCase(a[(--j)]) < 0)
/* 249 */           if (j == left) break;
/* 250 */         if (i >= j) break;
/* 251 */         exch(a, index, i, j);
/*     */       }
/* 253 */     exch(a, index, i, right);
/* 254 */     return i;
/*     */   }
/*     */ 
/*     */   private static void exch(String[] a, int[] index, int i, int j)
/*     */   {
/* 259 */     String swap = a[i];
/* 260 */     a[i] = a[j];
/* 261 */     a[j] = swap;
/* 262 */     int b = index[i];
/* 263 */     index[i] = index[j];
/* 264 */     index[j] = b;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.util.Tools
 * JD-Core Version:    0.6.2
 */