/*    */ package ij.util;
/*    */ 
/*    */ public class StringSorter
/*    */ {
/*    */   public static void sort(String[] a)
/*    */   {
/*  8 */     if (!alreadySorted(a))
/*  9 */       sort(a, 0, a.length - 1);
/*    */   }
/*    */ 
/*    */   static void sort(String[] a, int from, int to) {
/* 13 */     int i = from; int j = to;
/* 14 */     String center = a[((from + to) / 2)];
/*    */     do {
/* 16 */       while ((i < to) && (center.compareTo(a[i]) > 0)) i++;
/* 17 */       while ((j > from) && (center.compareTo(a[j]) < 0)) j--;
/* 18 */       if (i < j) { String temp = a[i]; a[i] = a[j]; a[j] = temp; }
/* 19 */       if (i <= j) { i++; j--; } 
/* 20 */     }while (i <= j);
/* 21 */     if (from < j) sort(a, from, j);
/* 22 */     if (i < to) sort(a, i, to); 
/*    */   }
/*    */ 
/*    */   static boolean alreadySorted(String[] a)
/*    */   {
/* 26 */     for (int i = 1; i < a.length; i++) {
/* 27 */       if (a[i].compareTo(a[(i - 1)]) < 0)
/* 28 */         return false;
/*    */     }
/* 30 */     return true;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.util.StringSorter
 * JD-Core Version:    0.6.2
 */